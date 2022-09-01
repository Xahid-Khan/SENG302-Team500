package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BasePostContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PostService {

    @Autowired
    private PostModelRepository postRepository;
    @Autowired
    private CommentService commentService;

    public List<PostModel> getAllPosts () {
        try {
            return (ArrayList<PostModel>) postRepository.findAll();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<PostModel> getAllPostsForAGroup(int groupId) {
        return postRepository.findPostModelByGroupId(groupId);
    }

    /**
     * This funciton will create new instance of the post and save it in the database.
     * @param newPost A Base post contract containing groupId and contents of the post.
     * @param userId Integer (Id of the user who made the post)
     * @return True if successful false otherwise.
     */
    public boolean createPost(BasePostContract newPost, int userId) {
        try {
            if (newPost.postContent().length() == 0) {
                return false;
            }
            PostModel postModel = new PostModel(newPost.groupId(), userId, newPost.postContent());
            postRepository.save(postModel);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This function will delete the post by using the postId.
     * @param postId Integer Post ID
     * @return True if deletion is successful False otherwise
     */
    public boolean deletePost(int postId) {
        try {
            var postFound = postRepository.findById(postId);
            if (postFound.isPresent()) {
                postRepository.deleteById(postId);
                commentService.deleteCommentById(postId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This function will update the changes made to the post.
     * @param updatedPost A BasePostContract
     * @param postId Integer ID of the Post
     * @return True if update is successful False otherwise.
     */
    public boolean updatePost(BasePostContract updatedPost, int postId) {
        try {
            var post = postRepository.findById(postId).orElseThrow();
            post.setPostContent(updatedPost.postContent());
            postRepository.save(post);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This function will get a specific post using Post ID and return it.
     * @param postId Integer Post ID
     * @return Returns PostModel if found, null otherwise.
     */
    public PostModel getPostById(int postId) {
        try {
            return postRepository.findById(postId).orElseThrow();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
