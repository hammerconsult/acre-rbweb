package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;

/**
 * Created by mga on 12/06/2017.
 */
public class FiltroRelatorioRh {

    private Integer mes;
    private Integer ano;
    private Integer versao;
    private ModalidadeContratoFP modalidadeContratoFP;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Boolean agruparPorOrgao;
    private Boolean todosOrgao;

    public void inicializarAtributos() {
        this.versao = null;
        this.ano = null;
        this.mes = null;
        this.tipoFolhaDePagamento = null;
        this.modalidadeContratoFP = null;
        this.hierarquiaOrganizacional= null;
        this.agruparPorOrgao = Boolean.FALSE;
        this.todosOrgao= Boolean.TRUE;
    }

    public Boolean getAgruparPorOrgao() {
        return agruparPorOrgao;
    }

    public void setAgruparPorOrgao(Boolean agruparPorOrgao) {
        this.agruparPorOrgao = agruparPorOrgao;
    }

    public Boolean getTodosOrgao() {
        return todosOrgao;
    }

    public void setTodosOrgao(Boolean todosOrgao) {
        this.todosOrgao = todosOrgao;
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

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
