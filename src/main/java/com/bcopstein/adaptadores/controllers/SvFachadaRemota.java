package com.bcopstein.adaptadores.controllers;

import java.util.ArrayList;
import java.util.List;

import com.bcopstein.negocio.entities.ItemCarrinho;
import com.bcopstein.negocio.entities.Produto;
import com.bcopstein.negocio.services.ProdutoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vendas")
public class SvFachadaRemota {

  private ProdutoService service;

  public SvFachadaRemota(ProdutoService service) {
    this.service = service;
  }

  @GetMapping("/produtos")
  @CrossOrigin(origins = "*")
  public List<Produto> listaProdutos() {
    return service.findAll();
  }

  @GetMapping("/autorizacao")
  @CrossOrigin(origins = "*")
  public boolean podeVender(@RequestParam final Long codProd,
                            @RequestParam final Integer qtdade) {
    return service.podeVender(codProd, qtdade);
  }

  @PostMapping("/confirmacao")
  @CrossOrigin(origins = "*")
  public boolean confirmaVenda(@RequestBody final ItemCarrinho[] itens) {
    return service.confirmaVenda(itens);
  }

  @GetMapping("/historico")
  @CrossOrigin(origins = "*")
  public List<String> vendasEfetuadas() {
    return service.vendasEfetuadas();
  }

  @PostMapping("/subtotal")
  @CrossOrigin(origins = "*")
  public Integer[] calculaSubtotal(@RequestBody final ItemCarrinho[] itens) {
    return service.calculaSubtotal(itens);
  }
}
