/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel.event;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.sanplayermodel.SanPlayerModel;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.math.Rotations;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SanPlayerModel.ID)
public class ItemClickEvent
{
    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        //TODO: fix code
//        if( event.getItemStack().getItem() instanceof ItemArmorStand && event.getEntity() instanceof EntityPlayer ) {
//            EntityPlayer player = (EntityPlayer) event.getEntity();
//            if( SanPlayerModel.isSanPlayer(player) && !player.isSneaking() ) {
//                EnumFacing facing = event.getFace();
//                if( facing != null && facing != EnumFacing.DOWN ) {
//                    World world = event.getEntity().world;
//                    boolean replaceable = world.getBlockState(event.getPos()).getBlock().isReplaceable(world, event.getPos());
//                    BlockPos pos1 = replaceable ? event.getPos() : event.getPos().offset(facing);
//                    ItemStack heldItem = player.getHeldItem(event.getHand());
//
//                    if( player.canPlayerEdit(pos1, facing, heldItem) ) {
//                        BlockPos pos2 = pos1.up();
//                        boolean isOccupied = !world.isAirBlock(pos1) && !world.getBlockState(pos1).getBlock().isReplaceable(world, pos1);
//                        isOccupied = isOccupied | (!world.isAirBlock(pos2) && !world.getBlockState(pos2).getBlock().isReplaceable(world, pos2));
//
//                        if( !isOccupied ) {
//                            double posX = pos1.getX();
//                            double posY = pos1.getY();
//                            double posZ = pos1.getZ();
//                            List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(posX, posY, posZ, posX + 1.0D, posY + 2.0D, posZ + 1.0D));
//
//                            if( entities.isEmpty() ) {
//                                if( !world.isRemote ) {
//                                    world.removeBlock(pos1);
//                                    world.removeBlock(pos2);
//                                    EntitySanArmorStand stand = new EntitySanArmorStand(world, posX + 0.5D, posY, posZ + 0.5D);
//                                    float rotation = (float) MathHelper.floor((MathHelper.wrapDegrees(player.rotationYaw - 180.0F) + 22.5F) / 45.0F) * 45.0F;
//                                    stand.setLocationAndAngles(posX + 0.5D, posY, posZ + 0.5D, rotation, 0.0F);
//                                    doRandomRotations(stand);
//                                    ItemMonsterPlacer.applyItemEntityDataToEntity(world, player, heldItem, stand);
//                                    world.spawnEntity(stand);
//                                    world.playSound(null, stand.posX, stand.posY, stand.posZ, SoundEvents.ENTITY_ARMORSTAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
//                                }
//
//                                heldItem.shrink(1);
//
//                                event.setCanceled(true);
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    private static void doRandomRotations(EntityArmorStand armorStand) {
        Rotations origHeadRotation = armorStand.getHeadRotation();
        float rnd1 = MiscUtils.RNG.randomFloat() * 5.0F;
        float rnd2 = MiscUtils.RNG.randomFloat() * 20.0F - 10.0F;
        Rotations newRotation = new Rotations(origHeadRotation.getX() + rnd1, origHeadRotation.getY() + rnd2, origHeadRotation.getZ());
        armorStand.setHeadRotation(newRotation);
        origHeadRotation = armorStand.getBodyRotation();
        rnd1 = MiscUtils.RNG.randomFloat() * 10.0F - 5.0F;
        newRotation = new Rotations(origHeadRotation.getX(), origHeadRotation.getY() + rnd1, origHeadRotation.getZ());
        armorStand.setBodyRotation(newRotation);
    }
}
