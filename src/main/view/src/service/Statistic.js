export default class Statistic {
    static getStatisticByOptions= option=>fetch("/COTrendGraph_war_exploded/statistic",{
        method:"PUT",
        body:JSON.stringify(option)
    }).then(res => res.json());
}