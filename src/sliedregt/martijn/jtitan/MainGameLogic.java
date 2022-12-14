package sliedregt.martijn.jtitan;

import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.manager.InputManager;
import sliedregt.martijn.jtitan.manager.PhysicsManager;
import sliedregt.martijn.jtitan.manager.ResourceManager;
import sliedregt.martijn.jtitan.manager.TaskManager;

public abstract class MainGameLogic
{

	protected InputManager input;
	protected ResourceManager rm;
	protected TaskManager tm;
	protected EngineThread et;
	protected Configuration config;
	protected PhysicsManager pm;
	
	private boolean isInitialized;

	public MainGameLogic()
	{
		isInitialized = false;
	}

	protected void initialize()
	{
		if (config == null)
			return;
		// if (isInitialized)
		// return;
		isInitialized = true;
		initializeEngine();
		atInitialize();
		
	}

	private void initializeEngine()
	{
		// if (et == null)
		et = new EngineThread(config);

		input = et.getInput();
		tm = et.getTm();
		rm = et.getRm();
		pm = et.getPm();
	}

	abstract protected void atInitialize();

	
	protected void terminate()
	{
		terminateEngine();
	}

	private void terminateEngine()
	{
		if (!isInitialized)
			return;

		isInitialized = false;
		et.end(this);
	}

	abstract protected void atTerminate();

	public void start()
	{
		// if (!isInitialized)
		initialize();
		// if (et.isAlive()) et.notify();

		et.start();
	}

	protected InputManager getInput()
	{
		return input;
	}

	protected ResourceManager getRm()
	{
		return rm;
	}

	protected TaskManager getTm()
	{
		return tm;
	}

	protected EngineThread getEt()
	{
		return et;
	}

	protected Configuration getConfig()
	{
		return config;
	}

	protected boolean isInitialized()
	{
		return isInitialized;
	}

}
