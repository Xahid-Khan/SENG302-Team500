package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BasePostContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostModelRepository postRepository;


    public PostModel createPost(BasePostContract newPost, int userId) {
        return postRepository.save(new PostModel(newPost.groupId(), userId, newPost.postContent()));
    }

    public void deletePost(long postId) {
        postRepository.deleteById(postId);
    }

    public void updatePost(BasePostContract updatedPost, long postId) {
        var post = postRepository.findById(postId).orElseThrow();
        post.setPostContent(updatedPost.postContent());
        postRepository.save(post);
    }

}
