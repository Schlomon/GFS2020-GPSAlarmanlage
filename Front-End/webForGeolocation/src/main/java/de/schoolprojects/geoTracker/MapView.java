package de.schoolprojects.geoTracker;

import com.vaadin.addon.leaflet4vaadin.LeafletMap;
import com.vaadin.addon.leaflet4vaadin.layer.map.options.DefaultMapOptions;
import com.vaadin.addon.leaflet4vaadin.layer.map.options.MapOptions;
import com.vaadin.addon.leaflet4vaadin.layer.raster.TileLayer;
import com.vaadin.addon.leaflet4vaadin.layer.ui.marker.Marker;
import com.vaadin.addon.leaflet4vaadin.layer.vectors.Polyline;
import com.vaadin.addon.leaflet4vaadin.layer.vectors.structure.LatLngArray;
import com.vaadin.addon.leaflet4vaadin.types.Icon;
import com.vaadin.addon.leaflet4vaadin.types.LatLng;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;

@PageTitle("Map for coordinates")
public class MapView extends Div {

    Label optionTelNumber;

    UserEntry user;

    public MapView(UserEntry user) {
        this.user = user;
        setSizeFull();
        add(getMapView());
    }

    private HorizontalLayout getMapView() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        horizontalLayout.setSizeFull();

        VerticalLayout sideBarLeft = new VerticalLayout();

        Label timeLabel = new Label("Timestamp for clicked marker");
        Label coordLabel = new Label("Coordinate for clicked marker");
        Label tempLabel = new Label("Temperature for clicked marker");

        MapOptions options = new DefaultMapOptions();
        options.setCenter(new LatLng(48.551578, 8.722878));
        options.setZoom(14);

        LeafletMap leafletMap = new LeafletMap(options);
        leafletMap.setBaseUrl("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png");

        TileLayer openStreetmap = new TileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png");
        openStreetmap.setAttribution("OpenStreetmap");
        openStreetmap.setSubdomains("1");
        openStreetmap.addTo(leafletMap);

        Button optionsButton = new Button("Options", e -> {
            removeAll();
            add(getOptionsView());
        });

        Button logoutButton = new Button("Logout", e -> {
            removeAll();
            VaadinSession.getCurrent().close();
            VaadinService.getCurrentRequest().getWrappedSession().invalidate();
            UI.getCurrent().navigate(MainView.class);
        });

        sideBarLeft.setHeight("100%");
        sideBarLeft.setWidth(optionsButton.getWidth());

        sideBarLeft.add(optionsButton);

        sideBarLeft.add(timeLabel);
        sideBarLeft.add(coordLabel);
        sideBarLeft.add(tempLabel);

        sideBarLeft.add(logoutButton);

        ArrayList<CoordinatesEntry> coords = DatabaseAccess.getCoordinatesByTelNumber(user.getTelNumber());

        LatLngArray latLngArray = new LatLngArray(new LatLng[]{});

        if (coords.size() > 0) {
            int count = 0;
            Marker marker;
            for (CoordinatesEntry coordinatesEntry : coords) {
                count++;
                marker = new Marker(coordinatesEntry.getCoords());
                marker.onClick(mouseEvent -> {
                    timeLabel.setText(coordinatesEntry.getTime());
                    coordLabel.setText(coordinatesEntry.getCoords().toString());
                    tempLabel.setText(coordinatesEntry.getTemp());
                });
                marker.setIcon(new Icon("icons/marker.png", 40));
                marker.bindTooltip(count + "");
                marker.addTo(leafletMap);
                latLngArray.add(coordinatesEntry.getCoords());
            }

            Polyline path = new Polyline(latLngArray);

            path.setFill(false);

            path.addTo(leafletMap);

            leafletMap.whenReady(mapReadyEvent -> {
                leafletMap.fitBounds(path.getBounds());
            });
        } else {
            coordLabel.setText("No coordinates received yet!");
        }

        horizontalLayout.add(sideBarLeft);
        horizontalLayout.add(leafletMap);

        return horizontalLayout;
    }

    private VerticalLayout getOptionsView() {
        VerticalLayout verticalLayout = new VerticalLayout();

        Div space = new Div();
        space.setHeight("10px");
        verticalLayout.add(space);

        verticalLayout.add(new Label("Telephone number:"));

        optionTelNumber = new Label();
        optionTelNumber.setText(user.getTelNumber());
        verticalLayout.add(optionTelNumber);

        verticalLayout.add(new Button("Ok", e -> {
            removeAll();
            add(getMapView());
        }));

        verticalLayout.add(space);
        verticalLayout.add(space);

        verticalLayout.add(new Label("Credits:"));
        verticalLayout.add(new Label("Marker image source: https://de.wikipedia.org/wiki/Datei:Map_marker.svg"));

        return verticalLayout;
    }
}
