class City {
    private String name;
    private double x;
    private double y;

    public City(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return this.name;
    }

    public double getXCo() {
        return this.x;
    }

    public double getYCo() {
        return this.y;
    }
}