import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.*;

public class Cartographer extends javafx.scene.canvas.Canvas {
    private Object[] loadInformation;
    public Player player;
    private BoundsMapper mapBounds;
    public Room startRoom;
    Canvas mapDrawing;

    //Sets up Drawing
    private double dotX;
    private double dotY;

    public Cartographer(String map, Canvas canvas){
        this.loadInformation = MapIO.loadMap(map);
        this.mapDrawing = canvas;
        if (this.loadInformation.equals(null)){
            System.err.println("Unable to Load file");
            System.exit(2);
        }
        this.player = (Player) loadInformation[0];
        this.startRoom = (Room) loadInformation[1];
        drawRooms();
    }

    public void drawRooms(){
        GraphicsContext graphic = mapDrawing.getGraphicsContext2D();
        this.mapBounds = new BoundsMapper(startRoom);
        this.mapBounds.walk();
        for(Pair i: this.mapBounds.coords.values()){
            System.out.println(i.x+","+i.y);
        }

        drawRoom(graphic);
    }
    private void drawRoom(GraphicsContext graphic){
        this.dotX = this.mapDrawing.getWidth()/2;
        this.dotY = this.mapDrawing.getHeight()/2;
        for (Pair coords: this.mapBounds.coords.values()) {
            if (coords.x == 0 && coords.y == 0){
                graphic.strokeRect(this.dotX, this.dotY, 50,50);
            }
            else {
                graphic.strokeRect(this.dotX + coords.x*50,
                        this.dotY +coords.y*50, 50,50);
            }
        }
//        graphic.strokeRect(dotX,dotY,50,50);
//        for (String room: this.startRoom.getExits().keySet()){
//            if (room.equals("North")){
//                graphic.strokeLine(this.dotX+25, this.dotY-10,
//                        this.dotX+25, this.dotY+10);
//            }
//            if (room.equals("South")){
//                graphic.strokeLine(this.dotX+25, this.dotY+40,
//                        this.dotX+25, this.dotY+60);
//            }
//            if (room.equals("East")){
//                graphic.strokeLine(this.dotX+40, this.dotY+25,
//                        this.dotX+60, dotY+25);
//            }
//            if (room.equals("West")){
//                graphic.strokeLine(this.dotX-10, this.dotY+25,
//                        this.dotX+10, this.dotY+25);
//            }
//        }
//        walkDrawer(graphic);
    }

//    public void walkDrawer(GraphicsContext graphic) {
//        boolean northToggle = false;
//        boolean southToogle = false;
//        boolean eastToggle = false;
//        boolean westToogle = false;
//
//        Deque<Room> toDraw = new LinkedList<Room>();
//        Set<Room> drawnRooms = new HashSet<Room>();
//        toDraw.add(this.startRoom);
//        while (! toDraw.isEmpty()) {
//            Room r = toDraw.removeFirst();
//            if (!drawnRooms.contains(r)) {
//                drawnRooms.add(r);
//                for (Room e : r.getExits().values()) {
//                    toDraw.add(e);
//                }
//                for (String p: r.getExits().keySet()){
//                    if (p.equals("North")){
//                        graphic.strokeRect(this.dotX,this.dotY-50,
//                                50,50);
//
//                    }
//                    if (p.equals("South")){
//                        graphic.strokeRect(this.dotX,this.dotY+50,
//                                50,50);
//
//                    }
//                    if (p.equals("East")){
//                        graphic.strokeRect(this.dotX+50,this.dotY,
//                                50,50);
//
//                    }
//                    if (p.equals("West")){
//                        graphic.strokeRect(this.dotX-50,this.dotY,
//                                50,50);
//
//                    }
//                }
//
//                drawnRooms.add(r);
//                System.out.println(r.getExits().keySet());
//            }
//        }
//    }


}
