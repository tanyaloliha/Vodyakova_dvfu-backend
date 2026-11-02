package app.model;

import java.util.ArrayList;
import java.util.List;

public class Workout {
    private String id;
    private String name;
    private String type;
    private int maxCapacity;
    private int currentEnrolled;
    private List<String> enrolledClientIds = new ArrayList<>();

    public Workout(String id, String name, String type, int maxCapacity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.maxCapacity = maxCapacity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public int getCurrentEnrolled() { return currentEnrolled; }
    public void setCurrentEnrolled(int currentEnrolled) { this.currentEnrolled = currentEnrolled; }
    public List<String> getEnrolledClientIds() { return enrolledClientIds; }
    public void setEnrolledClientIds(List<String> enrolledClientIds) { this.enrolledClientIds = enrolledClientIds; }
}