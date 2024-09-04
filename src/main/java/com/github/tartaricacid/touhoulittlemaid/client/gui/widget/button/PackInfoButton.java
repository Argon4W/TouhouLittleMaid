package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.download.pojo.DownloadInfo;
import com.github.tartaricacid.touhoulittlemaid.client.download.pojo.DownloadStatus;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;

public class PackInfoButton extends FlatColorButton {
    private static final ResourceLocation BG = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/download_background.png");
    private final DownloadInfo info;

    public PackInfoButton(int pX, int pY, DownloadInfo info, OnPress onPress) {
        super(pX, pY, 268, 46, Component.empty(), onPress);
        this.info = info;
        this.setTooltips(Lists.newArrayList(getI18nFormatDesc(info.getDesc()), getI18nFormatLicense(info.getLicense())));
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BG);
        // 背景色
        if (isSelect) {
            fillGradient(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, 0xff_1E90FF, 0xff_1E90FF);
        } else if (info.getStatus() != DownloadStatus.DOWNLOADED) {
            fillGradient(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, 0xff_434242, 0xff_434242);
        }

        // 如果是下载状态，绘制下载进度条
        int downloadProgress = info.getDownloadProgress();
        if (info.getStatus() == DownloadStatus.DOWNLOADING && downloadProgress >= 0) {
            int widthProgress = this.width * downloadProgress / 100;
            fillGradient(poseStack, this.x, this.y, this.x + widthProgress, this.y + this.height, 0xff_ff5733, 0xff_ff5733);
        }

        // 绘制需要更新的提示
        if (info.getStatus() == DownloadStatus.NEED_UPDATE) {
            blit(poseStack, this.x + 240, this.y + 15, 48, 16, 16, 16);
        }

        // 悬浮或者选中时，显示为蓝色
        if (this.isHoveredOrFocused()) {
            fillGradient(poseStack, this.x, this.y + 1, this.x + 1, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            fillGradient(poseStack, this.x, this.y, this.x + this.width, this.y + 1, 0xff_F3EFE0, 0xff_F3EFE0);
            fillGradient(poseStack, this.x + this.width - 1, this.y + 1, this.x + this.width, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            fillGradient(poseStack, this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0xff_F3EFE0, 0xff_F3EFE0);
        }

        // 显示成女仆、坐垫或者声音图标
        int count = info.getTypeCount();
        if (count == 3) {
            blit(poseStack, this.x + 7, this.y + 7, 0, 96, 32, 32);
        } else if (count == 2) {
            if (!info.hasType(DownloadInfo.TypeEnum.MAID)) {
                blit(poseStack, this.x + 7, this.y + 7, 64, 64, 32, 32);
            } else if (!info.hasType(DownloadInfo.TypeEnum.CHAIR)) {
                blit(poseStack, this.x + 7, this.y + 7, 32, 64, 32, 32);
            } else {
                blit(poseStack, this.x + 7, this.y + 7, 0, 64, 32, 32);
            }
        } else {
            if (info.hasType(DownloadInfo.TypeEnum.MAID)) {
                blit(poseStack, this.x + 7, this.y + 7, 0, 32, 32, 32);
            } else if (info.hasType(DownloadInfo.TypeEnum.CHAIR)) {
                blit(poseStack, this.x + 7, this.y + 7, 32, 32, 32, 32);
            } else {
                blit(poseStack, this.x + 7, this.y + 7, 64, 32, 32, 32);
            }
        }

        // 渲染文本
        int i = getFGColor();
        this.renderString(poseStack, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
        this.renderString(poseStack, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);

        // 下载完成，灰色遮罩
        if (info.getStatus() == DownloadStatus.DOWNLOADED) {
            fillGradient(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, 0x7f_232222, 0x7f_232222);
        }
    }

    @Override
    public void renderString(PoseStack poseStack, Font font, int pColor) {
        int startX = this.x + 50;
        int startY = this.y + 4;

        MutableComponent packName = Component.translatable(info.getName());
        drawString(poseStack, font, packName, startX, startY, ChatFormatting.WHITE.getColor());
        drawString(poseStack, font, getI18nFormatFileVersion(info.getVersion()), startX + 5 + font.width(packName), startY, ChatFormatting.GREEN.getColor());
        drawString(poseStack, font, getI18nFormatFileSize(info.getFormatFileSize()), startX, startY + 10, ChatFormatting.GOLD.getColor());
        drawString(poseStack, font, getI18nFormatAuthor(info.getAuthor()), startX, startY + 20, ChatFormatting.AQUA.getColor());
        drawString(poseStack, font, getI18nFormatFileTime(info.getFormatData()), startX, startY + 30, ChatFormatting.GRAY.getColor());
    }

    private String getI18nFormatFileVersion(String version) {
        return "§nv" + version;
    }

    private MutableComponent getI18nFormatAuthor(List<String> authors) {
        String str = String.join(I18n.get("gui.touhou_little_maid.resources_download.author.delimiter"), authors);
        return Component.translatable("gui.touhou_little_maid.resources_download.author", str);
    }

    private MutableComponent getI18nFormatFileSize(String size) {
        return Component.translatable("gui.touhou_little_maid.resources_download.file_size", size);
    }

    private MutableComponent getI18nFormatFileTime(String time) {
        return Component.translatable("gui.touhou_little_maid.resources_download.upload_time", time);
    }

    private MutableComponent getI18nFormatLicense(String license) {
        return Component.translatable("gui.touhou_little_maid.resources_download.license", license).withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.ITALIC);
    }

    private MutableComponent getI18nFormatDesc(String desc) {
        return Component.translatable(desc).withStyle(ChatFormatting.GRAY);
    }
}
