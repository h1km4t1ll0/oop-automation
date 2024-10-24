import React, {useCallback} from 'react';
import {Descriptions, Layout} from 'antd';
import type { DescriptionsProps } from 'antd';
import {Task} from "../types/Task.ts";


const TaskView: React.FC<{task: Task}> = ({task}) => {
    const makeUserDescription = useCallback((task: Task): DescriptionsProps['items'] => {
        return [
            {
                key: '1',
                label: 'ID',
                children: task.id,
            },
            {
                key: '2',
                label: 'Название',
                children: task.title
            },
            {
                key: '3',
                label: 'Жесткий дедлайн',
                children: task.hardDeadline,
            },
            {
                key: '4',
                label: 'Мягкий дедлайн',
                children: task.softDeadline,
            },
        ];
    }, []);

    return (
        <Layout>
            <Descriptions bordered layout="vertical" title="Информация о задаче" items={makeUserDescription(task)} />
        </Layout>
    );
}

export default TaskView;