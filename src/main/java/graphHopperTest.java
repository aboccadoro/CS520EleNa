import com.google.maps.model.LatLng;
import com.graphhopper.*;
import com.graphhopper.reader.dem.SRTMProvider;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.AlternativeRoute;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FootFlagEncoder;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.weighting.AbstractWeighting;
import com.graphhopper.storage.GHDirectory;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint3D;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class doesn't even work the way it was supposed to,
 * but it makes ALMOST EVERY CLASS we have redundant.
 * The only thing is class needs is a custom weight for all 3 of our outputs
 * 1. Least Elevation Gain
 * 2. Most Elevation Gain
 * 3. Shortest Path
 * Please download the MA map data. (225MB)
 * http://download.geofabrik.de/north-america/us/massachusetts.html
 */
public class graphHopperTest {

    public static LinkedList<LatLng>[] pather(LatLng start, LatLng end) {

        // create one GraphHopper instance
        GraphHopper hopper = new GraphHopperOSM().forServer();
        hopper.setDataReaderFile("file.pbf");
// where to store graphhopper files?
        hopper.setCHEnabled(false);
        hopper.setGraphHopperLocation("graph/");
        hopper.setEncodingManager(new EncodingManager("foot"));
        hopper.setElevationProvider(new SRTMProvider());
        hopper.setElevation(true);

// now this can take minutes if it imports or a few seconds for loading
// of course this is dependent on the area you import
        hopper.importOrLoad();

// simple configuration of the request object, see the GraphHopperServlet classs for more possibilities.
        GHRequest req = new GHRequest(start.lat, start.lng, end.lat, end.lng).
                setWeighting("fastest").
                setVehicle("foot").
                setLocale(Locale.US);
        req.setAlgorithm(Parameters.Algorithms.ALT_ROUTE);
        req.getHints().put(Parameters.Algorithms.AltRoute.MAX_PATHS, "3");
        GHResponse rsp = hopper.route(req);

// first check for errors
        if (rsp.hasErrors()) {
            // handle them!
            // rsp.getErrors()
            System.out.println(rsp.getErrors());
        }

// use the best path, see the GHResponse class for more possibilities.
        PathWrapper path = rsp.getBest();

// points, distance in meters and time in millis of the full path
        PointList pointList = path.getPoints();
        double distance = path.getDistance();
        long timeInMs = path.getTime();

        LinkedList<LatLng>[] out = new LinkedList[3];

        InstructionList il = path.getInstructions();
        int count = 0;
        for(PathWrapper k:rsp.getAll()){
            LinkedList<LatLng> temp = new LinkedList<LatLng>();
            System.out.println("\nPath:");
            for(GHPoint3D x:k.getPoints()){
                System.out.print(x.toString()+" ");
                temp.add(new LatLng(x.toGeoJson()[1],x.toGeoJson()[0]));
            }
            out[count++] = temp;
        }
// iterate over every turn instruction
        for (Instruction instruction : il) {
            instruction.getDistance();
            System.out.println(instruction.getName()+". Distance: "+instruction.getDistance());
        }

        System.out.println(path.getAscend());

// or get the json
        List<Map<String, Object>> iList = il.createJson();
        return out;
    }
}