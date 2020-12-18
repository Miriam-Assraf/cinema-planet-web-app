package com.miriam.assraf.backend.utils;

public enum ActionType {
    ORDER_SEAT("order seat"),
    GET_MIDDLE_SEAT("get middle seat"),
    GET_MIDDLE_ROW("get middle row"),
    GET_AVAILABLE_SEATS("get available seats"),
    GET_ROW_BY_DISTANCE("get row by distance");

    private final String action;

    ActionType(final String action){
        this.action=action;
    }

    @Override
    public String toString() {
        return action;
    }
}
