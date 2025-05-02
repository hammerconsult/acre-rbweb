package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Usuario
 * Date: 24/10/13
 * Time: 09:21
 * To change this template use File | Settings | File Templates.
 */
public class CreditoSalarioDetalheSegmentoA implements Serializable {

    private String codigoBanco;
    private String loteServico;
    private String tipoRegistro;
    private String sequenciaRegistroNoLote;
    private String codigoSegmento;
    private String tipoMovimento;
    private String codigoInstrucaoParaMovimento;
    private String codigoCamaraCentralizadora;
    private String codigoBancoFavorecido;
    private String agenciaMantenedoraFavorecido;
    private String digitoVerificadorAgenciaFavorecido;
    private String numeroContaCorrente;
    private String digitoVerificadorContaCorrente;
    private String digitoVerificadorAgenciaContaCorrente;
    private String nomeFavorecido;
    private String numeroDocumentoParaEmpresa;
    private String dataPagamento;
    private String tipoMoeda;
    private String quantidadeMoeda;
    private String valorPagamento;
    private String numeroDocumentoPeloBanco;
    private String dataRealEfetivacaoPagamento;
    private String valorRealEfetivacaoPagamento;
    private String informacao;
    private String complementoTipoServico;
    private String codigoFinalidadeTED;
    private String complementoFinalidadePagamento;
    private String cnab3posicoes; //febraban
    private String avisoAoFavorecido;
    private String codigoOcorrenciaRetorno;
    private final Integer NUMERO_CARACTERES_POR_LINHA = 240;

    private VinculoFP vinculoFP;
    private FichaFinanceiraFP ficha;
    private String numeroMatricula;

    public CreditoSalarioDetalheSegmentoA(VinculoFP vinculoFP, FichaFinanceiraFP ficha, String numeroMatricula) {
        this.vinculoFP = vinculoFP;
        this.ficha = ficha;
        this.numeroMatricula = numeroMatricula;
    }

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

    public String getSequenciaRegistroNoLote() {
        return sequenciaRegistroNoLote;
    }

    public void setSequenciaRegistroNoLote(String sequenciaRegistroNoLote) {
        this.sequenciaRegistroNoLote = sequenciaRegistroNoLote;
    }

    public String getCodigoSegmento() {
        return codigoSegmento;
    }

    public void setCodigoSegmento(String codigoSegmento) {
        this.codigoSegmento = codigoSegmento;
    }

    public String getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public String getCodigoInstrucaoParaMovimento() {
        return codigoInstrucaoParaMovimento;
    }

    public void setCodigoInstrucaoParaMovimento(String codigoInstrucaoParaMovimento) {
        this.codigoInstrucaoParaMovimento = codigoInstrucaoParaMovimento;
    }

    public String getCodigoCamaraCentralizadora() {
        return codigoCamaraCentralizadora;
    }

    public void setCodigoCamaraCentralizadora(String codigoCamaraCentralizadora) {
        this.codigoCamaraCentralizadora = codigoCamaraCentralizadora;
    }

    public String getCodigoBancoFavorecido() {
        return codigoBancoFavorecido;
    }

    public void setCodigoBancoFavorecido(String codigoBancoFavorecido) {
        this.codigoBancoFavorecido = codigoBancoFavorecido;
    }

    public String getAgenciaMantenedoraFavorecido() {
        return agenciaMantenedoraFavorecido;
    }

    public void setAgenciaMantenedoraFavorecido(String agenciaMantenedoraFavorecido) {
        this.agenciaMantenedoraFavorecido = agenciaMantenedoraFavorecido;
    }

    public String getDigitoVerificadorAgenciaFavorecido() {
        return digitoVerificadorAgenciaFavorecido;
    }

    public void setDigitoVerificadorAgenciaFavorecido(String digitoVerificadorAgenciaFavorecido) {
        this.digitoVerificadorAgenciaFavorecido = digitoVerificadorAgenciaFavorecido;
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

    public String getNomeFavorecido() {
        return nomeFavorecido;
    }

    public void setNomeFavorecido(String nomeFavorecido) {
        this.nomeFavorecido = nomeFavorecido;
    }

    public String getNumeroDocumentoParaEmpresa() {
        return numeroDocumentoParaEmpresa;
    }

    public void setNumeroDocumentoParaEmpresa(String numeroDocumentoParaEmpresa) {
        this.numeroDocumentoParaEmpresa = numeroDocumentoParaEmpresa;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getTipoMoeda() {
        return tipoMoeda;
    }

    public void setTipoMoeda(String tipoMoeda) {
        this.tipoMoeda = tipoMoeda;
    }

    public String getQuantidadeMoeda() {
        return quantidadeMoeda;
    }

    public void setQuantidadeMoeda(String quantidadeMoeda) {
        this.quantidadeMoeda = quantidadeMoeda;
    }

    public String getValorPagamento() {
        return valorPagamento;
    }

    public void setValorPagamento(String valorPagamento) {
        this.valorPagamento = valorPagamento;
    }

    public String getNumeroDocumentoPeloBanco() {
        return numeroDocumentoPeloBanco;
    }

    public void setNumeroDocumentoPeloBanco(String numeroDocumentoPeloBanco) {
        this.numeroDocumentoPeloBanco = numeroDocumentoPeloBanco;
    }

    public String getDataRealEfetivacaoPagamento() {
        return dataRealEfetivacaoPagamento;
    }

    public void setDataRealEfetivacaoPagamento(String dataRealEfetivacaoPagamento) {
        this.dataRealEfetivacaoPagamento = dataRealEfetivacaoPagamento;
    }

    public String getValorRealEfetivacaoPagamento() {
        return valorRealEfetivacaoPagamento;
    }

    public void setValorRealEfetivacaoPagamento(String valorRealEfetivacaoPagamento) {
        this.valorRealEfetivacaoPagamento = valorRealEfetivacaoPagamento;
    }

    public String getInformacao() {
        return informacao;
    }

    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    public String getComplementoTipoServico() {
        return complementoTipoServico;
    }

    public void setComplementoTipoServico(String complementoTipoServico) {
        this.complementoTipoServico = complementoTipoServico;
    }

    public String getCodigoFinalidadeTED() {
        return codigoFinalidadeTED;
    }

    public void setCodigoFinalidadeTED(String codigoFinalidadeTED) {
        this.codigoFinalidadeTED = codigoFinalidadeTED;
    }

    public String getComplementoFinalidadePagamento() {
        return complementoFinalidadePagamento;
    }

    public void setComplementoFinalidadePagamento(String complementoFinalidadePagamento) {
        this.complementoFinalidadePagamento = complementoFinalidadePagamento;
    }

    public String getCnab3posicoes() {
        return cnab3posicoes;
    }

    public void setCnab3posicoes(String cnab3posicoes) {
        this.cnab3posicoes = cnab3posicoes;
    }

    public String getAvisoAoFavorecido() {
        return avisoAoFavorecido;
    }

    public void setAvisoAoFavorecido(String avisoAoFavorecido) {
        this.avisoAoFavorecido = avisoAoFavorecido;
    }

    public String getCodigoOcorrenciaRetorno() {
        return codigoOcorrenciaRetorno;
    }

    public void setCodigoOcorrenciaRetorno(String codigoOcorrenciaRetorno) {
        this.codigoOcorrenciaRetorno = codigoOcorrenciaRetorno;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public FichaFinanceiraFP getFicha() {
        return ficha;
    }

    public void setFicha(FichaFinanceiraFP ficha) {
        this.ficha = ficha;
    }

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(String numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }

    public String getDetalheSegmentoA() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCodigoBanco());
        //Controle - Lote
        sb.append(getLoteServico());
        //Controle - Registro
        sb.append(getTipoRegistro());
        //Serviço - Número do Registro
        sb.append(getSequenciaRegistroNoLote());
        //Serviço - Segmento
        sb.append(getCodigoSegmento());
        //Serviço - Movimento - Tipo
        sb.append(getTipoMovimento());
        //Serviço - Movimento - Código
        sb.append(getCodigoInstrucaoParaMovimento());
        //Favorecido - Câmara
        sb.append(getCodigoCamaraCentralizadora());
        //Favorecido - Banco
        sb.append(getCodigoBancoFavorecido());
        //Favorecido - Conta Corrente - Agência - Código
        sb.append(getAgenciaMantenedoraFavorecido());
        //Favorecido - Conta Corrente - Agência - DV
        sb.append(getDigitoVerificadorAgenciaFavorecido());
        //Favorecido - Conta Corrente - Conta - Código
        sb.append(getNumeroContaCorrente());
        //Favorecido - Conta Corrente - Conta - DV
        sb.append(getDigitoVerificadorContaCorrente());
        //Favorecido - Conta Corrente - DV
        sb.append(getDigitoVerificadorAgenciaContaCorrente());
        //Favorecido - Nome
        sb.append(getNomeFavorecido());
        //Crédito - Seu Número
        sb.append(getNumeroDocumentoParaEmpresa());
        //Crédito - Data de Pagamento
        sb.append(getDataPagamento());
        //Crédito - Moeda - Tipo
        sb.append(getTipoMoeda());
        //Crédito - Moeda - Quantidade
        sb.append(getQuantidadeMoeda());
        //Crédito - Valor Pagamento
        sb.append(getValorPagamento());
        //Crédito - Nosso Número
        sb.append(getNumeroDocumentoPeloBanco());
        //Crédito - Data Real
        sb.append(getDataRealEfetivacaoPagamento());
        //Crédito - Valor Real
        sb.append(getValorRealEfetivacaoPagamento());
        //Informação 2
        sb.append(getInformacao());
        //Código Finalidade Doc
        sb.append(getComplementoTipoServico());
        //Código Finalidade TED
        sb.append(getCodigoFinalidadeTED());
        //Código Finalidade Complementar
        sb.append(getComplementoFinalidadePagamento());
        //CNAB
        sb.append(getCnab3posicoes());
        //Aviso
        sb.append(getAvisoAoFavorecido());
        //Ocorrências
        sb.append(getCodigoOcorrenciaRetorno());

        if (!validaDetalheSegmentoA(sb.toString())) {
            //System.out.println("o DETALHE DO SEGMENTO 'A' TEM " + sb.length() + " caracteres de " + NUMERO_CARACTERES_POR_LINHA);
        }

        sb.append("\r\n");

        return sb.toString();
    }

    public void montaDetalheSegmentoA(CreditoSalario creditoSalario, Banco banco, BigDecimal valorPagamento, Integer sequenciaLote, Integer registroNoLote) {

        //mesmo banco = 01 (credito em conta corrente) -- outros bancos = 03 (DOC/TED)
        String formaLancamento = isMesmoBanco(creditoSalario, banco) ? "01" : "03";

        //Controle - Banco
        setCodigoBanco(StringUtil.cortaOuCompletaEsquerda(vinculoFP.getContaCorrente().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        //Controle - Lote
        setLoteServico(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        //Controle - Registro
        setTipoRegistro("3");
        //Serviço - Número do Registro
        setSequenciaRegistroNoLote(StringUtil.cortaOuCompletaEsquerda(registroNoLote.toString(), 5, "0"));
        //Serviço - Segmento
        setCodigoSegmento("A");
        //Serviço - Movimento - Tipo
        setTipoMovimento("0");
        //Serviço - Movimento - Código
        setCodigoInstrucaoParaMovimento("00");
        //Favorecido - Câmara
        if (formaLancamento.equals("01")) {
            setCodigoCamaraCentralizadora("000");
        } else if (formaLancamento.equals("03")) {
            if (valorPagamento.compareTo(new BigDecimal("5000.00")) < 0) {
                setCodigoCamaraCentralizadora("700");
            } else {
                setCodigoCamaraCentralizadora("018");
            }
        }
        //Favorecido - Banco
        setCodigoBancoFavorecido(StringUtil.cortaOuCompletaEsquerda(vinculoFP.getContaCorrente().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        //Favorecido - Conta Corrente - Agência - Código
        setAgenciaMantenedoraFavorecido(StringUtil.cortaOuCompletaEsquerda(vinculoFP.getContaCorrente().getAgencia().getNumeroAgencia().toString(), 5, "0"));

        //Favorecido - Conta Corrente - Agência - DV
        if (vinculoFP.getContaCorrente().getAgencia().getDigitoVerificador() == null)
            setDigitoVerificadorAgenciaFavorecido(" ");
        else {
            setDigitoVerificadorAgenciaFavorecido(StringUtil.cortaOuCompletaEsquerda(vinculoFP.getContaCorrente().getAgencia().getDigitoVerificador().toString().toUpperCase(), 1, "0"));
        }

        //Favorecido - Conta Corrente - Conta - Código
        setNumeroContaCorrente(StringUtil.cortaOuCompletaEsquerda(vinculoFP.getContaCorrente().getNumeroConta(), 12, "0"));
        //Favorecido - Conta Corrente - Conta - DV
        setDigitoVerificadorContaCorrente(StringUtil.cortaOuCompletaEsquerda(vinculoFP.getContaCorrente().getDigitoVerificador().toUpperCase(), 1, "0"));
        //Favorecido - Conta Corrente - DV
        setDigitoVerificadorAgenciaContaCorrente(" ");
        //Favorecido - Nome
        setNomeFavorecido(StringUtil.cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciais(vinculoFP.getMatriculaFP().getPessoa().getNome()), 30, " "));
        //Crédito - Seu Número (matrícula/contrato)
        String matriculaContrato = StringUtil.cortaOuCompletaEsquerda(vinculoFP.getMatriculaFP().getMatricula() + "/" + vinculoFP.getNumero(), 10, "0");
        setNumeroDocumentoParaEmpresa(StringUtil.cortaOuCompletaDireita(matriculaContrato, 20, " "));
        //Crédito - Data de Pagamento
        setDataPagamento(Util.dateToString(new Date()).replaceAll("/", ""));
        //Crédito - Moeda - Tipo
        setTipoMoeda("BRL");
        //Crédito - Moeda - Quantidade
        setQuantidadeMoeda(StringUtils.repeat("0", 15));
        //Crédito - Valor Pagamento
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        String vp = df.format(valorPagamento);
        setValorPagamento(StringUtil.cortaOuCompletaEsquerda(vp.replaceAll("\\.", "").replaceAll("\\,", ""), 15, "0"));
        //Crédito - Nosso Número
        setNumeroDocumentoPeloBanco(StringUtil.cortaOuCompletaDireita("", 20, " "));
        //Crédito - Data Real
        setDataRealEfetivacaoPagamento(StringUtils.repeat("0", 8));
        //Crédito - Valor Real
        setValorRealEfetivacaoPagamento(StringUtils.repeat("0", 15));
        //Informação 2
        //setInformacao(StringUtil.cortaOuCompletaDireita(isMesmoBanco(creditoSalario) ? "CREDITO EM CONTA CORRENTE" : "DOC/TED EM CONTA CORRENTE", 40, " "));
        setInformacao(StringUtil.cortaOuCompletaDireita(" ", 40, " "));
        //Código Finalidade Doc
        setComplementoTipoServico(StringUtil.cortaOuCompletaDireita("", 2, " "));
        //Código Finalidade TED
        setCodigoFinalidadeTED(StringUtil.cortaOuCompletaDireita("", 5, " "));
        //Código Finalidade Complementar
        setComplementoFinalidadePagamento(StringUtil.cortaOuCompletaDireita("", 2, " "));
        //CNAB
        setCnab3posicoes(StringUtil.cortaOuCompletaDireita("", 3, " "));
        //Aviso
        setAvisoAoFavorecido("0");
        //Ocorrências
        setCodigoOcorrenciaRetorno(StringUtil.cortaOuCompletaDireita("", 10, " "));
    }

    private boolean isMesmoBanco(CreditoSalario creditoSalario) {
        if (creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco().equals(vinculoFP.getContaCorrente().getAgencia().getBanco().getNumeroBanco())) {
            return true;
        }
        return false;
    }

    public boolean validaDetalheSegmentoA(String linha) {
        boolean valido = true;
        if (linha.length() != NUMERO_CARACTERES_POR_LINHA) {
            valido = false;
        }

        return valido;
    }

    private boolean isMesmoBanco(CreditoSalario creditoSalario, Banco banco) {
        if (creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco().equals(banco.getNumeroBanco())) {
            return true;
        }
        return false;
    }


}
