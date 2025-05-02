/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.MoedaFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Renato
 */
@ManagedBean
public class ConverterRealUfm {

    @EJB
    private MoedaFacade moedaFacade;

    public BigDecimal converterToUFMVigente(BigDecimal value) throws UFMException {
        if (value != null) {
            BigDecimal valorUFMVigente = moedaFacade.recuperaValorVigenteUFM();
            if (!valorUFMVigente.equals(BigDecimal.ZERO)) {
                return value.divide(valorUFMVigente, 4, RoundingMode.HALF_EVEN);
            } else {
                throw new UFMException(" Atenção! Não foi encontrado valor UFM para a data " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            }
        }
        return new BigDecimal(BigInteger.ZERO);
    }

    public BigDecimal converterToUFMComData(BigDecimal value, Date date) throws UFMException {
        BigDecimal valorUFMVigente = moedaFacade.recuperaValorUFMParaData(date);
        if (!valorUFMVigente.equals(BigDecimal.ZERO)) {
            return value.divide(valorUFMVigente, 4, RoundingMode.HALF_EVEN);
        } else {
            throw new UFMException(" Atenção! Não foi encontrado valor UFM para a data " + new SimpleDateFormat("dd/MM/yyyy").format(date));
        }
    }

    public BigDecimal converterToUFMParaExercicioSemMensagem(BigDecimal value, Exercicio exercicio)  {
        try {
            return moedaFacade.converterToUFMParaExercicio(value, exercicio);
        } catch (UFMException e) {
            return new BigDecimal(BigInteger.ZERO);
        }
    }

    public BigDecimal converterToUFMParaExercicio(BigDecimal value, Exercicio exercicio) throws UFMException {
        try {
            return moedaFacade.converterToUFMParaExercicio(value, exercicio);
        } catch (UFMException e) {
            FacesUtil.addError("", e.getMessage());
            return new BigDecimal(BigInteger.ZERO);
        }
    }

    public BigDecimal converterToReal(BigDecimal valueUFM) throws UFMException {
        BigDecimal valorUFMVigente = moedaFacade.recuperaValorVigenteUFM();
        if (valorUFMVigente.equals(BigDecimal.ZERO)) {
            throw new UFMException(" Atenção! Não foi encontrado valor UFM para a data " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        } else {
            return valorUFMVigente.multiply(valueUFM).setScale(2, RoundingMode.HALF_EVEN);
        }
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public void setMoedaFacade(MoedaFacade moedaFacade) {
        this.moedaFacade = moedaFacade;
    }
}
