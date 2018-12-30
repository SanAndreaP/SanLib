package de.sanandrew.mods.sanlib.lib.client.gui;

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class GuiDataDrivenScreen
    extends GuiScreen
{
//    protected final GuiDefinition def;

    public GuiDataDrivenScreen(ResourceLocation buildData) throws IOException {
        File src = Loader.instance().getIndexedModList().get(buildData.getNamespace()).getSource();
        File defFile = null;
        if( src.isFile() ) {
            FileSystem fs = FileSystems.newFileSystem(src.toPath(), null);
            defFile = fs.getPath("/assets/" + buildData.getNamespace() + "/guis/" + buildData.getPath()).toFile();
        } else if( src.isDirectory() ) {
            defFile = src.toPath().resolve("assets/" + buildData.getNamespace() + "/guis/" + buildData.getPath()).toFile();
        } else {
            throw new IOException("Cannot instanciate a data-driven GUI with an arbitrary mod directory!");
        }

        if( !defFile.isFile() ) {
            throw new IOException("Path to data-driven GUI definition is not a file!");
        }
//        BufferedReader br = Files.newBufferedReader(defFile); {
////
////        }
    }
}
