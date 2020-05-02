import React, {useEffect, useState} from 'react';
import Grid from "@material-ui/core/Grid";
import Charts from "../Charts/Charts";
import Statistic from "../../service/Statistic";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";
import Country from "../../service/Country";
import Button from "@material-ui/core/Button";
import Input from '@material-ui/core/Input';
import Checkbox from '@material-ui/core/Checkbox';
import {makeStyles} from '@material-ui/core/styles';
import Backdrop from "@material-ui/core/Backdrop";
import CircularProgress from "@material-ui/core/CircularProgress";
import ListItemText from "@material-ui/core/ListItemText";

const useStyles = makeStyles(theme => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: '#fff',
    },
    formControl: {
        margin: theme.spacing(1),
        fullWidth: true,
        display: 'flex',
        wrap: 'nowrap'
    },
    form: {
        margin: "11px"
    },
    selectionMenuContainer: {
        marginTop: "15px"
    }
}));

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
    PaperProps: {
        style: {
            maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
            width: 250,
        },
    },
};


const SelectionMenu = () => {
    const classes = useStyles();
    const [load, setLoad] = useState(false);
    const [data, setData] = useState([]);
    const [countries, setCountries] = useState([]);
    const [countriesSelected, setCountriesSelected] = useState(["Germany"])
    const [countriesSelection, setCountriesSelection] = useState([])
    const [criterion, setCriterion] = useState("confirmed");
    const [activeCountry, setActiveCountry] = useState("Ukraine");
    useEffect(() => {
        setLoad(true);
        Country.getCountry().then(res => {
            setCountries(res.filter((item, pos) => {
                return res.indexOf(item) === pos
            }));
        })
        setCountriesSelection(countriesSelected);
        loadData();
    }, []);

    const handleChangeActiveCountry = event => {
        setActiveCountry(event.target.value);
    }
    const handleChangeSelectedCountries = event => {
        setCountriesSelection(event.target.value);
    }
    const handleChangeCriterion = event=>{
        setCriterion(event.target.value)
    }

    const loadData = () => {
        const option = {
            "countries": countriesSelected.concat([activeCountry]),
            "criterion": criterion
        }
        Statistic.getStatisticByOptions(option).then(r => {
            setData(r.data);
            setLoad(false);
        });
    }
    const submitData = e => {
        e.preventDefault();
        console.log("-->");
        // loadData();
    }
    const handleClose = () => {
        setLoad(false);
    }
    return (
        <>
            <Grid container className={classes.selectionMenuContainer}>
                <Grid item xl={8} lg={8} md={8} sm={12} xs={12}>
                    <Charts data={data} countries={countriesSelected} activeCountry={activeCountry}/>
                </Grid>
                <Grid item xl={4} lg={4} md={4} sm={12} xs={12}>
                    <form noValidate={false} className={classes.form} onSubmit={submitData}>
                        <FormControl className={classes.formControl}>
                            <InputLabel id="active_country-label">Active country</InputLabel>
                            <Select
                                labelId="active_country-label"
                                id="active_country"
                                fullWidth
                                label="Active country"
                                value={activeCountry}
                                onChange={handleChangeActiveCountry}
                            >
                                {
                                    countries.map(c => <MenuItem key={c + Math.random()}
                                                                 value={c}>{c}</MenuItem>)
                                }
                            </Select>
                        </FormControl>
                        <FormControl className={classes.formControl}>
                            <InputLabel id="selected_country-label">Select other country</InputLabel>
                            <Select
                                labelId="selected_country-label"
                                id='selected_countries'
                                multiple
                                value={countriesSelection}
                                onChange={handleChangeSelectedCountries}
                                input={<Input/>}
                                renderValue={selected => selected.join(", ")}
                                MenuProps={MenuProps}
                            >
                                {
                                    countries.map(con => (<MenuItem key={con + Math.random()} value={con}>
                                        <Checkbox checked={countriesSelection.indexOf(con) > -1}/>
                                        <ListItemText primary={con}/>
                                    </MenuItem>))
                                }
                            </Select>
                        </FormControl>
                        <FormControl className={classes.formControl}>
                            <InputLabel id="criterion-label">Criterion</InputLabel>
                            <Select
                                labelId="criterion-label"
                                id="criterion"
                                fullWidth
                                label="Criterion"
                                value={criterion}
                                onChange={handleChangeCriterion}
                            >
                                <MenuItem value={"confirmed"}>Confirmed</MenuItem>
                                <MenuItem value={"deaths"}>Deaths</MenuItem>
                                <MenuItem value={"recovered"}>Recovered</MenuItem>
                            </Select>
                        </FormControl>
                        <Button
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