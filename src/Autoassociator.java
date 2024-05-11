import java.util.Random;

// Class definition for the Autoassociator
public class Autoassociator {
    // Declare instance variables
    private int weights[][]; // 2D array to store weights between neurons
    private int trainingCapacity; // Stores the number of neurons (capacity of training)

    // Constructor to initialize the Autoassociator with courses
    public Autoassociator(CourseArray courses) {
        int numberOfNeurons = courses.length(); // Get the number of neurons from the length of courses
        weights = new int[numberOfNeurons][numberOfNeurons]; // Initialize the weights array
        trainingCapacity = numberOfNeurons; // Set the training capacity
        initializeWeights(); // Initialize the weights randomly
    }

    // Method to get the training capacity
    public int getTrainingCapacity() {
        return trainingCapacity;
    }

    // Method to train the Autoassociator with a pattern
    public void training(int pattern[]) {
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                if (i != j) {
                    weights[i][j] += pattern[i] * pattern[j]; // Update weights based on the pattern
                }
            }
        }
    }

    // Method to update a single neuron
    public int[] unitUpdate(int neurons[]) {
        Random random = new Random();
        int index = random.nextInt(neurons.length); // Randomly select a neuron
        unitUpdate(neurons, index);
        return new int[]{index, neurons[index]}; // Return the index of the updated neuron
    }

    // Overloaded method to update a specific neuron
    public void unitUpdate(int neurons[], int index) {
        int sum = 0;
        for (int i = 0; i < neurons.length; i++) {
            sum += weights[index][i] * neurons[i]; // Calculate the sum based on weights
        }
        neurons[index] = sum >= 0 ? 1 : -1; // Update the neuron based on the sum
    }

    // Method to update neurons in a chain for a specified number of steps
    public void chainUpdate(int neurons[], int steps) {
        for (int i = 0; i < steps; i++) {
            unitUpdate(neurons); // Update neurons in a chain
        }
    }

    // Method to update all neurons until stable state is reached
    public void fullUpdate(int neurons[]) {
        boolean stable = false;
        while (!stable) {
            int[] prevNeurons = neurons.clone(); // Clone the current state of neurons
            unitUpdate(neurons); // Update neurons
            stable = compareArrays(neurons, prevNeurons); // Check if neurons have stabilized
        }
    }

    // Method to compare two arrays of neurons
    private boolean compareArrays(int[] arr1, int[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) {
                return false; // If any difference found, return false
            }
        }
        return true; // If arrays are identical, return true
    }

    // Method to initialize weights randomly
    private void initializeWeights() {
        Random random = new Random();
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                // Randomly initialize weights to -1 or 1
                weights[i][j] = random.nextInt(2) == 0 ? 1 : -1;
            }
        }
    }
}
