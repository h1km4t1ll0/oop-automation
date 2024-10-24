package ru.nsu.dolgov.taskchecker;

import ru.nsu.dolgov.taskchecker.checker.CheckRunner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CheckRunner runner = new CheckRunner();
        runner.start();
        runner.join();
    }
}
