package Model;

class ObjectExistExeption extends Exception 
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
