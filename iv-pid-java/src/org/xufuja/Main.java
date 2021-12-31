package org.xufuja;

import org.xufuja.ui.UserInput;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // write your code here
        Scanner scanner = new Scanner(System.in);
        UserInput UI = new UserInput(scanner);
        UI.welcome();
    }
}
