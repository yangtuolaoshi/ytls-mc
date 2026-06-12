package icu.ytlsnb.ytls.client;

import com.mojang.blaze3d.platform.InputConstants;
import icu.ytlsnb.ytls.ModConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public final class ClientKeyMappings {

    public static final KeyMapping USE_MILK_ABILITY = new KeyMapping(
        "key.ytls.use_milk_ability",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_G,
        "key.categories.ytls"
    );

    public static final KeyMapping COLLECT_SELF_MILK = new KeyMapping(
        "key.ytls.collect_self_milk",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_M,
        "key.categories.ytls"
    );

    private ClientKeyMappings() {
    }
}
