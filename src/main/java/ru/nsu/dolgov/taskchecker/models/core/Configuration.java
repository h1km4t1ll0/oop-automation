package ru.nsu.dolgov.taskchecker.models.core;


import ru.nsu.dolgov.taskchecker.Logger;
import ru.nsu.dolgov.taskchecker.exceptions.NoSuchGroupException;
import ru.nsu.dolgov.taskchecker.exceptions.NoSuchStudentException;
import ru.nsu.dolgov.taskchecker.exceptions.NoSuchTaskException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;


public class Configuration {
    public List<Task> tasks = new ArrayList<>();
    public List<Group> groups = new ArrayList<>();
    public AdditionalSettings additionalSettings = new AdditionalSettings();
    public List<Student> students = new ArrayList<>();

    public void addStudents(List<Student> students) {
        List<String> nicknames = this.students.stream().map(student -> student.nickname).toList();
        for (Student student : students) {
            if (!nicknames.contains(student.nickname)) {
                this.students.add(student);
            }
        }
    }

    public Group getGroupByName(String groupName) throws NoSuchGroupException {
        try {
            return this.groups.stream()
                    .filter(
                            group -> Objects.equals(
                                    group.name, groupName
                            )
                    )
                    .toList()
                    .getFirst();
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.log(Logger.LogLevel.ERROR, "No such group: '" + groupName + "'!");
            throw new NoSuchGroupException(e.getMessage());
        }
    }

    public Task getTaskByName(String taskName) throws NoSuchTaskException {
        try {
            return this.tasks.stream()
                    .filter(
                            task -> Objects.equals(
                                    task.id, taskName
                            ) || Objects.equals(
                                    task.title, taskName
                            )
                    )
                    .toList()
                    .getFirst();
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.log(Logger.LogLevel.ERROR, "No such task: '" + taskName + "'!");
            throw new NoSuchTaskException(e.getMessage());
        }
    }

    public Student getStudentByName(String studentName) throws NoSuchStudentException {
        try {
            return this.students.stream()
                    .filter(
                            student -> Objects.equals(
                                    student.studentName, studentName
                            ) || Objects.equals(
                                    student.nickname, studentName
                            ))
                    .toList()
                    .getFirst();
        } catch (ArrayIndexOutOfBoundsException | NoSuchElementException e) {
            Logger.log(Logger.LogLevel.ERROR, "No such student: '" + studentName + "'!");
            throw new NoSuchStudentException(e.getMessage());
        }
    }
}
