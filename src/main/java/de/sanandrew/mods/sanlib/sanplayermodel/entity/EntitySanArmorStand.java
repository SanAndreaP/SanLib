/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel.entity;

import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.world.World;

public class EntitySanArmorStand
        extends EntityArmorStand
{
    public EntitySanArmorStand(World worldIn) {
        super(worldIn);
    }

    public EntitySanArmorStand(World worldIn, double posX, double posY, double posZ) {
        super(worldIn, posX, posY, posZ);
    }
}
