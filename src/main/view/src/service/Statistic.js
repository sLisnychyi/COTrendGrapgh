export default class Statistic {
    static getStatisticByOptions= option=>fetch("/statistic",{
        method:"PUT",
        body:JSON.stringify(option)
    }).then(res => res.json());
}