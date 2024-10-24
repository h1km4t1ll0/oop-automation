import React, {useCallback, useMemo} from 'react';
import type {DescriptionsProps} from 'antd';
import {Card, Descriptions, Layout, Progress, Row, Space} from 'antd';
import {Student} from "../types/Student.ts";
import LineChart from "./LineChart.tsx";


const StudentView: React.FC<{
    student: Student,
    showGraph: boolean
}> = ({student, showGraph}) => {
    const makeUserDescription = useCallback((student: Student): DescriptionsProps['items'] => {
        return [
            {
                key: '1',
                label: 'Никнейм',
                children: student.nickname,
            },
            {
                key: '2',
                label: 'ФИО',
                children: student.studentName,
            },
            {
                key: '3',
                label: 'Ссылка на репозиторий',
                children: <a>{student.repository}</a>,
            },
        ];
    }, []);

    const progressStatus: "normal" | "exception" | "active" | "success" | undefined = useMemo(() => {
        const persentage = (
            student.commitsCheckResult.totalActiveWeeks / student.commitsCheckResult.totalWeeks
        ) * 100;
        if (persentage > 50 && persentage < 75) {
            return 'active';
        } else if (persentage >= 75) {
            return 'success';
        } else {
            return 'exception';
        }
    }, [student.commitsCheckResult.totalActiveWeeks, student.commitsCheckResult.totalWeeks]);

    return (
        <Layout>
            <Space direction="vertical" style={{width: '100%', height: '100%'}} align="center">
                <Row>
                    <Descriptions layout="vertical" bordered title="Информация о студенте"
                                  items={makeUserDescription(student)}/>
                </Row>
                <Row>
                    <Card title="Статистика по коммитам">
                        <Row>
                            <b>Всего коммитов</b>: {student.commitsCheckResult.totalCommits}
                        </Row>
                        <Row>
                            <b>Максимальное количество коммитов</b>: {
                            student.commitsCheckResult.maximumCommitsPerWeek
                        }
                        </Row>
                        <Row>
                            <b>Активных недель</b>: {
                            student.commitsCheckResult.totalActiveWeeks
                        }/{student.commitsCheckResult.totalWeeks}
                        </Row>
                    </Card>
                    <Card title="Активность">
                        <Progress percent={
                            (
                                student.commitsCheckResult.totalActiveWeeks / student.commitsCheckResult.totalWeeks
                            ) * 100
                        } status={progressStatus} format={(percent) => `${percent}%`} type="circle"/>
                    </Card>
                </Row>
                {
                    showGraph && (
                        <Row>
                            <LineChart data={student.commitsCheckResult.commitsPerWeekList} xField="week"
                                       yField="commits"/>
                        </Row>
                    )
                }
            </Space>
        </Layout>
    );
}

export default StudentView;