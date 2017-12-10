import com.google.maps.model.LatLng;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.dem.SRTMProvider;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.AlternativeRoute;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;
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

    public static LinkedList<LatLng> pather(LatLng start, LatLng end) {
        LinkedList<LatLng> out = new LinkedList<LatLng>();

        // create one GraphHopper instance
        GraphHopper hopper = new GraphHopperOSM().forServer();
        hopper.setDataReaderFile("north-america_us_massachusetts.pbf");
// where to store graphhopper files?
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

        InstructionList il = path.getInstructions();
        for(GHPoint3D x:path.getPoints()){
            out.add(new LatLng(x.toGeoJson()[1],x.toGeoJson()[0]));
            System.out.println(x.toString());
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