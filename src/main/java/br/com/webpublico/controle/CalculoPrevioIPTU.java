/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.negocios.CalculoIptuBean;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author munif
 */
@ManagedBean
public class CalculoPrevioIPTU implements Serializable {

    @EJB
    private CalculoIptuBean calculoIptuBean;
    private String inscricaoCadastral = "100100020239001";
    private String formulaValorVenalLote = "ufmrb*f.pontuacao(lote,topografia)*f.pontuacao(lote,pedologia)*f.pontuacao(lote,situacao)*cadastroimobiliario.lote.areaLote*testada.face.valor";
    private BigDecimal valorVenalLote;

    public CalculoIptuBean getCalculoIptuBean() {
        return calculoIptuBean;
    }

    public String getFormulaValorVenalLote() {
        return formulaValorVenalLote;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public BigDecimal getValorVenalLote() {
        return valorVenalLote;
    }

//    public List<String> getAtributosContexto() {
//        return calculoIptuBean.getAtributosContexto();
//    }


}
