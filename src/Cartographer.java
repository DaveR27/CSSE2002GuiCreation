import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.*;

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

    public Room getStartRoom(){
        return this.startRoom;
    }

    public void cleanCanvas(){
        this.graphic.clearRect(0,0, this.mapDrawing.getWidth(),
                this.mapDrawing.getHeight());
    }


    public Map<Room, Pair> getMap(){
        return this.mapBounds.coords;
    }

    public void drawRoom(){
        this.dotX = this.mapDrawing.getWidth()/2;
        this.dotY = this.mapDrawing.getHeight()/2;
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

        for (Map.Entry<Room, Pair> mappedMap: this.mapBounds.coords.entrySet()){
            for(String exit: mappedMap.getKey().getExits().keySet()){
               this.roomDetailsDrawer(exit, mappedMap.getKey(),
                       mappedMap.getValue());
            }
        }
    }

    private void roomDetailsDrawer(String exit, Room room, Pair coOrdinates){
        //Checks for origin as it is a special case
        if (coOrdinates.x == 0 && coOrdinates.y == 0){
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
                                (this.dotX + coOrdinates.x*this.squareSize) +
                                (this.squareSize*0.15),
                                (this.dotY +coOrdinates.y*this.squareSize) +
                                (this.squareSize*0.75));
                    }
                    else{
                        graphic.strokeText("m",
                                (this.dotX + coOrdinates.x*this.squareSize) +
                                (this.squareSize*0.75),
                                (this.dotY +coOrdinates.y*this.squareSize) +
                                (this.squareSize*0.75));
                    }
                }
            }
            if (exit.equals("North")){
                graphic.strokeLine((this.dotX + coOrdinates.x*this.squareSize)+(this.squareSize/2),
                        (this.dotY +coOrdinates.y*this.squareSize)-5,
                        (this.dotX + coOrdinates.x*this.squareSize)+(this.squareSize/2),
                        (this.dotY +coOrdinates.y*this.squareSize)+5);
            }
            if (exit.equals("South")){
                graphic.strokeLine((this.dotX + coOrdinates.x*this.squareSize)+(this.squareSize/2),
                        (this.dotY +coOrdinates.y*this.squareSize)+45,
                        (this.dotX + coOrdinates.x*this.squareSize)+(this.squareSize/2),
                        (this.dotY +coOrdinates.y*this.squareSize)+55);
            }
            if (exit.equals("East")){
                graphic.strokeLine((this.dotX + coOrdinates.x*this.squareSize)+45,
                        (this.dotY +coOrdinates.y*this.squareSize)+(this.squareSize/2),
                        (this.dotX + coOrdinates.x*this.squareSize)+55,
                        (this.dotY +coOrdinates.y*this.squareSize)+(this.squareSize/2));
            }
            if (exit.equals("West")){
                graphic.strokeLine((this.dotX + coOrdinates.x*this.squareSize)-5,
                        (this.dotY +coOrdinates.y*this.squareSize)+(this.squareSize/2),
                        (this.dotX + coOrdinates.x*this.squareSize)+5,
                        (this.dotY +coOrdinates.y*this.squareSize)+(this.squareSize/2));
            }
        }
    }


}
