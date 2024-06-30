package com.example.herhealth;

public class TimeConverter {

    public static String convertMillisToTimeString(long currentTimeMillis) {
        // Convert milliseconds to seconds
        long seconds = currentTimeMillis / 1000;

        // Convert seconds to minutes
        long minutes = seconds / 60;

        // Convert minutes to hours
        long hours = minutes / 60;

        // Convert hours to days
        long days = hours / 24;

        // Generate the time string
        if (days > 0) {
            return days + (days == 1 ? " day" : " days");
        } else if (hours > 0) {
            return hours + (hours == 1 ? " hour" : " hours");
        } else if (minutes > 0) {
            return minutes + (minutes == 1 ? " minute" : " minutes");
        } else {
            return seconds + (seconds == 1 ? " second" : " seconds");
        }
    }

}
