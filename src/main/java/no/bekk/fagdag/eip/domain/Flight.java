package no.bekk.fagdag.eip.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 *
 */
public class Flight implements Serializable{
    public String id;
    public String flight;
    public LocalDate date;
    public LocalTime time;
    public LocalTime newTime;
    public Flight.Status status;

    public Flight withFlight(String flight) {
        this.flight = flight;
        return this;
    }

    public Flight withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public Flight withTime(LocalTime time) {
        this.time = time;
        return this;
    }

    public Flight withNewTime(LocalTime time) {
        this.newTime = time;
        return this;
    }

    public Flight withStatus(Status status) {
        this.status = status;
        return this;
    }

    public String getKey() {
        return flight + date.format(ofPattern("yyyyMMdd"));
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id='" + id + '\'' +
                ", flight='" + flight + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", newTime=" + newTime +
                ", status=" + status +
                '}';
    }

    public enum Status {
        OnTime,
        Delayed,
        Boarding,
        GateClosing,
        GateClosed
    }
}
