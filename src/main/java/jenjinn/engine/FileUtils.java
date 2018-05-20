/**
 *
 */
package jenjinn.engine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

/**
 * @author ThomasB
 */
public final class FileUtils
{
	private FileUtils() {}

	/**
	 * Returns the absolute resource name for the specified class and relative resource name.
	 * @param  cls           the class whose package contains the resource.
	 * @param  relativeName  the relative resource name for which the absolute name is required.
	 * @return the absolute resource name for {@code relativeName}.
	 */
	public static String absoluteName(final Class<?> cls, final String relativeName)
	{
		return "/" + cls.getPackage().getName().replace('.', '/') + "/" + relativeName;
	}

	/**
	 * @param cls - A {@link Class} object residing in the same package as the resource is to be loaded from
	 * @param relativeResourceName - The name of the resource file to load.
	 * @return - A {@link Stream} of the lines contained in the file.
	 */
	public static Stream<String> loadResourceFromPackageOf(final Class<?> cls, final String relativeResourceName)
	{
		final InputStream is = cls.getResourceAsStream(absoluteName(cls, relativeResourceName));
		return new BufferedReader(new InputStreamReader(is)).lines();
	}
}
