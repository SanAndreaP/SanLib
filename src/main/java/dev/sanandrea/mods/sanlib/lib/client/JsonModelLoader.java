/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import dev.sanandrea.mods.sanlib.SanLib;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class loads a JSON file as a model to be used with entities and TESRs.<br>
 * It loads it from a ResourceLocation, builds the ModelPart boxes and adds them to the ModelBase cube list.<br>
 * If boxes in the JSON have a parent name, it is added to the children list of a box with the same name from either the JSON itself
 * or from the ModelBase box list.<br>
 * If the JSON itself references a parent JSON, then the parent JSON is loaded first, and then the current one.<br>
 * If defined, this will check if all mandatory box names are loaded.
 */
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public final class JsonModelLoader
{
    public static LayerDefinition load(ResourceLocation modelLocation, String... mandatoryParts) {
        return load(modelLocation, 0.0F, mandatoryParts);
    }

    public static LayerDefinition load(ResourceLocation modelLocation, float scale, String... mandatoryParts) {
        try {
            Map<String, Boolean> mandatoryChecklist = new HashMap<>(Arrays.stream(mandatoryParts).collect(Collectors.toMap(p->p, p->false)));
            return loadJson(modelLocation, true, scale, mandatoryChecklist, new HashMap<>(), new ArrayList<>());
        } catch( IOException | JsonSyntaxException | JsonIOException ex ) {
            SanLib.LOG.log(Level.WARN, String.format("Can't load model location %s!", modelLocation.toString()), ex);
        }

        return null;
    }

    private static LayerDefinition loadJson(ResourceLocation modelLocation, boolean isMain, float scale, Map<String, Boolean> mandatoryChecklist, Map<String, List<CubeContainer>> children, List<CubeContainer> mainBoxesList)
            throws IOException, JsonIOException, JsonSyntaxException
    {
        Resource res = Minecraft.getInstance().getResourceManager().getResourceOrThrow(modelLocation);
        try( BufferedReader in = new BufferedReader(new InputStreamReader(res.open())) ) {
            JsonBase json = new Gson().fromJson(in, JsonBase.class);
            if( json.parent != null && !json.parent.isEmpty() ) {
                loadJson(ResourceLocation.parse(json.parent), false, scale, mandatoryChecklist, children, mainBoxesList);
            }

            if( json.cubes != null ) {
                for( Cube cb : json.cubes ) {
                    loadCube(cb, scale, mandatoryChecklist, children, mainBoxesList);
                }
            }

            if( isMain ) {
                if( mandatoryChecklist.containsValue(false) ) {
                    SanLib.LOG.printf(Level.WARN, "Model %s has not all mandatory boxes! Missing %s", modelLocation.toString(),
                                      mandatoryChecklist.keySet().stream().filter(name -> !mandatoryChecklist.get(name)).collect(Collectors.joining(", ")));

                    throw new IOException();
                }

                MeshDefinition mesh = new MeshDefinition();
                PartDefinition meshRoot = mesh.getRoot();

                mainBoxesList.forEach(box -> {
                    PartDefinition boxPart = meshRoot.addOrReplaceChild(box.name, box.builder, box.pose);
                    makeChildParts(children, box.name, boxPart);
                });

                return LayerDefinition.create(mesh, json.textureWidth, json.textureHeight);
            }
        }

        return null;
    }

    private static void loadCube(Cube cb, float scale, Map<String, Boolean> mandatoryChecklist, Map<String, List<CubeContainer>> children, List<CubeContainer> mainBoxesList) {
        Double scaling = (double) scale;
        if( cb.scaling != null ) {
            scaling = MiscUtils.calcFormula(cb.scaling.replace("x", Float.toString(scale)));
            if( scaling == null ) {
                scaling = 0.0D;
            }
        }

        CubeListBuilder builder = CubeListBuilder.create()
                                                 .texOffs(cb.textureX, cb.textureY)
                                                 .mirror(cb.mirror)
                                                 .addBox(-cb.rotationPointX, -cb.rotationPointY, -cb.rotationPointZ, cb.sizeX, cb.sizeY, cb.sizeZ, new CubeDeformation(scaling.floatValue()));

        PartPose pose = PartPose.offsetAndRotation(cb.offsetX, cb.offsetY, cb.offsetZ, cb.rotateAngleX, cb.rotateAngleY, cb.rotateAngleZ);
        CubeContainer container = new CubeContainer(builder, cb.boxName, pose);
        if( cb.parentBox != null && !cb.parentBox.isEmpty() ) {
            children.computeIfAbsent(cb.parentBox, k -> new ArrayList<>()).add(container);
        } else {
            mainBoxesList.add(container);
        }

        mandatoryChecklist.put(cb.boxName, true);
    }

    private static void makeChildParts(Map<String, List<CubeContainer>> children, String parentName, final PartDefinition parentPart) {
        if( children.containsKey(parentName) ) {
            children.get(parentName).forEach(c -> {
                PartDefinition childPart = parentPart.addOrReplaceChild(c.name, c.builder, c.pose);
                makeChildParts(children, c.name, childPart);
            });
        }
    }

    @SuppressWarnings({"WeakerAccess", "java:S1104"})
    private static class JsonBase
    {
        public String parent;
        public Cube[] cubes;
        public int     textureWidth   = 64;
        public int     textureHeight  = 32;
    }

    private record CubeContainer(CubeListBuilder builder, String name, PartPose pose) { }

    @SuppressWarnings({"WeakerAccess", "java:S1104"})
    private static class Cube
    {
        public String  boxName;
        public int     sizeX;
        public int     sizeY;
        public int     sizeZ;
        public int     textureX;
        public int     textureY;
        public boolean mirror         = false;
        public float   rotationPointX = 0.0F;
        public float   rotationPointY = 0.0F;
        public float   rotationPointZ = 0.0F;
        public float   rotateAngleX   = 0.0F;
        public float   rotateAngleY   = 0.0F;
        public float   rotateAngleZ   = 0.0F;
        public float   offsetX;
        public float   offsetY;
        public float   offsetZ;
        public String  parentBox;
        public String  scaling;
    }
}
