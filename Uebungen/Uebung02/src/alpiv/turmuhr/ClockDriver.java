package alpiv.turmuhr;

import java.util.GregorianCalendar;

public class ClockDriver
extends Thread
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	private GregorianCalendar now = new GregorianCalendar();

	private Turmuhr uhr;
	private int takt;
	private final int AUFLÖSUNG;

	private int lastBell = -1;

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                   Konstruktoren                   |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public ClockDriver(Turmuhr uhr, int takt)
	{
		this.uhr = uhr;
		this.takt = takt;

		// Einstellen der zeitlichen Auflösung:
		AUFLÖSUNG = 1000/takt;

		updateClock();
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Methoden                      |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public void run()
	{
		while (true)
		{
			updateClock();

			try
			{
				Thread.sleep(AUFLÖSUNG);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * Setzt die neuen Zeitwerte und startet ggf. neue Threads zum Läuten der Glocken
	 */
	private void updateClock()
	{
		int hour = now.get(GregorianCalendar.HOUR);
		int minute = now.get(GregorianCalendar.MINUTE);
		int second = now.get(GregorianCalendar.SECOND);

		uhr.setTime(hour, minute, second);

		// muß geklingelt werden?
		if ((minute == 0 || minute == 15 || minute == 30 || minute == 45) && lastBell != minute)
		{
			if (hour == 0) hour = 12;

			switch (minute)
			{
				case 0:
					new RingBellsThread(hour, 4, uhr).start();
					break;
				case 15:
					new RingBellsThread(0, 1, uhr).start();
					break;
				case 30:
					new RingBellsThread(0, 2, uhr).start();
					break;
				case 45:
					new RingBellsThread(0, 3, uhr).start();
					break;
			}

			lastBell = minute;
		}

		// Zeit fortschreiten:
		now.add(GregorianCalendar.MILLISECOND, takt * AUFLÖSUNG);
	}
}