![SanLib](../logo_banner.png)

[![CurseForge Downloads](http://cf.way2muchnoise.eu/short_sanlib.svg)][sanlib]
[![CurseForge Latest](http://cf.way2muchnoise.eu/versions/sanlib_latest.svg)][sanlib]
[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)
***
Welcome to the SanLib repository. This is a [Minecraft](https://minecraft.net) mod used as a library by most of my mods.
***
## For Players
* If you have any suggestion or bug / issue to report, use the [Issue Tracker](https://github.com/SanAndreasP/SanLib/issues) for that  
  Please consult the [CONTRIBUTING.md](CONTRIBUTING.md) on the contribution guidelines.
* To get the compiled, ready-to-use version of this mod for your Minecraft version, go to the [CurseForge listing][sanlib]

## For Developers
* *Improving this mod* - For suggestions, bugs and issues, see **For Players**. If you want to change something within the code of this mod, create a [Pull Request](https://github.com/SanAndreasP/SanLib/pulls).  
  Please consult the [CONTRIBUTING.md](CONTRIBUTING.md) on the contribution guidelines and how to do a Pull Request.
* *Using a part of this mod* - If you feel like your mod only needs a part of what this has to offer, copy that part **to a new package / path** to prevent any potential conflicts occurring. **Please respect the aforementioned license**.
* *Using this mod as a dependency* - To use this as a dependency in your project, use the maven repository in gradle.
  * Add this to your `build.gradle`:
    ```groovy
    repositories {
        maven {
            name = "SanLib"
            url = "https://github.com/SanAndreasP/SanLib/raw/1.12/maven/"
        }
    }
    
    dependencies {
        deobfCompile "de.sanandrew.mods:SanLib:{mc_version}-{mod_version}"
    }
    ```
    > don't edit anything within the `buildscript` block, as that is used for ForgeGradle and that alone (unless you know what you're doing, of course). These lines go outside of it.
    > if you already
  * Choose a version:
    * `{mc_version}` should be replaced by the Minecraft version you want to use. (i.e. `1.12.2`)
    * `{mod_version}` should be replaced by the version of SanLib you want to use (i.e `1.6.0`)
  * After adding the lines to your `build.gradle`, run the following console command: `./gradlew setupDecompWorkspace --refresh-dependencies`
    
[sanlib]: https://www.curseforge.com/minecraft/mc-mods/sanlib
