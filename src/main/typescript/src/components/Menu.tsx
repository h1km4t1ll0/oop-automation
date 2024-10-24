import {FC, ReactElement, useCallback, useState} from 'react';
import {LineChartOutlined, ReconciliationOutlined, SettingOutlined, TeamOutlined} from '@ant-design/icons';
import {Empty, Layout, Menu as AntMenu, MenuProps, Result} from 'antd';
import {useReportData} from "../providers/ReportDataProvider.ts";
import {MenuInfo} from "rc-menu/lib/interface";
import StudentView from "./StudentView.tsx";
import TaskView from "./TaskView.tsx";
import AdditionalSettingsView from "./AdditionalSettingsView.tsx";
import TaskCheckView from "./TaskCheckView.tsx";

type MenuItem = Required<MenuProps>['items'][number];
const {Content, Sider} = Layout;


const Menu: FC = () => {
    const [currentMenuOption, setCurrentMenuOption] = useState<string>("no_message")
    const onClick: MenuProps['onClick'] = useCallback((e: MenuInfo) => {
        setCurrentMenuOption(e.key);
        console.log('click ', e);
    }, []);

    const reportData = useReportData();

    const menuItemBuilder = useCallback((
        key: string,
        label: string,
        children: MenuItem[] | undefined = undefined
    ): MenuItem => {
        return {
            key: key,
            label: label,
            children: children
        };
    }, []);

    const getComponent = useCallback((menuKey: string): ReactElement => {
        try {
            const menuKeyPrefix = menuKey.split('/')[0];
            const menuKeyData = menuKey.split('/')[1];
            if (menuKeyPrefix === 'student') {
                const studentFound = reportData.students.find(
                    (student) => student.nickname === menuKeyData
                );
                if (!studentFound) {
                    const errorMessage = `Невозможно найти студента с никнеймом ${menuKeyData}`;

                    return (
                        <Result
                            status="error"
                            title="Произошла ошибка!"
                            subTitle={errorMessage}
                        />
                    );
                }
                return (<StudentView student={studentFound} showGraph/>);
            }

            if (menuKeyPrefix === 'task') {
                const taskFound = reportData.tasks.find(
                    (task) => task.id === menuKeyData
                );
                if (!taskFound) {
                    const errorMessage = `Невозможно найти задачу с ID ${menuKeyData}`;

                    return (
                        <Result
                            status="error"
                            title="Произошла ошибка!"
                            subTitle={errorMessage}
                        />
                    );
                }
                return (<TaskView task={taskFound} />);
            }

            if (menuKeyPrefix === 'additionalSettings') {
                return (<AdditionalSettingsView additionalSettings={reportData.additionalSettings} />);
            }

            if (menuKeyPrefix === 'taskCheckView') {
                return (<TaskCheckView />);
            }
        } catch (e) {
            console.error(e);
            return (
                <Result
                    status="error"
                    title="Произошла ошибка!"
                    subTitle="Пожалуйста, перезагрузите страницу и воспользуйтесь меню"
                />
            );
        }

        return (<Empty description="Нет данных">Воспользуйтесь меню</Empty>);
            }, [reportData.additionalSettings, reportData.students, reportData.tasks]
        )
    ;

    const buildMenu = useCallback(() => {
        const menuItemList: MenuItem[] = [
            {
                key: 'groups',
                label: 'Группы',
                icon: <TeamOutlined/>,
                children: reportData.groups.map(
                    (group) => menuItemBuilder(
                        group.name,
                        group.name,
                        group.students.map(
                            (student) => menuItemBuilder(
                                'student/' + student.nickname,
                                student.studentName,
                                undefined
                            )
                        )
                    )
                ),
            },
            {
                key: 'tasks',
                label: 'Задания',
                icon: <ReconciliationOutlined/>,
                children: reportData.tasks.map(
                    (task) => menuItemBuilder(
                        'task/' + task.id,
                        task.title
                    )
                ),
            },{
                key: 'taskCheckView',
                label: 'Итоги проверки',
                icon: <LineChartOutlined />
            },
            {
                key: 'additionalSettings',
                label: 'Настройки',
                icon: <SettingOutlined/>
            },
        ];

        return menuItemList;
    }, [menuItemBuilder, reportData.groups, reportData.tasks]);

    return (
        <Layout style={{height: '100%'}}>
            <Sider style={{height: '100%'}}>
                <AntMenu
                    onClick={onClick}
                    style={{width: '100%', height: '100%', borderRight: '0'}}
                    defaultSelectedKeys={['1']}
                    defaultOpenKeys={['sub1']}
                    mode="inline"
                    items={buildMenu()}
                />
            </Sider>
            <Layout>
                <Content style={{paddingLeft: 20, paddingRight: 20, margin: 0, overflow: 'auto'}}>
                    {getComponent(currentMenuOption)}
                </Content>
            </Layout>
        </Layout>
    );
};

export default Menu;
