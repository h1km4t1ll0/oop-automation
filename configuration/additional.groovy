additionalSettings {
    pointsForActivenessPerWeek 2
    repositoriesPath "repositories"
    runInParallel true
    cleanUp false
    controlPoints getStringArrayList("2023-10-21", "2023-12-21")
    githubToken ''

    marksMap {
        excellent 20
        good 17
        satisfactory 15
    }

    checks {
        toCheck {
            student getStudentByName('blanedefyne')
            tasksToCheck getTasksList(getTaskByName('Task_1_1_1'), getTaskByName('Task_1_1_2'))
        }

        toCheck {
            student getStudentByName('melarozz')
            tasksToCheck getTasksList(getTaskByName('Task_1_1_1'), getTaskByName('Task_1_1_2'))
        }

        toCheck {
            student getStudentByName('kislosladky')
            tasksToCheck getTasksList(
                    getTaskByName('Task_1_1_1'),
                    getTaskByName('Task_1_1_2')
            )
        }

        toCheck {
            student getStudentByName('h1km4t1ll0')
            tasksToCheck getTasksList(
                    getTaskByName('Task_1_1_1'),
                    getTaskByName('Task_1_1_2')
            )
        }

        toCheck {
            student getStudentByName('MurenMurenus')
            tasksToCheck getTasksList(
                    getTaskByName('Task_1_1_1'),
                    getTaskByName('Task_1_1_2')
            )
        }

        toCheck {
            student getStudentByName('solloball')
            tasksToCheck getTasksList(
                    getTaskByName('Task_1_1_1'),
                    getTaskByName('Task_1_1_2')
            )
        }

        toCheck {
            student getStudentByName('TheBlek')
            tasksToCheck getTasksList(
                    getTaskByName('Task_1_1_1'),
                    getTaskByName('Task_1_1_2')
            )
        }
    }

    toCheckPlagiarism {
        plagiarismReportFolder 'reports'
        source {
            suspectedStudent getStudentByName('h1km4t1ll0')
        }
        source {
            suspectedStudent getStudentByName('melarozz')
        }
        source {
            suspectedStudent getStudentByName('blanedefyne')
        }
    }
}
