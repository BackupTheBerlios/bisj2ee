package alpiv.trucks;

import java.util.Random;

public class Main
{
	private static void usage()
	{
		String[] scenarios = RoadMap.getScenarios();
		System.err.println("usage: <scenario> <number of trucks> [0|1]");
		System.err.println("possible scenarios are:");
		for (int i = 0; i < scenarios.length; i++)
			System.err.println(scenarios[i]);
		System.err.println("if last parameter is 1 cars' breaks have random duration between 0 and 500 ms," +
		                   " else all cars' breaks have a duration of 500 ms");
		System.exit(5);
	}

	/**
	 * Es wird das entsprendende Szenarion geladen, die übergebene Anzahl von Autos erzeugt und je in
	 * einem eigenen Thread gesteuert. Wählt man Zufallspausen, dann ist die Pausenlänge nach jedem
	 * Fahrschritt jedes Autos stets zufällig zwischen 0 und 500 ms gewählt. Dies führt dazu, daß sich alle
	 * Autos mit variablem Tempo bewegen, was sie Asynchronität erhöht (Verklemmungen werden häufiger,
	 * Szenario realistischer);
	 * Ansonsten ist die Pause bei allen Fahrschritten aller Autos jeweils 500 ms. Dadurch bewegen sich die
	 * Autos viel regelmäßiger und es ensteht häufiger ein Rhythmus, was Verklemmungen seltener macht,
	 * das Szenario ist jedoch weniger realistisch.
	 *
	 * @param args Szenarioname Truckanzahl [0|1] für konstante oder Zufallspausen beim Fahren
	 */
	public static void main(String[] args)
	{
		// parse command line arguments
		if (args.length < 3)
		{
			usage();
		}

		// create the road map scenario to use
		try
		{
			final RoadMap roads = new RoadMap(args[0]);
			final int trucks = Integer.parseInt(args[1]);
			final boolean useRandomBreaks = Integer.parseInt(args[2]) == 1;
			final Random random = new Random();

			Thread[] threads = new Thread[trucks];

			for (int i = 1; i <= trucks; i++)
			{
				// create and place truck
				final Road startRoad = roads.getStart(random.nextInt(roads.getStarts()));

				// drive the beetle
				final int k = i;
				Thread thread = new Thread(new Runnable()
				{
					public void run()
					{
						final MyCar myCar = new MyCar(startRoad, Road.NORTH);

						while (!myCar.arrived())
						{
							myCar.drive();
							roads.roadChanged();

							try
							{
								Thread.sleep(useRandomBreaks ? random.nextInt(500) : 500);
							}
							catch (InterruptedException ex)
							{
								ex.printStackTrace();
							}
						}

						System.out.println("The truck " + k + " has arrived! (moved to garage)");
					}
				});

				threads[i - 1] = thread;
			}

			for (int i = 0; i < threads.length; i++)
			{
				threads[i].start();
			}
		}
		catch (Exception ex)
		{
			usage();
		}
	}
}
