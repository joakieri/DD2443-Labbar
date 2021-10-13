import java.util.Random;


class Populate {
    int[] uniformRandom;
    int[] normalRandom;
    Random rand = new Random();
    long uniformSum = 0;
    long normalSum = 0;
    long uniformAverage;
    long normalAverage;
    int uniformMax = 0;
    int normalMax = 0;

    void generateUniform(int size) {
        uniformRandom = GenerateRandomArr.generateUniform(size).clone();
        for (int i : uniformRandom) {
            uniformSum += i;
            if (i > uniformMax) uniformMax = i;
        }
        uniformAverage = uniformSum/((long)size);
    }

    void generateNormal(int size) {
        normalRandom = GenerateRandomArr.generateNormal(size).clone();
        for (int i : normalRandom) {
            normalSum += i;
            if (i > normalMax) normalMax = i;
        }
        normalAverage = normalSum/((long)size);
    }

    void generateBoth(int size) {
        generateUniform(size);
        generateNormal(size);
    }

    double getStandardDeviation(int[] arr, double mean) {
        double standardDeviation = 0.0;
        for (int i = 0; i < arr.length; i++) {
            standardDeviation += Math.pow((arr[i]-mean), 2);
        }
        return Math.sqrt(standardDeviation/arr.length);
    }

    void printMeanStd() {
        double uniformStd = getStandardDeviation(uniformRandom, uniformAverage);
        double normalStd = getStandardDeviation(normalRandom, normalAverage);

        System.out.println("Uniform: sum: " + uniformSum + " , max: " + uniformMax + " , avg: " + uniformAverage + " , std: " + uniformStd);
        System.out.println("Normal: sum: " + normalSum + " , max: " + normalMax + " , avg: " + normalAverage+ " , std: " + normalStd);
    
    }

    int[] getUniformRandom() {
        return this.uniformRandom;
    }

    int[] getNormalRandom() {
        return this.normalRandom;
    }
    
}


    