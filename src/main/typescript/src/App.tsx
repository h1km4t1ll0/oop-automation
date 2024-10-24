import React, {useMemo} from 'react';
import Menu from "./components/Menu.tsx";
import data from '../public/task-checker-report.json'
import ReportDataProvider from "./providers/ReportDataProvider.ts";
import {Report} from "./types/Report.ts";

const App: React.FC = () => {
    const reportData = useMemo<Report>(() => {
        const report: Report = JSON.parse(JSON.stringify(data));
        report.taskRunnerResults = report.taskRunnerResults.sort(
            (a, b) => a.student.groupName > b.student.groupName ? 1 : -1
        );
        return report;
    }, []);
    return (
        <ReportDataProvider value={reportData}>
            <Menu/>
        </ReportDataProvider>
    );
};

export default App;