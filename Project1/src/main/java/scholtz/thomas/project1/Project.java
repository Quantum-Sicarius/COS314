package scholtz.thomas.project1;

import java.nio.*;
import java.util.*;

class Project {
    private String fileToRead;
    private Integer searchMethod;

    private Graph g;

    public Project() {
        System.out.println("Starting:");
        System.out.println("===============================");
        System.out.println("Select problem:");
        System.out.println("1) wi29");
        System.out.println("2) dj38");
        System.out.println("3) eil51");
        System.out.println("4) small");
        System.out.println("5) other file");
        System.out.println("===============================\n");

        Scanner sc = new Scanner(System.in);
        int value = 0;
        if(sc.hasNextLine()) {
            value = sc.nextInt();
        }
        
        if(value == 1) {
            System.out.println("You selected option wi29!");
            this.fileToRead = "wi29.tsp";
            printProblemSearchMethod();

        } else if (value == 2) {
            System.out.println("You selected option dj38!");
            this.fileToRead = "dj38.tsp";
            printProblemSearchMethod();

        } else if (value == 3) {
            System.out.println("You selected option eil51!");
            this.fileToRead = "eil51.tsp";
            printProblemSearchMethod();

        } else if (value == 4) {
            System.out.println("You selected option small!");
            this.fileToRead = "small.tsp";
            printProblemSearchMethod();
    
        } else if (value == 5) {
            System.out.println("You selected option other!");
            System.out.println("Enter file name (exapmle.tsp): ");

            String otherFileName = sc.next().trim();
            this.fileToRead = otherFileName;
            printProblemSearchMethod();
    
        } else {
            System.out.println("Invalid option!");
            return;
        }
    }

    private void printProblemSearchMethod() {
        System.out.println("\n===============================");
        System.out.println("Select search method:");
        System.out.println("1) Depth first search with iterative deepening");
        System.out.println("2) Breadth first search");
        System.out.println("3) A* Algorithm");
        System.out.println("===============================\n");

        Scanner sc = new Scanner(System.in);
        int value = 0;

        while(true) {
            if(sc.hasNextLine()) {
                value = sc.nextInt();
            }
            
            if(value == 1) {
                System.out.println("You selected 'Depth first search with iterative deepening'");
                this.searchMethod = 1;
                break;

            } else if (value == 2) {
                System.out.println("You selected 'Breadth first search'");
                this.searchMethod = 2;
                break;
            } else if (value == 3) {
                System.out.println("You selected 'A* Algorithm'");
                this.searchMethod = 3;
                break;
            } else {
                System.out.println("Invalid option " + value + " try again!");
            }
        }

        start();
    }

    private void start() {
        FileReader f = new FileReader(fileToRead);

        System.out.println("Start building graph...");

        g = new Graph();

        for (City c : f.getCities()) {
            g.addNewVertex(c.getName(), c.getXCo(), c.getYCo());
        }

        System.out.println("Graph building done!");
        System.out.println("Starting search!");

        if(searchMethod == 1) {
            if(!memoryCheck()) {
                System.out.println("Do you want to continue? [Y/n]");
                Scanner sc = new Scanner(System.in);
                String value = "";
                value = sc.next();
                if(value.trim().toUpperCase().compareTo("Y") != 0) {
                    return;
                }
            }
            long startTime = System.nanoTime();
            depthFirstSearchWithIterativeDeepening();
            long endTime = System.nanoTime();
            long duration = (endTime - startTime)/1000000;

            System.out.println("Algorithm took: " + duration + " milliseconds!");
        }
        else if(searchMethod == 2) {
            if(!memoryCheck()) {
                System.out.println("Do you want to continue? [Y/n]");
                Scanner sc = new Scanner(System.in);
                String value = "";
                value = sc.next();
                if(value.trim().toUpperCase().compareTo("Y") != 0) {
                    return;
                }
            }
            long startTime = System.nanoTime();
            breadthFirstSearch();
            long endTime = System.nanoTime();
            long duration = (endTime - startTime)/1000000;

            System.out.println("Algorithm took: " + duration + " milliseconds!");
        }
        else if(searchMethod == 3) {
            long startTime = System.nanoTime();
            AStar();
            long endTime = System.nanoTime();
            long duration = (endTime - startTime)/1000000;

            System.out.println("Algorithm took: " + duration + " milliseconds!");
        }
    }

    private static long factorial(int number) {
        long result = 1;

        for (int factor = 2; factor <= number; factor++) {
            result *= factor;
        }

        return result;
    }

    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private boolean memoryCheck() {
        // In a modern 64-bit JDK, an object has a 12-byte header, padded to a multiple of 8 bytes, so the minimum object size is 16 bytes.
        Long estimatedBytes = (factorial(g.getVertices().size() - 1) * (16+8) * (16));
        Long available = (Runtime.getRuntime().maxMemory());
        
        if(estimatedBytes < 0 || estimatedBytes > available) {
            System.out.println("==============================================================");
            System.out.println("You are attempting a exhuastive search with too little memory!"); 
            System.out.println("In a modern 64-bit JDK, an object has a 12-byte header, padded to a multiple of 8 bytes, so the minimum object size is 16 bytes."); 
            System.out.println("Considering my not so memory efficient implemmentation you will run out of memory and crash!"); 
            System.out.println("You will be attempting to use over: " + humanReadableByteCount(Math.abs(estimatedBytes), false)); 
            System.out.println("The JVM only has: " + humanReadableByteCount(Runtime.getRuntime().maxMemory(), false) + " available for this program!"); 
            System.out.println("==============================================================");

            return false;
        }

        return true;
    }

    private ArrayList<Path> depthFirstSearch(Vertex start,int depthLimit) {
        if( depthLimit == 0) {
            return null;
        }

        // We know our goal is our starting position so all we actually want to do is go through all our nodes.
        Deque<Pair<Path, Vertex>> toVisit = new LinkedList<Pair<Path, Vertex>>();

        ArrayList<Path> paths = new ArrayList<Path>();

        Path p = new Path();
        p.add(start);
        Path localPath = p;

        Pair<Path, Vertex> startPair = new Pair<Path, Vertex>();
        startPair.first = p;
        startPair.second = start;

        toVisit.add(startPair);

        while(!toVisit.isEmpty()) {
            Pair<Path, Vertex> currentPair = toVisit.remove();

            localPath = currentPair.first;
            Vertex currentVertex = currentPair.second;

            //System.out.print("PathID: " + localPath.toString() + "\tCurrent vertex " + currentVertex.getName()); 
            //System.out.println();

            if(localPath.currentPath.size() - 1 >= depthLimit) {
                continue;
            }

            ArrayList<Vertex> neighbors = currentVertex.getNeighbors();
            // Each neighbor is a new path.
            for (Vertex neighbor : neighbors) {
                // If the neighbor is not in the current path make a new path and add to queue
                if(!localPath.currentPath.contains(neighbor)) {
                    Path newPath = new Path(localPath);
                    newPath.add(neighbor);
                    paths.add(newPath);
                    Pair<Path, Vertex> newPair = new Pair<Path, Vertex>();
                    newPair.first = newPath;
                    newPair.second = neighbor;

                    toVisit.offerFirst(newPair);
                }
            }
            
        }


        ArrayList<Path> finalPaths = new ArrayList<Path>();

        for (int i = 0; i < paths.size(); i++) {
            if(paths.get(i).currentPath.size() - 1 >= depthLimit) {
                finalPaths.add(paths.get(i));
            }
        }

        return finalPaths;
    }

    private void depthFirstSearchWithIterativeDeepening() {

        Vertex start = g.getVertices().get(0);
        for (int i = 1; i < g.getVertices().size(); i++) {
            ArrayList<Path> solutions = depthFirstSearch(start, i);
            int totalSolutions = 0;
            for(Path sol: solutions) {
                if(sol.currentPath.size() == this.g.getVertices().size()) {
                    totalSolutions++;
                }
            }
            if(totalSolutions > 0) {
                System.out.println("Found " + totalSolutions + " potential solutions at Depth = " + i);

                Path bestPath = solutions.get(0);

                for(Path sol: solutions) {
                    if((bestPath.getTotalDistance() + bestPath.getLastAdded().getDistanceTo(start)) > (sol.getTotalDistance() + sol.getLastAdded().getDistanceTo(start))) {
                        bestPath = sol;
                    }
                }
                System.out.println("Best solution: " + bestPath + start.getName() + " with a distance of: " + Math.round((bestPath.getTotalDistance() + bestPath.getLastAdded().getDistanceTo(start))));
                System.out.println();
                break;
            } else {
                System.out.println("No solutions have been found yet! Depth = " + i);
            }
        }

    }

    private void breadthFirstSearch() {
        /*
        A - B
        | \ |
        C - D
        */

        Vertex start = g.getVertices().get(0);
        start.currentDistanceFromStart = 0;

        Queue<Pair<Path,Vertex>> toVisit = new LinkedList<Pair<Path,Vertex>>();

        ArrayList<Path> paths = new ArrayList<Path>();

        Path p = new Path();
        p.add(start);
        Path localPath = p;

        Pair<Path, Vertex> startPair = new Pair<Path, Vertex>();
        startPair.first = p;
        startPair.second = start;

        toVisit.add(startPair);
        // Now we have a queue with [[BCD],[CDB],[DBC]]
        // Where B, C, D is our second branch
        // so we must deque the first item from each of our queues.

        while(!toVisit.isEmpty()) {
            Pair<Path,Vertex> currentPair = toVisit.remove();

            localPath = currentPair.first;
            Vertex currentVertex = currentPair.second;

            //System.out.print("PathID: " + localPath.toString() + "\tCurrent vertex " + currentVertex.getName()); 
            //System.out.println();

            ArrayList<Vertex> neighbors = currentVertex.getNeighbors();
            // Each neighbor is a new path.
            for (Vertex neighbor : neighbors) {
                // If the neighbor is not in the current path make a new path and add to queue
                if(!localPath.currentPath.contains(neighbor)) {
                    Path newPath = new Path(localPath);
                    newPath.add(neighbor);
                    paths.add(newPath);
                    Pair<Path, Vertex> newPair = new Pair<Path, Vertex>();
                    newPair.first = newPath;
                    newPair.second = neighbor;

                    toVisit.add(newPair);
                }
            }
        }

        ArrayList<Path> finalPaths = new ArrayList<Path>();

        for (int i = 0; i < paths.size(); i++) {
            if(paths.get(i).currentPath.size() >= g.getVertices().size()) {
                finalPaths.add(paths.get(i));
            }
        }

        Path bestPath = finalPaths.get(0);

        for (Path path : finalPaths) {
            if((bestPath.getTotalDistance() + bestPath.getLastAdded().getDistanceTo(start)) > (path.getTotalDistance() + path.getLastAdded().getDistanceTo(start))) {
                bestPath = path;
            }
            //System.out.println("Path: " + path.toString() + " with distance: " + (path.getTotalDistance() + path.getLastAdded().getDistanceTo(start)));
        }

        System.out.println();
        System.out.println("Totals potential solutions: " + finalPaths.size());
        System.out.println("Best solution: " + bestPath + start.getName() + " with a distance of: " + Math.round((bestPath.getTotalDistance() + bestPath.getLastAdded().getDistanceTo(start))));
        System.out.println();
    }

    class Subset
    {
        Vertex originalVertex;
        Vertex parent;
        int rank;
    };

    Subset find(ArrayList<Subset> subsets, Vertex vert)
    {
        // find root and make root as parent of i (path compression)
        for (Subset subset : subsets) {
            if(subset.originalVertex == vert) {
                for (Subset parentSubset : subsets) {
                    if(subset.parent == parentSubset.originalVertex) {
                        return parentSubset;
                    }
                }
            }
        }

        return null;
    }
 
    // A function that does union of two sets of x and y
    // (uses union by rank)
    private void Union(ArrayList<Subset> subsets, Subset xroot, Subset yroot) {
        // Attach smaller rank tree under root of high rank tree
        // (Union by Rank)
        if (xroot.rank < yroot.rank)
            xroot.parent = yroot.parent;
        else if (xroot.rank > yroot.rank)
            yroot.parent = xroot.parent;
 
        // If ranks are same, then make one as root and increment
        // its rank by one
        else
        {
            yroot.parent = xroot.parent;
            xroot.rank++;
        }
    }

    private double getMSTDistanceOfUnvisisted(ArrayList<Vertex> unvisited) {
        /*
        sort the edges of G in increasing order of cost
        keep a subgraph S of G, initially empty
        for each edge e in sorted order
            if the endpoints of e are disconnected in S (i.e., does not form a cycle)
                add e to S
        return S
         */

        Graph subGraph = new Graph();

        ArrayList<Edge> sortedEdges = new ArrayList<Edge>();

        for (Vertex v : unvisited) {
            for (Edge e : v.getEdges()) {
                if(!sortedEdges.contains(e)) {
                    if(unvisited.contains(e.getVertexA()) && unvisited.contains(e.getVertexB())) {
                            sortedEdges.add(e);
                    }
                }
            }
        }

        Collections.sort(sortedEdges, new Comparator<Edge>() {
            @Override
            public int compare(Edge lhs, Edge rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getDistance() < rhs.getDistance() ? -1 : (lhs.getDistance() > rhs.getDistance()) ? 1 : 0;
            }
        });


        ArrayList<Subset> subsets = new ArrayList<Subset>();
        for (Vertex v : unvisited) {
            Subset s = new Subset();
            s.originalVertex = v;
            s.parent = v;
            s.rank = 0;

            subsets.add(s);
        }

        for (Edge e : sortedEdges) {
            //System.out.println("Edge weight: " + e.getDistance() + " A: " + e.getVertexA().getName() + " B: " + e.getVertexB().getName());

            Subset xroot = find(subsets, e.getVertexA());
            Subset yroot = find(subsets, e.getVertexB());
 
            // If including this edge does't cause cycle,
            // include it in result and increment the index 
            // of result for next edge
            if (xroot.parent != yroot.parent)
            {
                //result[e++] = next_edge;
                subGraph.AddManualVertex(e);
                Union(subsets, xroot, yroot);
            }
        }

        /*for (Vertex v : subGraph.getVertices()) {
            System.out.print("Vertex: " + v.getName() + " has " + v.getEdges().size() + " edges, " + " neighbors: [");
            for (Vertex n : v.getNeighbors()) {
                System.out.print(n.getName()+ ",");
            }
            System.out.print("]");
            System.out.println();
        }*/

        //System.out.println("SubGraph has " + subGraph.getEdges().size() + " edges with " + subGraph.getVertices().size());

        if(subGraph.getVertices().size() == 0) {
            return 0;
        }

        double heuristicDistance = 0.0;

        for (Edge e : subGraph.getEdges()) {
            heuristicDistance += e.getDistance();
        }

        //System.out.println("Heuristic distance: " + heuristicDistance);

        return heuristicDistance;
    }

    private void AStar() {
        ArrayList<Vertex> unvisited = new ArrayList<Vertex>();
        for (Vertex v : this.g.getVertices()) {
            unvisited.add(v);
        }

        Queue<Pair<Path,Vertex>> compToVisit = new LinkedList<Pair<Path,Vertex>>();
        Path localPath = new Path();
        Vertex start = g.getVertices().get(0);
        localPath.add(start);

        Pair<Path,Vertex> startPair = new Pair<Path,Vertex>();
        startPair.first =localPath;
        startPair.second = start;

        compToVisit.add(startPair);

        ArrayList<Path> paths = new ArrayList<Path>();

        while(!compToVisit.isEmpty()) {
            Pair<Path,Vertex> curPair = compToVisit.remove();

            localPath = curPair.first;
            Vertex curVertex = curPair.second;

            unvisited.remove(curVertex);

            ArrayList<Vertex> neighbors = curVertex.getNeighbors();

            Vertex bestNext = neighbors.get(0);

            double MSTHeuristic = getMSTDistanceOfUnvisisted(unvisited);

            double bestHeuristicDistance = Double.POSITIVE_INFINITY;
            //System.out.println("Best Heuristic Distance: " + bestHeuristicDistance);

            /*
                Here we need to simulate each node and get their h(n) value
            */

            for (Vertex neighbor : neighbors) {
                if (!localPath.currentPath.contains(neighbor)) {
                    //System.out.println("Vertex: " + localPath.getLastAdded().getName() + " has neighbor: " + neighbor.getName());

                    // h(n): distance to the nearest unvisited city from the current city 
                    // + estimated distance to travel all the unvisited cities (MST heuristic used here) 
                    // + nearest distance from an unvisited city to the start city.
                    double NearestDistanceUnvisitedToStart = Double.POSITIVE_INFINITY;

                    ArrayList<Vertex> tempUnvisited = new ArrayList<Vertex>();
                    for (Vertex temp : unvisited) {
                        if(temp != neighbor) {
                            tempUnvisited.add(temp);
                        }
                    }

                    for (Vertex v : tempUnvisited) {
                        if(NearestDistanceUnvisitedToStart > v.getDistanceTo(start)) {
                            NearestDistanceUnvisitedToStart = v.getDistanceTo(start);
                        }
                    }

                    double heuristic = curVertex.getDistanceTo(neighbor) + MSTHeuristic + NearestDistanceUnvisitedToStart;

                    if (heuristic <= bestHeuristicDistance) {
                        //System.out.println("Better neighbor found " + heuristic + " Neighbor: " + neighbor.getName());
                        bestHeuristicDistance = heuristic;
                        bestNext = neighbor;
                    }
                }

            }

            if(!localPath.currentPath.contains(bestNext)) {
                Path newPath = new Path(localPath);
                newPath.add(bestNext);

                Pair<Path,Vertex> newPair = new Pair<Path,Vertex>();
                newPair.first = newPath;
                newPair.second = bestNext;

                compToVisit.add(newPair);

                paths.add(newPath);
            }
        }

        ArrayList<Path> finalPaths = new ArrayList<Path>();

        for (int i = 0; i < paths.size(); i++) {
            if(paths.get(i).currentPath.size() >= g.getVertices().size()) {
                finalPaths.add(paths.get(i));
            }
        }

        Path bestPath = finalPaths.get(0);

        for (Path path : finalPaths) {
            if((bestPath.getTotalDistance() + bestPath.getLastAdded().getDistanceTo(start)) > (path.getTotalDistance() + path.getLastAdded().getDistanceTo(start))) {
                bestPath = path;
            }
            //System.out.println("Path: " + path.toString() + " with distance: " + (path.getTotalDistance() + path.getLastAdded().getDistanceTo(start)));
        }

        System.out.println();
        System.out.println("Totals potential solutions: " + finalPaths.size());
        System.out.println("Best solution: " + bestPath + start.getName() + " with a distance of: " + Math.round((bestPath.getTotalDistance() + bestPath.getLastAdded().getDistanceTo(start))));
        System.out.println();
    }
}