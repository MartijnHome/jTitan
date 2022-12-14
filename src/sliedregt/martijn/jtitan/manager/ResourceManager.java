package sliedregt.martijn.jtitan.manager;

import java.util.ArrayList;
import java.util.List;

import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.datatype.resource.Model;
import sliedregt.martijn.jtitan.datatype.resource.Resource;
import sliedregt.martijn.jtitan.datatype.scene.Scene;
import sliedregt.martijn.jtitan.datatype.task.EngineTask;
import sliedregt.martijn.jtitan.datatype.task.ResourceTask;

public class ResourceManager
{
	private List<Resource> r;
	private List<Integer> f;
	private Configuration c;
	private TaskManager tm;

	public ResourceManager(Configuration c, TaskManager tm)
	{
		r = new ArrayList<Resource>();
		f = new ArrayList<Integer>();
		this.c = c;
		this.tm = tm;
	}

	public boolean loadAll()
	{
		for (Resource i : r)
		{
			boolean v = i.isLoaded();
			if (v)
				continue;
			i.initialize(c);
			if (!i.isLoaded())
				return false;
		}
		return true;
	}

	public EngineTask loadScene(final Scene sc)
	{
		
		Model[] m = new Model[sc.getR().size()];
		for (int i = 0; i < m.length; i++)
		{
			m[i] = sc.getR().get(i).getModel();
		}

		List<ResourceTask> rt = new ArrayList<ResourceTask>();

		for (int i = 0; i < m.length; i++)
		{
			boolean dupe = false;
			if (m[i].getRequired() == null)
			{

				for (ResourceTask t : rt)
				{
					if (t.getId() == m[i].getIndex())
						dupe = true;
				}
			} else
			{
				for (int j = 0; j < m[i].getRequired().length; j++)
				{
					boolean duped = false;
					for (ResourceTask t : rt)
					{
						if (t.getId() == m[i].getRequired()[j])
							duped = true;
					}
					if (!duped)
						rt.add(new ResourceTask(r.get(m[i].getRequired()[j]), c));
				}
			}
			if (!dupe)
				rt.add(new ResourceTask(m[i], c));
		}

		EngineTask e = new EngineTask(false)
		{
			{
				if (c.isLoadMultiThreaded())
				{
					for (ResourceTask j : rt)
					{
						synchronized (tm)
						{
							if (!j.getConfig().isOnlyEngine())
								tm.addTask(j);
						}
					}
				}
				this.setDescription("Load Scene - MultiLoad: " + c.isLoadMultiThreaded());
				this.getConfig().setOnlyEngine(true);
			}

			@Override
			protected boolean doTask()
			{
				for (ResourceTask t : rt)
				{
					if (!r.get(t.getId()).isLoaded())
					{
						if (t.getConfig().isOnlyEngine())
						{
							r.get(t.getId()).initialize(c);
							return false;
						}
						if (c.isLoadMultiThreaded())
						{
							return false;
						}
						if (!r.get(t.getId()).isLoading())
						{
							if (!c.isLoadMultiThreaded())
								r.get(t.getId()).initialize(c);
							return false;
						}
					}
				}
				this.getTaskState().setFinished(true);
				return true;
			}
		};

		return e;
	}

	public boolean unloadAll()
	{
		//boolean done = false;
		int c = r.size();
		//boolean succes = false;
		for (int i = 0; i < c; i++)
			if (r.get(i).isLoaded())
				r.get(i).release();			
		
		/*
		while (!done)
		{
			done = true;
			succes = false;
			for (int i = 0; i < c; i++)
			{
				if (r.get(i).isLoaded())
				{
					done = false;
					//if (r.get(i).hasNoOwners())
					//{
						if (r.get(i).release())
						{
							succes = true;
							break;
						}
					//}
				}
			}
			if (!succes)
				return false;
		}
		*/
		return true;
	}

	public void addResource(Resource r)
	{
		// if (f.isEmpty()) {
		this.r.add(r);
		r.setIndex(this.r.size() - 1);
		/*
		 * } /*else {
		 * 
		 * int i = f.get(0); this.r.set(i, r); r.index = i; f.remove(0); }
		 */
	}

	public boolean removeResource(int i)
	{
		if (r.get(i).isLoaded())
			if (r.get(i).release())
				return false;
		f.add(i);
		return true;
	}

	public boolean removeResource(Resource v)
	{
		return removeResource(v.getIndex());
	}
}
