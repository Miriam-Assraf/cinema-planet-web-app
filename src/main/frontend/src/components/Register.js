import React from "react";
import { Card, Form, Button } from "react-bootstrap";
import { UserContext } from "./context/UserContext";

export default class Register extends React.Component {
  state = {
    user: {
      email: "",
      username: "",
      role: "PLAYER",
      avatar: "",
    },
  };

  handleChange = (event) => {
    const { user } = { ...this.state };
    const currentState = user;
    const { name, value } = event.target;
    currentState[name] = value;

    this.setState({ user: currentState });
  };

  render() {
    const marginBottom = {
      marginBottom: "80px",
    };

    return (
      <React.Fragment>
        <UserContext.Consumer>
          {(context) => (
            <Card
              className={"border border-dark bg-dark text-white"}
              style={marginBottom}
            >
              <Card.Body>
                <Form id="registerForm">
                  <Form.Group controlId="formBasicEmail">
                    <Form.Label>Email</Form.Label>
                    <Form.Control
                      required
                      type="email"
                      placeholder="Enter email"
                      name="email"
                      onChange={this.handleChange}
                    />
                    <Form.Text className="text-muted">
                      We'll never share your email with anyone else.
                    </Form.Text>
                  </Form.Group>

                  <Form.Group controlId="formBasicUsername">
                    <Form.Label>Username</Form.Label>
                    <Form.Control
                      required
                      type="text"
                      placeholder="Enter username"
                      name="username"
                      onChange={this.handleChange}
                    />
                  </Form.Group>

                  <Form.Group controlId="formBasicAvatar">
                    <Form.Label>Avatar</Form.Label>
                    <Form.Control
                      required
                      type="text"
                      placeholder="Enter image URL"
                      name="avatar"
                      onChange={this.handleChange}
                    />
                  </Form.Group>
                  <br />
                  <Button
                    onClick={async (event) => {
                      event.preventDefault();
                      const res = await context.register(this.state.user);
                      if (res) {
                        context.setUser(res.data);
                        this.props.history.push("/");
                      } else {
                        context.resetForm("registerForm");
                      }
                    }}
                  >
                    Register
                  </Button>
                </Form>
              </Card.Body>
            </Card>
          )}
        </UserContext.Consumer>
      </React.Fragment>
    );
  }
}
