package com.simplon.servicepost;

import com.simplon.media.MediaServiceClient;
import com.simplon.notification.NotificationClient;
import com.simplon.notification.NotificationDTO;
import com.simplon.reaction.ReactionClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ServicePostTest {
    @Mock
    private Ipost postService;
    @Mock
    private  MediaServiceClient mediaServiceClient;
    @Mock
    private  ReactionClient reactionsClient;
    @Mock
    private  NotificationClient notificationClient;
    @Autowired
    ServicePostTest(Ipost postService1){
        this.postService = postService1;
    }
    PostDTO postDTO,post;
    NotificationDTO notification;
    @BeforeEach
    void setUp() {
        postDTO = new PostDTO();
        postDTO.setContent("Hello");
        postDTO.setUserId(1);
        postDTO.setDatePost("2021-07-01");
        postDTO.setDeleted(false);
        notification =new NotificationDTO();
        notification.setContentNotif("New post");
        notification.setTypeNotif("POST");
        notification.setSenderId(postDTO.getUserId());
        LocalDateTime nowNotif = LocalDateTime.now();
        notification.setDateNotif(nowNotif);
    }

    @AfterEach
    void tearDown() {
        /*if(this.postService.getPost(post.getPostId())!=null)
            this.postService.harddeletePost(post.getPostId());*/
    }

    @Test
    void addPost() {
        when(mediaServiceClient.addMedia(null,postDTO.getUserId())).thenReturn(ResponseEntity.ok(null));
        when(postService.addPost(postDTO,null)).thenReturn(postDTO);
        when(notificationClient.createNotification(notification)).thenReturn(ResponseEntity.ok(notification));
        post= postService.addPost(postDTO,null);
      assertEquals(post.getContent(),postDTO.getContent());
    }

    @Test
    void deletePost() {
        when(postService.deletePost(postDTO.getPostId())).thenReturn(true);
        boolean result = postService.deletePost(postDTO.getPostId());
        assertTrue(result);
    }

    @Test
    void updatePost() {
        when(postService.updatePost(postDTO,postDTO.getPostId())).thenReturn(postDTO);
        String content=postDTO.getContent();
        postDTO.setContent("helloworld");
        postDTO.setDatePost("2021-08-01");
        PostDTO postDTO1=postService.updatePost(postDTO,postDTO.getPostId());
        assertNotEquals(content,postDTO1.getContent());
    }

    @Test
    void getPost() {
         when(postService.getPost(postDTO.getPostId())).thenReturn(postDTO);
        assertEquals(postService.getPost(postDTO.getPostId()).getContent(),postDTO.getContent());
    }

    @Test
    void getAllPosts() {
        List<PostDTO> postDTOS1=new ArrayList<>();
        postDTOS1.add(postDTO);
        when(postService.getAllPosts()).thenReturn(postDTOS1);
        List<PostDTO> postDTOS=this.postService.getAllPosts();
        assertNotNull(postDTOS);
    }

    @Test
    void getPostsByUser() {
        when(postService.getPostsByUser(postDTO.getUserId())).thenReturn(List.of(postDTO));
        List<PostDTO> postDTOS=this.postService.getPostsByUser(postDTO.getUserId());
        assertNotNull(postDTOS);
    }
}