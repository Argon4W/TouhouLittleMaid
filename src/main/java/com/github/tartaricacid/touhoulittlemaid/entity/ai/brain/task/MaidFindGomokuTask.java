package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.block.BlockJoy;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityGomoku;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MaidFindGomokuTask extends MaidMoveToBlockTask {
    public MaidFindGomokuTask(float movementSpeed, int searchLength) {
        super(movementSpeed, searchLength);
    }

    @Override
    protected boolean shouldMoveTo(ServerLevel worldIn, EntityMaid entityIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos);
        return blockstate.getBlock() instanceof BlockJoy && !this.isOccupied(worldIn, pos);
    }

    @Override
    protected void start(ServerLevel worldIn, EntityMaid maid, long gameTimeIn) {
        if (maid.getVehicle() == null && !maid.isInSittingPose()) {
            this.searchForDestination(worldIn, maid);
        }
    }

    private boolean isOccupied(ServerLevel worldIn, BlockPos pos) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof TileEntityGomoku gomoku) {
            return worldIn.getEntity(gomoku.getSitId()) != null;
        }
        return true;
    }
}
