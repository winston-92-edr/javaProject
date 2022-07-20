package com.mynet.shared.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

import static jodd.util.StringUtil.repeat;

public class ProfanityFilter {
    private static Logger logger = LoggerFactory.getLogger(ProfanityFilter.class);

    private Map<String, Boolean> badWords = new HashMap<>();
    private Map<String, Boolean> defaultMessages = new HashMap<>();

    private static int largestBadWordLength = 0;

    public ProfanityFilter() {
        loadConfigs();
    }

    public void loadConfigs() {
        try {
            largestBadWordLength = 0;

            badWords = new HashMap<>();

            File file = new File("swear.txt");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            String str = new String(data, "UTF-8");

            if(str == null) return;

            String[] content = str.split(",");

            if(content.length == 0) return;

            for(String word:content) {
                try {

                    String badWord = replaceTurkishCharacters(word).trim().toLowerCase();

                    if(word.length() > largestBadWordLength) {
                        largestBadWordLength = word.length();
                    }

                    badWords.put(badWord, true);

                } catch(Exception e) {
                    logger.error(e.getMessage());
                }

            }

            defaultMessages = new HashMap<>();

            file = new File("defaultMessages.txt");
            fis = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            str = new String(data, "UTF-8");

            if(str == null) return;

            content = str.split(",");

            if(content.length == 0) return;

            for(String word:content) {
                try {
                    defaultMessages.put(word, true);
                } catch(Exception e) {
                    logger.error(e.getMessage());
                }

            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    public ArrayList<String> getBadWords(String input) {
        if(input == null) {
            return new ArrayList<>();
        }

        String turkishRemoved =  replaceTurkishCharacters(input).toLowerCase();

        ArrayList<String> badWords = new ArrayList<>();

        checkBadWords(input, turkishRemoved, badWords);

        String leetRemoved =  replaceLeetSpeak(turkishRemoved).toLowerCase();

        checkBadWords(input, leetRemoved, badWords);

        return badWords;
    }

    private void checkBadWords(String input, String text, ArrayList<String> badWords) {
        for (int start = 0; start < text.length(); start++) {
            // from each letter, keep going to find bad words until either the end of the sentence is reached, or the max word length is reached.
            for (int offset = 1; offset < (text.length() + 1 - start) && offset < largestBadWordLength; offset++) {
                String wordToCheck = text.substring(start, start + offset);
                if (this.badWords.containsKey(wordToCheck)) {
                    badWords.add(input.substring(start, start + offset));
                }
            }
        }
    }

    public String filterText(String input) {
        try {
            if (defaultMessages.containsKey(input)) return input;

            ArrayList<String> badWords = getBadWords(input);

            badWords.sort(Comparator.comparingInt(String::length).reversed());

            if (badWords.size() > 0) {
                for (String badWord : badWords) {
                    String censor = repeat("*", badWord.length());

                    input = input.replace(badWord, censor);
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage());
        }

        return input;
    }

    private static String replaceTurkishCharacters(String input){
        input = input.replaceAll("ş","s");
        input = input.replaceAll("ü","u");
        input = input.replaceAll("ç","c");
        input = input.replaceAll("ğ","g");
        input = input.replaceAll("ö","o");
        input = input.replaceAll("ı","i");
        input = input.replaceAll("Ş","s");
        input = input.replaceAll("Ü","u");
        input = input.replaceAll("Ç","c");
        input = input.replaceAll("Ğ","g");
        input = input.replaceAll("Ö","o");
        input = input.replaceAll("İ","i");

        return input;
    }

    private static String replaceLeetSpeak(String input) {
        input = input.replaceAll("1","i");
        input = input.replaceAll("!","i");
        input = input.replaceAll("3","e");
        input = input.replaceAll("4","a");
        input = input.replaceAll("@","a");
        input = input.replaceAll("5","s");
        input = input.replaceAll("7","t");
        input = input.replaceAll("0","o");
        input = input.replaceAll("9","g");
        input = input.replaceAll("w","v");

        return input;
    }
}
