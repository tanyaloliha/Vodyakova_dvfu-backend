package app.model;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private String id;
    private String name;
    private String subscriptionType;
    private List<String> enrolledWorkoutIds = new ArrayList<>();

    public Client(String id, String name, String subscriptionType) {
        this.id = id;
        this.name = name;
        this.subscriptionType = subscriptionType;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }
    public List<String> getEnrolledWorkoutIds() { return enrolledWorkoutIds; }
    public void setEnrolledWorkoutIds(List<String> enrolledWorkoutIds) { this.enrolledWorkoutIds = enrolledWorkoutIds; }

    public int getMaxWorkoutsPerWeek() {
        return "PREMIUM".equalsIgnoreCase(subscriptionType) ? 7 : 2;
    }
}