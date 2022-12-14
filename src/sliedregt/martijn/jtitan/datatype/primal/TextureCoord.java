package sliedregt.martijn.jtitan.datatype.primal;

public class TextureCoord
{

	public float x, y;

	public TextureCoord(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public TextureCoord()
	{
		set(0f, 0f);
	}

	public void set(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
}
