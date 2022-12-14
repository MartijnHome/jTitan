package sliedregt.martijn.jtitan.datatype.task;

import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.datatype.resource.Resource;

public class ResourceTask extends EngineTask
{

	private Resource r;
	private int id;
	private Configuration c;

	public ResourceTask(Resource r, Configuration c)
	{
		super(false);
		this.r = r;
		this.id = r.getIndex();
		this.c = c;
		this.setDescription("Load Resource: " + r.getIndex());
		this.getConfig().setOnlyEngine(true);
	}

	@Override
	protected boolean doTask()
	{
		if (r.isLoaded())
		{
			r.setLoading(false);
			return true;
		}

		r.initialize(c);
		if (!r.isLoaded())
			r.setLoading(false);
		return false;
	}

	public Resource getR()
	{
		return r;
	}

	public int getId()
	{
		return id;
	}

	public Configuration getC()
	{
		return c;
	}

}
