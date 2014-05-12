package org.aisillinois.mobile;

// Class to contain events information.
public class Event {
    private String title;
    private String when;
    private String where;
    private String description;

    public Event() {
    }

    public Event(String title, String when, String where, String description) {
        this.title = title;
        this.when = when;
        this.where = where;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}