package no.bekk.fagdag.eip.converters;

import no.bekk.fagdag.eip.avinor.Airport;
import no.bekk.fagdag.eip.domain.Flight;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */

@Converter
public class FlightFromAvinorFlight extends TypeConverterSupport {
  public static final Map<String, Flight.Status> koder = new HashMap<String, Flight.Status>();

  static {
    for (Flight.Status kode : Flight.Status.values()) {
      koder.put(kode.kode, kode);
    }
  }

  @Converter
  public Flight convertStringToFlight(Airport.Flights.Flight value) throws TypeConversionException {

    return new Flight()
        .withId(value.getUniqueID().longValue())
        .withAirport(value.getAirport())
        .withCarrier(value.getAirline())
        .withFlight(value.getFlightId())
        .withScheduledTime(fromDate(value.getScheduleTime()))
        .withStatus(value.getStatus() != null ? koder.get(value.getStatus().getCode()) : Flight.Status.Unknown);
  }

  private Date fromDate(XMLGregorianCalendar scheduleTime) {
    if (scheduleTime == null) return null;
    return scheduleTime.toGregorianCalendar().getTime();
  }

  @Override
  public <T> T convertTo(Class<T> tClass, Exchange exchange, Object o) throws TypeConversionException {
    return (T) convertStringToFlight((Airport.Flights.Flight) o);
  }
}
