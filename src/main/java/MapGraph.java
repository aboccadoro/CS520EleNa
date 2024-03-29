import java.util.ArrayList;
import java.util.PriorityQueue;

import com.graphhopper.routing.Dijkstra;
import com.graphhopper.util.Parameters.Algorithms;

public class MapGraph{


    //returns three MapPaths in an array; path 0 is the shortest path ignoring elevation,
    // 1 is the shortest path with minimal elevation, 
    //and 2 is the shortest path with maximal elevation.
   public static MapPath[] modifiedYens(MapNode start, MapNode end, float xpercent){
       int K = 1;
       ArrayList<MapPath> shortestList = new ArrayList<MapPath>();
       PriorityQueue<MapPath> potentialPaths = new PriorityQueue<MapPath>(40, new MapLengthComparator());
       shortestList.set(0, dijkstra(start, end));
       //replace with while loop with comparison statement with xpercent
       for(int k = 1; k < K; k++){

           for(int i = 0; i < shortestList.get(k - 1).getNumberNodes(); i++){
               MapNode spurNode = shortestList.get(k-1).getNode(i);
               MapPath rootPath = shortestList.get(k-1).getSubPath(0, i);

               for(int j = 0; j < shortestList.size(); j++){
                   MapPath p = shortestList.get(j);
                    if(Model.comparePaths(rootPath, p.getSubPath(0, i))){
                        p.setEdgeActive(i, i + 1, false);
                    }
               }
              // for each node rootPathNode in rootPath except spurNode:
              // remove rootPathNode from Graph;
               for(int j = 0; j < rootPath.getNumberNodes(); j++){
                   MapNode curr = rootPath.getNode(j);
                   if(!curr.equals(spurNode)){
                        rootPath.getNode(j).setactivated(false);
                   }
               }
               MapPath spurPath = dijkstra(spurNode, end);
               MapPath totalPath = MapPath.mergePaths(rootPath, spurPath);

               potentialPaths.add(totalPath);

               //activate all nodes and edges again
               for(int j = 0; j < rootPath.getNumberNodes(); j++){
                   MapNode curr = rootPath.getNode(j);
                   curr.setactivated(true);
                   DistPair[] list = curr.getPairs();
                   for(int l = 0; l < list.length;l++){
                        list[l].setActive(true);
                   }
               }
           }
           if(potentialPaths.isEmpty()){
               break;
           }
           shortestList.add(potentialPaths.poll());
           potentialPaths.clear();
       }

       MapPath result[] = new MapPath[3];
       result[0] = shortestList.get(0);
       result[1] = shortestList.get(0);
       result[2] = shortestList.get(0);
       for(int i = 0; i < shortestList.size(); i++){
           if(result[1].getElevationGain() > shortestList.get(i).getElevationGain()){
            result[1] = shortestList.get(i);
           }
           if(result[2].getElevationGain() < shortestList.get(i).getElevationGain()){
            result[2] = shortestList.get(i);
           }
        }
    return result; 
   }


   //placeholder dikjstra algorithm function
   public static MapPath dijkstra(MapNode start, MapNode end){
       return new MapPath(start);
   }
}