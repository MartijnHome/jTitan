package sliedregt.martijn.jtitan.datatype.primal;

public class Rotation
{

	private float x, y, z;

	public Rotation(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Rotation()
	{
		set(0f, 0f, 0f);
	}

	public void set(float x, float y, float z)
	{
		setX(x);
		setY(y);
		setZ(z);
	}
	
	public void setX(float x)
	{
		while (x < 0.0f)
			x += 360.0f;
		this.x = x % 360.0f;
	}
	
	public void setY(float y)
	{
		while (y < 0.0f)
			y += 360.0f;
		this.y = y % 360.0f;
	}
	
	public void setZ(float z)
	{
		while (z < 0.0f)
			z += 360.0f;
		this.z = z % 360.0f;
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
