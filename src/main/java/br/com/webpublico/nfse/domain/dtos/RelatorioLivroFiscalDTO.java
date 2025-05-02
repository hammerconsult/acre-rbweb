package br.com.webpublico.nfse.domain.dtos;

import java.util.Date;
import java.util.List;

public class RelatorioLivroFiscalDTO implements Comparable<RelatorioLivroFiscalDTO> {

    private Long idLivro;
    private Long idEmpresa;
    private String nomeRazaoSocialEmpresa;
    private String cpfCnpjEmpresa;
    private String inscricaoMunicipalEmpresa;
    private String enderecoEmpresa;
    private String numeroEmpresa;
    private String complementoEmpresa;
    private String bairroEmpresa;
    private String cepEmpresa;
    private String cidadeEmpresa;
    private String ufEmpresa;
    private Date dataAberturaEmpresa;
    private Integer numeroLivro;
    private String tipoMovimento;
    private Date periodoInicial;
    private Date periodoFinal;
    private String nomeRazaoSocialEscritorio;
    private String cpfCnpjEscritorio;
    private String nomeContador;
    private String crcEscritorio;
    private List<RelatorioLivroFiscalPeriodoDTO> periodos;
    private List<RelatorioLivroFiscalResumoDTO> resumo;

    public RelatorioLivroFiscalDTO() {
    }

    public String getNomeRazaoSocialEmpresa() {
        return nomeRazaoSocialEmpresa;
    }

    public void setNomeRazaoSocialEmpresa(String nomeRazaoSocialEmpresa) {
        this.nomeRazaoSocialEmpresa = nomeRazaoSocialEmpresa;
    }

    public String getCpfCnpjEmpresa() {
        return cpfCnpjEmpresa;
    }

    public void setCpfCnpjEmpresa(String cpfCnpjEmpresa) {
        this.cpfCnpjEmpresa = cpfCnpjEmpresa;
    }

    public String getInscricaoMunicipalEmpresa() {
        return inscricaoMunicipalEmpresa;
    }

    public void setInscricaoMunicipalEmpresa(String inscricaoMunicipalEmpresa) {
        this.inscricaoMunicipalEmpresa = inscricaoMunicipalEmpresa;
    }

    public String getEnderecoEmpresa() {
        return enderecoEmpresa;
    }

    public void setEnderecoEmpresa(String enderecoEmpresa) {
        this.enderecoEmpresa = enderecoEmpresa;
    }

    public String getNumeroEmpresa() {
        return numeroEmpresa;
    }

    public void setNumeroEmpresa(String numeroEmpresa) {
        this.numeroEmpresa = numeroEmpresa;
    }

    public String getComplementoEmpresa() {
        return complementoEmpresa;
    }

    public void setComplementoEmpresa(String complementoEmpresa) {
        this.complementoEmpresa = complementoEmpresa;
    }

    public String getBairroEmpresa() {
        return bairroEmpresa;
    }

    public void setBairroEmpresa(String bairroEmpresa) {
        this.bairroEmpresa = bairroEmpresa;
    }

    public String getCepEmpresa() {
        return cepEmpresa;
    }

    public void setCepEmpresa(String cepEmpresa) {
        this.cepEmpresa = cepEmpresa;
    }

    public String getCidadeEmpresa() {
        return cidadeEmpresa;
    }

    public void setCidadeEmpresa(String cidadeEmpresa) {
        this.cidadeEmpresa = cidadeEmpresa;
    }

    public String getUfEmpresa() {
        return ufEmpresa;
    }

    public void setUfEmpresa(String ufEmpresa) {
        this.ufEmpresa = ufEmpresa;
    }

    public Date getDataAberturaEmpresa() {
        return dataAberturaEmpresa;
    }

    public void setDataAberturaEmpresa(Date dataAberturaEmpresa) {
        this.dataAberturaEmpresa = dataAberturaEmpresa;
    }

    public Integer getNumeroLivro() {
        return numeroLivro;
    }

    public void setNumeroLivro(Integer numeroLivro) {
        this.numeroLivro = numeroLivro;
    }

    public Date getPeriodoInicial() {
        return periodoInicial;
    }

    public void setPeriodoInicial(Date periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    public Date getPeriodoFinal() {
        return periodoFinal;
    }

    public void setPeriodoFinal(Date periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    public String getNomeRazaoSocialEscritorio() {
        return nomeRazaoSocialEscritorio;
    }

    public void setNomeRazaoSocialEscritorio(String nomeRazaoSocialEscritorio) {
        this.nomeRazaoSocialEscritorio = nomeRazaoSocialEscritorio;
    }

    public String getCpfCnpjEscritorio() {
        return cpfCnpjEscritorio;
    }

    public void setCpfCnpjEscritorio(String cpfCnpjEscritorio) {
        this.cpfCnpjEscritorio = cpfCnpjEscritorio;
    }

    public String getNomeContador() {
        return nomeContador;
    }

    public void setNomeContador(String nomeContador) {
        this.nomeContador = nomeContador;
    }

    public String getCrcEscritorio() {
        return crcEscritorio;
    }

    public void setCrcEscritorio(String crcEscritorio) {
        this.crcEscritorio = crcEscritorio;
    }

    public List<RelatorioLivroFiscalResumoDTO> getResumo() {
        return resumo;
    }

    public void setResumo(List<RelatorioLivroFiscalResumoDTO> resumo) {
        this.resumo = resumo;
    }

    public Long getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(Long idLivro) {
        this.idLivro = idLivro;
    }

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public List<RelatorioLivroFiscalPeriodoDTO> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<RelatorioLivroFiscalPeriodoDTO> periodos) {
        this.periodos = periodos;
    }

    public String getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    @Override
    public int compareTo(RelatorioLivroFiscalDTO o) {
        return this.numeroLivro.compareTo(o.getNumeroLivro());
    }
}
