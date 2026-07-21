package com.example.module.springboottest.util;

import java.util.ArrayList;
import java.util.List;

public class NumberWordConverter {

    private static final String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};

    private static final String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

    public static String convertToWords(long number) {
        if (number == 0) {
            return "Zero";
        }

        List<String> words = new ArrayList<>();
        int crorePart = Math.toIntExact(number / 10000000);
        int otherPart = Math.toIntExact(number % 10000000);

        if (crorePart > 0) {
            convertToWords(crorePart, words);
            words.add("Crore");
        }
        convertToWords(otherPart, words);

        return String.join(" ", words);
    }

    private static void convertToWords(int number, List<String> words) {
        int lakh = Math.toIntExact((number % 10000000) / 100000);
        int thousand = Math.toIntExact((number % 100000) / 1000);
        int hundred = Math.toIntExact((number % 1000) / 100);
        int tenOne = Math.toIntExact(number % 100);

        convertToWords(lakh, "Lakh", words);
        convertToWords(thousand, "Thousand", words);
        convertToWords(hundred, "Hundred", words);
        convertToWords(tenOne, "", words);
    }

    private static void convertToWords(int number, String unit, List<String> words) {
        if (number == 0) {
            return;
        }

        convert(number, words);
        if (!unit.isEmpty()) words.add(unit);
    }

    private static void convert(int number, List<String> words) {
        if (number < 20) {
            words.add(units[number]);
            return;
        }

        int tensPlace = number / 10;
        int onesPlace = number % 10;
        words.add(tens[tensPlace]);
        if (onesPlace != 0) {
            words.add(units[onesPlace]);
        }
    }
}
