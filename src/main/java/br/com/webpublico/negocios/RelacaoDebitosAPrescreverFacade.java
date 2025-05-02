package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.enums.TipoPrazoPrescricional;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author octavio
 * Date : 26/04/2019
 */

@Stateless
public class RelacaoDebitosAPrescreverFacade {

    private static Logger logger = LoggerFactory.getLogger(RelacaoDebitosAPrescreverFacade.class);

    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    @Resource
    private SessionContext sessionContext;

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public CadastroFacade getCadastroFacade() {
        return cadastroFacade;
    }

    private Long buscarQuantidadeDebitosAPrescrever(TipoPrazoPrescricional prazo) {
        String sql = " select count(*) from ( " +
            " select vw.* from vwconsultadedebitosaprescrever vw " +
            " where vw.dataprescricao >= to_date( :dataAtual, 'dd/MM/yyyy') " +
            " and vw.dataprescricao <= to_date( :dataPrazo, 'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql);
        Date dataPrazo = DataUtil.adicionaAnos(new Date(), prazo.getAno());
        q.setParameter("dataAtual", Util.dateToString(new Date()));
        q.setParameter("dataPrazo", Util.dateToString(dataPrazo));
        return ((BigDecimal) q.getResultList().get(0)).longValue();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    public void lancarNotificacaoDebitosAPrescrever() {
        sessionContext.getBusinessObject(RelacaoDebitosAPrescreverFacade.class).marcarNotificacoesAnterioresComoLido();
        for (TipoPrazoPrescricional prazo : TipoPrazoPrescricional.values()) {
            Long quantidade = buscarQuantidadeDebitosAPrescrever(prazo);
            if (quantidade > 0L) {
                Notificacao notificacao = new Notificacao();
                notificacao.setDescricao("Existem " + Util.formataNumeroInteiro(quantidade) + " Débitos à prescrever com prazo de prescrição de " + prazo.getDescricao());
                notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
                notificacao.setTitulo("Notificação de débitos à prescrever.");
                notificacao.setTipoNotificacao(TipoNotificacao.AVISO_DE_DEBITOS_A_PRESCREVER);
                notificacao.setLink("/tributario/relacao-debitos-a-prescrever/");
                NotificacaoService.getService().notificar(notificacao);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void marcarNotificacoesAnterioresComoLido() {
        NotificacaoService.getService().marcarComoLida(TipoNotificacao.AVISO_DE_DEBITOS_A_PRESCREVER);
    }
}
