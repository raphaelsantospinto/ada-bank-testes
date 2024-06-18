package br.ada.caixa.service.cliente;

import br.ada.caixa.dto.request.RegistrarClientePFRequestDto;
import br.ada.caixa.dto.request.RegistrarClientePJRequestDto;
import br.ada.caixa.dto.response.ClienteResponseDto;
import br.ada.caixa.dto.response.RegistrarClienteResponseDto;
import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.TipoCliente;
import br.ada.caixa.enums.StatusCliente;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Random;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)

class ClienteServiceTest {

    @BeforeEach
    void setup(){


    }

    @AfterEach
    void tearDown(){

    }


    @Mock
    private ContaRepository contaRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    private ClienteService clienteService;

    @DisplayName("Dada requisicao de criacao de um cliente, o metodo deve" +
            "retornar um objeto do tipo Cliente")
    @Test
    void registrarPFTest() {
        Random rand = new Random();

        //var expected = mock(Cliente.class);

        // GIVEN
        //O retorno do metodo Registrar deve retornar um cliente;

        var cliente = mock(Cliente.class);

        RegistrarClientePFRequestDto clientePFRequestDto = mock(RegistrarClientePFRequestDto.class);
        var documentoCliente = String.valueOf(rand.nextLong());

        given(clienteRepository.save(cliente)).willReturn(cliente);
        given(cliente.getDocumento()).willReturn(documentoCliente);
        given(modelMapper.map(clientePFRequestDto, Cliente.class)).willReturn(cliente);

        //When
        RegistrarClienteResponseDto actual = clienteService.registrarPF(clientePFRequestDto);

        //Then
        assertEquals(documentoCliente, actual.getDocumento());
        verify(clienteRepository).save(cliente);
        verify(cliente).setTipo(TipoCliente.PF);
        verify(cliente).setStatus(StatusCliente.ATIVO);




    }

    @Test
    void registrarPJ() {
        Random rand = new Random();
        //var expected = mock(Cliente.class);

        // GIVEN
        //O retorno do metodo Registrar deve retornar um cliente;

        var cliente = mock(Cliente.class);

        RegistrarClientePJRequestDto clientePJRequestDto = mock(RegistrarClientePJRequestDto.class);
        var documentoCliente = String.valueOf(rand.nextLong());

        given(clienteRepository.save(cliente)).willReturn(cliente);
        given(cliente.getDocumento()).willReturn(documentoCliente);
        given(modelMapper.map(clientePJRequestDto, Cliente.class)).willReturn(cliente);

        //When
        RegistrarClienteResponseDto actual = clienteService.registrarPJ(clientePJRequestDto);

        //Then
        assertEquals(documentoCliente, actual.getDocumento());

        //queria testar se o tipo de usuario criado é do tipo PF.


        verify(clienteRepository).save(cliente);
        verify(cliente).setTipo(TipoCliente.PJ);
        verify(cliente).setStatus(StatusCliente.ATIVO);



    }

    @Test
    void listarTodos() {
        Cliente cliente = mock(Cliente.class);
        List<Cliente> clientes = List.of(cliente);
        ClienteResponseDto clienteResponseDto = new ClienteResponseDto();


        given(cliente.getTipo()).willReturn(TipoCliente.PF);
        given(clienteRepository.findAll()).willReturn(clientes);
        given(modelMapper.map(cliente, ClienteResponseDto.class)).willReturn(clienteResponseDto);


        //WHEN

        List<ClienteResponseDto> actual = clienteService.listarTodos();


        //THEN
        verify(clienteRepository).findAll();
        assertEquals(clientes.size(), actual.size());

    }

    @Test
    void listarTodosComParm(){
        Cliente clientePF = mock(Cliente.class);
        //clientePF.setTipo(TipoCliente.PF); -> NAO FUNCIONOU. DROGA :(
        Cliente clientePJ = mock(Cliente.class);
        //clientePF.setTipo(TipoCliente.PJ);


        given(clientePF.getTipo()).willReturn(TipoCliente.PF);
        given(clientePJ.getTipo()).willReturn(TipoCliente.PJ);

        given(modelMapper.map(clientePF, ClienteResponseDto.class)).willReturn(new ClienteResponseDto());
        given(modelMapper.map(clientePJ, ClienteResponseDto.class)).willReturn(new ClienteResponseDto());
        given(clienteRepository.findAllByTipo(TipoCliente.PF)).willReturn(List.of(clientePF));
        given(clienteRepository.findAllByTipo(TipoCliente.PJ)).willReturn(List.of(clientePJ));


        //when
        List<ClienteResponseDto> actualPF = clienteService.listarTodos(TipoCliente.PF);
        List<ClienteResponseDto> actualPJ = clienteService.listarTodos(TipoCliente.PJ);


        verify(clienteRepository).findAllByTipo(TipoCliente.PF);
        verify(clienteRepository).findAllByTipo(TipoCliente.PJ);
    }

}