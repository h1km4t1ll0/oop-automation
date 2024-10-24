import ru.nsu.dolgov.taskchecker.models.core.Task

/**
 * Utility variables to get an access to the students, tasks and groups.
 */
def studentsList = config.students
def tasksList = config.tasks
def groupsList = config.groups

/**
 * An utility to get student by name from the configuration.
 *
 * @param name name of the student (could be nickname).
 * @return student object.
 */
def getStudentByName(String name) {
    return config.getStudentByName(name)
}

/**
 * An utility to transform args of type Task to an ArrayList<Task>.
 *
 * @param taskList arguments of type Task.
 * @return ArrayList<Task>.
 */
static def getTasksList(Task... taskList) {
    return new ArrayList<>(Arrays.asList(taskList))
}

/**
 * An utility to get group by name from the configuration.
 *
 * @param group group name.
 * @return Group object.
 */
def getStudentsByGroup(String group) {
    return config.getGroupByName(group).students
}

/**
 * An utility to transform args of type String to the ArrayList<String>.
 *
 * @param strings String args.
 * @return ArrayList<String>.
 */
static def getStringArrayList(String... strings) {
    return new ArrayList<>(Arrays.asList(strings))
}

/**
 * An utility to get Task by its name or ID.
 *
 * @param taskName task name or ID.
 * @return Task object.
 */
def getTaskByName(String taskName) {
    return config.getTaskByName(taskName)
}
