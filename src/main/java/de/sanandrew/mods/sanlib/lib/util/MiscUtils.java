/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util;

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.XorShiftRandom;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Utility class for miscellaneous tasks and methods
 */
@SuppressWarnings("unused")
public final class MiscUtils
{
    /**
     * A globally used pseudorandom number generator. It uses the {@link XorShiftRandom} implementation.
     */
    public static final XorShiftRandom RNG = new XorShiftRandom();

    /** Method to calculate a value based on a formula represented as a {@link java.lang.String}.<br>
     * <i>Code from <a href="http://stackoverflow.com/a/26227947">http://stackoverflow.com/a/26227947</a> with minor changes</i>
     * @param str Formula to calculate, like {@code "2 + 2"}. Also accepts parentheses grouping,
     *            ({@code a}){@code sin}/({@code a}){@code cos}/({@code a}){@code tan}/{@code sqrt} functions and exponentials via " {@code ^} "
     * @return A calculated value from the formula, or {@code null} if the formula is invalid
     */
    public static Double calcFormula(final String str) {
        return new Object() {
            private int pos = -1, ch;

            private void nextChar() {
                this.ch = ++this.pos < str.length() ? str.charAt(this.pos) : -1;
            }

            private boolean eat(int charToEat) {
                while( ch == ' ' ) {
                    this.nextChar();
                }

                if( ch == charToEat ) {
                    this.nextChar();
                    return true;
                }

                return false;
            }

            private Double parse() {
                this.nextChar();
                double x = parseExpression();

                if( this.pos < str.length() ) {
                    return null;
                }

                return x;
            }

            private double parseExpression() {
                double x = this.parseTerm();
                while(true) {
                    if( eat('+') ) {
                        x += parseTerm(); // addition
                    } else if( eat('-') ) {
                        x -= parseTerm(); // subtraction
                    } else {
                        return x;
                    }
                }
            }

            private double parseTerm() {
                Double x = parseFactor();
                if( x == null ) {
                    return 0.0D;
                }
                while(true) {
                    if( eat('*') ) {
                        x *= parseFactor(); // multiplication
                    } else if( eat('/') ) {
                        x /= parseFactor(); // division
                    } else {
                        return x;
                    }
                }
            }

            private Double parseFactor() {
                double sign = 1.0D;
                while( true ) {
                    if( eat('+') ) {
                        continue;
                    }
                    if( eat('-') ) {
                        sign *= -1.0D;
                        continue;
                    }

                    Double x;
                    int startPos = this.pos;
                    if( eat('(') ) { // parentheses
                        x = parseExpression();
                        eat(')');
                    } else if( (ch >= '0' && ch <= '9') || ch == '.' ) { // numbers
                        while( (ch >= '0' && ch <= '9') || ch == '.' ) {
                            nextChar();
                        }
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                    } else if( ch >= 'a' && ch <= 'z' ) { // functions
                        while( ch >= 'a' && ch <= 'z' ) {
                            nextChar();
                        }
                        String func = str.substring(startPos, this.pos);
                        x = parseFactor();
                        if( x == null ) {
                            return null;
                        }
                        switch( func ) {
                            case "sqrt":
                                x = Math.sqrt(x);
                                break;
                            case "sin":
                                x = StrictMath.sin(Math.toRadians(x));
                                break;
                            case "asin":
                                x = StrictMath.sin(Math.toRadians(x));
                                break;
                            case "cos":
                                x = StrictMath.cos(Math.toRadians(x));
                                break;
                            case "acos":
                                x = StrictMath.cos(Math.toRadians(x));
                                break;
                            case "tan":
                                x = StrictMath.tan(Math.toRadians(x));
                                break;
                            case "atan":
                                x = StrictMath.atan(Math.toRadians(x));
                                break;
                            default:
                                return null;
                        }
                    } else {
                        return null;
                    }

                    if( eat('^') ) {
                        Double y = parseFactor();
                        if( y == null ) {
                            return null;
                        }
                        x = StrictMath.pow(x, y); // exponentiation
                    }

                    return x * sign;
                }
            }
        }.parse();
    }

    /**
     * Gets a string representation of the ticks as time
     * @param ticks The ticks to be represented as time
     * @return The formatted time
     */
    public static String getTimeFromTicks(int ticks) {
        int hours = ticks / 72_000;
        int minutes = (ticks - hours * 72_000) / 1_200;
        float seconds = (ticks - hours * 72_000 - minutes * 1_200) / 20.0F;

        StringBuilder sb = new StringBuilder();
        if( hours > 0 ) {
            sb.append(String.format("%dh", hours));
        }
        if( minutes > 0 ) {
            if( sb.length() > 0 ) {
                sb.append(' ');
            }
            sb.append(String.format("%dm", minutes));
        }
        if( seconds > 0.0F ) {
            if( sb.length() > 0 ) {
                sb.append(' ');
            }
            sb.append(String.format("%.1fs", seconds));
        }

        return sb.toString();
    }

    /**
     * Adds the suffix for a list number and appends that to the number, resulting in a string, like "1" becomes "1st"
     * @param nr the number to be suffixed
     * @return the suffixed number as string
     */
    public static String getListNrWithSuffix(int nr) {
        String nrStr = Integer.toString(nr);
        if( nrStr.endsWith("11") || nrStr.endsWith("12") || nrStr.endsWith("13") ) {
            return nrStr + "th";
        }
        Character lastNum = nrStr.charAt(nrStr.length() - 1);
        switch( lastNum ) {
            case '1': return nrStr + "st";
            case '2': return nrStr + "nd";
            case '3': return nrStr + "rd";
            default: return nrStr + "th";
        }
    }

    /**
     * Checks if val is {@code null} and returns {@code def} if so, otherwise {@code val} is returned
     * @param val The value to be checked for null and eventually returned
     * @param def The default value when {@code val} is null
     * @param <T> The type of the value
     * @return value, if it's not null, def otherwise
     */
    public static <T> T defIfNull(T val, T def) {
        return val != null ? val : def;
    }

    /**
     * Looks inside a directory (outside and inside of classpath) and calls the processor for each file found
     * @param mod The mod container to be looked in (Use {@link net.minecraftforge.fml.common.Loader} to get one)
     * @param base The path of the directory to be scanned
     * @param preprocessor A functional reference that gets called before the files are scanned. Its parameter is the path to the directory.
     *                     It should return true if successful, false otherwise (which also cancels further operations)
     * @param processor A functional reference that gets called on each file found. Its parameters are the path to the directory the file is in and the path of the file itself.
     *                  It should return true if the processing was successful, false otherwise (which also cancels further operations)
     * @return true if both the preprocessor (for the directory) and the processor (for each file) return true, false otherwise.
     */
    public static boolean findFiles(ModContainer mod, String base, Function<Path, Boolean> preprocessor, BiFunction<Path, Path, Boolean> processor) {
        File source = mod.getSource();

        if( source.isFile() ) {
            try( FileSystem fs = FileSystems.newFileSystem(source.toPath(), null) ) {
                return findFilesIntrn(fs.getPath('/' + base), mod, preprocessor, processor);
            } catch( IOException e ) {
                SanLib.LOG.log(Level.ERROR, "Error loading FileSystem from jar: ", e);
                return false;
            }
        } else if( source.isDirectory() ) {
            return findFilesIntrn(source.toPath().resolve(base), mod, preprocessor, processor);
        }

        return false;
    }

    private static boolean findFilesIntrn(Path root, ModContainer mod, Function<Path, Boolean> preprocessor, BiFunction<Path, Path, Boolean> processor) {
        if( root == null || !Files.exists(root) ) {
            return false;
        }

        if( preprocessor != null && !MiscUtils.defIfNull(preprocessor.apply(root), false) ) {
            return false;
        }

        if( processor != null ) {
            Iterator<Path> itr;
            try {
                itr = Files.walk(root).iterator();
            } catch( IOException e ) {
                SanLib.LOG.log(Level.ERROR, String.format("Error iterating filesystem for: %s", mod.getModId()), e);
                return false;
            }

            while( itr.hasNext() ) {
                if( !MiscUtils.defIfNull(processor.apply(root, itr.next()), false) ) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean doesNbtContainOther(NBTTagCompound mainNBT, NBTTagCompound otherNBT) {
        return doesNbtContainOther(mainNBT, otherNBT, true);
    }

    public static boolean doesNbtContainOther(final NBTTagCompound mainNBT, final NBTTagCompound otherNBT, boolean strict) {
        return otherNBT == null
               || (mainNBT != null && otherNBT.getKeySet().stream().allMatch(key -> {
                        if( mainNBT.hasKey(key) ) {
                            if( strict ) {
                                return mainNBT.getTagId(key) == otherNBT.getTagId(key) && mainNBT.getTag(key).equals(otherNBT.getTag(key));
                            } else {
                                return compareNBTBase(mainNBT.getTag(key), otherNBT.getTag(key));
                            }
                        }

                        return false;
                    })
               );
    }

    private static boolean compareNBTBase(NBTBase main, NBTBase other) {
        if( main instanceof NBTPrimitive && other instanceof NBTPrimitive ) {
            NBTPrimitive mainBase = ((NBTPrimitive) main);
            NBTPrimitive otherBase = ((NBTPrimitive) other);

            long mainNb = isNbtDouble(mainBase) ? Double.doubleToLongBits(mainBase.getDouble()) : mainBase.getLong();
            long otherNb = isNbtDouble(otherBase) ? Double.doubleToLongBits(otherBase.getDouble()) : otherBase.getLong();

            return mainNb == otherNb;
        } else if( main instanceof NBTTagList && other instanceof NBTTagList ) {
            NBTTagList mainList = (NBTTagList) main;
            NBTTagList otherList = (NBTTagList) other.copy();

            if( mainList.getTagType() == otherList.getTagType() ) {
                for( int i = mainList.tagCount() - 1; i >= 0 && otherList.tagCount() > 0; i-- ) {
                    for( int j = otherList.tagCount() - 1; j >= 0; j-- ) {
                        if( compareNBTBase(mainList.get(i), otherList.get(j)) ) {
                            otherList.removeTag(j);
                            break;
                        }
                    }
                }

                return otherList.tagCount() == 0;
            }

            return false;
        } else if( main instanceof NBTTagCompound && other instanceof NBTTagCompound ) {
            return doesNbtContainOther((NBTTagCompound) main, (NBTTagCompound) other, false);
        } else {
            return main.equals(other);
        }
    }

    private static boolean isNbtDouble(NBTBase base) {
        return base instanceof NBTTagDouble || base instanceof NBTTagFloat;
    }
}
