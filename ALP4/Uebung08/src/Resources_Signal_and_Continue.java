
public class /*MONITOR*/ Resources_Signal_and_Continue
extends Resources
{

	/**
	 * Wenn eine bestimmte Anzahl von Resourcen abgefordert wird, wird zunächst getestet, ob diese Anzahl
	 * lieferbar ist. Falls ja, wird die Anzahl der lieferbaren Resourcen dekrementiert und der Monitor
	 * verlassen. Falls nein, wird der aktuelle Thread in die Warteschlange und schlafen gelegt.
	 *
	 * @param num Die beantrangte Anzahl von Resourcen.
	 */
	public void request(int num)
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
	 * Da bei jedem Aufweckvorgang aktuelle Thread nicht blockiert und der aufgeweckte Thread den Monitor
	 * erst erhält, wenn release völlig abgearbeitet ist, wird available entsprechend dekrementiert BEVOR
	 * der jeweils nächste Thread geweckt wird. Nur so kann man feststellen, wieviele Thread noch bedient
	 * werden können.
	 *
	 * @param num freizugebende Resourcen.
	 */
	public void release(int num)
	{
		PRECONDITION_release(num);

		available += num;
		if (requests.size() > 0)
		{
			ResourceRequest currentRequest;

			while ((currentRequest = nextRequest()) != null && currentRequest.claim <= available)
			{
				available -= currentRequest.claim;
				currentRequest.event.SIGNAL();
			}
		}
	}
}