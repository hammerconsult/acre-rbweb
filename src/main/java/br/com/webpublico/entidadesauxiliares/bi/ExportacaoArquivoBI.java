package br.com.webpublico.entidadesauxiliares.bi;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.BarraProgressoItens;
import com.google.common.collect.Lists;
import org.primefaces.model.StreamedContent;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ExportacaoArquivoBI implements Serializable {

    private TipoExportacaoArquivoBI tipo;
    private Date dataOperacao;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private BarraProgressoItens barraProgressoItens;
    private StringBuilder linha;
    private StreamedContent streamedContent;
    private Boolean deuErro;
    private List<TipoExportacaoArquivoBI> tiposSelecionados;

    public ExportacaoArquivoBI() {
        this.deuErro = false;
        this.tiposSelecionados = Lists.newArrayList();
    }

    public TipoExportacaoArquivoBI getTipo() {
        return tipo;
    }

    public void setTipo(TipoExportacaoArquivoBI tipo) {
        this.tipo = tipo;
    }

    public List<TipoExportacaoArquivoBI> getTiposSelecionados() {
        return tiposSelecionados;
    }

    public void setTiposSelecionados(List<TipoExportacaoArquivoBI> tiposSelecionados) {
        this.tiposSelecionados = tiposSelecionados;
    }

    public BarraProgressoItens getBarraProgressoItens() {
        return barraProgressoItens;
    }

    public void setBarraProgressoItens(BarraProgressoItens barraProgressoItens) {
        this.barraProgressoItens = barraProgressoItens;
    }

    public StringBuilder getLinha() {
        return linha;
    }

    public void setLinha(StringBuilder linha) {
        this.linha = linha;
    }

    public StreamedContent getStreamedContent() {
        return streamedContent;
    }

    public void setStreamedContent(StreamedContent streamedContent) {
        this.streamedContent = streamedContent;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public Boolean getDeuErro() {
        return deuErro;
    }

    public void setDeuErro(Boolean deuErro) {
        this.deuErro = deuErro;
    }

    public void quebrarLinha() {
        getLinha().append("\r\n");
    }

    public void adicionarSeparador() {
        getLinha().append(";");
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }
}
