////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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
        return (worldObj instanceof ServerWorld ? ((ServerWorld) worldObj).getEntityByUuid(uuid) : null);//new ArrayList<>(worldObj.getLoadedEntitiesWithinAABB()).stream().filter(entity -> entity.getUniqueID().equals(uuid)).findFirst().orElse(null);
    }

    public static <T extends Entity> List<T> getPassengersOfClass(Entity e, Class<T> psgClass) {
        //noinspection unchecked
        return e.getPassengers().stream().filter(psgClass::isInstance).map(entity -> (T) entity).collect(Collectors.toList());
    }

    public static <T extends Goal> List<T> getAisFromTaskList(Set<PrioritizedGoal> taskList, Class<T> cls) {
        //noinspection unchecked
        return taskList.stream().filter(task -> cls.equals(task.getGoal().getClass())).map(task -> (T) task.getGoal()).collect(Collectors.toList());
    }

    public static boolean tryApplyModifier(LivingEntity e, Attribute attribute, AttributeModifier modifier, boolean persist) {
        ModifiableAttributeInstance attrib = e.getAttribute(attribute);
        if( attrib != null && !attrib.hasModifier(modifier) ) {
            if( persist ) {
                attrib.applyPersistentModifier(modifier);
            } else {
                attrib.applyNonPersistentModifier(modifier);
            }

            return true;
        }

        return false;
    }

//    public static boolean tryApplyModifier(LivingEntity e, String attributeName, AttributeModifier modifier) {
//        ModifiableAttributeInstance attrib = e.getAttributeManager(attributeName);
//        if( attrib != null && !attrib.hasModifier(modifier) ) {
//            attrib.applyModifier(modifier);
//
//            return true;
//        }
//
//        return false;
//    }

    public static boolean tryRemoveModifier(LivingEntity e, Attribute attribute, AttributeModifier modifier) {
        ModifiableAttributeInstance attrib = e.getAttribute(attribute);
        if( attrib != null && attrib.hasModifier(modifier) ) {
            attrib.removeModifier(modifier);

            return true;
        }

        return false;
    }

//    public static boolean tryRemoveModifier(LivingEntity e, String attributeName, AttributeModifier modifier) {
//        ModifiableAttributeInstance attrib = e.getAttributeMap().getAttributeInstanceByName(attributeName);
//        if( attrib != null && attrib.hasModifier(modifier) ) {
//            attrib.removeModifier(modifier);
//
//            return true;
//        }
//
//        return false;
//    }
}
