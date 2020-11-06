import React, { Component } from "react";

class SeatIcon extends Component {
  //state = {  }
  render() {
    return (
      <svg
        id="Layer_1"
        data-name="Layer 1"
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 577.53 532.66"
        height={this.props.seatSize}
        width={this.props.seatSize}
      >
        <title>seatnew2</title>
        <path
          d="M197.76,824.93H775.29s0-158.42-86.9-158.42H286.53C197.76,666.51,197.76,824.93,197.76,824.93Z"
          transform="translate(-197.76 -292.27)"
          style={{ fill: this.props.seatColor }}
        />
        <path
          d="M292.15,631.67H686.53V331.6c0-11.24-1-39.33-30.22-39.33H331.47c-28.09,0-39.32,16.85-39.32,37.08Z"
          transform="translate(-197.76 -292.27)"
          style={{ fill: this.props.seatColor }}
        />
      </svg>
    );
  }
}

export default SeatIcon;
