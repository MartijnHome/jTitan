package sliedregt.martijn.jtitan.datatype.primal;

public class Normal
{

	public float x, y, z;

	public Normal(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Normal()
	{
		set(0f, 0f, 0f);
	}

	public void set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
