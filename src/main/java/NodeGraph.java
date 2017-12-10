import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

public class NodeGraph {

    public static int r = 0;

    public double[] currLL = {0.0, 0.0};
    public double[] endLL = {0.0, 0.0};
    public double totalDist;
    public double distTo;
    public int distance = 0;
    public double angleTo = 0; // Angle from curr to end
    public double angleFrom = 0; // Angle from prev to curr
    public String placeID = "";
    public boolean isGoal = false;
    public LinkedList<NodeGraph> edges = new LinkedList<NodeGraph>();

    public NodeGraph(LatLng start, LatLng end, double totalDist) {
        currLL[0] = start.lat;
        currLL[1] = start.lng;
        endLL[0] = end.lat;
        endLL[1] = end.lng;
        this.totalDist = totalDist;
        distTo = Math.sqrt(Math.pow(currLL[0] - endLL[0], 2) + Math.pow(currLL[1] - endLL[1], 2));
        //roadPoints();
        angleTo = Math.atan2(endLL[0] - currLL[0], endLL[1] - currLL[1]);
    }

    public NodeGraph(LatLng start, LatLng end, double angle, double totalDist) {
        currLL[0] = start.lat;
        currLL[1] = start.lng;
        endLL[0] = end.lat;
        endLL[1] = end.lng;
        this.angleFrom = angle;
        this.totalDist = totalDist;
        distTo = Math.sqrt(Math.pow(currLL[0] - endLL[0], 2) + Math.pow(currLL[1] - endLL[1], 2));
        angleTo = Math.atan2(endLL[0] - currLL[0], endLL[1] - currLL[1]);
        roadPoints();
    }

    public LinkedList<double[]> getPoints() {
        LinkedList<double[]> points = new LinkedList<double[]>() {
            @Override
            public String toString() {
                String out = "";
                for (double[] x : this) {
                    if (!out.equals("")) {
                        out += "|";
                    }
                    out += x[0] + "," + x[1];
                }
                return out;
            }
        };

        double[] c_xy = {0.0, 0.0};
        double radius;

        points.add(new double[]{currLL[0], currLL[1]});
        double phi = (Math.sqrt(5) + 1) / 2;

        if (distTo < 0.003) {
            points.add(endLL);
            isGoal = true;
            return points;
        }
        for(int j = 0; j< 16; j++){
            radius = Math.min(distTo, 0.003);
            c_xy[0] = radius * Math.sin(j/8.0 * Math.PI) + currLL[0];
            c_xy[0] = Math.floor(c_xy[0] * Math.pow(10, 5)) / Math.pow(10, 5);
            c_xy[1] = radius * Math.cos(j/8.0 * Math.PI) + currLL[1];
            c_xy[1] = Math.floor(c_xy[1] * Math.pow(10, 5)) / Math.pow(10, 5);
            points.add(new double[]{c_xy[0], c_xy[1]});
        }

//        for (int j = 1; j < 80; j++) {
//            radius = Math.min(distTo, 0.003) * Math.sqrt(j - 1.0 / 2) / Math.sqrt(80.0 - 1.0 / 2);
//            double theta = (2 * Math.PI * j) / Math.pow(phi, 2);
//            c_xy[0] = radius * Math.sin(theta) + currLL[0];
//            c_xy[0] = Math.floor(c_xy[0] * Math.pow(10, 5)) / Math.pow(10, 5);
//            c_xy[1] = radius * Math.cos(theta) + currLL[1];
//            c_xy[1] = Math.floor(c_xy[1] * Math.pow(10, 5)) / Math.pow(10, 5);
//            points.add(new double[]{c_xy[0], c_xy[1]});
//        }
        return points;
    }

    public LinkedList<double[]> roadPoints() {
        BufferedReader reader;
        JsonObject json;
        JsonArray locations;
        LinkedList<double[]> out = new LinkedList<double[]>() {
            @Override
            public String toString() {
                String out = "";
                for (double[] x : this) {
                    if (!out.equals("")) {
                        out += "|";
                    }
                    out += x[0] + "," + x[1];
                }
                return out;
            }
        };

        LinkedList points;
        HashMap<String, LinkedList<double[]>> locator = new HashMap<String, LinkedList<double[]>>();
        String placeID;
        double[] latLong = {0.0, 0.0};

        points = getPoints();

        r++;
        if (r< 150 && !isGoal) {
            System.out.println(r);
            try {
                String roadURL = "https://roads.googleapis.com/v1/nearestRoads?points="
                        + points.toString()
                        + "&key=AIzaSyDs9LC1iCmWP1W4mfJxKuYM3ypooc3nnkM";
                URL url = new URL(roadURL);
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                JsonReader jr = new JsonReader(reader);
                JsonParser parser = new JsonParser();
                json = parser.parse(jr).getAsJsonObject();
                if (json.isJsonNull()) {
                    return out;
                }
                locations = json.get("snappedPoints").getAsJsonArray();
                this.placeID = locations.get(0).getAsJsonObject().get("placeId").getAsString();
                for (JsonElement x : locations) {
                    placeID = x.getAsJsonObject().get("placeId").getAsString().substring(0, 16);
                    latLong[0] = x.getAsJsonObject().get("location").getAsJsonObject().get("latitude").getAsDouble();
                    latLong[0] = Math.floor(latLong[0] * Math.pow(10, 5)) / Math.pow(10, 5);
                    latLong[1] = x.getAsJsonObject().get("location").getAsJsonObject().get("longitude").getAsDouble();
                    latLong[1] = Math.floor(latLong[1] * Math.pow(10, 5)) / Math.pow(10, 5);
                    if (!locator.containsKey(placeID)) {
                        LinkedList<double[]> dist_List = new LinkedList<double[]>();
                        dist_List.add(latLong.clone());
                        locator.put(placeID, dist_List);
                    } else {
                        locator.get(placeID).add(latLong.clone());
                    }
                }

                double dist_xy;
                for (String x : locator.keySet()) {
                    double[] cF_latLong = {0, 0};
                    double furthest_dist = 0;
                    for (double[] y : locator.get(x)) {
                        dist_xy = Math.sqrt(Math.pow(currLL[0] - y[0], 2) + Math.pow(currLL[1] - y[1], 2));
                        if (furthest_dist < dist_xy) {
                            furthest_dist = dist_xy;
                            cF_latLong = y.clone();
                        }
                    }
                    out.add(cF_latLong);
                }
                System.out.println(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(out.toString());
            addPathable(out);
        }
        return out;
    }

    public void addPathable(LinkedList<double[]> input) {

        double eleft = (angleFrom + Math.PI * 2) % (Math.PI * 2) + (90 * Math.PI / 180);
        double eright = (angleFrom + Math.PI * 2) % (Math.PI * 2) - (90 * Math.PI / 180);

        for (double[] x : input) {
            double angle = (Math.atan2(x[0] - currLL[0], x[1] - currLL[1]) + Math.PI * 2) % (Math.PI * 2);
            if ((angle < eleft && angle > eright) || angleFrom == 0.0) {
                BufferedReader reader;
                JsonObject json = new JsonObject();
                try {
                    boolean success = false;
                    int attempt = 0;
                    String graphURL = "https://maps.googleapis.com/maps/api/directions/json?origin="
                            + currLL[0] + "," + currLL[1]
                            + "&destination="
                            + endLL[0] + "," + endLL[1]
                            + "&waypoints=via:" + x[0] + "," + x[1]
                            + "&mode=walking"
                            +"&key=AIzaSyASi8i_P2xPy-mlxEMurV_kWXBgQvVFyF4";
                    URL url = new URL(graphURL);
                    while (!success && attempt < 10) {
                        attempt++;
                        reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        JsonReader jr = new JsonReader(reader);
                        JsonParser parser = new JsonParser();
                        json = parser.parse(jr).getAsJsonObject();
                        if (json.get("routes").getAsJsonArray().size() != 0) {
                            success = true;
                        } else {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                System.out.println("no");
                            }
                        }
                    }
                    if (totalDist * 1.05 > json.get("routes").getAsJsonArray().get(0).getAsJsonObject().get("legs").getAsJsonArray().get(0).getAsJsonObject().get("distance").getAsJsonObject().get("value").getAsInt()) {
                        LatLng n = new LatLng(x[0], x[1]);
                        edges.add(new NodeGraph(n, new LatLng(endLL[0], endLL[1]), angle, json.get("routes").getAsJsonArray().get(0).getAsJsonObject().get("legs").getAsJsonArray().get(0).getAsJsonObject().get("distance").getAsJsonObject().get("value").getAsInt()));
                    }
                } catch (IOException e) {
                    System.exit(1);
                }
            }
        }
    }

    public String toString() {
        String out = "";
        out += currLL[0] + "," + currLL[1];
        for (NodeGraph x : edges) {
            out += "|" + x.currLL[0] + "," + x.currLL[1];
        }
        for (NodeGraph x : edges) {
            out += "|" + x.toString();
        }
        return out;
    }
}