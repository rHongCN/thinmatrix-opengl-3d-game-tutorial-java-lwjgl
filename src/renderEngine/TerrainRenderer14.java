package renderEngine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import terrains.Terrain;
import terrains.TerrainShader14;
import textures.ModelTexture;
import toolbox.Maths;

public class TerrainRenderer14 {

    private TerrainShader14 shader;

    public TerrainRenderer14(TerrainShader14 shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(List<Terrain> terrains) {
        for (Terrain terrain:terrains) {
        	prepareTerrain(terrain);
        	loadModelMatrix(terrain);
        	int vertexCount = terrain.getModel().getVertexCount();
			//GL11.glDrawElements(GL11.GL_LINE_LOOP, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        	GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        	unbindTerrain();
        }
    }

    public void prepareTerrain(Terrain terrain) {
    	RawModel rawModel = terrain.getModel();
    	GL30.glBindVertexArray(rawModel.getVaoID());
    	GL20.glEnableVertexAttribArray(0); // position
    	GL20.glEnableVertexAttribArray(1); // textureCoordinates
    	GL20.glEnableVertexAttribArray(2); // normal
    	ModelTexture texture = terrain.getTexture();
    	shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
    	GL13.glActiveTexture(GL13.GL_TEXTURE0);
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
    }
	
    // unbindTexturedModel
    public void unbindTerrain() {
    	GL20.glDisableVertexAttribArray(0);
    	GL20.glDisableVertexAttribArray(1);
    	GL20.glDisableVertexAttribArray(2);
    	GL30.glBindVertexArray(0);
    }
    
    private void loadModelMatrix(Terrain terrain) {
    	Vector3f translation = terrain.getPosition();
    	float rx = 0; //terrain.getRotX();
    	float ry = 0; //terrain.getRotY();
    	float rz = 0; //terrain.getRotZ();
    	float scale = 1;
    	
    	Matrix4f transformationMatrix = Maths.createTransformationMatrix(
    			translation, rx, ry, rz, scale);
    	shader.loadTransformationMatrix(transformationMatrix);
    }
}
