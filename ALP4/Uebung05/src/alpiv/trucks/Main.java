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
	{
		Random rnd = new Random();

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

		// create and place truck
		Road startR = roads.getStart(rnd.nextInt(roads.getStarts()));
		final Beetle beetle = new Beetle(startR, Road.NORTH);

		Road startR2;
		do
		{
			startR2 = roads.getStart(rnd.nextInt(roads.getStarts()));
		}
		while (startR2 == startR);

		final Beetle beetle2 = new Beetle(startR2, Road.NORTH);

		// drive the beetle
		final RoadMap roads1 = roads;
		Thread t1 = new Thread(new Runnable()
		{
			public void run()
			{
				for (;
				     !beetle.arrived(); beetle.drive())
				{
					// force an update
					roads1.roadChanged();

					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});


		Thread t2 = new Thread(new Runnable()
		{
			public void run()
			{
				for (;
				     !beetle2.arrived(); beetle2.drive())
				{
					// force an update
					roads1.roadChanged();

					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});

		t1.start();
		t2.start();

		try
		{
			t1.join();
			t2.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		System.out.println("The truck arrived!");
	}
}

