## Issue submission
Hey there. Looks like you want to contribute with an issue. Great. Here's what you have to do:
* Have a look through the [Issues tab](https://github.com/SanAndreasP/SanLib/issues), to see if your issue has been reported/solved already.
* Read through "[How to write a bug report](https://chase-seibert.github.io/blog/2016/02/26/QA-101-How-to-write-a-bug-report.html)" (You may skip this if you're already familiar with reporting issues, but it's good for newcomers).
* Note the following points:
  * Issues regarding "Bukkit+Forge" servers that are not reproducible with forge only are **not accepted**.
  * Issues regarding outdated versions of the mod, especially for older versions of Minecraft, are **not accepted**.
  * Unless you manually update every mod on the pack to the latest, issues regarding any public modpack that is not officially maintained or supported by me (the ones in the modpack section of the website are not) are **not accepted**.
  * Duplicate issues or issues that have been solved already (use the search feature!) will be closed without asking.
  * Do not tag your issues' names. "Something Broke" is prefered to "[Bug] Something Broke"  because there's a proper label system in place.

The following "bugs" are not accepted:
* Intended Behaviour
  * This mod alters the Minecraft window title; to prevent this, there is a configuration option called `setSplashTitle`.
* Not a SanLib problem/Not fixable

[Report the Issue](https://github.com/SanAndreasP/SanLib/issues/new)!

---
## Pull Requests
If you want to make a Pull Request, here are the steps:
  1. Fork this repository.
  2. Download your fork with a git client of your choice.
  3. Go to the local repository directory and run `./gradlew setupDecompWorkspace`.
  4. Import the build.gradle as a gradle project into the IDE of your choice.
     * If you have trouble importing a gradle project into eclipse, run `./gradlew eclipse` and import the resulting eclipse project.
     * On IntelliJ IDEA, run `./gradlew genIntelliJRuns` in the IDEA terminal. There are a 2 issues that may occur due to how IDEA / ForgeGradle work.
       > If the command shows errors, you need to temporarily add a run configuration (`"Run" -> "Edit Configurations..." -> click the "+" icon top right -> "Application" -> OK`), rerun the command and then you can remove the manual configuration again.  
         &nbsp;  
         If the generated configurations show errors, edit the configuration (`"Run" -> "Edit Configurations..." -> choose the erroring application configuration`) and set the module to `SanLib.main`.
  5. Once you have made the changes and tested them, commit your changes to the fork and create a Pull Request.
  6. After I've reviewed and merged the Pull Request, you can delete your fork of SanLib.

**Keep these in mind**:
* Do NOT use the github editor. Test your PRs before you submit them.
* I'm very strict when it comes to syntax. Make sure your PR's syntax matches the syntax of the rest of the code. That includes spacing after if/for/(etc), proper bracket usage, camel casing and copyleft headers on new classes.
* If your pull request edits very small chunks of code and isn't flawless I'll close it as it'll probably take less time to fix it myself rather than pull yours and change the code.

&nbsp;  
&nbsp;  
&nbsp;  
###### This text was provided by [Botania](https://github.com/Vazkii/Botania/blob/master/.github/CONTRIBUTING.md), made by Vazkii, and altered by SanAndreasP