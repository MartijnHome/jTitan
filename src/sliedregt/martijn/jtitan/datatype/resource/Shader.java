package sliedregt.martijn.jtitan.datatype.resource;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import sliedregt.martijn.jtitan.config.Configuration;

public class Shader extends Resource
{

	public Shader(String f, int type)
	{
		super();
		this.file = new String[] {f};
		this.type = type;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int type;
	private int apiID;

	public final static int TYPE_GL_VERTEX_SHADER = GL_VERTEX_SHADER;
	public final static int TYPE_GL_FRAGMENT_SHADER = GL_FRAGMENT_SHADER;
	public final static int TYPE_GL_GEOMETRY_SHADER = GL32.GL_GEOMETRY_SHADER;

	@Override
	protected boolean load(Configuration c)
	{
		if (c.isShowDebugFrame())
		{
			c.getDebugFrame().writeLogText("Loading shader");
		}
		
		// Load the source
		String source;
		try
		{
			source = readFileAsString(file[0], c);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		// Create the shader and set the source
		apiID = glCreateShader(type);
		glShaderSource(apiID, source);

		// Compile the shader
		glCompileShader(apiID);

		// Check for errors
		if (glGetShaderi(apiID, GL_COMPILE_STATUS) == GL11.GL_FALSE)
		{
			String shader = (type == TYPE_GL_VERTEX_SHADER) ? 	"vertex shader" :
							(type == TYPE_GL_FRAGMENT_SHADER) ? "fragment shader" :
							(type == TYPE_GL_GEOMETRY_SHADER) ? "geometry shader" :
																"unknown shader";
			String error = "Error creating " + shader + "\n" + file[0] +"\n";
			throw new RuntimeException(error + glGetShaderInfoLog(apiID, glGetShaderi(apiID, GL_INFO_LOG_LENGTH)));
		}
		// Attach the shader
		// glAttachShader(programID, vertexShaderID);
		return true;
	}

	public int getApiID()
	{
		return apiID;
	}

	@Override
	protected boolean unload()
	{

		// glDetachShader(programID, vertexShaderID);
		// glDetachShader(programID, fragmentShaderID);

		// Delete the shaders
		// glDeleteShader(vertexShaderID);
		glDeleteShader(apiID);

		// Delete the program
		// glDeleteProgram(programID);
		return false;
	}

	private String readFileAsString(String filename, Configuration c) throws Exception
	{
		StringBuilder source = new StringBuilder();

		FileInputStream in = new FileInputStream(filename);

		File f = new File(filename);
		
		if (c.isShowDebugFrame())
		{
			c.getDebugFrame().writeLogText(f + (f.exists() ? " is found " : " is missing "));
		}

		Exception exception = null;

		BufferedReader reader;
		try
		{
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			Exception innerExc = null;
			try
			{
				String line;
				while ((line = reader.readLine()) != null)
					source.append(line).append('\n');
			} catch (Exception exc)
			{
				exception = exc;
			} finally
			{
				try
				{
					reader.close();
				} catch (Exception exc)
				{
					if (innerExc == null)
						innerExc = exc;
					else
						exc.printStackTrace();
				}
			}

			if (innerExc != null)
				throw innerExc;
		} catch (Exception exc)
		{
			exception = exc;
		} finally
		{
			try
			{
				in.close();
			} catch (Exception exc)
			{
				if (exception == null)
					exception = exc;
				else
					exc.printStackTrace();
			}

			if (exception != null)
				throw exception;
		}

		return source.toString();
	}

	public int getType()
	{
		return type;
	}
}
