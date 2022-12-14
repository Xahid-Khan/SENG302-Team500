package nz.ac.canterbury.seng302.portfolio.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nz.ac.canterbury.seng302.portfolio.model.contract.PostContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseNotificationContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import nz.ac.canterbury.seng302.portfolio.repository.PostModelRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

  @Autowired private PostModelRepository postRepository;

  @Autowired private GroupsClientService groupsClientService;

  @Autowired private SubscriptionService subscriptionService;

  @Autowired private CommentService commentService;
  @Autowired private NotificationService notificationService;

  @Autowired private UserAccountService userAccountService;

  public List<PostModel> getAllPosts() {
    try {
      return (List<PostModel>) postRepository.findAll();
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
   *
   * @param newPost A post contract containing groupId and contents of the post.
   * @param userId Integer (Id of the user who made the post)
   * @return True if successful false otherwise.
   */
  public boolean createPost(PostContract newPost, int userId) {
    if (newPost.postContent().length() == 0) {
      return false;
    }
    try {
      PostModel postModel = new PostModel(newPost.groupId(), userId, newPost.postContent());
      postRepository.save(postModel);

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

  public List<PostModel> getAllPostsForAUser(int userId) {
    return postRepository.findPostModelByUserId(userId);
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
