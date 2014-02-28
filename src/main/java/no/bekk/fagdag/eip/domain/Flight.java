package no.bekk.fagdag.eip.domain;

import java.io.Serializable;
import java.util.Date;

public class Flight implements Serializable {

  public Long id;
  public String carrier;
  public String flight;
  public Date scheduledTime;
  public String airport;
  public Flight.Status status;


  public Flight() {
  }

  public Flight withId(Long id) {
    this.id = id;
    return this;
  }

  public Flight withCarrier(String carrier) {
    this.carrier = carrier;
    return this;
  }

  public Flight withFlight(String flight) {
    this.flight = flight;
    return this;
  }

  public Flight withScheduledTime(Date scheduledTime) {
    this.scheduledTime = scheduledTime;
    return this;
  }

  public Flight withAirport(String airport) {
    this.airport = airport;
    return this;
  }

  public Flight withStatus(Status status) {
    this.status = status;
    return this;
  }

  public String getId() {
    return "" + id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Flight flight1 = (Flight) o;

    if (airport != null ? !airport.equals(flight1.airport) : flight1.airport != null) return false;
    if (carrier != null ? !carrier.equals(flight1.carrier) : flight1.carrier != null) return false;
    if (flight != null ? !flight.equals(flight1.flight) : flight1.flight != null) return false;
    if (id != null ? !id.equals(flight1.id) : flight1.id != null) return false;
    if (scheduledTime != null ? !scheduledTime.equals(flight1.scheduledTime) : flight1.scheduledTime != null)
      return false;
    if (status != flight1.status) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (carrier != null ? carrier.hashCode() : 0);
    result = 31 * result + (flight != null ? flight.hashCode() : 0);
    result = 31 * result + (scheduledTime != null ? scheduledTime.hashCode() : 0);
    result = 31 * result + (airport != null ? airport.hashCode() : 0);
    result = 31 * result + (status != null ? status.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Flight{" +
        "id=" + id +
        ", carrier='" + carrier + '\'' +
        ", flight='" + flight + '\'' +
        ", scheduledTime=" + scheduledTime +
        ", airport='" + airport + '\'' +
        ", status=" + status +
        '}';
  }

  public enum Status {
    Arrived("A"),
    Cancelled("C"),
    Departed("D"),
    NewTime("E"),
    NewInfo("N"), Unknown("?");
    public final String kode;

    Status(String statuskode) {
      this.kode = statuskode;
    }
  }
}
