/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 15/06/15
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity
@Etiqueta("Anulação da Emenda")
public class AnulacaoEmenda extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Emenda")
    private Emenda emenda;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    @Pesquisavel
    @Tabelavel
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Projeto/Atividade")
    @Pesquisavel
    @Tabelavel
    private AcaoPPA acaoPPA;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Sub-Projeto/Atividade")
    @Pesquisavel
    @Tabelavel
    private SubAcaoPPA subAcaoPPA;
    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Conta de Despesa")
    private Conta conta;
    @Tabelavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Fonte de Recurso")
    private Conta destinacaoDeRecursos;
    @Tabelavel
    @Etiqueta("Especificação da Meta")
    private String especificacaoMeta;
    @Tabelavel
    @Etiqueta("Quantidade")
    private Integer quantidade;
    @Tabelavel
    @Monetario
    @Etiqueta("Valor")
    private BigDecimal valor;

    public AnulacaoEmenda() {
        super();
        this.quantidade = 0;
        this.valor = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Emenda getEmenda() {
        return emenda;
    }

    public void setEmenda(Emenda emenda) {
        this.emenda = emenda;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Conta getDestinacaoDeRecursos() {
        return destinacaoDeRecursos;
    }

    public void setDestinacaoDeRecursos(Conta destinacaoDeRecursos) {
        this.destinacaoDeRecursos = destinacaoDeRecursos;
    }

    public ContaDeDestinacao getContaAsDestinacao() {
        return (ContaDeDestinacao) destinacaoDeRecursos;
    }

    public String getEspecificacaoMeta() {
        return especificacaoMeta;
    }

    public void setEspecificacaoMeta(String especificacaoMeta) {
        this.especificacaoMeta = especificacaoMeta;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getTotalAnulacoes() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        if (this.emenda.getAnulacaoEmendas() != null) {
            for (AnulacaoEmenda anul : this.emenda.getAnulacaoEmendas()) {
                total = total.add(anul.getValor());
            }
        }
        return total;
    }

    public Integer getTotalQtdeMetas() {
        Integer total = 0;
        if (this.emenda.getAnulacaoEmendas() != null) {
            for (AnulacaoEmenda anul : this.emenda.getAnulacaoEmendas()) {
                total = total + anul.getQuantidade();
            }
        }
        return total;
    }
}
