package aufgabe02;


/**
 * Implementation einer handelsüblichen binären Semaphore.
 * <p/>
 * Prinzip: Beliebig viele Threads rufen ACQUIRE auf, um den lock auf die Semaphore zu bekommen.
 * Bis auf einen werden alle Threads in den Wartezustand verlegt bis RELEASE aufgerufen wird.
 * Ein Thread wird wieder geweckt und läuft weiter. Wird RELEASE aufgerufen, wird der nächste geweckt usw.
 * <p/>
 * Daducrh wird ein wechselseitiger anonymer Ausschluß garantiert.
 */
public class Semaphore
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private int threadCapacity = 1;

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Methoden                      |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	/**
	 * Threadzähler wird dekrementiert. Ist der Zähler unter null, wird der aktuelle Thread sofort in den Wartezustand
	 * versetzt.
	 */
	public final synchronized void ACQUIRE()
	{
		if (--threadCapacity < 0)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
			}
		}
	}

	/**
	 * Zähler wird inkrementiert, Notify wird aufgerufen.
	 */
	public final synchronized void RELEASE()
	{
		threadCapacity++;
		notify();
	}
}