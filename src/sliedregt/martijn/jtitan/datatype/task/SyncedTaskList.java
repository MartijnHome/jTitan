package sliedregt.martijn.jtitan.datatype.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sliedregt.martijn.jtitan.config.Configuration;

public class SyncedTaskList<T extends JTitanTask>
{

	private List<T> currentTasks;
	private List<T> newTasks;

	public SyncedTaskList()
	{
		currentTasks = Collections.synchronizedList(new ArrayList<T>());
		newTasks = Collections.synchronizedList(new ArrayList<T>());
	}

	public void addTask(T t)
	{
		synchronized (newTasks)
		{
			newTasks.add(t);
		}
	}

	public List<T> getCurrentTasks()
	{
		synchronized (currentTasks)
		{
			return currentTasks;
		}
	}

	public List<T> getNewTasks()
	{
		synchronized (newTasks)
		{
			return newTasks;
		}
	}

	public void update(Configuration config)
	{
		final List<T> tasks = new ArrayList<T>();

		synchronized (newTasks)
		{
			tasks.addAll(newTasks);
			newTasks.clear();
		}
		synchronized (currentTasks)
		{
			for (T t : currentTasks)
				if (!t.getTaskState().isFinished())
					tasks.add(t);
			currentTasks.clear();
			currentTasks.addAll(tasks);
		}

		for (T jt : tasks)
		{
			if (!jt.getTaskState().isRunning())
			{
				if (!jt.getConfig().isOnlyEngine() && config.isLoadMultiThreaded())
				{
					jt.run();
				} else
				{
					jt.getTaskState().setFinished(jt.doTask());
				}
			}
		}
	}
}
