package icu.ytlsnb.ytls.registry;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.item.CrowbarItem;
import icu.ytlsnb.ytls.item.MilkBucketItem;
import icu.ytlsnb.ytls.milk.MilkType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModConstants.MOD_ID);

    public static final Map<MilkType, RegistryObject<Item>> MILK_BUCKETS = new EnumMap<>(MilkType.class);

    public static final RegistryObject<Item> GALACTAGOGUE = ITEMS.register("galactagogue",
        () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(0.6F)
            .alwaysEat()
            .build())));

    public static final RegistryObject<Item> MILK_ALTAR_ITEM = ITEMS.register("milk_altar",
        () -> new BlockItem(ModBlocks.MILK_ALTAR.get(), new Item.Properties()));

    public static final RegistryObject<Item> CROWBAR = ITEMS.register("crowbar",
        () -> new CrowbarItem(new Item.Properties()));

    private ModItems() {
    }

    public static void bootstrap() {
        for (MilkType milkType : MilkType.values()) {
            RegistryObject<Item> item = ITEMS.register(milkType.id() + "_bucket",
                () -> new MilkBucketItem(milkType, ModFluids.SOURCE_FLUIDS.get(milkType).get(),
                    new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
            MILK_BUCKETS.put(milkType, item);
        }
    }
}
