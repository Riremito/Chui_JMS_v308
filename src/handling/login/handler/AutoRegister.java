package handling.login.handler;

import client.LoginCrypto;
import client.LoginCryptoLegacy;
import client.MapleClient;
import database.DatabaseConnection;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoRegister {

    private static final int ACCOUNTS_PER_IP = 3;

    public static boolean getAccountExists(String login) {
        boolean accountExists = false;
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM accounts WHERE name = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                accountExists = true;
            }
        } catch (SQLException ex) {
            System.out.println("Ex:" + ex);
        }
        return accountExists;
    }

    public static boolean createAccount(String login, String pwd, String eip) {
        String sockAddr = eip;
        Connection con;

        try {
            con = DatabaseConnection.getConnection();
        } catch (Exception ex) {
            System.out.println("Ex:" + ex);
            return false;
        }

        String password2_hash = null;
        try {
            password2_hash = LoginCryptoLegacy.encodeSHA1("777777");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MapleClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MapleClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        try {
            ResultSet rs;
            try (PreparedStatement ipc = con.prepareStatement("SELECT SessionIP FROM accounts WHERE SessionIP = ?")) {
                ipc.setString(1, sockAddr.substring(1, sockAddr.lastIndexOf(':')));
                rs = ipc.executeQuery();
                if (rs.first() == false || rs.last() == true && rs.getRow() < ACCOUNTS_PER_IP) {
                    try {
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (name, password, email, birthday, macs, SessionIP, 2ndpassword) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                            ps.setString(1, login);
                            ps.setString(2, LoginCrypto.hexSha1(pwd));
                            ps.setString(3, "autoregister@mail.com");
                            ps.setString(4, "2008-04-07 00:00:00");
                            ps.setString(5, "00-00-00-00-00-00");
                            ps.setString(6, sockAddr.substring(1, sockAddr.lastIndexOf(':')));
                            ps.setString(7, password2_hash);
                            ps.executeUpdate();
                        }

                        return true;
                    } catch (SQLException ex) {
                        System.out.println("Ex:" + ex);
                    }
                }
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println("Ex:" + ex);
        }
        return false;
    }
}
