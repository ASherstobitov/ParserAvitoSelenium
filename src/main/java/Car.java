import java.io.Serializable;

public class Car implements Serializable {
    private String model;
    private int year;
    private int cost;
    private int km;
    private String city;

    public Car(String model, int year, int cost, int km, String city) {
        this.model = model;
        this.year = year;
        this.cost = cost;
        this.km = km;
        this.city = city;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public int getCost() {
        return cost;
    }

    public int getKm() {
        return km;
    }

    public String getCity() {
        return city;
    }
}
