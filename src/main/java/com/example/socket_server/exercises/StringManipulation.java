package com.example.socket_server.exercises;

import java.util.*;

public class StringManipulation {
    // Bài 4: Đảo ngược chuỗi
    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    // Bài 5: Xử lý chuỗi nâng cao
    public static class AdvancedOperations {
        // Chuyển sang chữ hoa
        public static String toUpperCase(String str) {
            return str.toUpperCase();
        }

        // Chuyển sang chữ thường
        public static String toLowerCase(String str) {
            return str.toLowerCase();
        }

        // Đổi chữ hoa thành thường và ngược lại
        public static String swapCase(String str) {
            StringBuilder result = new StringBuilder();
            for (char c : str.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    result.append(Character.toLowerCase(c));
                } else if (Character.isLowerCase(c)) {
                    result.append(Character.toUpperCase(c));
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        }

        // Đếm số từ
        public static int countWords(String str) {
            str = str.trim();
            if (str.isEmpty()) return 0;
            return str.split("\\s+").length;
        }

        // Tìm nguyên âm
        public static String findVowels(String str) {
            StringBuilder vowels = new StringBuilder();
            for (char c : str.toLowerCase().toCharArray()) {
                if ("aeiouàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹ".indexOf(c) != -1) {
                    vowels.append(c);
                }
            }
            return vowels.toString();
        }

        // Tạo kết quả xử lý chuỗi nâng cao
        public static String processString(String str) {
            StringBuilder result = new StringBuilder();
            result.append("Kết quả xử lý chuỗi:\n\n");
            result.append("a. Chuỗi đảo ngược:\n   " + reverse(str) + "\n\n");
            result.append("b. Chuỗi chữ hoa:\n   " + toUpperCase(str) + "\n\n");
            result.append("c. Chuỗi chữ thường:\n   " + toLowerCase(str) + "\n\n");
            result.append("d. Chuỗi đảo chữ hoa/thường:\n   " + swapCase(str) + "\n\n");
            result.append("e. Số từ trong chuỗi: " + countWords(str) + "\n\n");
            result.append("f. Các nguyên âm:\n   " + findVowels(str) + "\n");
            return result.toString();
        }
    }

    // Bài 6: Phân tích chuỗi
    public static class Analysis {
        // In từng từ trên mỗi dòng
        public static String printWordsPerLine(String str) {
            return String.join("\n", str.trim().split("\\s+"));
        }

        // Tạo bảng tần số xuất hiện của các ký tự
        public static Map<Character, Integer> getCharacterFrequency(String str) {
            Map<Character, Integer> frequency = new TreeMap<>();
            for (char c : str.toCharArray()) {
                frequency.put(c, frequency.getOrDefault(c, 0) + 1);
            }
            return frequency;
        }

        // Tạo kết quả phân tích chuỗi
        public static String analyzeString(String str) {
            StringBuilder result = new StringBuilder();
            result.append("Kết quả phân tích chuỗi:\n\n");
            result.append("a. Các từ trên từng dòng:\n\n" + printWordsPerLine(str) + "\n\n");
            result.append("b. Bảng tần số xuất hiện:\n\n");
            
            Map<Character, Integer> frequency = getCharacterFrequency(str);
            for (Map.Entry<Character, Integer> entry : frequency.entrySet()) {
                result.append(String.format("   '%s': %d lần\n", entry.getKey(), entry.getValue()));
            }
            result.append("\n");
            
            return result.toString();
        }
    }

    // Tạo kết quả đảo ngược chuỗi đơn giản (Bài 4)
    public static String reverseString(String str) {
        return "Chuỗi đảo ngược:\n\n" + reverse(str) + "\n";
    }
} 