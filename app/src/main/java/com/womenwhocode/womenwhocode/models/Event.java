package com.womenwhocode.womenwhocode.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.womenwhocode.womenwhocode.utils.ModelJSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zassmin on 10/16/15.
 */
@ParseClassName("Event")
public class Event extends ParseObject {
    public static final String NETWORK_KEY = "network";
    public static final String DATETIME_KEY = "event_date"; // UTC start time of the event, in milliseconds since the epoch
    private static final String LOCATION_KEY = "location"; // venue should be attribute name here
    private static final String URL_KEY = "url"; // could be an event url from meetup, facebook, eventbrite, etc
    private static final String TITLE_KEY = "title";
    private static final String FEATURED_KEY = "featured";
    private static final String MEETUP_EVENT_ID_KEY = "meetup_event_id";
    private static final String DESCRIPTION_KEY = "description";
    private static final String TIMEZONE_KEY = "timezone";
    private static final String SUBSCRIBE_COUNT = "subscribe_count";
    private static final String HEX_COLOR = "hex_color";

    private static Event fromJSON(ModelJSONObject jsonObject) {
        String eventId = "";
        try {
            eventId = jsonObject.getString("id"); // this should never be null!
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Event event = new Event();
        // Find the event object
        ParseQuery<Event> eventParseQuery = ParseQuery.getQuery(Event.class);
        eventParseQuery.whereEqualTo(Event.MEETUP_EVENT_ID_KEY, eventId);
        try {
            if (eventParseQuery.count() > 0) {
                event = eventParseQuery.getFirst();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            event.setMeetupEventId(jsonObject.getString("id"));
            event.setDescription(jsonObject.getString("description"));
            event.setFeatured(jsonObject.getBoolean("featured"));
            event.setTitle(jsonObject.getString("name"));
            event.setLocation(jsonObject.getJSONObject("venue").getString("name"));
            event.setEventDateTime(jsonObject.getString("time"));
            event.setTimeZone(jsonObject.getString("timezone"));
            event.setUrl(jsonObject.getString("event_url"));
            String networkMeetupId = String.valueOf(jsonObject.getJSONObject("group").getInt("id"));
            Network network = Network.findByMeetupId(networkMeetupId);
            if (network != null) {
                event.setNetwork(network); // FIXME: optimization needed, don't find the network for each event
            }
            event.save();
            // save in background and the callback!
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        return event;

    }

    public static ArrayList<Event> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Event> events = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject eventJSON = jsonArray.getJSONObject(i);
                ModelJSONObject modelJSONObject = new ModelJSONObject(eventJSON);
                Event event = fromJSON(modelJSONObject);
                if (event != null) {
                    events.add(event);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return events;
    }

    public String getHexColor() {
        return this.get(HEX_COLOR).toString();
    }

    public void setHexColor(String hexColor) {
        put(HEX_COLOR, hexColor);
    }

    public int getSubscribeCount() {
        int count;
        Object c = this.get(SUBSCRIBE_COUNT);
        if (c != null) {
            count = Integer.parseInt(c.toString());
        } else {
            count = 0;
        }
        return count;
    }

    public void setSubscribeCount(int count) {
        put(SUBSCRIBE_COUNT, count);
    }

    public Network getNetwork() {
        Network network = null;
        try {
            network = this.getParseObject(NETWORK_KEY).fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return network;
    }

    private void setNetwork(Network network) {
        put(NETWORK_KEY, network);
    }

    public String getEventDateTime() {
        return this.get(DATETIME_KEY).toString();
    }

    private void setEventDateTime(String datetime) {
        put(DATETIME_KEY, datetime);
    }

    public String getLocation() {
        return this.get(LOCATION_KEY).toString();
    }

    private void setLocation(String location) {
        put(LOCATION_KEY, location);
    }

    public String getUrl() {
        return this.get(URL_KEY).toString();
    }

    private void setUrl(String url) {
        put(URL_KEY, url);
    }

    public String getTitle() {
        return this.get(TITLE_KEY).toString();
    }

    private void setTitle(String title) {
        put(TITLE_KEY, title);
    }

    public boolean getFeatured() {
        return this.getBoolean(FEATURED_KEY);
    }

    private void setFeatured(boolean featured) {
        put(FEATURED_KEY, featured);
    }

    public String getMeetupEventId() {
        return this.get(MEETUP_EVENT_ID_KEY).toString();
    }

    private void setMeetupEventId(String id) {
        put(MEETUP_EVENT_ID_KEY, id);
    }

    public String getDescription() {
        return this.get(DESCRIPTION_KEY).toString();
    }

    private void setDescription(String description) {
        put(DESCRIPTION_KEY, description);
    }

    private void setTimeZone(String timeZone) {
        put(TIMEZONE_KEY, timeZone);
    }

    public String getTimezone() {
        return this.getString(TIMEZONE_KEY);
    }

    // FIXME: display time according to local time zone
}
