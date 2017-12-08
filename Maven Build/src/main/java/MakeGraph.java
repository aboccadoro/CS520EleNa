import org.geogig.osm.internal.OSMImportOp;
import org.geogig.osm.internal.OSMReport;
import com.google.common.base.Optional;
import org.locationtech.geogig.repository.Hints;
import org.locationtech.geogig.repository.impl.ContextBuilder;
import org.locationtech.geogig.repository.impl.GlobalContextBuilder;

public class MakeGraph {

    public static void main(String[] args) {
        OSMImportOp importop = new OSMImportOp();
        importop.setDataSource("MAways.osm.pbf");
        importop.setNoRaw(true);
        importop.setAdd(true);
        importop.setMapping(null);
        ContextBuilder context = GlobalContextBuilder.builder();
        Hints hints = Hints.readOnly();
        importop.setContext(context.build(hints));
        Optional<OSMReport> report = importop.call();
        System.out.println(report.isPresent());
    }
}
