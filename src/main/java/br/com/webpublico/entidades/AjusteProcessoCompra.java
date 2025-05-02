package br.com.webpublico.entidades;


import br.com.webpublico.entidadesauxiliares.AjusteProcessoCompraVO;
import br.com.webpublico.enums.TipoAjusteProcessoCompra;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Alteração Fornecedor Licitação")
public class AjusteProcessoCompra extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Long numero;

    @Etiqueta("Processo")
    @Obrigatorio
    private Long idProcesso;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Processo")
    private TipoMovimentoProcessoLicitatorio tipoProcesso;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Ajuste")
    private TipoAjusteProcessoCompra tipoAjuste;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    @Etiqueta("Proposta Fornecedor")
    private PropostaFornecedor propostaFornecedor;

    @ManyToOne
    @Etiqueta("Fornecedor Licitação")
    private LicitacaoFornecedor licitacaoFornecedor;

    @ManyToOne
    @Etiqueta("Fornecedor Dispensa")
    private FornecedorDispensaDeLicitacao fornecedorDispensaLicitacao;

    @Obrigatorio
    @Etiqueta("Motivo")
    @Length(maximo = 3000)
    private String motivo;

    @Lob
    @Etiqueta("Histórico")
    private String historico;

    @OneToMany(mappedBy = "ajusteProcessoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AjusteProcessoCompraItem> itens;

    public AjusteProcessoCompra() {
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String historico) {
        this.motivo = historico;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historicoProcesso) {
        this.historico = historicoProcesso;
    }

    public Long getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public TipoMovimentoProcessoLicitatorio getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoMovimentoProcessoLicitatorio tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public TipoAjusteProcessoCompra getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(TipoAjusteProcessoCompra tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public FornecedorDispensaDeLicitacao getFornecedorDispensaLicitacao() {
        return fornecedorDispensaLicitacao;
    }

    public void setFornecedorDispensaLicitacao(FornecedorDispensaDeLicitacao fornecedorDispensaLicitacao) {
        this.fornecedorDispensaLicitacao = fornecedorDispensaLicitacao;
    }

    public List<AjusteProcessoCompraItem> getItens() {
        return itens;
    }

    public void setItens(List<AjusteProcessoCompraItem> itens) {
        this.itens = itens;
    }

    public static AjusteProcessoCompra toAjusteProcessoCompraVO(AjusteProcessoCompraVO ajusteVO){
        AjusteProcessoCompra entity = new AjusteProcessoCompra();
        entity.itens = Lists.newArrayList();
        entity.dataLancamento = ajusteVO.getDataLancamento();
        entity.idProcesso = ajusteVO.getIdProcesso();
        entity.tipoAjuste = ajusteVO.getTipoAjuste();
        entity.tipoProcesso = ajusteVO.getTipoProcesso();
        entity.usuarioSistema = ajusteVO.getUsuarioSistema();
        entity.propostaFornecedor = ajusteVO.getPropostaFornecedor();
        entity.licitacaoFornecedor = ajusteVO.getLicitacaoFornecedor();
        entity.fornecedorDispensaLicitacao = ajusteVO.getFornecedorDispensaLicitacao();
        entity.motivo = ajusteVO.getMotivo();
        entity.historico = ajusteVO.getHistorico();
        return entity;
    }
}
