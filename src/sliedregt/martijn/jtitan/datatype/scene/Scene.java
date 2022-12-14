package sliedregt.martijn.jtitan.datatype.scene;

import java.util.ArrayList;

public class Scene
{

	private ArrayList<Renderable> r;
	private ArrayList<Light> l;
	private Fog fog;
	
	public Scene()
	{
		setFog(new Fog());
		r = new ArrayList<Renderable>();
		l = new ArrayList<Light>();
	}

	public Scene(Renderable[] r)
	{
		this();
		for (int i = 0; i < r.length; i++)
			this.r.add(r[i]);
	}

	public ArrayList<Renderable> getR()
	{
		return r;
	}

	public void setR(ArrayList<Renderable> r)
	{
		this.r = r;
	}

	public ArrayList<Light> getL()
	{
		return l;
	}

	public void setL(ArrayList<Light> l)
	{
		this.l = l;
	}

	public Fog getFog()
	{
		return fog;
	}

	public void setFog(Fog fog)
	{
		this.fog = fog;
	}
}
