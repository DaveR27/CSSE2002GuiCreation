import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.util.Map;

public class Cartographer extends javafx.scene.canvas.Canvas {
    private Object[] loadInformation;
    public Player player;
    BoundsMapper mapBounds;
    public Room startRoom;

    public Cartographer(String map){
        loadInformation[2] = MapIO.loadMap(map);
        this.player = (Player) loadInformation[0];
        this.startRoom = (Room) loadInformation[1];

    }

    public void drawRooms(){
        Canvas mapDrawing = new Canvas();
        GraphicsContext graphic = mapDrawing.getGraphicsContext2D();
        this.mapBounds = new BoundsMapper(startRoom);
        this.mapBounds.walk();
        drawRoom(graphic);
    }

    private void drawRoom(GraphicsContext graphic){
        for (Map.Entry<Room, Pair> rooms: this.mapBounds.coords.entrySet()){
            graphic.rect(rooms.getValue().x, rooms.getValue().y,10,10);
            int xCo = rooms.getValue().x;
            int yCo = rooms.getValue().y;
            for (String exit: rooms.getKey().getExits().keySet()){
                switch (exit){
                    case ("North"):
                        graphic.strokeLine(xCo+5,
                                yCo-2, xCo+5 , yCo+2);
                        break;
                    case ("South"):
                        graphic.strokeLine(xCo+5,
                                yCo+8, xCo+5 , yCo+12);
                        break;
                    case ("East"):
                        graphic.strokeLine(xCo+8,
                                yCo+5, xCo+12 , yCo+5);
                        break;
                    case ("West"):
                        graphic.strokeLine(xCo-2,
                                yCo+5, xCo+2 , yCo+5);
                        break;
                }
            }
        }
    }

}
