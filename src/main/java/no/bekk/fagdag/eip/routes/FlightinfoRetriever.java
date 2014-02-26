package no.bekk.fagdag.eip.routes;

import no.bekk.fagdag.eip.domain.Flight;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hazelcast.HazelcastConstants;

import java.io.InputStream;
import java.util.Random;

import static org.apache.camel.ExchangePattern.InOnly;

/**
 *
 */

public class FlightinfoRetriever extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        //kjorer ved oppstart for innlasting av flytider
        from("timer:runOnce?repeatCount=1&delay=20000").to("direct:reload");

        //rute for henting av flytider, her fra fil
        //eap's i bruk: contentEnricher, splitter, normalizer/converter
        from("direct:reload")
                .pollEnrich("file:c:/dev/code/untitled/infos/?noop=true")
                .log("${body}")
                .split(body(InputStream.class).tokenize("\n"))
                .convertBodyTo(Flight.class)
                .log("[STARTER] ${body}")
                .to(InOnly, "seda:externalService")
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
                .to("direct:lager", "direct:failSomtimes");

        from("direct:lager")
                .setHeader(HazelcastConstants.OPERATION, constant(HazelcastConstants.PUT_OPERATION))
                .setHeader(HazelcastConstants.OBJECT_ID, simple("body.key"))
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
