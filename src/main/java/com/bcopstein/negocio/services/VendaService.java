package com.bcopstein.negocio.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import com.bcopstein.negocio.entities.*;
import com.bcopstein.negocio.repositories.IEstoqueRepository;
import com.bcopstein.negocio.repositories.IVendaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendaService {

    private IVendaRepository vendaRepository;
    private EstoqueService estoqueService;
    private ProdutoService produtoService;

    private static long IMPOSTO = 10L;
    
    @Autowired
    public VendaService(IVendaRepository vendaRepository, EstoqueService estoqueService, ProdutoService produtoService) {
        this.vendaRepository = vendaRepository;
        this.estoqueService = estoqueService;
        this.produtoService = produtoService;
    }

    public Integer[] calculaSubtotal(ItemEstoque[] itens) {
        Integer subtotal = 0;
        Integer imposto = 0;

        for (final ItemEstoque it : itens) {
            if (it != null) {
                Produto produto = it.getProduto();
                if(produto != null)
                    subtotal += (int) (produto.getPreco() * it.getQtdProduto());
            } else {
                throw new IllegalArgumentException("Codigo invalido");
            }
        }
        imposto = (int) (subtotal * 0.1);
        final Integer[] resp = new Integer[3];
        resp[0] = subtotal;
        resp[1] = imposto;
        resp[2] = subtotal + imposto;
        return resp;
    }
    
    public boolean confirmaVenda(ItemEstoque[] itens) {
        ArrayList<ItemVenda> listaItemVenda = new ArrayList<>();
        HashMap<Long, Integer> qtdadePorProduto = new HashMap<>();
        ItemEstoque itemEstoqueRep = null;
        for (ItemEstoque item : itens) {
            long codigoProduto = item.getProduto() != null ? item.getCodigo() : -1L;
            int quantidade = item.getQtdProduto();
            if(quantidade < 0)
                return false;
            itemEstoqueRep = estoqueService.getItemEstoque(codigoProduto);
            if(itemEstoqueRep == null)
                return false;
            if(!qtdadePorProduto.containsKey(codigoProduto)){
                if(itemEstoqueRep.getQtdProduto() - quantidade >= 0)
                    qtdadePorProduto.put(codigoProduto, itemEstoqueRep.getQtdProduto() - quantidade);
                else 
                    return false;
            }
            else {
                int nqtdade = qtdadePorProduto.get(codigoProduto) - quantidade; 
                if(nqtdade >= 0){
                    qtdadePorProduto.replace(codigoProduto, nqtdade);
                    quantidade += qtdadePorProduto.get(codigoProduto);
                }
                else 
                    return false;
            }
         if(listaItemVenda.stream().anyMatch(x -> x.getCodigo() == codigoProduto)){
            ItemVenda itemLista = listaItemVenda.stream().filter(x -> x.getCodigo() == codigoProduto).findFirst().get();
            listaItemVenda.remove(itemLista);
            itemLista.setQtdProduto(quantidade);
            listaItemVenda.add(itemLista);
         }
         else
            listaItemVenda.add(new ItemVenda(quantidade, itemEstoqueRep.getProduto().getPreco(), IMPOSTO, itemEstoqueRep.getProduto()));
        }
        Venda venda = new Venda(LocalDateTime.now(), listaItemVenda);
        vendaRepository.save(venda);
        return true;
    }

    public Iterable<Venda> vendasEfetuadas() {
        return vendaRepository.findAll();
    }
}
