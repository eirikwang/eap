package no.bekk.fagdag.eip.converters;

import no.bekk.fagdag.eip.domain.Flight;
import org.apache.camel.Converter;
import org.apache.camel.TypeConversionException;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 */

@Converter
public class FlightFromStringConverter {
    @Converter
    public Flight convertStringToFlight(String value) throws TypeConversionException {
        //1,SK101,2014-01-01,14:50,OnTime
        String[] line = ((String) value).split(",");
        return new Flight().withFlight(line[1])
                .withDate(LocalDate.parse(line[2]))
                .withTime(LocalTime.parse(line[3]))
                .withStatus(Flight.Status.valueOf(line[4].trim()));
    }
}
