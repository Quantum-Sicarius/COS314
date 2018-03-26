import java.nio.*;
import java.util.*;

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

    private Path depthFirstSearch(Vertex start,int depthLimit) {
        // We know our goal is our starting position so all we actually want to do is go through all our nodes.
        int currentDepth = 0;

        Deque<Vertex> toVisit = new LinkedList<Vertex>();
        toVisit.add(start);

        ArrayList<Vertex> visited = new ArrayList<Vertex>();
        Path p = new Path();

        while(!toVisit.isEmpty()) {
            Vertex currentVertex = toVisit.remove();
            visited.add(currentVertex);

            p.add(currentVertex);

            //System.out.println("[DEBUG] Current vertex= " + currentVertex.getName());

            for (Vertex child : currentVertex.getNeighbors()) {
                //System.out.println("[DEBUG] " + child.getName() + " is a neighbor of: " + currentVertex.getName());
                if(!visited.contains(child)) {
                    toVisit.offerFirst(child);
                }
            }
            currentDepth++;
            if(currentDepth == depthLimit+1) {
                break;
            }
        }

        if(visited.size() == g.getVertices().size()) {
            System.out.println("[DEBUG] All nodes explored.");
            p.complete = true;
            p.add(start);
        }

        return p;
    }

    private void depthFirstSearchWithIterativeDeepening() {
        for (int i = 1; i < g.getVertices().size(); i++) {
            Path solution = depthFirstSearch(g.getVertices().get(0), i);
            if (solution.complete) {
                // If a Solution has been found.
                System.out.println("Solution has been found at depth = " + i);
                System.out.println(solution.toString());
                System.out.println("Distance for solution = " + solution.getTotalDistance());
                break;
            } else {
                System.out.println("Solution has not been found at depth = " + i);
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

    /*private void bfsRec(ArrayList<Path> p, ) {
        // Say we are at B and our Q=[CD]
        Vertex currentVertex = currentQueue.remove();

        // Now we must get the permutations of CD
        ArrayList<Vertex> nextPermutation = new ArrayList<Vertex>(Arrays.asList(currentQueue.toArray()));
        // This is basically an entire new queue we need to explore
        ArrayList<ArrayList<Vertex>> permutations = generatePermutations(uniqueNeighbors);


    }*/

    /*private void exploreBFS(ArrayList<Path> paths, Path localPath, Vertex currentVertex) {
        localPath.add(currentVertex);
        // Now we must get the permutations of CD
        ArrayList<Vertex> nextPermutation = new ArrayList<Vertex>(Arrays.asList(currentQueue.toArray()));
        // This is basically an entire new queue we need to explore
        // New permutations [[CD], [DC]]
        ArrayList<ArrayList<Vertex>> permutations = generatePermutations(uniqueNeighbors);

        for (ArrayList<Vertex> permutation : permutations) {
            // For each permutation generate and search.
            for (Vertex child : permutation) {
                // Each child is a new path.
                if (!localPath.currentPath.contains(child)) {
                    Path childPath = new Path(localPath);
                    paths.add(childPath);
                    
                    
                }
            }
        }
    }*/

    private void breadthFirstSearch() {
        /*
        A - B
        | \ |
        C - D
        */

        Vertex start = g.getVertices().get(0);
        start.currentDistanceFromStart = 0;

        Queue<Pair<Path,Queue<Vertex>>> queuesToVisit = new LinkedList<Pair<Path,Queue<Vertex>>>();

        ArrayList<Path> paths = new ArrayList<Path>();
        Path bestPath = null;

        Path p = new Path();
        p.add(start);

        // Seed the queue
        ArrayList<ArrayList<Vertex>> basePermutations = generatePermutations(start.getNeighbors());
        for (ArrayList<Vertex> basePermutation : basePermutations) {
            Pair<Path,Queue<Vertex>> h = new Pair<Path, Queue<Vertex>>();
            Queue<Vertex> q = new LinkedList<Vertex>();
            for (Vertex child : basePermutation) {
                q.add(child);
            }
            h.first = p;
            h.second = q;
            queuesToVisit.add(h);
        }
        // Now we have a queue with [[BCD],[CDB],[DBC]]
        // Where B, C, D is our second branch
        // so we must deque the first item from each of our queues.

        while(!queuesToVisit.isEmpty()) {
            Pair<Path,Queue<Vertex>> currentPair = queuesToVisit.remove();

            Path localPath = currentPair.first;
            Queue<Vertex> currentQueue = currentPair.second;

            /*System.out.print("PathID: " + localPath.toString() + "\tCurrent Queue ["); 
            for (Vertex v : currentQueue) {
                System.out.print(v.getName() + ",");
            }
            System.out.print("]"); 
            System.out.println();*/

            while(!currentQueue.isEmpty()) {
                // Say we are at B and our Q=[CD]
                Vertex currentVertex = currentQueue.remove();
                //exploreBFS(paths, localPath, currentVertex);

                if(currentQueue.isEmpty()) {
                    Path childPath = new Path(localPath);
                    childPath.add(currentVertex);
                    paths.add(childPath);
                }

                //localPath.add(currentVertex);
                // Now we must get the permutations of CD
                ArrayList<Vertex> nextPermutation = new ArrayList<Vertex>(currentQueue);

                // This is basically an entire new queue we need to explore
                // New permutations [[CD], [DC]]
                ArrayList<ArrayList<Vertex>> permutations = generatePermutations(nextPermutation);



                // Add permutation to queue
                for (ArrayList<Vertex> permutation : permutations) {
                    Pair<Path, Queue<Vertex>> newPair = new Pair<Path, Queue<Vertex>>();
                    Queue<Vertex> nextToVisit = new LinkedList<Vertex>();

                    for (Vertex child : permutation) {
                        nextToVisit.add(child);
                        Path childPath = new Path(localPath);
                        childPath.add(currentVertex);
                        paths.add(childPath);
            
                        newPair.first = childPath;
                        newPair.second = nextToVisit;
                    }
                    queuesToVisit.add(newPair);
                }

            }
            //paths.remove(localPath);
        }

        ArrayList<Path> finalPaths = new ArrayList<Path>();

        for (int i = 0; i < paths.size(); i++) {
            if(paths.get(i).currentPath.size() >= g.getVertices().size()) {
                finalPaths.add(paths.get(i));
            }
        }

        bestPath = finalPaths.get(0);

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