/**
 *
 */
package jenjinn.engine.enums;

import jenjinn.engine.misc.Infinity;

/**
 * @author TB
 * @date 1 Feb 2017
 */
public enum TerminationType {
	// Make just a little bigger than the initial alpha beta calls so we don't
	// change the bounds for terminal states..
	/*
	 * Surely we do want to change the bounds for terminakl states, or does it not
	 * matter at all?
	 */
	WHITE_WIN(Infinity.SHORT_INFINITY), BLACK_WIN(-Infinity.SHORT_INFINITY), DRAW(0), NOT_TERMINAL(0);

	public short value;

	private TerminationType(final int value)
	{
		this.value = (short) value;
	}

	public boolean isTerminal()
	{
		return this != NOT_TERMINAL;
	}

	public boolean isWin()
	{
		return this == WHITE_WIN || this == BLACK_WIN;
	}

	@SuppressWarnings("incomplete-switch")
	public boolean matches(final Side s)
	{
		switch (this) {
		case WHITE_WIN:
			return s.isWhite();
		case BLACK_WIN:
			return !s.isWhite();
		}
		return true;
	}
}
