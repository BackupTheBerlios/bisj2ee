
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
	 * Falls eine Freigabe zu Zeit nicht m�glich ist (!available), wird der Thread zun�chst in den Wartezustand
	 * versetzt bis die Freigabe m�glich ist.
	 * <p/>
	 * Sobald die Freigabe m�glich ist, wird available auf false gesetzt und der Monitor verlassen.
	 */
	public synchronized void P()
	throws InterruptedException
	{
		if (!available)
			wait();

		available = false;
	}

	/**
	 * Erm�glicht die Neuvergabe der Freigabe. Available wird einfach auf false gesetzt und ein ggf. wartender Thread
	 * wird wieder erweckt.
	 */
	public synchronized void V()
	{
		available = true;
		notify();
	}
}
