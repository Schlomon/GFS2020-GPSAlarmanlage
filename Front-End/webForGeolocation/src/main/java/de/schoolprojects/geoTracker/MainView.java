package de.schoolprojects.geoTracker;


import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@Route("")
@PWA(name = "Location tracker",
        shortName = "idk")
public class MainView extends Div {
    LoginView loginView;
    MapView mapView;

    public MainView() {
        setSizeFull();
        loginView = new LoginView();

        loginView.addLoginListener(user -> {
            mapView = new MapView(user);
            removeAll();
            add(mapView);
        });

        this.add(loginView);
    }
}