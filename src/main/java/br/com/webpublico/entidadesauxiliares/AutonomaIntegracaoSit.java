package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.interfaces.DetentorAtributoGenerico;
import br.com.webpublico.negocios.tributario.auxiliares.AtributoGenerico;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.AtributoIntegracaoSit;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AutonomaIntegracaoSit implements Serializable, DetentorAtributoGenerico {
    @AtributoIntegracaoSit(nome = "autonoma")
    private Integer autonoma;
    @AtributoIntegracaoSit(nome = "autonoma_cancelada")
    private Boolean autonomaCancelada;
    @AtributoIntegracaoSit(nome = "data_cancelamento")
    private Date dataCancelamento;
    @AtributoIntegracaoSit(nome = "area_total_construida")
    private BigDecimal areaTotalConstruida;
    @AtributoIntegracaoSit(nome = "testada_id")
    private Integer testadaId;
    private List<ProprietarioIntegracaoSit> proprietarios;
    @AtributoIntegracaoSit(nome = "endereco_complemento")
    private String enderecoComplemento;
    @AtributoIntegracaoSit(nome = "endereco_numero")
    private String enderecoNumero;
    @AtributoIntegracaoSit(nome = "ano_documento")
    private Integer anoDocumento;
    @AtributoIntegracaoSit(nome = "numero_documento")
    private String numeroDocumento;
    @AtributoIntegracaoSit(nome = "inscricao_anterior")
    private String inscricaoAnterior;
    @AtributoIntegracaoSit(nome = "predial")
    private Boolean predial;
    @AtributoIntegracaoSit(nome = "tipo_documento")
    private String tipoDocumento;
    @AtributoIntegracaoSit(nome = "folha_documento")
    private String folhaDocumento;
    @AtributoIntegracaoSit(nome = "livro_documento")
    private String livroDocumento;
    @AtributoIntegracaoSit(nome = "quantidade_de_avaliacoes")
    private Integer quantidadeDeAvaliacoes;
    private CadastroImobiliarioIntegracaoSit cadastroSit;
    private List<AvaliacaoIntegracaoSit> avaliacoesSits;
    private Map<AtributoIntegracaoSit.ClasseAtributoGenerico, List<AtributoGenerico>> atributos;
    private List<CompromissarioIntegracaoSit> compromissarios;


    public AutonomaIntegracaoSit() {
        atributos = Maps.newHashMap();
        proprietarios = Lists.newArrayList();
        avaliacoesSits = Lists.newArrayList();
        compromissarios = Lists.newArrayList();
    }

    public void addProprietario(ProprietarioIntegracaoSit proprietario) {
        proprietario.setAutonomaSit(this);
        proprietarios.add(proprietario);
    }

    public void addCompromissario(CompromissarioIntegracaoSit compromissario) {
        compromissario.setAutonomaSit(this);
        compromissarios.add(compromissario);
    }

    public void addAvaliacao(AvaliacaoIntegracaoSit avaliacao) {
        avaliacao.setAutonoma(this);
        avaliacoesSits.add(avaliacao);
    }

    public List<AvaliacaoIntegracaoSit> getAvaliacoesSits() {
        return avaliacoesSits;
    }

    public String getInscricaoCadastral() {
        return cadastroSit.getDistrito()
                + StringUtil.preencheString(cadastroSit.getSetor() + "", 3, '0')
                + StringUtil.preencheString(cadastroSit.getQuadra() + "", 4, '0')
                + StringUtil.preencheString(cadastroSit.getLote() + "", 4, '0')
                + StringUtil.preencheString(getAutonoma() + "", 3, '0');

    }

    public String getNumeroAnoDocumento() {
        return (numeroDocumento != null ? numeroDocumento : " ") + "/" + (anoDocumento != null ? anoDocumento : "");
    }


    public Integer getAutonoma() {
        return autonoma;
    }

    public void setAutonoma(Integer autonoma) {
        this.autonoma = autonoma;
    }

    public Boolean getAutonomaCancelada() {
        return autonomaCancelada;
    }

    public void setAutonomaCancelada(Boolean autonomaCancelada) {
        this.autonomaCancelada = autonomaCancelada;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public BigDecimal getAreaTotalConstruida() {
        return areaTotalConstruida;
    }

    public void setAreaTotalConstruida(BigDecimal areaTotalConstruida) {
        this.areaTotalConstruida = areaTotalConstruida;
    }

    public Integer getTestadaId() {
        return testadaId;
    }

    public void setTestadaId(Integer testadaId) {
        this.testadaId = testadaId;
    }

    public List<ProprietarioIntegracaoSit> getProprietarios() {
        return proprietarios;
    }

    public List<CompromissarioIntegracaoSit> getCompromissarios() {
        return compromissarios;
    }

    public void setCompromissarios(List<CompromissarioIntegracaoSit> compromissarios) {
        this.compromissarios = compromissarios;
    }

    public String getEnderecoComplemento() {
        return enderecoComplemento;
    }

    public void setEnderecoComplemento(String enderecoComplemento) {
        this.enderecoComplemento = enderecoComplemento;
    }

    public String getEnderecoNumero() {
        return enderecoNumero;
    }

    public void setEnderecoNumero(String enderecoNumero) {
        this.enderecoNumero = enderecoNumero;
    }

    public Integer getAnoDocumento() {
        return anoDocumento;
    }

    public void setAnoDocumento(Integer anoDocumento) {
        this.anoDocumento = anoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getInscricaoAnterior() {
        return inscricaoAnterior;
    }

    public void setInscricaoAnterior(String inscricaoAnterior) {
        this.inscricaoAnterior = inscricaoAnterior;
    }

    public Boolean getPredial() {
        return predial;
    }

    public void setPredial(Boolean predial) {
        this.predial = predial;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getFolhaDocumento() {
        return folhaDocumento;
    }

    public void setFolhaDocumento(String folhaDocumento) {
        this.folhaDocumento = folhaDocumento;
    }

    public String getLivroDocumento() {
        return livroDocumento;
    }

    public void setLivroDocumento(String livroDocumento) {
        this.livroDocumento = livroDocumento;
    }

    public Integer getQuantidadeDeAvaliacoes() {
        return quantidadeDeAvaliacoes;
    }

    public void setQuantidadeDeAvaliacoes(Integer quantidadeDeAvaliacoes) {
        this.quantidadeDeAvaliacoes = quantidadeDeAvaliacoes;
    }

    public CadastroImobiliarioIntegracaoSit getCadastroSit() {
        return cadastroSit;
    }

    public void setCadastroSit(CadastroImobiliarioIntegracaoSit cadastroSit) {
        this.cadastroSit = cadastroSit;
    }

    public Map<AtributoIntegracaoSit.ClasseAtributoGenerico, List<AtributoGenerico>> getAtributos() {
        return atributos;
    }

}
