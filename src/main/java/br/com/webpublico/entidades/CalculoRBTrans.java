/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCalculoRBTRans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author cheles
 */
@Entity
@GrupoDiagrama(nome = "RBTrans")
@Audited
@Etiqueta(value = "Cálculo RBTrans")
public class CalculoRBTrans extends Calculo implements Serializable {

    @ManyToOne
    private ProcessoCalculoRBTrans processoCalculo;
    @Enumerated(EnumType.STRING)
    private TipoCalculoRBTRans tipoCalculoRBTRans;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "calculoRBTrans")
    private List<ItemCalculoRBTrans> itensCalculo;
    //Utilizado para definir o vencimento do débito da RBTrans,
    //se não informar utilizará o vencimento padrao(ultimo dia do mes)
    @Temporal(TemporalType.DATE)
    private Date vencimento;
    @Tabelavel
    @Etiqueta("Número do Lançamento")
    private Integer numeroLancamento;
    @Tabelavel
    @Transient// Apenas para a Pesquisa Genérica :(
    @Etiqueta("C.M.C")
    private CadastroEconomico cadastroPesquisa;
    @Tabelavel
    @Transient// Apenas para a Pesquisa Genérica :(
    @Etiqueta("Ano de Lançamento")
    private Integer anoPesquisa;
    @Tabelavel
    @Transient// Apenas para a Pesquisa Genérica :(
    @Etiqueta("Contribuínte")
    private Pessoa contribuintePesquisa;
    @Tabelavel
    @Transient// Apenas para a Pesquisa Genérica :(
    @Etiqueta("Referência")
    private String referenciaPesquisa;

    public CalculoRBTrans() {
        super();
        super.setTipoCalculo(TipoCalculo.RB_TRANS);
        this.itensCalculo = Lists.newArrayList();
    }

    public ProcessoCalculoRBTrans getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalculoRBTrans processoCalculo) {
        super.setProcessoCalculo(processoCalculo);
        this.processoCalculo = processoCalculo;
    }

    public TipoCalculoRBTRans getTipoCalculoRBTRans() {
        return tipoCalculoRBTRans;
    }

    public void setTipoCalculoRBTRans(TipoCalculoRBTRans tipoCalculoRBTRans) {
        this.tipoCalculoRBTRans = tipoCalculoRBTRans;
    }

    public CadastroEconomico getCadastro() {
        return (CadastroEconomico) super.getCadastro();
    }

    public List<ItemCalculoRBTrans> getItensCalculo() {
        return itensCalculo;
    }

    public void setItensCalculo(List<ItemCalculoRBTrans> itensCalculo) {
        this.itensCalculo = itensCalculo;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public CadastroEconomico getCadastroPesquisa() {
        return cadastroPesquisa;
    }

    public void setCadastroPesquisa(CadastroEconomico cadastroPesquisa) {
        this.cadastroPesquisa = cadastroPesquisa;
    }

    public Integer getAnoPesquisa() {
        return anoPesquisa;
    }

    public void setAnoPesquisa(Integer anoPesquisa) {
        this.anoPesquisa = anoPesquisa;
    }

    public Pessoa getContribuintePesquisa() {
        return contribuintePesquisa;
    }

    public String getReferenciaPesquisa() {
        return referenciaPesquisa;
    }

    public void setReferenciaPesquisa(String referenciaPesquisa) {
        this.referenciaPesquisa = referenciaPesquisa;
    }

    public void setContribuintePesquisa(Pessoa contribuintePesquisa) {
        this.contribuintePesquisa = contribuintePesquisa;
    }

    public Integer getNumeroLancamento() {
        return numeroLancamento;
    }

    public void setNumeroLancamento(Integer numeroLancamento) {
        this.numeroLancamento = numeroLancamento;
    }

    public void preencherColunasPesquisaGenerica() {
        setCadastroPesquisa(this.getCadastro());
        setContribuintePesquisa(((CadastroEconomico) this.getCadastro()).getPessoa());
        setAnoPesquisa(this.getProcessoCalculo().getExercicio().getAno());
        setReferenciaPesquisa(this.getReferencia());
    }

    public void defineReferencia() {
        StringBuilder sb = new StringBuilder();
        sb.append("RBTrans - ");
        sb.append(tipoCalculoRBTRans.getDescricao());
        for (ItemCalculoRBTrans item : itensCalculo) {
            sb.append(" - ").append(item.getTributo().getDescricao());
        }
        super.setReferencia(sb.toString());
    }
}
