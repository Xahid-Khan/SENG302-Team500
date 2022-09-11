package nz.ac.canterbury.seng302.portfolio.service;

import java.util.List;
import java.util.NoSuchElementException;
import nz.ac.canterbury.seng302.portfolio.mapping.PostMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.PostContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BasePostContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostEntity;
import nz.ac.canterbury.seng302.portfolio.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** This service handles posts. */
@Service
public class PostService {
  @Autowired private PostRepository postRepository;
  @Autowired private PostMapper postMapper;

  /**
   * Gets all posts as a list of contracts.
   *
   * @return all posts
   * @throws NoSuchElementException if no posts have been created
   */
  public List<PostContract> getAllPosts() {
    return ((List<PostEntity>) postRepository.findAll())
        .stream().map(postEntity -> postMapper.toContract(postEntity)).toList();
  }

  /**
   * Gets all the posts for a group.
   *
   * @param groupId the group to get all posts from
   * @return the posts if they are found.
   * @throws NoSuchElementException if group cannot be found, or group has no posts
   */
  public List<PostContract> getAllPostsForGroup(int groupId) {
    return postRepository.findPostByGroupId(groupId).stream()
        .map(postEntity -> postMapper.toContract(postEntity))
        .toList();
  }

  /**
   * This function will get a specific post using Post ID and return it.
   *
   * @param postId the post's ID. Must not be null
   * @return a PostContract
   * @throws NoSuchElementException if the ID is invalid
   */
  public PostContract getPostById(String postId) {
    return postMapper.toContract(postRepository.findById(postId).orElseThrow());
  }

  /**
   * This function will create new instance of the post and save it in the database.
   *
   * @param newPost A BasePostContract
   * @return a PostContract
   */
  public PostContract createPost(BasePostContract newPost) {
    PostEntity postEntity = postMapper.toEntity(newPost);

    postRepository.save(postEntity);
    return postMapper.toContract(postEntity);
  }

  /**
   * This function will delete the post by using the postId.
   *
   * @param postId the post's ID. Must not be null
   * @throws NoSuchElementException if the ID is invalid
   * @throws IllegalArgumentException if the post's ID is null
   */
  public void deletePost(String postId) {
    PostEntity post = postRepository.findById(postId).orElseThrow();
    postRepository.delete(post);
  }

  /**
   * This function will update the changes made to the post, and send back the new contract.
   *
   * @param updatedPost a BasePostContract to use for updating
   * @param postId the post's ID. Must not be null
   * @return a new updated contract
   * @throws NoSuchElementException if the ID is invalid
   */
  public PostContract updatePost(BasePostContract updatedPost, String postId) {
    PostEntity post = postRepository.findById(postId).orElseThrow();
    post.setPostContent(updatedPost.postContent());
    postRepository.save(post);
    return postMapper.toContract(post);
  }
}
