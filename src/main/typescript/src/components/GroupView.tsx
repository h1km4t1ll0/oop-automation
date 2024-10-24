import React, {useCallback} from 'react';
import {Descriptions, Layout, Popover} from 'antd';
import type { DescriptionsProps } from 'antd';
import {Group} from "../types/Group.ts";
import StudentView from "./StudentView.tsx";


const GroupView: React.FC<{group: Group}> = ({group}) => {
    const makeUserDescription = useCallback((group: Group): DescriptionsProps['items'] => {
        return [
            {
                key: '1',
                label: 'Имя группы',
                children: group.name,
            },
            {
                key: '2',
                label: 'Студенты',
                children: group.students.map(
                    student => (
                        <>
                            <Popover content={<StudentView student={student} showGraph={false}/>}>{student.studentName}</Popover>
                            <br/>
                        </>
                    )
                ),
            },
        ];
    }, []);

    return (
        <Layout>
            <Descriptions layout="vertical" bordered title="Информация о группе" items={makeUserDescription(group)} />
        </Layout>
    );
}

export default GroupView;