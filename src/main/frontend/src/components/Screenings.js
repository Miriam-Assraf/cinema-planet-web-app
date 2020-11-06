import React from "react";
import { Jumbotron, Button } from "react-bootstrap";
import { MovieContext } from "./context/MovieContext";
import { TheaterContext } from "./context/TheaterContext";
import { RowContext } from "./context/RowContext";
import DayPicker from "react-day-picker";
import "react-day-picker/lib/style.css";
import { FaCalendarAlt } from "react-icons/fa";

export default class Screenings extends React.Component {
  state = {
    screenings: [],
    showCalender: false,
  };

  handleShowCalender = () => {
    this.setState({ showCalender: !this.state.showCalender });
  };

  // sameDate = (screenings) => {
  //   if (this.index === 0) {
  //     this.index++;
  //     return false;
  //   } else if (screenings[this.index] === screenings[this.index - 1]) {
  //     this.index++;
  //     return true;
  //   } else {
  //     this.index++;
  //     return false;
  //   }
  // };

  render() {
    const headerstyle = {
      padding: "10px",
      fontWeight: "bold",
      fontSize: "50px",
      fontFamily: "Century Gothic",
    };

    const fontstyle = {
      fontWeight: "bold",
      fontSize: "15px",
      fontFamily: "Century Gothic",
    };

    return (
      <MovieContext.Consumer>
        {(movieContext) => (
          <TheaterContext.Consumer>
            {(theaterContext) => (
              <RowContext.Consumer>
                {(rowContext) => (
                  <Jumbotron>
                    <h1 style={headerstyle}>Screenings</h1>
                    <button onClick={this.handleShowCalender}>
                      <FaCalendarAlt />
                    </button>
                    <br />
                    {this.state.showCalender ? <DayPicker /> : null}
                    <br />
                    {movieContext.state.screenings.map((screening) => (
                      <div id={screening.elementId}>
                        <Button
                          style={fontstyle}
                          variant="light"
                          onClick={async (event) => {
                            event.preventDefault();
                            const restheater = await theaterContext.getTheater(
                              screening.elementId
                            );
                            if (restheater) {
                              const resrows = await theaterContext.getRows(
                                restheater.data[0].elementId
                              );
                              if (resrows) {
                                theaterContext.setRows(resrows.data);
                                resrows.data.map(async (row) => {
                                  const resseats = await rowContext.getSeats(
                                    row.elementId
                                  );
                                  if (resseats) {
                                    rowContext.setSeats(resseats.data);
                                  }
                                });
                                this.props.history.push("/movie/theater");
                              }
                            }
                          }}
                        >
                          {screening.elementAttributes.date} :{" "}
                          {screening.elementAttributes.time}
                        </Button>
                        <hr />
                      </div>
                    ))}
                    <p>
                      <Button
                        variant="dark"
                        size="sm"
                        style={fontstyle}
                        onClick={(event) => {
                          event.preventDefault();
                          this.props.history.push("/");
                        }}
                      >
                        Back to movies
                      </Button>
                    </p>
                  </Jumbotron>
                )}
              </RowContext.Consumer>
            )}
          </TheaterContext.Consumer>
        )}
      </MovieContext.Consumer>
    );
  }
}
