

export interface Student {
    studentName: string;
    repository: string;
    nickname: string;
    groupName: string;
    commitsCheckResult: {
        totalCommits: number;
        totalActiveWeeks: number;
        maximumCommitsPerWeek: number;
        totalWeeks: number;
        commitsPerWeekList: {
            week: string;
            commits: number;
        }[];
    };
}