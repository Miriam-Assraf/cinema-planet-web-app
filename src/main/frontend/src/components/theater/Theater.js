import "../../App.css";
import React, { Component } from "react";
import Row from "./Row";
import { Button } from "react-bootstrap";
import { Badge } from "react-bootstrap";
import { UserContext } from "../context/UserContext";
import { TheaterContext } from "../context/TheaterContext";
import { RowContext } from "../context/RowContext";
import Legend from "./legend";
export default class Theater extends Component {
  theaterId;
  state = {
    rows: [],
    selectedSeats: [],
  };

  constructor() {
    super();
  }

  onSeatClick = (rowNum, seatNum, isAvailable, seat) => {
    let newSelected = this.state.selectedSeats;

    const seatToRemove = newSelected.findIndex(
        //if seat is already on selectedSeats it will return index of seat else it will return -1
        (s) => s.name === seatNum //&& s.rowId === rowId
    );

    if (seatToRemove === -1 && isAvailable) {
      newSelected.push(seat); // clicked seat is unselected yet
    } else if(seatToRemove !== -1 && isAvailable){
      newSelected.splice(seatToRemove, 1); //  seatToRemove is index of seat to be unselected, 1 is so only it will be removed from list
    }
    this.setState({ selectedSeats : newSelected });
  };

  render() {
    const fontstyle = {
      fontWeight: "bold",
      fontSize: "15px",
      fontFamily: "Century Gothic",
    };

    return (
        <UserContext.Consumer>
          {(userContext) => (
              <TheaterContext.Consumer>
                {(theaterContext) => (
                    <RowContext.Consumer>
                      {(rowContext) => (
                          <div>
                            <div className="movie-info-in-theater"></div>
                            <div
                                className={"screen"}
                                style={{
                                  backgroundColor: "white",
                                  width: 630,
                                  height: 10,
                                }}
                            ></div>
                            <h2 style={{ marginLeft: 570, color: "white" }}>Screen</h2>

                            <div className={"center-theater-container"}>
                              <Legend className="legend-container" />
                              <div className={"seats-container"}>
                                {theaterContext.state.rows.map((r) => (
                                    <div className="row-container">
                                      <h5
                                          style={{
                                            color: "white",
                                            margin: 7,
                                          }}
                                      >
                                        {r.name}
                                      </h5>
                                      <Row
                                          seatsInRow={theaterContext.state.seatsPerRow}
                                          selectedSeats={this.state.selectedSeats}
                                          availableSeats={theaterContext.state.availableSeats}
                                          rowNum={r.name}
                                          rowId={r.elementId}
                                          onSeatClick={this.onSeatClick}
                                      />
                                    </div>
                                ))}
                              </div>
                            </div>
                            <h4 className="number-of-seats-counter">
                              Number of selected seats{" "}
                              <Badge className="seat-item" variant="secondary">
                                {this.state.selectedSeats.length}
                              </Badge>
                            </h4>
                            <br />
                            <div class="text-center">
                              <Button
                                  variant="light"
                                  size="sm"
                                  style={fontstyle}
                                  onClick={(event) => {
                                    if (this.state.selectedSeats.length > 0) {
                                      event.preventDefault();
                                      this.state.selectedSeats.forEach(async (seat) => {
                                        const res = await rowContext.getAction(
                                            "order seat",
                                            seat,
                                            userContext.state.user.email
                                        );
                                      });
                                      this.props.history.push("/");
                                    }
                                  }
                                  }
                              >
                                Finish order
                              </Button>
                              <br />
                              <br />
                              <Button
                                  variant="light"
                                  size="sm"
                                  style={fontstyle}
                                  onClick={(event) => {
                                    event.preventDefault();
                                    this.props.history.push("/movie/screenings");
                                  }}
                              >
                                Back to screenings
                              </Button>
                            </div>
                          </div>
                      )}
                    </RowContext.Consumer>
                )}
              </TheaterContext.Consumer>
          )}
        </UserContext.Consumer>
    );
  }
}