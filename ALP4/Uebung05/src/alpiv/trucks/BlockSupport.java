package alpiv.trucks;


/** implement your synchronization mechanisms here!
 */

/**
 * Erlaubt das Belegen eines Objekts durch einen Thread.
 * Es kann zu jeder Zeit nur ein Thread das Objekt belegen.
 */
public class BlockSupport
// todo comment this
{

	private volatile Thread current = null;
	private int threadCapacity = 1;

	/**
	 * Liefert den Thread, der gerade das Objekt belegt hat, oder null.
	 * Achtung, dies sollte nur zu Diagnosezwecken verwendet werden!
	 *
	 * @return den belegenden Thread oder null
	 */
	public synchronized Thread blocking()
	{
		return current;
	}

	/**
	 * Belegt das Objekt.
	 * Ist das Objekt bereits belegt, kehrt diese Methode erst zurück,
	 * wenn es wieder freigegeben wird.
	 *
	 * @throws InterruptedException ein wartender Thread wurde unterbrochen
	 */
	public synchronized void block()
	{
		if (--threadCapacity < 0)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		current = Thread.currentThread();
	}

	/**
	 * Gibt das Objekt wieder frei.
	 * Warten andere Threads auf die Freigabe, so wird nichtdeterministisch
	 * einer davon ausgewählt und belegt als nächster das Objekt.
	 */
	public synchronized void unblock()
	{
		threadCapacity++;
		notify();
	}
}