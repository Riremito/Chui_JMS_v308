/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.farm;

import constants.ServerConfig;
import handling.MapleServerHandler;
import handling.channel.PlayerStorage;
import java.io.IOException;
import java.net.InetSocketAddress;
import handling.ServerType;
import handling.netty.ServerConnection;

/**
 *
 * @author Itzik
 */
public class FarmServer {

    private static String ip;
    public static final int PORT = 8601;
    private static ServerConnection init;
    private static PlayerStorage players;
    private static boolean finishedShutdown = false;

    public static void run_startup_configurations() {
        ip = ServerConfig.IP + ":" + PORT;
        players = new PlayerStorage(MapleServerHandler.FARM_SERVER);
        try {
            init = new ServerConnection(PORT, MapleServerHandler.FARM_SERVER, ServerType.農場服務器);
            init.run();
            System.out.println("Farm Server is listening on port 8601.");
        } catch (Exception e) {
            System.err.println("Binding to port 8601 failed");
            throw new RuntimeException("Binding failed.", e);
        }
    }

    public static String getIP() {
        return ip;
    }

    public static PlayerStorage getPlayerStorage() {
        return players;
    }

    public static void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("Saving all connected clients (Farm)...");
        players.disconnectAll();
        System.out.println("Shutting down Farm...");

        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
}
