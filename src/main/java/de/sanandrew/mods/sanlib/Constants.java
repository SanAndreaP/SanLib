////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib;

@SuppressWarnings("unused")
public class Constants
{
    public static final String ID                      = "sanlib";
    static final        String NAME                    = "San's Library";
    static final        String CERTIFICATE_FINGERPRINT = "@FINGERPRINT@";
    static final        String VERSION                 = "2.0.0";
    static final        String ACCEPTED_REMOTE_VER     = "[2.0.0,)";
    static final        String MCVER                   = "[1.16.4, 1.17)";
    static final        String DEPENDENCIES            = "required-after:forge@[35.1.4,]";

    static final String BUILD_MCVER    = "1.16.4";
    static final String BUILD_FORGEVER = "1.16.4-35.1.4";
    static final String BUILD_MAPPINGS_CHANNEL = "snapshot";
    static final String BUILD_MAPPINGS_VERSION = "20201028-1.16.3";

    static final String COMMON_PROXY = "de.sanandrew.mods.sanlib.CommonProxy";
    static final String CLIENT_PROXY = "de.sanandrew.mods.sanlib.client.ClientProxy";

    public static final String PM_ID                  = "sanplayermodel";
    public static final String PM_NAME                = "San's Player Model";
    public static final String PM_VERSION             = "1.2.1";
    public static final String PM_ACCEPTED_REMOTE_VER = "[1.2.0,)";

    public static final String PM_COMMON_PROXY = "de.sanandrew.mods.sanlib.sanplayermodel.CommonProxy";
    public static final String PM_CLIENT_PROXY = "de.sanandrew.mods.sanlib.sanplayermodel.client.ClientProxy";
}
