import {Task} from "./Task.ts";

export interface TaskResult {
    build: boolean;
    javadoc: boolean;
    passedTests: number;
    totalTests: number;
    failedTests: number;
    ignoredTests: number;
    deadlinesCheckResult: {
        softDeadlinePass: boolean;
        hardDeadlinePass: boolean
    },
    task: Task;
    points: number;
}