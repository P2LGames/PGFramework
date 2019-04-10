package environment;

import java.util.List;

/**
 * We want to limit them to having one class in the environment package in the command dump to simulate the environment
 * being a singleton implementing this class
 */
public abstract class Environment {

    public Double distance(List<Double> coordinates1, List<Double> coordinates2) {
        assert(coordinates1.size() == coordinates2.size());
        double sumOfDifferencesSquared = 0;
        for(int i = 0; i < coordinates1.size(); i++) {
            sumOfDifferencesSquared += Math.pow((coordinates2.get(i) - coordinates1.get(i)), 2);
        }
        return Math.sqrt(sumOfDifferencesSquared);
    }



}
