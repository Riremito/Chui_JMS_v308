package scripting;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.MapleQuestStatus;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.OverrideMonsterStats;
import server.maps.Event_PyramidSubway;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.maps.MapleMapObject;
import server.maps.MapleNodes.DirectionInfo;
import server.maps.SavedLocationType;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.NPCPacket;
import tools.packet.CField.UIPacket;
import tools.packet.CWvsContext;
import tools.packet.MobPacket;

public class MapScriptMethods {

    private static final Point witchTowerPos = new Point(-60, 184);

    public static enum MapScriptType {

        directionInfo,
        onFirstUserEnter,
        onUserEnter,
        UNK;

        private static MapScriptType fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return UNK;
            }
        }
    }

    public static void startDirectionInfo(MapleCharacter chr, boolean start) {
        final MapleClient c = chr.getClient();
        DirectionInfo di = chr.getMap().getDirectionInfo(start ? 0 : chr.getDirection());
        if (di != null && di.eventQ.size() > 0) {
            if (start) {
                c.announce(UIPacket.disableOthers(true));
                c.announce(UIPacket.getDirectionInfo(3, 4));
            } else {
                for (String s : di.eventQ) {
                    switch (s) {
                        case "merTutorDrecotion01": //direction info: 1 is probably the time
                            c.announce(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/0", 2000, 0, -100, 1, 0));
                            break;
                        case "merTutorDrecotion02":
                            c.announce(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/1", 2000, 0, -100, 1, 0));
                            break;
                        case "merTutorDrecotion03":
                            c.announce(UIPacket.getDirectionInfo(3, 2));
                            c.announce(UIPacket.getDirectionStatus(true));
                            c.announce(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/2", 2000, 0, -100, 1, 0));
                            break;
                        case "merTutorDrecotion04":
                            c.announce(UIPacket.getDirectionInfo(3, 2));
                            c.announce(UIPacket.getDirectionStatus(true));
                            c.announce(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/3", 2000, 0, -100, 1, 0));
                            break;
                        case "merTutorDrecotion05":
                            c.announce(UIPacket.getDirectionInfo(3, 2));
                            c.announce(UIPacket.getDirectionStatus(true));
                            c.announce(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/4", 2000, 0, -100, 1, 0));
                            EventTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    c.announce(UIPacket.getDirectionInfo(3, 2));
                                    c.announce(UIPacket.getDirectionStatus(true));
                                    c.announce(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/5", 2000, 0, -100, 1, 0));
                                }
                            }, 2000);
                            EventTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    c.announce(UIPacket.lockUI(false));
                                    c.announce(CWvsContext.enableActions());
                                }
                            }, 4000);
                            break;
                        case "merTutorDrecotion12":
                            c.announce(UIPacket.getDirectionInfo(3, 2));
                            c.announce(UIPacket.getDirectionStatus(true));
                            c.announce(UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/8", 2000, 0, -100, 1, 0));
                            c.announce(UIPacket.lockUI(false));
                            break;
                        case "merTutorDrecotion21":
                            c.announce(UIPacket.getDirectionInfo(3, 1));
                            c.announce(UIPacket.getDirectionStatus(true));
                            MapleMap mapto = c.getChannelServer().getMapFactory().getMap(910150005);
                            c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                            break;
                        default: {
                            if (c.getPlayer().isShowErr()) {
                                c.getPlayer().showInfo("directionInfo", true, "找不到地圖directionInfo：" + s + c.getPlayer().getMap());
                            }
                        }
                    }

                    if (c.getPlayer().isShowInfo()) {
                        c.getPlayer().showInfo("directionInfo", false, "開始地圖directionInfo：" + s + c.getPlayer().getMap());
                    }
                }
            }
            c.announce(UIPacket.getDirectionInfo(1, 2000));
            chr.setDirection(chr.getDirection() + 1);
            if (chr.getMap().getDirectionInfo(chr.getDirection()) == null) {
                chr.setDirection(-1);
            }
        } else if (start) {
            switch (chr.getMapId()) {
                //hack
                case 931050300:
                    while (chr.getLevel() < 10) {
                        chr.levelUp();
                    }
                    final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(931050000);
                    chr.changeMap(mapto, mapto.getPortal(0));
                    break;
            }
        }
    }

    public static void startScript_FirstUser(MapleClient c, String scriptName) {
        if (c.getPlayer() == null) {
            return;
        }
        switch (scriptName) {
            case "mCastle_enter":
                c.announce(CField.MapEff("event/mCastle"));
                break;
            case "mapFU_910028310":
                final MapleMap map = c.getPlayer().getMap();
                map.resetFully();
                c.getPlayer().getMap().startMapEffect("Be sure to clean up the Party Room!", 5120079);
                break;
            case "mapFU_910028360":
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().startMapEffect("Get rid of the Whipped Cream Wight.", 5120079);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9500579), new Point(733, 146));
                break;
            case "mapFU_910028330":
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().startMapEffect("Hunt down Witch Cats and collect 10 Party Outfix Boxes.", 5120079);
                break;
            case "mapFU_910028350":
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().startMapEffect("Vanquish those ghosts and find the letter.", 5120079);
                break;
            case "boss_Event_PinkZakum":
                c.getPlayer().getMap().startMapEffect("DO NOT BE ALARMED! The Pink Zakum clone was just to help adventurers like you relieve stress!", 5120039);
                break;
            case "dojang_Eff": {
                if (c.getPlayer().getMapId() == 925020100 || c.getPlayer().getMapId() == 925030100 || c.getPlayer().getMapId() == 925040100) {
                    c.getPlayer().getMap().startMapEffect("Don't forget that you have to clear it within the time limit! Take down the monster and head to the next floor!", 5120024);
                }
                int temp = (c.getPlayer().getMapId() - 925000000) / 100;
                int stage = (int) (temp - ((temp / 100) * 100));
//                String lol = c.getPlayer().getInfoQuest((int)7214);
//                System.err.println("ol " + lol);
//                int ad = Integer.parseInt(lol);

                sendDojoClock(c, 120);//getTiming(stage) * 60);
                sendDojoStart(c, stage - getDojoStageDec(stage));
                break;
            }
            case "PinkBeen_before": {
                handlePinkBeanStart(c);
                break;
            }
            case "onRewordMap": {
                reloadWitchTower(c);
                break;
            }
            //5120019 = orbis(start_itemTake - onUser)
            case "moonrabbit_mapEnter": {
                c.getPlayer().getMap().startMapEffect("Gather the Primrose Seeds around the moon and protect the Moon Bunny!", 5120016);
                break;
            }

            case "Ranmaru_Before": {
                if (c.getPlayer().getMap().getMobsSize() == 0) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9421581), new Point(109, 123));
                    break;
                }
            }
            case "pierre_Summon": {
                if (c.getPlayer().getMap().getMobsSize() == 0) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8900000), new Point(497, 551));
                    break;
                }
            }
            case "queen_summon0": {
                if (c.getPlayer().getMap().getMobsSize() == 0) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8920000), new Point(4, 135));
                    break;
                }
            }
            case "abysscave_ent": {
                if (c.getPlayer().getMap().getMobsSize() == 0) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8930000), new Point(256, 443));
                    break;
                }
            }
            case "banban_Summon": {
                if (c.getPlayer().getMap().getMobsSize() == 0) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8910000), new Point(256, 443));
                    break;
                }
            }

            case "StageMsg_goddess": {
                switch (c.getPlayer().getMapId()) {
                    case 920010000:
                        c.getPlayer().getMap().startMapEffect("Please save me by collecting Cloud Pieces!", 5120019);
                        break;
                    case 920010100:
                        c.getPlayer().getMap().startMapEffect("Bring all the pieces here to save Minerva!", 5120019);
                        break;
                    case 920010200:
                        c.getPlayer().getMap().startMapEffect("Destroy the monsters and gather Statue Pieces!", 5120019);
                        break;
                    case 920010300:
                        c.getPlayer().getMap().startMapEffect("Destroy the monsters in each room and gather Statue Pieces!", 5120019);
                        break;
                    case 920010400:
                        c.getPlayer().getMap().startMapEffect("Play the correct LP of the day!", 5120019);
                        break;
                    case 920010500:
                        c.getPlayer().getMap().startMapEffect("Find the correct combination!", 5120019);
                        break;
                    case 920010600:
                        c.getPlayer().getMap().startMapEffect("Destroy the monsters and gather Statue Pieces!", 5120019);
                        break;
                    case 920010700:
                        c.getPlayer().getMap().startMapEffect("Get the right combination once you get to the top!", 5120019);
                        break;
                    case 920010800:
                        c.getPlayer().getMap().startMapEffect("Summon and defeat Papa Pixie!", 5120019);
                        break;
                }
                break;
            }
            case "StageMsg_crack": {
                switch (c.getPlayer().getMapId()) {
                    case 922010100:
                        c.getPlayer().getMap().startMapEffect("Defeat all the Ratz!", 5120018);
                        break;
                    case 922010200:
                        c.getPlayer().getMap().startSimpleMapEffect("Collect all the passes!", 5120018);
                        break;
                    case 922010300:
                        c.getPlayer().getMap().startMapEffect("Destroy the monsters!", 5120018);
                        break;
                    case 922010400:
                        c.getPlayer().getMap().startMapEffect("Destroy the monsters in each room!", 5120018);
                        break;
                    case 922010500:
                        c.getPlayer().getMap().startMapEffect("Collect passes from each room!", 5120018);
                        break;
                    case 922010600:
                        c.getPlayer().getMap().startMapEffect("Get to the top!", 5120018);
                        break;
                    case 922010700:
                        c.getPlayer().getMap().startMapEffect("Destroy the Rombots!", 5120018);
                        break;
                    case 922010800:
                        c.getPlayer().getMap().startSimpleMapEffect("Get the right combination!", 5120018);
                        break;
                    case 922010900:
                        c.getPlayer().getMap().startMapEffect("Defeat Alishar!", 5120018);
                        break;
                }
                break;
            }
            case "StageMsg_together": {
                switch (c.getPlayer().getMapId()) {
                    case 103000800:
                        c.getPlayer().getMap().startMapEffect("Solve the question and gather the amount of passes!", 5120017);
                        break;
                    case 103000801:
                        c.getPlayer().getMap().startMapEffect("Get on the ropes and unveil the correct combination!", 5120017);
                        break;
                    case 103000802:
                        c.getPlayer().getMap().startMapEffect("Get on the platforms and unveil the correct combination!", 5120017);
                        break;
                    case 103000803:
                        c.getPlayer().getMap().startMapEffect("Get on the barrels and unveil the correct combination!", 5120017);
                        break;
                    case 103000804:
                        c.getPlayer().getMap().startMapEffect("Defeat King Slime and his minions!", 5120017);
                        break;
                }
                break;
            }
            case "StageMsg_romio": {
                switch (c.getPlayer().getMapId()) {
                    case 926100000:
                        c.getPlayer().getMap().startMapEffect("Please find the hidden door by investigating the Lab!", 5120021);
                        break;
                    case 926100001:
                        c.getPlayer().getMap().startMapEffect("Find  your way through this darkness!", 5120021);
                        break;
                    case 926100100:
                        c.getPlayer().getMap().startMapEffect("Fill the beakers to power the energy!", 5120021);
                        break;
                    case 926100200:
                        c.getPlayer().getMap().startMapEffect("Get the files for the experiment through each door!", 5120021);
                        break;
                    case 926100203:
                        c.getPlayer().getMap().startMapEffect("Please defeat all the monsters!", 5120021);
                        break;
                    case 926100300:
                        c.getPlayer().getMap().startMapEffect("Find your way through the Lab!", 5120021);
                        break;
                    case 926100401:
                        c.getPlayer().getMap().startMapEffect("Please, protect my love!", 5120021);

                        break;
                }
                break;
            }
            case "StageMsg_juliet": {
                switch (c.getPlayer().getMapId()) {
                    case 926110000:
                        c.getPlayer().getMap().startMapEffect("Please find the hidden door by investigating the Lab!", 5120022);
                        break;
                    case 926110001:
                        c.getPlayer().getMap().startMapEffect("Find  your way through this darkness!", 5120022);
                        break;
                    case 926110100:
                        c.getPlayer().getMap().startMapEffect("Fill the beakers to power the energy!", 5120022);
                        break;
                    case 926110200:
                        c.getPlayer().getMap().startMapEffect("Get the files for the experiment through each door!", 5120022);
                        break;
                    case 926110203:
                        c.getPlayer().getMap().startMapEffect("Please defeat all the monsters!", 5120022);
                        break;
                    case 926110300:
                        c.getPlayer().getMap().startMapEffect("Find your way through the Lab!", 5120022);
                        break;
                    case 926110401:
                        c.getPlayer().getMap().startMapEffect("Please, protect my love!", 5120022);
                        break;
                }
                break;
            }
            case "party6weatherMsg": {
                switch (c.getPlayer().getMapId()) {
                    case 930000000:
                        c.getPlayer().getMap().startMapEffect("Step in the portal to be transformed.", 5120023);
                        break;
                    case 930000100:
                        c.getPlayer().getMap().startMapEffect("Defeat the poisoned monsters!", 5120023);
                        break;
                    case 930000200:
                        c.getPlayer().getMap().startMapEffect("Eliminate the spore that blocks the way by purifying the poison!", 5120023);
                        break;
                    case 930000300:
                        c.getPlayer().getMap().startMapEffect("Uh oh! The forest is too confusing! Find me, quick!", 5120023);
                        break;
                    case 930000400:
                        c.getPlayer().getMap().startMapEffect("Purify the monsters by getting Purification Marbles from me!", 5120023);
                        break;
                    case 930000500:
                        c.getPlayer().getMap().startMapEffect("Find the Purple Magic Stone!", 5120023);
                        break;
                    case 930000600:
                        c.getPlayer().getMap().startMapEffect("Place the Magic Stone on the altar!", 5120023);
                        break;
                }
                break;
            }
            case "prisonBreak_mapEnter": {
                break;
            }
            case "StageMsg_davy": {
                switch (c.getPlayer().getMapId()) {
                    case 925100000:
                        c.getPlayer().getMap().startMapEffect("Defeat the monsters outside of the ship to advance!", 5120020);
                        break;
                    case 925100100:
                        c.getPlayer().getMap().startMapEffect("We must prove ourselves! Get me Pirate Medals!", 5120020);
                        break;
                    case 925100200:
                        c.getPlayer().getMap().startMapEffect("Defeat the guards here to pass!", 5120020);
                        break;
                    case 925100300:
                        c.getPlayer().getMap().startMapEffect("Eliminate the guards here to pass!", 5120020);
                        break;
                    case 925100400:
                        c.getPlayer().getMap().startMapEffect("Lock the doors! Seal the root of the Ship's power!", 5120020);
                        break;
                    case 925100500:
                        c.getPlayer().getMap().startMapEffect("Destroy the Lord Pirate!", 5120020);
                        break;
                }
                final EventManager em = c.getChannelServer().getEventSM().getEventManager("Pirate");
                if (c.getPlayer().getMapId() == 925100500 && em != null && em.getProperty("stage5") != null) {
                    int mobId = Randomizer.nextBoolean() ? 9300107 : 9300119; //lord pirate
                    final int st = Integer.parseInt(em.getProperty("stage5"));
                    switch (st) {
                        case 1:
                            mobId = Randomizer.nextBoolean() ? 9300119 : 9300105; //angry
                            break;
                        case 2:
                            mobId = Randomizer.nextBoolean() ? 9300106 : 9300105; //enraged
                            break;
                    }
                    final MapleMonster shammos = MapleLifeFactory.getMonster(mobId);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                    }
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(shammos, new Point(411, 236));
                }
                break;
            }
            case "astaroth_summon": {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400633), new Point(600, -26)); //rough estimate
                break;
            }
            case "boss_Ravana_mirror":
            case "boss_Ravana": { //event handles this so nothing for now until i find out something to do with it
                c.getPlayer().getMap().broadcastMessage(CWvsContext.broadcastMsg(5, "Ravana has appeared!"));
                break;
            }
            case "killing_BonusSetting": { //spawns monsters according to mapid
                //910320010-910320029 = Train 999 bubblings.
                //926010010-926010029 = 30 Yetis
                //926010030-926010049 = 35 Yetis
                //926010050-926010069 = 40 Yetis
                //926010070-926010089 - 50 Yetis (specialized? immortality)
                c.getPlayer().getMap().resetFully();
                c.announce(CField.showEffect("killing/bonus/bonus"));
                c.announce(CField.showEffect("killing/bonus/stage"));
                Point pos1 = null, pos2 = null, pos3 = null;
                int spawnPer = 0;
                int mobId = 0;
                //9700019, 9700029
                //9700021 = one thats invincible
                if (c.getPlayer().getMapId() >= 910320010 && c.getPlayer().getMapId() <= 910320029) {
                    pos1 = new Point(121, 218);
                    pos2 = new Point(396, 43);
                    pos3 = new Point(-63, 43);
                    mobId = 9700020;
                    spawnPer = 10;
                } else if (c.getPlayer().getMapId() >= 926010010 && c.getPlayer().getMapId() <= 926010029) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 10;
                } else if (c.getPlayer().getMapId() >= 926010030 && c.getPlayer().getMapId() <= 926010049) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 15;
                } else if (c.getPlayer().getMapId() >= 926010050 && c.getPlayer().getMapId() <= 926010069) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700019;
                    spawnPer = 20;
                } else if (c.getPlayer().getMapId() >= 926010070 && c.getPlayer().getMapId() <= 926010089) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9700029;
                    spawnPer = 20;
                } else {
                    break;
                }
                for (int i = 0; i < spawnPer; i++) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos1));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos2));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos3));
                }
                c.getPlayer().startMapTimeLimitTask(120, c.getPlayer().getMap().getReturnMap());
                break;
            }

            case "mPark_summonBoss": {
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getEventInstance().getProperty("boss") != null && c.getPlayer().getEventInstance().getProperty("boss").equals("0")) {
                    for (int i = 9800119; i < 9800125; i++) {
                        final MapleMonster boss = MapleLifeFactory.getMonster(i);
                        c.getPlayer().getEventInstance().registerMonster(boss);
                        c.getPlayer().getMap().spawnMonsterOnGroundBelow(boss, new Point(c.getPlayer().getMap().getPortal(2).getPosition()));
                    }
                }
                break;
            }
            case "shammos_Fenter": {
                if (c.getPlayer().getMapId() >= 921120100 && c.getPlayer().getMapId() < 921120300) {
                    final MapleMonster shammos = MapleLifeFactory.getMonster(9300275);
                    if (c.getPlayer().getEventInstance() != null) {
                        int averageLevel = 0, size = 0;
                        for (MapleCharacter pl : c.getPlayer().getEventInstance().getPlayers()) {
                            averageLevel += pl.getLevel();
                            size++;
                        }
                        if (size <= 0) {
                            return;
                        }
                        averageLevel /= size;
                        shammos.changeLevel(averageLevel);
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                        if (c.getPlayer().getEventInstance().getProperty("HP") == null) {
                            c.getPlayer().getEventInstance().setProperty("HP", averageLevel + "000");
                        }
                        shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                    }
                    c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                    shammos.switchController(c.getPlayer(), false);
                    c.announce(MobPacket.getNodeProperties(shammos, c.getPlayer().getMap()));

                    /*} else if (c.getPlayer().getMapId() == (GameConstants.GMS ? 921120300 : 921120500) && c.getPlayer().getMap().getAllMonstersThreadsafe().size() == 0) {
                     final MapleMonster shammos = MapleLifeFactory.getMonster(9300281);
                     if (c.getPlayer().getEventInstance() != null) {
                     int averageLevel = 0, size = 0;
                     for (MapleCharacter pl : c.getPlayer().getEventInstance().getPlayers()) {
                     averageLevel += pl.getLevel();
                     size++;
                     }
                     if (size <= 0) {
                     return;
                     }
                     averageLevel /= size;
                     shammos.changeLevel(Math.max(120, Math.min(200, averageLevel)));
                     }
                     c.getPlayer().getMap().spawnMonsterOnGroundBelow(shammos, new Point(350, 170));*/
                }
                break;
            }
            //5120038 =  dr bing. 5120039 = visitor lady. 5120041 = unknown dr bing.
            case "iceman_FEnter": {
                if (c.getPlayer().getMapId() >= 932000100 && c.getPlayer().getMapId() < 932000300) {
                    final MapleMonster shammos = MapleLifeFactory.getMonster(9300438);
                    if (c.getPlayer().getEventInstance() != null) {
                        int averageLevel = 0, size = 0;
                        for (MapleCharacter pl : c.getPlayer().getEventInstance().getPlayers()) {
                            averageLevel += pl.getLevel();
                            size++;
                        }
                        if (size <= 0) {
                            return;
                        }
                        averageLevel /= size;
                        shammos.changeLevel(averageLevel);
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                        if (c.getPlayer().getEventInstance().getProperty("HP") == null) {
                            c.getPlayer().getEventInstance().setProperty("HP", averageLevel + "000");
                        }
                        shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                    }
                    c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                    shammos.switchController(c.getPlayer(), false);
                    c.announce(MobPacket.getNodeProperties(shammos, c.getPlayer().getMap()));

                }
                break;
            }
            case "PRaid_D_Fenter": {
                switch (c.getPlayer().getMapId() % 10) {
                    case 0:
                        c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120033);
                        break;
                    case 1:
                        c.getPlayer().getMap().startMapEffect("Break the boxes and eliminate the monsters!", 5120033);
                        break;
                    case 2:
                        c.getPlayer().getMap().startMapEffect("Eliminate the Officer!", 5120033);
                        break;
                    case 3:
                        c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120033);
                        break;
                    case 4:
                        c.getPlayer().getMap().startMapEffect("Find the way to the other side!", 5120033);
                        break;
                }
                break;
            }
            case "PRaid_B_Fenter": {
                c.getPlayer().getMap().startMapEffect("Defeat the Ghost Ship Captain!", 5120033);
                break;
            }
            case "summon_pepeking": {
                c.getPlayer().getMap().resetFully();
                final int rand = Randomizer.nextInt(10);
                int mob_ToSpawn = 100100;
                if (rand >= 4) { //60%
                    mob_ToSpawn = 3300007;
                } else if (rand >= 1) {
                    mob_ToSpawn = 3300006;
                } else {
                    mob_ToSpawn = 3300005;
                }
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mob_ToSpawn), c.getPlayer().getPosition());
                break;
            }
            case "Xerxes_summon": {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(6160003), c.getPlayer().getPosition());
                break;
            }
            case "shammos_FStart":
                c.getPlayer().getMap().startMapEffect("Defeat the monsters!", 5120035);
                break;
            case "kenta_mapEnter":
                switch ((c.getPlayer().getMapId() / 100) % 10) {
                    case 1:
                        c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120052);
                        break;
                    case 2:
                        c.getPlayer().getMap().startMapEffect("Get me 20 Air Bubbles for me to survive!", 5120052);
                        break;
                    case 3:
                        c.getPlayer().getMap().startMapEffect("Help! Make sure I live for three minutes!", 5120052);
                        break;
                    case 4:
                        c.getPlayer().getMap().startMapEffect("Eliminate the two Pianus!", 5120052);
                        break;
                } //TODOO find out which one it really is, lol
                break;
            case "cygnus_Summon": {
                c.getPlayer().getMap().startMapEffect("It's been a long time since I've had guests. It's been even longer since any have left alive.", 5120043);
                break;
            }
            case "iceman_Boss": {
                c.getPlayer().getMap().startMapEffect("You will perish!", 5120050);
                break;
            }
            case "Visitor_Cube_poison": {
                c.getPlayer().getMap().startMapEffect("Eliminate all the monsters!", 5120039);
                break;
            }
            case "Visitor_Cube_Hunting_Enter_First": {
                c.getPlayer().getMap().startMapEffect("Eliminate all the Visitors!", 5120039);
                break;
            }
            case "VisitorCubePhase00_Start": {
                c.getPlayer().getMap().startMapEffect("Eliminate all the flying monsters!", 5120039);
                break;
            }
            case "visitorCube_addmobEnter": {
                c.getPlayer().getMap().startMapEffect("Eliminate all the monsters by moving around the map!", 5120039);
                break;
            }
            case "Visitor_Cube_PickAnswer_Enter_First_1": {
                c.getPlayer().getMap().startMapEffect("One of the aliens must have a clue to the way out.", 5120039);
                break;
            }
            case "visitorCube_medicroom_Enter": {
                c.getPlayer().getMap().startMapEffect("Eliminate all of the Unjust Visitors!", 5120039);
                break;
            }
            case "visitorCube_iceyunna_Enter": {
                c.getPlayer().getMap().startMapEffect("Eliminate all of the Speedy Visitors!", 5120039);
                break;
            }
            case "Visitor_Cube_AreaCheck_Enter_First": {
                c.getPlayer().getMap().startMapEffect("The switch at the top of the room requires a heavy weight.", 5120039);
                break;
            }
            case "visitorCube_boomboom_Enter": {
                c.getPlayer().getMap().startMapEffect("The enemy is powerful! Watch out!", 5120039);
                break;
            }
            case "visitorCube_boomboom2_Enter": {
                c.getPlayer().getMap().startMapEffect("This Visitor is strong! Be careful!", 5120039);
                break;
            }
            case "CubeBossbang_Enter": {
                c.getPlayer().getMap().startMapEffect("This is it! Give it your best shot!", 5120039);
                break;
            }
            case "MalayBoss_Int":
            case "storymap_scenario":
            case "VanLeon_Before":
            case "dojang_Msg":
            case "balog_summon":
            case "easy_balog_summon": { //we dont want to reset
                break;
            }
            case "metro_firstSetting":
            case "killing_MapSetting":
            case "Sky_TrapFEnter":
            case "balog_bonusSetting": { //not needed
                c.getPlayer().getMap().resetFully();
                break;
            }
            case "magnus_summon": {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8880000), c.getPlayer().getPosition());
                for (int i = 0; i < 30; i++) {
                    c.getPlayer().getMap().spawnObtacleAtom();
                }
                break;
            }
            case "magnus_summon_N": {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8880002), c.getPlayer().getPosition());
                for (int i = 0; i < 30; i++) {
                    c.getPlayer().getMap().spawnObtacleAtom();
                }
                break;
            }
            default: {
                if (c.getPlayer().isShowErr()) {
                    c.getPlayer().showInfo("onFirstUserEnter", true, "找不到地圖onFirstUserEnter：" + scriptName + c.getPlayer().getMap());
                    c.getPlayer().showInfo("onFirstUserEnter", true, "開始打開對應JS腳本");
                }
                NPCScriptManager.getInstance().onFirstUserEnter(c, scriptName);
                return;
            }
        }

        if (c.getPlayer().isShowInfo()) {
            c.getPlayer().showInfo("onFirstUserEnter", false, "開始地圖onFirstUserEnter：" + scriptName + c.getPlayer().getMap());
        }
    }

    @SuppressWarnings("empty-statement")
    public static void startScript_User(final MapleClient c, String scriptName) {
        if (c.getPlayer() == null) {
            return;
        }
        String data = "";
        switch (scriptName) {

            case "direction_59070b": {
                c.announce(UIPacket.getDirectionStatus(true));
                c.announce(UIPacket.lockUI(true));
                c.announce(UIPacket.disableOthers(true));
                c.announce(UIPacket.getDirectionInfo(3, 0));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9390336, "BeastTamerQuestLine4");
//                Thread.sleep(2000);
                c.announce(UIPacket.lockUI(false));
                c.announce(UIPacket.disableOthers(false));
                break;
            }

            case "direction_59070": {
                c.announce(UIPacket.getDirectionStatus(true));
                c.announce(UIPacket.getDirectionInfo(3, 0));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9390336, "BeastTamerQuestLine3");
                break;
            }

            case "direction_59063": {
                try {
                    if (c.getPlayer().getQuestStatus(59063) == 1) {
                        MapleQuest.getInstance(59063).forceComplete(c.getPlayer(), 0);
                    }
                    c.announce(CWvsContext.getTopMsg("On voyage to Nautilus."));
                    c.announce(CField.getClock(1 * 30));
                    Thread.sleep(30000);
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(866000240);
                    c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                } catch (InterruptedException e) {
                }
                break;
            }

            case "direction_59061": {
                if (c.getPlayer().getQuestStatus(59061) == 1) {
                    MapleQuest.getInstance(59061).forceComplete(c.getPlayer(), 0);
                    //MapleQuest.getInstance(59063).forceStart(c.getPlayer(), 0, null);
                    //MapleMap mapto = c.getChannelServer().getMapFactory().getMap(866000230);
                    //c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                }
                //c.announce(UIPacket.getDirectionStatus(true));
                //c.announce(UIPacket.getDirectionInfo(3, 0));
                break;
            }

            case "enter_866033000": {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9390915), new Point(-153, 49));
                break;
            }

            case "direction_59054": {
                try {
                    c.announce(UIPacket.getDirectionStatus(true));
                    c.announce(UIPacket.lockUI(false));
                    c.announce(UIPacket.disableOthers(true));
                    c.announce(UIPacket.getDirectionInfo(3, 1));
                    c.announce(UIPacket.getDirectionInfo(1, 100));
                    Thread.sleep(100);
                    c.announce(UIPacket.getDirectionInfo(3, 0));
                    c.announce(UIPacket.getDirectionInfoNew((byte) 0, 500, 575, 865));
                    c.announce(UIPacket.getDirectionInfo(1, 2825));
                    Thread.sleep(2825);
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 9390313, "BeastTamerQuestLine1");
                } catch (InterruptedException e) {
                }
                break;
            }

            case "root_camera": {
                if (c.getPlayer().getQuestStatus(30000) == 1) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 1064026, "AbyssTut01");
                    break;
                }
            }

            case "root_ereb00": {
                c.announce(CField.UIPacket.lockUI(true));
                c.announce(CField.UIPacket.disableOthers(true));
                if (!c.getPlayer().getMap().containsNPC(1064026)) {
                    c.getPlayer().getMap().spawnNpc(1064026, new Point(-113, 88));
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1064026, "AbyssTut00");
                break;
            }

            case "enter_101072002": {
                c.getPlayer().getMap().resetFully();
                c.announce(CField.UIPacket.lockUI(true));
                c.announce(CField.UIPacket.disableOthers(true));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1500004, null);
                break;
            }
            case "enter_101073300": {
                c.getPlayer().getMap().resetFully();
                if (c.getPlayer().getQuestStatus(32128) == 1) {
                    MapleQuest.getInstance(32128).forceComplete(c.getPlayer(), 0);
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1500016, null);
                break;
            }

            case "enter_101073201": {
                c.getPlayer().getMap().resetFully();
                c.announce(CField.UIPacket.lockUI(true));
                c.announce(CField.UIPacket.disableOthers(true));
                if (!c.getPlayer().getMap().containsNPC(1500026)) {
                    c.getPlayer().getMap().spawnNpc(1500026, new Point(-369, 245));
                }
                if (!c.getPlayer().getMap().containsNPC(1500031)) {
                    c.getPlayer().getMap().spawnNpc(1500031, new Point(55, 245));
                }
                if (!c.getPlayer().getMap().containsNPC(1500032)) {
                    c.getPlayer().getMap().spawnNpc(1500032, new Point(200, 245));
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1500026, null);
                break;
            }

            case "enter_101073110": {
                c.getPlayer().getMap().resetFully();
                if (c.getPlayer().getQuestStatus(32126) == 1) {
                    MapleQuest.getInstance(32126).forceComplete(c.getPlayer(), 0);
                }
                c.announce(CField.getClock(10 * 60));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1500019, null);
                break;
            }

            case "enter_101073010": {
                c.getPlayer().getMap().resetFully();
                if (c.getPlayer().getQuestStatus(32123) == 1) {
                    MapleQuest.getInstance(32123).forceComplete(c.getPlayer(), 0);
                }
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(3501006), new Point(-187, 245));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(3501006), new Point(-187, 245));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(3501006), new Point(-187, 245));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(3501006), new Point(-187, 245));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(3501006), new Point(-187, 245));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(3501006), new Point(-53, 185));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(3501006), new Point(-53, 185));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(3501006), new Point(-53, 185));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(3501006), new Point(-53, 185));
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(3501006), new Point(-53, 185));
                c.announce(CField.getClock(10 * 60));
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1500017, null);
                break;
            }

            case "enter_101070000": {
                try {
                    c.announce(CWvsContext.getTopMsg("The forest of fairies seems to materialize from nowhere as you exit the passage."));
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                c.announce(CField.MapEff("temaD/enter/fairyAcademy"));
                break;
            }

            case "evolvingDirection1": {
                try {
                    MapleQuest.getInstance(1801).forceStart(c.getPlayer(), 9075005, null);
                    c.announce(CField.UIPacket.lockUI(true));
                    c.announce(CField.UIPacket.disableOthers(true));
                    c.announce(CField.MapEff("evolving/mapname"));
                    Thread.sleep(4000);
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9075005, "TutEvolving1");
                break;
            }

            case "evolvingDirection2": {
                try {
                    MapleQuest.getInstance(1801).forceComplete(c.getPlayer(), 0);
                    c.getPlayer().getMap().resetFully();
                    c.announce(CField.UIPacket.lockUI(true));
                    c.announce(CField.UIPacket.disableOthers(true));
                    c.announce(CField.MapEff("evolving/swoo1"));
                    if (!c.getPlayer().getMap().containsNPC(9075004)) {
                        c.getPlayer().getMap().spawnNpc(9075004, new Point(70, 136));
                    }
                    Thread.sleep(14000);
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9075004, "TutEvolving2");
                break;
            }

            case "enter_931060110": {
                c.getPlayer().saveLocation(SavedLocationType.fromString("TUTORIAL"));
                try {
                    c.announce(CField.UIPacket.lockUI(true));
//                    c.announce(CField.UIPacket.getDirectionInfo((byte) 4, 9072200));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 2));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 1200));
                    Thread.sleep(1200);
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 1));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 30));
                    Thread.sleep(30);
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 0));
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9072200, "enter_931060110");
            }
            case "enter_931060120": {
                c.getPlayer().saveLocation(SavedLocationType.fromString("TUTORIAL"));
                try {
                    c.announce(CField.UIPacket.lockUI(true));
//                    c.announce(CField.UIPacket.getDirectionInfo((byte) 4, 9072200));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 2));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 1200));
                    Thread.sleep(1200);
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 1));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 30));
                    Thread.sleep(30);
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 0));
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9072200, "enter_931060120");
            }
            case "rootabyssTakeItem": {
                break;
            }
            case "cannon_tuto_direction": {
                showIntro(c, "Effect/Direction4.img/cannonshooter/Scene00");
                showIntro(c, "Effect/Direction4.img/cannonshooter/out00");
                break;
            }
            case "cannon_tuto_direction1": {
                c.announce(UIPacket.disableOthers(true));
                c.announce(UIPacket.lockKey(true));
                c.announce(UIPacket.getDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balloon/0", 5000, 0, 0, 1, 0));
                c.announce(UIPacket.getDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balloon/1", 5000, 0, 0, 1, 0));
                c.announce(UIPacket.getDirectionInfo("Effect/Direction4.img/effect/cannonshooter/balloon/2", 5000, 0, 0, 1, 0));
                c.announce(EffectPacket.showWZEffect("Effect/Direction4.img/cannonshooter/face04"));//173 ?
                c.announce(EffectPacket.showWZEffect("Effect/Direction4.img/cannonshooter/out01"));//173 ?
                c.announce(UIPacket.getDirectionInfo(1, 5000));
                break;
            }
            case "cannon_tuto_direction2": {
                showIntro(c, "Effect/Direction4.img/cannonshooter/Scene01");
                showIntro(c, "Effect/Direction4.img/cannonshooter/out02");
                break;
            }
            case "shammos_Enter": { //nothing to go on inside the map
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getMapId() == 921120300) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 2022006, null);
                }
                break;
            }
            case "iceman_Enter": { //nothing to go on inside the map
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getMapId() == 932000300) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 2159020, null);
                }
                break;
            }
            case "start_itemTake": { //nothing to go on inside the map
                final EventManager em = c.getChannelServer().getEventSM().getEventManager("OrbisPQ");
                if (em != null && em.getProperty("pre").equals("0")) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.removeClickedNPC();
                    NPCScriptManager.getInstance().start(c, 2013001, null);
                }
                break;
            }
            case "PRaid_W_Enter": {
                c.announce(CWvsContext.sendPyramidEnergy("PRaid_expPenalty", "0"));
                c.announce(CWvsContext.sendPyramidEnergy("PRaid_ElapssedTimeAtField", "0"));
                c.announce(CWvsContext.sendPyramidEnergy("PRaid_Point", "-1"));
                c.announce(CWvsContext.sendPyramidEnergy("PRaid_Bonus", "-1"));
                c.announce(CWvsContext.sendPyramidEnergy("PRaid_Total", "-1"));
                c.announce(CWvsContext.sendPyramidEnergy("PRaid_Team", ""));
                c.announce(CWvsContext.sendPyramidEnergy("PRaid_IsRevive", "0"));
                c.getPlayer().writePoint("PRaid_Point", "-1");
                c.getPlayer().writeStatus("Red_Stage", "1");
                c.getPlayer().writeStatus("Blue_Stage", "1");
                c.getPlayer().writeStatus("redTeamDamage", "0");
                c.getPlayer().writeStatus("blueTeamDamage", "0");
                break;
            }
            case "jail": {
                if (!c.getPlayer().isIntern()) {
                    c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_TIME)).setCustomData(String.valueOf(System.currentTimeMillis()));
                    final MapleQuestStatus stat = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_QUEST));
                    if (stat.getCustomData() != null) {
                        final int seconds = Integer.parseInt(stat.getCustomData());
                        if (seconds > 0) {
                            c.getPlayer().startMapTimeLimitTask(seconds, c.getChannelServer().getMapFactory().getMap(950000100));
                        }
                    }
                }
                break;
            }
            case "TD_neo_BossEnter":
            case "findvioleta": {
                c.getPlayer().getMap().resetFully();
                break;
            }

            case "StageMsg_crack":
                if (c.getPlayer().getMapId() == 922010400) { //2nd stage
                    MapleMapFactory mf = c.getChannelServer().getMapFactory();
                    int q = 0;
                    for (int i = 0; i < 5; i++) {
                        q += mf.getMap(922010401 + i).getAllMonstersThreadsafe().size();
                    }
                    if (q > 0) {
                        c.getPlayer().dropMessage(-1, "There are still " + q + " monsters remaining.");
                    }
                } else if (c.getPlayer().getMapId() >= 922010401 && c.getPlayer().getMapId() <= 922010405) {
                    if (c.getPlayer().getMap().getAllMonstersThreadsafe().size() > 0) {
                        c.getPlayer().dropMessage(-1, "There are still some monsters remaining in this map.");
                    } else {
                        c.getPlayer().dropMessage(-1, "There are no monsters remaining in this map.");
                    }
                }
                break;
            case "q31102e":
                if (c.getPlayer().getQuestStatus(31102) == 1) {
                    MapleQuest.getInstance(31102).forceComplete(c.getPlayer(), 2140000);
                }
                break;
            case "q31103s":
                if (c.getPlayer().getQuestStatus(31103) == 0) {
                    MapleQuest.getInstance(31103).forceComplete(c.getPlayer(), 2142003);
                }
                break;
            case "check_q20833":
                /*if (c.getPlayer().getQuestStatus(20833) == 1) {
                 MapleQuest.getInstance(20833).forceComplete(c.getPlayer(), 0);
                 c.announce(CWvsContext.getTopMsg("Who's that on the right of the map?"));
                 }*/
                break;
            case "q2614M":
                if (c.getPlayer().getQuestStatus(2614) == 1) {
                    MapleQuest.getInstance(2614).forceComplete(c.getPlayer(), 0);
                }
                break;
            case "Resi_tutor20":
                c.announce(CField.MapEff("resistance/tutorialGuide"));
                break;
            case "Resi_tutor30":
                c.announce(EffectPacket.TutInstructionalBalloon("Effect/OnUserEff.img/guideEffect/resistanceTutorial/userTalk"));
                break;
            case "Resi_tutor40":
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 2159012, null);
                break;
            case "Resi_tutor50":
                c.announce(UIPacket.disableOthers(false));
                c.announce(UIPacket.lockKey(false));
                c.announce(CWvsContext.enableActions());
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 2159006, null);
                break;
            case "Resi_tutor70":
                showIntro(c, "Effect/Direction4.img/Resistance/TalkJ");
                break;
            case "Resi_tutor80":
            case "startEreb":
            case "mirrorCave":
            case "babyPigMap":
            case "evanleaveD": {
                c.announce(UIPacket.disableOthers(false));
                c.announce(UIPacket.lockKey(false));
                c.announce(CWvsContext.enableActions());
                break;
            }
            case "dojang_Msg": {
                String[] mulungEffects = {
                    "我等你！ 還有勇氣的話，歡迎再來挑戰！",
                    "想挑戰武陵道場…還真有勇氣！",
                    "挑戰武陵道場的傢伙，我一定會讓他(她)後悔！！",
                    "真是膽大包頭！ 勇敢和無知請不要搞混了！！",
                    "想被稱呼為失敗者嗎？歡迎來挑戰！"};
                c.getPlayer().getMap().startMapEffect(mulungEffects[Randomizer.nextInt(mulungEffects.length)], 5120024);
                break;
            }
            case "dojang_1st": {
                c.getPlayer().writeMulungEnergy();
                break;
            }
            case "undomorphdarco":
            case "reundodraco": {
                c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(2210016), false, -1);
                break;
            }
            case "goAdventure": {
                showIntro(c, "Effect/Direction3.img/goAdventure/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case "crash_Dragon":
                showIntro(c, "Effect/Direction4.img/crash/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case "getDragonEgg":
                showIntro(c, "Effect/Direction4.img/getDragonEgg/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case "meetWithDragon":
                showIntro(c, "Effect/Direction4.img/meetWithDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case "PromiseDragon":
                showIntro(c, "Effect/Direction4.img/PromiseDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case "evanPromotion":
                switch (c.getPlayer().getMapId()) {
                    case 900090000:
                        data = "Effect/Direction4.img/promotion/Scene0" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 900090001:
                        data = "Effect/Direction4.img/promotion/Scene1";
                        break;
                    case 900090002:
                        data = "Effect/Direction4.img/promotion/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 900090003:
                        data = "Effect/Direction4.img/promotion/Scene3";
                        break;
                    case 900090004:
                        c.announce(UIPacket.disableOthers(false));
                        c.announce(UIPacket.lockKey(false));
                        c.announce(CWvsContext.enableActions());
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(900010000);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                        return;
                }
                showIntro(c, data);
                break;
            case "mPark_stageEff":
                c.getPlayer().dropMessage(-1, "All monsters must be eliminated before proceeding to the next stage.");
                switch ((c.getPlayer().getMapId() % 1000) / 100) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        c.announce(CField.MapEff("monsterPark/stageEff/stage"));
                        c.announce(CField.MapEff("monsterPark/stageEff/number/" + (((c.getPlayer().getMapId() % 1000) / 100) + 1)));
                        break;
                    case 4:
                        if (c.getPlayer().getMapId() / 1000000 == 952) {
                            c.announce(CField.MapEff("monsterPark/stageEff/final"));
                        } else {
                            c.announce(CField.MapEff("monsterPark/stageEff/stage"));
                            c.announce(CField.MapEff("monsterPark/stageEff/number/5"));
                        }
                        break;
                    case 5:
                        c.announce(CField.MapEff("monsterPark/stageEff/final"));
                        break;
                }

                break;
            case "TD_MC_title": {
                c.announce(UIPacket.disableOthers(false));
                c.announce(UIPacket.lockKey(false));
                c.announce(CWvsContext.enableActions());
                c.announce(CField.MapEff("temaD/enter/mushCatle"));
                break;
            }
            case "TD_NC_title": {
                switch ((c.getPlayer().getMapId() / 100) % 10) {
                    case 0:
                        c.announce(CField.MapEff("temaD/enter/teraForest"));
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        c.announce(CField.MapEff("temaD/enter/neoCity" + ((c.getPlayer().getMapId() / 100) % 10)));
                        break;
                }
                break;
            }

            case "enter_masRoom": {
                if (c.getPlayer().getQuestStatus(23213) == 1 && c.getPlayer().getQuestStatus(23214) != 1 && c.getPlayer().getQuestStatus(23214) != 2) {;

                    MapleQuest.getInstance(23213).forceComplete(c.getPlayer(), 0);
                    MapleQuest.getInstance(23214).forceStart(c.getPlayer(), 0, "1");
                    final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(931050120); //exit Map
                    c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                }
                break;
            }

            case "enter_23214": {
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9001038), new Point(816, -14));
                break;
            }
            case "map_913070000": {
                try {
                    c.announce(CField.UIPacket.lockUI(true));
                    c.announce(CField.UIPacket.disableOthers(true));
                    c.announce(CWvsContext.getTopMsg("Mr.Limbert's General Store"));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 500));
                    Thread.sleep(500);
                    c.announce(CWvsContext.getTopMsg("Month 3, Day 4"));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.announce(UIPacket.getDirectionInfo("Effect/Direction7.img/effect/tuto/step0/0", 2000, 0, -100, 1, 0));
                    c.announce(UIPacket.getDirectionFacialExpression(6, 10000));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.announce(UIPacket.getDirectionInfo("Effect/Direction7.img/effect/tuto/step0/1", 2000, 0, -100, 1, 0));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.announce(UIPacket.getDirectionInfo("Effect/Direction7.img/effect/tuto/step0/2", 3000, 0, -100, 1, 0));
                    c.announce(UIPacket.getDirectionFacialExpression(4, 8000));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 3000));
                    Thread.sleep(3000);
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 1));
                    Thread.sleep(1000);
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 0));
                    c.announce(UIPacket.getDirectionInfo("Effect/Direction7.img/effect/tuto/step0/3", 2000, 0, -100, 1, 0));
                    c.announce(UIPacket.getDirectionFacialExpression(6, 2000));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 1000));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 1));
                    Thread.sleep(1000);
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 0));
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 9075005, "tuto001");
                break;
            }
            case "map_913070001": {
                c.announce(CWvsContext.getTopMsg("Mr.Limbert's General Store"));
                c.announce(CWvsContext.getTopMsg("Month 3, Day 4"));
                break;
            }
            case "map_913070002": {
                c.announce(CWvsContext.getTopMsg("Mr.Limbert's General Store"));
                c.announce(CWvsContext.getTopMsg("Month 3, Day 8"));
                break;
            }
            case "map_913070020": {
                c.announce(CWvsContext.getTopMsg("Mr.Limbert's General Store"));
                //bigby spawn is not gms like yet
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9001051), new Point(185, 65));
                c.announce(CField.getClock(5 * 60));
                MapTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        if (c.getPlayer().getMapId() == 913070020) {
                            c.getPlayer().changeMap(913070003, 0);
                        }
                    }
                }, 5 * 60 * 1000);
                break;
            }
            case "map_913070004": {
                try {
                    c.announce(CField.UIPacket.lockUI(true));
                    c.announce(CField.UIPacket.disableOthers(true));
                    c.announce(CWvsContext.getTopMsg("Mr.Limbert's General Store"));
                    c.announce(CWvsContext.getTopMsg("Month 3, Day 11"));
                    Thread.sleep(500);
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 2));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 1));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.announce(UIPacket.getDirectionInfo("Effect/Direction7.img/effect/tuto/step0/5", 2000, 0, -100, 1, 0));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 2));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 500));
                    Thread.sleep(500);
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 3, 0));
                    c.announce(UIPacket.getDirectionInfo("Effect/Direction7.img/effect/tuto/step0/6", 2000, 0, -100, 1, 0));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.announce(UIPacket.getDirectionInfo("Effect/Direction7.img/effect/tuto/step0/4", 2000, 0, -100, 1, 0));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.announce(UIPacket.getDirectionInfo("Effect/Direction7.img/effect/tuto/step0/7", 2000, 0, -100, 1, 0));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.announce(UIPacket.getDirectionInfo("Effect/Direction7.img/effect/tuto/step0/8", 2000, 0, -100, 1, 0));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 1000));
                    Thread.sleep(1000);
                    c.announce(CWvsContext.getTopMsg("Someone suspicious is in the back yard..."));
                } catch (InterruptedException ex) {
                }
                c.announce(CField.UIPacket.lockUI(false));
                c.announce(CField.UIPacket.disableOthers(false));
                c.announce(CWvsContext.enableActions());
                break;
            }
            case "map_913070050": {
                try {
                    MapleQuest.getInstance(20034).forceStart(c.getPlayer(), 1106000, null);
                    c.announce(CField.UIPacket.lockUI(true));
                    c.announce(CField.UIPacket.disableOthers(true));
                    c.announce(CWvsContext.getTopMsg("General Store Yard"));
                    c.announce(CField.getClock(10 * 60));
                    MapTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            if (c.getPlayer().getMapId() >= 913070050 && c.getPlayer().getMapId() < 913070070) {
                                c.getPlayer().changeMap(913070004, 0);
                            }
                        }
                    }, 10 * 60 * 1000);
                    c.announce(UIPacket.getDirectionInfo("Effect/Direction7.img/effect/tuto/step0/4", 2000, 0, -100, 1, 0));
                    c.announce(CField.UIPacket.getDirectionInfo((byte) 1, 2000));
                    Thread.sleep(2000);
                    c.announce(UIPacket.getDirectionFacialExpression(6, 10000));
                } catch (InterruptedException ex) {
                }
                NPCScriptManager.getInstance().dispose(c);
                c.removeClickedNPC();
                NPCScriptManager.getInstance().start(c, 1106000, "tuto004");
                break;
            }
            case "mihail_direc": {
                try {
                    c.announce(UIPacket.disableOthers(true));
                    c.announce(UIPacket.lockKey(true));
                    showIntro(c, "Effect/Direction7.img/mikhail/1st_Job");
                    while (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().levelUp();
                    }
                    c.getPlayer().changeJob((short) 5100);
                    Thread.sleep(4000);
                    c.announce(UIPacket.disableOthers(false));
                    c.announce(UIPacket.lockKey(false));
                    c.getPlayer().changeMap(130000000, 0);
                    c.getPlayer().forceChangeChannel(c.getChannel());
                } catch (InterruptedException ex) {
                }
                break;
            }
            case "enter_edelstein":
            case "patrty6_1stIn":
            case "standbyAzwan":
            case "angelic_tuto0":
                if (c.getPlayer().getJob() == 6001) {
                    while (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().levelUp();
                    }
                    c.getPlayer().changeJob((short) 6500);
                    c.getPlayer().gainItem(1142495, 1);//Nova Contractor
                    MapleMap mapto = c.getChannelServer().getMapFactory().getMap(400000000);
                    c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                }
                break;

            case "q53244_dun_in": {
                c.announce(UIPacket.lockUI(false));
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                }
                c.getPlayer().getMap().resetFully();
                c.getPlayer().dropMessage(-1, "Father, There they are. All located in the planets!");
                if (!c.getPlayer().getMap().containsNPC(9270084)) {
                    c.getPlayer().getMap().spawnNpc(9270084, new Point(-103, 55));
                }
                if (!c.getPlayer().getMap().containsNPC(9270090)) {
                    c.getPlayer().getMap().spawnNpc(9270090, new Point(65, 55));
                }
                c.announce(UIPacket.lockUI(true));
                c.announce(UIPacket.getDirectionInfo("Effect/DirectionNewPirate.img/newPirate/balloonMsg2/11", 2000, 0, 1, -100, 1));
                for (int i = 0; i < 10; i++) {
                    c.announce(UIPacket.getDirectionInfo(3, 5));
                    try {
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                    }
                }
                c.announce(UIPacket.getDirectionInfo(3, 2));

                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.announce(UIPacket.getDirectionInfo(3, 0));
                        c.getPlayer().dropMessage(-1, "Heh heh heh, nguoi da cham soc no tot that day!");
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                        c.announce(UIPacket.getDirectionInfo(3, 2));
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                        c.announce(UIPacket.getDirectionInfo(3, 0));
                        //c.announce(UIPacket.getDirectionInfo(4, 1403002));
                        NPCScriptManager.getInstance().start(c, 9270090, "q53244_dun_in");
                    }
                }, 1000);
                break;
            }

            case "q53251_enter": {
                c.announce(UIPacket.lockUI(true));
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                }
                c.getPlayer().getMap().resetFully();
                if (!c.getPlayer().getMap().containsNPC(9270092)) {
                    c.getPlayer().getMap().spawnNpc(9270092, new Point(352, 55));
                }
                c.announce(UIPacket.getDirectionInfo(3, 2));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        NPCScriptManager.getInstance().start(c, 9270092, "q53251_enter");
                    }
                }, 1000);
                //final MapleMap mapmap = c.getChannelServer().getMapFactory().getMap(552000074);
                break;
            }
            case "cannon_tuto_01": {
                c.announce(UIPacket.disableOthers(true));
                c.announce(UIPacket.lockUI(true));
                c.announce(UIPacket.getDirectionStatus(true));
                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(110), (byte) 1, (byte) 1);
                c.announce(UIPacket.getDirectionInfo(3, 0));
//                c.announce(UIPacket.getDirectionInfo(4, 1096000));
                NPCScriptManager.getInstance().dispose(c);
                NPCScriptManager.getInstance().start(c, 1096000, null);
                break;
            }
            case "TD_MC_Openning": {
                showIntro(c, "Effect/Direction2.img/open");
                break;
            }
            case "TD_MC_gasi": {
                showIntro(c, "Effect/Direction2.img/gasi");
                break;
            }
            case "check_count": {
                if (c.getPlayer().getMapId() == 950101010 && (!c.getPlayer().haveItem(4001433, 20) || c.getPlayer().getLevel() < 50)) { //ravana Map
                    final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(950101100); //exit Map
                    c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                }
                break;
            }
            case "Massacre_first": { //sends a whole bunch of shit.
                if (c.getPlayer().getPyramidSubway() == null) {
                    c.getPlayer().setPyramidSubway(new Event_PyramidSubway(c.getPlayer()));
                }
                break;
            }
            case "magnus_enter_HP": {
                if (c.getPlayer().getMapId() >= 401060100 && c.getPlayer().getMapId() < 401060100) {
                    final MapleMonster shammos = MapleLifeFactory.getMonster(8880000);
                    if (c.getPlayer().getEventInstance() != null) {
                        int averageLevel = 0, size = 0;
                        for (MapleCharacter pl : c.getPlayer().getEventInstance().getPlayers()) {
                            averageLevel += pl.getLevel();
                            size++;
                        }
                        if (size <= 0) {
                            return;
                        }
                        averageLevel /= size;
                        shammos.changeLevel(averageLevel);
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                        if (c.getPlayer().getEventInstance().getProperty("HP") == null) {
                            c.getPlayer().getEventInstance().setProperty("HP", averageLevel + "000");
                        }
                        shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                    }
                    c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                    shammos.switchController(c.getPlayer(), false);
                    c.announce(MobPacket.getNodeProperties(shammos, c.getPlayer().getMap()));

                }
                break;
            }

            case "azwan_stageEff": {
                //  c.announce(CWvsContext.getTopMsg("Remove all the monsters in the field need to be able to move to the next stage."));
                switch ((c.getPlayer().getMapId() % 1000) / 100) {
                    case 1:
                    case 2:
                    case 3:
                        c.announce(CField.showEffect("aswan/stageEff/stage"));
                        c.announce(CField.showEffect("aswan/stageEff/number/" + (((c.getPlayer().getMapId() % 1000) / 100))));
                        break;
                }
                synchronized (MapScriptMethods.class) {
                    for (MapleMapObject mon : c.getPlayer().getMap().getAllMonster()) {
                        MapleMonster mob = (MapleMonster) mon;
                        if (mob.getEventInstance() == null) {
                            c.getPlayer().getEventInstance().registerMonster(mob);
                        }
                    }
                }
                break;
            }
            case "Massacre_result": { //clear, give exp, etc.
                //if (c.getPlayer().getPyramidSubway() == null) {
                c.announce(CField.showEffect("killing/fail"));
                //} else {
                //	c.announce(CField.showEffect("killing/clear"));
                //}
                //left blank because pyramidsubway handles this.
                break;
            }
            case "hayatoJobChange": {
                if (c.getPlayer().getJob() == MapleJob.劍豪1轉.getId()) {
                    if (c.getPlayer().getQuestStatus(28862) != 2) {
                        while (c.getPlayer().getLevel() < 10) {
                            c.getPlayer().levelUp();
                        }
                        unequip(c, 1003567, true);
                        unequip(c, 1052473, true);
                        unequip(c, 1072678, true);
                        unequip(c, 1082442, true);
                        unequip(c, 1542044, true);
                        c.getPlayer().forceCompleteQuest(28862);
                    }
                } else {
                    while (c.getPlayer().getLevel() < 10) {
                        c.getPlayer().levelUp();
                    }
                }
            }
            default: {
                NPCScriptManager.getInstance().onUserEnter(c, scriptName);
                return;
            }
        }

        if (c.getPlayer().isShowInfo()) {
            c.getPlayer().showInfo("onUserEnter", false, "開始地圖onUserEnter：" + scriptName + c.getPlayer().getMap());
        }
    }

    private static void equip(MapleClient c, int itemId, byte slot) {
        equip(c, itemId, slot, true);
    }

    private static void equip(MapleClient c, int itemId, byte slot, boolean add) {
        MapleInventory equip = c.getPlayer().getInventory(MapleInventoryType.EQUIP);
        Equip eqp = null;
        for (Item item : equip.newList()) {
            if (item.getItemId() == itemId) {
                eqp = (Equip) item;
            }
        }

        if (eqp == null) {
            if (add) {
                final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
                Item item = li.getEquipById(itemId);
                item.setPosition(equip.getNextFreeSlot());
                item.setGMLog("從地圖腳本中獲得, 時間 " + FileoutputUtil.CurrentReadable_Time());
                eqp = (Equip) item;
                MapleInventoryManipulator.addbyItem(c, eqp);
            } else {
                return;
            }
        }

        MapleInventoryManipulator.equip(c, eqp.getPosition(), slot);
    }

    private static void unequip(MapleClient c, int itemId) {
        unequip(c, itemId, false);
    }

    private static void unequip(MapleClient c, int itemId, boolean remove) {
        MapleInventory equipped = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        MapleInventory equip = c.getPlayer().getInventory(MapleInventoryType.EQUIP);
        Equip eqp = null;
        for (Item item : equipped.newList()) {
            if (item.getItemId() == itemId) {
                eqp = (Equip) item;
            }
        }

        if (eqp == null) {
            return;
        }

        MapleInventoryManipulator.unequip(c, eqp.getPosition(), equip.getNextFreeSlot());
        if (remove) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, eqp.getPosition(), (short) 1, remove);
        }
    }

    private static int getTiming(int ids) {
        if (ids <= 5) {
            return 5;
        } else if (ids >= 7 && ids <= 11) {
            return 6;
        } else if (ids >= 13 && ids <= 17) {
            return 7;
        } else if (ids >= 19 && ids <= 23) {
            return 8;
        } else if (ids >= 25 && ids <= 29) {
            return 9;
        } else if (ids >= 31 && ids <= 35) {
            return 10;
        } else if (ids >= 37 && ids <= 38) {
            return 15;
        }
        return 0;
    }

    private static int getDojoStageDec(int ids) {
        if (ids <= 5) {
            return 0;
        } else if (ids >= 7 && ids <= 11) {
            return 1;
        } else if (ids >= 13 && ids <= 17) {
            return 2;
        } else if (ids >= 19 && ids <= 23) {
            return 3;
        } else if (ids >= 25 && ids <= 29) {
            return 4;
        } else if (ids >= 31 && ids <= 35) {
            return 5;
        } else if (ids >= 37 && ids <= 38) {
            return 6;
        }
        return 0;
    }

    private static void showIntro(final MapleClient c, final String data) {
        c.announce(UIPacket.disableOthers(true));
        c.announce(UIPacket.lockKey(true));
        c.announce(EffectPacket.showWZEffect(data));//176.3 - goArcher
    }

    private static void sendDojoClock(MapleClient c, int time) {
        c.announce(CField.getClock(time));
    }

    private static void sendDojoStart(MapleClient c, int stage) {
        for (int i = 0; i < 3; i++) {
            c.getPlayer().updateInfoQuest(1213, "try=3");
        }
        c.announce(CField.environmentChange("Dojang/start", 5));//was4
        c.announce(CField.environmentChange("dojang/start/stage", 13));//was3
        c.announce(CField.environmentChange("dojang/start/number/" + stage, 13));//was3
        c.announce(CField.trembleEffect(0, 1));
    }

    private static void handlePinkBeanStart(MapleClient c) {
        final MapleMap map = c.getPlayer().getMap();
        map.resetFully();

        if (!map.containsNPC(2141000)) {
            map.spawnNpc(2141000, new Point(-190, -42));
        }
    }

    private static void reloadWitchTower(MapleClient c) {
        final MapleMap map = c.getPlayer().getMap();
        map.killAllMonsters(false);

        final int level = c.getPlayer().getLevel();
        int mob;
        if (level <= 10) {
            mob = 9300367;
        } else if (level <= 20) {
            mob = 9300368;
        } else if (level <= 30) {
            mob = 9300369;
        } else if (level <= 40) {
            mob = 9300370;
        } else if (level <= 50) {
            mob = 9300371;
        } else if (level <= 60) {
            mob = 9300372;
        } else if (level <= 70) {
            mob = 9300373;
        } else if (level <= 80) {
            mob = 9300374;
        } else if (level <= 90) {
            mob = 9300375;
        } else if (level <= 100) {
            mob = 9300376;
        } else {
            mob = 9300377;
        }
        MapleMonster theMob = MapleLifeFactory.getMonster(mob);
        OverrideMonsterStats oms = new OverrideMonsterStats();
        oms.setOMp(theMob.getMobMaxMp());
        oms.setOExp(theMob.getMobExp());
        oms.setOHp((int) (long) Math.ceil(theMob.getMobMaxHp() * (level / 5.0))); //10k to 4m
        theMob.setOverrideStats(oms);
        map.spawnMonsterOnGroundBelow(theMob, witchTowerPos);
    }
}
