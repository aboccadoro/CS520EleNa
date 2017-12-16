Project EleNa

EleNa is a map-based pathfinding application that takes elevation gain into account when creating paths. 

EleNa requires an internet connection and Apache Ant to operate.

To build the application,  run ant with no parameters, i.e. simply type "ant". A java jar file named CS520EleNa.jar will appear; simply double click it to access the application.
You will be brought to the UI.

Users Guide

Double click anywhere on the map to zoom in; double right click to zoom out.

At the top of the window will be two coordinates: an origin, and a destination, and each coordinate has a 'select' button. Click on either of these buttons, then click anywhere on the map. The location will snap automatically to a road.
Once each point is selected, click the 'search' button, and three paths will be available for viewing: the shortest path (without taking elevation into account), the shortest path with maximal elevation gain, and the shortest path with minimal elevation gain.
Click on each of the buttons to view that path.

Behind the Scenes

This application uses graphHopper to build and traverse a graph, representing the map and its roads. The map itself is rendered using the GoogleMaps api.
The paths are found using a modified Yen's algorithm; we find N-shortest paths, keeping track of how much elevation each path has gained. Then, we pull the paths with the least gain and most gain as appropriate.
Note that we are using graphHopper's internal search tools for this, though there exists a modified Yen's Algorithm in java in this project, which is what more or less happens in graphHopper.