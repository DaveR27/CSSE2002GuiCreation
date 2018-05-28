import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Map;


public class CrawlGui extends javafx.application.Application{

    Parameters mapToLoad;
    Cartographer mapDrawing;
    Map<Room, Pair> map;
    TextArea mainText;

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
        this.mainText = new TextArea();
        this.mainText.setEditable(false);

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

        //Tells you the starting room
        this.mainText.setText("You find yourself in" + " " +
                this.mapDrawing.getStartRoom().getDescription());

        //Sets up main container to hold all the different components in.
        BorderPane mainPane = new BorderPane();
        mainPane.setRight(buttonPane);
        mainPane.setBottom(mainText);
        mainPane.bottomProperty();
        mainPane.setLeft(mapPane);


        /*
        Gives actions to all the move buttons, depending on what button is
        pressed will change what argument is given to moveExplorer()
         */
        this.map = this.mapDrawing.getMap();
        northButton.setOnAction((northEvent) ->
                this.moveExplorer("North"));
        southButton.setOnAction((northEvent) ->
                this.moveExplorer("South"));
        eastButton.setOnAction((northEvent) ->
                this.moveExplorer("East"));
        westButton.setOnAction((northEvent) ->
                this.moveExplorer("West"));


        //Gives action to lookButton
        lookButton.setOnAction((look) ->
                this.lookInRoom());
        Scene mainScene = new Scene(mainPane);
        stage.setScene(mainScene);
        stage.show();

    }

    private void examine(){
        Room playerRoom;
        Explorer player;
        Stage whatToFind;
        Button ok;
        Button cancel;
        TextArea finding;
        BorderPane settingBox;

        if (findPlayer() != null){
            playerRoom = (Room) findPlayer()[0];
            player = (Explorer) findPlayer()[1];
            settingBox = new BorderPane();
            whatToFind = new Stage();
            whatToFind.setTitle("Examine What?");
            ok = new Button ("OK");
            cancel = new Button("Cancel");
            Pane buttonPane = new Pane(cancel, ok);
            finding = new TextArea();
            finding.
            settingBox.setCenter(finding);
            settingBox.setBottom(buttonPane);
            Scene scene = new Scene(settingBox);
            whatToFind.setScene(scene);
        }
    }

    /**
     * Looks in current player room and displays the room name and
     * contents of the room. It then displays what the player is carrying
     * and how much everything the player is carrying is worth.
     */
    private void lookInRoom(){
        Room playerRoom;
        Explorer player;
        double playerInvWorth = 0;

        if (findPlayer() != null){
            playerRoom = (Room) findPlayer()[0];
            player = (Explorer) findPlayer()[1];
            this.mainText.setText(mainText.getText() + "\n" +
            playerRoom.getDescription() + " " + "- you see:");
            for (Thing things: playerRoom.getContents()){
                this.mainText.setText(this.mainText.getText() + "\n"+
                " " + things.getShort());
            }
            this.mainText.setText(this.mainText.getText() + "\n" +
            "You are carrying:");
            for (Thing thing: player.getContents()){
                if (thing instanceof Lootable){
                    playerInvWorth += ((Lootable) thing).getValue();
                }
                this.mainText.setText(this.mainText.getText() + "\n"
                + " " + thing.getShort());
            }
            this.mainText.setText(this.mainText.getText() + "\n" +
            "worth " + String.valueOf(playerInvWorth) + " in total");
            this.mainText.positionCaret(this.mainText.getLength());
        }
    }

    /**
     * Moves the explorer object through Available exits
     *
     * @param leavingFrom Which exit to leave from
     */
    private void moveExplorer(String leavingFrom){
        Boolean canExit = false;
        Critter critterFight = null;
        Room nextRoom = null;
        Room playerRoom;
        Explorer player;
        if (findPlayer() != null) {
            playerRoom = (Room) findPlayer()[0];
            player = (Explorer) findPlayer()[1];
            if (playerRoom != null) {
                for (Map.Entry<String, Room> exit :
                        playerRoom.getExits().entrySet()) {
                    if (exit.getKey().equals(leavingFrom)) {
                        canExit = true;
                        nextRoom = exit.getValue();
                    }
                }
                if (canExit != true){
                    this.mainText.setText(this.mainText.getText() + "\n" +
                            "No door that way");
                }
            }
            for (Thing things: playerRoom.getContents()){
                if (things instanceof Critter){
                    critterFight = (Critter) things;
                }
            }
            if (critterFight != null){
                if (critterFight.wantsToFight(player)) {
                    this.mainText.setText(this.mainText.getText() + "\n" +
                            "Something prevents you from leaving");
                }
            }
            else {
                if (canExit == true && player != null && nextRoom != null) {
                    playerRoom.leave(player);
                    nextRoom.enter(player);
                    this.mainText.setText(this.mainText.getText() + "\n" +
                            "You enter the" + " " +
                            nextRoom.getDescription());
                    //this.mapDrawing.cleanCanvas();
                    this.mapDrawing.drawRoom();
                }
            }
        }
        this.mainText.positionCaret(this.mainText.getLength());
    }

    /**
     * Finds where ever the explorer object is in a map and what room they are
     * in.
     *
     * @return Object[0] = player's room location, Object[1] = Explorer object,
     *         if explorer not found null is returned.
     */
    private Object[] findPlayer(){
        Room playerRoom;
        Explorer player;

        for (Room rooms: this.map.keySet()){
            for (Thing things: rooms.getContents()){
                if (things instanceof Explorer){
                    playerRoom = rooms;
                    player = (Explorer) things;
                    Object[] playerLocation = {playerRoom, player};
                    return playerLocation;
                }
            }
        }
        return null;
    }

public static void main(String[] args){
        launch(args);
}

}
