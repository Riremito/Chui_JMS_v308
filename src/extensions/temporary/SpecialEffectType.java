package extensions.temporary;

/**
 *
 * @author 黯淡
 */
public enum SpecialEffectType {

    // 等級提升[182-完成]
    LEVEL_UP(0x0),
    // 近端技能特效[182-完成]
    LOCAL_SKILL(0x1),
    // 遠端技能特效[182-完成]
    REMOTE_SKILL(0x2),
    // 神之子特效[182-完成]
    ZERO(0x99),
    // 機甲戰神-輔助機器特效[182-完成]
    MECHANIC(0x99),
    SHOW_DICE(0x3),
    UNK_6(0x4),
    // 物品獲得/丟棄文字特效[182-完成]
    ITEM_OPERATION(0x5),
    // 寵物等級提升[182-完成]
    PET_LEVEL_UP(0x6),
    // 技能飛行體特效[182-完成]
    SKILL_FLYING_OBJECT(0x7),
    // 抵抗異常狀態[182-完成]
    RESIST(0x8),
    // 使用護身符[182-完成]
    USE_AMULET(0x9),
    UNK_C(0xA),
    MULUNG_DOJO_UP(0xB),
    // 職業變更[182-完成]
    CHANGE_JOB(0xC),
    // 任務完成[182-完成]
    QUET_COMPLETE(0xD),
    // 回復特效(Byte)[182-完成]
    HEALED_BYTE(0xE),
    UNK_11(0xF),
    UNK_12(0x10),
    MONSTER_BOOK(0x11),
    REWARD_ITEM(0x12),
    ITEM_LEVEL_UP(0x13),
    ITEM_MAKER_SUCCESS(0x14),
    DODGE_CHANCE(0x15),
    UNK_18(0x99),
    // 顯示WZ的效果[182-完成]
    WZ(0x16),
    // 聊天窗顯示"消耗1個原地復活術 ，於角色所在原地進行復活！（尚餘Byte個）"[182-完成]
    RESURRECTION_INFO(0x17),
    UNK_1B(0x18),
    // 顯示WZ的效果2[182-完成]
    WZ2(0x19),
    UNK_1D(0x1A),
    UNK_1E(0x1B),
    SOUND(0x1C),
    UNK_20(0x1D),
    UNK_21(0x1E),
    // 商城道具效果[182-完成]
    CASH_ITEM(0x1F),
    UNK_23(0x20),
    // 回復特效(Int)[182-完成]
    HEALED_INT(0x21),
    CHAMPION(0x22),
    UNK_26(0x28),
    UNK_27(0x29),
    UNK_28(0x2A),
    UNK_29(0x2B),
    UNK_2A(0x2C),
    UNK_2B(0x2D),
    UNK_2C(0x2E),
    // 採集/挖礦[182-完成]
    CRAFTING(0x29),
    UNK_2E(0x30),
    UNK_2F(0x31),
    UNK_30(0x32),
    UNK_31(0x33),
    UNK_32(0x34),
    // 背景變黑
    BLACK_BACKGROUND(0x99),
    UNSEAL_BOX(0x99),
    UNK_35(0x2C),
    // 影武者出生劇情背景黑暗特效[182-完成]
    DARK(0x2D),
    UNK_37(0x2E),
    // 天使技能充能效果[182-完成]
    ANGELIC_RECHARGE(0x2F),
    UNK_39(0x30),
    UNK_3A(0x31),
    UNK_3B(0x32), // FAMILAR 逃走了
    UNK_3C(0x3E),
    UNK_3D(0x3F),
    WZ3(0x40),
    UNK_3F(0x41),
    UNK_40(0x42),
    UNK_41(0x43),
    UNK_42(0x44),
    //NPC說話特效
    NPC_BUBBLE(0x45),
    UNK_44(0x46),
    // 暗夜行者技能特效[182-完成]
    FIRE(0x47),
    UNK_46(0x48),
    UNK_47(0x48),
    UNK_48(0x49),
    UNK_49(0x4A),
    // 獲得道具頂部提示
    ITEM_TOP_MSG(0x4B),
    UNK_4B(0x4C),
    ;

    private final int value;

    private SpecialEffectType(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
