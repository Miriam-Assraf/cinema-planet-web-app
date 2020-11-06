import React, { Component } from "react";
import "../../App.css";
import Seat from "./SeatIcon";
import Wheelchair from "../../images/wheelchair.svg";

const Legend = () => {
  const pStyle = {
    color: "white",
    fontSize: "10px",
  };

  const seatSize = "25px";

  return (
    <div>
      <Seat seatColor="#17d4ff" seatSize={seatSize} />
      <p style={pStyle}>Seat available</p>
      <Seat seatColor="#79a2ab" seatSize={seatSize} />
      <p style={pStyle}>Seat unavailable</p>
      <Seat seatColor="#ff7e00" seatSize={seatSize} />
      <p style={pStyle}>Selected seat</p>
      <Seat seatColor="#941dd3" seatSize={seatSize} />
      <p style={pStyle}>middle of screen width wise</p>
      <img src={Wheelchair} height={seatSize} width={seatSize} alt="seat" />
      <p style={pStyle}>Wheelchair</p>
      {/* <WheelChair /> */}
    </div>
  );
};

export default Legend;
