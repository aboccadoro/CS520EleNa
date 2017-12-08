import org.geogig.osm.internal.OSMImportOp;
import java.io.File;

public class MakeGraph {

    public static void main(String[] args) {
        OSMImportOp importop = new OSMImportOp();
        importop.setDataSource("MAways.osm.pbf");
        importop.setDownloadFile(new File(""));
    }
}
