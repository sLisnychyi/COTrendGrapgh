import React from 'react';
import Container from "@material-ui/core/Container";
import {
    LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
} from 'recharts';

const Charts = (props) => {
    const {data, countries, activeCountry} = props;
    const randomColor = () => '#' + Math.floor(Math.random() * 16777215).toString(16);
    return (
        <div>
            <Container>
                {
                    data.length > 0 && <ResponsiveContainer width="98%" minHeight={350}>
                        <LineChart
                            data={data}
                            margin={{
                                top: 5, right: 5, left: 5, bottom: 5,
                            }}
                        >
                            <CartesianGrid vertical={false}/>
                            <XAxis dataKey="name" tickLine={false}/>
                            <YAxis axisLine={false} tickLine={false}/>
                            <Tooltip/>
                            <Legend/>
                            <Line type="monotone" dataKey={activeCountry} stroke="#a83232" dot={false} strokeWidth={3}/>

                            {
                                countries.map(country => {
                                    return <Line key={country} type="monotone" dataKey={country} stroke={randomColor()} dot={false}/>
                                })
                            }
                        </LineChart>
                    </ResponsiveContainer>
                }
            </Container>
        </div>
    );
};

export default Charts;