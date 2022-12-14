package sliedregt.martijn.jtitan.api.graphics.opengl;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.awt.Dimension;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import sliedregt.martijn.jtitan.config.Configuration;
import sliedregt.martijn.jtitan.datatype.RenderBatch;
import sliedregt.martijn.jtitan.datatype.primal.Color;
import sliedregt.martijn.jtitan.datatype.primal.Material;
import sliedregt.martijn.jtitan.datatype.primal.Position;
import sliedregt.martijn.jtitan.datatype.primal.Uniform;
import sliedregt.martijn.jtitan.datatype.resource.FrameBuffer;
import sliedregt.martijn.jtitan.datatype.resource.Model;
import sliedregt.martijn.jtitan.datatype.resource.ShaderProgram;
import sliedregt.martijn.jtitan.datatype.resource.Texture;
import sliedregt.martijn.jtitan.datatype.scene.Camera;
import sliedregt.martijn.jtitan.datatype.scene.Fog;
import sliedregt.martijn.jtitan.datatype.scene.Light;
import sliedregt.martijn.jtitan.datatype.scene.Renderable;

public class GL46 extends OpenGL
{

	private Position p;

	public GL46()
	{
		super(46L);
	}

	@Override
	public boolean terminate()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void swap()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void clear(Color c)
	{
		// TODO Auto-generated method stub
		// Set the clear color
		glClearColor(c.r, c.g, c.b, c.a);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

	}

	@Override
	public void position(Renderable r)
	{
		Position n = r.getPosition();
		this.p.set(n.x, n.y, n.z, n.w);
	}

	@Override
	public boolean initialize(Configuration c)
	{
		boolean r = super.initialize(c);
		this.p = new Position();
		// glfwWindowHint(GLFW_SAMPLES, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, c.isShowDebugFrame() ? 1 : 0);
		glfwWindowHint(GLFW.GLFW_SAMPLES, c.getDisplaySettings().getAntiAliasingSamples());
		
		return r;
	}

	private void setShaderProgram(int apiID, Camera c)
	{
		GL20.glUseProgram(apiID);
		float[] p = new float[16];
		float[] v = new float[16];
		float[] y = new float[16];
		c.getProjectionMatrix().get(p);
		c.getViewMatrix().get(v);
		c.getYMatrix().get(y);
		setProjectionViewMatrices(apiID, p, v, y);
	}

	private void passUniforms(ShaderProgram s)
	{
		if (s.getUniform().isEmpty()) return;
		for (Uniform u : s.getUniform())
		{
			switch(u.getType())
			{
				case Uniform.TYPE_FLOAT:
					GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), u.getName()), ((float) u.getValue()));
					break;
					
				case Uniform.TYPE_INT:
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), u.getName()), (int) u.getValue());
					break;
					
				case Uniform.TYPE_VEC2:
					GL20.glUniform2f(GL20.glGetUniformLocation(s.getApiID(), u.getName()), ((float[]) u.getValue())[0], ((float[]) u.getValue())[1]);
					break;	
					
				case Uniform.TYPE_VEC3:
					GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), u.getName()), ((float[]) u.getValue())[0], ((float[]) u.getValue())[1], ((float[]) u.getValue())[2]);
					break;
			}
		}
	}
	
	private void setProjectionViewMatrices(int apiID, float[] projection, float[] view, float[] y)
	{
		GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(apiID, "projectionMatrix"), false, projection);
		GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(apiID, "viewMatrix"), false, view);
		GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(apiID, "yMatrix"), false, y);
	}

	
	@Override
	public void draw(List<Renderable> rl, Camera c, List<Light> l, Fog fog, Dimension screenSize)
	{
		GL11.glViewport(0, 0, c.getDimension().width, c.getDimension().height);
		Model loadedM = null;
		ShaderProgram loadedS = null;
		Model m = rl.get(0).getModel();
		ShaderProgram s = rl.get(0).getModel().getShaderProgram();
		
		setShaderProgram(s.getApiID(), c);
		passUniforms(s);
		loadedS = s;
		for (Renderable r : rl)
		{
			m = r.getModel();
			s = m.getShaderProgram();
			if (loadedS != s || loadedM != m)
			{		
				setShaderProgram(s.getApiID(), c);
				passUniforms(s);
				loadedS = s;
				loadedM = null;
				
				
				//fog
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "fog.enabled"), fog.isEnabled() ? 1 : 0);
				GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "fog.color"), fog.getColor().r, fog.getColor().g, fog.getColor().b);
				GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "fog.start"), fog.getStart());
				GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "fog.end"), fog.getEnd());
				GL20.glUniform2f(GL20.glGetUniformLocation(s.getApiID(), "screenSize"), (float) screenSize.width, (float) screenSize.height);
				if (fog.getFogTexture() != null)
				{
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "fog.useTexture"), 1);
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "fog_sampler"), 5);
					// Activate normal texture unit
					GL13.glActiveTexture(GL13.GL_TEXTURE5);
					// Bind the texture
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, fog.getFogTexture().getApiID());
				} else
				{
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "fog.useTexture"), 0);
				}
				
				//Camera
				GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "camera.position"), c.getPosition().x, c.getPosition().y,
						c.getPosition().z);
				GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "camera.angle"), c.getRotation().getX(), c.getRotation().getY(),
						c.getRotation().getZ());

				//Lights
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "lightCount"), l.size());
				for (int j = 0; j < l.size(); j++)
				{
					GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].position"), l.get(j).getPosition().x, l.get(j).getPosition().y, l.get(j).getPosition().z);
					GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].color"), l.get(j).getColor().r, l.get(j).getColor().g, l.get(j).getColor().b);
					GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].direction"), l.get(j).getDirection().x, l.get(j).getDirection().y, l.get(j).getDirection().z);
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].type"), l.get(j).getType());
					GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].power"), l.get(j).getPower());
					GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].range"), l.get(j).getRange());
					GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].cutOffRange"), l.get(j).getCutOffRange());
					GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].cutOffAngle"), (float) Math.cos(Math.toRadians(l.get(j).getCutOffAngle())));
					GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].cutOffAngleOuter"), (float) Math.cos(Math.toRadians(l.get(j).getCutOffAngleOuter())));
				}
			}

			float[] pmf = new float[16];

			new Matrix4f()
			{
				{
					identity().translate(r.getPosition().x, r.getPosition().y, r.getPosition().z)
							.rotateY((float) Math.toRadians(-r.getRotation().getY()))
							.rotateX((float) Math.toRadians(-r.getRotation().getX()))
							.rotateZ((float) Math.toRadians(-r.getRotation().getZ()))
							.scale(r.getScale().x, r.getScale().y, r.getScale().z);
				}
			}.get(pmf);
			GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(s.getApiID(), "modelMatrix"), false, pmf);

			
			if (loadedM != m)
			{
				loadedM = m;
				//Pass materials
				Material mat = m.getMaterial();
				if (mat != null)
				{
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasMaterial"), 1);

					Color ca = mat.getAmbient();
					Color cd = mat.getDiffuse();
					Color cs = mat.getSpecular();

					GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "material.ambient"), ca.r, ca.g, ca.b);
					GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "material.diffuse"), cd.r, cd.g, cd.b);
					GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "material.specular"), cs.r, cs.g, cs.b);
					GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "material.shininess"), mat.getShininess());
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasTexture"), m.getTexture() != null ? m.getTexture().getType() == Texture.TYPE_TEXTURE_2D ? 2 : 3 : 0);
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasNormalMap"), m.getNormalMap() != null ? 1 : 0);
				} else
				{
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasMaterial"), 0);
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasTexture"), 0);
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasNormalMap"), 0);
				}

				if (m.getTexture() != null)
				{
					if (m.getTexture().getType() == Texture.TYPE_TEXTURE_2D)
					{
						// Activate first texture unit
						GL13.glActiveTexture(GL13.GL_TEXTURE0);
						GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "texture_sampler2D"), 0);
					}
						
					if (m.getTexture().getType() == Texture.TYPE_TEXTURE_3D)
					{
						// Activate first texture unit
						GL13.glActiveTexture(GL13.GL_TEXTURE3);
						GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "texture_sampler3D"), 3);
					}
					// Bind the texture
					GL11.glBindTexture(m.getTexture().getType(), m.getTexture().getApiID());
				}
			
				if (m.getNormalMap() != null)
				{
					if (m.getNormalMap().getType() == Texture.TYPE_TEXTURE_2D)
					{
						GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "normalmap_sampler"), 1);
						// Activate normal texture unit
						GL13.glActiveTexture(GL13.GL_TEXTURE1);
						// Bind the texture
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, m.getNormalMap().getApiID());
					} else
					{
						GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "normalmap_sampler3D"), 6);
						// Activate normal texture unit
						GL13.glActiveTexture(GL13.GL_TEXTURE6);
						// Bind the texture
						GL11.glBindTexture(m.getNormalMap().getType(), m.getNormalMap().getApiID());
					}
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasNormalMap"), 1);
				} else
				{
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasNormalMap"), 0);
				}


				if (m.getReflectionTexture() != null)
				{
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "reflectionmap_sampler"), 2);
					// Activate normal texture unit
					GL13.glActiveTexture(GL13.GL_TEXTURE2);
					// Bind the texture
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, m.getReflectionTexture().getApiID());
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasReflectionMap"), 1);
				} else
				{
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasReflectionMap"), 0);
				}
				
				if (m.getHeightTexture() != null)
				{
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "heightmap_sampler"), 4);
					// Activate normal texture unit
					GL13.glActiveTexture(GL13.GL_TEXTURE4);
					// Bind the texture
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, m.getHeightTexture().getApiID());
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasHeightMap"), 1);
				} else
				{
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasHeightMap"), 0);
				}

				// Bind to the VAO
				glBindVertexArray(m.getMesh().getVaoId());
				glEnableVertexAttribArray(0);
				glEnableVertexAttribArray(1);
				glEnableVertexAttribArray(2);
				glEnableVertexAttribArray(3);
				glEnableVertexAttribArray(4);
				glEnableVertexAttribArray(5);
				
				// Bind to the index VBO that has all the information about the order of the
				// vertices
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m.getMesh().getVboIId());
			}
			

			r.doAtPreRender();

			// Draw the vertices
			GL11.glDrawElements(GL11.GL_TRIANGLES, m.getMesh().getIndices().getIndex().size(), GL11.GL_UNSIGNED_INT, 0);

			r.doAtPostRender();
		}
	}
	
	/*
	 @Override
	public void draw(List<Renderable> rl, Camera c, List<Light> l, Fog fog, Dimension screenSize)
	{
		Model m = rl.get(0).getModel();
		ShaderProgram s = rl.get(0).getModel().getShaderProgram();
		setShaderProgram(s.getApiID(), c);
		passUniforms(s);
		
		for (Renderable r : rl)
		{
			m = r.getModel();
			
			if (m.getShaderProgram() != s)
			{
				s = m.getShaderProgram();
				setShaderProgram(s.getApiID(), c);
				passUniforms(s);
			}

			//setShaderProgram(s.getApiID(), c);

			float[] pmf = new float[16];

			new Matrix4f()
			{
				{
					identity().translate(r.getPosition().x, r.getPosition().y, r.getPosition().z)
							.rotateX((float) Math.toRadians(-r.getRotation().x))
							.rotateY((float) Math.toRadians(-r.getRotation().y))
							.rotateZ((float) Math.toRadians(-r.getRotation().z))
							.scale(r.getScale().x, r.getScale().y, r.getScale().z);
				}
			}.get(pmf);
			GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(s.getApiID(), "modelMatrix"), false, pmf);

			new Matrix4f(c.getViewMatrix()).mul(new Matrix4f()
			{
				{
					identity().translate(r.getPosition().x, r.getPosition().y, r.getPosition().z)
							.rotateX((float) Math.toRadians(-r.getRotation().x))
							.rotateY((float) Math.toRadians(-r.getRotation().y))
							.rotateZ((float) Math.toRadians(-r.getRotation().z))
							.scale(r.getScale().x, r.getScale().y, r.getScale().z);
				}
			}).get(pmf);
			GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(s.getApiID(), "modelViewMatrix"), false, pmf);

			//Pass materials
			Material mat = m.getMaterial();
			if (mat != null)
			{
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasMaterial"), 1);

				Color ca = mat.getAmbient();
				Color cd = mat.getDiffuse();
				Color cs = mat.getSpecular();

				GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "material.ambient"), ca.r, ca.g, ca.b);
				GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "material.diffuse"), cd.r, cd.g, cd.b);
				GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "material.specular"), cs.r, cs.g, cs.b);
				GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "material.shininess"), mat.getShininess());
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasTexture"), m.getTexture() != null ? m.getTexture().getType() == Texture.TYPE_TEXTURE_2D ? 2 : 3 : 0);
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasNormalMap"), m.getNormalMap() != null ? 1 : 0);
			} else
			{
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasMaterial"), 0);
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasTexture"), 0);
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasNormalMap"), 0);
			}

			GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "camera_pos"), c.getPosition().x, c.getPosition().y,
					c.getPosition().z);

			//Lights
			GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "lightCount"), l.size());
			for (int j = 0; j < l.size(); j++)
			{
				GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].position"), l.get(j).getPosition().x, l.get(j).getPosition().y, l.get(j).getPosition().z);
				GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].color"), l.get(j).getColor().r, l.get(j).getColor().g, l.get(j).getColor().b);
				GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].direction"), l.get(j).getDirection().x, l.get(j).getDirection().y, l.get(j).getDirection().z);
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].type"), l.get(j).getType());
				GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].power"), l.get(j).getPower());
				GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].range"), l.get(j).getRange());
				GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].cutOffRange"), l.get(j).getCutOffRange());
				GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].cutOffAngle"), (float) Math.cos(Math.toRadians(l.get(j).getCutOffAngle())));
				GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "light["+j+"].cutOffAngleOuter"), (float) Math.cos(Math.toRadians(l.get(j).getCutOffAngleOuter())));
				
			}


			

			if (m.getTexture() != null)
			{
				if (m.getTexture().getType() == Texture.TYPE_TEXTURE_2D)
				{
					// Activate first texture unit
					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "texture_sampler2D"), 0);
				}
					
				if (m.getTexture().getType() == Texture.TYPE_TEXTURE_3D)
				{
					// Activate first texture unit
					GL13.glActiveTexture(GL13.GL_TEXTURE3);
					GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "texture_sampler3D"), 3);
				}
				// Bind the texture
				GL11.glBindTexture(m.getTexture().getType(), m.getTexture().getApiID());
			}
		
			if (m.getNormalMap() != null)
			{
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "normalmap_sampler"), 1);
				// Activate normal texture unit
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				// Bind the texture
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, m.getNormalMap().getApiID());
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasNormalMap"), 1);
			} else
			{
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasNormalMap"), 0);
			}


			if (m.getReflectionTexture() != null)
			{
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "reflectionmap_sampler"), 2);
				// Activate normal texture unit
				GL13.glActiveTexture(GL13.GL_TEXTURE2);
				// Bind the texture
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, m.getReflectionTexture().getApiID());
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasReflectionMap"), 1);
			} else
			{
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasReflectionMap"), 0);
			}
			
			if (m.getHeightTexture() != null)
			{
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "heightmap_sampler"), 4);
				// Activate normal texture unit
				GL13.glActiveTexture(GL13.GL_TEXTURE4);
				// Bind the texture
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, m.getHeightTexture().getApiID());
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasHeightMap"), 1);
			} else
			{
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "material.hasHeightMap"), 0);
			}
			
			//fog
			GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "fog.enabled"), fog.isEnabled() ? 1 : 0);
			GL20.glUniform3f(GL20.glGetUniformLocation(s.getApiID(), "fog.color"), fog.getColor().r, fog.getColor().g, fog.getColor().b);
			GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "fog.start"), fog.getStart());
			GL20.glUniform1f(GL20.glGetUniformLocation(s.getApiID(), "fog.end"), fog.getEnd());
			GL20.glUniform2f(GL20.glGetUniformLocation(s.getApiID(), "screenSize"), (float) screenSize.width, (float) screenSize.height);
			if (fog.getFogTexture() != null)
			{
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "fog.useTexture"), 1);
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "fog_sampler"), 5);
				// Activate normal texture unit
				GL13.glActiveTexture(GL13.GL_TEXTURE5);
				// Bind the texture
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, fog.getFogTexture().getApiID());
			} else
			{
				GL20.glUniform1i(GL20.glGetUniformLocation(s.getApiID(), "fog.useTexture"), 0);
			}

			r.doAtPreRender();
			// Bind to the VAO
			glBindVertexArray(r.getModel().getMesh().getVaoId());
			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glEnableVertexAttribArray(2);
			glEnableVertexAttribArray(3);
			glEnableVertexAttribArray(4);
			glEnableVertexAttribArray(5);
			
			// Bind to the index VBO that has all the information about the order of the
			// vertices
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, r.getModel().getMesh().getVboIId());

			// Draw the vertices
			GL11.glDrawElements(GL11.GL_TRIANGLES, r.getModel().getMesh().getIndices().getIndex().size(),
					GL11.GL_UNSIGNED_INT, 0);

			// Put everything back to default (deselect)
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			 GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

			// Restore state
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glDisableVertexAttribArray(2);
			glDisableVertexAttribArray(3);
			glDisableVertexAttribArray(4);
			glDisableVertexAttribArray(5);
			glBindVertexArray(0);

	
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL11.glBindTexture(GL14.GL_TEXTURE_3D, 0);

			r.doAtPostRender();
		}
	}
	*/
	
	@Override
	public void renderBatch(RenderBatch r, Dimension screenSize)
	{
	
		if (r.getFbo() != null)
			bindFrameBuffer(r.getFbo());
		
		clear(new Color(0.0f, 0.0f, 0.0f, 1.0f));
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		r.getCamera().update();
		render(r.getRenderable(), r.getCamera(), r.getLight(), r.getFog(), screenSize);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	@Override
	public void bindFrameBuffer(FrameBuffer fbo)
	{
		// TODO Auto-generated method stub
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo.getApiID());

	}
}
