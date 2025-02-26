package com.bcopstein.negocio.entities;

import javax.persistence.*;

@Entity(name = "produtos")
public class Produto {

  @Id
  @Column(name="codigo")
  private Long codigo;
  
  @OneToOne(mappedBy = "produto")
  private ItemEstoque itemEstoque;
  private String descricao;
  private int preco;

  public Produto(Long codigo, String descricao, int preco) {
    this.codigo = codigo;
    this.descricao = descricao;
    this.preco = preco;
  }

  // public Produto() {

  // }

  public Long getCodigo() {
    return codigo;
  }

  public String getDescricao() {
    return descricao;
  }

  public int getPreco() {
    return preco;
  }


  public void setPreco(int preco) {
    this.preco = preco;
  }

  @Override
  public String toString() {
    return "Produto [codigo=" + codigo + ", descricao=" + descricao + ", preco=" + preco
        + "]";
  }
}
