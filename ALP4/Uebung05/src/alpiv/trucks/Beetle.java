package alpiv.trucks;

class Beetle
implements Truck
{
	int myDirection;
	Road myRoad;

	Beetle(Road road, int dir)
	{
		myDirection = dir;
		myRoad = road;
		myRoad.setTraffic(this);
	}

	void drive()
	{
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
			myRoad.setTraffic(null);
			myRoad = nextR;
			myRoad.setTraffic(this);
		}
	}

	boolean arrived()
	{
		return myRoad.getType() == Road.GOAL;
	}

	public int getDirection()
	{
		return myDirection;
	}

	void setDirection(int dir)
	{
		myDirection = dir;
	}
}

