////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package net.minecraft.client.renderer.block.model;

public final class SanLibDeserializers
{
    private SanLibDeserializers() { }

    public static BlockPart.Deserializer getForBlockPart() {
        return new BlockPart.Deserializer();
    }

    public static BlockFaceUV.Deserializer getForBlockFaceUV() {
        return new BlockFaceUV.Deserializer();
    }

    public static ItemTransformVec3f.Deserializer getForItemTransformVec3f() {
        return new ItemTransformVec3f.Deserializer();
    }

    public static ItemCameraTransforms.Deserializer getForItemCameraTransforms() {
        return new ItemCameraTransforms.Deserializer();
    }

    public static ItemOverride.Deserializer getForItemOverride() {
        return new ItemOverride.Deserializer();
    }

    public static BlockPartFace.Deserializer getForBlockPartFace() {
        return new BlockPartFace.Deserializer();
    }
}
