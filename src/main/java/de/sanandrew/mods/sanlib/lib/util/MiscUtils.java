////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.XorShiftRandom;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Utility class for miscellaneous tasks and methods
 */
@SuppressWarnings({"unused"})
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
    @SuppressWarnings({"java:S135", "java:S3776"})
    public static Double calcFormula(final String str) {
        return new Object() {
            private int pos = -1;
            private int ch;

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
                                x = StrictMath.asin(Math.toRadians(x));
                                break;
                            case "cos":
                                x = StrictMath.cos(Math.toRadians(x));
                                break;
                            case "acos":
                                x = StrictMath.acos(Math.toRadians(x));
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
        return getTimeFromTicks(ticks, 2);
    }

    public static int hexToInt(String hex) {
        if( hex.startsWith("0x") ) {
            hex = hex.substring(2);
        } else if( hex.startsWith("#") ) {
            hex = hex.substring(1);
        }

        try {
            return (int) Long.parseLong(hex, 16);
        } catch( NumberFormatException ex ) {
            SanLib.LOG.log(Level.ERROR, String.format("cannot parse hexadecimal number string %s", hex), ex);
            return 0;
        }
    }

    public static String getTimeFromTicks(int ticks, int secondsPrecision) {
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
            DecimalFormat df = new DecimalFormat("#.#");
            df.setMaximumFractionDigits(secondsPrecision);
            sb.append(String.format("%ss", df.format(seconds)));
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
        char lastNum = nrStr.charAt(nrStr.length() - 1);
        switch( lastNum ) {
            case '1': return nrStr + "st";
            case '2': return nrStr + "nd";
            case '3': return nrStr + "rd";
            default: return nrStr + "th";
        }
    }

    /**
     * <p>This checks if <tt>val</tt> is between <tt>lo</tt> and <tt>hi</tt> (inclusive). If <tt>lo</tt> is greater than <tt>hi</tt>, both values switch around internally.</p>
     * <p>It acts like (<tt>lo <= val <= hi</tt>) or more verbose (<tt>lo <= val && val <= hi</tt>)</p>
     *
     * @param lo the lower value of the range
     * @param val the value that should be between <tt>lo</tt> and <tt>hi</tt>
     * @param hi the higher value of the range
     *
     * @return <tt>true</tt>, if <tt>val</tt> is between <tt>lo</tt> and <tt>hi</tt>, <tt>false</tt> otherwise
     */
    public static boolean between(float lo, float val, float hi) {
        return hi < lo ? hi <= val && val <= lo : lo <= val && val <= hi;
    }

    /**
     * <p>Checks if val is <tt>null</tt> and returns <tt>def</tt> if so, otherwise <tt>val</tt> is returned</p>
     *
     * @param val The value to be checked for null and eventually returned
     * @param def The default value when {@code val} is null
     * @param <T> The type of the value
     *
     * @return value, if it's not null, def otherwise
     */
    public static <T> T get(T val, T def) {
        return val != null ? val : def;
    }

    /**
     * Checks if val is <tt>null</tt> and returns the return value from <tt>def</tt> if so, otherwise <tt>val</tt> is returned.
     *
     * @param val The value to be checked for null and eventually returned
     * @param def The supplier function called when {@code val} is null
     * @param <T> The type of the value
     *
     * @return val, if it's not null, return value from def otherwise
     */
    public static <T> T get(T val, Supplier<T> def) {
        return val != null ? val : def.get();
    }

    /**
     * Calls the function defined in <tt>onNonNull</tt> if and only if <tt>obj</tt> is not <tt>null</tt>.
     *
     * @param obj The object the function <tt>onNonNull</tt> is called with as parameter
     * @param onNonNull The function called if <tt>obj</tt> is not <tt>null</tt>
     * @param <T> The type of <tt>obj</tt>
     */
    public static <T> void accept(T obj, Consumer<T> onNonNull) {
        if( obj != null ) {
            onNonNull.accept(obj);
        }
    }

    /**
     * Calls the function defined in <tt>onNonNull</tt> if and only if <tt>obj</tt> is not <tt>null</tt> and
     * returns the value returned by the function, <tt>null</tt> otherwise.
     *
     * @param obj The object the function <tt>onNonNull</tt> is called with as parameter
     * @param onNonNull The function called if <tt>obj</tt> is not <tt>null</tt>
     * @param <T> The type of <tt>obj</tt>
     * @param <R> The type of the return value
     */
    public static <T, R> R apply(T obj, Function<T, R> onNonNull) {
        return obj != null ? onNonNull.apply(obj) : null;
    }

    public static void findFiles(String dataFolder, IResourceManager resMgr, boolean idNoExtension, Predicate<String> fileNameFilter,
                                 FileProcessor processor)
            throws IOException
    {
        for( ResourceLocation rl : resMgr.listResources(dataFolder, fileNameFilter) ) {
            ResourceLocation conversionId = new ResourceLocation(rl.getNamespace(), getFilename(dataFolder, rl.getPath(), idNoExtension));
            for( IResource r : resMgr.getResources(rl) ) {
                try( InputStream is = r.getInputStream() ) {
                    processor.accept(conversionId, is);
                }
                IOUtils.closeQuietly(r);
            }
        }
    }

    private static String getFilename(String folder, String path, boolean noExt) {
        return path.substring(folder.length() + 1, path.length() - (noExt ? FilenameUtils.getExtension(path).length() : 0));
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
            ListNBT mainList = (ListNBT) main;
            ListNBT otherList = (ListNBT) other.copy();

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
        } else if( main instanceof CompoundNBT && other instanceof CompoundNBT ) {
            return doesNbtContainOther((CompoundNBT) main, (CompoundNBT) other, false);
        } else {
            return main.equals(other);
        }
    }

    private static boolean isNbtDouble(INBT base) {
        return base instanceof DoubleNBT || base instanceof FloatNBT;
    }

    public static float wrap360(float angle) {
        if( angle >= 360.0F ) {
            return wrap360(angle - 360.0F);
        }

        return angle < 0 ? wrap360(angle + 360.0F) : angle;
    }

    public static <T, R> R apply(T nullableObj, Function<T, R> onNonNull, R defReturn) {
        if( nullableObj != null ) {
            return onNonNull.apply(nullableObj);
        }

        return defReturn;
    }

    public static Integer getInteger(String s) {
        try {
            s = s.startsWith("#") ? s.substring(1) : s;
            s = s.startsWith("0x") ? s : "0x" + s;

            long l = Long.decode(s);

            return (int) (l & 0xFFFFFFFFL);
        } catch( NumberFormatException ex ) {
            return null;
        }
    }

    public static NumberFormat getNumberFormat(int numFract, boolean grouping, String langCode) {
        NumberFormat nf;

        if( numFract == 0 ) {
            nf = NumberFormat.getIntegerInstance(Locale.forLanguageTag(langCode));
        } else {
            nf = NumberFormat.getNumberInstance(Locale.forLanguageTag(langCode));
            nf.setMaximumFractionDigits(numFract);
            nf.setMinimumFractionDigits(numFract);
        }

        nf.setGroupingUsed(grouping);

        return nf;
    }

    private enum SiPrefixes {
        YOTTA("Y", 24),
        ZETTA("Z", 21),
        EXA  ("E", 18),
        PETA ("P", 15),
        TERA ("T", 12),
        GIGA ("G", 9),
        MEGA ("M", 6),
        KILO ("k", 3),
        NONE ("", 0),
        MILLI("m", -3),
        MICRO("μ", -6),
        NANO ("n", -9),
        PICO ("p", -12),
        FEMTO("f", -15),
        ATTO ("a", -18),
        ZEPTO("z", -21),
        YOCTO("y", -24);

        public final int exp;
        public final String prefix;

        private static final SiPrefixes[] PB_VALUES = values();

        SiPrefixes(String prefix, int exp) {
            this.prefix = prefix;
            this.exp = exp;
        }
    }

    public static String getNumberSiPrefixed(double number, int precision, String langCode) {
        for( SiPrefixes prefix : SiPrefixes.PB_VALUES ) {
            double scaledNum = number / Math.pow(10, prefix.exp);
            if( scaledNum >= 1.0 ) {
                return getNumberFormat(precision, false, langCode).format(scaledNum) + ' ' + prefix.prefix;
            }
        }

        return getNumberFormat(precision, false, langCode).format(number) + ' ';
    }

    /**
     * @deprecated I don't know where this is used now, it was used before the MC recipe refactoring...
     */
    @Deprecated
    public static ResourceLocation getPathedRL(String domain, Path root, Path file) {
        Path filePath = Paths.get(FilenameUtils.getPathNoEndSeparator(root.relativize(file).toString()),
                                  FilenameUtils.removeExtension(file.getFileName().toString()));
        return new ResourceLocation(domain, FilenameUtils.separatorsToUnix(filePath.toString()));
    }

    @FunctionalInterface
    public interface FileProcessor
    {
        void accept(ResourceLocation rl, InputStream is) throws IOException;
    }
}
