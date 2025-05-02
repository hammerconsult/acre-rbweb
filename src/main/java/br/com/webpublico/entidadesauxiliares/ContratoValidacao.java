package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoAjusteDadosCadastrais;
import br.com.webpublico.util.Util;

import java.util.Date;

public class ContratoValidacao {

    private Long id;
    private String numeroTermo;
    private Exercicio exercicioContrato;
    private Date dataAprovacao;
    private Date dataDeferimento;
    private Date dataAssinatura;
    private HierarquiaOrganizacional unidadeAdministrativa;
    private Contrato contrato;

    public ContratoValidacao(Contrato contrato) {
        this.contrato = contrato;
        this.id = contrato.getId();
        this.numeroTermo = contrato.getNumeroTermo();
        this.exercicioContrato = contrato.getExercicioContrato();
        this.dataAprovacao = contrato.getDataAprovacao();
        this.dataDeferimento = contrato.getDataDeferimento();
        this.dataAssinatura = contrato.getDataAssinatura();
        this.unidadeAdministrativa = contrato.getUnidadeAdministrativa();
    }

    public ContratoValidacao(AjusteContrato ajuste) {
        AjusteContratoDadosCadastrais ajusteDados = ajuste.getAjusteContratoDadosCadastraisDe(TipoAjusteDadosCadastrais.ALTERACAO);
        this.contrato = ajuste.getContrato();
        this.id = contrato.getId();
        this.numeroTermo = !Util.isStringNulaOuVazia(ajusteDados.getNumeroTermo()) ? ajusteDados.getNumeroTermo() : this.contrato.getNumeroTermo();
        this.exercicioContrato = Util.isNotNull(ajusteDados.getExercicio()) ? ajusteDados.getExercicio() : this.contrato.getExercicioContrato();
        this.dataAprovacao = Util.isNotNull(ajusteDados.getDataAprovacao()) ? ajusteDados.getDataAprovacao() : this.contrato.getDataAprovacao();
        this.dataDeferimento = Util.isNotNull(ajusteDados.getDataDeferimento()) ? ajusteDados.getDataDeferimento() : this.contrato.getDataDeferimento();
        this.dataAssinatura = Util.isNotNull(ajusteDados.getDataAssinatura()) ? ajusteDados.getDataAssinatura() : this.contrato.getDataAssinatura();
        this.unidadeAdministrativa = this.contrato.getUnidadeAdministrativa();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroTermo() {
        return numeroTermo;
    }

    public void setNumeroTermo(String numeroTermo) {
        this.numeroTermo = numeroTermo;
    }

    public Exercicio getExercicioContrato() {
        return exercicioContrato;
    }

    public void setExercicioContrato(Exercicio exercicioContrato) {
        this.exercicioContrato = exercicioContrato;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public Date getDataDeferimento() {
        return dataDeferimento;
    }

    public void setDataDeferimento(Date dataDeferimento) {
        this.dataDeferimento = dataDeferimento;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public HierarquiaOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(HierarquiaOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }
}
