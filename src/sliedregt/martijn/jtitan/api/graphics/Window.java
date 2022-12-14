package sliedregt.martijn.jtitan.api.graphics;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.system.MemoryUtil.NULL;

import sliedregt.martijn.jtitan.config.Configuration;


public class Window
{

	private long handle;

	private WindowConfiguration config;
	private GraphicsApi api;

	public Window(Configuration c)
	{
		api = c.getGraphicsApi();
		config = new WindowConfiguration(c.getDisplaySettings().getResolution().width, c.getDisplaySettings().getResolution().height,  c.getTitle(), c.getDisplaySettings().isFullScreen() ? glfwGetPrimaryMonitor() : NULL, NULL);
	}

	public void create()
	{
		handle = api.createWindow(this);
	}

	public long getHandle()
	{
		return handle;
	}

	public void setHandle(long handle)
	{
		this.handle = handle;
	}

	public WindowConfiguration getConfig()
	{
		return config;
	}

	public void setConfig(WindowConfiguration config)
	{
		this.config = config;
	}

	public void setVisible(boolean v)
	{
		api.setWindowVisible(this, v);
	}

	public class WindowConfiguration
	{
		int width;
		int height;
		String title;
		long monitor;
		long share;

		public WindowConfiguration(int w, int h, String t, long m, long s)
		{
			width = w;
			height = h;
			title = t;
			monitor = m;
			share = s;
		}

		public int getWidth()
		{
			return width;
		}

		public void setWidth(int width)
		{
			this.width = width;
		}

		public int getHeight()
		{
			return height;
		}

		public void setHeight(int height)
		{
			this.height = height;
		}

		public String getTitle()
		{
			return title;
		}

		public void setTitle(String title)
		{
			this.title = title;
		}

		public long getMonitor()
		{
			return monitor;
		}

		public void setMonitor(long monitor)
		{
			this.monitor = monitor;
		}

		public long getShare()
		{
			return share;
		}

		public void setShare(long share)
		{
			this.share = share;
		}
	}

}
