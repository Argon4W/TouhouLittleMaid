package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.block.BlockGarageKit;
import com.github.tartaricacid.touhoulittlemaid.item.ItemChair;
import com.github.tartaricacid.touhoulittlemaid.item.ItemEntityPlaceholder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import vazkii.patchouli.common.item.ItemModBook;

import static com.github.tartaricacid.touhoulittlemaid.init.InitItems.*;

public class InitCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TouhouLittleMaid.MOD_ID);

    public static RegistryObject<CreativeModeTab> MAIN_TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.touhou_little_maid.main"))
            .icon(() -> InitItems.HAKUREI_GOHEI.get().getDefaultInstance())
            .displayItems((par, output) -> {
                if (ModList.get().isLoaded("patchouli")) {
                    output.accept(ItemModBook.forBook(new ResourceLocation(TouhouLittleMaid.MOD_ID, "memorizable_gensokyo")));
                }
                output.accept(MAID_SPAWN_EGG.get());
                output.accept(FAIRY_SPAWN_EGG.get());
                output.accept(MAID_BACKPACK_SMALL.get());
                output.accept(MAID_BACKPACK_MIDDLE.get());
                output.accept(MAID_BACKPACK_BIG.get());
                output.accept(HAKUREI_GOHEI.get());
                output.accept(MAID_BED.get());
                output.accept(EXTINGUISHER.get());
                output.accept(ULTRAMARINE_ORB_ELIXIR.get());
                output.accept(EXPLOSION_PROTECT_BAUBLE.get());
                output.accept(FIRE_PROTECT_BAUBLE.get());
                output.accept(PROJECTILE_PROTECT_BAUBLE.get());
                output.accept(MAGIC_PROTECT_BAUBLE.get());
                output.accept(FALL_PROTECT_BAUBLE.get());
                output.accept(DROWN_PROTECT_BAUBLE.get());
                output.accept(NIMBLE_FABRIC.get());
                output.accept(ITEM_MAGNET_BAUBLE.get());
                output.accept(MUTE_BAUBLE.get());
                output.accept(SUBSTITUTE_JIZO.get());
                output.accept(POWER_POINT.get());
                output.accept(CAMERA.get());
                output.accept(PHOTO.get());
                output.accept(FILM.get());
                output.accept(CHISEL.get());
                output.accept(SMART_SLAB_EMPTY.get());
                output.accept(SMART_SLAB_INIT.get());
                output.accept(TRUMPET.get());
                output.accept(WIRELESS_IO.get());
                output.accept(MAID_BEACON.get());
                output.accept(MODEL_SWITCHER.get());
                output.accept(CHAIR_SHOW.get());
                ItemEntityPlaceholder.fillItemCategory(output);
            }).build());

    public static RegistryObject<CreativeModeTab> GARAGE_KIT_TAB = TABS.register("chair", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.touhou_little_maid.chair"))
            .icon(() -> InitItems.CHAIR.get().getDefaultInstance())
            .displayItems((par, output) -> {
                ItemChair.fillItemCategory(output);
            }).build());

    public static RegistryObject<CreativeModeTab> CHAIR_TAB = TABS.register("garage_kit", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.touhou_little_maid.garage_kit"))
            .icon(() -> InitItems.GARAGE_KIT.get().getDefaultInstance())
            .displayItems((par, output) -> {
                BlockGarageKit.fillItemCategory(output);
            }).build());
}