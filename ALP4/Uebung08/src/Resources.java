
import java.util.Vector;

/**
 * Abstrakte Basisklasse. Request und Release werden je nach Variante von Signal in den Unterklassen
 * implementiert. Die Basisklasse ernthält einen Vector, in dem die anstehenden Releases in FIFO
 * Reihenfolge abgelegt werden. Da die Eintrittsbedingungen für Release und Request in allen Unterklassen
 * gleich sind, werden sie bereits hier definiert und in den Unterklasssen verwendet.
 */
public abstract class /*MONITOR*/ Resources
{
	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Instanzvariablen                  |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	final Vector<ResourceRequest> requests = new Vector<ResourceRequest>();

	final int max = 100;
	int available = max;

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                 Abstrakte Methoden                |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	public abstract void request(int num);

	public abstract void release(int num);

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                     Methoden                      |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	protected ResourceRequest nextRequest()
	{
		if (requests.size() == 0) return null;

		ResourceRequest result = requests.elementAt(0);

		requests.removeElementAt(0);

		return result;
	}

	protected void PRECONDITION_request(int num)
	{
		if (num < 0)
			throw new IllegalArgumentException("num must not be negative");
		if (num > max)
			throw new IllegalArgumentException("num must not be greater than maximum of " +
			                                   "available resources");
	}

	protected void PRECONDITION_release(int num)
	{
		if (num < 0)
			throw new IllegalArgumentException("num must not be negative");
		if (num + available > max)
			throw new IllegalArgumentException("num must not be greater than" +
			                                   "max - available.");
	}

	//  | = - = - = - = - = - /-||=||-\ - = - = - = - = - = |   \\
	//  |                   Inner Classes                   |   \\
	//  | = - = - = - = - = - \-||=||-/ - = - = - = - = - = |   \\

	class ResourceRequest
	{
		public final int claim;
		public final Event event = new Event();

		public ResourceRequest(int claim)
		{
			this.claim = claim;
		}
	}
}
