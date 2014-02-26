package no.bekk.fagdag.eip.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.websocket.WebsocketComponent;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 *
 */

public class RestUpdater extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        WebsocketComponent wc = getContext().getComponent("websocket", WebsocketComponent.class);
        //Denne linja gjør at vi starter en json server på port 9191, og server alle ressurser i resources
        wc.setStaticResources("classpath:.");

        from("direct:updateListeners")
                .marshal().json(JsonLibrary.Gson)
                //vi lager en websocket server som får alle oppdateringer
                .to("websocket://flights?sendToAll=true");
    }
}
