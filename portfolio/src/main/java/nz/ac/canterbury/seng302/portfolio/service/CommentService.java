package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseCommentContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.CommentModel;
import nz.ac.canterbury.seng302.portfolio.model.entity.CommentModelRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private CommentModelRepository commentRepository;

    public List<CommentModel> getAllComments () {
        try {
            return (ArrayList<CommentModel>) commentRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

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

    public boolean deleteCommentById (int commentId) {
        try {
            return commentRepository.deleteById(commentId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAllCommentByPostId (int postId) {
        try {
            return commentRepository.deleteCommentsByPostId(postId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
