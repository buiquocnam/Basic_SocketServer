package com.example.socket_server.exercises;

public class NumberChecking {
    // Kiểm tra số nguyên tố
    public static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    // Kiểm tra số chính phương
    public static boolean isPerfectSquare(int n) {
        int sqrt = (int) Math.sqrt(n);
        return sqrt * sqrt == n;
    }

    // Kiểm tra số hoàn hảo
    public static boolean isPerfectNumber(int n) {
        if (n <= 1) return false;
        int sum = 1;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                sum += i;
                if (i != n/i) sum += n/i;
            }
        }
        return sum == n;
    }

    // Kiểm tra số Armstrong
    public static boolean isArmstrong(int n) {
        int originalNumber = n;
        int sum = 0;
        int digits = String.valueOf(n).length();
        
        while (n > 0) {
            int digit = n % 10;
            sum += Math.pow(digit, digits);
            n /= 10;
        }
        
        return sum == originalNumber;
    }

    // Tạo kết quả kiểm tra số
    public static String checkNumber(int number) {
        StringBuilder result = new StringBuilder();
        result.append(number + " là:\n\n");
        result.append("- Số nguyên tố: " + (isPrime(number) ? "Có" : "Không") + "\n");
        result.append("- Số chính phương: " + (isPerfectSquare(number) ? "Có" : "Không") + "\n");
        result.append("- Số hoàn hảo: " + (isPerfectNumber(number) ? "Có" : "Không") + "\n");
        result.append("- Số Armstrong: " + (isArmstrong(number) ? "Có" : "Không") + "\n");
        return result.toString();
    }
} 