package sliedregt.martijn.jtitan.api.graphics.opengl;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GLCapabilities;

import sliedregt.martijn.jtitan.api.graphics.GraphicsApi;
import sliedregt.martijn.jtitan.api.graphics.Window;
import sliedregt.martijn.jtitan.api.graphics.Window.WindowConfiguration;
import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.config.graphics.RenderSettings;
import sliedregt.martijn.jtitan.datatype.scene.Camera;
import sliedregt.martijn.jtitan.datatype.scene.Fog;
import sliedregt.martijn.jtitan.datatype.scene.Light;
import sliedregt.martijn.jtitan.datatype.scene.Renderable;

abstract class OpenGL extends GraphicsApi
{
	List<Renderable> orderedList;
	List<Renderable> frontToBackList;
	List<Renderable> backToFrontList;
	List<Renderable> backFirst;
	List<Renderable> backLast;
	List<Float> frontToBackDistance;
	List<Float> backToFrontDistance;
	
	abstract void position(Renderable r);

	abstract void draw(List<Renderable> rl, Camera c, List<Light> l, Fog fog, Dimension screenSize);

	public OpenGL(final long version)
	{
		super("OpenGL", version);
		orderedList = new ArrayList<Renderable>();
		frontToBackList = new ArrayList<Renderable>();
		backToFrontList = new ArrayList<Renderable>();
		backFirst = new ArrayList<Renderable>();
		backLast = new ArrayList<Renderable>();
		frontToBackDistance = new ArrayList<Float>();
		backToFrontDistance = new ArrayList<Float>();
	}

	@Override
	final public void render(List<Renderable> r, Camera c, List<Light> l, Fog fog, Dimension screenSize)
	{
		draw(orderList(r, c), c, l, fog, screenSize);
	}

	private List<Renderable> orderList(List<Renderable> r, Camera c)
	{
		orderedList.clear();
		frontToBackList.clear(); 
		backToFrontList.clear();
		backFirst.clear();
		backLast.clear();
		frontToBackDistance.clear(); 
		backToFrontDistance.clear();
		for (Renderable rr : r)
		{
			if (!rr.getRenderSettings().isVisible())
				continue;
			
			switch(rr.getRenderSettings().getRenderOrder())
			{
			case RenderSettings.RENDER_ORDER_NO_CARE:
				orderedList.add(rr);
				break;
			
			case RenderSettings.RENDER_ORDER_FRONT_TO_BACK:
				{
					float distance = Math.abs(rr.getPosition().x - c.getPosition().x) + Math.abs(rr.getPosition().y - c.getPosition().y) + Math.abs(rr.getPosition().z - c.getPosition().z);
					if (!rr.getRenderSettings().isUseMaximumDistance() || (rr.getRenderSettings().isUseMaximumDistance() && distance < rr.getRenderSettings().getMaximumDistance()))
					{
						//If the list is empty we just add the first one
						if (frontToBackList.size() == 0)
						{
							frontToBackList.add(rr);
							frontToBackDistance.add(distance);
						} else
						{
							//Boolean n starts true, when we don't find any other renderable closer than the one we compare to we need to add it
							boolean n = true;
							for (int i = 0; i < frontToBackList.size(); i++)
							{
								if (distance < frontToBackDistance.get(i).floatValue())
								{
									//If we find the index inside the list with a greater distance, we insert our renderable at the index i
									frontToBackList.add(i, rr);
									frontToBackDistance.add(i, distance);
									n = false;
									break;
								}
							}
							if (n)
							{
								//The current renderable has the biggest distance
								frontToBackList.add(rr);
								frontToBackDistance.add(distance);
							}
						}
					}
				}
				break;
			
			case RenderSettings.RENDER_ORDER_BACK_TO_FRONT:
				{
					//Now we do the same but we check if the distance is greater than the ones in the list
					float distance = Math.abs(rr.getPosition().x - c.getPosition().x) + Math.abs(rr.getPosition().y - c.getPosition().y) + Math.abs(rr.getPosition().z - c.getPosition().z);
					if (!rr.getRenderSettings().isUseMaximumDistance() || (rr.getRenderSettings().isUseMaximumDistance() && distance < rr.getRenderSettings().getMaximumDistance()))
					{
						if (backToFrontList.size() == 0)
						{
							backToFrontList.add(rr);
							backToFrontDistance.add(distance);
						} else {
							boolean n = true;
							for (int i = 0; i < backToFrontList.size(); i++)
							{
								if (distance > backToFrontDistance.get(i).floatValue())
								{
									backToFrontList.add(i, rr);
									backToFrontDistance.add(i, distance);
									n = false;
									break;
								}
							}
							if (n)
							{
								backToFrontList.add(rr);
								backToFrontDistance.add(distance);
							}
						}
					}
				}
				break;
			
			case RenderSettings.RENDER_ORDER_BACK_LAST:
				backLast.add(rr);
				break;
			
			case RenderSettings.RENDER_ORDER_BACK_FIRST:
				backFirst.add(rr);
				break;
			}
		}
			
		orderedList.addAll(frontToBackList);
		orderedList.addAll(backFirst);
		orderedList.addAll(backToFrontList);
		orderedList.addAll(backLast);
		return orderedList;
	}
	@Override
	public boolean initialize(Configuration c)
	{
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		boolean r = glfwInit();

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		return r;
	}

	@Override
	public void setup()
	{
		// TODO Auto-generated method stub
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.

		// glfwSwapInterval(1);

		@SuppressWarnings("unused")
		GLCapabilities g = GL.createCapabilities();
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL14.GL_TEXTURE_3D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glEnable(GL40.GL_DEPTH_CLAMP);
		//GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE );
		//GLUtil.setupDebugMessageCallback();
		return;
	}

	@Override
	public long createWindow(Window w)
	{
		WindowConfiguration wc = w.getConfig();
		return glfwCreateWindow(wc.getWidth(), wc.getHeight(), wc.getTitle(), wc.getMonitor(), wc.getShare());
	}

	@Override
	public void setKeyCallBack(long w, GLFWKeyCallback k)
	{
		glfwSetKeyCallback(w, k);
	}

	@Override
	public void setWindowVisible(Window w, boolean v)
	{
		// TODO Auto-generated method stub
		if (v)
			glfwShowWindow(w.getHandle());
	}

	@Override
	public void setContext(Window w)
	{
		// TODO Auto-generated method stub
		// Make the OpenGL context current
		glfwMakeContextCurrent(w.getHandle());
	}

	@Override
	public void setVSync(boolean v)
	{
		// TODO Auto-generated method stub
		if (v)
			// Enable v-sync
			glfwSwapInterval(1);
	}

}
