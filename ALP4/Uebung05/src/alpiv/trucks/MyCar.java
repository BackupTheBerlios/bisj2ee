package alpiv.trucks;

class MyCar
extends Beetle
{
	MyCar(Road road, int dir)
	{
		super(road, dir);
	}

	/**
	 * Falls das Auto breits angekommen ist, muß nichts getan werden.
	 * Verändert gegenüber diser Methode in Beetle wurde die untere Hälte.
	 * Zunächst wird versucht, den lock auf das nächste Straßenstück zu bekommen, sobald er erhalten wurde,
	 * wird der lock auf das aktuelle Stück freigegeben, dann wird das Auto auf das nächste Straßenstück
	 * (auf das es bereiets den lock hat) umgesetzt. Im Original war die Reihenfolge, entgegen der
	 * Beschreibung in der Aufgabenstellen andersrum, so daß das aktuelle Stück freigegeben wurde, bevor
	 * das nächste Stück tatsächlich gelockt wurde, was zu einem gelegentlichen "Verschwinden" des Autos
	 * auf der Karte führte.
	 * <p/>
	 * Ist das Auto im Ziel angekommen, belibt es dort für 750 ms, damit man es auf der Karte noch sieht,
	 * und dann wird das entsprechende Strßenstück entleert - das Auto verschwindet.
	 */
	void drive()
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