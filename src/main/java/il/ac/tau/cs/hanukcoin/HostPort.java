package il.ac.tau.cs.hanukcoin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by talfranji on 14/04/2020.
 */
public record HostPort(String host, int port)  {
    public static void main(String argv[]) {
        Map<HostPort, String> m = new HashMap<HostPort, String>();
        HostPort hp = new HostPort("1.2.3.4", 8080);
        String dummyNodeInfo = "This is value for testiing HostPost as key";
        m.put(hp, dummyNodeInfo);
        String res = m.get(hp);
        System.out.println(res);
    }
}
