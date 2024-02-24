package edu.gatech.cs6310.groceryexpressproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "clock")
public class Clock implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int time;
    private int lastPurgeTime;
    public Clock() {
        this.time = 0;
        this.lastPurgeTime = 0;
    }
    /**
     * Checks if the given time is during daytime.
     *
     * @param  time  the time in minutes
     * @return       true if the time is during daytime, false otherwise
     */
    public boolean isDaytime(int time) {
        int startTime = 360; // Start time in minutes (6:00 AM)
        int endTime = 1080; // End time in minutes (6:00 PM)
        int adjustedTime = time % 1440; // Adjust time to 24-hour clock
        return adjustedTime >= startTime && adjustedTime < endTime;
    }

    /**
     * Converts the current time into a Day-Hour-Minute format
     * @return the current time in Day-Hour-Minute format
     */
    public String getTimeDateFormat() {
        return this.getTimeDateFormat(this.time);
    }

    /**
     * Converts a given date into a Day-Hour-Minute format
     * @param date - the date to convert
     * @return a String representing the date in Day-Hour-Minute format
     */
    public String getTimeDateFormat(int date) {
        if (date < 0) {
            System.out.printf("ERROR:date_%d_is_invalid", date);
            return "date_is_invalid";
        }
        int days = date / 1440;
        int hours = (date % 1440) / 60;
        int minutes = (date % 1440) % 60;
        String amOrPM = "AM";

        if (hours == 12) {
            amOrPM = "PM";
        } else if (hours >= 13) {
            hours -= 12;
            amOrPM = "PM";
        }
        return String.format("Day %d, %02d:%02d%s", days, hours, minutes, amOrPM);
    }

    public static String getTimestamp(int time) {
        int days = time / 1440;
        int hours = (time % 1440) / 60;
        int minutes = (time % 1440) % 60;
        String amOrPM = "AM";

        if (hours == 12) {
            amOrPM = "PM";
        } else if (hours >= 13) {
            hours -= 12;
            amOrPM = "PM";
        }
        return String.format("Day_%d_%02d:%02d_%s", days, hours, minutes, amOrPM);
    }

    /**
     * Function returning this current time in String format
     * @return time written in String format
     */
    @Override
    public String toString() {
        return this.time + " min (" + this.getTimeDateFormat() + ")";
    }
}
