package sliedregt.martijn.jtitan;

public class EngineState
{

	private int current = 0;

	public static final int INACTIVE = 0, INITIALIZING = 1, READY = 2, RUNNING = 3, STOPPING = 4, STOPPED = 5,
			WAITING = 6, INITIALIZED = 7, LOADING = 8;

	public EngineState()
	{
		current = INACTIVE;
	}

	synchronized public int getCurrent()
	{
		return current;
	}

	synchronized public void setCurrent(int current)
	{
		this.current = current;
	}

}