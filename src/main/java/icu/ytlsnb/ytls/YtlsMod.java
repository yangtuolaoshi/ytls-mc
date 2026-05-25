package icu.ytlsnb.ytls;

import icu.ytlsnb.ytls.framework.bootstrap.Framework;
import net.minecraftforge.fml.common.Mod;

/**
 * 模组入口：仅负责触发框架初始化，不包含业务逻辑。
 */
@Mod(ModConstants.MOD_ID)
public final class YtlsMod {
    public YtlsMod() {
        Framework.initialize();
    }
}
