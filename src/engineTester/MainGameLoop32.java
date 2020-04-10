package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Camera31;
import entities.Entity;
import entities.Light;
import entities.PlayerWater04;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer31;
import skybox.Sky;
import terrains.Terrain;
import terrains.World;
import terrains.World32;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer30;
import water.WaterShader30;

// OpenGL 3D Game Tutorial 32: Font Rendering
// https://www.youtube.com/watch?v=mnIQEQoHHCU&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP&index=32

// Hiero: A bitmap font packing tool
// https://libgdx.badlogicgames.com/tools.html
// https://github.com/libgdx/libgdx/wiki/Hiero
// http://www.angelcode.com/products/bmfont/
// https://github.com/libgdx/libgdx/wiki/Bitmap-fonts

public class MainGameLoop32
{
    boolean vsync = true;
	
	String tutorial = "OpenGL 3D Game Tutorial 32: Font Rendering";
	String subSubTitle = "Use keys w, a, s, d to move player, use mouse to control camera";
	 //"Use key c to swap to second camera, move it with arrow keys";

    List<Entity> entities = new ArrayList<>();
    List<Entity> normalMapEntities = new ArrayList<>();
    Random random = new Random(676451);
    Loader loader = new Loader();

    
    public void addEntity(World world, TexturedModel texturedModel, float rx, float rz, float scale) {
    	int numTextureRows = texturedModel.getTexture().getNumberOfRows();
    	int numSubTextures = numTextureRows * numTextureRows;
    	
    	float terrainSize = world.getTerrainSize();
	    float x = random.nextFloat() * terrainSize - terrainSize / 2;
		float z = random.nextFloat() * terrainSize - terrainSize / 2;
		float y = world.getHeightOfTerrain(x, z);
		if (y > world.getHeightOfWater(x, z)) {
	        float ry = random.nextFloat() * 360;
	        
	        if (numSubTextures > 1) {
	        	int textureIndex = random.nextInt(numSubTextures);
	        	entities.add(new Entity(texturedModel, textureIndex, new Vector3f(x, y, z), rx, ry, rz, scale));
	        }
	        else {
	        	entities.add(new Entity(texturedModel, new Vector3f(x, y, z), rx, ry, rz, scale));
	        }
		}
    }
	
    public MainGameLoop32() {
        float terrainSize = 2000;
        
        float terrainMaxHeight = 520;
        float rocksYOffset = terrainMaxHeight * 0.4075f;
        float waterHeight = terrainMaxHeight * 0.11f;

    	String title = tutorial.split(":")[0].trim();
    	String subTitle = tutorial.split(":")[1].trim();
    	
    	DisplayManager.createDisplay(tutorial);
        MasterRenderer31 renderer = new MasterRenderer31(loader);
    	DisplayManager.setVSync(vsync);

        TextMaster.init(loader);
        if (title.length() > 0) {
	        FontType font = new FontType(loader.loadFontTextureAtlas("candara"), new File("res/fonts/candara.fnt"));
	        GUIText text = new GUIText(title, 1.3f, font, new Vector2f(0.0f, 0.85f), 0.3f, true);
	        text.setColor(0.1f, 0.1f, 0.4f);
        }
        if (subTitle.length() > 0) {
        	FontType font2 = new FontType(loader.loadFontTextureAtlas("candara"), new File("res/fonts/candara.fnt"));
        	GUIText text2 = new GUIText(subTitle, 1f, font2, new Vector2f(0.0f, 0.9f), 0.3f, true);
        	text2.setColor(0.4f, 0.1f, 0.1f);
        }
        if (subSubTitle.length() > 0) {
	        FontType font3 = new FontType(loader.loadFontTextureAtlas("candara"), new File("res/fonts/candara.fnt"));
	        GUIText text3 = new GUIText(subSubTitle, 0.7f, font3, new Vector2f(0.0f, 0.95f), 0.3f, true);
	        text3.setColor(0.1f, 0.4f, 0.1f);
        }
        
        World world = new World32(loader, terrainSize, terrainMaxHeight, waterHeight);
        List<Terrain> terrains = world.getTerrains();

        // *****************************************

        TexturedModel treeModel = loader.createTexturedModel("tree", "tree", 1, 0);
        TexturedModel lowPolyTreeModel = loader.createTexturedModel("lowPolyTree", "lowPolyTree4", 2, 1, 0, false, false);
        TexturedModel pineModel = loader.createTexturedModel("pine", "pine", 10, 0.5f);
        TexturedModel grassModel = loader.createTexturedModel("grassModel", "grassTexture", 1, 0, true, true);
        TexturedModel flowerModel = loader.createTexturedModel("grassModel", "flower", 1, 0, true, true);
        TexturedModel fernModel = loader.createTexturedModel("fern", "fern4", 2, 1, 0, true, false);
        TexturedModel rocksModel = loader.createTexturedModel("rocks", "rocks", 10, 1);
        TexturedModel toonRocksModel = loader.createTexturedModel("toonRocks", "toonRocks", 10, 1);
        TexturedModel boxModel = loader.createTexturedModel("box", "box", 10, 1);
        TexturedModel stallModel = loader.createTexturedModel("stall", "stallTexture", 15, 1);
        TexturedModel oldBarrelModel = loader.createTexturedModel("barrel", "barrel", 20, 0.5f);
        TexturedModel exampleModel = loader.createTexturedModel("example", "white", 1, 0);
        TexturedModel lampModel = loader.createTexturedModel("lamp", "lamp", 1, 0, false, true);

        //******************NORMAL MAP MODELS************************

        TexturedModel barrelModel = loader.createTexturedModel("barrel", "barrel", "barrelNormal", 10, 0.5f);
        TexturedModel crateModel = loader.createTexturedModel("crate", "crate", "crateNormal", 10, 0.5f);
        TexturedModel boulderModel = loader.createTexturedModel("boulder", "boulder", "boulderNormal", 10, 0.5f);
        TexturedModel footballModel = loader.createTexturedModel("foot", "foot", "footNormal", 10, 0.5f);

        //************ENTITIES*******************

        Vector3f barrelPosition = world.getTerrainPoint(75, 75, 5);
        Entity entity = new Entity(barrelModel, barrelPosition, 1f);
        normalMapEntities.add(entity);

        Vector3f boulderPosition = world.getTerrainPoint(15, 75, 5);
        Entity entity2 = new Entity(boulderModel, boulderPosition, 2f);
        normalMapEntities.add(entity2);

        Vector3f cratePosition = world.getTerrainPoint(55, 75, 5);
        Entity entity3 = new Entity(crateModel, cratePosition, 0.04f);
        normalMapEntities.add(entity3);

        Vector3f footballPosition = world.getTerrainPoint(155, 75, 5);
        Entity entity4 = new Entity(footballModel, footballPosition, 1f);
        normalMapEntities.add(entity4);
       
        // the position of this should be at the center of the terrain tiles
        Vector3f rocksPosition = world.getTerrainPoint(terrainSize/2, terrainSize/2, rocksYOffset);
        entities.add(new Entity(rocksModel, rocksPosition, (terrainSize-1)/2));

        Vector3f boxPosition = world.getTerrainPoint(100, 300, 5);
        entities.add(new Entity(boxModel, boxPosition, 10));

        Vector3f stallPosition = world.getTerrainPoint(50, 250, 0);
        entities.add(new Entity(stallModel, stallPosition, 0, 50, 0, 2f));

        Vector3f oldBarrelPosition = world.getTerrainPoint(40, 240, 3);
        entities.add(new Entity(oldBarrelModel, oldBarrelPosition, 0.5f));

        Vector3f examplePosition = world.getTerrainPoint(30, 230, 0);
        entities.add(new Entity(exampleModel, examplePosition, 1f));

        Vector3f lamp9Position = world.getTerrainPoint(30, 220, 0);
        entities.add(new Entity(lampModel, lamp9Position, 1f));

        Vector3f box2Position = world.getTerrainPoint(225, 352, 5);
        Entity boxEntity = new Entity(boxModel, box2Position, 0, 25f, 0, 5f);
        entities.add(boxEntity);
        
        Sky sky = new Sky(0.388f, 0.552f, 0.678f, 0.0001f, 1f);
        
        List<Light> lights = new ArrayList<Light>();

        // OpenGL 3D Game Tutorial 25: Multiple Lights
        lights.add(new Light(new Vector3f(30000, 300, 0), new Vector3f(0.39f, 0.55f, 0.68f)));
       
        Vector3f lamp1Position = world.getTerrainPoint(126.3969f, 621.307f, 0);
        Vector3f light1Position = new Vector3f(lamp1Position.x, lamp1Position.y + 14, lamp1Position.z); 
        entities.add(new Entity(lampModel, lamp1Position, 1f));
        lights.add(new Light(light1Position, new Vector3f(3, 1, 1), new Vector3f(1, 0.01f, 0.002f)));

        Vector3f lamp2Position = world.getTerrainPoint(175.8717f, 287.5373f, 0);
        Vector3f light2Position = new Vector3f(lamp2Position.x, lamp2Position.y + 14, lamp2Position.z);
        entities.add(new Entity(lampModel, lamp2Position, 1f));
        lights.add(new Light(light2Position, new Vector3f(1, 2, 0), new Vector3f(1, 0.01f, 0.002f)));

        Vector3f lamp3Position = world.getTerrainPoint(62.69772f, 16.70355f, 0);
        Vector3f light3Position = new Vector3f(lamp3Position.x, lamp3Position.y + 14, lamp3Position.z);
        Entity lamp3Entity = new Entity(lampModel, lamp3Position, 1f);
        entities.add(lamp3Entity);
        Light lamp3Light = new Light(light3Position, new Vector3f(12, 12, 8), new Vector3f(1, 0.01f, 0.002f));
        lights.add(lamp3Light);
        
        for (int i = 0; i < 200; i++) {
        	if (i % 3 == 0) {
        		addEntity(world, grassModel, 0, 0, 1.8f);
        		addEntity(world, flowerModel, 0, 0, 2.3f);
        	}

        	if (i % 2 == 0) {
        		addEntity(world, fernModel, 10 * random.nextFloat() - 5, 10 * random.nextFloat() - 5, 0.9f);
        		
	            // low poly tree "bobble"
        		addEntity(world, lowPolyTreeModel, 4 * random.nextFloat() - 2, 4 * random.nextFloat() - 2, random.nextFloat() * 0.1f + 0.6f);
	
        		addEntity(world, treeModel,  4 * random.nextFloat() - 2, 4 * random.nextFloat() - 2, random.nextFloat() * 1f + 4f);
	        	addEntity(world, pineModel,  4 * random.nextFloat() - 2, 4 * random.nextFloat() - 2, random.nextFloat() * 4f + 1f);
	        	
	        	addEntity(world, toonRocksModel, 0, 0, 4 * random.nextFloat());
        	}
        }

    	Vector3f playerPosition = world.getTerrainPoint(0, 0, 0);
        TexturedModel playerModel = loader.createTexturedModel("person", "playerTexture", 1, 0);
        PlayerWater04 player = new PlayerWater04(playerModel, playerPosition, 0, 45, 0, 0.6f);
        entities.add(player);
        
        Camera camera = new Camera31(player);
        //camera.getPosition().translate(0, 20, 0);
       
        // Water
        WaterFrameBuffers buffers = new WaterFrameBuffers();
        
        WaterShader30 waterShader = new WaterShader30();
        WaterRenderer30 waterRenderer = new WaterRenderer30(loader, waterShader, renderer.getProjectionMatrix(), buffers);

        List<GuiTexture> guiTextures = new ArrayList<>();
        //GuiTexture refrGui = new GuiTexture(buffers.getRefractionTexture(), new Vector2f( 0.8f, -0.8f), new Vector2f(0.2f, 0.2f));
        //GuiTexture reflGui = new GuiTexture(buffers.getReflectionTexture(), new Vector2f(-0.8f, -0.8f), new Vector2f(0.2f, 0.2f));
        //GuiTexture reflGui = new GuiTexture(buffers.getReflectionTexture(), new Vector2f(-0.6f, -0.6f), new Vector2f(0.4f, 0.4f));
        //guiTextures.add(refrGui);
        //guiTextures.add(reflGui);

        //GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.7f, 0.5f), new Vector2f(0.125f, 0.125f));
        GuiTexture gui2 = new GuiTexture(loader.loadTexture("thinmatrix"), new Vector2f(0.7f, 0.8f), new Vector2f(0.2f, 0.2f));
        //GuiTexture gui3 = new GuiTexture(loader.loadTexture("health"), new Vector2f(0.8f, 0.9f), new Vector2f(0.2f, 0.2f));

        //guiTextures.add(gui);
        guiTextures.add(gui2);
        //guiTextures.add(gui3);

        GuiRenderer guiRenderer = new GuiRenderer(loader);
        
        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), world);

        //****************Game Loop Below*********************
        
        while (!Display.isCloseRequested()) {
        	
        	player.move(world);
        	       	
        	camera.move();
        	
            //entity.increaseRotation(0.1f, 0.2f, 0.3f);
            //entity2.increaseRotation(0.3f, 0.1f, 0.2f);
            //entity3.increaseRotation(0.2f, 0.3f, 0.1f);
        	float dt = DisplayManager.getFrameTimeSeconds();
            entity4.increaseRotation(12f * dt , 20f * dt, 6f * dt);
        	
            picker.update();
            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
            if (terrainPoint != null) {
            	lamp3Entity.setPosition(terrainPoint);
            	lamp3Light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 14, terrainPoint.z));
            }
        	
        	GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

        	// render to reflection texture: set the clip plane to clip stuff above water
        	buffers.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - world.getHeightOfWater(0, 0));
            // change position and pitch of camera to render the reflection 
            camera.getPosition().y -= distance;
            camera.invertPitch();
        	renderer.renderScene(entities, normalMapEntities, terrains, lights, sky, camera, new Vector4f(0, 1, 0, -world.getHeightOfWater(0, 0)+1f), true);
            camera.getPosition().y += distance;
            camera.invertPitch();

        	// render to refraction texture: set the clip plane to clip stuff below water
        	buffers.bindRefractionFrameBuffer();
        	renderer.renderScene(entities, normalMapEntities, terrains, lights, sky, camera, new Vector4f(0, -1, 0, world.getHeightOfWater(0, 0)+1f), true);
        	
        	// render to screen: set the clip plane at a great height, so it won't clip anything
        	buffers.unbindCurrentFrameBuffer();
        	renderer.renderScene(entities, normalMapEntities, terrains, lights, sky, camera, new Vector4f(0, -1, 0, 1000000), false);

        	waterRenderer.render(world.getWaterTiles(), sky, camera, lights);
        	
        	guiRenderer.render(guiTextures);

        	TextMaster.render();
            
        	// frames = 0 means a new second
        	int frames = DisplayManager.updateDisplay();
            
            if (frames == 0) {
            	camera.printPosition();
                System.out.println("ray:          " + picker.getCurrentRay());
                System.out.println("terrainPoint: " + picker.getCurrentTerrainPoint());
            }
        }

        buffers.cleanUp();
        waterShader.cleanUp();
        TextMaster.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

	public static void main(String[] args) {
		new MainGameLoop32();
	}
}
