import java.nio.*;
import java.util.*;
import java.math.BigInteger;

class Project1 {
    private String fileToRead;
    private Integer searchMethod;

    private Graph g;

    public Project1() {
        System.out.println("Starting:");
        System.out.println("============================");
        System.out.println("Select problem:");
        System.out.println("1) wi29");
        System.out.println("2) dj38");
        System.out.println("3) eil51");
        System.out.println("4) small");
        System.out.println("5) other file");
        System.out.println("============================\n");

        Scanner sc = new Scanner(System.in);
        int value = 0;
        if(sc.hasNextLine()) {
            value = sc.nextInt();
        }
        
        if(value == 1) {
            System.out.println("You selected option 1!");
            this.fileToRead = "wi29.tsp";
            printProblemSearchMethod();

        } else if (value == 2) {
            System.out.println("You selected option 2!");
            this.fileToRead = "dj38.tsp";
            printProblemSearchMethod();

        } else if (value == 3) {
            System.out.println("You selected option 3!");
            this.fileToRead = "eil51.tsp";
            printProblemSearchMethod();

        } else if (value == 4) {
            System.out.println("You selected option 4!");
            this.fileToRead = "small.tsp";
            printProblemSearchMethod();
    
        } else {
            System.out.println("Invalid option!");
            return;
        }
    }

    private void printProblemSearchMethod() {
        System.out.println("\n============================");
        System.out.println("Select search method:");
        System.out.println("1) Depth first search with iterative deepening");
        System.out.println("2) Breadth first search");
        System.out.println("3) A* Algorithm");
        System.out.println("============================\n");

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
            depthFirstSearchWithIterativeDeepening();
        }
        else if(searchMethod == 2) {
            breadthFirstSearch();
        }
    }

    private ArrayList<Path> depthFirstSearch(Vertex start,int depthLimit) {
        if( depthLimit == 0) {
            return null;
        }

        // We know our goal is our starting position so all we actually want to do is go through all our nodes.
        int currentDepth = 0;

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
                System.out.println("Best solution: " + bestPath + start.getName() + " with a distance of: " + (bestPath.getTotalDistance() + bestPath.getLastAdded().getDistanceTo(start)));

                break;
            } else {
                System.out.println("No solutions have been found yet! Depth = " + i);
            }
        }

    }

    private ArrayList<ArrayList<Vertex>> generatePermutations(ArrayList<Vertex> list) {
        ArrayList<ArrayList<Vertex>> perm = new ArrayList<ArrayList<Vertex>>();

        // Add base combination.
        for (int i = 0; i < list.size(); i++) {
            Collections.rotate(list, 1);
            ArrayList<Vertex> newlist = new ArrayList<Vertex>(list);
            perm.add(newlist);
        }

        return perm;
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
        Long estimatedBytes = (factorial(g.getVertices().size()) * (g.getVertices().size()*(16+16)));
        Long available = (Runtime.getRuntime().maxMemory());
        


        if(estimatedBytes < 0 || estimatedBytes >= available) {
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

    private void breadthFirstSearch() {

        /*if(!memoryCheck()) {
            return;
        }*/

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
        System.out.println("Best solution: " + bestPath + start.getName() + " with a distance of: " + (bestPath.getTotalDistance() + bestPath.getLastAdded().getDistanceTo(start)));
    }
}