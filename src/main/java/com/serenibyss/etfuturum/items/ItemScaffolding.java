package com.serenibyss.etfuturum.items;

import com.serenibyss.etfuturum.blocks.BlockScaffolding;
import com.serenibyss.etfuturum.blocks.EFMBlocks;
import com.serenibyss.etfuturum.util.IModelRegister;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemScaffolding extends ItemBlock implements IModelRegister {
    public ItemScaffolding() {
        super(EFMBlocks.SCAFFOLDING.getBlock());
        setTranslationKey("scaffolding_item");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        ItemStack itemStack = player.getHeldItem(hand);
        if(state.getBlock() != this.getBlock()) {
            if(BlockScaffolding.getDistance(worldIn, pos) == 7) {
                return EnumActionResult.FAIL;
            } else {
                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(pos.offset(facing));
                this.placeBlockAt(itemStack, player, worldIn, mutableBlockPos, facing, hitX, hitY, hitZ, EFMBlocks.SCAFFOLDING.getBlock().getDefaultState().withProperty(BlockScaffolding.DISTANCE, 0).withProperty(BlockScaffolding.ON_AIR, false));
                return EnumActionResult.PASS;
            }
        } else {
            EnumFacing direction;
            if(player.isSneaking()) {
                RayTraceResult ray = this.rayTrace(worldIn, player, false);
                BlockPos hitBlock = ray.getBlockPos();
                AxisAlignedBB pos2 = makeAABB(hitBlock);
                direction = player.getEntityBoundingBox().intersects(pos2) ? facing.getOpposite() : facing;
            } else {
                direction = facing == EnumFacing.UP ? player.getHorizontalFacing() : EnumFacing.UP;
            }

            int i = 0;
            int distance = state.getValue(BlockScaffolding.DISTANCE);
            BlockPos.MutableBlockPos mutableBlockPos = (new BlockPos.MutableBlockPos(pos)).move(direction);

            while(i < 7) {
                if(!worldIn.isRemote && !worldIn.isValid(mutableBlockPos)) {
                    int j = worldIn.getHeight();
                    if(player instanceof EntityPlayerMP playerMP && mutableBlockPos.getY() >= j) {
                        TextComponentTranslation textcomponenttranslation = new TextComponentTranslation("build.tooHigh", new Object[] {playerMP.server.getBuildLimit()});
                        textcomponenttranslation.getStyle().setColor(TextFormatting.RED);
                        playerMP.connection.sendPacket(new SPacketChat(textcomponenttranslation, ChatType.GAME_INFO));
                    }
                    break;
                }

                state = worldIn.getBlockState(mutableBlockPos);
                if(state.getBlock() != this.getBlock()) {
                    if(state.getBlock().isReplaceable(worldIn, pos)) {
//                        if((i + distance) > 7)
//                            System.out.println("wtf");
                        //BlockPos pos2 = mutableBlockPos.move(face);
                        this.placeBlockAt(player.getHeldItem(hand), player, worldIn, mutableBlockPos, direction, hitX, hitY, hitZ, EFMBlocks.SCAFFOLDING.getBlock().getDefaultState().withProperty(BlockScaffolding.ON_AIR, true));

                        itemStack.shrink(1);
                        return EnumActionResult.PASS;
                    }
                    break;
                }

                mutableBlockPos.move(direction);
                if(direction.getAxis().isHorizontal()) {
                    ++i;
                }
            }

            return null;
        }
    }

    private static AxisAlignedBB makeAABB(BlockPos pos) {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0D, pos.getY() + 1.0D, pos.getZ() + 1.0D);
    }
}
