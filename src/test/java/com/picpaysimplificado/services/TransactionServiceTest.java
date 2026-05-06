package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository repository;

    @Mock
    private AuthorizationService authService;

    @Mock
    private NotificationService notificationService;

    @Autowired
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Deve criar uma transação com sucesso")
    void createTransactionCase01() throws Exception {
        User sender = new User(1L, "João", "Santos", "99999999901", "joao@gmail.com", "1234", new BigDecimal(10),UserType.COMMON);
        User receiver = new User(2L, "Pedro", "Queiroz", "99999999902", "pedro@gmail.com", "1234", new BigDecimal(10),UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authService.authorizeTransaction(any(), any())).thenReturn(true);

        TransactionDTO transactionDTO = new TransactionDTO(new BigDecimal(10), 1L, 2L);
        transactionService.createTransaction(transactionDTO);

        verify(repository, times(1)).save(any());

        sender.setBalance(new BigDecimal(0));
        verify(userService, times(1)).saveUser(sender);

        receiver.setBalance(new BigDecimal(20));
        verify(userService, times(1)).saveUser(receiver);

        verify(notificationService, times(1)).sendNotification(sender, "Transação realizada com sucesso");
        verify(notificationService, times(1)).sendNotification(receiver, "Transação recebida com sucesso");


    }

    @Test
    @DisplayName("Deve lançar exceção quando a transação não for autorizada")
    void createTransactionCase02() throws Exception {
        User sender = new User(1L, "João", "Santos", "99999999901", "joao@gmail.com", "1234", new BigDecimal(10),UserType.COMMON);
        User receiver = new User(2L, "Pedro", "Queiroz", "99999999902", "pedro@gmail.com", "1234", new BigDecimal(10),UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authService.authorizeTransaction(any(), any())).thenReturn(false);

        Exception thrown = assertThrows(Exception.class, () -> {
            TransactionDTO transactionDTO = new TransactionDTO(new BigDecimal(10), 1L, 2L);
            transactionService.createTransaction(transactionDTO);
        });

        Assertions.assertEquals("Transação não autorizada", thrown.getMessage());
    }
}