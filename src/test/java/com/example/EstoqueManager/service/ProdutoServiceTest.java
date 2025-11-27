
package com.example.EstoqueManager.service;

import com.example.EstoqueManager.dto.ProdutoCurvaABCDTO;
import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.*;
import com.example.EstoqueManager.repository.CategoriaRepository;
import com.example.EstoqueManager.repository.ItemVendaRepository;
import com.example.EstoqueManager.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

        categoria = new CategoriaModel();
        categoria.setId(1L);
        categoria.setNome("Categoria Teste");

        usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setCpf("12345678901");
        usuario.setIdade(30);
        usuario.setLogin("usuario1");
        usuario.setSenha("senha123");
        usuario.setCargo(Cargo.VENDEDOR);

        produto = new ProdutoModel();
        produto.setId(null);
        produto.setNome("Produto Teste");
        produto.setQuantidade(10);
        produto.setPreco(100.0);
        produto.setCategoria(categoria);
        produto.setAtivo(true);
    }

    // ==================== TESTES DE FINDALL ====================

    @Test
    void findAll_ReturnsListOfProdutos() {
        List<ProdutoModel> produtos = List.of(produto);
        when(produtoRepository.findAll()).thenReturn(produtos);

        List<ProdutoModel> result = produtoService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void findAll_ReturnsEmptyList() {
        when(produtoRepository.findAll()).thenReturn(new ArrayList<>());

        List<ProdutoModel> result = produtoService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(produtoRepository, times(1)).findAll();
    }

    // ==================== TESTES DE FINDBYID ====================

    /*@Test
    void findById_ValidId_ReturnsProduto() {
        produto.setId(1L);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoModel result = produtoService.findById(1L);

        assertEquals("Produto Teste", result.getNome());
        verify(produtoRepository, times(1)).findById(1L);
    }*/

    @Test
    void findById_NullId_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.findById(null));

        assertEquals("ID inválido. Deve ser um número positivo.", exception.getMessage());
        verify(produtoRepository, never()).findById(any());
    }

    @Test
    void findById_ZeroId_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.findById(0L));

        assertEquals("ID inválido. Deve ser um número positivo.", exception.getMessage());
        verify(produtoRepository, never()).findById(any());
    }

    @Test
    void findById_NegativeId_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.findById(-1L));

        assertEquals("ID inválido. Deve ser um número positivo.", exception.getMessage());
        verify(produtoRepository, never()).findById(any());
    }

    @Test
    void findById_NotFound_ThrowsResourceNotFoundException() {
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> produtoService.findById(999L));

        assertEquals("Produto não encontrado com ID: 999", exception.getMessage());
        verify(produtoRepository, times(1)).findById(999L);
    }

    // ==================== TESTES DE SAVE ====================

    @Test
    void save_ValidProduto_ReturnsProduto() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);

        ProdutoModel result = produtoService.save(produto, usuario);

        assertNotNull(result);
        assertEquals("Produto Teste", result.getNome());
        assertTrue(result.getAtivo());
        assertNotNull(result.getUsuarioUltimaAlteracao());
        assertNotNull(result.getDataUltimaAlteracao());
        verify(produtoRepository, times(1)).save(any(ProdutoModel.class));
    }

    @Test
    void save_ProdutoWithId_ThrowsBusinessException() {
        produto.setId(1L);

        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(produto, usuario));

        assertEquals("ID deve ser nulo ao criar um novo produto.", exception.getMessage());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void save_NullUsuario_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(produto, null));

        assertEquals("Usuário responsável é obrigatório.", exception.getMessage());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void save_UsuarioWithoutId_ThrowsBusinessException() {
        usuario.setId(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(produto, usuario));

        assertEquals("Usuário responsável é obrigatório.", exception.getMessage());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void save_CategoriaNotFound_ThrowsResourceNotFoundException() {
        when(categoriaRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> produtoService.save(produto, usuario));

        assertEquals("Categoria não encontrada com ID: 1", exception.getMessage());
        verify(produtoRepository, never()).save(any());
    }

    // ==================== TESTES DE VALIDAÇÃO ====================

    @Test
    void save_NullProduto_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(null, usuario));

        assertEquals("Produto não pode ser nulo.", exception.getMessage());
    }

    @Test
    void save_NullNome_ThrowsBusinessException() {
        produto.setNome(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(produto, usuario));

        assertEquals("Nome do produto é obrigatório.", exception.getMessage());
    }

    @Test
    void save_EmptyNome_ThrowsBusinessException() {
        produto.setNome("   ");

        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(produto, usuario));

        assertEquals("Nome do produto é obrigatório.", exception.getMessage());
    }

    @Test
    void save_NullQuantidade_ThrowsBusinessException() {
        produto.setQuantidade(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(produto, usuario));

        assertEquals("Quantidade deve ser maior ou igual a zero.", exception.getMessage());
    }

    @Test
    void save_NegativeQuantidade_ThrowsBusinessException() {
        produto.setQuantidade(-1);

        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(produto, usuario));

        assertEquals("Quantidade deve ser maior ou igual a zero.", exception.getMessage());
    }

    @Test
    void save_NullPreco_ThrowsBusinessException() {
        produto.setPreco(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(produto, usuario));

        assertEquals("Preço deve ser maior que zero.", exception.getMessage());
    }

    @Test
    void save_ZeroPreco_ThrowsBusinessException() {
        produto.setPreco(0.0);

        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(produto, usuario));

        assertEquals("Preço deve ser maior que zero.", exception.getMessage());
    }

    @Test
    void save_NegativePreco_ThrowsBusinessException() {
        produto.setPreco(-10.0);

        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(produto, usuario));

        assertEquals("Preço deve ser maior que zero.", exception.getMessage());
    }

    @Test
    void save_NullCategoria_ThrowsBusinessException() {
        produto.setCategoria(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(produto, usuario));

        assertEquals("Categoria é obrigatória.", exception.getMessage());
    }

    @Test
    void save_CategoriaWithoutId_ThrowsBusinessException() {
        categoria.setId(null);
        produto.setCategoria(categoria);

        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.save(produto, usuario));

        assertEquals("Categoria é obrigatória.", exception.getMessage());
    }

    // ==================== TESTES DE UPDATE ====================

    @Test
    void updateByID_ValidUpdate_ReturnsUpdatedProduto() {
        produto.setId(1L);
        ProdutoModel produtoAtualizado = new ProdutoModel();
        produtoAtualizado.setNome("Produto Atualizado");
        produtoAtualizado.setQuantidade(20);
        produtoAtualizado.setPreco(150.0);
        produtoAtualizado.setCategoria(categoria);
        produtoAtualizado.setAtivo(false);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);

        ProdutoModel result = produtoService.updateByID(1L, produtoAtualizado, usuario);

        assertNotNull(result);
        assertEquals("Produto Atualizado", result.getNome());
        assertEquals(20, result.getQuantidade());
        assertEquals(150.0, result.getPreco());
        assertFalse(result.getAtivo());
        verify(produtoRepository, times(1)).save(any(ProdutoModel.class));
    }

    @Test
    void updateByID_NullId_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.updateByID(null, produto, usuario));

        assertEquals("ID inválido. Deve ser um número positivo.", exception.getMessage());
    }

    @Test
    void updateByID_ZeroId_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.updateByID(0L, produto, usuario));

        assertEquals("ID inválido. Deve ser um número positivo.", exception.getMessage());
    }

    @Test
    void updateByID_NegativeId_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.updateByID(-1L, produto, usuario));

        assertEquals("ID inválido. Deve ser um número positivo.", exception.getMessage());
    }

    @Test
    void updateByID_NullUsuario_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.updateByID(1L, produto, null));

        assertEquals("Usuário responsável é obrigatório.", exception.getMessage());
    }

    @Test
    void updateByID_UsuarioWithoutId_ThrowsBusinessException() {
        usuario.setId(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.updateByID(1L, produto, usuario));

        assertEquals("Usuário responsável é obrigatório.", exception.getMessage());
    }

    @Test
    void updateByID_ProdutoNotFound_ThrowsResourceNotFoundException() {
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> produtoService.updateByID(999L, produto, usuario));

        assertEquals("Produto não encontrado com ID: 999", exception.getMessage());
    }

    @Test
    void updateByID_CategoriaNotFound_ThrowsResourceNotFoundException() {
        produto.setId(1L);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(categoriaRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> produtoService.updateByID(1L, produto, usuario));

        assertEquals("Categoria não encontrada com ID: 1", exception.getMessage());
    }

    // ==================== TESTES DE DELETE ====================

    @Test
    void deleteById_ProdutoNotInUse_HardDelete() {
        produto.setId(1L);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(itemVendaRepository.findAll()).thenReturn(new ArrayList<>());
        doNothing().when(produtoRepository).deleteById(1L);

        assertDoesNotThrow(() -> produtoService.deleteById(1L));

        verify(produtoRepository, times(1)).deleteById(1L);
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void deleteById_ProdutoInUse_SoftDelete() {
        produto.setId(1L);
        ItemVendaModel itemVenda = new ItemVendaModel();
        itemVenda.setProduto(produto);
        itemVenda.setQuantidadeVendida(2);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(itemVendaRepository.findAll()).thenReturn(List.of(itemVenda));
        when(produtoRepository.save(any(ProdutoModel.class))).thenReturn(produto);

        assertDoesNotThrow(() -> produtoService.deleteById(1L));

        assertFalse(produto.getAtivo());
        assertNotNull(produto.getDataUltimaAlteracao());
        verify(produtoRepository, times(1)).save(produto);
        verify(produtoRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_NullId_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.deleteById(null));

        assertEquals("ID inválido. Deve ser um número positivo.", exception.getMessage());
    }

    @Test
    void deleteById_ZeroId_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.deleteById(0L));

        assertEquals("ID inválido. Deve ser um número positivo.", exception.getMessage());
    }

    @Test
    void deleteById_NegativeId_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> produtoService.deleteById(-1L));

        assertEquals("ID inválido. Deve ser um número positivo.", exception.getMessage());
    }

    @Test
    void deleteById_NotFound_ThrowsResourceNotFoundException() {
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> produtoService.deleteById(999L));

        assertEquals("Produto não encontrado com ID: 999", exception.getMessage());
    }

    // ==================== TESTES DE CURVA ABC ====================

    @Test
    void getCurvaABC_EmptyList_ReturnsEmptyList() {
        when(itemVendaRepository.findAll()).thenReturn(new ArrayList<>());

        List<ProdutoCurvaABCDTO> result = produtoService.getCurvaABC();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getCurvaABC_WithPrecoVendidoNull_UsesPrecoFromProduto() {
        ProdutoModel produto1 = new ProdutoModel();
        produto1.setId(1L);
        produto1.setNome("Produto 1");
        produto1.setPreco(100.0);

        ItemVendaModel item = new ItemVendaModel();
        item.setProduto(produto1);
        item.setQuantidadeVendida(5);
        item.setPrecoVendido(null); // Preço vendido nulo

        when(itemVendaRepository.findAll()).thenReturn(List.of(item));

        List<ProdutoCurvaABCDTO> result = produtoService.getCurvaABC();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(500.0, result.get(0).getValorTotalVendido()); // 5 * 100.0
    }

    @Test
    void getCurvaABC_ClassificacaoA_ReturnsCorrectly() {
        ProdutoModel produto1 = new ProdutoModel();
        produto1.setId(1L);
        produto1.setNome("Produto A");
        produto1.setPreco(100.0);

        ItemVendaModel item = new ItemVendaModel();
        item.setProduto(produto1);
        item.setQuantidadeVendida(10);
        item.setPrecoVendido(100.0);

        when(itemVendaRepository.findAll()).thenReturn(List.of(item));

        List<ProdutoCurvaABCDTO> result = produtoService.getCurvaABC();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getClassificacao());
        assertEquals(100.0, result.get(0).getPercentualAcumulado());
    }
/*
    @Test
    void getCurvaABC_ClassificacaoB_ReturnsCorrectly() {
        // Produto A: 80% do faturamento
        ProdutoModel produtoA = new ProdutoModel();
        produtoA.setId(1L);
        produtoA.setNome("Produto A");
        produtoA.setPreco(80.0);

        ItemVendaModel itemA = new ItemVendaModel();
        itemA.setProduto(produtoA);
        itemA.setQuantidadeVendida(10);
        itemA.setPrecoVendido(80.0); // Total: 800

        // Produto B: 15% do faturamento
        ProdutoModel produtoB = new ProdutoModel();
        produtoB.setId(2L);
        produtoB.setNome("Produto B");
        produtoB.setPreco(15.0);

        ItemVendaModel itemB = new ItemVendaModel();
        itemB.setProduto(produtoB);
        itemB.setQuantidadeVendida(10);
        itemB.setPrecoVendido(15.0); // Total: 150

        when(itemVendaRepository.findAll()).thenReturn(List.of(itemA, itemB));

        List<ProdutoCurvaABCDTO> result = produtoService.getCurvaABC();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getClassificacao());
        assertEquals("B", result.get(1).getClassificacao());
    }

    @Test
    void getCurvaABC_ClassificacaoC_ReturnsCorrectly() {
        // Produto A: 80%
        ProdutoModel produtoA = new ProdutoModel();
        produtoA.setId(1L);
        produtoA.setNome("Produto A");

        ItemVendaModel itemA = new ItemVendaModel();
        itemA.setProduto(produtoA);
        itemA.setQuantidadeVendida(1);
        itemA.setPrecoVendido(800.0);

        // Produto B: 15%
        ProdutoModel produtoB = new ProdutoModel();
        produtoB.setId(2L);
        produtoB.setNome("Produto B");

        ItemVendaModel itemB = new ItemVendaModel();
        itemB.setProduto(produtoB);
        itemB.setQuantidadeVendida(1);
        itemB.setPrecoVendido(150.0);

        // Produto C: 5%
        ProdutoModel produtoC = new ProdutoModel();
        produtoC.setId(3L);
        produtoC.setNome("Produto C");

        ItemVendaModel itemC = new ItemVendaModel();
        itemC.setProduto(produtoC);
        itemC.setQuantidadeVendida(1);
        itemC.setPrecoVendido(50.0);

        when(itemVendaRepository.findAll()).thenReturn(List.of(itemA, itemB, itemC));

        List<ProdutoCurvaABCDTO> result = produtoService.getCurvaABC();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("A", result.get(0).getClassificacao());
        assertEquals("B", result.get(1).getClassificacao());
        assertEquals("C", result.get(2).getClassificacao());
    }*/
}