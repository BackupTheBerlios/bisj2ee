package aufgabe01;

class ThreadSaveMaxComputation
{

	private static int myMax = Integer.MIN_VALUE;

	/**
	 * Hier mu� nichts ge�ndert werden, da es sich um einen nur lesenden Zugriff auf einem int handelt;
	 */
	public static int getMax()
	{
		return myMax;
	}

	/**
	 * Im Vergleich zur Originalmethode wurde ein kritsicher Abschnitt eingef�gt, der auf den Klassenobjekt synchronisiert ist.
	 * Der Abschnitt umschlie�t den Vergleich zusammen mit dem evtl. Schreiben des neuen Wertes und verhindert so, da� ein Fehler
	 * wie er im beiliegenden Zeitschnitt angegeben ist, vorkomen kann. (Kurz: Falls ein neues Maximum gefunden wurde, wird es
	 * auch sofort abgespeichert.
	 */
	public static void compute(int[] field)
	{
		if (field == null) return;

		for (int i = 0; i < field.length; i++)
		{
			synchronized (ThreadSaveMaxComputation.class)
			{
				if (field[i] > myMax)
				{
					myMax = field[i];
				}
			}
		}
	}
}
