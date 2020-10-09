package de.schoolprojects.geoTracker;

public class DDef {

    public static String pathToDatabase;
 // new File(LoginView.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath().toString() + "geoData.db"
    static {
        pathToDatabase = "jdbc:sqlite:/home/pi/sqlite3/geoData.db";
//        pathToDatabase = "jdbc:sqlite:/home/sh/Desktop/geoData.db";
    }

    static class Command {
        public static String select = "SELECT ";
        public static String from = "FROM ";
        public static String where = "WHERE ";
        public static String orderBy = "ORDER BY ";
        public static String asc = "ASC";
    }

    static class usersTable {
        public static String tableName = "users";
        public static String telNumber = "tel_number";
        public static String userName = "name";
        public static String password = "pwd";
    }

    static class coordinatesTable {
        public static String tableName = "coordinates";
        public static String telNumber = "tel_number";
        public static String lng = "long";
        public static String lat = "lat";
        public static String date = "date";
        public static String temperature = "temperatur";
    }
}
