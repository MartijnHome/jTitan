package sliedregt.martijn.jtitan.datatype.primal;

public class Scale
{

	public float x, y, z;

	public Scale(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Scale()
	{
		set(0f, 0f, 0f);
	}

	public void set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setX(float x)
	{
		this.x = x;
	}
	
	public void setY(float y)
	{
		this.y = y;
	}
	
	public void setZ(float z)
	{
		this.z = z;
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public float getZ()
	{
		return z;
	}
	
}
