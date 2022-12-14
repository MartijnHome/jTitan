package sliedregt.martijn.jtitan.config.graphics;

import java.awt.Dimension;

public class DisplaySettings
{

	private boolean isFullScreen;
	private int maxFPS;
	private Dimension resolution;
	private int depth;
	private int refreshRate;
	private int antiAliasingSamples;
	
	public DisplaySettings(boolean isFullScreen, int maxFPS, Dimension resolution, int depth, int refreshRate)
	{
		super();
		this.isFullScreen = isFullScreen;
		this.maxFPS = maxFPS;
		this.refreshRate = refreshRate;
		this.depth = depth;
		this.resolution = resolution;
		this.setAntiAliasingSamples(4);
	}
	
	public DisplaySettings()
	{
		this(false, 0, new Dimension(800, 600), 32, 60);
	}
	
	public Dimension getResolution()
	{
		return resolution;
	}
	
	public void setResolution(Dimension resolution)
	{
		this.resolution = resolution;
	}
	
	public int getDepth()
	{
		return depth;
	}
	
	public void setDepth(int depth)
	{
		this.depth = depth;
	}
	
	public int getRefreshRate()
	{
		return refreshRate;
	}
	
	public void setRefreshRate(int refreshRate)
	{
		this.refreshRate = refreshRate;
	}
	
	public boolean isFullScreen()
	{
		return isFullScreen;
	}

	public void setFullScreen(boolean isFullScreen)
	{
		this.isFullScreen = isFullScreen;
	}

	public int getMaxFPS()
	{
		return maxFPS;
	}

	public void setMaxFPS(int maxFPS)
	{
		this.maxFPS = maxFPS;
	}

	public int getAntiAliasingSamples()
	{
		return antiAliasingSamples;
	}

	public void setAntiAliasingSamples(int antiAliasingSamples)
	{
		this.antiAliasingSamples = antiAliasingSamples;
	}

}
