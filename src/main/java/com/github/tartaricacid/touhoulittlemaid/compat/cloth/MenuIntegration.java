package com.github.tartaricacid.touhoulittlemaid.compat.cloth;


import com.github.tartaricacid.touhoulittlemaid.config.subconfig.ChairConfig;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig;
import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;


public class MenuIntegration {
    public static ConfigBuilder getConfigBuilder() {
        ConfigBuilder root = ConfigBuilder.create().setTitle(Component.literal("Touhou Little Maid"));
        root.setGlobalized(true);
        root.setGlobalizedExpanded(false);
        ConfigEntryBuilder entryBuilder = root.entryBuilder();
        maidConfig(root, entryBuilder);
        chairConfig(root, entryBuilder);
        miscConfig(root, entryBuilder);
        return root;
    }


    @SuppressWarnings("all")
    private static void maidConfig(ConfigBuilder root, ConfigEntryBuilder entryBuilder) {
        ConfigCategory maid = root.getOrCreateCategory(Component.translatable("entity.touhou_little_maid.maid"));
        maid.addEntry(entryBuilder.startDropdownMenu(Component.translatable("config.touhou_little_maid.maid.maid_tamed_item.name"),
                        DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MaidConfig.MAID_TAMED_ITEM.get()))),
                        DropdownMenuBuilder.CellCreatorBuilder.ofItemObject())
                .setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new)))
                .setDefaultValue(Items.CAKE).setTooltip(Component.translatable("config.touhou_little_maid.maid.maid_tamed_item.desc"))
                .setSaveConsumer(s -> MaidConfig.MAID_TAMED_ITEM.set(ForgeRegistries.ITEMS.getKey(s).toString())).build());


        maid.addEntry(entryBuilder.startDropdownMenu(Component.translatable("config.touhou_little_maid.maid.maid_temptation_item.name"),
                        DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MaidConfig.MAID_TEMPTATION_ITEM.get()))),
                        DropdownMenuBuilder.CellCreatorBuilder.ofItemObject())
                .setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new)))
                .setDefaultValue(Items.CAKE).setTooltip(Component.translatable("config.touhou_little_maid.maid.maid_temptation_item.desc"))
                .setSaveConsumer(s -> MaidConfig.MAID_TEMPTATION_ITEM.set(ForgeRegistries.ITEMS.getKey(s).toString())).build());


        maid.addEntry(entryBuilder.startDropdownMenu(Component.translatable("config.touhou_little_maid.maid.maid_ntr_item.name"),
                        DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(ForgeRegistries.ITEMS.getValue(new ResourceLocation(MaidConfig.MAID_NTR_ITEM.get()))),
                        DropdownMenuBuilder.CellCreatorBuilder.ofItemObject())
                .setSelections(Registry.ITEM.stream().sorted(Comparator.comparing(Item::toString)).collect(Collectors.toCollection(LinkedHashSet::new)))
                .setDefaultValue(Items.STRUCTURE_VOID).setTooltip(Component.translatable("config.touhou_little_maid.maid.maid_ntr_item.desc"))
                .setSaveConsumer(s -> MaidConfig.MAID_NTR_ITEM.set(ForgeRegistries.ITEMS.getKey(s).toString())).build());


        maid.addEntry(entryBuilder.startIntSlider(Component.translatable("config.touhou_little_maid.maid.maid_home_range.name"), MaidConfig.MAID_HOME_RANGE.get(), 3, 64)
                .setDefaultValue(8).setTooltip(Component.translatable("config.touhou_little_maid.maid.maid_home_range.desc"))
                .setSaveConsumer(i -> MaidConfig.MAID_HOME_RANGE.set(i)).build());


        maid.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.touhou_little_maid.maid.maid_change_model.name"), MaidConfig.MAID_CHANGE_MODEL.get())
                .setDefaultValue(true).setTooltip(Component.translatable("config.touhou_little_maid.maid.maid_change_model.desc"))
                .setSaveConsumer(MaidConfig.MAID_CHANGE_MODEL::set).build());


        maid.addEntry(entryBuilder.startIntField(Component.translatable("config.touhou_little_maid.maid.owner_max_maid_num.name"), MaidConfig.OWNER_MAX_MAID_NUM.get())
                .setDefaultValue(Integer.MAX_VALUE).setMin(0).setMax(Integer.MAX_VALUE)
                .setTooltip(Component.translatable("config.touhou_little_maid.maid.owner_max_maid_num.desc"))
                .setSaveConsumer(i -> MaidConfig.OWNER_MAX_MAID_NUM.set(i)).build());


        maid.addEntry(entryBuilder.startStrList(Component.translatable("config.touhou_little_maid.maid.maid_ranged_attack_ignore.name"), MaidConfig.MAID_RANGED_ATTACK_IGNORE.get())
                .setDefaultValue(Lists.newArrayList())
                .setTooltip(Component.translatable("config.touhou_little_maid.maid.maid_ranged_attack_ignore.desc"))
                .setSaveConsumer(l -> MaidConfig.MAID_RANGED_ATTACK_IGNORE.set(l)).build());
    }


    private static void chairConfig(ConfigBuilder root, ConfigEntryBuilder entryBuilder) {
        ConfigCategory chair = root.getOrCreateCategory(Component.translatable("entity.touhou_little_maid.chair"));
        chair.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.touhou_little_maid.chair.chair_change_model.name"), ChairConfig.CHAIR_CHANGE_MODEL.get())
                .setDefaultValue(true).setTooltip(Component.translatable("config.touhou_little_maid.chair.chair_change_model.desc"))
                .setSaveConsumer(ChairConfig.CHAIR_CHANGE_MODEL::set).build());


        chair.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.touhou_little_maid.chair.chair_can_destroyed_by_anyone.name"), ChairConfig.CHAIR_CAN_DESTROYED_BY_ANYONE.get())
                .setDefaultValue(true).setTooltip(Component.translatable("config.touhou_little_maid.chair.chair_can_destroyed_by_anyone.desc"))
                .setSaveConsumer(ChairConfig.CHAIR_CAN_DESTROYED_BY_ANYONE::set).build());
    }


    @SuppressWarnings("all")
    private static void miscConfig(ConfigBuilder root, ConfigEntryBuilder entryBuilder) {
        ConfigCategory misc = root.getOrCreateCategory(Component.translatable("config.touhou_little_maid.misc"));
        misc.addEntry(entryBuilder.startDoubleField(Component.translatable("config.touhou_little_maid.misc.maid_fairy_power_point.name"), MiscConfig.MAID_FAIRY_POWER_POINT.get())
                .setDefaultValue(0.16).setMin(0).setMax(5)
                .setTooltip(Component.translatable("config.touhou_little_maid.misc.maid_fairy_power_point.desc"))
                .setSaveConsumer(d -> MiscConfig.MAID_FAIRY_POWER_POINT.set(d)).build());


        misc.addEntry(entryBuilder.startIntField(Component.translatable("config.touhou_little_maid.misc.maid_fairy_spawn_probability.name"), MiscConfig.MAID_FAIRY_SPAWN_PROBABILITY.get())
                .setDefaultValue(70).setMin(0).setMax(Integer.MAX_VALUE)
                .setTooltip(Component.translatable("config.touhou_little_maid.misc.maid_fairy_spawn_probability.desc"))
                .setSaveConsumer(d -> MiscConfig.MAID_FAIRY_SPAWN_PROBABILITY.set(d)).build());


        misc.addEntry(entryBuilder.startStrList(Component.translatable("config.touhou_little_maid.misc.maid_fairy_blacklist_biome.name"), (List<String>) MiscConfig.MAID_FAIRY_BLACKLIST_BIOME.get())
                .setDefaultValue((List<String>) MiscConfig.MAID_FAIRY_BLACKLIST_BIOME.getDefault())
                .setTooltip(Component.translatable("config.touhou_little_maid.misc.maid_fairy_blacklist_biome.desc"))
                .setSaveConsumer(l -> MiscConfig.MAID_FAIRY_BLACKLIST_BIOME.set(l)).build());


        misc.addEntry(entryBuilder.startStrList(Component.translatable("config.touhou_little_maid.misc.maid_fairy_blacklist_dimension.name"), (List<String>) MiscConfig.MAID_FAIRY_BLACKLIST_DIMENSION.get())
                .setDefaultValue((List<String>) MiscConfig.MAID_FAIRY_BLACKLIST_DIMENSION.getDefault())
                .setTooltip(Component.translatable("config.touhou_little_maid.misc.maid_fairy_blacklist_dimension.desc"))
                .setSaveConsumer(l -> MiscConfig.MAID_FAIRY_BLACKLIST_DIMENSION.set(l)).build());


        misc.addEntry(entryBuilder.startDoubleField(Component.translatable("config.touhou_little_maid.misc.player_death_loss_power_point.name"), MiscConfig.PLAYER_DEATH_LOSS_POWER_POINT.get())
                .setDefaultValue(1.0).setMin(0).setMax(5)
                .setTooltip(Component.translatable("config.touhou_little_maid.misc.player_death_loss_power_point.desc"))
                .setSaveConsumer(d -> MiscConfig.PLAYER_DEATH_LOSS_POWER_POINT.set(d)).build());


        misc.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.touhou_little_maid.misc.give_smart_slab.name"), MiscConfig.GIVE_SMART_SLAB.get())
                .setDefaultValue(true).setTooltip(Component.translatable("config.touhou_little_maid.misc.give_smart_slab.desc"))
                .setSaveConsumer(MiscConfig.GIVE_SMART_SLAB::set).build());


        misc.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.touhou_little_maid.misc.give_patchouli_book.name"), MiscConfig.GIVE_PATCHOULI_BOOK.get())
                .setDefaultValue(true).setTooltip(Component.translatable("config.touhou_little_maid.misc.give_patchouli_book.desc"))
                .setSaveConsumer(MiscConfig.GIVE_PATCHOULI_BOOK::set).build());


        misc.addEntry(entryBuilder.startDoubleField(Component.translatable("config.touhou_little_maid.misc.shrine_lamp_effect_cost.name"), MiscConfig.SHRINE_LAMP_EFFECT_COST.get())
                .setDefaultValue(0.9).setMin(0).setMax(Double.MAX_VALUE)
                .setTooltip(Component.translatable("config.touhou_little_maid.misc.shrine_lamp_effect_cost.desc"))
                .setSaveConsumer(d -> MiscConfig.SHRINE_LAMP_EFFECT_COST.set(d)).build());


        misc.addEntry(entryBuilder.startDoubleField(Component.translatable("config.touhou_little_maid.misc.shrine_lamp_max_storage.name"), MiscConfig.SHRINE_LAMP_MAX_STORAGE.get())
                .setDefaultValue(100).setMin(0).setMax(Double.MAX_VALUE)
                .setTooltip(Component.translatable("config.touhou_little_maid.misc.shrine_lamp_max_storage.desc"))
                .setSaveConsumer(d -> MiscConfig.SHRINE_LAMP_MAX_STORAGE.set(d)).build());


        misc.addEntry(entryBuilder.startIntField(Component.translatable("config.touhou_little_maid.misc.shrine_lamp_max_range.name"), MiscConfig.SHRINE_LAMP_MAX_RANGE.get())
                .setDefaultValue(6).setMin(0).setMax(Integer.MAX_VALUE)
                .setTooltip(Component.translatable("config.touhou_little_maid.misc.shrine_lamp_max_range.desc"))
                .setSaveConsumer(d -> MiscConfig.SHRINE_LAMP_MAX_RANGE.set(d)).build());
    }


    public static void registerModsPage() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> getConfigBuilder().setParentScreen(parent).build()));
    }
}