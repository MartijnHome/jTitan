package sliedregt.martijn.jtitan.datatype.resource.special;

import sliedregt.martijn.jtitan.datatype.resource.Mesh;
import sliedregt.martijn.jtitan.util.MeshUtil;

public class TerrainMesh extends Mesh
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int[][] vertexIndex;

	//private float width;
	//private float depth;
	//private int tx;
	//private int tz;

	public TerrainMesh(float width, float depth, int tx, int tz)
	{
		//this.width = width;
		//this.depth = depth;
		//this.tx = tx;
		//this.tz = tz;

		final Mesh t = MeshUtil.generateTerrain(100f, 100f, 155, 155);
		this.getVertices().addAll(t.getVertices());
		this.setIndices(t.getIndices());
		vertexIndex = new int[tx + 1][tz + 1];

		for (int i = 0, c = 0; i <= tx; i++)
		{
			for (int j = 0; j <= tz; j++)
			{
				vertexIndex[i][j] = c;
				c++;
			}
		}
	}

	public int[][] getVertexIndex()
	{
		return vertexIndex;
	}

	public int getVertexIndex(int x, int z)
	{
		return vertexIndex[x][z];
	}
}
