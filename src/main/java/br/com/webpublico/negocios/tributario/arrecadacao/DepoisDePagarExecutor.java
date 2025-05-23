package br.com.webpublico.negocios.tributario.arrecadacao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DepoisDePagarExecutor implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(DepoisDePagarExecutor.class);
    private final LoteBaixaFacade loteBaixaFacade;
    private final CalculoITBIFacade calculoITBIFacade;
    private final PermissaoTransporteFacade permissaoTransporteFacade;
    private final NFSAvulsaFacade nfsAvulsaFacade;
    private final FiscalizacaoRBTransFacade fiscalizacaoRBTransFacade;
    private final ProcessoFiscalizacaoFacade processoFiscalizacaoFacade;
    private final ProcessoParcelamentoFacade processoParcelamentoFacade;
    private final CalculoTaxasDiversasFacade calculoTaxasDiversasFacade;
    private final InscricaoDividaAtivaFacade dividaAtivaFacade;
    private final DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;
    private final CalculoAlvaraFacade calculoAlvaraFacade;
    private final AlvaraConstrucaoFacade alvaraConstrucaoFacade;
    private final DAMFacade damFacade;
    private final List<ParcelaValorDivida> parcelas;

    private DepoisDePagarExecutor() {
        this.loteBaixaFacade = (LoteBaixaFacade) Util.getFacadeViaLookup("java:module/LoteBaixaFacade");
        this.calculoITBIFacade = (CalculoITBIFacade) Util.getFacadeViaLookup("java:module/CalculoITBIFacade");
        this.damFacade = (DAMFacade) Util.getFacadeViaLookup("java:module/DAMFacade");
        this.permissaoTransporteFacade = (PermissaoTransporteFacade) Util.getFacadeViaLookup("java:module/PermissaoTransporteFacade");
        this.nfsAvulsaFacade = (NFSAvulsaFacade) Util.getFacadeViaLookup("java:module/NFSAvulsaFacade");
        this.fiscalizacaoRBTransFacade = (FiscalizacaoRBTransFacade) Util.getFacadeViaLookup("java:module/FiscalizacaoRBTransFacade");
        this.processoFiscalizacaoFacade = (ProcessoFiscalizacaoFacade) Util.getFacadeViaLookup("java:module/ProcessoFiscalizacaoFacade");
        this.processoParcelamentoFacade = (ProcessoParcelamentoFacade) Util.getFacadeViaLookup("java:module/ProcessoParcelamentoFacade");
        this.calculoTaxasDiversasFacade = (CalculoTaxasDiversasFacade) Util.getFacadeViaLookup("java:module/CalculoTaxasDiversasFacade");
        this.dividaAtivaFacade = (InscricaoDividaAtivaFacade) Util.getFacadeViaLookup("java:module/InscricaoDividaAtivaFacade");
        this.declaracaoMensalServicoFacade = (DeclaracaoMensalServicoFacade) Util.getFacadeViaLookup("java:module/DeclaracaoMensalServicoFacade");
        this.calculoAlvaraFacade = (CalculoAlvaraFacade) Util.getFacadeViaLookup("java:module/CalculoAlvaraFacade");
        this.alvaraConstrucaoFacade = (AlvaraConstrucaoFacade) Util.getFacadeViaLookup("java:module/AlvaraConstrucaoFacade");
        this.parcelas = Lists.newArrayList();
    }

    public static DepoisDePagarExecutor build(LoteBaixa loteBaixa) {
        DepoisDePagarExecutor build = new DepoisDePagarExecutor();
        for (ItemLoteBaixa item : loteBaixa.getItemLoteBaixa()) {
            try {
                if (item.getDam() != null) {
                    DAM dam = build.damFacade.recuperar(item.getDam().getId());
                    if (dam != null) {
                        for (ItemDAM itemDAM : dam.getItens()) {
                            build.parcelas.add(itemDAM.getParcela());
                        }
                    }
                }
            } catch (Exception e) {
                build.logger.error("Erro ao recuperar DAM para pós pagamento", e);
            }
        }
        return build;
    }

    public static DepoisDePagarExecutor build(Compensacao compensacao) {
        DepoisDePagarExecutor build = new DepoisDePagarExecutor();
        for (ItemCompensacao item : compensacao.getItens()) {
            build.parcelas.add(item.getParcela());
        }
        return build;
    }

    public static DepoisDePagarExecutor build(List<ParcelaValorDivida> parcelas) {
        DepoisDePagarExecutor build = new DepoisDePagarExecutor();
        build.parcelas.addAll(parcelas);
        return build;
    }


    public void execute() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(this);

        executorService.shutdown();
    }

    @Override
    public void run() {
        AssistenteBarraProgresso assistenteArrecadacao = new AssistenteBarraProgresso("Executando as dependências de pós pagamento do lote", parcelas.size());
        for (ParcelaValorDivida parcela : parcelas) {
            try {
                depoisDePagar(parcela.getValorDivida());
            } catch (Exception e) {
                logger.error("Não efetuou o pós pagamento ", e);
            }
            assistenteArrecadacao.conta();
        }
    }

    private void depoisDePagar(ValorDivida valorDivida) {
        Calculo calculo = loteBaixaFacade.recuperarCalculo(valorDivida);
        depoisDePagar(calculo);
    }

    public void depoisDePagar(Calculo calculo) {
        Calculo.TipoCalculo tipo = calculo.getTipoCalculo();
        switch (tipo) {
            case ITBI:
                calculoITBIFacade.depoisDePagar(calculo);
                break;
            case RB_TRANS:
                permissaoTransporteFacade.depoisDePagar(calculo);
                break;
            case NFS_AVULSA:
                nfsAvulsaFacade.depoisDePagar(calculo);
                break;
            case FISCALIZACAO_RBTRANS:
                fiscalizacaoRBTransFacade.depoisDePagar(calculo);
                break;
            case PROC_FISCALIZACAO:
                processoFiscalizacaoFacade.depoisDePagar(calculo);
                break;
            case PARCELAMENTO:
                processoParcelamentoFacade.depoisDePagar(calculo);
                break;
            case TAXAS_DIVERSAS:
                calculoTaxasDiversasFacade.depoisDePagar(calculo);
                break;
            case INSCRICAO_DA:
                dividaAtivaFacade.depoisDePagar(calculo);
                Calculo calculoOriginal = loteBaixaFacade.getArrecadacaoFacade().buscarCalculoOriginalDaDividaAtiva(calculo);
                if (calculoOriginal != null) {
                    depoisDePagar(calculoOriginal);
                }
                break;
            case ISS:
                declaracaoMensalServicoFacade.depoisDePagar(calculo);
                break;
            case ALVARA:
                calculoAlvaraFacade.enviarPdfsRedeSim(calculo, calculoAlvaraFacade.buscarConfiguracaoTriutarioVigente());
                break;
            case ALVARA_CONSTRUCAO_HABITESE:
                alvaraConstrucaoFacade.depoisDePagar(calculo);
                break;
        }

    }

}
