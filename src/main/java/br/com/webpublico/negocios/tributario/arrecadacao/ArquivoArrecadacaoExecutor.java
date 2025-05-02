package br.com.webpublico.negocios.tributario.arrecadacao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ArquivoBancarioTributario;
import br.com.webpublico.entidadesauxiliares.ArquivoRCB001;
import br.com.webpublico.enums.TipoArquivoBancarioTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.BancoFacade;
import br.com.webpublico.negocios.ConfiguracaoTributarioFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LoteBaixaFacade;
import br.com.webpublico.util.ArquivoRetornoUtil;
import br.com.webpublico.util.AsyncExecutor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ArquivoArrecadacaoExecutor {

    private final DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private final Logger logger = LoggerFactory.getLogger(ArquivoArrecadacaoExecutor.class);

    private final LoteBaixaFacade loteBaixaFacade;
    private final BancoFacade bancoFacade;
    private final ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    private LoteBaixaFacade.ProcessaArquivo processaArquivo;

    public ArquivoArrecadacaoExecutor(LoteBaixaFacade loteBaixaFacade,
                                      BancoFacade bancoFacade,
                                      ConfiguracaoTributarioFacade configuracaoTributarioFacade) {
        this.loteBaixaFacade = loteBaixaFacade;
        this.bancoFacade = bancoFacade;
        this.configuracaoTributarioFacade = configuracaoTributarioFacade;

    }

    public CompletableFuture<LoteBaixaFacade.ProcessaArquivo> execute(LoteBaixaFacade.ProcessaArquivo processaArquivo) {
        this.processaArquivo = processaArquivo;
        return AsyncExecutor.getInstance().execute(processaArquivo,
            this::processar);
    }

    public LoteBaixaFacade.ProcessaArquivo processar() {
        carregarArquivos();
        processaArquivo.setArquivos(Lists.newArrayList());
        for (ArquivoLoteBaixa arquivoLoteBaixa : processaArquivo.getMapArquivoBancarioTributario().keySet()) {
            if (TipoArquivoBancarioTributario.RCB001.equals(arquivoLoteBaixa.getTipoArquivoBancarioTributario())) {
                processaArquivo.getArquivos().add(processarArquivoRCB001(arquivoLoteBaixa));
            } else if (TipoArquivoBancarioTributario.DAF607.equals(arquivoLoteBaixa.getTipoArquivoBancarioTributario())) {
                processaArquivo.getArquivos().add(processarArquivoDAF607(arquivoLoteBaixa));
            }
        }
        return processaArquivo;
    }

    private void carregarArquivos() {
        processaArquivo.getMapArquivoBancarioTributario().clear();
        Integer total = 0;
        for (ArquivoLoteBaixa arquivoLoteBaixa : processaArquivo.getArquivos()) {
            try {
                Arquivo arquivo = loteBaixaFacade.getArquivoFacade().recuperaDependencias(arquivoLoteBaixa.getArquivo().getId());
                StringBuilder buffer = new StringBuilder("");
                for (ArquivoParte parte : arquivo.getPartes()) {
                    buffer.append(new String(parte.getDados()));
                }
                Map<LoteBaixaDoArquivo, ArquivoBancarioTributario> mapArquivoPorLote = lerArquivoBancario(buffer.toString(), arquivoLoteBaixa);
                for (LoteBaixaDoArquivo loteBaixaDoArquivo : mapArquivoPorLote.keySet()) {
                    total = total + mapArquivoPorLote.get(loteBaixaDoArquivo).getQuantidadeRegistros();
                }
                processaArquivo.getMapArquivoBancarioTributario().put(arquivoLoteBaixa, mapArquivoPorLote);
            } catch (Exception e) {
                logger.error("Erro ao carregar arquivos. Erro: {}", e.getMessage());
                logger.debug("Detalhes do erro ao carregar arquivos.", e);
            }
        }
        processaArquivo.setTotal(total);
    }

    public Map<LoteBaixaDoArquivo, ArquivoBancarioTributario> lerArquivoBancario(String conteudoArquivo, ArquivoLoteBaixa arquivoLoteBaixa) throws IOException, ParseException, ValidacaoException {
        arquivoLoteBaixa.getLotes().clear();
        arquivoLoteBaixa.setTipoArquivoBancarioTributario(identificarTipoArquivoBancario(conteudoArquivo));
        switch (arquivoLoteBaixa.getTipoArquivoBancarioTributario()) {
            case RCB001: {
                arquivoLoteBaixa.setValido(Boolean.TRUE);
                return lerArquivoBancarioRCB001(conteudoArquivo, arquivoLoteBaixa);
            }
            case DAF607: {
                arquivoLoteBaixa.setValido(Boolean.TRUE);
                return lerArquivoBancarioDAF607(conteudoArquivo, arquivoLoteBaixa);
            }
        }
        return Maps.newHashMap();
    }

    private TipoArquivoBancarioTributario identificarTipoArquivoBancario(String conteudoArquivo) {
        if (conteudoArquivo != null && !conteudoArquivo.trim().isEmpty()) {
            if (conteudoArquivo.contains(TipoArquivoBancarioTributario.DAF607.name())) {
                return TipoArquivoBancarioTributario.DAF607;
            }
        }
        return TipoArquivoBancarioTributario.RCB001;
    }

    public Map<LoteBaixaDoArquivo, ArquivoBancarioTributario> lerArquivoBancarioRCB001(String conteudoArquivo, ArquivoLoteBaixa arquivoLoteBaixa) throws IOException, ParseException {
        Map<LoteBaixaDoArquivo, ArquivoBancarioTributario> toReturn = Maps.newHashMap();
        List<ArquivoRCB001> arquivos = ArquivoRetornoUtil.gerarArquivoRCB001(conteudoArquivo);
        for (ArquivoRCB001 arquivo : arquivos) {
            if (!arquivo.isValorTotalValido()) {
                throw new ExcecaoNegocioGenerica("O valor total especificado no arquivo de retorno bancário não corresponde a soma dos DAMs informados. Valor calculado " + arquivo.getValorTotal() + ", valor do arquivo " + arquivo.getTrailler().getValorTotal());
            }
            if (!arquivo.isTotalLinhasValido()) {
                throw new ExcecaoNegocioGenerica("A quantidade de linhas especificadas no arquivo de retorno bancário não corresponde com a soma das linhas do mesmo. Linhas contadas " + arquivo.getTotalLinhas() + ", linhas do arquivo " + arquivo.getTrailler().getTotalRegistros());
            }
            LoteBaixaDoArquivo loteBaixaDoArquivo = new LoteBaixaDoArquivo();
            loteBaixaDoArquivo.setArquivoLoteBaixa(arquivoLoteBaixa);
            loteBaixaDoArquivo.setBanco(arquivo.getHeader().getNomeBanco());
            loteBaixaDoArquivo.setCodigoBanco(arquivo.getHeader().getCodigoBanco());
            loteBaixaDoArquivo.setValorTotalArquivo(arquivo.getValorTotal());
            loteBaixaDoArquivo.setNumero(Integer.parseInt(arquivo.getHeader().getNumeroArquivo()));
            loteBaixaDoArquivo.setArquivoBancarioTributario(arquivo);
            if (!arquivo.getRegistros().isEmpty()) {
                loteBaixaDoArquivo.setDataPagamento(formatter.parse(arquivo.getRegistros().get(0).getDataPagamento()));
                loteBaixaDoArquivo.setDataMovimentacao(formatter.parse(arquivo.getRegistros().get(0).getDataCredito()));
            }
            arquivoLoteBaixa.getLotes().add(loteBaixaDoArquivo);
            toReturn.put(loteBaixaDoArquivo, arquivo);
        }
        return toReturn;
    }

    public Map<LoteBaixaDoArquivo, ArquivoBancarioTributario> lerArquivoBancarioDAF607(String conteudoArquivo, ArquivoLoteBaixa arquivoLoteBaixa) throws IOException, ParseException {
        Map<LoteBaixaDoArquivo, ArquivoBancarioTributario> toReturn = Maps.newHashMap();
        ValidacaoException validacaoException = new ValidacaoException();
        List<ArquivoDAF607> arquivos = ArquivoRetornoUtil.gerarArquivoDAF607(conteudoArquivo);
        for (ArquivoDAF607 arquivo : arquivos) {
            LoteBaixaDoArquivo loteBaixaDoArquivo = new LoteBaixaDoArquivo();
            loteBaixaDoArquivo.setArquivoLoteBaixa(arquivoLoteBaixa);
            Banco banco = bancoFacade.retornaBancoPorNumero(arquivo.getHeaderDAF607().getCodigoBancoArrecadador());
            if (banco != null && banco.getId() != null) {
                loteBaixaDoArquivo.setCodigoBanco(banco.getNumeroBanco());
                loteBaixaDoArquivo.setBanco(banco.getDescricao());
            } else {
                validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado banco cadastrado com o código <b>"
                    + arquivo.getHeaderDAF607().getCodigoBancoArrecadador() + " </b>");
                validacaoException.lancarException();
            }
            loteBaixaDoArquivo.setValorTotalArquivo(new BigDecimal(arquivo.getTraillerDAF607().getValorTotalRecebido()));
            loteBaixaDoArquivo.setNumero(Integer.parseInt(arquivo.getHeaderDAF607().getSequencialRegistro()));
            loteBaixaDoArquivo.setArquivoBancarioTributario(arquivo);
            loteBaixaDoArquivo.setDataPagamento(formatter.parse(arquivo.getHeaderDAF607().getDataCreditoConta()));
            loteBaixaDoArquivo.setDataMovimentacao(formatter.parse(arquivo.getHeaderDAF607().getDataCreditoConta()));
            arquivoLoteBaixa.getLotes().add(loteBaixaDoArquivo);
            toReturn.put(loteBaixaDoArquivo, arquivo);
        }
        return toReturn;
    }

    private ArquivoLoteBaixa processarArquivoRCB001(ArquivoLoteBaixa arquivoLoteBaixa) {
        for (LoteBaixaDoArquivo loteBaixaDoArquivo : arquivoLoteBaixa.getLotes()) {
            try {
                LoteBaixa loteBaixa = loteBaixaFacade.criarLoteBaixaRBC001(processaArquivo, arquivoLoteBaixa,
                    loteBaixaDoArquivo);
                loteBaixaDoArquivo.setLoteBaixa(loteBaixa);
            } catch (Exception e) {
                logger.error("Erro ao criar lote de baixa para rbc001. Erro: {}", e.getMessage());
                logger.debug("Detalhes do erro ao criar lote de baixa para rbc001.", e);
            }
        }
        return loteBaixaFacade.salvaViaArquivo(arquivoLoteBaixa);
    }


    public ArquivoLoteBaixa processarArquivoDAF607(ArquivoLoteBaixa arquivoLoteBaixa) {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        for (LoteBaixaDoArquivo loteBaixaDoArquivo : arquivoLoteBaixa.getLotes()) {
            try {
                LoteBaixa loteBaixa = loteBaixaFacade.criarLoteBaixaDAF607(processaArquivo, arquivoLoteBaixa,
                    loteBaixaDoArquivo, configuracaoTributario);
                loteBaixaDoArquivo.setLoteBaixa(loteBaixa);
            } catch (Exception e) {
                logger.error("Erro ao criar lote de baixa para daf607. Erro: {}", e.getMessage());
                logger.debug("Detalhes do erro ao criar lote de baixa para daf607.", e);
            }
        }
        return loteBaixaFacade.salvaViaArquivo(arquivoLoteBaixa);
    }

}
