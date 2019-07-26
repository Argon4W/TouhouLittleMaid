package com.github.tartaricacid.touhoulittlemaid.proxy;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidAPI;
import com.github.tartaricacid.touhoulittlemaid.api.util.ItemDefinition;
import com.github.tartaricacid.touhoulittlemaid.bauble.UltramarineOrbElixir;
import com.github.tartaricacid.touhoulittlemaid.bauble.UndyingTotem;
import com.github.tartaricacid.touhoulittlemaid.client.resources.pojo.CustomModelPackPOJO;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityMarisaBroom;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.EntityDanmaku;
import com.github.tartaricacid.touhoulittlemaid.init.MaidItems;
import com.github.tartaricacid.touhoulittlemaid.internal.task.*;
import com.github.tartaricacid.touhoulittlemaid.network.MaidGuiHandler;
import com.github.tartaricacid.touhoulittlemaid.network.simpleimpl.*;
import com.google.gson.Gson;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CommonProxy {
    public static SimpleNetworkWrapper INSTANCE = null;
    /**
     * 服务端用模型列表，
     * 这个只会在服务器启动时候读取默认原版的列表，
     * 主要用于刷怪蛋、刷怪笼、自然生成的随机模型，
     * <p>
     * 只有 ResourceLocation 类和基本数据类型，不会导致服务端崩溃
     */
    public static CustomModelPackPOJO MODEL_LIST;

    public void preInit(FMLPreInitializationEvent event) {
        // 初始化默认模型列表
        initModelList();

        // 注册 TOP
        if (Loader.isModLoaded("theoneprobe")) {
            FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe",
                    "com.github.tartaricacid.touhoulittlemaid.compat.theoneprobe.TheOneProbeInfo");
        }

        EntityRegistry.registerModEntity(new ResourceLocation(TouhouLittleMaid.MOD_ID, "entity.passive.maid"),
                EntityMaid.class, "touhou_little_maid.maid", 0, TouhouLittleMaid.INSTANCE, 64,
                3, true, 0x4a6195, 0xffffff);
        EntityRegistry.registerModEntity(new ResourceLocation(TouhouLittleMaid.MOD_ID, "entity.projectile.danmaku"),
                EntityDanmaku.class, "touhou_little_maid.danmaku", 1, TouhouLittleMaid.INSTANCE, 64,
                3, true);
        EntityRegistry.registerModEntity(new ResourceLocation(TouhouLittleMaid.MOD_ID, "entity.item.marisa_broom"),
                EntityMarisaBroom.class, "touhou_little_maid.marisa_broom", 2, TouhouLittleMaid.INSTANCE, 64,
                3, true);

        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(TouhouLittleMaid.MOD_ID);
        INSTANCE.registerMessage(ChangeGuiMessage.Handler.class, ChangeGuiMessage.class, 0, Side.SERVER);
        INSTANCE.registerMessage(ChangePickupDataMessage.Handler.class, ChangePickupDataMessage.class, 1, Side.SERVER);
        INSTANCE.registerMessage(ChangeMaidTaskMessage.Handler.class, ChangeMaidTaskMessage.class, 2, Side.SERVER);
        INSTANCE.registerMessage(ChangeHomeDataMessage.Handler.class, ChangeHomeDataMessage.class, 3, Side.SERVER);
        INSTANCE.registerMessage(ChangeGoheiMessage.Handler.class, ChangeGoheiMessage.class, 4, Side.SERVER);
        INSTANCE.registerMessage(ChangeMaidSkinMessage.Handler.class, ChangeMaidSkinMessage.class, 5, Side.SERVER);
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(TouhouLittleMaid.INSTANCE, new MaidGuiHandler());
    }

    public void postInit(FMLPostInitializationEvent event) {
        // 注册饰品
        LittleMaidAPI.registerBauble(ItemDefinition.of(MaidItems.ULTRAMARINE_ORB_ELIXIR), new UltramarineOrbElixir());
        LittleMaidAPI.registerBauble(ItemDefinition.of(Items.TOTEM_OF_UNDYING), new UndyingTotem());

        // 注册女仆模式
        LittleMaidAPI.registerTask(new TaskAttack());
        LittleMaidAPI.registerTask(new TaskAttackRanged());
        LittleMaidAPI.registerTask(new TaskAttackDanmaku());
        LittleMaidAPI.registerTask(new TaskFarm());
        LittleMaidAPI.registerTask(new TaskFeed());
        LittleMaidAPI.registerTask(new TaskIdle());
        LittleMaidAPI.registerTask(new TaskShears());
        LittleMaidAPI.registerTask(new TaskTorch());

        LittleMaidAPI.registerFarmHandler(new VanillaFarmHandler());
        LittleMaidAPI.registerFeedHandler(new VanillaFeedHandler());
    }

    /**
     * 初始化默认的模型列表
     */
    private void initModelList() {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("assets/touhou_little_maid/maid_model.json");
        if (input != null) {
            Gson gson = new Gson();
            // 将其转换为 pojo 对象
            MODEL_LIST = gson.fromJson(new InputStreamReader(input, StandardCharsets.UTF_8), CustomModelPackPOJO.class);
        }
        // 别忘了关闭输入流
        IOUtils.closeQuietly(input);
    }

    /**
     * 重新复写一遍原版本地化方法
     */
    public String translate(String key, Object... format) {
        return I18n.translateToLocalFormatted(key, format);
    }
}