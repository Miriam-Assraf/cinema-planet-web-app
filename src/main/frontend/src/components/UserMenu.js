import React from "react";
import { withStyles } from "@material-ui/core/styles";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import ListItemText from "@material-ui/core/ListItemText";
import Avatar from "@material-ui/core/Avatar";
import { UserContext } from "./context/UserContext";
import { Link } from "react-router-dom";

const StyledMenu = withStyles({
  paper: {
    border: "1px solid #d3d4d5",
  },
})((props) => (
  <Menu
    elevation={0}
    getContentAnchorEl={null}
    anchorOrigin={{
      vertical: "bottom",
      horizontal: "center",
    }}
    transformOrigin={{
      vertical: "top",
      horizontal: "center",
    }}
    {...props}
  />
));

const StyledMenuItem = withStyles((theme) => ({
  root: {
    "&:hover": {
      backgroundColor: "#8195a9",
      "& .MuiListItemText-primary": {
        color: "0c1115",
      },
    },
  },
}))(MenuItem);

export default function CustomizedMenus() {
  const [anchorEl, setAnchorEl] = React.useState(null);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const marginRight = {
    marginRight: "30px",
  };

  return (
    <UserContext.Consumer>
      {(context) => (
        <div>
          <Avatar
            src={context.state.user.avatar}
            style={marginRight}
            onClick={handleClick}
          />
          <StyledMenu
            id="customized-menu"
            anchorEl={anchorEl}
            keepMounted
            open={Boolean(anchorEl)}
            onClose={handleClose}
          >
            {context.state.loggedIn ? (
              <div>
                <StyledMenuItem
                  onClick={(event) => {
                    handleClose();
                    window.location.reload(true);
                  }}
                >
                  <ListItemText primary="Logout" />
                </StyledMenuItem>
              </div>
            ) : (
              <div>
                <StyledMenuItem onClick={handleClose}>
                  <Link
                    to="/login"
                    style={{ textDecoration: "none", color: "#0c1115" }}
                  >
                    <ListItemText primary="Login" />
                  </Link>
                </StyledMenuItem>
                <StyledMenuItem onClick={handleClose}>
                  <Link
                    to="/register"
                    style={{ textDecoration: "none", color: "#0c1115" }}
                  >
                    <ListItemText primary="Register" />
                  </Link>
                </StyledMenuItem>
              </div>
            )}
          </StyledMenu>
        </div>
      )}
    </UserContext.Consumer>
  );
}
