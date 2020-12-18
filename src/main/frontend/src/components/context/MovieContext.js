import React from "react";
import axios from "axios";

export const MovieContext = React.createContext();
const elementUrl = "http://cinemaplanet-env.eba-zpzpkar4.eu-central-1.elasticbeanstalk.com/acs/elements";

export default class MovieProvider extends React.Component {
    state = {
        movie: null,
        screenings: [],
    };

    setMovie = (newmovie) => {
        this.setState({movie: newmovie});
    };

    getScreenings = (movieId) => {
        const res = axios
            .get(`${elementUrl}/${movieId}/children`)
            .catch((error) => {
                alert("Error: " + error.response.status);
            });
        return res;
    };

    setScreenings = (newscreenings) => {
        this.setState({screenings: newscreenings});
        this.setState({showScreenings: true});
    };

    render() {
        return (
            <MovieContext.Provider
                value={{
                    state: this.state,
                    setMovie: this.setMovie,
                    getScreenings: this.getScreenings,
                    setScreenings: this.setScreenings,
                }}
            >
                {this.props.children}
            </MovieContext.Provider>
        );
    }
}