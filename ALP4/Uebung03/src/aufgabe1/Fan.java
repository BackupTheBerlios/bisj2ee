package aufgabe1;

public class Fan
{
	/**
	 * @param tasks die auszuführenden tasks
	 *              <p/>
	 *              Die Methode legt zun�chst ein Array mit je einem Thread f�r je einen Task an.
	 *              Anschlie�end werden alle Threads nacheinander gestartet.
	 *              Um die komplette Ausf�hrung aller Threads vor Abgabe der Kontrollflusses an den Aufrufer
	 *              sicherzustellen, wird auf die ordnungsgem��e Beendigung aller Threads per join() gewartet.
	 *              Sollte ein Thread nicht ordnungsgem�� beendet werden, wird die Fehlermeldung ausgegeben.
	 */
	public void fan(Runnable[] tasks)
	{
		Thread[] threads = new Thread[tasks.length];

		// Threads anlegen:
		for (int i = 0; i < tasks.length; i++)
		{
			threads[i] = new Thread(tasks[i]);
		}

		// Threads starten:
		for (int i = 0; i < tasks.length; i++)
		{
			threads[i].start();
		}

		// auf Beendigung der Threads warten:
		for (int i = 0; i < tasks.length; i++)
		{
			try
			{
				threads[i].join();
			}
			catch (InterruptedException e)
			{
				System.err.println("Ein Thread konnte nicht ordnungsgem�� beendet werden.");
				e.printStackTrace();
			}
		}
	}

}
