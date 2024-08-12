/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.farm.handler;

import client.MapleCharacter;
import client.MapleClient;
import constants.WorldConstants;
import handling.channel.ChannelServer;
import handling.farm.FarmServer;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.World;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import server.farm.MapleFarm;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.FarmPacket;

/**
 *
 * @author Itzik
 */
public class FarmOperation {

    public static void EnterFarm(final CharacterTransfer transfer, final MapleClient c) {
        if (transfer == null) {
            c.getSession().close();
            return;
        }
        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);

        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());

        if (!c.CheckIPAddress()) { // Remote hack
            c.getSession().close();
            return;
        }

        final int state = c.getLoginState();
        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
            World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()));
        }
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        FarmServer.getPlayerStorage().registerPlayer(chr);
        c.announce(FarmPacket.updateMonster(new LinkedList()));
        c.announce(FarmPacket.enterFarm(c));
        c.announce(FarmPacket.farmQuestData(new LinkedList(), new LinkedList()));
        c.announce(FarmPacket.updateMonsterInfo(new LinkedList()));
        c.announce(FarmPacket.updateAesthetic(c.getFarm().getAestheticPoints()));
        c.announce(FarmPacket.spawnFarmMonster1());
        c.announce(FarmPacket.farmPacket1());
        c.announce(FarmPacket.updateFarmFriends(new LinkedList()));
        c.announce(FarmPacket.updateFarmInfo(c));
        //c.announce(CField.getPacketFromHexString("19 72 1E 02 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 43 72 65 61 74 69 6E 67 2E 2E 2E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 00 00 00 00 00 01 00 00 00 00 0B 00 43 72 65 61 74 69 6E 67 2E 2E 2E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 FF FF FF FF 00"));
        c.announce(FarmPacket.updateQuestInfo(21002, (byte) 1, ""));
        SimpleDateFormat sdfGMT = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        sdfGMT.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        String timeStr = sdfGMT.format(Calendar.getInstance().getTime()).replaceAll("-", "");
        c.announce(FarmPacket.updateQuestInfo(21001, (byte) 1, timeStr));
        c.announce(FarmPacket.updateQuestInfo(21003, (byte) 1, "30"));
        c.announce(FarmPacket.updateUserFarmInfo(chr, false));
        List<Pair<MapleFarm, Integer>> ranking = new LinkedList();
        ranking.add(new Pair<>(MapleFarm.getDefault(1, c, "Pyrous"), 999999));
        ranking.add(new Pair<>(MapleFarm.getDefault(1, c, "Sango"), 1));
        ranking.add(new Pair<>(MapleFarm.getDefault(1, c, "Hemmi"), -1));
        c.announce(FarmPacket.sendFarmRanking(chr, ranking));
        c.announce(FarmPacket.updateAvatar(new Pair<>(WorldConstants.getMainWorld(), chr), null, false));
        if (c.getFarm().getName().equals("Creating...")) {
            c.announce(FarmPacket.updateQuestInfo(1111, (byte) 0, "A1/"));
            c.announce(FarmPacket.updateQuestInfo(2001, (byte) 0, "A1/"));
        }
    }

    public static void LeaveFarm(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        FarmServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        try {
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
            c.announce(CField.getChannelChange(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
        } finally {
            final String s = c.getSessionIPAddress();
            LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
            chr.saveToDB(false, true);
            c.setPlayer(null);
            c.setReceiving(false);
            c.getSession().close();
        }
    }
}
