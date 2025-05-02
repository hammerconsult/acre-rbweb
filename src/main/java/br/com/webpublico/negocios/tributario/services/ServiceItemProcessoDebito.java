package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.ItemProcessoDebito;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.ProcessoDebito;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.negocios.ProcessoDebitoFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Buzatto on 17/08/2016.
 */
@Service
@Transactional
public class ServiceItemProcessoDebito {

    private static final Logger logger = LoggerFactory.getLogger(ServiceItemProcessoDebito.class);

    @PersistenceContext
    protected transient EntityManager em;
    @Autowired
    private JdbcParcelaValorDividaDAO dao;
    private ProcessoDebitoFacade processoDebitoFacade;

    public ServiceItemProcessoDebito() {
        try {
            processoDebitoFacade = (ProcessoDebitoFacade) new InitialContext().lookup("java:module/ProcessoDebitoFacade");
        } catch (NamingException e) {
            logger.error("Service Item Processo Debito", e);
        }
    }

    public List<ProcessoDebito> buscarTodosProcessosFinalizadosVencidos() {
        Query q = em.createNativeQuery(
            " select distinct * " +
                "  from processodebito pro" +
                "   where pro.situacao = :situacao " +
                "   and pro.validade is not null" +
                "   and trunc(pro.validade) < trunc(current_date)", ProcessoDebito.class);
        q.setParameter("situacao", SituacaoProcessoDebito.FINALIZADO.name());
        List<ProcessoDebito> processos = q.getResultList();
        for (ProcessoDebito p : processos) {
            p.getItens().size();
            if(p.getDetentorArquivoComposicao() != null) {
                Hibernate.initialize(p.getDetentorArquivoComposicao().getArquivosComposicao());
            }
            if (p.getItens() != null) {
                for (ItemProcessoDebito itemProcessoDebito : p.getItens()) {
                    itemProcessoDebito.getItemProcessoDebitoParcela().size();
                    itemProcessoDebito.getItemProcessoDebitoParcelamento().size();
                }
            }
        }
        return processos;
    }

    public List<ParcelaValorDivida> buscarParcelasEmAbertoParaProcessosFinalizadosNaoVencidos() {
        Query q = em.createNativeQuery(" select distinct pvd.* " +
            "  from processodebito pro" +
            "  inner join itemprocessodebito item on item.processodebito_id = pro.id " +
            "   inner join parcelavalordivida pvd on pvd.id = item.parcela_id " +
            "   inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id" +
            "   where pro.situacao = :situacao " +
            "   and pro.validade is not null" +
            "   and spvd.situacaoparcela not in (:situacaoSuspenso, :situacaoPago) " +
            "   and trunc(pro.validade) > trunc(current_date)", ParcelaValorDivida.class);
        q.setParameter("situacao", SituacaoProcessoDebito.FINALIZADO.name());
        q.setParameter("situacaoSuspenso", SituacaoParcela.SUSPENSAO.name());
        q.setParameter("situacaoPago", SituacaoParcela.PAGO.name());
        return q.getResultList();
    }

    @Async
    public void suspenderParcelas(List<ParcelaValorDivida> parcelas) {
        for (ParcelaValorDivida parcela : parcelas) {
            processoDebitoFacade.salvarSituacaoParcela(parcela, SituacaoParcela.SUSPENSAO, true);
        }
    }

    @Async
    public void estornarProcessos(List<ProcessoDebito> processos) {
        for (ProcessoDebito processo : processos) {
            processoDebitoFacade.estornar(new AssistenteBarraProgresso(), processo, SituacaoProcessoDebito.ESTORNADO);
        }
    }


}
