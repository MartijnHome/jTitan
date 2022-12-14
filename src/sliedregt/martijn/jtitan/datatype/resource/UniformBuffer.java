package sliedregt.martijn.jtitan.datatype.resource;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import sliedregt.martijn.jtitan.config.Configuration;

public class UniformBuffer extends Resource
{

	public UniformBuffer()
	{
	
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private int apiID;



	@Override
	protected boolean load(Configuration c)
	{
		// Create the shader and set the source
		apiID = GL30.glGenFramebuffers();
		
        // Create the depth map texture
        //depthMap = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT);

        // Attach the the depth map texture to the FBO
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, apiID);
		if (texture != null)
		{
			if (!texture.isLoaded) texture.load(c);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getApiID());
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture.getApiID(), 0); 
		       
		}
        //glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getId(), 0);
        
        // Set only depth
        if (this.type == TYPE_DEPTH_BUFFER)
        {
        	 GL11.glDrawBuffer(GL11.GL_NONE);
             GL11.glReadBuffer(GL11.GL_NONE);
        } else
        {
        	if (this.type == TYPE_REFLECTION_BUFFER)
        	{
        		renderBuffer = GL30.glGenRenderbuffers();
        		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBuffer); 
        		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT, texture.getDimension().width, texture.getDimension().height);  
        		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, renderBuffer);
        		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        	}
        }

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("AAAI");
        }
        
        // Unbind
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		return true;
	}

	public int getApiID()
	{
		return apiID;
	}

	@Override
	protected boolean unload()
	{
		if (texture != null)
			if (texture.isLoaded) texture.unload();
		
		if (renderBuffer > -1)
			GL30.glDeleteRenderbuffers(renderBuffer);
		
		GL30.glDeleteFramebuffers(apiID);
		return false;
	}

	public int getType()
	{
		return type;
	}

	public Texture getTexture()
	{
		return texture;
	}

	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}
}
