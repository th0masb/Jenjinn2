/**
 * 
 */
package jenjinn.engine.enums;

import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author t
 */
public class BoardSquareDirectionTestData 
{
	private final BoardSquare startSquare;
	private final Map<Direction, List<BoardSquare>> expectedValues;
	
	public BoardSquareDirectionTestData(final BoardSquare startSquare, final Map<Direction, List<BoardSquare>> expectedValues) 
	{
		this.startSquare = startSquare;
		this.expectedValues = expectedValues;
	}

	public List<BoardSquare> getExpectedSquaresUniDirection(final Direction direction, final int lengthCap)
	{
		return take(lengthCap, expectedValues.get(direction));
	}
	
	public Set<BoardSquare> getExpectedSquaresBiDirection(final Direction direction1, final Direction direction2, final int lengthCap)
	{
		final List<BoardSquare> direction1Squares = getExpectedSquaresUniDirection(direction1, lengthCap);
		final List<BoardSquare> direction2Squares = getExpectedSquaresUniDirection(direction2, lengthCap);
		return new HashSet<>(Stream.of(direction1Squares, direction2Squares) .flatMap(List::stream).collect(toList()));
	}
	
	public List<BoardSquare> getActualSquaresUniDirection(final Direction direction, final int lengthCap)
	{
		return startSquare.getAllSquaresInDirections(direction, lengthCap);
	}
	
	public Set<BoardSquare> getActualSquaresBiDirection(final Direction direction1, final Direction direction2, final int lengthCap)
	{
		final List<BoardSquare> direction1Squares = startSquare.getAllSquaresInDirections(direction1, lengthCap);
		final List<BoardSquare> direction2Squares = startSquare.getAllSquaresInDirections(direction2, lengthCap);
		return new HashSet<>(Stream.of(direction1Squares,direction2Squares).flatMap(List::stream).collect(toList()));
	}
	
	private List<BoardSquare> take(final int n, final List<BoardSquare> src)
	{
		return src.subList(0, Math.min(n, src.size()));
	}
}
