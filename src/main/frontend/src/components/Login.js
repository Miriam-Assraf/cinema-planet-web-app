import React from "react";
import { Card, Form, Button } from "react-bootstrap";
import { UserContext } from "./context/UserContext";
import { Link } from "react-router-dom";

class Login extends React.Component {
  state = {
    email: "",
    password: "",
  };

  handleChange = (event) => {
    this.setState({ [event.target.name]: [event.target.value] });
  };

  render() {
    const marginBottom = {
      marginBottom: "10px",
    };

    return (
      <React.Fragment>
        <UserContext.Consumer>
          {(context) => (
            <Card className={"border border-dark bg-dark text-white"}>
              <Card.Body>
                <Form id="loginForm">
                  <React.Fragment>
                    <Form.Group controlId="formBasicEmail">
                      <Form.Label>Email</Form.Label>
                      <Form.Control
                        required
                        name="email"
                        type="email"
                        placeholder="Enter email"
                        onChange={this.handleChange}
                      />
                      <Form.Text className="text-muted">
                        We'll never share your email with anyone else.
                      </Form.Text>
                    </Form.Group>

                    <Form.Group controlId="formBasicPassword">
                      <Form.Label>Password</Form.Label>
                      <Form.Control
                        required
                        name="password"
                        type="password"
                        placeholder="Password"
                        onChange={this.handleChange}
                      />
                    </Form.Group>
                    <br />
                    <Button
                      style={marginBottom}
                      onClick={async (event) => {
                        event.preventDefault();
                        const res = await context.login(this.state.email);
                        if (res) {
                          context.setUser(res.data);
                          this.props.history.push("/");
                        } else {
                          context.resetForm("loginForm");
                        }
                      }}
                    >
                      Login
                    </Button>
                  </React.Fragment>
                </Form>
                <Card.Footer>
                  Not registered?
                  <Link to="/register"> register now</Link>
                </Card.Footer>
              </Card.Body>
            </Card>
          )}
        </UserContext.Consumer>
      </React.Fragment>
    );
  }
}

export default Login;
