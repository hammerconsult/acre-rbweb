package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPrazo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 18/07/14
 * Time: 11:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Proposta Fornecedor Dispensa de Licitação")
public class PropostaFornecedorDispensa implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data da Proposta")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataDaProposta;

    @Etiqueta("Prazo de Validade da Proposta")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Integer prazoDeValidade;

    @Etiqueta("Tipo de Prazo de Validade da Proposta")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazoDeValidade;

    @Etiqueta("Prazo de Execução da Proposta")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Integer prazoDeExecucao;

    @Etiqueta("Tipo de Prazo de Execução da Proposta")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazoDeExecucao;

    @Etiqueta("Lotes da Proposta")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propostaFornecedorDispensa", orphanRemoval = true)
    private List<LotePropostaFornecedorDispensa> lotesDaProposta;

    @OneToMany(mappedBy = "propostaFornecedorDispensa")
    private List<FornecedorDispensaDeLicitacao> fornecedorDispensaDeLicitacao;

    @Invisivel
    @Transient
    private Long criadoEm;

    public PropostaFornecedorDispensa() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<FornecedorDispensaDeLicitacao> getFornecedorDispensaDeLicitacao() {
        return fornecedorDispensaDeLicitacao;
    }

    public void setFornecedorDispensaDeLicitacao(List<FornecedorDispensaDeLicitacao> fornecedorDispensaDeLicitacao) {
        this.fornecedorDispensaDeLicitacao = fornecedorDispensaDeLicitacao;
    }

    public Date getDataDaProposta() {
        return dataDaProposta;
    }

    public void setDataDaProposta(Date dataDaProposta) {
        this.dataDaProposta = dataDaProposta;
    }

    public Integer getPrazoDeValidade() {
        return prazoDeValidade;
    }

    public void setPrazoDeValidade(Integer prazoDeValidade) {
        this.prazoDeValidade = prazoDeValidade;
    }

    public TipoPrazo getTipoPrazoDeValidade() {
        return tipoPrazoDeValidade;
    }

    public void setTipoPrazoDeValidade(TipoPrazo tipoPrazoDeValidade) {
        this.tipoPrazoDeValidade = tipoPrazoDeValidade;
    }

    public Integer getPrazoDeExecucao() {
        return prazoDeExecucao;
    }

    public void setPrazoDeExecucao(Integer prazoDeExecucao) {
        this.prazoDeExecucao = prazoDeExecucao;
    }

    public TipoPrazo getTipoPrazoDeExecucao() {
        return tipoPrazoDeExecucao;
    }

    public void setTipoPrazoDeExecucao(TipoPrazo tipoPrazoDeExecucao) {
        this.tipoPrazoDeExecucao = tipoPrazoDeExecucao;
    }

    public List<LotePropostaFornecedorDispensa> getLotesDaProposta() {
        return lotesDaProposta;
    }

    public void setLotesDaProposta(List<LotePropostaFornecedorDispensa> lotesDaProposta) {
        this.lotesDaProposta = lotesDaProposta;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }
}
