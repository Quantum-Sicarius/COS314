import java.nio.*;
import java.util.Scanner;

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
    }

    private double depthFirstSearchWithIterativeDeepening() {
        
    }
}