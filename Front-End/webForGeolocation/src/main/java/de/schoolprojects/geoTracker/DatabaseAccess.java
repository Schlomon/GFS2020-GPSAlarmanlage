package de.schoolprojects.geoTracker;

import com.vaadin.addon.leaflet4vaadin.types.LatLng;
import de.schoolprojects.geoTracker.DDef.Command;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseAccess {

    public static UserEntry getUserByName(String userName) {
        String sqlCommand = Command.select + "* " + Command.from + DDef.usersTable.tableName + " " + Command.where + DDef.usersTable.userName + " = '" + userName + "'";

        ResultSet resultSet = executeCommand(sqlCommand);

        try {
            resultSet.next();
            return new UserEntry(
                    resultSet.getString(DDef.usersTable.telNumber),
                    resultSet.getString(DDef.usersTable.userName),
                    resultSet.getString(DDef.usersTable.password));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<CoordinatesEntry> getCoordinatesByTelNumber(String telNumber) {
        String sqlCommand = Command.select + "* " +
                Command.from + DDef.coordinatesTable.tableName + " " +
                Command.where + DDef.coordinatesTable.telNumber + " = '" + telNumber + "' " +
                Command.orderBy + DDef.coordinatesTable.date + " " + Command.asc + ";";

        ResultSet resultSet = executeCommand(sqlCommand);

        ArrayList<CoordinatesEntry> allCoordinates = new ArrayList<>();

        try {
            while (resultSet.next()) {
                allCoordinates.add(new CoordinatesEntry(
                        new LatLng(
                                Double.parseDouble(resultSet.getString(DDef.coordinatesTable.lat)),
                                Double.parseDouble(resultSet.getString(DDef.coordinatesTable.lng))),
                        telNumber,
                        new Timestamp(resultSet.getInt(DDef.coordinatesTable.date)).toString(),
                        resultSet.getString(DDef.coordinatesTable.temperature)));
                // old TODO: remove
//                coordinates.add(new LatLng(
//                        Double.parseDouble(resultSet.getString(DDef.coordinatesTable.lat)),
//                        Double.parseDouble(resultSet.getString(DDef.coordinatesTable.lng))));
            }
            return allCoordinates;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

//    public static UserEntry getUserByTelNumber(String telNumber) {
//        String sqlCommand = DDef.Command.select + "* " + DDef.Command.from + DDef.usersTable.tableName + " " + DDef.Command.where + DDef.usersTable.telNumber + " = \'" + telNumber + "\'";
//
//        ResultSet resultSet = executeCommand(sqlCommand);
//
//        try {
//            resultSet.next();
//            return new UserEntry(
//                    resultSet.getString(DDef.usersTable.telNumber),
//                    resultSet.getString(DDef.usersTable.userName),
//                    resultSet.getString(DDef.usersTable.password));
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static ArrayList<UserEntry> getAllUsers() {
        String sqlCommand = Command.select + "* " + Command.from + DDef.usersTable.tableName;

        ResultSet resultSet = executeCommand(sqlCommand);

        ArrayList<UserEntry> allUserFields = new ArrayList<>();

        try {
            while (resultSet.next()) {
                allUserFields.add(new UserEntry(
                        resultSet.getString(DDef.usersTable.telNumber),
                        resultSet.getString(DDef.usersTable.userName),
                        resultSet.getString(DDef.usersTable.password)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allUserFields;
    }

    public static ResultSet executeCommand(String sqlCommand) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(DDef.pathToDatabase);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlCommand);
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
