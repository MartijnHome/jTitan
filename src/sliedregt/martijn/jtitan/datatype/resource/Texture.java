package sliedregt.martijn.jtitan.datatype.resource;

import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import de.matthiasmann.twl.utils.PNGDecoder;
import sliedregt.martijn.jtitan.config.Configuration;

public class Texture extends Resource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int TYPE_TEXTURE_1D = GL11.GL_TEXTURE_1D,
							TYPE_TEXTURE_2D = GL_TEXTURE_2D,
							TYPE_TEXTURE_3D = GL30.GL_TEXTURE_2D_ARRAY;
							//TYPE_TEXTURE_3D = GL14.GL_TEXTURE_3D;
	
	public static final int RENDER_TEXTURE_REPEAT = 1,
							RENDER_TEXTURE_CLAMP_EDGE = 2;
	
	
	private int type;
	private int apiID;
	private int renderMode;
	private Dimension dimension;
	private BufferedImage[] image;

	public Texture(int type, BufferedImage[] image)
	{
		this.file = null;
		this.type = type;
		this.setRenderMode(RENDER_TEXTURE_CLAMP_EDGE);
		this.dimension = new Dimension(image[0].getWidth(), image[0].getHeight());
		this.image = image;
	}
	
	public Texture(int type, BufferedImage image)
	{
		this(type, new BufferedImage[] {image});
	}
	
	public Texture(int type)
	{
		this.file = null;
		this.type = type;
		this.setRenderMode(RENDER_TEXTURE_CLAMP_EDGE);
		this.dimension = new Dimension(0,0);
		setImage(null);
	}
	
	public Texture(String f, int type)
	{
		this(new String[] {f}, type);
	}
	
	public Texture(String[] f, int type)
	{
		this.file = f;
		this.type = type;
		this.setRenderMode(RENDER_TEXTURE_CLAMP_EDGE);
		this.dimension = new Dimension(0,0);
		setImage(null);
	}

	@Override
	protected boolean load(Configuration c)
	{
		if (this.file == null)
		{
			if (image == null)
			{
				if (c.isShowDebugFrame())
				{
					c.getDebugFrame().writeLogText("Creating blank texture");
				}
				return createBlankTexture();
			}
			if (c.isShowDebugFrame())
			{
				c.getDebugFrame().writeLogText("Creating texture from buffered image");
			}
			return createTextureFromImage();
		} else
		{
			if (c.isShowDebugFrame())
			{
				c.getDebugFrame().writeLogText("Loading texture from file");
			}
			return loadFromFile();
		}
		
	}

	private boolean createTextureFromImage()
	{
		//Create texture from bufferedimage
		int width = dimension.width;
		int height = dimension.height;
		int layers = image.length;
		int internalFormat = image[0].getColorModel().hasAlpha() ? GL_RGBA : GL11.GL_RGB;
		
		ByteBuffer buf = ByteBuffer.allocateDirect(4 * width * height * layers);
		
		for (int index = 0; index < layers; index++)
		{
			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++)
				{
					int color = image[index].getRGB(x, y);
					
					if (image[index].getColorModel().hasAlpha())
					{
						byte alpha = (byte) ((color >> 24) & 0x000000FF);
						buf.put(alpha);
					}
					
					byte red = (byte) ((color >> 16) & 0x000000FF);
					byte green = (byte) ((color >>8 ) & 0x000000FF);
					byte blue = (byte) ((color) & 0x000000FF);
					
					buf.put(red);
					buf.put(green);
					buf.put(blue);	
				}
		}

		buf.flip();

		// Create a new OpenGL texture
		apiID = glGenTextures();

		// Bind the texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		glBindTexture(type, apiID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		if (type == TYPE_TEXTURE_2D)
			glTexImage2D(type, 0, internalFormat, width, height, 0, internalFormat, GL_UNSIGNED_BYTE, buf);
		
		if (type == TYPE_TEXTURE_3D)
			GL14.glTexImage3D(type, 0, internalFormat, width, height, layers, 0, internalFormat, GL_UNSIGNED_BYTE, buf);
		
		glTexParameteri(type, GL11.GL_TEXTURE_WRAP_S, (renderMode == RENDER_TEXTURE_CLAMP_EDGE) ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
		
		if (type == TYPE_TEXTURE_2D || type == TYPE_TEXTURE_3D)
			glTexParameteri(type, GL11.GL_TEXTURE_WRAP_T, (renderMode == RENDER_TEXTURE_CLAMP_EDGE) ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
		
		if (type == TYPE_TEXTURE_3D)
			glTexParameteri(type, GL30.GL_TEXTURE_MAX_LEVEL, file.length);
			//glTexParameteri(type, GL14.GL_TEXTURE_WRAP_R, (renderMode == RENDER_TEXTURE_CLAMP_EDGE) ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
		
		//glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		//glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		
		
		glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		glTexParameterf(type, GL12.GL_TEXTURE_MIN_LOD, -1000f);
		glTexParameterf(type, GL12.GL_TEXTURE_MAX_LOD, 1000f);
		glTexParameterf(type, GL14.GL_TEXTURE_LOD_BIAS, -0.8f);
		GL30.glGenerateMipmap(type);
		
		glBindTexture(type, 0);
		isLoaded = true;
		return true;	
	}
	
	private boolean createBlankTexture()
	{
		// Create a new OpenGL texture
		apiID = glGenTextures();

		// Bind the texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		glBindTexture(type, apiID);

		glTexImage2D(type, 0, GL11.GL_RGB, dimension.width, dimension.height, 0, GL11.GL_RGB,
				GL_UNSIGNED_BYTE, 0);
		
		glTexParameteri(type, GL11.GL_TEXTURE_WRAP_S, (renderMode == RENDER_TEXTURE_CLAMP_EDGE) ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
		glTexParameteri(type, GL11.GL_TEXTURE_WRAP_T, (renderMode == RENDER_TEXTURE_CLAMP_EDGE) ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
		glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		glBindTexture(type, 0);
		isLoaded = true;
		return true;
	}
	
	private boolean loadFromFile()
	{
		// TODO Auto-generated method stub
		InputStream in;
		try
		{
			in = new FileInputStream(file[0]);
			PNGDecoder decoder = new PNGDecoder(in);
			
			int width = decoder.getWidth();
			int height = decoder.getHeight();
			int layers = file.length;

			ByteBuffer buf = ByteBuffer.allocateDirect(4 * width * height * layers);
			
			for (int index = 0; index < file.length;)
			{
				ByteBuffer b = ByteBuffer.allocateDirect(4 * width * height);
				decoder.decode(b, width * 4, PNGDecoder.Format.RGBA);
				
				b.rewind();
				for (int j = 0; j < b.capacity(); j++)
					buf.put(b.get());
				
				in.close();
				
				index++;
				if (index < file.length)
				{
					in = new FileInputStream(file[index]);
					decoder = new PNGDecoder(in);
				}
			}

			buf.flip();

			// Create a new OpenGL texture
			apiID = glGenTextures();

			// Bind the texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			glBindTexture(type, apiID);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			
			if (type == TYPE_TEXTURE_2D)
				glTexImage2D(type, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
			
			if (type == TYPE_TEXTURE_3D)
				GL14.glTexImage3D(type, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), layers, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
			
			glTexParameteri(type, GL11.GL_TEXTURE_WRAP_S, (renderMode == RENDER_TEXTURE_CLAMP_EDGE) ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
			
			if (type == TYPE_TEXTURE_2D || type == TYPE_TEXTURE_3D)
				glTexParameteri(type, GL11.GL_TEXTURE_WRAP_T, (renderMode == RENDER_TEXTURE_CLAMP_EDGE) ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
			
			if (type == TYPE_TEXTURE_3D)
				glTexParameteri(type, GL30.GL_TEXTURE_MAX_LEVEL, file.length);
				//glTexParameteri(type, GL14.GL_TEXTURE_WRAP_R, (renderMode == RENDER_TEXTURE_CLAMP_EDGE) ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT);
			
			//glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			//glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			
			
			glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
			glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_NEAREST_MIPMAP_LINEAR);
			glTexParameterf(type, GL12.GL_TEXTURE_MIN_LOD, -1000f);
			glTexParameterf(type, GL12.GL_TEXTURE_MAX_LOD, 1000f);
			glTexParameterf(type, GL14.GL_TEXTURE_LOD_BIAS, -0.8f);
			GL30.glGenerateMipmap(type);
			
			glBindTexture(type, 0);
	
			dimension.setSize(decoder.getWidth(), decoder.getHeight());
			isLoaded = true;
			return true;
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected boolean unload()
	{
		// TODO Auto-generated method stub\
		GL11.glDeleteTextures(apiID);
		return false;
	}

	public int getType()
	{
		return type;
	}

	public int getApiID()
	{
		return apiID;
	}

	public int getRenderMode()
	{
		return renderMode;
	}

	public void setRenderMode(int renderMode)
	{
		this.renderMode = renderMode;
	}

	public Dimension getDimension()
	{
		return dimension;
	}

	public void setDimension(Dimension dimension)
	{
		this.dimension = dimension;
	}

	public BufferedImage[] getImage()
	{
		return image;
	}

	public void setImage(BufferedImage[] image)
	{
		this.image = image;
	}

}
