import React from "react";
import axios from "axios";

export const TheaterContext = React.createContext();
const elementUrl = "http://cinemaplanet-env-2.eba-vd7pk3vm.eu-central-1.elasticbeanstalk.com/acs/elements";

export default class TheaterProvider extends React.Component {
  state = {
    rows: [],
  };

  getTheater = (screeningId) => {
    const res = axios
      .get(`${elementUrl}/manager@demo.com/${screeningId}/children`)
      .catch((error) => {
        alert("Error: " + error.response.status);
      });
    return res;
  };

  setRows = (newrows) => {
    this.setState({ rows: newrows });
  };

  getRows = (theaterId) => {
    const res = axios
      .get(`${elementUrl}/manager@demo.com/${theaterId}/children`)
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
