import React, { useContext } from 'react';
import {Report} from "../types/Report.ts";

const ReportDataProvider = React.createContext<Report>({} as Report);

export function useReportData() {
    return useContext(ReportDataProvider);
}

export default ReportDataProvider.Provider;
