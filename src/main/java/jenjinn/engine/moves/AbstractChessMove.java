/**
 *
 */
package jenjinn.engine.moves;

import jenjinn.engine.enums.BoardSquare;

/**
 * @author ThomasB
 *
 */
public abstract class AbstractChessMove implements ChessMove
{
	private final BoardSquare source, target;

	public AbstractChessMove(final BoardSquare start, final BoardSquare target) {
		this.source = start;
		this.target = target;
	}

	@Override
	public BoardSquare getSource()
	{
		return source;
	}

	@Override
	public BoardSquare getTarget()
	{
		return target;
	}
}
