////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class EntityUtils
{
    public static final int ATTR_ADD_VAL_TO_BASE = 0;
    public static final int ATTR_ADD_PERC_VAL_TO_SUM = 1;
    public static final int RISE_SUM_WITH_PERC_VAL = 2;

    public static Entity getEntityByUUID(World worldObj, UUID uuid) {
        return new ArrayList<>(worldObj.loadedEntityList).stream().filter(entity -> entity.getUniqueID().equals(uuid)).findFirst().orElse(null);
    }

    public static <T extends Entity> List<T> getPassengersOfClass(Entity e, Class<T> psgClass) {
        //noinspection unchecked
        return e.getPassengers().stream().filter(psgClass::isInstance).map(entity -> (T) entity).collect(Collectors.toList());
    }

    public static <T extends EntityAIBase> List<T> getAisFromTaskList(Set<EntityAITasks.EntityAITaskEntry> taskList, Class<T> cls) {
        //noinspection unchecked
        return taskList.stream().filter(task -> cls.equals(task.action.getClass())).map(task -> (T) task.action).collect(Collectors.toList());
    }
}
