export default class Country {
    static getCountry = () => fetch("/COTrendGraph_war_exploded/country", {method: "GET"}).then(res => res.json());
}
