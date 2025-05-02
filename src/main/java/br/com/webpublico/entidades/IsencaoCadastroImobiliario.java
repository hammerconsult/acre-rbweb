package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLancamentoIsencaoIPTU;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Solicitação de Isenção de IPTU")
public class IsencaoCadastroImobiliario extends SuperEntidade implements Comparable<IsencaoCadastroImobiliario> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long sequencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Tipo de Lançamento")
    private TipoLancamentoIsencaoIPTU tipoLancamentoIsencao;
    @Etiqueta("Lança Imposto")
    private Boolean lancaImposto;
    @Etiqueta("Lança Taxa")
    private Boolean lancaTaxa;
    @ManyToOne
    @Etiqueta("Cadastro Imobiliário")
    @Pesquisavel
    private CadastroImobiliario cadastroImobiliario;
    @ManyToOne
    @Etiqueta("Solicitação de Processo de Isenção de IPTU")
    @Pesquisavel
    private ProcessoIsencaoIPTU processoIsencaoIPTU;
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @ManyToOne(cascade = CascadeType.ALL)
    private DocumentoOficial documentoOficial;
    @Pesquisavel
    @Etiqueta("Código")
    @Transient
    @Tabelavel
    private Long codigoProcesso;
    @Pesquisavel
    @Etiqueta("Exercício de Referência")
    @Transient
    @Tabelavel
    private Exercicio exercicioProcesso;
    @Etiqueta("Início e Fim de vigência")
    @Transient
    @Tabelavel
    private String vigenciaString;
    @Pesquisavel
    @Etiqueta("Protocolo")
    @Transient
    @Tabelavel
    private String protocoloProcesso;
    @Pesquisavel
    @Etiqueta("Situação do Processo")
    @Transient
    @Tabelavel
    private ProcessoIsencaoIPTU.Situacao situacaoProcesso;
    @Tabelavel
    @Etiqueta("Cadastro Imobiliário")
    @Transient
    private String cadastroImobiliarioString;
    @Tabelavel
    @Etiqueta("Categoria")
    @Transient
    private String categoriaString;
    private BigDecimal percentual;

    public IsencaoCadastroImobiliario(IsencaoCadastroImobiliario obj) {
        this.id = obj.getId();
        this.codigoProcesso = obj.getProcessoIsencaoIPTU().getNumero();
        this.exercicioProcesso = obj.getProcessoIsencaoIPTU().getExercicioReferencia();
        this.protocoloProcesso = obj.getProcessoIsencaoIPTU().getNumeroProtocolo() + "/" + obj.getProcessoIsencaoIPTU().getAnoProtocoloString();
        this.situacaoProcesso = obj.getProcessoIsencaoIPTU().getSituacao();
        this.cadastroImobiliario = obj.getCadastroImobiliario();
        this.cadastroImobiliarioString = obj.getCadastroImobiliario().toString();
        if (!obj.getCadastroImobiliario().getPropriedadeVigente().isEmpty()) {
            this.cadastroImobiliarioString += " - " + obj.getCadastroImobiliario().getPropriedadeVigente().get(0).getPessoa().getNomeCpfCnpj();
        }
        this.tipoLancamentoIsencao = obj.getTipoLancamentoIsencao();
        this.categoriaString = obj.getProcessoIsencaoIPTU().getCategoriaIsencaoIPTU().toString();
        if (obj.inicioVigencia != null && obj.finalVigencia != null) {
            this.vigenciaString = "<table> <tr>  " +
                "<td>" + DataUtil.getDataFormatada(obj.inicioVigencia) + " </td> " +
                "<td> <div style='width:10px!important'> </div> </td> " +
                "<td> - </td>" +
                "<td> <div style='width:10px!important'> </div> </td> " +
                "<td> " + DataUtil.getDataFormatada(obj.finalVigencia) + "</td>" +
                "</tr> </table>";
        } else {
            this.vigenciaString = "";
        }
    }

    public IsencaoCadastroImobiliario(Long id) {
        this.id = id;
    }

    public IsencaoCadastroImobiliario() {
        lancaImposto = true;
        lancaTaxa = true;
        inicioVigencia = new Date();
        situacao = Situacao.ATIVO;
    }

    public IsencaoCadastroImobiliario(Long id, Boolean lancaImposto, Boolean lancaTaxa) {
        this.id = id;
        this.lancaImposto = lancaImposto;
        this.lancaTaxa = lancaTaxa;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public ProcessoIsencaoIPTU getProcessoIsencaoIPTU() {
        return processoIsencaoIPTU;
    }

    public void setProcessoIsencaoIPTU(ProcessoIsencaoIPTU processoIsencaoIPTU) {
        this.processoIsencaoIPTU = processoIsencaoIPTU;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        if (id != null) {
            setId(id.longValue());
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public TipoLancamentoIsencaoIPTU getTipoLancamentoIsencao() {
        return tipoLancamentoIsencao;
    }

    public void setTipoLancamentoIsencao(TipoLancamentoIsencaoIPTU tipoLancamentoIsencao) {
        this.tipoLancamentoIsencao = tipoLancamentoIsencao;
    }

    public Boolean getLancaImposto() {
        return lancaImposto;
    }

    public void setLancaImposto(Boolean lancaImposto) {
        this.lancaImposto = lancaImposto;
    }

    public Boolean getLancaTaxa() {
        return lancaTaxa;
    }

    public void setLancaTaxa(Boolean lancaTaxa) {
        this.lancaTaxa = lancaTaxa;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    @Override
    public String toString() {
        return "(" + getCadastroImobiliario().getInscricaoCadastral() + ") " + processoIsencaoIPTU.getCategoriaIsencaoIPTU().getDescricao()
            + " de " + DataUtil.getDataFormatada(inicioVigencia) + " até " + DataUtil.getDataFormatada(finalVigencia);
    }

    @Override
    public int compareTo(IsencaoCadastroImobiliario o) {
        try {
            return o.getProcessoIsencaoIPTU().getDataLancamento().compareTo(this.processoIsencaoIPTU.getDataLancamento());
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean getVencido() {
        return (finalVigencia != null && finalVigencia.before(new Date())) || Situacao.CANCELADO.equals(situacao);
    }

    public Boolean isAtivo() {
        return Situacao.ATIVO.equals(this.getSituacao());
    }

    public Long getCodigoProcesso() {
        return codigoProcesso;
    }

    public void setCodigoProcesso(Long codigoProcesso) {
        this.codigoProcesso = codigoProcesso;
    }

    public Exercicio getExercicioProcesso() {
        return exercicioProcesso;
    }

    public void setExercicioProcesso(Exercicio exercicioProcesso) {
        this.exercicioProcesso = exercicioProcesso;
    }

    public String getProtocoloProcesso() {
        return protocoloProcesso;
    }

    public void setProtocoloProcesso(String protocoloProcesso) {
        this.protocoloProcesso = protocoloProcesso;
    }

    public ProcessoIsencaoIPTU.Situacao getSituacaoProcesso() {
        return situacaoProcesso;
    }

    public void setSituacaoProcesso(ProcessoIsencaoIPTU.Situacao situacaoProcesso) {
        this.situacaoProcesso = situacaoProcesso;
    }

    public String getCadastroImobiliarioString() {
        return cadastroImobiliarioString;
    }

    public void setCadastroImobiliarioString(String cadastroImobiliarioString) {
        this.cadastroImobiliarioString = cadastroImobiliarioString;
    }

    public String getCategoriaString() {
        return categoriaString;
    }

    public void setCategoriaString(String categoriaString) {
        this.categoriaString = categoriaString;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public static enum Situacao {
        EM_ABERTO("Em Aberto"), ATIVO("Ativo"), CANCELADO("Cancelado"), VENCIDO("Vencido");
        private String descricao;

        private Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
