package aufgabe02;

public class Semaphore
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private int threadCapacity = 1;

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Methoden                      |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public synchronized void OCCUPY()
	{
		if ( --threadCapacity < 0 )
		{
			try
			{
				wait();
			}
			catch ( InterruptedException e )
			{
			}
		}
	}

	public synchronized void RELEASE()
	{
		threadCapacity++;
		notify();
	}
}