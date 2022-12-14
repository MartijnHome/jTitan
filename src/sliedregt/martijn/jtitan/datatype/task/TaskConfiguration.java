package sliedregt.martijn.jtitan.datatype.task;

public final class TaskConfiguration
{

	private boolean tryOnce;
	private boolean noSleep;
	private boolean onlyEngine;

	{
		tryOnce = true;
		noSleep = false;
		onlyEngine = false;
	}

	public boolean isTryOnce()
	{
		return tryOnce;
	}

	public void setTryOnce(boolean tryOnce)
	{
		this.tryOnce = tryOnce;
	}

	public boolean isNoSleep()
	{
		return noSleep;
	}

	public void setNoSleep(boolean noSleep)
	{
		this.noSleep = noSleep;
	}

	public boolean isOnlyEngine()
	{
		return onlyEngine;
	}

	public void setOnlyEngine(boolean onlyEngine)
	{
		this.onlyEngine = onlyEngine;
	}
}