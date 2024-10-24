package ru.nsu.dolgov.taskchecker

import ru.nsu.dolgov.taskchecker.models.core.AdditionalSettings.MarksMap
import ru.nsu.dolgov.taskchecker.models.core.AdditionalSettings.PlagiarismCandidate
import ru.nsu.dolgov.taskchecker.models.core.AdditionalSettings
import ru.nsu.dolgov.taskchecker.models.core.Group
import ru.nsu.dolgov.taskchecker.models.core.Student
import ru.nsu.dolgov.taskchecker.models.core.Task

/**
 * Task parser.
 *
 * @param cl closure describing the task.
 * @return nothing, adds task to the Configuration object.
 */
def task(Closure cl) {
    Task task = new Task()
    cl.delegate = task
    cl.call()
    config.tasks.add(task)
}

/**
 * Additional settings parser.
 *
 * @param cl closure describing the AdditionalSettings object.
 * @return nothing, sets additional settings to the Configuration object.
 */
def additionalSettings(Closure cl) {
    AdditionalSettings additionalSettings = new AdditionalSettings()
    cl.delegate = new AdditionalDelegate(additionalSettings)
    cl.resolveStrategy = Closure.DELEGATE_FIRST
    cl.call()

    config.additionalSettings = additionalSettings
}

/**
 * Group parser.
 *
 * @param cl closure describing the Group.
 * @return nothing, adds group and its students to the Configuration object.
 */
def group(Closure cl) {
    Group group = new Group()
    cl.delegate = new GroupDelegate(group)
    cl.resolveStrategy = Closure.DELEGATE_FIRST
    cl.call()
    if (group.name != null && group.students.size() > 0) {
        config.groups.add(group)
        config.addStudents(group.students)
    }
}

/**
 * Parser of the groups object.
 *
 * @param cl closure describing groups list.
 * @return nothing, calls group parser.
 */
def groups(Closure cl) {
    group(cl)
}

/**
 * Class to parse the Group object.
 */
class GroupDelegate {
    Group group

    /**
     * Constructor, accepts a group.
     *
     * @param group group tha will be filled with the data
     */
    GroupDelegate(Group group) {
        this.group = group
    }

    /**
     * Group name parser.
     *
     * @param name name of the group.
     */
    void name(String name) {
        group.setName(name)
    }

    /**
     * Parser of the students object of the group.
     *
     * @param cl closure describing list of students.
     */
    void students(Closure cl) {
        cl.delegate = this
        cl.call()
    }

    /**
     * Student parser.
     *
     * @param cl closure that describes a single Student object.
     */
    void student(Closure cl) {
        Student student = new Student()
        cl.delegate = student
        cl.call()
        student.groupName = group.name
        group.students.add(student)
    }
}

/**
 * Additional settings parser.
 */
class AdditionalDelegate {
    /**
     * Internal class-helper that helps with parsing students that need to be checked.
     */
    class ToCheck {
        Student student
        List<Task> tasksToCheck

        /**
         * Student parser.
         *
         * @param cl closure describing a single student.
         */
        void student(Student cl) {
            student = cl
        }

        /**
         * Parser of the tasks that will be processed.
         *
         * @param cl closure describing list of tasks.
         */
        void tasksToCheck(List<Task> cl) {
            tasksToCheck = cl
        }
    }

    AdditionalSettings settings

    /**
     * AdditionalDelegate constructor. Accepts an AdditionalSettings object as a parameter.
     *
     * @param settings Additional settings that will be filled with data.
     */
    AdditionalDelegate(AdditionalSettings settings) {
        this.settings = settings
    }

    /**
     * Parser that parses a checks object that describes the list of checks.
     *
     * @param cl closure that describes list of checks.
     */
    void checks(Closure cl) {
        cl.delegate = this
        cl.call()
    }

    /**
     * Parser of the marksMap object.
     *
     * @param cl closure that describes the list of available marks.
     */
    void marksMap(Closure cl) {
        MarksMap marksMap = new MarksMap()
        cl.delegate = marksMap
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl.call()

        settings.marksMap = marksMap
    }

    /**
     * Parser of the control points.
     *
     * @param cp closure describing list of control points.
     */
    void controlPoints(List<String> cp) {
        settings.setControlPoints(cp)
    }

    /**
     * Parser of points that will be added to the student if he is active.
     *
     * @param points Integer, points for each active week.
     */
    void pointsForActivenessPerWeek(Integer points) {
        settings.pointsForActivenessPerWeek = points
    }

    /**
     * Parser of a runInParallel property.
     *
     * @param parallelRun Boolean, describes whether app should run in parallel or not.
     */
    void runInParallel(Boolean parallelRun) {
        settings.runInParallel = parallelRun
    }

    /**
     * Parser of the repositories path.
     *
     * @param path String, folder that will be used to store repositories.
     */
    void repositoriesPath(String path) {
        settings.repositoriesPath = path
    }

    /**
     * ToCheckPlagiarism object parser.
     *
     * @param cl closure describing the toCheckPlagiarism object.
     */
    void toCheckPlagiarism(Closure cl) {
        cl.delegate = this
        cl.call()
    }

    /**
     * Github token parser.
     *
     * @param token String, used to access api of the Github.
     */
    void githubToken(String token) {
        settings.githubToken = token
    }

    /**
     * Parses folder that will be used to store plagiarism reports.
     *
     * @param path String, folder that PlagiarismChecker will use.
     */
    void plagiarismReportFolder(String path) {
        settings.plagiarismReportFolder = path
    }

    /**
     * Parses the cleanUp property.
     *
     * @param cleanup Boolean, if true all repos will be deleted right after the check.
     */
    void cleanUp(Boolean cleanup) {
        settings.cleanUp = cleanup
    }

    /**
     * Parses the student that will be checked for the plagiarism.
     *
     * @param cl closure describing the plagiarism candidate.
     */
    void source(Closure cl) {
        PlagiarismCandidate plagiarismCandidate = new PlagiarismCandidate()
        cl.delegate = plagiarismCandidate
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl.call()

        settings.plagiarismCandidateList.add(plagiarismCandidate)
    }

    /**
     * Parses student and tasks that will be processed.
     *
     * @param cl closure describing the student and tasks that will be processed.
     */
    void toCheck(Closure cl) {
        ToCheck toCheck = new ToCheck()
        cl.delegate = toCheck
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl.call()

        settings.toCheck.put(toCheck.student, toCheck.tasksToCheck)
    }
}
