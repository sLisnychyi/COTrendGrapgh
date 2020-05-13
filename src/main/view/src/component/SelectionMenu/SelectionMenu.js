import React, {useEffect, useState} from 'react';
import Grid from "@material-ui/core/Grid";
import Charts from "../Charts/Charts";
import Statistic from "../../service/Statistic";
import Country from "../../service/Country";
import Button from "@material-ui/core/Button";
import {makeStyles} from '@material-ui/core/styles';
import Backdrop from "@material-ui/core/Backdrop";
import CircularProgress from "@material-ui/core/CircularProgress";
import Select from 'react-select-me';
import 'react-select-me/lib/ReactSelectMe.css';
import Typography from "@material-ui/core/Typography";

const useStyles = makeStyles(theme => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: '#fff',
    },
    form: {
        margin: "11px"
    },
    selectionMenuContainer: {
        marginTop: "15px"
    },
    buttonSubmit: {
        marginTop: "15px",
        zIndex:"0"
    },
    inputSelect:{
        zIndex:"-2"
    }
}));


const SelectionMenu = () => {
    const classes = useStyles();
    const [load, setLoad] = useState(false);
    const [data, setData] = useState([]);
    const [countries, setCountries] = useState([]);
    const [countriesSelected, setCountriesSelected] = useState(["Germany"])
    const [countriesSelection, setCountriesSelection] = useState([])
    const [criterion, setCriterion] = useState("confirmed");
    const [activeCountrySelected, setActiveCountrySelected] = useState("Ukraine");
    const [activeCountrySelection, setActiveCountrySelection] = useState("");
    useEffect(() => {
        setLoad(true);
        Country.getCountry().then(res => {
            setCountries(res.filter((item, pos) => {
                return res.indexOf(item) === pos
            }));
        })
        setCountriesSelection(countriesSelected);
        setActiveCountrySelection(activeCountrySelected);

        const option = {
            "countries": countriesSelected.concat([activeCountrySelected]),
            "criterion": criterion
        }
        Statistic.getStatisticByOptions(option).then(r => {
            setData(r.data);
            setLoad(false);
        });
        // eslint-disable-next-line
    }, []);

    const handleChangeActiveCountry = value => {

        let index = countriesSelection.indexOf(value.value);
        if (index !== -1) countriesSelection.splice(index, 1);
        setCountriesSelection(countriesSelection);
        setActiveCountrySelection(value.value);
    }
    const handleChangeSelectedCountries = value => {
        setCountriesSelection(value.map(e => e.value));
    }
    const handleChangeCriterion = value => {
        setCriterion(value.value)
    }

    const submitData = e => {
        e.preventDefault();
        const option = {
            "countries": countriesSelection.concat([activeCountrySelection]),
            "criterion": criterion
        }
        Statistic.getStatisticByOptions(option).then(r => {
            setData(r.data);
            setActiveCountrySelected(activeCountrySelection);
            setCountriesSelected(countriesSelection);
            setLoad(false);
        });
    }
    const handleClose = () => {
        setLoad(false);
    }
    const getOption = () => {
        let rem = [...countries];
        let index = countries.indexOf(activeCountrySelection);
        if (index !== -1) rem.splice(index, 1);
        return rem;
    }
    return (
        <>
            <Grid container className={classes.selectionMenuContainer}>
                <Grid item xl={8} lg={8} md={8} sm={12} xs={12}>
                    <Charts data={data} countries={countriesSelected} activeCountry={activeCountrySelected}/>
                </Grid>
                <Grid item xl={4} lg={4} md={4} sm={12} xs={12}>
                    <form noValidate={false} className={classes.form} onSubmit={submitData}>
                        <Typography variant="body1">
                            Select active country
                        </Typography>
                        <Select
                            className={classes.selectionMenuContainer}
                            options={countries}
                            value={activeCountrySelection}
                            onChange={handleChangeActiveCountry}
                        />
                        <Typography variant="body1">
                            Select other countries
                        </Typography>
                        <Select
                            className={classes.selectionMenuContainer}
                            options={getOption()}
                            value={countriesSelection}
                            onChange={handleChangeSelectedCountries}
                            beforeClose={e => !e.target.classList.contains("dd__option")}
                            multiple={true}
                        />
                        <Typography variant="body1">
                            Select criterion
                        </Typography>
                        <Select
                            className={classes.selectionMenuContainer}
                            options={[
                                {value: "confirmed", label: "Confirmed"},
                                {value: "deaths", label: "Deaths"},
                                {value: "recovered", label: "Recovered"}]}
                            value={criterion}
                            onChange={handleChangeCriterion}
                        />
                        <Button
                            className={classes.buttonSubmit}
                            type="submit"
                            fullWidth
                            variant="contained"
                            color="primary"
                        >
                            Enter
                        </Button>
                    </form>
                </Grid>
            </Grid>
            <Backdrop className={classes.backdrop} open={load} onClick={handleClose}>
                <CircularProgress color="inherit"/>
            </Backdrop>
        </>
    );
};

export default SelectionMenu;