// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.engine.rendering.world.selection;

import org.lwjgl.opengl.GL11;
import org.terasology.gestalt.module.sandbox.API;
import org.terasology.engine.math.JomlUtil;
import org.terasology.math.geom.Rect2f;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.rendering.assets.material.Material;
import org.terasology.engine.rendering.assets.mesh.Mesh;
import org.terasology.engine.rendering.assets.shader.ShaderProgramFeature;
import org.terasology.engine.rendering.assets.texture.Texture;
import org.terasology.engine.rendering.assets.texture.TextureRegionAsset;
import org.terasology.engine.rendering.primitives.Tessellator;
import org.terasology.engine.rendering.primitives.TessellatorHelper;
import org.terasology.engine.rendering.world.WorldRenderer;
import org.terasology.engine.utilities.Assets;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslated;

/**
 * Renders a selection. Is used by the BlockSelectionSystem.
 * <br><br>
 * TODO total overhaul of this class. its neither good code, nor optimized in any way!
 *
 */
@API
public class BlockSelectionRenderer {
    private Mesh overlayMesh;
    private Mesh overlayMesh2;
    private Texture effectsTexture;
    private Material defaultTextured;
    private Rect2f textureRegion = Rect2f.createFromMinAndSize(0f, 0f, 1f, 1f);

    public BlockSelectionRenderer(Texture effectsTexture) {
        this.effectsTexture = effectsTexture;
        initialize();
    }

    private void initialize() {
        Tessellator tessellator = new Tessellator();
        TessellatorHelper.addBlockMesh(tessellator, new org.joml.Vector4f(1, 1, 1, 1f), JomlUtil.from(textureRegion.min()), JomlUtil.from(textureRegion.size()), 1.001f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
        overlayMesh = tessellator.generateMesh();
        tessellator = new Tessellator();
        TessellatorHelper.addBlockMesh(tessellator, new org.joml.Vector4f(1, 1, 1, .2f), JomlUtil.from(textureRegion.min()), JomlUtil.from(textureRegion.size()), 1.001f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
        overlayMesh2 = tessellator.generateMesh();
        defaultTextured = Assets.getMaterial("engine:prog.defaultTextured").get();
    }

    public void setEffectsTexture(TextureRegionAsset textureRegionAsset) {
        setEffectsTexture(textureRegionAsset.getTexture());
        textureRegion = JomlUtil.from(textureRegionAsset.getRegion());
        // reinitialize to recreate the mesh's UV coordinates for this textureRegion
        initialize();
    }

    public void setEffectsTexture(Texture newEffectsTexture) {
        if ((effectsTexture.getWidth() == newEffectsTexture.getWidth()) && (effectsTexture.getHeight() == newEffectsTexture.getHeight())) {
            this.effectsTexture = newEffectsTexture;
        } else {
            // This should not be possible with the current BlockSelectionRenderSystem implementation
            throw new RuntimeException("New effectsTexture must have same height and width as the original effectsTexture");
        }
    }

    public void beginRenderOverlay() {
        if (effectsTexture == null || !effectsTexture.isLoaded()) {
            return;
        }

        defaultTextured.activateFeature(ShaderProgramFeature.FEATURE_ALPHA_REJECT);
        defaultTextured.enable();

        glBindTexture(GL11.GL_TEXTURE_2D, effectsTexture.getId());

        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void endRenderOverlay() {
        glDisable(GL11.GL_BLEND);

        defaultTextured.deactivateFeature(ShaderProgramFeature.FEATURE_ALPHA_REJECT);
    }

    public void renderMark(Vector3i blockPos) {
        Vector3f cameraPos = getCameraPosition();

        glPushMatrix();
        glTranslated(blockPos.x - cameraPos.x, blockPos.y - cameraPos.y, blockPos.z - cameraPos.z);

        glMatrixMode(GL_MODELVIEW);

        overlayMesh.render();

        glPopMatrix();
    }

    public void renderMark2(Vector3i blockPos) {
        Vector3f cameraPos = getCameraPosition();

        glPushMatrix();
        glTranslated(blockPos.x - cameraPos.x, blockPos.y - cameraPos.y, blockPos.z - cameraPos.z);

        glMatrixMode(GL_MODELVIEW);

        overlayMesh2.render();

        glPopMatrix();
    }

    private Vector3f getCameraPosition() {
        return JomlUtil.from(CoreRegistry.get(WorldRenderer.class).getActiveCamera().getPosition());
    }

}