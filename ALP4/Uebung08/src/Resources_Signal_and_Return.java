
public class Resources_Signal_and_Return
extends Resources
{
	public synchronized void request(int num)
	{
		PRECONDITION_request(num);

		if (num <= available)
		{
			available -= num;
			return;
		}

		ResourceRequest newRequest = new ResourceRequest(num);
		requests.add(newRequest);

		newRequest.event.WAIT();
	}

	public final synchronized void release(int num)
	{
		PRECONDITION_release(num);

		available += num;

		if (requests.size() == 0) return;

		int i = 0;
		int count = 0;

		while (count <= available)
		{
			ResourceRequest request = requests.elementAt(i);
			count += request.claim;
			i++;
		}

		recursiveRelease(i - 1);
	}

	private void recursiveRelease(int i)
	{
		if (i < 0) return;

		recursiveRelease(i - 1);

		requests.elementAt(i).event.SIGNAL();
	}
}
