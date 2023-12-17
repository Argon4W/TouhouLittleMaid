package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import com.github.tartaricacid.touhoulittlemaid.util.PlaceHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.github.tartaricacid.touhoulittlemaid.item.MaidGroup.MAIN_TAB;

public class ItemPhoto extends Item {
    private static final String MAID_INFO = "MaidInfo";

    public ItemPhoto() {
        super((new Properties()).tab(MAIN_TAB).stacksTo(1));
    }

    public static boolean hasMaidData(ItemStack stack) {
        return stack.hasTag() && !Objects.requireNonNull(stack.getTag()).getCompound(MAID_INFO).isEmpty();
    }

    public static CompoundNBT getMaidData(ItemStack stack) {
        if (hasMaidData(stack)) {
            return Objects.requireNonNull(stack.getTag()).getCompound(MAID_INFO);
        }
        return new CompoundNBT();
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        Direction facing = context.getClickedFace();
        World worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos();
        PlayerEntity player = context.getPlayer();
        ItemStack photo = context.getItemInHand();
        Vector3d clickLocation = context.getClickLocation();

        if (player == null) {
            return super.useOn(context);
        }

        // 方向不对或者位置不合适
        if (facing != Direction.UP || PlaceHelper.notSuitableForPlaceMaid(worldIn, pos)) {
            if (worldIn.isClientSide) {
                player.sendMessage(new TranslationTextComponent("message.touhou_little_maid.photo.not_suitable_for_place_maid"), Util.NIL_UUID);
            }
            return ActionResultType.FAIL;
        }

        // 检查照片的 NBT 数据
        if (!hasMaidData(photo)) {
            if (worldIn.isClientSide) {
                player.sendMessage(new TranslationTextComponent("message.touhou_little_maid.photo.have_no_nbt_data"), Util.NIL_UUID);
            }
            return ActionResultType.FAIL;
        }

        // 最后才应用生成实体的逻辑
        Optional<Entity> entityOptional = EntityType.create(getMaidData(photo), worldIn);
        if (entityOptional.isPresent() && entityOptional.get() instanceof EntityMaid) {
            EntityMaid maid = (EntityMaid) entityOptional.get();
            maid.setPos(clickLocation.x, clickLocation.y, clickLocation.z);
            // 实体生成必须在服务端应用
            if (!worldIn.isClientSide) {
                worldIn.addFreshEntity(maid);
            }
            maid.spawnExplosionParticle();
            photo.shrink(1);
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.FAIL;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!entity.isInvulnerable()) {
            entity.setInvulnerable(true);
        }
        Vector3d position = entity.position();
        if (position.y < 0) {
            entity.setNoGravity(true);
            entity.setDeltaMovement(Vector3d.ZERO);
            entity.setPos(position.x, 0, position.z);
        }
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!hasMaidData(stack)) {
            tooltip.add(new TranslationTextComponent("tooltips.touhou_little_maid.photo.no_data.desc").withStyle(TextFormatting.DARK_RED));
        } else {
            CompoundNBT maidData = getMaidData(stack);
            if (maidData.contains(EntityMaid.MODEL_ID_TAG, Constants.NBT.TAG_STRING)) {
                String modelId = maidData.getString(EntityMaid.MODEL_ID_TAG);
                if (StringUtils.isNotBlank(modelId)) {
                    CustomPackLoader.MAID_MODELS.getInfo(modelId).ifPresent(modelItem ->
                            tooltip.add(new TranslationTextComponent("tooltips.touhou_little_maid.photo.maid.desc",
                                    I18n.get(ParseI18n.getI18nKey(modelItem.getName()))).withStyle(TextFormatting.GRAY)
                            ));
                }
            }
        }
    }
}
