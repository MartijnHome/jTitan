package sliedregt.martijn.game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.joml.Vector3f;

import com.formdev.flatlaf.FlatDarculaLaf;

import sliedregt.martijn.jtitan.MainGameLogic;
import sliedregt.martijn.jtitan.api.graphics.opengl.GL46;
import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.config.graphics.DisplaySettings;
import sliedregt.martijn.jtitan.config.graphics.RenderSettings;
import sliedregt.martijn.jtitan.datatype.HeightMap;
import sliedregt.martijn.jtitan.datatype.RenderBatch;
import sliedregt.martijn.jtitan.datatype.primal.Color;
import sliedregt.martijn.jtitan.datatype.primal.Direction;
import sliedregt.martijn.jtitan.datatype.primal.Material;
import sliedregt.martijn.jtitan.datatype.primal.Position;
import sliedregt.martijn.jtitan.datatype.primal.Rotation;
import sliedregt.martijn.jtitan.datatype.primal.Scale;
import sliedregt.martijn.jtitan.datatype.primal.Uniform;
import sliedregt.martijn.jtitan.datatype.resource.FrameBuffer;
import sliedregt.martijn.jtitan.datatype.resource.Mesh;
import sliedregt.martijn.jtitan.datatype.resource.Model;
import sliedregt.martijn.jtitan.datatype.resource.Shader;
import sliedregt.martijn.jtitan.datatype.resource.ShaderProgram;
import sliedregt.martijn.jtitan.datatype.resource.Texture;
import sliedregt.martijn.jtitan.datatype.scene.Camera;
import sliedregt.martijn.jtitan.datatype.scene.Light;
import sliedregt.martijn.jtitan.datatype.scene.Renderable;
import sliedregt.martijn.jtitan.datatype.scene.Scene;
import sliedregt.martijn.jtitan.datatype.task.EngineTask;
import sliedregt.martijn.jtitan.datatype.task.JTitanTask;
import sliedregt.martijn.jtitan.listener.InputListener;
import sliedregt.martijn.jtitan.listener.KeyListener;
import sliedregt.martijn.jtitan.physics.CollisionShape;
import sliedregt.martijn.jtitan.physics.shape.Cylinder;
import sliedregt.martijn.jtitan.physics.shape.EndlessCylinder;
import sliedregt.martijn.jtitan.physics.shape.Sphere;
import sliedregt.martijn.jtitan.util.MeshUtil;

public class Main extends MainGameLogic
{

	HeightMap 	h;				//The height map of the ground is used to put the player and scenery at the correct height

	String 		assetFolder;	//Assets folder
	JFrame 		frame;			//JFrame used for assets folder chooser
	Scene		scene;
	Camera		camera;
	Camera		reflectionCamera;
	FrameBuffer reflectionBuffer;
	FrameBuffer fogBuffer;
	Renderable	water;
	Renderable	underWater;
	Renderable	skybox;
	DisplaySettings displaySettings;
	
	ShaderProgram waterProgram;
	ShaderProgram underWaterProgram;
	ShaderProgram standardProgram;
	ShaderProgram reflectionProgram;
	ShaderProgram terrainProgram;
	ShaderProgram skyboxProgram;
	
	//Boat
	Renderable boat;
	CollisionShape boatCollision;
	float boatAngle = 0.0f;
	float boatAngle2 = 0.0f;
	float boatRadius = 71.0f;
	
	//Player
	Renderable 	player;
	float jumpVelocity = 0.0f;
	boolean jumping = false;
	boolean falling = false;
	float fallVelocity = 0.035f;
	float jumpPower = 0.8f;
	float gravity = 0.055f;
	float maxGravity = 1.0f;
	float moveSpeed = 0.2f;
	boolean playerOnBoat = false;
	boolean playerRunning = false;
	
	//Fence
	Renderable fence;
	Material mat;
	Renderable car;
	
	public static void main(String[] arg)
	{
		new Main();
	}

	public Main()
	{
		/*
		//Nimbus is cross platform and looks good
		try
		{
			FlatDarculaLaf.setup();
		} catch (Exception ignored)
		{
			//Critical error?
			System.exit(0);
		}
*/
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");

		displaySettings = new DisplaySettings();
		displaySettings.setResolution(new Dimension(1280, 800));
		displaySettings.setFullScreen(false);
		displaySettings.setMaxFPS(60);
		
		config = new Configuration(new GL46(), null, displaySettings);
		config.setTitle("jTitan Game Engine");
		config.setShowDebugFrame(true);
		config.setYieldInsteadOfSleep(false);
		config.setLoadMultiThreaded(false);
		config.setPriority(Thread.MAX_PRIORITY);

		boolean publicTest = false;
		
		if (publicTest)
		{
			String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			try
			{
				String decodedPath = URLDecoder.decode(path, "UTF-8");
				assetFolder = decodedPath.substring(0, path.lastIndexOf("/") + 1) + "assets";
				start();
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			assetFolder = "/home/martijnvs/Bureaublad/JTitan workspace/src";
			getAssetFolder();
		}

	}

	private void getAssetFolder()
	{
		frame = new JFrame();
		JPanel panel = new JPanel();
		JButton startButton = new JButton("Start");
		JButton selectButton = new JButton("Select");
		JTextField textField = new JTextField(assetFolder);

		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(textField);
		panel.add(selectButton);
		panel.setPreferredSize(new Dimension(400, 100));

    	if (new File(assetFolder + "/Shader/standard.frag").exists())
		{
    		startButton.setText("Start");
		} else
		{
			startButton.setText("Folder not valid!");
		}
		
		frame.setTitle("jTitan engine");
		frame.add(BorderLayout.NORTH, new JLabel("Choose asset folder"));
		frame.add(BorderLayout.CENTER, panel);
		frame.add(BorderLayout.SOUTH, startButton);
		frame.validate();
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.addWindowListener(new WindowListener()
		{

			@Override
			public void windowOpened(WindowEvent e)
			{}

			@Override
			public void windowClosing(WindowEvent e)
			{
				// TODO Auto-generated method stub
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e)
			{}

			@Override
			public void windowIconified(WindowEvent e)
			{}

			@Override
			public void windowDeiconified(WindowEvent e)
			{}

			@Override
			public void windowActivated(WindowEvent e)
			{}

			@Override
			public void windowDeactivated(WindowEvent e)
			{}
		});

		selectButton.addActionListener(e ->
		{
			JFileChooser chooser = new JFileChooser();
		    chooser.setCurrentDirectory(new java.io.File("."));
		    chooser.setDialogTitle("Please select asset folder");
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    //
		    // disable the "All files" option.
		    //
		    chooser.setAcceptAllFileFilterUsed(false);
		    //
		    if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
		    {
		    	assetFolder = chooser.getSelectedFile().toString();
		    	textField.setText(assetFolder);
		    	if (new File(assetFolder + "/Shader/standard.frag").exists())
				{
		    		startButton.setText("Start");
				} else
				{
					startButton.setText("Folder not valid!");
				}
		    }
		});

		startButton.addActionListener(e ->
		{
			if (new File(assetFolder + "/Shader/standard.frag").exists())
			{
				frame.setVisible(false);
				start();
			}
		});

	}

	private List<RenderBatch> makeRenderPipeline()
	{
		//This list holds all render batches
		List<RenderBatch> renderBatch = new ArrayList<>();
		
		//We need 2 cameras, one as the player's camera and the second for reflections
		camera = new Camera(Camera.MODE_3D_PERSP, displaySettings.getResolution());
		camera.setRotation(new Rotation(0.0f, 0.0f, 0.0f));
		reflectionCamera = new Camera(Camera.MODE_3D_PERSP, displaySettings.getResolution());

		Camera carCamera = new Camera(Camera.MODE_3D_PERSP, displaySettings.getResolution());
		carCamera.setPosition(car.getPosition().clone());
		carCamera.getPosition().setY(carCamera.getPosition().getY() + 5.0f);
		
		//We need a texture and a framebuffer to draw our water reflection
		Texture reflectionTexture = new Texture(Texture.TYPE_TEXTURE_2D);
		reflectionTexture.setRenderMode(Texture.RENDER_TEXTURE_CLAMP_EDGE);
		reflectionTexture.getDimension().setSize(displaySettings.getResolution());
		reflectionBuffer = new FrameBuffer(FrameBuffer.TYPE_REFLECTION_BUFFER, reflectionTexture);
		water.getModel().setReflectionTexture(reflectionTexture);
		rm.addResource(reflectionTexture);
		rm.addResource(reflectionBuffer);
		
		//We need a car texture and framebuffer
		Texture carReflectionTexture = new Texture(Texture.TYPE_TEXTURE_2D);
		carReflectionTexture.setRenderMode(Texture.RENDER_TEXTURE_CLAMP_EDGE);
		carReflectionTexture.getDimension().setSize(displaySettings.getResolution());
		FrameBuffer carBuffer = new FrameBuffer(FrameBuffer.TYPE_REFLECTION_BUFFER, carReflectionTexture);
		car.getModel().setReflectionTexture(carReflectionTexture);
		rm.addResource(carReflectionTexture);
		rm.addResource(carBuffer);
		
		//We need a texture and framebuffer for our fog
		Texture fogTexture = new Texture(Texture.TYPE_TEXTURE_2D);
		fogTexture.setRenderMode(Texture.RENDER_TEXTURE_CLAMP_EDGE);
		fogTexture.getDimension().setSize(displaySettings.getResolution().width, displaySettings.getResolution().height);
		fogBuffer = new FrameBuffer(FrameBuffer.TYPE_REFLECTION_BUFFER, fogTexture);
		rm.addResource(fogTexture);
		rm.addResource(fogBuffer);
		scene.getFog().setFogTexture(fogTexture);
		
		RenderBatch fogBatch = new RenderBatch(fogBuffer, camera, skybox, scene.getL(), scene.getFog());
		
		List<Renderable> carList = new ArrayList<Renderable>();
		carList.add(skybox);
		carList.add(player);
		RenderBatch carBatch = new RenderBatch(carBuffer, carCamera, carList, scene.getL(), scene.getFog())
		{
			@Override
			public void doAtPreRender()
			{
				carCamera.pointAtPosition(camera.getPosition());
			}
		};
		
		//Render our scene for a reflection texture, we hide the water
		//Also because this batch is the first to be executed we do logic game code inside doAtPreRender
		RenderBatch reflectionBatch = new RenderBatch(reflectionBuffer, reflectionCamera, scene.getR(), scene.getL(), scene.getFog())
		{
			@Override
			public void doAtPreRender()
			{
				
				
				reflectionCamera.getPosition().set(camera.getPosition().x, -(camera.getPosition().y), camera.getPosition().z, 1f);
				reflectionCamera.getRotation().set(-camera.getRotation().getX(), camera.getRotation().getY(), camera.getRotation().getZ());
				water.getRenderSettings().setVisible(false);
				
				//This makes the water move
				float a = ((float) waterProgram.getUniform().get(0).getValue()) - 0.18f;
				if (a < 0.0f)
					a = 360.0f - a;
			
				waterProgram.getUniform().get(0).setValue(a);
				waterProgram.getUniform().get(2).setValue(new float[] {player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ()});
				waterProgram.getUniform().get(4).setValue(new float[] {boat.getPosition().getX(), -2.75f, boat.getPosition().getZ()});
				
				//waterProgram.getUniform().get(2).setValue(b);
				underWaterProgram.getUniform().get(0).setValue(a);
				//Ignore underwater vertices to prevent reflection distortion
				terrainProgram.getUniform().get(0).setValue(1);
				skyboxProgram.getUniform().get(0).setValue(1);
				standardProgram.getUniform().get(0).setValue(1);
				reflectionProgram.getUniform().get(0).setValue(1);
				//Blue filter when camera is underwater
				terrainProgram.getUniform().get(1).setValue(0);
				
				//Move the boat
				boatAngle += 0.08f;
				if (boatAngle > 360.0f)
					boatAngle -= 360.0f;
				boatAngle2 += 2f;
				if (boatAngle2 > 360.0f)
					boatAngle2 -= 360.0f;
				
				float x = 55f + ((float) Math.sin(Math.toRadians(boatAngle)) * boatRadius * 2f);
				float z = ((float) Math.cos(Math.toRadians(boatAngle)) * boatRadius);
				float y = -4f +((float) Math.sin(Math.toRadians(boatAngle2)) * 0.7f);
				float angleZ = (float) Math.sin(Math.toRadians(boatAngle2)) * 1.5f;
				
				if (playerOnBoat)
				{
					Position old = boat.getPosition();
					float angleYDifference = (270f + (-boatAngle)) - boat.getRotation().getY();
					Vector3f difference = new Vector3f(x - old.x, y - old.y, z - old.z);
					player.getPosition().set(player.getPosition().x + difference.x, player.getPosition().y + difference.y, player.getPosition().z + difference.z, 1);
					player.getRotation().set(player.getRotation().getX(), player.getRotation().getY() + angleYDifference, player.getRotation().getZ());
				}
				
				boat.setRotation(new Rotation(angleZ, 270f + (-boatAngle), angleZ));
				boat.setPosition(new Position(x, y, z, 0.0f));
				et.getScene().getL().get(1).getPosition().set(x, 14.0f, z, 1.0f);
				et.getScene().getL().get(1).getDirection().set((float) Math.sin(Math.toRadians(90f + boatAngle)), 0f, (float) Math.cos(Math.toRadians(90f + boatAngle)));
				boatCollision.setPosition(new Position(x, y, z, 0.0f));

				
				//Player
				{
					Position n = player.getPosition().clone();
					if (jumping)
					{
						n.y += jumpVelocity;
						jumpVelocity -= fallVelocity;
						if (pm.isColliding(n) != null)
						{
							falling = false;
							jumping = false;
						}
					} else {
						if (falling)
						{
							jumpVelocity -= fallVelocity;
							n.y += jumpVelocity;
							if (pm.isColliding(n) != null)
								falling = false;
						} else {
							jumpVelocity = fallVelocity;
							n.y -= fallVelocity;
						}
					}
					
					if (!playerOnBoat)
					{
						if (boatCollision.isInside(n))
							playerOnBoat = true;
					} else
					{
						if (!boatCollision.isInside(n))
							playerOnBoat = false;
					}
					
					
					n = pm.check(player.getPosition(), n, 5, 0f);
					player.getPosition().set(n.x, n.y, n.z, 1f);
					
				
				}

				float terrainX = player.getPosition().x + 500.0f;
				float terrainZ = player.getPosition().z + 500.0f;
				terrainX = (terrainX / (1000f / ((float) h.getTx() - 1)));
				terrainZ = (terrainZ / (1000f / ((float) h.getTz() - 1)));

				float terrainHeight;
				//if ((terrainX >= 0 && terrainX < h.getTx() - 1) && (terrainZ >= 0 && terrainZ < h.getTz() - 1))
					terrainHeight = h.getHeightAtLocation(terrainX, terrainZ);
				//else
					//terrainHeight = 0.0f;

				if (player.getPosition().y < terrainHeight)
				{
					player.getPosition().y = terrainHeight;
					if (jumping)
						jumping = false;
					if (falling)
						falling = false;
				} else
				{
					if (player.getPosition().y > terrainHeight)
					{
						if (!jumping)
						{
							if(!falling && pm.isColliding(player.getPosition().clone()) == null)
								falling = true;
						}
					}
				}
				
				
				// Point camera at cube
				Position p = player.getPosition().clone();
				p.y += 4.0f;
				Position n = pm.check(camera.getPosition().clone(), camera.placeBehindPosition(p, 20.0f, player.getRotation().getY()), 5, 0.1f);
				
				terrainX = n.x + 500.0f;
				terrainZ = n.z + 500.0f;
				terrainX = (terrainX / (1000f / ((float) h.getTx() - 1)));
				terrainZ = (terrainZ / (1000f / ((float) h.getTz() - 1)));

				if ((terrainX >= 0 && terrainX < h.getTx() - 1) && (terrainZ >= 0 && terrainZ < h.getTz() - 1))
				{
					terrainHeight = h.getHeightAtLocation(terrainX, terrainZ);
					if (n.y < terrainHeight + 2.0f)
						n.y = terrainHeight + 2.0f;
				}
				
				camera.getPosition().set(n.x, n.y, n.z, 1f);
				camera.pointAtPosition(player.getPosition());
			}
			
			@Override
			public void doAtPostRender()
			{
				water.getRenderSettings().setVisible(true);
				
				//Ignore underwater vertices to prevent reflection distortion
				terrainProgram.getUniform().get(0).setValue(0);
				skyboxProgram.getUniform().get(0).setValue(0);
				standardProgram.getUniform().get(0).setValue(0);
				reflectionProgram.getUniform().get(0).setValue(0);
			}
		};
		

		//Render the player's camera
		RenderBatch playerBatch = new RenderBatch(null, camera, scene.getR(), scene.getL(), scene.getFog())
		{
			@Override
			public void doAtPreRender()
			{

				//Blue filter when camera is underwater
				terrainProgram.getUniform().get(1).setValue((camera.getPosition().y < 0.0f) ? 1 : 0);
			}
			
			@Override
			public void doAtPostRender()
			{
				//Ignore underwater vertices to prevent reflection distortion
				terrainProgram.getUniform().get(1).setValue(0);
			}
		};
		
		renderBatch.add(fogBatch);
		renderBatch.add(carBatch);
		renderBatch.add(reflectionBatch);
		renderBatch.add(playerBatch);
		
		return renderBatch;
	}

	@Override
	protected void atInitialize()
	{
		tm.addTask(new EngineTask(true)
		{
			@Override
			protected boolean doTask()
			{
				scene = makeScene();
				getEt().setRenderBatch(makeRenderPipeline());
				getEt().startScene(scene);
				/*
				getTm().addTask(new JTitanTask(false)
				{

					{
						setDescription(generateDescription());
						
					}
					
					@Override
					protected boolean doTask()
					{
						Position p = player.getPosition();
						Position f = fence.getPosition();
						
						float distance = (float) (Math.pow(p.getX() - f.getX(), 2) + Math.pow(p.getY() - f.getY(), 2) + Math.pow(p.getZ() - f.getZ(), 2));
						
						if (distance > 30)
							return false;
						
						float x = 0.0f;
						float z = 0.0f;
						float y = -1.0f;
						while (y < 3.5f)
						{
							x = -500.0f + ((float) Math.random() * 1000.0f);
							z = -500.0f + ((float) Math.random() * 1000.0f);
							float terrainX = x + 500.0f;
							float terrainZ = z + 500.0f;
							terrainX = (terrainX / (1000f / ((float) h.getTx() - 1)));
							terrainZ = (terrainZ / (1000f / ((float) h.getTz() - 1)));
							y = h.getHeightAtLocation(terrainX, terrainZ);
						}
						
						fence.getPosition().set(x, y, z, 1);
						setDescription(generateDescription());
						return false;
					}
					
					private String generateDescription()
					{
						return "Fence fetch quest - position: " +
								"X: " + fence.getPosition().x +
								"Z: " + fence.getPosition().z;
					}
				});
				*/
				return true;
			}
		});
		
	}

	@Override
	protected void atTerminate()
	{
		System.exit(0);
	}

	private Scene makeScene()
	{
		Scene s = new Scene();

		final Renderable[] rocks;
		final Renderable[] trees;
		final Renderable[] streetLights;
		
		mat = new Material(new Color(0.52f, 0.52f, 0.52f, 1f), new Color(0.92f, 0.92f, 0.92f, 1f),
				new Color(0.70f, 0.70f, 0.70f, 1.0f), 22f);
		
		makeStandardShaderProgram();
		makeTerrainShaderProgram();
		s.getR().add(makeGround());
		
		makeWaterShaderProgram();
		makeReflectionShaderProgram();
		makeSkyboxShaderProgram();
		makeUnderWaterProgram();
		

		player = makePlayer(); //Player
		rocks = makeRocks(); //Rocks
		skybox = makeSkybox();
		trees = makeTrees();
		water = makeWater();
		underWater = makeUnderWater();
		boat = makeShip();
		//streetLights = makeStreetLights();
		fence = makeFence();
		car = makeCar();
		
		s.getR().add(player);
		s.getR().add(water);
		s.getR().add(underWater);
		s.getR().add(boat);
		//s.getR().add(makeHut());
		s.getR().add(fence);
		//s.getR().add(makeCrate());
		s.getR().add(skybox);
		s.getR().add(car);
		
		for (Renderable r : rocks)
			s.getR().add(r);
		for (Renderable r : makeGrass())
			s.getR().add(r);
		for (Renderable r : trees)
			s.getR().add(r);


		s.getL().add(Light.DirectionalLight(new Direction(-0.15f, 0.7f, -0.55f), new Color(0.9f, 0.75f, 0.75f, 1.0f)));
		//s.getL().add(Light.SpotLight(new Position(0.0f, 0.0f, 0.0f, 0.0f), new Color(0.9f, 0.9f, 0.9f, 1.0f), 5000f, 5000f));
		s.getL().add(Light.FlashLight(new Position(0f, 0f, 0f, 0f), new Direction(0f, 0f, 0f), new Color(0.8f, 0.99f, 0.99f, 1.0f), 350f, 250f, 15f, 20f));
		s.getL().get(0).setPower(0.72f);
		s.getL().get(1).setPower(0.95f);

		/*
		for (Renderable r : streetLights)
		{
			s.getR().add(r);
			Position p = r.getPosition().clone();
			p.y += 25.0f;
			Direction d = new Direction(0.0f, -1.0f, 0.0f);
			float c = (float) Math.random();
			s.getL().add(Light.FlashLight(p, d, new Color(c, c, c, 1.0f), 35f, 30f, 110f, 130f));
			
		}
		*/
		
		// Pass cube to key controls
		setKeys(player);

		s.getFog().setEnabled(true);
		s.getFog().setStart(240f);
		s.getFog().setEnd(280f);
		s.getFog().setColor(new Color(0.33f, 0.24f, 0.33f, 1f));
		return s;
	}

	private void setKeys(final Renderable r)
	{
		input.addKeyListener(new KeyListener(InputListener.KEY_LEFT_SHIFT, KeyListener.STATE_PRESSED)
		{
			@Override
			public void run()
			{
				playerRunning = true;
			}
		});
		
		input.addKeyListener(new KeyListener(InputListener.KEY_LEFT_SHIFT, KeyListener.STATE_RELEASED)
		{
			@Override
			public void run()
			{
				playerRunning = false;
			}
		});
		
		
		
		input.addKeyListener(new KeyListener(InputListener.KEY_ESCAPE, KeyListener.STATE_PRESSED)
		{
			@Override
			public void run()
			{
				terminate();
			}
		});
		
		input.addKeyListener(new KeyListener(InputListener.KEY_SPACE, KeyListener.STATE_PRESSED)
		{
			@Override
			public void run()
			{
				if (!jumping && !falling)
				{
					jumping = true;
					jumpVelocity = jumpPower;
				}
			}
		});
		
		input.addKeyListener(new KeyListener(InputListener.KEY_W, KeyListener.STATE_PRESSED)
		{
			@Override
			public void run()
			{
				float speed = (playerRunning) ? moveSpeed * 2.0f : moveSpeed;
				float ay = (float) Math.toRadians(r.getRotation().getY());
				float dx = ((float) Math.sin(ay)) * speed;
				float dz = ((float) Math.cos(ay)) * -speed;

				Position n = r.getPosition().clone();
				n.x += dx;
				n.z += dz;
			

				
				float terrainX = n.x + 500.0f;
				float terrainZ = n.z + 500.0f;
				terrainX = (terrainX / (1000f / ((float) h.getTx() - 1)));
				terrainZ = (terrainZ / (1000f / ((float) h.getTz() - 1)));

				float terrainHeight = h.getHeightAtLocation(terrainX, terrainZ);
				
				if (!jumping)
				{
					float distanceX = r.getPosition().x - n.x;
					float distanceZ = r.getPosition().z - n.z;
					float distance = (float) Math.sqrt((distanceX * distanceX) + (distanceZ * distanceZ));
					float heightAngle = (float) Math.atan((terrainHeight - n.y) / distance);
					float speedFactor = (terrainHeight - n.y > 0.0f) ? (float) Math.pow(Math.cos(heightAngle), 6f) : 1f;
			
					n = r.getPosition().clone();
					n.x += dx * speedFactor;
					n.z += dz * speedFactor;
					
					terrainX = n.x + 500.0f;
					terrainZ = n.z + 500.0f;
					terrainX = (terrainX / (1000f / ((float) h.getTx() - 1)));
					terrainZ = (terrainZ / (1000f / ((float) h.getTz() - 1)));
					
					n.y = h.getHeightAtLocation(terrainX, terrainZ);
				}
				
				n = pm.check(r.getPosition(), n, 4, 0.01f);
				r.getPosition().x = n.x;
				r.getPosition().z = n.z;
				
				
			}
		});

		input.addKeyListener(new KeyListener(InputListener.KEY_S, KeyListener.STATE_PRESSED)
		{
			@Override
			public void run()
			{
				float speed = (playerRunning) ? moveSpeed * 2.0f : moveSpeed;
				float ay = (float) Math.toRadians(r.getRotation().getY());
				float dx = ((float) Math.sin(ay)) * -speed;
				float dz = ((float) Math.cos(ay)) * speed;

				Position n = r.getPosition().clone();
				n.x += dx;
				n.z += dz;
			

				
				float terrainX = n.x + 500.0f;
				float terrainZ = n.z + 500.0f;
				terrainX = (terrainX / (1000f / ((float) h.getTx() - 1)));
				terrainZ = (terrainZ / (1000f / ((float) h.getTz() - 1)));

				float terrainHeight = h.getHeightAtLocation(terrainX, terrainZ);
				
				if (!jumping)
				{
					float distanceX = r.getPosition().x - n.x;
					float distanceZ = r.getPosition().z - n.z;
					float distance = (float) Math.sqrt((distanceX * distanceX) + (distanceZ * distanceZ));
					float heightAngle = (float) Math.atan((terrainHeight - n.y) / distance);
					float speedFactor = (float) Math.pow(Math.cos(heightAngle), 6f);
			
					n = r.getPosition().clone();
					n.x += dx * speedFactor;
					n.z += dz * speedFactor;
					
					terrainX = n.x + 500.0f;
					terrainZ = n.z + 500.0f;
					terrainX = (terrainX / (1000f / ((float) h.getTx() - 1)));
					terrainZ = (terrainZ / (1000f / ((float) h.getTz() - 1)));
					
					n.y = h.getHeightAtLocation(terrainX, terrainZ);
				}
				
				n = pm.check(r.getPosition(), n, 4, 0.01f);
				r.getPosition().x = n.x;
				r.getPosition().z = n.z;
				
			}
		});

		input.addKeyListener(new KeyListener(InputListener.KEY_A, KeyListener.STATE_PRESSED)
		{
			@Override
			public void run()
			{
				r.getRotation().setY(r.getRotation().getY() - 1f);
			}
		});

		input.addKeyListener(new KeyListener(InputListener.KEY_D, KeyListener.STATE_PRESSED)
		{
			@Override
			public void run()
			{
				r.getRotation().setY(r.getRotation().getY() + 1f);
			}
		});
	}

	private Renderable makeRenderable(final Model model)
	{
		Renderable r = new Renderable();
		r.setModel(model);
		r.setPosition(new Position(0.0f, 0.0f, 0.0f, 1.0f));
		r.setRotation(new Rotation(0.0f, 0.0f, 0.0f));
		r.setScale(new Scale(1.0f, 1.0f, 1.0f));
		return r;
	}

	private Renderable makeSkybox()
	{
		Texture mt = new Texture(assetFolder+"/Texture/skybox5.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);

		Mesh m = MeshUtil.importFromFile(assetFolder+"/Mesh/skyboxnew.obj");
		rm.addResource(m);

		Model model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setTexture(mt);
		model.setShaderProgram(skyboxProgram);
		rm.addResource(model);

		Renderable r = new Renderable()
		{
			@Override
			public void doAtPreRender()
			{
				setPosition(new Position(player.getPosition().x, player.getPosition().y - 50f, player.getPosition().z, 0.0f));
			}
		};
		r.setModel(model);
		r.setPosition(new Position(0.0f, 0.0f, 0.0f, 0.0f));
		r.setRotation(new Rotation(180.0f, 0.0f, 0.0f));
		r.setScale(new Scale(-2000f, -2000f, -2000f));
		return r;
	}

	private Renderable makeShip()
	{
		Texture mt = new Texture(assetFolder+"/Texture/ship.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);

		Mesh m = MeshUtil.importFromFile(assetFolder+"/Mesh/tugboat2.obj");
		rm.addResource(m);

		Model model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setTexture(mt);
		model.setMaterial(mat);
		model.setShaderProgram(standardProgram);

		mt = new Texture(assetFolder+"/Texture/ship.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);
		model.setNormalMap(mt);
		rm.addResource(model);

		Renderable r = new Renderable();
		boatCollision = new Sphere(new Position(45f, -4.0f, 0f, 0.0f), 17f);
		pm.getShape().add(boatCollision);
		
		r.setModel(model);
		r.setPosition(new Position(-20.0f, -1.0f, 20.0f, 0.0f));
		r.setRotation(new Rotation(0.0f, 0.0f, 0.0f));
		r.setScale(new Scale(0.3f, 0.3f, 0.3f));
		return r;
	}

	private Renderable makeCar()
	{
		Texture mt = new Texture(assetFolder+"/Texture/ship.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);

		Mesh m = MeshUtil.importFromFile(assetFolder+"/Mesh/legend.obj");
		rm.addResource(m);

		Model model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setTexture(mt);
		model.setMaterial(mat);
		model.setShaderProgram(reflectionProgram);
		rm.addResource(model);

		Renderable r = new Renderable();
		
		r.setModel(model);
		r.setPosition(new Position(60.0f, 4.0f, -220.0f, 0.0f));
		r.setRotation(new Rotation(0.0f, 0.0f, 0.0f));
		r.setScale(new Scale(6f, 6f, 6f));
		return r;
	}
	
	private Renderable makePlayer()
	{
		Texture mt = new Texture(assetFolder+"/Texture/stone.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);

		Mesh m = MeshUtil.importFromFile(assetFolder+"/Mesh/human.obj");
		rm.addResource(m);

		Model model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setTexture(mt);
		model.setMaterial(mat);
		model.setShaderProgram(standardProgram);

		rm.addResource(model);

		final Renderable r = new Renderable()
		{


			{
				this.setName("Player");


			}

			@Override
			public void doAtPreRender()
			{


			}
		};
		r.setModel(model);
		r.setPosition(new Position(0.0f, 3.0f, 0.0f, 0.0f));
		r.setRotation(new Rotation(0.0f, 0.0f, 0.0f));
		r.setScale(new Scale(1f, 1f, 1));
		return r;
	}

	private Renderable makeGround()
	{
		Mesh m;
		Model model;
		Texture mt;

		h = new HeightMap(assetFolder+"/Terrain/12.bmp");
		h.offsetHeight(-29.5f);
		h.makeHills(0.5f);
		h.addRandomHeight(1f);
		m = MeshUtil.generateTerrain(1000f, 1000f, h, 0.70f);
		rm.addResource(m);

		model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setMaterial(mat);

		rm.addResource(model);

		mt = new Texture(new String[]
					{	assetFolder+"/Texture/terrain/1.png",
						assetFolder+"/Texture/terrain/2.png",
						assetFolder+"/Texture/terrain/3.png",
						assetFolder+"/Texture/terrain/4.png",
						assetFolder+"/Texture/terrain/5.png",
						assetFolder+"/Texture/terrain/6.png",
						assetFolder+"/Texture/terrain/7.png",
						assetFolder+"/Texture/terrain/10.png",
						assetFolder+"/Texture/terrain/9.png"}, Texture.TYPE_TEXTURE_3D);

		mt.setRenderMode(Texture.RENDER_TEXTURE_REPEAT);
		rm.addResource(mt);
		model.setTexture(mt);
		
		mt = new Texture(new String[]
					{	assetFolder+"/Texture/terrain/1normal.png",
						assetFolder+"/Texture/terrain/2normal.png",
						assetFolder+"/Texture/terrain/3normal.png",
						assetFolder+"/Texture/terrain/4normal.png",
						assetFolder+"/Texture/terrain/5normal.png",
						assetFolder+"/Texture/terrain/6normal.png",
						assetFolder+"/Texture/terrain/7normal.png",
						assetFolder+"/Texture/terrain/10normal.png"}, Texture.TYPE_TEXTURE_3D);
		
		//mt = new Texture(assetFolder+"/Texture/terrain/1normal.png", Texture.TYPE_TEXTURE_2D);
		mt.setRenderMode(Texture.RENDER_TEXTURE_REPEAT);
		rm.addResource(mt);
		model.setNormalMap(mt);

		model.setShaderProgram(terrainProgram);

		final Renderable r = makeRenderable(model);
		r.setName("Terrain");
		return r;
	}
	
	private void makeWaterShaderProgram()
	{
		Shader shv;
		Shader shg;
		Shader shf;
		ShaderProgram sh;
		
		shv = new Shader(assetFolder+"/Shader/water.vert",
				Shader.TYPE_GL_VERTEX_SHADER);
		shg = new Shader(assetFolder+"/Shader/water.geo",
				Shader.TYPE_GL_GEOMETRY_SHADER);
		shf = new Shader(assetFolder+"/Shader/water.frag",
				Shader.TYPE_GL_FRAGMENT_SHADER);

		sh = new ShaderProgram();
		sh.addShader(shv);
		sh.addShader(shg);
		sh.addShader(shf);
		sh.getUniform().add(new Uniform("angle", 1f, Uniform.TYPE_FLOAT));
		sh.getUniform().add(new Uniform("waveSpeed", 10f, Uniform.TYPE_FLOAT));
		sh.getUniform().add(new Uniform("renderable[0].position", new float[] {0f, 0f, 0f}, Uniform.TYPE_VEC3));
		sh.getUniform().add(new Uniform("renderable[0].radius", 4f, Uniform.TYPE_FLOAT));
		sh.getUniform().add(new Uniform("renderable[1].position", new float[] {0f, 0f, 0f}, Uniform.TYPE_VEC3));
		sh.getUniform().add(new Uniform("renderable[1].radius",  boatRadius / 1.5f, Uniform.TYPE_FLOAT));
		rm.addResource(shv);
		rm.addResource(shg);
		rm.addResource(shf);
		rm.addResource(sh);
		
		waterProgram = sh;
	}

	private void makeUnderWaterProgram()
	{
		Shader shv;
		Shader shf;
		ShaderProgram sh;
		
		shv = new Shader(assetFolder+"/Shader/water.vert",
				Shader.TYPE_GL_VERTEX_SHADER);
		shf = new Shader(assetFolder+"/Shader/standard.frag",
				Shader.TYPE_GL_FRAGMENT_SHADER);

		sh = new ShaderProgram();
		sh.addShader(shv);
		sh.addShader(shf);
		sh.getUniform().add(new Uniform("angle", 1f, Uniform.TYPE_FLOAT));
		
		rm.addResource(shv);
		rm.addResource(shf);
		rm.addResource(sh);
		
		underWaterProgram = sh;
	}
	
	private void makeStandardShaderProgram()
	{
		Shader shv;
		Shader shf;
		ShaderProgram sh;
		
		shv = new Shader(assetFolder+"/Shader/standard.vert",
				Shader.TYPE_GL_VERTEX_SHADER);
		shf = new Shader(assetFolder+"/Shader/standard.frag",
				Shader.TYPE_GL_FRAGMENT_SHADER);

		sh = new ShaderProgram();
		sh.addShader(shv);
		sh.addShader(shf);
		
		rm.addResource(shv);
		rm.addResource(shf);
		rm.addResource(sh);		
		
		standardProgram = sh;
		sh.getUniform().add(new Uniform("discardUnderWater", 0, Uniform.TYPE_INT));
	}
	
	private void makeReflectionShaderProgram()
	{
		Shader shv;
		Shader shf;
		ShaderProgram sh;
		
		shv = new Shader(assetFolder+"/Shader/standard.vert",
				Shader.TYPE_GL_VERTEX_SHADER);
		shf = new Shader(assetFolder+"/Shader/reflection.frag",
				Shader.TYPE_GL_FRAGMENT_SHADER);

		sh = new ShaderProgram();
		sh.addShader(shv);
		sh.addShader(shf);
		
		rm.addResource(shv);
		rm.addResource(shf);
		rm.addResource(sh);		
		
		reflectionProgram = sh;
		sh.getUniform().add(new Uniform("discardUnderWater", 0, Uniform.TYPE_INT));
	}
	
	private void makeTerrainShaderProgram()
	{
		Shader shv;
		Shader shf;
		ShaderProgram sh;
		
		shv = new Shader(assetFolder+"/Shader/standard.vert",
				Shader.TYPE_GL_VERTEX_SHADER);
		shf = new Shader(assetFolder+"/Shader/terrain.frag",
				Shader.TYPE_GL_FRAGMENT_SHADER);

		sh = new ShaderProgram();
		sh.addShader(shv);
		sh.addShader(shf);
		
		rm.addResource(shv);
		rm.addResource(shf);
		rm.addResource(sh);		
		
		terrainProgram = sh;
		sh.getUniform().add(new Uniform("discardUnderWater", 0, Uniform.TYPE_INT));
		sh.getUniform().add(new Uniform("blueFilterUnderWater", 0, Uniform.TYPE_INT));
	}
	
	private void makeSkyboxShaderProgram()
	{
		Shader shv;
		Shader shf;
		ShaderProgram sh;
		
		shv = new Shader(assetFolder+"/Shader/standard.vert",
				Shader.TYPE_GL_VERTEX_SHADER);
		shf = new Shader(assetFolder+"/Shader/skybox.frag",
				Shader.TYPE_GL_FRAGMENT_SHADER);

		sh = new ShaderProgram();
		sh.addShader(shv);
		sh.addShader(shf);
		
		rm.addResource(shv);
		rm.addResource(shf);
		rm.addResource(sh);		
		
		skyboxProgram = sh;
		sh.getUniform().add(new Uniform("discardUnderWater", 0, Uniform.TYPE_INT));
	}
	
	private Renderable makeWater()
	{
		Mesh m;
		Model model;
		Texture mt;

		float cellX = 1000.0f / 255.0f;
		float cellZ = 1000.0f / 255.0f;

		m = MeshUtil.generateTerrain(1000.0f, 1000.0f, 255, 255);
		rm.addResource(m);
	
		model = new Model();
		model.setMesh(m);
		Material ma = new Material(new Color(0.0f, 0.0f, 0.0f, 1f), new Color(0.92f, 0.92f, 0.92f, 1f),
				new Color(0.970f, 0.970f, 0.970f, 1.0f), 1f);
		model.setMaterial(ma);
		rm.addResource(model);

		mt = new Texture(assetFolder+"/Texture/water3.png", Texture.TYPE_TEXTURE_2D);
		mt.setRenderMode(Texture.RENDER_TEXTURE_REPEAT);
		rm.addResource(mt);
		model.setTexture(mt);

		//mt = new Texture(assetFolder+"/Texture/water3normal.png", Texture.TYPE_TEXTURE_2D);
		//mt.setRenderMode(Texture.RENDER_TEXTURE_REPEAT);
		//rm.addResource(mt);
		//model.setNormalMap(mt);

		BufferedImage coastImage = new BufferedImage(h.getTx(), h.getTz(), BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < coastImage.getWidth(); x++)
			for (int y = 0; y < coastImage.getHeight(); y++)
			{
				int rgb = 0;
				float height = h.getHeightAtLocation((float) x, (float) y);
				if (height > -4f)
				{
					int r = (int) (height + 4f);
					rgb = ((r&0x0ff)<<16)|((255&0x0ff)<<8)|(255&0x0ff);
				}
					
				coastImage.setRGB(x, y, rgb);
			}
		
		mt = new Texture(Texture.TYPE_TEXTURE_2D, coastImage);
		rm.addResource(mt);
		model.setHeightTexture(mt);
		model.setShaderProgram(waterProgram);

		final Renderable r = new Renderable()
		{
			{
				setPosition(new Position(0.0f, 0.0f, 0.0f, 1.0f));
				setRotation(new Rotation(0.0f, 0.0f, 0.0f));
				setScale(new Scale(1.0f, 1.0f, 1.0f));
				this.getCustom().add(0.0f);
			}
			
			public void doAtPreRender()
			{
				float x = 0.0f + ((float) ((int) (player.getPosition().x / cellX)) * cellX);
				float z = 0.0f + ((float) ((int) (player.getPosition().z / cellZ)) * cellZ);
				getPosition().x = x;
				getPosition().z = z;
			}

		};
		r.setModel(model);
		r.setName("Water");
		r.getRenderSettings().setRenderOrder(RenderSettings.RENDER_ORDER_BACK_FIRST);
		return r;
	}

	private Renderable makeUnderWater()
	{
		Mesh m;
		Model model;
		Texture mt;


		m = MeshUtil.generateTerrain(1000.0f, 1000.0f, 127, 127);
		rm.addResource(m);

		model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setMaterial(mat);
		rm.addResource(model);

		mt = new Texture(assetFolder+"/Texture/water2.png", Texture.TYPE_TEXTURE_2D);
		mt.setRenderMode(Texture.RENDER_TEXTURE_REPEAT);
		rm.addResource(mt);
		model.setTexture(mt);

		mt = new Texture(assetFolder+"/Texture/water2normal.png", Texture.TYPE_TEXTURE_2D);
		mt.setRenderMode(Texture.RENDER_TEXTURE_REPEAT);
		rm.addResource(mt);
		model.setNormalMap(mt);

		model.setShaderProgram(standardProgram);

		final Renderable r = new Renderable()
		{
			{
				setPosition(new Position(0.0f, -0.2f, 0.0f, 1.0f));
				setRotation(new Rotation(180.0f, 0.0f, 0.0f));
				setScale(new Scale(1.0f, 1.0f, 1.0f));
				this.getCustom().add(0.0f);
			}

		};
		r.setModel(model);
		r.setName("Water");
		r.getRenderSettings().setRenderOrder(RenderSettings.RENDER_ORDER_BACK_FIRST);
		return r;
	}
	
	private Renderable[] makeTrees()
	{
		Renderable[] r = new Renderable[60];
		Mesh m;
		Model model;
		Texture mt;

		mt = new Texture(assetFolder+"/Texture/palmtree.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);

		m = MeshUtil.importFromFile(assetFolder+"/Mesh/palmtree2.obj");
		rm.addResource(m);

		model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setMaterial(mat);
		rm.addResource(model);

		model.setTexture(mt);
		model.setShaderProgram(standardProgram);

		for (int i = 0; i < r.length; i++)
		{
			float x = 0.0f;
			float z = 0.0f;
			float y = -1.0f;
			float angle = (float) Math.random() * 360.0f;
			float scale = (float) Math.random() * 0.4f + 0.8f;

			while (y < 3.5f)
			{
				x = -500.0f + ((float) Math.random() * 1000.0f);
				z = -500.0f + ((float) Math.random() * 1000.0f);
				float terrainX = x + 500.0f;
				float terrainZ = z + 500.0f;
				terrainX = (terrainX / (1000f / ((float) h.getTx() - 1)));
				terrainZ = (terrainZ / (1000f / ((float) h.getTz() - 1)));
				y = h.getHeightAtLocation(terrainX, terrainZ);
			}

			r[i] = new Renderable();
			r[i].setScale(new Scale(scale, scale, scale));
			r[i].setRotation(new Rotation(0f, angle, 0f));
			r[i].setModel(model);
			r[i].setPosition(new Position(x, y , z, 1.0f));
			r[i].setScale(new Scale(1.3f, 1.3f, 1.3f));
			r[i].setName("Tree");
			r[i].setDescription("Tree array index: " + i);

			pm.getShape().add(new EndlessCylinder(r[i].getPosition(), 3.2f * scale));
		}
		return r;
	}

	private Renderable makeHut()
	{
		Renderable r = new Renderable();
		Mesh m;
		Model model;
		Texture mt;

		mt = new Texture(assetFolder+"/Texture/reedHut.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);

		m = MeshUtil.importFromFile(assetFolder+"/Mesh/reedHut.obj");
		rm.addResource(m);

		model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setMaterial(mat);
		rm.addResource(model);

		model.setTexture(mt);
		model.setShaderProgram(standardProgram);

		r.setModel(model);
		r.setPosition(new Position(0.0f, 3.0f, -180.0f, 0.0f));
		r.setRotation(new Rotation(0.0f, 0.0f, 0.0f));
		r.setScale(new Scale(1f, 1f, 1));
		
		return r;
	}
	
	private Renderable makeFence()
	{
		Renderable r = new Renderable();
		Mesh m;
		Model model;
		Texture mt;

		mt = new Texture(assetFolder+"/Texture/fence.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);

		m = MeshUtil.importFromFile(assetFolder+"/Mesh/fence.obj");
		
		rm.addResource(m);

		model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setMaterial(mat);
		rm.addResource(model);

		model.setTexture(mt);
		
		//mt = new Texture(assetFolder+"/Texture/fenceNormal.png",
		//		Texture.TYPE_TEXTURE_2D);
		//rm.addResource(mt);
		//model.setNormalMap(mt);
		model.setShaderProgram(standardProgram);

		r.setModel(model);
		r.setPosition(new Position(0.0f, 3.0f, -220.0f, 0.0f));
		r.setRotation(new Rotation(0.0f, 0.0f, 0.0f));
		r.setScale(new Scale(0.05f, 0.05f, 0.05f));
		
		return r;
	}
	
	private Renderable makeCrate()
	{
		Renderable r = new Renderable();
		Mesh m;
		Model model;
		Texture mt;

		mt = new Texture(assetFolder+"/Texture/crate.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);

		m = MeshUtil.importFromFile(assetFolder+"/Mesh/crate.obj");
		rm.addResource(m);

		model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setMaterial(mat);
		rm.addResource(model);
		model.setTexture(mt);
		model.setShaderProgram(standardProgram);

		r.setModel(model);
		r.setPosition(new Position(30.0f, 20.0f, -220.0f, 0.0f));
		r.setRotation(new Rotation(0.0f, 0.0f, 0.0f));
		r.setScale(new Scale(0.5f, 0.5f, 0.5f));
		
		return r;
	}
	
	private Renderable[] makeStreetLights()
	{
		Renderable[] r = new Renderable[39];
		Model model;
		Texture mt;

		mt = new Texture(assetFolder+"/Texture/streetLight.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);

		Mesh m = MeshUtil.importFromFile(assetFolder+"/Mesh/streetLight.obj");
		rm.addResource(m);

		model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setMaterial(mat);
		rm.addResource(model);

		model.setTexture(mt);
		
		mt = new Texture(assetFolder+"/Texture/streetLightNormal.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);
		model.setNormalMap(mt);
		model.setShaderProgram(standardProgram);

		for (int i = 0; i < r.length; i++)
		{
			float x = 0.0f;
			float z = 0.0f;
			float y = -1.0f;
			while (y < 4.5f)
			{
				x = -500.0f + ((float) Math.random() * 1000.0f);
				z = -500.0f + ((float) Math.random() * 1000.0f);
				float terrainX = x + 500.0f;
				float terrainZ = z + 500.0f;
				terrainX = (terrainX / (1000f / ((float) h.getTx() - 1)));
				terrainZ = (terrainZ / (1000f / ((float) h.getTz() - 1)));
				y = h.getHeightAtLocation(terrainX, terrainZ);
			}

			r[i] = new Renderable();
			r[i].setModel(model);
			r[i].setPosition(new Position(x, y , z, 1.0f));
			r[i].setRotation(new Rotation(0f, 0f, 0f));
			r[i].setScale(new Scale(5f, 5f, 5f));
			r[i].setName("StreetLight");
			r[i].setDescription("StreetLight array index: " + i);
			r[i].getRenderSettings().setRenderOrder(RenderSettings.RENDER_ORDER_FRONT_TO_BACK);
		}
		return r;
	}
	
	private Renderable[] makeGrass()
	{
		Renderable[] r = new Renderable[8740];
		Model model;
		Texture mt;

		mt = new Texture(assetFolder+"/Texture/grassy.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);

		Mesh m = MeshUtil.generateTerrain(3.0f, 3.0f, 1, 1);
		rm.addResource(m);

		model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setMaterial(mat);
		rm.addResource(model);

		model.setTexture(mt);
		model.setShaderProgram(standardProgram);

		for (int i = 0; i < r.length; i++)
		{
			float x = 0.0f;
			float z = 0.0f;
			float y = -1.0f;
			while (y < 2.5f)
			{
				x = -500.0f + ((float) Math.random() * 1000.0f);
				z = -500.0f + ((float) Math.random() * 1000.0f);
				float terrainX = x + 500.0f;
				float terrainZ = z + 500.0f;
				terrainX = (terrainX / (1000f / ((float) h.getTx() - 1)));
				terrainZ = (terrainZ / (1000f / ((float) h.getTz() - 1)));
				y = h.getHeightAtLocation(terrainX, terrainZ);
			}

			r[i] = new Renderable()
			{
				float angle = (float) Math.random() * 360.0f;
				{

					this.setRotation(new Rotation(270f, 0f, angle));
				}
			};
			r[i].setModel(model);
			r[i].setPosition(new Position(x, y + 1.0f, z, 1.0f));
			r[i].setScale(new Scale(1f, 1f, 1f));
			r[i].setName("Grass");
			r[i].setDescription("Grass array index: " + i);
			r[i].getRenderSettings().setRenderOrder(RenderSettings.RENDER_ORDER_BACK_TO_FRONT);
			r[i].getRenderSettings().setUseMaximumDistance(true);
			r[i].getRenderSettings().setMaximumDistance(190f);
		}
		return r;
	}

	private Renderable[] makeRocks()
	{
		Renderable[] r = new Renderable[150];
		Mesh m;
		Model model;
		Texture mt;

		mt = new Texture(assetFolder+"/Texture/rock.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);


		//m = MeshUtil.generateTerrain(8f, 8f, 1, 1);
		m = MeshUtil.importFromFile(assetFolder+"/Mesh/rock2.obj");
		rm.addResource(m);

		model = new Model();
		model.setMesh(m);
		model.setIndices(m.getIndices());
		model.setMaterial(mat);
		rm.addResource(model);

		model.setTexture(mt);

		mt = new Texture(assetFolder+"/Texture/rocknormal.png",
				Texture.TYPE_TEXTURE_2D);
		rm.addResource(mt);
		model.setNormalMap(mt);
		model.setShaderProgram(standardProgram);

		for (int i = 0; i < r.length; i++)
		{
			float x = 0.0f;
			float z = 0.0f;
			float y = -1.0f;
			float angle = (float) Math.random() * 360.0f;
			float scale = (float) Math.random() * 0.8f + 0.2f;

			while (y < 3.5f)
			{
				x = -500.0f + ((float) Math.random() * 1000.0f);
				z = -500.0f + ((float) Math.random() * 1000.0f);
				float terrainX = x + 500.0f;
				float terrainZ = z + 500.0f;
				terrainX = (terrainX / (1000f / ((float) h.getTx() - 1)));
				terrainZ = (terrainZ / (1000f / ((float) h.getTz() - 1)));
				y = h.getHeightAtLocation(terrainX, terrainZ);
			}

			r[i] = new Renderable();
			r[i].setScale(new Scale(scale, scale, scale));
			r[i].setRotation(new Rotation(0f, angle, 0f));
			r[i].setModel(model);
			r[i].setPosition(new Position(x, y, z, 1.0f));
			r[i].setName("Rock");
			r[i].setDescription("Rock array index: " + i);
			r[i].getRenderSettings().setRenderOrder(RenderSettings.RENDER_ORDER_FRONT_TO_BACK);

			Position spherePosition = r[i].getPosition().clone();
			spherePosition.y += 5.5f * scale;
			pm.getShape().add(new Sphere(spherePosition, 6.0f * scale));
			pm.getShape().add(new Cylinder(r[i].getPosition(), 6.0f * scale, 5.5f * scale));
		}
		return r;
	}

}
