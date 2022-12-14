package sliedregt.martijn.jtitan.datatype.primal;

public class Color
{

	public float r, g, b, a;

	public Color(float r, float g, float b, float a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color()
	{
		set(0f, 0f, 0f, 0f);
	}

	public void set(float r, float g, float b, float a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void setR(float r)
	{
		this.r = (r < 0.0f) ? 0.0f : (r > 1.0f) ? 1.0f : r;
	}
	
	public void setG(float g)
	{
		this.g = (g < 0.0f) ? 0.0f : (g > 1.0f) ? 1.0f : g;
	}
	
	public void setB(float b)
	{
		this.b = (b < 0.0f) ? 0.0f : (b > 1.0f) ? 1.0f : b;
	}
	
	public void setA(float a)
	{
		this.a = (a < 0.0f) ? 0.0f : (a > 1.0f) ? 1.0f : a;
	}
	
	public float getR()
	{
		return r;
	}
	
	public float getG()
	{
		return g;
	}
	
	public float getB()
	{
		return b;
	}
	
	public float getA()
	{
		return a;
	}
}
