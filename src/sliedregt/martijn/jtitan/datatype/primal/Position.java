package sliedregt.martijn.jtitan.datatype.primal;

public class Position implements Cloneable
{

	public float x, y, z, w;

	public Position(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Position()
	{
		set(0f, 0f, 0f, 0f);
	}

	public Position(Position p)
	{
		this(p.x, p.y, p.z, p.w);
	}

	public void set(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
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
	
	public void setW(float w)
	{
		this.w = w;
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
	
	public float getW()
	{
		return w;
	}
	
	public Position clone()
	{
		return new Position(this.x, this.y, this.z, this.w);
	}
}
