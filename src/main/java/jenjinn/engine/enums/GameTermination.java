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
	// Make just a little bigger than the initial alpha beta calls so we don't
	// change the bounds for terminal states..
	/*
	 * Surely we do want to change the bounds for terminakl states, or does it not
	 * matter at all?
	 */
	WHITE_WIN(Infinity.INT_INFINITY), BLACK_WIN(-Infinity.INT_INFINITY), DRAW(0), NOT_TERMINAL(0);

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
