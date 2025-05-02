package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * Created by mga on 02/06/2017.
 */
public class ServidorInfo {

    private Integer item;
    private String servidor;
    private String idade;
    private Date dataNascimento;
    private Date admissao;
    private Date finalVigencia;
    private String modalidadeContrato;
    private String regimeJuridico;
    private String cargoContrato;
    private String unidadeAdministrativa;
    private String recursoVinculo;

    private List<ServidorInfo> unidadesAdministrativas;
    private List<ServidorInfo> recursosVinculo;
    private List<ServidorInfoFalta> faltas;
    private List<ServidorInfoConcessaoFerias> concessaoFerias;
    private List<ServidorInfoConcessaoFerias> licencasPremio;
    private List<ServidorInfoAfastamento> afastamentos;
    private List<ServidorInfoExperienciaExtraCurricular> experienciasExtraCurricular;
    private List<ServidorInfoPeriodoAquisitivo> periodoAquisitivoSemConcessaoFerias;
    private List<ServidorInfoDependente> dependentes;
    private List<ServidorInfoDependentePensaoAlimenticia> dependentesPensaoAlimenticia;
    private List<ServidorInfoCargoComissao> cargosComissao;
    private List<ServidorInfoFuncaoGratificada> funcoesGratificadas;
    private List<ServidorInfoEnquadramentoFuncional> enquadramentosFuncionais;
    private List<ServidorInfoHorasExtras> horasExtras;
    private List<ServidorInfoPenalidades> penalidades;


    public ServidorInfo() {
        unidadesAdministrativas = Lists.newArrayList();
        recursosVinculo = Lists.newArrayList();
        faltas = Lists.newArrayList();
        concessaoFerias = Lists.newArrayList();
        licencasPremio = Lists.newArrayList();
        afastamentos = Lists.newArrayList();
        experienciasExtraCurricular = Lists.newArrayList();
        periodoAquisitivoSemConcessaoFerias = Lists.newArrayList();
        dependentes = Lists.newArrayList();
        dependentesPensaoAlimenticia = Lists.newArrayList();
        cargosComissao = Lists.newArrayList();
        funcoesGratificadas = Lists.newArrayList();
        enquadramentosFuncionais = Lists.newArrayList();
        horasExtras = Lists.newArrayList();
        penalidades = Lists.newArrayList();
    }

    public List<ServidorInfoPenalidades> getPenalidades() {
        return penalidades;
    }

    public void setPenalidades(List<ServidorInfoPenalidades> penalidades) {
        this.penalidades = penalidades;
    }

    public List<ServidorInfoConcessaoFerias> getLicencasPremio() {
        return licencasPremio;
    }

    public void setLicencasPremio(List<ServidorInfoConcessaoFerias> licencasPremio) {
        this.licencasPremio = licencasPremio;
    }

    public List<ServidorInfoHorasExtras> getHorasExtras() {
        return horasExtras;
    }

    public void setHorasExtras(List<ServidorInfoHorasExtras> horasExtras) {
        this.horasExtras = horasExtras;
    }

    public List<ServidorInfoEnquadramentoFuncional> getEnquadramentosFuncionais() {
        return enquadramentosFuncionais;
    }

    public void setEnquadramentosFuncionais(List<ServidorInfoEnquadramentoFuncional> enquadramentosFuncionais) {
        this.enquadramentosFuncionais = enquadramentosFuncionais;
    }

    public List<ServidorInfoFuncaoGratificada> getFuncoesGratificadas() {
        return funcoesGratificadas;
    }

    public void setFuncoesGratificadas(List<ServidorInfoFuncaoGratificada> funcoesGratificadas) {
        this.funcoesGratificadas = funcoesGratificadas;
    }

    public List<ServidorInfoCargoComissao> getCargosComissao() {
        return cargosComissao;
    }

    public void setCargosComissao(List<ServidorInfoCargoComissao> cargosComissao) {
        this.cargosComissao = cargosComissao;
    }

    public List<ServidorInfoDependentePensaoAlimenticia> getDependentesPensaoAlimenticia() {
        return dependentesPensaoAlimenticia;
    }

    public void setDependentesPensaoAlimenticia(List<ServidorInfoDependentePensaoAlimenticia> dependentesPensaoAlimenticia) {
        this.dependentesPensaoAlimenticia = dependentesPensaoAlimenticia;
    }

    public List<ServidorInfoDependente> getDependentes() {
        return dependentes;
    }

    public void setDependentes(List<ServidorInfoDependente> dependentes) {
        this.dependentes = dependentes;
    }

    public List<ServidorInfoPeriodoAquisitivo> getPeriodoAquisitivoSemConcessaoFerias() {
        return periodoAquisitivoSemConcessaoFerias;
    }

    public void setPeriodoAquisitivoSemConcessaoFerias(List<ServidorInfoPeriodoAquisitivo> periodoAquisitivoSemConcessaoFerias) {
        this.periodoAquisitivoSemConcessaoFerias = periodoAquisitivoSemConcessaoFerias;
    }

    public List<ServidorInfoExperienciaExtraCurricular> getExperienciasExtraCurricular() {
        return experienciasExtraCurricular;
    }

    public void setExperienciasExtraCurricular(List<ServidorInfoExperienciaExtraCurricular> experienciasExtraCurricular) {
        this.experienciasExtraCurricular = experienciasExtraCurricular;
    }

    public List<ServidorInfoAfastamento> getAfastamentos() {
        return afastamentos;
    }

    public void setAfastamentos(List<ServidorInfoAfastamento> afastamentos) {
        this.afastamentos = afastamentos;
    }

    public List<ServidorInfoConcessaoFerias> getConcessaoFerias() {
        return concessaoFerias;
    }

    public void setConcessaoFerias(List<ServidorInfoConcessaoFerias> concessaoFerias) {
        this.concessaoFerias = concessaoFerias;
    }

    public List<ServidorInfoFalta> getFaltas() {
        return faltas;
    }

    public void setFaltas(List<ServidorInfoFalta> faltas) {
        this.faltas = faltas;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public Date getAdmissao() {
        return admissao;
    }

    public void setAdmissao(Date admissao) {
        this.admissao = admissao;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public String getModalidadeContrato() {
        return modalidadeContrato;
    }

    public void setModalidadeContrato(String modalidadeContrato) {
        this.modalidadeContrato = modalidadeContrato;
    }

    public String getRegimeJuridico() {
        return regimeJuridico;
    }

    public void setRegimeJuridico(String regimeJuridico) {
        this.regimeJuridico = regimeJuridico;
    }

    public String getCargoContrato() {
        return cargoContrato;
    }

    public void setCargoContrato(String cargoContrato) {
        this.cargoContrato = cargoContrato;
    }

    public String getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(String unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public String getRecursoVinculo() {
        return recursoVinculo;
    }

    public void setRecursoVinculo(String recursoVinculo) {
        this.recursoVinculo = recursoVinculo;
    }

    public List<ServidorInfo> getUnidadesAdministrativas() {
        return unidadesAdministrativas;
    }

    public void setUnidadesAdministrativas(List<ServidorInfo> unidadesAdministrativas) {
        this.unidadesAdministrativas = unidadesAdministrativas;
    }

    public List<ServidorInfo> getRecursosVinculo() {
        return recursosVinculo;
    }

    public void setRecursosVinculo(List<ServidorInfo> recursosVinculo) {
        this.recursosVinculo = recursosVinculo;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }
}
