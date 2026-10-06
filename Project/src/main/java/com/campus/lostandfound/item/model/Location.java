package com.campus.lostandfound.item.model;

import java.util.List;

public record Location(
        String id,
        String building,
        String floor,
        String roomOrZone,
        String securityDeskContact
) {
    public String fullDisplayName() {
        return building + " (" + floor + ", " + roomOrZone + ")";
    }

    public static List<Location> defaultLocations() {
        return List.of(
                new Location("LOC-LIB-02", "Central Library", "2nd Floor", "Quiet Study Hall & Carrels", "Library Security Desk (Ext 401)"),
                new Location("LOC-ENG-01", "Engineering Complex B", "1st Floor", "Robotics & Hardware Labs", "South Gate Security (Ext 402)"),
                new Location("LOC-STU-01", "Student Activity Center", "Ground Floor", "Food Court & Lounge", "SAC Desk (Ext 403)"),
                new Location("LOC-SCI-03", "Science Lecture Hall", "3rd Floor", "Auditorium A & Physics Labs", "North Gate Desk (Ext 404)"),
                new Location("LOC-GYM-01", "Campus Sports Complex", "Ground Floor", "Basketball Court & Lockers", "Athletics Desk (Ext 405)"),
                new Location("LOC-ADM-01", "Administration Building", "1st Floor", "Registrar & Bursar Office", "Central Security HQ (Ext 400)")
        );
    }
}
