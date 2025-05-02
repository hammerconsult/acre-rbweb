package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.AlteracaoORC;
import br.com.webpublico.entidades.AnulacaoORC;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.util.List;

public class AssistenteAlteracaoOrc extends AssistenteBarraProgresso {

    private AlteracaoORC alteracaoORC;
    private BarraProgressoItens barraProgressoItens;
    private List<String> mensagens = Lists.newArrayList();
    private List<AnulacaoORC> anulacoesRemovidas;

    public AssistenteAlteracaoOrc() {
        barraProgressoItens = new BarraProgressoItens();
        anulacoesRemovidas = Lists.newArrayList();
    }

    public List<AnulacaoORC> getAnulacoesRemovidas() {
        return anulacoesRemovidas;
    }

    public void setAnulacoesRemovidas(List<AnulacaoORC> anulacoesRemovidas) {
        this.anulacoesRemovidas = anulacoesRemovidas;
    }

    public AlteracaoORC getAlteracaoORC() {
        return alteracaoORC;
    }

    public void setAlteracaoORC(AlteracaoORC alteracaoORC) {
        this.alteracaoORC = alteracaoORC;
    }

    public BarraProgressoItens getBarraProgressoItens() {
        return barraProgressoItens;
    }

    public void setBarraProgressoItens(BarraProgressoItens barraProgressoItens) {
        this.barraProgressoItens = barraProgressoItens;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<String> mensagens) {
        this.mensagens = mensagens;
    }

    public void adicionarQuantidadeRegistroParaProcessar(int size) {
        getBarraProgressoItens().setTotal(size);
    }

    public void contarRegistroProcessado() {
        getBarraProgressoItens().setProcessados(getBarraProgressoItens().getProcessados() + 1);
    }

    public Integer getTotalListas() {
        try {
            Integer retorno = 0;
            Integer qtdeVezesUtilizadaSuplementacao = 2;
            if (!this.alteracaoORC.getSuplementacoesORCs().isEmpty()) {
                retorno = retorno + alteracaoORC.getSuplementacoesORCs().size() * qtdeVezesUtilizadaSuplementacao;
            }
            Integer qtdeVezesUtilizadaAnulacao = 4;
            if (!this.alteracaoORC.getAnulacoesORCs().isEmpty()) {
                retorno = retorno + alteracaoORC.getAnulacoesORCs().size() * qtdeVezesUtilizadaAnulacao;
            }
            if (!this.alteracaoORC.getReceitasAlteracoesORCs().isEmpty()) {
                retorno = retorno + alteracaoORC.getReceitasAlteracoesORCs().size();
            }
            if (!this.anulacoesRemovidas.isEmpty()) {
                retorno = retorno + anulacoesRemovidas.size();
            }
            return retorno;
        } catch (Exception e) {
            return 0;
        }
    }
}
