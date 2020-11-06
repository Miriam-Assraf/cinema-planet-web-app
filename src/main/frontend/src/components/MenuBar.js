import React from "react";
import logo from "../images/cinema-planet.png";
import { Navbar, Nav, Form } from "react-bootstrap";
import { UserContext } from "./context/UserContext";
import IconButton from "@material-ui/core/IconButton";
import InputAdornment from "@material-ui/core/InputAdornment";
import SearchIcon from "@material-ui/icons/Search";
import TextField from "@material-ui/core/TextField";
import UserMenu from "./UserMenu";

export default class MenuBar extends React.Component {
  render() {
    const searchStyle = {
      marginTop: "120px",
      backgroundColor: "#70feff",
    };

    const marginRight = {
      marginRight: "30px",
    };

    return (
      <UserContext.Consumer>
        {(context) => (
          <div>
            <Navbar fixed="top" bg="dark" variant="dark" name="logo">
              <Navbar.Brand href="/" className="navbar-brand">
                <img
                  src={logo}
                  className="app-logo"
                  alt="Logo"
                  width="100"
                  height="100"
                />
              </Navbar.Brand>
              <Nav className="mr-auto"></Nav>
              <Form inline>
                <UserMenu />
              </Form>
            </Navbar>
            <Navbar fixed="top" name="search" style={searchStyle}>
              <Nav className="mr-auto"></Nav>
              <Form inline>
                <TextField
                  style={marginRight}
                  label="Search..."
                  InputProps={{
                    endAdornment: (
                      <InputAdornment>
                        <IconButton>
                          <SearchIcon />
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />
              </Form>
            </Navbar>
          </div>
        )}
      </UserContext.Consumer>
    );
  }
}
