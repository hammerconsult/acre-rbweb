package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.FichaFinanceiraFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created by peixe on 06/08/2015.
 */
public class WSFichaFinanceira implements Serializable {

    private Long id;
    private WSVinculoFP wsVinculoFP;
    private Mes mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private String nomePessoa;
    private String autenticidade;
    private Boolean permiteEmitirInformeRendimentos;

    public WSFichaFinanceira() {
    }

    public WSFichaFinanceira(Long id, Mes mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento, VinculoFP vinculoFP) {
        this.id = id;
        this.mes = mes;
        this.ano = ano;
        this.wsVinculoFP = new WSVinculoFP(vinculoFP);
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
        this.permiteEmitirInformeRendimentos = false;
    }

    public static List<WSFichaFinanceira> convertFichaFinanceiraToWSFichaFinanceiraList(List<FichaFinanceiraFP> vinculoFPs) {
        List<WSFichaFinanceira> wsVinculoFPList = Lists.newLinkedList();
        for (FichaFinanceiraFP ficha : vinculoFPs) {
            WSFichaFinanceira ws = new WSFichaFinanceira(ficha.getId(), ficha.getFolhaDePagamento().getMes(), ficha.getFolhaDePagamento().getAno(), ficha.getFolhaDePagamento().getTipoFolhaDePagamento(), ficha.getVinculoFP());
            wsVinculoFPList.add(ws);
        }
        return wsVinculoFPList;
    }

    public WSVinculoFP getWsVinculoFP() {
        return wsVinculoFP;
    }

    public void setWsVinculoFP(WSVinculoFP wsVinculoFP) {
        this.wsVinculoFP = wsVinculoFP;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
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

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public String getAutenticidade() {
        return autenticidade;
    }

    public void setAutenticidade(String autenticidade) {
        this.autenticidade = autenticidade;
    }

    public Boolean getPermiteEmitirInformeRendimentos() {
        return permiteEmitirInformeRendimentos;
    }

    public void setPermiteEmitirInformeRendimentos(Boolean permiteEmitirInformeRendimentos) {
        this.permiteEmitirInformeRendimentos = permiteEmitirInformeRendimentos;
    }
}
