import React from "react";
import axios from "axios";

export const RowContext = React.createContext();
const elementUrl = "http://cinemaplanet-env-2.eba-vd7pk3vm.eu-central-1.elasticbeanstalk.com/acs/elements";
const actionUrl = "http://cinemaplanet-env-2.eba-vd7pk3vm.eu-central-1.elasticbeanstalk.com/acs/actions";

export default class RowProvider extends React.Component {
  state = {
    seats: [],
  };

  setSeats = (newseats) => {
    this.setState({ seats: newseats });
  };

  getSeats = (rowId) => {
    const res = axios
      .get(`${elementUrl}/manager@demo.com/${rowId}/children`)
      .catch((error) => {
        alert("Error: " + error.response.status);
      });
    return res;
  };

  getSeatByNumSeat = (seatnum) => {
    let res;
    this.state.seats.forEach((seat) => {
      if (seat.name === seatnum) {
        res = seat;
      }
    });
    return res;
  };

  getAction = (actionType, seat, userEmail) => {
    const action = {
      type: actionType,
      element: {
        elementId: seat.elementId,
      },
      invokedBy: {
        email: userEmail,
      },
    };
    const res = axios.post(actionUrl, action).catch(function (error) {
      alert("Error: " + error.response.status);
    });
    return res;
  };

  isMiddleSeat = (numSeat) => {
    let res = false;
    this.state.seats.forEach((seat) => {
      if (seat.name === numSeat && seat.location.lng === "0.0") {
        res = true;
      }
    });
    return res;
  };

  isSeatAvailable = (numSeat) => {
    let res = true;
    this.state.seats.forEach((seat) => {
      if (seat.name === numSeat && seat.elementAttributes.available === false) {
        res = false;
      }
    });
    return res;
  };

  render() {
    return (
      <RowContext.Provider
        value={{
          state: this.state,
          setSeats: this.setSeats,
          getSeats: this.getSeats,
          getSeatByNumSeat: this.getSeatByNumSeat,
          getAction: this.getAction,
          isMiddleSeat: this.isMiddleSeat,
          isSeatAvailable: this.isSeatAvailable,
        }}
      >
        {this.props.children}
      </RowContext.Provider>
    );
  }
}
