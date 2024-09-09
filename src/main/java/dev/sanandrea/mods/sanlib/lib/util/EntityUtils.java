/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.util;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

@SuppressWarnings({ "unused", "unchecked" })
public final class EntityUtils
{
    private EntityUtils() {}

    public static Entity getServerEntity(Level level, UUID uuid) {
        return (level instanceof ServerLevel serverLvl ? serverLvl.getEntity(uuid) : null);
    }

    public static <T extends Entity> List<T> getPassengersOfClass(Entity e, Class<T> psgClass) {
        return e.getPassengers().stream().filter(psgClass::isInstance).map(entity -> (T) entity).toList();
    }

//    public static <T extends Goal> List<T> getAisFromTaskList(Brain<?> brain, Class<T> cls) {
//        return brain..stream().filter(task -> cls.equals(task.getGoal().getClass())).map(task -> (T) task.getGoal()).collect(Collectors.toList());
//    }

    public static boolean tryApplyModifier(LivingEntity e, Holder<Attribute> attribute, AttributeModifier modifier, boolean permanent) {
        AttributeInstance attrib = e.getAttribute(attribute);
        if( attrib != null && !attrib.hasModifier(modifier.id()) ) {
            if( permanent ) {
                attrib.addPermanentModifier(modifier);
            } else {
                attrib.addTransientModifier(modifier);
            }

            return true;
        }

        return false;
    }

    public static boolean tryRemoveModifier(LivingEntity e, Holder<Attribute> attribute, AttributeModifier modifier) {
        AttributeInstance attrib = e.getAttribute(attribute);
        if( attrib != null && attrib.hasModifier(modifier.id()) ) {
            attrib.removeModifier(modifier);

            return true;
        }

        return false;
    }
}
