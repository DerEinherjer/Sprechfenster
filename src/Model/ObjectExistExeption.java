package Model;

/**
 * The main objectiv of this Exception is to get out the construcktor with
 * out creating an object.
 * @author deus
 */
public class ObjectExistExeption extends Exception 
{
	private Object object;
	
	public ObjectExistExeption(Object object) 
	{
		super("There is allready an object for this set of data.");
		this.object = object;
	}
	
	public Object getObject()
	{
		return object;
	}
}
