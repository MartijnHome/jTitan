package sliedregt.martijn.jtitan.datatype;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HeightMap
{

	private int tx;
	private int tz;

	private float[][] h;

	public HeightMap(int x, int z)
	{
		init(x, z);
	}

	private void init(int x, int z)
	{
		tx = x;
		tz = z;
		
		h = new float[tx][tz];
		
		for (int i = 0; i < tx; i++)
			for(int j = 0; j < tz; j++)
				h[i][j] = 0.0f;
		
	}

	public HeightMap(String f)
	{
		BufferedImage image = null;

		try
		{
			image = ImageIO.read(new File(f));
			init(image.getWidth(), image.getHeight());
			for (int i = 0; i < tx ; i++)
				for (int j = 0; j < tz; j++)
					h[i][j] = (float) (image.getRGB(i, j) & 0xff);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getTx()
	{
		return tx;
	}

	public int getTz()
	{
		return tz;
	}

	public float[][] getH()
	{
		return h;
	}

	public void setHeight(int x, int z, float h)
	{
		this.h[x][z] = h;
	}
	
	public void offsetHeight(float h)
	{
		for (int i = 0; i < tx; i++)
			for (int j = 0; j < tz; j++)
			{
				this.h[i][j] += h;
			}
	}
	
	public void makeHills(float h)
	{
		for (int i = 0; i < tx; i++)
			for (int j = 0; j < tz; j++)
			{
				float angle = (float) Math.sin(Math.toRadians((60.0f * (float) i) + (60.0f * (float) j)));
				this.h[i][j] += angle * h;
			}
	}
	
	public void addRandomHeight(float h)
	{
		for (int i = 0; i < tx; i++)
			for (int j = 0; j < tz; j++)
				this.h[i][j] += (float) Math.random() * h;
			
	}
	
	public final float getHeightAtLocation(float x, float z)
	{
		int tileX = (int) x;
		int tileZ = (int) z;
		int select = 0;
		
		tileX = (tileX < 0) ? 0 : (tileX >= tx) ? tx - 1 : tileX;
		tileZ = (tileZ < 0) ? 0 : (tileZ >= tz) ? tz - 1 : tileZ;

		if (tileX == tx - 1) select += 1;	//X is at the edge of the height map
		if (tileZ == tz - 1) select += 2;	//Z is at the edge of the height map
		
		switch (select)
		{
			case 1:
				{	//Interpolate between Z
					float a = h[tileX][tileZ];
					float b = h[tileX][tileZ+1];
					return a + ((b - a) * (z % 1.0f));
				}
			case 2:
				{	//Interpolate between X
					float a = h[tileX][tileZ];
					float b = h[tileX+1][tileZ];
					return a + ((b - a) * (x % 1.0f));
				}
			case 3: 
				{	//Return last X&Z height
					return h[tileX][tileZ];
				}
			default:
				{	//Full interpolation
					float a = h[tileX][tileZ] + ((h[tileX+1][tileZ] - h[tileX][tileZ]) * (x % 1.0f));
					float b = h[tileX][tileZ+1] + ((h[tileX+1][tileZ+1] - h[tileX][tileZ+1]) * (x % 1.0f));
					return a + ((b - a) * (z % 1.0f));
				}
		}
	}
	
	public final float getHeightAtLocation(int x, int z) {
		return h[x][z];
	}
}
