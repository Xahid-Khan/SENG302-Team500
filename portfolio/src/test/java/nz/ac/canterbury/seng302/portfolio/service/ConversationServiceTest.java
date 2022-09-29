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


}
