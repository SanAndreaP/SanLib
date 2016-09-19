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
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelJsonLoader<T extends ModelBase & ModelJsonHandler<T, U>, U extends ModelJsonLoader.ModelJson>
        implements IResourceManagerReloadListener
{
    private T modelBase;
    private ModelRenderer[] mainBoxes;
    private Map<String, ModelRenderer> nameToBoxList;
    private Map<ModelRenderer, String> boxToNameList;
    private ResourceLocation resLoc;
    private String[] mandatNames;
    private boolean loaded;
    private Class<U> jsonClass;
    private U jsonInst;

    public static <T extends ModelBase & ModelJsonHandler<T, ModelJson>> ModelJsonLoader<T, ModelJson>
            create(T modelBase, ResourceLocation location, String... mandatoryNames)
    {
        return new ModelJsonLoader<>(modelBase, ModelJson.class, location, mandatoryNames);
    }

    public static <T extends ModelBase & ModelJsonHandler<T, U>, U extends ModelJsonLoader.ModelJson> ModelJsonLoader<T, U>
            create(T modelBase, Class<U> jsonClass, ResourceLocation location, String... mandatoryNames)
    {
        return new ModelJsonLoader<>(modelBase, jsonClass, location, mandatoryNames);
    }

    private ModelJsonLoader(T modelBase, Class<U> jsonClass, ResourceLocation location, String[] mandatoryNames) {
        this.modelBase = modelBase;
        this.mainBoxes = new ModelRenderer[0];
        this.nameToBoxList = new HashMap<>();
        this.boxToNameList = new HashMap<>();
        this.mandatNames = mandatoryNames.clone();
        this.resLoc = location;
        this.loaded = false;
        this.jsonClass = jsonClass;

        if( Minecraft.getMinecraft().getResourceManager() instanceof SimpleReloadableResourceManager ) {
            ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
        }
    }

    public void unregister() {
        if( Minecraft.getMinecraft().getResourceManager() instanceof SimpleReloadableResourceManager ) {
            ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).reloadListeners.remove(this);
        }
    }
    
    public void load() {
        this.mainBoxes = new ModelRenderer[0];
        this.nameToBoxList.clear();
        this.boxToNameList.clear();

        try( IResource res = Minecraft.getMinecraft().getResourceManager().getResource(this.resLoc);
             BufferedReader in = new BufferedReader(new InputStreamReader(res.getInputStream())) )
        {
            this.jsonInst = new Gson().fromJson(in, this.jsonClass);
            List<ChildCube> children = new ArrayList<>();
            List<ModelRenderer> mainBoxesList = new ArrayList<>();
            Map<String, Boolean> mandatoryChecklist = new HashMap<>();
            Arrays.asList(this.mandatNames).forEach((name) -> mandatoryChecklist.put(name, false));

            for( Cube cb : this.jsonInst.cubes ) {
                float baseScale = this.modelBase.getBaseScale();
                Double scaling;
                if( cb.scaling != null ) {
                    scaling = calcFormula(cb.scaling.replace("x", Float.toString(baseScale)));
                    if( scaling == null ) {
                        scaling = (double) baseScale;
                    }
                } else {
                    scaling = (double) baseScale;
                }

                ModelRenderer box = ModelBoxBuilder.newBuilder(this.modelBase, cb.boxName)
                                                   .setTexture(cb.textureX, cb.textureY, cb.mirror, cb.textureWidth, cb.textureHeight)
                                                   .setLocation(cb.rotationPointX, cb.rotationPointY, cb.rotationPointZ)
                                                   .setRotation(cb.rotateAngleX, cb.rotateAngleY, cb.rotateAngleZ)
                                                   .getBox(cb.offsetX, cb.offsetY, cb.offsetZ, cb.sizeX, cb.sizeY, cb.sizeZ, scaling.floatValue());
                box.isHidden = cb.isHidden;

                this.nameToBoxList.put(cb.boxName, box);
                this.boxToNameList.put(box, cb.boxName);

                if( cb.parentBox != null && !cb.parentBox.isEmpty() ) {
                    children.add(new ChildCube(box, cb.parentBox));
                } else {
                    mainBoxesList.add(box);
                }

                mandatoryChecklist.put(cb.boxName, true);
            }

            if( mandatoryChecklist.containsValue(false) ) {
                SanPlayerModel.LOG.printf(Level.WARN, "Model %s has not all mandatory boxes! Missing %s", this.resLoc.toString(),
                                          String.join(", ", mandatoryChecklist.keySet().stream().filter((name) -> !mandatoryChecklist.get(name)).collect(Collectors.toList())));
                this.nameToBoxList.clear();
                this.boxToNameList.clear();
                this.mainBoxes = new ModelRenderer[0];
            }

            children.forEach((child) -> {
                if( this.nameToBoxList.containsKey(child.parentName) ) {
                    this.nameToBoxList.get(child.parentName).addChild(child.box);
                } else {
                    this.modelBase.boxList.stream().filter((box) -> box.boxName != null && box.boxName.equals(child.parentName)).forEach((box) -> box.addChild(child.box));
                }
            });

            this.mainBoxes = mainBoxesList.toArray(new ModelRenderer[mainBoxesList.size()]);

            this.modelBase.setTexture(this.jsonInst.texture);

            this.loaded = true;
        } catch( IOException ex) {
            SanPlayerModel.LOG.log(Level.INFO, "Can't load model location %s!", this.resLoc.toString());
            this.nameToBoxList.clear();
            this.boxToNameList.clear();
            this.mainBoxes = new ModelRenderer[0];
            this.jsonInst = null;
            this.loaded = false;
        } catch( JsonSyntaxException | JsonIOException ex ) {
            SanPlayerModel.LOG.log(Level.WARN, String.format("Can't load model location %s!", this.resLoc.toString()), ex);
            this.nameToBoxList.clear();
            this.boxToNameList.clear();
            this.mainBoxes = new ModelRenderer[0];
            this.jsonInst = null;
            this.loaded = false;
        }
    }

    public ModelRenderer getBox(String name) {
        return this.nameToBoxList.get(name);
    }

    public String getName(ModelRenderer box) {
        return this.boxToNameList.get(box);
    }

    public ModelRenderer[] getMainBoxes() {
        return this.mainBoxes;
    }

    public U getModelJsonInstance() {
        return this.jsonInst;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.modelBase.onReload(resourceManager, this);
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    /**
     * Code from http://stackoverflow.com/a/26227947
     * with minor changes
     */
    public static Double calcFormula(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                this.ch = ++this.pos < str.length() ? str.charAt(this.pos) : -1;
            }

            boolean eat(int charToEat) {
                while( ch == ' ' ) {
                    this.nextChar();
                }

                if( ch == charToEat ) {
                    this.nextChar();
                    return true;
                }

                return false;
            }

            Double parse() {
                this.nextChar();
                double x = parseExpression();

                if( this.pos < str.length() ) {
                    return null;
                }

                return x;
            }

            double parseExpression() {
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

            double parseTerm() {
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

            Double parseFactor() {
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
                                x = Math.sin(Math.toRadians(x));
                                break;
                            case "cos":
                                x = Math.cos(Math.toRadians(x));
                                break;
                            case "tan":
                                x = Math.tan(Math.toRadians(x));
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
                        x = Math.pow(x, y); // exponentiation
                    }

                    return x * sign;
                }
            }
        }.parse();
    }

    public static class ModelJson
    {
        public String texture;
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
        public String boxName;
        public int sizeX;
        public int sizeY;
        public int sizeZ;
        public int textureX;
        public int textureY;
        public float textureWidth = 64.0F;
        public float textureHeight = 32.0F;
        public boolean mirror = false;
        public float rotationPointX = 0.0F;
        public float rotationPointY = 0.0F;
        public float rotationPointZ = 0.0F;
        public float rotateAngleX = 0.0F;
        public float rotateAngleY = 0.0F;
        public float rotateAngleZ = 0.0F;
        public boolean isHidden = false;
        public float offsetX;
        public float offsetY;
        public float offsetZ;
        public String parentBox;
        public String scaling;
    }
}
