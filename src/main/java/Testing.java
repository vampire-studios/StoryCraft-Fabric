import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Testing {

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 100;i++) {
                System.out.println(generateLastName() + " " + generateLastName() + " " + generateLastName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateLastName() throws IOException {
        String lastNameOut;
        Random rand = new Random();
        InputStream stream = Testing.class.getClassLoader().getResourceAsStream("dictionary.txt");
        Scanner scanner = new Scanner(Objects.requireNonNull(stream));
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
            builder.append(",");
        }
        String[] strings = builder.toString().split(",");
        lastNameOut = strings[rand.nextInt(strings.length)];
        stream.close();
        scanner.close();
        return lastNameOut;
    }

}
