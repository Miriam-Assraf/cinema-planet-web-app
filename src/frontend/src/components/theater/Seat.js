import React, { Component } from "react";
import { RowContext } from "../context/RowContext";

export default class Seat extends Component {
  state = {
    seatColor: "#17d4ff",
  };

  // seatColor: #17d4ff selectedSeatColor: #ff7e00 unavailableSeatColor: #79a2ab

  setSeatColor = (isSelected, isAvailable, isMiddle) => {
    let color;
    if (isMiddle) {
      color = "#941dd3";
    } else {
      color = "#17d4ff";
    }
    if (isSelected) {
      color = "#ff7e00";
    }
    if (!isAvailable) {
      color = "#79a2ab";
    }
    return color;
  };

  render() {
    const { seatNumber, rowId, onSeatClick, isSelected } = this.props;
    const seatSize = "35";
    let isMiddle;
    let isAvailable = true;

    return (
      <RowContext.Consumer>
        {(context) => (
          <div
            onClick={() =>
              onSeatClick({ rowId: rowId, seatNumber, isAvailable })
            }
            className={"seat-item"}
          >
            {(isMiddle = context.isMiddleSeat(seatNumber))}

            {(isAvailable = context.isSeatAvailable(seatNumber))}

            <svg
              id={`${rowId}${seatNumber}`}
              data-name="Layer 1"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 577.53 532.66"
              height={seatSize}
              width={seatSize}
            >
              <title>seat</title>
              <path
                d="M197.76,824.93H775.29s0-158.42-86.9-158.42H286.53C197.76,666.51,197.76,824.93,197.76,824.93Z"
                transform="translate(-197.76 -292.27)"
                style={{
                  fill: this.setSeatColor(isSelected, isAvailable, isMiddle), //isSelected ? "#ff7e00" : "#17d4ff",
                }}
              />
              <path
                d="M292.15,631.67H686.53V331.6c0-11.24-1-39.33-30.22-39.33H331.47c-28.09,0-39.32,16.85-39.32,37.08Z"
                transform="translate(-197.76 -292.27)"
                style={{
                  fill: this.setSeatColor(isSelected, isAvailable, isMiddle), //isSelected ? "#ff7e00" : "#17d4ff"
                }}
              />
            </svg>
          </div>
        )}
      </RowContext.Consumer>
    );
  }
}
