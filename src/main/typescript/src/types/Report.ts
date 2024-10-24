import {Student} from "./Student.ts";
import {TaskResult} from "./TaskResult.ts";
import {Task} from "./Task.ts";
import {Group} from "./Group.ts";
import {AdditionalSettings} from "./AdditionalSettings.ts";

export interface Report {
    taskRunnerResults: {
        student: Student;
        tasksResults: TaskResult[];
    }[];
    additionalSettings: AdditionalSettings;
    plagiarismReportFolder: string;
    controlPoints: string[];
    pointsForActivenessPerWeek: number;
    runInParallel: boolean;
    cleanUp: boolean;
    githubToken: string;
    repositoriesPath: string;
    toCheckList: {
        student: Student;
        tasks: Task[];
    }[];
    tasks: Task[];
    groups: Group[];
    students: Student[];
    plagiarismReportPath: string;
}