/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sapmanpack.lib;

import java.io.Serializable;
import java.util.Arrays;

public abstract class Tuple
        implements Serializable
{
    private static final long serialVersionUID = 2093701238703979767L;

    private final Data<?>[] values;

    protected Tuple(Object... values) {
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

    private static class Data<T> {
//        final Class<V1> cls;
        final T value;

        Data(T value) {
            //noinspection unchecked
//            this.cls = (Class<V1>) ((ParameterizedType)value.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            this.value = value;
        }
    }

    public static final class Nullple
            extends Tuple
    {
        private static final long serialVersionUID = -4573970308065454465L;

        public Nullple() {
            super();
        }
    }

    public static final class Monuple<V1>
            extends Tuple
    {
        private static final long serialVersionUID = -8499722794710952513L;
        public final V1 val1;

        public Monuple(V1 val1) {
            super(val1);
            this.val1 = val1;
        }
    }

    public static final class Couple<V1, V2>
            extends Tuple
    {
        private static final long serialVersionUID = -2848182165133644998L;
        public final V1 val1;
        public final V2 val2;

        public Couple(V1 val1, V2 val2) {
            super(val1, val2);
            this.val1 = val1;
            this.val2 = val2;
        }
    }

    public static final class Triple<V1, V2, V3>
            extends Tuple
    {
        private static final long serialVersionUID = -2490176910031447069L;
        public final V1 val1;
        public final V2 val2;
        public final V3 val3;

        public Triple(V1 val1, V2 val2, V3 val3) {
            super(val1, val2, val3);
            this.val1 = val1;
            this.val2 = val2;
            this.val3 = val3;
        }
    }

    public static final class Quadruple<V1, V2, V3, V4>
            extends Tuple
    {
        private static final long serialVersionUID = 3485494179641670565L;
        public final V1 val1;
        public final V2 val2;
        public final V3 val3;
        public final V4 val4;

        public Quadruple(V1 val1, V2 val2, V3 val3, V4 val4) {
            super(val1, val2, val3, val4);
            this.val1 = val1;
            this.val2 = val2;
            this.val3 = val3;
            this.val4 = val4;
        }
    }
}
