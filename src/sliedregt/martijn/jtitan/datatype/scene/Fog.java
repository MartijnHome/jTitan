package sliedregt.martijn.jtitan.datatype.scene;

import sliedregt.martijn.jtitan.datatype.primal.Color;
import sliedregt.martijn.jtitan.datatype.resource.Texture;

public class Fog
{
	private boolean enabled;
	private Color color;
	private float start;
	private float end;
	private Texture fogTexture;
	
	public Fog()
	{
		fogTexture = null;
		setEnabled(false);
		setColor(new Color(0f, 0f, 0f, 0f));
		setStart(0f);
		setEnd(0f);
	}
	
	public Fog(Color color, float start, float end)
	{
		fogTexture = null;
		setEnabled(true);
		this.setColor(color);
		this.setStart(start);
		this.setEnd(end);
	}

	public float getEnd()
	{
		return end;
	}

	public void setEnd(float end)
	{
		this.end = end;
	}

	public float getStart()
	{
		return start;
	}

	public void setStart(float start)
	{
		this.start = start;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public Texture getFogTexture()
	{
		return fogTexture;
	}

	public void setFogTexture(Texture fogTexture)
	{
		this.fogTexture = fogTexture;
	}
}
