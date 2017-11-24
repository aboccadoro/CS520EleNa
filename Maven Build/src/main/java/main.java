import com.google.gson.JsonObject;

public class main {

    public static void main(String[] arga) {
        EleNa test = new EleNa();
        JsonObject json = test.getJson();
        System.out.println(test.getCoords(json));
    }
}
