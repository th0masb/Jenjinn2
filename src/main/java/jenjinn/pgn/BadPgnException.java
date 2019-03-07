/**
 *
 */
package jenjinn.pgn;

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

	public BadPgnException(String arg0)
	{
		super(arg0);
	}

	public BadPgnException(Throwable arg0)
	{
		super(arg0);
	}

	public BadPgnException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public BadPgnException(String arg0, Throwable arg1, boolean arg2, boolean arg3)
	{
		super(arg0, arg1, arg2, arg3);
	}
}
