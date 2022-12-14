package sliedregt.martijn.jtitan.datatype.primal;

public class Material
{

	private Color ambient;
	private Color diffuse;
	private Color specular;
	private float shininess;

	public Material(Color ambient, Color diffuse, Color specular, float shininess)
	{
		super();
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.setShininess(shininess);
	}

	public Color getAmbient()
	{
		return ambient;
	}

	public void setAmbient(Color ambient)
	{
		this.ambient = ambient;
	}

	public Color getDiffuse()
	{
		return diffuse;
	}

	public void setDiffuse(Color diffuse)
	{
		this.diffuse = diffuse;
	}

	public Color getSpecular()
	{
		return specular;
	}

	public void setSpecular(Color specular)
	{
		this.specular = specular;
	}

	public float getShininess()
	{
		return shininess;
	}

	public void setShininess(float shininess)
	{
		this.shininess = shininess;
	}

}
