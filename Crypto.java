import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Crypto {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final String ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ.,!?:;%-0123456789[]'' ";

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        int option;
        System.out.println("Привет!");
        while (true) {
            System.out.println("Выберите, пожалуйста, вариант работы программы(1 - 5): ");
            System.out.println("1. Зашифровать файл");
            System.out.println("2. Расшифровать файл");
            System.out.println("3. Брутфорс");
            System.out.println("4. Статистический анализ");
            System.out.println("5. Выход из программы");
            option = SCANNER.nextInt();

            switch (option) {
                case 1 -> encrypt();
                case 2 -> decrypt();
                case 3 -> brutForce();
                case 4 -> statAnalyze();
                case 5 -> System.exit(0);
                default -> System.out.println("Вы выбрали неправильный вариант!");
            }
        }
    }

    private static void statAnalyze() {
        System.out.println("Введите путь к файлу для анализа: ");
        SCANNER.nextLine();
        String filePath = SCANNER.nextLine();
        String decryptedText = getFileContent(filePath);
        System.out.println("Введите путь к файлу для сбора статистики: ");
        String statFilePath = SCANNER.nextLine();
        String textForStats = getFileContent(statFilePath);
        HashMap<Character, Integer> decryptedTextStats = getCharStats(decryptedText);
        HashMap<Character, Integer> generalStats = getCharStats(textForStats);
        HashMap<Character, Character> charStats = getCharStats(decryptedTextStats, generalStats);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < decryptedText.length(); i++) {
            char decChar = charStats.get(decryptedText.charAt(i));
            result.append(decChar);
        }
        writeContentToFile(result.toString(), filePath, "_analysed");
    }

    private static HashMap<Character, Character> getCharStats(HashMap<Character, Integer> decryptedTextStats
            , HashMap<Character, Integer> generalStats) {
        HashMap<Character, Character> result = new HashMap<>();
        for (int i = 0; i < ALPHABET.length(); i++) {
            char ch = ALPHABET.charAt(i);
            Integer charStats = generalStats.get(ch);
            Character closestCharFromStatMap = getClosestCharFromStatMap(decryptedTextStats, charStats);
            result.put(closestCharFromStatMap, ch);
        }

        return result;
    }

    private static Character getClosestCharFromStatMap(HashMap<Character, Integer> statMap, Integer value) {
        int minDelta = Integer.MAX_VALUE;
        Character currentChar = 'c';
        for (Map.Entry<Character, Integer> characterIntegerEntry : statMap.entrySet()) {
            int delta = Math.abs(characterIntegerEntry.getValue() - value);
            if (delta < minDelta) {
                minDelta = delta;
                currentChar = characterIntegerEntry.getKey();
            }
        }
        return currentChar;
    }

    private static HashMap<Character, Integer> getCharStats(String text) {
        HashMap<Character, Integer> absResult = new HashMap<>();
        HashMap<Character, Integer> result = new HashMap<>();
        //int size = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (absResult.containsKey(ch)) {
                result.put(ch, result.get(ch) + 1);
            } else {
                result.put(ch, 1);
                absResult.put(ch, 1);
            }
            //size++;
        }
//        for (Map.Entry<Character, Integer> entry : result.entrySet()) {
//            int entries = entry.getValue() * 100 / size;
//            result.put(entry.getKey(), entries);
//        }

        return result;
    }

    private static void brutForce() {
        System.out.println("Введите путь к файлу для брутфорса: ");
        SCANNER.nextLine();
        String filePath = SCANNER.nextLine();
        String fileContent = getFileContent(filePath);
        for (int i = 0; i < ALPHABET.length(); i++) {
            String decryptedText = decryptText(fileContent, i);
            boolean isValid = isValidText(decryptedText);
            if (isValid) {
                System.out.println("Ключ: " + i);
                writeContentToFile(decryptedText, filePath, "_bruteforce");
                break;
            }
        }
    }

    private static boolean isValidText(String text) {
        String[] strings = text.split(" ");
        for (String string : strings) {
            if (string.length() > 24) {
                return false;
            }
        }
        System.out.println("Понятен ли Вам этот текст?");
        int stringStart = new Random().nextInt(text.length() / 2);
        int stringEnd = stringStart + 200;
        if (stringEnd > text.length()) {
            stringEnd = stringStart + (text.length() - stringStart);
        }
        System.out.println(text.substring(stringStart, stringEnd));
        System.out.println("1. Yes");
        System.out.println("2. No");
        int answer = SCANNER.nextInt();

        return switch (answer) {
            case 1 -> true;
            case 2 -> false;
            default -> throw new IllegalStateException("Введено неправильное значение!");
        };
    }

    private static String encryptText(String text, int key) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            int index = ALPHABET.indexOf(ch);
            int newIndex = index + key;
            newIndex = newIndex % ALPHABET.length();
            char encChar = ALPHABET.charAt(newIndex);
            stringBuilder.append(encChar);
        }
        return stringBuilder.toString();
    }

    private static String decryptText(String text, int key) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            int index = ALPHABET.indexOf(ch);
            int newIndex = index - key + ALPHABET.length();
            newIndex = newIndex % ALPHABET.length();
            char decChar = ALPHABET.charAt(newIndex);
            stringBuilder.append(decChar);
        }
        return stringBuilder.toString();
    }

    private static void decrypt() {
        System.out.println("Начало расшифровки...");
        System.out.println("Введите путь к файлу: ");
        SCANNER.nextLine();
        String filesPath = SCANNER.nextLine();
        String fileContent = getFileContent(filesPath);
        System.out.println("Введите ключ шифрования: ");
        int key = SCANNER.nextInt();
        String decryptedText = decryptText(fileContent, key);
        writeContentToFile(decryptedText, filesPath, "_decrypted");
    }

    private static void encrypt() {
        System.out.println("Начало шифрования...");
        System.out.println("Введите путь к файлу: ");
        SCANNER.nextLine();
        String filesPath = SCANNER.nextLine();
        String fileContent = getFileContent(filesPath);
        System.out.println("Введите ключ шифрования: ");
        int key = SCANNER.nextInt();
        String encryptedText = encryptText(fileContent, key);
        writeContentToFile(encryptedText, filesPath, "_encrypted");
    }

    private static void writeContentToFile(String content, String prevFilePath, String suffix) {
        int dot = prevFilePath.lastIndexOf(".");
        String fileBeforeDot = prevFilePath.substring(0, dot);
        String fileAfterDot = prevFilePath.substring(dot);
        String newFileName = fileBeforeDot + suffix + fileAfterDot;
        try {
            Files.writeString(Path.of(newFileName), content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFileContent(String filesPath) {
        Path path = Path.of(filesPath);
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}