import React from "react";
import axios from "axios";

export const TheaterContext = React.createContext();
const elementUrl = "http://cinemaplanet-env.eba-zpzpkar4.eu-central-1.elasticbeanstalk.com/acs/elements";

const initialState = {
    seatsPerRow:[],
    rows: [],
    availableSeats: [],
};

export default class TheaterProvider extends React.Component {
    constructor(props) {
        super(props);

        this.state = initialState;
        this.setSeats = this.setSeats.bind(this);
    }

    resetState=()=>{
        this.setState(initialState);
    }

    setSeats= (newSeats, rowNum) => {
        let seats=[];
        newSeats.map((seat)=>
        {
            seats.push(seat);
        })

        //this.state.seatsPerRow.push(seats);
        let newState = {row : rowNum, seats:seats};
        //this.state.seatsPerRow.push(newState);
        this.setState({seatsPerRow: [...this.state.seatsPerRow, newState]})
    }

    getTheater = (screeningId) => {
        const res = axios
            .get(`${elementUrl}/${screeningId}/children`)
            .catch((error) => {
                alert("Error: " + error.response.status);
            });
        return res;
    };

    setRows = (newRows) => {
        if(this.state.rows.length===0) {
            newRows.map((row) => {
                this.state.rows.push(row)
            })
        }
    };

    getRows = (theaterId) => {
        const res = axios
            .get(`${elementUrl}/${theaterId}/children`)
            .catch((error) => {
                alert("Error: " + error.response.status);
            });
        return res;
    };

    render() {
        return (
            <TheaterContext.Provider
                value={{
                    state: this.state,
                    resetState:this.resetState,
                    setSeats: this.setSeats,
                    getTheater: this.getTheater,
                    setRows: this.setRows,
                    getRows: this.getRows,
                }}
            >
                {this.props.children}
            </TheaterContext.Provider>
        );
    }
}