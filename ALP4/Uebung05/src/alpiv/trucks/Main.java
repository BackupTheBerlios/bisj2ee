package alpiv.trucks;

import java.util.Random;

public class Main
{
	private static void usage()
	{
		String[] scenarios = RoadMap.getScenarios();
		System.err.println("usage: <scenario>");
		System.err.println("possible scenarios are:");
		for (int i = 0; i < scenarios.length; i++)
			System.err.println(scenarios[i]);
		System.exit(5);
	}

	/**
	 * The main method for the demonstration run.
	 * The first parameter must be the name of a supported road map scenario.
	 */
	public static void main(String[] args)
	// todo comment this
	{

		final Random rnd = new Random();
		int trucks = 100;

		// parse command line arguments
		if (args.length < 1)
		{
			usage();
		}

		// create the road map scenario to use
		RoadMap roads = null;

		try
		{
			roads = new RoadMap(args[0]);
		}
		catch (RuntimeException ex)
		{
			usage();
		}

		Thread[] threads = new Thread[trucks];

		for (int i = 1; i <= trucks; i++)
		{
			// create and place truck
			final Road startR = roads.getStart(new Random().nextInt(roads.getStarts()));

			// drive the beetle
			final RoadMap roads1 = roads;
			final int k = i;
			Thread thread = new Thread(new Runnable()
			{
				public void run()
				{
					MyCar myCar = new MyCar(startR, Road.NORTH);

					for (; !myCar.arrived(); myCar.drive())
					{
						try
						{
							Thread.sleep(rnd.nextInt(2000));
						}
						catch (InterruptedException ex)
						{
							ex.printStackTrace();
						}

						roads1.roadChanged();
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
}

