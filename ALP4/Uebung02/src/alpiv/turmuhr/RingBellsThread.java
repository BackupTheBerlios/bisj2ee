package alpiv.turmuhr;

public class RingBellsThread
extends Thread
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private int lowRings;
	private int highRings;
	private Turmuhr uhr;

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                   Konstruktoren                   |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public RingBellsThread(int lowRings, int highRings, Turmuhr uhr)
	{
		this.lowRings = lowRings;
		this.highRings = highRings;
		this.uhr = uhr;
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Methoden                      |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	// läutet highRings mal hoch und lowRings mal low
	public void run()
	{
		int i = highRings;

		while (i-- > 0)
		{
			uhr.highBell();

			try
			{
				Thread.sleep(250);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		i = lowRings;

		while (i-- > 0)
		{
			uhr.lowBell();

			try
			{
				Thread.sleep(1400);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
