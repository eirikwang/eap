package no.bekk.fagdag.eip;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import no.bekk.fagdag.eip.routes.ClientNotifier;
import no.bekk.fagdag.eip.routes.FlightinfoRetriever;
import no.bekk.fagdag.eip.routes.HazelcastListener;
import org.apache.camel.main.Main;

/**
 *
 */
public class Runner {
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.addRouteBuilder(new FlightinfoRetriever());
        main.addRouteBuilder(new HazelcastListener());
        main.addRouteBuilder(new ClientNotifier());
        main.run();
    }

    private static class HazelcastServer {
        public static void main(String[] args) throws Exception {
            HazelcastInstance server = Hazelcast.newHazelcastInstance();
            System.in.read();
        }
    }
}
