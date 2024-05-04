package com.sammy.malum.core.systems.spirit;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.AbstractWorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;

import java.awt.*;

public class UmbralSpiritMotif extends SpiritVisualMotif {

    public UmbralSpiritMotif(Color primaryColor, Color secondaryColor, float mainColorCoefficient, Easing mainColorEasing) {
        super(4f, primaryColor, secondaryColor, mainColorCoefficient, mainColorEasing);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <K extends AbstractWorldParticleBuilder<K, ?>> void applyWorldParticleChanges(K builder) {
        builder.setRenderType(LodestoneWorldParticleRenderType.LUMITRANSPARENT);
        builder.getScaleData().multiplyCoefficient(1.5f);
        builder.getTransparencyData().multiplyValue(4f).multiplyCoefficient(1.5f);
        builder.multiplyLifetime(2.5f);
    }
}
