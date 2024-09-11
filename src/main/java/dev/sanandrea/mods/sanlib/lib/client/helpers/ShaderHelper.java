/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.helpers;

import dev.sanandrea.mods.sanlib.SanLib;
import dev.sanandrea.mods.sanlib.SanLibConfig;
import dev.sanandrea.mods.sanlib.client.event.ClientTickEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.function.IntConsumer;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public final class ShaderHelper
{
    private ShaderHelper() {}

    public static void useShader(int shader, IntConsumer callback) {
        if( !areShadersEnabled() ) {
            return;
        }

        ARBShaderObjects.glUseProgramObjectARB(shader);

        if( shader != 0 ) {
            int time = ARBShaderObjects.glGetUniformLocationARB(shader, "time");
            ARBShaderObjects.glUniform1iARB(time, (int)(ClientTickEventHandler.getClientTicks() % (long) Integer.MAX_VALUE));

            if( callback != null ) {
                callback.accept(shader);
            }
        }
    }

    public static void useShader(int shader) {
        useShader(shader, null);
    }

    public static void releaseShader() {
        useShader(0);
    }

    public static boolean areShadersEnabled() {
        return SanLibConfig.useShaders;
    }

    public static int getSecondaryTextureUnit() {
        return SanLibConfig.glSecondaryTextureUnit;
    }

    // Most of the code taken from the LWJGL wiki
    // http://lwjgl.org/wiki/index.php?title=GLSL_Shaders_with_LWJGL
    public static int createProgram(ResourceLocation vert, ResourceLocation frag) {
        int vertId = 0;
        int fragId = 0;
        int program;
        if( vert != null ) {
            vertId = createShader(vert, ARBVertexShader.GL_VERTEX_SHADER_ARB);
        }
        if( frag != null ) {
            fragId = createShader(frag, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
        }

        program = ARBShaderObjects.glCreateProgramObjectARB();
        if( program == 0 ) {
            return 0;
        }

        if( vert != null ) {
            ARBShaderObjects.glAttachObjectARB(program, vertId);
        }
        if( frag != null ) {
            ARBShaderObjects.glAttachObjectARB(program, fragId);
        }

        ARBShaderObjects.glLinkProgramARB(program);
        if( ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE ) {
            SanLib.LOG.log(Level.ERROR, () -> getLogInfo(program));
            return 0;
        }

        ARBShaderObjects.glValidateProgramARB(program);
        if( ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE ) {
            SanLib.LOG.log(Level.ERROR, () ->getLogInfo(program));
            return 0;
        }

        return program;
    }

    private static int createShader(ResourceLocation file, int shaderType){
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

            if( shader == 0 ) {
                return 0;
            }

            ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(file));
            ARBShaderObjects.glCompileShaderARB(shader);

            if( ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE ) {
                throw new ShaderCreationException("Error creating shader: " + getLogInfo(shader));
            }

            return shader;
        } catch( IOException | NullPointerException e ) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            SanLib.LOG.log(Level.ERROR, "Cannot create Shader!", e);
            return -1;
        }
    }

    private static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    private static String readFileAsString(ResourceLocation file) throws IOException {
        StringBuilder source = new StringBuilder();
        Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(file);
        if( res.isEmpty() ) {
            return "";
        }

        try( BufferedReader reader = res.get().openAsReader() ) {
            String line;
            while( (line = reader.readLine()) != null ) {
                source.append(line).append('\n');
            }
        } catch( NullPointerException ex ) {
            return "";
        }

        return source.toString();
    }

    public static class ShaderCreationException
            extends RuntimeException
    {
        public ShaderCreationException(String message) {
            super(message);
        }
    }
}
