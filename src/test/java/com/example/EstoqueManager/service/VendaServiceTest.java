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

    // ==================== TESTES DE LISTAGEM E BUSCA ====================

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

    // ==================== TESTES DE REGISTRO DE VENDA ====================

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
        assertEquals(98, produto.getQuantidade()); // Verifica se o estoque foi decrementado (100 - 2)
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

    // ==================== TESTES DE PAGAMENTO ====================

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

    // ==================== TESTES DE COMPRADOR ====================

    @Test
    void registrarVenda_ComCompradorExistente_Success() {
        CompradorModel comprador = new CompradorModel();
        comprador.setId(1L);
        comprador.setNome("Comprador Teste");
        comprador.setCpf("98765432100");
        venda.setComprador(comprador);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(compradorRepository.findById(1L)).thenReturn(Optional.of(comprador)); // Comprador encontrado
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        VendaModel result = vendaService.registrarVenda(venda);

        assertNotNull(result);
        assertEquals(comprador, result.getComprador());
        verify(compradorRepository, times(1)).findById(1L);
        verify(compradorRepository, never()).save(any()); // Não deve salvar se já existe
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
        compradorSalvo.setId(2L); // Simula o ID gerado

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(compradorRepository.save(any(CompradorModel.class))).thenReturn(compradorSalvo); // Comprador salvo
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

    // Incluir todos os testes de validação de Novo Comprador (CPF, Email)
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


    // ==================== TESTES DE UPDATE (COBRINDO CANCELAMENTO) ====================

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
        // Setup de uma venda ativa
        venda.setAtivo(true);
        venda.setItensDevolvidos(false);

        VendaModel vendaCancelamento = new VendaModel();
        vendaCancelamento.setAtivo(false);
        vendaCancelamento.setItensDevolvidos(true); // Indica a devolução de estoque

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto)); // Garante que o produto é encontrado na reversão
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        VendaModel result = vendaService.updateVenda(1L, vendaCancelamento);

        assertNotNull(result);
        assertFalse(result.isAtivo());
        assertTrue(result.getItensDevolvidos());
        verify(produtoRepository, times(1)).save(produto);
        // Verificação de estoque (se o produto original tinha 100, e vendeu 2, deve voltar para 100)
        assertEquals(102, produto.getQuantidade(), "O estoque deve ser revertido (100 + 2).");
    }

    @Test
    void updateVenda_ReverteEstoqueCorretamente_OnCancelamentoComDevolucao() {
        // Cenário 1: Setup da venda original
        ProdutoModel produtoAntes = new ProdutoModel();
        produtoAntes.setId(1L);
        produtoAntes.setQuantidade(10); // Estoque original: 10

        ItemVendaModel item = new ItemVendaModel();
        item.setProduto(produtoAntes);
        item.setQuantidadeVendida(5); // Venda: 5 unidades (Estoque fictício após venda: 5)

        VendaModel vendaExistente = new VendaModel();
        vendaExistente.setId(1L);
        vendaExistente.setAtivo(true);
        vendaExistente.setItensDevolvidos(false);
        vendaExistente.setItens(List.of(item));

        // Cenário 2: Atualização para cancelamento e devolução
        VendaModel vendaAtualizacao = new VendaModel();
        vendaAtualizacao.setAtivo(false);
        vendaAtualizacao.setItensDevolvidos(true); // Indica devolução de estoque

        // Mocks
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(vendaExistente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoAntes));
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produtoAntes);
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(vendaExistente);


        // Ação
        vendaService.updateVenda(1L, vendaAtualizacao);

        // Verificação: O estoque DEVE ter sido revertido (10 + 5 = 15, ou se a regra subtraiu 5, deve voltar 5)
        // Como a regra de negócio deve reverter a quantidade vendida,
        // o estoque original (10) deve ser restaurado.
        // Se a quantidade é 10, e ele vendeu 5, o save da venda diminuiu.
        // Aqui, simulamos que a venda está sendo cancelada e os itens voltando.

        // Para que este teste funcione, a lógica do Service deve:
        // 1. Pegar a quantidade vendida (5).
        // 2. Somar ao estoque atual do produto.

        verify(produtoRepository, times(1)).save(produtoAntes);

        // Verificação: Assumindo que o produto Antes tinha estoque atual 10 no momento do cancelamento
        // e ele vendeu 5. O estoque final após reversão deve ser 15 (10 + 5).
        // Se o produto.setQuantidade(100) no BeforeEach, ele fica com 98 após a venda, então reverte para 100.
        // Para este teste de reversão isolado: vamos assumir que o produtoAntes.getQuantidade() é o estoque ATUAL no momento do cancelamento.
        // O teste é para garantir que a quantidade vendida (5) seja somada ao estoque atual (10) => 15.
        assertEquals(15, produtoAntes.getQuantidade(), "O estoque deve ser revertido somando a quantidade vendida de volta.");
    }

    @Test
    void updateVenda_CancelarVenda_SemDevolucao_Success() {
        venda.setAtivo(true);
        venda.setItensDevolvidos(false);

        VendaModel vendaCancelamento = new VendaModel();
        vendaCancelamento.setAtivo(false);
        vendaCancelamento.setItensDevolvidos(false); // Não há devolução de estoque

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        VendaModel result = vendaService.updateVenda(1L, vendaCancelamento);

        assertNotNull(result);
        assertFalse(result.isAtivo());
        assertFalse(result.getItensDevolvidos());
        verify(produtoRepository, never()).save(any(ProdutoModel.class)); // O estoque não deve ser revertido
    }

    @Test
    void updateVenda_CancelarVenda_SemInformarDevolucao_ThrowsBusinessException() {
        venda.setAtivo(true);

        VendaModel vendaCancelamento = new VendaModel();
        vendaCancelamento.setAtivo(false);
        vendaCancelamento.setItensDevolvidos(null); // Campo obrigatório não preenchido

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.updateVenda(1L, vendaCancelamento));

        assertTrue(exception.getMessage().contains("obrigatório informar se os itens foram devolvidos"));
    }

    @Test
    void updateVenda_CancelarVendaJaCancelada_ThrowsBusinessException() {
        venda.setAtivo(false); // Venda já inativa

        VendaModel vendaCancelamento = new VendaModel();
        vendaCancelamento.setAtivo(false);

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

        Exception exception = assertThrows(BusinessException.class,
                () -> vendaService.updateVenda(1L, vendaCancelamento));

        assertEquals("Esta venda já está cancelada.", exception.getMessage());
    }

    @Test
    void updateVenda_NaoMudarStatusAtivo_NaoRequerDevolucao() {
        // Testa se uma atualização que não seja cancelamento não exige o campo ItensDevolvidos
        VendaModel vendaUpdate = new VendaModel();
        vendaUpdate.setMetodoPagamento(MetodoPagamento.PIX); // Apenas muda o método de pagamento
        vendaUpdate.setItensDevolvidos(null); // Não se aplica e deve ser ignorado

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        // Deve rodar sem lançar BusinessException
        assertDoesNotThrow(() -> vendaService.updateVenda(1L, vendaUpdate));

        // Verifica se o valor foi aplicado
        assertEquals(MetodoPagamento.PIX, venda.getMetodoPagamento());
        verify(vendaRepository, times(1)).save(venda);
    }
}
