package alpiv.trucks;

class MyCar
extends Beetle
{
	MyCar(Road road, int dir)
	{
		super(road, dir);
	}

	void drive()
	// todo comment this: oder changed to prevent disappearing trucks
	{
		if (arrived()) return;

		int steer;

		// try straight ahead
		if (myRoad.getExit(Road.nextDirection(myDirection, Road.AHEAD)) != null)
		{
			steer = Road.AHEAD;
		}
		else
		{
			// turn right
			// (Beetles can turn in place, real trucks of course
			// cannot!)
			steer = Road.RIGHT;
		}

		myDirection = Road.nextDirection(myDirection, steer);

		// try to move
		Road nextRoad = myRoad.getExit(myDirection);
		if (nextRoad != null)
		{
			nextRoad.setTraffic(this);
			myRoad.setTraffic(null);
			myRoad = nextRoad;

			if (arrived())
			{
				try
				{
					Thread.sleep(750);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				myRoad.setTraffic(null);
			}
		}
	}
}

