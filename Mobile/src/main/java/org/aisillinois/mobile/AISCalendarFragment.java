package org.aisillinois.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kodspider.xml.XMLParser;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class AISCalendarFragment extends CaldroidFragment {

    /* Constants */
    public static final SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d, yyyy"); // Mon Jun 2, 2014 9pm
    public static final String CALENDAR_ID = "3mmb0i9rplg8s86dflrdg4h50k@group.calendar.google.com";
    //public static final String CALENDAR_URL = String.format("https://www.google.com/calendar/feeds/aisillinois%40gmail.com/public/basic", CALENDAR_ID);
    public static final String CALENDAR_URL = "https://www.google.com/calendar/feeds/aisillinois%40gmail.com/public/basic";
    /* Attributes */
    HashMap<Date, Event> eventsMap; // Map that relates dates with events.
    TextView eventTitle;
    TextView eventTime;
    TextView eventPlace;
    TextView eventDescription;

    LinearLayout textLayout;
    LinearLayout globalLayout;

    public AISCalendarFragment() {
        super();
    }

    public static AISCalendarFragment newInstance() {
        // Initialize the fragment.
        AISCalendarFragment calendarFragment = new AISCalendarFragment();

        // Build a bundle of arguments.
        Bundle args = new Bundle();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(java.util.Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(java.util.Calendar.YEAR));
        args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
        args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

        // Give the arguments to the fragment and return it.
        calendarFragment.setArguments(args);
        return calendarFragment;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        eventsMap = new HashMap<Date, Event>();

        eventTitle = new TextView(activity);
        eventTime = new TextView(activity);
        eventPlace = new TextView(activity);
        eventDescription = new TextView(activity);

        eventTitle.setTextSize(20);
        eventTitle.setTextColor(getResources().getColor(R.color.ais_blue));
        eventTitle.setTypeface(eventTitle.getTypeface(), Typeface.BOLD);

        eventTime.setTextSize(15);
        eventTime.setTypeface(eventTime.getTypeface(), Typeface.BOLD);

        eventPlace.setTextSize(15);
        eventPlace.setTypeface(eventPlace.getTypeface(), Typeface.BOLD);

        eventDescription.setTextSize(15);

        // Set up calendar listener.
        this.setCaldroidListener(new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                textLayout.removeAllViews();
                if (eventsMap.containsKey(date)) {
                    Event event = eventsMap.get(date);

                    String title = event.getTitle();
                    String time = event.getWhen();
                    String place = event.getWhen();
                    String description = event.getDescription();

                    if (title != null) {
                        eventTitle.setText(event.getTitle());
                        textLayout.addView(eventTitle);
                    }
                    if (time != null) {
                        eventTime.setText(event.getWhen());
                        textLayout.addView(eventTime);
                    }
                    if (place != null) {
                        eventPlace.setText(event.getWhere());
                        textLayout.addView(eventPlace);
                    }
                    if (description != null) {
                        eventDescription.setText(event.getDescription());
                        textLayout.addView(eventDescription);
                    }
                }
            }

            @Override
            public void onChangeMonth(int month, int year) {
            }

            @Override
            public void onLongClickDate(Date date, View view) {
            }

            @Override
            public void onCaldroidViewCreated() {
            }

        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View calendarView = super.onCreateView(inflater, container, savedInstanceState);

        textLayout = new LinearLayout(this.getActivity());
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setDividerPadding(3);
        textLayout.setPadding(20, 20, 0, 0);

        globalLayout = new LinearLayout(this.getActivity());
        globalLayout.setOrientation(LinearLayout.VERTICAL);
        globalLayout.addView(calendarView);
        globalLayout.addView(textLayout);

        return globalLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        new CalendarFeedTask().execute(CALENDAR_URL);
    }

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new AISCalendarAdapter(getActivity(), month, year,
                getCaldroidData(), extraData);
    }

    private class CalendarFeedTask extends AsyncTask<String, Void, Integer> {
        /* Constants */
        private static final int OK = 0;
        private static final int URL_FETCHING_ERROR = -1;
        private static final int XML_PARSING_ERROR = -2;
        private static final int CONTENT_PARSING_ERROR = -3;

        /* Attributes */
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading events...");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... url) {
            // Variables declaration.
            String title = null;
            String time = null;
            String place = null;
            String description = null;

            // Retrieve the XML calendar feed from the URL.
            XMLParser parser = new XMLParser();
            String feedXML = parser.getXmlFromUrl(url[0]);
            if (feedXML == null) return URL_FETCHING_ERROR;

            // Parse the XML and generate the DOM tree.
            Document dom = parser.getDomElement(feedXML);
            if (dom == null) return XML_PARSING_ERROR;

            try {
                // Cover the tree and parse the events information.
                NodeList nodes = dom.getElementsByTagName("entry");
                for (int i = 0; i < nodes.getLength(); ++i) {
                    NodeList innerNodes = nodes.item(i).getChildNodes();

                    Event event = new Event();
                    for (int j = 0; j < innerNodes.getLength(); ++j) {
                        Node node = innerNodes.item(j);
                        String nodeName = node.getNodeName();

                        if (nodeName.equals("title")) {
                            title = parser.getElementValue(node);
                            event.setTitle(title);

                        } else if (nodeName.equals("content")) {
                            String[] htmlLines = parser.getElementValue(node).split("<br />"); // HTML break line.

                            for (String line : htmlLines) {
                                if (line.contains("When:")) {
                                    time = line.substring(line.indexOf(':') + 2, line.lastIndexOf('\n') - 1);
                                    event.setWhen(time);

                                } else if (line.contains("Where:")) {
                                    place = line.substring(line.indexOf(':') + 1).trim();
                                    event.setWhere(place);

                                } else if (line.contains("Event Description:")) {
                                    description = line.substring(line.indexOf(':') + 1).trim();
                                    event.setDescription(description);
                                }
                            }
                        }
                    }
                    Date dateKey = formatter.parse(time, new ParsePosition(0));
                    eventsMap.put(dateKey, event);  // Store the event in the map of events.
                }
            } catch (Exception e) {
                e.printStackTrace();
                return CONTENT_PARSING_ERROR;
            }

            return OK;
        }

        @Override
        protected void onPostExecute(Integer result) {
            switch (result) {
                case URL_FETCHING_ERROR:
                    Toast.makeText(getActivity(), "Unable to get events data from the Internet.",
                            Toast.LENGTH_SHORT).show();
                    break;
                case XML_PARSING_ERROR:
                    Toast.makeText(getActivity(), "Invalid data fetched from the Internet.",
                            Toast.LENGTH_SHORT).show();
                    break;
                case CONTENT_PARSING_ERROR:
                    Toast.makeText(getActivity(), "Error while processing events information.",
                            Toast.LENGTH_SHORT).show();
                    break;
                case OK:
                    // Change the background and font color of dates with events.
                    HashMap<Date, Integer> datesBackgroundColor = new HashMap<Date, Integer>();
                    HashMap<Date, Integer> datesTextColor = new HashMap<Date, Integer>();

                    for (Date date : eventsMap.keySet()) {
                        datesBackgroundColor.put(date, R.color.ais_blue);
                        datesTextColor.put(date, R.color.white);
                    }

                    setBackgroundResourceForDates(datesBackgroundColor);
                    setTextColorForDates(datesTextColor);
                    refreshView();
                default:
                    // Do nothing.
            }
            dialog.dismiss();
        }
    }

}