/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel.client;

import de.sanandrew.mods.sanlib.sanplayermodel.SanPlayerModel;
import net.minecraft.util.ResourceLocation;

public class Resources
{
    public static final ResourceLocation MAIN_MODEL = new ResourceLocation(SanPlayerModel.ID, "models/entity/sanplayer_base.json");
    public static final ResourceLocation MAIN_MODEL_TEXTURE = new ResourceLocation("sanplayermodel", "textures/entity/player/sanplayer.png");
    public static final ResourceLocation MAIN_MODEL_TEXTURE_SLEEP = new ResourceLocation("sanplayermodel", "textures/entity/player/sanplayer_sleeping.png");
}
