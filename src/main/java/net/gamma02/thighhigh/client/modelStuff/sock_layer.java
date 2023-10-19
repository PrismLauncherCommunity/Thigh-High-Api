package net.gamma02.thighhigh.client.modelStuff;// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.gamma02.thighhigh.ThighHighs;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
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
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;

	public sock_layer(ModelPart root) {
		super(root);
		this.RightLeg = root.getChild("RightLegSock");
		this.LeftLeg = root.getChild("LeftLegSock");
	}

	public static TexturedModelData createBodyLayer(Dilation dilation) {

		ModelData meshdefinition = BipedEntityModel.getModelData(dilation, 0.0F);

		ModelPartData partdefinition = meshdefinition.getRoot();

		ModelPartData RightLeg = partdefinition.addChild("RightLegSock", ModelPartBuilder.create().uv(12, 12).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

		ModelPartData LeftLeg = partdefinition.addChild("LeftLegSock", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(1.9F, 12.0F, 0.0F));

		return TexturedModelData.of(meshdefinition, 32, 32);
	}

	public void copySockStateFrom(BipedEntityModel<T> model) {
		this.copyStateTo(this);
		this.leftArmPose = model.leftArmPose;
		this.rightArmPose = model.rightArmPose;
		this.sneaking = model.sneaking;
		this.head.copyTransform(model.head);
		this.hat.copyTransform(model.hat);
		this.body.copyTransform(model.body);
		this.rightArm.copyTransform(model.rightArm);
		this.leftArm.copyTransform(model.leftArm);
		this.rightLeg.copyTransform(model.rightLeg);
		this.leftLeg.copyTransform(model.leftLeg);
		this.LeftLeg.copyTransform(model.leftLeg);
		this.RightLeg.copyTransform(model.rightLeg);
	}

	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
		boolean bl = livingEntity.getRoll() > 4;
		boolean bl2 = livingEntity.isInSwimmingPose();
		this.head.yaw = i * 0.017453292F;
		if (bl) {
			this.head.pitch = -0.7853982F;
		} else if (this.leaningPitch > 0.0F) {
			if (bl2) {
				this.head.pitch = this.lerpAngle(this.leaningPitch, this.head.pitch, -0.7853982F);
			} else {
				this.head.pitch = this.lerpAngle(this.leaningPitch, this.head.pitch, j * 0.017453292F);
			}
		} else {
			this.head.pitch = j * 0.017453292F;
		}

		this.body.yaw = 0.0F;
		this.rightArm.pivotZ = 0.0F;
		this.rightArm.pivotX = -5.0F;
		this.leftArm.pivotZ = 0.0F;
		this.leftArm.pivotX = 5.0F;
		float k = 1.0F;
		if (bl) {
			k = (float)livingEntity.getVelocity().lengthSquared();
			k /= 0.2F;
			k *= k * k;
		}

		if (k < 1.0F) {
			k = 1.0F;
		}

		this.rightArm.pitch = MathHelper.cos(f * 0.6662F + MathConstants.PI) * 2.0F * g * 0.5F / k;
		this.leftArm.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k;
		this.rightArm.roll = 0.0F;
		this.leftArm.roll = 0.0F;
		float pitchConstant = 1.75f;
		this.RightLeg.pitch = (MathHelper.cos(f * 0.6662F) * pitchConstant * g / k) ;
		this.LeftLeg.pitch = (MathHelper.cos(f * 0.6662F + MathConstants.PI) * pitchConstant * g / k) ;
		this.RightLeg.yaw = 0.006F;
		this.LeftLeg.yaw = -0.006F;
		this.RightLeg.roll = 0.006F;

		this.LeftLeg.roll = -0.006F;
		ModelPart var10000;
		if (this.riding) {
			var10000 = this.rightArm;
			var10000.pitch += -0.62831855F;
			var10000 = this.leftArm;
			var10000.pitch += -0.62831855F;
			this.RightLeg.pitch = -1.4137167F;
			this.RightLeg.yaw = 0.31415927F;
			this.RightLeg.roll = 0.07853982F;
			this.LeftLeg.pitch = -1.4137167F;
			this.LeftLeg.yaw = -0.31415927F;
			this.LeftLeg.roll = -0.07853982F;
		}

		this.rightArm.yaw = 0.0F;
		this.leftArm.yaw = 0.0F;
		boolean bl3 = livingEntity.getMainArm() == Arm.RIGHT;
		boolean bl4;
		if (livingEntity.isUsingItem()) {
			bl4 = livingEntity.getActiveHand() == Hand.MAIN_HAND;
			if (bl4 == bl3) {
				this.positionRightArm(livingEntity);
			} else {
				this.positionLeftArm(livingEntity);
			}
		} else {
			bl4 = bl3 ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
			if (bl3 != bl4) {
				this.positionLeftArm(livingEntity);
				this.positionRightArm(livingEntity);
			} else {
				this.positionRightArm(livingEntity);
				this.positionLeftArm(livingEntity);
			}
		}

		this.animateArms(livingEntity, h);
		if (this.sneaking) {
			this.body.pitch = 0.5F;
			var10000 = this.rightArm;
			var10000.pitch += 0.4F;
			var10000 = this.leftArm;
			var10000.pitch += 0.4F;
			this.RightLeg.pivotZ = 6F;
			this.LeftLeg.pivotZ = 6F;
			this.RightLeg.pivotY = 12F;
			this.LeftLeg.pivotY = 12F;
//			this.RightLeg.
			this.head.pivotY = 4.2F;
			this.body.pivotY = 3.2F;
			this.leftArm.pivotY = 5.2F;
			this.rightArm.pivotY = 5.2F;
		} else {
			this.body.pitch = 0.0F;
			this.RightLeg.pivotZ = 0.0F;
			this.LeftLeg.pivotZ = 0.0F;
			this.RightLeg.pivotY = 12.0F;
			this.LeftLeg.pivotY = 12.0F;
			this.head.pivotY = 0.0F;
			this.body.pivotY = 0.0F;
			this.leftArm.pivotY = 2.0F;
			this.rightArm.pivotY = 2.0F;
		}

		if (this.rightArmPose != BipedEntityModel.ArmPose.SPYGLASS) {
			CrossbowPosing.swingArm(this.rightArm, h, 1.0F);
		}

		if (this.leftArmPose != BipedEntityModel.ArmPose.SPYGLASS) {
			CrossbowPosing.swingArm(this.leftArm, h, -1.0F);
		}

		if (this.leaningPitch > 0.0F) {
			float l = f % 26.0F;
			Arm arm = this.getPreferredArm(livingEntity);
			float m = arm == Arm.RIGHT && this.handSwingProgress > 0.0F ? 0.0F : this.leaningPitch;
			float n = arm == Arm.LEFT && this.handSwingProgress > 0.0F ? 0.0F : this.leaningPitch;
			float o;
			if (!livingEntity.isUsingItem()) {
				if (l < 14.0F) {
					this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 0.0F);
					this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 0.0F);
					this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, 3.1415927F);
					this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, 3.1415927F);
					this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, 3.1415927F + 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
					this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 3.1415927F - 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
				} else if (l >= 14.0F && l < 22.0F) {
					o = (l - 14.0F) / 8.0F;
					this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 1.5707964F * o);
					this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 1.5707964F * o);
					this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, 3.1415927F);
					this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, 3.1415927F);
					this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, 5.012389F - 1.8707964F * o);
					this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 1.2707963F + 1.8707964F * o);
				} else if (l >= 22.0F && l < 26.0F) {
					o = (l - 22.0F) / 4.0F;
					this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 1.5707964F - 1.5707964F * o);
					this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 1.5707964F - 1.5707964F * o);
					this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, 3.1415927F);
					this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, 3.1415927F);
					this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, 3.1415927F);
					this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 3.1415927F);
				}
			}

			o = 0.3F;
			float p = 0.33333334F;
			this.LeftLeg.pitch = MathHelper.lerp(this.leaningPitch, this.LeftLeg.pitch, 0.3F * MathHelper.cos(f * 0.33333334F + 3.1415927F));
			this.RightLeg.pitch = MathHelper.lerp(this.leaningPitch, this.RightLeg.pitch, 0.3F * MathHelper.cos(f * 0.33333334F));
		}

		this.hat.copyTransform(this.head);
	}

	private void positionRightArm(T entity) {
		switch (this.rightArmPose) {
			case EMPTY:
				this.rightArm.yaw = 0.0F;
				break;
			case BLOCK:
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.9424779F;
				this.rightArm.yaw = -0.5235988F;
				break;
			case ITEM:
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.31415927F;
				this.rightArm.yaw = 0.0F;
				break;
			case THROW_SPEAR:
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - 3.1415927F;
				this.rightArm.yaw = 0.0F;
				break;
			case BOW_AND_ARROW:
				this.rightArm.yaw = -0.1F + this.head.yaw;
				this.leftArm.yaw = 0.1F + this.head.yaw + 0.4F;
				this.rightArm.pitch = -1.5707964F + this.head.pitch;
				this.leftArm.pitch = -1.5707964F + this.head.pitch;
				break;
			case CROSSBOW_CHARGE:
				CrossbowPosing.charge(this.rightArm, this.leftArm, entity, true);
				break;
			case CROSSBOW_HOLD:
				CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
				break;
			case BRUSH:
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.62831855F;
				this.rightArm.yaw = 0.0F;
				break;
			case SPYGLASS:
				this.rightArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622F - (entity.isInSneakingPose() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
				this.rightArm.yaw = this.head.yaw - 0.2617994F;
				break;
			case TOOT_HORN:
				this.rightArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
				this.rightArm.yaw = this.head.yaw - 0.5235988F;
		}

	}
	private Arm getPreferredArm(T entity) {
		Arm arm = entity.getMainArm();
		return entity.preferredHand == Hand.MAIN_HAND ? arm : arm.getOpposite();
	}

	private float method_2807(float f) {
		return -65.0F * f + f * f;
	}

	private void positionLeftArm(T entity) {
		switch (this.leftArmPose) {
			case EMPTY:
				this.leftArm.yaw = 0.0F;
				break;
			case BLOCK:
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - 0.9424779F;
				this.leftArm.yaw = 0.5235988F;
				break;
			case ITEM:
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - 0.31415927F;
				this.leftArm.yaw = 0.0F;
				break;
			case THROW_SPEAR:
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - 3.1415927F;
				this.leftArm.yaw = 0.0F;
				break;
			case BOW_AND_ARROW:
				this.rightArm.yaw = -0.1F + this.head.yaw - 0.4F;
				this.leftArm.yaw = 0.1F + this.head.yaw;
				this.rightArm.pitch = -1.5707964F + this.head.pitch;
				this.leftArm.pitch = -1.5707964F + this.head.pitch;
				break;
			case CROSSBOW_CHARGE:
				CrossbowPosing.charge(this.rightArm, this.leftArm, entity, false);
				break;
			case CROSSBOW_HOLD:
				CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, false);
				break;
			case BRUSH:
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - 0.62831855F;
				this.leftArm.yaw = 0.0F;
				break;
			case SPYGLASS:
				this.leftArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622F - (entity.isInSneakingPose() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
				this.leftArm.yaw = this.head.yaw + 0.2617994F;
				break;
			case TOOT_HORN:
				this.leftArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
				this.leftArm.yaw = this.head.yaw + 0.5235988F;
		}

	}

	@Override
	public void render(MatrixStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.scale(0.8f, 0.96f, 0.7f);
		RightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}