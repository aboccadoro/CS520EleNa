import com.google.maps.*;
import com.google.gson.*;
import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class pathFinder {
    public static void main(String[] args) throws Exception {
        //TODO Derive URL from geocoded keyboard input using addressing syntax (static+output?parameters)
        String key = "&key=AIzaSyASi8i_P2xPy-mlxEMurV_kWXBgQvVFyF4";
        URL stat = new URL("https://maps.googleapis.com/maps/api/geocode/");
        BufferedReader buffer;
        String response;
        //while() {
            //Enter location to geoencode
            Scanner input = new Scanner(System.in);
            String in = input.nextLine();
            char[] format = in.toCharArray();
            int index = 0;
            CharacterIterator it = new StringCharacterIterator(in);
            for(char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
                if(ch == ' ') {
                    format[index] = '+';
                }
                index++;
            }
            String address = "json?address=";
            for(char ch : format) {
                address += ch;
            }
            URL request = new URL(stat, address + key);
            buffer = new BufferedReader(new InputStreamReader(request.openStream()));
            //TODO Check the status of the output
        //}
        //TODO Implement exponential backoff
        while ((response = buffer.readLine()) != null) {
            //TODO Parse the output
            System.out.println(response);
        }
        buffer.close();
    }
}