package sliedregt.martijn.jtitan.interfaces;

import sliedregt.martijn.jtitan.config.Configuration;

public interface Manageable
{

	public boolean initialize(Configuration c);

	public boolean isLoaded();

	public boolean isLoading();

	public boolean release();

	public int[] getRequired();

}
