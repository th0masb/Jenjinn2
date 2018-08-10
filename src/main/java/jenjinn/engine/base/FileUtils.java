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

import xawd.jflow.collections.FlowList;
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
	public static String absoluteName(final Class<?> cls, final String relativeName)
	{
		return absoluteName(cls.getPackage(), relativeName);
	}

	/**
	 * Returns the absolute resource name for the specified class and relative resource name.
	 * @param  cls           the class whose package contains the resource.
	 * @param  relativeName  the relative resource name for which the absolute name is required.
	 * @return the absolute resource name for {@code relativeName}.
	 */
	public static String absoluteName(final Package pkg, final String relativeName)
	{
		return "/" + pkg.getName().replace('.', '/') + "/" + relativeName;
	}
	
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
	
	/*
	 * Get non empty lines satisfying the given filters
	 */
	@SafeVarargs
	public static FlowList<String> cacheResource(Class<?> cls, String relativeName, Predicate<? super String>... filters)
	{
		return cacheResource(cls, relativeName, Arrays.asList(filters));
	}
	
	public static FlowList<String> cacheResource(Class<?> cls, String relativeName, Collection<Predicate<? super String>> filters)
	{
		try (BufferedReader reader = loadResource(cls, relativeName)) {
			return Lists.copy(reader.lines()
					.map(String::trim)
					.filter(x -> !x.isEmpty())
					.filter(x -> Iterate.over(filters).allMatch(filter -> filter.test(x)).get())
					.collect(Collectors.toList()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

//	/**
//	 * @param cls - A {@link Class} object residing in the same package as the resource is to be loaded from
//	 * @param relativeResourceName - The name of the resource file to load.
//	 * @return - A {@link Stream} of the lines contained in the file.
//	 */
//	public static Stream<String> loadResourceFromPackageOf(Class<?> cls, String relativeResourceName)
//	{
//		String absoluteLoc = absoluteName(cls, relativeResourceName);
//		InputStream is = cls.getResourceAsStream(absoluteLoc);
//		
//		if (Objects.isNull(is)) {
//			throw new NullPointerException("Class: " + cls.getSimpleName() + ", Resource: " + absoluteLoc);
//		}
//		
//		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//		return reader.lines().onClose(() -> {
//			try {
//				reader.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		});
//	}
}
