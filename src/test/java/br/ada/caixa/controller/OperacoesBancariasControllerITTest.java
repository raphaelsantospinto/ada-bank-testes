package br.ada.caixa.controller;

import br.ada.caixa.dto.request.DepositoRequestDto;
import br.ada.caixa.dto.request.SaqueRequestDto;
import br.ada.caixa.dto.request.TransferenciaRequestDto;
import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoCliente;
import br.ada.caixa.entity.TipoConta;
import br.ada.caixa.enums.StatusCliente;
import br.ada.caixa.exceptions.ValidacaoException;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import br.ada.caixa.service.conta.ContaService;
import br.ada.caixa.service.operacoesbancarias.deposito.DepositoService;
import br.ada.caixa.service.operacoesbancarias.investimento.InvestimentoService;
import br.ada.caixa.service.operacoesbancarias.saldo.SaldoService;
import br.ada.caixa.service.operacoesbancarias.saque.SaqueService;
import br.ada.caixa.service.operacoesbancarias.transferencia.TransferenciaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class OperacoesBancariasControllerITTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @SpyBean
    private ContaRepository contaRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    DepositoService depositoService;
    @Autowired
    SaqueService saqueService;
    @Autowired
    TransferenciaService transferenciaService;
    @Autowired
    SaldoService saldoService;
    @Autowired
    InvestimentoService investimentoService;
    @Autowired
    ContaService contaService;

    private String url;

    @BeforeEach
    void setUp() {
        //SET URL
        url = "http://localhost:" + port + "/operacoes";

        //CRIAR CLIENTES
        var cliente1 = Cliente.builder()
                .documento("123456889")
                .nome("Teste 1")
                .dataNascimento(LocalDate.now())
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PF)
                .createdAt(LocalDate.now())
                .build();
        var cliente2 = Cliente.builder()
                .documento("1234567891")
                .nome("Teste 2")
                .dataNascimento(LocalDate.now())
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PF)
                .createdAt(LocalDate.now())
                .build();

        var cliente3 = Cliente.builder()
                .documento("561845984894")
                .nome("Cliente 3")
                .dataNascimento(LocalDate.now())
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PF)
                .createdAt(LocalDate.now())
                .build();

        clienteRepository.saveAllAndFlush(List.of(cliente1, cliente2, cliente3));

        //CRIAR CONTAS
        var contaCorrente1 = Conta.builder()
                .numero(1L)
                .saldo(BigDecimal.ZERO)
                .tipo(TipoConta.CONTA_CORRENTE)
                .cliente(cliente1)
                .createdAt(LocalDate.now())
                .build();

        var contaCorrente2 = Conta.builder()
                .numero(2L)
                .saldo(BigDecimal.ZERO)
                .tipo(TipoConta.CONTA_CORRENTE)
                .cliente(cliente2)
                .createdAt(LocalDate.now())
                .build();
        var contaCorrente3 = Conta.builder()
                .numero(3L)
                .saldo(BigDecimal.valueOf(2000))
                .tipo(TipoConta.CONTA_CORRENTE)
                .cliente(cliente3)
                .createdAt(LocalDate.now())
                .build();
        contaRepository.saveAllAndFlush(List.of(contaCorrente1, contaCorrente2, contaCorrente3));

    }

    @AfterEach
    void tearDown() {
        contaRepository.deleteAllInBatch();
        clienteRepository.deleteAllInBatch();
    }

    @Test
    void depositarTest() {
        //given
        final var valor = BigDecimal.valueOf(100.50);
        final var numeroConta = 1L;
        DepositoRequestDto depositoRequestDto =
                DepositoRequestDto.builder()
                        .numeroConta(numeroConta)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/depositar", depositoRequestDto, Void.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(valor.compareTo(contaRepository.findByNumero(numeroConta).get().getSaldo())).isZero();
        assertEquals(0, valor.compareTo(contaRepository.findByNumero(numeroConta).get().getSaldo()));

        verify(contaRepository).save(any(Conta.class));
    }

    @Test
    void sacar() {
        final var valor = BigDecimal.valueOf(1000);
        final var expected = BigDecimal.valueOf(1000);
        final var numeroConta = 3L;
        SaqueRequestDto saqueRequestDto =
                SaqueRequestDto.builder()
                        .numeroConta(numeroConta)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/sacar", saqueRequestDto, Void.class);

        //Then

        assertEquals(HttpStatus.OK, response.getStatusCode());
        contaRepository.
                findByNumero(3L)
                .map(Conta::getSaldo)
                .ifPresent(saldo->assertEquals(expected, saldo));


    }
    @Test
    void sacarValorAcimaDoSaldo() {
        final var valor = BigDecimal.valueOf(1000);
        final var numeroConta = 1L;
        SaqueRequestDto saqueRequestDto =
                SaqueRequestDto.builder()
                        .numeroConta(numeroConta)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/sacar", saqueRequestDto, Void.class);
        //then

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Mockito.verify(contaRepository,Mockito.atLeastOnce()).findByNumero(numeroConta);
        Mockito.verify(contaRepository, Mockito.never()).save(any(Conta.class));

    }

    @Test
    void transferencia() {

        final var valor = BigDecimal.valueOf(5.50);
        final var numeroContaOrigem = 2L;
        final var numeroContaDestino = 1L;

        TransferenciaRequestDto transferenciaRequestDto =
                TransferenciaRequestDto.builder()
                        .numeroContaOrigem(numeroContaOrigem)
                        .numeroContaDestino(numeroContaDestino)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/transferir", transferenciaRequestDto, Void.class);

        //then
        verify(transferenciaService).transferir(any(), any(), eq(valor));
        verify(saqueService).sacar(any(), eq(valor));
        verify(depositoService).depositar(any(), eq(valor));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);


    }

    @Test
    void consultarSaldo() {

        final var valor = BigDecimal.valueOf(2000.00);
        final var numeroConta = 3L;


        var response = restTemplate.getForEntity(url + "/saldo/" + numeroConta , Void.class);
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, valor.compareTo(contaRepository.findByNumero(numeroConta).get().getSaldo()));

    }

    @Test
    void investir() {

    }

    @Test
    void abrirContaPoupanca() {
    }
}