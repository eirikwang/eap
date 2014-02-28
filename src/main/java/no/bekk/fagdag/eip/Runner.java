package no.bekk.fagdag.eip;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import no.bekk.fagdag.eip.routes.*;
import org.apache.camel.main.Main;

/**
 *
 */
public class Runner {
  public static void main(String[] args) throws Exception {
    Main main = new Main();
    main.enableHangupSupport();
    main.addRouteBuilder(new AvinorUpdater());
    main.addRouteBuilder(new FlightinfoRetriever());
    main.addRouteBuilder(new HazelcastListener());
    main.addRouteBuilder(new ClientNotifier());
    main.addRouteBuilder(new TelnetRouteBuilder());
    main.start();
    main.run();
  }

  private static class HazelcastServer {
    public static void main(String[] args) throws Exception {
      HazelcastInstance server = Hazelcast.newHazelcastInstance();
      System.in.read();
    }
  }
}
