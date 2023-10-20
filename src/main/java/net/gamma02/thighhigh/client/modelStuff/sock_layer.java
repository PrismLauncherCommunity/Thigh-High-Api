package net.gamma02.thighhigh.client.modelStuff;// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.gamma02.thighhigh.ThighHighs;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathConstants;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class sock_layer<T extends LivingEntity> extends BipedEntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
//	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(ThighHighs.resource("sock_layer"), "main");
//	private final ModelPart RightLeg;
//	private final ModelPart LeftLeg;

	public sock_layer(ModelPart root) {
		super(root);
//		this.RightLeg = root.getChild("RightLegSock");
//		this.LeftLeg = root.getChild("LeftLegSock");
	}

	public static TexturedModelData createBodyLayer(Dilation dilation) {

		ModelData meshdefinition = BipedEntityModel.getModelData(dilation, 0.0F);

		ModelPartData partdefinition = meshdefinition.getRoot();

		ModelPartData RightLeg = partdefinition.addChild("RightLegSock", ModelPartBuilder.create().uv(12, 12).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(-0.1f)), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

		ModelPartData LeftLeg = partdefinition.addChild("LeftLegSock", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(-0.1f)), ModelTransform.pivot(1.9F, 12.0F, 0.0F));

		return TexturedModelData.of(meshdefinition, 32, 32);
	}


	public void copySockStateFrom(BipedEntityModel<T> model) {

		this.sneaking = model.sneaking;
		model.rightLeg.copyTransform(this.rightLeg);
		model.leftLeg.copyTransform(this.leftLeg);
	}

	public static TexturedModelData getModelData(float dilation) {
		ModelData modelData = BipedEntityModel.getModelData(new Dilation(0.26f + dilation), 0.0f);
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(12, 12).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, new Dilation(0.26f).add(dilation-1)), ModelTransform.pivot(-1.9f, 12.0f, 0.0f));
		modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, new Dilation(0.26f).add(dilation-1)), ModelTransform.pivot(1.9f, 12.0f, 0.0f));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void render(MatrixStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//		poseStack.scale(0.8f, 0.96f, 0.7f);
		this.rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}