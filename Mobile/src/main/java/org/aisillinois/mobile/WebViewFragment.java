package org.aisillinois.mobile;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;



public class WebViewFragment extends Fragment {


    public WebViewFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = (View) inflater.inflate(R.layout.fragment_web_view, container, false);
        WebView wv = (WebView) v.findViewById(R.id.webview);


        wv.setWebViewClient(new WebViewClient());
        wv.setInitialScale(100);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("http://aisillinois.org/");



        return v;
    }


}
