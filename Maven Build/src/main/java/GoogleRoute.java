import com.google.gson.stream.JsonReader;
import com.google.maps.*;
import com.google.gson.*;
import com.google.maps.errors.*;
import com.google.maps.model.LatLng;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.concurrent.TimeoutException;

public class GoogleRoute {

    private String key = "&key=AIzaSyASi8i_P2xPy-mlxEMurV_kWXBgQvVFyF4";

    GoogleRoute() {}

    JsonObject query() {
        //Derive URL from geocoded keyboard input using addressing syntax URL(URL(static), output?address + &key="key")
        JsonObject json = null;
        try {
            URL stat = new URL("https://maps.googleapis.com/maps/api/distancematrix/");
            int tries = 0;
            while(true) {
                //Location to geoencode
            	System.out.print("Enter a source address: ");
            	Scanner source = new Scanner(System.in);
                String in = source.nextLine();
                char[] format = in.toCharArray();
                int index = 0;
                CharacterIterator it = new StringCharacterIterator(in);
                for(char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
                    if(ch == ' ') format[index] = '+';
                    index++;
                }
                StringBuilder origin = new StringBuilder("origins=");
                for(char ch : format) origin.append(ch);
                
                
                System.out.print("Enter a destination address: ");
                source = new Scanner(System.in);
                in = source.nextLine();
                format = in.toCharArray();
                index = 0;
                it = new StringCharacterIterator(in);
                for(char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
                    if(ch == ' ') format[index] = '+';
                    index++;
                }
                StringBuilder destination = new StringBuilder("&destinations=");
                for(char ch : format) destination.append(ch);
                
                URL request = new URL(stat, "json?" + origin + destination + "&driving=true&language=eng-US&units=imperial" + key);
                BufferedReader br = new BufferedReader(new InputStreamReader(request.openStream()));
                JsonReader jr = new JsonReader(br);
                JsonParser parser = new JsonParser();
                json = parser.parse(jr).getAsJsonObject();

                if(json.get("status").getAsString().equals("OK")) break;
                else if(json.get("status").getAsString().equals("OVER_QUERY_LIMIT")) throw new OverQueryLimitException("Query limit exceeded during geocoding of location: " + in);
                else if(json.get("status").getAsString().equals("INVALID_REQUEST")) throw new InvalidRequestException("Check the address, components and/or latlng fields of: " + in);
                else if(json.get("status").getAsString().equals("REQUEST_DENIED")) throw new RequestDeniedException("The request was denied for the location: " + in);
                else if(tries > 5) throw new TimeoutException("The request failed 5 times in a row, please try again later.");
                tries++;
                br.close();
                jr.close();
            }
        }
        catch (MalformedURLException e) {System.out.println("Malformed URL"); return null;}
        catch (IOException e) {System.out.println("Keyboard input error" + e); return null;}
        catch (OverQueryLimitException e) {System.out.println("Query limit exceeded on Geocoding API"); System.exit(1);}
        catch (InvalidRequestException e) {System.out.println("Invalid request"); return null;}
        catch (RequestDeniedException e) {System.out.println("Request denied"); return null;}
        catch (TimeoutException e) {System.exit(1);}
        return json;
    }
    int getDistance(JsonObject json) {
    	return json.get("rows").getAsJsonArray().get(0).getAsJsonObject().get("elements").getAsJsonArray().get(0).getAsJsonObject().get("distance").getAsJsonObject().get("value").getAsInt();
    }
    String getDistanceText(JsonObject json) {
    	return json.get("rows").getAsJsonArray().get(0).getAsJsonObject().get("elements").getAsJsonArray().get(0).getAsJsonObject().get("distance").getAsJsonObject().get("text").getAsString();
    }

}
