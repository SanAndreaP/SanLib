/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel.client.model;

import net.minecraft.client.model.ModelArmorStand;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSanArmorStand
        extends ModelArmorStand
{
    public ModelSanArmorStand() {
        this(0.0F);
    }

    public ModelSanArmorStand(float modelSize) {
        super(modelSize);

        this.bipedBody = new ModelRenderer(this, 0, 26);
        this.bipedBody.addBox(-4.0F, 0.0F, -1.5F, 8, 3, 3, modelSize);
        this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
    }
}
