package br.com.webpublico.negocios.contabil.services;

import br.com.webpublico.controle.ReprocessamentoSaldoGrupoBemGrupoMaterialControlador;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.entidadesauxiliares.ReprocessamentoSaldoFonteDespesaOrc;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.util.Date;

/**
 * Created by mateus on 07/12/17.
 */
@Service
@Transactional
public class ServiceReprocessamento {

    private static final Logger logger = LoggerFactory.getLogger(ServiceReprocessamento.class.getName());
    private ExercicioFacade exercicioFacade;
    private ReprocessamentoSaldoFonteDespesaOrcFacade reprocessamentoSaldoFonteDespesaOrcFacade;
    private ReprocessamentoSaldoSubContaFacade reprocessamentoSaldoSubContaFacade;
    private ReprocessamentoSaldoBensFacade reprocessamentoSaldoBensFacade;
    private ReprocessamentoSaldoCreditoReceberFacade reprocessamentoSaldoCreditoReceberFacade;
    private ReprocessamentoSaldoDividaPublicaFacade reprocessamentoSaldoDividaPublicaFacade;
    private Date dataInicial;
    private Date dataFinal;

    public ServiceReprocessamento() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        dataFinal = new Date();
        dataInicial = DataUtil.removerDias(dataFinal, 1);
    }

    @PostConstruct
    public void init() {
        try {
            reprocessamentoSaldoFonteDespesaOrcFacade = (ReprocessamentoSaldoFonteDespesaOrcFacade) new InitialContext().lookup("java:module/ReprocessamentoSaldoFonteDespesaOrcFacade");
            reprocessamentoSaldoSubContaFacade = (ReprocessamentoSaldoSubContaFacade) new InitialContext().lookup("java:module/ReprocessamentoSaldoSubContaFacade");
            reprocessamentoSaldoBensFacade = (ReprocessamentoSaldoBensFacade) new InitialContext().lookup("java:module/ReprocessamentoSaldoBensFacade");
            reprocessamentoSaldoCreditoReceberFacade = (ReprocessamentoSaldoCreditoReceberFacade) new InitialContext().lookup("java:module/ReprocessamentoSaldoCreditoReceberFacade");
            reprocessamentoSaldoDividaPublicaFacade = (ReprocessamentoSaldoDividaPublicaFacade) new InitialContext().lookup("java:module/ReprocessamentoSaldoDividaPublicaFacade");
            exercicioFacade = (ExercicioFacade) new InitialContext().lookup("java:module/ExercicioFacade");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void reprocessar() {
        reprocessarSaldoFonteDespesaOrc();
        reprocessarSaldoSubConta();
        reprocessarGrupoBensMoveis();
        reprocessarGrupoBensImoveis();
        reprocessarGrupoBensIntangiveis();
        reprocessarGrupoMaterial();
        reprocessarCreditosAReceber();
        reprocessarDividaPublica();
    }

    private void reprocessarSaldoSubConta() {
        try {
            reprocessamentoSaldoSubContaFacade.reprocessar(getFiltrosAssistenteReprocessamento());
        } catch (Exception ex) {
            logger.error("Erro ao reprocessar saldo subconta automaticamente: " + ex.getMessage());
        }
    }

    private void reprocessarSaldoFonteDespesaOrc() {
        try {
            reprocessamentoSaldoFonteDespesaOrcFacade.reprocessar(getFiltrosReprocessamentoSaldoFonteDespesaOrc());
        } catch (Exception ex) {
            logger.error("Erro ao reprocessar saldo fonte despesa orc automaticamente: " + ex.getMessage());
        }
    }

    private void reprocessarGrupoBensMoveis() {
        try {
            reprocessamentoSaldoBensFacade.reprocessar(getFiltrosAssistenteReprocessamento(), ReprocessamentoSaldoGrupoBemGrupoMaterialControlador.TipoSaldo.BENS_MOVEIS);
        } catch (Exception ex) {
            logger.error("Erro ao reprocessar grupo de bens moveis automaticamente: " + ex.getMessage());
        }
    }

    private void reprocessarGrupoBensImoveis() {
        try {
            reprocessamentoSaldoBensFacade.reprocessar(getFiltrosAssistenteReprocessamento(), ReprocessamentoSaldoGrupoBemGrupoMaterialControlador.TipoSaldo.BENS_IMOVEIS);
        } catch (Exception ex) {
            logger.error("Erro ao reprocessar grupo de bens imoveis automaticamente: " + ex.getMessage());
        }
    }

    private void reprocessarGrupoBensIntangiveis() {
        try {
            reprocessamentoSaldoBensFacade.reprocessar(getFiltrosAssistenteReprocessamento(), ReprocessamentoSaldoGrupoBemGrupoMaterialControlador.TipoSaldo.BENS_INTANGIVEIS);
        } catch (Exception ex) {
            logger.error("Erro ao reprocessar grupo de bens intangiveis automaticamente: " + ex.getMessage());
        }
    }

    private void reprocessarGrupoMaterial() {
        try {
            reprocessamentoSaldoBensFacade.reprocessar(getFiltrosAssistenteReprocessamento(), ReprocessamentoSaldoGrupoBemGrupoMaterialControlador.TipoSaldo.BENS_ESTOQUE);
        } catch (Exception ex) {
            logger.error("Erro ao reprocessar grupo material automaticamente: " + ex.getMessage());
        }
    }

    private void reprocessarCreditosAReceber() {
        try {
            reprocessamentoSaldoCreditoReceberFacade.reprocessar(getFiltrosAssistenteReprocessamento());
        } catch (Exception ex) {
            logger.error("Erro ao reprocessar creditos a receber automaticamente: " + ex.getMessage());
        }
    }

    private void reprocessarDividaPublica() {
        try {
            reprocessamentoSaldoDividaPublicaFacade.reprocessar(getFiltrosAssistenteReprocessamento());
        } catch (Exception ex) {
            logger.error("Erro ao reprocessar divida publica automaticamente: " + ex.getMessage());
        }
    }

    private AssistenteReprocessamento getFiltrosReprocessamentoSaldoFonteDespesaOrc() {
        AssistenteReprocessamento assistenteReprocessamento = getFiltrosAssistenteReprocessamento();
        assistenteReprocessamento.setReprocessamentoSaldoFonteDespesaOrc(new ReprocessamentoSaldoFonteDespesaOrc());
        return assistenteReprocessamento;
    }

    private AssistenteReprocessamento getFiltrosAssistenteReprocessamento() {
        AssistenteReprocessamento assistenteReprocessamento = new AssistenteReprocessamento();
        assistenteReprocessamento.setDataInicial(dataInicial);
        assistenteReprocessamento.setDataFinal(dataFinal);
        assistenteReprocessamento.setExercicio(exercicioFacade.getExercicioPorAno(Integer.parseInt(Util.getAnoDaData(dataInicial))));
        assistenteReprocessamento.setFiltro("");
        return assistenteReprocessamento;
    }
}
