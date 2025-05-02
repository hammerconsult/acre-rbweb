package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ConfiguracaoContabil;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.IntegracaoRHContabil;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class AssistenteIntegracaoRHContabil {

    private ConfiguracaoContabil configuracaoContabil;
    private Date dataOperacao;
    private Exercicio exercicio;
    private IntegracaoRHContabil integracaoRHContabil;
    private BarraProgressoItens barraProgressoItens;
    private List<String> mensagens = Lists.newArrayList();

    public AssistenteIntegracaoRHContabil() {
        this.barraProgressoItens = new BarraProgressoItens();
    }

    public IntegracaoRHContabil getIntegracaoRHContabil() {
        return integracaoRHContabil;
    }

    public void setIntegracaoRHContabil(IntegracaoRHContabil integracaoRHContabil) {
        this.integracaoRHContabil = integracaoRHContabil;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<String> mensagens) {
        this.mensagens = mensagens;
    }

    public BarraProgressoItens getBarraProgressoItens() {
        return barraProgressoItens;
    }

    public void setBarraProgressoItens(BarraProgressoItens barraProgressoItens) {
        this.barraProgressoItens = barraProgressoItens;
    }

    public ConfiguracaoContabil getConfiguracaoContabil() {
        return configuracaoContabil;
    }

    public void setConfiguracaoContabil(ConfiguracaoContabil configuracaoContabil) {
        this.configuracaoContabil = configuracaoContabil;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
}
