/**
 *
 */
package jenjinn.engine.enums;

import jenjinn.engine.misc.Infinity;

/**
 * @author TB
 * @date 1 Feb 2017
 */
public enum GameTermination
{
	WHITE_WIN(Infinity.INT_INFINITY / 2), BLACK_WIN(-Infinity.INT_INFINITY / 2), DRAW(0), NOT_TERMINAL(0);

	public int value;

	private GameTermination(final int value)
	{
		this.value = value;
	}

	public boolean isTerminal()
	{
		return this != NOT_TERMINAL;
	}

	public boolean isWin()
	{
		return this == WHITE_WIN || this == BLACK_WIN;
	}

	public static GameTermination getWinFor(final Side side)
	{
		return side.isWhite()? WHITE_WIN : BLACK_WIN;
	}
}
