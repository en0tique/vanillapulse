package net.kn.horrormod.item;


import net.kn.horrormod.HorrorMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    //Відкладенна реєстрація для всіх предметів
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, HorrorMod.MOD_ID);
    //Реєстрація самого предмета. Це сталий вираз.
    public static final RegistryObject<Item> STEEL_INGOT =
            ITEMS.register("steel_ingot", ()-> new Item(new Item.Properties()
                    .stacksTo(128)
                    .rarity(Rarity.EPIC)
            ));


    public static final RegistryObject<Item> SHIT_INGOT =
            ITEMS.register("shit_ingot", ()-> new Item(new Item.Properties()
                    .stacksTo(4)
                    .rarity(Rarity.RARE)
                    .fireResistant()
            ));

    public static final RegistryObject<Item> CELT_CREST =
            ITEMS.register("celt_crest", ()-> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)
                    .fireResistant()
                    .setNoRepair()
            ));

    public static final RegistryObject<Item> STRAW_HAT =
            ITEMS.register(
                    "straw_hat",
                    () -> new StrawHatItem(
                            new Item.Properties()
                    )
            );


}
