package icu.ytlsnb.ytls.framework.data.provider;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.registry.ForgeRegistryProvider;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public final class AutoLangProvider extends LanguageProvider {
    private final ForgeRegistryProvider registry;

    public AutoLangProvider(PackOutput output, ForgeRegistryProvider registry) {
        super(output, ModConstants.MOD_ID, "zh_cn");
        this.registry = registry;
    }

    @Override
    protected void addTranslations() {
        for (var entry : registry.catalog()) {
            if (entry.kind() == RegistryKind.BLOCK) {
                add(entry.location().toString(), humanize(entry.name()));
            }
            if (entry.kind() == RegistryKind.ITEM) {
                add(entry.location().toString(), humanize(entry.name()));
            }
        }
        add("itemGroup." + ModConstants.MOD_ID + ".main", "YTLS 创意");
    }

    private static String humanize(String registryName) {
        String[] parts = registryName.replace('_', ' ').split(" ");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
            builder.append(' ');
        }
        return builder.toString().trim();
    }
}
