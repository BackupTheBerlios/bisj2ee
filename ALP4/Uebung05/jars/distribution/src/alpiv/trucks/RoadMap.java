package alpiv.trucks;

import java.awt.Canvas;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Label;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.Random;



/**
 * Objects of this class represent a map of connected roads.
 * This class contains factory methods to create certain scenario maps,
 * and a main method where test runs can be performed.
 */
public class RoadMap
    extends Canvas
    implements RoadObserver
{
    /** The names of the supported scenarios. */
    private static final String[] scenarios = new String[] {
        "Kreuzung", "InselA", "InselB", "Acht"
    };

    /** The size of image tiles for roads and traffic */
    private static final int TILESIZE = 40;

    /** Images for the different kinds of road (start, goal, regular). */
    private Image[] roadi = new Image[] {
        getToolkit().getImage(RoadMap.class.getResource("/alpiv/trucks/images/road.gif")),
        getToolkit().getImage(RoadMap.class.getResource("/alpiv/trucks/images/start.gif")),
        getToolkit().getImage(RoadMap.class.getResource("/alpiv/trucks/images/ziel.gif"))
    };

    /** Images for trucks heading in a specific direction. */
    private Image[] brummi = new Image[] {
        getToolkit().getImage(RoadMap.class.getResource("/alpiv/trucks/images/brummi-n.gif")),
        getToolkit().getImage(RoadMap.class.getResource("/alpiv/trucks/images/brummi-ne.gif")),
        getToolkit().getImage(RoadMap.class.getResource("/alpiv/trucks/images/brummi-e.gif")),
        getToolkit().getImage(RoadMap.class.getResource("/alpiv/trucks/images/brummi-se.gif")),
        getToolkit().getImage(RoadMap.class.getResource("/alpiv/trucks/images/brummi-s.gif")),
        getToolkit().getImage(RoadMap.class.getResource("/alpiv/trucks/images/brummi-sw.gif")),
        getToolkit().getImage(RoadMap.class.getResource("/alpiv/trucks/images/brummi-w.gif")),
        getToolkit().getImage(RoadMap.class.getResource("/alpiv/trucks/images/brummi-nw.gif"))
    };

    /**
     * The list of road sections this map contains.
     * Start sections must be listed at the beginning.
     */
    private Road[] roads;
    /** The number of start positions in the map. */
    private int starts = 0;

    /** The frame that contains the roadmap display. */
    private Frame frame;


    /**
     * Main constructor.
     * This creates the given scenario map.
     * @param scenario  the name of the scenario to create
     */
    public RoadMap(String scenario)
    {
        // create the given scenario map
        if (scenario.equals(scenarios[0]))
            this.roads = createCrossing(this);
        else
        if (scenario.equals(scenarios[1]))
            this.roads = createIslandA(this);
        else
        if (scenario.equals(scenarios[2]))
            this.roads = createIslandB(this);
        else
        if (scenario.equals(scenarios[3]))
            this.roads = createEight(this);
        else
            throw new RuntimeException("no such scenario");

        // determine the number of start positions in the map
        int r = 0;
        while (roads[r++].getType() == Road.START)
            starts++;

        // create a frame for the main display
        frame = new Frame();
        frame.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    frame.setVisible(false);
                    frame.dispose();
                    System.exit(0);
                }
            }
        );

        // add the main display canvas
        frame.add(this);

        // calculate the required size of the display
        int x = 0;
        int y = 0;
        for (int i=0; i<roads.length; i++)
        {
            if (roads[i].getX() > x) x = roads[i].getX();
            if (roads[i].getY() > y) y = roads[i].getY();
        }
        frame.setSize(TILESIZE*(x+2),TILESIZE*(y+2));

        // show time
        frame.setVisible(true);
    }

    static String[] getScenarios()
    {
        return scenarios;
    }


    /**
     * Create a hidden condition dialog with a quit button.
     * The dialog will display the given traffic condition,
     * and its button will cause the application to quit when pressed.
     * @param condition The message to show
     * @return a hidden dialog for the given message
     */
    Dialog createTrafficDialog(String condition)
    {
        Dialog jam = new Dialog(frame, "Traffic Condition", true);
        jam.add(new Label(condition), "Center");
        Button quit = new Button("Quit");
        quit.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    frame.setVisible(false);
                    frame.dispose();
                    System.exit(0);
                }
            }
        );
        jam.add(quit, "South");
        jam.pack();
        return jam;
    }


    /**
     * Get the number of start positions in the map.
     * @return the number of start positions.
     */
    public int getStarts()
    {
        return this.starts;
    }

    /**
     * Get the indicated start Road of the map.
     * @return a Road object of thype START, or null for no such number
     */
    public Road getStart(int index)
    {
        if (index >= starts)
            return null;
        else
            return roads[index];
    }


    /**
     * Draw the map in the main display canvas.
     * Lines indicate exits, icons represent the types of the road sections,
     * or the trucks moving on them in a specific direction.
     * @param g     the graphcis context to draw with
     */
    public void paint(Graphics g)
    {
        // exits
        for (int i=0; i < roads.length; i++)
        {
            for (int e=0; e<Road.DIRECTIONS; e++)
            {
                if (roads[i].getExit(e) != null)
                    g.drawLine(
                        (TILESIZE/2)+TILESIZE*roads[i].getX(),
                        (TILESIZE/2)+TILESIZE*roads[i].getY(),
                        (TILESIZE/2)+TILESIZE*(
                               roads[i].getX()+offsetX(e)),
                        (TILESIZE/2)+TILESIZE*(
                               roads[i].getY()+offsetY(e)));
            }
        }

        // contents
        Truck traffic = null;
        for (int i=0; i < roads.length; i++)
        {
            // traffic could change during drawing, get it only once
            traffic = roads[i].getTraffic();
            if (traffic == null)
                g.drawImage(roadi[roads[i].getType()],
                        TILESIZE*(roads[i].getX()),
                        TILESIZE*(roads[i].getY()), this);
            else
                g.drawImage(brummi[traffic.getDirection()],
                        TILESIZE*(roads[i].getX()),
                        TILESIZE*(roads[i].getY()), this);
        }
    }


    /**
     * Callback method for observing traffic changes.
     * This is called by road objects when trucks are placed on them
     * or removed again. It schedules a repainting of the display.
     */
    public void roadChanged()
    {
        this.repaint();
    }


    /**
     * A helper method to determine the X offset of the given direction.
     * This assumes a coordinate system with origin in the upper left corner.
     * @param dir   the requested direction
     */
    public static int offsetX(int dir)
    {
        if (dir >= Road.NORTHEAST && dir <= Road.SOUTHEAST) return 1;
        if (dir >= Road.SOUTHWEST && dir <= Road.NORTHWEST) return -1;
        return 0;
    }

    /**
     * A helper method to determine the Y offset of the given direction.
     * This assumes a coordinate system with origin in the upper left corner.
     * @param dir   the requested direction
     */
    public static int offsetY(int dir)
    {
        if (dir >= Road.SOUTHEAST && dir <= Road.SOUTHWEST) return 1;
        if (dir == Road.NORTHWEST ||
                dir >= Road.NORTH && dir <= Road.NORTHEAST) return -1;
        return 0;
    }


    /**
     * A helper method to bidirectionally link two road sections.
     * This is used in road map construction. An exit is added to the
     * starting road in the given direction, leading to the target road.
     * An exit in the opposite direction is added to the target road,
     * leading back to the start road. Also, the location coordinates
     * of the target road are set relative to the coordinates of the start
     * road, shifted by the appropriate X and Y offset of the direction.
     * @param source    the starting road
     * @param dir       the direction of the exit from the source road
     * @param dest      the target road reachable by the exit
     */
    protected static void link(Road source, int dir, Road dest)
    {
        source.setExit(dir, dest);
        dest.setExit(Road.oppositeDirection(dir), source);
        dest.setLocation(
            source.getX()+offsetX(dir), source.getY()+offsetY(dir));
    }

    /**
     * Sets up the Crossing map.
     *
     *    0   1   2   3   4
     * 0      AZ      BS
     *
     * 1  CS A3C1 C2 B1C3 CZ
     *
     * 2      A2      B2
     *
     * 3  BZ A1B4 B3
     *
     * 4      AS
     */
    private static Road[] createCrossing(RoadObserver observer)
    {
        Road[] roads = new Road[13];
        roads[0] = new Road("AS",observer,1,4,Road.START);
        roads[5] = new Road("A1B4",observer);
        roads[9] = new Road("A2",observer);
        roads[7] = new Road("A3C1",observer);
        roads[3] = new Road("AZ",observer,Road.GOAL);

        roads[1] = new Road("BS",observer,3,0,Road.START);
        roads[8] = new Road("B1C3",observer);
        roads[10] = new Road("B2",observer);
        roads[11] = new Road("B3",observer);
        // already have A1B4
        roads[4] = new Road("BZ",observer,Road.GOAL);

        roads[2] = new Road("CS",observer,0,1,Road.START);
        // already have A3C1
        roads[12] = new Road("C2",observer);
        // already have B1C3
        roads[6] = new Road("CZ",observer,Road.GOAL);

        link(roads[0], Road.NORTH, roads[5]);
        link(roads[5], Road.NORTH, roads[9]);
        link(roads[9], Road.NORTH, roads[7]);
        link(roads[7], Road.NORTH, roads[3]);

        link(roads[1], Road.SOUTH, roads[8]);
        link(roads[8], Road.SOUTH, roads[10]);
        link(roads[10], Road.SOUTHWEST, roads[11]);
        link(roads[11], Road.WEST, roads[5]);
        link(roads[5], Road.WEST, roads[4]);

        link(roads[2], Road.EAST, roads[7]);
        link(roads[7], Road.EAST, roads[12]);
        link(roads[12], Road.EAST, roads[8]);
        link(roads[8], Road.EAST, roads[6]);

        return roads;
    }


    /**
     * Sets up the Island map, variant A.
     *
     *    0   1   2   3   4
     * 0                  AZ
     *
     * 1          B2 B1A3 BS
     *
     * 2  AS A1B3 A2
     *
     * 3  BZ
     */
    private static Road[] createIslandA(RoadObserver observer)
    {
        Road[] roads = new Road[8];
        roads[0] = new Road("AS",observer,0,2,Road.START);
        roads[1] = new Road("BS",observer,4,1,Road.START);
        roads[2] = new Road("AZ",observer,Road.GOAL);
        roads[3] = new Road("BZ",observer,Road.GOAL);
        roads[4] = new Road("A1B3", observer);
        roads[5] = new Road("B1A3", observer);
        roads[6] = new Road("A2", observer);
        roads[7] = new Road("B2", observer);

        link(roads[0], Road.EAST, roads[4]);
        link(roads[4], Road.EAST, roads[6]);
        link(roads[6], Road.NORTHEAST, roads[5]);
        link(roads[5], Road.NORTHEAST, roads[2]);

        link(roads[1], Road.WEST, roads[5]);
        link(roads[5], Road.WEST, roads[7]);
        link(roads[7], Road.SOUTHWEST, roads[4]);
        link(roads[4], Road.SOUTHWEST, roads[3]);

        return roads;
    }


    /**
     * Sets up the Island map, variant B.
     *
     *    0   1   2   3   4
     * 0  AS      B2 B1A3 AZ
     *
     * 1  BZ A1B3 A2      BS
     */
    private static Road[] createIslandB(RoadObserver observer)
    {
        Road[] roads = new Road[8];
        roads[0] = new Road("AS",observer,0,0,Road.START);
        roads[1] = new Road("BS",observer,4,1,Road.START);
        roads[2] = new Road("AZ",observer,Road.GOAL);
        roads[3] = new Road("BZ",observer,Road.GOAL);
        roads[4] = new Road("A1B3", observer);
        roads[5] = new Road("B1A3", observer);
        roads[6] = new Road("A2", observer);
        roads[7] = new Road("B2", observer);

        link(roads[0], Road.SOUTHEAST, roads[4]);
        link(roads[4], Road.EAST, roads[6]);
        link(roads[6], Road.NORTHEAST, roads[5]);
        link(roads[5], Road.EAST, roads[2]);

        link(roads[1], Road.NORTHWEST, roads[5]);
        link(roads[5], Road.WEST, roads[7]);
        link(roads[7], Road.SOUTHWEST, roads[4]);
        link(roads[4], Road.WEST, roads[3]);

        return roads;
    }


    /**
     * Sets up the figure Eight map.
     *
     *    0   1   2   3   4   5   6
     * 0      L2  L1      R1  R2
     *
     * 1  L3          M1  -S      R3
     *
     * 2  L4      Z-  M2          R4
     *
     * 3      L5  L3      R6  R5
     *
     */
    private static Road[] createEight(RoadObserver observer)
    {
        Road[] roads = new Road[16];
        roads[0] = new Road("S",observer,4,1,Road.START);
        roads[1] = new Road("Z",observer,Road.GOAL);
        roads[2] = new Road("M1", observer);
        roads[3] = new Road("M2", observer);

        roads[4] = new Road("L1", observer);
        roads[5] = new Road("L2", observer);
        roads[6] = new Road("L3", observer);
        roads[7] = new Road("L4", observer);
        roads[8] = new Road("L5", observer);
        roads[9] = new Road("L6", observer);

        roads[10] = new Road("R1", observer);
        roads[11] = new Road("R2", observer);
        roads[12] = new Road("R3", observer);
        roads[13] = new Road("R4", observer);
        roads[14] = new Road("R5", observer);
        roads[15] = new Road("R6", observer);

        link(roads[0], Road.WEST, roads[2]);

        link(roads[2], Road.NORTHWEST, roads[4]);
        link(roads[4], Road.WEST, roads[5]);
        link(roads[5], Road.SOUTHWEST, roads[6]);
        link(roads[6], Road.SOUTH, roads[7]);
        link(roads[7], Road.SOUTHEAST, roads[8]);
        link(roads[8], Road.EAST, roads[9]);
        link(roads[9], Road.NORTHEAST, roads[3]);

        link(roads[3], Road.NORTH, roads[2]);

        link(roads[2], Road.NORTHEAST, roads[10]);
        link(roads[10], Road.EAST, roads[11]);
        link(roads[11], Road.SOUTHEAST, roads[12]);
        link(roads[12], Road.SOUTH, roads[13]);
        link(roads[13], Road.SOUTHWEST, roads[14]);
        link(roads[14], Road.WEST, roads[15]);
        link(roads[15], Road.NORTHWEST, roads[3]);

        link(roads[3], Road.WEST, roads[1]);

        return roads;
    }

    /**
     * A helper method to find a valid exit from the given road section.
     * This raises a RuntimeException if there are no exits.
     * @param from  the road to check
     * @return the direction of the next valid exit
     */
    static int findDirection(Road from)
    {
        for (int i=0; i<Road.DIRECTIONS; i++)
            if (from.getExit(i) != null)
                return i;
        throw new RuntimeException("no exits on "+from.getName());
    }



}
