package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.ConversationContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.MessageContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseMessageContract;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * The controller for the message endpoints.
 */
@RestController
@RequestMapping("/api/v1/messages/")
public class MessageController extends AuthenticatedController{

    @Autowired private MessageService messageService;

    /**
     * This is similar to autowiring, but apparently recommended more than field injection.
     *
     * @param authStateService   an AuthStateService
     * @param userAccountService a UserAccountService
     */
    protected MessageController(AuthStateService authStateService, UserAccountService userAccountService) {
        super(authStateService, userAccountService);
    }

    /**
     * Getting All Previous Conversations:
     * Getting Paginated Conversations
     * - GET /api/v1/messages
     * - Status 200 OK
     * - Authentication: Student
     * - Content Type: application/json
     * - Body: {
     * 	offset: int
     * 	limit: int
     *  }
     *  - Response: List<ConversationContract>
     *
     * @param principal Authentication Principal
     * @return List of ConversationContracts
     */
    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity<List<ConversationContract>> getAllPaginatedConversations(@AuthenticationPrincipal PortfolioPrincipal principal,@RequestParam("offset") Optional<Integer> offset) {
        try {
            if (offset.isPresent() && offset.get().toString().equals("undefined")) {
                offset = Optional.empty();
            }

            int offsetValue = offset.orElse(0);

            if (offsetValue < 0) {
                offsetValue = 0;
            }

            Page<ConversationModel> conversationsPage = messageService.getPaginatedConversations(getUserId(principal), offsetValue,20);
            // Convert page to list
            List<ConversationModel> conversationModels = conversationsPage.getContent();
            // Convert list to list of contracts
            List<ConversationContract> contracts = conversationModels.stream().map(ConversationModel::toContract).toList();


            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Loading Paginated Messages
     * - GET /api/v1/messages/{conversationId}
     * - Status 200 OK
     * - Authentication: Conversation Member
     * - Content Type: application/json
     * - Body: {
     * 	offset: int
     * 	limit: int
     *  }
     * - Response: List<MessageContract>
     *
     * @param principal Authentication Principal
     * @param conversationId Conversation Id
     * @param offset The number of pages to skip
     * @return
     */
    @GetMapping(value = "/{conversationId}", produces = "application/json")
    public ResponseEntity<List<MessageContract>> getPaginatedMessages(@AuthenticationPrincipal PortfolioPrincipal principal, @PathVariable("conversationId") int conversationId, @RequestParam("offset") Optional<Integer> offset) {
        try {
            //Authenticates that the user is a member of the conversation
            if (!messageService.isInConversation(getUserId(principal), conversationId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (offset.isPresent() && offset.get().toString().equals("undefined")) {
                offset = Optional.empty();
            }

            int offsetValue = offset.orElse(0);

            if (offsetValue < 0) {
                offsetValue = 0;
            }

            Page<MessageModel> messagesPage = messageService.getPaginatedMessages(conversationId, offsetValue, 20);
            // Convert page to list
            List<MessageModel> messageModels = messagesPage.getContent();
            // Convert list to list of contracts
            List<MessageContract> contracts = messageModels.stream().map(MessageModel::toContract).toList();

            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


    /**
     * This is the endpoint for creating a new message.
     *
     * Send Message:
     * - POST /api/v1/messages/{userIds}
     * - Status 200 OK
     * - Authentication: Student
     * - Content Type: application/json
     * - Body: message content
     *
     * @param principal Authentication Principal
     * @param messageContract The contract for the message to be created
     * @param conversationId The id of the conversation of the message
     * @return The created message
     */
    @PostMapping(value = "/{conversationId}", produces = "application/json")
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal PortfolioPrincipal principal, @RequestBody BaseMessageContract messageContract, @PathVariable int conversationId) {
        try {
            int userId = getUserId(principal);

            //if the user is not in the conversation, they cannot send a message
            if (!messageService.isInConversation(userId, conversationId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            boolean response = messageService.sendMessage(userId, conversationId, messageContract);

            //if false, message was not sent
            if (!response) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }else {
                return ResponseEntity.ok().build();
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Delete Message:
     * - DELETE /api/v1/messages/{userIds}
     * - Status 200 OK
     * - Authentication: Student
     * - Content Type: application/json
     * - Body: message id
     *
     */
    @DeleteMapping(value = "/{conversationId}", produces = "application/json")
    public ResponseEntity<?> deleteMessage(@AuthenticationPrincipal PortfolioPrincipal principal, @RequestBody int conversationId, @PathVariable int recipientId) {
        try {
            int userId = getUserId(principal);
            boolean response = messageService.deleteMessage(userId, recipientId, conversationId);

            //if false, message was not sent
            if (!response) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }else {
                return ResponseEntity.ok().build();
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
