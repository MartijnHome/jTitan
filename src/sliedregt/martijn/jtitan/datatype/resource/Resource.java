package sliedregt.martijn.jtitan.datatype.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.interfaces.Manageable;

public abstract class Resource implements Manageable, Serializable
{

	private static final long serialVersionUID = 1L;

	volatile boolean isLoaded = false;
	volatile boolean isLoading = false;
	public volatile boolean loadInContext = false;

	int index = -1;
	String[] file = null;

	String customLabel;

	{
		customLabel = "";
	}

	private List<Integer> owner = new ArrayList<Integer>();

	public int getIndex()
	{
		return index;
	}

	@Override
	final public boolean initialize(Configuration c)
	{
		if (!isLoaded)
		{
			if (!isLoading)
			{
				isLoading = true;
			}
			isLoaded = load(c);
			if (isLoaded)
				isLoading = false;
			return isLoaded;
		} else
		{
			isLoading = false;
		}
		return true;
	}

	protected abstract boolean load(Configuration c);

	@Override
	final public boolean isLoading()
	{
		return isLoading;

	}

	@Override
	final public boolean isLoaded()
	{
		return isLoaded;

	}

	@Override
	public int[] getRequired()
	{
		return null;
	}

	@Override
	final public boolean release()
	{
		//if (!owner.isEmpty())
			//return false;
		if (isLoaded)
			isLoaded = unload();
		return !isLoaded;
	}

	protected abstract boolean unload();

	public boolean hasNoOwners()
	{
		return owner.isEmpty();
	}

	public int checkOwner(int i)
	{
		for (int j = 0; j < owner.size(); j++)
		{
			if (owner.get(j).intValue() == i)
				return j;
		}
		return -1;
	}

	public boolean addOwner(int i)
	{
		if (checkOwner(i) == -1)
		{
			owner.add(i);
			return true;
		}
		return false;
	}

	public boolean removeOwner(int i)
	{
		if (checkOwner(i) != -1)
		{
			owner.remove(checkOwner(i));
			return true;
		}
		return false;
	}

	public void setLoadingTrue()
	{
		isLoading = true;
	}

	public void setLoading(boolean l)
	{
		isLoading = l;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public String getCustomLabel()
	{
		return customLabel;
	}

	public void setCustomLabel(String customLabel)
	{
		this.customLabel = customLabel;
	}

	// public abstract boolean readFromFile();
}
