package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.interfaces.BloqueioMovimentoContabil;

import java.math.BigDecimal;
import java.util.Date;

public abstract class MovimentoContabil extends SuperEntidade implements BloqueioMovimentoContabil {

    private Date dataBloqueio;

    public MovimentoContabil() {
     }

    public abstract Long getId();

    public abstract Object getNumero();

    public abstract Date getData();

    public abstract BigDecimal getValor();

    public abstract Exercicio getExercicio();

    public abstract String getHistoricoRazao();

    public abstract String getCaminho();

    public abstract UsuarioSistema getUsuarioSistema();

    public abstract void setUsuarioSistema(UsuarioSistema usuarioSistema);

    public String getCaminhoEditar() {
        return getCaminho() + "editar/" + getId() + "/";
    }

    public String getCaminhoVer() {
        return getCaminho() + "ver/" + getId() + "/";
    }

    public abstract void gerarHistoricos();

    public abstract UnidadeOrganizacional getUnidadeOrganizacionalAdm();

    public abstract UnidadeOrganizacional getUnidadeOrganizacionalOrc();

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    @Override
    public String getMensagemBloqueioMovimentoContabil() {
        return getArtigoDefinidoDaEntidade().toUpperCase() + "  " + getNomeDaEntidade() + " (" + toString() + ") "
            + getPromonomeDemonstrativoDaEntidade() +
            " sendo utilizado em outro processo, por favor tente novamente mais tarde.";
    }
}
