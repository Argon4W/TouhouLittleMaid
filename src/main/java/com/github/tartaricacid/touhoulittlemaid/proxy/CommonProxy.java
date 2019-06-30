package com.github.tartaricacid.touhoulittlemaid.proxy;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.EntityDanmaku;
import com.github.tartaricacid.touhoulittlemaid.network.MaidGuiHandler;
import com.github.tartaricacid.touhoulittlemaid.network.simpleimpl.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {
    public static SimpleNetworkWrapper INSTANCE = null;

    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(TouhouLittleMaid.MOD_ID, "entity.tlm.maid"),
                EntityMaid.class, "tlm.maid", 0, TouhouLittleMaid.INSTANCE, 32,
                3, true, 0x4a6195, 0xffffff);
        EntityRegistry.registerModEntity(new ResourceLocation(TouhouLittleMaid.MOD_ID, "entity.tlm.damaku"),
                EntityDanmaku.class, "tlm.danmaku", 1, TouhouLittleMaid.INSTANCE, 32,
                3, true);

        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(TouhouLittleMaid.MOD_ID);
        INSTANCE.registerMessage(ChangeGuiMessage.Handler.class, ChangeGuiMessage.class, 0, Side.SERVER);
        INSTANCE.registerMessage(ChangePickupDataMessage.Handler.class, ChangePickupDataMessage.class, 1, Side.SERVER);
        INSTANCE.registerMessage(ChangeMaidModeMessage.Handler.class, ChangeMaidModeMessage.class, 2, Side.SERVER);
        INSTANCE.registerMessage(ChangeHomeDataMessage.Handler.class, ChangeHomeDataMessage.class, 3, Side.SERVER);
        INSTANCE.registerMessage(ChangeGoheiMessage.Handler.class, ChangeGoheiMessage.class, 4, Side.SERVER);
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(TouhouLittleMaid.INSTANCE, new MaidGuiHandler());
    }

    public void postInit(FMLPostInitializationEvent event) {
    }
}
