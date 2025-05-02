package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.interfaces.DetentorAtributoGenerico;
import br.com.webpublico.negocios.tributario.auxiliares.AtributoGenerico;
import br.com.webpublico.util.anotacoes.AtributoIntegracaoSit;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static br.com.webpublico.util.anotacoes.AtributoIntegracaoSit.ClasseAtributoGenerico.CONSTRUCAO;


public class AvaliacaoIntegracaoSit implements Serializable, DetentorAtributoGenerico {

    @AtributoIntegracaoSit(nome = "tipo_de_construcao", atributoGenerico = "tipo_construcao", classe = CONSTRUCAO)
    private Integer tipoDeConstrucao;
    @AtributoIntegracaoSit(nome = "tipo_de_imovel", atributoGenerico = "tipo_imovel", classe = CONSTRUCAO)
    private Integer tipoDeImovel;
    @AtributoIntegracaoSit(nome = "utilizacao_do_imovel", atributoGenerico = "utilizacao_imovel", classe = CONSTRUCAO)
    private Integer utilizacaoDoImovel;
    @AtributoIntegracaoSit(nome = "revest_fachada_principal", atributoGenerico = "revestimento_fachada_principal", classe = CONSTRUCAO)
    private Integer revestFachadaPrincipal;
    @AtributoIntegracaoSit(nome = "qualidade_construcao", atributoGenerico = "qualidade_construcao", classe = CONSTRUCAO)
    private Integer qualidadeConstrucao;
    @AtributoIntegracaoSit(nome = "piscina", atributoGenerico = "piscina", classe = CONSTRUCAO)
    private Integer piscina;
    @AtributoIntegracaoSit(nome = "piso", atributoGenerico = "piso", classe = CONSTRUCAO)
    private Integer piso;
    @AtributoIntegracaoSit(nome = "padrao_residencial", atributoGenerico = "padrao_residencial", classe = CONSTRUCAO)
    private Integer padraoResidencial;
    @AtributoIntegracaoSit(nome = "parede", atributoGenerico = "parede", classe = CONSTRUCAO)
    private Integer parede;
    @AtributoIntegracaoSit(nome = "area_de_esporte", atributoGenerico = "area_esporte", classe = CONSTRUCAO)
    private Integer areaDeEsporte;
    @AtributoIntegracaoSit(nome = "cobertura", atributoGenerico = "cobertura", classe = CONSTRUCAO)
    private Integer cobertura;
    @AtributoIntegracaoSit(nome = "comercial", atributoGenerico = "comercial", classe = CONSTRUCAO)
    private Integer comercial;
    @AtributoIntegracaoSit(nome = "estrutura", atributoGenerico = "estrutura", classe = CONSTRUCAO)
    private Integer estrutura;
    @AtributoIntegracaoSit(nome = "forro", atributoGenerico = "forro", classe = CONSTRUCAO)
    private Integer forro;
    @AtributoIntegracaoSit(nome = "instalacao_eletrica", atributoGenerico = "instalacao_eletrica", classe = CONSTRUCAO)
    private Integer instalacaoEletrica;
    @AtributoIntegracaoSit(nome = "instalacao_sanitaria", atributoGenerico = "instalacao_sanitaria", classe = CONSTRUCAO)
    private Integer instalacaoSanitaria;
    @AtributoIntegracaoSit(nome = "ano_alvara")
    private Integer anoAlvara;
    @AtributoIntegracaoSit(nome = "data_emissao_alvara")
    private Date dataEmissaoAlvara;
    @AtributoIntegracaoSit(nome = "numero_alvara")
    private String numeroAlvara;
    @AtributoIntegracaoSit(nome = "tipo_alvara")
    private String tipoAlvara;
    @AtributoIntegracaoSit(nome = "ano_habitese")
    private Integer anoHabitese;
    @AtributoIntegracaoSit(nome = "data_emissao_habitese")
    private Date dataEmissaoHabitese;
    @AtributoIntegracaoSit(nome = "numero_habitese")
    private String numeroHabitese;
    @AtributoIntegracaoSit(nome = "pavimentos")
    private Integer pavimentos;
    @AtributoIntegracaoSit(nome = "avaliacao")
    private Integer avaliacao;
    @AtributoIntegracaoSit(nome = "avaliacao_cancelada")
    private Boolean avaliacaoCancelada;
    @AtributoIntegracaoSit(nome = "area_construida")
    private BigDecimal areaConstruida;
    private AutonomaIntegracaoSit autonoma;
    private Map<AtributoIntegracaoSit.ClasseAtributoGenerico, List<AtributoGenerico>> atributos;

    public AvaliacaoIntegracaoSit() {
        atributos = Maps.newHashMap();
    }


    public AutonomaIntegracaoSit getAutonoma() {
        return autonoma;
    }

    public void setAutonoma(AutonomaIntegracaoSit autonoma) {
        this.autonoma = autonoma;
    }

    public String getNumeroAnoAlvara() {
        return (numeroAlvara != null ? numeroAlvara : " ") + "/" + (anoAlvara != null ? anoAlvara : "");
    }

    public String getNumeroAnoHabitese() {
        return (numeroHabitese != null ? numeroHabitese : " ") + "/" + (anoHabitese != null ? anoHabitese : "");
    }

    public Integer getTipoDeConstrucao() {
        return tipoDeConstrucao;
    }

    public void setTipoDeConstrucao(Integer tipoDeConstrucao) {
        this.tipoDeConstrucao = tipoDeConstrucao;
    }

    public Integer getTipoDeImovel() {
        return tipoDeImovel;
    }

    public void setTipoDeImovel(Integer tipoDeImovel) {
        this.tipoDeImovel = tipoDeImovel;
    }

    public Integer getUtilizacaoDoImovel() {
        return utilizacaoDoImovel;
    }

    public void setUtilizacaoDoImovel(Integer utilizacaoDoImovel) {
        this.utilizacaoDoImovel = utilizacaoDoImovel;
    }

    public Integer getRevestFachadaPrincipal() {
        return revestFachadaPrincipal;
    }

    public void setRevestFachadaPrincipal(Integer revestFachadaPrincipal) {
        this.revestFachadaPrincipal = revestFachadaPrincipal;
    }

    public Integer getQualidadeConstrucao() {
        return qualidadeConstrucao;
    }

    public void setQualidadeConstrucao(Integer qualidadeConstrucao) {
        this.qualidadeConstrucao = qualidadeConstrucao;
    }

    public Integer getPiscina() {
        return piscina;
    }

    public void setPiscina(Integer piscina) {
        this.piscina = piscina;
    }

    public Integer getPiso() {
        return piso;
    }

    public void setPiso(Integer piso) {
        this.piso = piso;
    }

    public Integer getPadraoResidencial() {
        return padraoResidencial;
    }

    public void setPadraoResidencial(Integer padraoResidencial) {
        this.padraoResidencial = padraoResidencial;
    }

    public Integer getParede() {
        return parede;
    }

    public void setParede(Integer parede) {
        this.parede = parede;
    }

    public Integer getAreaDeEsporte() {
        return areaDeEsporte;
    }

    public void setAreaDeEsporte(Integer areaDeEsporte) {
        this.areaDeEsporte = areaDeEsporte;
    }

    public Integer getCobertura() {
        return cobertura;
    }

    public void setCobertura(Integer cobertura) {
        this.cobertura = cobertura;
    }

    public Integer getComercial() {
        return comercial;
    }

    public void setComercial(Integer comercial) {
        this.comercial = comercial;
    }

    public Integer getEstrutura() {
        return estrutura;
    }

    public void setEstrutura(Integer estrutura) {
        this.estrutura = estrutura;
    }

    public Integer getForro() {
        return forro;
    }

    public void setForro(Integer forro) {
        this.forro = forro;
    }

    public Integer getInstalacaoEletrica() {
        return instalacaoEletrica;
    }

    public void setInstalacaoEletrica(Integer instalacaoEletrica) {
        this.instalacaoEletrica = instalacaoEletrica;
    }

    public Integer getInstalacaoSanitaria() {
        return instalacaoSanitaria;
    }

    public void setInstalacaoSanitaria(Integer instalacaoSanitaria) {
        this.instalacaoSanitaria = instalacaoSanitaria;
    }

    public Integer getAnoAlvara() {
        return anoAlvara;
    }

    public void setAnoAlvara(Integer anoAlvara) {
        this.anoAlvara = anoAlvara;
    }

    public Date getDataEmissaoAlvara() {
        return dataEmissaoAlvara;
    }

    public void setDataEmissaoAlvara(Date dataEmissaoAlvara) {
        this.dataEmissaoAlvara = dataEmissaoAlvara;
    }

    public String getNumeroAlvara() {
        return numeroAlvara;
    }

    public void setNumeroAlvara(String numeroAlvara) {
        this.numeroAlvara = numeroAlvara;
    }

    public String getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(String tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public Integer getAnoHabitese() {
        return anoHabitese;
    }

    public void setAnoHabitese(Integer anoHabitese) {
        this.anoHabitese = anoHabitese;
    }

    public Date getDataEmissaoHabitese() {
        return dataEmissaoHabitese;
    }

    public void setDataEmissaoHabitese(Date dataEmissaoHabitese) {
        this.dataEmissaoHabitese = dataEmissaoHabitese;
    }

    public String getNumeroHabitese() {
        return numeroHabitese;
    }

    public void setNumeroHabitese(String numeroHabitese) {
        this.numeroHabitese = numeroHabitese;
    }

    public Integer getPavimentos() {
        return pavimentos;
    }

    public void setPavimentos(Integer pavimentos) {
        this.pavimentos = pavimentos;
    }

    public Integer getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Integer avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Boolean getAvaliacaoCancelada() {
        return avaliacaoCancelada;
    }

    public void setAvaliacaoCancelada(Boolean avaliacaoCancelada) {
        this.avaliacaoCancelada = avaliacaoCancelada;
    }

    public BigDecimal getAreaConstruida() {
        return areaConstruida;
    }

    public void setAreaConstruida(BigDecimal areaConstruida) {
        this.areaConstruida = areaConstruida;
    }

    @Override
    public Map<AtributoIntegracaoSit.ClasseAtributoGenerico, List<AtributoGenerico>> getAtributos() {
        return atributos;
    }
}
