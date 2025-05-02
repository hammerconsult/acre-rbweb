package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ServicoUrbano;
import br.com.webpublico.negocios.tributario.auxiliares.AtributoGenerico;
import br.com.webpublico.util.anotacoes.AtributoIntegracaoSit;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static br.com.webpublico.util.anotacoes.AtributoIntegracaoSit.ClasseAtributoGenerico.LOTE;

public class CadastroImobiliarioIntegracaoSit implements Serializable {


    @AtributoIntegracaoSit(nome = "distrito")
    private Integer distrito;
    @AtributoIntegracaoSit(nome = "setor")
    private Integer setor;
    @AtributoIntegracaoSit(nome = "quadra")
    private Integer quadra;
    @AtributoIntegracaoSit(nome = "lote")
    private Integer lote;
    @AtributoIntegracaoSit(nome = "loteamento_id")
    private Integer loteamentoId;
    @AtributoIntegracaoSit(nome = "loteamento_lote")
    private String loteamentoLote;
    @AtributoIntegracaoSit(nome = "loteamento_quadra")
    private String loteamentoQuadra;
    @AtributoIntegracaoSit(nome = "loteamento_nome")
    private String loteamentoNome;
    @AtributoIntegracaoSit(nome = "area_terreno")
    private BigDecimal areaTerreno;
    @AtributoIntegracaoSit(nome = "topografia", atributoGenerico = "topografia", classe = LOTE)
    private Integer topografia;
    @AtributoIntegracaoSit(nome = "pedologia", atributoGenerico = "pedologia", classe = LOTE)
    private Integer pedologia;
    @AtributoIntegracaoSit(nome = "passeio", atributoGenerico = "passeio", classe = LOTE)
    private Integer passeio;
    @AtributoIntegracaoSit(nome = "limitacao", atributoGenerico = "limitacao", classe = LOTE)
    private Integer limitacao;
    @AtributoIntegracaoSit(nome = "situacao", atributoGenerico = "situacao", classe = LOTE)
    private Integer situacao;
    @AtributoIntegracaoSit(nome = "nat_juridica", atributoGenerico = "natureza_juridica", classe = LOTE)
    private Integer natJuridica;
    @AtributoIntegracaoSit(nome = "patrimonio", atributoGenerico = "patrimonio", classe = LOTE)
    private Integer patrimonio;
    @AtributoIntegracaoSit(nome = "arvores_frutiferas", atributoGenerico = "arvores_frutiferas", classe = LOTE)
    private Integer arvoresFrutiferas;
    @AtributoIntegracaoSit(nome = "horta", atributoGenerico = "horta", classe = LOTE)
    private Integer horta;
    @AtributoIntegracaoSit(nome = "coletores_de_lixo", atributoGenerico = "coletores_lixo", classe = LOTE)
    private Integer coletoresDeLixo;
    @AtributoIntegracaoSit(nome = "revisao")
    private Integer revisao;
    private List<TestadaIntegracaoSit> testadas;
    private List<AutonomaIntegracaoSit> autonomas;

    public CadastroImobiliarioIntegracaoSit() {
        testadas = Lists.newArrayList();
        autonomas = Lists.newArrayList();
    }

    public void addTestada(TestadaIntegracaoSit testada) {
        testada.setCadastroSit(this);
        testadas.add(testada);
    }

    public void addAutonoma(AutonomaIntegracaoSit autonoma) {
        autonoma.setCadastroSit(this);
        autonomas.add(autonoma);
    }

    public BigDecimal getAreaTerreno() {
        return areaTerreno;
    }

    public void setAreaTerreno(BigDecimal areaTerreno) {
        this.areaTerreno = areaTerreno;
    }


    public Integer getArvoresFrutiferas() {
        return arvoresFrutiferas;
    }

    public void setArvoresFrutiferas(Integer arvoresFrutiferas) {
        this.arvoresFrutiferas = arvoresFrutiferas;
    }


    public Integer getColetoresDeLixo() {
        return coletoresDeLixo;
    }

    public void setColetoresDeLixo(Integer coletoresDeLixo) {
        this.coletoresDeLixo = coletoresDeLixo;
    }


    public Integer getDistrito() {
        return distrito;
    }

    public void setDistrito(Integer distrito) {
        this.distrito = distrito;
    }


    public Integer getHorta() {
        return horta;
    }

    public void setHorta(Integer horta) {
        this.horta = horta;
    }


    public Integer getLote() {
        return lote;
    }

    public void setLote(Integer lote) {
        this.lote = lote;
    }

    public Integer getLoteamentoId() {
        return loteamentoId;
    }

    public void setLoteamentoId(Integer loteamentoId) {
        this.loteamentoId = loteamentoId;
    }

    public String getLoteamentoLote() {
        return loteamentoLote;
    }

    public void setLoteamentoLote(String loteamentoLote) {
        this.loteamentoLote = loteamentoLote;
    }

    public String getLoteamentoQuadra() {
        return loteamentoQuadra;
    }

    public void setLoteamentoQuadra(String loteamentoQuadra) {
        this.loteamentoQuadra = loteamentoQuadra;
    }

    public Integer getNatJuridica() {
        return natJuridica;
    }

    public void setNatJuridica(Integer natJuridica) {
        this.natJuridica = natJuridica;
    }


    public Integer getPasseio() {
        return passeio;
    }

    public void setPasseio(Integer passeio) {
        this.passeio = passeio;
    }

    public Integer getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(Integer patrimonio) {
        this.patrimonio = patrimonio;
    }


    public void setPedologia(Integer pedologia) {
        this.pedologia = pedologia;
    }

    public Integer getSetor() {
        return setor;
    }

    public void setSetor(Integer setor) {
        this.setor = setor;
    }

    public Integer getQuadra() {
        return quadra;
    }

    public void setQuadra(Integer quadra) {
        this.quadra = quadra;
    }

    public String getLoteamentoNome() {
        return loteamentoNome;
    }

    public void setLoteamentoNome(String loteamentoNome) {
        this.loteamentoNome = loteamentoNome;
    }

    public Integer getTopografia() {
        return topografia;
    }

    public void setTopografia(Integer topografia) {
        this.topografia = topografia;
    }

    public Integer getPedologia() {
        return pedologia;
    }

    public Integer getLimitacao() {
        return limitacao;
    }

    public void setLimitacao(Integer limitacao) {
        this.limitacao = limitacao;
    }

    public Integer getSituacao() {
        return situacao;
    }

    public void setSituacao(Integer situacao) {
        this.situacao = situacao;
    }

    public Integer getRevisao() {
        return revisao;
    }

    public void setRevisao(Integer revisao) {
        this.revisao = revisao;
    }


    public List<TestadaIntegracaoSit> getTestadas() {
        return testadas;
    }

    public List<AutonomaIntegracaoSit> getAutonomas() {
        return autonomas;
    }

    public String getDescricaoLoteamento() {
        return "Nome: " + (getLoteamentoNome() != null ? getLoteamentoNome() : "")
            + " Q: " + (getLoteamentoQuadra() != null ? getLoteamentoQuadra() : "")
            + " L: " + (getLoteamentoLote() != null ? getLoteamentoLote() : "");
    }
}
