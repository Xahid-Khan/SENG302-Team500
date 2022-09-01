package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseCommentContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.CommentModel;
import nz.ac.canterbury.seng302.portfolio.model.entity.CommentModelRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentModelRepository mockCommentRepository;

    private BaseCommentContract comment1;
    private BaseCommentContract comment2;
    private BaseCommentContract comment3;
    private BaseCommentContract comment4;

    private List<CommentModel> commentList;

    @BeforeEach
    void setup () {
        mockCommentRepository.deleteAll();
        comment1 = new BaseCommentContract(1, 1, "This is a cool post");
        comment2 = new BaseCommentContract(4, 3, "This is a comment to a comment");
        comment3 = new BaseCommentContract(2, 2, "This is new Comment to a post");
        comment4 = new BaseCommentContract(100, 10000, "A Comment to 10000th post of the group");
        commentList = new ArrayList<>();
        commentList.add(new CommentModel(comment1.postId(), comment1.userId(), comment1.comment()));
        commentList.add(new CommentModel(comment2.postId(), comment1.userId(), comment1.comment()));
        commentList.add(new CommentModel(comment3.postId(), comment1.userId(), comment1.comment()));
        commentList.add(new CommentModel(comment4.postId(), comment1.userId(), comment1.comment()));

        commentList.forEach(comment -> {
            mockCommentRepository.save(comment);
        });
    }


    @Test
    void getAllCommentsExpectPass () throws Exception {
        Mockito.when(mockCommentRepository.findAll()).thenReturn(commentList);
        var result = commentService.getAllComments();
        Assertions.assertNotNull(result);
        for (int i=0; i<result.size(); i++) {
            Assertions.assertEquals(commentList.get(i).getPostId(), result.get(i).getPostId());
            Assertions.assertEquals(commentList.get(i).getUserId(), result.get(i).getUserId());
            Assertions.assertEquals(commentList.get(i).getCommentContent(), result.get(i).getCommentContent());
        }
    }

    @Test
    void getAllCommentsByPostIdExpectPass () throws Exception  {
        var postId = 1;
        ArrayList<CommentModel> filteredCommentsByPost = (ArrayList<CommentModel>) commentList.stream().filter(
                comment -> {
                   return comment.getPostId() == postId;}).collect(Collectors.toList());
        Mockito.when(mockCommentRepository.findAllCommentByPostId(postId)).thenReturn(filteredCommentsByPost);

        var result = commentService.getCommentsForGivenPost(postId);
        for (int i=0; i<filteredCommentsByPost.size(); i++) {
            Assertions.assertEquals(filteredCommentsByPost.get(i).getPostId(), result.get(i).getPostId());
            Assertions.assertEquals(filteredCommentsByPost.get(i).getUserId(), result.get(i).getUserId());
            Assertions.assertEquals(filteredCommentsByPost.get(i).getCommentContent(), result.get(i).getCommentContent());
        }
    }

    @Test
    void AddNewCommentToAPostExpectPass () throws Exception  {
        Mockito.when(mockCommentRepository.save(commentList.get(0))).thenReturn(null);
        var result = commentService.addNewCommentsToAPost(comment1);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(commentList.get(0).getPostId(), result.getPostId());
        Assertions.assertEquals(commentList.get(0).getUserId(), result.getUserId());
        Assertions.assertEquals(commentList.get(0).getCommentContent(), result.getCommentContent());
    }

    @Test
    void UpdateACommentExpectPass () throws Exception  {
        Mockito.when(mockCommentRepository.findById(commentList.get(0).getId())).thenReturn(Optional.ofNullable(commentList.get(0)));
        CommentModel updatedComment = commentList.get(0);
        updatedComment.setCommentContent("This comment has been edited");
        Mockito.when(mockCommentRepository.save(updatedComment)).thenReturn(updatedComment);

        BaseCommentContract newContract = new BaseCommentContract(comment1.userId(), comment1.postId(), "This comment has been edited");

        var result = commentService.updateAComment(commentList.get(0).getId(), newContract);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(updatedComment.getCommentContent(), result.getCommentContent());
    }

    @Test
    void deleteCommentsByCommentIdExpectPass () throws Exception {
        List<CommentModel> filteredList = commentList.stream().filter(comment -> {
            return comment.getId() != commentList.get(0).getId();
        }).collect(Collectors.toList());

        Mockito.when(mockCommentRepository.deleteById(commentList.get(0).getId())).thenReturn(true);
        Mockito.when(mockCommentRepository.findAll()).thenReturn(filteredList);

        var deletedComment = commentService.deleteCommentById(commentList.get(0).getId());
        Assertions.assertTrue(deletedComment);

        var result = commentService.getAllComments();
        for (int i=0; i < filteredList.size(); i++) {
            Assertions.assertEquals(filteredList.get(i).getUserId(), result.get(i).getUserId());
            Assertions.assertEquals(filteredList.get(i).getPostId(), result.get(i).getPostId());
            Assertions.assertEquals(filteredList.get(i).getCommentContent(), result.get(i).getCommentContent());
        }
    }

    @Test
    void deleteCommentsByInvalidCommentIdExpectFail () throws Exception {
        int commentId = 1000000;
        List<CommentModel> filteredList = commentList.stream().filter(comment -> {
            return comment.getId() != commentId;
        }).collect(Collectors.toList());

        Mockito.when(mockCommentRepository.deleteById(commentId)).thenReturn(false);
        Mockito.when(mockCommentRepository.findAll()).thenReturn(filteredList);

        var deletedComment = commentService.deleteCommentById(commentList.get(0).getId());
        Assertions.assertFalse(deletedComment);

        var result = commentService.getAllComments();
        for (int i=0; i < filteredList.size(); i++) {
            Assertions.assertEquals(filteredList.get(i).getUserId(), result.get(i).getUserId());
            Assertions.assertEquals(filteredList.get(i).getPostId(), result.get(i).getPostId());
            Assertions.assertEquals(filteredList.get(i).getCommentContent(), result.get(i).getCommentContent());
        }
    }

    @Test
    void deleteAllCommentsByPostIdExpectPass () throws Exception {
        Mockito.when(mockCommentRepository.deleteCommentsByPostId(commentList.get(0).getPostId())).thenReturn(true);
        Mockito.when(mockCommentRepository.findAllCommentByPostId(commentList.get(0).getPostId())).thenReturn(new ArrayList<>());

        var deleteComments = commentService.deleteAllCommentByPostId(commentList.get(0).getPostId());
        Assertions.assertTrue(deleteComments);

        var result = commentService.getCommentsForGivenPost(commentList.get(0).getPostId());
        Assertions.assertEquals(new ArrayList<>(), result);
    }

    @Test
    void deleteAllCommentsByInvalidPostIdExpectFail () throws Exception {
        int postId = 10000;
        Mockito.when(mockCommentRepository.deleteCommentsByPostId(postId)).thenReturn(false);
        Mockito.when(mockCommentRepository.findAllCommentByPostId(postId)).thenReturn(new ArrayList<>());

        var deleteComments = commentService.deleteAllCommentByPostId(postId);
        Assertions.assertFalse(deleteComments);

        var result = commentService.getCommentsForGivenPost(postId);
        Assertions.assertEquals(new ArrayList<>(), result);
    }
}
