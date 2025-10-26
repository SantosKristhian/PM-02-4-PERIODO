
package com.example.EstoqueManager.service;

import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.*;
import com.example.EstoqueManager.repository.CompradorRepository;
import com.example.EstoqueManager.repository.ProdutoRepository;
import com.example.EstoqueManager.repository.UsuarioRepository;
import com.example.EstoqueManager.repository.VendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VendaServiceTest {

    @InjectMocks
    private VendaService vendaService;

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CompradorRepository compradorRepository;

    private VendaModel venda;
    private ProdutoModel produto;
    private UsuarioModel usuario;
    private ItemVendaModel itemVenda;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        produto = new ProdutoModel();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPreco(50.0);
        produto.setQuantidade(100);

        usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setCpf("12345678901");
        usuario.setIdade(30);
        usuario.setLogin("usuario1");
        usuario.setSenha("senha123");
        usuario.setCargo(Cargo.VENDEDOR);

        itemVenda = new ItemVendaModel();
        itemVenda.setProduto(produto);
        itemVenda.setQuantidadeVendida(2);
        itemVenda.setPrecoVendido(50.0);

        venda = new VendaModel();
        venda.setId(1L);
        venda.setData(LocalDateTime.now());
        venda.setAtivo(true);
        venda.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
        venda.setValortotal(100.0);
        venda.setUsuario(usuario);
        venda.setItens(new ArrayList<>(Arrays.asList(itemVenda)));
    }

    @Test
    void listarVendas_ReturnsVendaList() {
        when(vendaRepository.findAll()).thenReturn(Arrays.asList(venda));

        List<VendaModel> vendas = vendaService.listarVendas();

        assertNotNull(vendas);
        assertEquals(1, vendas.size());
        verify(vendaRepository, times(1)).findAll();
    }

    @Test
    void listarVendas_ReturnsEmptyList() {
        when(vendaRepository.findAll()).thenReturn(new ArrayList<>());

        List<VendaModel> vendas = vendaService.listarVendas();

        assertNotNull(vendas);
        assertTrue(vendas.isEmpty());
        verify(vendaRepository, times(1)).findAll();
    }



    @Test
    void buscarVendaPorId_ValidId_ReturnsVenda() {
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

        VendaModel result = vendaService.buscarVendaPorId(1L);

        assertNotNull(result);
        assertEquals(venda.getId(), result.getId());
        verify(vendaRepository, times(1)).findById(1L);
    }

    @Test
    void buscarVendaPorId_InvalidId_ThrowsBusinessException() {
        assertThrows(BusinessException.class, () -> vendaService.buscarVendaPorId(null));
        assertThrows(BusinessException.class, () -> vendaService.buscarVendaPorId(0L));
        assertThrows(BusinessException.class, () -> vendaService.buscarVendaPorId(-1L));
    }

    @Test
    void buscarVendaPorId_NotFound_ThrowsResourceNotFoundException() {
        when(vendaRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> vendaService.buscarVendaPorId(999L));

        assertEquals("Venda não encontrada com ID: 999", exception.getMessage());
    }



    @Test
    void registrarVenda_ValidVenda_ReturnsVenda() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        VendaModel result = vendaService.registrarVenda(venda);

        assertNotNull(result);
        assertEquals(100.0, result.getValortotal());
        assertTrue(result.isAtivo());
        verify(vendaRepository, times(1)).save(any(VendaModel.class));
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void registrarVenda_NullVenda_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.registrarVenda(null));

        assertEquals("Venda não pode ser nula.", exception.getMessage());
    }

    @Test
    void registrarVenda_SemItens_ThrowsBusinessException() {
        venda.setItens(new ArrayList<>());

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.registrarVenda(venda));

        assertEquals("A venda deve conter ao menos um item.", exception.getMessage());
    }

    @Test
    void registrarVenda_SemMetodoPagamento_ThrowsBusinessException() {
        venda.setMetodoPagamento(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.registrarVenda(venda));

        assertEquals("Método de pagamento é obrigatório.", exception.getMessage());
    }

    @Test
    void registrarVenda_SemUsuario_ThrowsBusinessException() {
        venda.setUsuario(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.registrarVenda(venda));

        assertEquals("Usuário responsável pela venda é obrigatório.", exception.getMessage());
    }

    @Test
    void registrarVenda_UsuarioNaoEncontrado_ThrowsResourceNotFoundException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> vendaService.registrarVenda(venda));

        assertEquals("Usuário não encontrado com ID: 1", exception.getMessage());
    }

    @Test
    void registrarVenda_ProdutoNaoEncontrado_ThrowsResourceNotFoundException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> vendaService.registrarVenda(venda));

        assertEquals("Produto não encontrado com ID: 1", exception.getMessage());
    }

    @Test
    void registrarVenda_EstoqueInsuficiente_ThrowsBusinessException() {
        produto.setQuantidade(1); // Menos que os 2 solicitados
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.registrarVenda(venda));

        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
    }

    @Test
    void registrarVenda_ItemSemProduto_ThrowsBusinessException() {
        itemVenda.setProduto(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.registrarVenda(venda));

        assertEquals("Produto inválido no item da venda.", exception.getMessage());
    }

    @Test
    void registrarVenda_QuantidadeInvalida_ThrowsBusinessException() {
        itemVenda.setQuantidadeVendida(0);

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.registrarVenda(venda));

        assertEquals("Quantidade vendida deve ser maior que zero.", exception.getMessage());
    }



    @Test
    void registrarVenda_PagamentoDinheiro_ComTroco_Success() {
        venda.setMetodoPagamento(MetodoPagamento.DINHEIRO);
        venda.setValorPago(150.0);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        VendaModel result = vendaService.registrarVenda(venda);

        assertNotNull(result);
        assertEquals(150.0, result.getValorPago());
        assertEquals(50.0, result.getTroco());
    }

    @Test
    void registrarVenda_PagamentoDinheiro_SemValorPago_ThrowsBusinessException() {
        venda.setMetodoPagamento(MetodoPagamento.DINHEIRO);
        venda.setValorPago(null);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.registrarVenda(venda));

        assertEquals("Valor pago é obrigatório para pagamento em dinheiro.", exception.getMessage());
    }

    @Test
    void registrarVenda_PagamentoDinheiro_ValorInsuficiente_ThrowsBusinessException() {
        venda.setMetodoPagamento(MetodoPagamento.DINHEIRO);
        venda.setValorPago(50.0); // Menos que os 100.0 do total

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.registrarVenda(venda));

        assertTrue(exception.getMessage().contains("insuficiente"));
    }

    @Test
    void registrarVenda_PagamentoCartao_SemTroco_Success() {
        venda.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        VendaModel result = vendaService.registrarVenda(venda);

        assertNotNull(result);
        assertEquals(100.0, result.getValorPago());
        assertEquals(0.0, result.getTroco());
    }



    @Test
    void registrarVenda_ComCompradorExistente_Success() {
        CompradorModel comprador = new CompradorModel();
        comprador.setId(1L);
        comprador.setNome("Comprador Teste");
        comprador.setCpf("98765432100");
        comprador.setEmail("comprador@mail.com");
        venda.setComprador(comprador);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(compradorRepository.findById(1L)).thenReturn(Optional.of(comprador));
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        VendaModel result = vendaService.registrarVenda(venda);

        assertNotNull(result);
        assertEquals(comprador, result.getComprador());
        verify(compradorRepository, times(1)).findById(1L);
    }

    @Test
    void registrarVenda_CompradorNaoEncontrado_ThrowsResourceNotFoundException() {
        CompradorModel comprador = new CompradorModel();
        comprador.setId(999L);
        venda.setComprador(comprador);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(compradorRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> vendaService.registrarVenda(venda));

        assertEquals("Comprador não encontrado com ID: 999", exception.getMessage());
    }

    @Test
    void registrarVenda_NovoComprador_Success() {
        CompradorModel novoComprador = new CompradorModel();
        novoComprador.setNome("Novo Comprador");
        novoComprador.setCpf("11111111111");
        novoComprador.setEmail("novo@mail.com");
        venda.setComprador(novoComprador);

        CompradorModel compradorSalvo = new CompradorModel();
        compradorSalvo.setId(2L);
        compradorSalvo.setNome("Novo Comprador");
        compradorSalvo.setCpf("11111111111");
        compradorSalvo.setEmail("novo@mail.com");

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(compradorRepository.save(any(CompradorModel.class))).thenReturn(compradorSalvo);
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        VendaModel result = vendaService.registrarVenda(venda);

        assertNotNull(result);
        verify(compradorRepository, times(1)).save(any(CompradorModel.class));
    }

    @Test
    void registrarVenda_NovoComprador_SemNome_ThrowsBusinessException() {
        CompradorModel novoComprador = new CompradorModel();
        novoComprador.setCpf("11111111111");
        novoComprador.setEmail("novo@mail.com");
        venda.setComprador(novoComprador);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.registrarVenda(venda));

        assertEquals("Nome do comprador é obrigatório para cadastro.", exception.getMessage());
    }

    @Test
    void registrarVenda_NovoComprador_SemCpf_ThrowsBusinessException() {
        CompradorModel novoComprador = new CompradorModel();
        novoComprador.setNome("Novo Comprador");
        novoComprador.setEmail("novo@mail.com");
        venda.setComprador(novoComprador);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.registrarVenda(venda));

        assertEquals("CPF do comprador é obrigatório para cadastro.", exception.getMessage());
    }

    @Test
    void registrarVenda_NovoComprador_SemEmail_ThrowsBusinessException() {
        CompradorModel novoComprador = new CompradorModel();
        novoComprador.setNome("Novo Comprador");
        novoComprador.setCpf("11111111111");
        venda.setComprador(novoComprador);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.registrarVenda(venda));

        assertEquals("Email do comprador é obrigatório para cadastro.", exception.getMessage());
    }



    @Test
    void updateVenda_ValidId_ReturnsUpdatedVenda() {
        ProdutoModel novoProduto = new ProdutoModel();
        novoProduto.setId(2L);
        novoProduto.setNome("Novo Produto");
        novoProduto.setPreco(75.0);
        novoProduto.setQuantidade(50);

        ItemVendaModel novoItem = new ItemVendaModel();
        novoItem.setProduto(novoProduto);
        novoItem.setQuantidadeVendida(3);

        VendaModel vendaAtualizada = new VendaModel();
        vendaAtualizada.setItens(new ArrayList<>(Arrays.asList(novoItem)));

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.findById(2L)).thenReturn(Optional.of(novoProduto));
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        VendaModel result = vendaService.updateVenda(1L, vendaAtualizada);

        assertNotNull(result);
        verify(vendaRepository, times(1)).save(any(VendaModel.class));
    }

    @Test
    void updateVenda_InvalidId_ThrowsBusinessException() {
        VendaModel vendaAtualizada = new VendaModel();

        assertThrows(BusinessException.class, () -> vendaService.updateVenda(null, vendaAtualizada));
        assertThrows(BusinessException.class, () -> vendaService.updateVenda(0L, vendaAtualizada));
    }

    @Test
    void updateVenda_NotFound_ThrowsResourceNotFoundException() {
        VendaModel vendaAtualizada = new VendaModel();
        when(vendaRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> vendaService.updateVenda(999L, vendaAtualizada));

        assertEquals("Venda não encontrada com ID: 999", exception.getMessage());
    }



    @Test
    void updateVenda_CancelarVenda_ComDevolucao_Success() {
        VendaModel vendaCancelamento = new VendaModel();
        vendaCancelamento.setAtivo(false);
        vendaCancelamento.setItensDevolvidos(true);

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        VendaModel result = vendaService.updateVenda(1L, vendaCancelamento);

        assertNotNull(result);
        assertFalse(result.isAtivo());
        assertTrue(result.getItensDevolvidos());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void updateVenda_CancelarVenda_SemDevolucao_Success() {
        VendaModel vendaCancelamento = new VendaModel();
        vendaCancelamento.setAtivo(false);
        vendaCancelamento.setItensDevolvidos(false);

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        VendaModel result = vendaService.updateVenda(1L, vendaCancelamento);

        assertNotNull(result);
        assertFalse(result.isAtivo());
        assertFalse(result.getItensDevolvidos());
        verify(produtoRepository, never()).save(any(ProdutoModel.class));
    }

    @Test
    void updateVenda_CancelarVenda_SemInformarDevolucao_ThrowsBusinessException() {
        VendaModel vendaCancelamento = new VendaModel();
        vendaCancelamento.setAtivo(false);
        vendaCancelamento.setItensDevolvidos(null);

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.updateVenda(1L, vendaCancelamento));

        assertTrue(exception.getMessage().contains("obrigatório informar se os itens foram devolvidos"));
    }

    @Test
    void updateVenda_CancelarVendaJaCancelada_ThrowsBusinessException() {
        venda.setAtivo(false);
        VendaModel vendaCancelamento = new VendaModel();
        vendaCancelamento.setAtivo(false);

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.updateVenda(1L, vendaCancelamento));

        assertEquals("Esta venda já está cancelada.", exception.getMessage());
    }
}