package ru.nsu.dolgov.taskchecker.models.results;

import java.time.LocalDate;
import java.util.List;

/**
 * Class used to describe the commits check result.
 */
public class CommitsCheckResult {
    public Integer totalCommits = 0;
    public Integer totalActiveWeeks = 0;
    public Integer maximumCommitsPerWeek = 0;
    public Integer totalWeeks = 0;
    public List<CommitsPerWeek> commitsPerWeekList;

    public static class CommitsPerWeek {
        public LocalDate week;
        public Integer commits;
    }
}
