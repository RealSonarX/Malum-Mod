package com.sammy.malum.data.recipe.infusion;

import com.sammy.malum.data.recipe.builder.*;
import com.sammy.malum.registry.common.item.*;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.common.*;

import java.util.function.*;

import static com.sammy.malum.registry.common.SpiritTypeRegistry.*;

public class MiscSpiritInfusionRecipes {

    public static void buildRecipes(Consumer<FinishedRecipe> consumer) {
        new SpiritInfusionRecipeBuilder(ItemRegistry.RUNEWOOD_PLANKS.get(), 2, ItemRegistry.RUNEWOOD_OBELISK.get(), 1)
                .addExtraItem(ItemRegistry.HALLOWED_GOLD_INGOT.get(), 2)
                .addExtraItem(ItemRegistry.HEX_ASH.get(), 1)
                .addSpirit(AERIAL_SPIRIT, 16)
                .addSpirit(SACRED_SPIRIT, 8)
                .build(consumer);

        new SpiritInfusionRecipeBuilder(ItemRegistry.RUNEWOOD_PLANKS.get(), 2, ItemRegistry.BRILLIANT_OBELISK.get(), 1)
                .addExtraItem(ItemRegistry.CLUSTER_OF_BRILLIANCE.get(), 2)
                .addExtraItem(ItemRegistry.HEX_ASH.get(), 1)
                .addSpirit(AERIAL_SPIRIT, 16)
                .addSpirit(AQUEOUS_SPIRIT, 8)
                .build(consumer);

        new SpiritInfusionRecipeBuilder(ItemRegistry.RUNEWOOD_ITEM_PEDESTAL.get(), 1, ItemRegistry.RUNIC_WORKBENCH.get(), 1)
                .addExtraItem(ItemRegistry.RUNEWOOD_PLANKS.get(), 4)
                .addExtraItem(ItemRegistry.HALLOWED_GOLD_INGOT.get(), 2)
                .addSpirit(AQUEOUS_SPIRIT, 16)
                .addSpirit(SACRED_SPIRIT, 8)
                .build(consumer);

        new SpiritInfusionRecipeBuilder(Items.HONEY_BOTTLE, 1, ItemRegistry.CONCENTRATED_GLUTTONY.get(), 2)
                .addExtraItem(ItemRegistry.ROTTING_ESSENCE.get(), 1)
                .addSpirit(AQUEOUS_SPIRIT, 2)
                .addSpirit(SACRED_SPIRIT, 2)
                .addSpirit(WICKED_SPIRIT, 2)
                .build(consumer);

        new SpiritInfusionRecipeBuilder(Items.HONEY_BOTTLE, 1, ItemRegistry.SPLASH_OF_GLUTTONY.get(), 2)
                .addExtraItem(ItemRegistry.ROTTING_ESSENCE.get(), 1)
                .addExtraItem(Ingredient.of(Tags.Items.GUNPOWDER), 1)
                .addSpirit(AQUEOUS_SPIRIT, 3)
                .addSpirit(SACRED_SPIRIT, 2)
                .addSpirit(WICKED_SPIRIT, 2)
                .build(consumer);

        new SpiritInfusionRecipeBuilder(ItemRegistry.CONCENTRATED_GLUTTONY.get(), 1, ItemRegistry.SPLASH_OF_GLUTTONY.get(), 1)
                .addExtraItem(Ingredient.of(Tags.Items.GUNPOWDER), 1)
                .addSpirit(AQUEOUS_SPIRIT, 1)
                .build(consumer, "splash_of_gluttony_from_concentrated_gluttony");

        new SpiritInfusionRecipeBuilder(Ingredient.of(Tags.Items.INGOTS_IRON), 2, ItemRegistry.LAMPLIGHTERS_TONGS.get(), 1)
                .addSpirit(ARCANE_SPIRIT, 8)
                .addSpirit(INFERNAL_SPIRIT, 8)
                .addExtraItem(ItemRegistry.RUNEWOOD_PLANKS.get(), 2)
                .addExtraItem(ItemRegistry.PROCESSED_SOULSTONE.get(), 1)
                .build(consumer);

    }
}
