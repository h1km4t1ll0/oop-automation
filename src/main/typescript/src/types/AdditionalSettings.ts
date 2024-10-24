import {PlagiarismCandidate} from "./PlagiarismCandidate.ts";
import {Student} from "./Student.ts";
import {Task} from "./Task.ts";

export interface AdditionalSettings {
    plagiarismCandidateList: PlagiarismCandidate[];
    plagiarismReportFolder: string;
    controlPoints: string[];
    pointsForActivenessPerWeek: number;
    runInParallel: boolean;
    cleanUp: boolean;
    githubToken: string;
    marksMap: {
        excellent: number;
        good: number;
        satisfactory: number;
    };
    repositoriesPath: string;
    toCheckList: {
        student: Student;
        tasks: Task[];
    }[];
}