package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Usuario
 * Date: 18/10/13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class CreditoSalarioHeader implements Serializable {
    private String codigoBanco;
    private String loteServico;
    private String tipoRegistro;
    private String cnab9posicoes; //febraban
    private String tipoInscricaoEmpresa;
    private String numeroInscricaoEmpresa;
    private String codigoConvenio;
    private String agenciaMantenedoraConta;
    private String digitoVerificadorAgencia;
    private String numeroContaCorrente;
    private String digitoVerificadorContaCorrente;
    private String digitoVerificadorAgenciaContaCorrente;
    private String nomeEmpresa;
    private String nomeBanco;
    private String cnab10posicoes; //febraban
    private String codigoRemessaRetorno;
    private String dataGeracaoArquivo;
    private String horaGeracaoArquivo;
    private String numeroSequencial;
    private String numeroVersaoLayout;
    private String densidadeGravacao;
    private String usoReservadoBanco;
    private String usoReservadoEmpresa;
    private String cnab29posicoes; //febraban

    private final String VERSAO_DO_LAYOUT = "087";
    private final Integer NUMERO_CARACTERES_POR_LINHA = 240;

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    public String getLoteServico() {
        return loteServico;
    }

    public void setLoteServico(String loteServico) {
        this.loteServico = loteServico;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getCnab9posicoes() {
        return cnab9posicoes;
    }

    public void setCnab9posicoes(String cnab9posicoes) {
        this.cnab9posicoes = cnab9posicoes;
    }

    public String getTipoInscricaoEmpresa() {
        return tipoInscricaoEmpresa;
    }

    public void setTipoInscricaoEmpresa(String tipoInscricaoEmpresa) {
        this.tipoInscricaoEmpresa = tipoInscricaoEmpresa;
    }

    public String getNumeroInscricaoEmpresa() {
        return numeroInscricaoEmpresa;
    }

    public void setNumeroInscricaoEmpresa(String numeroInscricaoEmpresa) {
        this.numeroInscricaoEmpresa = numeroInscricaoEmpresa;
    }

    public String getCodigoConvenio() {
        return codigoConvenio;
    }

    public void setCodigoConvenio(String codigoConvenio) {
        this.codigoConvenio = codigoConvenio;
    }

    public String getAgenciaMantenedoraConta() {
        return agenciaMantenedoraConta;
    }

    public void setAgenciaMantenedoraConta(String agenciaMantenedoraConta) {
        this.agenciaMantenedoraConta = agenciaMantenedoraConta;
    }

    public String getDigitoVerificadorAgencia() {
        return digitoVerificadorAgencia;
    }

    public void setDigitoVerificadorAgencia(String digitoVerificadorAgencia) {
        this.digitoVerificadorAgencia = digitoVerificadorAgencia;
    }

    public String getNumeroContaCorrente() {
        return numeroContaCorrente;
    }

    public void setNumeroContaCorrente(String numeroContaCorrente) {
        this.numeroContaCorrente = numeroContaCorrente;
    }

    public String getDigitoVerificadorContaCorrente() {
        return digitoVerificadorContaCorrente;
    }

    public void setDigitoVerificadorContaCorrente(String digitoVerificadorContaCorrente) {
        this.digitoVerificadorContaCorrente = digitoVerificadorContaCorrente;
    }

    public String getDigitoVerificadorAgenciaContaCorrente() {
        return digitoVerificadorAgenciaContaCorrente;
    }

    public void setDigitoVerificadorAgenciaContaCorrente(String digitoVerificadorAgenciaContaCorrente) {
        this.digitoVerificadorAgenciaContaCorrente = digitoVerificadorAgenciaContaCorrente;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getNomeBanco() {
        return nomeBanco;
    }

    public void setNomeBanco(String nomeBanco) {
        this.nomeBanco = nomeBanco;
    }

    public String getCnab10posicoes() {
        return cnab10posicoes;
    }

    public void setCnab10posicoes(String cnab10posicoes) {
        this.cnab10posicoes = cnab10posicoes;
    }

    public String getCodigoRemessaRetorno() {
        return codigoRemessaRetorno;
    }

    public void setCodigoRemessaRetorno(String codigoRemessaRetorno) {
        this.codigoRemessaRetorno = codigoRemessaRetorno;
    }

    public String getDataGeracaoArquivo() {
        return dataGeracaoArquivo;
    }

    public void setDataGeracaoArquivo(String dataGeracaoArquivo) {
        this.dataGeracaoArquivo = dataGeracaoArquivo;
    }

    public String getHoraGeracaoArquivo() {
        return horaGeracaoArquivo;
    }

    public void setHoraGeracaoArquivo(String horaGeracaoArquivo) {
        this.horaGeracaoArquivo = horaGeracaoArquivo;
    }

    public String getNumeroSequencial() {
        return numeroSequencial;
    }

    public void setNumeroSequencial(String numeroSequencial) {
        this.numeroSequencial = numeroSequencial;
    }

    public String getNumeroVersaoLayout() {
        return numeroVersaoLayout;
    }

    public void setNumeroVersaoLayout(String numeroVersaoLayout) {
        this.numeroVersaoLayout = numeroVersaoLayout;
    }

    public String getDensidadeGravacao() {
        return densidadeGravacao;
    }

    public void setDensidadeGravacao(String densidadeGravacao) {
        this.densidadeGravacao = densidadeGravacao;
    }

    public String getUsoReservadoBanco() {
        return usoReservadoBanco;
    }

    public void setUsoReservadoBanco(String usoReservadoBanco) {
        this.usoReservadoBanco = usoReservadoBanco;
    }

    public String getUsoReservadoEmpresa() {
        return usoReservadoEmpresa;
    }

    public void setUsoReservadoEmpresa(String usoReservadoEmpresa) {
        this.usoReservadoEmpresa = usoReservadoEmpresa;
    }

    public String getCnab29posicoes() {
        return cnab29posicoes;
    }

    public void setCnab29posicoes(String cnab29posicoes) {
        this.cnab29posicoes = cnab29posicoes;
    }

    public String getHeaderArquivo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCodigoBanco());
        sb.append(getLoteServico());
        sb.append(getTipoRegistro());
        sb.append(getCnab9posicoes());
        sb.append(getTipoInscricaoEmpresa());
        sb.append(getNumeroInscricaoEmpresa());
        sb.append(getCodigoConvenio());
        sb.append(getAgenciaMantenedoraConta());
        sb.append(getDigitoVerificadorAgencia());
        sb.append(getNumeroContaCorrente());
        sb.append(getDigitoVerificadorContaCorrente());
        sb.append(getDigitoVerificadorAgenciaContaCorrente());
        sb.append(getNomeEmpresa());
        sb.append(getNomeBanco());
        sb.append(getCnab10posicoes());
        sb.append(getCodigoRemessaRetorno());
        sb.append(getDataGeracaoArquivo());
        sb.append(getHoraGeracaoArquivo());
        sb.append(getNumeroSequencial());
        sb.append(getNumeroVersaoLayout());
        sb.append(getDensidadeGravacao());
        sb.append(getUsoReservadoBanco());
        sb.append(getUsoReservadoEmpresa());
        sb.append(getCnab29posicoes());

        if (!validaHeader(sb.toString())) {
            //System.out.println("o TRAILER DO LOTE TEM " + sb.length() + " caracteres de "+NUMERO_CARACTERES_POR_LINHA);
        }
        sb.append("\r\n");

        return sb.toString();
    }

    public void montaHeader(CreditoSalario creditoSalario, HierarquiaOrganizacional ho, GrupoRecursoFP grupo) {

        PessoaJuridica pj = ho.getSubordinada().getEntidade().getPessoaJuridica();
        setCodigoBanco(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        setLoteServico(StringUtil.cortaOuCompletaEsquerda("0", 4, "0"));
        setTipoRegistro("0");
        setCnab9posicoes(StringUtil.cortaOuCompletaEsquerda(" ", 9, " "));
        setTipoInscricaoEmpresa("2");
        setNumeroInscricaoEmpresa(StringUtil.cortaOuCompletaEsquerda(pj.getCnpj().replaceAll("/", "").replaceAll("\\.", "").replaceAll("-", ""), 14, "0"));

        String codigoConvenio = StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getCodigoDoConvenio(), 9, "0");
        String codigoPagamentoConvenio = "0126";

        setCodigoConvenio(StringUtil.cortaOuCompletaDireita(codigoConvenio + codigoPagamentoConvenio, 20, " "));

        setAgenciaMantenedoraConta(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getNumeroAgencia(), 5, "0"));
        setDigitoVerificadorAgencia(creditoSalario.getContaBancariaEntidade().getAgencia().getDigitoVerificador() != null ? creditoSalario.getContaBancariaEntidade().getAgencia().getDigitoVerificador().toUpperCase() : " ");
        setNumeroContaCorrente(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getNumeroConta(), 12, "0"));
        setDigitoVerificadorContaCorrente(creditoSalario.getContaBancariaEntidade().getDigitoVerificador() != null ? creditoSalario.getContaBancariaEntidade().getDigitoVerificador().toUpperCase() : " ");
        setDigitoVerificadorAgenciaContaCorrente(" ");
        setNomeEmpresa(StringUtil.cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciais(grupo.getNomeEmpresa().toUpperCase()), 30, " "));
        setNomeBanco(StringUtil.cortaOuCompletaDireita(creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getDescricao(), 30, " "));
        setCnab10posicoes(StringUtil.cortaOuCompletaDireita(" ", 10, " "));
        setCodigoRemessaRetorno("1");
        setDataGeracaoArquivo(Util.dateToString(new Date()).replaceAll("/", ""));
        setHoraGeracaoArquivo(Util.hourToString(new Date()).replaceAll(":", ""));
        setNumeroSequencial(StringUtil.cortaOuCompletaEsquerda("1", 6, "0"));
        setNumeroVersaoLayout(VERSAO_DO_LAYOUT);
        setDensidadeGravacao(StringUtil.cortaOuCompletaEsquerda("0", 5, "0"));
        setUsoReservadoBanco(StringUtil.cortaOuCompletaEsquerda(" ", 20, " "));
        setUsoReservadoEmpresa(StringUtil.cortaOuCompletaEsquerda(" ", 20, " "));
        setCnab29posicoes(StringUtil.cortaOuCompletaEsquerda(" ", 29, " "));
    }

    public boolean validaHeader(String linha) {
        boolean valido = true;

        if (linha.length() != NUMERO_CARACTERES_POR_LINHA) {
            valido = false;
        }

        return valido;
    }

}
