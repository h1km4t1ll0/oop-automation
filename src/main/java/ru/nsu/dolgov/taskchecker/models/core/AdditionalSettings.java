package ru.nsu.dolgov.taskchecker.models.core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdditionalSettings {
    public List<PlagiarismCandidate> plagiarismCandidateList = new ArrayList<>();
    public String plagiarismReportFolder = "reports";
    public transient Map<Student, List<Task>> toCheck = new HashMap<>();
    public List<LocalDate> controlPoints = new ArrayList<>();
    public Integer pointsForActivenessPerWeek = 0;
    public Boolean runInParallel = false;
    public Boolean cleanUp = false;
    public String githubToken = null;
    public MarksMap marksMap;
    public String repositoriesPath = "repositories";
    public List<StudentWithTasks> toCheckList = new ArrayList<>();


    public void setControlPoints(List<String> controlPoints) {
        this.controlPoints = controlPoints
                .stream()
                .map(LocalDate::parse)
                .collect(Collectors.toList());
    }

    public void setToCheckList() {
        for (Student student : this.toCheck.keySet()) {
            StudentWithTasks studentWithTasks = new StudentWithTasks();
            studentWithTasks.student = student;
            studentWithTasks.tasks = this.toCheck.get(student);

            this.toCheckList.add(studentWithTasks);
        }
    }

    public static class MarksMap {
        public Integer excellent;
        public Integer good;
        public Integer satisfactory;

        public void excellent(Integer excellent) {
            this.excellent = excellent;
        }

        public void good(Integer good) {
            this.good = good;
        }

        public void satisfactory(Integer satisfactory) {
            this.satisfactory = satisfactory;
        }
    }

    public static class PlagiarismCandidate {
        public Student suspectedStudent;

        public void suspectedStudent(Student suspectedStudent) {
            this.suspectedStudent = suspectedStudent;
        }
    }
}
