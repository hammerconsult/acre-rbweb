package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;
import java.util.List;

/**
 * Created by william on 15/09/17.
 */
public class ConfiguracaoNfseDTO implements Serializable, NfseDTO {

    private Long id;
    private MunicipioNfseDTO municipio;
    private Boolean verificarAidfe;
    private String logoMunicipio;
    private String urlNfse;
    private String urlAutenticao;
    private List<Long> idsDividasIss;
    private String secretaria;
    private String departamento;
    private String endereco;
    private String tituloNotaPremiada;
    private String imagemNotaPremiada;
    private String tituloAtendimento;
    private String enderecoAtendimento;
    private String horarioAtendimento;
    private String telefoneAtendimento;
    private Boolean emissaoRetroativa;
    private Boolean emissaoRetroativaUltimaEmitida;
    private Boolean vincularRpsNaEmissao;
    private Boolean validarDadosDeducaoNfse;
    private Boolean validarDmsAbertaMesesAnteriores;
    private Boolean producao;
    private Boolean permiteSelcionarServicoNaoAutorizado;
    private Boolean isentaServicoIncorporacao;
    private Boolean permiteAlterarOpcoesEmailPerfilEmpresa;
    private Boolean permiteEmissaoIssFixoComDebitoVencido;
    private Boolean permiteServicoDeclaradoPrestado;
    private ServicoNfseDTO padraoServicoPrestado;
    private String mensagemLogin;
    private String mensagemAutenticacao;
    private Boolean bloqueiaLogin;
    private Boolean permiteEmissaoNfsAvulsa;
    private Boolean permiteEmissaoRetroativaUltimaEmitida;
    private Boolean enviaEmailEmHomologacao;
    private DetentorArquivoComposicaoNfseDTO arquivoBrasao;
    private String tiposRpsPermitido;
    private Integer diaLimiteCancelamento;
    private Integer quantidadeCancelamentosPermitidos;
    private Boolean habilitaPrimeiroAcesso;
    private String mensagemPrimeiroAcessoDesabilitado;
    private Boolean permiteCancelamentoForaPrazo;
    private String textoBloqueioCancelamentoForaPrazo;

    public ConfiguracaoNfseDTO() {

    }

    public ConfiguracaoNfseDTO(Long id, br.com.webpublico.nfse.domain.dtos.MunicipioNfseDTO municipio, Boolean verificarAidfe) {
        this.id = id;
        this.municipio = municipio;
        this.verificarAidfe = verificarAidfe;
    }

    public Boolean getVerificarAidfe() {
        return verificarAidfe;
    }

    public void setVerificarAidfe(Boolean verificarAidfe) {
        this.verificarAidfe = verificarAidfe;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MunicipioNfseDTO getMunicipio() {
        return municipio;
    }

    public void setMunicipio(MunicipioNfseDTO municipio) {
        this.municipio = municipio;
    }

    public String getLogoMunicipio() {
        return logoMunicipio;
    }

    public void setLogoMunicipio(String logoMunicipio) {
        this.logoMunicipio = logoMunicipio;
    }

    public List<Long> getIdsDividasIss() {
        return idsDividasIss;
    }

    public void setIdsDividasIss(List<Long> idsDividasIss) {
        this.idsDividasIss = idsDividasIss;
    }

    public String getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(String secretaria) {
        this.secretaria = secretaria;
    }

    public String getTituloNotaPremiada() {
        return tituloNotaPremiada;
    }

    public void setTituloNotaPremiada(String tituloNotaPremiada) {
        this.tituloNotaPremiada = tituloNotaPremiada;
    }

    public String getImagemNotaPremiada() {
        return imagemNotaPremiada;
    }

    public void setImagemNotaPremiada(String imagemNotaPremiada) {
        this.imagemNotaPremiada = imagemNotaPremiada;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Boolean getEmissaoRetroativa() {
        return emissaoRetroativa;
    }

    public void setEmissaoRetroativa(Boolean emissaoRetroativa) {
        this.emissaoRetroativa = emissaoRetroativa;
    }

    public Boolean getEmissaoRetroativaUltimaEmitida() {
        return emissaoRetroativaUltimaEmitida;
    }

    public void setEmissaoRetroativaUltimaEmitida(Boolean emissaoRetroativaUltimaEmitida) {
        this.emissaoRetroativaUltimaEmitida = emissaoRetroativaUltimaEmitida;
    }

    public Boolean getVincularRpsNaEmissao() {
        return vincularRpsNaEmissao;
    }

    public void setVincularRpsNaEmissao(Boolean vincularRpsNaEmissao) {
        this.vincularRpsNaEmissao = vincularRpsNaEmissao;
    }

    public Boolean getValidarDadosDeducaoNfse() {
        return validarDadosDeducaoNfse;
    }

    public void setValidarDadosDeducaoNfse(Boolean validarDadosDeducaoNfse) {
        this.validarDadosDeducaoNfse = validarDadosDeducaoNfse;
    }

    public Boolean getValidarDmsAbertaMesesAnteriores() {
        return validarDmsAbertaMesesAnteriores;
    }

    public void setValidarDmsAbertaMesesAnteriores(Boolean validarDmsAbertaMesesAnteriores) {
        this.validarDmsAbertaMesesAnteriores = validarDmsAbertaMesesAnteriores;
    }

    public Boolean getProducao() {
        return producao;
    }

    public void setProducao(Boolean producao) {
        this.producao = producao;
    }

    public String getTituloAtendimento() {
        return tituloAtendimento;
    }

    public void setTituloAtendimento(String tituloAtendimento) {
        this.tituloAtendimento = tituloAtendimento;
    }

    public String getEnderecoAtendimento() {
        return enderecoAtendimento;
    }

    public void setEnderecoAtendimento(String enderecoAtendimento) {
        this.enderecoAtendimento = enderecoAtendimento;
    }

    public String getHorarioAtendimento() {
        return horarioAtendimento;
    }

    public void setHorarioAtendimento(String horarioAtendimento) {
        this.horarioAtendimento = horarioAtendimento;
    }

    public String getTelefoneAtendimento() {
        return telefoneAtendimento;
    }

    public void setTelefoneAtendimento(String telefoneAtendimento) {
        this.telefoneAtendimento = telefoneAtendimento;
    }

    public Boolean getPermiteSelcionarServicoNaoAutorizado() {
        return permiteSelcionarServicoNaoAutorizado;
    }

    public void setPermiteSelcionarServicoNaoAutorizado(Boolean permiteSelcionarServicoNaoAutorizado) {
        this.permiteSelcionarServicoNaoAutorizado = permiteSelcionarServicoNaoAutorizado;
    }

    public Boolean getIsentaServicoIncorporacao() {
        return isentaServicoIncorporacao;
    }

    public void setIsentaServicoIncorporacao(Boolean isentaServicoIncorporacao) {
        this.isentaServicoIncorporacao = isentaServicoIncorporacao;
    }

    public ServicoNfseDTO getPadraoServicoPrestado() {
        return padraoServicoPrestado;
    }

    public void setPadraoServicoPrestado(ServicoNfseDTO padraoServicoPrestado) {
        this.padraoServicoPrestado = padraoServicoPrestado;
    }

    public Boolean getPermiteAlterarOpcoesEmailPerfilEmpresa() {
        return permiteAlterarOpcoesEmailPerfilEmpresa;
    }

    public void setPermiteAlterarOpcoesEmailPerfilEmpresa(Boolean permiteAlterarOpcoesEmailPerfilEmpresa) {
        this.permiteAlterarOpcoesEmailPerfilEmpresa = permiteAlterarOpcoesEmailPerfilEmpresa;
    }

    public Boolean getPermiteServicoDeclaradoPrestado() {
        return permiteServicoDeclaradoPrestado;
    }

    public void setPermiteServicoDeclaradoPrestado(Boolean permiteServicoDeclaradoPrestado) {
        this.permiteServicoDeclaradoPrestado = permiteServicoDeclaradoPrestado;
    }

    public String getMensagemLogin() {
        return mensagemLogin;
    }

    public void setMensagemLogin(String mensagemLogin) {
        this.mensagemLogin = mensagemLogin;
    }

    public Boolean getBloqueiaLogin() {
        return bloqueiaLogin;
    }

    public void setBloqueiaLogin(Boolean bloqueiaLogin) {
        this.bloqueiaLogin = bloqueiaLogin;
    }

    public Boolean getPermiteEmissaoNfsAvulsa() {
        return permiteEmissaoNfsAvulsa;
    }

    public void setPermiteEmissaoNfsAvulsa(Boolean permiteEmissaoNfsAvulsa) {
        this.permiteEmissaoNfsAvulsa = permiteEmissaoNfsAvulsa;
    }

    public Boolean getPermiteEmissaoRetroativaUltimaEmitida() {
        return permiteEmissaoRetroativaUltimaEmitida;
    }

    public void setPermiteEmissaoRetroativaUltimaEmitida(Boolean permiteEmissaoRetroativaUltimaEmitida) {
        this.permiteEmissaoRetroativaUltimaEmitida = permiteEmissaoRetroativaUltimaEmitida;
    }

    public DetentorArquivoComposicaoNfseDTO getArquivoBrasao() {
        return arquivoBrasao;
    }

    public void setArquivoBrasao(DetentorArquivoComposicaoNfseDTO arquivoBrasao) {
        this.arquivoBrasao = arquivoBrasao;
    }

    public String getUrlNfse() {
        return urlNfse;
    }

    public void setUrlNfse(String urlNfse) {
        this.urlNfse = urlNfse;
    }

    public String getUrlAutenticao() {
        return urlAutenticao;
    }

    public void setUrlAutenticao(String urlAutenticao) {
        this.urlAutenticao = urlAutenticao;
    }

    public String getTiposRpsPermitido() {
        return tiposRpsPermitido;
    }

    public void setTiposRpsPermitido(String tiposRpsPermitido) {
        this.tiposRpsPermitido = tiposRpsPermitido;
    }

    public Boolean getPermiteEmissaoIssFixoComDebitoVencido() {
        return permiteEmissaoIssFixoComDebitoVencido;
    }

    public void setPermiteEmissaoIssFixoComDebitoVencido(Boolean permiteEmissaoIssFixoComDebitoVencido) {
        this.permiteEmissaoIssFixoComDebitoVencido = permiteEmissaoIssFixoComDebitoVencido;
    }

    public Boolean getEnviaEmailEmHomologacao() {
        return enviaEmailEmHomologacao;
    }

    public void setEnviaEmailEmHomologacao(Boolean enviaEmailEmHomologacao) {
        this.enviaEmailEmHomologacao = enviaEmailEmHomologacao;
    }

    public Integer getDiaLimiteCancelamento() {
        return diaLimiteCancelamento;
    }

    public void setDiaLimiteCancelamento(Integer diaLimiteCancelamento) {
        this.diaLimiteCancelamento = diaLimiteCancelamento;
    }

    public Boolean getHabilitaPrimeiroAcesso() {
        return habilitaPrimeiroAcesso;
    }

    public void setHabilitaPrimeiroAcesso(Boolean habilitaPrimeiroAcesso) {
        this.habilitaPrimeiroAcesso = habilitaPrimeiroAcesso;
    }

    public String getMensagemPrimeiroAcessoDesabilitado() {
        return mensagemPrimeiroAcessoDesabilitado;
    }

    public void setMensagemPrimeiroAcessoDesabilitado(String mensagemPrimeiroAcessoDesabilitado) {
        this.mensagemPrimeiroAcessoDesabilitado = mensagemPrimeiroAcessoDesabilitado;
    }

    public String getMensagemAutenticacao() {
        return mensagemAutenticacao;
    }

    public void setMensagemAutenticacao(String mensagemAutenticacao) {
        this.mensagemAutenticacao = mensagemAutenticacao;
    }

    public Integer getQuantidadeCancelamentosPermitidos() {
        return quantidadeCancelamentosPermitidos;
    }

    public void setQuantidadeCancelamentosPermitidos(Integer quantidadeCancelamentosPermitidos) {
        this.quantidadeCancelamentosPermitidos = quantidadeCancelamentosPermitidos;
    }

    public Boolean getPermiteCancelamentoForaPrazo() {
        return permiteCancelamentoForaPrazo;
    }

    public void setPermiteCancelamentoForaPrazo(Boolean permiteCancelamentoForaPrazo) {
        this.permiteCancelamentoForaPrazo = permiteCancelamentoForaPrazo;
    }

    public String getTextoBloqueioCancelamentoForaPrazo() {
        return textoBloqueioCancelamentoForaPrazo;
    }

    public void setTextoBloqueioCancelamentoForaPrazo(String textoBloqueioCancelamentoForaPrazo) {
        this.textoBloqueioCancelamentoForaPrazo = textoBloqueioCancelamentoForaPrazo;
    }
}
