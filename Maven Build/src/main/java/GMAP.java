import com.google.gson.stream.JsonReader;
import com.google.maps.*;
import com.google.gson.*;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.errors.OverQueryLimitException;
import com.google.maps.errors.RequestDeniedException;
import com.google.maps.model.LatLng;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.concurrent.TimeoutException;

class GMAP {

    private String key = "&key=AIzaSyASi8i_P2xPy-mlxEMurV_kWXBgQvVFyF4";

    GMAP() {}

    JsonObject getJson() {
        //Derive URL from geocoded keyboard input using addressing syntax URL(URL(static), output?address + &key="key")
        JsonObject json = null;
        try {
            URL stat = new URL("https://maps.googleapis.com/maps/api/geocode/");
            int tries = 0;
            while(true) {
                //Enter location to geoencode
                Scanner input = new Scanner(System.in);
                String in = input.nextLine();
                char[] format = in.toCharArray();
                int index = 0;
                CharacterIterator it = new StringCharacterIterator(in);
                for(char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
                    if(ch == ' ') format[index] = '+';
                    index++;
                }
                StringBuilder address = new StringBuilder("json?address=");
                for(char ch : format) address.append(ch);
                URL request = new URL(stat, address + key);
                BufferedReader br = new BufferedReader(new InputStreamReader(request.openStream()));
                JsonReader jr = new JsonReader(br);
                JsonParser parser = new JsonParser();
                json = parser.parse(jr).getAsJsonObject();

                if(json.get("status").getAsString().equals("OK")) break;
                else if(json.get("status").getAsString().equals("OVER_QUERY_LIMIT")) throw new OverQueryLimitException("Query limit exceeded during geocoding of location: " + input.toString());
                else if(json.get("status").getAsString().equals("INVALID_REQUEST")) throw new InvalidRequestException("Check the address, components and/or latlng fields of: " + input.toString());
                else if(json.get("status").getAsString().equals("REQUEST_DENIED")) throw new RequestDeniedException("The request was denied for the location: " + input.toString());
                else if(tries > 5) throw new TimeoutException("The request failed 5 times in a row, please try again later.");
                tries++;
                br.close();
                jr.close();
            }
        }
        catch (MalformedURLException e) {System.out.println("Malformed URL"); return null;}
        catch (IOException e) {System.out.println("Keyboard input error"); return null;}
        catch (OverQueryLimitException e) {System.out.println("Query limit exceeded on Geocoding API"); System.exit(1);}
        catch (InvalidRequestException e) {System.out.println("Invalid request"); return null;}
        catch (RequestDeniedException e) {System.out.println("Request denied"); return null;}
        catch (TimeoutException e) {System.exit(1);}
        return json;
    }

    LatLng getCoords(JsonObject geolocation) {
        JsonObject json = geolocation.get("results").getAsJsonArray().get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject();
        return new LatLng(json.get("lat").getAsDouble(), json.get("lng").getAsDouble());
    }

    double elevData(LatLng coords) {
        JsonObject json = null;
        int tries = 0;
        try {
            URL stat = new URL("https://maps.googleapis.com/maps/api/elevation/");
            while (true) {
                URL request = new URL(stat, "json?locations=" + coords.lat + "," + coords.lng + key);
                BufferedReader br = new BufferedReader(new InputStreamReader(request.openStream()));
                JsonReader jr = new JsonReader(br);
                JsonParser parser = new JsonParser();
                json = parser.parse(jr).getAsJsonObject();

                if(json.get("status").getAsString().equals("OK")) break;
                else if(json.get("status").getAsString().equals("OVER_QUERY_LIMIT")) throw new OverQueryLimitException("Query limit exceeded during geocoding of location: " + coords);
                else if(json.get("status").getAsString().equals("INVALID_REQUEST")) throw new InvalidRequestException("Check the address, components and/or latlng fields of: " + coords);
                else if(json.get("status").getAsString().equals("REQUEST_DENIED")) throw new RequestDeniedException("The request was denied for the location: " + coords);
                else if(tries > 5) throw new TimeoutException("The request failed 5 times in a row, please try again later.");
                tries++;
                br.close();
                jr.close();
            }
        }
        catch (MalformedURLException e) {System.out.println("Malformed URL"); System.exit(1);}
        catch (IOException e) {System.out.println("Keyboard input error"); System.exit(1);}
        catch (OverQueryLimitException e) {System.out.println("Query limit exceeded on Elevation Services API"); System.exit(1);}
        catch (InvalidRequestException e) {System.out.println("Invalid request"); System.exit(1);}
        catch (RequestDeniedException e) {System.out.println("Request denied"); System.exit(1);}
        catch (TimeoutException e) {System.out.println("Timeout exceeded"); System.exit(1);}
        return json.get("results").getAsJsonArray().get(0).getAsJsonObject().get("elevation").getAsDouble();
    }
}