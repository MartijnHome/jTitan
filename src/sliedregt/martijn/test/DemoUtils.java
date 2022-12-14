package sliedregt.martijn.test;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.joml.Vector3f;

/**
 * Utility methods for most of the ray tracing demos.
 * 
 * @author Kai Burjack
 */
public class DemoUtils {

    private static final Vector3f VECTOR_MINUS_ONE = new Vector3f(-1.0f, -1.0f, -1.0f);
    private static final Vector3f VECTOR_PLUS_ONE = new Vector3f(1.0f, 1.0f, 1.0f);

    /**
     * Write the vertices (position and normal) of an axis-aligned unit box into
     * the provided {@link FloatBuffer}.
     *
     * @param fv
     *            the {@link FloatBuffer} receiving the vertex position and
     *            normal
     */
    public static void triangulateUnitBox(FloatBuffer fv) {
        triangulateBox(VECTOR_MINUS_ONE, VECTOR_PLUS_ONE, fv);
    }

    /**
     * Write the vertices (position and normal) of an axis-aligned box with the
     * given corner coordinates into the provided {@link FloatBuffer}.
     *
     * @param min
     *            the min corner
     * @param max
     *            the max corner
     * @param fv
     *            the {@link FloatBuffer} receiving the vertex position and
     *            normal
     */
    public static void triangulateBox(Vector3f min, Vector3f max, FloatBuffer fv) {
        /* Front face */
        fv.put(min.x).put(min.y).put(max.z).put(0.0f).put(0.0f).put(1.0f);
        fv.put(max.x).put(min.y).put(max.z).put(0.0f).put(0.0f).put(1.0f);
        fv.put(max.x).put(max.y).put(max.z).put(0.0f).put(0.0f).put(1.0f);
        fv.put(max.x).put(max.y).put(max.z).put(0.0f).put(0.0f).put(1.0f);
        fv.put(min.x).put(max.y).put(max.z).put(0.0f).put(0.0f).put(1.0f);
        fv.put(min.x).put(min.y).put(max.z).put(0.0f).put(0.0f).put(1.0f);
        /* Back face */
        fv.put(max.x).put(min.y).put(min.z).put(0.0f).put(0.0f).put(-1.0f);
        fv.put(min.x).put(min.y).put(min.z).put(0.0f).put(0.0f).put(-1.0f);
        fv.put(min.x).put(max.y).put(min.z).put(0.0f).put(0.0f).put(-1.0f);
        fv.put(min.x).put(max.y).put(min.z).put(0.0f).put(0.0f).put(-1.0f);
        fv.put(max.x).put(max.y).put(min.z).put(0.0f).put(0.0f).put(-1.0f);
        fv.put(max.x).put(min.y).put(min.z).put(0.0f).put(0.0f).put(-1.0f);
        /* Left face */
        fv.put(min.x).put(min.y).put(min.z).put(-1.0f).put(0.0f).put(0.0f);
        fv.put(min.x).put(min.y).put(max.z).put(-1.0f).put(0.0f).put(0.0f);
        fv.put(min.x).put(max.y).put(max.z).put(-1.0f).put(0.0f).put(0.0f);
        fv.put(min.x).put(max.y).put(max.z).put(-1.0f).put(0.0f).put(0.0f);
        fv.put(min.x).put(max.y).put(min.z).put(-1.0f).put(0.0f).put(0.0f);
        fv.put(min.x).put(min.y).put(min.z).put(-1.0f).put(0.0f).put(0.0f);
        /* Right face */
        fv.put(max.x).put(min.y).put(max.z).put(1.0f).put(0.0f).put(0.0f);
        fv.put(max.x).put(min.y).put(min.z).put(1.0f).put(0.0f).put(0.0f);
        fv.put(max.x).put(max.y).put(min.z).put(1.0f).put(0.0f).put(0.0f);
        fv.put(max.x).put(max.y).put(min.z).put(1.0f).put(0.0f).put(0.0f);
        fv.put(max.x).put(max.y).put(max.z).put(1.0f).put(0.0f).put(0.0f);
        fv.put(max.x).put(min.y).put(max.z).put(1.0f).put(0.0f).put(0.0f);
        /* Top face */
        fv.put(min.x).put(max.y).put(max.z).put(0.0f).put(1.0f).put(0.0f);
        fv.put(max.x).put(max.y).put(max.z).put(0.0f).put(1.0f).put(0.0f);
        fv.put(max.x).put(max.y).put(min.z).put(0.0f).put(1.0f).put(0.0f);
        fv.put(max.x).put(max.y).put(min.z).put(0.0f).put(1.0f).put(0.0f);
        fv.put(min.x).put(max.y).put(min.z).put(0.0f).put(1.0f).put(0.0f);
        fv.put(min.x).put(max.y).put(max.z).put(0.0f).put(1.0f).put(0.0f);
        /* Bottom face */
        fv.put(min.x).put(min.y).put(min.z).put(0.0f).put(-1.0f).put(0.0f);
        fv.put(max.x).put(min.y).put(min.z).put(0.0f).put(-1.0f).put(0.0f);
        fv.put(max.x).put(min.y).put(max.z).put(0.0f).put(-1.0f).put(0.0f);
        fv.put(max.x).put(min.y).put(max.z).put(0.0f).put(-1.0f).put(0.0f);
        fv.put(min.x).put(min.y).put(max.z).put(0.0f).put(-1.0f).put(0.0f);
        fv.put(min.x).put(min.y).put(min.z).put(0.0f).put(-1.0f).put(0.0f);
    }

    /**
     * Create a shader object from the given classpath resource.
     *
     * @param resource
     *            the class path
     * @param type
     *            the shader type
     *
     * @return the shader object id
     *
     * @throws IOException
     */
    public static int createShader(String resource, int type) throws IOException {
        return createShader(resource, type, null);
    }

    /**
     * Create a shader object from the given classpath resource.
     *
     * @param resource
     *            the class path
     * @param type
     *            the shader type
     * @param version
     *            the GLSL version to prepend to the shader source, or null
     *
     * @return the shader object id
     *
     * @throws IOException
     */
    public static int createShader(String resource, int type, String version) throws IOException {
        int shader = glCreateShader(type);

	    String strings = "";
		try
		{
			strings = readFileAsString(resource);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
        glShaderSource(shader, strings);
       

        glCompileShader(shader);
        int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
        String shaderLog = glGetShaderInfoLog(shader);
        if (shaderLog.trim().length() > 0) {
            System.err.println(shaderLog);
        }
        if (compiled == 0) {
            throw new AssertionError("Could not compile shader");
        }
        return shader;
    }
    
    
    private static String readFileAsString(String filename) throws Exception
	{
		StringBuilder source = new StringBuilder();

		FileInputStream in = new FileInputStream(filename);

		//File f = new File(filename);
		


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

}