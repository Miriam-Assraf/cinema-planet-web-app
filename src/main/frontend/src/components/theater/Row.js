import React from "react";
import Seat from "./Seat";
import { RowContext } from "../context/RowContext";

const Row = ({ seatsFromDb, rowId, onSeatClick, selectedSeats }) => {
  const seats = [];

  // selectedRowSeats will have only seats number in row that was selected
  const selectedRowSeats = selectedSeats
    .filter((s) => s.rowId === rowId)
    .map((s) => s.seatNumber);

  seatsFromDb.map((seat) => {
    let isSelected;
    //let isAvailable;

    selectedRowSeats.includes(seat.name)
      ? (isSelected = true)
      : (isSelected = false);

    seats.push({ seat, isSelected });
  });

  return (
    <RowContext.Consumer>
      {(context) => (
        <div className={"row-container"}>
          {seats.map((s) => (
            <Seat
              rowId={rowId}
              seatNumber={s.seat.name}
              onSeatClick={onSeatClick}
              isSelected={s.isSelected}
              isAvaiable={s.isAvaiable}
            />
          ))}
        </div>
      )}
    </RowContext.Consumer>
  );
};

export default Row;
