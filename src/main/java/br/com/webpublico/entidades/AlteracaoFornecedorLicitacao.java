package br.com.webpublico.entidades;


import br.com.webpublico.enums.TipoAlteracaoFornecedorLicitacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Table(name = "ALTERACAOFORNECEDORLIC")
@Etiqueta("Alteração Fornecedor Licitação")
public class AlteracaoFornecedorLicitacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Long numero;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    @Etiqueta("Proposta do Fornecedor")
    private PropostaFornecedor propostaFornecedor;

    @ManyToOne
    @Etiqueta("Fornecedor")
    private LicitacaoFornecedor licitacaoFornecedor;

    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;

    @Enumerated(EnumType.STRING)
    private TipoAlteracaoFornecedorLicitacao tipoAlteracao;

    @ManyToOne
    @Etiqueta("Fornecedor")
    private FornecedorDispensaDeLicitacao fornecedorDispensaLicitacao;

    @OneToMany(mappedBy = "alteracaoFornecedorLicit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlteracaoFornecedorLicitacaoItem> itens;

    public AlteracaoFornecedorLicitacao() {
        itens = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public PropostaFornecedor getPropostaFornecedor() {
        return propostaFornecedor;
    }

    public void setPropostaFornecedor(PropostaFornecedor propostaFornecedor) {
        this.propostaFornecedor = propostaFornecedor;
    }

    public LicitacaoFornecedor getLicitacaoFornecedor() {
        return licitacaoFornecedor;
    }

    public void setLicitacaoFornecedor(LicitacaoFornecedor licitacaoFornecedor) {
        this.licitacaoFornecedor = licitacaoFornecedor;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public TipoAlteracaoFornecedorLicitacao getTipoAlteracao() {
        return tipoAlteracao;
    }

    public void setTipoAlteracao(TipoAlteracaoFornecedorLicitacao tipoAlteracao) {
        this.tipoAlteracao = tipoAlteracao;
    }

    public FornecedorDispensaDeLicitacao getFornecedorDispensaLicitacao() {
        return fornecedorDispensaLicitacao;
    }

    public void setFornecedorDispensaLicitacao(FornecedorDispensaDeLicitacao fornecedorDispensaLicitacao) {
        this.fornecedorDispensaLicitacao = fornecedorDispensaLicitacao;
    }

    public List<AlteracaoFornecedorLicitacaoItem> getItens() {
        return itens;
    }

    public void setItens(List<AlteracaoFornecedorLicitacaoItem> itens) {
        this.itens = itens;
    }
}
