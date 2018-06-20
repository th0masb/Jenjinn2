/**
 *
 */
package jenjinn.engine.pgn.movebuilder;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jenjinn.engine.pgn.BadPgnException;
import jenjinn.engine.pgn.PgnGameConverter;
import jenjinn.engine.utils.FileUtils;

/**
 * @author ThomasB
 *
 */
class PgnGameConversionIntegrationTest
{
	@ParameterizedTest
	@MethodSource
	void test(String pgnString)
	{
		try {
			PgnGameConverter.parse(pgnString);
		} catch (final BadPgnException e) {
			e.printStackTrace();
			fail();
		}
	}

	static Stream<Arguments> test()
	{
		final Class<?> cls = PgnGameConversionIntegrationTest.class;
		return FileUtils.loadResourceFromPackageOf(cls, "integrationTestCases").map(String::trim).map(Arguments::of);
	}
}
