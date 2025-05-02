package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import com.google.common.collect.Lists;

import java.util.List;

public class FiltroRelatorioServidoresPorVerbas {

    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private Integer mes;
    private Integer ano;
    private Integer versao;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private EventoFP verba;
    private List<EventoFP> verbas;
    private Boolean desvincularVerbas;
    private Boolean naoExibirRetroacao;
    private TipoEventoFP tipoEventoFP;
    private TipoEventoFP tipoEventoFPFicha;

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public EventoFP getVerba() {
        return verba;
    }

    public void setVerba(EventoFP verba) {
        this.verba = verba;
    }

    public List<EventoFP> getVerbas() {
        return verbas;
    }

    public void setVerbas(List<EventoFP> verbas) {
        this.verbas = verbas;
    }

    public Boolean getDesvincularVerbas() {
        return desvincularVerbas == null ? false : desvincularVerbas;
    }

    public void setDesvincularVerbas(Boolean desvincularVerbas) {
        this.desvincularVerbas = desvincularVerbas;
    }

    public Boolean getNaoExibirRetroacao() {
        return naoExibirRetroacao == null ? false : naoExibirRetroacao;
    }

    public void setNaoExibirRetroacao(Boolean naoExibirRetroacao) {
        this.naoExibirRetroacao = naoExibirRetroacao;
    }

    public TipoEventoFP getTipoEventoFP() {
        return tipoEventoFP;
    }

    public void setTipoEventoFP(TipoEventoFP tipoEventoFP) {
        this.tipoEventoFP = tipoEventoFP;
    }

    public TipoEventoFP getTipoEventoFPFicha() {
        return tipoEventoFPFicha;
    }

    public void setTipoEventoFPFicha(TipoEventoFP tipoEventoFPFicha) {
        this.tipoEventoFPFicha = tipoEventoFPFicha;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (getAno() == null || (getAno() != null && getAno() == 0)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (getTipoFolhaDePagamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha deve ser informado.");
        }
        if (getMes() != null && (getMes() < 1 || getMes() > 12)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (getHierarquiaAdministrativa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        ve.lancarException();
    }

    public void limparCampos() {
        verbas = Lists.newArrayList();
        hierarquiaAdministrativa = null;
        mes = null;
        ano = null;
        tipoFolhaDePagamento = null;
        naoExibirRetroacao = false;
        tipoEventoFP = null;
        tipoEventoFPFicha = null;
    }
}
