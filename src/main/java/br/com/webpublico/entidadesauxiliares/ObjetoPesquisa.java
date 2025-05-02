package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 25/01/14
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */
public class ObjetoPesquisa implements Serializable {
    private Integer matricula;
    private Integer numero;
    private Integer ano;
    private Integer mes;
    private TipoFolhaDePagamento tipoFolhaDePagamentoWeb;
    private Integer tipoFolhaDePagamentoTurmalina;
    private List<EventoFP> eventosFPList;
    private VinculoFP vinculoFP;
    private boolean consignado;
    private String mesAnoFa;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public String getMesAnoFa() {
        return mesAnoFa != null ? mesAnoFa.replaceAll("\\-", "").trim() : mesAnoFa;
    }

    public void setMesAnoFa(String mesAnoFa) {
        this.mesAnoFa = mesAnoFa;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public boolean isConsignado() {
        return consignado;
    }

    public void setConsignado(boolean consignado) {
        this.consignado = consignado;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
        if(vinculoFP != null){
            this.matricula = Integer.parseInt(vinculoFP.getMatriculaFP().getMatricula());
            this.numero = Integer.parseInt(vinculoFP.getNumero());
        }
    }

    public ObjetoPesquisa() {
        eventosFPList = new LinkedList<>();
    }

    public Integer getMatricula() {
        return matricula;
    }

    public List<EventoFP> getEventosFPList() {
        return eventosFPList;
    }

    public void setEventosFPList(List<EventoFP> eventosFPList) {
        this.eventosFPList = eventosFPList;
    }

    public void setMatricula(Integer matricula) {
        this.matricula = matricula;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamentoWeb() {
        return tipoFolhaDePagamentoWeb;
    }

    public String getTipoFolhaDePagamento() {
        if (tipoFolhaDePagamentoWeb.equals(TipoFolhaDePagamento.NORMAL)) {
            return "1";
        }
        if (tipoFolhaDePagamentoWeb.equals(TipoFolhaDePagamento.SALARIO_13)) {
            return "120";
        }
        if (tipoFolhaDePagamentoWeb.equals(TipoFolhaDePagamento.RESCISAO)) {
            return " 151,153,152,157,154,158,150,156,160,159 ";
        }
        return "1";
    }

    public void setTipoFolhaDePagamentoWeb(TipoFolhaDePagamento tipoFolhaDePagamentoWeb) {
        this.tipoFolhaDePagamentoWeb = tipoFolhaDePagamentoWeb;
    }

    public Integer getTipoFolhaDePagamentoTurmalina() {
        return tipoFolhaDePagamentoTurmalina;
    }

    public void setTipoFolhaDePagamentoTurmalina(Integer tipoFolhaDePagamentoTurmalina) {
        this.tipoFolhaDePagamentoTurmalina = tipoFolhaDePagamentoTurmalina;
    }
}
