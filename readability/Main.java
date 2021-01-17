package readability;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    static int totalChars = 0;
    static int totalWords = 0;
    static int totalSentences = 0;
    static int totalSyllables = 0;
    static int totalPolysyllables = 0;

    public static void main(String[] args) {
        if (args == null) {
            return;
        }
        File file = new File(args[0]);
        getTotalCounts(file);
        printTotals();

        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): all");
            String answer = sc.next();
            double index;
            switch (answer) {
                case "ARI":
                    index = getARI();
                    System.out.println("Automated Readability Index: " + index + " (about " + getReaderAge(index) + " year olds).");
                    break;
                case "FK":
                    index = getFK();
                    System.out.println("Flesch–Kincaid readability tests: " + index + " (about " + getReaderAge(index) + " year olds).");
                    break;
                case "SMOG":
                    index = getSMOG();
                    System.out.println("Simple Measure of Gobbledygook: " + index + " (about " + getReaderAge(index) + " year olds).");
                    break;
                case "CL":
                    index = getCL();
                    System.out.println("Coleman–Liau index: " + index + " (about " + getReaderAge(index) + " year olds).");
                    break;
                case "all":
                    double averageAge = 0d;
                    index = getARI();
                    averageAge += getReaderAge(index);
                    System.out.println(String.format("Automated Readability Index: %.2f (about %d year olds).", index, getReaderAge(index)));
                    index = getFK();
                    averageAge += getReaderAge(index);
                    System.out.println(String.format("Flesch–Kincaid readability tests: %.2f (about %d year olds).",  index, getReaderAge(index)));
                    index = getSMOG();
                    averageAge += getReaderAge(index);
                    System.out.println(String.format("Simple Measure of Gobbledygook: %.2f (about %d year olds).", index, getReaderAge(index)));
                    index = getCL();
                    averageAge += getReaderAge(index);
                    System.out.println(String.format("Coleman–Liau index: %.2f (about %d year olds).", index, getReaderAge(index)));
                    System.out.println();
                    System.out.println("This text should be understood in average by " + averageAge/4 + " year olds.");
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

//        System.out.println(String.format("This text should be understood by %d year olds.", getReaderAge(index)));
//        System.out.println((double) totalWords / totalSentences <= 10 ? "EASY" : "HARD");
    }

    private static void getTotalCounts(File file) {
        try (Scanner sc = new Scanner(file)) {
            StringBuilder sb = new StringBuilder();
            String pattern = "[\\w\\S]+[.!?]";
            while (sc.hasNext()) {
                String word = sc.next();
                sb.setLength(0);
                sb.append(word);
                totalChars += sb.length();
                totalWords++;
                if (sb.toString().matches(pattern)) totalSentences++;
                int syllablesInTheWord = getSyllables(word);
                totalSyllables += syllablesInTheWord;
                totalPolysyllables += syllablesInTheWord > 2 ? 1 : 0;
            }
            totalSentences += sb.toString().matches("[^\\.\\!\\?]+") ? 1 : 0;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printTotals() {
        System.out.println(String.format("Words: %d\n" +
                "Sentences: %d\n" +
                "Characters: %d\n" +
                "Syllables: %d\n" +
                "Polysyllables: %d", totalWords, totalSentences, totalChars, totalSyllables, totalPolysyllables));
    }

    private static int getSyllables(String word) {
        int syllables = 0;
        word = word.replaceAll("[\\.\\?!]", "");
        String pattern = "[aeiouyAEIOUY]";
        char[] letters = word.toCharArray();
        for (int i = 0; i < letters.length; i++) {
            char thisChar = letters[i];
            char nextChar = i == letters.length - 1 ? 'z' : letters[i + 1];
            if (String.valueOf(thisChar).matches(pattern) && !String.valueOf(nextChar).matches(pattern)) {
                syllables++;
            }
        }
        if (letters[letters.length - 1] == 'e') syllables--;
        if (syllables > 2) {
            System.out.println(word);
        }
        if (syllables==0) syllables = 1;
        return syllables;
    }

    private static int getReaderAge(double index) {
        switch ((int) Math.round(index)) {
            case 1:
                return 6;
            case 2:
                return 7;
            case 3:
                return 9;
            case 4:
                return 10;
            case 5:
                return 11;
            case 6:
                return 12;
            case 7:
                return 13;
            case 8:
                return 14;
            case 9:
                return 15;
            case 10:
                return 16;
            case 11:
                return 17;
            case 12:
                return 18;
            case 13:
                return 24;
            default:
                return 25;
        }
    }

    private static double getARI() {
        return 4.71 * totalChars / totalWords + 0.5 * totalWords / totalSentences - 21.43;
    }

    private static double getFK() {
        return 0.39 * totalWords / totalSentences + 11.8 * totalSyllables / totalWords - 15.59;
    }

    private static double getSMOG() {
        return 1.043 * Math.sqrt(totalPolysyllables * 30d / totalSentences) + 3.1291;
    }

    private static double getCL() {
        return 0.0588 * totalChars / totalWords * 100 - 0.296 * totalSentences / totalWords * 100 - 15.8;
    }

}
