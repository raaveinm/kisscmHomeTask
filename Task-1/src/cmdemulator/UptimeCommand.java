package cmdemulator;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class UptimeCommand implements Command {
    @Override
    public String execute(String[] args) {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        long uptimeInMillis = rb.getUptime();
        long uptimeInSeconds = uptimeInMillis / 1000;

        return "Uptime: " + uptimeInSeconds + " seconds";
    }
}