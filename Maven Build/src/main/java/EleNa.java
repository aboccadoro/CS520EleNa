import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.openstreetmap.osmosis.core.Osmosis;

import java.util.Scanner;

public class EleNa {

    public static void main(String[] args) {
        System.out.println("Welcome to EleNa, a Google Maps project focusing on elevation gain on an x% shortest path between two locations.");
        System.out.print("Enter a source address: ");
        Scanner scan = new Scanner(System.in);
        String src = scan.nextLine();
        GMAP gmap = new GMAP();
        JsonObject source = gmap.geocode(src);
        while (src == null) {
            source = gmap.geocode(src);
        }
        double srcel = gmap.elevData(gmap.getCoords(source));
        System.out.println(gmap.getCoords(source));
        System.out.println(srcel);
        System.out.print("Enter a destination address: ");
        scan = new Scanner(System.in);
        String dst = scan.nextLine();
        JsonObject destination = gmap.geocode(dst);
        while (dst == null) {
            destination = gmap.geocode(dst);
        }
        double dstel = gmap.elevData(gmap.getCoords(destination));
        System.out.println(gmap.getCoords(destination));
        System.out.println(dstel);
        System.out.println("\nElevation change: " + (dstel - srcel));
        
        //route code
        GoogleRoute route = new GoogleRoute();
        JsonObject distance = route.getDistanceJson(src, dst);
        JsonObject path = route.getDirectionsJson(src, dst);
        System.out.println("Distance: " + route.getDistanceText(distance) + " " + "(" + route.getDistance(distance) + " meters)");
        System.out.println("\n");
        System.out.println("Path: " + route.getDirections(path));
        
    }
}
