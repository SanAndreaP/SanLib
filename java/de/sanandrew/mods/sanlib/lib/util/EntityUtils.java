/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class EntityUtils
{
    public static Entity getEntityByUUID(World worldObj, UUID uuid) {
        return worldObj.loadedEntityList.stream().filter(entity -> entity.getUniqueID().equals(uuid)).findFirst().orElse(null);
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
