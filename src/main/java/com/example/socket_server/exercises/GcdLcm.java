package com.example.socket_server.exercises;

public class GcdLcm {
    // Tìm UCLN
    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // Tìm BCNN
    public static int lcm(int a, int b) {
        return (a * b) / gcd(a, b);
    }

    // Tạo kết quả tính UCLN và BCNN
    public static String calculate(int a, int b) {
        return String.format("\n- UCLN của %d và %d là: %d\n- BCNN của %d và %d là: %d\n",
            a, b, gcd(a, b), a, b, lcm(a, b));
    }
} 