/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import model.rounds.TournamentMatch;

/**
 *
 * @author Stefan
 */
public class Score extends Observable implements iScore, Observer, Comparable
{

  private final Fencer fencer;

  private int wins = 0;
  private int hits = 0;
  private int gotHit = 0;

  List<TournamentMatch> matches = new ArrayList<>();

  protected Score(Fencer f)
  {
    this.fencer = f;
  }

  public void addMatch(TournamentMatch r)
  {
    if (!r.isFencerInMatch(fencer))
    {
      return;
    }

    matches.add(r);
    r.addObserver(this);
    update(null, EventPayload.Type.valueChanged);
  }

  public void removeMatch(TournamentMatch r)
  {
    matches.remove(r);
    r.deleteObserver(this);
    update(null, EventPayload.Type.valueChanged);
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

  public iFencer getFencer()
  {
    return fencer;
  }

  @Override
  public void update(Observable o, Object arg)
  {
    wins = 0;
    hits = 0;
    gotHit = 0;

    for (TournamentMatch r : matches)
    {
      try
      {
        if (r.isFinished())
        {
          iFencer winner = r.getWinner();
          if (winner != null && winner.equals(fencer))
          {
            wins++;
          }

          hits += r.getPoints(fencer);
          gotHit += r.getOpponentPoints(fencer);
        }
      } catch (ObjectDeprecatedException e)
      {
        //Depricated object wasen't removed properply; adjust and ignor
        matches.remove(r);
      }
    }

    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public int compareTo(Object o)
  {
    Score tmp = (Score) o;
    if (this.wins > tmp.getWins())
    {
      return 1;
    }
    if (this.wins < tmp.getWins())
    {
      return -1;
    }
    if (this.hits > tmp.getHits())
    {
      return 1;
    }
    if (this.hits < tmp.getHits())
    {
      return -1;
    }
    if (this.gotHit > tmp.getGotHit())
    {
      return 1;
    }
    if (this.gotHit < tmp.getGotHit())
    {
      return -1;
    }
    return 0;
  }

}
