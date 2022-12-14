package sliedregt.martijn.jtitan.datatype.resource;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.datatype.primal.Indices;
import sliedregt.martijn.jtitan.datatype.primal.Vertex;

public class Mesh extends Resource
{

	private static final long serialVersionUID = 1L;
	private List<Vertex> vertices;

	private transient int vaoId;
	private int vboId;
	private int vboIId;
	private int vertexCount;

	private Indices indices;

	public int getVaoId()
	{
		return vaoId;
	}

	public int getVertexCount()
	{
		return vertexCount;
	}

	public void setIndices(Indices indices)
	{
		this.indices = indices;
	}

	public void cleanUp()
	{
		glDisableVertexAttribArray(0);

		// Delete the VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboId);

		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
	}

	public Mesh()
	{
		vertices = Collections.synchronizedList(new ArrayList<Vertex>());
	}

	public Mesh(List<Vertex> vertices)
	{
		this.vertices = vertices;
	}

	public List<Vertex> getVertices()
	{
		return vertices;
	}

	@Override
	protected boolean load(Configuration c)
	{
		if (c.isShowDebugFrame())
		{
			c.getDebugFrame().writeLogText("Creating mesh from vertices");
		}
		
		if (this.isLoaded == true)
			return true;

		synchronized (vertices)
		{
			if (!vertices.isEmpty())
			{
				if (c.getGraphicsApi().getVersion() == 10L)
				{
					isLoaded = true;
					return true;
				}
				vertexCount = vertices.size();
				FloatBuffer verticesBuffer = null;
				IntBuffer indicesBuffer = null;
				try
				{
					verticesBuffer = MemoryUtil.memAllocFloat(vertexCount * Vertex.elementCount);
					int pC = vertexCount * Vertex.elementCount;

					float[] p = new float[pC];
					int i = 0;
					for (int v = 0; v < vertexCount; v++)
					{
						int j = 0;
						for (float[] e = vertices.get(v).getElements(); j < e.length; j++)
							p[i++] = e[j];
					}
					verticesBuffer.put(p).flip();

					int[] ib = new int[indices.getIndex().size()];
					i = 0;
					indicesBuffer = MemoryUtil.memAllocInt(indices.getIndex().size());
					for (int v = 0; v < indices.getIndex().size(); v++)
					{
						ib[i++] = indices.getIndex().get(v).intValue();
					}

					indicesBuffer.put(ib).flip();

					vaoId = org.lwjgl.opengl.GL30.glGenVertexArrays();
					org.lwjgl.opengl.GL30.glBindVertexArray(vaoId);

					vboId = org.lwjgl.opengl.GL15.glGenBuffers();
					org.lwjgl.opengl.GL15.glBindBuffer(org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER, vboId);

					org.lwjgl.opengl.GL15.glBufferData(org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER, verticesBuffer,
							org.lwjgl.opengl.GL15.GL_STATIC_DRAW);

					int stride = Vertex.stride;
					org.lwjgl.opengl.GL20.glVertexAttribPointer(0, Vertex.positionElementCount,
							org.lwjgl.opengl.GL11.GL_FLOAT, false, stride, Vertex.positionByteOffset);
					org.lwjgl.opengl.GL20.glVertexAttribPointer(1, Vertex.colorElementCount,
							org.lwjgl.opengl.GL11.GL_FLOAT, false, stride, Vertex.colorByteOffset);
					org.lwjgl.opengl.GL20.glVertexAttribPointer(2, Vertex.normalElementCount,
							org.lwjgl.opengl.GL11.GL_FLOAT, false, stride, Vertex.normalByteOffset);
					org.lwjgl.opengl.GL20.glVertexAttribPointer(3, Vertex.textureElementCount,
							org.lwjgl.opengl.GL11.GL_FLOAT, false, stride, Vertex.textureByteOffset);
					org.lwjgl.opengl.GL20.glVertexAttribPointer(4, Vertex.tangentElementCount,
							org.lwjgl.opengl.GL11.GL_FLOAT, false, stride, Vertex.tangentByteOffset);
					org.lwjgl.opengl.GL20.glVertexAttribPointer(5, Vertex.biTangentElementCount,
							org.lwjgl.opengl.GL11.GL_FLOAT, false, stride, Vertex.biTangentByteOffset);
					glBindBuffer(GL_ARRAY_BUFFER, 0);

					org.lwjgl.opengl.GL30.glBindVertexArray(0);

					vboIId = org.lwjgl.opengl.GL15.glGenBuffers();
					org.lwjgl.opengl.GL15.glBindBuffer(org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER, vboIId);
					org.lwjgl.opengl.GL15.glBufferData(org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer,
							org.lwjgl.opengl.GL15.GL_STATIC_DRAW);
					// Deselect (bind to 0) the VBO
					org.lwjgl.opengl.GL15.glBindBuffer(org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
				} finally
				{
					if (verticesBuffer != null)
					{
						MemoryUtil.memFree(verticesBuffer);
					}
				}
				isLoaded = true;
				return true;
			} else
			{
				if (file != null)
				{
					InputStream f;
					InputStream buffer;
					ObjectInput input;

					try
					{
						f = new FileInputStream(file[0]);
						buffer = new BufferedInputStream(f);
						input = new ObjectInputStream(buffer);
						try
						{
							int size = input.readInt();
							for (int i = 0; i < size; i++)
							{
								Vertex v = new Vertex();
								v.setXYZW(input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat());
								v.setRGBA(input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat());
								v.setNOP(input.readFloat(), input.readFloat(), input.readFloat());
								v.setSTR(input.readFloat(), input.readFloat(), 0f);
								vertices.add(v);
							}
						} finally
						{
							input.close();
						}
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return false;
			}
		}
	}

	public void calculateTangents()
	{
		for (int i = 0 ; i < indices.getIndex().size() ; i += 3) {
		    Vertex v0 = vertices.get(indices.getIndex().get(i));
		    Vertex v1 = vertices.get(indices.getIndex().get(i+1));
		    Vertex v2 = vertices.get(indices.getIndex().get(i+2));

		    Vector3f Edge1 = new Vector3f(v1.getXYZ()[0], v1.getXYZ()[1], v1.getXYZ()[2]).sub(v0.getXYZ()[0], v0.getXYZ()[1], v0.getXYZ()[2]);
		    Vector3f Edge2 = new Vector3f(v2.getXYZ()[0], v2.getXYZ()[1], v2.getXYZ()[2]).sub(v0.getXYZ()[0], v0.getXYZ()[1], v0.getXYZ()[2]);

		    float DeltaU1 = v1.getSTR()[0] - v0.getSTR()[0];
		    float DeltaV1 = v1.getSTR()[1] - v0.getSTR()[1];
		    float DeltaU2 = v2.getSTR()[0] - v0.getSTR()[0];
		    float DeltaV2 = v2.getSTR()[1] - v0.getSTR()[1];

		    float f = 1.0f / (DeltaU1 * DeltaV2 - DeltaU2 * DeltaV1);

		    Vector3f tangent, biTangent;
		    tangent = new Vector3f(0f, 0f, 0f);
		    biTangent = new Vector3f(0f, 0f, 0f);
		    
		    tangent.x = f * (DeltaV2 * Edge1.x - DeltaV1 * Edge2.x);
		    tangent.y = f * (DeltaV2 * Edge1.y - DeltaV1 * Edge2.y);
		    tangent.z = f * (DeltaV2 * Edge1.z - DeltaV1 * Edge2.z);

		    biTangent.x = f * (-DeltaU2 * Edge1.x + DeltaU1 * Edge2.x);
		    biTangent.y = f * (-DeltaU2 * Edge1.y + DeltaU1 * Edge2.y);
		    biTangent.z = f * (-DeltaU2 * Edge1.z + DeltaU1 * Edge2.z);

		    v0.setTangent(v0.getTangent()[0] + tangent.x, v0.getTangent()[1] + tangent.y, v0.getTangent()[2] + tangent.z, 0f);
		    v1.setTangent(v1.getTangent()[0] + tangent.x, v1.getTangent()[1] + tangent.y, v1.getTangent()[2] + tangent.z, 0f);
		    v2.setTangent(v2.getTangent()[0] + tangent.x, v2.getTangent()[1] + tangent.y, v2.getTangent()[2] + tangent.z, 0f);
		    
		    v0.setBiTangent(v0.getBiTangent()[0] + biTangent.x, v0.getBiTangent()[1] + biTangent.y, v0.getBiTangent()[2] + biTangent.z, 0f);
		    v1.setBiTangent(v1.getBiTangent()[0] + biTangent.x, v1.getBiTangent()[1] + biTangent.y, v1.getBiTangent()[2] + biTangent.z, 0f);
		    v2.setBiTangent(v2.getBiTangent()[0] + biTangent.x, v2.getBiTangent()[1] + biTangent.y, v2.getBiTangent()[2] + biTangent.z, 0f);
		}

		for (int i = 0 ; i < vertices.size() ; i++) {
		    Vector3f tangent, biTangent;
		    tangent = new Vector3f(vertices.get(i).getTangent()[0], vertices.get(i).getTangent()[1], vertices.get(i).getTangent()[2]);
		    biTangent = new Vector3f(vertices.get(i).getBiTangent()[0], vertices.get(i).getBiTangent()[1], vertices.get(i).getBiTangent()[2]);
		    Vector3f n = new Vector3f(vertices.get(i).getNOP()[0], vertices.get(i).getNOP()[1], vertices.get(i).getNOP()[2]);
		    n = n.cross(tangent);
		    if (n.dot(biTangent) < 0.0f){
		    	tangent.x *= -1.0f;
		    	tangent.y *= -1.0f;
		    	tangent.z *= -1.0f;
		    }
		    
		    tangent.normalize();
		    biTangent.normalize();
		    vertices.get(i).setTangent(tangent.x, tangent.y, tangent.z, 0f);
		    vertices.get(i).setBiTangent(biTangent.x, biTangent.y, biTangent.z, 0f);
		} 
	}
	@Override
	protected boolean unload()
	{
		if (vertices.isEmpty())
			return true;
		vertices.clear();
		((ArrayList<Vertex>) vertices).trimToSize();
		return false;
	}

	public int getVboIId()
	{
		return vboIId;
	}

	public Indices getIndices()
	{
		return indices;
	}

}
