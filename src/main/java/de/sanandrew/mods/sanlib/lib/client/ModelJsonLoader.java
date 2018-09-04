/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.sanplayermodel.SanPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.minecraftforge.client.resource.VanillaResourceType.TEXTURES;

/**
 * This class loads a JSON file as a model to be used with entities and TESRs.<br>
 * It loads it from a ResourceLocation, builds the ModelRenderer boxes and adds them to the ModelBase cube list.<br>
 * If boxes in the JSON have a parent name, it is added to the children list of a box with the same name from either the JSON itself
 * or from the ModelBase box list.<br>
 * If the JSON itself references a parent JSON, then the parent JSON is loaded first, and then the current one.<br>
 * If defined, this will check if all mandatory box names are loaded.
 * @param <T> The type of the ModelBase class loading and handling the boxes/cubes.
 * @param <U> The type of a ModelJson.
 */
@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ModelJsonLoader<T extends ModelBase & ModelJsonHandler<T, U>, U extends ModelJsonLoader.ModelJson>
        implements ISelectiveResourceReloadListener, IResourceType
{
    private T modelBase;
    private ModelRenderer[] mainBoxes;
    private Map<String, ModelRenderer> nameToBoxList;
    private Map<ModelRenderer, String> boxToNameList;
    private Map<String, Class<? extends ModelRenderer>> cstBoxRenderer;
    private ResourceLocation resLoc;
    private String[] mandatNames;
    private boolean loaded;
    private Class<U> jsonClass;
    private U jsonInst;

    public static final Queue<ModelJsonLoader<?, ?>> REGISTERED_JSON_LOADERS = new ConcurrentLinkedQueue<>();

    /**
     * Creates a new instance of the loader. This uses the default {@link ModelJson} class to parse the JSON values.
     * @param modelBase The base model class
     * @param location The location of the model
     * @param mandatoryNames An optional list of names that are required to exist in the model
     * @param <T> the type of the base model class
     * @return a new instance of the JSON model loader.
     */
    public static <T extends ModelBase & ModelJsonHandler<T, ModelJson>> ModelJsonLoader<T, ModelJson>
            create(T modelBase, ResourceLocation location, String... mandatoryNames)
    {
        return new ModelJsonLoader<>(modelBase, ModelJson.class, location, mandatoryNames);
    }

    /**
     * Creates a new instance of the loader.
     * @param modelBase The base model class
     * @param jsonClass A custom class to be used by the loader to parse the JSON
     * @param location The location of the model
     * @param mandatoryNames An optional list of names that are required to exist in the model
     * @param <T> the type of the base model class
     * @param <U> the type of the custom parsed class
     * @return a new instance of the JSON model loader.
     */
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
        this.cstBoxRenderer = new HashMap<>();
        this.mandatNames = mandatoryNames.clone();
        this.resLoc = location;
        this.loaded = false;
        this.jsonClass = jsonClass;

        if( Minecraft.getMinecraft().getResourceManager() instanceof SimpleReloadableResourceManager ) {
            ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
        }

        REGISTERED_JSON_LOADERS.add(this);
    }

    public void addCustomModelRenderer(String boxName, Class<? extends ModelRenderer> rendererClass) {
        this.cstBoxRenderer.put("boxName", rendererClass);
    }

    /**
     * Removes this loader from the Resource Manager reload listener list.
     */
    public void unregister() {
        if( Minecraft.getMinecraft().getResourceManager() instanceof SimpleReloadableResourceManager ) {
            ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).reloadListeners.remove(this);
        }

        REGISTERED_JSON_LOADERS.remove(this);
    }

    private void loadJson(ResourceLocation resource, Map<String, Boolean> mandatoryChecklist)
            throws IOException, JsonIOException, JsonSyntaxException
    {
        this.loadJson(resource, true, mandatoryChecklist, new HashMap<>(), new HashMap<>());
    }

    private void loadJson(ResourceLocation resource, boolean isMain, Map<String, Boolean> mandatoryChecklist, Map<String, ChildCube> children, Map<String, ModelRenderer> mainBoxesList)
            throws IOException, JsonIOException, JsonSyntaxException
    {
        try( IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
             BufferedReader in = new BufferedReader(new InputStreamReader(res.getInputStream())) )
        {
            U json = new Gson().fromJson(in, this.jsonClass);
            if( json.parent != null && !json.parent.isEmpty() ) {
                this.loadJson(new ResourceLocation(json.parent), false, mandatoryChecklist, children, mainBoxesList);
            }

            if( json.cubes != null ) {
                for( Cube cb : json.cubes ) {
                    float baseScale = this.modelBase.getBaseScale();
                    Double scaling;
                    if( cb.scaling != null ) {
                        scaling = MiscUtils.calcFormula(cb.scaling.replace("x", Float.toString(baseScale)));
                        if( scaling == null ) {
                            scaling = (double) baseScale;
                        }
                    } else {
                        scaling = (double) baseScale;
                    }

                    Class<? extends ModelRenderer> mrCls = MiscUtils.defIfNull(this.cstBoxRenderer.get(cb.boxName), ModelRenderer.class);
                    ModelRenderer box = ModelBoxBuilder.newBuilder(this.modelBase, cb.boxName, mrCls)
                            .setTexture(cb.textureX, cb.textureY, cb.mirror, cb.textureWidth, cb.textureHeight)
                            .setLocation(cb.rotationPointX, cb.rotationPointY, cb.rotationPointZ)
                            .setRotation(cb.rotateAngleX, cb.rotateAngleY, cb.rotateAngleZ)
                            .getBox(cb.offsetX, cb.offsetY, cb.offsetZ, cb.sizeX, cb.sizeY, cb.sizeZ, scaling.floatValue());
                    box.isHidden = cb.isHidden;

                    if( cb.parentBox != null && !cb.parentBox.isEmpty() ) {
                        children.put(box.boxName, new ChildCube(box, cb.parentBox));
                    } else {
                        mainBoxesList.put(box.boxName, box);
                    }

                    mandatoryChecklist.put(cb.boxName, true);
                }
            }

            if( json.texture != null && !json.texture.isEmpty() ) {
                this.modelBase.setTexture(json.texture);
            }

            if( isMain ) {
                if( mandatoryChecklist.containsValue(false) ) {
                    SanPlayerModel.LOG.printf(Level.WARN, "Model %s has not all mandatory boxes! Missing %s", this.resLoc.toString(),
                            String.join(", ", mandatoryChecklist.keySet().stream().filter((name) -> !mandatoryChecklist.get(name)).collect(Collectors.toList())));
                    this.nameToBoxList.clear();
                    this.boxToNameList.clear();
                    this.mainBoxes = new ModelRenderer[0];
                    throw new IOException();
                }

                mainBoxesList.forEach((name, box) -> {
                    this.nameToBoxList.put(name, box);
                    this.boxToNameList.put(box, name);
                });

                children.forEach((name, child) -> {
                    this.nameToBoxList.put(name, child.box);
                    this.boxToNameList.put(child.box, name);
                });

                children.forEach((name, child) -> {
                    if( this.nameToBoxList.containsKey(child.parentName) ) {
                        this.nameToBoxList.get(child.parentName).addChild(child.box);
                    } else {
                        this.modelBase.boxList.stream().filter((box) -> box.boxName != null && box.boxName.equals(child.parentName)).forEach((box) -> box.addChild(child.box));
                    }
                });

                this.mainBoxes = mainBoxesList.values().toArray(new ModelRenderer[mainBoxesList.size()]);

                this.jsonInst = json;
                this.loaded = true;
            }
        }
    }

    /**
     * loads and parses the JSON.
     */
    public void load() {
        this.mainBoxes = new ModelRenderer[0];
        this.nameToBoxList.clear();
        this.boxToNameList.clear();

        try {
            Map<String, Boolean> mandatoryChecklist = new HashMap<>();
            Arrays.asList(this.mandatNames).forEach((name) -> mandatoryChecklist.put(name, false));
            loadJson(this.resLoc, mandatoryChecklist);
        } catch( IOException ex) {
            SanPlayerModel.LOG.log(Level.INFO, String.format("Can't load model location %s!", this.resLoc.toString()));
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

    /**
     * gets a box from the loaders box list
     * @param name the name of a box
     * @return A box matching the name
     */
    public ModelRenderer getBox(String name) {
        return this.nameToBoxList.get(name);
    }

    /**
     * gets the name from the box specified
     * @param box the box
     * @return the name of the box
     */
    @SuppressWarnings("unused")
    public String getName(ModelRenderer box) {
        return this.boxToNameList.get(box);
    }

    /**
     * gets all main boxes (boxes with no parent). Useful for rendering the model.
     * @return An array of all main boxes
     */
    public ModelRenderer[] getMainBoxes() {
        return this.mainBoxes;
    }

    /**
     * Gets the parsed instance of the {@link ModelJson} class
     * @return A parsed {@link ModelJson} instance
     */
    public U getModelJsonInstance() {
        return this.jsonInst;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if( resourcePredicate.test(this) || resourcePredicate.test(TEXTURES) ) {
            this.modelBase.onReload(resourceManager, this);
        }
    }

    /**
     * Wether or not this model is loaded
     * @return true, if model is loaded, false otherwise
     */
    public boolean isLoaded() {
        return this.loaded;
    }

    /**
     * The default class, which holds all values of the parsed JSON model.
     * Extend this class and use {@link ModelJsonLoader#create(ModelBase, Class, ResourceLocation, String...)} to load custom properties.
     */
    public static class ModelJson
    {
        public String texture;
        public String parent;
        protected Cube[] cubes;
    }

    private static class ChildCube
    {
        private final ModelRenderer box;
        private final String parentName;

        private ChildCube(ModelRenderer box, String parentName) {
            this.box = box;
            this.parentName = parentName;
        }
    }

    @SuppressWarnings("WeakerAccess")
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
