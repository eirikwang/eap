package no.bekk.fagdag.eip.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hazelcast.HazelcastConstants;
import org.springframework.stereotype.Service;

/**
 *
 */

public class HazelcastListener extends RouteBuilder {
  @Override
  public void configure() throws Exception {
    fromF("hazelcast:%sflights", HazelcastConstants.MAP_PREFIX)
        .id("HAZELCAST listener")
        .choice()
        .when(header(HazelcastConstants.LISTENER_ACTION).in(HazelcastConstants.ADDED, HazelcastConstants.UPDATED))
        .to("direct:updateListeners");
  }
}
