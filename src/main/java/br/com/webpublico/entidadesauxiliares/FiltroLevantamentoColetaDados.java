package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Bairro;
import br.com.webpublico.entidades.Logradouro;
import br.com.webpublico.entidades.Lote;
import br.com.webpublico.entidades.Propriedade;
import com.google.common.base.Strings;

public class FiltroLevantamentoColetaDados {

    private Propriedade proprietario;
    private String inscricaoBciInicial;
    private String inscricaoBciFinal;
    private Lote lote;
    private Bairro bairro;
    private Logradouro logradouro;

    public Propriedade getProprietario() {
        return proprietario;
    }

    public void setProprietario(Propriedade proprietario) {
        this.proprietario = proprietario;
    }

    public String getInscricaoBciInicial() {
        return inscricaoBciInicial;
    }

    public void setInscricaoBciInicial(String inscricaoBciInicial) {
        this.inscricaoBciInicial = inscricaoBciInicial;
    }

    public String getInscricaoBciFinal() {
        return inscricaoBciFinal;
    }

    public void setInscricaoBciFinal(String inscricaoBciFinal) {
        this.inscricaoBciFinal = inscricaoBciFinal;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public String getCondicaoPesquisaBci() {
        StringBuilder sb = new StringBuilder("");
        if (proprietario != null) {
            sb.append(" and prop.id = ").append(proprietario.getId());
        }
        if (!Strings.isNullOrEmpty(inscricaoBciInicial)) {
            sb.append(" and ci.inscricaocadastral like '").append(inscricaoBciInicial).append("%' ");
        }
        if (!Strings.isNullOrEmpty(inscricaoBciFinal)) {
            sb.append(" and ci.inscricaocadastral like '").append(inscricaoBciFinal).append("%' ");
        }
        if (lote != null) {
            sb.append(" and lote.id = ").append(lote.getId());
        }
        if (bairro != null) {
            sb.append(" and br.id = ").append(bairro.getId());
        }
        if (logradouro != null) {
            sb.append(" and lg.id = ").append(logradouro.getId());
        }
        return sb.toString();
    }
}
