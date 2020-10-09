package de.schoolprojects.geoTracker;

import com.vaadin.addon.leaflet4vaadin.types.LatLng;

public class CoordinatesEntry {
    LatLng coords;
    String telNum;
    String time;
    String temp;

    public CoordinatesEntry(LatLng coords, String telNum, String time, String temp) {
        this.coords = coords;
        this.telNum = telNum;
        this.time = time;
        this.temp = temp;
    }

    public LatLng getCoords() {
        return coords;
    }

    public String getTelNum() {
        return telNum;
    }

    public String getTime() {
        return time;
    }

    public String getTemp() {
        return temp;
    }
}
