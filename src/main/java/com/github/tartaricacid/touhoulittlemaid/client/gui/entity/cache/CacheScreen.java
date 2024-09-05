package com.github.tartaricacid.touhoulittlemaid.client.gui.entity.cache;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.texture.CacheIconTexture;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.IModelInfo;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
import com.github.tartaricacid.touhoulittlemaid.util.IconCache;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Queue;
import java.util.concurrent.ExecutionException;


@OnlyIn(Dist.CLIENT)
public class CacheScreen<T extends LivingEntity, E extends IModelInfo> extends Screen {
    private final Screen parent;
    private final EntityType<T> entityType;
    private final Queue<E> modelInfos;
    private final EntityRender<T, E> entityRender;
    private final int totalCount;

    public CacheScreen(Screen parent, EntityType<T> entityType, Queue<E> modelInfos, EntityRender<T, E> entityRender) {
        super(Component.literal(" Cache Screen"));
        this.parent = parent;
        this.entityType = entityType;
        this.modelInfos = modelInfos;
        this.entityRender = entityRender;
        this.totalCount = modelInfos.size();
    }

    @SuppressWarnings("unchecked")
    private void drawEntity(GuiGraphics graphics, int posX, int posY, E modelInfo, int scaleModified) {
        Level world = getMinecraft().level;
        if (world == null) {
            return;
        }
        T entity;
        try {
            entity = (T) EntityCacheUtil.ENTITY_CACHE.get(entityType, () -> entityType.create(world));
        } catch (ExecutionException | ClassCastException e) {
            e.fillInStackTrace();
            return;
        }
        entityRender.render(graphics, posX, posY, modelInfo, scaleModified, entity);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (modelInfos.isEmpty()) {
            Minecraft.getInstance().setScreen(parent);
            return;
        }

        E modelInfo = modelInfos.poll();
        if (modelInfo != null) {
            double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
            int scaleModified = (int) Math.ceil((256 / guiScale));

            graphics.fill(0, 0, scaleModified, scaleModified + 2, IconCache.BACKGROUND_COLOR);
            this.drawEntity(graphics, 0, 0, modelInfo, scaleModified);
            NativeImage nativeImage = IconCache.exportImageFromScreenshot(256, IconCache.BACKGROUND_COLOR_SHIFTED);

            ResourceLocation modelId = modelInfo.getModelId();
            CacheIconTexture cacheIconTexture = new CacheIconTexture(modelId, nativeImage);
            Minecraft.getInstance().getTextureManager().register(modelInfo.getCacheIconId(), cacheIconTexture);
        }

        int finishSize = totalCount - modelInfos.size();
        graphics.drawCenteredString(font, Component.translatable("gui.touhou_little_maid.cache_screen.progress", finishSize, totalCount), this.width / 2, this.height - 42, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.translatable("gui.touhou_little_maid.cache_screen.desc"), this.width / 2, this.height - 30, 0xFFFFFF);
    }

    public interface EntityRender<T extends LivingEntity, E extends IModelInfo> {
        void render(GuiGraphics graphics, int posX, int posY, E modelInfo, int scaleModified, T entity);
    }
}