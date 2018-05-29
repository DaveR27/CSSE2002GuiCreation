import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Optional;


public class CrawlGui extends javafx.application.Application{

    Parameters mapToLoad;
    Cartographer mapDrawing;
    Map<Room, Pair> map;
    TextArea mainText;

    Button northButton;
    Button eastButton;
    Button southButton;
    Button westButton;
    Button lookButton;
    Button examineButton;
    Button dropButton;
    Button takeButton;
    Button fightButton;
    Button saveButton;

    public void start(Stage stage){
        //Sets main window
        stage.setTitle("Crawl - Explore");
        this.mapToLoad =  getParameters();

        //Button layout
        this.northButton = new Button("North");
        this.eastButton = new Button("East");
        this.southButton = new Button("South");
        this.westButton = new Button("West");

        //Utilities buttons
        this.lookButton = new Button("Look");
        this.examineButton = new Button("Examine");
        this.dropButton = new Button("Drop");
        this.takeButton = new Button("Take");
        this.fightButton = new Button("Fight");
        this.saveButton = new Button("Save");

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
        southButton.setOnAction((southEvent) ->
                this.moveExplorer("South"));
        eastButton.setOnAction((eastEvent) ->
                this.moveExplorer("East"));
        westButton.setOnAction((westEvent) ->
                this.moveExplorer("West"));


        //Gives action to lookButton
        lookButton.setOnAction((look) ->
                this.lookInRoom());
        examineButton.setOnAction((examinePush) ->
            this.examine());
        dropButton.setOnAction((dropping) ->
            this.dropButtonAction());
        fightButton.setOnAction((fight) ->
            this.fightButtonAction());
        takeButton.setOnAction((pickUp) ->
            this.takeActionButton());
        saveButton.setOnAction((save) ->
            this.saveActionButton());
        Scene mainScene = new Scene(mainPane);
        stage.setScene(mainScene);
        stage.show();

    }

    /**
     * Button action that allows the map to be saved, if the save is successful
     * it will print "Saved" to the game window, if not it will print "unable to
     * save".
     */
    private void saveActionButton(){
        TextInputDialog saveText = new TextInputDialog();
        saveText.setTitle("Save filename?");
        saveText.setGraphic(null);
        saveText.setHeaderText(null);
        Optional<String> saveMe = saveText.showAndWait();
        if(saveMe.isPresent()){ //Checks for a response from user
            if (MapIO.saveMap(this.mapDrawing.getStartRoom(),
                    saveText.getEditor().getText())){
                //Prints to game and saves
                this.mainText.setText(this.mainText.getText() + "\n" +
                "Saved");
                MapIO.saveMap(this.mapDrawing.getStartRoom(),
                        saveText.getEditor().getText());
            }
            else{
                //Prints that the game cannot be saved
                this.mainText.setText(this.mainText.getText() + "\n" +
                        "Unable to save");
            }
        }
        this.mainText.positionCaret(this.mainText.getLength());
    }

    /**
     * Method that is called when the take button is pressed, it will bring up
     * a TextInputDialog, it will then call take() on the String passed in by
     * the user in the text area.
     */
    private void takeActionButton(){
        Room playerRoom;
        Explorer player;

        if (findPlayer() != null) {
            playerRoom = (Room) findPlayer()[0];
            player = (Explorer) findPlayer()[1];
            TextInputDialog takeText = new TextInputDialog();
            takeText.setTitle("Take what?");
            takeText.setGraphic(null);
            takeText.setHeaderText(null);
            Optional<String> takeMe = takeText.showAndWait();
            if(takeMe.isPresent()){
                take(takeText.getEditor().getText(), playerRoom, player);
            }
        }
    }

    /**
     * Will search the contents of the room that the player is currently in and
     * will skip over any explorer objects in the search, but if the String
     * entered in by the user matches the short description of the a Thing in
     * that room it will check to see if it is a critter. If the thing is a
     * critter it will be checked to see if its alive, if it is alive the method
     * fails silently if it is dead it will be taken from the room and added
     * to the explorers inventory. If the thing isn't a critter is will
     * instantly be taken from the room and added to players inventory.
     *
     * @param textBox input into the dialog box by the user.
     * @param currentRoom the current room the explorer is in.
     * @param player the explorer object.
     */
    private void take(String textBox, Room currentRoom, Explorer player){
        for (Thing thing: currentRoom.getContents()){
            if (!(thing instanceof Explorer)){ // skipping explorer
                if (thing.getShortDescription().equals(textBox)){
                    if (thing instanceof Critter){
                        /*checks to see if critter is alive then fails silently
                        if it is
                         */
                        if (!((Critter) thing).isAlive()){
                            currentRoom.leave(thing);
                            player.add(thing);
                            this.mapDrawing.cleanCanvas();
                            this.mapDrawing.drawRoom();
                        }
                    }
                    // for when thing isn't a critter
                    else {
                        currentRoom.leave(thing);
                        player.add(thing);
                        this.mapDrawing.cleanCanvas();
                        this.mapDrawing.drawRoom();
                    }
                }
            }
        }
        this.mainText.positionCaret(this.mainText.getLength());
    }

    private void fightButtonAction(){
        Room playerRoom;
        Explorer player;

        if (findPlayer() != null) {
            playerRoom = (Room) findPlayer()[0];
            player = (Explorer) findPlayer()[1];
            TextInputDialog fightText = new TextInputDialog();
            fightText.setTitle("Item to drop?");
            fightText.setGraphic(null);
            fightText.setHeaderText(null);
            Optional<String> fightMe = fightText.showAndWait();
            if(fightMe.isPresent()){
                fighting(fightText.getEditor().getText(), playerRoom, player);
            }
        }
    }

    private void fighting(String textBox, Room currentRoom, Explorer player){
        for (Thing thing: currentRoom.getContents()){
            if (thing.getShortDescription().equals(textBox)){
                if (thing instanceof Critter){
                    player.fight((Mob) thing);
                    if(player.isAlive()){
                        this.mainText.setText(this.mainText.getText() + "\n" +
                        "You won");
                        this.mapDrawing.cleanCanvas();
                        this.mapDrawing.drawRoom();
                    }
                    else {
                        this.mainText.setText(this.mainText.getText() + "\n" +
                                "Game Over");
                        this.northButton.setDisable(true);
                        this.southButton.setDisable(true);
                        this.eastButton.setDisable(true);
                        this.westButton.setDisable(true);
                        this.dropButton.setDisable(true);
                        this.lookButton.setDisable(true);
                        this.examineButton.setDisable(true);
                        this.saveButton.setDisable(true);
                        this.fightButton.setDisable(true);
                        this.takeButton.setDisable(true);
                    }
                }
            }
        }
        this.mainText.positionCaret(this.mainText.getLength());
    }

    private void dropButtonAction(){
        Room playerRoom;
        Explorer player;

        if (findPlayer() != null){
            playerRoom = (Room) findPlayer()[0];
            player = (Explorer) findPlayer()[1];
            TextInputDialog dropText = new TextInputDialog();
            dropText.setTitle("Item to drop?");
            dropText.setGraphic(null);
            dropText.setHeaderText(null);
            Optional<String> droppingItem = dropText.showAndWait();
            if(droppingItem.isPresent()){
                drop(dropText.getEditor().getText(), playerRoom, player);
            }
        }
    }

    private void drop(String textBox, Room currentRoom, Explorer player){
        Boolean found = false;
        for(Thing things: player.getContents()){
            if (things.getShortDescription().equals(textBox)){
                player.drop(things);
                currentRoom.enter(things);
                this.mapDrawing.cleanCanvas();
                this.mapDrawing.drawRoom();
                found = true;
            }
        }
        if(!found){
            this.mainText.setText(this.mainText.getText() + "\n" + "Nothing " +
                    "found with that name");
        }
    }

    private void examine(){
        Room playerRoom;
        Explorer player;

        if (findPlayer() != null){
            playerRoom = (Room) findPlayer()[0];
            player = (Explorer) findPlayer()[1];
            TextInputDialog examineText = new TextInputDialog();
            examineText.setTitle("Examine What?");
            examineText.setHeaderText(null);
            examineText.setGraphic(null);
            Optional<String> findingItem = examineText.showAndWait();
            if (findingItem.isPresent()){
                examineFind(examineText.getEditor().getText(), playerRoom,
                        player);
            }
        }
    }

    private void examineFind(String item, Room currentRoom, Explorer player){
        Boolean notInInv = true;
        Boolean notInRoom = true;
        for (Thing things: player.getContents()){
            if (things.getShort().equals(item)){
                this.mainText.setText(this.mainText.getText() + "\n" +
                things.getShortDescription());
                notInInv = false;
            }
        }
        if (notInInv){
            for (Thing thing: currentRoom.getContents()){
                if (thing.getShort().equals(item)){
                    this.mainText.setText(this.mainText.getText() + "\n" +
                    thing.getDescription());
                    notInRoom = false;
                }
            }
        }
        if (notInInv && notInRoom){
            this.mainText.setText(this.mainText.getText() + "\n" +
                    "Nothing found with that name");
        }
        this.mainText.positionCaret(this.mainText.getLength());
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
                    playerInvWorth = playerInvWorth +
                            ((Lootable) thing).getValue();
                }
                this.mainText.setText(this.mainText.getText() + "\n"
                + " " + thing.getShort());
            }
            this.mainText.setText(this.mainText.getText() + "\n" +
            "worth " + String.valueOf(playerInvWorth) + " in total");
        }
        this.mainText.positionCaret(this.mainText.getLength());
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
                if (critterFight.wantsToFight(player) &&
                        critterFight.isAlive()) {
                    this.mainText.setText(this.mainText.getText() + "\n" +
                            "Something prevents you from leaving");
                }
            }
            if (critterFight == null){
                if (canExit == true && player != null && nextRoom != null) {
                    playerRoom.leave(player);
                    nextRoom.enter(player);
                    this.mainText.setText(this.mainText.getText() + "\n" +
                            "You enter the" + " " +
                            nextRoom.getDescription());
                    this.mapDrawing.cleanCanvas();
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
