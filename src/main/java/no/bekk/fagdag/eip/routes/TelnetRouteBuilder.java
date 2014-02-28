package no.bekk.fagdag.eip.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Service;

/**
 *
 */

public class TelnetRouteBuilder extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("netty:tcp://localhost:8000?sync=true&textline=true")
        .id("Telnet Server")
        .log("fikk kommando: ${body}")
        .choice()
        .when(body(String.class).contains("reset"))
        .to("direct:resetFlights")
        .to("direct:reload")
        .when(body(String.class).startsWith("message"))
        .to("direct:sendMessage")
        .end();

  }
}
