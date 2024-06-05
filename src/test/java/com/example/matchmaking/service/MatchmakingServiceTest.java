package com.example.matchmaking.service;

import com.example.matchmaking.data.MatchmakingData;
import com.example.matchmaking.model.MatchmakingUserModel;
import com.example.matchmaking.model.SongModel;
import com.example.matchmaking.model.UserMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MatchmakingServiceTest {

    @Mock
    private SimpMessageSendingOperations messagingTemplate;

    @Mock
    private MatchmakingData matchmakingData;

    @Mock
    private LeaderboardService leaderboardService;

    @Mock
    private ModeService modeService;

    @InjectMocks
    private MatchmakingService matchmakingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerUserWhenQueueIsNotFull() {
        Queue<MatchmakingUserModel> queue = new LinkedBlockingQueue<>();

        when(matchmakingData.getRegisteredQueue()).thenReturn(queue);
        matchmakingService.register(new UserMessage("user1"));

        verify(matchmakingData, times(6)).getRegisteredQueue();
        assertEquals(1, matchmakingData.getRegisteredQueue().size());
        assertEquals("user1", matchmakingData.getRegisteredQueue().peek().getUsername());
    }

    @Test
    public void registerUserWhenQueueIsFull() {
        Queue<MatchmakingUserModel> queue = new LinkedBlockingQueue<>();
        queue.add(new MatchmakingUserModel());
        queue.add(new MatchmakingUserModel());
        when(matchmakingData.getRegisteredQueue()).thenReturn(queue);

        matchmakingService.register(new UserMessage("user1"));

        verify(matchmakingData, times(6)).getRegisteredQueue();
//        verify(matchmakingData, times(1)).match();
    }

    @Test
    public void removeUserSuccessfully() {
        Queue<MatchmakingUserModel> queue = new LinkedBlockingQueue<>();
        MatchmakingUserModel user = new MatchmakingUserModel();
        user.setUsername("user1");
        queue.add(user);
        when(matchmakingData.getRegisteredQueue()).thenReturn(queue);

        matchmakingService.remove("user1");

        verify(matchmakingData, times(3)).getRegisteredQueue();
        verify(matchmakingData, times(3)).getRegisteredSet();
    }

    @Test
    public void removeUserNotRegistered() {
        Queue<MatchmakingUserModel> queue = new LinkedBlockingQueue<>();
        when(matchmakingData.getRegisteredQueue()).thenReturn(queue);

        matchmakingService.remove("user1");

        verify(matchmakingData, times(3)).getRegisteredQueue();
    }

    @Test
    public void matchUsersSuccessfully() {
        Queue<MatchmakingUserModel> queue = new LinkedBlockingQueue<>();
        queue.add(new MatchmakingUserModel());
        queue.add(new MatchmakingUserModel());
        when(matchmakingData.getRegisteredQueue()).thenReturn(queue);
        when(modeService.randomSongModel(any())).thenReturn(new SongModel());

        matchmakingService.match();

        verify(matchmakingData, times(2)).getRegisteredQueue();
        verify(matchmakingData, times(2)).getRegisteredSet();
    }
}