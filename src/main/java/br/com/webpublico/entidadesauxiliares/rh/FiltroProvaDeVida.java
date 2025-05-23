package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class FiltroProvaDeVida implements Serializable {

    public static final String APOSENTADOS_PENSIONISTAS = "Aposentados e Pensionistas";
    public static final String APOSENTADOS = "Aposentados";
    public static final String PENSIONISTAS = "Pensionistas";

    private String mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private boolean apenasAniversariantes = true;
    private String tipoBeneficiario = APOSENTADOS_PENSIONISTAS;


    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public boolean isApenasAniversariantes() {
        return apenasAniversariantes;
    }

    public void setApenasAniversariantes(boolean apenasAniversariantes) {
        this.apenasAniversariantes = apenasAniversariantes;
    }

    public String getTipoBeneficiario() {
        return tipoBeneficiario;
    }

    public void setTipoBeneficiario(String tipoBeneficiario) {
        this.tipoBeneficiario = tipoBeneficiario;
    }

    public void validarGeracao() {
        ValidacaoException ve = new ValidacaoException();
        if (StringUtils.isBlank(mes)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o mês de referência.");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o ano de referência.");
        }
        int mesInteiro = Integer.parseInt(mes);
        if (mesInteiro < 1 || mesInteiro > 12) {
            ve.adicionarMensagemDeCampoObrigatorio("O mês informado é inválido !");
        }

        if (ve.temMensagens()) throw ve;
    }

}
