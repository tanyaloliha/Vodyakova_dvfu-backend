package app.service;

import app.model.Client;
import app.model.Workout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FitnessService {
    private final Map<String, Workout> workouts = new HashMap<>();
    private final Map<String, Client> clients = new HashMap<>();

    public Workout createWorkout(Workout workout) {
        if (workouts.containsKey(workout.getId())) throw new IllegalArgumentException("Тренировка с таким ID уже существует");
        workouts.put(workout.getId(), workout);
        return workout;
    }

    public Workout getWorkout(String id) { return workouts.get(id); }

    public List<Workout> getWorkouts(String type, Boolean hasFreeSpots) {
        return workouts.values().stream()
                .filter(w -> type == null || w.getType().equalsIgnoreCase(type))
                .filter(w -> hasFreeSpots == null || (hasFreeSpots && w.getCurrentEnrolled() < w.getMaxCapacity()) || (!hasFreeSpots && w.getCurrentEnrolled() >= w.getMaxCapacity()))
                .collect(Collectors.toList());
    }

    public Workout updateWorkout(String id, Workout updated) {
        Workout existing = workouts.get(id);
        if (existing == null) return null;
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setMaxCapacity(updated.getMaxCapacity());
        return existing;
    }

    public boolean deleteWorkout(String id) {
        Workout w = workouts.get(id);
        if (w != null && w.getCurrentEnrolled() > 0) {
            throw new IllegalStateException("Нельзя удалить тренировку, на которую уже записаны клиенты");
        }
        return workouts.remove(id) != null;
    }

    public Client createClient(Client client) {
        if (clients.containsKey(client.getId())) throw new IllegalArgumentException("Клиент с таким ID уже существует");
        clients.put(client.getId(), client);
        return client;
    }

    public Client getClient(String id) { return clients.get(id); }

    public void enrollClient(String clientId, String workoutId) {
        Client client = clients.get(clientId);
        Workout workout = workouts.get(workoutId);

        if (client == null) throw new IllegalArgumentException("Клиент не найден");
        if (workout == null) throw new IllegalArgumentException("Тренировка не найдена");
        
        if (workout.getCurrentEnrolled() >= workout.getMaxCapacity()) 
            throw new IllegalStateException("На тренировку больше нет свободных мест");
            
        if (client.getEnrolledWorkoutIds().contains(workoutId)) 
            throw new IllegalStateException("Клиент уже записан на эту тренировку");
            
        if (client.getEnrolledWorkoutIds().size() >= client.getMaxWorkoutsPerWeek()) 
            throw new IllegalStateException("Превышен лимит тренировок для данного абонемента");

        workout.getEnrolledClientIds().add(clientId);
        workout.setCurrentEnrolled(workout.getCurrentEnrolled() + 1);
        client.getEnrolledWorkoutIds().add(workoutId);
    }

    public void cancelEnrollment(String clientId, String workoutId) {
        Client client = clients.get(clientId);
        Workout workout = workouts.get(workoutId);

        if (client == null || workout == null) throw new IllegalArgumentException("Клиент или тренировка не найдены");
        if (!client.getEnrolledWorkoutIds().contains(workoutId)) 
            throw new IllegalStateException("Клиент не записан на эту тренировку");

        workout.getEnrolledClientIds().remove(clientId);
        workout.setCurrentEnrolled(workout.getCurrentEnrolled() - 1);
        client.getEnrolledWorkoutIds().remove(workoutId);
    }
}