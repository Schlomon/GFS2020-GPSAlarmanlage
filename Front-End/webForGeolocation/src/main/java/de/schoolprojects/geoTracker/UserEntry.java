package de.schoolprojects.geoTracker;

public class UserEntry {
    private String telNumber;
    private String name;
    private String pwdHash;

    public UserEntry(String telNumber, String name, String pwdHash) {
        this.telNumber = telNumber;
        this.name = name;
        this.pwdHash = pwdHash;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public String getName() {
        return name;
    }

    public String getPwdHash() {
        return pwdHash;
    }
}
