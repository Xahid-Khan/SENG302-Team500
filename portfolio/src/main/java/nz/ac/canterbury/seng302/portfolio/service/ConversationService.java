package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.ConversationContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseConversationContract;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {

    public boolean createConversation(
            BaseConversationContract baseConversationContract) {
        return false;
    }

    public void deleteConversation(String conversationId) {
    }


    public boolean updateConversation(
            BaseConversationContract baseConversationContract, String conversationId) {
      return false;
    }


}
