package icu.ytlsnb.ytls;

import icu.ytlsnb.ytls.core.ModConstants;
import icu.ytlsnb.ytls.init.ModBusSetup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * 模组入口：仅负责把 DeferredRegister 挂到模组事件总线。
 * 具体注册条目见 {@link icu.ytlsnb.ytls.init} 包。
 */
@Mod(ModConstants.MOD_ID)
public final class YtlsMod {
    public YtlsMod() {
        ModBusSetup.subscribeDeferredRegisters(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
