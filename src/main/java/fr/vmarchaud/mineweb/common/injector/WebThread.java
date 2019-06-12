package fr.vmarchaud.mineweb.common.injector;

import fr.vmarchaud.mineweb.common.*;
import org.bukkit.*;

public class WebThread extends Thread
{
    private final ICore api;
    private NettyServer webServer;
    
    public WebThread(final ICore api) {
        this.api = api;
        this.webServer = new NettyServer(api);
    }
    
    @Override
    public void run() {
        try {
            this.webServer.start();
        }
        catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("HTTP server start failed! (" + e.getMessage() + ")");
            this.api.logger().info("HTTP server start failed! (" + e.getMessage() + ")");
            this.interrupt();
        }
    }
    
    public void stopThread() {
        try {
            this.webServer.stop();
            this.interrupt();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
