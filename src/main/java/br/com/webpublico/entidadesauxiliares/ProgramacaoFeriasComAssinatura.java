package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by mateus on 19/09/17.
 */
public class ProgramacaoFeriasComAssinatura implements Serializable {

    private String orgao;
    private String matricula;
    private String nomeServidor;
    private String vinculo;
    private Date dataAdmissao;
    private List<ProgramacaoFeriasComAssinaturaPeriodoAquisitivo> periodosAquisitivos;
    private Date dataPrevista;
    private String lotacao;

    public ProgramacaoFeriasComAssinatura() {
        periodosAquisitivos = Lists.newArrayList();
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNomeServidor() {
        return nomeServidor;
    }

    public void setNomeServidor(String nomeServidor) {
        this.nomeServidor = nomeServidor;
    }

    public String getVinculo() {
        return vinculo;
    }

    public void setVinculo(String vinculo) {
        this.vinculo = vinculo;
    }

    public Date getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(Date dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public List<ProgramacaoFeriasComAssinaturaPeriodoAquisitivo> getPeriodosAquisitivos() {
        return periodosAquisitivos;
    }

    public void setPeriodosAquisitivos(List<ProgramacaoFeriasComAssinaturaPeriodoAquisitivo> periodosAquisitivos) {
        this.periodosAquisitivos = periodosAquisitivos;
    }

    public Date getDataPrevista() {
        return dataPrevista;
    }

    public void setDataPrevista(Date dataPrevista) {
        this.dataPrevista = dataPrevista;
    }

    public String getLotacao() {
        return lotacao;
    }

    public void setLotacao(String lotacao) {
        this.lotacao = lotacao;
    }
}
