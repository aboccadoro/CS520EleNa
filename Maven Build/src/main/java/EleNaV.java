import java.awt.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;


public class EleNaV {
    private JFrame gui = new JFrame("EleNa Test");

    private JPanel wrapper = new JPanel(new BorderLayout());
    private JPanel ioWrapper = new JPanel(new BorderLayout());

    private JPanel ioPanel = new JPanel(new SpringLayout());
    private JPanel mapPanel = new JPanel(new GridLayout());
    private JPanel elevPanel = new JPanel(new FlowLayout());

    private JButton search = new JButton("Search");
    private JButton reset = new JButton("Reset");
    private JButton[] select = {new JButton("Select"), new JButton("Select")};
    private JButton[] zoom;

    private JTextField[] input = {new JTextField(), new JTextField()};

    private JLabel map = new JLabel();
    private JLabel elevGraph = new JLabel();

    // Helper Method
    private void ioPanel_init(){
        String[] labels = {"Origin: ", "Dest: "};
        for(int i = 0;i < 2; i++){
            JLabel l = new JLabel(labels[i], JLabel.TRAILING);
            ioPanel.add(l);
            l.setLabelFor(input[i]);
            ioPanel.add(input[i]);
            ioPanel.add(select[i]);
        }
        SpringUtilities.makeCompactGrid(ioPanel, 2, 3, 6, 6, 6, 6);
        ioWrapper.add(ioPanel, BorderLayout.CENTER);
        ioWrapper.add(search, BorderLayout.LINE_END);
    }

    // GroupLayout Test
    private void ioPanel_init2(){
        GroupLayout layout = new GroupLayout(ioPanel);
    }

    // Helper Method
    private void mapPanel_init(){
        mapPanel.add(map);
    }

    // Helper Method
    private void elevPanel_init(){
        elevPanel.add(elevGraph);
    }

    private void wrapper_init(){
        wrapper.add(ioWrapper, BorderLayout.PAGE_START);
        wrapper.add(mapPanel, BorderLayout.CENTER);
        wrapper.add(elevPanel, BorderLayout.PAGE_END);
    }

    public EleNaV() throws IOException{
        double[] center = {0,0};
        renderMap(center, 2, "");
        ioPanel_init();
        mapPanel_init();
        elevPanel_init();
        wrapper_init();
        gui.add(wrapper);
        gui.setVisible(true);
        gui.pack();
    }

    public void renderElevation(String path, int samples) throws IOException {
        BufferedReader reader;
        JsonObject json;
        double[] elevArray = new double[samples];
        String parsedElev = "t:";
        try {
            String elevURL = "https://maps.googleapis.com/maps/api/elevation/json?"
                    + "path="
                    + path
                    + "&samples="
                    + samples;
            String destFile = "elev.jpg";
            URL url = new URL(elevURL);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            JsonReader jr = new JsonReader(reader);
            JsonParser parser = new JsonParser();
            json = parser.parse(jr).getAsJsonObject();
            for (int i = 0; i < samples; i++) {
                elevArray[i] = json.get("results").getAsJsonArray().get(i).getAsJsonObject().get("elevation").getAsDouble();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            String chartURL = "https://chart.apis.google.com/chart?"
                    + "cht=lc"
                    + "&chs=600x160"
                    + "&chl=Elevation"
                    + "&chco=orange"
                    + "&chds=-500,5000"
                    + "&chxt=x,y"
                    + "&chxr=1,-500,5000&chd=t:"
                    + elevArray[0];
            for (int i = 1; i < samples; i++) {
                chartURL += "," + elevArray[i];
            }
            String destFile = "elev.jpg";
            System.out.print(chartURL);
            URL url = new URL(chartURL);
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(destFile);
            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        ImageIcon elevIcon = new ImageIcon((new ImageIcon("elev.jpg"))
                .getImage().getScaledInstance(600, 160,
                        java.awt.Image.SCALE_SMOOTH));
        elevGraph.setIcon(elevIcon);
    }

    public void renderMap(double[] center, int zoom, String path) throws IOException{
        double latitude = center[0];
        double longitude = center[1];
        try {
            String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?center="
                    + latitude
                    + ","
                    + longitude
                    + "&zoom="
                    + zoom
                    + "&path="
                    + path
                    + "&size=640x640&scale=2&maptype=roadmap";
            String destFile = "image.jpg";
            URL url = new URL(imageUrl);
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(destFile);
            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        ImageIcon mapIcon = new ImageIcon((new ImageIcon("image.jpg"))
                .getImage().getScaledInstance(640, 640,
                        java.awt.Image.SCALE_SMOOTH));
        map.setIcon(mapIcon);
    }

    public static void main(String[] args) throws IOException{
        double[] center = {36.23998,-116.83171};
        String path = "36.578581,-118.291994|36.23998,-116.83171";
        //String path = "40.737102,-73.990318|40.749825,-73.987963|40.752946,-73.987384|40.755823,-73.986397";
        int zoom = 13;
        EleNaV frame = new EleNaV();
        frame.renderMap(center, zoom, path);
        frame.renderElevation(path, 100);
        // show the GUI window
    }
}
