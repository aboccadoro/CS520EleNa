import com.google.gson.JsonObject;
import com.google.maps.model.LatLng;
import java.util.Scanner;

public class EleNa {

    public static void main(String[] args) {
        System.out.println("Welcome to EleNa, a Google Maps project focusing on elevation gain on a shortest path between two locations.");
        System.out.print("Enter a source address: ");
        Scanner source = new Scanner(System.in);
        GMAP gmap = new GMAP();
        JsonObject src = gmap.geolocate(source);
        while (src == null) {
            src = gmap.geolocate(source);
        }
        double elevation = gmap.elevData(gmap.getCoords(src));
        System.out.println(gmap.getCoords(src));
        System.out.println(elevation);
    }
}
