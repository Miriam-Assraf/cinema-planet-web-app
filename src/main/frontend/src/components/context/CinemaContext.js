import React from "react";
import axios from "axios";

export const CinemaContext = React.createContext();
const elementUrl = "http://cinemaplanet-env.eba-zpzpkar4.eu-central-1.elasticbeanstalk.com/acs/elements";

export default class CinemaProvider extends React.Component {
    state = {
        movies: [],
    };

    setMovies = (newmovies) => {
        this.setState({ movies: newmovies });
    };

    getAllMovies = () => {
        const res = axios
            .get(`${elementUrl}/search/byType/movie`)
            .catch((error) => {
                alert("Error: " + error.response.status);
            });
        return res;
    };

    getMoviesByName = (name) => {
        const res = axios
            .get(`${elementUrl}/search/byName/${name}/byType/movie`)
            .catch((error) => {
                alert("Error: " + error.response.status);
            });
        return res;
    }
    render() {
        return (
            <CinemaContext.Provider
                value={{
                    state: this.state,
                    setMovies: this.setMovies,
                    getAllMovies: this.getAllMovies,
                    getMoviesByName: this.getMoviesByName,
                }}
            >
                {this.props.children}
            </CinemaContext.Provider>
        );
    }
}
