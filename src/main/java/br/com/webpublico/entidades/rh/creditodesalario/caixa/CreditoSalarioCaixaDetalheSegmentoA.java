package br.com.webpublico.entidades.rh.creditodesalario.caixa;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.ContaCorrenteBancaria;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.enums.ModalidadeConta;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: Usuario
 * Date: 24/10/13
 * Time: 09:21
 * To change this template use File | Settings | File Templates.
 */
public class CreditoSalarioCaixaDetalheSegmentoA implements Serializable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final Integer NUMERO_CARACTERES_POR_LINHA = 240;
    private String codigoBanco;
    private String loteServico;
    private String tipoRegistro;
    private String sequenciaRegistroNoLote;
    private String codigoSegmento;
    private String tipoMovimento;
    private String codigoInstrucaoParaMovimento;
    private String camaraCompensacao;
    private String codigoBancoFavorecido;
    private String agenciaMantenedoraFavorecido;
    private String digitoVerificadorAgenciaFavorecido;
    private String numeroContaCorrente;
    private String digitoVerificadorContaCorrente;
    private String digitoVerificadorAgenciaContaCorrente;
    private String nomeFavorecido;
    private String numeroDocumentoParaEmpresa;
    private String filler13;
    private String tipoDeConta;
    private String dataVencimento;
    private String tipoMoeda;
    private String quantidadeMoeda;
    private String valorPagamento;
    private String numeroDocumentoPeloBanco;
    private String filler3;
    private String quantidadeParcelas;
    private String indicadorBloqueio;
    private String indicadorParcelamento;
    private String periodoDiaVencimento;
    private String numeroParcela;
    private String dataEfetivacao;
    private String valorRealEfetivacao;
    private String dataRealEfetivacaoPagamento;
    private String informacao;
    private String codigoFinalidadeDOC;
    private String usoFebraban;
    private String avisoAoFavorecido;
    private String codigoOcorrenciaRetorno;
    private VinculoFP vinculoFP;
    private String numeroMatricula;

    public CreditoSalarioCaixaDetalheSegmentoA(VinculoFP vinculoFP, String numeroMatricula) {
        this.vinculoFP = vinculoFP;
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

    public String getFiller13() {
        return filler13;
    }

    public void setFiller13(String filler13) {
        this.filler13 = filler13;
    }

    public String getTipoDeConta() {
        return tipoDeConta;
    }

    public void setTipoDeConta(String tipoDeConta) {
        this.tipoDeConta = tipoDeConta;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
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

    public String getCamaraCompensacao() {
        return camaraCompensacao;
    }

    public void setCamaraCompensacao(String camaraCompensacao) {
        this.camaraCompensacao = camaraCompensacao;
    }

    public String getFiller3() {
        return filler3;
    }

    public void setFiller3(String filler3) {
        this.filler3 = filler3;
    }

    public String getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(String quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public String getIndicadorBloqueio() {
        return indicadorBloqueio;
    }

    public void setIndicadorBloqueio(String indicadorBloqueio) {
        this.indicadorBloqueio = indicadorBloqueio;
    }

    public String getIndicadorParcelamento() {
        return indicadorParcelamento;
    }

    public void setIndicadorParcelamento(String indicadorParcelamento) {
        this.indicadorParcelamento = indicadorParcelamento;
    }

    public String getPeriodoDiaVencimento() {
        return periodoDiaVencimento;
    }

    public void setPeriodoDiaVencimento(String periodoDiaVencimento) {
        this.periodoDiaVencimento = periodoDiaVencimento;
    }

    public String getNumeroParcela() {
        return numeroParcela;
    }

    public void setNumeroParcela(String numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    public String getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(String dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public String getInformacao() {
        return informacao;
    }

    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    public String getCodigoFinalidadeDOC() {
        return codigoFinalidadeDOC;
    }

    public void setCodigoFinalidadeDOC(String codigoFinalidadeDOC) {
        this.codigoFinalidadeDOC = codigoFinalidadeDOC;
    }

    public String getUsoFebraban() {
        return usoFebraban;
    }

    public void setUsoFebraban(String usoFebraban) {
        this.usoFebraban = usoFebraban;
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

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(String numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }

    public String getDetalheSegmentoA() {
        StringBuilder sb = new StringBuilder();

        sb.append(getCodigoBanco());
        sb.append(getLoteServico());
        sb.append(getTipoRegistro());
        sb.append(getSequenciaRegistroNoLote());
        sb.append(getCodigoSegmento());
        sb.append(getTipoMovimento());
        sb.append(getCodigoInstrucaoParaMovimento());
        sb.append(getCamaraCompensacao());
        sb.append(getCodigoBancoFavorecido());
        sb.append(getAgenciaMantenedoraFavorecido());
        sb.append(getDigitoVerificadorAgenciaFavorecido());
        sb.append(getNumeroContaCorrente());
        sb.append(getDigitoVerificadorContaCorrente());
        sb.append(getDigitoVerificadorAgenciaContaCorrente());
        sb.append(getNomeFavorecido());
        sb.append(getNumeroDocumentoParaEmpresa());
        sb.append(getFiller13());
        sb.append(getTipoDeConta());
        sb.append(getDataVencimento());
        sb.append(getTipoMoeda());
        sb.append(getQuantidadeMoeda());
        sb.append(getValorPagamento());
        sb.append(getNumeroDocumentoPeloBanco());
        sb.append(getFiller3());
        sb.append(getQuantidadeParcelas());
        sb.append(getIndicadorBloqueio());
        sb.append(getIndicadorParcelamento());
        sb.append(getPeriodoDiaVencimento());
        sb.append(getNumeroParcela());
        sb.append(getDataEfetivacao());
        sb.append(getValorRealEfetivacao());
        sb.append(getInformacao());
        sb.append(getCodigoFinalidadeDOC());
        sb.append(getUsoFebraban());
        sb.append(getAvisoAoFavorecido());
        sb.append(getCodigoOcorrenciaRetorno());

        sb.append("\r\n");
        String retorno = sb.toString();
        retorno = retorno.toUpperCase();
        retorno = StringUtil.removeCaracteresEspeciais(retorno);
        retorno = retorno.replaceAll("[^\\x00-\\x7F]", " ");

        return retorno;
    }

    public void montaDetalheSegmentoA(CreditoSalario creditoSalario, Integer sequenciaLote, Integer registroNoLote, Integer sequencial, String identificador) {

        //mesmo banco = 01 (credito em conta corrente) -- outros bancos = 03 (DOC/TED)
//        String formaLancamento = isMesmoBanco(creditoSalario, banco) ? "01" : "03";
        String formaLancamento = "01";

        ContaCorrenteBancaria cc = null;

        try {
            cc = vinculoFP.getContaCorrente() != null ? vinculoFP.getContaCorrente() : vinculoFP.getMatriculaFP().getPessoa().getContaCorrentePrincipal().getContaCorrenteBancaria();
        } catch (NullPointerException npe) {
            return;
        }

        setCodigoBanco(StringUtil.cortaOuCompletaEsquerda(creditoSalario.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        setLoteServico(StringUtil.cortaOuCompletaEsquerda(sequenciaLote.toString(), 4, "0"));
        setTipoRegistro("3");
        setSequenciaRegistroNoLote(StringUtil.cortaOuCompletaEsquerda(registroNoLote.toString(), 5, "0"));
        setCodigoSegmento("A");
        setTipoMovimento("0");
        setCodigoInstrucaoParaMovimento("00");

        setCamaraCompensacao(getCodigoCamaraCompensao(creditoSalario, cc));

        setCodigoBancoFavorecido(StringUtil.cortarOuCompletarEsquerda(cc.getAgencia().getBanco().getNumeroBanco(), 3, "0"));
        setAgenciaMantenedoraFavorecido(StringUtil.cortarOuCompletarEsquerda(cc.getAgencia().getNumeroAgencia() + "", 5, "0"));
        setDigitoVerificadorAgenciaFavorecido(StringUtil.cortaOuCompletaDireita(cc.getAgencia().getDigitoVerificador() == null ? "" : cc.getAgencia().getDigitoVerificador() + "", 1, " "));


        String numeroContaCorrente = getOperacaoDaConta(cc, creditoSalario);
        numeroContaCorrente = StringUtil.cortarOuCompletarEsquerda(numeroContaCorrente, getNumeroCaracteresPreConta(cc, creditoSalario), "0");

        String auxNumeroConta = cc.getNumeroConta();
        auxNumeroConta = auxNumeroConta != null ? auxNumeroConta.replace(" ", "") : "";
        auxNumeroConta = auxNumeroConta != null ? auxNumeroConta.replace(".0", "") : "";
        numeroContaCorrente += StringUtil.cortarOuCompletarEsquerda(auxNumeroConta, getNumeroCaracteresConta(cc, creditoSalario), "0");

        setNumeroContaCorrente(numeroContaCorrente);
        setDigitoVerificadorContaCorrente(StringUtil.cortaOuCompletaDireita(cc.getDigitoVerificador(), 1, " "));
        setDigitoVerificadorAgenciaContaCorrente(" ");
        setNomeFavorecido(StringUtil.cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciais(vinculoFP.getMatriculaFP().getPessoa().getNome()), 30, " "));
        if (!Strings.isNullOrEmpty(identificador)) {
            setNumeroDocumentoParaEmpresa(StringUtil.cortarOuCompletarEsquerda(identificador, 6, "0"));
        } else {
            setNumeroDocumentoParaEmpresa(StringUtil.cortarOuCompletarEsquerda(sequencial + "", 6, "0"));
        }
        setFiller13("             ");
        setTipoDeConta(getTipoContaFormatado(creditoSalario, cc));
        setDataVencimento(StringUtil.cortarOuCompletarEsquerda(Util.dateToString(creditoSalario.getDataCredito()).replaceAll("/", ""), 8, "0"));
        setTipoMoeda("BRL");
        setQuantidadeMoeda(StringUtils.repeat("0", 15));
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        String vp = df.format(new BigDecimal(valorPagamento));
        setValorPagamento(StringUtil.cortarOuCompletarEsquerda(vp.replaceAll("\\.", "").replaceAll("\\,", ""), 15, "0"));
        setNumeroDocumentoPeloBanco(StringUtil.cortarOuCompletarEsquerda("", 9, "0"));
        setFiller3("   ");
        setQuantidadeParcelas("01");
        setIndicadorBloqueio("N");
        setIndicadorParcelamento("1");
        setPeriodoDiaVencimento(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDia(creditoSalario.getDataCredito()) + "", 2, "0"));
        setNumeroParcela("00");
        setDataEfetivacao(StringUtil.cortarOuCompletarEsquerda("0", 8, "0"));
        setValorRealEfetivacao(StringUtil.cortarOuCompletarEsquerda("0", 15, "0"));
        setInformacao(StringUtil.cortaOuCompletaDireita("", 40, " "));
        String codigoFinalidadeDOC = !isMesmoBanco(creditoSalario, cc.getBanco()) && !creditoSalario.isTipoArquivoServidor() ? "00" : (getCamaraCompensacao().equals("018") ? "01" : "00");
        setCodigoFinalidadeDOC(codigoFinalidadeDOC);
        setUsoFebraban("          ");
        setAvisoAoFavorecido("0");
        setCodigoOcorrenciaRetorno("          ");
    }

    private int getNumeroCaracteresPreConta(ContaCorrenteBancaria cc, CreditoSalario creditoSalario) {
        if (creditoSalario.isTipoArquivoServidor() && cc.isModalidadeContaSalarioNSGD()) {
            return 3;
        }
        if (creditoSalario.isTipoArquivoPensionista() && cc.getNumeroConta().trim().length() == 9) {
            return 3;
        }
        return 4;
    }

    private int getNumeroCaracteresConta(ContaCorrenteBancaria cc, CreditoSalario creditoSalario) {
        if (creditoSalario.isTipoArquivoServidor() && cc.isModalidadeContaSalarioNSGD()) {
            return 9;
        }
        if (creditoSalario.isTipoArquivoPensionista() && cc.getNumeroConta().trim().length() == 9) {
            return 9;
        }
        return 8;
    }

    private String getTipoContaFormatado(CreditoSalario creditoSalario, ContaCorrenteBancaria cc) {
        //Houve uma confusao para definir como seria o valor desse campo, optou-se por colocar o mesmo valor que o
        //turmlina está colocando que é 1 para os serivdores de conta salário.
        if (isMesmoBanco(creditoSalario, cc.getBanco())) {
            return "1";
        }
        if (ModalidadeConta.CONTA_CORRENTE.equals(cc.getModalidadeConta())) {
            return "1";
        }
        if (ModalidadeConta.CONTA_POUPANCA.equals(cc.getModalidadeConta())) {
            return "2";
        }
        return " ";
    }

    private String getCodigoCamaraCompensao(CreditoSalario creditoSalario, ContaCorrenteBancaria cc) {
        if (creditoSalario.isTipoArquivoServidor() || isMesmoBanco(creditoSalario, cc.getBanco())) {
            return "700";
        } else {
            return "018";
        }
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

    public String getValorRealEfetivacao() {
        return valorRealEfetivacao;
    }

    public void setValorRealEfetivacao(String valorRealEfetivacao) {
        this.valorRealEfetivacao = valorRealEfetivacao;
    }

    @Override
    public boolean equals(Object outro) {
        return vinculoFP.equals(outro);
    }

    public String getOperacaoDaConta(ContaCorrenteBancaria contaCorrenteBancaria, CreditoSalario creditoSalario) {
        if (!isMesmoBanco(creditoSalario, contaCorrenteBancaria.getBanco()) && !creditoSalario.isTipoArquivoServidor()) {
            return "000";
        }
        switch (contaCorrenteBancaria.getModalidadeConta()) {

            case CONTA_CORRENTE:
                return creditoSalario.isTipoArquivoServidor() ? "037" : "001";
            case CONTA_CAIXA_FACIL:
                return ModalidadeConta.CONTA_CAIXA_FACIL.getCodigoCaixa();
            case CONTA_POUPANCA:
                return "013";
            case CONTA_SALARIO:
                return "037";
            case CONTA_SALARIO_NSGD:
                return ModalidadeConta.CONTA_SALARIO_NSGD.getCodigoCaixa();
            default:
                return "037";
        }
    }
}
