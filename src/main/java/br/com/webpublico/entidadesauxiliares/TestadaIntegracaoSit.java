package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.entidades.ServicoUrbano;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.AtributoIntegracaoSit;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class TestadaIntegracaoSit implements Serializable {

    @AtributoIntegracaoSit(nome = "testada_id")
    private Integer testadaId;
    @AtributoIntegracaoSit(nome = "principal")
    private Boolean principal;
    @AtributoIntegracaoSit(nome = "lado")
    private String lado;
    @AtributoIntegracaoSit(nome = "logradouro_id")
    private Integer logradouroId;
    @AtributoIntegracaoSit(nome = "logradouro_nome")
    private String logradouroNome;
    @AtributoIntegracaoSit(nome = "logradouro_tipo")
    private String logradouroTipo;
    @AtributoIntegracaoSit(nome = "logradouro_titulo")
    private String logradouroTitulo;
    @AtributoIntegracaoSit(nome = "sequencial")
    private Integer sequencial;
    @AtributoIntegracaoSit(nome = "codigoface")
    private String codigoFace;
    @AtributoIntegracaoSit(nome = "comprimento")
    private BigDecimal comprimento;
    @AtributoIntegracaoSit(nome = "bairro_id")
    private Integer bairroId;
    @AtributoIntegracaoSit(nome = "bairro_nome")
    private String bairroNome;
    @AtributoIntegracaoSit(nome = "arborizacoes", servicoUrbano = "arborizacao")
    private Integer arborizacoes;
    @AtributoIntegracaoSit(nome = "coleta_de_lixo_alternado", servicoUrbano = "coleta_lixo_alternado")
    private Integer coletaDeLixo;
    @AtributoIntegracaoSit(nome = "coleta_de_lixo_diario", servicoUrbano = "coleta_lixo_diario")
    private Integer coletaDeLixoDiario;
    @AtributoIntegracaoSit(nome = "galerias_pluviais", servicoUrbano = "galerias_pluviais")
    private Integer galeriasPluviais;
    @AtributoIntegracaoSit(nome = "iluminacao_publica", servicoUrbano = "iluminacao_publica")
    private Integer iluminacaoPublica;
    @AtributoIntegracaoSit(nome = "limpeza_publica", servicoUrbano = "limpeza_publica")
    private Integer limpezaPublica;
    @AtributoIntegracaoSit(nome = "pavimentacao", servicoUrbano = "pavimentacao_asfaltica")
    private Integer pavimentacao;
    @AtributoIntegracaoSit(nome = "rede_agua", servicoUrbano = "rede_agua")
    private Integer redeAgua;
    @AtributoIntegracaoSit(nome = "rede_eletrica", servicoUrbano = "rede_eletrica")
    private Integer redeEletrica;
    @AtributoIntegracaoSit(nome = "rede_esgoto", servicoUrbano = "rede_esgoto")
    private Integer redeEsgoto;
    @AtributoIntegracaoSit(nome = "rede_telefonica", servicoUrbano = "rede_telefonica")
    private Integer redeTelefonica;
    @AtributoIntegracaoSit(nome = "sarjetas", servicoUrbano = "sarjetas")
    private Integer sarjetas;
    @AtributoIntegracaoSit(nome = "emplacamento", servicoUrbano = "emplacamento")
    private Integer emplacamento;
    @AtributoIntegracaoSit(nome = "cep")
    private String cep;
    @AtributoIntegracaoSit(nome = "valor_m2")
    private BigDecimal valorM2;
    private CadastroImobiliarioIntegracaoSit cadastroSit;
    private List<ServicoUrbano> servicosUrbanos;

    public TestadaIntegracaoSit() {
        servicosUrbanos = Lists.newArrayList();

    }

    public List<ServicoUrbano> getServicosUrbanos() {
        return servicosUrbanos;
    }

    public void setServicosUrbanos(List<ServicoUrbano> servicosUrbanos) {
        this.servicosUrbanos = servicosUrbanos;
    }

    public String getCodigoFace() {
        return codigoFace != null && !codigoFace.equals("0") ? codigoFace+getLadoSimples() : StringUtil.cortaOuCompletaEsquerda(logradouroId.toString(), 6, "0")
            + StringUtil.cortaOuCompletaEsquerda(sequencial.toString(), 4, "0") + getLadoSimples();
    }

    public CadastroImobiliarioIntegracaoSit getCadastroSit() {
        return cadastroSit;
    }

    public void setCadastroSit(CadastroImobiliarioIntegracaoSit cadastroSit) {
        this.cadastroSit = cadastroSit;
    }

    public void setCodigoFace(String codigoFace) {
        this.codigoFace = codigoFace;
    }

    public Integer getTestadaId() {
        return testadaId;
    }

    public void setTestadaId(Integer testadaId) {
        this.testadaId = testadaId;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }


    public Integer getLogradouroId() {
        return logradouroId;
    }

    public void setLogradouroId(Integer logradouroId) {
        this.logradouroId = logradouroId;
    }

    public String getLogradouroNome() {
        return logradouroNome;
    }

    public void setLogradouroNome(String logradouroNome) {
        this.logradouroNome = logradouroNome;
    }

    public String getLogradouroTipo() {
        return logradouroTipo;
    }

    public void setLogradouroTipo(String logradouroTipo) {
        this.logradouroTipo = logradouroTipo;
    }

    public String getLogradouroTitulo() {
        return logradouroTitulo;
    }

    public void setLogradouroTitulo(String logradouroTitulo) {
        this.logradouroTitulo = logradouroTitulo;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

    public BigDecimal getComprimento() {
        return comprimento;
    }

    public void setComprimento(BigDecimal comprimento) {
        this.comprimento = comprimento;
    }

    public Integer getBairroId() {
        return bairroId;
    }

    public void setBairroId(Integer bairroId) {
        this.bairroId = bairroId;
    }

    public String getBairroNome() {
        return bairroNome;
    }

    public void setBairroNome(String bairroNome) {
        this.bairroNome = bairroNome;
    }

    public Integer getArborizacoes() {
        return arborizacoes;
    }

    public void setArborizacoes(Integer arborizacoes) {
        this.arborizacoes = arborizacoes;
    }

    public Integer getColetaDeLixo() {
        return coletaDeLixo;
    }

    public void setColetaDeLixo(Integer coletaDeLixo) {
        this.coletaDeLixo = coletaDeLixo;
    }

    public Integer getColetaDeLixoDiario() {
        return coletaDeLixoDiario;
    }

    public void setColetaDeLixoDiario(Integer coletaDeLixoDiario) {
        this.coletaDeLixoDiario = coletaDeLixoDiario;
    }

    public Integer getGaleriasPluviais() {
        return galeriasPluviais;
    }

    public void setGaleriasPluviais(Integer galeriasPluviais) {
        this.galeriasPluviais = galeriasPluviais;
    }

    public Integer getIluminacaoPublica() {
        return iluminacaoPublica;
    }

    public void setIluminacaoPublica(Integer iluminacaoPublica) {
        this.iluminacaoPublica = iluminacaoPublica;
    }

    public Integer getLimpezaPublica() {
        return limpezaPublica;
    }

    public void setLimpezaPublica(Integer limpezaPublica) {
        this.limpezaPublica = limpezaPublica;
    }

    public Integer getPavimentacao() {
        return pavimentacao;
    }

    public void setPavimentacao(Integer pavimentacao) {
        this.pavimentacao = pavimentacao;
    }

    public Integer getRedeAgua() {
        return redeAgua;
    }

    public void setRedeAgua(Integer redeAgua) {
        this.redeAgua = redeAgua;
    }

    public Integer getRedeEletrica() {
        return redeEletrica;
    }

    public void setRedeEletrica(Integer redeEletrica) {
        this.redeEletrica = redeEletrica;
    }

    public Integer getRedeEsgoto() {
        return redeEsgoto;
    }

    public void setRedeEsgoto(Integer redeEsgoto) {
        this.redeEsgoto = redeEsgoto;
    }

    public Integer getRedeTelefonica() {
        return redeTelefonica;
    }

    public void setRedeTelefonica(Integer redeTelefonica) {
        this.redeTelefonica = redeTelefonica;
    }

    public Integer getSarjetas() {
        return sarjetas;
    }

    public void setSarjetas(Integer sarjetas) {
        this.sarjetas = sarjetas;
    }

    public Integer getEmplacamento() {
        return emplacamento;
    }

    public void setEmplacamento(Integer emplacamento) {
        this.emplacamento = emplacamento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public BigDecimal getValorM2() {
        return valorM2;
    }

    public void setValorM2(BigDecimal valorM2) {
        this.valorM2 = valorM2;
    }

    public String getLado() {
        return "D".equals(lado) ? "DIREITO" : "ESQUERDO";
    }

    public String getLadoSimples() {
        return lado != null ? lado : "";
    }

    public void setLado(String lado) {
        this.lado = lado;
    }
}
