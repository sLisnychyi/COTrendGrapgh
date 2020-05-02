export default class Country {
    static getCountry = () => fetch("/country", {method: "GET"}).then(res => res.json());
}
