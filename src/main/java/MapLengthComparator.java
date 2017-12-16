import java.util.Comparator;

public class MapLengthComparator implements Comparator<MapPath>{

    public MapLengthComparator(){

    }

    public int compare(MapPath a, MapPath b){
        if(a.getTotalDistance() < b.getTotalDistance()){
            return -1;
        }
        if(a.getTotalDistance() == b.getTotalDistance()){
            return 0;
        }
        else{
            return 1;
        }
    }
}