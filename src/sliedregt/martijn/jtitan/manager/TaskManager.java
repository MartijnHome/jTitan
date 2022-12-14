package sliedregt.martijn.jtitan.manager;

import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.datatype.task.EngineTask;
import sliedregt.martijn.jtitan.datatype.task.JTitanTask;
import sliedregt.martijn.jtitan.datatype.task.SyncedTaskList;

public class TaskManager
{

	private SyncedTaskList<EngineTask> et;
	private SyncedTaskList<JTitanTask> jt;

	public TaskManager()
	{
		et = new SyncedTaskList<EngineTask>();
		jt = new SyncedTaskList<JTitanTask>();
	}

	public void addTask(EngineTask t)
	{
		synchronized (et)
		{
			et.addTask(t);
		}
	}

	public void addTask(JTitanTask t)
	{
		synchronized (jt)
		{
			jt.addTask(t);
		}
	}

	public void update(Configuration config)
	{
		synchronized (et)
		{
			et.update(config);
		}
		synchronized (jt)
		{
			jt.update(config);
		}
	}

	public SyncedTaskList<EngineTask> getEt()
	{
		return et;
	}

	public SyncedTaskList<JTitanTask> getJt()
	{
		return jt;
	}
}
