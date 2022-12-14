package sliedregt.martijn.jtitan.api;

import sliedregt.martijn.jtitan.config.Configuration;

public abstract class Api
{

	public final static int TYPE_AUDIO = 1, TYPE_GRAPHICS = 2;

	protected final int type;
	protected final String name;
	protected final long version;

	protected boolean isInitialized;

	protected Api(final int type, final String name, final long version)
	{
		isInitialized = false;
		this.type = type;
		this.name = name;
		this.version = version;
	}

	public int getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public long getVersion()
	{
		return version;
	}

	public boolean isInitialized()
	{
		return isInitialized;
	}

	abstract public boolean initialize(Configuration c);

	abstract public boolean terminate();
}
