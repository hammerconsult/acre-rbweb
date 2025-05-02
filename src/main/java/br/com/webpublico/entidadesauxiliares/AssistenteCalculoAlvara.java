package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.exception.ValidacaoException;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AssistenteCalculoAlvara implements Serializable {
    private ProcessoCalculoAlvara procCalculo;
    private ConfiguracaoTributario configTributario;
    private CadastroEconomico cmc;
    private ClassificacaoAtividade classificacaoAtividade;
    private Exercicio exercicio;
    private BigDecimal areaUtil;
    private Map<Divida, List<ConfiguracaoAlvara>> mapaDividas;
    private boolean isRecalculo;
    private Boolean gerarTaxaLocalizacao;

    public AssistenteCalculoAlvara() {
        this.areaUtil = BigDecimal.ZERO;
        this.mapaDividas = Maps.newHashMap();
        this.gerarTaxaLocalizacao = Boolean.FALSE;
    }

    public AssistenteCalculoAlvara(ProcessoCalculoAlvara processo, CadastroEconomico cmc, Exercicio exercicio) {
        super();
        this.procCalculo = processo;
        this.cmc = cmc;
        this.exercicio = exercicio;
    }

    public ProcessoCalculoAlvara getProcCalculo() {
        return procCalculo;
    }

    public void setProcCalculo(ProcessoCalculoAlvara procCalculo) {
        this.procCalculo = procCalculo;
    }

    public ConfiguracaoTributario getConfigTributario() {
        return configTributario;
    }

    public void setConfigTributario(ConfiguracaoTributario configTributario) {
        this.configTributario = configTributario;
    }

    public CadastroEconomico getCmc() {
        return cmc;
    }

    public void setCmc(CadastroEconomico cmc) {
        this.cmc = cmc;
    }

    public ClassificacaoAtividade getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(ClassificacaoAtividade classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getAreaUtil() {
        return areaUtil;
    }

    public void setAreaUtil(BigDecimal areaUtil) {
        this.areaUtil = areaUtil;
    }

    public Map<Divida, List<ConfiguracaoAlvara>> getMapaDividas() {
        return mapaDividas;
    }

    public void setMapaDividas(Map<Divida, List<ConfiguracaoAlvara>> mapaDividas) {
        this.mapaDividas = mapaDividas;
    }

    public boolean isRecalculo() {
        return isRecalculo;
    }

    public void setRecalculo(boolean recalculo) {
        isRecalculo = recalculo;
    }

    public Boolean getGerarTaxaLocalizacao() {
        return gerarTaxaLocalizacao;
    }

    public void setGerarTaxaLocalizacao(Boolean gerarTaxaLocalizacao) {
        this.gerarTaxaLocalizacao = gerarTaxaLocalizacao;
    }

    public boolean naoGerarTaxaLocalizacao() {
        return gerarTaxaLocalizacao != null && !gerarTaxaLocalizacao;
    }

    public boolean gerarTaxaLocalizacao() {
        return gerarTaxaLocalizacao != null && gerarTaxaLocalizacao;
    }
}
