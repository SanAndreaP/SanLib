/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib;

import de.sanandrew.mods.sanlib.SanLib;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

/**
 * A class that resembles a one-class-fits-all approach to tuples for Java using generics.
 */
public class Tuple
        implements Serializable, Comparable<Tuple>, Iterable<Object>
{
    private static final long serialVersionUID = -287900116776462007L;

    private final Data<?>[] values;

    /**
     * Creates a new Tuple with a set of defined values.
     * @param values The values stored in this new Tuple.
     */
    public Tuple(Object... values) {
        //noinspection Convert2MethodRef
        this.values = Arrays.stream(values).map(Data::new).toArray(size -> new Data<?>[size]);
    }

    /**
     * Converts this Tuple into an Object array.
     * @return An Object array containing all values in this Tuple.
     */
    public Object[] toArray() {
        return values.clone();
    }

    /**
     * Indicates if another object is equal to this one. It is if:
     * <ul>
     *     <li>the other object is the same reference as this one (==), or</li>
     *     <li>the other object is an instance of Tuple and its values equal to this Tuples values via {@link Arrays#equals(Object[], Object[])}</li>
     * </ul>
     * @param obj The object to be checked
     * @return {@code true}, if the other object is equal, {@link false} otherwise
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof Tuple && Arrays.equals(this.values, ((Tuple) obj).values);
    }

    /**
     * Retrieves one value from this Tuple at the specified location.
     * @param index The location of the desired value.
     * @param <T> The expected type of the value returned.
     * @return The value at the index.
     * @throws IllegalArgumentException if the index is not {@code >= 0} or not {@code < length}
     */
    public <T> T getValue(int index) {
        if( index < 0 || index >= this.values.length ) {
            throw new IllegalArgumentException(String.format("Cannot get tuple value! Index %d out of range", index));
        }

        return (T) this.values[index].value;
    }

    /**
     * Uses the specified function to check a value from this Tuple at the specified location.
     * This is essentially a wrapper method for {@code checkMtd.apply(tupleImpl.getValue(index))}.
     * @param index The location of the value checked.
     * @param checkMtd The function reference to check for the value.
     * @param <T> The type of the value checked.
     * @return true if the function also returns true (when the value is valid), false otherwise
     */
    public <T> boolean checkValue(int index, Function<T, Boolean> checkMtd) {
        return checkMtd.apply(getValue(index));
    }

    /**
     * Deserializes a Tuple read from an InputStream via an {@link ObjectDecoderInputStream}.
     * @param stream The InputStream a Tuple is in.
     * @return a Tuple, if one is present and can be read, {@code null} otherwise
     */
    public static Tuple readFromStream(InputStream stream) {
        try( ObjectDecoderInputStream odis = new ObjectDecoderInputStream(stream) ) {
            return (Tuple) odis.readObject();
        } catch( IOException | ClassNotFoundException ex ) {
            SanLib.LOG.log(Level.ERROR, "Cannot deserialize Tuple!", ex);
        }
        return null;
    }

    /**
     * Serializes this Tuple and writes it into an OutputStream via an {@link ObjectEncoderOutputStream}.
     * @param stream The OutputStream this Tuple should be written in.
     */
    public void writeToStream(OutputStream stream) {
        try( ObjectEncoderOutputStream oeos = new ObjectEncoderOutputStream(stream) ) {
            oeos.writeObject(this);
        } catch( IOException ex ) {
            SanLib.LOG.log(Level.ERROR, "Cannot serialize Tuple!", ex);
        }
    }

    /**
     * Compares this Tuple with another Tuple for order.
     * @param o The Tuple to be compared
     * @return a negative integer, zero, or a positive integer as this Tuple
     *         is less than, equal to, or greater than the specified Tuple.
     * @see Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(Tuple o) {
        final int tLen = this.values.length;
        final int oLen = o.values.length;

        for( int i = 0; i < tLen && i < oLen; i++ ) {
            final int comparison = this.values[i].compareTo(o.values[i]);

            if( comparison != 0 ) {
                return comparison;
            }
        }

        return (Integer.valueOf(tLen)).compareTo(oLen);
    }

    /**
     * Returns an iterator over elements of the values from this Tuple.
     * @return an Iterator
     * @see Iterable#iterator()
     */
    @Override
    public Iterator<Object> iterator() {
        return Arrays.stream(this.values).map(data -> (Object)data.value).iterator();
    }

    private static class Data<T>
            implements Serializable, Comparable<Data<?>>
    {
        private static final long serialVersionUID = -7494878631121169815L;

        private final T value;

        private Data(T value) {
            this.value = value;
        }

        @Override
        @SuppressWarnings("unchecked")
        public int compareTo(Data<?> o) {
            if( this.value instanceof Comparable && o.value instanceof Comparable ) {
                return ((Comparable) this.value).compareTo(o.value);
            } else {
                return -1;
            }
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Data && (this.value == null && ((Data) obj).value == null || (this.value != null && this.value.equals(((Data) obj).value)));
        }
    }
}
