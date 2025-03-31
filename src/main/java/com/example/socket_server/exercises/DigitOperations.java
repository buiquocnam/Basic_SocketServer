package com.example.socket_server.exercises;

public class DigitOperations {
    // Tính tổng các chữ số
    public static int sumOfDigits(int n) {
        int sum = 0;
        while (n > 0) {
            sum += n % 10;
            n /= 10;
        }
        return sum;
    }

    // Tính tích các chữ số
    public static long productOfDigits(int n) {
        long product = 1;
        while (n > 0) {
            product *= n % 10;
            n /= 10;
        }
        return product;
    }

    // Tạo kết quả tính toán
    public static String calculateDigits(int number) {
        return String.format("Số %d có:\n\n- Tổng các chữ số: %d\n- Tích các chữ số: %d\n",
            number, sumOfDigits(number), productOfDigits(number));
    }
} 