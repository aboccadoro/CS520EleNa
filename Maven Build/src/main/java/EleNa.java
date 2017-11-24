import com.google.gson.JsonObject;
import com.google.maps.model.LatLng;

public class EleNa {

    public static void main(String[] args) {
        GMAP gmap = new GMAP();
        JsonObject location = gmap.getJson();
        while (location == null) {
            location = gmap.getJson();
        }
        LatLng coords = gmap.getCoords(location);
        double elevation = gmap.elevData(coords);

        System.out.println(elevation);
    }
}
