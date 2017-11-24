import com.google.gson.stream.JsonReader;
import com.google.maps.*;
import com.google.gson.*;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.errors.OverQueryLimitException;
import com.google.maps.errors.RequestDeniedException;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.concurrent.TimeoutException;

public class pathFinder {
    public static void main(String[] args) throws Exception {
        //Derive URL from geocoded keyboard input using addressing syntax URL(URL(static), output?address + &key="key")
        String key = "&key=AIzaSyASi8i_P2xPy-mlxEMurV_kWXBgQvVFyF4";
        URL stat = new URL("https://maps.googleapis.com/maps/api/geocode/");
        Gson gson = new Gson();
        JsonObject json;
        String response = "";
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
            String address = "json?address=";
            for(char ch : format) address += ch;
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
        }
        System.out.println(json);
    }
}