import java.util.ArrayList;
import java.util.PriorityQueue;

import com.graphhopper.routing.Dijkstra;
import com.graphhopper.util.Parameters.Algorithms;

public class MapGraph{


   public static MapPath[] modifiedYens(MapNode start, MapNode end, float xpercent){
       int K = 1;
       ArrayList<MapPath> shortestList = new ArrayList<MapPath>();
       PriorityQueue<MapPath> potentialPaths = new PriorityQueue<MapPath>(40, new MapLengthComparator());
       shortestList.set(0, Algorithms.dijkstra(start, end));
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
               MapPath totalPath = MapPath.mergePaths(totalPath, spurPath);

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
   }

}