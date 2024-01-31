package org.example;

import java.util.ArrayList;
import java.util.List;

public class Result implements ResultIMPL {
    private List<Integer> intNumbers;
    private List<Double> doubleNumbers;
    private List<String> words;

    public Result(String inputString) {

        // Разделение строки по ","
        String[] items = inputString.split(",");

        this.intNumbers = new ArrayList<>();
        this.doubleNumbers = new ArrayList<>();
        this.words = new ArrayList<>();

        for (String item : items) {
            try {
                intNumbers.add(Integer.parseInt(item));
            } catch (NumberFormatException eInt) {
                try {
                    doubleNumbers.add(Double.parseDouble(item));
                } catch (NumberFormatException eDouble) {
                    words.add(item);
                }
            }
        }
    }
    @Override
    public String getResult() {
        return "Целые числа:" + intNumbers.toString() +
                "Числа с плавающей точкой:" + doubleNumbers +
                "Оставшиеся слова" + words.toString();
    }
}
