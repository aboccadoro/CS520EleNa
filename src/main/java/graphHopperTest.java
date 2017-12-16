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

    public static PathWrapper[] pather(LatLng start, LatLng end) {

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
                setWeighting("shortest").
                setVehicle("foot").
                setLocale(Locale.US);
        req.setAlgorithm(Parameters.Algorithms.ALT_ROUTE);
        req.getHints().put(Parameters.Algorithms.AltRoute.MAX_PATHS, "100");
        req.getHints().put(Parameters.Algorithms.AltRoute.MAX_WEIGHT, "1.4");
        req.getHints().put(Parameters.Algorithms.AltRoute.MAX_SHARE, "0.9");
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

        PathWrapper[] out = {path, path, path};

        InstructionList il = path.getInstructions();
        double[] info = {path.getTime(), path.getAscend(), path.getAscend()};
        for (PathWrapper k : rsp.getAll()) {
            if (k.getAscend() >= info[1]) {
                out[1] = k;
                info[1] = k.getAscend();
            }
            if (k.getAscend() <= info[2]) {
                out[2] = k;
                info[2] = k.getAscend();
            }
        }
// iterate over every turn instruction
        for (Instruction instruction : il) {
            instruction.getDistance();
            //System.out.println(instruction.getName()+". Distance: "+instruction.getDistance());
        }

        System.out.println(path.getAscend());

// or get the json
        List<Map<String, Object>> iList = il.createJson();
        return out;
    }
}