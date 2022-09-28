package nz.ac.canterbury.seng302.portfolio.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;
import nz.ac.canterbury.seng302.portfolio.mapping.ConversationMapper;
import nz.ac.canterbury.seng302.portfolio.mapping.MessageMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseMessageContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ConversationEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.MessageEntity;
import nz.ac.canterbury.seng302.portfolio.repository.ConversationRepository;
import nz.ac.canterbury.seng302.portfolio.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
public class ConversationServiceTest {
  @InjectMocks private ConversationService conversationService;
  @InjectMocks private MessageService messageService;
  @Mock private ConversationRepository conversationRepository;
  @Mock private MessageRepository messageRepository;
  @Mock private ConversationMapper conversationMapper;
  @Mock private MessageMapper messageMapper;

  private ConversationEntity conversation1;
  private ConversationEntity conversation2;
  private ConversationEntity conversation3;

  @BeforeEach
  void beforeEach() {
    messageRepository.deleteAll();
    conversationRepository.deleteAll();

    conversation1 = new ConversationEntity(List.of(1, 2, 3));

    conversation2 = new ConversationEntity(List.of(1, 2));

    conversation3 = new ConversationEntity(List.of(2, 3));

    conversationRepository.saveAll(List.of(conversation1, conversation2, conversation3));
    Mockito.when(conversationRepository.save(any())).thenReturn(null);
    Mockito.when(messageRepository.save(any())).thenReturn(null);
  }

  @Test
  void testGetPaginatedConversationsAndExpectMostRecentMessageFirst() {
    // Get all 3 conversations (since user 2 is in all 3)
    var result = conversationService.getPaginatedConversations(2, 0, 3);
    Pageable request = PageRequest.of(0, 3);//, Sort.by("creationDate").descending());
    result = conversationRepository.getPaginatedPostsByUserIdsIn(List.of(2), request);
    System.err.println(result);
    System.err.println(conversationRepository.getPaginatedPostsByUserIdsIn(List.of(2, 3), request));
    System.err.println(conversationRepository.findAll());
    assertSame(result.getContent().get(0).getId(), conversation1.getId());

    // Add message to see if conversation 3 becomes the topmost one now
    MessageEntity message = new MessageEntity("Hello world!", 2);
    Mockito.when(messageMapper.toEntity(any())).thenReturn(message);
    Mockito.when(conversationRepository.findById(any())).thenReturn(
        Optional.ofNullable(conversation3));
    messageService.createMessage(conversation3.getId(), new BaseMessageContract("Hello World!", 2));

    //result = conversationService.getPaginatedConversations(2, 0, 3);

    result = conversationRepository.getPaginatedPostsByUserIdsIn(List.of(2), request);

    System.err.println(result);

    assertSame(result.getContent().get(0).getId(), conversation3.getId());
  }
}
