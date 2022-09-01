package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BasePostContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModelRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private PostModelRepository postRepository;

    public List<PostModel> getAllPosts () {
        return (ArrayList) postRepository.findAll();
    }
    public List<PostModel> getAllPostsForAGroup(int groupId) {
        return postRepository.findPostModelByGroupId(groupId);
    }

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

    public boolean deletePost(int postId) {

        try {
            var postFound = postRepository.findById(postId);
            if (!postFound.isEmpty()) {
                postRepository.deleteById(postId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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

    public PostModel getPostById(int postId) {
        try {
            return postRepository.findById(postId).orElseThrow();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
