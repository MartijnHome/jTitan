package sliedregt.martijn.jtitan.datatype.task;

import java.util.UUID;

public abstract class JTitanTask
{

	private volatile TaskState state;
	private volatile TaskConfiguration config;
	private String description;
	protected UUID uuid;

	private Thread tt;

	public JTitanTask(boolean tryOnce)
	{
		uuid = UUID.randomUUID();
		description = "";
		state = new TaskState();
		config = new TaskConfiguration();
		config.setTryOnce(tryOnce);

		tt = new Thread()
		{

			@Override
			public void run()
			{
				final TaskState state = getTaskState();
				if (!state.isFinished())
				{
					state.setRunning(true);
					while (!state.isFinished())
					{
						state.setFinished(doTask());
						if (!config.isNoSleep())
							try
							{
								sleep(50);
							} catch (InterruptedException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				} else
				{
					state.setRunning(false);
				}

			}

		};
	}

	protected abstract boolean doTask();

	public final void run()
	{
		if (!state.isRunning())
		{
			state.setRunning(true);
			tt.start();
		}
	}

	public synchronized TaskState getTaskState()
	{
		return state;
	}

	public synchronized TaskConfiguration getConfig()
	{
		return config;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public UUID getUuid()
	{
		return uuid;
	}

	public void setUuid(UUID uuid)
	{
		this.uuid = uuid;
	}

}
