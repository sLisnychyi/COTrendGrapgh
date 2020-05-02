import React, {useState, useEffect} from 'react';
import Container from "@material-ui/core/Container";
import {
    LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
} from 'recharts';
import Box from "@material-ui/core/Box";
import Statistic from "../service/Statistic";

const Charts = () => {
    const [load, setLoad] = useState(false);
    const [data, setData] = useState([]);
    useEffect(() => {
        const option = {
            "countries": [
                "Ukraine",
                "Germany"
            ],
            "criterion": "confirmed"
        }
        Statistic.getStatisticByOptions(option).then(r => {
            setData(r.data);
        });
    }, []);
    console.log(data);
    return (
        <div>
            <Container>
                <Box pt={4}>
                    {
                        data.length > 0 && <ResponsiveContainer width='100%' minHeight={350}>
                            <LineChart
                                width={500}
                                height={300}
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
                                <Line type="monotone" dataKey="Ukraine" stroke="#8884d8" dot={false}/>
                                <Line type="monotone" dataKey="Germany" stroke="#82ca9d" dot={false}/>
                            </LineChart>
                        </ResponsiveContainer>
                    }

                </Box>
            </Container>
        </div>
    )
        ;
};

export default Charts;