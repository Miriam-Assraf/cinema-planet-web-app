import React from "react";
import Seat from "./Seat";
import { RowContext } from "../context/RowContext";
import axios from "axios";
import {TheaterContext} from "../context/TheaterContext";
const elementUrl = "http://cinemaplanet-env.eba-zpzpkar4.eu-central-1.elasticbeanstalk.com/acs/elements";

const Row = ({ seatsInRow, rowNum, rowId, onSeatClick, selectedSeats, availableSeats}) => {

    let seats = [];
  if(Array.isArray(seatsInRow)) {
      if (seatsInRow.length === 5) {
           seatsInRow.map((obj) => {
               if (obj.row === rowNum) {
                   seats=obj.seats;
               }
           })
          return (
              <div className={"row-container"}>
                  {seats.map((seat) => (
                          <Seat
                              rowId={rowId}
                              rowNum={rowNum}
                              seat={seat}
                              onSeatClick={onSeatClick}
                              isSelected={selectedSeats.includes(seat)}
                          />
                  ))}
           </div>
          )
          //})
      }
      return null
  }
};

export default Row;
