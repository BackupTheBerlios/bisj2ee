
/**
 * Implementation einer boolschen Semaphore.
 */
public class BooleanSemaphore
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private boolean available = true;

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Methoden                      |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	/**
	 * Falls eine Freigabe zu Zeit nicht möglich ist (!available), wird der Thread zunächst in den Wartezustand
	 * versetzt bis die Freigabe möglich ist.
	 * <p/>
	 * Sobald die Freigabe möglich ist, wird available auf false gesetzt und der Monitor verlassen.
	 */
	public synchronized void P()
	throws InterruptedException
	{
		if (!available)
			wait();

		available = false;
	}

	/**
	 * Ermöglicht die Neuvergabe der Freigabe. Available wird einfach auf false gesetzt und ein ggf. wartender Thread
	 * wird wieder erweckt.
	 */
	public synchronized void V()
	{
		available = true;
		notify();
	}
}
