package handling.cashshop;

import constants.ServerConfig;
import handling.MapleServerHandler;
import handling.channel.PlayerStorage;
import java.io.IOException;
import java.net.InetSocketAddress;
import handling.ServerType;
import handling.netty.ServerConnection;

public class CashShopServer {

    private static String ip;
    public final static int PORT = 8790;
    private static ServerConnection init;
    private static PlayerStorage players;
    private static boolean finishedShutdown = false;

    public static void run_startup_configurations() {
        System.out.print("正在加載商城...");
        ip = ServerConfig.IP + ":" + PORT;
        players = new PlayerStorage(MapleServerHandler.CASH_SHOP_SERVER);
        try {
            init = new ServerConnection(PORT, MapleServerHandler.CASH_SHOP_SERVER, ServerType.商城服務器);
            init.run();
            System.out.println("完成!");
            System.out.println("商城伺服器正在監聽" + PORT + "端口\r\n");
        } catch (final Exception e) {
            System.out.println("失敗!");
            System.err.println("無法綁定" + PORT + "端口");
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
        System.out.println("儲存所有連接的用戶端(商城)...");
        players.disconnectAll();
        System.out.println("正在關閉商城...");
        init.close();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
}
