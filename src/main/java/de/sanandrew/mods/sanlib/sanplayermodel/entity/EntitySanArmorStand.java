////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

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

    @Override
    public boolean getShowArms() {
        return true;
    }
}
