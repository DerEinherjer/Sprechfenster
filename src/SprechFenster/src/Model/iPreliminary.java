package Model;

import java.util.List;

public interface iPreliminary 
{	
	public void initTurnamentID(int id);
	public void initGroup(int group);
	public void initFencer1(Fencer f);
	public void initFencer2(Fencer f);
	
	public int getGroup();
	public List<Fencer> getFencer();
}
