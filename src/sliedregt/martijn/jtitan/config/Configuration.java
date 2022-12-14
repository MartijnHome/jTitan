package sliedregt.martijn.jtitan.config;

import sliedregt.martijn.jtitan.api.audio.AudioApi;
import sliedregt.martijn.jtitan.api.graphics.GraphicsApi;
import sliedregt.martijn.jtitan.config.graphics.DisplaySettings;
import sliedregt.martijn.jtitan.debug.DebugFrame;

public class Configuration
{

	private final GraphicsApi 		graphicsApi;
	private final AudioApi 			audioApi;
	private final DisplaySettings displaySettings;
	
	private boolean loadMultiThreaded 	= false;
	private boolean showDebugFrame 		= false;
	private boolean yieldInsteadOfSleep = true;
	
	private DebugFrame 	df;
	private String 		title;
	
	private int priority;

	public Configuration(final GraphicsApi g, final AudioApi a, final DisplaySettings d)
	{
		graphicsApi = g;
		audioApi = a;
		displaySettings = d;
		setTitle("Hello World!");
		setPriority(Thread.NORM_PRIORITY);
	}

	public AudioApi getAudioApi()
	{
		return audioApi;
	}

	public GraphicsApi getGraphicsApi()
	{
		return graphicsApi;
	}

	public DisplaySettings getDisplaySettings()
	{
		return displaySettings;
	}

	public boolean isLoadMultiThreaded()
	{
		return loadMultiThreaded;
	}

	public void setLoadMultiThreaded(boolean loadMultiThreaded)
	{
		this.loadMultiThreaded = loadMultiThreaded;
	}

	public boolean isShowDebugFrame()
	{
		return showDebugFrame;
	}

	public void setShowDebugFrame(boolean showDebugFrame)
	{
		this.showDebugFrame = showDebugFrame;
	}

	public boolean isYieldInsteadOfSleep()
	{
		return yieldInsteadOfSleep;
	}

	public void setYieldInsteadOfSleep(boolean yieldInsteadOfSleep)
	{
		this.yieldInsteadOfSleep = yieldInsteadOfSleep;
	}

	public DebugFrame getDebugFrame()
	{
		return df;
	}

	public void setDebugFrame(DebugFrame df)
	{
		this.df = df;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}
}
