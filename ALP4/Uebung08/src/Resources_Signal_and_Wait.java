
public class Resources_Signal_and_Wait
extends Resources
{
	/**
	 * Wenn eine bestimmte Anzahl von Resourcen abgefordert wird, wird zunächst getestet, ob diese Anzahl
	 * lieferbar ist. Falls nicht, wird der aktuelle Thread in die Warteschlange und schlafen gelegt.
	 * Falls ja bzw, nachdem der Thread wieder geweckt wurde, wird die Anzahl der lieferbaren Resourcen
	 * dekrementiert und der Monitor verlassen.
	 *
	 * @param num Die beantrangte Anzahl von Resourcen.
	 */
	public void request(int num)
	{
		PRECONDITION_request(num);

		if (num > available)
		{
			ResourceRequest newRequest = new ResourceRequest(num);
			requests.add(newRequest);

			newRequest.event.WAIT();
		}

		available -= num;
	}

	/**
	 * Wenn eine bestimmte Anzahl von Resourcen freigegeben wird, wird zunächst die Anzahl der verfügbaren
	 * Resourcen um eben diese Zahl erhöht. Anschließend werden alle ggf, vorhandenen Request der Reihe
	 * nach geweckt, solange die von ihnen angeforderte Resourcenzahl noch lieferbar ist.
	 * <p/>
	 * Da bei jedem Aufweckvorgang aktuelle Thread blockiert und der aufgeweckte Thread den Monitor erhält,
	 * wird der Geweckte die release-Methode an der Stelle fortsetzten, an der er zuvor in die
	 * Warteschlange geschickt wurde. Dort wird available entsprechend dekrementiert bevor der jeweils
	 * nächste Thread geweckt wird.
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
				currentRequest.event.SIGNAL();
			}
		}
	}
}
