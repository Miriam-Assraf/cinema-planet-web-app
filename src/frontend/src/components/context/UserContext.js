import React from "react";
import axios from "axios";

export const UserContext = React.createContext();
const userUrl = "http://cinemaplanet-env-2.eba-vd7pk3vm.eu-central-1.elasticbeanstalk.com/acs/users";

export default class UserProvider extends React.Component {
  state = {
    loggedIn: false,
    user: {
      email: "",
      username: "",
      role: "",
      avatar: "/broken-image.jpg",
    },
  };

  login = (useremail) => {
    const res = axios
      .get(`${userUrl}/login/${useremail}`)
      .catch(function (error) {
        alert(
          "Error:" +
            error.response.status +
            "\nNo user with email: " +
            useremail +
            " fond."
        );
      });
    return res;
  };

  register = (user) => {
    const res = axios.post(userUrl, user).catch(function (error) {
      alert(
        "Error:" +
          error.response.status +
          "\nRegistration failed.\nCasue: Invalid input."
      );
    });
    return res;
  };

  resetForm = (formId) => {
    document.getElementById(formId).reset();
  };

  setUser = (newUser) => {
    this.setState({ user: newUser });
    this.setState({ loggedIn: true });
  };

  render() {
    return (
      <UserContext.Provider
        value={{
          state: this.state,
          login: this.login,
          register: this.register,
          resetForm: this.resetForm,
          setUser: this.setUser,
        }}
      >
        {this.props.children}
      </UserContext.Provider>
    );
  }
}
