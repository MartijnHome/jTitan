package sliedregt.martijn.jtitan.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import sliedregt.martijn.jtitan.datatype.HeightMap;
import sliedregt.martijn.jtitan.datatype.primal.Indices;
import sliedregt.martijn.jtitan.datatype.primal.Position;
import sliedregt.martijn.jtitan.datatype.primal.Vertex;
import sliedregt.martijn.jtitan.datatype.resource.Mesh;

public class MeshUtil
{

	public static final int AUTODETECT = 0, WAVEFRONT_OBJ = 1, JTITAN_JTT = 2;
	
	public static Mesh generateTerrain(float width, float depth, HeightMap h)
	{
		return generateTerrain(width, depth, h, 1.0f);
	}

	public static Mesh generateTerrain(float width, float depth, HeightMap h, float heightModifier)
	{
		final List<Vertex> lv = new ArrayList<Vertex>();
		final Indices indices = new Indices();

		final int x = h.getTx();
		final int z = h.getTz();

		final float tileW =  width / ((float) x - 1);
		final float tileD =  depth / ((float) z - 1);
		final float startX = width / -2.0f;
		final float startZ = depth / -2.0f;

		Vertex[][] v = new Vertex[x][z];
		int[][] vi = new int[x][z];
		int c = 0;

		for (int i = 0; i < x; i++)
		{
			for (int j = 0; j < z; j++)
			{
				final float[] st = new float[]
				{ (float) i, (float) j };
				Position p = new Position();
				p.x = startX + (tileW * ((float) i));
				p.z = startZ + (tileD * ((float) j));
				p.y = ((float) h.getH()[i][j]) * heightModifier;
				v[i][j] = new Vertex()
				{
					{
						this.setXYZW(p.x, p.y, p.z, 1.0f);
						float color = 0.4f + ((float) Math.random() * 0.6f);
						//float color = 1.0f;
						this.setRGBA(color, color, color, 1f);
						float layer = 0f;
						if (p.y > 2.2f)
						{
							layer = (p.y - 2.2f) / (10.0f);
						}
						//layer = (float) Math.round(layer);
						/*
						if (p.y > 3.2f) layer += 0.5f;
						if (p.y > 7.0f) layer += 0.5f;
						if (p.y > 15.0f) layer += 0.5f;
						if (p.y > 26.0f) layer += 0.5f;
						if (p.y > 37.0f) layer += 0.5f;
						if (p.y > 42.0f) layer += 0.5f;
						
						if (p.y > 1.6f) layer += 0.5f;
						if (p.y > 4.0f) layer += 0.5f;
						if (p.y > 12.0f) layer += 0.5f;
						if (p.y > 20.0f) layer += 0.5f;
						if (p.y > 31.0f) layer += 0.5f;
						if (p.y > 39.5f) layer += 0.5f;
						*/
						
						if (layer < 0.0f) layer = 0.0f;
						if (layer > 6.0f) layer = 6.0f;
						
						//layer = (float) Math.round((float) Math.random() * 6.0f);
						this.setSTR(st[0], st[1], layer);
					}
				};
				vi[i][j] = c;

				c++;
			}
		}

		v = smooth(v);
		for (int i = 0; i < x; i++)
			for (int j = 0; j < z; j++)
				h.setHeight(i, j, v[i][j].getXYZ()[1]);
		
		for (int i = 0; i < x; i++)
		{
			for (int j = 0; j < z; j++)
			{
				Vector3f n = calculate_normal(v, i, j, tileW, tileD);
				v[i][j].setNOP(n.x, n.y, n.z);
				lv.add(v[i][j]);
				/*
				 * if (i < x - 1 && j < z - 1 && i > 0 && j > 0) { if (v[i][j].getXYZ()[1] <
				 * v[i+1][j+1].getXYZ()[1]) { float[] rgb = v[i-1][j-1].getRGB(); float cr =
				 * rgb[0] - 0.1f, cg = rgb[1] - 0.1f, cb = rgb[2] - 0.1f; v[i][j].setRGB(cr, cg,
				 * cb); } }
				 */
			}
		}
		for (int i = 0; i < x - 1; i++)
		{
			for (int j = 0; j < z - 1; j++)
			{
				int[] ind = new int[]
				{ vi[i + 1][j], vi[i][j], vi[i][j + 1], vi[i][j + 1], vi[i + 1][j + 1], vi[i + 1][j] };
				for (int k = 0; k < 6; k++)
				{
					indices.getIndex().add(ind[k]);
				}
			}
		}

		Mesh m = new Mesh(lv);
		m.setIndices(indices);
		m.calculateTangents();
		return m;
	}

	
	private static Vertex[][] smooth(Vertex[][] v)
	{
		final int x = v.length - 1;
		final int z = v[0].length - 1;

		for (int l = 0; l < 1; l++)
		{
			for (int i = 1; i < x; i++)
			{
				for (int j = 1; j < z; j++)
				{
					float[] x1;
					float[] c1;
					x1 = v[i][j].getXYZW();
					c1 = v[i][j].getRGB();
					float[] y1;
					float[] c2;
					y1 = new float[]
					{ v[i + 1][j].getXYZW()[1], v[i][j + 1].getXYZW()[1], v[i - 1][j].getXYZW()[1],
							v[i][j - 1].getXYZW()[1] };
					c2 = new float[]
							{ v[i + 1][j].getRGB()[1], v[i][j + 1].getRGB()[1], v[i - 1][j].getRGB()[1],
									v[i][j - 1].getRGB()[1] };
					float ah = (y1[0] + y1[1] + y1[2] + y1[3] + x1[1]) / 5.0f;
					float ch =  (c2[0] + c2[1] + c2[2] + c2[3] + c1[1]) / 5.0f;
					float dh = x1[1] - ah;
					float ny = x1[1] - (dh / 2.0f);
					v[i][j].setXYZW(x1[0], ny, x1[2], x1[3]);
					v[i][j].setRGB(ch, ch, ch);
				}
			}
		}

		return v;
	}

	public static Mesh generateTerrain(float width, float depth, int x, int z)
	{
		
			//return generateTerrain(width, depth, new HeightMap(x + 1, z + 1));
		

		final List<Vertex> lv = new ArrayList<Vertex>();
		final Indices indices = new Indices();

		final float tileW = width / ((float) x);
		final float tileD = depth / ((float) z);
		final float startX = width / -2.0f;
		final float startZ = depth / -2.0f;

		Vertex[][] v = new Vertex[x + 1][z + 1];
		int[][] vi = new int[x + 1][z + 1];
		int c = 0;

		for (int i = 0; i <= x; i++)
		{
			for (int j = 0; j <= z; j++)
			{
				final float[] st = new float[]
				{ (float) i, (float) j };
				
				Position p = new Position();
				p.x = startX + (tileW * ((float) i));
				p.z = startZ + (tileD * ((float) j));
				p.y = 0.0f;
				p.w = 1.0f;
				
				v[i][j] = new Vertex()
				{
					{
						//float color = 0.4f + ((float) Math.random() * 0.6f);
						//this.setRGBA(color, color, color, 1f);
						this.setXYZW(p.x, p.y, p.z, p.w);
						this.setNOP(0.0f, 1.0f, 0.0f);
						this.setRGB(1.0f, 1.0f, 1.0f);
						this.setSTR(st[0], st[1], 0f);
					}
				};
				vi[i][j] = c;
				c++;
			}
		}

		for (int i = 0; i <= x; i++)
		{
			for (int j = 0; j <= z; j++)
			{
				//Vector3f n = calculate_normal(v, i, j, tileW, tileD);
				//v[i][j].setNOP(n.x, n.y, n.z);
				// float cc =
				// Position cc = new Position();

				lv.add(v[i][j]);
			}
		}
		for (int i = 0; i < x; i++)
		{
			for (int j = 0; j < z; j++)
			{
				int[] ind = new int[]
				{ vi[i + 1][j], vi[i][j], vi[i][j + 1], vi[i][j + 1], vi[i + 1][j + 1], vi[i + 1][j] };
				for (int k = 0; k < 6; k++)
				{
					indices.getIndex().add(ind[k]);
				}
			}
		}

		Mesh m = new Mesh(lv);
		m.setIndices(indices);
		//m.calculateTangents();
		return m;
	}

	private static Vector3f calculate_normal(Vertex[][] v, int x, int z, float width, float depth)
	{
		Position[][] p = new Position[3][3];

		Position posi = new Position();
		posi.x = v[x][z].getXYZ()[0];
		posi.y = v[x][z].getXYZ()[1];
		posi.z = v[x][z].getXYZ()[2];
		p[1][1] = posi;
	

		for (int i = -1; i < 2; i++)
		{
			for (int j = -1; j < 2; j++)
			{
				if (i == 0 && j == 0)
					continue;

				int vx = x + i;
				int vz = z + j;

				boolean legal = true;

				if (vx < 0)
				{
					legal = false;
				}
				if (vz < 0)
				{
					legal = false;
				}
				if (vx > v.length - 1)
				{
					legal = false;
				}
				if (vz > v[0].length - 1)
				{
					legal = false;
				}

				Position pos = new Position();
				if (legal)
				{
					pos.x = v[vx][vz].getXYZ()[0];
					pos.y = v[vx][vz].getXYZ()[1];
					pos.z = v[vx][vz].getXYZ()[2];
				} else
				{
					pos.x = v[x][z].getXYZ()[0] + (((float) i) * width);
					pos.y = v[x][z].getXYZ()[1];
					pos.z = v[x][z].getXYZ()[2] + (((float) j) * depth);
				}
				p[i + 1][j + 1] = pos;
			}
		}
		
		/*
		for (int i = -1; i < 2; i++)
		{
			for (int j = -1; j < 2; j++)
			{
				if (i == 0 && j == 0)
					continue;

				int vx = x + i;
				int vz = z + j;

				int ex = 0;
				int ez = 0;

				if (vx < 0)
				{
					ex = -1;
				}
				if (vz < 0)
				{
					ez = -1;
				}
				if (vx > v.length - 1)
				{
					ex = 1;
				}
				if (vz > v[0].length - 1)
				{
					ez = 1;
				}

				Position pos = new Position();
				if (ex == 0 && ez == 0)
				{
					pos.x = v[vx][vz].getXYZ()[0];
					pos.y = v[vx][vz].getXYZ()[1];
					pos.z = v[vx][vz].getXYZ()[2];
				} else
				{
					float ew = (ex == 0) ? 0.0f : (ex == -1) ? -width : width;
					float ed = (ez == 0) ? 0.0f : (ez == -1) ? -depth : depth;
					pos.x = v[x][z].getXYZ()[0] + ew;
					pos.y = v[x][z].getXYZ()[1];
					pos.z = v[x][z].getXYZ()[2] + ed;
				}

				p[i + 1][j + 1] = pos;
			}
		}
		 */
		Vector3f[] cv = new Vector3f[8];

		for (int i = 0; i < 8; i++)
		{
			int px = new int[]
			{ 0, 0, 0, 1, 2, 2, 2, 1 }[i];
			int pz = new int[]
			{ 0, 1, 2, 2, 2, 1, 0, 0 }[i];
			Position pc = p[1][1];
			Position pt = p[px][pz];
			cv[i] = new Vector3f(pc.x - pt.x, pc.y - pt.y, pc.z - pt.z);
		}

		Vector3f[] crv = new Vector3f[8];
		for (int i = 0; i < 8; i++)
		{
			//crv[i] = cv[i].cross(cv[(i + 1 == 8) ? 0 : i + 1]);
		
			crv[i] = new Vector3f(cv[i]).cross(cv[(i + 1 == 8) ? 0 : i + 1]);
			crv[i] = crv[i].normalize();
		}

		Vector3f n = new Vector3f();
		for (int i = 0; i < 8; i++)
		{
			n.add(crv[i]);
		}
		n.normalize();

		return n;
		/*
		 * float hl = v[minx][z].getXYZW()[1]; float hr = v[addx][z].getXYZW()[1]; float
		 * hd = v[x][addz].getXYZW()[1]; Terrain expands towards -Z float hu =
		 * v[x][minz].getXYZW()[1]; Vector3f n = new Vector3f(hl - hr, 2.0f, hd - hu);
		 * n.normalize(); return n;
		 */
	}
	
	public static Mesh importFromFile(String filename)
	{
		return importFromFile(filename, AUTODETECT);
	}

	public static Mesh importFromFile(String filename, int type)
	{
		switch(type)
		{
		case 1:
			return importFromOBJ(filename);
		case 2:
			return importFromJTitan(filename);
		default:
			return importFromOBJ(filename);
		}
	}
	
	private static Mesh importFromJTitan(String filename)
	{
		FileInputStream fis = null;
		DataInputStream dis = null;
		
		try
		{
			fis = new FileInputStream(filename);
			dis = new DataInputStream(fis);
			
			List<Vertex> v = new ArrayList<Vertex>();
			Indices in = new Indices();
			
			int vCount = dis.readInt();
			for (int i = 0; i < vCount; i++)
			{
				float[] element = new float[Vertex.elementCount];
				for (int j = 0; j < element.length; j++)
					element[j] = dis.readFloat();
				
				Vertex vx = new Vertex();
				vx.setElements(element);
				
				v.add(vx);
			}
			
			int inCount = dis.readInt();
			for (int i = 0; i < inCount; i++)
				in.getIndex().add(Integer.valueOf(dis.readInt()));
			
			Mesh m = new Mesh(v);
			m.setIndices(in);
			return m;
		}	
		catch (IOException e)
		{
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		finally
		{
			if (dis != null || fis != null)
			try
			{
				dis.close();
				fis.close();
			} 
			catch (IOException e)
			{
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void exportToJTitan(Mesh m, String filename)
	{
		//Check for existing file, create new file
		File f;
		try
		{
			f = new File(filename);
			if (!f.createNewFile())
				return;
	   	} 
		catch (IOException e) 
		{
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	        return;
	   	}
		
		//Write mesh data to file
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		try
		{
			fos = new FileOutputStream(filename);
			dos = new DataOutputStream(fos);
	
			dos.writeInt(m.getVertices().size());
			for (int i = 0; i < m.getVertices().size(); i++)
				for (float v : m.getVertices().get(i).getElements())
					dos.writeFloat(v);
			
			dos.writeInt(m.getIndices().getIndex().size());
			for (int i = 0; i < m.getIndices().getIndex().size(); i++)
				for (int v : m.getIndices().getIndex())
					dos.writeInt(v);
			
			dos.flush();
		} 
		catch (IOException e)
		{
			System.out.println("An error occurred.");
			e.printStackTrace();
			return;
		}
		finally
		{
			if (dos != null || fos != null)
				try
				{
					dos.close();
					fos.close();
				} catch (IOException e)
				{
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
		}
	}
	
	private static Mesh importFromOBJ(String filename)
	{

		final List<Data> ld = new ArrayList<Data>();

		Mesh m;

		// File f = new File(filename);
		// System.out.println(f + (f.exists()? " is found " : " is missing "));

		try
		{
			FileInputStream in = new FileInputStream(filename);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			String line;
			while ((line = reader.readLine()) != null)
			{
				ld.add(new Data(line));
			}

			reader.close();
			in.close();

		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final List<Vertex> lv = new ArrayList<Vertex>();
		final List<Tupple> lt = new ArrayList<Tupple>();
		final List<Position> lp = new ArrayList<Position>();
		final List<Position> ln = new ArrayList<Position>();
		final List<Position> ltx = new ArrayList<Position>();
		final Indices indices = new Indices();

		for (Data d : ld)
		{
			if (d.type.equals("v"))
			{
				Position p = new Position();
				if (d.value[0] == "" || d.value[1] == "")
					System.out.println("help");
				p.x = Float.parseFloat(d.value[0]);
				p.y = Float.parseFloat(d.value[1]);
				p.z = (d.value.length > 2) ? Float.parseFloat(d.value[2]) : 0.0f;
				p.w = (d.value.length > 3) ? Float.parseFloat(d.value[3]) : 1.0f;
				lp.add(p);
			}
			if (d.type.equals("vn"))
			{
				Position p = new Position();
				p.x = Float.parseFloat(d.value[0]);
				p.y = Float.parseFloat(d.value[1]);
				p.z = (d.value.length > 2) ? Float.parseFloat(d.value[2]) : 0.0f;
				ln.add(p);
			}
			if (d.type.equals("vt"))
			{
				Position p = new Position();
				p.x = Float.parseFloat(d.value[0]);
				p.y = 1f - Float.parseFloat(d.value[1]);
				//p.z = (d.value.length > 2) ? Float.parseFloat(d.value[2]) : 0.0f;
				ltx.add(p);
			}
			if (d.type.equals("f"))
			{
				String[] data = d.value;
				for (int i = 0; i < data.length; i++)
				{
					data[i] = data[i].replaceAll("//", "/1/");
				}

				if (data.length == 4)
				data = new String[] { data[0], data[1], data[2], data[0], data[2], data[3] };

				for (String s : data)
				{
					String[] sub = s.split("/");
					int[] value = new int[3];
					for (int i = 0; i < 3; i++) value[i] = Integer.parseInt(sub[i]) - 1;
					Tupple t = new Tupple(value[0], value[1], value[2]);
					t.i = -1;
					lt.add(t);

				}
				
			}
		}
		
		if (ltx.isEmpty())
			ltx.add(new Position(0.0f, 0.0f, 0.0f, 0.0f));

		for (int i = 0; i < lt.size(); i++)
		{
			Tupple t = lt.get(i);

			for (Tupple j : lt)
			{
				if (j.v == t.v)
					if (j.n == t.n)
						if (j.t == t.t)
							t.i = j.i;
			}
			
			if (t.i == -1)
			{
				t.i = lv.size();
				lv.add(new Vertex()
				{
					{
						Position p = lp.get(t.v);
						Position n = ln.get(t.n);
						Position tx = ltx.get(t.t);
						
						this.setXYZW(p.x, p.y, p.z, 1.0f);
						this.setNOP(n.x, n.y, n.z);
						this.setSTR(tx.x, tx.y, 0f);

						Position c = new Position();
						c.x = 1f;
						c.y = 1f;
						c.z = 1f;
						this.setRGBA(c.x, c.y, c.z, 1f);
					}
				});
			}

			indices.getIndex().add(t.i);
		}

		m = new Mesh(lv);
		m.setIndices(indices);
		//m.calculateTangents();
		return m;
	}

	static private class Data
	{
		String type;
		String[] value;

		Data(String input)
		{
			String[] t = input.split(" ");
			value = new String[t.length - 1];
			for (int i = 0; i < t.length; i++)
			{
				if (i == 0)
				{
					type = t[i];
				} else
				{
					value[i - 1] = t[i];
				}
			}
		}
	}

	static private class Tupple
	{
		int v;
		int n;
		int t;
		int i;

		Tupple(int v, int t, int n)
		{
			this.v = v;
			this.n = n;
			this.t = t;
			i = -1;
		}
	}
}
