package no.bekk.fagdag.eip.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hazelcast.HazelcastConstants;
import org.springframework.stereotype.Service;

import java.util.Random;

import static org.apache.camel.ExchangePattern.InOnly;

/**
 *
 */

public class FlightinfoRetriever extends RouteBuilder {
  @Override
  public void configure() throws Exception {
    //kjorer ved oppstart for innlasting av flytider
    from("timer://avinor?fixedRate=true&period=60000")
        .id("POLLER avinor")
        .to("direct:reload");

    from("direct:processFlight")
        .id("POLLER router")
        .to(InOnly, "seda:lagring")
        .log("[SENDT] ${body}");


    //Rute som setter opp behandling av en flight
    //eap i bruk: throtteler, redelivery
    from("seda:externalService?concurrentConsumers=2")
        .onException(Exception.class).handled(true).useOriginalMessage()
        .maximumRedeliveries(-1).useExponentialBackOff().backOffMultiplier(10)
        .logHandled(false).logStackTrace(false)
        .log("[FAILED] ${body}")
        .end()
            //throtteler. Max 5 meldinger pr. 10 sek.
        .throttle(5).timePeriodMillis(10000)
        .log("[SENDER] ${body}")
        .multicast()
        .to("seda:lagring", "direct:failSomtimes");

    from("seda:lagring?concurrentConsumers=1")
        .id("HAZELCAST avinor")
        .setHeader(HazelcastConstants.OPERATION, constant(HazelcastConstants.PUT_OPERATION))
        .setHeader(HazelcastConstants.OBJECT_ID, simple("${body.id}"))
        .toF("hazelcast:%sflights", HazelcastConstants.MAP_PREFIX);
    from("direct:failSomtimes")
        //Sender alle feil tilbake til hvor den ble kallt fra.
        .errorHandler(noErrorHandler())
        .process(new Processor() {
          @Override
          public void process(Exchange exchange) throws Exception {
            if (new Random().nextInt(10) < 5) {
              throw new Exception("Feiler");
            }
          }
        }).log("[FERDIG] med ${body}");
  }
}
