import React from "react";
import "./App.css";
import MenuBar from "./components/MenuBar";
import Movies from "./components/Movies";
import Footer from "./components/Footer";
import Login from "./components/Login";
import Register from "./components/Register";
import Screenings from "./components/Screenings";
import { Container, Row, Col } from "react-bootstrap";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import UserProvider from "./components/context/UserContext";
import MovieProvider from "./components/context/MovieContext";
import TheaterProvider from "./components/context/TheaterContext";
import RowProvider from "./components/context/RowContext";
import Theater from "./components/theater/Theater";

function App() {
  const marginTop = {
    marginTop: "200px",
  };

  return (
    <Router>
      <UserProvider>
        <MovieProvider>
          <TheaterProvider>
            <RowProvider>
              <MenuBar />
              <Container>
                <Row>
                  <Col lg={12} style={marginTop}>
                    <Switch>
                      <Route path="/" exact component={Movies} />
                      <Route
                        path="/movie/screenings"
                        exact
                        component={Screenings}
                      />
                      <Route path="/movie/theater" exact component={Theater} />
                      <Route path="/login" exact component={Login} />
                      <Route path="/register" exact component={Register} />
                    </Switch>
                  </Col>
                </Row>
              </Container>
              <Footer />
            </RowProvider>
          </TheaterProvider>
        </MovieProvider>
      </UserProvider>
    </Router>
  );
}
export default App;
