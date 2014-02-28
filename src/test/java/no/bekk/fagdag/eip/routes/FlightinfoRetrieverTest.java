package no.bekk.fagdag.eip.routes;

import no.bekk.fagdag.eip.domain.Flight;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;

/**
 *
 */

@Ignore
public class FlightinfoRetrieverTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:ut")
    protected MockEndpoint resultEndpoint;
    @Produce(uri = "direct:reload")
    protected ProducerTemplate template;
    private String flights = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" +
            "<airport xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:noNamespaceSchemaLocation=\"http://flydata.avinor.no/XmlFeed.xsd\"\n" +
            "         name=\"OSL\">\n" +
            "    <flights lastUpdate=\"2014-02-27T13:58:27Z\">\n" +
            "        <flight uniqueID=\"3979105\">\n" +
            "            <airline>KL</airline>\n" +
            "            <flight_id>KL1142</flight_id>\n" +
            "            <dom_int>S</dom_int>\n" +
            "            <schedule_time>2014-02-04T08:45:00Z</schedule_time>\n" +
            "            <arr_dep>D</arr_dep>\n" +
            "            <airport>AMS</airport>\n" +
            "            <check_in>B</check_in>\n" +
            "            <status code=\"N\" time=\"2014-02-04T08:45:00Z\"/>\n" +
            "        </flight>\n" +
            "        <flight uniqueID=\"3979278\">\n" +
            "            <airline>BM</airline>\n" +
            "            <flight_id>BM1358</flight_id>\n" +
            "            <dom_int>I</dom_int>\n" +
            "            <schedule_time>2014-02-04T18:15:00Z</schedule_time>\n" +
            "            <arr_dep>D</arr_dep>\n" +
            "            <airport>ABZ</airport>\n" +
            "            <check_in>E</check_in>\n" +
            "            <gate>REM</gate>\n" +
            "            <status code=\"N\" time=\"2014-02-04T18:15:00Z\"/>\n" +
            "        </flight></flights></airport>";

    @Test
    public void testSendMatchingMessage() throws Exception {

        Flight flight1 = new Flight()
                .withId(3979105L)
                .withScheduledTime(new Date(2014, 2, 4, 8, 45))
                .withFlight("KL1142")
                .withAirport("AMS")
                .withCarrier("KL")
                .withStatus(Flight.Status.NewInfo);
        Flight flight2 = new Flight()
                .withId(3979278L)
                .withScheduledTime(new Date(2014, 2, 4, 18, 15))
                .withFlight("BM1358")
                .withAirport("ABZ")
                .withCarrier("BM")
                .withStatus(Flight.Status.NewInfo);
        resultEndpoint.expectedBodiesReceived(flight1, flight2);
        template.sendBody("direct:enrich", flights);
        template.sendBody(flights);
        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new AvinorUpdater().withAvinor("direct:enrich").withProcess("mock:ut");
    }

}
