import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class EleNaV {
    private JFrame gui = new JFrame("EleNa Test");

    private JPanel wrapper = new JPanel(new BorderLayout());
    private JPanel ioWrapper = new JPanel(new BorderLayout());

    private JPanel ioPanel = new JPanel(new SpringLayout());
    private JPanel mapPanel = new JPanel();
    private JPanel elevPanel = new JPanel(new FlowLayout());

    private JButton search = new JButton("Search");
    private JButton[] select = {new JButton("Select"), new JButton("Select")};

    private JTextField[] input = {new JTextField(), new JTextField()};

    private JLabel map = new JLabel();
    private JLabel elevGraph = new JLabel();

    private double[] center = {41.4928464,-73.761624};
    private int zoomLevel = 2;
    private String path = "";

    /*
     * Begin Controller Methods:
     * For mouse buttons,
     * 1 = left click
     * 3 = right click
     */
    private MouseListener clicker = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            double mpP = 156543.03392 * Math.cos(center[0] * Math.PI / 180) / Math.pow(2, zoomLevel);
            boolean render = false;
            // Temp code
            if(select[0].getActionCommand().equals("pressed") && e.getButton()==1){
                double[] latLong = {(center[0]+(mpP*(e.getY()-319)*-1)/110574),(center[1]+(mpP*(e.getX()-319))/(110574 * Math.cos(center[0] * Math.PI / 180)))};
                input[0].setText(latLong[0]+","+latLong[1]);
                select[0].setActionCommand("");
                select[1].setEnabled(true);
                render = true;
            }else if (select[1].getActionCommand().equals("pressed") && e.getButton()==1){
                double[] latLong = {(center[0]+(mpP*(e.getY()-319)*-1)/110574),(center[1]+(mpP*(e.getX()-319))/(110574 * Math.cos(center[0] * Math.PI / 180)))};
                input[1].setText(latLong[0]+","+latLong[1]);
                select[1].setActionCommand("");
                select[0].setEnabled(true);
                render = true;
            }
            // End Temp

            // MouseWheelListener not used to reduce amount of polls to Google
            if(e.getClickCount()>=2 && e.getButton()==1){
                zoomLevel++;
                render = true;
            }else if(e.getClickCount()>=2 && e.getButton()==3){
                zoomLevel--;
                render = true;
            }else if(e.getButton()==3 && e.getClickCount()==1 && e.isShiftDown()){
//                System.out.println((e.getX()-319)+","+(e.getY()-319)*-1);
//                System.out.println("Change in long (X):"+(mpP*(e.getX()-319))/110574);
//                System.out.println("Change in lat (Y):"+(mpP*(e.getY()-319)*-1)/110574);
//                System.out.println("Meters away from center (X):"+(mpP*(e.getX()-319)));
//                System.out.println("Meters away from center (Y):"+(mpP*(e.getY()-319)));
                // Around 0.02%-1.0% off depending on zoomLevel
                center[0] = (center[0]+(mpP*(e.getY()-319)*-1)/110574);
                center[1] = (center[1]+(mpP*(e.getX()-319))/(110574 * Math.cos(center[0] * Math.PI / 180)));
                System.out.println("Long:"+center[0]+"Lat:"+center[1]);
                render = true;
            }
            if(render){
                try{
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
            if(button == select[0]){
                select[1].setEnabled(false);
            }else{
                select[0].setEnabled(false);
            }
        }
    };

    private ActionListener radiiTest = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            BufferedReader reader;
            JsonObject json;
            JsonArray test = new JsonArray();
            String route = "";
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
                test = json.get("routes").getAsJsonArray();
                route = json.get("routes").getAsJsonArray().get(0).getAsJsonObject().get("overview_polyline").getAsJsonObject().get("points").getAsString();
                path = route;
                renderMap();
            } catch (IOException x) {
                System.exit(1);
            }
            System.out.println(test);
            System.out.println(route);
        }
    };

    /*
     * End Controller Methods.
     */

    // Helper Method
    private void ioPanel_init(){
        String[] labels = {"Origin: ", "Dest: "};
        for(int i = 0;i < 2; i++){
            select[i].addActionListener(poll);
            JLabel l = new JLabel(labels[i], JLabel.TRAILING);
            ioPanel.add(l);
            l.setLabelFor(input[i]);
            ioPanel.add(input[i]);
            ioPanel.add(select[i]);
        }
        search.addActionListener(radiiTest);
        SpringUtilities.makeCompactGrid(ioPanel, 2, 3, 6, 6, 6, 6);
        ioWrapper.add(ioPanel, BorderLayout.CENTER);
        ioWrapper.add(search, BorderLayout.LINE_END);
    }

    // Helper Method
    private void mapPanel_init(){
        mapPanel.setPreferredSize(new Dimension(640,640));
        map.addMouseListener(clicker);
        mapPanel.add(map);
    }

    // Helper Method
    private void elevPanel_init(){
        elevPanel.add(elevGraph);
    }

    private void wrapper_init(){
        wrapper.setPreferredSize(new Dimension(640, 885));
        wrapper.add(ioWrapper, BorderLayout.PAGE_START);
        wrapper.add(elevPanel, BorderLayout.PAGE_END);
        wrapper.add(mapPanel, BorderLayout.CENTER);
    }

    public EleNaV() throws IOException{
        renderMap();
        ioPanel_init();
        mapPanel_init();
        elevPanel_init();
        wrapper_init();
        gui.add(wrapper);
        gui.setVisible(true);
        gui.pack();
    }

    public void renderElevation(int samples) throws IOException {
        BufferedReader reader;
        JsonObject json;
        double maxElev = 0;
        double minElev = 100;
        double[] elevArray = new double[samples];
        // Redundant if elevation Data came from elsewhere
        try {
            String elevURL = "https://maps.googleapis.com/maps/api/elevation/json?"
                    + "path=enc:"
                    + path
                    + "&samples="
                    + samples;
            URL url = new URL(elevURL);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            JsonReader jr = new JsonReader(reader);
            JsonParser parser = new JsonParser();
            json = parser.parse(jr).getAsJsonObject();
            for (int i = 0; i < samples; i++) {
                elevArray[i] = json.get("results").getAsJsonArray().get(i).getAsJsonObject().get("elevation").getAsDouble();
                if(elevArray[i]>maxElev){
                    maxElev = elevArray[i]+100;
                }else if(elevArray[i]<minElev){
                    minElev = elevArray[i]-100;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // End of Redundancy
        try {
            String chartURL = "https://chart.apis.google.com/chart?"
                    + "cht=lc"
                    + "&chs=600x160"
                    + "&chl=Elevation"
                    + "&chco=orange"
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

    public void renderMap() throws IOException{
        try {
            String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?center="
                    + center[0]
                    + ","
                    + center[1]
                    + "&zoom="
                    + zoomLevel
                    + "&path=enc:"
                    + path
                    + "&size=640x640&scale=2&maptype=roadmap"
                    + "&markers=size:mid%7Ccolor:red%7C"
                    + center[0]
                    + ","
                    + center[1]
                    + "%7C"
                    + input[0].getText()+"%7C"
                    + input[1].getText()+"%7C";
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
        EleNaV frame = new EleNaV();
        frame.path = "exk|Flcz`MK~@M\\gAvBEd@B`@ZlAf@bDn@hCRj@x@zAbAxARPpBjAz@r@v@dATb@fBlEb@bAPn@\\pBZtAZh@`BjBXn@Jb@Dn@AfAFtBNfAJb@j@vAjBpEpJfPnAtA`DpCd@f@dAtAhAbBP^n@pBrC~DbDjEpAt@jCxAxB`BxAn@rAn@jA`@zBn@`@TnB~An@n@~AjBf@f@t@j@^XRZz@fBXf@hA|AvAzBt@bAlAvAj@t@r@lA`@~@j@`BXxFJfAPx@Vf@\\b@hBpA^d@Xj@Nj@F|@AtNAdAHhBNzA~@rG^hCh@`B`AvEr@dFl@|BvApEtBlIjCjNRfBFp@XlAp@jBLh@|BfIbAxDl@bB|@|BjAhFxAhDhPpWvD|FlAzAbAp@\\~ANbAFfA^hAlAdC`BlFbArH\\jBDZJPT`@\\\\\\LIb@E\\Cx@Bp@Fd@Nl@jAdC`A~Bz@bC`AxCj@rAfAxC|AxFp@bB~BtFVt@T|@L|@FhBDnCJbBVhB~@`Ff@`Bn@nArA|BlEjI^z@Rp@RfAJ|ATjLBjCIhBe@vEWjCMb@aAbCa@`AiApB]n@c@bAUt@]jA_@jBy@lFItA?r@JnAlArIZnBNn@Z~@Xr@nBnEtBhErAtCh@dBz@bDr@fC\\pAJv@D~AJjGLhAL`@Zp@~BfE~@vAjDbE`EjEdD|Cz@r@h@l@p@~@fCjEfAfBb@bAdDtK^vAN~@H|@JnCDxAO@ODg@XYX";
        frame.zoomLevel = 12;
        frame.renderMap();
        frame.renderElevation(20);
    }
}
