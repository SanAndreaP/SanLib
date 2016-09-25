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

public class Tuple
        implements Serializable, Comparable<Tuple>, Iterable<Object>
{
    private static final long serialVersionUID = -287900116776462007L;

    private final Data<?>[] values;

    public Tuple(Object... values) {
        //noinspection Convert2MethodRef
        this.values = Arrays.stream(values).map(Data::new).toArray(size -> new Data<?>[size]);
    }

    public Object[] toArray() {
        return values.clone();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof Tuple && Arrays.equals(this.values, ((Tuple) obj).values);
    }

    public <T> T getValue(int index) {
        if( index < 0 || index >= this.values.length ) {
            throw new IllegalArgumentException(String.format("Cannot get tuple value! Index %d out of range", index));
        }

        return (T) this.values[index].value;
    }

    public <T> boolean checkValue(int index, Function<T, Boolean> checkMtd) {
        return checkMtd.apply(getValue(index));
    }

    public static Tuple readFromStream(InputStream stream) {
        try( ObjectDecoderInputStream odis = new ObjectDecoderInputStream(stream) ) {
            return (Tuple) odis.readObject();
        } catch( IOException | ClassNotFoundException ex ) {
            SanLib.LOG.log(Level.ERROR, "Cannot deserialize Tuple!", ex);
        }
        return null;
    }

    public static void writeToStream(Tuple tuple, OutputStream stream) {
        try( ObjectEncoderOutputStream oeos = new ObjectEncoderOutputStream(stream) ) {
            oeos.writeObject(tuple);
        } catch( IOException ex ) {
            SanLib.LOG.log(Level.ERROR, "Cannot serialize Tuple!", ex);
        }
    }

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
