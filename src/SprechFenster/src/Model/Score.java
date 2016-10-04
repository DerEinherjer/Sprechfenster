package Model;

public class Score implements iScore
{
	private Fencer fencer;
	
	private int wins = 0;
	private int hits = 0;
	private int gotHit = 0;
	
	Score(Fencer f)
	{
		this.fencer = f;
	}
	
	void addWin()
	{
		wins++;
	}
	
	void subWin()
	{
		wins--;
	}
	
	void addHits(int add)
	{
		hits += add;
	}
	
	void addGotHit(int add)
	{
		gotHit += add;
	}
	
	public iFencer getFencer()
	{
		return fencer;
	}
	
	public int getWins()
	{
		return wins;
	}
	
	public int getHits()
	{
		return hits;
	}
	
	public int getGotHit()
	{
		return gotHit;
	}
}
