/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.DBConnection.DBBaseClass;

/**
 *
 * @author Stefan
 */
public class MainApplication extends Application
{

  @Override
  public void start(Stage stage) throws Exception
  {
    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/MainFXML.fxml"));
    Scene scene = new Scene(root);
    stage.setTitle("Sprechfenster");
    stage.setScene(scene);
    stage.show();
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args)
  {
    try
    {
      File configFile = new File("dist/SprechFensterLogging.config");
      if (!configFile.exists())
      {
        configFile = new File("SprechFensterLogging.config");
      }
      FileInputStream configStream = new FileInputStream(configFile);
      LogManager.getLogManager().readConfiguration(configStream);
      LoggingUtilities.LOGGER.log(Level.INFO, "Logging configuration file loaded");
      System.out.println(System.getProperty("java.io.tmpdir"));
      //rebind stdout/stderr to logger
      LoggingOutputStream los;
      los = new LoggingOutputStream(LoggingUtilities.LOGGER, StdOutErrLevel.STDOUT);
      System.setOut(new PrintStream(los, true));
      los = new LoggingOutputStream(LoggingUtilities.LOGGER, StdOutErrLevel.STDERR);
      System.setErr(new PrintStream(los, true));

    } catch (IOException ex)
    {
      ex.printStackTrace();
      System.out.println("WARNING: Could not open configuration file");
      System.out.println("WARNING: Logging not configured (console output only)");
    }
    LoggingUtilities.LOGGER.log(Level.INFO, "Starting SprechFenster");
    DBBaseClass.InitDatabase();
    launch(args);
  }

}
