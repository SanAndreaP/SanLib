**1.6.2**
- **requires Forge 14.23.5.2831 or higher**
- fixes crash with Ido and my player model hack

**1.6.1**
- **requires Forge 14.23.5.2831 or higher**
- added `ItemStackUtils.getCompactItems(NonNullList<ItemStack>, int, Integer)`, allowing to override the stack size check with a custom value

**1.6.0**
- **requires Forge 14.23.5.2831 or higher**
- added API for data-driven GUIs based on the JSON format
- added emissive block/item model
- deprecated Lexicon API in favor of *Patchouli*
- added `GuiUtils#drawGradientRect()` and `GuiUtils#buildColoredQuad()`
- `@Init` from the Config-API now has 2 stages, `Pre` (default) and `Post`
- Config-API can now handle private/protected fields
- `MiscUtils#calcFormula()` now correctly interprets asin/acos, not changing them to sin/cos
- `LangUtils` now converts newline characters properly
- fixed hue "overshooting" past 360.0 in `ColorObj#calcAndSetRgbFromHsl()`
- added `Procedure` functional interface
- relicense to BSD-3-clause
- added fixed item_nbt variant for custom machine recipes
- `LangUtils#translate(String langKey, Object... args)` now won't try to format with no additional arguments
- fixed `InventoryUtils#addStackToCapability()` to return the remainder instead of either all or none
- fixed `ConcurrentModificationException` whilst looking up an entity with their UUID via `EntityUtils#getEntityByUUID()`
- added `RenderUtils#renderStackInWorld()` method with customizable transform type parameter
- added `RenderUtils#renderStackInWorld()` method with entity parameter (for custom model overrides)
- added `GuiUtils#buildColoredQuad()` method
- added new methods to `JsonUtils` for adding properties to JSON objects
- added new methods to `EntityUtils`
  - `tryApplyMultiplier()`
  - `tryRemoveMultiplier()`
- added new methods to `InventoryUtils`
  - `mergeItemStack()`
  - `finishTransfer()`
  - `dropBlockItems()`
- added new methods to `ItemStackUtils`
  - `dropBlockItem()`
  - `getCompactItems()`
- added new methods to `MiscUtils`
  - `wrap360()`
  - `applyNonNull()`
  - `buildCustomBlockStateContainer()`
  - `readFile()`
  - `readInteger()`
  - `getNumberFormat()`
  - `getNumberSiPrefixed()`
  - `getPathedRL()`
- added `getHeldItemOfType()` to `PlayerUtils`
- changed my implementation of my player model, it now uses the vanilla model with some modifications; it's now compatible with e.g. the Emote system from *Quark*

**1.5.1**
- SanLib command has been renamed `/sanlibc` and is now client-side only
- added `ILexiconGuiHelper#getEntryButtonList()` and `ILexiconGuiHelper#getGuiButtonList()`
- deprecated `globalButtons` and `entryButtons` parameters from `ILexiconPageRender#initPage()`
- if the `links` parameter is null in `ILexiconGuiHelper#drawContentString()` the links will be rendered as regular text
- lexicon entries can now define their own language key for the title
- items and mouseover effect should render correctly now when scaling is not a whole number whilst using `LexiconGuiHelper#drawItemGrid()`
- added `ILexicon#getNavButtonOffsetY()`
- added `MiscUtils#getTimeFromTicks()` with `secondsPrecision` parameter to customize output, old method now has a precision of 2 (0.00 seconds) instead of 1 (0.0 s)
- `ModelJsonLoader` now implements `ISelectiveResourceReloadListener` instead of `IResourceManagerReloadListener`
- fixed lexicon search page positions on certain GUIs
- reset page renderer to the empty one once you go back to the entry/group list
- config now uses new annotation-based method
- a random splash text is now written in the window title on load (like Terraria)
- added `MiscUtils#defIfNull()` with functional interface (supplier) as default parameter
- jar is now signed
- added `@Init` interface for config to declare initializer methods regardless of their name, also initializer methods can be called within the config classes themselves now
- added `MiscUtils#call()` and `MiscUtils#between()` methods
- `Tuple#compareTo()` will now immediately return 0 if parameter is the instance itself
- lexicon group buttons are now further spread out, also they'll now respect the entry position
- added method to get list of matching recipes for an item
- deprecated `LexiconRenderCraftingGrid` and `ILexiconEntryCraftingGrid`, they're not needed anymore and now existing methods for rendering recipes provide way more flexibility
- deprecated `ILexiconEntryFurnace`
- lexicons can now have their own language subfolder
- fixed `LexiconRenderStandard` not render pages properly
- added textures and models to my player model for EnderIO and Botania terrasteel armor
- fixed some arm and item locations on my player model

**1.5.0**
- added Lexicon API
- added ConfigUtils
- added LangUtils