package aufgabe02;


/**
 * Implementation einer handels�blichen bin�ren Semaphore.
 * <p/>
 * Prinzip: Beliebig viele Threads rufen ACQUIRE auf, um den lock auf die Semaphore zu bekommen.
 * Bis auf einen werden alle Threads in den Wartezustand verlegt bis RELEASE aufgerufen wird.
 * Ein Thread wird wieder geweckt und l�uft weiter. Wird RELEASE aufgerufen, wird der n�chste geweckt usw.
 * <p/>
 * Daducrh wird ein wechselseitiger anonymer Ausschlu� garantiert.
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
	 * Threadz�hler wird dekrementiert. Ist der Z�hler unter null, wird der aktuelle Thread sofort in den Wartezustand
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
	 * Z�hler wird inkrementiert, Notify wird aufgerufen.
	 */
	public final synchronized void RELEASE()
	{
		threadCapacity++;
		notify();
	}
}