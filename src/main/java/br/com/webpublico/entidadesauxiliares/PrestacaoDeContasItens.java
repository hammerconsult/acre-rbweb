package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;

import javax.persistence.Temporal;
import javax.persistence.Transient;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Edi on 18/05/2015.
 */
public class PrestacaoDeContasItens {

    private File zipFile;
    private StreamedContent streamedContent;
    private Exercicio exercicio;
    private Mes mes;
    private UnidadeGestora unidadeGestora;
    private ConverterAutoComplete converterUnidadeGestora;
    private List<String> errosGeracaoDoArquivo;
    private Integer quantidadeDeLancamento;
    private Integer quantidadeDeLancamentoPorMovimento;
    private Boolean gerarArquivoOrcamento;
    private DualListModel<String> tiposEventosContabeis;
    private List<LancamentoArquivoTCE> lancamentosSaldoInicial;
    private List<PartidaArquivoTCE> partidasSaldoInicial;
    private Boolean buscarSaldoInicial;
    private String mensagens;
    private Double quantoFalta;
    private Integer total;
    private Long decorrido;
    private Long tempo;
    private Integer processados;
    private HashMap<String, File> files;
    private Boolean calculando;
    private Date dataOperacao;
    private UsuarioSistema usuarioSistema;

    public PrestacaoDeContasItens() {
        errosGeracaoDoArquivo = new ArrayList<>();
        lancamentosSaldoInicial = new ArrayList<>();
        partidasSaldoInicial = new ArrayList<>();
        files = new HashMap<>();
        tiposEventosContabeis = new DualListModel<>();
    }

    public File getZipFile() {
        return zipFile;
    }

    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }

    public HashMap<String, File> getFiles() {
        return files;
    }

    public void setFiles(HashMap<String, File> files) {
        this.files = files;
    }

    public StreamedContent getStreamedContent() {
        return streamedContent;
    }

    public void setStreamedContent(StreamedContent streamedContent) {
        this.streamedContent = streamedContent;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public ConverterAutoComplete getConverterUnidadeGestora() {
        return converterUnidadeGestora;
    }

    public void setConverterUnidadeGestora(ConverterAutoComplete converterUnidadeGestora) {
        this.converterUnidadeGestora = converterUnidadeGestora;
    }

    public List<String> getErrosGeracaoDoArquivo() {
        return errosGeracaoDoArquivo;
    }

    public void setErrosGeracaoDoArquivo(List<String> errosGeracaoDoArquivo) {
        this.errosGeracaoDoArquivo = errosGeracaoDoArquivo;
    }

    public Integer getQuantidadeDeLancamento() {
        return quantidadeDeLancamento;
    }

    public void setQuantidadeDeLancamento(Integer quantidadeDeLancamento) {
        this.quantidadeDeLancamento = quantidadeDeLancamento;
    }

    public Integer getQuantidadeDeLancamentoPorMovimento() {
        return quantidadeDeLancamentoPorMovimento;
    }

    public void setQuantidadeDeLancamentoPorMovimento(Integer quantidadeDeLancamentoPorMovimento) {
        this.quantidadeDeLancamentoPorMovimento = quantidadeDeLancamentoPorMovimento;
    }

    public Boolean getGerarArquivoOrcamento() {
        return gerarArquivoOrcamento;
    }

    public void setGerarArquivoOrcamento(Boolean gerarArquivoOrcamento) {
        this.gerarArquivoOrcamento = gerarArquivoOrcamento;
    }

    public DualListModel<String> getTiposEventosContabeis() {
        return tiposEventosContabeis;
    }

    public void setTiposEventosContabeis(DualListModel<String> tiposEventosContabeis) {
        this.tiposEventosContabeis = tiposEventosContabeis;
    }

    public List<LancamentoArquivoTCE> getLancamentosSaldoInicial() {
        return lancamentosSaldoInicial;
    }

    public void setLancamentosSaldoInicial(List<LancamentoArquivoTCE> lancamentosSaldoInicial) {
        this.lancamentosSaldoInicial = lancamentosSaldoInicial;
    }

    public List<PartidaArquivoTCE> getPartidasSaldoInicial() {
        return partidasSaldoInicial;
    }

    public void setPartidasSaldoInicial(List<PartidaArquivoTCE> partidasSaldoInicial) {
        this.partidasSaldoInicial = partidasSaldoInicial;
    }

    public Boolean getBuscarSaldoInicial() {
        return buscarSaldoInicial;
    }

    public void setBuscarSaldoInicial(Boolean buscarSaldoInicial) {
        this.buscarSaldoInicial = buscarSaldoInicial;
    }

    public boolean isCalculando() {
        return calculando;
    }

    public void setCalculando(boolean calculando) {
        this.calculando = calculando;
    }

    public String getMensagens() {
        return mensagens;
    }

    public void setMensagens(String mensagens) {
        this.mensagens = mensagens;
    }

    public Double getQuantoFalta() {
        return quantoFalta;
    }

    public void setQuantoFalta(Double quantoFalta) {
        this.quantoFalta = quantoFalta;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Long getDecorrido() {
        return decorrido;
    }

    public void setDecorrido(Long decorrido) {
        this.decorrido = decorrido;
    }

    public Long getTempo() {
        return tempo;
    }

    public void setTempo(Long tempo) {
        this.tempo = tempo;
    }

    public Integer getProcessados() {
        return processados;
    }

    public void setProcessados(Integer processados) {
        this.processados = processados;
    }

    public Boolean getCalculando() {
        return calculando;
    }

    public void setCalculando(Boolean calculando) {
        this.calculando = calculando;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
