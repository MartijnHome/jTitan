package sliedregt.martijn.jtitan.datatype.resource;

import java.util.ArrayList;
import java.util.List;

import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.datatype.primal.Indices;
import sliedregt.martijn.jtitan.datatype.primal.Material;
import sliedregt.martijn.jtitan.interfaces.Manageable;

public class Model extends Resource
{

	// Mesh
	// -Vertexdata
	// position x y z w
	// normal x y z
	// tex s t r

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Mesh mesh;
	private Texture texture;
	private Texture normalMap;
	private Texture reflectionTexture;
	private Texture heightTexture;

	
	private ShaderProgram shader;
	private Indices indices;
	private Material material;

	{
		indices = new Indices();
	}

	public Mesh getMesh()
	{
		return mesh;
	}

	public Indices getIndices()
	{
		return indices;
	}

	public void setIndices(Indices indices)
	{
		this.indices = indices;
	}

	public void setMesh(Mesh mesh)
	{
		this.mesh = mesh;
	}

	public Texture getTexture()
	{
		return texture;
	}

	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}

	public ShaderProgram getShaderProgram()
	{
		return shader;
	}

	public void setShaderProgram(ShaderProgram shader)
	{
		this.shader = shader;
	}

	@Override
	protected boolean load(Configuration c)
	{
		if (c.getGraphicsApi().getVersion() == 10L)
			shader = null;

		for (Manageable m : new Manageable[]
		{ mesh, shader, texture, normalMap, heightTexture })
		{
			if (m == null)
				continue;
			((Resource) m).addOwner(index);
			if (!m.isLoaded())
			{
				// if (m.isLoading())
				// return false;
				boolean r = m.initialize(c);
				if (r)
					continue;
				return false;
			}
		}
		return true;

	}

	@Override
	public int[] getRequired()
	{
		List<Resource> i = new ArrayList<Resource>();
		for (Resource m : new Resource[]
		{ mesh, shader, texture, normalMap, heightTexture })
			if (m != null)
				i.add(m);

		int[] id = new int[i.size()];

		for (int j = 0; j < i.size(); j++)
			id[j] = i.get(j).getIndex();

		return id;
	}

	@Override
	protected boolean unload()
	{
		return false;
		/*
		boolean all = true;
		for (Manageable m : new Manageable[]
		{ mesh, shader, texture })
		{
			if (m.isLoaded())
			{
				boolean r = m.release();
				if (!r)
					all = false;
			}
		}
		return all;
		*/
	}

	public Material getMaterial()
	{
		return material;
	}

	public void setMaterial(Material material)
	{
		this.material = material;
	}

	public Texture getNormalMap()
	{
		return normalMap;
	}

	public void setNormalMap(Texture normalMap)
	{
		this.normalMap = normalMap;
	}

	public Texture getReflectionTexture()
	{
		return reflectionTexture;
	}

	public void setReflectionTexture(Texture reflectionTexture)
	{
		this.reflectionTexture = reflectionTexture;
	}

	public Texture getHeightTexture()
	{
		return heightTexture;
	}

	public void setHeightTexture(Texture heightTexture)
	{
		this.heightTexture = heightTexture;
	}

}
