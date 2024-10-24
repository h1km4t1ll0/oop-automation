import { Column } from '@ant-design/plots';
import React from 'react';

const BarChart: React.FC = () => {
    const config = {
        data: {
            type: 'fetch',
            value: 'https://render.alipay.com/p/yuyan/180020010001215413/antd-charts/column-column.json',
        },
        xField: 'letter',
        yField: 'frequency',
        label: {
            text: (d: {frequency: number}) => `${(d.frequency * 100).toFixed(1)}%`,
            textBaseline: 'bottom',
        },
        axis: {
            y: {
                labelFormatter: '.0%',
            },
        },
        style: {
            // 圆角样式
            radiusTopLeft: 10,
            radiusTopRight: 10,
        },
    };
    return <Column {...config} />;
};

export default BarChart;