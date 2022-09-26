package nz.ac.canterbury.seng302.portfolio.service;

import java.util.NoSuchElementException;
import nz.ac.canterbury.seng302.portfolio.mapping.ConversationMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.ConversationContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseConversationContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ConversationEntity;
import nz.ac.canterbury.seng302.portfolio.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** This service handles interacting with the repository for conversations. */
@Service
public class ConversationService {
  @Autowired private ConversationRepository conversationRepository;

  @Autowired private ConversationMapper conversationMapper;

  /**
   * Creates a new conversation based on a base contract.
   *
   * @param baseConversationContract the base contract to create the conversation from
   * @return a full ConversationContract
   */
  public ConversationContract createConversation(
      BaseConversationContract baseConversationContract) {
    ConversationEntity conversation = conversationMapper.toEntity(baseConversationContract);
    conversationRepository.save(conversation);
    return conversationMapper.toContract(conversation);
  }

  /**
   * Deletes a conversation based on its ID.
   *
   * @param conversationId the conversation's ID
   */
  public void deleteConversation(String conversationId) {
    conversationRepository.deleteById(conversationId);
  }

  /**
   * Updates a conversation, which only updates its members.
   *
   * @param baseConversationContract the base contract for a conversation
   * @param conversationId the conversation's ID
   * @return the updated ConversationContract
   * @throws NoSuchElementException if the id is invalid
   */
  public ConversationContract updateConversation(
      BaseConversationContract baseConversationContract, String conversationId) {
    ConversationEntity conversation = conversationRepository.findById(conversationId).orElseThrow();
    conversation.setUserIds(baseConversationContract.userIds());
    conversationRepository.save(conversation);
    return conversationMapper.toContract(conversation);
  }
}
