import React from "react";
import axios from "axios";

export const RowContext = React.createContext();
const elementUrl = "http://cinemaplanet-env.eba-zpzpkar4.eu-central-1.elasticbeanstalk.com/acs/elements";
const actionUrl = "http://cinemaplanet-env.eba-zpzpkar4.eu-central-1.elasticbeanstalk.com/acs/actions";

export default class RowProvider extends React.Component {
  state = {
    seats: [],
    selectedSeats: [],
  };

  setSeats = (newseats) => {
    this.setState({seats: newseats});
  };

  getSeats = (rowId) => {
    const res = axios
        .get(`${elementUrl}/${rowId}/children`)
        .catch((error) => {
          alert("Error: " + error.response.status);
        });
    return res;
  };

  getSeat = (seatId) => {
    const res = axios
        .get(`${elementUrl}/${seatId}/children`)
        .catch((error) => {
          alert("Error: " + error.response.status);
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

  isMiddleSeat = (seat) => {
      let res = false;

      if(seat.location.lng === "0.0"){
        res = true;
      }
    return res;
  };

  isSeatAvailable = (seat) => {
    let res = true;
      if (seat.elementAttributes.available === false) {
        res = false;
      }
    return res;
  };

  render() {
    return (
        <RowContext.Provider
            value={{
              state: this.state,
              setSeats: this.setSeats,
              getSeats: this.getSeats,
              getSeat: this.getSeat,
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