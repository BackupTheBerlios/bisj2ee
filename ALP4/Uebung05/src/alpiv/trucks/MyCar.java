package alpiv.trucks;

class MyCar
extends Beetle
{
	MyCar(Road road, int dir)
	{
		super(road, dir);
	}

	void drive()
	{
		if (arrived()) return;

		int steer;

		// try straight ahead
		if (myRoad.getExit(Road.nextDirection(myDirection, Road.AHEAD))
		    != null)
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
		Road nextR = myRoad.getExit(myDirection);
		if (nextR != null)
		{
			nextR.setTraffic(this);
			myRoad.setTraffic(null);
			myRoad = nextR;

			if (arrived())
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				myRoad.setTraffic(null);
			}

			// todo comment this: oder changed to prevent disappearing trucks
		}
	}

	boolean arrived()
	{
		return myRoad.getType() == Road.GOAL;
	}
}

