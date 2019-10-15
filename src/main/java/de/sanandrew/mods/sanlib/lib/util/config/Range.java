/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Range
{
    int minI() default Integer.MIN_VALUE;

    int maxI() default Integer.MAX_VALUE;

    long minL() default Long.MIN_VALUE;

    long maxL() default Long.MAX_VALUE;

    double minD() default -Double.MAX_VALUE;

    double maxD() default Double.MAX_VALUE;

    boolean listFixed() default false;

    int maxListLength() default -1;

    Pattern validationPattern() default @Pattern;
}
