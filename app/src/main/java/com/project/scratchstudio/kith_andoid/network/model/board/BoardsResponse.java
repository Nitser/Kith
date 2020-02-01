package com.project.scratchstudio.kith_andoid.network.model.board;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BoardsResponse {

    @SerializedName("boards")
    private ArrayList<Board> boards;

    public ArrayList<Board> getBoards() {
        return boards;
    }
}
