/*
 * Codigo gerado automaticamente em Mon Mar 05 14:54:30 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AprovacaoFerias;
import br.com.webpublico.entidades.FolhaDePagamento;
import br.com.webpublico.entidades.SugestaoFerias;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Date;

@Stateless
public class AprovacaoFeriasFacade extends AbstractFacade<AprovacaoFerias> {

    private static final Logger logger = LoggerFactory.getLogger(AprovacaoFeriasFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AprovacaoFeriasFacade() {
        super(AprovacaoFerias.class);
    }

    @Override
    public void salvar(AprovacaoFerias entity) {
        if (!entity.isAprovada()) {
            Object obj = recuperar(entity.getId());
            if (obj != null) {
                getEntityManager().remove(obj);
            }

        } else {
            try {
                getEntityManager().merge(entity);
            } catch (Exception ex) {
                logger.error("Problema ao alterar", ex);
            }
        }
    }

    private Date prepararDataDeAprovacao() {
        FolhaDePagamento fp = folhaDePagamentoFacade.recuperarUltimaFolhaDePagamentoNormalEfetivada();
        Date dataEfetivacao = fp.getEfetivadaEm();
        dataEfetivacao = Util.getDataHoraMinutoSegundoZerado(dataEfetivacao);

        Date hoje = new Date();
        hoje = Util.getDataHoraMinutoSegundoZerado(hoje);

        if (dataEfetivacao.compareTo(hoje) >= 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(dataEfetivacao);
            c.add(Calendar.DAY_OF_YEAR, 1);
            hoje = c.getTime();
        }
        return hoje;
    }

    public SugestaoFerias aprovar(SugestaoFerias sf) {
        Date hoje = prepararDataDeAprovacao();
        sf = aprovar(sf, hoje);
        return sf;
    }

    public SugestaoFerias aprovar(SugestaoFerias sf, Date dataAprovacao) {
        if (!sf.estaProgramada()) {
            throw new ExcecaoNegocioGenerica("Não é possível aprovar pois ainda não foi feita a programação de férias deste período aquisitivo.");
        }
        AprovacaoFerias af;
        try {
            af = sf.getAprovacaoFerias();
        } catch (ExcecaoNegocioGenerica eng) {
            af = new AprovacaoFerias();
        }
        if(af == null){
            af = new AprovacaoFerias();
        }
        af.setSugestaoFerias(sf);
        af.setDataAprovacao(dataAprovacao);
        af.setAprovado(Boolean.TRUE);
        sf.setListAprovacaoFerias(Util.adicionarObjetoEmLista(sf.getListAprovacaoFerias(), af));
        sf = em.merge(sf);
        return sf;
    }
}
