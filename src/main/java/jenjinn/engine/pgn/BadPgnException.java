/**
 *
 */
package jenjinn.engine.pgn;

/**
 * @author ThomasB
 *
 */
public class BadPgnException extends Exception
{
	private static final long serialVersionUID = -8663000605400082149L;

	public BadPgnException()
	{
	}

	public BadPgnException(final String arg0)
	{
		super(arg0);
	}

	public BadPgnException(final Throwable arg0)
	{
		super(arg0);
	}

	public BadPgnException(final String arg0, final Throwable arg1)
	{
		super(arg0, arg1);
	}

	public BadPgnException(final String arg0, final Throwable arg1, final boolean arg2, final boolean arg3)
	{
		super(arg0, arg1, arg2, arg3);
	}
}
