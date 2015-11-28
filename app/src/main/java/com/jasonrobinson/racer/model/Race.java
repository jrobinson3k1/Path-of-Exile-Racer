package com.jasonrobinson.racer.model;

import java.util.Date;
import java.util.List;

public class Race {

    private String id;
    private String description;
    private String url;
    private boolean event;
    private Date registerAt;
    private Date startAt;
    private Date endAt;
    private List<Rule> rules;

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public boolean isEvent() {
        return event;
    }

    public Date getRegisterAt() {
        return registerAt;
    }

    public Date getStartAt() {
        return startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public boolean isInProgress() {
        Date now = new Date(System.currentTimeMillis());
        return now.after(getStartAt()) && now.before(getEndAt());
    }

    public boolean isFinished() {
        Date now = new Date(System.currentTimeMillis());
        return now.after(getEndAt());
    }

    public boolean isRegistrationOpen() {
        Date now = new Date(System.currentTimeMillis());
        return now.after(getRegisterAt()) && now.before(getEndAt());
    }

    public static class Rule {

        private long id;
        private String name;
        private String description;

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
