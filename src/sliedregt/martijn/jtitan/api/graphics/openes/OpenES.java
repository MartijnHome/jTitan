package sliedregt.martijn.jtitan.api.graphics.openes;

import static org.lwjgl.glfw.GLFW.glfwInit;

import sliedregt.martijn.jtitan.api.graphics.GraphicsApi;
import sliedregt.martijn.jtitan.config.Configuration;

abstract class OpenES extends GraphicsApi
{

	public OpenES(final long version)
	{
		super("OpenES", version);
	}

	@Override
	public boolean initialize(Configuration c)
	{
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		return glfwInit();
	}
}
