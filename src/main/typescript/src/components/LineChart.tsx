import { Line } from '@ant-design/plots';
import React from "react";

const LineChart: React.FC<{
    data: {[x : string]: string | number}[],
    xField: string,
    yField: string
}> = ({data, xField, yField}) => {
    const config = {
        data,
        xField,
        yField,
        point: {
            shapeField: 'circle',
            sizeField: 4,
        },
        interaction: {
            tooltip: {
                marker: false,
            },
        },
        style: {
            lineWidth: 2
        },
        width: 800
    };
    return <Line {...config} />;
};

export default LineChart;
