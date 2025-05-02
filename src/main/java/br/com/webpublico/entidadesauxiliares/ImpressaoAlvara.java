package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.enums.TipoPorte;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by FABIO on 28/04/2016.
 */
public class ImpressaoAlvara {

    private Long idAlvara;
    private Date iniciovigencia;
    private Date finalvigencia;
    private TipoAlvara tipoAlvara;
    private Date vencimento;
    private Long idCadastroEconomico;
    private BigDecimal areaOcupada;
    private Boolean licencaEspecial;
    private String observacao;
    private String assinaturaDigital;
    private Boolean provisorio;
    private String razaoSocial;
    private String nomeFantasia;
    private String cpfCnpj;
    private Date abertura;
    private String classificacaoAtividade;
    private String inscricaoCadastral;
    private String protocoloOficio;
    private Date emissao;
    private Integer ano;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String inscricaoEstadual;
    private String juntacomercial;
    private String rodapeAlvara;
    private Long impressoes;
    private Date dataPrimeiraImpressao;
    private Date dataCalculo;
    private String porte;
    private InputStream qrCode;
    public String urlPortal;
    private String caracteristicasAdicionais;
    private String cabecalhoTiposAlvara;
    private List<AlvaraCnaes> alvarasCnaesPrimarias;
    private List<AlvaraCnaes> alvarasCnaesSecundarias;
    private List<AlvaraAtividadesLicenciadas> alvarasAtividadesLicenciadas;
    private String naturezaJuridica;
    private List<AlvaraEnderecos> enderecosSecundarios;
    private List<AlvaraCnaes> alvaraCnaes;
    private Double areaUtilizacaoEnderecoPrincipal;
    boolean alvaraSuspenso, alvaraCassado;

    public ImpressaoAlvara(Object[] obj, boolean[] situacoesAlvara) {
        this.setAlvaraSuspenso(situacoesAlvara[0]);
        this.setAlvaraCassado(situacoesAlvara[1]);
        this.setIdAlvara(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
        this.setIniciovigencia(obj[1] != null ? (Date) obj[1] : null);
        this.setFinalvigencia(obj[2] != null ? (Date) obj[2] : null);
        this.setTipoAlvara(obj[3] != null ? TipoAlvara.valueOf((String) obj[3]) : null);
        this.setVencimento(obj[4] != null ? (Date) obj[4] : null);
        this.setIdCadastroEconomico(obj[5] != null ? ((BigDecimal) obj[5]).longValue() : null);
        this.setAreaOcupada(obj[6] != null ? (BigDecimal) obj[6] : null);
        this.setLicencaEspecial(obj[7] != null && ((BigDecimal) obj[7]).intValue() == 1);
        this.setObservacao(obj[8] != null ? (String) obj[8] : null);
        this.setAssinaturaDigital(obj[9] != null ? (String) obj[9] : null);
        this.setProvisorio(obj[10] != null && ((BigDecimal) obj[10]).intValue() == 1);
        this.setRazaoSocial(obj[11] != null ? (String) obj[11] : null);
        this.setNomeFantasia(obj[12] != null ? (String) obj[12] : null);
        this.setCpfCnpj(obj[13] != null ? (String) obj[13] : null);
        this.setAbertura(obj[14] != null ? (Date) obj[14] : null);
        this.setClassificacaoAtividade(obj[15] != null ? (String) obj[15] : null);
        this.setInscricaoCadastral(obj[16] != null ? (String) obj[16] : null);
        this.setProtocoloOficio(obj[17] != null ? (String) obj[17] : null);
        this.setEmissao(obj[18] != null ? (Date) obj[18] : null);
        this.setAno(obj[19] != null ? ((BigDecimal) obj[19]).intValue() : null);
        this.setInscricaoEstadual(obj[20] != null ? (String) obj[20] : null);
        this.setJuntacomercial(obj[21] != null ? (String) obj[21] : null);
        this.setImpressoes(obj[22] != null ? ((BigDecimal) obj[22]).longValue() : null);
        this.setDataCalculo(obj[23] != null ? (Date) obj[23] : null);
        this.setDataPrimeiraImpressao(obj[24] != null ? (Date) obj[24] : null);
        this.setLogradouro(obj[25] != null ? (String) obj[25] : null);
        this.setNumero(obj[26] != null ? (String) obj[26] : null);
        this.setComplemento(obj[27] != null ? (String) obj[27] : null);
        this.setBairro(obj[28] != null ? (String) obj[28] : null);
        this.setCep(obj[29] != null ? (String) obj[29] : null);
        this.setPorte(obj[30] != null ? TipoPorte.valueOf((String) obj[30]).getDescricao() : null);
        this.setCaracteristicasAdicionais(obj[31] != null ? (String) obj[31] : null);
        this.setNaturezaJuridica(obj[32] != null ? (String) obj[32] : null);
    }

    public Long getIdAlvara() {
        return idAlvara;
    }

    public void setIdAlvara(Long idAlvara) {
        this.idAlvara = idAlvara;
    }

    public Date getIniciovigencia() {
        return iniciovigencia;
    }

    public void setIniciovigencia(Date iniciovigencia) {
        this.iniciovigencia = iniciovigencia;
    }

    public Date getFinalvigencia() {
        return finalvigencia;
    }

    public void setFinalvigencia(Date finalvigencia) {
        this.finalvigencia = finalvigencia;
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public Long getIdCadastroEconomico() {
        return idCadastroEconomico;
    }

    public void setIdCadastroEconomico(Long idCadastroEconomico) {
        this.idCadastroEconomico = idCadastroEconomico;
    }

    public BigDecimal getAreaOcupada() {
        return areaOcupada;
    }

    public void setAreaOcupada(BigDecimal areaOcupada) {
        this.areaOcupada = areaOcupada;
    }

    public Boolean getLicencaEspecial() {
        return licencaEspecial != null ? licencaEspecial : false;
    }

    public void setLicencaEspecial(Boolean licencaEspecial) {
        this.licencaEspecial = licencaEspecial;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getAssinaturaDigital() {
        return assinaturaDigital;
    }

    public void setAssinaturaDigital(String assinaturaDigital) {
        this.assinaturaDigital = assinaturaDigital;
    }

    public Boolean getProvisorio() {
        return provisorio != null ? provisorio : false;
    }

    public void setProvisorio(Boolean provisorio) {
        this.provisorio = provisorio;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public Date getAbertura() {
        return abertura;
    }

    public void setAbertura(Date abertura) {
        this.abertura = abertura;
    }

    public String getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(String classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getProtocoloOficio() {
        return protocoloOficio;
    }

    public void setProtocoloOficio(String protocoloOficio) {
        this.protocoloOficio = protocoloOficio;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getJuntacomercial() {
        return juntacomercial;
    }

    public void setJuntacomercial(String juntacomercial) {
        this.juntacomercial = juntacomercial;
    }

    public Long getImpressoes() {
        return impressoes;
    }

    public void setImpressoes(Long impressoes) {
        this.impressoes = impressoes;
    }

    public Date getDataCalculo() {
        return dataCalculo;
    }

    public void setDataCalculo(Date dataCalculo) {
        this.dataCalculo = dataCalculo;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public List<AlvaraCnaes> getAlvarasCnaesPrimarias() {
        return alvarasCnaesPrimarias;
    }

    public void setAlvarasCnaesPrimarias(List<AlvaraCnaes> alvarasCnaesPrimarias) {
        this.alvarasCnaesPrimarias = alvarasCnaesPrimarias;
    }

    public List<AlvaraCnaes> getAlvarasCnaesSecundarias() {
        return alvarasCnaesSecundarias;
    }

    public void setAlvarasCnaesSecundarias(List<AlvaraCnaes> alvarasCnaesSecundarias) {
        this.alvarasCnaesSecundarias = alvarasCnaesSecundarias;
    }

    public List<AlvaraAtividadesLicenciadas> getAlvarasAtividadesLicenciadas() {
        return alvarasAtividadesLicenciadas;
    }

    public void setAlvarasAtividadesLicenciadas(List<AlvaraAtividadesLicenciadas> alvarasAtividadesLicenciadas) {
        this.alvarasAtividadesLicenciadas = alvarasAtividadesLicenciadas;
    }

    public InputStream getQrCode() {
        return qrCode;
    }

    public void setQrCode(InputStream qrCode) {
        this.qrCode = qrCode;
    }

    public String getRodapeAlvara() {
        return rodapeAlvara;
    }

    public void setRodapeAlvara(String rodapeAlvara) {
        this.rodapeAlvara = rodapeAlvara;
    }

    public String getUrlPortal() {
        return urlPortal;
    }

    public void setUrlPortal(String urlPortal) {
        this.urlPortal = urlPortal;
    }

    public String getCaracteristicasAdicionais() {
        return caracteristicasAdicionais;
    }

    public void setCaracteristicasAdicionais(String caracteristicasAdicionais) {
        this.caracteristicasAdicionais = caracteristicasAdicionais;
    }

    public String getCabecalhoTiposAlvara() {
        return cabecalhoTiposAlvara;
    }

    public void setCabecalhoTiposAlvara(String cabecalhoTiposAlvara) {
        this.cabecalhoTiposAlvara = cabecalhoTiposAlvara;
    }

    public Date getDataPrimeiraImpressao() {
        return dataPrimeiraImpressao;
    }

    public void setDataPrimeiraImpressao(Date dataPrimeiraImpressao) {
        this.dataPrimeiraImpressao = dataPrimeiraImpressao;
    }

    public String getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(String naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public List<AlvaraEnderecos> getEnderecosSecundarios() {
        return enderecosSecundarios;
    }

    public void setEnderecosSecundarios(List<AlvaraEnderecos> enderecosSecundarios) {
        this.enderecosSecundarios = enderecosSecundarios;
    }

    public List<AlvaraCnaes> getAlvaraCnaes() {
        return alvaraCnaes;
    }

    public void setAlvaraCnaes(List<AlvaraCnaes> alvaraCnaes) {
        this.alvaraCnaes = alvaraCnaes;
    }

    public Double getAreaUtilizacaoEnderecoPrincipal() {
        return areaUtilizacaoEnderecoPrincipal;
    }

    public void setAreaUtilizacaoEnderecoPrincipal(Double areaUtilizacaoEnderecoPrincipal) {
        this.areaUtilizacaoEnderecoPrincipal = areaUtilizacaoEnderecoPrincipal;
    }

    public boolean isAlvaraSuspenso() {
        return alvaraSuspenso;
    }

    public void setAlvaraSuspenso(boolean alvaraSuspenso) {
        this.alvaraSuspenso = alvaraSuspenso;
    }

    public boolean isAlvaraCassado() {
        return alvaraCassado;
    }

    public void setAlvaraCassado(boolean alvaraCassado) {
        this.alvaraCassado = alvaraCassado;
    }
}
