/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.*;

import java.util.Date;

/**
 * @author gecen
 */
public interface EntidadePagavelRH {

    Long getId();

    Long getIdCalculo();

    MatriculaFP getMatriculaFP();

    CBO getCbo();

    FolhaCalculavel getFolha();

    ContratoFP getContratoFP();

    Integer getAno();

    Integer getMes();

    FichaFinanceiraFP getFicha();

    boolean isCalculandoRetroativo();

    boolean getPrimeiroContrato();

    void setPrimeiroContrato(boolean primeiroContrato);

    void setFolha(FolhaCalculavel folhaDePagamento);

    void setFicha(FichaFinanceiraFP ficha);

    void setAno(Integer ano);

    void setMes(Integer mes);

    void setCalculandoRetroativo(boolean calculando);

    Cargo getCargo();

    Date getFinalVigencia();

}
