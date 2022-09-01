package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseCommentContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.CommentModel;
import nz.ac.canterbury.seng302.portfolio.model.entity.CommentModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentModelRepository commentRepository;

    /**
     * This funciton will gather all the comments in the database and return it.
     * @return A List of Comment Models
     */
    public List<CommentModel> getAllComments () {
        try {
            return (ArrayList<CommentModel>) commentRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * This function will gather all the comments related to a specific post and return them in a list.
     * @param postId Integer Post ID
     * @return A List of Comment Models
     */
    public List<CommentModel> getCommentsForGivenPost (int postId) {
        try {
            ArrayList<CommentModel> result = commentRepository.findAllCommentByPostId(postId);
            if (!result.isEmpty()) {
                return result;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * This function will add a new comment made to a post into the database.
     * @param newComment BaseCommentContract containing postId, UserId and Comment content.
     * @return CommentModel
     */
    public CommentModel addNewCommentsToAPost(BaseCommentContract newComment) {
        try {
            CommentModel comment = new CommentModel(newComment.postId(), newComment.userId(), newComment.comment());
            commentRepository.save(comment);
            return comment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This function will update the edited comment and save it into the database.
     * @param commentId Integer Comment Id
     * @param updatedComment BaseCommentContract containing postId, UserId, and Comment content
     * @return updated CommentModel
     */
    public CommentModel updateAComment (int commentId, BaseCommentContract updatedComment) {
        try {
            Optional<CommentModel> getComment = commentRepository.findById(commentId);
            if (getComment.isPresent()) {
                CommentModel comment = getComment.get();
                comment.setCommentContent(updatedComment.comment());
                commentRepository.save(comment);
                return comment;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This funciton will delete the comment by the give comment ID.
     * @param commentId Integer comment ID
     * @return True if deletion is successful False otherwise
     */
    public boolean deleteCommentById (int commentId) {
        try {
            return commentRepository.deleteById(commentId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This function will delete all the comments for a given post.
     * @param postId Integer post ID
     * @return True if deletion is successful False otherwise
     */
    public boolean deleteAllCommentByPostId (int postId) {
        try {
            return commentRepository.deleteCommentsByPostId(postId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
