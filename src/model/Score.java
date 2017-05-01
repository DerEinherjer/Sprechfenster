package model;

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

    public int getHitDifference() 
    {
        return hits-gotHit;
    }

    public int compareTo(iScore o) 
    {
        if(wins<o.getWins())
                return 1;
        else if(wins > o.getWins())
                return -1;
        else
        {
            if(hits<o.getHits())
                return 1;
            else if(hits>o.getHits())
                return -1;
            else
            {
                if(hits-gotHit<o.getHits()-o.getGotHit())
                        return 1;
                else if(hits-gotHit>o.getHits()-o.getGotHit())
                        return -1;
                else
                        return 0;
            }
        }
    }
        
    public String toString()
    {
        return fencer.getFullName()+" | "+getWins()+" | "+getHits();
    }   
}
