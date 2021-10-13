import java.util.Random;

public class GenerateRandomArr {

    public static int[] generateUniform(int size) {
        Random rand = new Random();
        int[] uniformRandom = new int[size];
        for (int i = 0; i < size; i++) {
            uniformRandom[i] = rand.nextInt(size);
        }
        return uniformRandom;
    }

    public static int[] generateNormal(int size) {
        Random rand = new Random();
        double sigma = Math.pow(10, 5);
        int[] normalRandom = new int[size];
        for (int i = 0; i < size; i++) {
            normalRandom[i] = (int)(rand.nextGaussian()*sigma + size/2);
        }
        return normalRandom;
    }

    public static int[] generateInstructions(final int size, double addRatio, double removeRatio, double containsRatio) {
        int[] instructions = new int[size];
        double sizeD = (double)size;
        int addLimit = (int)(sizeD*addRatio);
        int removeLimit = (int)(sizeD*removeRatio)+addLimit;
        int containsLimit = size;
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            if(i < addLimit){
                instructions[i] = 0;
            }
            else if (i < removeLimit) {
                instructions[i] = 1;
            }
            else {
                instructions[i] = 2;
            }
        }

        for (int i = 0; i < size; i++) {
            int randomIndex = rand.nextInt(size);
            int temp = instructions[i];
            instructions[i] = instructions[randomIndex];
            instructions[randomIndex] = temp;
        }

        return instructions;
    }
}
