package sliedregt.martijn.jtitan.datatype.primal;

public class Vertex
{

	private float[] xyzw = new float[]
	{ 0f, 0f, 0f, 1f };
	private float[] rgba = new float[]
	{ 1f, 1f, 1f, 1f };
	private float[] nop = new float[]
	{ 0f, 0f, 0f };
	private float[] str = new float[]
	{ 0f, 0f, 0f };
	private float[] tangent = new float[]
	{ 0f, 0f, 0f, 0f };
	private float[] biTangent = new float[]
	{ 0f, 0f, 0f, 0f };

	// The amount of bytes an element has
	public static final int elementBytes = 4;

	// Elements per parameter
	public static final int positionElementCount = 4;
	public static final int colorElementCount = 4;
	public static final int normalElementCount = 3;
	public static final int textureElementCount = 3;
	public static final int tangentElementCount = 4;
	public static final int biTangentElementCount = 4;
	
	// Bytes per parameter
	public static final int positionBytesCount = positionElementCount * elementBytes;
	public static final int colorByteCount = colorElementCount * elementBytes;
	public static final int normalByteCount = normalElementCount * elementBytes;
	public static final int textureByteCount = textureElementCount * elementBytes;
	public static final int tangentByteCount = tangentElementCount * elementBytes;
	public static final int biTangentByteCount = biTangentElementCount * elementBytes;
	
	// Byte offsets per parameter
	public static final int positionByteOffset = 0;
	public static final int colorByteOffset = positionByteOffset + positionBytesCount;
	public static final int normalByteOffset = colorByteOffset + colorByteCount;
	public static final int textureByteOffset = normalByteOffset + normalByteCount;
	public static final int tangentByteOffset = textureByteOffset + textureByteCount;
	public static final int biTangentByteOffset = tangentByteOffset + tangentByteCount;

	// The amount of elements that a vertex has
	public static final int elementCount = positionElementCount + colorElementCount + normalElementCount
			+ textureElementCount + tangentElementCount + biTangentElementCount;
	// The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
	public static final int stride = positionBytesCount + colorByteCount + normalByteCount + textureByteCount
			+ tangentByteCount + biTangentByteCount;

	// Setters
	public void setXYZ(float x, float y, float z)
	{
		this.setXYZW(x, y, z, xyzw[3]);
	}

	public void setRGB(float r, float g, float b)
	{
		this.setRGBA(r, g, b, rgba[3]);
	}

	public void setNOP(float n, float o, float p)
	{
		this.nop = new float[]
		{ n, o, p };
	}

	public void setSTR(float s, float t, float r)
	{
		this.str = new float[]
		{ s, t, r };
	}

	public void setXYZW(float x, float y, float z, float w)
	{
		this.xyzw = new float[]
		{ x, y, z, w };
	}

	public void setRGBA(float r, float g, float b, float a)
	{
		this.rgba = new float[]
		{ r, g, b, a };
	}

	// Getters
	public float[] getElements()
	{
		float[] out = new float[elementCount];
		int i = 0;

		// Insert XYZW elements
		out[i++] = this.xyzw[0];
		out[i++] = this.xyzw[1];
		out[i++] = this.xyzw[2];
		out[i++] = this.xyzw[3];
		// Insert RGBA elements
		out[i++] = this.rgba[0];
		out[i++] = this.rgba[1];
		out[i++] = this.rgba[2];
		out[i++] = this.rgba[3];
		// Insert NOP elements
		out[i++] = this.nop[0];
		out[i++] = this.nop[1];
		out[i++] = this.nop[2];
		// Insert ST elements
		out[i++] = this.str[0];
		out[i++] = this.str[1];
		out[i++] = this.str[2];
		// Insert tangent elements
		out[i++] = this.tangent[0];
		out[i++] = this.tangent[1];
		out[i++] = this.tangent[2];
		out[i++] = this.tangent[3];
		// Insert biTangent elements
		out[i++] = this.biTangent[0];
		out[i++] = this.biTangent[1];
		out[i++] = this.biTangent[2];
		out[i++] = this.biTangent[3];
		return out;
	}
	
	public void setElements(float[] e)
	{
		if (e.length != elementCount)
			return;
		
		int i = 0;
		setXYZW(e[i++], e[i++], e[i++], e[i++]);
		setRGBA(e[i++], e[i++], e[i++], e[i++]);
		setNOP(e[i++], e[i++], e[i++]);
		setSTR(e[i++], e[i++], e[i++]);
		setTangent(e[i++], e[i++], e[i++], e[i++]);
		setBiTangent(e[i++], e[i++], e[i++], e[i]);
	}

	public float[] getXYZW()
	{
		return new float[]
		{ this.xyzw[0], this.xyzw[1], this.xyzw[2], this.xyzw[3] };
	}

	public float[] getXYZ()
	{
		return new float[]
		{ this.xyzw[0], this.xyzw[1], this.xyzw[2] };
	}

	public float[] getRGBA()
	{
		return new float[]
		{ this.rgba[0], this.rgba[1], this.rgba[2], this.rgba[3] };
	}

	public float[] getRGB()
	{
		return new float[]
		{ this.rgba[0], this.rgba[1], this.rgba[2] };
	}

	public float[] getNOP()
	{
		return new float[]
		{ this.nop[0], this.nop[1], this.nop[2] };
	}

	public float[] getSTR()
	{
		return new float[]
		{ this.str[0], this.str[1], this.str[2] };
	}

	public float[] getTangent()
	{
		return tangent;
	}

	public void setTangent(float x, float y, float z, float w)
	{
		this.tangent = new float[]
		{ x, y, z, w };
	}

	public float[] getBiTangent()
	{
		return biTangent;
	}

	public void setBiTangent(float x, float y, float z, float w)
	{
		this.biTangent = new float[]
		{ x, y, z, w };
	}
}