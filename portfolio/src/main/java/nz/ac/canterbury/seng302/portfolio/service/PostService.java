package nz.ac.canterbury.seng302.portfolio.service;

import java.util.List;
import nz.ac.canterbury.seng302.portfolio.model.contract.PostContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseNotificationContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostEntity;
import nz.ac.canterbury.seng302.portfolio.repository.PostRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PostService {

  @Autowired private PostRepository postRepository;

  @Autowired private GroupsClientService groupsClientService;

  @Autowired private SubscriptionService subscriptionService;

  @Autowired private CommentService commentService;

  @Autowired private NotificationService notificationService;

  @Autowired private UserAccountService userAccountService;

  /**
   * Handles pagination using PageRequest.of, taking into account a group ID.
   *
   * @param groupId The group id
   * @param page which page of the data to load (I.E., 0 will load 0 - limit)
   * @param limit limit of posts to grab. Must be greater than 0
   * @return the specified posts based on the parameters given
   */
  public Page<PostEntity> getPaginatedPostsForGroup(int groupId, int page, int limit) {
    Pageable request = PageRequest.of(page, limit, Sort.by("created").descending());
    return postRepository.getPaginatedPostsByGroupId(groupId, request);
  }

  /**
   * Handles pagination using PageRequest.of.
   *
   * @param page which page of the data to load (I.E., 0 will load 0 - limit)
   * @param limit limit of posts to grab. Must be greater than 0
   * @return the specified posts based on the parameters given
   */
  public Page<PostEntity> getPaginatedPosts(int page, int limit) {
    Pageable request = PageRequest.of(page, limit, Sort.by("created").descending());
    return postRepository.findAll(request);
  }

  /**
   * This function will create new instance of the post and save it in the database.
   *
   * @param newPost A post contract containing groupId and contents of the post.
   * @param userId Integer (The id of the user who made the post)
   * @return True if successful false otherwise.
   */
  public boolean createPost(PostContract newPost, int userId) {
    if (newPost.postContent().length() == 0) {
      return false;
    }
    try {
      PostEntity postEntity = new PostEntity(newPost.groupId(), userId, newPost.postContent());
      postRepository.save(postEntity);

      // Gets details for notification
      GroupDetailsResponse groupDetails = groupsClientService.getGroupById(newPost.groupId());

      List<Integer> userIds = subscriptionService.getAllByGroupId(newPost.groupId());
      String posterUsername = userAccountService.getUserById(userId).getUsername();
      String groupName = groupDetails.getShortName();

      // Send notification to all members of the group
      for (Integer otherUserId : userIds) {
        if (otherUserId != userId) {
          notificationService.create(
              new BaseNotificationContract(
                  otherUserId,
                  "Your Subscriptions",
                  posterUsername + " created a post in " + groupName + "!"));
        }
      }

      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * This function will delete the post by using the postId.
   *
   * @param postId Integer Post ID
   * @return True if deletion is successful False otherwise
   */
  public boolean deletePost(int postId) {
    try {
      var postFound = postRepository.findById(postId);
      if (postFound.isPresent()) {
        var comments = commentService.getCommentsForGivenPost(postId);
        postRepository.deleteById(postId);
        if (!comments.isEmpty()) {
          commentService.getCommentsForGivenPost(postId);
        }
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
   *
   * @param updatedPost A PostContract
   * @param postId Integer ID of the Post
   * @return True if update is successful False otherwise.
   */
  public boolean updatePost(PostContract updatedPost, int postId) {
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
   *
   * @param postId Integer Post ID
   * @return Returns PostEntity if found, null otherwise.
   */
  public PostEntity getPostById(int postId) {
    try {
      return postRepository.findById(postId).orElseThrow();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
