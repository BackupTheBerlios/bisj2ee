package alpiv.trucks;

/**
 * Objects of this class represent sections of a road map.
 * A road can be (b)locked for exclusive access by a (Truck)-Thread.
 * It is also possible register a truck as traffic on the road,
 * but only one truck at a time.
 */
public class Road
extends BlockSupport
{
	/**
	 * Constants for the directions of exits.
	 */
	public static final int DIRECTIONS = 8;
	public static final int NORTH = 0;
	public static final int NORTHEAST = 1;
	public static final int EAST = 2;
	public static final int SOUTHEAST = 3;
	public static final int SOUTH = 4;
	public static final int SOUTHWEST = 5;
	public static final int WEST = 6;
	public static final int NORTHWEST = 7;

	/**
	 * Names for the directions.
	 */
	public static final String[] DIRNAMES = new String[]
	{
		"north", "northeast", "east", "southeast",
		"south", "southwest", "west", "northwest"
	};

	/**
	 * Constants for direction changes.
	 */
	public static final int LEFT = -1;
	public static final int AHEAD = 0;
	public static final int RIGHT = 1;
	public static final int NOWHERE = 2;

	/**
	 * Constants for the road types.
	 */
	public static final int REGULAR = 0;
	public static final int START = 1;
	public static final int GOAL = 2;


	/**
	 * The name of this road section.
	 */
	private String name;
	/**
	 * The type of this road section. One of the constants above.
	 */
	private int type;
	/**
	 * The exits from this road. One entry per possible direction.
	 */
	private Road[] exits;
	/**
	 * The truck currently driving on this road section.
	 */
	private Truck traffic;

	/**
	 * The layout coordinates for this road section on the map.
	 */
	private int x;
	private int y;
	/**
	 * Whom to notified of traffic changes.
	 */
	private RoadObserver observer;


	/**
	 * A helper method to calculate direction changes.
	 * This takes the passed current direction, and calculates the
	 * new direction based on the given direction change constant.
	 *
	 * @param current the current direction
	 * @param turn    the direction to move to
	 * @return the new direction
	 */
	public static int nextDirection(int current, int turn)
	{
		int next = (current + turn) % DIRECTIONS;
		if (next < 0) next = next + DIRECTIONS;
		return next;
	}

	/**
	 * A helper method to calculate the opposite of a direction.
	 *
	 * @param current the current direction
	 * @return the opposite direction of the current one
	 */
	public static int oppositeDirection(int current)
	{
		return (current + (DIRECTIONS / 2)) % DIRECTIONS;
	}

	/**
	 * Convenience constructor with name and observer.
	 * This creates a regular road with coordinates 0/0.
	 *
	 * @param name     the name of the Road object
	 * @param observer whom to notify of traffic changes
	 */
	public Road(String name, RoadObserver observer)
	{
		this(name, observer, 0, 0, REGULAR);
	}

	/**
	 * Convenience constructor with name, observer, and type.
	 * This creates a road of the given type with coordinates 0/0.
	 *
	 * @param name     the name of the Road object
	 * @param observer whom to notify of traffic changes
	 * @param type     what kind of road is this
	 */
	public Road(String name, RoadObserver observer, int type)
	{
		this(name, observer, 0, 0, type);
	}

	/**
	 * Full constructor.
	 * This creates a road of the given type at the given coordinates.
	 * Name and observer must be valid non-null Objects.
	 * The type must be one of the constants defined above.
	 *
	 * @param name     the name of the Road object
	 * @param observer whom to notify of traffic changes
	 * @param x        X coordinate for displaying this road
	 * @param y        Y coordinate for displaying this road
	 * @param type     what kind of road is this
	 */
	public Road(String name, RoadObserver observer, int x, int y, int type)
	{
		this.name = name;
		this.observer = observer;
		this.x = x;
		this.y = y;
		this.type = type;
		this.traffic = null;
		this.exits = new Road[DIRECTIONS];
		for (int i = 0; i < DIRECTIONS; i++)
			this.exits[i] = null;
	}

	/**
	 * Get the type of the road object.
	 * Returns one of the constants defined above.
	 *
	 * @return type of the road
	 */
	public int getType()
	{
		return this.type;
	}

	/**
	 * Get the name of the road object.
	 *
	 * @return name of the road object.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Get the X coordinate for map layout.
	 *
	 * @return the X coordinate of this road object
	 */
	public int getX()
	{
		return this.x;
	}

	/**
	 * Get the Y coordinate for map layout.
	 *
	 * @return the Y coordinate of this road object
	 */
	public int getY()
	{
		return this.y;
	}

	/**
	 * Get the current traffic on this road.
	 * This is either a truck object or null.
	 *
	 * @return current ruck on the road, or null
	 */
	public Truck getTraffic()
	{
		return this.traffic;
	}

	/**
	 * Set this roads location for layout purposes.
	 *
	 * @param x the X coordinate of this road
	 * @param y the Y coordinate of this road
	 */
	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Place or remove some traffic on the road.
	 * A null argument removes the traffic, an non-null arguments sets it.
	 * The latter is only allowed if the road is currently empty.
	 * Otherwise, a crash occurs, indicated by a RuntimeException.
	 * Whenever this method is called, the roads observer is
	 * notified of the change.
	 *
	 * @param traffic the truck to place on this road, or null to clear it
	 */
	public synchronized void setTraffic(Truck traffic)
	{
		if (this.traffic == null)
		{
			if (traffic != null) block();
		}
		else // this.traffic != null
		{
			if (traffic == null)
			{
				assert blocking() == Thread.currentThread();
				this.traffic = null;
				observer.roadChanged();
				unblock();
				return;
			}
			else
			{
				block();
				assert this.traffic == null;
			}
		}

		this.traffic = traffic;
		observer.roadChanged();
	}

	/**
	 * Link this road to another one in the given direction.
	 * The direction must be one of the constants defined above.
	 *
	 * @param direction direction where to reach the other road
	 * @param to        the road the exits leads to
	 */
	public void setExit(int direction, Road to)
	{
		if (direction < 0 || direction >= DIRECTIONS) // sanity check
			throw new IllegalArgumentException("direction out of bounds");
		this.exits[direction] = to;
	}

	/**
	 * Get the road reachable in the given direction.
	 * The direction must be one of the constants defined above.
	 *
	 * @param direction direction to check
	 * @return The road reachable in the given direction, or null if none
	 */
	public Road getExit(int direction)
	{
		if (direction < 0 || direction >= DIRECTIONS) // sanity check
			throw new IllegalArgumentException("direction out of bounds");
		return this.exits[direction];
	}


}
