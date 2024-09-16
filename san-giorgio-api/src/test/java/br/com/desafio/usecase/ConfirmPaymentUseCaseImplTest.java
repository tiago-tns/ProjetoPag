package br.com.desafio.usecase;

import br.com.desafio.dto.PaymentDTO;
import br.com.desafio.dto.PaymentItemDTO;
import br.com.desafio.model.ChargerModel;
import br.com.desafio.model.MerchantModel;
import br.com.desafio.repository.ChargerRepository;
import br.com.desafio.repository.MerchantRepository;
import br.com.desafio.util.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
// Mockito para simular o ambiente para não depender de dados reais
class ConfirmPaymentUseCaseImplTest {

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private ChargerRepository chargerRepository;
    
	//mockar o Kafka uma vez que no meu projeto não existem ambientes
    @Mock
    private KafkaProducerService kafkaProducerService; 
    
    @Mock
    private PaymentKafkaService paymentKafkaService; //não esquecer do Service

    @InjectMocks
    private ConfirmPaymentUseCaseImpl confirmPaymentUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  //inicializar os mocks
    }

    @Test
    void shouldReturnNotFoundIfClientIdDoesNotExist() {

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setClientId(1); // ID inexistente
        when(merchantRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<String> response = confirmPaymentUseCase.confirm(paymentDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Cliente não encontrado", response.getBody());
    }

    @Test
    void shouldProcessPaymentIfClientIdExists() {

        // Configurar um MerchantModel com o ID correto
        MerchantModel merchantModel = new MerchantModel();
        merchantModel.setId(2);  // Configura o ID do cliente
        when(merchantRepository.findById(2)).thenReturn(Optional.of(merchantModel));

        // Configurar um ChargerModel com os valores esperados
        ChargerModel chargerModel = new ChargerModel();
        chargerModel.setIdcharger(1);
        chargerModel.setOriginalAmount(BigDecimal.valueOf(1000));
        chargerModel.setMerchantId(2);
        chargerModel.setOrderItemId("123"); 
        when(chargerRepository.findByOrderItemIdAndMerchantId("123", 2)).thenReturn(chargerModel);

    	
        PaymentDTO paymentDTO = new PaymentDTO();
        
        paymentDTO.setClientId(2); // ID existente

        PaymentItemDTO paymentItem = new PaymentItemDTO();
        paymentItem.setPaymentId("123");
        paymentItem.setPaymentValue(BigDecimal.valueOf(1000));
        paymentItem.setPaymentStatus("");
        paymentDTO.setPaymentItems(Arrays.asList(paymentItem));
        
        // Mockando o comportamento do KafkaProducerService
        doNothing().when(paymentKafkaService).sendFullPayment(any(PaymentItemDTO.class));


        ResponseEntity<String> response = confirmPaymentUseCase.confirm(paymentDTO);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), containsString("Pagamento processado com sucesso"));
        //não pode ser equal, por conter uma parte personalizada com o status do pagamento
        //assertEquals("Pagamento processado com sucesso", response.getBody()); 
        verify(paymentKafkaService, times(1)).sendFullPayment(any(PaymentItemDTO.class));
    }

    @Test
    void shouldReturnBadRequestIfChargeDataDoesNotMatch() {

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setClientId(1); // ID existente

        PaymentItemDTO paymentItem = new PaymentItemDTO();
        paymentItem.setPaymentId("123");
        paymentItem.setPaymentValue(BigDecimal.valueOf(1000));
        paymentDTO.setPaymentItems(Arrays.asList(paymentItem));

        MerchantModel merchantModel = new MerchantModel();
        when(merchantRepository.findById(1)).thenReturn(Optional.of(merchantModel));

        when(chargerRepository.findByOrderItemIdAndMerchantId("123", 1)).thenReturn(null); // Item não encontrado


        ResponseEntity<String> response = confirmPaymentUseCase.confirm(paymentDTO);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Dados de cobrança não conferem", response.getBody());
    }

    @Test
    void shouldProcessPaymentPartialPayments() {

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setClientId(1); // ID existente

        PaymentItemDTO paymentItem = new PaymentItemDTO();
        paymentItem.setPaymentId("123");
        paymentItem.setPaymentValue(BigDecimal.valueOf(500)); // Valor parcial
        paymentDTO.setPaymentItems(Arrays.asList(paymentItem));

        MerchantModel merchantModel = new MerchantModel();
        when(merchantRepository.findById(1)).thenReturn(Optional.of(merchantModel));

        ChargerModel chargerModel = new ChargerModel();
        chargerModel.setOriginalAmount(BigDecimal.valueOf(1000));
        when(chargerRepository.findByOrderItemIdAndMerchantId("123", 1)).thenReturn(chargerModel);

        // Mockando o comportamento do KafkaProducerService
        doNothing().when(paymentKafkaService).sendPartialPayment(any(PaymentItemDTO.class));
       

        ResponseEntity<String> response = confirmPaymentUseCase.confirm(paymentDTO);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), containsString("Pagamento processado com sucesso")); 
        verify(paymentKafkaService, times(1)).sendPartialPayment(any(PaymentItemDTO.class));
    }
    
    @Test
    void shouldProcessPaymentExcessPayments() {

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setClientId(1); // ID existente

        PaymentItemDTO paymentItem = new PaymentItemDTO();
        paymentItem.setPaymentId("123");
        paymentItem.setPaymentValue(BigDecimal.valueOf(1500)); // Valor parcial
        paymentDTO.setPaymentItems(Arrays.asList(paymentItem));

        MerchantModel merchantModel = new MerchantModel();
        when(merchantRepository.findById(1)).thenReturn(Optional.of(merchantModel));

        ChargerModel chargerModel = new ChargerModel();
        chargerModel.setOriginalAmount(BigDecimal.valueOf(1000));
        when(chargerRepository.findByOrderItemIdAndMerchantId("123", 1)).thenReturn(chargerModel);

        // Mockando o comportamento do KafkaProducerService
        doNothing().when(paymentKafkaService).sendExcessPayment(any(PaymentItemDTO.class));
        
        ResponseEntity<String> response = confirmPaymentUseCase.confirm(paymentDTO);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody(), containsString("Pagamento processado com sucesso")); 
        verify(paymentKafkaService, times(1)).sendExcessPayment(any(PaymentItemDTO.class));
    }
}


