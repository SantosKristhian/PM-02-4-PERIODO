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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        produto = new ProdutoModel();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPreco(50.0);
        produto.setQuantidade(100);

        ItemVendaModel itemVenda = new ItemVendaModel();
        itemVenda.setProduto(produto);
        itemVenda.setQuantidadeVendida(2);
        itemVenda.setPrecoVendido(produto.getPreco() * 2);

        venda = new VendaModel();
        venda.setId(1L);
        venda.setData(LocalDateTime.now());
        venda.setAtivo(true);
        venda.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO); // Usando a opção válida do enum
        venda.setValortotal(100.0); // Substituído para usar "valortotal"
        venda.setItens(Arrays.asList(itemVenda)); // Substituído para usar "itens"
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
    void buscarVendaPorId_ExistingId_ReturnsVenda() {
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

        VendaModel result = vendaService.buscarVendaPorId(1L);

        assertNotNull(result);
        assertEquals(venda.getId(), result.getId());
        verify(vendaRepository, times(1)).findById(1L);
    }

    @Test
    void registrarVenda_ValidVenda_ReturnsVenda() {
        // Mock do Produto
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // Mock do Usuário
        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario)); // Adicionado mock do usuário

        // Mock do Salvar Venda
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(venda);

        // Definindo o usuário responsável pela venda
        venda.setUsuario(usuario);

        // Chamando o método de registro de venda
        VendaModel result = vendaService.registrarVenda(venda);

        // Assertivas
        assertNotNull(result);
        assertEquals(100.0, result.getValortotal()); // Substituído para "valortotal"
        assertEquals(usuario, venda.getUsuario()); // Verificando o usuário atribuído
        verify(vendaRepository, times(1)).save(venda); // Garantindo que o método save foi chamado
    }

    @Test
    void updateVenda_ExistingId_ReturnsUpdatedVenda() {
        // Cria um novo objeto da venda atualizada
        VendaModel vendaAtualizada = new VendaModel();
        vendaAtualizada.setItens(Arrays.asList(
                new ItemVendaModel(null, venda, produto, 5, produto.getPreco() * 5)
        ));
        vendaAtualizada.setValortotal(250.0);

        // Mock para o repositório simular o comportamento esperado
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(vendaRepository.save(any(VendaModel.class))).thenReturn(vendaAtualizada);

        // Executa o método de serviço
        VendaModel result = vendaService.updateVenda(1L, vendaAtualizada);

        // Verificações
        assertNotNull(result);
        assertEquals(250.0, result.getValortotal()); // Verifica o valor total
        verify(vendaRepository, times(1)).findById(1L);
        verify(vendaRepository, times(1)).save(any(VendaModel.class)); // Ignora a verificação exata do objeto
    }
}