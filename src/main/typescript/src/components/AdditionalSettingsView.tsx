import React, {useMemo} from 'react';
import {Card, Col, Layout, Popover, Row, Space, Table, TableProps, Typography} from 'antd';
import {AdditionalSettings} from "../types/AdditionalSettings.ts";
import {Task} from "../types/Task.ts";
import TaskView from "./TaskView.tsx";
import {Student} from "../types/Student.ts";
import StudentView from "./StudentView.tsx";
import {useReportData} from "../providers/ReportDataProvider.ts";


interface DataType {
    studentName: string;
    studentNickname: {
        nickname: string;
        repository: string;
    };
    tasks: Task[];
}

interface PlagiarismDataType {
    studentName: string;
    studentNickname: {
        nickname: string;
        repository: string;
    };
}

const columns: TableProps<DataType>['columns'] = [
    {
        title: 'Имя студента',
        dataIndex: 'studentName',
        key: 'studentName',
        // render: (student: Student) => <p>{student.studentName}</p>,
    },
    {
        title: 'Никнейм студента',
        dataIndex: 'studentNickname',
        key: 'studentNickname',
        render: (student: Student) => <Popover content={<StudentView student={student} showGraph={false}/>}>
            <a href={student.repository}>{student.nickname}</a>
        </Popover>,
    },
    {
        title: 'Задачи',
        dataIndex: 'tasks',
        key: 'tasks',
        render: (tasks: Task[]) => tasks.map(
            task => (
                <>
                    <Popover content={<TaskView task={task}/>}>{task.id}</Popover>
                    <br/>
                </>
            )
        ),
    },
];

const plagiarismColumns: TableProps<PlagiarismDataType>['columns'] = [
    {
        title: 'Имя студента',
        dataIndex: 'studentName',
        key: 'studentName',
    },
    {
        title: 'Никнейм студента',
        dataIndex: 'studentNickname',
        key: 'studentNickname',
        render: (student: Student) => <Popover content={<StudentView student={student} showGraph={false}/>}>
            <a href={student.repository}>{student.nickname}</a>
        </Popover>,
    },
];


const AdditionalSettingsView: React.FC<{ additionalSettings: AdditionalSettings }> = ({additionalSettings}) => {
    const report = useReportData();

    const data = useMemo(() => additionalSettings.toCheckList.map(
        toCheckItem => ({
            studentName: toCheckItem.student.studentName,
            studentNickname: toCheckItem.student,
            tasks: toCheckItem.tasks
        })
    ), [additionalSettings.toCheckList]);

    const plagiarismData = useMemo(() => {
        return additionalSettings.plagiarismCandidateList.map(
            suspectedItem => ({
                studentName: suspectedItem.suspectedStudent.studentName,
                studentNickname: suspectedItem.suspectedStudent
            })
        );
    }, [additionalSettings.plagiarismCandidateList]);

    return (
        <Layout>
            <Space style={{width: '100%'}} align="center" direction="vertical">
                <Row>
                    <Card size="default" title="Системные настройки">
                        <Row>
                            <Space direction="horizontal">
                                <Col>
                                    <Typography.Text strong>Чистка директорий</Typography.Text>
                                </Col>
                                <Col>
                                    {additionalSettings.cleanUp ? 'Да' : 'Нет'}
                                </Col>
                            </Space>
                        </Row>
                        <Row>
                            <Space direction="horizontal">
                                <Col>
                                    <Typography.Text strong>Токен GitHub</Typography.Text>
                                </Col>
                                <Col>
                                    {additionalSettings.githubToken === '' ? 'Не задан' : additionalSettings.githubToken}
                                </Col>
                            </Space>
                        </Row>
                        <Row>
                            <Space direction="horizontal">
                                <Col>
                                    <Typography.Text strong>Плагиат</Typography.Text>
                                </Col>
                                <Col>
                                    {additionalSettings.plagiarismReportFolder ?? 'Не задан'}
                                </Col></Space>
                        </Row>
                        <Row>
                            <Space direction="horizontal">
                                <Col>
                                    <Typography.Text strong>Баллы за активность</Typography.Text>
                                </Col>
                                <Col>
                                    {additionalSettings.pointsForActivenessPerWeek ?? 'Не заданы'}
                                </Col>
                            </Space>
                        </Row>
                        <Row>
                            <Space direction="horizontal">
                                <Col>
                                    <Typography.Text strong>Репозитории</Typography.Text>
                                </Col>
                                <Col>
                                    {additionalSettings.repositoriesPath ?? 'Не задан'}
                                </Col>
                            </Space>
                        </Row>
                        <Row>
                            <Space direction="horizontal">
                                <Col>
                                    <Typography.Text strong>Параллельное выполнение</Typography.Text>
                                </Col>
                                <Col>
                                    {additionalSettings.runInParallel ? 'Да' : 'Нет'}
                                </Col>
                            </Space>
                        </Row>
                        <Row>
                            <Space direction="horizontal">
                                <Col>
                                    <Typography.Text strong>Контрольные точки</Typography.Text>
                                </Col>
                                <Col>
                                    {additionalSettings.controlPoints.join(', ') ?? 'Нет'}
                                </Col>
                            </Space>
                        </Row>
                    </Card>
                    <Card size="default" title="Оценки">
                        <Row>
                            <Space direction="horizontal">
                                <Col>
                                    <Typography.Text strong>Отлично</Typography.Text>
                                </Col>
                                <Col>
                                    {additionalSettings.marksMap.excellent ?? 'Отлично'}
                                </Col>
                            </Space>
                        </Row>
                        <Row>
                            <Space direction="horizontal">
                                <Col>
                                    <Typography.Text strong>Хорошо</Typography.Text>
                                </Col>
                                <Col>
                                    {additionalSettings.marksMap.good ?? 'Не задано'}
                                </Col>
                            </Space>
                        </Row>
                        <Row>
                            <Space direction="horizontal">
                                <Col>
                                    <Typography.Text strong>Удовлетворительно</Typography.Text>
                                </Col>
                                <Col>
                                    {additionalSettings.marksMap.satisfactory ?? 'Не задано'}
                                </Col></Space>
                        </Row>
                    </Card>
                        <Card size="default" title="Проверка на плагиат">
                            <Row>
                                <Typography.Text strong>
                                    <a href="https://jplag.github.io/JPlag/">Сайт для проверки плагиата</a>
                                </Typography.Text>
                            </Row>
                            <Row>
                                <Col>
                                    <b>Файл отчета:</b>
                                </Col>
                                <Col>
                                    {report.plagiarismReportPath}
                                </Col>
                            </Row>
                            <Table columns={plagiarismColumns} dataSource={plagiarismData} pagination={false}/>
                        </Card>
                </Row>
                <Row>
                    <Card size="default" title="Задания на проверку">
                        <Table columns={columns} dataSource={data} pagination={false}/>
                    </Card>
                </Row>
            </Space>
        </Layout>
    );
}

export default AdditionalSettingsView;