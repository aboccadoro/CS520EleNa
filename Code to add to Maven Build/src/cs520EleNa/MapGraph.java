package cs520EleNa;

import java.util.ArrayList;
import java.util.PriorityQueue;

import javafx.scene.shape.Path;

public class MapGraph{

    ArrayList<MapNode> adjList;
    public MapGraph(){
        adjList = new ArrayList<MapNode>();
    }

    public ArrayList<MapNode> ModifiedYens(Path shortest, MapNode start, MapNode end, float xpercent){
        ArrayList<MapPath> A = new ArrayList<Path>(shortest);

        int K = 1;
        PriorityQueue<MapPath> potentialPaths = new PriorityQueue<MapPath>();
        for(int k = 0; k < K; k++){
            for(int i = 0; i < spurList.get(k-1).getTotalDistance()){
                
            }
        }
    }

}