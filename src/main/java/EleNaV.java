import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.Border;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import com.google.maps.model.LatLng;
import com.google.maps.internal.PolylineEncoding;

public class EleNaV {
    private JFrame gui = new JFrame("EleNa Test");

    private JPanel wrapper = new JPanel(new BorderLayout());
    private JPanel ioWrapper = new JPanel(new BorderLayout());

    private JPanel ioPanel = new JPanel(new SpringLayout());
    private JPanel mapPanel = new JPanel();
    private JPanel elevPanel = new JPanel(new FlowLayout());

    private JPanel modePanel = new JPanel(new FlowLayout());

    private JButton search = new JButton("Search");
    private JButton[] select = {new JButton("Select"), new JButton("Select")};
    private JButton[] pathB = new JButton[3];

    private JTextField[] input = {new JTextField("42.391670,-71.170700"), new JTextField("42.380180,-71.200340")};

    private JLabel map = new JLabel();
    private JLabel elevGraph = new JLabel();

    private LatLng start = new LatLng(42.391670,-71.170700);
    private LatLng center = new LatLng(42.391670,-71.170700);
    private LatLng end = new LatLng(42.380180,-71.200340);
    private int zoomLevel = 8;
    private String[] path = {"","",""};
    private int pathMode = 0;

    private String markers = "";

    /*
     * Begin Controller Methods:
     * For mouse buttons,
     * 1 = left click
     * 3 = right click
     */
    private MouseListener clicker = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            double mpP = 156543.03392 * Math.cos(center.lat * Math.PI / 180) / Math.pow(2, zoomLevel);
            boolean render = false;
            // Temp code
            if (select[0].getActionCommand().equals("pressed") && e.getButton() == 1) {
                LatLng latLong = new LatLng((center.lat + (mpP * (e.getY() - 319) * -1) / 110574), (center.lng + (mpP * (e.getX() - 319)) / (110574 * Math.cos(center.lat * Math.PI / 180))));
                latLong.lat = Math.floor(latLong.lat * Math.pow(10, 5)) / Math.pow(10, 5);
                latLong.lng = Math.floor(latLong.lng * Math.pow(10, 5)) / Math.pow(10, 5);
                start = latLong;
                input[0].setText(latLong.toString());
                select[0].setActionCommand("");
                select[1].setEnabled(true);
                render = true;
            } else if (select[1].getActionCommand().equals("pressed") && e.getButton() == 1) {
                LatLng latLong = new LatLng((center.lat + (mpP * (e.getY() - 319) * -1) / 110574), (center.lng + (mpP * (e.getX() - 319)) / (110574 * Math.cos(center.lat * Math.PI / 180))));
                latLong.lat = Math.floor(latLong.lat * Math.pow(10, 5)) / Math.pow(10, 5);
                latLong.lng = Math.floor(latLong.lng * Math.pow(10, 5)) / Math.pow(10, 5);
                end = latLong;
                input[1].setText(latLong.toString());
                select[1].setActionCommand("");
                select[0].setEnabled(true);
                render = true;
            }
            // End Temp

            // MouseWheelListener not used to reduce amount of polls to Google
            if (e.getClickCount() >= 2 && e.getButton() == 1) {
                zoomLevel = Math.min(20, zoomLevel + 1);
                render = true;
            } else if (e.getClickCount() >= 2 && e.getButton() == 3) {
                zoomLevel = Math.max(1, zoomLevel - 1);
                render = true;
            } else if (e.getButton() == 3 && e.getClickCount() == 1 && e.isShiftDown()) {
                center.lat = (center.lat + (mpP * (e.getY() - 319) * -1) / 110574);
                center.lng = (center.lng + (mpP * (e.getX() - 319)) / (110574 * Math.cos(center.lat * Math.PI / 180)));
                center.lat = Math.floor(center.lat * Math.pow(10, 5)) / Math.pow(10, 5);
                center.lng = Math.floor(center.lng * Math.pow(10, 5)) / Math.pow(10, 5);
                System.out.println("LatLong:" + center.toString());
                render = true;
            }
            if (render) {
                try {
                    renderMap();
                } catch (IOException x) {
                    System.exit(1);
                }
            }
        }
    };

    private ActionListener poll = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            button.setActionCommand("pressed");
            if (button == select[0]) {
                select[1].setEnabled(false);
            } else {
                select[0].setEnabled(false);
            }
        }
    };

    private ActionListener pathMode_AL = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            pathMode = Integer.parseInt(button.getName());
            try{
                renderMap();
                renderElevation(100);
            }catch (IOException x){
                System.exit(1);
            }
        }
    };

    private ActionListener radiiTest = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            start.lat = Double.parseDouble(input[0].getText().split(",")[0]);
            start.lng = Double.parseDouble(input[0].getText().split(",")[1]);
            end.lat = Double.parseDouble(input[1].getText().split(",")[0]);
            end.lng = Double.parseDouble(input[1].getText().split(",")[1]);
            center.lat = (start.lat+end.lat)/2;
            center.lng = (start.lng+end.lng)/2;
            double distTo = Math.sqrt(Math.pow(start.lat - end.lat, 2) + Math.pow(start.lng - end.lng, 2));
            //Math.cos(center.lat * Math.PI / 180) / mpp  =  Math.pow(2, zoomLevel)
            zoomLevel = (int)(Math.ceil(Math.log(Math.cos(center.lat * Math.PI / 180)/(distTo/640))/Math.log(2)));
            System.out.println(zoomLevel);

            try{
                //getPath();
                getGHPath();
                renderMap();
                renderElevation(100);
            }catch (IOException x){
                System.exit(1);
            }
        }
    };

    private void getPath(){
        BufferedReader reader;
        JsonObject json;
        String route;
//        NodeGraph node;
        try {
            String graphURL = "https://maps.googleapis.com/maps/api/directions/json?origin="
                    + input[0].getText()
                    + "&destination="
                    + input[1].getText()
                    + "&mode=walking";
            URL url = new URL(graphURL);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            JsonReader jr = new JsonReader(reader);
            JsonParser parser = new JsonParser();
            json = parser.parse(jr).getAsJsonObject();
            route = json.get("routes").getAsJsonArray().get(0).getAsJsonObject().get("overview_polyline").getAsJsonObject().get("points").getAsString();

            // Experimental Code # Uncomment to crash
            //node = new NodeGraph(start, end, json.get("routes").getAsJsonArray().get(0).getAsJsonObject().get("legs").getAsJsonArray().get(0).getAsJsonObject().get("distance").getAsJsonObject().get("value").getAsInt());
            //node.roadPoints().toString();
            //markers = node.toString();
            //System.out.println(node.toString());
            // End
            path[0] = route;
        } catch (IOException x) {
            System.exit(1);
        }
    }

    private void getGHPath(){
        LinkedList<LatLng>[] in = graphHopperTest.pather(start,end);
        for(int i=0;i<3;i++){
            path[i] = PolylineEncoding.encode(in[i]);
        }
        System.out.println(markers);
    }

    /*
     * End Controller Methods.
     */

    // Helper Method
    private void ioPanel_init() {
        String[] labels = {"Origin: ", "Dest: "};
        for (int i = 0; i < 2; i++) {
            select[i].addActionListener(poll);
            select[i].setToolTipText("Click a point on the map.");
            JLabel l = new JLabel(labels[i], JLabel.TRAILING);
            ioPanel.add(l);
            l.setLabelFor(input[i]);
            ioPanel.add(input[i]);
            ioPanel.add(select[i]);
        }
        String[] temp = {"Shortest Path", "Most Elevation Gain", "Least Elevation Gain"};
        for(int i=0;i<3;i++){
            pathB[i] = new JButton(temp[i]);
            pathB[i].addActionListener(pathMode_AL);
            pathB[i].setName(Integer.toString(i));
            modePanel.add(pathB[i]);
        }
        search.addActionListener(radiiTest);
        SpringUtilities.makeCompactGrid(ioPanel, 2, 3, 6, 6, 6, 6);
        ioWrapper.add(ioPanel, BorderLayout.CENTER);
        ioWrapper.add(search, BorderLayout.LINE_END);
        ioWrapper.add(modePanel, BorderLayout.PAGE_END);
    }

    // Helper Method
    private void mapPanel_init() {
        mapPanel.setPreferredSize(new Dimension(640, 640));
        map.addMouseListener(clicker);
        mapPanel.add(map);
    }

    // Helper Method
    private void elevPanel_init() {
        elevPanel.add(elevGraph);
    }

    private void wrapper_init() {
        wrapper.setPreferredSize(new Dimension(640, 885));
        wrapper.add(ioWrapper, BorderLayout.PAGE_START);
        wrapper.add(elevPanel, BorderLayout.PAGE_END);
        wrapper.add(mapPanel, BorderLayout.CENTER);
    }

    public EleNaV() throws IOException {
        renderMap();
        ioPanel_init();
        mapPanel_init();
        elevPanel_init();
        wrapper_init();
        gui.add(wrapper);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
        gui.pack();
    }

    public void renderElevation(int samples) throws IOException {
        BufferedReader reader;
        JsonObject json;
        double maxElev = 0;
        double minElev = 100000;
        double[] elevArray = new double[samples];
        // Redundant if elevation Data came from elsewhere
        try {
            String elevURL = "https://maps.googleapis.com/maps/api/elevation/json?"
                    + "path=enc:" + path[pathMode]
                    + "&samples=" + samples;
            URL url = new URL(elevURL);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            JsonReader jr = new JsonReader(reader);
            JsonParser parser = new JsonParser();
            json = parser.parse(jr).getAsJsonObject();
            System.out.println(json);
            for (int i = 0; i < samples; i++) {
                elevArray[i] = json.get("results").getAsJsonArray().get(i).getAsJsonObject().get("elevation").getAsDouble();
                if (elevArray[i] > maxElev) {
                    maxElev = elevArray[i] + 100;
                } else if (elevArray[i] < minElev) {
                    minElev = elevArray[i] - 100;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // End of Redundancy
        try {
            String chartURL = "https://chart.apis.google.com/chart?"
                    + "cht=lc&chs=600x160&chl=Elevation&chco=orange"
                    + "&chds="
                    + minElev + "," + maxElev
                    + "&chxt=x,y"
                    + "&chxr=1,"
                    + minElev + "," + maxElev
                    + "&chd=t:"
                    + elevArray[0];
            for (int i = 1; i < samples; i++) {
                chartURL += "," + elevArray[i];
            }
            String destFile = "elev.jpg";
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
                .getImage().getScaledInstance(640, 160,
                        java.awt.Image.SCALE_SMOOTH));
        elevGraph.setIcon(elevIcon);
    }

    public void renderMap() throws IOException {
        try {
            String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?center="
                    + center.toString()
                    + "&zoom=" + zoomLevel
                    + "&path=enc:" + path[pathMode]
                    + "&size=640x640&scale=2&maptype=roadmap"
                    + "&markers=size:mid|color:blue|" + input[0].getText()
                    + "&markers=size:mid|color:red|" + input[1].getText()
                    + "&markers=size:small|color:green|" + markers
                    + "&key=AIzaSyAO1sUMxigsq0jqP_p9t5PVDtb25Jfu5Zc";
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
    public static void main(String[] args) throws IOException {
        EleNaV frame = new EleNaV();
        frame.renderMap();
    }
}