import com.google.gson.JsonObject;
import com.google.maps.model.LatLng;

public class main {

    public static void main(String[] arga) {
        EleNa test = new EleNa();
        JsonObject location = test.getJson();
        LatLng coords = test.getCoords(location);
        Tuple elevation = test.elevData(coords);
        System.out.println(elevation.y);
    }
}
