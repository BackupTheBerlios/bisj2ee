
public class Resources_Signal_and_Return
extends Resources
{
	/**
	 * Wenn eine bestimmte Anzahl von Resourcen abgefordert wird, wird zun�chst getestet, ob diese Anzahl
	 * lieferbar ist. Falls ja, wird die Anzahl der lieferbaren Resourcen dekrementiert und der Monitor
	 * verlassen. Falls nein, wird der aktuelle Thread in die Warteschlange und schlafen gelegt.
	 *
	 * @param num Die beantrangte Anzahl von Resourcen.
	 */
	public synchronized void request(int num)
	{
		PRECONDITION_request(num);

		if (num <= available)
		{
			available -= num;
			return;
		}

		ResourceRequest newRequest = new ResourceRequest(num);
		requests.add(newRequest);

		newRequest.event.WAIT();
	}

	/**
	 * Wenn eine bestimmte Anzahl von Resourcen freigegeben wird, wird zun�chst die Anzahl der verf�gbaren
	 * Resourcen um eben diese Zahl erh�ht. Anschlie�end werden alle ggf, vorhandenen Request der Reihe
	 * nach geweckt, solange die von ihnen angeforderte Resourcenzahl noch lieferbar ist.
	 * <p/>
	 * Da bei der Signal & Return Variante das Signal-Kommando das letzte der Methode ist (quasi nach
	 * Signal immer implizit ein return steht), mu� vor dem t�ts�chlichen Wecken zun�chst festgestellt
	 * werden, welche Threads geweckt werden k�nnen. Dazu geht man der Reihe nach durch und summiert die
	 * beantragten Resourcen. �bersteigt die Summe die verf�gbaren Resourcen oder gibt es keine wartenden
	 * Threads mehr, wei� man, bis zu welchem Thread man wecken kann.
	 * <p/>
	 * Doch bevor man weckt, mu� alles erledigt sein - nach dem Wecken gibt es ja keine Gelegenheit mehr
	 * dazu. Also mu� available um die soeben gez�hlte Summe noch dekeremtiert werden und dann k�nnen alle
	 * Threads geweckt werden.
	 * <p/>
	 * Daf�r scheidet eine iterative L�sung wegen dem impliziten return aus, aber eine rekusive L�sung ist
	 * m�glich. Dies leistet die Hilfsmethode {@link #recursiveRelease(int)}, welche den Thread an der
	 * �bergebenen Stelle weckt, vorher aber sich selbst rekursiv mit dem um 1 verminderten Argument
	 * aufruft. Der Rekursionsanker ist logischerweise, wenn das Argument kleiner als 0 wird.
	 */
	public final synchronized void release(int num)
	{
		PRECONDITION_release(num);

		available += num;

		if (requests.size() == 0) return;

		int i = 0;
		int count = 0;

		while (count <= available && i < requests.size())
		{
			ResourceRequest request = requests.elementAt(i);
			count += request.claim;
			i++;
		}

		available -= count;

		recursiveRelease(i - 1);
	}

	private void recursiveRelease(int i)
	{
		if (i < 0) return;

		recursiveRelease(i - 1);

		requests.elementAt(i).event.SIGNAL();
		return;
	}
}
