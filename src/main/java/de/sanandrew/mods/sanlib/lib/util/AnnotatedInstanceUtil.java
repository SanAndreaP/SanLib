/*
 * This class is from JEI (JustEnoughItems -> https://github.com/mezz/JustEnoughItems), modified by SanAndreasP.
 * Original License for this code is below:
 *
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 mezz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.sanandrew.mods.sanlib.lib.util;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnnotatedInstanceUtil
{
    @SuppressWarnings("SameParameterValue")
    public static <T> List<T> getInstances(Class annotationClass, Class<T> instanceClass, Logger logger) {
        Type annotationType = Type.getType(annotationClass);
        List<ModFileScanData> allScanData = ModList.get().getAllScanData();
        List<String> pluginClassNames = new ArrayList<>();
        for( ModFileScanData scanData : allScanData ) {
            List<ModFileScanData.AnnotationData> annotations = scanData.getAnnotations();
            for( ModFileScanData.AnnotationData a : annotations ) {
                if( Objects.equals(a.getAnnotationType(), annotationType) ) {
                    String memberName = a.getMemberName();
                    pluginClassNames.add(memberName);
                }
            }
        }
        List<T> instances = new ArrayList<>();
        for( String className : pluginClassNames ) {
            try {
                Class<?> asmClass = Class.forName(className);
                Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
                T instance = asmInstanceClass.newInstance();
                instances.add(instance);
            } catch( ClassNotFoundException | InstantiationException | IllegalAccessException | LinkageError e ) {
                logger.error("Failed to load: {}", className, e);
            }
        }
        return instances;
    }
}
