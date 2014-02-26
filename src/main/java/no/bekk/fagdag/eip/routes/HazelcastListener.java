package no.bekk.fagdag.eip.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hazelcast.HazelcastConstants;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 *
 */

public class HazelcastListener extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        fromF("hazelcast:%sflights", HazelcastConstants.MAP_PREFIX)
                .choice()
                .when(header(HazelcastConstants.LISTENER_ACTION).in(HazelcastConstants.ADDED, HazelcastConstants.UPDATED))
                .to("direct:updateListeners");
    }
}
