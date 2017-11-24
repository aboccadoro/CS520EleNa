import com.google.maps.*;
import com.google.gson.*;
import java.net.*;
import java.io.*;

public class pathFinder {
    public static void main(String[] args) throws Exception {
        URL request = new URL("");
        BufferedReader in = new BufferedReader(new InputStreamReader(request.openStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
    }
}