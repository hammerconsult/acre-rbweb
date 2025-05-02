package br.com.webpublico.negocios;

import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.AbstractVOConsultaLancamento;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoProcessoDebito;
import br.com.webpublico.entidadesauxiliares.VOConsultaProcessoDebito;
import br.com.webpublico.enums.TipoCadastroTributario;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 11/05/15
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class RelacaoLancamentoProcessoDebitoFacade extends AbstractRelacaoLancamentoTributarioFacade {

    private static Logger logger = LoggerFactory.getLogger(RelacaoLancamentoProcessoDebitoFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DividaFacade dividaFacade;

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }


    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    @Override
    public Future<? extends List<? extends AbstractVOConsultaLancamento>> consultarLancamentos(AbstractFiltroRelacaoLancamentoDebito filtroRelacaoLancamentoDebito) {
        List<VOConsultaProcessoDebito> resultado;

        FiltroRelacaoLancamentoProcessoDebito filtroRelacaoLancamentoProcessoDebito = (FiltroRelacaoLancamentoProcessoDebito) montarWhere(filtroRelacaoLancamentoDebito);

        resultado = consultarLancamentoProcessoDebito(filtroRelacaoLancamentoProcessoDebito.getWhere().toString());
        return new AsyncResult<>(resultado);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 12)
    public List<VOConsultaProcessoDebito> consultarLancamentoProcessoDebito(String where) {
        String sql = " select * " +
            "  from vwconsultaprocessodebito vw ";
        sql += where;
        logger.debug("SQL: [{}]", sql);
        Query q = em.createNativeQuery(sql);
        List<Object[]> resultado = q.getResultList();
        List<VOConsultaProcessoDebito> toReturn = Lists.newArrayList();
        if (resultado != null && resultado.size() > 0) {
            for (List<Object[]> parte : Lists.partition(resultado, 100000)) {
                toReturn.addAll(preencherLancamentoProcessoDebito(parte));
            }
        }
        return toReturn;
    }

    public List<VOConsultaProcessoDebito> preencherLancamentoProcessoDebito(List<Object[]> objetos) {
        List<VOConsultaProcessoDebito> lista = Lists.newArrayList();
        VOConsultaProcessoDebito voConsultaProcessoDebito;
        if (objetos != null) {
            for (Object[] obj : objetos) {
                voConsultaProcessoDebito = new VOConsultaProcessoDebito(obj);
                lista.add(voConsultaProcessoDebito);
            }
        }
        return lista;
    }


    @Override
    protected AbstractFiltroRelacaoLancamentoDebito montarWhere(AbstractFiltroRelacaoLancamentoDebito abstractFiltroRelacaoLancamentoDebito) {
        FiltroRelacaoLancamentoProcessoDebito filtro = (FiltroRelacaoLancamentoProcessoDebito) super.montarWhere(abstractFiltroRelacaoLancamentoDebito);
        if (filtro.getTipoCadastroTributario() != null) {
            filtro.addFiltro("vw.tipo_cadastro", "=", "Tipo de Cadastro", filtro.getTipoCadastroTributario());

            if (filtro.getTipoCadastroTributario().equals(TipoCadastroTributario.IMOBILIARIO)) {
                filtro.addFiltro("vw.cadastro", ">=", "Inscrição Imobiliária Inicial", filtro.getInscricaoInicial());
                filtro.addFiltro("vw.cadastro", "<=", "Inscrição Imobiliária Final", filtro.getInscricaoFinal());
            }

            if (filtro.getTipoCadastroTributario().equals(TipoCadastroTributario.ECONOMICO)) {
                filtro.addFiltro("vw.cadastro", ">=", "CMC Inicial", filtro.getInscricaoInicial());
                filtro.addFiltro("vw.cadastro", "<=", "CMC Final", filtro.getInscricaoFinal());
            }

            if (filtro.getTipoCadastroTributario().equals(TipoCadastroTributario.RURAL)) {
                filtro.addFiltro("vw.cadastro", ">=", "Inscrição Rural Inicial", filtro.getInscricaoInicial());
                filtro.addFiltro("vw.cadastro", "<=", "Inscrição Rural Final", filtro.getInscricaoFinal());
            }

            if (filtro.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)) {
                filtro.addFiltroNull("vw.id_cadastro");
            }
        }
        if (filtro.getExercicioInicial() != null) {
            filtro.addFiltro("vw.anoProcesso", ">=", "Ano do Processo de Débito Inicial", filtro.getExercicioInicial().getAno());
        }

        if (filtro.getExercicioFinal() != null) {
            filtro.addFiltro("vw.anoProcesso", "<=", "Ano do Processo de Débito Final", filtro.getExercicioFinal().getAno());
        }

        filtro.addFiltro("vw.tipo_processo", "=", "Tipo de Processo de Débito", filtro.getTipoProcessoDebito());

        filtro.addFiltro("vw.codigo_processo", ">=", "Número do Processo Inicial", filtro.getCodigoProcessoInicial());
        filtro.addFiltro("vw.codigo_processo", "<=", "Número do Processo Final", filtro.getCodigoProcessoFinal());

        return filtro;
    }
}
