package sliedregt.martijn.jtitan.datatype.primal;

public class Direction implements Cloneable
{

	public float x, y, z;

	public Direction(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Direction()
	{
		set(0f, 0f, 0f);
	}

	public Direction(Direction p)
	{
		this(p.x, p.y, p.z);
	}

	public void set(float x, float y, float z)
	{
		setX(x);
		setY(y);
		setZ(z);
	}
	
	public void setX(float x)
	{
		this.x = (x < -1.0f) ? -1.0f : (x > 1.0f) ? 1.0f : x;
	}
	
	public void setY(float y)
	{
		this.y = (y < -1.0f) ? -1.0f : (y > 1.0f) ? 1.0f : y;
	}
	
	public void setZ(float z)
	{
		this.z = (z < -1.0f) ? -1.0f : (z > 1.0f) ? 1.0f : z;
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
	
	@Override
	public Direction clone()
	{
		return new Direction(this.x, this.y, this.z);
	}
}
