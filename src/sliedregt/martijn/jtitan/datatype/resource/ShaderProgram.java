package sliedregt.martijn.jtitan.datatype.resource;

import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL20;

import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.datatype.primal.Uniform;

public class ShaderProgram extends Resource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int apiID;
	private List<Shader> shader;
	private List<Uniform> uniform;
	
	public ShaderProgram()
	{
		super();
		shader = new ArrayList<Shader>();
		uniform = new ArrayList<Uniform>();
		// TODO Auto-generated constructor stub
	}

	public List<Shader> getShader()
	{
		return shader;
	}

	public void addShader(Shader shader)
	{
		this.shader.add(shader);
	}

	public int getApiID()
	{
		return apiID;
	}

	@Override
	protected boolean load(Configuration c)
	{
		if (c.isShowDebugFrame())
		{
			c.getDebugFrame().writeLogText("Creating shader program");
		}
		apiID = glCreateProgram();

		for (Shader s : shader)
		{
			if (!s.isLoaded())
				s.initialize(c);
			glAttachShader(apiID, s.getApiID());
		}
		org.lwjgl.opengl.GL20.glBindAttribLocation(apiID, 0, "in_position");
		org.lwjgl.opengl.GL20.glBindAttribLocation(apiID, 1, "in_color");
		org.lwjgl.opengl.GL20.glBindAttribLocation(apiID, 2, "in_normal");
		org.lwjgl.opengl.GL20.glBindAttribLocation(apiID, 3, "in_texcoord");
		org.lwjgl.opengl.GL20.glBindAttribLocation(apiID, 4, "in_tangent");
		org.lwjgl.opengl.GL20.glBindAttribLocation(apiID, 5, "in_biTangent");
		glLinkProgram(apiID);
		
		////
		int isLinked = org.lwjgl.opengl.GL20.glGetProgrami(apiID, GL20.GL_LINK_STATUS);
		if (isLinked == 0)
		{
			int maxLength = org.lwjgl.opengl.GL20.glGetProgrami(apiID, GL20.GL_INFO_LOG_LENGTH);

			// The maxLength includes the NULL character

			System.out.println(org.lwjgl.opengl.GL20.glGetProgramInfoLog(apiID, maxLength));
			// The program is useless now. So delete it.
			// Provide the infolog in whatever manner you deem best.
			// Exit with failure.
			System.exit(1);
		}
		
		
		/////
		glValidateProgram(apiID);
		return true;
	}

	@Override
	protected boolean unload()
	{

		for (Shader s : shader)
		{
			if (s.isLoaded())
			{
				glDetachShader(apiID, s.getApiID());
				s.release();
			}
		}

		// Delete the program
		glDeleteProgram(apiID);
		// TODO Auto-generated method stub
		return false;
	}

	public List<Uniform> getUniform()
	{
		return uniform;
	}

}
