import com.google.gson.JsonObject;
import com.google.maps.model.LatLng;
import java.util.Scanner;

public class EleNa {

    public static void main(String[] args) {
        System.out.println("Welcome to EleNa, a Google Maps project focusing on elevation gain on an x% shortest path between two locations.");
        System.out.print("Enter a source address: ");
        Scanner source = new Scanner(System.in);
        GMAP gmap = new GMAP();
        JsonObject src = gmap.geocode(source);
        while (src == null) {
            src = gmap.geocode(source);
        }
        double srcel = gmap.elevData(gmap.getCoords(src));
        System.out.println(gmap.getCoords(src));
        System.out.println(srcel);
        System.out.print("Enter a destination address: ");
        source = new Scanner(System.in);
        JsonObject dst = gmap.geocode(source);
        while (dst == null) {
            dst = gmap.geocode(source);
        }
        double dstel = gmap.elevData(gmap.getCoords(dst));
        System.out.println(gmap.getCoords(dst));
        System.out.println(dstel);
        System.out.println("\nElevation change: " + Math.abs(dstel - srcel));
    }
}
