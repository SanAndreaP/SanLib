package de.sanandrew.mods.sanlib.lib.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NumberNBT;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public final class NBTUtils
{
    private NBTUtils() { }

    public static <E, T extends Collection<E>> ListNBT getNewNbtList(T collectionToProcess, BiConsumer<E, ListNBT> processFunc) {
        ListNBT ln = new ListNBT();
        for( E elem : collectionToProcess ) {
            processFunc.accept(elem, ln);
        }

        return ln;
    }

    public static boolean doesNbtContainOther(CompoundNBT mainNBT, CompoundNBT otherNBT) {
        return doesNbtContainOther(mainNBT, otherNBT, true);
    }

    public static boolean doesNbtContainOther(final CompoundNBT mainNBT, final CompoundNBT otherNBT, boolean strict) {
        return otherNBT == null
               || (mainNBT != null && otherNBT.getAllKeys().stream().allMatch(key -> {
                        if( mainNBT.contains(key) ) {
                            if( strict ) {
                                return mainNBT.getTagType(key) == otherNBT.getTagType(key) && Objects.equals(mainNBT.get(key), otherNBT.get(key));
                            } else {
                                return compareNBTBase(mainNBT.get(key), otherNBT.get(key));
                            }
                        }

                        return false;
                    })
               );
    }

    private static boolean compareNBTBase(INBT main, INBT other) {
        if( main instanceof NumberNBT && other instanceof NumberNBT ) {
            NumberNBT mainBase = ((NumberNBT) main);
            NumberNBT otherBase = ((NumberNBT) other);

            long mainNb = isNbtDouble(mainBase) ? Double.doubleToLongBits(mainBase.getAsDouble()) : mainBase.getAsLong();
            long otherNb = isNbtDouble(otherBase) ? Double.doubleToLongBits(otherBase.getAsDouble()) : otherBase.getAsLong();

            return mainNb == otherNb;
        } else if( main instanceof ListNBT && other instanceof ListNBT ) {
            return compareNbtLists((ListNBT) main, (ListNBT) other);
        } else if( main instanceof CompoundNBT && other instanceof CompoundNBT ) {
            return doesNbtContainOther((CompoundNBT) main, (CompoundNBT) other, false);
        } else {
            return main.equals(other);
        }
    }

    private static boolean compareNbtLists(ListNBT mainList, ListNBT otherList) {
        otherList = otherList.copy();

        if( mainList.getElementType() == otherList.getElementType() ) {
            for( int i = mainList.size() - 1; i >= 0 && !otherList.isEmpty(); i-- ) {
                for( int j = otherList.size() - 1; j >= 0; j-- ) {
                    if( compareNBTBase(mainList.get(i), otherList.get(j)) ) {
                        otherList.remove(j);
                        break;
                    }
                }
            }

            return otherList.isEmpty();
        }

        return false;
    }

    private static boolean isNbtDouble(INBT base) {
        return base instanceof DoubleNBT || base instanceof FloatNBT;
    }
}
