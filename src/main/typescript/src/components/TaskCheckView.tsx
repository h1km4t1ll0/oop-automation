import React, {useCallback, useMemo, useState} from 'react';
import {Badge, FloatButton, InputNumber, Popover, Space, Table, Tag} from 'antd';
import {ColumnsType} from "antd/es/table";
import TaskView from "./TaskView.tsx";
import {useReportData} from "../providers/ReportDataProvider.ts";
import StudentView from "./StudentView.tsx";
import GroupView from "./GroupView.tsx";
import {Task} from "../types/Task.ts";
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import {DownloadOutlined} from "@ant-design/icons";

interface FlattenedData {
    align: string;
    key: string;
    studentName: string;
    repository: string;
    nickname: string;
    groupName: string;
    build: boolean;
    javadoc: boolean;
    passedTests: number;
    totalTests: number;
    failedTests: number;
    ignoredTests: number;
    softDeadlinePass: boolean;
    hardDeadlinePass: boolean;
    taskTitle: {
        title: string,
        id: string
    };
    points: number;
    totalPoints: number;
    task: Task;
    mark: number;
}


const TaskCheckView: React.FC = () => {
    const report = useReportData();

    const computeMark = useCallback((points: number) => {
        if (points >= report.additionalSettings.marksMap.excellent) {
            return 5;
        }
        if (points < report.additionalSettings.marksMap.excellent &&
            points >= report.additionalSettings.marksMap.good) {
            return 4;
        }
        if (points < report.additionalSettings.marksMap.good &&
            points >= report.additionalSettings.marksMap.satisfactory) {
            return 3;
        }

        return 2;
    }, [
        report.additionalSettings.marksMap.excellent,
        report.additionalSettings.marksMap.good,
        report.additionalSettings.marksMap.satisfactory
    ]);
    const flattenedData: FlattenedData[] = useMemo(() => report.taskRunnerResults.flatMap(
        (item, index) => {
            const totalPoints = item.tasksResults.reduce(
                (acc, curr) => acc + curr.points + (
                    curr.deadlinesCheckResult?.hardDeadlinePass ?? false ? 0.5 : 0
                ) + (
                    curr.deadlinesCheckResult?.softDeadlinePass ?? false ? 0.5 : 0
                ), 0
            );

            return item.tasksResults.map((taskResult, subIndex) => ({
                key: `${index}-${subIndex}`,
                align: 'center',
                studentName: item.student.studentName,
                repository: item.student.repository,
                nickname: item.student.nickname,
                groupName: item.student.groupName,
                softDeadlinePass: taskResult.deadlinesCheckResult?.softDeadlinePass ?? false,
                hardDeadlinePass: taskResult.deadlinesCheckResult?.hardDeadlinePass ?? false,
                mark: computeMark(totalPoints),
                ...taskResult,
                taskTitle: {
                    title: taskResult.task.title,
                    id: taskResult.task.id
                },
                task: taskResult.task,
                totalPoints
            }))
        }
    ), [computeMark, report.taskRunnerResults]);

    const [tableData, setTableData] = useState<FlattenedData[]>(flattenedData);

    const exportToExcel = useCallback(() => {
        const workbook = XLSX.utils.book_new();
        const worksheet = XLSX.utils.json_to_sheet(tableData.map(
            element => ({
                "Имя студента": element.studentName,
                "Репозиторий": element.repository,
                "Группа": element.groupName,
                "Мягкий дедлайн": element.softDeadlinePass ? "Да" : "Нет",
                "Жесткий дедлайн": element.hardDeadlinePass ? "Да" : "Нет",
                "Сборка": element.build ? "Да" : "Нет",
                "Документация": element.javadoc ? "Да" : "Нет",
                "Всего тестов": element.totalTests,
                "Прошло тестов": element.passedTests,
                "Не прошло тестов": element.failedTests,
                "Задача": element.task.title,
                "Баллов за задачу": element.points,
                "Всего баллов": element.totalPoints,
                "Оценка": element.mark
            })
        ));

        XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');

        const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });

        const data = new Blob([excelBuffer], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8'
        });
        saveAs(data, 'report.xlsx');
    }, [tableData]);

    const handleDeadlineChange = useCallback((index: number, softDeadline: boolean = false) => {
        const newData = [...tableData];
        if (softDeadline) {
            newData[index].softDeadlinePass = !newData[index].softDeadlinePass;
        } else {
            newData[index].hardDeadlinePass = !newData[index].hardDeadlinePass;
        }

        if (newData[index].hardDeadlinePass && !newData[index].softDeadlinePass) {
            newData[index].points = 0.5;
        } else if (!newData[index].hardDeadlinePass && newData[index].softDeadlinePass) {
            newData[index].points = 0.0;
        } else if (newData[index].hardDeadlinePass && newData[index].softDeadlinePass) {
            newData[index].points = 1;
        } else {
            newData[index].points = -0.5;
        }

        const studentTasks = newData.filter(
            item => item.nickname === newData[index].nickname
        );
        const totalPoints = studentTasks.reduce(
            (acc, task) => acc + task.points, 0
        );
        studentTasks.forEach(task => task.totalPoints = totalPoints);

        setTableData(newData);
    }, [tableData]);

    const handlePointsChange = useCallback((index: number, value: string | null) => {
        if (value === null) {
            return;
        }
        const newData = [...tableData];
        newData[index].points = Number.parseFloat(value);
        newData[index].mark = computeMark(newData[index].points);
        const studentTasks = newData.filter(
            item => item.nickname === newData[index].nickname
        );
        const totalPoints = studentTasks.reduce(
            (acc, task) => acc + task.points, 0
        );
        studentTasks.forEach(task => task.totalPoints = totalPoints);

        setTableData(newData);
    }, [computeMark, tableData]);

    const rowSpanData = useMemo(() => {
        const groupSpanMap: Record<string, number> = {};
        const studentSpanMap: Record<string, Record<string, number>> = {};

        flattenedData.forEach((item) => {
            if (!groupSpanMap[item.groupName]) {
                groupSpanMap[item.groupName] = 0;
            }
            groupSpanMap[item.groupName] += 1;

            if (!studentSpanMap[item.groupName]) {
                studentSpanMap[item.groupName] = {};
            }
            if (!studentSpanMap[item.groupName][item.nickname]) {
                studentSpanMap[item.groupName][item.nickname] = 0;
            }
            studentSpanMap[item.groupName][item.nickname] += 1;
        });

        return flattenedData.map((item) => {
            const groupSpan = groupSpanMap[item.groupName];
            const studentSpan = studentSpanMap[item.groupName][item.nickname];
            if (studentSpan) {
                studentSpanMap[item.groupName][item.nickname] = 0;
                return {groupSpan, studentSpan};
            }
            return {groupSpan: 0, studentSpan: 0};
        });
    }, [flattenedData])

    const columns: ColumnsType<FlattenedData> = useMemo(() => [
        {
            align: 'center',
            title: 'Группа',
            dataIndex: 'groupName',
            onCell: (record, index) => {
                const groupSpan = index !== undefined && flattenedData.findIndex(
                    item => item.groupName === record.groupName
                ) === index ? flattenedData.filter(
                    item => item.groupName === record.groupName
                ).length : 0;
                return {
                    rowSpan: groupSpan,
                };
            },
            render: (groupName: string) => {
                const group = report.groups.find(
                    group => group.name === groupName
                );
                if (!group) {
                    return 'Студент не найден';
                }
                return (
                    <Popover style={{cursor: 'pointer'}} content={<GroupView group={group}/>}>
                        {groupName}
                    </Popover>
                );
            },
        },
        {
            align: 'center',
            title: 'Имя студента',
            dataIndex: 'nickname',
            onCell: (record, index) => {
                const studentSpan = index !== undefined && flattenedData.findIndex(
                    item => item.nickname === record.nickname && item.groupName === record.groupName
                ) === index ? flattenedData.filter(
                    item => item.nickname === record.nickname && item.groupName === record.groupName
                ).length : 0;
                return {
                    rowSpan: studentSpan,
                };
            },
            render: (studentNickname: string) => {
                const student = report.students.find(
                    student => student.nickname === studentNickname
                );
                if (!student) {
                    return 'Студент не найден';
                }
                return (
                    <Popover content={<StudentView student={student} showGraph={false}/>}>
                        <a href={student.repository}>{student.studentName}</a>
                    </Popover>
                );
            }
        },
        {
            align: 'center',
            title: 'Название задачи',
            dataIndex: 'task',
            render: (task: Task) => (<Popover content={<TaskView task={task}/>}>{task.title}</Popover>)
        },
        {
            align: 'center',
            title: 'Мягкий дедлайн',
            dataIndex: 'softDeadlinePass',
            render: (softDeadlinePass: boolean, _, index) => (
                <Popover content={
                    <Space direction="horizontal">
                        Мягкий Дедлайн: {tableData[index].task.softDeadline}
                    </Space>
                }>
                    {softDeadlinePass ? (
                        <Tag style={{cursor: 'pointer'}}
                             onClick={() => handleDeadlineChange(index, true)}
                             color="success">
                            Да
                        </Tag>) : (
                        <Tag style={{cursor: 'pointer'}}
                             onClick={() => handleDeadlineChange(index, true)}
                             color="error">
                            Нет
                        </Tag>)
                    }
                </Popover>
            )
        },
        {
            align: 'center',
            title: 'Жесткий дедлайн',
            dataIndex: 'hardDeadlinePass',
            render: (hardDeadlinePass: boolean, _, index) => (
                <Popover content={
                    <Space direction="horizontal">
                        Жесткий Дедлайн: {tableData[index].task.hardDeadline}
                    </Space>
                }>
                    {hardDeadlinePass ? (<Tag style={{cursor: 'pointer'}}
                                              onClick={() => handleDeadlineChange(index)} color="success">Да</Tag>) : (
                        <Tag style={{cursor: 'pointer'}}
                             onClick={() => handleDeadlineChange(index)} color="error">Нет</Tag>)}
                </Popover>
            )
        },
        {
            align: 'center',
            title: 'Сборка',
            dataIndex: 'build',
            render: (build: boolean) => (
                build ? <Tag color="success">Да</Tag> : <Tag color="error">Нет</Tag>
            )
        },
        {
            align: 'center',
            title: 'Документация',
            dataIndex: 'javadoc',
            render: (build: boolean) => (
                build ? <Tag color="success">Да</Tag> : <Tag color="error">Нет</Tag>
            )
        },
        {
            align: 'center',
            title: 'Всего тестов',
            dataIndex: 'totalTests',
            render: (tests: number) => tests === 0 ? (
                <Badge color="red" showZero count={0}/>
            ) : (
                <Badge color="#00000040" count={tests}/>
            )
        },
        {
            align: 'center',
            title: 'Успешных тестов',
            dataIndex: 'passedTests',
            render: (tests: number) => tests === 0 ? (
                <Badge color="red" showZero count={tests}/>
            ) : (
                <Badge color="green" count={tests}/>
            )
        },
        {
            align: 'center',
            title: 'Неуспешных тестов',
            dataIndex: 'failedTests',
            render: (tests: number) => tests === 0 ? <p>0</p> : (<Badge color="red" count={tests}/>)
        },
        {
            align: 'center',
            title: 'Баллов за задачу',
            dataIndex: 'points',
            render: (points: number, _, index) => <InputNumber<string>
                min="-0.5"
                defaultValue={points.toString()}
                value={points.toString()}
                step="0.5"
                onChange={(value) => handlePointsChange(index, value)}
                stringMode
                changeOnWheel
            />
        },
        {
            align: 'center',
            title: 'Всего баллов',
            dataIndex: 'totalPoints',
            onCell: (_, index) => ({
                rowSpan: index !== undefined ? rowSpanData[index].studentSpan : 0,
            }),
        },
        {
            align: 'center',
            title: 'Оценка',
            dataIndex: 'mark',
            render: (mark: number) => {
                if (mark === 2){
                    return (
                        <Badge color="red" showZero count={mark}/>
                    );
                }
                if (mark === 3) {
                    return (
                        <Badge color="gold" count={mark}/>
                    );
                }
                if (mark === 4) {
                    return (
                        <Badge color="#00000040" count={mark}/>
                    );
                }
                if (mark === 5) {
                    return (
                        <Badge color="green" count={mark}/>
                    );
                }
            },
            onCell: (_, index) => ({
                rowSpan: index !== undefined ? rowSpanData[index].studentSpan : 0,
            }),
        }
    ], [flattenedData, handleDeadlineChange, handlePointsChange, report.groups, report.students, rowSpanData, tableData]);

    return (
        <Space style={{width: '100%'}} align="center" direction="vertical">
            <FloatButton icon={ <DownloadOutlined />} shape="square" type="primary" onClick={() => exportToExcel()}/>

            <Table
                style={{height: '100vh'}}
                columns={columns}
                dataSource={tableData}
                pagination={false}
                sticky
            />
        </Space>
    );
}

export default TaskCheckView;
