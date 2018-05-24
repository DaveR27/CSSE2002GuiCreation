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
            graphic.fillRect(rooms.getValue().x, rooms.getValue().y,10,10);
        }
    }

}
