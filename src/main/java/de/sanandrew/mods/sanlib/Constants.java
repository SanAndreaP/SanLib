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
    static final        String VERSION                 = "1.6.3";
    static final        String ACCEPTED_REMOTE_VER     = "[1.6.1,)";
    static final        String MCVER                   = "[1.12.2, 1.13)";
    static final        String DEPENDENCIES            = "required-after:forge@[14.23.5.2831,]";

    static final String BUILD_MCVER    = "1.12.2";
    static final String BUILD_FORGEVER = "1.12.2-14.23.5.2831";
    static final String BUILD_MAPPINGS = "snapshot_20180814";

    static final String COMMON_PROXY = "de.sanandrew.mods.sanlib.CommonProxy";
    static final String CLIENT_PROXY = "de.sanandrew.mods.sanlib.client.ClientProxy";

    public static final String PM_ID                  = "sanplayermodel";
    public static final String PM_NAME                = "San's Player Model";
    public static final String PM_VERSION             = "1.2.2";
    public static final String PM_ACCEPTED_REMOTE_VER = "[1.2.0,)";

    public static final String PM_COMMON_PROXY = "de.sanandrew.mods.sanlib.sanplayermodel.CommonProxy";
    public static final String PM_CLIENT_PROXY = "de.sanandrew.mods.sanlib.sanplayermodel.client.ClientProxy";
}
