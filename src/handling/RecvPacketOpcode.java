package handling;

import tools.EncodingDetect;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public enum RecvPacketOpcode implements WritableIntValueHolder {

    // 未知 [01 00 00 00 00 00 00 00 00]
    STRANGE_DATA(),
    // 客戶端驗證[完成]
    CLIENT_HELLO(0x14),
    
    // 0x68

    // 密碼驗證[完成]
    LOGIN_PASSWORD(0x15),
    // 角色選單[完成]
    CHARLIST_REQUEST(0x1A),
    
    // 0x1B
    // 0x1C
    
    // 建立角色驗證[完成](186+)
    CREATE_CHAR_AUTH_REQUEST(),
    // 玩家登入[完成]
    PLAYER_LOGGEDIN(0x1D),
    // 選擇角色[完成]
    CHAR_SELECT(0x1C),
    
    // 0x70
    // 0x71
    
    // 伺服器選單回覆
    SERVERLIST_REQUEST(),
    // 自動登入轉向
    LOGIN_REDIRECTOR(),
    // 檢查角色名稱[完成]
    CHECK_CHAR_NAME(0x1E),
    
    // 0x75
    // 0x76
    // 0x77
    // 0x78
    // 0x79
    // 0x7A
    // 0x7B
    // 0x7C

    // 建立角色[完成]
    CREATE_CHAR(0x20),
    // 50等角色卡角色建立
    CREATE_LV50_CHAR(0x21),
    // 建立終極冒險家
    CREATE_ULTIMATE(0x22),
    // 刪除角色
    DELETE_CHAR(0x23),

    ENTER_SECONDPW(0x29),
    // 0x82
    // 0x83
    // 0x84
    // 0x85
    
    // 客戶端錯誤[完成]
    CLIENT_FEEDBACK(),
    
    // 0x87
    // 0x88
    // 0x89
    // 0x8A
    // 0x8B
    // 0x8C
    // 0x8D
    
    // 打工系统
    PART_TIME_JOB(0x30),
    // 角色卡
    CHARACTER_CARD(0x31),
    // 未知
    ENABLE_LV50_CHAR(0x32),
    
    // 0x91
    // 0x92
    // 0x93
    
    // Pong[完成]
    PONG(),
    
    // 0x95
    
    // 客戶端錯誤【[ Name: %s, Job: %d, Field: %d, World: %d, Channel: %d ]\r\n】[完成]
    CLIENT_ERROR(),
    
    // 0x97
    // 0x98
    // 0x99
    // 0x9A
    // 0x9B
    // 0x9C
    
    // 選擇性別
    SET_GENDER(),
    // 伺服器狀態
    SERVERSTATUS_REQUEST(),
    
    // 0x9F
    
    // 背景驗證[完成]
    GET_SERVER(0x2E),
    
    // 0xA1
    // 0xA2
    // 0xA3
    // 0xA4

    // 客戶端開始(顯示視窗)[完成]
    CLIENT_START(),
    
    // 0xA6 客戶端報錯
    // 0xA7 開啟遊戲會出現的包
    
    // 申請變更角色名稱[完成]
    APPLY_CHANGE_CHAR_NAME(),
    
    // 創建角色二次密碼認證[完成]
    CREATE_CHAR_AUTH(),
    
    // 0xAA
    
    // 燃燒計畫
    COMBUSTION_PROJECT(),
    // 變更角色順序[完成]
    CHANGE_CHAR_POSITION(),
    // 創角進入遊戲[完成]
    CREATE_CHAR_SELECT(),
    
    // 變更地圖[完成]
    CHANGE_MAP(0x35),
    // 變更頻道[完成]
    CHANGE_CHANNEL(0x36),
    // 購物商城[完成]
    ENTER_CASH_SHOP(0x37),
    
    // 0x38
    
    // PvP開始
    ENTER_PVP(0x39),
    // 阿斯旺開始
    ENTER_AZWAN(0x3A),
    // 阿斯旺活動
    ENTER_AZWAN_EVENT(0x3B),
    // 離開阿斯旺
    LEAVE_AZWAN(0x3C),
    // PvP隊伍
    ENTER_PVP_PARTY(0x3D),
    // 離開PvP
    LEAVE_PVP(0x3E),

    // 玩家移動[完成]
    MOVE_PLAYER(0x3F),
    
    // 0x40

    // 取消椅子[完成]
    CANCEL_CHAIR(0x41),
    // 使用椅子[完成]
    USE_CHAIR(0x42),

    // 0x43
    // 0x44
    // 0x45
    
    // 近距離攻擊[完成]
    CLOSE_RANGE_ATTACK(0x46),
    // 遠距離攻擊[完成]
    RANGED_ATTACK(0x47),
    // 魔法攻擊[完成]
    MAGIC_ATTACK(0x48),
    // 被動攻擊(抗壓...)
    PASSIVE_ATTACK(0x49),

    // 0x4A
    
    // 角色受傷[完成]
    TAKE_DAMAGE(0x4B),
    // PvP攻擊
    PVP_ATTACK(0x4C),
    // 普通聊天[完成]
    GENERAL_CHAT(0x4D),
    // 關閉黑板
    CLOSE_CHALKBOARD(0x4E),
    // 臉部情緒
    FACE_EXPRESSION(0x4F),
    // 機器人臉部情緒
    FACE_ANDROID(0x50),
    // 使用物品效果
    USE_ITEMEFFECT(0x51),
    // 使用原地復活
    WHEEL_OF_FORTUNE(0x52),
    // 使用稱號效果
    USE_TITLE(0x53),
    // 變更天使破壞者外觀
    ANGELIC_CHANGE(0x54),

    // 0x55
    // 0x56
    // 0x57
    // 0x58
    // 0x59
    // 0x5A
    // 0x5B
    // 0x5C
    // 0x5D
    // 0x5E
    
    // Npc交談[完成]
    NPC_TALK(0x5F),
    
    // 0x60
    // 0x61
    // 0x62
    // 0x63
    
    // Npc詳細交談[完成]
    NPC_TALK_MORE(0x64),
    // Npc商店[完成]
    NPC_SHOP(0x65),
    // 倉庫
    STORAGE(0x66),
    // 精靈商人
    USE_HIRED_MERCHANT(0x67),
    // 精靈商人物品
    MERCH_ITEM_STORE(0x68),
    // 宅配操作
    PACKAGE_OPERATION(0x69),
    // 取消開放通道
    MECH_CANCEL(0x6A),
    
    // 0x68
    // 0x69
    
    // 寒冰迅移[完成]
    SPAWN_SPECIAL(),
    
    // 0x6A
    // 0x6B
    
    
    // 智慧貓頭鷹(5230000)
    OWL(0x6C),
    // 智慧貓頭鷹購買
    OWL_WARP(0x6D),
    
    // 0x6E
    
    // 管理員商店
    ADMIN_SHOP(0x6F),
    // 向上整理[完成]
    ITEM_SORT(0x70),
    // 種類排序[完成]
    ITEM_GATHER(0x71),
    // 物品移動[完成]
    ITEM_MOVE(0x72),
    
    // 0x73【輸入觀戰板內容】

    // 移動道具至背包欄位
    MOVE_BAG(0x74),
    // 背包道具至道具欄位
    SWITCH_BAG(0x75),
    
    // 0x76
    
    // 使用物品[完成]
    USE_ITEM(0x76),
    // 取消物品效果
    CANCEL_ITEM_EFFECT(0x77),
    
    // 0x78
    
    // 使用召喚包(2100017)
    USE_SUMMON_BAG(0x79),
    // 使用寵物食品
    PET_FOOD(0x7A),
    // 提神飲料
    USE_MOUNT_FOOD(0x7),
    // 使用腳本物品
    USE_SCRIPTED_NPC_ITEM(0x7B),
    // 使用製作書
    USE_RECIPE(0x7C),
    
    // 0x7D
    
    // 使用商城道具
    USE_CASH_ITEM(0x7E),
    // 使用附加潛能印章
    USE_ADDITIONAL_ITEM(0x7F),
    // 是否允許寵物拾取道具
    ALLOW_PET_LOOT(0x80),
    // 是否允許寵物自動餵食
    ALLOW_PET_AOTO_EAT(0x81),
    // 使用捕捉道具
    USE_CATCH_ITEM(0x82),
    
    // 0x83
    // 0x84

    // 使用技能書
    USE_SKILL_BOOK(0x85),
    
    // 0x86
    // 0x87
    // 0x88
    
    // 經驗瓶(2230000)
    USE_EXP_POTION(0x89),
    
    // 0x8A
    // 0x8B
    // 0x8C
    // 0x8D
    // 0x8E
    
    // 智慧貓頭鷹開始搜索
    USE_OWL_MINERVA(0x91),
    // 使用瞬移之石
    USE_TELE_ROCK(0x92),
    // 使用回家卷軸[完成]
    USE_RETURN_SCROLL(0x93),
    // 移動至梅斯特鎮
    MOVE_ARDENTMILL(),
    // 使用卷軸
    USE_UPGRADE_SCROLL(0x94),
    // 使用卷軸保護卡(5064300)
    USE_FLAG_SCROLL(0x95),
    // 使用裝備強化卷軸
    USE_EQUIP_SCROLL(0x96),
    // 使用潛能賦予卷軸
    USE_POTENTIAL_SCROLL(0x98),
    // 使用附加潛在能力賦予卷軸
    USE_BONUS_POTENTIAL_SCROLL(0x99),
    // 使用烙的印章(2049500)
    USE_CARVED_SEAL(0x9A),
    // 使用奇怪的方塊(2710000)
    USE_CRAFTED_CUBE(0x9B),

    // 0x9C
    
    // 靈魂卷軸
    USE_SOUL_ENCHANTER(0x9D),
    // 靈魂寶珠
    USE_SOUL_SCROLL(0x9E),
    // 咒文的痕跡[完成]
    EQUIPMENT_ENCHANT(),
    // 使用背包[完成]
    USE_BAG(0xA0),
    // 使用放大鏡[完成]
    USE_MAGNIFY_GLASS(0xA1),
    
    // 0xA2
    // 0xA3
    // 0xA4
    
    // 使用能力點數[完成]
    DISTRIBUTE_AP(0xA5),
    // 自動分配能力點數[完成]
    AUTO_ASSIGN_AP(0xA6),
    // 自動恢復HP/MP[完成]
    HEAL_OVER_TIME(0xA7),
    
    // 0xA8
    // 0xA9 [Int][Long][Short][Short]

    // 使用技能點數[完成]
    DISTRIBUTE_SP(0xAA),
    // 角色使用技能[完成]
    SPECIAL_SKILL(0xAB),
    // 取消輔助效果[完成]
    CANCEL_BUFF(0xAC),
    // 技能效果[完成]
    SKILL_EFFECT(0xAD),
    // 楓幣掉落[完成]
    MESO_DROP(0xAE),
    // 添加名聲
    GIVE_FAME(0xAF),
    
    // 0xB0
    
    // 角色信息[完成]
    CHAR_INFO_REQUEST(0xB1),
    // 召喚寵物[完成]
    SPAWN_PET(0xB2),
    
    // 0xB3
    
    // 取消異常效果
    CANCEL_DEBUFF(0xB4),
    // 腳本地圖
    CHANGE_MAP_SPECIAL(0xB6),
    // 使用時空門
    USE_INNER_PORTAL(0xB7),
    
    // 0xB8
    
    // 使用瞬移之石
    TROCK_ADD_MAP(0xB9),
    // 使用測謊機
    LIE_DETECTOR(0xBA),
    // 測謊機技能
    LIE_DETECTOR_SKILL(0xBB),
    // 確認測謊機驗證碼 
    LIE_DETECTOR_RESPONSE(0xBC),
    // 重新整理測謊機驗證碼
    LIE_DETECTOR_REFRESH(0xBD),
    // 舉報玩家
    REPORT(),
    // 任務操作
    QUEST_ACTION(0xBE),
    // 重新領取勳章
    REISSUE_MEDAL(0xBF),
    // 輔助效果回應
    BUFF_RESPONSE(0xC0),
    
    // 0xC1 凱薩龍劍風
    // 0xC2
    
    // 技能組合[完成]
    SKILL_MACRO(0xC3),
    
    // 0xC4
    
    // 獎勵道具
    REWARD_ITEM(0xC5),
    // 鍛造技能
    ITEM_MAKER(0xC6),
    // 全部修理(勇士之村(辛德))
    REPAIR_ALL(0xC7),
    // 裝備修理
    REPAIR(0xC8),

    // 0xC9
    
    // 請求跟隨()
    FOLLOW_REQUEST(),
    
    // 0x164
    // 0x165
    // 0x166
    // 0x167
    
    // 組隊任務獎勵
    PQ_REWARD(0xD2),
    // 請求跟隨回覆
    FOLLOW_REPLY(0xD3),
    // 自動跟隨回覆()
    AUTO_FOLLOW_REPLY(0xD4),
    // 能力值信息[完成]
    PROFESSION_INFO(0xD5),
    // 使用培養皿[完成]
    USE_POT(0xD6),
    // 清理培養皿[完成]
    CLEAR_POT(0xD7),
    // 餵食培養皿[完成]
    FEED_POT(0xD8),
    // 治癒培養皿[完成]
    CURE_POT(0xD9),
    // 培養皿獎勵[完成]
    REWARD_POT(0xDA),
    // 阿斯旺復活
    AZWAN_REVIVE(0xDB),
    // 使用髮型卷[2540000]
    USE_COSMETIC(),
    // DF連擊[意志之劍取消]
    DF_COMBO(),
    // 神之子狀態轉換
    ZERO_STAT_CHANGE(),
    // 神之子
    ZERO_CLOTHES(),
    // 使用能力傳播者
    INNER_CIRCULATOR(),
    // PvP重生
    PVP_RESPAWN(),
    // 管理員聊天
    ADMIN_CHAT(0xF5),
    // 隊伍聊天
    PARTYCHAT(0xF6),
    // 悄悄話[完成]
    COMMAND(0xF7),
    // 聊天招待[完成]
    MESSENGER(0xF8),
    // 玩家互動
    PLAYER_INTERACTION(0xF9),

    // 隊伍操作[完成]
    PARTY_OPERATION(0xFE),
    // 接受/拒絕組隊邀請
    DENY_PARTY_REQUEST(0xFF),
    // 允許組隊邀請
    ALLOW_PARTY_INVITE(0x100),
    // 建立遠征隊
    EXPEDITION_OPERATION(0x101),
    // 遠征隊搜尋
    EXPEDITION_LISTING(0x102),
    // 公會操作[完成]
    GUILD_OPERATION(0x103),
    // 拒絕公會邀請
    DENY_GUILD_REQUEST(),
    // 申請加入公會
    JOIN_GUILD_REQUEST(),
    // 取消加入公會
    JOIN_GUILD_CANCEL(),
    // 允許加入公會邀請
    ALLOW_GUILD_JOIN(0x104),
    // 拒絕加入公會邀請
    DENY_GUILD_JOIN(0x105),
    // 管理員指令
    ADMIN_COMMAND(),
    // 管理員指令
    ADMIN_COMMAND2(),
    // 管理員日誌
    ADMIN_LOG(),
    // 好友操作[完成]
    BUDDYLIST_MODIFY(0x107),
    // 訊息操作
    NOTE_ACTION(0x108),
    
    // 0x109
    
    // 使用時空門
    USE_DOOR(0x10A),
    // 使用開放通道
    USE_MECH_DOOR(0x10B),
    
    // 0x10C
    
    // 變更鍵盤設置[完成]
    CHANGE_KEYMAP(0x10D),
    // 猜拳遊戲
    RPS_GAME(0x10E),
    // 戒指操作
    RING_ACTION(0x10F),
    // 結婚操作
    WEDDING_ACTION(0x110),
    // 公會聯盟操作
    ALLIANCE_OPERATION(0x111),
    // 拒絕公會聯盟邀請
    DENY_ALLIANCE_REQUEST(0x112),
    // 與 狂狼/皇家騎士團 嚮導時召喚的NPC對話
    CYGNUS_SUMMON(0x113),
    
    // 0x114
    
    // 狂郎勇士連擊
    ARAN_COMBO(0x115),

    // 怪物CRC Key改變回傳
    MONSTER_CRC_KEY(0x122),
    // 製作道具完成[完成]
    CRAFT_DONE(0x123),
    // 製作道具效果[完成]
    CRAFT_EFFECT(0x124),
    // 製作道具開始[完成]
    CRAFT_MAKE(0x125),
    
    // 0x126
    // 0x127
    
    // 變更房間[完成]
    CHANGE_ROOM_CHANNEL(0x128),
    
    // 0x129
    
    // 選擇技能
    CHOOSE_SKILL(0x12A),
    // 技能竊取
    SKILL_SWIPE(0x12B),
    // 檢視技能
    VIEW_SKILLS(0x12C),
    // 撤銷偷竊技能
    CANCEL_OUT_SWIPE(0x12D),
    
    // 0x1E3
    // 0x1E4
    // 0x1E5
    // 0x1E6
    // 0x1E7
    // 0x1E8
    
    // 釋放意志之劍[完成]
    RELEASE_TEMPEST_BLADES(0x135),
    
    // 0x1EA
    // 0x1EB
    // 0x1EC
    // 0x1ED
    // 0x1EE
    // 0x1EF
    
    // 更新超級技能[完成]
    UPDATE_HYPER(),
    // 重置超級技能[完成]
    RESET_HYPER(),
    // 更新超級能力點[完成]
    UPDATE_HYPER_AP(),
    // 重置超級能力點[完成]
    RESET_HYPER_AP(),
    // 被怪物抓到
    MONSTER_BAN(),
    // 返回選角界面[完成]
    BACK_TO_CHARLIST(),
    // 創建角色跟刪除角色輸入的驗證碼
    SECURITY_CODE(),
    // 更新烈焰溜溜球個數
    PINKBEAN_YOYO_REQUEST(),
    // 快速移動(非打開NPC)
    QUICK_MOVE_SPECIAL(),
    // 幸運怪物
    LUCKY_LUCKY_MONSTORY(),
    // 活動卡片
    EVENT_CARD(),
    // 神之子鏡子世界地圖傳送
    ZERO_QUICK_MOVE(),
    // 開啟活動列表[完成]
    OPEN_EVENT_LIST(),
    // 凱撒快速鍵[完成]
    KAISER_QUICK_KEY(0x148),
    // 黑名單[完成]
    BLACK_LIST(),
    // 賓果
    BINGO(),
    // 0x15E 變形 惡殺的技能
    // 0x15F
    // 0x160
    // 0x161
    王圖復活(),
    // 0x162
    // 寵物移動[完成]
    MOVE_PET(0x163),
    // 寵物說話[完成]
    PET_CHAT(0x164),
    // 寵物指令[完成]
    PET_COMMAND(0x165),
    // 寵物拾取[完成]
    PET_LOOT(0x166),
    // 寵物自動吃藥[完成]
    PET_AUTO_POT(0x167),
    // 寵物_除外道具[完成]
    PET_IGNORE(0x168),
    // 寵物自動吃食品[完成]
    PET_AUTO_FOOD(0x169),

    // 花狐移動
    MOVE_HAKU(0x170),
    // 花狐動作(包括變身)
    HAKU_ACTION(0x171),
    // 影朋花狐使用輔助技能
    HAKU_USE_BUFF(0x172),

    // 召喚獸移動[完成]
    MOVE_SUMMON(0x173),
    // 召喚獸攻擊[完成]
    SUMMON_ATTACK(0x174),
    // 召喚獸傷害[完成]
    DAMAGE_SUMMON(0x175),
    // 召喚獸技能[完成]
    SUB_SUMMON(0x176),
    // 召喚獸移除[完成]
    REMOVE_SUMMON(0x177),
    // 龍神移動
    MOVE_DRAGON(0x178),
    // 使用物品任務
    USE_ITEM_QUEST(0x179),
    // 機器人移動
    MOVE_ANDROID(0x17A),
    // 機器人情感回覆(176.Done)
    ANDROID_EMOTION_RESULT(0x17B),
    // 更新任務
    UPDATE_QUEST(),
    // 任務物品
    QUEST_ITEM(),
    
    // 0x275
    // 0x276
    // 0x277
    
    // 快速欄按鍵[完成]
    QUICK_SLOT(0x18D),
    
    // 按下按鈕
    BUTTON_PRESSED(0x18E),
    
    // 0x27A
    // 0x27B
    
    // 操控角色完成回覆
    DIRECTION_COMPLETE(),
    
    // 0x27D
    // 0x27E
    
    // 程序清單
    SYSTEM_PROCESS_LIST(),
    
    // 0x280
    // 0x281
    
    // 神之子-開始強化
    ZERO_SCROLL_START(),
    // 神之子-武器潛在能力
    ZERO_WEAPON_ABILITY(),
    // 神之子-武器介面
    ZERO_WEAPON_UI(),
    // 神之子-與精靈對話
    ZERO_NPC_TALK(),
    // 神之子-使用卷軸
    ZERO_WEAPON_SCROLL(),
    // 神之子-武器成長
    ZERO_WEAPON_UPGRADE(),
    // 神之子-武器成長
    ZERO_WEAPON_UPGRADE_START(),
    // 讀取角色成功
    LOAD_PLAYER_SCCUCESS(),
    
    // 0x28A
    // 0x28B
    // 0x28C
    // 0x28D
    // 0x28E
    
    // 箭座控制[完成]
    ARROW_BLASTER_ACTION(),
    
    // 0x261
    // 0x262
    // 0x263
    // 0x264
    // 0x265
    // 0x266
    // 0x267
    // 0x268
    // 0x269
    // 0x26A
    // 0x26B
    // 0x26C
    // 0x26D
    // 0x26E
    // 0x26F
    // 0x270
    // 0x271
    
    // 遊戲嚮導[完成]
    GUIDE_TRANSFER(),
    
    // 0x273
    // 0x274
    // 0x275
    // 0x276
    // 0x277
    // 0x278
    // 0x279
    // 0x27A
    // 0x27B
    // 0x27C
    // 0x27D
    
    // 新星世界[完成]
    SHINING_STAR_WORLD(),
    // Boss清單[完成]
    BOSS_LIST(),
    
    // +22
    
    // 公會佈告欄操作
    BBS_OPERATION(),
    // 離開遊戲 
    EXIT_GAME(),
    // 潘姆音樂
    PAM_SONG(),
    // 新年賀卡(2160101)[完成]
    NEW_YEAR_CARD(),
    // 聖誕團隊藥水[2212000][完成]
    TRANSFORM_PLAYER(),
    // 進擊的巨人視窗選項反饋
    ATTACK_ON_TITAN_SELECT(),
    // 拍賣系統[完成]
    ENTER_MTS(),
    // 使用兵法書(2370000)[完成]
    SOLOMON(),
    // 獲得兵法書經驗值[完成]
    GACH_EXP(),
    // 使用強化任意門[完成]
    CHRONOSPHERE(),
    // 使用閃耀方塊(5062017)[完成]
    USE_FLASH_CUBE(),
    // 保存攻擊SKIN
    SAVE_DAMAGE_SKIN(),
    // 更改攻擊SKIN
    CHANGE_DAMAGE_SKIN(),
    // 刪除攻擊SKIN
    DELETE_DAMAGE_SKIN(),

    // 怪物移動[完成]
    MOVE_LIFE(0x1A4),
    // 怪物復仇[完成]
    AUTO_AGGRO(0x1A5),
    
    // 0x1A6
    // 0x1A7
    // 0x1A8
    // 0x1A9
    
    // 怪物自爆[完成]
    MONSTER_BOMB(0x1AA),
    
    // 0x370
    // 0x371
    // 0x372
    // 0x373
    // 0x374
    // 0x375
    // 0x376
    // 0x377
    // 0x378
    // 0x379
    // 0x37A
    // 0x37B
    // 0x37C
    // 0x37D
    // 0x37E
    // 0x37F
    // 0x380
    // 0x381
    // 0x382
    // 0x383
    
    // Npc動作(包括說話)[完成]
    NPC_ACTION(0x1B5),
    
    // 0x1B6
    // 0x1B7
    // 0x1B8
    // 0x1B9
    
    // 拾取物品[完成]
    ITEM_PICKUP(0x1BA),
    
    // 0x1BB
    // 0x1BC
    
    // 攻擊箱子[完成]
    DAMAGE_REACTOR(0x1BD),
    // 雙擊箱子[完成]
    TOUCH_REACTOR(0x1BE),
    
    // 0x1BF
    // 0x1C0
    // 0x1C1
    
    // 召喚分解機
    MAKE_EXTRACTOR(),
    
    // 0x394
    // 0x395
    // 0x396
    
    // 玩家資料更新
    UPDATE_ENV(),
    
    // 0x3B1
    // 0x3B2
    
    // 滾雪球
    SNOWBALL(0x1E1),
    // 向左擊飛
    LEFT_KNOCK_BACK(0x1E2),
    // 玩家更新
    PLAYER_UPDATE(0x1E3),
    // 推薦隊員[完成]
    MEMBER_SEARCH(0x1E4),
    // 尋找隊伍[完成]
    PARTY_SEARCH(0x1E5),
    // 開始採集[完成]
    START_HARVEST(0x1E6),
    // 停止採集[完成]
    STOP_HARVEST(0x1E7),
    
    // 0x1E8
    // 0x1E9
    
    // 快速移動(開啟Npc)[完成]
    QUICK_MOVE(),
    // 採集符文輪
    TOUCH_RUNE(),
    // 取得符文
    USE_RUNE(),
    // 儲值樂豆點[完成]
    CS_BUY_CASH(),
    // 購物商城更新[完成]
    CS_UPDATE(0x1EE),
    // 購買點數道具[完成]
    BUY_CS_ITEM(0x1EF),
    // 使用兌換券[完成]
    COUPON_CODE(0x1F0),
    // 購物商城送禮[完成]
    CS_GIFT(),
    // 儲存造型設計[完成]
    CASH_CATEGORY(),    
    // 里程[完成]
    CS_MILEAGE(),
    
    // 使用黃金鐵鎚
    GOLDEN_HAMMER(0x211),
    // 黃金鐵鎚使用完成
    VICIOUS_HAMMER(0x222),
    
    // 0x223
    
    // 使用白金鎚子
    PLATINUM_HAMMER(0x224),
    
    // 陶德的槌子
    TOAD_HAMMER(0x7FFF),
    
    // 戰鬥分析開始[完成]
    BATTLE_STATISTICS(),
    
    // 獲得獎勵
    REWARD(),
    // 裝備特效開關
    EFFECT_SWITCH(),
    // 未知OPS，不繼續增加
    UNKNOWN,
    // 使用世界樹的祝福(2048500)
    USE_ABYSS_SCROLL,
    MONSTER_BOOK_DROPS,
    // General
    RSA_KEY,
    MAPLETV,
    CRASH_INFO,
    // Login
    GUEST_LOGIN,
    TOS,
    VIEW_SERVERLIST,
    REDISPLAY_SERVERLIST,
    CHAR_SELECT_NO_PIC,
    AUTH_REQUEST,
    VIEW_REGISTER_PIC,
    VIEW_SELECT_PIC,
    CLIENT_FAILED,
    ENABLE_SPECIAL_CREATION,
    CREATE_SPECIAL_CHAR,
    AUTH_SECOND_PASSWORD,
    WRONG_PASSWORD,
    ENTER_FARM,
    CHANGE_CODEX_SET,
    CODEX_UNK,
    USE_NEBULITE,
    USE_ALIEN_SOCKET,
    USE_ALIEN_SOCKET_RESPONSE,
    USE_NEBULITE_FUSION,
    TOT_GUIDE,
    GET_BOOK_INFO,
    USE_FAMILIAR,
    SPAWN_FAMILIAR,
    RENAME_FAMILIAR,
    PET_BUFF,
    USE_TREASURE_CHEST,
    SOLOMON_EXP,    
    XMAS_SURPRISE,
    TWIN_DRAGON_EGG,
    YOUR_INFORMATION,
    FIND_FRIEND,
    PINKBEAN_CHOCO_OPEN,
    PINKBEAN_CHOCO_SUMMON,
    BUY_SILENT_CRUSADE,
    CASSANDRAS_COLLECTION,
    BUDDY_ADD,
    //HAKU_1D8,
    //HAKU_1D9,
    PVP_SUMMON,
    MOVE_FAMILIAR,
    TOUCH_FAMILIAR,
    ATTACK_FAMILIAR,
    REVEAL_FAMILIAR,
    FRIENDLY_DAMAGE,
    HYPNOTIZE_DMG,
    MOB_BOMB,
    MOB_NODE,
    DISPLAY_NODE,
    MONSTER_CARNIVAL,
    CLICK_REACTOR,
    CANDY_RANKING,
    COCONUT,
    SHIP_OBJECT,
    PLACE_FARM_OBJECT,
    FARM_SHOP_BUY,
    FARM_COMPLETE_QUEST,
    FARM_NAME,
    HARVEST_FARM_BUILDING,
    USE_FARM_ITEM,
    RENAME_MONSTER,
    NURTURE_MONSTER,
    EXIT_FARM,
    FARM_QUEST_CHECK,
    FARM_FIRST_ENTRY,
    PYRAMID_BUY_ITEM,
    CLASS_COMPETITION,
    MAGIC_WHEEL,
    BLACK_FRIDAY,
    RECEIVE_GIFT_EFFECT,
    UPDATE_RED_LEAF,
    //Not Placed:
    SPECIAL_STAT,
    DRESSUP_TIME,
    OS_INFORMATION,
    LUCKY_LOGOUT,
    MESSENGER_RANKING;

    private short code = -2;

    public void setValue(int code) {
        this.code = (short) code;
    }
    
    @Override
    public void setValue(short newval) {
        this.code = newval;
    }

    @Override
    public final short getValue() {
        return code;
    }

    private RecvPacketOpcode() {
        this.code = 0x7FFE;
    }

    private RecvPacketOpcode(final int code) {
        this.code = (short) code;
    }

    public static String nameOf(short value) {
        for (RecvPacketOpcode header : RecvPacketOpcode.values()) {
            if (header.getValue() == value) {
                return header.name();
            }
        }
        return "UNKNOWN";
    }

    public static boolean isSpamHeader(RecvPacketOpcode header) {
        switch (header.name()) {
            case "PONG":
            case "NPC_ACTION":
//            case "ENTER"":
//            case "CRASH_INFO":
//            case "AUTH_REQUEST":
//            case "SPECIAL_MOVE":
            case "MOVE_LIFE":
            case "MOVE_PLAYER":
            case "MOVE_ANDROID":
//            case "MOVE_DRAGON":
            case "MOVE_SUMMON":
//            case "MOVE_FAMILIAR":
            case "MOVE_PET":
            case "AUTO_AGGRO":
//            case "CLOSE_RANGE_ATTACK":
            case "QUEST_ACTION":
            case "HEAL_OVER_TIME":
//            case "CHANGE_KEYMAP":
//            case "USE_INNER_PORTAL":
            case "MOVE_HAKU":
//            case "FRIENDLY_DAMAGE":
//            case "CLOSE_RANGE_ATTACK":
//            case "RANGED_ATTACK":
//            case "ARAN_COMBO":
//            case "SPECIAL_STAT":
//            case "UPDATE_HYPER":
//            case "RESET_HYPER":
//            case "ANGELIC_CHANGE":
//            case "DRESSUP_TIME":
            case "PROFESSION_INFO":
            case "BUTTON_PRESSED":
            case "STRANGE_DATA":
            case "SYSTEM_PROCESS_LIST":
            case "PINKBEAN_YOYO_REQUEST":
            case "CHANGE_KEYMAP":
            case "TAKE_DAMAGE":
            case "GENERAL_CHAT":
            case "BUFF_RESPONSE":
            case "SPECIAL_SKILL":
                return true;
            default:
                return false;
        }
    }

    public static void loadValues() {
        Properties props = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("recvops.properties")) {
            props.load(new BufferedReader(new InputStreamReader(fileInputStream, EncodingDetect.getJavaEncode("recvops.properties"))));
        } catch (IOException ex) {
            InputStream in = RecvPacketOpcode.class.getClassLoader().getResourceAsStream("recvops.properties");
            if (in == null) {
                System.out.println("未讀取 recvops.properties 檔案, 使用內建 RecvPacketOpcode 列舉");
                return;
            }
            try {
                props.load(in);
            } catch (IOException e) {
                throw new RuntimeException("讀取 recvops.properties 檔案異常", e);
            }
        }
        ExternalCodeTableGetter.populateValues(props, values());
    }

    static {
        loadValues();
    }
}
