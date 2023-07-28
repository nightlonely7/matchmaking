package com.example.matchmaking.data;

import com.example.matchmaking.model.SongModel;
import com.example.matchmaking.model.SongSelection;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class ModeData {
    private List<SongModel> songList;
    private SongSelection songSelection;
}
