package com.mikarific.bugmine.mixins.fixes;

import com.google.gson.*;
import com.llamalad7.mixinextras.sugar.Local;
import com.mikarific.bugmine.config.ServerConfig;
import net.minecraft.aprilfools.WorldEffect;
import net.minecraft.class_10967;
import net.minecraft.class_11114;
import net.minecraft.component.type.SpecialMineComponent;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.GameInstance;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Mixin(class_10967.class)
public class DimensionGeneratorMixin {
    @Unique
    private static final Gson gson = new GsonBuilder().setFormattingStyle(FormattingStyle.PRETTY).create();

    @Unique
    private static void createData(TagKey<Biome> biomeTag, List<String> tags, Path prefix, Set<Identifier> modifiedIdentifiers, Identifier identifier) {
        List<String> values = tags.stream().map(tag -> RegistryKey.of(RegistryKeys.BIOME, Identifier.ofVanilla(identifier.getPath() + "/" + tag)).getValue()).filter(modifiedIdentifiers::contains).map(Identifier::toString).toList();
        if (values.isEmpty()) return;
        Path path = prefix.resolve("data").resolve(biomeTag.id().getNamespace()).resolve("tags").resolve(RegistryKeys.BIOME.getValue().getPath()).resolve(biomeTag.id().getPath() + ".json");
        try {
            Files.createDirectories(path.getParent());
            JsonObject data = new JsonObject();
            data.add("values", gson.toJsonTree(values).getAsJsonArray());
            String json = gson.toJson(data);
            Files.writeString(path, json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "LocalMayBeArgsOnly"})
    @Inject(method = "method_69062", at = @At("TAIL"))
    private static void generateDimension(GameInstance gameInstance, List<WorldEffect> activeEffects, Optional<SpecialMineComponent> specialMine, CallbackInfoReturnable<class_10967.class_10970> cir, @Local Identifier identifier, @Local ServerWorldProperties serverWorldProperties, @Local(ordinal = 0) Path path, @Local class_11114 worldGenBuilder) {
        if (ServerConfig.applyLevelBiomeTags) {
            List<class_11114.class_11116> modifiedBiomes = worldGenBuilder.method_70212(
                    gameInstance.getRegistryManager().getOrThrow(RegistryKeys.BIOME), identifier.getPath()
            );
            Set<Identifier> modifiedIdentifiers = modifiedBiomes.stream()
                    .map(class_11114.class_11116::modified)
                    .map(RegistryKey::getValue)
                    .collect(Collectors.toSet());

            createData(BiomeTags.IS_DEEP_OCEAN, Arrays.asList("deep_frozen_ocean", "deep_cold_ocean", "deep_ocean", "deep_lukewarm_ocean"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_OCEAN, Arrays.asList("deep_frozen_ocean", "deep_cold_ocean", "deep_ocean", "deep_lukewarm_ocean", "frozen_ocean", "ocean", "cold_ocean", "lukewarm_ocean", "warm_ocean"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_BEACH, Arrays.asList("beach", "snowy_beach"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_RIVER, Arrays.asList("river", "frozen_river"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_MOUNTAIN, Arrays.asList("meadow", "frozen_peaks", "jagged_peaks", "stony_peaks", "snowy_slopes", "cherry_grove"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_BADLANDS, Arrays.asList("badlands", "eroded_badlands", "wooded_badlands"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_HILL, Arrays.asList("windswept_hills", "windswept_forest", "windswept_gravelly_hills"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_TAIGA, Arrays.asList("taiga", "snowy_taiga", "old_growth_pine_taiga", "old_growth_spruce_taiga"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_JUNGLE, Arrays.asList("bamboo_jungle", "jungle", "sparse_jungle"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_FOREST, Arrays.asList("forest", "flower_forest", "birch_forest", "old_growth_birch_forest", "dark_forest", "pale_garden", "grove"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_SAVANNA, Arrays.asList("savanna", "savanna_plateau", "windswept_savanna"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_NETHER, Arrays.asList("nether_wastes", "soul_sand_valley", "crimson_forest", "warped_forest", "basalt_deltas"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_OVERWORLD, Arrays.asList("mushroom_fields", "deep_frozen_ocean", "frozen_ocean", "deep_cold_ocean", "cold_ocean", "deep_ocean", "ocean", "deep_lukewarm_ocean", "lukewarm_ocean", "warm_ocean", "stony_shore", "swamp", "mangrove_swamp", "snowy_slopes", "snowy_plains", "snowy_beach", "windswept_gravelly_hills", "grove", "windswept_hills", "snowy_taiga", "windswept_forest", "taiga", "plains", "meadow", "beach", "forest", "old_growth_spruce_taiga", "flower_forest", "birch_forest", "dark_forest", "pale_garden", "savanna_plateau", "savanna", "basalt_deltas", "soul_sand_valley", "nether_wastes", "jungle", "crimson_forest", "warped_forest", "badlands", "desert", "end_highlands", "end_midlands", "end_barrens", "wooded_badlands", "small_end_islands", "the_end", "jagged_peaks", "stony_peaks", "frozen_river", "river", "ice_spikes", "old_growth_pine_taiga", "sunflower_plains", "old_growth_birch_forest", "sparse_jungle", "bamboo_jungle", "eroded_badlands", "windswept_savanna", "cherry_grove", "frozen_peaks", "dripstone_caves", "lush_caves", "deep_dark"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IS_END, Arrays.asList("the_end", "end_highlands", "end_midlands", "small_end_islands", "end_barrens"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.BURIED_TREASURE_HAS_STRUCTURE, Arrays.asList("beach", "snowy_beach"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.DESERT_PYRAMID_HAS_STRUCTURE, List.of("desert"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.IGLOO_HAS_STRUCTURE, Arrays.asList("snowy_taiga", "snowy_plains", "snowy_slopes"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.JUNGLE_TEMPLE_HAS_STRUCTURE, Arrays.asList("bamboo_jungle", "jungle"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.MINESHAFT_HAS_STRUCTURE, Arrays.asList("deep_frozen_ocean", "deep_cold_ocean", "deep_ocean", "deep_lukewarm_ocean", "frozen_ocean", "ocean", "cold_ocean", "lukewarm_ocean", "warm_ocean", "river", "frozen_river", "beach", "snowy_beach", "meadow", "frozen_peaks", "jagged_peaks", "stony_peaks", "snowy_slopes", "cherry_grove", "windswept_hills", "windswept_forest", "windswept_gravelly_hills", "taiga", "snowy_taiga", "old_growth_pine_taiga", "old_growth_spruce_taiga", "bamboo_jungle", "jungle", "sparse_jungle", "forest", "flower_forest", "birch_forest", "old_growth_birch_forest", "dark_forest", "pale_garden", "grove", "stony_shore", "mushroom_fields", "ice_spikes", "windswept_savanna", "desert", "savanna", "snowy_plains", "plains", "sunflower_plains", "swamp", "mangrove_swamp", "savanna_plateau", "dripstone_caves", "lush_caves"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.MINESHAFT_MESA_HAS_STRUCTURE, Arrays.asList("badlands", "eroded_badlands", "wooded_badlands"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.MINESHAFT_BLOCKING, List.of("deep_dark"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.OCEAN_MONUMENT_HAS_STRUCTURE, Arrays.asList("deep_frozen_ocean", "deep_cold_ocean", "deep_ocean", "deep_lukewarm_ocean"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.REQUIRED_OCEAN_MONUMENT_SURROUNDING, Arrays.asList("deep_frozen_ocean", "deep_cold_ocean", "deep_ocean", "deep_lukewarm_ocean", "frozen_ocean", "ocean", "cold_ocean", "lukewarm_ocean", "warm_ocean", "river", "frozen_river"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.OCEAN_RUIN_COLD_HAS_STRUCTURE, Arrays.asList("frozen_ocean", "cold_ocean", "ocean", "deep_frozen_ocean", "deep_cold_ocean", "deep_ocean"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.OCEAN_RUIN_WARM_HAS_STRUCTURE, Arrays.asList("lukewarm_ocean", "warm_ocean", "deep_lukewarm_ocean"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.PILLAGER_OUTPOST_HAS_STRUCTURE, Arrays.asList("desert", "plains", "savanna", "snowy_plains", "taiga", "meadow", "frozen_peaks", "jagged_peaks", "stony_peaks", "snowy_slopes", "cherry_grove", "grove"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.RUINED_PORTAL_DESERT_HAS_STRUCTURE, List.of("desert"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.RUINED_PORTAL_JUNGLE_HAS_STRUCTURE, Arrays.asList("bamboo_jungle", "jungle", "sparse_jungle"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.RUINED_PORTAL_OCEAN_HAS_STRUCTURE, Arrays.asList("deep_frozen_ocean", "deep_cold_ocean", "deep_ocean", "deep_lukewarm_ocean", "frozen_ocean", "ocean", "cold_ocean", "lukewarm_ocean", "warm_ocean"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE, Arrays.asList("swamp", "mangrove_swamp"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.RUINED_PORTAL_MOUNTAIN_HAS_STRUCTURE, Arrays.asList("badlands", "eroded_badlands", "wooded_badlands", "windswept_hills", "windswept_forest", "windswept_gravelly_hills", "savanna_plateau", "windswept_savanna", "stony_shore", "meadow", "frozen_peaks", "jagged_peaks", "stony_peaks", "snowy_slopes", "cherry_grove"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.RUINED_PORTAL_STANDARD_HAS_STRUCTURE, Arrays.asList("beach", "snowy_beach", "river", "frozen_river", "taiga", "snowy_taiga", "old_growth_pine_taiga", "old_growth_spruce_taiga", "forest", "flower_forest", "birch_forest", "old_growth_birch_forest", "dark_forest", "pale_garden", "grove", "mushroom_fields", "ice_spikes", "dripstone_caves", "lush_caves", "savanna", "snowy_plains", "plains", "sunflower_plains"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.SHIPWRECK_BEACHED_HAS_STRUCTURE, Arrays.asList("beach", "snowy_beach"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.SHIPWRECK_HAS_STRUCTURE, Arrays.asList("deep_frozen_ocean", "deep_cold_ocean", "deep_ocean", "deep_lukewarm_ocean", "frozen_ocean", "ocean", "cold_ocean", "lukewarm_ocean", "warm_ocean"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.SWAMP_HUT_HAS_STRUCTURE, List.of("swamp"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.VILLAGE_DESERT_HAS_STRUCTURE, List.of("desert"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE, Arrays.asList("plains", "meadow"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.VILLAGE_SAVANNA_HAS_STRUCTURE, List.of("savanna"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.VILLAGE_SNOWY_HAS_STRUCTURE, List.of("snowy_plains"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.VILLAGE_TAIGA_HAS_STRUCTURE, List.of("taiga"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.TRAIL_RUINS_HAS_STRUCTURE, Arrays.asList("taiga", "snowy_taiga", "old_growth_pine_taiga", "old_growth_spruce_taiga", "old_growth_birch_forest", "jungle"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.WOODLAND_MANSION_HAS_STRUCTURE, Arrays.asList("dark_forest", "pale_garden"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.STRONGHOLD_BIASED_TO, Arrays.asList("plains", "sunflower_plains", "snowy_plains", "ice_spikes", "desert", "forest", "flower_forest", "birch_forest", "dark_forest", "pale_garden", "old_growth_birch_forest", "old_growth_pine_taiga", "old_growth_spruce_taiga", "taiga", "snowy_taiga", "savanna", "savanna_plateau", "windswept_hills", "windswept_gravelly_hills", "windswept_forest", "windswept_savanna", "jungle", "sparse_jungle", "bamboo_jungle", "badlands", "eroded_badlands", "wooded_badlands", "meadow", "grove", "snowy_slopes", "frozen_peaks", "jagged_peaks", "stony_peaks", "mushroom_fields", "dripstone_caves", "lush_caves"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.STRONGHOLD_HAS_STRUCTURE, Arrays.asList("mushroom_fields", "deep_frozen_ocean", "frozen_ocean", "deep_cold_ocean", "cold_ocean", "deep_ocean", "ocean", "deep_lukewarm_ocean", "lukewarm_ocean", "warm_ocean", "stony_shore", "swamp", "mangrove_swamp", "snowy_slopes", "snowy_plains", "snowy_beach", "windswept_gravelly_hills", "grove", "windswept_hills", "snowy_taiga", "windswept_forest", "taiga", "plains", "meadow", "beach", "forest", "old_growth_spruce_taiga", "flower_forest", "birch_forest", "dark_forest", "pale_garden", "savanna_plateau", "savanna", "basalt_deltas", "soul_sand_valley", "nether_wastes", "jungle", "crimson_forest", "warped_forest", "badlands", "desert", "end_highlands", "end_midlands", "end_barrens", "wooded_badlands", "small_end_islands", "the_end", "jagged_peaks", "stony_peaks", "frozen_river", "river", "ice_spikes", "old_growth_pine_taiga", "sunflower_plains", "old_growth_birch_forest", "sparse_jungle", "bamboo_jungle", "eroded_badlands", "windswept_savanna", "cherry_grove", "frozen_peaks", "dripstone_caves", "lush_caves", "deep_dark"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.TRIAL_CHAMBERS_HAS_STRUCTURE, Arrays.asList("mushroom_fields", "deep_frozen_ocean", "frozen_ocean", "deep_cold_ocean", "cold_ocean", "deep_ocean", "ocean", "deep_lukewarm_ocean", "lukewarm_ocean", "warm_ocean", "stony_shore", "swamp", "mangrove_swamp", "snowy_slopes", "snowy_plains", "snowy_beach", "windswept_gravelly_hills", "grove", "windswept_hills", "snowy_taiga", "windswept_forest", "taiga", "plains", "meadow", "beach", "forest", "old_growth_spruce_taiga", "flower_forest", "birch_forest", "dark_forest", "pale_garden", "savanna_plateau", "savanna", "basalt_deltas", "soul_sand_valley", "nether_wastes", "jungle", "crimson_forest", "warped_forest", "badlands", "desert", "end_highlands", "end_midlands", "end_barrens", "wooded_badlands", "small_end_islands", "the_end", "jagged_peaks", "stony_peaks", "frozen_river", "river", "ice_spikes", "old_growth_pine_taiga", "sunflower_plains", "old_growth_birch_forest", "sparse_jungle", "bamboo_jungle", "eroded_badlands", "windswept_savanna", "cherry_grove", "frozen_peaks", "dripstone_caves", "lush_caves"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.NETHER_FORTRESS_HAS_STRUCTURE, Arrays.asList("nether_wastes", "soul_sand_valley", "crimson_forest", "warped_forest", "basalt_deltas"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.NETHER_FOSSIL_HAS_STRUCTURE, List.of("soul_sand_valley"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.BASTION_REMNANT_HAS_STRUCTURE, Arrays.asList("crimson_forest", "nether_wastes", "soul_sand_valley", "warped_forest"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.ANCIENT_CITY_HAS_STRUCTURE, List.of("deep_dark"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.RUINED_PORTAL_NETHER_HAS_STRUCTURE, Arrays.asList("nether_wastes", "soul_sand_valley", "crimson_forest", "warped_forest", "basalt_deltas"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.END_CITY_HAS_STRUCTURE, Arrays.asList("end_highlands", "end_midlands"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL, List.of("warm_ocean"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.PLAYS_UNDERWATER_MUSIC, Arrays.asList("deep_frozen_ocean", "deep_cold_ocean", "deep_ocean", "deep_lukewarm_ocean", "frozen_ocean", "ocean", "cold_ocean", "lukewarm_ocean", "warm_ocean", "river", "frozen_river"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.HAS_CLOSER_WATER_FOG, Arrays.asList("swamp", "mangrove_swamp"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.WATER_ON_MAP_OUTLINES, Arrays.asList("deep_frozen_ocean", "deep_cold_ocean", "deep_ocean", "deep_lukewarm_ocean", "frozen_ocean", "ocean", "cold_ocean", "lukewarm_ocean", "warm_ocean", "river", "frozen_river", "swamp", "mangrove_swamp"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.WITHOUT_ZOMBIE_SIEGES, List.of("mushroom_fields"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.WITHOUT_PATROL_SPAWNS, List.of("mushroom_fields"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS, Arrays.asList("the_void", "hub"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.SPAWNS_COLD_VARIANT_FROGS, Arrays.asList("snowy_plains", "ice_spikes", "frozen_peaks", "jagged_peaks", "snowy_slopes", "frozen_ocean", "deep_frozen_ocean", "grove", "deep_dark", "frozen_river", "snowy_taiga", "snowy_beach", "the_end", "end_highlands", "end_midlands", "small_end_islands", "end_barrens"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.SPAWNS_WARM_VARIANT_FROGS, Arrays.asList("desert", "warm_ocean", "bamboo_jungle", "jungle", "sparse_jungle", "savanna", "savanna_plateau", "windswept_savanna", "nether_wastes", "soul_sand_valley", "crimson_forest", "warped_forest", "basalt_deltas", "badlands", "eroded_badlands", "wooded_badlands", "mangrove_swamp"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS, Arrays.asList("snowy_plains", "ice_spikes", "frozen_peaks", "jagged_peaks", "snowy_slopes", "frozen_ocean", "deep_frozen_ocean", "grove", "deep_dark", "frozen_river", "snowy_taiga", "snowy_beach", "the_end", "end_highlands", "end_midlands", "small_end_islands", "end_barrens", "cold_ocean", "deep_cold_ocean", "old_growth_pine_taiga", "old_growth_spruce_taiga", "taiga", "windswept_forest", "windswept_gravelly_hills", "windswept_hills", "stony_peaks"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS, Arrays.asList("desert", "warm_ocean", "bamboo_jungle", "jungle", "sparse_jungle", "savanna", "savanna_plateau", "windswept_savanna", "nether_wastes", "soul_sand_valley", "crimson_forest", "warped_forest", "basalt_deltas", "badlands", "eroded_badlands", "wooded_badlands", "mangrove_swamp", "deep_lukewarm_ocean", "lukewarm_ocean"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.SPAWNS_GOLD_RABBITS, List.of("desert"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.SPAWNS_WHITE_RABBITS, Arrays.asList("snowy_plains", "ice_spikes", "frozen_ocean", "snowy_taiga", "frozen_river", "snowy_beach", "frozen_peaks", "jagged_peaks", "snowy_slopes", "grove"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.REDUCE_WATER_AMBIENT_SPAWNS, Arrays.asList("river", "frozen_river"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT, List.of("lush_caves"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.POLAR_BEARS_SPAWN_ON_ALTERNATE_BLOCKS, Arrays.asList("frozen_ocean", "deep_frozen_ocean"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.MORE_FREQUENT_DROWNED_SPAWNS, Arrays.asList("river", "frozen_river"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS, Arrays.asList("swamp", "mangrove_swamp"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.SPAWNS_SNOW_FOXES, Arrays.asList("snowy_plains", "ice_spikes", "frozen_ocean", "snowy_taiga", "frozen_river", "snowy_beach", "frozen_peaks", "jagged_peaks", "snowy_slopes", "grove"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.INCREASED_FIRE_BURNOUT, Arrays.asList("bamboo_jungle", "mushroom_fields", "mangrove_swamp", "snowy_slopes", "frozen_peaks", "jagged_peaks", "swamp", "jungle"), path, modifiedIdentifiers, identifier);
            createData(BiomeTags.SNOW_GOLEM_MELTS, Arrays.asList("badlands", "basalt_deltas", "crimson_forest", "desert", "eroded_badlands", "nether_wastes", "savanna", "savanna_plateau", "soul_sand_valley", "warped_forest", "windswept_savanna", "wooded_badlands"), path, modifiedIdentifiers, identifier);
        }
    }
}
