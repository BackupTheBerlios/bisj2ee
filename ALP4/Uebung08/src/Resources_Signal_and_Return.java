
public class Resources_Signal_and_Return
extends Resources
{
	/**
	 * Wenn eine bestimmte Anzahl von Resourcen abgefordert wird, wird zunächst getestet, ob diese Anzahl
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
	 * Wenn eine bestimmte Anzahl von Resourcen freigegeben wird, wird zunächst die Anzahl der verfügbaren
	 * Resourcen um eben diese Zahl erhöht. Anschließend werden alle ggf, vorhandenen Request der Reihe
	 * nach geweckt, solange die von ihnen angeforderte Resourcenzahl noch lieferbar ist.
	 * <p/>
	 * Da bei der Signal & Return Variante das Signal-Kommando das letzte der Methode ist (quasi nach
	 * Signal immer implizit ein return steht), muß vor dem tätsächlichen Wecken zunächst festgestellt
	 * werden, welche Threads geweckt werden können. Dazu geht man der Reihe nach durch und summiert die
	 * beantragten Resourcen. Übersteigt die Summe die verfügbaren Resourcen oder gibt es keine wartenden
	 * Threads mehr, weiß man, bis zu welchem Thread man wecken kann.
	 * <p/>
	 * Doch bevor man weckt, muß alles erledigt sein - nach dem Wecken gibt es ja keine Gelegenheit mehr
	 * dazu. Also muß available um die soeben gezählte Summe noch dekeremtiert werden und dann können alle
	 * Threads geweckt werden.
	 * <p/>
	 * Dafür scheidet eine iterative Lösung wegen dem impliziten return aus, aber eine rekusive Lösung ist
	 * möglich. Dies leistet die Hilfsmethode {@link #recursiveRelease(int)}, welche den Thread an der
	 * übergebenen Stelle weckt, vorher aber sich selbst rekursiv mit dem um 1 verminderten Argument
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
