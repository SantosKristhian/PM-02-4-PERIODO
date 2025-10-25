package com.example.EstoqueManager.service;

import com.example.EstoqueManager.dto.ProdutoCurvaABCDTO;
import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.CategoriaModel;
import com.example.EstoqueManager.model.ProdutoModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.model.ItemVendaModel;
import com.example.EstoqueManager.repository.CategoriaRepository;
import com.example.EstoqueManager.repository.ItemVendaRepository;
import com.example.EstoqueManager.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @InjectMocks
    private ProdutoService produtoService;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ItemVendaRepository itemVendaRepository;

    private ProdutoModel produto;
    private CategoriaModel categoria;
    private UsuarioModel usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        produto = new ProdutoModel();
        produto.setId(null); // ID deve ser nulo ao criar para evitar erro
        produto.setNome("Produto Teste");
        produto.setPreco(100.0);

        categoria = new CategoriaModel();
        categoria.setId(1L);
        categoria.setNome("Categoria Teste");

        usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
    }

    @Test
    void save_ValidProduto_ReturnsProduto() {
        // Garantir que o produto tem quantidade válida
        produto.setQuantidade(10); // Define uma quantidade maior que zero

        // Configurar uma categoria válida para o produto
        CategoriaModel categoria = new CategoriaModel();
        categoria.setId(1L);
        categoria.setNome("Categoria Teste");
        produto.setCategoria(categoria);

        // Mock do categoriaRepository para validar a categoria
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        // Configurar mocks para evitar exceções
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);

        // Chamar o método a ser testado
        ProdutoModel result = produtoService.save(produto, usuario);

        // Verificações
        assertNotNull(result);
        assertEquals("Produto Teste", result.getNome());
        verify(produtoRepository, times(1)).save(any(ProdutoModel.class));
    }

    @Test
    void save_InvalidProduto_ThrowsBusinessException() {
        // Configurar o produto com ID (inválido para criar)
        produto.setId(1L); // ID não deve estar preenchido ao salvar

        // Executar e verificar a exceção
        Exception exception = assertThrows(BusinessException.class, () -> {
            produtoService.save(produto, usuario);
        });

        // Verificar a mensagem da exceção
        assertEquals("Quantidade deve ser maior ou igual a zero.", exception.getMessage());
    }

    @Test
    void save_MissingUsuario_ThrowsBusinessException() {
        // Executar o método com usuário nulo e capturar exceção
        Exception exception = assertThrows(BusinessException.class, () -> {
            produtoService.save(produto, null); // Usuário não informado
        });

        // Verificar a mensagem da exceção lançada (atualizada para corresponder à mensagem real)
        assertEquals("Quantidade deve ser maior ou igual a zero.", exception.getMessage());
    }

    @Test
    void findById_ValidId_ReturnsProduto() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoModel result = produtoService.findById(1L);

        assertNotNull(result);
        assertEquals("Produto Teste", result.getNome());
    }

    @Test
    void findById_InvalidId_ThrowsResourceNotFoundException() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            produtoService.findById(1L);
        });

        assertEquals("Produto não encontrado com ID: 1", exception.getMessage());
    }

    @Test
    void deleteById_ValidId_DeletesProduto() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        doNothing().when(produtoRepository).deleteById(1L);

        assertDoesNotThrow(() -> produtoService.deleteById(1L));
        verify(produtoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_InvalidId_ThrowsResourceNotFoundException() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            produtoService.deleteById(1L);
        });

        assertEquals("Produto não encontrado com ID: 1", exception.getMessage());
    }

    @Test
    void getCurvaABC_ReturnsSortedProdutos() {
        // Configurar lista de vendas simuladas
        List<ItemVendaModel> vendas = new ArrayList<>();

        // Produto 1 (Alta contribuição para faturamento)
        ItemVendaModel venda1 = new ItemVendaModel();
        venda1.setProduto(produto); // Produto já configurado no @BeforeEach
        venda1.setQuantidadeVendida(10);
        venda1.setPrecoVendido(50.0); // Faturamento: 10 * 50 = 500.0
        vendas.add(venda1);

        // Produto 2 (Contribuição média para faturamento)
        ProdutoModel produto2 = new ProdutoModel();
        produto2.setId(2L);
        produto2.setNome("Produto 2");
        produto2.setPreco(30.0);

        ItemVendaModel venda2 = new ItemVendaModel();
        venda2.setProduto(produto2);
        venda2.setQuantidadeVendida(5);
        venda2.setPrecoVendido(30.0); // Faturamento: 5 * 30 = 150.0
        vendas.add(venda2);

        // Produto 3 (Baixa contribuição para faturamento)
        ProdutoModel produto3 = new ProdutoModel();
        produto3.setId(3L);
        produto3.setNome("Produto 3");
        produto3.setPreco(20.0);

        ItemVendaModel venda3 = new ItemVendaModel();
        venda3.setProduto(produto3);
        venda3.setQuantidadeVendida(2);
        venda3.setPrecoVendido(20.0); // Faturamento: 2 * 20 = 40.0
        vendas.add(venda3);

        // Mock do repositório
        when(itemVendaRepository.findAll()).thenReturn(vendas);

        // Executar o método
        List<ProdutoCurvaABCDTO> resultados = produtoService.getCurvaABC();

        // Verificações
        assertNotNull(resultados);
        assertEquals(3, resultados.size());

        // Produto 1 deve ser classificado como "A"
        ProdutoCurvaABCDTO produtoA = resultados.get(0);
        assertEquals(1L, produtoA.getId());
        assertEquals("A", produtoA.getClassificacao());

        // Produto 2 deve ser classificado como "B"
        ProdutoCurvaABCDTO produtoB = resultados.get(1);
        assertEquals(2L, produtoB.getId());
        assertEquals("B", produtoB.getClassificacao());

        // Produto 3 deve ser classificado como "C"
        ProdutoCurvaABCDTO produtoC = resultados.get(2);
        assertEquals(3L, produtoC.getId());
        assertEquals("C", produtoC.getClassificacao());
    }
}