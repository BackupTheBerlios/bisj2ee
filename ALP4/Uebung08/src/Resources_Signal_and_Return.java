
public class /*MONITOR*/ Resources_Signal_and_Return
extends Resources
{
	/**
	 * Wenn eine bestimmte Anzahl von Resourcen abgefordert wird, wird zun�chst getestet, ob diese Anzahl
	 * lieferbar ist. Falls ja, wird die Anzahl der lieferbaren Resourcen dekrementiert und der Monitor
	 * verlassen. Falls nein, wird der aktuelle Thread in die Warteschlange und schlafen gelegt. Nach dem
	 * Aufwecken wird die Methode {@link #release(int)} aufrufen mit dem neutralen Wert 0 aufgerufen, um
	 * den n�chsten Thread evtl. aufzuwecken.
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
		release(0);
	}

	/**
	 * Wenn eine bestimmte Anzahl von Resourcen freigegeben wird, wird zun�chst die Anzahl der verf�gbaren
	 * Resourcen um eben diese Zahl erh�ht. Anschlie�end werden alle ggf, vorhandenen Requests der Reihe
	 * nach geweckt, solange die von ihnen angeforderte Resourcenzahl noch lieferbar ist.
	 * <p/>
	 * Da bei der Signal & Return Variante das Signal-Kommandomit dem Verlassen des Monitors verbunden ist,
	 * wird das sukzessive Wecken der Thread durch ein Domino-Prinzip gel�st. Es wurd zun�chst der erste
	 * wartende Thread geweckt, welcher, sobald er den Monitor hat, selbst wieder release(0) aufruft und so
	 * zum Weckenden des n�chsten Threads wird, dieser erh�lt den Monitor und weckt selbst den n�chsten
	 * usw. solange noch Resourcen verf�gbar sind.
	 */
	public final synchronized void release(int num)
	{
		PRECONDITION_release(num);

		available += num;

		if (requests.size() != 0 && requests.elementAt(0).claim <= available)
		{
			ResourceRequest request = nextRequest();

			available -= request.claim;
			request.event.SIGNAL();
		}
	}
}
