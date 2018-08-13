/**
 *
 */
package jenjinn.engine.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import xawd.jflow.collections.FList;
import xawd.jflow.collections.Lists;
import xawd.jflow.iterators.factories.Iterate;

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
	public static String absoluteName(Class<?> cls, String relativeName)
	{
		return absoluteName(cls.getPackage(), relativeName);
	}

	/**
	 * Returns the absolute resource name for the specified class and relative resource name.
	 * @param  pkg           the package which contains the resource.
	 * @param  relativeName  the relative resource name for which the absolute name is required.
	 * @return the absolute resource name for {@code relativeName}.
	 */
	public static String absoluteName(Package pkg, String relativeName)
	{
		return "/" + pkg.getName().replace('.', '/') + "/" + relativeName;
	}
	
	/**
	 * Create a {@linkplain BufferedReader} pointing at a resource file.
	 * 
	 * @param cls          A class which lies in the same package as the file.
	 * @param relativeName The name of the resource file relative to it's parent
	 *                     package.
	 */
	public static BufferedReader loadResource(Class<?> cls, String relativeName)
	{
		String absoluteLoc = absoluteName(cls, relativeName);
		InputStream is = cls.getResourceAsStream(absoluteLoc);
		if (is == null) {
			throw new NullPointerException("Class: " + cls.getSimpleName() + ", Resource: " + absoluteLoc);
		}
		else {
			return new BufferedReader(new InputStreamReader(is));
		}
	}
	
	/**
	 * Loads and caches the lines of a resource which satisfy all of the given
	 * predicates.
	 * 
	 * @param cls          A class which lies in the same package as the file.
	 * @param relativeName The name of the resource file relative to it's parent
	 *                     package.
	 * @param filters      A collection of predicates each line must satisfy.
	 */
	@SafeVarargs
	public static FList<String> cacheResource(Class<?> cls, String relativeName, Predicate<? super String>... filters)
	{
		return cacheResource(cls, relativeName, Arrays.asList(filters));
	}
	
	/**
	 * Loads and caches the lines of a resource which satisfy all of the given
	 * predicates.
	 * 
	 * @param cls          A class which lies in the same package as the file.
	 * @param relativeName The name of the resource file relative to it's parent
	 *                     package.
	 * @param filters      A collection of predicates each line must satisfy.
	 */
	public static FList<String> cacheResource(Class<?> cls, String relativeName, Collection<Predicate<? super String>> filters)
	{
		try (BufferedReader reader = loadResource(cls, relativeName)) {
			return Lists.copy(reader.lines()
					.map(String::trim)
					.filter(x -> !x.isEmpty())
					.filter(x -> Iterate.over(filters).allMatch(filter -> filter.test(x)))
					.collect(Collectors.toList()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
