/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.sapmanpack.lib.client;

import com.google.gson.Gson;
import de.sanandrew.mods.sapmanpack.sanplayermodel.SanPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModelJsonLoader
        implements IResourceManagerReloadListener
{
    private IResourceManagerReloadListener modelReloader;
    private Map<String, ModelRenderer> mainBoxes;
    private Map<String, ModelRenderer> boxes;

    private ModelJsonLoader(IResourceManagerReloadListener modelReloader) {
        this.modelReloader = modelReloader;
        this.mainBoxes = new HashMap<>();
        this.boxes = new HashMap<>();
    }
    
    public static <T extends ModelBase & IResourceManagerReloadListener> ModelJsonLoader load(T base, ResourceLocation location) {
        ModelJsonLoader inst = new ModelJsonLoader(base);

        try( IResource res = Minecraft.getMinecraft().getResourceManager().getResource(location);
             BufferedReader in = new BufferedReader(new InputStreamReader(res.getInputStream())) )
        {
            ModelJson model = new Gson().fromJson(in, ModelJson.class);
            List<ChildCube> children = new ArrayList<>();

            for( Cube cb : model.cubes ) {
                ModelRenderer box = ModelBoxBuilder.newBuilder(base)
                                                   .setTexture(cb.textureX, cb.textureY, cb.mirror, cb.textureWidth, cb.textureHeight)
                                                   .setLocation(cb.rotationPointX, cb.rotationPointY, cb.rotationPointZ)
                                                   .setRotation(cb.rotateAngleX, cb.rotateAngleY, cb.rotateAngleZ)
                                                   .getBox(cb.offsetX, cb.offsetY, cb.offsetZ, cb.sizeX, cb.sizeY, cb.sizeZ, cb.scaling);
                box.isHidden = cb.isHidden;

                inst.boxes.put(cb.boxName, box);

                if( cb.parentBox != null && !cb.parentBox.isEmpty() ) {
                    children.add(new ChildCube(box, cb.parentBox));
                } else {
                    inst.mainBoxes.put(box.boxName, box);
                }
            }

            for( ChildCube child : children ) {
                inst.boxes.get(child.parentName).addChild(child.box);
            }

            if( Minecraft.getMinecraft().getResourceManager() instanceof SimpleReloadableResourceManager ) {
                ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(inst);
            }

            return inst;
        } catch( IOException ex ) {
            SanPlayerModel.LOG.printf(Level.WARN, "Can't load model location %s!", location.toString());
            return null;
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.modelReloader.onResourceManagerReload(resourceManager);
    }

    private static class ModelJson
    {
        public Cube[] cubes;
    }

    private static class ChildCube
    {
        public final ModelRenderer box;
        public final String parentName;

        protected ChildCube(ModelRenderer box, String parentName) {
            this.box = box;
            this.parentName = parentName;
        }
    }

    private static class Cube
    {
        public int sizeX;
        public int sizeY;
        public int sizeZ;
        public int textureX;
        public int textureY;
        public boolean mirror;
        /** The size of the texture file's width in pixels. */
        public float textureWidth;
        /** The size of the texture file's height in pixels. */
        public float textureHeight;
        public float rotationPointX;
        public float rotationPointY;
        public float rotationPointZ;
        public float rotateAngleX;
        public float rotateAngleY;
        public float rotateAngleZ;
        public boolean isHidden;
        public String boxName;
        public float offsetX;
        public float offsetY;
        public float offsetZ;
        public String parentBox;
        public float scaling;
    }
}
