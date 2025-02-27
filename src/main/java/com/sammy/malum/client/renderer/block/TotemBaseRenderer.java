package com.sammy.malum.client.renderer.block;

import com.mojang.blaze3d.vertex.*;
import com.sammy.malum.*;
import com.sammy.malum.client.*;
import com.sammy.malum.common.block.curiosities.totem.*;
import com.sammy.malum.common.spiritrite.*;
import com.sammy.malum.core.systems.spirit.*;
import com.sammy.malum.registry.client.*;
import com.sammy.malum.registry.common.item.*;
import net.minecraft.client.*;
import net.minecraft.client.player.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.*;
import net.minecraft.util.*;
import net.minecraft.world.item.*;
import net.minecraftforge.event.*;
import team.lodestar.lodestone.registry.client.*;
import team.lodestar.lodestone.systems.easing.*;
import team.lodestar.lodestone.systems.rendering.*;
import team.lodestar.lodestone.systems.rendering.rendeertype.*;

import static com.sammy.malum.client.RenderUtils.*;


public class TotemBaseRenderer implements BlockEntityRenderer<TotemBaseBlockEntity> {

    public static final RenderTypeToken AREA_COVERAGE_TEXTURE = RenderTypeToken.createToken(MalumMod.malumPath("textures/vfx/area_coverage.png"));

    private static float totemicStaffHeldTimer = 0;
    private static boolean isHoldingStaff;

    public TotemBaseRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void checkForTotemicStaff(TickEvent.ClientTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START)) {
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }
            final Item totemicStaff = ItemRegistry.TOTEMIC_STAFF.get();
            if ((player.getMainHandItem().getItem().equals(totemicStaff) || player.getOffhandItem().getItem().equals(totemicStaff))) {
                if (TotemBaseRenderer.totemicStaffHeldTimer < 20) {
                    TotemBaseRenderer.totemicStaffHeldTimer++;
                }
                isHoldingStaff = true;
            } else if (TotemBaseRenderer.totemicStaffHeldTimer > 0) {
                TotemBaseRenderer.totemicStaffHeldTimer--;
                isHoldingStaff = false;
            }
        }
    }

    @Override
    public void render(TotemBaseBlockEntity blockEntityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (totemicStaffHeldTimer > 0f) {
            if (blockEntityIn.cachedRadiusRite == null) {
                return;
            }
            float staffTimer = Mth.clamp((totemicStaffHeldTimer + (isHoldingStaff ? 1 : -1) * partialTicks), 0, 20);
            float totemTimer = Mth.clamp((blockEntityIn.radiusVisibility + (blockEntityIn.isActiveOrAssembling() ? 1 : -1) * partialTicks), 0, 40);
            float scalar = Easing.SINE_IN_OUT.ease(staffTimer / 20f, 0, 1, 1) * Easing.SINE_IN_OUT.ease(totemTimer / 40f, 0, 1, 1);
            MalumSpiritType spiritType = blockEntityIn.cachedRadiusRite.getIdentifyingSpirit();
            TotemicRiteEffect riteEffect = blockEntityIn.cachedRadiusRite.getRiteEffect(blockEntityIn.isSoulwood);
            BlockPos riteEffectCenter = riteEffect.getRiteEffectCenter(blockEntityIn);
            BlockPos offset = riteEffectCenter.subtract(blockEntityIn.getBlockPos());
            int width = riteEffect.getRiteEffectHorizontalRadius();
            if (width > 1) {
                width = width * 2 + 1;
            }
            int height = riteEffect.getRiteEffectVerticalRadius();
            if (height > 1) {
                height = height * 2 + 1;
            }
            float shaderWidth = width * 32;
            float shaderHeight = height * 32;
            float distortion = 6f+height/2f;
            float sideDistortion = 6f+width/2f;
            final LodestoneRenderType renderType = RenderTypeRegistry.ADDITIVE_DISTORTED_TEXTURE.applyWithModifierAndCache(AREA_COVERAGE_TEXTURE, b -> b.setCullState(LodestoneRenderTypeRegistry.NO_CULL));
            float index = shaderWidth + distortion;
            float sideIndex = shaderWidth*shaderHeight + sideDistortion;

            var builder = SpiritBasedWorldVFXBuilder.create(spiritType)
                    .setRenderType(LodestoneRenderTypeRegistry.applyUniformChanges(LodestoneRenderTypeRegistry.copyAndStore(index, renderType), s -> {
                        s.safeGetUniform("Speed").set(1500f);
                        s.safeGetUniform("Distortion").set(distortion);
                        s.safeGetUniform("Width").set(shaderWidth);
                        s.safeGetUniform("Height").set(shaderWidth);
                    }))
                    .setColor(spiritType.getPrimaryColor(), 0.7f * scalar);
            var sideBuilder = SpiritBasedWorldVFXBuilder.create(spiritType)
                    .setRenderType(LodestoneRenderTypeRegistry.applyUniformChanges(LodestoneRenderTypeRegistry.copyAndStore(sideIndex, renderType), s -> {
                        s.safeGetUniform("Speed").set(1500f);
                        s.safeGetUniform("Distortion").set(sideDistortion);
                        s.safeGetUniform("Width").set(shaderWidth);
                        s.safeGetUniform("Height").set(shaderHeight);
                    }))
                    .setColor(spiritType.getPrimaryColor(), 0.7f * scalar);


            poseStack.pushPose();
            poseStack.translate(offset.getX(), offset.getY(), offset.getZ());


            RenderUtils.CubeVertexData cubeVertexData = RenderUtils.makeCubePositions(width, height)
                    .applyWobble(0, 0.5f, 0.01f);
            RenderUtils.CubeVertexData inverse = RenderUtils.makeCubePositions(-width, -height)
                    .applyWobble(0, 0.5f, 0.01f);

            drawCube(poseStack, builder, sideBuilder, 1.05f, cubeVertexData);
            builder.setUV(0, 1, 1, 0).setColor(spiritType.getSecondaryColor(), 0.6f * scalar);
            sideBuilder.setUV(0, 1, 1, 0).setColor(spiritType.getSecondaryColor(), 0.6f * scalar);
            drawCube(poseStack, builder, sideBuilder, 1.05f, inverse);

            poseStack.popPose();
        }
    }
}