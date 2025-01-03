package client;

import client.MapleTrait.MapleTraitType;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import constants.EventConstants;
import constants.GameConstants;
import constants.ItemConstants;
import handling.world.World;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildSkill;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.StructItemOption;
import server.StructSetItem;
import server.StructSetItem.SetItem;
import server.Timer;
import server.life.Element;
import tools.Pair;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CWvsContext.InventoryPacket;
import tools.packet.JobPacket;

public class PlayerStats implements Serializable {

    private static final long serialVersionUID = -679541993413738569L;
    private final Map<Integer, Integer> setHandling = new HashMap<>(), skillsIncrement = new HashMap<>(), damageIncrease = new HashMap<>();
    private final EnumMap<Element, Integer> elemBoosts = new EnumMap<>(Element.class);
    private final List<Equip> durabilityHandling = new ArrayList<>(), equipLevelHandling = new ArrayList<>();
    private transient float shouldHealHP, shouldHealMP;
    private transient short passive_sharpeye_min_percent, passive_sharpeye_max_percent, passive_sharpeye_rate;
    private transient byte passive_mastery;
    private transient int localstr, localdex, localluk, localint_, localmaxhp, localmaxmp, magic, watk, hands, accuracy;
    private transient int addmaxhp, addmaxmp;
    public transient int incMaxDF;
    private transient float localmaxbasedamage, localmaxbasepvpdamage, localmaxbasepvpdamageL;
    public transient boolean equippedWelcomeBackRing, hasClone, Berserk;
    public transient double expBuff, indieExp, dropBuff, mesoBuff, cashBuff, mesoGuard, mesoGuardMeso, expMod, dropMod, pickupRange, dam_r, bossdam_r;
    public transient int recoverHP, recoverMP, mpconReduce, mpconPercent, incMesoProp, reduceCooltime, coolTimeR, suddenDeathR, expLossReduceR, DAMreflect, DAMreflect_rate, ignoreDAMr, ignoreDAMr_rate, ignoreDAM, ignoreDAM_rate,
            hpRecover, hpRecoverProp, hpRecoverPercent, mpRecover, mpRecoverProp, RecoveryUP, BuffUP, RecoveryUP_Skill, BuffUP_Skill,
            incAllskill, combatOrders, ignoreTargetDEF, defRange, BuffUP_Summon, dodgeChance, speed, speedMax, jump, harvestingTool,
            equipmentBonusExp, cashMod, levelBonus, ASR, TER, pickRate, decreaseDebuff, equippedFairy, equippedSummon,
            pvpDamage, hpRecoverTime, mpRecoverTime, dot, dotTime, questBonus, pvpRank, pvpExp, wdef, mdef, trueMastery, damX, reduceDamageRate;
    public transient int def, element_ice, element_fire, element_light, element_psn, raidenCount, raidenPorp, stanceProp;
    public int hp, maxhp, mp, maxmp, str, dex, luk, int_;
    private transient int percent_hp, percent_mp, percent_str, percent_dex, percent_int, percent_luk, percent_acc, percent_atk, percent_matk, percent_wdef, percent_mdef,
            add_hp, add_mp, add_str, add_dex, add_int, add_luk, add_acc, add_atk, add_matk, add_wdef, add_mdef;
    private final Map<Integer, Integer> add_skill_duration = new HashMap();
    private final Map<Integer, Integer> add_skill_attackCount = new HashMap();
    private final Map<Integer, Integer> add_skill_targetPlus = new HashMap();
    private final Map<Integer, Integer> add_skill_bossDamageRate = new HashMap();
    private final Map<Integer, Integer> add_skill_dotTime = new HashMap();
    private final Map<Integer, Integer> add_skill_prop = new HashMap();
    private final Map<Integer, Integer> add_skill_coolTimeR = new HashMap();
    private final Map<Integer, Integer> add_skill_ignoreMobpdpR = new HashMap();

    public void recalcLocalStats(MapleCharacter chra) {
        recalcLocalStats(false, chra);
    }

    private void resetLocalStats(final int job) {
        accuracy = 0;//命中率
        wdef = 0;//物理防御
        mdef = 0;//魔法防禦
        damX = 0;//攻擊
        addmaxhp = 0; //增加MAXHP
        addmaxmp = 0; //增加MAXMP
        localdex = getDex();//敏捷
        localint_ = getInt();//智力
        localstr = getStr();//力量
        localluk = getLuk();//運氣
        speed = 100;//速度
        speedMax = 140; // 最大移動速度
        jump = 100;//跳躍力
        stanceProp = 0; // 格擋, 泰山
        pickupRange = 0.0;//撿取範圍
        decreaseDebuff = 0;
        ASR = 0;
        TER = 0;
        dot = 0;//持續傷害
        questBonus = 1;
        dotTime = 0;//持續傷害時間
        trueMastery = 0;//熟練度
        percent_wdef = 0;//增加百分比物理防禦
        percent_mdef = 0;//增加百分比魔法防禦
        percent_hp = 0;//增加百分比HP
        percent_mp = 0;//增加百分比MP
        percent_str = 0;//增加百分比力量
        percent_dex = 0;//增加百分比敏捷
        percent_int = 0;//增加百分比智力
        percent_luk = 0;//增加百分比運氣
        percent_acc = 0;//增加百分比命中
        percent_atk = 0;//增加百分比攻擊
        percent_matk = 0;//增加百分比魔攻
        add_wdef = 0;
        add_mdef = 0;
        add_hp = 0;
        add_mp = 0;
        add_str = 0;
        add_dex = 0;
        add_int = 0;
        add_luk = 0;
        add_acc = 0;
        add_atk = 0;
        add_matk = 0;
        passive_sharpeye_rate = 5;//暴擊概率
        passive_sharpeye_min_percent = 20;//最小暴擊傷害
        passive_sharpeye_max_percent = 50;//最大暴擊傷害
        magic = 0;//魔法攻擊力
        watk = 0;//物理攻擊力
        dodgeChance = 0;//閃避
        pvpDamage = 0;
        mesoGuard = 50.0;//楓幣護盾
        mesoGuardMeso = 0.0;
        dam_r = 100.0;//增加百分比傷害 - 總傷
        bossdam_r = 100.0;//增加BOSS百分比傷害 - BOSS傷
        expBuff = 100.0;//經驗倍率加持
        indieExp = 100.0;//加持獎勵經驗
        cashBuff = 100.0;//樂豆點BUFF
        dropBuff = 100.0;//掉寶BUFF
        mesoBuff = 100.0;//楓幣BUFF
        recoverHP = 0;//恢復HP量
        recoverMP = 0;//恢復MP量
        mpconReduce = 0;
        mpconPercent = 100;//恢復MP百分比量
        incMesoProp = 0;
        reduceCooltime = 0;//技能冷卻
        coolTimeR = 0;//技能冷卻
        suddenDeathR = 0;
        expLossReduceR = 0;
        DAMreflect = 0;
        DAMreflect_rate = 0;
        ignoreDAMr = 0;//無視傷害百分比
        ignoreDAMr_rate = 0;//無視傷害百分比概率
        ignoreDAM = 0;//無視傷害
        ignoreDAM_rate = 0;//無視傷害幾率
        ignoreTargetDEF = 0;//無視目標防御力
        hpRecover = 0;//HP恢復
        hpRecoverProp = 0;//HP恢復概率
        hpRecoverPercent = 0;//HP恢復百分比
        mpRecover = 0;//MP恢復
        mpRecoverProp = 0;//MP恢復概率
        pickRate = 0;
        equippedWelcomeBackRing = false;
        equippedFairy = 0;
        equippedSummon = 0;
        hasClone = false;
        Berserk = false;
        equipmentBonusExp = 0;
        RecoveryUP = 0;
        BuffUP = 0;
        RecoveryUP_Skill = 0;
        BuffUP_Skill = 0;
        BuffUP_Summon = 0;
        dropMod = 1.0;
        expMod = 1.0;
        cashMod = 1;
        levelBonus = 0;
        incMaxDF = 0;
        incAllskill = 0;
        combatOrders = 0;
        defRange = isRangedJob(job) ? 200 : 0;
        durabilityHandling.clear();
        equipLevelHandling.clear();
        skillsIncrement.clear();
        damageIncrease.clear();
        setHandling.clear();
        add_skill_duration.clear();
        add_skill_attackCount.clear();
        add_skill_targetPlus.clear();
        add_skill_dotTime.clear();
        add_skill_prop.clear();
        add_skill_coolTimeR.clear();
        add_skill_ignoreMobpdpR.clear();
        harvestingTool = 0;
        element_fire = 100;
        element_ice = 100;
        element_light = 100;
        element_psn = 100;
        def = 100;
        raidenCount = 0;
        raidenPorp = 0;
        reduceDamageRate = 0;
    }

    /**
     * 計算各類屬性狀態
     *
     * @param first_login 是否第一次登入
     * @param chra 角色實例
     */
    public void recalcLocalStats(boolean first_login, MapleCharacter chra) {
        if (chra.isClone()) {
            return; //clones share PlayerStats objects and do not need to be recalculated
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int oldmaxhp = localmaxhp;
        int localmaxhp_ = getMaxHp();
        int localmaxmp_ = getMaxMp();
        resetLocalStats(chra.getJob());
        for (MapleTraitType t : MapleTraitType.values()) {
            chra.getTrait(t).clearLocalExp();
        }
        StructItemOption soc;
        final Map<Skill, SkillEntry> sData = new HashMap<>();
        //裝備屬性處理
        final Iterator<Item> itera = chra.getInventory(MapleInventoryType.EQUIPPED).newList().iterator();
        while (itera.hasNext()) {
            final Equip equip = (Equip) itera.next();
            if (equip.getPosition() == -11) {
                if (ItemConstants.類型.魔法武器(equip.getItemId())) {
                    final Map<String, Integer> eqstat = MapleItemInformationProvider.getInstance().getEquipStats(equip.getItemId());
                    if (eqstat != null) { //slow, poison, darkness, seal, freeze
                        if (eqstat.containsKey("incRMAF")) {
                            element_fire = eqstat.get("incRMAF");
                        }
                        if (eqstat.containsKey("incRMAI")) {
                            element_ice = eqstat.get("incRMAI");
                        }
                        if (eqstat.containsKey("incRMAL")) {
                            element_light = eqstat.get("incRMAL");
                        }
                        if (eqstat.containsKey("incRMAS")) {
                            element_psn = eqstat.get("incRMAS");
                        }
                        if (eqstat.containsKey("elemDefault")) {
                            def = eqstat.get("elemDefault");
                        }
                    }
                }
            }
            if ((equip.getItemId() / 10000 == 166 && equip.getAndroid() != null
                    || equip.getItemId() / 10000 == 167) && chra.getAndroid() == null) {
                final Equip android = (Equip) chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -32);
                final Equip heart = (Equip) chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -33);
                if (android != null && heart != null) {
                    chra.setAndroid(equip.getAndroid());
                }
            }
            //if (equip.getItemId() / 1000 == 1099) {
            //    equippedForce += equip.getMp();
            //}
            chra.getTrait(MapleTraitType.craft).addLocalExp(equip.getHands());
            accuracy += equip.getAcc();
            localmaxhp_ += equip.getHp();
            localmaxmp_ += equip.getMp();
            localdex += equip.getDex();
            localint_ += equip.getInt();
            localstr += equip.getStr();
            localluk += equip.getLuk();
            watk += equip.getWatk();
            magic += equip.getMatk();
            wdef += equip.getWdef();
            mdef += equip.getMdef();
            speed += equip.getSpeed();
            jump += equip.getJump();
            pvpDamage += equip.getPVPDamage();
            bossdam_r += equip.getBossDamage();
            ignoreTargetDEF += equip.getIgnorePDR();
            dam_r *= ((double) equip.getTotalDamage() + 100.0) / 100.0;
            percent_str += equip.getAllStat();
            percent_dex += equip.getAllStat();
            percent_int += equip.getAllStat();
            percent_luk += equip.getAllStat();
            switch (equip.getItemId()) {
                case 1112127: // Welcome Back
                    equippedWelcomeBackRing = true;
                    break;
                case 1122017: //精靈墜飾
                    equippedFairy = 10;
                    break;
                case 1122158:
                    equippedFairy = 5;
                    break;
                case 1112585: // 天使祝福
                    equippedSummon = 1085;
                    break;
                case 1112594: // 雪花天使祝福
                    equippedSummon = 1090;
                    break;
                case 1112586: // 黑天使祝福
                    equippedSummon = 1087;
                    break;
                case 1112663: // 白色精靈祝福
                    equippedSummon = 1179;
                    break;
                case 1113020: // 戰神祝福
                    equippedSummon = 80001262;
                    break;
                case 1112735: // 白天使祝福
                    equippedSummon = 80001154;
                    break;
                case 1114200: // 瑪瑙戒指 "遭遇"
                    equippedSummon = 80001518;
                    break;
                case 1114201: // 瑪瑙戒指 "共用"
                    equippedSummon = 80001519;
                    break;
                case 1114202: // 瑪瑙戒指 "共用"
                    equippedSummon = 80001520;
                    break;
                case 1114203: // 瑪瑙戒指 "共用"
                    equippedSummon = 80001521;
                    break;
                case 1114204: // 瑪瑙戒指 "共用"
                    equippedSummon = 80001522;
                    break;
                case 1114205: // 瑪瑙戒指 "共用"
                    equippedSummon = 80001523;
                    break;
                case 1114206: // 瑪瑙戒指 "共用"
                    equippedSummon = 80001524;
                    break;
                case 1114207: // 瑪瑙戒指 "成長"
                    equippedSummon = 80001525;
                    break;
                case 1114208: // 瑪瑙戒指 "成長"
                    equippedSummon = 80001526;
                    break;
                case 1114209: // 瑪瑙戒指 "成長"
                    equippedSummon = 80001527;
                    break;
                case 1114210: // 瑪瑙戒指 "成長"
                    equippedSummon = 80001528;
                    break;
                case 1114211: // 瑪瑙戒指 "成長"
                    equippedSummon = 80001529;
                    break;
                case 1114212: // 瑪瑙戒指 "成長"
                    equippedSummon = 80001530;
                    break;
                case 1114219: // 瑪瑙戒指 "遭遇"
                    equippedSummon = 80001715;
                    break;
                case 1114220: // 瑪瑙戒指 "共用"
                    equippedSummon = 80001716;
                    break;
                case 1114221: // 瑪瑙戒指 "共用"
                    equippedSummon = 80001717;
                    break;
                case 1114222: // 瑪瑙戒指 "共用"
                    equippedSummon = 80001718;
                    break;
                case 1114223: // 瑪瑙戒指 "共用"
                    equippedSummon = 80001719;
                    break;
                case 1114224: // 瑪瑙戒指 "共用"
                    equippedSummon = 80001720;
                    break;
                case 1114225: // 瑪瑙戒指 "共用"
                    equippedSummon = 80001721;
                    break;
                case 1114226: // 瑪瑙戒指 "完成"
                    equippedSummon = 80001722;
                    break;
                case 1114227: // 瑪瑙戒指 "完成"
                    equippedSummon = 80001723;
                    break;
                case 1114228: // 瑪瑙戒指 "完成"
                    equippedSummon = 80001724;
                    break;
                case 1114229: // 瑪瑙戒指 "完成"
                    equippedSummon = 80001725;
                    break;
                case 1114230: // 瑪瑙戒指 "完成"
                    equippedSummon = 80001726;
                    break;
                case 1114231: // 瑪瑙戒指 "完成"
                    equippedSummon = 80001727;
                    break;
                default:
                    for (int eb_bonus : GameConstants.Equipments_Bonus) {
                        if (equip.getItemId() == eb_bonus) {
                            //equipmentBonusExp += GameConstants.Equipment_Bonus_EXP(eb_bonus);
                            break;
                        }
                    }
                    break;
            }

            if (equip.getItemId() / 1000 == 1099) {
                this.incMaxDF += equip.getMp();
            }
            percent_hp += ii.getItemIncMHPr(equip.getItemId());
            percent_mp += ii.getItemIncMMPr(equip.getItemId());
            bossdam_r += equip.getBossDamage();
            ignoreTargetDEF += equip.getIgnorePDR();
            dam_r += equip.getTotalDamage();

            final Integer set = ii.getSetItemID(equip.getItemId());
            if (set != null && set > 0) {
                int value = 1;
                if (setHandling.containsKey(set)) {
                    value += setHandling.get(set);
                }
                setHandling.put(set, value); //id of Set, number of items to go with the set
            }
            if (equip.getIncSkill() > 0 && ii.getEquipSkills(equip.getItemId()) != null) {
                for (final int zzz : ii.getEquipSkills(equip.getItemId())) {
                    final Skill skil = SkillFactory.getSkill(zzz);
                    if (skil != null && skil.canBeLearnedBy(chra.getJob())) { //dont go over masterlevel :D
                        int value = 1;
                        if (skillsIncrement.get(skil.getId()) != null) {
                            value += skillsIncrement.get(skil.getId());
                        }
                        skillsIncrement.put(skil.getId(), value);
                    }
                }
            }
            final Pair<Integer, Integer> ix = handleEquipAdditions(ii, chra, first_login, sData, equip.getItemId());
            if (ix != null) {
                localmaxhp_ += ix.getLeft();
                localmaxmp_ += ix.getRight();
            }
            //潛能屬性處理
            if (equip.getState(false) >= 17 || equip.getState(true) >= 17) {
                final int[] potentials = {equip.getPotential(1, false), equip.getPotential(2, false), equip.getPotential(3, false), equip.getPotential(1, true), equip.getPotential(2, true), equip.getPotential(3, true),};
                for (final int i : potentials) {
                    if (i > 0) {
//                        soc = ii.getPotentialInfo(i).get(ii.getReqLevel(equip.getItemId()) / 10);
                        int itemReqLevel = ii.getReqLevel(equip.getItemId());
                        //System.err.println("潛能ID: " + i + " 裝備等級: " + itemReqLevel + " 潛能等級: " + (itemReqLevel - 1) / 10);
                        List<StructItemOption> potentialInfo = ii.getPotentialInfo(i);
                        soc = potentialInfo.get(Math.min(potentialInfo.size() - 1, (itemReqLevel - 1) / 10));
                        if (soc != null) {
                            localmaxhp_ += soc.get("incMHP");
                            localmaxmp_ += soc.get("incMMP");
                            handleItemOption(soc, chra, first_login, sData);
                        }
                    }
                }
            }
            //星岩屬性處理
            if (equip.getSocketState() > 15) {
                final int[] sockets = {equip.getSocket(1), equip.getSocket(2), equip.getSocket(3)};
                for (final int i : sockets) {
                    if (i > 0) {
                        soc = ii.getSocketInfo(i);
                        if (soc != null) {
                            localmaxhp_ += soc.get("incMHP");
                            localmaxmp_ += soc.get("incMMP");
                            handleItemOption(soc, chra, first_login, sData);
                        }
                    }
                }
            }
            //耐久度處理
            if (equip.getDurability() > 0) {
                durabilityHandling.add(equip);
            }
            //
            if (GameConstants.getMaxLevel(equip.getItemId()) > 0 && (GameConstants.getStatFromWeapon(equip.getItemId()) == null ? (equip.getEquipLevel() <= GameConstants.getMaxLevel(equip.getItemId())) : (equip.getEquipLevel() < GameConstants.getMaxLevel(equip.getItemId())))) {
                equipLevelHandling.add(equip);
            }
        }
        //套裝屬性處理
        final Iterator<Entry<Integer, Integer>> iter = setHandling.entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<Integer, Integer> entry = iter.next();
            final StructSetItem set = ii.getSetItem(entry.getKey());
            if (set != null) {
                final Map<Integer, SetItem> itemz = set.getItems();
                for (Entry<Integer, SetItem> ent : itemz.entrySet()) {
                    if (ent.getKey() <= entry.getValue()) {
                        SetItem se = ent.getValue();
                        localstr += se.incSTR + se.incAllStat;
                        localdex += se.incDEX + se.incAllStat;
                        localint_ += se.incINT + se.incAllStat;
                        localluk += se.incLUK + se.incAllStat;
                        watk += se.incPAD;
                        magic += se.incMAD;
                        speed += se.incSpeed;
                        accuracy += se.incACC;
                        localmaxhp_ += se.incMHP;
                        localmaxmp_ += se.incMMP;
                        percent_hp += se.incMHPr;
                        percent_mp += se.incMMPr;
                        wdef += se.incPDD;
                        mdef += se.incMDD;
                        if (se.option1 > 0 && se.option1Level > 0) {
                            soc = ii.getPotentialInfo(se.option1).get(se.option1Level);
                            if (soc != null) {
                                localmaxhp_ += soc.get("incMHP");
                                localmaxmp_ += soc.get("incMMP");
                                handleItemOption(soc, chra, first_login, sData);
                            }
                        }
                        if (se.option2 > 0 && se.option2Level > 0) {
                            soc = ii.getPotentialInfo(se.option2).get(se.option2Level);
                            if (soc != null) {
                                localmaxhp_ += soc.get("incMHP");
                                localmaxmp_ += soc.get("incMMP");
                                handleItemOption(soc, chra, first_login, sData);
                            }
                        }
                    }
                }
            }
        }
        handleProfessionTool(chra);
        double extraExpRate = 1.0;
        for (Item item : chra.getInventory(MapleInventoryType.CASH).newList()) {
            if (item.getItemId() / 10000 == 521) {
                double rate = ii.getExpCardRate(item.getItemId());
                if (item.getItemId() != 5210009 && rate > 1.0) {
                    if (!ii.isExpOrDropCardTime(item.getItemId()) || chra.getLevel() > ii.getExpCardMaxLevel(item.getItemId()) || (item.getExpiration() == -1L && !chra.isIntern())) {
                        if (item.getExpiration() == -1L && !chra.isIntern()) {
                            chra.dropMessage(5, ii.getName(item.getItemId()) + "屬性錯誤，經驗值加成無效。");
                        }
                        continue;
                    }
                    switch (item.getItemId()) {
                        case 5211000:
                        case 5211001:
                        case 5211002:
                            extraExpRate *= rate;
                            break;
                        default:
                            if (expMod < rate) {
                                expMod = rate;
                            }
                    }
                }
            } else if (dropMod == 1.0 && item.getItemId() / 10000 == 536) {
                if (item.getItemId() >= 5360000 && item.getItemId() < 5360100) {
                    if (!ii.isExpOrDropCardTime(item.getItemId()) || (item.getExpiration() == -1L && !chra.isIntern())) {
                        if (item.getExpiration() == -1L && !chra.isIntern()) {
                            chra.dropMessage(5, ii.getName(item.getItemId()) + "屬性錯誤，掉寶幾率加成無效。");
                        }
                        continue;
                    }
                    dropMod = 2.0;
                }
            } else if (item.getItemId() == 5710000) {
                questBonus = 2;
            } else if (item.getItemId() == 5590000) {
                levelBonus += 5;
            }
        }
        expMod = Math.max(extraExpRate, expMod);
        if (expMod > 0 && EventConstants.DoubleExpTime) {
            expMod *= 2.0;
        }
        if (dropMod > 0 && EventConstants.DoubleDropTime) {
            dropMod *= 2.0;
        }
        for (Item item : chra.getInventory(MapleInventoryType.ETC).list()) {
            switch (item.getItemId()) {
                case 4030003:
                    break;
                case 4030004:
                    break;
                case 4031864:
                    break;
            }
        }

        // add to localmaxhp_ if percentage plays a role in it, else add_hp
        handleBuffStats(chra);
        Integer buff = chra.getBuffedValue(MapleBuffStat.ENHANCED_MAXHP);
        if (buff != null) {
            localmaxhp_ += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.ENHANCED_MAXMP);
        if (buff != null) {
            localmaxmp_ += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.INDIE_MAX_HP);
        if (buff != null) {
            localmaxhp_ += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.INDIE_MAX_MP);
        if (buff != null) {
            localmaxmp_ += buff;
        }
        handlePassiveSkills(chra);//被動技能
        handleHyperPassiveSkills(chra);//超技被動
        if (chra.getGuildId() > 0) {
            final MapleGuild g = World.Guild.getGuild(chra.getGuildId());
            if (g != null && g.getSkills().size() > 0) {
                final long now = System.currentTimeMillis();
                for (MapleGuildSkill gs : g.getSkills()) {
                    if (gs.timestamp > now && gs.activator.length() > 0) {
                        final MapleStatEffect e = SkillFactory.getSkill(gs.skillID).getEffect(gs.level);
                        passive_sharpeye_rate += e.getCr();
                        watk += e.getAttackX();
                        magic += e.getMagicX();
                        expBuff *= e.getEXPRate() / 100.0;
                        dodgeChance += e.getER();
                        percent_wdef += e.getWDEFRate();
                        percent_mdef += e.getMDEFRate();
                    }
                }
            }
        }
        for (Pair<Integer, Integer> ix : chra.getCharacterCard().getCardEffects()) {
            System.out.println("[角色卡] 等級: " + ix.getRight() + " 技能: " + ix.getLeft() + " - " + SkillFactory.getSkillName(ix.getLeft()));
            final MapleStatEffect e = SkillFactory.getSkill(ix.getLeft()).getEffect(ix.getRight());
            percent_wdef += e.getWDEFRate();
            watk += (e.getLevelToWatk() * chra.getLevel());
            percent_hp += e.getPercentHP();
            percent_mp += e.getPercentMP();
            magic += (e.getLevelToMatk() * chra.getLevel());
            RecoveryUP += e.getMPConsumeEff();
            percent_acc += e.getPercentAcc();
            passive_sharpeye_rate += e.getCr();
            jump += e.getPassiveJump();
            speed += e.getPassiveSpeed();
            dodgeChance += e.getPercentAvoid();
            damX += (e.getLevelToDamage() * chra.getLevel());
            BuffUP_Summon += e.getSummonTimeInc();
            expLossReduceR += e.getEXPLossRate();
            ASR += e.getASRRate();
            //ignoreMobDamR
            suddenDeathR += e.getSuddenDeathR();
            BuffUP_Skill += e.getBuffTimeRate();
            //onHitHpRecoveryR
            //onHitMpRecoveryR
            coolTimeR += e.getCooltimeReduceR();
            incMesoProp += e.getMesoAcquisition();
            damX += Math.floor((e.getHpToDamage() * oldmaxhp) / 100.0f);
            damX += Math.floor((e.getMpToDamage() * oldmaxhp) / 100.0f);
            //finalAttackDamR
            passive_sharpeye_max_percent += e.getCriticalMax();
            ignoreTargetDEF += e.getIgnoreMob();
            localstr += e.getStrX();
            localdex += e.getDexX();
            localint_ += e.getIntX();
            localluk += e.getLukX();
            localmaxhp_ += e.getMaxHpX();
            localmaxmp_ += e.getMaxMpX();
            watk += e.getAttackX();
            magic += e.getMagicX();
            bossdam_r += e.getBossDamage();
        }

        localstr += Math.floor((localstr * percent_str) / 100.0f);
        localdex += Math.floor((localdex * percent_dex) / 100.0f);
        localint_ += Math.floor((localint_ * percent_int) / 100.0f);
        localluk += Math.floor((localluk * percent_luk) / 100.0f);
        if (localint_ > localdex) {
            accuracy += localint_ + Math.floor(localluk * 1.2);
        } else {
            accuracy += localluk + Math.floor(localdex * 1.2);
        }
        watk += Math.floor((watk * percent_atk) / 100.0f);
        magic += Math.floor((magic * percent_matk) / 100.0f);
        localint_ += Math.floor((localint_ * percent_matk) / 100.0f);

        wdef += Math.floor((localstr * 1.2) + ((localdex + localluk) * 0.5) + (localint_ * 0.4));
        mdef += Math.floor((localstr * 0.4) + ((localdex + localluk) * 0.5) + (localint_ * 1.2));
        wdef += Math.min(30000, Math.floor((wdef * percent_wdef) / 100.0f));
        mdef += Math.min(30000, Math.floor((wdef * percent_mdef) / 100.0f));

        hands = localdex + localint_ + localluk;
        calculateFame(chra);
        ignoreTargetDEF += chra.getTrait(MapleTraitType.charisma).getLevel() / 10;
        pvpDamage += chra.getTrait(MapleTraitType.charisma).getLevel() / 10;
        ASR += chra.getTrait(MapleTraitType.will).getLevel() / 5;

        accuracy += Math.floor((accuracy * percent_acc) / 100.0f);
        accuracy += chra.getTrait(MapleTraitType.insight).getLevel() * 15 / 10;

        localmaxhp_ += chra.getTrait(MapleTraitType.will).getLevel() * 20;
        localmaxhp_ += addmaxhp;
        if (MapleJob.is陰陽師(chra.getJob())) {
            localmaxhp_ += addmaxmp;
        }
        localmaxhp_ += Math.floor((percent_hp * localmaxhp_) / 100.0f);
        if (MapleJob.is陰陽師(chra.getJob())) {
            localmaxhp_ += Math.floor((percent_mp * localmaxhp_) / 100.0f);
        }
        localmaxhp = Math.min(99999/*500000*/, Math.abs(Math.max(-99999/*-500000*/, localmaxhp_)));

        localmaxmp_ += Math.floor((percent_mp * localmaxmp_) / 100.0f);
        localmaxmp_ += chra.getTrait(MapleTraitType.sense).getLevel() * 20;
        localmaxmp_ += addmaxmp;
        localmaxmp = Math.min(99999/*500000*/, Math.abs(Math.max(-99999/*-500000*/, localmaxmp_)));

        if (chra.getEventInstance() != null && chra.getEventInstance().getName().startsWith("PVP")) { //hack
            MapleStatEffect eff;
            localmaxhp = Math.min(40000, localmaxhp * 3); //approximate.
            localmaxmp = Math.min(20000, localmaxmp * 2);
            //not sure on 20000 cap
            for (int i : pvpSkills) {
                Skill skil = SkillFactory.getSkill(i);
                if (skil != null && skil.canBeLearnedBy(chra.getJob())) {
                    sData.put(skil, new SkillEntry((byte) 1, (byte) 0, -1));
                    eff = skil.getEffect(1);
                    switch ((i / 1000000) % 10) {
                        case 1:
                            if (eff.getX() > 0) {
                                pvpDamage += (wdef / eff.getX());
                            }
                            break;
                        case 3:
                            hpRecoverProp += eff.getProb();
                            hpRecover += eff.getX();
                            mpRecoverProp += eff.getProb();
                            mpRecover += eff.getX();
                            break;
                        case 5:
                            passive_sharpeye_rate += eff.getProb();
                            passive_sharpeye_max_percent = 100;
                            break;
                    }
                    break;
                }
            }
            eff = chra.getStatForBuff(MapleBuffStat.MORPH);
            if (eff != null && eff.getSourceId() % 10000 == 1105) { //ice knight
                localmaxhp = 99999/*500000*/;
                localmaxmp = 99999/*500000*/;
            }
        }
        chra.changeSkillLevel_Skip(sData, false);
        if (MapleJob.is惡魔殺手(chra.getJob()) || MapleJob.is凱內西斯(chra.getJob())) {
            localmaxmp = GameConstants.getMPByJob(chra.getJob());
            localmaxmp += this.incMaxDF;
        } else if (MapleJob.is神之子(chra.getJob())) {
            localmaxmp = 100;
        } else if (MapleJob.is陰陽師(chra.getJob())) {
            localmaxmp = 100;
            localmaxmp += this.incMaxDF;
        } else if (MapleJob.isNotMpJob(chra.getJob()) && chra.getJob() != 3001) {
            localmaxmp = 0;
        }
        if (MapleJob.is惡魔復仇者(chra.getJob())) {
            chra.getClient().announce(JobPacket.AvengerPacket.giveAvengerHpBuff(hp));
        }
        CalcPassive_SharpEye(chra);
        CalcPassive_Mastery(chra);
        recalcPVPRank(chra);
        if (first_login) {
            chra.silentEnforceMaxHpMp();
            relocHeal(chra);
        } else {
            chra.enforceMaxHpMp();
        }
        calculateMaxBaseDamage(Math.max(magic, watk), pvpDamage, chra);
        trueMastery = Math.min(100, trueMastery);
        if (oldmaxhp != 0 && oldmaxhp != localmaxhp) {
            chra.updatePartyMemberHP();
        }
    }

    private void handlePassiveSkills(MapleCharacter chra) {
        Skill bx;
        int bof;
        MapleStatEffect eff;

        //兩個連結技能
        bx = SkillFactory.getSkill(80000000);
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            localstr += eff.getStrX();
            localdex += eff.getDexX();
            localint_ += eff.getIntX();
            localluk += eff.getLukX();
            percent_hp += eff.getHpR();
            percent_mp += eff.getMpR();
        }
        bx = SkillFactory.getSkill(80000001);
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            bossdam_r += eff.getBossDamage();
        }

        //精靈的祝福
        bx = SkillFactory.getSkill(GameConstants.getBOF_ForJob(chra.getJob()));
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            watk += eff.getX();
            magic += eff.getY();
            accuracy += eff.getX();
        }

        //女皇的祝福
        bx = SkillFactory.getSkill(GameConstants.getEmpress_ForJob(chra.getJob()));
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            watk += eff.getX();
            magic += eff.getY();
            accuracy += eff.getZ();
        }

        int job = chra.getJob();
        if (MapleJob.is冒險家(job)) {
            if (MapleJob.is劍士(job)) {
                // 戰鬥技能
                bx = SkillFactory.getSkill(1000009);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    speed += eff.getPassiveSpeed();
                    jump += eff.getPassiveJump();
                    speed += eff.getSpeedMax();
                    addmaxhp += eff.getLevelToMaxHp() * chra.getLevel();
                }
                // 自身強化
                bx = SkillFactory.getSkill(1001003);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                }
            } else if (MapleJob.is法師(job)) {
                // 魔力增幅
                bx = SkillFactory.getSkill(2000006);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_mp += eff.getPercentMP();
                    addmaxmp += eff.getLevelToMaxMp() * chra.getLevel();
                }
            } else if (MapleJob.is弓箭手(job)) {
                bx = SkillFactory.getSkill(3000002);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    defRange += bx.getEffect(bof).getRange();
                }
            } else if (MapleJob.is盜賊(job) && chra.getSubcategory() == 0) {
                // 幻化術
                bx = SkillFactory.getSkill(4000000);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    accuracy += eff.getX();
                    dodgeChance += eff.getY();
                }
                // 速度激發
                bx = SkillFactory.getSkill(4001005);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    speedMax += eff.getSpeedMax();
                }
            } else if (MapleJob.is海盜(job) && !MapleJob.is重砲指揮官(job) && !MapleJob.is蒼龍俠客(job)) {
            }

            bx = SkillFactory.getSkill(74);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                levelBonus += bx.getEffect(bof).getX();
            }

            bx = SkillFactory.getSkill(80);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                levelBonus += bx.getEffect(bof).getX();
            }

            bx = SkillFactory.getSkill(10074);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                levelBonus += bx.getEffect(bof).getX();
            }

            bx = SkillFactory.getSkill(10080);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                levelBonus += bx.getEffect(bof).getX();
            }

            bx = SkillFactory.getSkill(110);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint_ += eff.getIntX();
                localluk += eff.getLukX();
                percent_hp += eff.getHpR();
                percent_mp += eff.getMpR();
            }

            bx = SkillFactory.getSkill(10110);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint_ += eff.getIntX();
                localluk += eff.getLukX();
                percent_hp += eff.getHpR();
                percent_mp += eff.getMpR();
            }
        } else if (MapleJob.is皇家騎士團(job)) {
            System.err.println("職業未處理被動技能:" + job);
        } else if (MapleJob.is末日反抗軍(job) && !MapleJob.is惡魔(job) && !MapleJob.is傑諾(job)) {
            bx = SkillFactory.getSkill(30000002);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                RecoveryUP += eff.getX() - 100;
            }
        }

        if (MapleJob.is英雄(job)) {
            // 武器精通
            bx = SkillFactory.getSkill(1100000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                trueMastery += eff.getMastery();
                accuracy += eff.getX();
            }
            // 體能訓練
            bx = SkillFactory.getSkill(1100009);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            // 恢復術
            bx = SkillFactory.getSkill(1110011);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                ASR += eff.getASRRate();
                TER += eff.getTERRate();
            }
            // 戰鬥精通
            bx = SkillFactory.getSkill(1120012);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                ignoreTargetDEF += eff.getIgnoreMob();
            }
            // 進階終極攻擊
            bx = SkillFactory.getSkill(1120013);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                accuracy += eff.getAccR();
                damageIncrease.put(1100002, eff.getDamage());
            }
            // 反抗姿態
            bx = SkillFactory.getSkill(1120014);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                stanceProp += eff.info.get(MapleStatInfo.stanceProp);
            }
        } else if (MapleJob.is聖騎士(job)) {
            // 武器精通
            bx = SkillFactory.getSkill(1200000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                trueMastery += eff.getMastery();
                accuracy += eff.getX();
            }
            // 體能訓練
            bx = SkillFactory.getSkill(1200009);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            // 盾防精通
            bx = SkillFactory.getSkill(1210001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                ASR += bx.getEffect(bof).getASRRate();
                TER += bx.getEffect(bof).getTERRate();
            }
            // 反抗姿態
            bx = SkillFactory.getSkill(1220017);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                stanceProp += eff.info.get(MapleStatInfo.stanceProp);
            }
        } else if (MapleJob.is黑騎士(job)) {
            // 武器精通
            bx = SkillFactory.getSkill(1300000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                trueMastery += eff.getMastery();
                accuracy += eff.getX();
            }
            // 體能訓練
            bx = SkillFactory.getSkill(1300009);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            // 暗黑之力
            bx = SkillFactory.getSkill(1310009);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                passive_sharpeye_rate += eff.getCr();
                passive_sharpeye_min_percent += eff.getCriticalMin();
                hpRecoverProp += eff.getProb();
                hpRecoverPercent += eff.getX();
            }
            // 恢復術
            bx = SkillFactory.getSkill(1310010);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                ASR += eff.getASRRate();
                TER += eff.getTERRate();
            }
            // 反抗姿態
            bx = SkillFactory.getSkill(1320017);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                stanceProp += eff.info.get(MapleStatInfo.stanceProp);
            }
        } else if (MapleJob.is大魔導士_火毒(job)) {
            bx = SkillFactory.getSkill(2100007);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                localint_ += bx.getEffect(bof).getIntX();
            }
            bx = SkillFactory.getSkill(2110000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dotTime += eff.getX();
                dot += eff.getZ();
            }
            bx = SkillFactory.getSkill(2110001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                mpconPercent += eff.getX() - 100;
                dam_r *= eff.getY() / 100.0;
                bossdam_r *= eff.getY() / 100.0;
            }
            bx = SkillFactory.getSkill(2121003);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                damageIncrease.put(2111003, eff.getX());
            }
            bx = SkillFactory.getSkill(2120009);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                magic += eff.getMagicX();
                BuffUP_Skill += eff.getX();
            }
            bx = SkillFactory.getSkill(2121005);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                TER += bx.getEffect(bof).getTERRate();
            }
            bx = SkillFactory.getSkill(2121009);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                magic += bx.getEffect(bof).getMagicX();
            }
            bx = SkillFactory.getSkill(2120010);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dam_r *= (eff.getX() * eff.getY() + 100.0) / 100.0;
                bossdam_r *= (eff.getX() * eff.getY() + 100.0) / 100.0;
                ignoreTargetDEF += eff.getIgnoreMob();
            }
        } else if (MapleJob.is大魔導士_冰雷(job)) {
            bx = SkillFactory.getSkill(2200007);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                localint_ += bx.getEffect(bof).getIntX();
            }
            bx = SkillFactory.getSkill(2210000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                dot += bx.getEffect(bof).getZ();
            }
            bx = SkillFactory.getSkill(2210001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                mpconPercent += eff.getX() - 100;
                dam_r += dam_r * (eff.getY() / 100.0);
                bossdam_r += bossdam_r * (eff.getY() / 100.0);
            }
            bx = SkillFactory.getSkill(2220009);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                magic += eff.getMagicX();
                BuffUP_Skill += eff.getX();
            }
            bx = SkillFactory.getSkill(2221005);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                TER += bx.getEffect(bof).getTERRate();
            }
            bx = SkillFactory.getSkill(2221009);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                magic += bx.getEffect(bof).getMagicX();
            }
            bx = SkillFactory.getSkill(2220010);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dam_r += dam_r * ((eff.getX() * eff.getY() + 100.0) / 100.0);
                bossdam_r *= bossdam_r * ((eff.getX() * eff.getY() + 100.0) / 100.0);
                ignoreTargetDEF += eff.getIgnoreMob();
            }
        } else if (MapleJob.is主教(job)) {
            bx = SkillFactory.getSkill(2300007);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                localint_ += bx.getEffect(bof).getIntX();
            }
            bx = SkillFactory.getSkill(2310008);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                passive_sharpeye_rate += bx.getEffect(bof).getCr();
            }
            bx = SkillFactory.getSkill(2320010);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                magic += eff.getMagicX();
                BuffUP_Skill += eff.getX();
            }
            bx = SkillFactory.getSkill(2321010);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                magic += bx.getEffect(bof).getMagicX();
            }
            bx = SkillFactory.getSkill(2320005);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                ASR += bx.getEffect(bof).getASRRate();
            }
            bx = SkillFactory.getSkill(2320011);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dam_r += dam_r * ((eff.getX() * eff.getY() + 100.0) / 100.0);
                bossdam_r += bossdam_r * ((eff.getX() * eff.getY() + 100.0) / 100.0);
                ignoreTargetDEF += eff.getIgnoreMob();
            }
        } else if (MapleJob.is箭神(job)) {
            bx = SkillFactory.getSkill(3100006);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                damageIncrease.put(3001004, eff.getX());
                damageIncrease.put(3001005, eff.getY());
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(3110007);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                dodgeChance += bx.getEffect(bof).getER();
            }
            // 弓術精通
            bx = SkillFactory.getSkill(3120005);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                watk += eff.getX();
                trueMastery += eff.getMastery();
                passive_sharpeye_min_percent += eff.getCriticalMin();
            }
        } else if (MapleJob.is神射手(job)) {
            // 弩術精通
            bx = SkillFactory.getSkill(3220004);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                watk += eff.getX();
                trueMastery += eff.getMastery();
                passive_sharpeye_min_percent += eff.getCriticalMin();
            }
            bx = SkillFactory.getSkill(3200006);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                damageIncrease.put(3001004, eff.getX());
                damageIncrease.put(3001005, eff.getY());
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(3220010);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                damageIncrease.put(3211006, bx.getEffect(bof).getDamage() - 150);
            }
            bx = SkillFactory.getSkill(3210007);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                dodgeChance += bx.getEffect(bof).getER();
            }
        } else if (MapleJob.is夜使者(job)) {
            // 精準暗器
            bx = SkillFactory.getSkill(4100000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                trueMastery += eff.getMastery();
                accuracy += eff.getX();
            }
            // 強力投擲
            bx = SkillFactory.getSkill(4100001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                passive_sharpeye_rate += eff.getProb();
                passive_sharpeye_min_percent += eff.getCriticalMin();
            }
            // 體能訓練
            bx = SkillFactory.getSkill(4100007);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localdex += eff.getDexX();
                localluk += eff.getLukX();
            }
            // 永恆黑暗
            bx = SkillFactory.getSkill(4110008);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                ASR += eff.getASRRate();
                percent_hp += eff.getPercentHP();
                TER += eff.getTERRate();
            }
            // 鏢術精通
            bx = SkillFactory.getSkill(4110012);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dam_r += eff.info.get(MapleStatInfo.pdR);
            }
            // 藥劑精通
            bx = SkillFactory.getSkill(4110014);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                passive_sharpeye_min_percent += eff.getCriticalMin();
                passive_sharpeye_max_percent += eff.getCriticalMax();
                RecoveryUP += eff.getX() - 100;
            }
            // 瞬身迴避
            bx = SkillFactory.getSkill(4120002);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dodgeChance += eff.getProb();
            }
            // 暗器精通
            bx = SkillFactory.getSkill(4120012);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                passive_sharpeye_max_percent += eff.getCriticalMax();
                trueMastery += eff.getMastery();
                watk += eff.getX();
            }
            // 黑暗能量
            bx = SkillFactory.getSkill(4121014);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                ignoreTargetDEF += eff.getIgnoreMob();
            }
            // 絕對領域
            bx = SkillFactory.getSkill(4121015);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                bossdam_r += eff.getBossDamage();
            }
        } else if (MapleJob.is暗影神偷(job)) {
            // 精準之刀
            bx = SkillFactory.getSkill(4200000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                trueMastery += eff.getMastery();
                accuracy += eff.getX();
            }
            // 體能訓練
            bx = SkillFactory.getSkill(4200007);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localdex += eff.getDexX();
                localluk += eff.getLukX();
            }
            // 強化盾
            bx = SkillFactory.getSkill(4200010);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0 && ItemConstants.類型.盾牌(chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10).getItemId())) {
                eff = bx.getEffect(bof);
                percent_wdef += eff.getWDEFRate();
                percent_mdef += eff.getMDEFRate();
                dodgeChance += bx.getEffect(bof).getER();
                watk += eff.getY();
            }
            // 貪婪
            bx = SkillFactory.getSkill(4210012);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                mesoBuff *= (eff.getMesoRate() + 100.0) / 100.0;
                pickRate += eff.getU();
                mesoGuard -= eff.getV();
                mesoGuardMeso -= eff.getW();
                // 楓幣炸彈
                damageIncrease.put(4211006, eff.getX());
                watk += eff.getAttackX();
            }
            // 永恆黑暗
            bx = SkillFactory.getSkill(4210013);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                ASR += eff.getASRRate();
                percent_hp += eff.getPercentHP();
                TER += eff.getTERRate();
            }
            // 瞬身迴避
            bx = SkillFactory.getSkill(4220002);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dodgeChance += eff.getProb();
            }
            // 進階精準之刀
            bx = SkillFactory.getSkill(4220012);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                passive_sharpeye_min_percent += eff.getCriticalMin();
                trueMastery += eff.getMastery();
                watk += eff.getX();
            }
            // 致命爆擊
            bx = SkillFactory.getSkill(4220015);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                passive_sharpeye_max_percent += eff.getCriticalMax();
            }
            // 瞬步連擊
            bx = SkillFactory.getSkill(4221007);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dam_r += eff.info.get(MapleStatInfo.pdR);
            }
            // 暗殺本能
            bx = SkillFactory.getSkill(4221013);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                ignoreTargetDEF += eff.getIgnoreMob();
            }
        } else if (MapleJob.is影武者(job) || (job == 400 && chra.getSubcategory() == 1)) {
            // 下忍 被動
            // 精準雙刀
            bx = SkillFactory.getSkill(4300000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                trueMastery += eff.getMastery();
                accuracy += eff.getX();
            }
            // 自我速度激發
            bx = SkillFactory.getSkill(4301003);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                speedMax += eff.getSpeedMax();
            }
            // 中忍 被動
            // 體能訓練
            bx = SkillFactory.getSkill(4310006);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localdex += eff.getDexX();
                localluk += eff.getLukX();
            }
            // 隱忍 被動
            // 竊取生命
            bx = SkillFactory.getSkill(4330007);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                hpRecoverProp += eff.getProb();
                hpRecoverPercent += eff.getX();
            }
            // 激進黑暗
            bx = SkillFactory.getSkill(4330008);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                ASR += eff.getASRRate();
                percent_hp += eff.getPercentHP();
                TER += eff.getTERRate();
            }
            // 血雨暴風狂斬
            bx = SkillFactory.getSkill(4331000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                damageIncrease.put(4301004, eff.getDAMRate());
                damageIncrease.put(4321006, eff.getDAMRate());
            }
            // 影武者 被動
            // 幻影替身
            bx = SkillFactory.getSkill(4341006);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dodgeChance += eff.getER();
                percent_wdef += eff.getWDEFRate();
                percent_mdef += eff.getMDEFRate();
            }
            // 雙刀流精通
            bx = SkillFactory.getSkill(4300000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                trueMastery += eff.getMastery();
                accuracy += eff.getX();
            }
        } else if (MapleJob.is拳霸(job)) {
            bx = SkillFactory.getSkill(5100009);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
            bx = SkillFactory.getSkill(5110008);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) { //Backspin Blow, Double Uppercut, and Corkscrew Blow
                eff = bx.getEffect(bof);
                damageIncrease.put(5101002, eff.getX());
                damageIncrease.put(5101003, eff.getY());
                damageIncrease.put(5101004, eff.getZ());
            }
        } else if (MapleJob.is槍神(job)) {
            bx = SkillFactory.getSkill(5220001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) { //Flamethrower and Ice Splitter
                eff = bx.getEffect(bof);
                damageIncrease.put(5211004, eff.getDamage());
                damageIncrease.put(5211005, eff.getDamage());
            }
        } else if (MapleJob.is重砲指揮官(job)) {
            bx = SkillFactory.getSkill(5010003);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                watk += bx.getEffect(bof).getAttackX();
            }
            bx = SkillFactory.getSkill(5300008);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(5310007);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getHpR();
                ASR += eff.getASRRate();
                percent_wdef += eff.getWDEFRate();
            }
            bx = SkillFactory.getSkill(5310006);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                watk += bx.getEffect(bof).getAttackX();
            }
        } else if (MapleJob.is蒼龍俠客(job)) {
            System.err.println("職業未處理被動技能:" + job);
        } else if (MapleJob.is聖魂劍士(job)) {
            bx = SkillFactory.getSkill(11000005);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
        } else if (MapleJob.is烈焰巫師(job)) {
            bx = SkillFactory.getSkill(12120008);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                dot += bx.getEffect(bof).getY();
            }
            bx = SkillFactory.getSkill(12000024);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                reduceDamageRate += bx.getEffect(bof).getX();
            }
            bx = SkillFactory.getSkill(12000025);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                percent_mp += bx.getEffect(bof).getPercentMP();
                addmaxmp += bx.getEffect(bof).getLevelToMaxMp() * chra.getLevel();
            }
            bx = SkillFactory.getSkill(12100008);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                localint_ += bx.getEffect(bof).getIntX();
            }
            bx = SkillFactory.getSkill(12110001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                mpconPercent += eff.getX() - 100;
                dam_r += eff.getY();
                bossdam_r += eff.getY();
            }
            bx = SkillFactory.getSkill(12110001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                mpconPercent += eff.getX() - 100;
                dam_r += dam_r * (eff.getY() / 100.0);
                bossdam_r += bossdam_r * (eff.getY() / 100.0);
            }

            bx = SkillFactory.getSkill(12111004);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                TER += bx.getEffect(bof).getTERRate();
            }
        } else if (MapleJob.is破風使者(job)) {
            bx = SkillFactory.getSkill(13000001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                defRange += bx.getEffect(bof).getRange();
            }
            bx = SkillFactory.getSkill(13110008);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                dodgeChance += bx.getEffect(bof).getER();
            }
            bx = SkillFactory.getSkill(13110003);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                trueMastery += eff.getMastery();
                passive_sharpeye_min_percent += eff.getCriticalMin();
            }
        } else if (MapleJob.is暗夜行者(job)) {
            bx = SkillFactory.getSkill(14110003);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                RecoveryUP += eff.getX() - 100;
                BuffUP += eff.getY() - 100;
            }
            bx = SkillFactory.getSkill(14000001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                defRange += bx.getEffect(bof).getRange();
            }
        } else if (MapleJob.is閃雷悍將(job)) {
            System.err.println("職業未處理被動技能:" + job);
        } else if (MapleJob.is狂狼勇士(job)) {
            bx = SkillFactory.getSkill(21101006);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dam_r += dam_r * ((eff.getDAMRate() + 100.0) / 100.0);
                bossdam_r += bossdam_r * ((eff.getDAMRate() + 100.0) / 100.0);
            }
            bx = SkillFactory.getSkill(21110002);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                damageIncrease.put(21000004, bx.getEffect(bof).getW());
            }
            bx = SkillFactory.getSkill(21111010);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                ignoreTargetDEF += bx.getEffect(bof).getIgnoreMob();
            }
            bx = SkillFactory.getSkill(21120001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                watk += eff.getX();
                trueMastery += eff.getMastery();
                passive_sharpeye_min_percent += eff.getCriticalMin();
            }
            bx = SkillFactory.getSkill(21120002);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                damageIncrease.put(21100007, bx.getEffect(bof).getZ());
            }
            bx = SkillFactory.getSkill(21120011);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                damageIncrease.put(21100002, eff.getDAMRate());
                damageIncrease.put(21110003, eff.getDAMRate());
            }
        } else if (MapleJob.is龍魔導士(job)) {
            bx = SkillFactory.getSkill(22000000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                magic += bof;
            }
            bx = SkillFactory.getSkill(22150000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                mpconPercent += eff.getX() - 100;
                dam_r *= eff.getY() / 100.0;
                bossdam_r *= eff.getY() / 100.0;
            }
            bx = SkillFactory.getSkill(22160000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dam_r *= (eff.getDamage() + 100.0) / 100.0;
                bossdam_r *= (eff.getDamage() + 100.0) / 100.0;
            }
            bx = SkillFactory.getSkill(22170001); // magic mastery, this is an invisible skill
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                magic += eff.getX();
                trueMastery += eff.getMastery();
                passive_sharpeye_min_percent += eff.getCriticalMin();
            }
        } else if (MapleJob.is精靈遊俠(job)) {
            bx = SkillFactory.getSkill(20021110);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                expBuff *= bx.getEffect(bof).getEXPRate() / 100.0;
            }
            bx = SkillFactory.getSkill(20020112);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                chra.getTrait(MapleTraitType.charm).addLocalExp(GameConstants.getTraitExpNeededForLevel(30));
            }
            bx = SkillFactory.getSkill(23000001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                dodgeChance += bx.getEffect(bof).getER();
            }
            bx = SkillFactory.getSkill(23100008);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(23110004);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                dodgeChance += bx.getEffect(bof).getProb();
            }
            bx = SkillFactory.getSkill(23110004);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                damageIncrease.put(23101001, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(23121004);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                dodgeChance += bx.getEffect(bof).getProb();
            }
            bx = SkillFactory.getSkill(23120009);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                watk += bx.getEffect(bof).getX();
            }
            bx = SkillFactory.getSkill(23120010);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                ignoreTargetDEF += bx.getEffect(bof).getX(); //or should we do 100?
            }
            bx = SkillFactory.getSkill(23120011);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                damageIncrease.put(23101001, bx.getEffect(bof).getDAMRate());
            }
            bx = SkillFactory.getSkill(23120012);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                watk += bx.getEffect(bof).getAttackX();
            }
        } else if (MapleJob.is幻影俠盜(job)) {
            bx = SkillFactory.getSkill(20030204); // +10% crit rate
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                passive_sharpeye_rate += eff.getCr();
                passive_sharpeye_min_percent += eff.getCriticalMin();
            }
            bx = SkillFactory.getSkill(20030206); // +40 DEX, Base Avoidability: +20%
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localdex += eff.getDexX();
                dodgeChance += eff.getER();
            }
            bx = SkillFactory.getSkill(24001002); // Swift Phantom
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                speed += eff.getPassiveSpeed();
                jump += eff.getPassiveJump();
            }
            bx = SkillFactory.getSkill(24000003); // Quick Evasion
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dodgeChance += eff.getX();
            }
            bx = SkillFactory.getSkill(24100006); //Luck Monopoly
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localluk += eff.getLukX();
            }
            bx = SkillFactory.getSkill(24110007); // Acute Sense
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                passive_sharpeye_rate += eff.getCr();
                passive_sharpeye_min_percent += eff.getCriticalMin();
            }
            bx = SkillFactory.getSkill(24111002); //Luck of Phantom Thief
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localluk += eff.getLukX();
            }
        } else if (MapleJob.is隱月(job)) {
            bx = SkillFactory.getSkill(25000105);//乾坤一体
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
                percent_mp += eff.getPercentMP();
//                    wdef += eff.getWdefX();
//                    mdef += eff.getMdefX();
            }
            bx = SkillFactory.getSkill(25101205); //后方移动
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dodgeChance += eff.getER();
            }
            bx = SkillFactory.getSkill(25100106); //拳甲修炼
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                trueMastery += eff.getMastery();
            }
            bx = SkillFactory.getSkill(25100108);//力量锻炼
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
            }
            bx = SkillFactory.getSkill(25110107);//精灵凝聚第3招
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
//                    percent_damage += eff.getDAMRate();
                watk += eff.getAttackX();
            }
            bx = SkillFactory.getSkill(25110108); //招魂式
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
//                    wdef += eff.getWdefX();
//                    mdef += eff.getMdefX();
                ASR += eff.getASRRate();
                TER += eff.getTERRate();
            }
            bx = SkillFactory.getSkill(25120112); //精灵凝聚第4招
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
//                    percent_ignore_mob_def_rate += eff.getIgnoreMob();
//                    percent_boss_damage_rate += eff.getBossDamage();
            }
            bx = SkillFactory.getSkill(25120113); //高级拳甲修炼
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                trueMastery += eff.getMastery();
                passive_sharpeye_min_percent += eff.getCriticalMin();
                passive_sharpeye_max_percent += eff.getCriticalMax();
            }
        } else if (MapleJob.is夜光(job)) {
            System.err.println("職業未處理被動技能:" + job);
        } else if (MapleJob.is惡魔(job)) {
            if (MapleJob.is惡魔殺手(job)) {
                mpRecoverProp = 100;
                bx = SkillFactory.getSkill(30010112);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    bossdam_r += eff.getBossDamage();
                    mpRecover += eff.getX();
                    mpRecoverProp += eff.getBossDamage(); //yes
                }
                bx = SkillFactory.getSkill(30010185);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    chra.getTrait(MapleTraitType.will).addLocalExp(GameConstants.getTraitExpNeededForLevel(eff.getY()));
                    chra.getTrait(MapleTraitType.charisma).addLocalExp(GameConstants.getTraitExpNeededForLevel(eff.getZ()));
                }
                bx = SkillFactory.getSkill(30010111);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    hpRecoverPercent += eff.getX();
                    hpRecoverProp += eff.getProb(); //yes
                }
                bx = SkillFactory.getSkill(31000003);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getHpR();
                }
                bx = SkillFactory.getSkill(31100007);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(31000004, eff.getDAMRate());
                    damageIncrease.put(31001006, eff.getDAMRate());
                    damageIncrease.put(31001007, eff.getDAMRate());
                    damageIncrease.put(31001008, eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(31100005);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(31100010);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(31000004, eff.getX());
                    damageIncrease.put(31001006, eff.getX());
                    damageIncrease.put(31001007, eff.getX());
                    damageIncrease.put(31001008, eff.getX());
                }
                bx = SkillFactory.getSkill(31111007);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                    bossdam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                }
                bx = SkillFactory.getSkill(31110008);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dodgeChance += eff.getX();
                    // HACK: shouldn't be here
                    hpRecoverPercent += eff.getY();
                    hpRecoverProp += eff.getX();
                    //mpRecover += eff.getY(); // handle in takeDamage
                    //mpRecoverProp += eff.getX();
                }
                bx = SkillFactory.getSkill(31110009);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mpRecover += 1;
                    mpRecoverProp += eff.getProb();
                }
                bx = SkillFactory.getSkill(31111006);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getX() + 100.0) / 100.0;
                    bossdam_r *= (eff.getX() + 100.0) / 100.0;
                    passive_sharpeye_rate += eff.getY();
                }
                bx = SkillFactory.getSkill(31121006);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    ignoreTargetDEF += bx.getEffect(bof).getIgnoreMob();
                }
                bx = SkillFactory.getSkill(31120011);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(31000004, eff.getX());
                    damageIncrease.put(31001006, eff.getX());
                    damageIncrease.put(31001007, eff.getX());
                    damageIncrease.put(31001008, eff.getX());
                }
                bx = SkillFactory.getSkill(31120008);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getAttackX();
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(31120010);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    percent_wdef += bx.getEffect(bof).getT();
                }
            } else if (MapleJob.is惡魔復仇者(job)) {
                bx = SkillFactory.getSkill(32110000);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    ASR += bx.getEffect(bof).getASRRate();
                    TER += bx.getEffect(bof).getTERRate();
                }
                bx = SkillFactory.getSkill(32110001);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                    bossdam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(32120000);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(32120001);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    dodgeChance += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(32120009);
                bof = chra.getPassiveLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
            }
        } else if (MapleJob.is煉獄巫師(job)) {
            System.err.println("職業未處理被動技能:" + job);
        } else if (MapleJob.is狂豹獵人(job)) {
            bx = SkillFactory.getSkill(33120000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                watk += eff.getX();
                trueMastery += eff.getMastery();
                passive_sharpeye_min_percent += eff.getCriticalMin();
            }
            bx = SkillFactory.getSkill(33110000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dam_r *= (eff.getDamage() + 100.0) / 100.0;
                bossdam_r *= (eff.getDamage() + 100.0) / 100.0;
            }
            bx = SkillFactory.getSkill(33120010);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                ignoreTargetDEF += eff.getIgnoreMob();
                dodgeChance += eff.getER();
            }
            bx = SkillFactory.getSkill(32110001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                bossdam_r *= (eff.getDAMRate() + 100.0) / 100.0;
            }
        } else if (MapleJob.is機甲戰神(job)) {
            bx = SkillFactory.getSkill(35100000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                watk += bx.getEffect(bof).getAttackX();
            }
            bx = SkillFactory.getSkill(35110014);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) { //ME-07 Drillhands, Atomic Hammer
                eff = bx.getEffect(bof);
                damageIncrease.put(35001003, eff.getDAMRate());
                damageIncrease.put(35101003, eff.getDAMRate());
            }
            bx = SkillFactory.getSkill(35120000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                trueMastery += bx.getEffect(bof).getMastery();
            }
            bx = SkillFactory.getSkill(35120001);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) { //Satellite
                eff = bx.getEffect(bof);
                damageIncrease.put(35111005, eff.getX());
                damageIncrease.put(35111011, eff.getX());
                damageIncrease.put(35121009, eff.getX());
                damageIncrease.put(35121010, eff.getX());
                damageIncrease.put(35121011, eff.getX());
                BuffUP_Summon += eff.getY();
            }
            bx = SkillFactory.getSkill(35121006);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) { //Satellite
                eff = bx.getEffect(bof);
                damageIncrease.put(35111001, eff.getDAMRate());
                damageIncrease.put(35111009, eff.getDAMRate());
                damageIncrease.put(35111010, eff.getDAMRate());
            }
        } else if (MapleJob.is傑諾(job)) {
            System.err.println("職業未處理被動技能:" + job);
        } else if (MapleJob.is劍豪(job)) {
            System.err.println("職業未處理被動技能:" + job);
        } else if (MapleJob.is陰陽師(job)) {
            // 五行的加護
            bx = SkillFactory.getSkill(40020000);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getPercentHP();
            }
        } else if (MapleJob.is米哈逸(job)) {
            // Mihile 1st Job Passive Skills
            bx = SkillFactory.getSkill(51000000); // Mihile || HP Boost
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
            bx = SkillFactory.getSkill(51000001); // Mihile || Soul Shield
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                percent_wdef += eff.getX();
                percent_mdef += eff.getX();
            }
            bx = SkillFactory.getSkill(51000002); // Mihile || Soul Devotion
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                accuracy += eff.getAccX();
                speed += eff.getPassiveSpeed();
                jump += eff.getPassiveJump();
            }

            // Mihile 2nd Job Passive Skills
            bx = SkillFactory.getSkill(51100000); // Mihile || Physical Training
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                damageIncrease.put(5001002, eff.getX());
                damageIncrease.put(5001003, eff.getY());
                localstr += eff.getStrX();
                localdex += eff.getDexX();
            }
            bx = SkillFactory.getSkill(51120002); // Mihile || Final Attack && Advanced Final Attack
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                watk += eff.getAttackX();
                damageIncrease.put(51100002, eff.getDamage());
            }

            // Mihile 3rd Job Passive Skills
            bx = SkillFactory.getSkill(51110000); // Mihile || Self Recovery
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                hpRecoverProp += eff.getProb();
                hpRecover += eff.getX();
                mpRecoverProp += eff.getProb();
                mpRecover += eff.getX();
            }
            bx = SkillFactory.getSkill(51110001); // Mihile || Intense Focus
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                // Add Attack Speed here
            }
            bx = SkillFactory.getSkill(51110002); // Mihile || Righteous Indignation
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                ASR += eff.getX();
                percent_atk += eff.getX();
                passive_sharpeye_min_percent += eff.getCriticalMin();
            }

            // Mihile 4th Job Passive Skills
            bx = SkillFactory.getSkill(51120000); // Mihile || Combat Mastery
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                ignoreTargetDEF += eff.getIgnoreMob();
            }
            bx = SkillFactory.getSkill(51120001); // Mihile || Expert Sword Mastery
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                watk += bx.getEffect(bof).getX();
                trueMastery += eff.getMastery();
                passive_sharpeye_min_percent += eff.getCriticalMin();
            }
            bx = SkillFactory.getSkill(51120003); // Mihile || Soul Asylum
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                percent_wdef += bx.getEffect(bof).getT();
            }
        } else if (MapleJob.is凱撒(job)) {
            System.err.println("職業未處理被動技能:" + job);
        } else if (MapleJob.is天使破壞者(job)) {
            System.err.println("職業未處理被動技能:" + job);
        } else if (MapleJob.is神之子(job)) {
            System.err.println("職業未處理被動技能:" + job);
        } else if (MapleJob.is幻獸師(job)) {
            bx = SkillFactory.getSkill(112000011); // Well Fed
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localint_ += eff.getIntX();
                percent_hp += bx.getEffect(bof).getPercentHP();
            }
            bx = SkillFactory.getSkill(112000010); // Dumb Luck
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                percent_wdef += eff.getWDEFRate();
                dam_r *= (eff.getDAMRate() + 100.0) / 100.0;
            }
            bx = SkillFactory.getSkill(112000015); // Fort Follow-Up
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                dam_r *= (eff.getDAMRate() + 100.0) / 100.0;
            }
            bx = SkillFactory.getSkill(112000014); // Bear Strength
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                passive_sharpeye_rate += eff.getCr();
                passive_sharpeye_min_percent += eff.getCriticalMin();
                passive_sharpeye_max_percent += eff.getCriticalMax();
                magic += eff.getMagicX();
            }
            bx = SkillFactory.getSkill(112000013); // Fort the Brave
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                magic += eff.getMagicX();
            }
            bx = SkillFactory.getSkill(112000020); // Billowing Trumpet
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                magic += eff.getMagicX();
            }
        } else if (MapleJob.is皮卡啾(job)) {
            // 皮卡啾之力
            bx = SkillFactory.getSkill(131000014);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                passive_sharpeye_rate += eff.getY();
                passive_sharpeye_rate += Math.ceil((double) chra.getLevel() / eff.getZ());
                dam_r += chra.getLevel() * eff.getW();
                watk += Math.ceil(chra.getLevel() / 2.0D/*getS*/) * eff.getV();
            }
            // 皮卡啾的品格
            bx = SkillFactory.getSkill(131000016);
            bof = chra.getPassiveLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                speed += eff.getPassiveSpeed();
                jump += eff.getPassiveJump();
                percent_hp += eff.getPercentHP();
                percent_mp += eff.getPercentMP();
                ignoreTargetDEF += eff.getIgnoreMob();
            }
        } else if (MapleJob.is凱內西斯(job)) {
            System.err.println("職業未處理被動技能:" + job);
        } else {
            System.err.println("職業未處理被動技能:" + job);
        }
    }

    private void handleHyperPassiveSkills(MapleCharacter chra) {
        int prefix = chra.getJob() * 10000;
        Skill bx;
        int bof;
        MapleStatEffect eff;
        for (int i = 30; i < 50; i++) {
            bx = SkillFactory.getSkill(prefix + i);
            bof = chra.getSkillLevel(bx);
            if (bx != null && bx.isHyper() && bof > 0) {
                eff = bx.getEffect(bof);
                if (eff != null) {
                    switch (i) {
                        case 30:
                            localstr += eff.getStrX();
                            break;
                        case 31:
                            localdex += eff.getDexX();
                            break;
                        case 32:
                            localint_ += eff.getIntX();
                            break;
                        case 33:
                            localluk += eff.getLukX();
                            break;
                        case 34:
                            passive_sharpeye_rate += eff.getCr();
                            break;
                        case 35:
                            accuracy += eff.getAccR();
                            break;
                        case 36:
                            percent_hp += eff.getPercentHP();
                            break;
                        case 37:
                            percent_mp += eff.getPercentMP();
                            break;
                        case 38:
                            localmaxmp += eff.getMaxDemonFury();
                            break;
                        case 39:
                            wdef += eff.getWDEFX();
                            break;
                        case 40:
                            mdef += eff.getMDEFX();
                            break;
                        case 41:
                            speed += eff.getSpeed();
                            break;
                        case 42:
                            jump += eff.getJump();
                            break;
                    }
                }
            }
        }
        for (int i = 30; i < 50; i++) {
            bx = SkillFactory.getSkill(prefix + i);
            bof = chra.getSkillLevel(bx);
            if (bx != null && bx.isHyper() && bof > 0) {
                eff = bx.getEffect(bof);
                String name = bx.getName();
                if (eff != null && name != null) {
                    int skill = GameConstants.findSkillByName(name.split(" - ")[0], prefix, 0);
                    if (skill != 0) {
                        Skill skil = SkillFactory.getSkill(skill);
                        if (skil != null && chra.getSkillLevel(skil) > 0) {
                            if (eff.getDAMRate() > 0) {
                                //skil.getEffect(chra.getSkillLevel(skil)).setDAMRate(eff.getDAMRate());
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleBuffStats(MapleCharacter chra) {
        MapleStatEffect eff = chra.getStatForBuff(MapleBuffStat.MONSTER_RIDING);
        if (eff != null && eff.getSourceId() == 33001001) { // jaguar
            passive_sharpeye_rate += eff.getW();
            percent_hp += eff.getZ();
        }
        Integer buff = chra.getBuffedValue(MapleBuffStat.DICE_ROLL);
        if (buff != null) {
            percent_wdef += GameConstants.getDiceStat(buff, 2);
            percent_mdef += GameConstants.getDiceStat(buff, 2);
            percent_hp += GameConstants.getDiceStat(buff, 3);
            percent_mp += GameConstants.getDiceStat(buff, 3);
            passive_sharpeye_rate += GameConstants.getDiceStat(buff, 4);
            dam_r *= (GameConstants.getDiceStat(buff, 5) + 100.0) / 100.0;
            bossdam_r *= (GameConstants.getDiceStat(buff, 5) + 100.0) / 100.0;
            expBuff *= GameConstants.getDiceStat(buff, 6) / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStat.INDIE_MHP_R);
        if (buff != null) {
            percent_hp += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.INDIE_MMP_R);
        if (buff != null) {
            percent_mp += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.DEFENCE_BOOST_R);
        if (buff != null) {
            percent_wdef += buff;
            percent_mdef += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.ABNORMAL_STATUS_R);
        if (buff != null) {
            ASR += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.ELEMENTAL_STATUS_R);
        if (buff != null) {
            TER += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.INFINITY);
        if (buff != null) {
            percent_matk += buff - 1;
        }
        buff = chra.getBuffedValue(MapleBuffStat.ONYX_SHROUD);
        if (buff != null) {
            dodgeChance += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.PVP_DAMAGE);
        if (buff != null) {
            pvpDamage += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.PVP_ATTACK);
        if (buff != null) {
            pvpDamage += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.FELINE_BERSERK);
        if (buff != null) {
            percent_hp += buff;
        }
        eff = chra.getStatForBuff(MapleBuffStat.BLUE_AURA_OLD);
        if (eff != null) {
            percent_wdef += eff.getZ() + eff.getY();
            percent_mdef += eff.getZ() + eff.getY();
        }
        buff = chra.getBuffedValue(MapleBuffStat.CONVERSION);
        if (buff != null) {
            percent_hp += buff;
        } else {
            buff = chra.getBuffedValue(MapleBuffStat.MAXHP);
            if (buff != null) {
                percent_hp += buff;
            }
        }
        buff = chra.getBuffedValue(MapleBuffStat.MAXMP);
        if (buff != null) {
            percent_mp += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.MP_BUFF);
        if (buff != null) {
            percent_mp += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.BUFF_MASTERY);
        if (buff != null) {
            BuffUP_Skill += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.STR);
        if (buff != null) {
            localstr += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.DEX);
        if (buff != null) {
            localdex += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.INT);
        if (buff != null) {
            localint_ += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.LUK);
        if (buff != null) {
            localluk += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.INDIE_ALL_STATE);
        if (buff != null) {
            localstr += buff;
            localdex += buff;
            localint_ += buff;
            localluk += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.ENHANCED_WDEF);
        if (buff != null) {
            wdef += buff;
        }
/*        buff = chra.getBuffedValue(MapleBuffStat.ENHANCED_MDEF);
        if (buff != null) {
            mdef += buff;
        }*/
        buff = chra.getBuffedValue(MapleBuffStat.WDEF);
        if (buff != null) {
            wdef += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.WDEF);
        if (buff != null) {
            mdef += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.MAPLE_WARRIOR);
        if (buff != null) {
            final double d = buff.doubleValue() / 100.0;
            localstr += d * str; //base only
            localdex += d * dex;
            localluk += d * luk;
            localint_ += d * int_;
        }
        buff = chra.getBuffedValue(MapleBuffStat.ECHO_OF_HERO);
        if (buff != null) {
            final double d = buff.doubleValue() / 100.0;
            watk += (int) (watk * d);
            magic += (int) (magic * d);
        }
        buff = chra.getBuffedValue(MapleBuffStat.ARAN_COMBO);
        if (buff != null) {
            watk += buff / 10;
        }
        buff = chra.getBuffedValue(MapleBuffStat.MESOGUARD);
        if (buff != null) {
            mesoGuardMeso += buff.doubleValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.EXPRATE);
        if (buff != null) {
            expBuff *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStat.INDIE_EXP);
        if (buff != null) {
            indieExp *= (buff.doubleValue() + 100.0) / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStat.DROP_RATE);
        if (buff != null) {
            dropBuff *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStat.ACASH_RATE);
        if (buff != null) {
            cashBuff *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStat.MESO_RATE);
        if (buff != null) {
            mesoBuff *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStat.MESOUP);
        if (buff != null) {
            mesoBuff *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStat.ACC);
        if (buff != null) {
            accuracy += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.INDIE_ACC);
        if (buff != null) {
            accuracy += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.INDIE_PAD);
        if (buff != null) {
            watk += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.INDIE_MAD);
        if (buff != null) {
            magic += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.WATK);
        if (buff != null) {
            watk += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.DAMAGE_R);
        if (buff != null) {
            passive_sharpeye_rate += buff;
            dam_r *= (buff + 100.0) / 100.0;
            bossdam_r *= (buff + 100.0) / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStat.ENHANCED_WATK);
        if (buff != null) {
            watk += buff;
        }
        eff = chra.getStatForBuff(MapleBuffStat.ENERGY_CHARGE);
        if (eff != null) {
            watk += eff.getWatk();
            accuracy += eff.getAcc();
        }
        buff = chra.getBuffedValue(MapleBuffStat.MATK);
        if (buff != null) {
            magic += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.SPEED);
        if (buff != null) {
            speed += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.JUMP);
        if (buff != null) {
            jump += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.DASH_SPEED);
        if (buff != null) {
            speed += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStat.DASH_JUMP);
        if (buff != null) {
            jump += buff;
        }
        eff = chra.getStatForBuff(MapleBuffStat.HIDDEN_POTENTIAL);
        if (eff != null) {
            passive_sharpeye_rate = 100; //INTENSE
            ASR = 100; //INTENSE

            wdef += eff.getX();
            mdef += eff.getX();
            watk += eff.getX();
            magic += eff.getX();
        }
        buff = chra.getBuffedValue(MapleBuffStat.DAMAGE_BUFF);
        if (buff != null) {
            dam_r *= (buff.doubleValue() + 100.0) / 100.0;
            bossdam_r *= (buff.doubleValue() + 100.0) / 100.0;
        }
        buff = chra.getBuffedSkill_Y(MapleBuffStat.FINAL_CUT);
        if (buff != null) {
            dam_r *= buff.doubleValue() / 100.0;
            bossdam_r *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedSkill_Y(MapleBuffStat.OWL_SPIRIT);
        if (buff != null) {
            dam_r *= buff.doubleValue() / 100.0;
            bossdam_r *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.BERSERK_FURY);
        if (buff != null) {
            dam_r *= buff.doubleValue() / 100.0;
            bossdam_r *= buff.doubleValue() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.BLESS);
        if (eff != null) {
            watk += eff.getX();
            magic += eff.getY();
            accuracy += eff.getV();
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.CONCENTRATE);
        if (buff != null) {
            mpconReduce += buff;
        }
        eff = chra.getStatForBuff(MapleBuffStat.HOLY_SHIELD);
        if (eff != null) {
            watk += eff.getX();
            magic += eff.getY();
            accuracy += eff.getV();
            mpconReduce += eff.getMPConReduce();
        }
        eff = chra.getStatForBuff(MapleBuffStat.MAGIC_RESISTANCE);
        if (eff != null) {
            ASR += eff.getX();
        }

        eff = chra.getStatForBuff(MapleBuffStat.COMBO);
        buff = chra.getBuffedValue(MapleBuffStat.COMBO);
        if (eff != null && buff != null) {
            dam_r *= ((100.0 + ((eff.getV() + eff.getDAMRate()) * (buff - 1))) / 100.0);
            bossdam_r *= ((100.0 + ((eff.getV() + eff.getDAMRate()) * (buff - 1))) / 100.0);
        }
        eff = chra.getStatForBuff(MapleBuffStat.SUMMON);
        if (eff != null) {
            if (eff.getSourceId() == 35121010) { //amp
                dam_r *= (eff.getX() + 100.0) / 100.0;
                bossdam_r *= (eff.getX() + 100.0) / 100.0;
            }
        }
        eff = chra.getStatForBuff(MapleBuffStat.DARK_AURA_OLD);
        if (eff != null) {
            dam_r *= (eff.getX() + 100.0) / 100.0;
            bossdam_r *= (eff.getX() + 100.0) / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.BODY_BOOST);
        if (eff != null) {
            dam_r *= (eff.getV() + 100.0) / 100.0;
            bossdam_r *= (eff.getV() + 100.0) / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.BEHOLDER);
        if (eff != null) {
            trueMastery += eff.getMastery();
        }
        eff = chra.getStatForBuff(MapleBuffStat.MECH_CHANGE);
        if (eff != null) {
            passive_sharpeye_rate += eff.getCr();
        }
        eff = chra.getStatForBuff(MapleBuffStat.PYRAMID_PQ);
        if (eff != null && eff.getBerserk() > 0) {
            dam_r *= eff.getBerserk() / 100.0;
            bossdam_r *= eff.getBerserk() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.WK_CHARGE);
        if (eff != null) {
            dam_r *= eff.getDamage() / 100.0;
            bossdam_r *= eff.getDamage() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.PICKPOCKET);
        if (eff != null) {
            pickRate = eff.getProb();
        }
        eff = chra.getStatForBuff(MapleBuffStat.DAMAGE_RATE);
        if (eff != null) {
            dam_r *= (eff.getDAMRate() + 100.0) / 100.0;
            bossdam_r *= (eff.getDAMRate() + 100.0) / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.LIGHTNING_CHARGE);
        if (eff != null) {
            dam_r *= eff.getDamage() / 100.0;
            bossdam_r *= eff.getDamage() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.WIND_WALK);
        if (eff != null) {
            dam_r *= eff.getDamage() / 100.0;
            bossdam_r *= eff.getDamage() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStat.DIVINE_SHIELD);
        if (eff != null) {
            watk += eff.getEnhancedWatk();
        }
        buff = chra.getBuffedSkill_Y(MapleBuffStat.DARKSIGHT);
        if (buff != null) {
            dam_r *= (buff + 100.0) / 100.0;
            bossdam_r *= (buff + 100.0) / 100.0;
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.ENRAGE);
        if (buff != null) {
            dam_r *= (buff + 100.0) / 100.0;
            bossdam_r *= (buff + 100.0) / 100.0;
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.COMBAT_ORDERS);
        if (buff != null) {
            combatOrders += buff;
        }
        eff = chra.getStatForBuff(MapleBuffStat.SHARP_EYES);
        if (eff != null) {
            passive_sharpeye_rate += eff.getX();
            passive_sharpeye_max_percent += eff.getCriticalMax();
        }
        buff = chra.getBuffedValue(MapleBuffStat.CRITICAL_RATE_BUFF);
        if (buff != null) {
            passive_sharpeye_rate += buff;
        }
        if (speed > 140) {
            speed = 140;
        }
        if (jump > 123) {
            jump = 123;
        }
        buff = chra.getBuffedValue(MapleBuffStat.MONSTER_RIDING);
        if (buff != null) {
            jump = 120;
            switch (buff) {
                case 1:
                    speed = 150;
                    break;
                case 2:
                    speed = 170;
                    break;
                case 3:
                    speed = 180;
                    break;
                default:
                    speed = 200; //lol
                    break;
            }
        }
        eff = chra.getStatForBuff(MapleBuffStat.ATTACK);
        if (eff != null) {
            watk = Integer.MAX_VALUE;
        }
    }

    public boolean checkEquipLevels(final MapleCharacter chr, long gain) {
        if (chr.isClone()) {
            return false;
        }
        boolean changed = false;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        List<Equip> all = new ArrayList<>(equipLevelHandling);
        for (Equip eq : all) {
            int lvlz = eq.getEquipLevel();
            eq.setItemEXP(Math.min(eq.getItemEXP() + gain, Long.MAX_VALUE));

            if (eq.getEquipLevel() > lvlz) { //lvlup
                for (int i = eq.getEquipLevel() - lvlz; i > 0; i--) {
                    //now for the equipment increments...
                    final Map<Integer, Map<String, Integer>> inc = ii.getEquipIncrements(eq.getItemId());
                    int extra = eq.getYggdrasilWisdom();
                    if (extra == 1) {
                        inc.get(lvlz + i).put("STRMin", 1);
                        inc.get(lvlz + i).put("STRMax", 3);
                    } else if (extra == 2) {
                        inc.get(lvlz + i).put("DEXMin", 1);
                        inc.get(lvlz + i).put("DEXMax", 3);
                    } else if (extra == 3) {
                        inc.get(lvlz + i).put("INTMin", 1);
                        inc.get(lvlz + i).put("INTMax", 3);
                    } else if (extra == 4) {
                        inc.get(lvlz + i).put("LUKMin", 1);
                        inc.get(lvlz + i).put("LUKMax", 3);
                    }
                    if (inc != null && inc.containsKey(lvlz + i)) { //flair = 1
                        eq = ii.levelUpEquip(eq, inc.get(lvlz + i));
                    }
                    //UGH, skillz
                    if (GameConstants.getStatFromWeapon(eq.getItemId()) == null && GameConstants.getMaxLevel(eq.getItemId()) < (lvlz + i) && Math.random() < 0.1 && eq.getIncSkill() <= 0 && ii.getEquipSkills(eq.getItemId()) != null) {
                        for (int zzz : ii.getEquipSkills(eq.getItemId())) {
                            final Skill skil = SkillFactory.getSkill(zzz);
                            if (skil != null && skil.canBeLearnedBy(chr.getJob())) { //dont go over masterlevel :D
                                eq.setIncSkill(skil.getId());
                                chr.dropMessage(5, "Your skill has gained a levelup: " + skil.getName() + " +1");
                            }
                        }
                    }
                }
                changed = true;
            }
            chr.forceReAddItem(eq.copy(), MapleInventoryType.EQUIPPED);
        }
        if (changed) {
            chr.equipChanged();
            chr.getClient().announce(EffectPacket.showItemLevelupEffect());
            chr.getMap().broadcastMessage(chr, EffectPacket.showItemLevelupEffect(chr), false);
        }
        return changed;
    }

    public boolean checkEquipDurabilitys(final MapleCharacter chr, int gain) {
        return checkEquipDurabilitys(chr, gain, false);
    }

    public boolean checkEquipDurabilitys(final MapleCharacter chr, int gain, boolean aboveZero) {
        if (chr.isClone() || chr.inPVP()) {
            return true;
        }
        List<Equip> all = new ArrayList<>(durabilityHandling);
        for (Equip item : all) {
            if (item != null && ((item.getPosition() >= 0) == aboveZero)) {
                item.setDurability(item.getDurability() + gain);
                if (item.getDurability() < 0) { //shouldnt be less than 0
                    item.setDurability(0);
                }
            }
        }
        for (Equip eqq : all) {
            if (eqq != null && eqq.getDurability() == 0 && eqq.getPosition() < 0) { //> 0 went to negative
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    chr.getClient().announce(InventoryPacket.getInventoryFull());
                    chr.getClient().announce(InventoryPacket.getShowInventoryFull());
                    return false;
                }
                durabilityHandling.remove(eqq);
                final short pos = chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
                MapleInventoryManipulator.unequip(chr.getClient(), eqq.getPosition(), pos);
            } else if (eqq != null) {
                chr.forceReAddItem(eqq.copy(), MapleInventoryType.EQUIPPED);
            }
        }
        return true;
    }

    private void CalcPassive_SharpEye(final MapleCharacter player) {
        Skill critSkill;
        int critlevel;
        if (MapleJob.is惡魔殺手(player.getJob())) {
            critSkill = SkillFactory.getSkill(30010022);
            critlevel = player.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                passive_sharpeye_rate += critSkill.getEffect(critlevel).getProb();
                this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
            }
        } else if (MapleJob.is精靈遊俠(player.getJob())) {
            critSkill = SkillFactory.getSkill(20020022);
            critlevel = player.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                passive_sharpeye_rate += critSkill.getEffect(critlevel).getProb();
                this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
            }
        } else if (MapleJob.is末日反抗軍(player.getJob())) {
            critSkill = SkillFactory.getSkill(30000022);
            critlevel = player.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                passive_sharpeye_rate += critSkill.getEffect(critlevel).getProb();
                this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
            }
        }
        switch (player.getJob()) { // Apply passive Critical bonus
            case 410: // Assasin
            case 411: // Hermit
            case 412: { // Night Lord
                critSkill = SkillFactory.getSkill(4100001); // Critical Throw
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 2412: { // Phantom
                critSkill = SkillFactory.getSkill(24120006);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                    this.watk += critSkill.getEffect(critlevel).getAttackX();
                }
                break;
            }
            case 1410:
            case 1411:
            case 1412: { // Night Walker
                critSkill = SkillFactory.getSkill(14100001);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 3100:
            case 3110:
            case 3111:
            case 3112: {
                critSkill = SkillFactory.getSkill(31100006);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.watk += critSkill.getEffect(critlevel).getAttackX();
                }
                break;
            }
            case 2300:
            case 2310:
            case 2311:
            case 2312: {
                critSkill = SkillFactory.getSkill(23000003);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 3210:
            case 3211:
            case 3212: {
                critSkill = SkillFactory.getSkill(32100006);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 434: {
                critSkill = SkillFactory.getSkill(4340010);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 520:
            case 521:
            case 522: {
                critSkill = SkillFactory.getSkill(5200007);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 1211:
            case 1212: {
                critSkill = SkillFactory.getSkill(12110000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 530:
            case 531:
            case 532: {
                critSkill = SkillFactory.getSkill(5300004);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 510:
            case 511:
            case 512: { // Buccaner, Viper
                critSkill = SkillFactory.getSkill(5110000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) critSkill.getEffect(critlevel).getProb();
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                //final Skill critSkill2 = SkillFactory.getSkill(5100008);
                // final int critlevel2 = player.getTotalSkillLevel(critSkill);
                //  if (critlevel2 > 0) {
                //     this.passive_sharpeye_rate += (short) critSkill2.getEffect(critlevel2).getCr();
                //     this.passive_sharpeye_min_percent += critSkill2.getEffect(critlevel2).getCriticalMin();
                // }
                return;
            }
            case 1511:
            case 1512: {
                critSkill = SkillFactory.getSkill(15110000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 2111:
            case 2112: {
                critSkill = SkillFactory.getSkill(21110000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) ((critSkill.getEffect(critlevel).getX() * critSkill.getEffect(critlevel).getY()) + critSkill.getEffect(critlevel).getCr());
                }
                break;
            }
            case 300:
            case 310:
            case 311:
            case 312:
            case 320:
            case 321:
            case 322: { // Bowman
                critSkill = SkillFactory.getSkill(3000001);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 1300:
            case 1310:
            case 1311:
            case 1312: { // Bowman
                critSkill = SkillFactory.getSkill(13000000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 2214:
            case 2215:
            case 2216:
            case 2217:
            case 2218: { //Evan
                critSkill = SkillFactory.getSkill(22140000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
        }
    }

    private void CalcPassive_Mastery(final MapleCharacter player) {
        if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11) == null) {
            passive_mastery = 0;
            return;
        }
        final int skil;
        final MapleWeaponType weaponType = ItemConstants.武器類型(player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11).getItemId());
        trueMastery += weaponType.getBaseMastery();

        if (MapleJob.is皮卡啾(player.getJob())) {
            if (player.getSkillLevel(131000014) <= 0) {
                passive_mastery = 0;
                return;
            }
            final MapleStatEffect eff = SkillFactory.getSkill(131000014).getEffect(player.getTotalSkillLevel(131000014));
            switch (weaponType) {
                case 單手劍:
                case 單手斧:
                case 單手棍:
                case 雙手斧:
                case 雙手劍:
                case 雙手棍:
                case 槍:
                case 矛:
                    passive_mastery = (byte) eff.getU();
                    break;
            }
            trueMastery = passive_mastery;
            return;
        }

        boolean acc = true;
        switch (weaponType) {
            case 閃亮克魯:
                if (player.getSkillLevel(27120007) > 0) {
                    skil = 27120007;
                } else {
                    skil = 27100005;
                }
                break;
            case 靈魂射手:
                if (player.getSkillLevel(65120005) > 0) {
                    skil = 65120005;
                } else {
                    skil = 65100003;
                }
                break;
            case 魔劍:
                if (player.getSkillLevel(31220006) > 0) {
                    skil = 31220006;
                } else {
                    skil = 31200005;
                }
                break;
            case 弓:
                skil = MapleJob.is皇家騎士團(player.getJob()) ? 13100000 : 3100000;
                break;
            case 拳套:
                skil = 4100000;
                break;
            case 手杖:
                skil = player.getTotalSkillLevel(24120006) > 0 ? 24120006 : 24100004;
                break;
            case 加農炮:
                skil = 5300005;
                break;
            case 短劍:
            case 雙刀:
                skil = player.getJob() >= 430 && player.getJob() <= 434 ? 4300000 : 4200000;
                break;
            case 弩:
                skil = MapleJob.is末日反抗軍(player.getJob()) ? 33100000 : 3200000;
                break;
            case 單手斧:
            case 單手棍:
                skil = MapleJob.is末日反抗軍(player.getJob()) ? 31100004 : (MapleJob.is皇家騎士團(player.getJob()) ? 11100000 : (player.getJob() > 112 ? 1200000 : 1100000)); //hero/pally
                break;
            case 雙手斧:
            case 單手劍:
            case 雙手劍:
            case 雙手棍:
                skil = MapleJob.is皇家騎士團(player.getJob()) ? 11100000 : (player.getJob() > 112 ? 1200000 : 1100000); //hero/pally
                break;
            case 矛:
                skil = MapleJob.is狂狼勇士(player.getJob()) ? 21100000 : 1300000;
                break;
            case 槍:
                skil = 1300000;
                break;
            case 指虎:
                skil = MapleJob.is皇家騎士團(player.getJob()) ? 15100001 : 5100001;
                break;
            case 火槍:
                skil = MapleJob.is末日反抗軍(player.getJob()) ? 35100000 : (MapleJob.is蒼龍俠客(player.getJob()) ? 5700000 : 5200000);
                break;
            case 雙弩槍:
                skil = 23100005;
                break;
            case 長杖:
            case 短杖:
                acc = false;
                skil = MapleJob.is末日反抗軍(player.getJob()) ? 32100006 : (player.getJob() <= 212 ? 2100006 : (player.getJob() <= 222 ? 2200006 : (player.getJob() <= 232 ? 2300006 : (player.getJob() <= 2000 ? 12100007 : 22120002))));
                break;
            default:
                passive_mastery = 0;
                return;
        }
        if (player.getSkillLevel(skil) <= 0) {
            passive_mastery = 0;
            return;
        }
        final MapleStatEffect eff = SkillFactory.getSkill(skil).getEffect(player.getTotalSkillLevel(skil));
        if (acc) {
            accuracy += eff.getX();
            if (skil == 35100000) {
                watk += eff.getX();
            }
        } else {
            magic += eff.getX();
        }
        passive_sharpeye_rate += eff.getCr();
        passive_mastery = (byte) eff.getMastery();
        trueMastery += passive_mastery;
        if (player.getJob() == 412) {
            final Skill bx = SkillFactory.getSkill(4120012); // Claw Expert
            final int bof = player.getTotalSkillLevel(bx);
            if (bof > 0) {
                final MapleStatEffect eff2 = bx.getEffect(bof);
                passive_mastery = (byte) eff2.getMastery(); // Override
                accuracy += eff2.getPercentAcc();
                dodgeChance += eff2.getPercentAvoid();
                watk += eff2.getX();
                trueMastery -= eff.getMastery(); // - old
                trueMastery += eff2.getMastery(); // add new
            }
        }
    }

    private void calculateFame(final MapleCharacter player) {
        player.getTrait(MapleTraitType.charm).addLocalExp(player.getFame());
        for (MapleTraitType t : MapleTraitType.values()) {
            player.getTrait(t).recalcLevel();
        }
    }

    public final short passive_sharpeye_min_percent() {
        return (short) Math.min(passive_sharpeye_min_percent, passive_sharpeye_max_percent);
    }

    public final short passive_sharpeye_percent() {
        return (short) Math.max(passive_sharpeye_min_percent, passive_sharpeye_max_percent);
    }

    public final short passive_sharpeye_rate() {
        return passive_sharpeye_rate;
    }

    public final byte passive_mastery() {
        return passive_mastery; //* 5 + 10 for mastery %
    }

    public final void calculateMaxBaseDamage(final int watk, final int pvpDamage, MapleCharacter chra) {
        if (watk <= 0) {
            localmaxbasedamage = 1;
            localmaxbasepvpdamage = 1;
        } else {
            final Item weapon_item = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
            final int job = chra.getJob();
            final MapleWeaponType weapon = weapon_item == null ? MapleWeaponType.沒有武器 : ItemConstants.武器類型(weapon_item.getItemId());
            int stat, statpvp;
            final boolean mage = MapleJob.is法師(job);
            switch (weapon) {
                case 能量劍:
                    stat = 4 * (localstr + localdex + localluk);
                    statpvp = 4 * (str + dex + luk);
                    break;
                case 靈魂射手:
                case 弓:
                case 弩:
                case 火槍:
                case 雙弩槍:
                    stat = 4 * localdex + localstr;
                    statpvp = 4 * + dex + str;
                    break;
                case 短劍:
                    stat = 4 * localluk + localdex;
                    statpvp = 4 * + luk + dex;
                    if (MapleJob.is盜賊(job)) {
                        stat = localstr;
                        statpvp = str;
                    }
                    break;
                case 拳套:
                case 手杖:
                    stat = 4 * localluk + localdex;
                    statpvp = 4 * + luk + dex;
                    break;
                case 魔劍:
                    stat = localmaxhp / 7 + localstr;
                    statpvp = maxhp / 7 + str;
                    break;
                default:
                    if (mage) {
                        stat = 4 * localint_ + localluk;
                        statpvp = 4 * + int_ + luk;
                    } else {
                        stat = 4 * localstr + localdex;
                        statpvp = 4 * + str + dex;
                    }
                    break;
            }
            localmaxbasedamage = weapon.getMaxDamageMultiplier(job) * stat * watk / 100.0f;
            localmaxbasepvpdamage = weapon.getMaxDamageMultiplier(job) * statpvp * (100.0f + (pvpDamage / 100.0f));
            localmaxbasepvpdamageL = weapon.getMaxDamageMultiplier(job) * stat * (100.0f + (pvpDamage / 100.0f));
            localmaxbasedamage *= dam_r / 100.0D;
            localmaxbasepvpdamage *= dam_r / 100.0D;
            localmaxbasepvpdamageL *= dam_r / 100.0D;
        }
    }

    public final float getHealHP() {
        return shouldHealHP;
    }

    public final float getHealMP() {
        return shouldHealMP;
    }

    public final void relocHeal(MapleCharacter chra) {
        if (chra.isClone()) {
            return;
        }
        final int playerjob = chra.getJob();

        shouldHealHP = 10 + recoverHP; // Reset
        shouldHealMP = MapleJob.is惡魔殺手(chra.getJob()) ? 0 : (3 + recoverMP + (localint_ / 10)); // i think
        mpRecoverTime = 0;
        hpRecoverTime = 0;
        if (playerjob == 111 || playerjob == 112) {
            final Skill effect = SkillFactory.getSkill(1110000); // Improving MP Recovery
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                MapleStatEffect eff = effect.getEffect(lvl);
                if (eff.getHp() > 0) {
                    shouldHealHP += eff.getHp();
                    hpRecoverTime = 4000;
                }
                shouldHealMP += eff.getMp();
                mpRecoverTime = 4000;
            }

        } else if (playerjob == 1111 || playerjob == 1112) {
            final Skill effect = SkillFactory.getSkill(11110000); // Improving MP Recovery
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealMP += effect.getEffect(lvl).getMp();
                mpRecoverTime = 4000;
            }
        } else if (MapleJob.is精靈遊俠(playerjob)) {
            final Skill effect = SkillFactory.getSkill(20020109); // Improving MP Recovery
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealHP += (effect.getEffect(lvl).getX() * localmaxhp) / 100;
                hpRecoverTime = 4000;
                shouldHealMP += (effect.getEffect(lvl).getX() * localmaxmp) / 100;
                mpRecoverTime = 4000;
            }
        } else if (MapleJob.is蒼龍俠客(playerjob) && playerjob != 508) {
            final Skill effect = SkillFactory.getSkill(5700005); // Perseverance
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                final MapleStatEffect eff = effect.getEffect(lvl);
                shouldHealHP += eff.getX();
                shouldHealMP += eff.getX();
                hpRecoverTime = eff.getY();
                mpRecoverTime = eff.getY();
            }
        } else if (playerjob == 3111 || playerjob == 3112) {
            final Skill effect = SkillFactory.getSkill(31110009); // Improving MP Recovery
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealMP += effect.getEffect(lvl).getY();
                mpRecoverTime = 4000;
            }
        }
        if (chra.getChair() != 0) { // Is sitting on a chair.
            shouldHealHP += 99; // Until the values of Chair heal has been fixed,
            shouldHealMP += 99; // MP is different here, if chair data MP = 0, heal + 1.5
        } else if (chra.getMap() != null) { // Because Heal isn't multipled when there's a chair :)
            final float recvRate = chra.getMap().getRecoveryRate();
            if (recvRate > 0) {
                shouldHealHP *= recvRate;
                shouldHealMP *= recvRate;
            }
        }
    }

    public final void connectData(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        mplew.writeShort(str);
        mplew.writeShort(dex);
        mplew.writeShort(int_);
        mplew.writeShort(luk);
        mplew.writeInt(hp);
        mplew.writeInt(maxhp);
        mplew.writeInt(mp);
        mplew.writeInt(GameConstants.fixDemonForce(chr));
    }

    public final void zeroData(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        mplew.writeInt(0);
        mplew.write(0xFF);
        mplew.write(0);
        mplew.writeInt(maxhp);
        mplew.writeInt(maxmp);
        mplew.write(0);
        mplew.writeInt(chr.getSecondHair());
        mplew.writeInt(chr.getSecondFace());
        mplew.writeInt(maxhp);
        mplew.writeInt(maxmp);
    }
    private final static int[] allJobs = {0, 10000, 10000000, 20000000, 20010000, 20020000, 20030000, 20040000, 20050000, 30000000, 30010000, 50000000};
    public final static int[] pvpSkills = {1000007, 2000007, 3000006, 4000010, 5000006, 5010004, 11000006, 12000006, 13000005, 14000006, 15000005, 21000005, 22000002, 23000004, 31000005, 32000012, 33000004, 35000005};

    public static int getSkillByJob(final int skillID, final int job) { //test
        return skillID + (MapleJob.getBeginner((short) job) * 10000);
    }

    public final int getSkillIncrement(final int skillID) {
        if (skillsIncrement.containsKey(skillID)) {
            return skillsIncrement.get(skillID);
        }
        return 0;
    }

    public final int getElementBoost(final Element key) {
        if (elemBoosts.containsKey(key)) {
            return elemBoosts.get(key);
        }
        return 0;
    }

    public final int getDamageIncrease(final int key) {
        if (damageIncrease.containsKey(key)) {
            return damageIncrease.get(key) + damX;
        }
        return damX;
    }

    public final int getAccuracy() {
        return accuracy;
    }

    public void heal_noUpdate(MapleCharacter chra) {
        setHp(getCurrentMaxHp(), chra);
        setMp(getCurrentMaxMp(), chra);
    }

    public void heal(MapleCharacter chra) {
        heal_noUpdate(chra);
        chra.updateSingleStat(MapleStat.HP, getCurrentMaxHp());
        chra.updateSingleStat(MapleStat.MP, getCurrentMaxMp());
    }

    public Pair<Integer, Integer> handleEquipAdditions(MapleItemInformationProvider ii, MapleCharacter chra, boolean first_login, Map<Skill, SkillEntry> sData, final int itemId) {
        final List<Triple<String, String, String>> additions = ii.getEquipAdditions(itemId);
        if (additions == null) {
            return null;
        }
        int localmaxhp_x = 0, localmaxmp_x = 0;
        int skillid = 0, skilllevel = 0;
        String craft, job, level;
        for (final Triple<String, String, String> add : additions) {
            if (add.getMid().contains("con")) {
                continue;
            }
            final int right = Integer.parseInt(add.getRight());
            switch (add.getLeft()) {
                case "elemboost":
                    craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                    if (add.getMid().equals("elemVol") && (craft == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                        int value = Integer.parseInt(add.getRight().substring(1, add.getRight().length()));
                        final Element key = Element.getFromChar(add.getRight().charAt(0));
                        if (elemBoosts.get(key) != null) {
                            value += elemBoosts.get(key);
                        }
                        elemBoosts.put(key, value);
                    }
                    break;
                case "mobcategory": //skip the category, thinkings too expensive to have yet another Map<Integer, Integer> for damage calculations
                    if (add.getMid().equals("damage")) {
                        dam_r *= (right + 100.0) / 100.0;
                        bossdam_r += (right + 100.0) / 100.0;
                    }
                    break;
                case "critical": // lv critical lvl?
                    boolean canJob = false,
                     canLevel = false;
                    job = ii.getEquipAddReqs(itemId, add.getLeft(), "job");
                    if (job != null) {
                        if (job.contains(",")) {
                            final String[] jobs = job.split(",");
                            for (final String x : jobs) {
                                if (chra.getJob() == Integer.parseInt(x)) {
                                    canJob = true;
                                }
                            }
                        } else {
                            if (chra.getJob() == Integer.parseInt(job)) {
                                canJob = true;
                            }
                        }
                    }
                    level = ii.getEquipAddReqs(itemId, add.getLeft(), "level");
                    if (level != null) {
                        if (chra.getLevel() >= Integer.parseInt(level)) {
                            canLevel = true;
                        }
                    }
                    if ((job != null && canJob || job == null) && (level != null && canLevel || level == null)) {
                        switch (add.getMid()) {
                            case "prob":
                                passive_sharpeye_rate += right;
                                break;
                            case "damage":
                                passive_sharpeye_min_percent += right;
                                passive_sharpeye_max_percent += right; //???CONFIRM - not sure if this is max or minCritDmg
                                break;
                        }
                    }
                    break;
                case "boss": // ignore prob, just add
                    craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                    if (add.getMid().equals("damage") && (craft == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                        bossdam_r *= (right + 100.0) / 100.0;
                    }
                    break;
                case "mobdie": // lv, hpIncRatioOnMobDie, hpRatioProp, mpIncRatioOnMobDie, mpRatioProp, modify =D, don't need mob to die
                    craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                    if ((craft == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                        switch (add.getMid()) {
                            case "hpIncOnMobDie":
                                hpRecover += right;
                                hpRecoverProp += 5;
                                break;
                            case "mpIncOnMobDie":
                                mpRecover += right;
                                mpRecoverProp += 5;
                                break;
                        }
                    }
                    break;
                case "skill": // all these are additional skills
                    if (first_login) {
                        craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                        if ((craft == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                            switch (add.getMid()) {
                                case "id":
                                    skillid = right;
                                    break;
                                case "level":
                                    skilllevel = right;
                                    break;
                            }
                        }
                    }
                    break;
                case "hpmpchange":
                    switch (add.getMid()) {
                        case "hpChangerPerTime":
                            recoverHP += right;
                            break;
                        case "mpChangerPerTime":
                            recoverMP += right;
                            break;
                    }
                    break;
                case "statinc":
                    boolean canJobx = false,
                     canLevelx = false;
                    job = ii.getEquipAddReqs(itemId, add.getLeft(), "job");
                    if (job != null) {
                        if (job.contains(",")) {
                            final String[] jobs = job.split(",");
                            for (final String x : jobs) {
                                if (chra.getJob() == Integer.parseInt(x)) {
                                    canJobx = true;
                                }
                            }
                        } else if (chra.getJob() == Integer.parseInt(job)) {
                            canJobx = true;
                        }
                    }
                    level = ii.getEquipAddReqs(itemId, add.getLeft(), "level");
                    if (level != null && chra.getLevel() >= Integer.parseInt(level)) {
                        canLevelx = true;
                    }
                    if ((!canJobx && job != null) || (!canLevelx && level != null)) {
                        continue;
                    }
                    if (itemId == 1142367) {
                        final int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                        if (day != 1 && day != 7) {
                            continue;
                        }
                    }
                    switch (add.getMid()) {
                        case "incPAD":
                            watk += right;
                            break;
                        case "incMAD":
                            magic += right;
                            break;
                        case "incSTR":
                            localstr += right;
                            break;
                        case "incDEX":
                            localdex += right;
                            break;
                        case "incINT":
                            localint_ += right;
                            break;
                        case "incLUK":
                            localluk += right;
                            break;
                        case "incJump":
                            jump += right;
                            break;
                        case "incMHP":
                            localmaxhp_x += right;
                            break;
                        case "incMMP":
                            localmaxmp_x += right;
                            break;
                        case "incPDD":
                            wdef += right;
                            break;
                        case "incMDD":
                            mdef += right;
                            break;
                        case "incACC":
                            accuracy += right;
                            break;
                        case "incEVA":
                            break;
                        case "incSpeed":
                            speed += right;
                            break;
                        case "incMMPr":
                            percent_mp += right;
                            break;
                    }
                    break;
            }
        }
        if (skillid != 0 && skilllevel != 0) {
            sData.put(SkillFactory.getSkill(skillid), new SkillEntry((byte) skilllevel, (byte) 0, -1));
        }
        return new Pair<>(localmaxhp_x, localmaxmp_x);
    }

    public void handleItemOption(StructItemOption soc, MapleCharacter chra, boolean first_login, Map<Skill, SkillEntry> hmm) {
        localstr += soc.get("incSTR");
        localdex += soc.get("incDEX");
        localint_ += soc.get("incINT");
        localluk += soc.get("incLUK");
        accuracy += soc.get("incACC");
        // incEVA -> increase dodge
        speed += soc.get("incSpeed");
        jump += soc.get("incJump");
        watk += soc.get("incPAD");
        magic += soc.get("incMAD");
        wdef += soc.get("incPDD");
        mdef += soc.get("incMDD");
        percent_str += soc.get("incSTRr");
        percent_dex += soc.get("incDEXr");
        percent_int += soc.get("incINTr");
        percent_luk += soc.get("incLUKr");
        percent_hp += soc.get("incMHPr");
        percent_mp += soc.get("incMMPr");
        percent_acc += soc.get("incACCr");
        dodgeChance += soc.get("incEVAr");
        percent_atk += soc.get("incPADr");
        percent_matk += soc.get("incMADr");
        percent_wdef += soc.get("incPDDr");
        percent_mdef += soc.get("incMDDr");
        passive_sharpeye_rate += soc.get("incCr");
        bossdam_r *= (soc.get("incDAMr") + 100.0) / 100.0;
        if (soc.get("boss") <= 0) {
            dam_r *= (soc.get("incDAMr") + 100.0) / 100.0;
        }
        recoverHP += soc.get("RecoveryHP"); // This shouldn't be here, set 4 seconds.
        recoverMP += soc.get("RecoveryMP"); // This shouldn't be here, set 4 seconds.
        if (soc.get("HP") > 0) { // Should be heal upon attacking
            hpRecover += soc.get("HP");
            hpRecoverProp += soc.get("prop");
        }
        if (soc.get("MP") > 0 && !MapleJob.is惡魔殺手(chra.getJob())) {
            mpRecover += soc.get("MP");
            mpRecoverProp += soc.get("prop");
        }
        ignoreTargetDEF += soc.get("ignoreTargetDEF");
        if (soc.get("ignoreDAM") > 0) {
            ignoreDAM += soc.get("ignoreDAM");
            ignoreDAM_rate += soc.get("prop");
        }
        incAllskill += soc.get("incAllskill");
        if (soc.get("ignoreDAMr") > 0) {
            ignoreDAMr += soc.get("ignoreDAMr");
            ignoreDAMr_rate += soc.get("prop");
        }
        RecoveryUP += soc.get("RecoveryUP"); // only for hp items and skills
        passive_sharpeye_min_percent += soc.get("incCriticaldamageMin");
        passive_sharpeye_max_percent += soc.get("incCriticaldamageMax");
        TER += soc.get("incTerR"); // elemental resistance = avoid element damage from monster
        ASR += soc.get("incAsrR"); // abnormal status = disease
        if (soc.get("DAMreflect") > 0) {
            DAMreflect += soc.get("DAMreflect");
            DAMreflect_rate += soc.get("prop");
        }
        mpconReduce += soc.get("mpconReduce");
        reduceCooltime += soc.get("reduceCooltime"); // in seconds
        incMesoProp += soc.get("incMesoProp"); // mesos + %
        dropBuff *= (100 + soc.get("incRewardProp")) / 100.0; // extra drop rate for item
        if (first_login && soc.get("skillID") > 0) {
            hmm.put(SkillFactory.getSkill(getSkillByJob(soc.get("skillID"), chra.getJob())), new SkillEntry((byte) 1, (byte) 0, -1));
        }
        bossdam_r *= (soc.get("bdR") + 100.0) / 100.0;
        ignoreTargetDEF *= (soc.get("imdR") + 100.0) / 100.0;
        // poison, stun, etc (uses level field -> cast disease to mob/player), face?
    }

    public final void handleProfessionTool(final MapleCharacter chra) {
        if (chra.getProfessionLevel(92000000) > 0 || chra.getProfessionLevel(92010000) > 0) {
            final Iterator<Item> itera = chra.getInventory(MapleInventoryType.EQUIP).newList().iterator();
            while (itera.hasNext()) { //goes to first harvesting tool and stops
                final Equip equip = (Equip) itera.next();
                if (equip.getDurability() != 0 && (equip.getItemId() / 10000 == 150 && chra.getProfessionLevel(92000000) > 0) || (equip.getItemId() / 10000 == 151 && chra.getProfessionLevel(92010000) > 0)) {
                    if (equip.getDurability() > 0) {
                        durabilityHandling.add(equip);
                    }
                    harvestingTool = equip.getPosition();
                    break;
                }
            }
        }
    }

    public void recalcPVPRank(MapleCharacter chra) {
        this.pvpRank = 10;
        this.pvpExp = chra.getTotalBattleExp();
        for (int i = 0; i < 10; i++) {
            if (pvpExp > GameConstants.getPVPExpNeededForLevel(i + 1)) {
                pvpRank--;
                pvpExp -= GameConstants.getPVPExpNeededForLevel(i + 1);
            }
        }
    }

    public int getHPPercent() {
        return (int) Math.ceil((hp * 100.0) / localmaxhp);
    }

    public final void init(MapleCharacter chra) {
        recalcLocalStats(chra);
    }

    public final int getStr() {
        return str;
    }

    public final int getDex() {
        return dex;
    }

    public final int getInt() {
        return int_;
    }

    public final int getLuk() {
        return luk;
    }

    public final int getHp() {
        return hp;
    }

    public final int getMp() {
        return mp;
    }

    public final int getMaxHp() {
        return maxhp;
    }

    public final int getMaxMp() {
        return maxmp;
    }

    public final void setStr(final short str, MapleCharacter chra) {
        this.str = str;
        recalcLocalStats(chra);
    }

    public final void setDex(final short dex, MapleCharacter chra) {
        this.dex = dex;
        recalcLocalStats(chra);
    }

    public final void setInt(final short int_, MapleCharacter chra) {
        this.int_ = int_;
        recalcLocalStats(chra);
    }

    public final void setLuk(final short luk, MapleCharacter chra) {
        this.luk = luk;
        recalcLocalStats(chra);
    }

    public final boolean setHp(final int newhp, MapleCharacter chra) {
        return setHp(newhp, false, chra);
    }

    public final boolean setHp(int newhp, boolean silent, MapleCharacter chra) {
        final int oldHp = hp;
        int thp = newhp;
        if (thp < 0) {
            thp = 0;
        }
        if (thp > localmaxhp) {
            thp = localmaxhp;
        }
        this.hp = thp;
        if (chra != null) {
            if (oldHp > hp && !chra.isAlive()) {
                // 技能免死在這
                if (chra.getBuffedValue(MapleBuffStat.FINAL_FEINT) != null) {
                    int percentage = chra.getBuffedValue(MapleBuffStat.FINAL_FEINT);
                    this.hp = ((int) (this.localmaxhp * (percentage / 100.0D)));
                    this.mp = ((int) (this.localmaxmp * (percentage / 100.0D)));
                    chra.updateSingleStat(MapleStat.HP, this.hp);
                    chra.updateSingleStat(MapleStat.MP, this.mp);
                    chra.cancelEffectFromBuffStat(MapleBuffStat.FINAL_FEINT);
                    chra.setStance(0);
                    chra.changeMap(chra.getMap(), chra.getMap().getPortal(0));
                    chra.dropMessage(6, "以消耗掉幸運幻影的效果替代死亡，並回復最大值" + percentage + "%的HP。");
                    chra.cancelEffectFromBuffStat(MapleBuffStat.FINAL_FEINT, -1);
                } else {
                    // 戰鬥機器人效果添加的地方
//                    Timer.MapTimer.getInstance().schedule(() -> chra.getClient().announce(CField.getDeathTip(1)), 1000);
//                    chra.getClient().announce(CField.getDeathTip(1));
                    chra.playerDead();
                }
            }
            if (!silent) {
                chra.checkBerserk();
                chra.updatePartyMemberHP();
            }
        }
        if (MapleJob.is惡魔復仇者(chra.getJob())) {
            chra.getClient().announce(JobPacket.AvengerPacket.giveAvengerHpBuff(hp));
        }
        return hp != oldHp;
    }

    public final boolean setMp(final int newmp, final MapleCharacter chra) {
        final int oldMp = mp;
        int tmp = newmp;
        if (tmp < 0) {
            tmp = 0;
        }
        if (tmp > localmaxmp) {
            tmp = localmaxmp;
        }
        this.mp = tmp;
        return mp != oldMp;
    }

    public final void setMaxHp(final int hp, MapleCharacter chra) {
        this.maxhp = hp;
        recalcLocalStats(chra);
    }

    public final void setMaxMp(final int mp, MapleCharacter chra) {
        this.maxmp = mp;
        recalcLocalStats(chra);
    }

    public final void setInfo(final int maxhp, final int maxmp, final int hp, final int mp) {
        this.maxhp = maxhp;
        this.maxmp = maxmp;
        this.hp = hp;
        this.mp = mp;
    }

    public final int getTotalStr() {
        return localstr;
    }

    public final int getTotalDex() {
        return localdex;
    }

    public final int getTotalInt() {
        return localint_;
    }

    public final int getTotalLuk() {
        return localluk;
    }

    public final int getTotalWatk() {
        return watk;
    }

    public final int getTotalMagic() {
        return magic;
    }

    public final int getCurrentMaxHp() {
        return localmaxhp;
    }

    public final int getCurrentMaxMp() {
        return localmaxmp;
    }

    public final int getHands() {
        return hands; // Only used for stimulator/maker skills
    }

    public final float getCurrentMaxBaseDamage() {
        return localmaxbasedamage;
    }

    public final float getCurrentMaxBasePVPDamage() {
        return localmaxbasepvpdamage;
    }

    public final float getCurrentMaxBasePVPDamageL() {
        return localmaxbasepvpdamageL;
    }

    public final boolean isRangedJob(final int job) {
        return MapleJob.is蒼龍俠客(job) || MapleJob.is精靈遊俠(job) || MapleJob.is重砲指揮官(job) || job == 400 || (job / 10 == 52) || (job / 100 == 3) || (job / 100 == 13) || (job / 100 == 14) || (job / 100 == 33) || (job / 100 == 35) || (job / 10 == 41);
    }

    public int getCoolTimeR() {
        if (this.coolTimeR > 5) {
            return 5;
        }
        return this.coolTimeR;
    }

    public int getReduceCooltime() {
        if (this.reduceCooltime > 5) {
            return 5;
        }
        return this.reduceCooltime;
    }

    public int getLimitBreak(MapleCharacter chra) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        int limitBreak = 999999;
        Equip weapon = (Equip) chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
        if (weapon != null) {
            limitBreak = ii.getLimitBreak(weapon.getItemId()) + weapon.getLimitBreak();

            Equip subweapon = (Equip) chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
            if ((subweapon != null) && (ItemConstants.類型.武器(subweapon.getItemId()))) {
                int subWeaponLB = ii.getLimitBreak(subweapon.getItemId()) + subweapon.getLimitBreak();
                if (subWeaponLB > limitBreak) {
                    limitBreak = subWeaponLB;
                }
            }
        }
        return limitBreak;
    }

    public int getAttackCount(int skillId) {
        if (add_skill_attackCount.containsKey(skillId)) {
            return (add_skill_attackCount.get(skillId));
        }
        return 0;
    }

    public int getMobCount(int skillId) {
        if (add_skill_targetPlus.containsKey(skillId)) {
            return (add_skill_targetPlus.get(skillId));
        }
        return 0;
    }

    public int getReduceCooltimeRate(int skillId) {
        if (this.add_skill_coolTimeR.containsKey(skillId)) {
            return (this.add_skill_coolTimeR.get(skillId));
        }
        return 0;
    }

    public int getIgnoreMobpdpR(int skillId) {
        if (add_skill_ignoreMobpdpR.containsKey(skillId)) {
            return (add_skill_ignoreMobpdpR.get(skillId)) + ignoreTargetDEF;
        }
        return this.ignoreTargetDEF;
    }

    public double getDamageRate() {
        return dam_r;
    }

    public double getBossDamageRate() {
        return bossdam_r;
    }

    public double getBossDamageRate(int skillId) {
        if (add_skill_bossDamageRate.containsKey(skillId)) {
            return (add_skill_bossDamageRate.get(skillId)) + bossdam_r;
        }
        return bossdam_r;
    }

    public int getDuration(int skillId) {
        if (add_skill_duration.containsKey(skillId)) {
            return (add_skill_duration.get(skillId));
        }
        return 0;
    }

    public void addDamageIncrease(int skillId, int val) { //增加伤害 
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (damageIncrease.containsKey(skillId)) {
            int oldval = (damageIncrease.get(Integer.valueOf(skillId)));
            damageIncrease.put(skillId, oldval + val);
        } else {
            damageIncrease.put(skillId, val);
        }
    }

    public void addTargetPlus(int skillId, int val) { //增加攻击目标数
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (add_skill_targetPlus.containsKey(skillId)) {
            int oldval = (add_skill_targetPlus.get(Integer.valueOf(skillId)));
            add_skill_targetPlus.put(skillId, oldval + val);
        } else {
            add_skill_targetPlus.put(skillId, val);
        }
    }

    public void addAttackCount(int skillId, int val) { //增加攻击次数
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (add_skill_attackCount.containsKey(skillId)) {
            int oldval = (add_skill_attackCount.get(Integer.valueOf(skillId)));
            add_skill_attackCount.put(skillId, oldval + val);
        } else {
            add_skill_attackCount.put(skillId, val);
        }
    }

    public void addBossDamageRate(int skillId, int val) { //增加BOSS伤害
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (add_skill_bossDamageRate.containsKey(skillId)) {
            int oldval = (add_skill_bossDamageRate.get(Integer.valueOf(skillId)));
            add_skill_bossDamageRate.put(skillId, oldval + val);
        } else {
            add_skill_bossDamageRate.put(skillId, val);
        }
    }

    public void addIgnoreMobpdpRate(int skillId, int val) {//增加无视怪物防御
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (add_skill_ignoreMobpdpR.containsKey(skillId)) {
            int oldval = (add_skill_ignoreMobpdpR.get(Integer.valueOf(skillId)));
            add_skill_ignoreMobpdpR.put(skillId, oldval + val);
        } else {
            add_skill_ignoreMobpdpR.put(skillId, val);
        }
    }

    public void addBuffDuration(int skillId, int val) { //增加BUFF时间
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (add_skill_duration.containsKey(skillId)) {
            int oldval = (add_skill_duration.get(Integer.valueOf(skillId)));
            add_skill_duration.put(skillId, oldval + val);
        } else {
            add_skill_duration.put(skillId, val);
        }
    }

    public void addDotTime(int skillId, int val) { //增加持续掉血时间
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (add_skill_dotTime.containsKey(skillId)) {
            int oldval = (add_skill_dotTime.get(Integer.valueOf(skillId)));
            add_skill_dotTime.put(skillId, oldval + val);
        } else {
            add_skill_dotTime.put(skillId, val);
        }
    }

    public void addCoolTimeReduce(int skillId, int val) { //增加减少冷却时间
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (add_skill_coolTimeR.containsKey(skillId)) {
            int oldval = (add_skill_coolTimeR.get(Integer.valueOf(skillId)));
            add_skill_coolTimeR.put(skillId, oldval + val);
        } else {
            add_skill_coolTimeR.put(skillId, val);
        }
    }

    public void addSkillProp(int skillId, int val) { //增加技能概率
        if ((skillId < 0) || (val <= 0)) {
            return;
        }
        if (add_skill_prop.containsKey(skillId)) {
            int oldval = (add_skill_prop.get(Integer.valueOf(skillId)));
            add_skill_prop.put(skillId, oldval + val);
        } else {
            add_skill_prop.put(skillId, val);
        }
    }
}
