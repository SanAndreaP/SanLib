////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package net.minecraftforge.client.model;

import javax.annotation.Nullable;

public final class SanLibVanillaLoaderGetter
{
    private SanLibVanillaLoaderGetter() { }

    @Nullable
    public static ModelLoader get() {
        return ModelLoader.VanillaLoader.INSTANCE.getLoader();
    }
}
