package aufgabe1;

public class Fan
{
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
				e.printStackTrace();
			}
		}
	}

}
