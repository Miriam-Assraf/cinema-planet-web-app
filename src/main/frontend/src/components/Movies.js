import React from "react";
import { Row, Col } from "react-bootstrap";
import axios from "axios";
import { MovieContext } from "./context/MovieContext";
import { UserContext } from "./context/UserContext";
import { Link } from "react-router-dom";
import {CinemaContext} from "./context/CinemaContext";

export default class Movies extends React.Component {
  elementUrl = "http://cinemaplanet-env.eba-zpzpkar4.eu-central-1.elasticbeanstalk.com/acs/elements";

  constructor() {
    super();
    this.state = {
      movies: [],
    };
  }

  componentDidMount() {
    axios
      .get(this.elementUrl + "/search/byType/movie")
      .then((response) => response.data)
      .then((data) => {
        this.setState({ movies: data });
      });
  }

  render() {
    const fontstyle = {
      color: "white",
      padding: "10px",
      fontSize: "15px",
      fontFamily: "Century Gothic",
    };

    const headerstyle = {
      color: "white",
      padding: "10px",
      fontWeight: "bold",
      fontSize: "50px",
      fontFamily: "Century Gothic",
    };

    return (
      <React.Fragment>
        <UserContext.Consumer>
          {(userContext) => (
            <MovieContext.Consumer>
              {(movieContext) =>
                  <div>
                    <h1>
                      <p style={headerstyle}>Movies</p>
                    </h1>

                    <Row>
                      {this.state.movies.map((movie) => (
                        <Col key={movie.elementId}>
                          <Link
                            to="/movie/screenings"
                            style={{ textDecoration: "none" }}
                          >
                            <img
                              src={movie.elementAttributes.poster}
                              height="300"
                              width="200"
                              alt="movie-poster"
                              onClick={async (event) => {
                                event.preventDefault();
                                movieContext.setMovie(movie);
                                const res = await movieContext.getScreenings(
                                  movie.elementId
                                );
                                if (res) {
                                  movieContext.setScreenings(res.data);
                                  this.props.history.push("/movie/screenings");
                                }
                              }}
                            />
                            {/*onClick call all children by movie.elementId (screenings) new compenent*/}
                            <br />
                            <p style={fontstyle}>{movie.name}</p>
                          </Link>
                        </Col>
                      ))}
                    </Row>

                    <br />
                  </div>
              }
            </MovieContext.Consumer>
          )}
        </UserContext.Consumer>
      </React.Fragment>
    );
  }
}
