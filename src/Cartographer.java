import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.Map;

public class Cartographer extends javafx.scene.canvas.Canvas {
    private Object[] loadInformation;
    private Player player;
    private BoundsMapper mapBounds;
    private Room startRoom;
    private Canvas mapDrawing;

    private GraphicsContext graphic;

    //Sets up Drawing
    private double dotX;
    private double dotY;
    private float squareSize = 50;

    /**
     * Creates a Cartographer object that draws a picture from the file
     * given in CrawlGui.
     *
     * @param map The location of the map that is going to be drawn.
     * @param canvas The canvas that the map is going to be drawn to.
     */
    public Cartographer(String map, Canvas canvas){
        this.loadInformation = MapIO.loadMap(map);
        this.mapDrawing = canvas;
        this.graphic = this.mapDrawing.getGraphicsContext2D();
        if (this.loadInformation.equals(null)){
            System.err.println("Unable to Load file");
            System.exit(2);
        }
        this.player = (Player) loadInformation[0];
        this.startRoom = (Room) loadInformation[1];
        this.startRoom.enter(this.player);
        this.mapBounds = new BoundsMapper(startRoom);
        this.mapBounds.walk();

        drawRoom();
    }

    /**
     * Gets the start room for the current map.
     *
     * @return the start room for the current map.
     */
    public Room getStartRoom(){
        return this.startRoom;
    }

    /**
     * Wipes the canvas clean so it can be redrawn on.
     */
    public void cleanCanvas(){
        this.graphic.clearRect(0,0, this.mapDrawing.getWidth(),
                this.mapDrawing.getHeight());
    }

    /**
     * Get method that gets the coordinates of the map.
     *
     * @return The Rooms and where the are located compared to the
     *         origin(start room)
     */
    public Map<Room, Pair> getMap(){
        return this.mapBounds.coords;
    }

    /**
     * Method that redraws the map on the canvas. This method finds the
     * middle of the canvas and will put the start room there, from there
     * everything is drawn.
     */
    public void drawRoom(){
        this.dotX = this.mapDrawing.getWidth()/2;
        this.dotY = this.mapDrawing.getHeight()/2;
        //Draws the square for the start room.
        for (Pair coords: this.mapBounds.coords.values()) {
            if (coords.x == 0 && coords.y == 0){
                graphic.strokeRect(this.dotX, this.dotY, this.squareSize,
                        this.squareSize);
            }
            else {
                graphic.strokeRect(this.dotX + coords.x*this.squareSize,
                        this.dotY +coords.y*this.squareSize, this.squareSize,
                        this.squareSize);
            }
        }
        //Draws all the squares for the rest of the map
        for (Map.Entry<Room, Pair> roomPairEntry:
                this.mapBounds.coords.entrySet()){
            for(String exit: roomPairEntry.getKey().getExits().keySet()){
               this.roomDetailsDrawer(exit, roomPairEntry.getKey(),
                       roomPairEntry.getValue());
            }
        }
    }

    /**
     * This method draws the contents and the exits for all the rooms within
     * the map file. Exits are indicated by a small line on the edge of the
     * square where an exit is located. Contents a room is drawn as follows:
     * explorer: "@" in top left
     * treasure: "$" in top right
     * alive critter: "M" bottom left
     * dead critter: "m" bottom right
     *
     * @param exit
     * @param room
     * @param coOrdinates
     */
    private void roomDetailsDrawer(String exit, Room room, Pair coOrdinates){
        //Checks for origin as it is a special case
        if (coOrdinates.x == 0 && coOrdinates.y == 0){
            this.startRoomDrawer(exit, room);
        }
        /*
        Checks the contents of all the rooms and draws the correct icons
        depending and on Thing is currently being iterated over. This is done
        relative to the start room.
         */
        else{
            for (Thing thing: room.getContents()){
                if (thing instanceof Explorer){
                    graphic.strokeText("@",
                            (this.dotX + coOrdinates.x*this.squareSize) +
                            (this.squareSize*0.20),
                            (this.dotY +coOrdinates.y*this.squareSize) +
                            (this.squareSize*0.25));
                }
                if (thing instanceof Treasure){
                    graphic.strokeText("$",
                            (this.dotX + coOrdinates.x*this.squareSize) +
                            (this.squareSize*0.75),
                            (this.dotY +coOrdinates.y*this.squareSize) +
                            (this.squareSize*0.20));
                }
                if (thing instanceof Critter){
                    if (((Critter) thing).isAlive()){
                        graphic.strokeText("M",
                                (this.dotX + coOrdinates.x*this.squareSize)
                                        + (this.squareSize*0.15),
                                (this.dotY +coOrdinates.y*this.squareSize) +
                                (this.squareSize*0.75));
                    }
                    else{
                        graphic.strokeText("m",
                                (this.dotX + coOrdinates.x*this.squareSize)
                                        + (this.squareSize*0.75),
                                (this.dotY +coOrdinates.y*this.squareSize) +
                                (this.squareSize*0.75));
                    }
                }
            }
            /*
            Checks all the cases for exits and then draws a line in that
            compass position. This is all done relative to the start room.
            */
            if (exit.equals("North")){
                graphic.strokeLine((this.dotX + coOrdinates.x*
                                this.squareSize)+(this.squareSize/2),
                        (this.dotY +coOrdinates.y*this.squareSize)-5,
                        (this.dotX + coOrdinates.x*this.squareSize)
                                +(this.squareSize/2),
                        (this.dotY +coOrdinates.y*this.squareSize)+5);
            }
            if (exit.equals("South")){
                graphic.strokeLine((this.dotX + coOrdinates.x
                                *this.squareSize)+(this.squareSize/2),
                        (this.dotY +coOrdinates.y*this.squareSize)+45,
                        (this.dotX + coOrdinates.x*this.squareSize)
                                +(this.squareSize/2),
                        (this.dotY +coOrdinates.y*this.squareSize)+55);
            }
            if (exit.equals("East")){
                graphic.strokeLine((this.dotX + coOrdinates.x
                                *this.squareSize)+45,
                        (this.dotY +coOrdinates.y*this.squareSize)
                                +(this.squareSize/2),
                        (this.dotX + coOrdinates.x*this.squareSize)+55,
                        (this.dotY +coOrdinates.y*this.squareSize)
                                +(this.squareSize/2));
            }
            if (exit.equals("West")){
                graphic.strokeLine((this.dotX + coOrdinates.x
                                *this.squareSize)-5,
                        (this.dotY +coOrdinates.y*this.squareSize)
                                +(this.squareSize/2),
                        (this.dotX + coOrdinates.x*this.squareSize)+5,
                        (this.dotY +coOrdinates.y*this.squareSize)
                                +(this.squareSize/2));
            }
        }
    }

    /**
     * Special drawer that draws the exits and contents of the start room,
     * all exits and contents placements follow what is in roomDetailsDrawer
     * documentation.
     *
     * @param exit exit that could possibly be drawn
     * @param room room that the exit belongs to
     */
    private void startRoomDrawer(String exit, Room room){
        /*
        Checks the contents of all the rooms and draws the correct icons
        depending and on Thing is currently being iterated over
         */
        for (Thing thing: room.getContents()){
            if (thing instanceof Explorer){
                graphic.strokeText("@", this.dotX +
                        (this.squareSize*0.20), this.dotY +
                        (this.squareSize*0.20));
            }
            if (thing instanceof Treasure){
                graphic.strokeText("$", this.dotX +
                        (this.squareSize*0.75), this.dotY +
                        (this.squareSize*0.20));
            }
            if (thing instanceof Critter){
                if (((Critter) thing).isAlive()){
                    graphic.strokeText("M", this.dotX +
                            (this.squareSize*0.20), this.dotY +
                            (this.squareSize*0.75));
                }
                else{
                    graphic.strokeText("m", this.dotX +
                            (this.squareSize*0.75), this.dotY +
                            (this.squareSize*0.75));
                }
            }
        }
        /*
        Checks all the cases for exits and then draws a line in that
        compass position.
         */
        if (exit.equals("North")){
            graphic.strokeLine(this.dotX+(this.squareSize/2),this.dotY-5,
                    this.dotX+(this.squareSize/2),this.dotY+5);
        }
        if (exit.equals("South")){
            graphic.strokeLine(this.dotX+(this.squareSize/2),this.dotY+45,
                    this.dotX+(this.squareSize/2),this.dotY+55);
        }
        if (exit.equals("East")){
            graphic.strokeLine(this.dotX+45,this.dotY+(this.squareSize/2),
                    this.dotX+55,this.dotY+(this.squareSize/2));
        }
        if (exit.equals("West")){
            graphic.strokeLine(this.dotX-5,this.dotY+(this.squareSize/2),
                    this.dotX+5,this.dotY+(this.squareSize/2));
        }
    }


}
