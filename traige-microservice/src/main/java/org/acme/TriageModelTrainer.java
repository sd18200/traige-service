package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import smile.classification.LogisticRegression;
import com.opencsv.CSVReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
@ApplicationScoped
public class TriageModelTrainer {

    private LogisticRegression.Multinomial model;
    private static final String MODEL_PATH = "triage-model.ser";

    public void trainOrLoadModel(String csvPath) throws Exception {
        File modelFile = new File(MODEL_PATH);
        if (modelFile.exists()) {
            System.out.println("Loading model from file...");
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFile))) {
                model = (LogisticRegression.Multinomial) ois.readObject();
            }
        } else {
            System.out.println("Training new model...");
            trainModel(csvPath);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MODEL_PATH))) {
                oos.writeObject(model);
                System.out.println("Model saved to disk.");
            }
        }
    }

    public void trainModel(String csvPath) throws Exception {
        double lambda = 0.1;
        double tolerance = 1e-5;
        int maxIter = 500;

        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
            String[] header = reader.readNext();
            List<double[]> featureList = new ArrayList<>();
            List<Integer> labelList = new ArrayList<>();

            int targetIndex = -1;
            for (int i = 0; i < header.length; i++) {
                if (header[i].equals("triage_level")) {
                    targetIndex = i;
                    break;
                }
            }

            if (targetIndex == -1) {
                throw new IllegalArgumentException("triage_level column not found");
            }

            String[] row;
            while ((row = reader.readNext()) != null) {
                double[] features = new double[header.length - 3]; // exclude target, triage_reason, Unnamed: 0
                int featureIdx = 0;
                boolean skipRow = false;

                for (int i = 0; i < row.length; i++) {
                    String colName = header[i];
                    if (i == targetIndex || colName.equalsIgnoreCase("triage_reason") || colName.equalsIgnoreCase("Unnamed: 0")) {
                        continue;
                    }

                    try {
                        features[featureIdx++] = Double.parseDouble(row[i]);
                    } catch (NumberFormatException e) {
                        System.err.printf("Skipping row due to invalid value '%s' in column '%s'\n", row[i], colName);
                        skipRow = true;
                        break;
                    }
                }

                if (!skipRow) {
                    featureList.add(features);
                    labelList.add(Integer.parseInt(row[targetIndex]));
                }
            }

            double[][] X = featureList.toArray(new double[0][]);
            int[] y = labelList.stream().mapToInt(i -> i).toArray();
            model = LogisticRegression.multinomial(X, y, lambda, tolerance, maxIter);
            System.out.println("Logistic Regression model trained.");
            int correct = 0;
            int[][] confusion = new int[3][3];

            for (int i = 0; i < featureList.size(); i++) {
                double[] input = featureList.get(i);
                int actual = labelList.get(i);
                int predicted = model.predict(input);
                if (predicted == actual) correct++;
                confusion[actual][predicted]++;
            }

            double accuracy = (double) correct / featureList.size();
            System.out.printf("Accuracy: %.2f%% (%d of %d samples)\n", accuracy * 100, correct, featureList.size());

            System.out.println("Confusion Matrix:");
            System.out.println("              Pred: Low  Pred: Med  Pred: Urgent");
            for (int i = 0; i < 3; i++) {
                String label = (i == 0) ? "True: Low   " : (i == 1) ? "True: Med   " : "True: Urgent";
                System.out.print(label);
                for (int j = 0; j < 3; j++) {
                    System.out.printf("%10d", confusion[i][j]);
                }
                System.out.println();
            }


        }
    }

    public int predict(double[] patientVector) {
        return model.predict(patientVector);
    }

    public double[] predictWithProbabilities(double[] patientVector) {
        double[] probs = new double[3];
        model.predict(patientVector, probs);
        return probs;
    }

    public boolean isReady() {
        return model != null;
    }
}
