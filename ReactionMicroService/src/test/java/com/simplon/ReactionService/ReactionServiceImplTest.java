package com.simplon.ReactionService;

import com.simplon.ReactionService.feignclients.notification.NotificationClient;
import com.simplon.ReactionService.feignclients.notification.NotificationDTO;
import com.simplon.ReactionService.feignclients.publication.PostDTO;
import com.simplon.ReactionService.feignclients.publication.PublicationServiceClient;
import com.simplon.ReactionService.feignclients.user.Gender;
import com.simplon.ReactionService.feignclients.user.Role;
import com.simplon.ReactionService.feignclients.user.UserClient;
import com.simplon.ReactionService.feignclients.user.UserDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReactionServiceImplTest {

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private ReactionMapper reactionMapper;
    @Mock
    private PublicationServiceClient publicationServiceClient;
    @Mock
    private NotificationClient notificationClient;


    @Mock
    private UserClient userClient;

    @InjectMocks
    private ReactionServiceImpl reactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

   @Test
    void testGetAllReactionsByPostId() {
        long postId = 1L;
        Pageable pageable = Pageable.unpaged();
        when(reactionRepository.findByPostId(eq(postId), any(Pageable.class)))
                .thenReturn(Page.empty());
        Page<ReactionDTO> result = reactionService.getAllReactionsByPostId(postId, pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reactionRepository, times(1)).findByPostId(eq(postId), any(Pageable.class));
    }
    @Test
    void testGetAllReactionsByUserId() {

        long userId = 1L;
        Pageable pageable = Pageable.unpaged();
        when(reactionRepository.findByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(Page.empty());
        Page<ReactionDTO> result = reactionService.getAllReactionsByUseId(userId, pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reactionRepository, times(1)).findByUserId(eq(userId), any(Pageable.class));
    }


    @Test
    void addReactionToPost() {
        ReactionDTO reactionDTO = new ReactionDTO(1L, 2L, 1L, TypeReaction.LIKE,LocalDateTime.now());
        Reaction reaction = new Reaction();
        when(reactionRepository.findReactionByUserIdAndPostId(anyLong(), anyLong()))
                .thenReturn(Optional.of(new Reaction()));
        when(reactionMapper.toEntity(reactionDTO)).thenReturn(reaction);
        when(reactionMapper.toDTO(reaction)).thenReturn(reactionDTO);
        verify(reactionRepository, never()).save(reaction);
        verify(reactionMapper, never()).toDTO(reaction);
    }


    @Test
    void removeReactionFromAPost_Success() {
        Long reactionId = 1L;
        Reaction existingReaction = new Reaction();
        existingReaction.setId(reactionId);
        when(reactionRepository.findById(reactionId)).thenReturn(Optional.of(existingReaction));
        assertDoesNotThrow(() -> reactionService.removeReactionFromAPost(reactionId));
        verify(reactionRepository, times(1)).deleteById(reactionId);
    }

    @Test
    void removeReactionFromAPost_WhenReactionNotFound() {
        Long reactionId = 1L;
        when(reactionRepository.findById(reactionId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> reactionService.removeReactionFromAPost(reactionId));

        assertEquals("Reaction with id " + reactionId + " not found", exception.getMessage());
        verify(reactionRepository, never()).deleteById(anyLong());
    }


    @Test
    void testUpdateReaction() {
        ReactionDTO reactionDTO = new ReactionDTO();
        reactionDTO.setTypeReaction(TypeReaction.LIKE);
        Reaction existingReaction = new Reaction();
        PostDTO postDTO = new PostDTO();
        postDTO.setPostId(1L);
        postDTO.setContent("content");
        postDTO.setUserId(1L);
        UserDto userDto = UserDto.builder()
                .idUser(1L)
                .firstName("firstName").lastName("lastName").email("email")
                .username("username").password("password")
                .gender(Gender.MALE).role(Role.USER).build();
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .contentNotif("A réagi à votre post")
                .typeNotif(reactionDTO.getTypeReaction().toString())
                .readNotif(false)
                .senderId(userDto.getIdUser())
                .recipientId(postDTO.getUserId())
                .dateNotif(LocalDateTime.now())
                .build();
        existingReaction.setUserId(1L);
        existingReaction.setPostId(1L);
        Optional<Reaction> existingReactionOptional = Optional.of(existingReaction);
        when(reactionMapper.toEntity(eq(reactionDTO))).thenReturn(existingReaction);
        when(reactionRepository.findReactionByUserIdAndPostId(eq(1L), eq(1L))).thenReturn(existingReactionOptional);
        when(reactionRepository.save(eq(existingReaction))).thenReturn(existingReaction);
        when(reactionMapper.toDTO(eq(existingReaction))).thenReturn(reactionDTO);
        when(publicationServiceClient.getPostById(eq(1L))).thenReturn(ResponseEntity.ok(postDTO));
        when(userClient.getUserById(eq(1L))).thenReturn(ResponseEntity.ok(userDto));
        when(notificationClient.createNotification(eq(notificationDTO))).thenReturn(ResponseEntity.ok(notificationDTO));
        ReactionDTO result = reactionService.updateReaction(reactionDTO);
        assertNotNull(result);
        verify(reactionMapper, times(1)).toEntity(eq(reactionDTO));
        verify(reactionRepository, times(1)).findReactionByUserIdAndPostId(eq(1L), eq(1L));
        verify(reactionRepository, times(1)).save(eq(existingReaction));
        verify(reactionMapper, times(1)).toDTO(eq(existingReaction));
    }



}
