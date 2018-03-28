package scholtz.thomas.project1;

import java.nio.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.ArrayList;

class FileReader {

    private ArrayList<City> cities;

    public FileReader(String fileName) {
        this.cities = new ArrayList<City>();
        this.readAndParseFile(fileName);
    }

    public void readAndParseFile(String fileName) {
        System.out.println("Attempting to read file: " + fileName);
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get("./" + fileName)));
        } catch(IOException e) {
            e.printStackTrace();
        }
        //System.out.println(content);
        System.out.println("File read without issues! Parsing...");
        
        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            if(Character.isDigit(line.trim().charAt(0))) {
                String[] city = line.split(" ");
                City c = new City(city[0], Double.parseDouble(city[1]), Double.parseDouble(city[2]));
                this.cities.add(c);
            }
        }

        System.out.println("Successfully parsed: " + this.cities.size() + " number of cities!"); 
    }

    public ArrayList<City> getCities() {
        return cities;
    }
}