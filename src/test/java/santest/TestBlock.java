package santest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TestBlock
        extends Block
{
    public static final TestBlock INSTANCE = new TestBlock();

    public TestBlock() {
        super(Properties.create(Material.CLAY));
        this.setRegistryName(new ResourceLocation("santest", "testblock"));
    }

    @Override
    @Deprecated
    @Nonnull
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos,
                                             @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit)
    {
        if( worldIn.isRemote ) {
            Minecraft.getInstance().displayGuiScreen(new TestGui());
        }
        return ActionResultType.SUCCESS;
    }
}
