package com.github.tartaricacid.touhoulittlemaid.client.gui.inventory;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidAPI;
import com.github.tartaricacid.touhoulittlemaid.client.gui.skin.MaidSkinGui;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.MaidSoundEvent;
import com.github.tartaricacid.touhoulittlemaid.inventory.MaidMainContainer;
import com.github.tartaricacid.touhoulittlemaid.network.simpleimpl.ChangeGuiMessage;
import com.github.tartaricacid.touhoulittlemaid.network.simpleimpl.ChangeHomeDataMessage;
import com.github.tartaricacid.touhoulittlemaid.network.simpleimpl.ChangePickupDataMessage;
import com.github.tartaricacid.touhoulittlemaid.proxy.CommonProxy;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * 女仆主 GUI 界面的集合，其他界面在此基础上拓展得到
 *
 * @author TartaricAcid
 */
@SideOnly(Side.CLIENT)
public abstract class AbstractMaidGuiContainer extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/inventory_main.png");
    protected MaidMainContainer container;
    EntityMaid maid;
    private EntityMaid renderingMaid;
    private int guiId;
    private GuiButtonToggle togglePickup;
    private GuiButtonToggle toggleHome;

    public AbstractMaidGuiContainer(MaidMainContainer inventorySlotsIn, int guiId) {
        super(inventorySlotsIn);
        this.guiId = guiId;
        this.container = inventorySlotsIn;
        this.maid = container.maid;
    }

    /**
     * 绘制自定义的背景，会在基础背景调用后，但在控件，前景图标，文本调用前渲染
     *
     * @param mouseX       鼠标 x 坐标
     * @param mouseY       鼠标 y 坐标
     * @param partialTicks tick 插值
     */
    public abstract void drawCustomBackground(int mouseX, int mouseY, float partialTicks);

    /**
     * 绘制自定义 GUI，会在主背景绘制后调用
     *
     * @param mouseX       鼠标 x 坐标
     * @param mouseY       鼠标 y 坐标
     * @param partialTicks tick 插值
     */
    public abstract void drawCustomScreen(int mouseX, int mouseY, float partialTicks);

    /**
     * 该 GUI 的名称
     */
    public abstract String getGuiName();

    @Override
    public void initGui() {
        super.initGui();
        int i = this.guiLeft;
        int j = this.guiTop;

        // 为了避免转向错误，初始化用于绘制的实体
        renderingMaid = new EntityMaid(mc.world);
        NBTTagCompound nbt = new NBTTagCompound();
        maid.writeEntityToNBT(nbt);
        renderingMaid.readEntityFromNBT(nbt);

        // 切换是否拾起物品的按钮
        togglePickup = new GuiButtonToggle(BUTTON.PICKUP.ordinal(), i + 143, j + 63, 26, 16, maid.isPickup());
        togglePickup.initTextureValues(178, 0, 28, 18, BACKGROUND);
        this.buttonList.add(togglePickup);

        // 不同标签页切换按钮
        this.buttonList.add(new GuiButtonImage(BUTTON.MAIN.ordinal(), i + 3, j - 25, 22,
                22, 234, 234, 0, BACKGROUND));
        this.buttonList.add(new GuiButtonImage(BUTTON.INVENTORY.ordinal(), i + 31, j - 25, 22,
                22, 234, 234, 0, BACKGROUND));
        this.buttonList.add(new GuiButtonImage(BUTTON.BAUBLE.ordinal(), i + 59, j - 25, 22,
                22, 234, 234, 0, BACKGROUND));

        // 模式切换按钮
        this.buttonList.add(new GuiButtonImage(BUTTON.TASK_SWITCH.ordinal(), i - 28, j, 28,
                26, 225, 230, 0, BACKGROUND));

        // 切换是否开启 home 模式的按钮
        toggleHome = new GuiButtonToggle(BUTTON.HOME.ordinal(), i + 116, j + 63, 26, 16, maid.isHome());
        toggleHome.initTextureValues(178, 36, 28, 18, BACKGROUND);
        this.buttonList.add(toggleHome);

        // 切换模型的按钮
        this.buttonList.add(new GuiButtonImage(BUTTON.SKIN.ordinal(), i + 65, j + 9, 9,
                9, 178, 72, 10, BACKGROUND));

        // 显示声音版权的页面
        this.buttonList.add(new GuiButtonImage(BUTTON.SOUND_CREDIT.ordinal(), i - 19, j + 141, 19,
                21, 233, 0, 22, BACKGROUND));

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.id == BUTTON.PICKUP.ordinal()) {
            if (maid.isPickup()) {
                togglePickup.setStateTriggered(false);
                CommonProxy.INSTANCE.sendToServer(new ChangePickupDataMessage(maid.getUniqueID(), false));
                return;
            } else {
                togglePickup.setStateTriggered(true);
                CommonProxy.INSTANCE.sendToServer(new ChangePickupDataMessage(maid.getUniqueID(), true));
                return;
            }
        }

        // 切换标签页
        if (button.id == BUTTON.MAIN.ordinal()) {
            CommonProxy.INSTANCE.sendToServer(new ChangeGuiMessage(mc.player.getUniqueID(), maid.getEntityId(), BUTTON.MAIN.getGuiId(), container.taskIndex));
            return;
        }
        if (button.id == BUTTON.INVENTORY.ordinal()) {
            CommonProxy.INSTANCE.sendToServer(new ChangeGuiMessage(mc.player.getUniqueID(), maid.getEntityId(), BUTTON.INVENTORY.getGuiId(), container.taskIndex));
            return;
        }
        if (button.id == BUTTON.BAUBLE.ordinal()) {
            CommonProxy.INSTANCE.sendToServer(new ChangeGuiMessage(mc.player.getUniqueID(), maid.getEntityId(), BUTTON.BAUBLE.getGuiId(), container.taskIndex));
            return;
        }

        // 切换任务
        if (button.id == BUTTON.TASK_SWITCH.ordinal()) {
            List<IMaidTask> tasks = LittleMaidAPI.getTasks();
            container.taskIndex = (container.taskIndex + 1) % tasks.size();
            container.task = LittleMaidAPI.getTasks().get(container.taskIndex);
            return;
        }

        if (button.id == BUTTON.HOME.ordinal()) {
            if (maid.isHome()) {
                toggleHome.setStateTriggered(false);
                CommonProxy.INSTANCE.sendToServer(new ChangeHomeDataMessage(maid.getUniqueID(), false));
                return;
            } else {
                toggleHome.setStateTriggered(true);
                CommonProxy.INSTANCE.sendToServer(new ChangeHomeDataMessage(maid.getUniqueID(), true));
                return;
            }
        }

        if (button.id == BUTTON.SKIN.ordinal()) {
            // 避免多线程的 Bug
            mc.addScheduledTask(() -> mc.displayGuiScreen(new MaidSkinGui(maid)));
            return;
        }

        if (button.id == BUTTON.SOUND_CREDIT.ordinal()) {
            mc.player.playSound(MaidSoundEvent.OTHER_CREDIT, 1, 1);
            mc.addScheduledTask(() -> mc.displayGuiScreen(new GuiSoundCredit(this)));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        // 绘制自定义前景
        this.drawCustomScreen(mouseX, mouseY, partialTicks);

        // 绘制物品的文本提示
        this.renderHoveredToolTip(mouseX, mouseY);

        int i = this.guiLeft;
        int j = this.guiTop;
        boolean xInRange;
        boolean yInRange;

        // 绘制模式上方的文字提示
        xInRange = (i - 28) < mouseX && mouseX < i;
        yInRange = j < mouseY && mouseY < (j + 26);
        if (xInRange && yInRange) {
            this.drawHoveringText(I18n.format("gui.touhou_little_maid.button.mode_switch",
                    I18n.format(container.task.getTranslationKey())), mouseX, mouseY);
        }

        // 绘制不同标签页的提示文字
        xInRange = (i + 28 * (guiId - 1)) < mouseX && mouseX < (i + 28 * guiId);
        yInRange = (j - 28) < mouseY && mouseY < j;
        if (xInRange && yInRange) {
            this.drawHoveringText(I18n.format("gui.touhou_little_maid.tab." + getGuiName()), mouseX, mouseY);
        }

        // Home 模式的描述
        xInRange = (i + 143) < mouseX && mouseX < (i + 169);
        yInRange = (j + 63) < mouseY && mouseY < (j + 79);
        if (xInRange && yInRange) {
            this.drawHoveringText(I18n.format("gui.touhou_little_maid.button.pickup." + maid.isPickup()), mouseX, mouseY);
        }

        // 拾物模式描述
        xInRange = (i + 116) < mouseX && mouseX < (i + 142);
        yInRange = (j + 63) < mouseY && mouseY < (j + 79);
        if (xInRange && yInRange) {
            this.drawHoveringText(I18n.format("gui.touhou_little_maid.button.home." + maid.isHome()), mouseX, mouseY);
        }

        // 切换皮肤描述
        xInRange = (i + 65) < mouseX && mouseX < (i + 74);
        yInRange = (j + 9) < mouseY && mouseY < (j + 18);
        if (xInRange && yInRange) {
            this.drawHoveringText(I18n.format("gui.touhou_little_maid.button.skin"), mouseX, mouseY);
        }
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int i = this.guiLeft;
        int j = this.guiTop;

        this.drawDefaultBackground();

        // 绘制选择图标背景
        mc.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(i, j - 28, 0, 193, 112, 32);

        // 绘制主背景
        mc.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        // 绘制自定义背景
        drawCustomBackground(mouseX, mouseY, partialTicks);

        // 绘制选择图标前景
        mc.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(i + 28 * (guiId - 1), j - 28, 28 * (guiId - 1), 224, 28, 32);

        // 绘制模式图标背景
        mc.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(i - 28, j, 0, 167, 31, 26);

        // 绘制模式图标
        this.drawItemStack(Items.WRITABLE_BOOK.getDefaultInstance(), i + 6, j - 19, "");
        this.drawItemStack(Item.getItemFromBlock(Blocks.CHEST).getDefaultInstance(), i + 34, j - 19, "");
        this.drawItemStack(new ItemStack(Items.DYE, 1, 4), i + 62, j - 19, "");
        this.drawItemStack(Items.DIAMOND_SWORD.getDefaultInstance(), i + 90, j - 19, "");

        // 绘制模式图标
        this.drawItemStack(container.task.getIcon(), i - 20, j + 5, "");

        // 绘制女仆样子
        // 首先同步手持和护甲物品
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            renderingMaid.setItemStackToSlot(slot, maid.getItemStackFromSlot(slot));
        }
        GuiInventory.drawEntityOnScreen(i + 51, j + 70, 28,
                (float) (i + 51) - mouseX, (float) (j + 70 - 45) - mouseY, renderingMaid);
    }

    /**
     * 通过 ItemStack 绘制对应图标
     *
     * @param altText 图标右下角的文字
     */
    private void drawItemStack(ItemStack stack, int x, int y, String altText) {
        RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) {
            font = fontRenderer;
        }
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, altText);
        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;

        RenderHelper.disableStandardItemLighting();
    }

    /**
     * 一个简单的打开连接的提示界面
     */
    private class GuiSoundCredit extends GuiConfirmOpenLink {
        public GuiSoundCredit(GuiYesNoCallback parentScreenIn) {
            super(parentScreenIn, "https://www14.big.or.jp/~amiami/happy/index.html", BUTTON.SOUND_CREDIT.ordinal(), true);
        }

        @Override
        protected void actionPerformed(GuiButton button) {
            if (button.id == 0) {
                try {
                    super.openWebLink(new URI("https://www14.big.or.jp/~amiami/happy/index.html"));
                } catch (URISyntaxException urisyntaxexception) {
                    TouhouLittleMaid.LOGGER.error("Can't open url for {}", urisyntaxexception);
                }
                return;
            }
            if (button.id == 1) {
                mc.addScheduledTask(() -> mc.displayGuiScreen(null));
                return;
            }
            if (button.id == 2) {
                this.copyLinkToClipboard();
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.disableSecurityWarning();
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.drawCenteredString(this.fontRenderer, TextFormatting.GOLD.toString() + TextFormatting.BOLD.toString() +
                    I18n.format("gui.touhou_little_maid.credit.url.close"), this.width / 2, 110, 0xffcccc);
        }
    }

    /**
     * 该界面的按钮枚举
     */
    public enum BUTTON {
        // 拾物模式按钮
        PICKUP(-1),
        // 主界面按钮
        MAIN(1),
        // 主物品栏按钮
        INVENTORY(2),
        // 饰品栏按钮
        BAUBLE(3),
        // 模式切换按钮
        TASK_SWITCH(-1),
        // HOME 模式切换按钮
        HOME(-1),
        // 女仆模型皮肤按钮
        SKIN(-1),
        // 声音素材致谢
        SOUND_CREDIT(-1);

        private int guiId;

        /**
         * @param guiId 摁下按钮后触发的 GUI 的 ID，如果不触发 GUI，可以将其设置为 -1
         */
        BUTTON(int guiId) {
            this.guiId = guiId;
        }

        public int getGuiId() {
            return guiId;
        }
    }
}