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
		Beetle beetle = new Beetle(startR, Road.NORTH);

		// drive the beetle
		for (;
		     !beetle.arrived();
		     beetle.drive())
		{
			// force an update
			roads.roadChanged();

			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
		}
		System.out.println("The truck arrived!");
	}
}

