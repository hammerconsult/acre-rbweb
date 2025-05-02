/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.FichaFinanceiraFP;

import java.util.List;

/**
 * @author boy
 */
public class RegistrosArquivoRetorno {

    private String registro;
    private List<String> erros;
    private FichaFinanceiraFP fichaFinanceira;
    //private ContratoFP contratoFP;
    //private String mes;
    //private String ano;

    public List<String> getErros() {
        return erros;
    }

    public void setErros(List<String> erros) {
        this.erros = erros;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

//    public ContratoFP getContratoFP() {
//        return contratoFP;
//    }

//    public void setContratoFP(ContratoFP contratoFP) {
//        this.contratoFP = contratoFP;
//    }

//    public String getAno() {
//        return ano;
//    }

//    public void setAno(String ano) {
//        this.ano = ano;
//    }

//    public String getMes() {
//        return mes;
//    }

//    public void setMes(String mes) {
//        this.mes = mes;
//    }

    public FichaFinanceiraFP getFichaFinanceira() {
        return fichaFinanceira;
    }

    public void setFichaFinanceira(FichaFinanceiraFP fichaFinanceira) {
        this.fichaFinanceira = fichaFinanceira;
    }
}
