package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseMessageContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ConversationEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    public boolean isInConversation(Integer userId, Integer conversationId) {
        return false;
    }

    public Page<ConversationEntity> getPaginatedConversations(Integer userId, Integer offset, Integer limit) {
        return null;
    }

    public Page<MessageEntity> getPaginatedMessages(Integer userId, Integer offset, Integer limit) {
        return null;
    }

    public boolean sendMessage(Integer userId, Integer conversationId, BaseMessageContract message) {
        return false;
    }

    public boolean deleteMessage(Integer userId, Integer conversationId) {
        return false;
    }

}
