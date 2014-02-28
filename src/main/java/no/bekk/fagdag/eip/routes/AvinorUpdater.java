package no.bekk.fagdag.eip.routes;

import no.bekk.fagdag.eip.avinor.Airport;
import no.bekk.fagdag.eip.converters.FlightFromAvinorFlight;
import no.bekk.fagdag.eip.domain.Flight;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.processor.aggregate.UseLatestAggregationStrategy;
import org.springframework.stereotype.Service;

public class AvinorUpdater extends RouteBuilder {
  public String processEndpoint = "direct:processFlight";
  //        .pollEnrich("file:c:/dev/code/untitled/infos/?noop=true")
  private String avinorEndpoint = "http:flydata.avinor.no/XmlFeed.asp";
  //private String avinorEndpointFile = "file:c:/dev/code/untitled/infos/?noop=true&fileName=flights.xml";
  private String lastUpdate;

  @Override
  public void configure() throws Exception {
    JaxbDataFormat jaxbDataFormat = new JaxbDataFormat();
    jaxbDataFormat.setContextPath(Airport.class.getPackage().getName());
    getContext()
        .getTypeConverterRegistry()
        .addTypeConverter(Flight.class, Airport.Flights.Flight.class, new FlightFromAvinorFlight());
    from("direct:reload")
        .id("AvinorFlightRetriever")
            //Sikrer vi ikke gjør mer enn 1 kall pr. 10. sekund
        .throttle(1).timePeriodMillis(10000)
        .log("[LASTER] laster data fra avinor")
        .setHeader("POLL_HTTP").method(this, "lastUpdateTime")
        .log("${headers.POLL_HTTP}")
        .recipientList(header("POLL_HTTP")).aggregationStrategy(new UseLatestAggregationStrategy())
        //.pollEnrich(avinorEndpoint)     //Funggerer ikke med headere. Er en jira inne på å forbedre pollEnrich
        .unmarshal(jaxbDataFormat)
        .bean(this, "updateLastUpdate")
        .setBody(simple("${body.flights.flight}"))
        .split(body())
        .convertBodyTo(Flight.class)
            //.to("direct:processFlight")
        .to(processEndpoint);
    from("direct:resetFlights")
        .id("AvinorFlightReset")
        .log("AVINOR resetting updated time")
        .bean(this, "resetLastUpdate");
  }

  public void resetLastUpdate() {
    this.lastUpdate = "";
  }

  public void updateLastUpdate(Airport airport) {
    this.lastUpdate = airport.getFlights().getLastUpdate().toString();
  }

  public String lastUpdateTime() {
    return avinorEndpoint + "?TimeFrom=1&TimeTo=7&airport=OSL&direction=D&lastUpdate=" + (lastUpdate == null ? "" : lastUpdate);
  }

  public AvinorUpdater withAvinor(String avinorEndpoint) {
    this.avinorEndpoint = avinorEndpoint;
    return this;
  }

  public AvinorUpdater withProcess(String processEndpoint) {
    this.processEndpoint = processEndpoint;
    return this;
  }
}
