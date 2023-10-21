package org.prismlauncher.thighhighapi.client.modelStuff;// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class sock_layer<T extends LivingEntity> extends BipedEntityModel<T> {

	public sock_layer(ModelPart root) {
		super(root);
	}


	public void copySockStateFrom(BipedEntityModel<T> model) {

		this.sneaking = model.sneaking;
		model.rightLeg.copyTransform(this.rightLeg);
		model.leftLeg.copyTransform(this.leftLeg);
	}

	//gets the textured model data for registering
	public static TexturedModelData getModelData(float dilation) {
		ModelData modelData = BipedEntityModel.getModelData(new Dilation(0.251f), 0.0f);
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(12, 12).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12f, 4.0f, new Dilation(0.251f)), ModelTransform.pivot(-1.9f, 12.0f, 0.0f));
		modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12f, 4.0f, new Dilation(0.251f)), ModelTransform.pivot(1.9f, 12.0f, 0.0f));
		return TexturedModelData.of(modelData, 32, 32);
	}

	//renders JUST the right and left leg
	@Override
	public void render(MatrixStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		this.leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}