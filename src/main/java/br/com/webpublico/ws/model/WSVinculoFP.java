package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.VinculoFP;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by peixe on 06/08/2015.
 */
public class WSVinculoFP implements Serializable {

    private Long id;
    private String matricula;
    private String numero;
    private String tipoVinculo;
    private String situacao;

    public WSVinculoFP() {
    }

    public WSVinculoFP(VinculoFP vinculoFP) {
        this.id = vinculoFP.getId();
        this.matricula = vinculoFP.getMatriculaFP().getMatricula();
        this.numero = vinculoFP.getNumero();
    }

    public static List<WSVinculoFP> convertVinculoFPToWSVinculoFPList(List<VinculoFP> vinculoFPs) {
        List<WSVinculoFP> wsVinculoFPList = new ArrayList<>();
        for (VinculoFP vinculoFP : vinculoFPs) {
            WSVinculoFP ws = new WSVinculoFP(vinculoFP);
            wsVinculoFPList.add(ws);
        }
        return wsVinculoFPList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipoVinculo() {
        return tipoVinculo;
    }

    public void setTipoVinculo(String tipoVinculo) {
        this.tipoVinculo = tipoVinculo;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
