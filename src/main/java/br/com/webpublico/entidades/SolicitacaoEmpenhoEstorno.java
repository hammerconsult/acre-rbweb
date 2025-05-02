package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.OrigemSolicitacaoEmpenho;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Solicitação de Empenho Estorno")
public class SolicitacaoEmpenhoEstorno extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data da Solicitação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataSolicitacao;

    @ManyToOne
    @Etiqueta("Empenho")
    private Empenho empenho;

    @ManyToOne
    @Etiqueta("Solicitação de Empenho")
    private SolicitacaoEmpenho solicitacaoEmpenho;

    @ManyToOne
    @Etiqueta("Empenho Estorno")
    private EmpenhoEstorno empenhoEstorno;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Histórico")
    private String historico;

    @Etiqueta("Valor")
    private BigDecimal valor;

    @Etiqueta("Tipo de Empenho")
    @Enumerated(EnumType.STRING)
    private OrigemSolicitacaoEmpenho origem;

    public SolicitacaoEmpenhoEstorno() {
    }

    public SolicitacaoEmpenhoEstorno(SolicitacaoEmpenho solicitacaoEmpenho, Empenho empenho, OrigemSolicitacaoEmpenho origem) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
        this.empenho = empenho;
        this.origem = origem;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public EmpenhoEstorno getEmpenhoEstorno() {
        return empenhoEstorno;
    }

    public void setEmpenhoEstorno(EmpenhoEstorno empenhoEstorno) {
        this.empenhoEstorno = empenhoEstorno;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public OrigemSolicitacaoEmpenho getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemSolicitacaoEmpenho origem) {
        this.origem = origem;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }

    public boolean isSolicitacaoEstornoPorSolicitacaoEmpenho() {
        return empenho == null;
    }

    public boolean hasSolicitacaoEstornoEmpenhoEstornada() {
        return empenhoEstorno != null;
    }

    public Date getData() {
        return isSolicitacaoEstornoPorSolicitacaoEmpenho() ? solicitacaoEmpenho.getDataSolicitacao() : empenho.getDataEmpenho();
    }

    public String getNumero() {
        return isSolicitacaoEstornoPorSolicitacaoEmpenho() ? "" : empenho.getNumero();
    }

    public CategoriaOrcamentaria getCategoria() {
        return isSolicitacaoEstornoPorSolicitacaoEmpenho() ? CategoriaOrcamentaria.NORMAL : empenho.getCategoriaOrcamentaria();
    }

    public Pessoa getFornecedor(){
        return isSolicitacaoEstornoPorSolicitacaoEmpenho() ? solicitacaoEmpenho.getFornecedor() : empenho.getFornecedor();
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return isSolicitacaoEstornoPorSolicitacaoEmpenho() ? solicitacaoEmpenho.getFonteDespesaORC() : empenho.getFonteDespesaORC();
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return isSolicitacaoEstornoPorSolicitacaoEmpenho() ? solicitacaoEmpenho.getUnidadeOrganizacional() : empenho.getUnidadeOrganizacional();
    }

    public ClasseCredor getClasseCredor(){
        return isSolicitacaoEstornoPorSolicitacaoEmpenho() ? solicitacaoEmpenho.getClasseCredor() : empenho.getClasseCredor();
    }

    @Override
    public String toString() {
        try {
            return DataUtil.getDataFormatada(dataSolicitacao) + " " + Util.formataValor(valor) + ", empenho nº " + empenho.getNumero();
        } catch (NullPointerException e) {
            return "";
        }
    }
}
