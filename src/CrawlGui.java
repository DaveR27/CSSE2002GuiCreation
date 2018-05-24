import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class CrawlGui extends javafx.application.Application{

    String mapToLoad;

    public void start(Stage stage){
        //Sets main window
        stage.setTitle("Crawl - Explore");

        //Button layout
        Button northButton = new Button("North");
        Button eastButton = new Button("East");
        Button southButton = new Button("South");
        Button westButton = new Button("West");

        //Utilities buttons
        Button lookButton = new Button("Look");
        Button examineButton = new Button("Examine");
        Button dropButton = new Button("Drop");
        Button takeButton = new Button("Take");
        Button fightButton = new Button("Fight");
        Button saveButton = new Button("Save");

        //Adds buttons to a Grid
        GridPane navGrid = new GridPane();
        GridPane utilGrid = new GridPane();
        navGrid.add(northButton,1,0);
        navGrid.add(eastButton, 2, 1);
        navGrid.add(southButton, 1, 3);
        navGrid.add(westButton, 0, 1);
        utilGrid.add(lookButton, 0, 0);
        utilGrid.add(examineButton,1,0);
        utilGrid.add(dropButton, 0, 1);
        utilGrid.add(takeButton, 1, 1);
        utilGrid.add(fightButton, 0, 2);
        utilGrid.add(saveButton, 0, 3);

        GridPane buttonPane = new GridPane();
        buttonPane.add(navGrid, 0,0);
        buttonPane.add(utilGrid, 0,1);

        //Sets Text Area
        TextArea mainText = new TextArea();
        mainText.setEditable(false);

        //Sets map Canvas
        Canvas mapCanvas = new Canvas();


        //Sets up main container to hold all the different components in.
        BorderPane mainPane = new BorderPane();
        mainPane.setRight(buttonPane);
        mainPane.setBottom(mainText);
        mainPane.bottomProperty();
        mainPane.setLeft(mapCanvas);

        Scene mainScene = new Scene(mainPane);
        stage.setScene(mainScene);
        stage.show();

    }

}