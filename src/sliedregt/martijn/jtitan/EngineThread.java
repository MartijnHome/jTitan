package sliedregt.martijn.jtitan;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;

import sliedregt.martijn.jtitan.api.graphics.Window;
import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.datatype.RenderBatch;
import sliedregt.martijn.jtitan.datatype.primal.Position;
import sliedregt.martijn.jtitan.datatype.primal.Vertex;
import sliedregt.martijn.jtitan.datatype.scene.Camera;
import sliedregt.martijn.jtitan.datatype.scene.Renderable;
import sliedregt.martijn.jtitan.datatype.scene.Scene;
import sliedregt.martijn.jtitan.datatype.task.EngineTask;
import sliedregt.martijn.jtitan.debug.DebugFrame;
import sliedregt.martijn.jtitan.manager.InputManager;
import sliedregt.martijn.jtitan.manager.PhysicsManager;
import sliedregt.martijn.jtitan.manager.ResourceManager;
import sliedregt.martijn.jtitan.manager.TaskManager;

public class EngineThread extends Thread
{

	// The window handle
	private long 	variableYieldTime, 
					lastTime, 
					currentTime, 
					endTime;

	private Scene 				scene;
	private Configuration 		config;
	private InputManager 		input;
	private ResourceManager 	rm;
	private TaskManager 		tm;
	private PhysicsManager 		pm;
	private EngineState 		state;
	private Window 				window;
	private DebugFrame 			df;
	private Camera 				camera;
	private List<RenderBatch>	renderBatch;
	
	private boolean pauseDuringLoading = false;

	protected EngineThread(Configuration c)
	{
		state = new EngineState();
		config = c;
		input = new InputManager();
		tm = new TaskManager();
		rm = new ResourceManager(config, tm);
		pm = new PhysicsManager();
		renderBatch = new ArrayList<RenderBatch>();

		if (config.isShowDebugFrame())
		{
			df = new DebugFrame();
			config.setDebugFrame(df);
		}
		
		this.setPriority(c.getPriority());
	}

	private void init()
	{
		if (config.isShowDebugFrame())
			df.setVisible(true);

		if (getEngineState().getCurrent() == EngineState.INACTIVE)
		{
			getEngineState().setCurrent(EngineState.INITIALIZING);
			if (!config.getGraphicsApi().initialize(config))
				throw new IllegalStateException("Unable to initialize " + config.getGraphicsApi().getName());
		}

		// Create the window
		window = new Window(config);
		window.create();

		if (window.getHandle() == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		input.init();
		config.getGraphicsApi().setKeyCallBack(window.getHandle(), InputManager.keyboard);
		config.getGraphicsApi().setContext(window);
		config.getGraphicsApi().setVSync(true);

		// Make the window visible
		window.setVisible(true);
		//glfwSetWindowIcon(window, imagebf);
		config.getGraphicsApi().setup();


		ActionListener l = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				getEngineState().setCurrent(EngineState.LOADING);
			}
		};
		
		ActionListener pause = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				getEngineState().setCurrent(EngineState.WAITING);
			}
		};
		
		ActionListener resume = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				getEngineState().setCurrent(EngineState.RUNNING);
			}
		};

		if (config.isShowDebugFrame())
		{
			df.setContinueButton(l);
			df.setPauseButton(pause);
			df.setResumeButton(resume);
			df.writeLogText("Current date: " + ZonedDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("uuuu.MM.dd")));
		}
	}
	
	public Window getWindow()
	{
		return window;
	}

	private final void terminate()
	{
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window.getHandle());
		glfwDestroyWindow(window.getHandle());

		// Terminate GLFW and free the error callback
		glfwTerminate();
		// glfwSetErrorCallback(null).free();
		getEngineState().setCurrent(EngineState.STOPPED);
	}

	private final void showInfo()
	{
		if (config.isShowDebugFrame())
		{
			df.writeLogText("Initialized LWJGL - version: " + Version.getVersion() + "!");
			df.writeLogText("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
			df.writeLogText("GL Extensions version: " + GL11.glGetString(GL11.GL_EXTENSIONS));
		}
	}

	private void loop()
	{
		int eState;
		currentTime = System.nanoTime();

		do
		{
			endTime = System.nanoTime();

			synchronized (state)
			{
				eState = state.getCurrent();
				if (glfwWindowShouldClose(window.getHandle()))
				{
					getEngineState().setCurrent(EngineState.STOPPING);
					eState = EngineState.STOPPING;
				}

				if (eState == EngineState.RUNNING || eState == EngineState.READY)
				{
					input.update();
					glfwPollEvents();
				}
			}

			if (config.isShowDebugFrame() && eState == EngineState.LOADING && pauseDuringLoading)
			{
				int temp = eState;
				if (config.isShowDebugFrame())
					df.writeLogText("Engine state changed: " + eState);
				
				eState = EngineState.WAITING;
				getEngineState().setCurrent(EngineState.WAITING);
				while (eState == EngineState.WAITING)
				{
					if (config.isYieldInsteadOfSleep())
					{
						yield();
					} else
					{
						try
						{
							sleep(50);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
					synchronized (state)
					{
						eState = state.getCurrent();
					}
				}
				synchronized (state)
				{
					state.setCurrent(temp);
					eState = temp;
				}
				if (config.isShowDebugFrame())
					df.writeLogText("New state: " + eState);
				
			}

			synchronized (tm)
			{
				tm.update(config);
			}
			switch (eState)
			{
				case EngineState.READY:
				{
					if (config.isYieldInsteadOfSleep())
					{
						yield();
					} else
					{
						try
						{
							sleep(50);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
					break;
				}
				case EngineState.WAITING:
				{
					if (config.isYieldInsteadOfSleep())
					{
						yield();
					} else
					{
						try
						{
							sleep(50);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
					break;
				}
				case EngineState.LOADING:
				{	//The scene is being loaded, the engine state will automatically change to ready when loading is done
					if (config.isYieldInsteadOfSleep())
					{
						yield();
					} else
					{
						try
						{
							sleep(50);
						} catch (InterruptedException e)
						{	//TODO catch interrupt and send it to debug frame or the game's logic thread
							e.printStackTrace();
						}
					}
	
					break;
				}
				case EngineState.RUNNING:
				{	//Render our loaded scene, first clear our screen, update our camera view's matrix and render
					for (RenderBatch batch : renderBatch)
					{
						batch.doAtPreRender();
						config.getGraphicsApi().renderBatch(batch, config.getDisplaySettings().getResolution());
						batch.doAtPostRender();
					}
					//TODO Change swapbuffers to getGraphicsApi().swap
					glfwSwapBuffers(window.getHandle()); 
					break;
				}
				case EngineState.STOPPING:
				{
					rm.unloadAll();
					getEngineState().setCurrent(EngineState.STOPPED);
					eState = EngineState.STOPPED;	
				}
			}
			currentTime = System.nanoTime();
			sync(config.getDisplaySettings().getMaxFPS());
			if (config.isShowDebugFrame() && eState != EngineState.WAITING)
				df.update(tm, scene);

		} while (eState != EngineState.STOPPED);
	}

	private void sync(int fps)
	{
		long deltaTime = currentTime - endTime;
		if (deltaTime == 0)
			return;
		//double seconds = (double) deltaTime / 1000000000.0;
		//double FPS = 1.00 / seconds;
	
		if (fps <= 0)
			return;

		long sleepTime = 1000000000 / fps; // nanoseconds to sleep this frame
		// yieldTime + remainder micro & nano seconds if smaller than sleepTime
		long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000 * 1000));
		long overSleep = 0; // time the sync goes over by

		try
		{
			long tt = System.nanoTime() - lastTime;
			if (tt < sleepTime - yieldTime)

				while (true)
				{
					long t = System.nanoTime() - lastTime;

					if (t < sleepTime - yieldTime)
					{
						Thread.sleep(1);
					} else if (t < sleepTime)
					{
						// burn the last few CPU cycles to ensure accuracy
						Thread.yield();
					} else
					{
						overSleep = t - sleepTime;
						break; // exit while loop
					}
				}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} finally
		{
			lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);

			// auto tune the time sync should yield
			if (overSleep > variableYieldTime)
			{
				// increase by 200 microseconds (1/5 a ms)
				variableYieldTime = Math.min(variableYieldTime + 200 * 1000, sleepTime);
			} else if (overSleep < variableYieldTime - 200 * 1000)
			{
				// decrease by 2 microseconds
				variableYieldTime = Math.max(variableYieldTime - 2 * 1000, 0);
			}
		}
	}

	@Override
	final public void run()
	{
		init();
		showInfo();
		getEngineState().setCurrent(EngineState.READY);
		
		loop();
		terminate();
		
		synchronized (tm)
		{
			tm.update(config);
		}	
	}

	public void end(MainGameLogic main)
	{
		if (config.isShowDebugFrame())
			df.writeLogText("Engine being terminated");
		
		glfwSetWindowShouldClose(window.getHandle(), true);
		
		addEngineTask(new EngineTask(false)
		{
			@Override
			protected boolean doTask()
			{
				synchronized (state)
				{
					if (state.getCurrent() == EngineState.STOPPED)
						main.atTerminate();

					return false;
				}
			}		
		});
	}

	synchronized public void addEngineTask(EngineTask t)
	{
		synchronized (tm)
		{
			tm.addTask(t);
		}
	}

	synchronized public Scene getScene()
	{
		return scene;
	}

	synchronized public void setScene(Scene scene)
	{
		this.scene = scene;
	}

	synchronized public void startScene(final Scene sc)
	{
		if (sc == null)
			return;

		if (this.getEngineState().getCurrent() == EngineState.LOADING)
			return;

	
		
		EngineTask loadTask = rm.loadScene(sc);
				
		EngineTask startTask = new EngineTask(false)
		{
			{
				this.setDescription("Start Scene");
			}

			@Override
			protected boolean doTask()
			{
				if (!loadTask.getTaskState().isFinished())
					return false;

				if (config.isShowDebugFrame())
				{
					int vertexCount = 0;
					int renderedCount = 0;
					
					for (Renderable r : sc.getR())
					{
						vertexCount += r.getModel().getMesh().getVertexCount();
						renderedCount += r.getModel().getMesh().getIndices().getIndex().size();
					}
					int vertexMemCount = (vertexCount * Vertex.stride) / 1024 / 1024;
					
					df.writeLogText("Finished loading scene");
					df.writeEngineText("Renderable count: " + sc.getR().size());
					df.writeEngineText("Light count: " + sc.getL().size());
					df.writeEngineText("Vertices count: " + vertexCount);
					df.writeEngineText("Indiced Vertices count: " + renderedCount);
					df.writeEngineText("GPU Memory used by vertices: " + vertexMemCount + "MB");
				}
				
				setScene(sc);
				if (config.isShowDebugFrame())
				{
					df.update(tm, sc);
				}
				getEngineState().setCurrent(EngineState.RUNNING);
				synchronized (state)
				{
					state.setCurrent(EngineState.RUNNING);
				}
				if (config.isShowDebugFrame())
				{
					df.setContinueButton(null);
				}
				return true;
			}
		};

		addEngineTask(loadTask);
		addEngineTask(startTask);

		this.getEngineState().setCurrent(EngineState.LOADING);
	}

	synchronized public InputManager getInput()
	{
		return input;
	}

	synchronized public ResourceManager getRm()
	{
		return rm;
	}

	synchronized public EngineState getEngineState()
	{
		return state;
	}

	synchronized public TaskManager getTm()
	{
		return tm;
	}

	public Camera getCamera()
	{
		return camera;
	}

	public PhysicsManager getPm()
	{
		return pm;
	}

	public List<RenderBatch> getRenderBatch()
	{
		return renderBatch;
	}

	public void setRenderBatch(List<RenderBatch> renderBatch)
	{
		this.renderBatch = renderBatch;
		for (RenderBatch b : renderBatch)
			if (b.getFbo() != null)
				if (!b.getFbo().isLoaded())
					b.getFbo().initialize(config);
	}

}
