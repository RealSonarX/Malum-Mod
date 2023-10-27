package com.sammy.malum.visual_effects;

import com.sammy.malum.core.systems.spirit.*;
import com.sammy.malum.registry.client.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.helpers.*;
import team.lodestar.lodestone.setup.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.*;
import team.lodestar.lodestone.systems.particle.*;
import team.lodestar.lodestone.systems.particle.builder.*;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.particle.type.*;

import java.util.*;
import java.util.List;
import java.util.function.*;

import static net.minecraft.util.Mth.nextFloat;

public class SpiritLightSpecs {

    public static void coolLookingShinyThing(Level level, Vec3 pos, MalumSpiritType spiritType) {
        var centralLightSpecs = spiritLightSpecs(level, pos, spiritType);
        centralLightSpecs.getBuilder()
                .multiplyLifetime(0.6f)
                .modifyColorData(d -> d.multiplyCoefficient(0.5f))
                .modifyData(WorldParticleBuilder::getScaleData, d -> d.multiplyValue(6f))
                .modifyData(WorldParticleBuilder::getTransparencyData, d -> d.multiplyValue(3f));
        centralLightSpecs.getBloomBuilder()
                .multiplyLifetime(0.6f)
                .modifyColorData(d -> d.multiplyCoefficient(0.5f))
                .modifyData(WorldParticleBuilder::getScaleData, d -> d.multiplyValue(4f))
                .modifyData(WorldParticleBuilder::getTransparencyData, d -> d.multiplyValue(3f));
        centralLightSpecs.spawnParticles();
    }

    public static void rotatingLightSpecs(Level level, Vec3 pos, MalumSpiritType spiritType, float distance, int rotatingSpecs) {
        rotatingLightSpecs(level, pos, spiritType, distance, rotatingSpecs, b -> {});
    }
    public static void rotatingLightSpecs(Level level, Vec3 pos, MalumSpiritType spiritType, float distance, int rotatingSpecs, Consumer<WorldParticleBuilder> sharedModifier) {
        rotatingLightSpecs(level, pos, spiritType, distance, rotatingSpecs, sharedModifier, sharedModifier);
    }
    public static void rotatingLightSpecs(Level level, Vec3 pos, MalumSpiritType spiritType, float distance, int rotatingSpecs, Consumer<WorldParticleBuilder> lightSpecModifier, Consumer<WorldParticleBuilder> bloomModifier) {
        long gameTime = level.getGameTime();
        if (level.getGameTime() % 2L == 0) {
            for (int i = 0; i < rotatingSpecs; i++) {
                long offsetGameTime = gameTime + i * 120L;
                double yOffset = Math.sin((offsetGameTime % 360) / 30f) * 0.1f;
                Vec3 offsetPosition = DataHelper.rotatingRadialOffset(pos.add(0, yOffset, 0), distance, i, rotatingSpecs, gameTime, 160);

                var lightSpecs = spiritLightSpecs(level, offsetPosition, spiritType);
                lightSpecs.getBuilder().act(lightSpecModifier).multiplyLifetime(2f).modifyData(WorldParticleBuilder::getScaleData, d -> d.multiplyValue(1.2f));
                lightSpecs.getBloomBuilder().act(bloomModifier).act(b -> b.multiplyLifetime(1.4f).modifyData(List.of(b::getScaleData, b::getTransparencyData), d -> d.multiplyValue(0.6f)));
                lightSpecs.spawnParticles();
            }
        }
        var lightSpecs = spiritLightSpecs(level, pos, spiritType);
        lightSpecs.getBuilder().act(lightSpecModifier)
                .modifyData(WorldParticleBuilder::getScaleData, d -> d.multiplyValue(1.7f))
                .modifyData(WorldParticleBuilder::getTransparencyData, d -> d.multiplyValue(0.5f));

        lightSpecs.getBloomBuilder().act(bloomModifier)
                .modifyData(WorldParticleBuilder::getScaleData, d -> d.multiplyValue(1.3f))
                .modifyData(WorldParticleBuilder::getTransparencyData, d -> d.multiplyValue(0.75f));

        lightSpecs.spawnParticles();
    }

    public static ParticleEffectSpawner<WorldParticleBuilder> spiritLightSpecs(Level level, Vec3 pos, MalumSpiritType spiritType) {
        return spiritLightSpecs(level, pos, spiritType, ParticleRegistry.LIGHT_SPEC_SMALL);
    }

    public static ParticleEffectSpawner<WorldParticleBuilder> spiritLightSpecs(Level level, Vec3 pos, MalumSpiritType spiritType, Supplier<LodestoneParticleType> particle) {
        var rand = level.getRandom();
        final ColorParticleData colorData = spiritType.createMainColorData(0.7f).build();
        final SpinParticleData spinData = SpinParticleData.createRandomDirection(rand, nextFloat(rand, 0.05f, 0.1f)).randomSpinOffset(rand).build();
        final Consumer<LodestoneWorldParticleActor> slowDown = p -> p.setParticleMotion(p.getParticleSpeed().scale(0.95f));
        int lifetime = RandomHelper.randomBetween(rand, 10, 20);
        final WorldParticleBuilder worldParticleBuilder = WorldParticleBuilder.create(particle)
                .setTransparencyData(GenericParticleData.create(0.8f, 0f).build())
                .setSpinData(spinData)
                .setScaleData(GenericParticleData.create(0.025f, RandomHelper.randomBetween(rand, 0.2f, 0.3f), 0).build())
                .setColorData(colorData)
                .setLifetime(lifetime)
                .enableNoClip()
                .addActor(slowDown);
        final WorldParticleBuilder bloomParticleBuilder = SpiritLightSpecs.spiritBloom(level, spiritType, spinData, lifetime).addActor(slowDown);
        return new ParticleEffectSpawner<>(level, pos, worldParticleBuilder, bloomParticleBuilder);
    }

    public static WorldParticleBuilder spiritBloom(Level level, MalumSpiritType spiritType, SpinParticleData spinData, int lifetime) {
        var rand = level.random;
        return WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                .setTransparencyData(GenericParticleData.create(0.35f, 0f).build())
                .setSpinData(spinData)
                .setScaleData(GenericParticleData.create(0.04f, RandomHelper.randomBetween(rand, 0.08f, 0.14f), 0).setEasing(Easing.SINE_IN, Easing.SINE_IN_OUT).build())
                .setColorData(spiritType.createBloomColorData().build())
                .setLifetime(lifetime)
                .enableNoClip();
    }
}