package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.TransporteConfiguracaoContabil;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.contabil.BarraProgressoAssistente;
import br.com.webpublico.enums.TipoConfiguracaoContabil;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by HardRock on 05/01/2017.
 */
public class AssistenteTransporteConfiguracaoContabil {

    private TipoConfiguracaoContabil tipoConfiguracaoContabil;
    private String conteudoLog;
    private Map<TipoConfiguracaoContabil, HashSet<String>> mapaMensagens;
    private BarraProgressoAssistente assistenteBarraProgresso;
    private UsuarioSistema usuarioSistema;
    private Date dataTransporte;
    private Exercicio exercicio;
    private String imagem;
    private TransporteConfiguracaoContabil transporteConfiguracaoContabil;
    private String queryOccConta;

    public AssistenteTransporteConfiguracaoContabil() {
        assistenteBarraProgresso = new BarraProgressoAssistente();
    }

    public TipoConfiguracaoContabil getTipoConfiguracaoContabil() {
        return tipoConfiguracaoContabil;
    }

    public void setTipoConfiguracaoContabil(TipoConfiguracaoContabil tipoConfiguracaoContabil) {
        this.tipoConfiguracaoContabil = tipoConfiguracaoContabil;
    }

    public String getConteudoLog() {
        return conteudoLog;
    }

    public void setConteudoLog(String conteudoLog) {
        this.conteudoLog = conteudoLog;
    }

    public Map<TipoConfiguracaoContabil, HashSet<String>> getMapaMensagens() {
        return mapaMensagens;
    }

    public void setMapaMensagens(Map<TipoConfiguracaoContabil, HashSet<String>> mapaMensagens) {
        this.mapaMensagens = mapaMensagens;
    }

    public BarraProgressoAssistente getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(BarraProgressoAssistente assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataTransporte() {
        return dataTransporte;
    }

    public void setDataTransporte(Date dataTransporte) {
        this.dataTransporte = dataTransporte;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public TransporteConfiguracaoContabil getTransporteConfiguracaoContabil() {
        return transporteConfiguracaoContabil;
    }

    public void setTransporteConfiguracaoContabil(TransporteConfiguracaoContabil transporteConfiguracaoContabil) {
        this.transporteConfiguracaoContabil = transporteConfiguracaoContabil;
    }

    public String getQueryOccConta() {
        return queryOccConta;
    }

    public void setQueryOccConta(String queryOccConta) {
        this.queryOccConta = queryOccConta;
    }
}
