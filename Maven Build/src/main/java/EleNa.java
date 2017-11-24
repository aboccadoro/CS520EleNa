import com.google.gson.JsonObject;
import com.google.maps.model.LatLng;

public class EleNa {

    public static void main(String[] args) {
        GMAP test = new GMAP();
        JsonObject location = test.getJson();
        while (location == null) {
            location = test.getJson();
        }
        LatLng coords = test.getCoords(location);
        Tuple elevation = test.elevData(coords);
        System.out.println(elevation.y);
    }
}
