package icu.ytlsnb.ytls.init;

import icu.ytlsnb.ytls.item.BaguaSlagItem;
import icu.ytlsnb.ytls.item.JiuchiDingpaItem;
import icu.ytlsnb.ytls.item.MiniBaguaFurnaceItem;
import icu.ytlsnb.ytls.item.ModArmorMaterials;
import icu.ytlsnb.ytls.item.ObsidianApple;
import icu.ytlsnb.ytls.item.ObsidianIngot;
import icu.ytlsnb.ytls.item.ObsidianPickaxe;
import icu.ytlsnb.ytls.item.ObsidianSword;
import icu.ytlsnb.ytls.item.RuyiJinguBangItem;
import icu.ytlsnb.ytls.item.XianDanItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static icu.ytlsnb.ytls.core.ModConstants.MOD_ID;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> OBSIDIAN_INGOT = ITEMS.register("obsidian_ingot", ObsidianIngot::new);
    public static final RegistryObject<Item> OBSIDIAN_APPLE = ITEMS.register("obsidian_apple", ObsidianApple::new);
    public static final RegistryObject<Item> OBSIDIAN_SWORD = ITEMS.register("obsidian_sword", ObsidianSword::new);
    public static final RegistryObject<Item> OBSIDIAN_PICKAXE = ITEMS.register("obsidian_pickaxe", ObsidianPickaxe::new);
    public static final RegistryObject<Item> OBSIDIAN_HELMET = ITEMS.register(
            "obsidian_helmet",
            () -> new ArmorItem(ModArmorMaterials.OBSIDIAN, ArmorItem.Type.HELMET, new Item.Properties())
    );
    public static final RegistryObject<Item> OBSIDIAN_CHESTPLATE = ITEMS.register(
            "obsidian_chestplate",
            () -> new ArmorItem(ModArmorMaterials.OBSIDIAN, ArmorItem.Type.CHESTPLATE, new Item.Properties())
    );
    public static final RegistryObject<Item> OBSIDIAN_LEGGINGS = ITEMS.register(
            "obsidian_leggings",
            () -> new ArmorItem(ModArmorMaterials.OBSIDIAN, ArmorItem.Type.LEGGINGS, new Item.Properties())
    );
    public static final RegistryObject<Item> OBSIDIAN_BOOTS = ITEMS.register(
            "obsidian_boots",
            () -> new ArmorItem(ModArmorMaterials.OBSIDIAN, ArmorItem.Type.BOOTS, new Item.Properties())
    );
    public static final RegistryObject<Item> OBSIDIAN_BLOCK = ITEMS.register(
            "obsidian_block",
            () -> new BlockItem(ModBlocks.OBSIDIAN_BLOCK.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> OBSIDIAN_COUNTER = ITEMS.register(
            "obsidian_counter",
            () -> new BlockItem(ModBlocks.OBSIDIAN_COUNTER.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> BAGUA_FURNACE = ITEMS.register(
            "bagua_furnace",
            () -> new BlockItem(ModBlocks.BAGUA_FURNACE.get(), new Item.Properties().fireResistant())
    );
    public static final RegistryObject<Item> BAGUA_SLAG = ITEMS.register("bagua_slag", BaguaSlagItem::new);
    public static final RegistryObject<Item> RUYI_JINGU_BANG = ITEMS.register("ruyi_jingu_bang", RuyiJinguBangItem::new);
    public static final RegistryObject<Item> JIUCHI_DINGPA = ITEMS.register("jiuchi_dingpa", JiuchiDingpaItem::new);
    public static final RegistryObject<Item> XIAN_DAN = ITEMS.register("xian_dan", XianDanItem::new);
    public static final RegistryObject<Item> MINI_BAGUA_FURNACE = ITEMS.register("mini_bagua_furnace", MiniBaguaFurnaceItem::new);

    private ModItems() {
    }
}
