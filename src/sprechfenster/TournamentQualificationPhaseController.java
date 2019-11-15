/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import model.ObjectDeprecatedException;
import model.iTournament;
import model.rounds.iQualificationMatch;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class TournamentQualificationPhaseController implements Initializable, Observer
{

  @FXML
  VBox GroupsBox;
  @FXML
  VBox FightsPerGroupBox;
  @FXML
  VBox FightsPerLaneBox;

  @FXML
  Button CreateQualificationRoundsButton1;
  @FXML
  Button CreateQualificationRoundsButton2;
  @FXML
  Button PrintGroupsViewButton;
  @FXML
  Button PrintLanesViewButton;

  private iTournament Tournament;
  private final ArrayList<GroupTableController> GroupControllers = new ArrayList<GroupTableController>();
  private final ArrayList<QualificationFightTableController> FightsPerGroupControllers = new ArrayList<QualificationFightTableController>();
  private final ArrayList<QualificationFightTableController> FightsPerLaneControllers = new ArrayList<QualificationFightTableController>();

  /**
   * Initializes the groupController class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb)
  {
  }

  public void SetTournament(iTournament tournament)
  {
    Tournament = tournament;
    UpdateData();
  }

  @FXML
  private void HandleCreateQualificationRoundsButtonAction(ActionEvent event)
  {
    if (Tournament != null)
    {
      try
      {
        Tournament.startQualificationPhase();
        UpdateData();
      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  @FXML
  private void HandlePrintGroupsViewButtonAction(ActionEvent event)
  {
    printTablesForFights(FightsPerGroupControllers);
  }

  @FXML
  private void HandlePrintLanesViewButtonAction(ActionEvent event)
  {
    printTablesForFights(FightsPerLaneControllers);
  }

  private void printTablesForFights(final ArrayList<QualificationFightTableController> tableControllers)
  {
    Printer printer = Printer.getDefaultPrinter(); //get the default printer
    PageLayout layout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
    PrinterJob job = PrinterJob.createPrinterJob();
    job.getJobSettings().setPageLayout(layout);
    if (job.showPrintDialog(GroupsBox.getScene().getWindow()))
    {
      for (QualificationFightTableController controller : tableControllers)
      {
        job.getJobSettings().jobNameProperty().set(controller.GetTableTitle());
        PrintOneTable(controller.GetTableViewForPrinting(), job);
        job.endJob();
        job = PrinterJob.createPrinterJob();
        job.getJobSettings().setPageLayout(layout);
      }
    }
  }

  private void PrintOneTable(TableView table, PrinterJob job)
  {
    PageLayout layout = job.getJobSettings().getPageLayout();
    double pagePrintableWidth = layout.getPrintableWidth();
    double pagePrintableHeight = layout.getPrintableHeight();

    table.prefHeightProperty().bind(Bindings.size(table.getItems()).multiply(35));
    table.minHeightProperty().bind(table.prefHeightProperty());
    table.maxHeightProperty().bind(table.prefHeightProperty());

    double scaleX = pagePrintableWidth / table.getBoundsInParent().getWidth();
    double scaleY = scaleX;
    double localScale = scaleX;
    double numberOfPages = Math.ceil((table.getPrefHeight() * localScale) / pagePrintableHeight);

    ObservableList<Transform> tableTransforms = table.getTransforms();
    tableTransforms.add(new Scale(scaleX, (scaleY)));
    tableTransforms.add(new Translate(0, 0));
    Translate gridTransform = new Translate();
    table.getTransforms().add(gridTransform);

    for (int i = 0; i < numberOfPages; i++)
    {
      gridTransform.setY(-i * (pagePrintableHeight / localScale));
      job.printPage(layout, table);
    }
    //remove the three added transforms
    tableTransforms.remove(tableTransforms.size() - 3, tableTransforms.size());
  }

  private void UpdateData()
  {
    GroupControllers.clear();
    FightsPerGroupControllers.clear();
    FightsPerLaneControllers.clear();
    FightsPerGroupBox.getChildren().clear();
    FightsPerLaneBox.getChildren().clear();
    GroupsBox.getChildren().clear();
    if (Tournament != null)
    {
      try
      {
        CreateQualificationRoundsButton1.setDisable(GUIUtilities.IsTournamentStarted(Tournament));
        CreateQualificationRoundsButton2.setDisable(GUIUtilities.IsTournamentStarted(Tournament));

        if (!Tournament.isPreparingPhase())
        {
          List<iQualificationMatch> qualificationFights = Tournament.getAllQualificationMatches();
          if (qualificationFights != null)
          {
            try
            {
              for (int groupNumber = 1; groupNumber <= Tournament.getGroups(); groupNumber++)
              {

                Node groupTable = GUIUtilities.LoadAsNode(this.getClass(), "GroupTable.fxml");
                GroupTableController groupController = GUIUtilities.GetLoader().<GroupTableController>getController();
                groupController.SetGroupName("Gruppe " + Integer.toString(groupNumber));
                groupController.SetTournament(Tournament);
                groupController.SetPhase(GroupTableController.TournamentPhase.QualificationPhase);
                GroupControllers.add(groupController);
                GroupsBox.getChildren().add(groupTable);

                QualificationFightTableController fightController = CreateFightTable(FightsPerGroupBox);
                fightController.SetGroupNumber(groupNumber);
                FightsPerGroupControllers.add(fightController);
              }
              for (int laneNumber = 1; laneNumber <= Tournament.getLanes(); laneNumber++)
              {
                QualificationFightTableController fightController = CreateFightTable(FightsPerLaneBox);
                fightController.SetLaneNumber(laneNumber);
                FightsPerLaneControllers.add(fightController);
              }
            } catch (IOException ex)
            {
              LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }
            qualificationFights.sort((a, b)
                    ->
            {
              try
              {
                return Integer.compare(a.getRound(), b.getRound());
              } catch (ObjectDeprecatedException ex)
              {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
                return -1;
              }
            });
            for (QualificationFightTableController fightController : FightsPerLaneControllers)
            {
              fightController.SetFights(qualificationFights);
            }
            for (QualificationFightTableController fightController : FightsPerGroupControllers)
            {
              fightController.SetFights(qualificationFights);
            }
            for (iQualificationMatch qualificationFight : qualificationFights)
            {
              int groupNumber;
              try
              {
                groupNumber = qualificationFight.getQualificationGroup();
              } catch (ObjectDeprecatedException ex)
              {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
                groupNumber = Integer.MAX_VALUE;
              }
              if (groupNumber <= GroupControllers.size() && groupNumber >= 1)
              {
                GroupTableController groupController = GroupControllers.get(groupNumber - 1);
                try
                {
                  groupController.AddFencers(qualificationFight.getFencer());
                } catch (ObjectDeprecatedException ex)
                {
                  LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
                }
              }
            }
          }
        }
        if (Tournament.isFinalsPhase())
        {
          FightsPerGroupBox.setDisable(true);
          FightsPerLaneBox.setDisable(true);
        }
      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  private QualificationFightTableController CreateFightTable(VBox parent)
  {
    try
    {
      Node fightTable = GUIUtilities.LoadAsNode(this.getClass(), "QualificationFightTable.fxml");
      QualificationFightTableController fightController = GUIUtilities.GetLoader().<QualificationFightTableController>getController();
      fightController.SetTournament(Tournament);
      parent.getChildren().add(fightTable);
      return fightController;
    } catch (IOException ex)
    {
      LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
    }
    return null;
  }

  @Override
  public void update(Observable o, Object o1)
  {
    UpdateData();
  }
}
