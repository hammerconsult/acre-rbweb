package br.com.webpublico.util.obn350;

import br.com.webpublico.enums.TipoMovimentoArquivoBancario;
import br.com.webpublico.util.FormataValoresUtil;

public class RegistroObn350TipoTres {

    private TipoMovimentoArquivoBancario tipoMovimentoArquivoBancario;
    private String linha;
    private String tipoRegistro;
    private String dataReferenciaDaRelacao;
    private String codigoOperacao;
    private String codigoAgenciaBancariaUGEmitente;
    private String codigoRelacaoOndeContasOB;
    private String numeroMovimento;
    private String numeroAutenticacao;
    private String codigoRetornoOperacao;
    private String observacaoOb;
    private String codigoReconhecimento;
    private String numeroSequencialArquivo;
    private String valorLiquidaOB;

    public RegistroObn350TipoTres(String linha) {
        this.linha = linha;
    }

    public String getTipoRegistro() {
        tipoRegistro = linha.substring(0, 1);
        return tipoRegistro;
    }

    public String getNumeroLancamento() {
        return getNumeroMovimento().substring(2, getNumeroMovimento().length()).trim();
    }

    public String getNumeroMovimento() {
        numeroMovimento = linha.substring(28, 39);
        if (numeroMovimento.startsWith(TipoMovimentoArquivoBancario.PAGAMENTO.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.PAGAMENTO;
        } else if (numeroMovimento.startsWith(TipoMovimentoArquivoBancario.PAGAMENTO_EXTRA.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.PAGAMENTO_EXTRA;
        } else if (numeroMovimento.startsWith(TipoMovimentoArquivoBancario.TRANSFERENCIA_FINANCEIRA.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.TRANSFERENCIA_FINANCEIRA;
        } else if (numeroMovimento.startsWith(TipoMovimentoArquivoBancario.TRANSFERENCIA_MESMA_UNIDADE.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.TRANSFERENCIA_MESMA_UNIDADE;
        } else if (numeroMovimento.startsWith(TipoMovimentoArquivoBancario.LIBERACAO.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.LIBERACAO;
        }
        return numeroMovimento;
    }

    public String getCodigoAgenciaBancariaUGEmitente() {
        codigoAgenciaBancariaUGEmitente = linha.substring(1, 6);
        return codigoAgenciaBancariaUGEmitente.substring(0, 4) + "-" + codigoAgenciaBancariaUGEmitente.substring(4, 5);
    }

    public String getCodigoRelacaoOndeContasOB() {
        codigoRelacaoOndeContasOB = linha.substring(17, 28);
        return codigoRelacaoOndeContasOB;
    }

    public String getDataReferenciaDaRelacao() {
        dataReferenciaDaRelacao = linha.substring(39, 47);
        return dataReferenciaDaRelacao.substring(0, 2) + "/" + dataReferenciaDaRelacao.substring(2, 4) + "/" + dataReferenciaDaRelacao.substring(4, 8);
    }

    public String getCodigoOperacao() {
        codigoOperacao = linha.substring(51, 53);
        return codigoOperacao;
    }

    public String getValorLiquidoOrdemBacariaConvertido() {
        valorLiquidaOB = linha.substring(63, 80);
        return FormataValoresUtil.valorConvertidoR$(valorLiquidaOB);
    }

    public String getValorLiquidoOrdemBacaria() {
        valorLiquidaOB = linha.substring(63, 80);
        return valorLiquidaOB;
    }

    public TipoMovimentoArquivoBancario getTipoMovimentoArquivoBancario() {
        return tipoMovimentoArquivoBancario;
    }

    public String getCodigoReconhecimento() {
        codigoReconhecimento = linha.substring(236, 253);
        return codigoReconhecimento;
    }

    public String getObservacaoOb() {
        observacaoOb = linha.substring(263, 303);
        return observacaoOb;
    }

    public String getNumeroAutenticacao() {
        numeroAutenticacao = linha.substring(303, 319);
        return numeroAutenticacao;
    }

    public String getCodigoRetornoOperacao() {
        codigoRetornoOperacao = linha.substring(341, 343);
        return codigoRetornoOperacao;
    }

    public String getNumeroSequencialArquivo() {
        numeroSequencialArquivo = linha.substring(343, 350);
        return numeroSequencialArquivo;
    }

    public boolean getObteveSucesso() {
        return getCodigoRetornoOperacao().equals("01");
    }
}
