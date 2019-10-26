package model;

import java.util.List;
import model.rounds.iFinalsMatch;
import model.rounds.iQualificationMatch;

public class Test
{

  public static void main(String args[])
  {
    try
    {

      iTournament t = new Tournament("Der ultimative Test");
      t.setFinalRounds(2);
      t.setGroups(2);
      t.setLanes(2);

      iFencer fencers[] = new iFencer[26];

      for (char i = 'A'; i <= 'Z'; i++)
      {
        fencers[i - 'A'] = new Fencer(i + "", i + "");
        fencers[i - 'A'].setBirthday("Geburtstag");
        if (!fencers[i - 'A'].getBirthday().equals("Geburtstag"))
        {
          System.out.println("Geburtstag wird nicht richtig behandelt");
        }
        t.addParticipant(fencers[i - 'A']);
      }

      //t.createPreliminaryTiming();
      printSchedule(t.getQualificationMatchSchedule());

      int count = t.getQualificationMatchCount();
      t.getAllQualificationMatches().get(0).delete();
      if (count - 1 == t.getQualificationMatchCount())
      {
        System.out.println("Turnier findet ein Prelim weniger");
      } else
      {
        System.out.println("Turnier findet gelöschtes Prelim immer noch.");
      }

      for (iQualificationMatch p : t.getAllQualificationMatches())
      {
        List<iFencer> tmp = p.getFencer();
        iFencer f1 = tmp.get(0);
        iFencer f2 = tmp.get(1);

        if (f1.getName().charAt(0) > f2.getName().charAt(0))
        {
          p.setPoints(f2, 2);
          p.setPoints(f1, 1);
          p.setFinished(true);
        } else
        {
          p.setPoints(f1, 2);
          p.setPoints(f2, 1);
          p.setFinished(true);
        }
      }

      for (iScore s : t.getQualifcationPhaseScores())
      {
        System.out.println(s.toString());
      }

      System.out.println("Vorrunde wird beendet");
      t.startFinalsPhase();

      System.out.println("Vorrunde wurde beendet");

      System.out.println("Anzahl Finalrunden: " + t.getAllFinalsMatches().size());

      boolean fertig = false;
      while (!fertig)
      {
        fertig = true;
        for (iFinalsMatch f : t.getAllFinalsMatches())
        {
          if (!f.isFinished() && f.getFencer().size() == 2)
          {
            fertig = false;
            iFencer f1 = f.getFencer().get(0);
            iFencer f2 = f.getFencer().get(1);
            if (f1.getName().charAt(0) > f2.getName().charAt(0))
            {
              f.setPoints(f2, 2);
              f.setPoints(f1, 1);
              f.setFinished(true);
            } else
            {
              f.setPoints(f1, 2);
              f.setPoints(f2, 1);
              f.setFinished(true);
            }
          }
        }
      }

    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  static void printSchedule(iQualificationMatch schedule[][]) throws ObjectDeprecatedException
  {
    for (int x = 0; x < schedule.length; x++)
    {
      String line = "";
      for (int y = 0; y < schedule[0].length; y++)
      {
        if (y != 0)
        {
          line += " | ";
        }
        if (schedule[x][y] != null)
        {
          line += schedule[x][y].getFencer().get(0).getFamilyName() + schedule[x][y].getFencer().get(1).getFamilyName();
        }
      }
      System.out.println(line);
    }
  }
}
