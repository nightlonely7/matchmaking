package com.example.matchmaking.service;

import com.example.matchmaking.model.SongModel;
import com.example.matchmaking.model.SongSelection;
import com.example.matchmaking.repository.ModeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModeService {
    private static final Random RANDOM = new Random();

    private final ModeRepository modeRepository;

    public void init() {
        modeRepository.init();
    }

    public List<SongModel> listSongModel() {
        return modeRepository.listSongModel();
    }

    public SongSelection getSongSelection() {
        return modeRepository.getSongSelection();
    }

    public SongModel randomSongModel(SongSelection songSelection) {
        List<SongModel> songModelList = modeRepository.listSongModel();
        List<SongModel> selectedSongModelList = songModelList.stream()
                .filter(songModel -> songModel.getMode().equalsIgnoreCase(songSelection.getMode())
                        && Objects.equals(songModel.getLevel(), songSelection.getLevel()))
                .toList();
        return selectedSongModelList.get(RANDOM.nextInt(selectedSongModelList.size()));
    }
}
