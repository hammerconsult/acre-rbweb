/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums.rh.dirf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
public enum DirfReg316 {

    RTRT,
    RTPO,
    RTDP,
    RTIRF,
    RTPP,
    RTPA,
    CJAC,
    CJAA,
    ESRT,
    ESPO,
    ESPP,
    ESDP,
    ESPA,
    ESIR,
    ESDJ,
    INFPC,
    INFPA,
    RIP65,
    RIDAC,
    RIIRP,
    RIAP,
    RIMOG,
    RIVC,
    RIBMR,
    RIO,
    DAJUD;

    private DirfReg316() {
    }

    public static List<DirfReg316> getRegistrosBPFDEC() {
        List<DirfReg316> retorno = new ArrayList<>();
        retorno.add(RTRT);
        retorno.add(RTPO);
        retorno.add(RTDP);
        retorno.add(RTIRF);
        retorno.add(RTPP);
        retorno.add(CJAC);
        retorno.add(CJAA);
        retorno.add(ESRT);
        retorno.add(ESPO);
        retorno.add(ESPP);
        retorno.add(ESDP);
        retorno.add(ESIR);
        retorno.add(ESDJ);
        retorno.add(INFPC);
        retorno.add(INFPA);
        retorno.add(RIP65);
        retorno.add(RIDAC);
        retorno.add(RIIRP);
        retorno.add(RIAP);
        retorno.add(RIMOG);
        retorno.add(RIVC);
        retorno.add(RIBMR);
        retorno.add(RIO);
        retorno.add(DAJUD);
        return retorno;
    }

    public static List<DirfReg316> getRegistrosINFPA() {
        List<DirfReg316> retorno = new ArrayList<>();
        retorno.add(RTPA);
        retorno.add(ESPA);
        return retorno;
    }
}
