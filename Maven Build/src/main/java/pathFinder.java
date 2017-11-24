import com.google.maps.*;
import com.google.gson.*;
import java.net.*;
import java.io.*;

public class pathFinder {
    public static void main(String[] args) throws Exception {
        //TODO Derive URL from keyboard input using addressing syntax and
        URL request = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyASi8i_P2xPy-mlxEMurV_kWXBgQvVFyF4");
        BufferedReader in = new BufferedReader(new InputStreamReader(request.openStream()));
        //TODO Check the status of the output
        String response;
        //TODO Implement exponential backoff
        while ((response = in.readLine()) != null) {
            //TODO Parse the output
            System.out.println(response);
        }
        in.close();
    }
}