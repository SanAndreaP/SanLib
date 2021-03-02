////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package santest;

//TODO: reimplement tests
@SuppressWarnings("all")
//@Mod(modid = "santest", dependencies = "required-after:" + Constants.ID)
public class Test
{
//    @Mod.Instance("santest")
//    public static Test instance;
//
//    public static int lexiconId;
//
//    @Mod.EventHandler
//    public void onPreInit(FMLPreInitializationEvent event) {
//        testConfig(event.getSuggestedConfigurationFile());
//        testTickFormat();
//
//        lexiconId = GuiLexicon.register(new ResourceLocation("santest", "lexicon"));
//    }
//
//    @Mod.EventHandler
//    public void onPostInit(FMLPostInitializationEvent event) {
//        testNbtMethods();
//    }
//
//    private static void testTickFormat() {
//        System.out.println(MiscUtils.getTimeFromTicks(5, 0));
//        System.out.println(MiscUtils.getTimeFromTicks(5, 1));
//        System.out.println(MiscUtils.getTimeFromTicks(5, 2));
//        System.out.println(MiscUtils.getTimeFromTicks(1, 0));
//        System.out.println(MiscUtils.getTimeFromTicks(1, 1));
//        System.out.println(MiscUtils.getTimeFromTicks(1, 2));
//    }
//
//    private static void testConfig(File path) {
//        Configuration config = ConfigUtils.loadConfigFile(path, "1.0", "santest");
//        ConfigUtils.loadCategories(config, TestConfig.class);
//        config.save();
//    }
//
//    private static void testNbtMethods() {
//        NBTTagCompound numericNbt1 = new NBTTagCompound(); numericNbt1.setInteger("num", 16); numericNbt1.setLong("num2", 32);
//        NBTTagCompound numericNbt2 = new NBTTagCompound(); numericNbt2.setByte("num", (byte) 16);
//
//        SanLib.LOG.log(Level.INFO,
//                       String.format("MiscUtils.doesNbtContainOther(numericNbt1, numericNbt2, false) = %s, should be true",
//                                     MiscUtils.doesNbtContainOther(numericNbt1, numericNbt2, false)));
//        SanLib.LOG.log(Level.INFO,
//                       String.format("MiscUtils.doesNbtContainOther(numericNbt2, numericNbt1, false) = %s, should be false",
//                                     MiscUtils.doesNbtContainOther(numericNbt2, numericNbt1, false)));
//
//        NBTTagCompound tagNbt1 = new NBTTagCompound(); tagNbt1.setTag("tag", numericNbt1);
//        NBTTagCompound tagNbt2 = new NBTTagCompound(); tagNbt2.setTag("tag", numericNbt2);
//
//        SanLib.LOG.log(Level.INFO,
//                       String.format("MiscUtils.doesNbtContainOther(tagNbt1, tagNbt2, false) = %s, should be true",
//                                     MiscUtils.doesNbtContainOther(tagNbt1, tagNbt2, false)));
//        SanLib.LOG.log(Level.INFO,
//                       String.format("MiscUtils.doesNbtContainOther(tagNbt2, tagNbt1, false) = %s, should be false",
//                                     MiscUtils.doesNbtContainOther(tagNbt2, tagNbt1, false)));
//
//        NBTTagList list1 = new NBTTagList(); list1.appendTag(numericNbt1); list1.appendTag(new NBTTagCompound());
//        NBTTagList list2 = new NBTTagList(); list2.appendTag(numericNbt2);
//        NBTTagCompound listNbt1 = new NBTTagCompound(); listNbt1.setTag("list", list1);
//        NBTTagCompound listNbt2 = new NBTTagCompound(); listNbt2.setTag("list", list2);
//
//        SanLib.LOG.log(Level.INFO,
//                       String.format("MiscUtils.doesNbtContainOther(listNbt1, listNbt2, false) = %s, should be true",
//                                     MiscUtils.doesNbtContainOther(listNbt1, listNbt2, false)));
//        SanLib.LOG.log(Level.INFO,
//                       String.format("MiscUtils.doesNbtContainOther(listNbt2, listNbt1, false) = %s, should be false",
//                                     MiscUtils.doesNbtContainOther(listNbt2, listNbt1, false)));
//    }
//
//    public static final class TestConfig
//    {
//        @Category(value = "config_nrm", comment = "a test 1", reqMcRestart = true)
//        public static final class Config1
//        {
//            @Value(value = "string_nrm", comment = "meh")
//            public static String str_nrm = "meh";
//
//            @Value(value = "string_pattern", comment = "meh", range = @Range(validationPattern = @Pattern("^#[\\da-fA-F]{6}$")))
//            public static String str_pattern = "#808080";
//
//            @Value(value = "double_def")
//            public static double double_def = 4.0;
//
//            @Value(value = "double_rng", range = @Range(minD = -5, maxD = 14))
//            public static double double_rng = 3.0;
//
//            @Value(value = "int_def")
//            public static int int_def = 3;
//
//            @Value(value = "int_rng", range = @Range(minI = -5, maxI = 14))
//            public static int int_rng = 2;
//
//            @Value(value = "int_array")
//            public static int[] int_array = new int[] {5, 4, 3, 2, 1, 0};
//
//            @Value(value = "int_array_fixed", range = @Range(listFixed = true))
//            public static int[] int_array_fixed = new int[] {5, 4, 3, 2, 1, 0};
//
//            @Value(value = "int_array_ranged", range = @Range(minI = 0, maxI = 10))
//            public static int[] int_array_ranged = new int[] {5, 4, 3, 2, 1, 0};
//
//            @Value(value = "int_array_ranged_fixed", range = @Range(listFixed = true, minI = 0, maxI = 10))
//            public static int[] int_array_ranged_fixed = new int[] {5, 4, 3, 2, 1, 0};
//
//            @Value(value = "string_array_patterned", range = @Range(validationPattern = @Pattern("^[\\d|\\w]+$")))
//            public static String[] string_array_pattern = new String[] {"1d", "2", "a", "4"};
//
//            private static void init() {
//                System.out.println("init called!");
//            }
//        }
//
//        @Category(value = "config_enum", comment = "haha", reqWorldRestart = true)
//        public enum Config2
//        {
//            DEE1,
//            DEE2,
//
//            @EnumExclude
//            UNKNOWN;
//
//            @Value(value = "int_1", comment = "rofl", range = @Range(minI = -82, maxI = 69))
//            public int ente;
//            @Value(value = "float_1", range = @Range(minD = 0, maxD = 5))
//            public float carnival;
//        }
//    }
}
