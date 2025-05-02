package br.com.webpublico.pncp.dto;

import br.com.webpublico.entidades.DocumentoLicitacao;
import br.com.webpublico.enums.ModoSolicitacaoCompra;
import br.com.webpublico.enums.TipoDocumentoAnexoPNCP;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class ContratacaoDTO {

    @JsonIgnore
    private Long id;

    @JsonIgnore
    private String idPncp;

    @JsonIgnore
    private String sequencialIdPncp;

    @JsonIgnore
    private Long processoDeCompraId;

    @JsonIgnore
    private File documento;

    @JsonIgnore
    private Long idDocumento;

    @JsonIgnore
    private String cnpj;

    @JsonIgnore
    private ModoSolicitacaoCompra modoSolicitacaoCompra;

    @JsonIgnore
    private Integer criterioJulgamento;

    @JsonIgnore
    private boolean orcamentoSigiloso;

    @JsonIgnore
    private Date dataEmissao;

    @JsonIgnore
    private String resumoDoObjeto;

    @JsonIgnore
    private String modalidade;

    @JsonIgnore
    private String situacao;

    @JsonIgnore
    private BigDecimal valorEstimado;

    @NotBlank(message = "O campo 'tituloDocumento' é obrigatório")
    @NotEmpty(message = "O campo 'tituloDocumento' não pode estar vazio")
    @Size(max = 50, message = "O 'tituloDocumento' deve ter no máximo 50 caracteres")
    private String tituloDocumento;

    @NotNull(message = "O campo 'tipoDocumentoId' é obrigatório")
    private TipoDocumentoAnexoPNCP tipoDocumentoId;

    @NotBlank(message = "O campo 'codigoUnidadeCompradora' é obrigatório")
    @NotEmpty(message = "O campo 'codigoUnidadeCompradora' não pode estar vazio")
    @Size(max = 20, message = "O 'codigoUnidadeCompradora' deve ter no máximo 20 caracteres")
    private String codigoUnidadeCompradora;

    @NotNull(message = "O campo 'tipoInstrumentoConvocatorioId' é obrigatório")
    private Integer tipoInstrumentoConvocatorioId;

    @NotNull(message = "O campo 'modalidadeId' é obrigatório")
    private Integer modalidadeId;

    @NotNull(message = "O campo 'modoDisputaId' é obrigatório")
    private Integer modoDisputaId;

    private Integer situacaoCompraId;

    @NotBlank(message = "O campo 'numeroCompra' é obrigatório")
    @NotEmpty(message = "O campo 'numeroCompra' não pode estar vazio")
    @Size(max = 50, message = "O 'numeroCompra' deve ter no máximo 50 caracteres")
    private String numeroCompra;

    @NotNull(message = "O campo 'anoCompra' é obrigatório")
    private Integer anoCompra;

    @NotBlank(message = "O campo 'numeroProcesso' é obrigatório")
    @NotEmpty(message = "O campo 'numeroProcesso' não pode estar vazio")
    @Size(max = 50, message = "O 'numeroProcesso' deve ter no máximo 50 caracteres")
    private String numeroProcesso;

    @NotBlank(message = "O campo 'objetoCompra' é obrigatório")
    @NotEmpty(message = "O campo 'objetoCompra' não pode estar vazio")
    @Size(max = 5120, message = "O 'objetoCompra' deve ter no máximo 5120 caracteres")
    private String objetoCompra;
    @NotBlank(message = "O campo 'informacaoComplementar' é obrigatório")
    @NotEmpty(message = "O campo 'informacaoComplementar' não pode estar vazio")
    @Size(max = 5120, message = "O 'informacaoComplementar' deve ter no máximo 5120 caracteres")
    private String informacaoComplementar;

    @NotNull(message = "O campo 'srp' é obrigatório")
    private Boolean srp;

    @NotBlank(message = "O campo 'dataAberturaProposta' é obrigatório")
    private Date dataAberturaProposta;

    private Date dataEncerramentoProposta;

    @NotNull(message = "O campo 'amparoLegalId' é obrigatório")
    private Integer amparoLegalId;

    @NotBlank(message = "O campo 'linkSistemaOrigem' é obrigatório")
    @NotEmpty(message = "O campo 'linkSistemaOrigem' não pode estar vazio")
    @Size(max = 512, message = "O 'linkSistemaOrigem' deve ter no máximo 512 caracteres")
    private String linkSistemaOrigem;

    @NotBlank(message = "O campo 'justificativaPresencial' é obrigatório")
    @NotEmpty(message = "O campo 'justificativaPresencial' não pode estar vazio")
    @Size(max = 5120, message = "A 'justificativaPresencial' deve ter no máximo 5120 caracteres")
    private String justificativaPresencial;

    private List<ItemCompraDTO> itensCompra;
    @JsonIgnore
    private List<DocumentoLicitacao> documentos;
    @JsonIgnore
    private List<ResultadoItemDTO> resultados;

    @JsonIgnore
    private String mensagemDeErros = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdPncp() {
        return idPncp;
    }

    public void setIdPncp(String idPncp) {
        this.idPncp = idPncp;
    }

    public String getSequencialIdPncp() {
        return sequencialIdPncp;
    }

    public void setSequencialIdPncp(String sequencialIdPncp) {
        this.sequencialIdPncp = sequencialIdPncp;
    }

    public File getDocumento() {
        return documento;
    }

    public void setDocumento(File documento) {
        this.documento = documento;
    }

    public Long getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Long idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public ModoSolicitacaoCompra getModoSolicitacaoCompra() {
        return modoSolicitacaoCompra;
    }

    public void setModoSolicitacaoCompra(ModoSolicitacaoCompra modoSolicitacaoCompra) {
        this.modoSolicitacaoCompra = modoSolicitacaoCompra;
    }

    public Integer getCriterioJulgamento() {
        return criterioJulgamento;
    }

    public void setCriterioJulgamento(Integer criterioJulgamento) {
        this.criterioJulgamento = criterioJulgamento;
    }

    public boolean isOrcamentoSigiloso() {
        return orcamentoSigiloso;
    }

    public void setOrcamentoSigiloso(boolean orcamentoSigiloso) {
        this.orcamentoSigiloso = orcamentoSigiloso;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getResumoDoObjeto() {
        return resumoDoObjeto;
    }

    public void setResumoDoObjeto(String resumoDoObjeto) {
        this.resumoDoObjeto = resumoDoObjeto;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public String getTituloDocumento() {
        return tituloDocumento;
    }

    public void setTituloDocumento(String tituloDocumento) {
        this.tituloDocumento = tituloDocumento;
    }

    public TipoDocumentoAnexoPNCP getTipoDocumentoId() {
        return tipoDocumentoId;
    }

    public void setTipoDocumentoId(TipoDocumentoAnexoPNCP tipoDocumentoId) {
        this.tipoDocumentoId = tipoDocumentoId;
    }

    public String getCodigoUnidadeCompradora() {
        return codigoUnidadeCompradora;
    }

    public void setCodigoUnidadeCompradora(String codigoUnidadeCompradora) {
        this.codigoUnidadeCompradora = codigoUnidadeCompradora;
    }

    public Integer getTipoInstrumentoConvocatorioId() {
        return tipoInstrumentoConvocatorioId;
    }

    public void setTipoInstrumentoConvocatorioId(Integer tipoInstrumentoConvocatorioId) {
        this.tipoInstrumentoConvocatorioId = tipoInstrumentoConvocatorioId;
    }

    public Integer getModalidadeId() {
        return modalidadeId;
    }

    public void setModalidadeId(Integer modalidadeId) {
        this.modalidadeId = modalidadeId;
    }

    public Integer getModoDisputaId() {
        return modoDisputaId;
    }

    public void setModoDisputaId(Integer modoDisputaId) {
        this.modoDisputaId = modoDisputaId;
    }

    public Integer getSituacaoCompraId() {
        return situacaoCompraId;
    }

    public void setSituacaoCompraId(Integer situacaoCompraId) {
        this.situacaoCompraId = situacaoCompraId;
    }

    public String getNumeroCompra() {
        return numeroCompra;
    }

    public void setNumeroCompra(String numeroCompra) {
        this.numeroCompra = numeroCompra;
    }

    public Integer getAnoCompra() {
        return anoCompra;
    }

    public void setAnoCompra(Integer anoCompra) {
        this.anoCompra = anoCompra;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(String objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public String getInformacaoComplementar() {
        return informacaoComplementar;
    }

    public void setInformacaoComplementar(String informacaoComplementar) {
        this.informacaoComplementar = informacaoComplementar;
    }

    public Boolean getSrp() {
        return srp;
    }

    public void setSrp(Boolean srp) {
        this.srp = srp;
    }

    public Date getDataAberturaProposta() {
        return dataAberturaProposta;
    }

    public void setDataAberturaProposta(Date dataAberturaProposta) {
        this.dataAberturaProposta = dataAberturaProposta;
    }

    public Date getDataEncerramentoProposta() {
        return dataEncerramentoProposta;
    }

    public void setDataEncerramentoProposta(Date dataEncerramentoProposta) {
        this.dataEncerramentoProposta = dataEncerramentoProposta;
    }

    public Integer getAmparoLegalId() {
        return amparoLegalId;
    }

    public void setAmparoLegalId(Integer amparoLegalId) {
        this.amparoLegalId = amparoLegalId;
    }

    public String getLinkSistemaOrigem() {
        return linkSistemaOrigem;
    }

    public void setLinkSistemaOrigem(String linkSistemaOrigem) {
        this.linkSistemaOrigem = linkSistemaOrigem;
    }

    public String getJustificativaPresencial() {
        return justificativaPresencial;
    }

    public void setJustificativaPresencial(String justificativaPresencial) {
        this.justificativaPresencial = justificativaPresencial;
    }

    public List<ItemCompraDTO> getItensCompra() {
        return itensCompra;
    }

    public void setItensCompra(List<ItemCompraDTO> itensCompra) {
        this.itensCompra = itensCompra;
    }

    public String getMensagemDeErros() {
        return mensagemDeErros;
    }

    public void setMensagemDeErros(String mensagemDeErros) {
        this.mensagemDeErros = mensagemDeErros;
    }

    public Boolean hasMensagemDeErros() {
        return this.mensagemDeErros != null && !this.mensagemDeErros.trim().isEmpty();
    }

    public Long getProcessoDeCompraId() {
        return processoDeCompraId;
    }

    public void setProcessoDeCompraId(Long processoDeCompraId) {
        this.processoDeCompraId = processoDeCompraId;
    }

    public List<DocumentoLicitacao> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoLicitacao> documentos) {
        this.documentos = documentos;
    }

    public List<ResultadoItemDTO> getResultados() {
        return resultados;
    }

    public void setResultados(List<ResultadoItemDTO> resultados) {
        this.resultados = resultados;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }
}
