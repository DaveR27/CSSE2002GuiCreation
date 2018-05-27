import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Map;


public class CrawlGui extends javafx.application.Application{

    Parameters mapToLoad;
    Cartographer mapDrawing;
    Map<Room, Pair> map;

    public void start(Stage stage){
        //Sets main window
        stage.setTitle("Crawl - Explore");
        this.mapToLoad =  getParameters();

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

        //Makes A Cartographer
        BorderPane mapPane = new BorderPane();
        Canvas mapDrawing = new Canvas(500,500);
        mapPane.setCenter(mapDrawing);


        Object[] mapToDraw = mapToLoad.getRaw().toArray();
        if (mapToDraw.length == 0 ){
            System.err.println("Usage: java CrawlGui mapname");
            System.exit(1);
        }
        this.mapDrawing = new Cartographer((String) mapToDraw[0],mapDrawing);

        //Sets up main container to hold all the different components in.
        BorderPane mainPane = new BorderPane();
        mainPane.setRight(buttonPane);
        mainPane.setBottom(mainText);
        mainPane.bottomProperty();
        mainPane.setLeft(mapPane);

        this.map = this.mapDrawing.getMap();
        northButton.setOnAction((northEvent) ->
                this.moveExplorer("North"));
        southButton.setOnAction((northEvent) ->
                this.moveExplorer("South"));
        eastButton.setOnAction((northEvent) ->
                this.moveExplorer("East"));
        westButton.setOnAction((northEvent) ->
                this.moveExplorer("West"));

        Scene mainScene = new Scene(mainPane);
        stage.setScene(mainScene);
        stage.show();

    }

    private void moveExplorer(String leavingFrom){
        Room playerRoom = null;
        Explorer player = null;
        Boolean canExit = false;
        Room nextRoom = null;
        for (Room rooms: this.map.keySet()){
            for (Thing things: rooms.getContents()){
                if (things instanceof Explorer){
                    playerRoom = rooms;
                    player = (Explorer) things;
                }
            }
        }
        if (playerRoom != null){
            for (Map.Entry<String, Room> exit:
                    playerRoom.getExits().entrySet()){
                if (exit.getKey().equals(leavingFrom)){
                    canExit = true;
                    nextRoom = exit.getValue();
                }
            }
        }
        if (canExit == true && player != null && nextRoom != null){
            playerRoom.leave(player);
            nextRoom.enter(player);
            this.mapDrawing.drawRoom();
        }
    }

public static void main(String[] args){
        launch(args);
}

}
