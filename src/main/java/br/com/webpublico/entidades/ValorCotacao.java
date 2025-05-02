/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemCotacao;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Cotação Solicitação")
public class ValorCotacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Preço")
    private BigDecimal preco;

    @Etiqueta("Observação")
    private String observacao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Marca")
    private String marca;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Item Cotação")
    @ManyToOne
    private ItemCotacao itemCotacao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Fornecedor")
    @ManyToOne
    private Pessoa fornecedor;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Contrato")
    @ManyToOne
    private Contrato contrato;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Unidade Externa")
    @ManyToOne
    private UnidadeExterna unidadeExterna;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Origem da Cotação")
    private OrigemCotacao origemCotacao;

    public ValorCotacao() {
        preco = BigDecimal.ZERO;
    }

    public Pessoa getFornecedorOrigemCotacao(){
        switch (origemCotacao){
            case BANCO_DE_PRECO_RBWEB: return contrato.getContratado();
            case BANCO_DE_PRECO_EXTERNO: return unidadeExterna.getPessoaJuridica();
            default: return fornecedor;
        }
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public ItemCotacao getItemCotacao() {
        return itemCotacao;
    }

    public void setItemCotacao(ItemCotacao itemCotacao) {
        this.itemCotacao = itemCotacao;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public OrigemCotacao getOrigemCotacao() {
        return origemCotacao;
    }

    public void setOrigemCotacao(OrigemCotacao origemCotacao) {
        this.origemCotacao = origemCotacao;
    }

    public UnidadeExterna getUnidadeExterna() {
        return unidadeExterna;
    }

    public void setUnidadeExterna(UnidadeExterna unidadeExterna) {
        this.unidadeExterna = unidadeExterna;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        return "ValorCotacao{" + "preco=" + preco + ", marca=" + marca + '}';
    }

    public BigDecimal getTotal() {
        try {
            if (itemCotacao.getQuantidade().compareTo(BigDecimal.ZERO) > 0) {
                return itemCotacao.getQuantidade().multiply(preco);
            }
            return preco;
        } catch (NullPointerException npe) {
            return BigDecimal.ZERO;
        }
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitario() {
        if (origemCotacao.isOrigemBancoExterno()){
            return TipoMascaraUnidadeMedida.QUATRO_CASAS_DECIMAL;
        }
        return itemCotacao.getUnidadeMedida().getMascaraValorUnitario();
    }

    public boolean isFornecedor(Pessoa fornecedor) {
        return this.fornecedor.equals(fornecedor);
    }
}
