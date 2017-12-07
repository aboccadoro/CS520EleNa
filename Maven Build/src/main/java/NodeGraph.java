import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

public class NodeGraph {
    public double[] currLL = {0.0, 0.0};
    public double[] endLL = {0.0, 0.0};
    public double totalDist;
    public int distance = 0;
    public double angleTo = 0; // Angle from curr to end
    public double angleFrom = 0; // Angle from prev to curr
    public String placeID = "";
    public LinkedList<NodeGraph> edges = new LinkedList<NodeGraph>();

    public NodeGraph(double[] start, double[] end) {
        currLL[0] = start[0];
        currLL[1] = start[1];
        endLL[0] = end[0];
        endLL[1] = end[1];
        totalDist = Math.sqrt(Math.pow(currLL[0] - endLL[0], 2) + Math.pow(currLL[1] - endLL[1], 2));
        //angleTo = Math.asin((currLL[1] - endLL[1]) / totalDist);
        angleTo = Math.atan2(endLL[0]-currLL[0],endLL[1]-currLL[1]);
    }

    public NodeGraph(double[] start, double[] end, double angle, String placeID) {
        currLL[0] = start[0];
        currLL[1] = start[1];
        endLL[0] = end[0];
        endLL[1] = end[1];
        this.angleFrom = angle;
        this.placeID = placeID;
        totalDist = Math.sqrt(Math.pow(currLL[0] - endLL[0], 2) + Math.pow(currLL[1] - endLL[1], 2));
        //angleTo = Math.asin((currLL[1] - endLL[1]) / totalDist);
        angleTo = Math.atan2(endLL[0]-currLL[0],endLL[1]-currLL[1]);
    }

    public String getPoints() {
        String points;
        double[] c_xy = {0.0, 0.0};
        double radius = Math.min(totalDist / 5, 0.00001);
        double cleft = angleFrom + (45 * Math.PI / 180);
        double cright = angleFrom - (45 * Math.PI / 180);
        double eleft = angleTo + (45 * Math.PI / 180);
        double eright = angleTo - (45 * Math.PI / 180);

        double left = Math.max(cleft,eleft);
        double right = Math.min(cright,eright);

        System.out.println(angleFrom * 180 / Math.PI);
        System.out.println(angleTo * 180 / Math.PI);

        System.out.println(left * 180 / Math.PI);
        System.out.println(right * 180 / Math.PI);

        points = (0.01 * Math.sin(angleFrom)+currLL[0]) + "," + (0.01 * Math.cos(angleFrom)+currLL[1]);
        points += "|"+(0.01 * Math.sin(angleTo)+currLL[0]) + "," + (0.01 * Math.cos(angleTo)+currLL[1]);
        for (int i = 0; i < 10; i++) {
            //radius = Math.min(radius + 0.0005, totalDist / 5);
            radius+= 0.0005;
            for (int j = 0; j < 8; j++) {
                c_xy[0] = radius * Math.sin((j / 4.0) * Math.PI + i) + currLL[0];
                c_xy[1] = radius * Math.cos((j / 4.0) * Math.PI + i) + currLL[1];
                points += "|" + c_xy[0] + "," + c_xy[1];
            }
        }
        return points;
    }

    public String roadPoints() {
        BufferedReader reader;
        JsonObject json;
        JsonArray locations;
        String points;
        HashMap<String, LinkedList<double[]>> locator = new HashMap<String, LinkedList<double[]>>();
        String placeID;
        double[] latLong = {0.0, 0.0};
        NodeGraph test = new NodeGraph(currLL, endLL);

        points = getPoints();

        try {
            String roadURL = "https://roads.googleapis.com/v1/nearestRoads?points="
                    + points
                    + "&key=AIzaSyDs9LC1iCmWP1W4mfJxKuYM3ypooc3nnkM";
            URL url = new URL(roadURL);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            JsonReader jr = new JsonReader(reader);
            JsonParser parser = new JsonParser();
            json = parser.parse(jr).getAsJsonObject();
            if (json.isJsonNull()) {
                return "";
            }
            points = "";
            locations = json.get("snappedPoints").getAsJsonArray();
            this.placeID = locations.get(0).getAsJsonObject().get("placeId").getAsString();
            for (JsonElement x : locations) {
                placeID = x.getAsJsonObject().get("placeId").getAsString();
                latLong[0] = x.getAsJsonObject().get("location").getAsJsonObject().get("latitude").getAsDouble();
                latLong[1] = x.getAsJsonObject().get("location").getAsJsonObject().get("longitude").getAsDouble();
                if (!locator.containsKey(placeID)) {
                    LinkedList<double[]> dist_List = new LinkedList<double[]>();
                    dist_List.add(latLong.clone());
                    locator.put(placeID, dist_List);
//                    if (!points.equals("")) {
//                        points += "|";
//                    }
//                    points += latLong[0] + "," + latLong[1];
                } else {
                    locator.get(placeID).add(latLong.clone());
                }
            }

            points += "&markers=size:mid|color:orange|";

            double dist_xy;
            for (String x : locator.keySet()) {
                double closest_dist = 404; // Essentially Max latLong distance
                double[][] cF_latLong = {{0, 0}, {0, 0}};
                double furthest_dist = 0;
                for (double[] y : locator.get(x)) {
                    dist_xy = Math.sqrt(Math.pow(currLL[0] - y[0], 2) + Math.pow(currLL[1] - y[1], 2));
                    if (x.equals(this.placeID)) {
                        if (furthest_dist < dist_xy) {
                            furthest_dist = dist_xy;
                            cF_latLong[0] = y.clone();
                        }
                    } else {
                        if (closest_dist > dist_xy) {
                            closest_dist = dist_xy;
                            cF_latLong[1] = y.clone();
                            double angle = Math.atan2(cF_latLong[1][0]-currLL[0],cF_latLong[1][1]-currLL[1]);
                            test = new NodeGraph(cF_latLong[1], endLL.clone(), angle, x);
                        }
                    }
                }
                if (!points.equals("")) {
                    points += "|";
                }
                points += cF_latLong[0][0] + "," + cF_latLong[0][1] + "|";
                points += cF_latLong[1][0] + "," + cF_latLong[1][1];
            }
            System.out.println(url);
            points += "&markers=size:small|color:red|";
            points += test.getPoints();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }
}