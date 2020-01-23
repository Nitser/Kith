package com.project.scratchstudio.kith_andoid.network.model.board;

import com.project.scratchstudio.kith_andoid.network.model.board.Board;

public class BoardsResponse {

    boolean status;
    Board[] boards;

    public boolean getStatus() {
        return status;
    }
}
