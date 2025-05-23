package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * @author Desenvolvimento
 */
@Stateless
public class ConfigAlienacaoAtivosFacade extends AbstractFacade<ConfigAlienacaoAtivos> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private DiariaContabilizacaoFacade diariaContabilizacaoFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigAlienacaoAtivosFacade() {
        super(ConfigAlienacaoAtivos.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public DiariaContabilizacaoFacade getDiariaContabilizacaoFacade() {
        return diariaContabilizacaoFacade;
    }

    public void verificarConfiguracaoExistente(ConfigAlienacaoAtivos config) throws ExcecaoNegocioGenerica {
        String sql =
            " select c.* " +
                "         from ConfigAlienacaoAtivos c " +
                " where c.evento_id = :evento ";
        if (config.getId() != null) {
            sql += " AND C.ID <> :config";
        }
        sql += "  and to_date(:data, 'dd/MM/yyyy') between c.inicioVigencia and coalesce(c.fimVigencia, to_date(:data, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigAlienacaoAtivos.class);
        q.setParameter("evento", config.getEvento().getId());
        q.setParameter("data", DataUtil.getDataFormatada(config.getInicioVigencia()));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        if (!q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Já existe uma configuração vigente para o evento: " + config.getEvento());
        }
    }

    public ConfigAlienacaoAtivos buscarConfigVigente(EventoContabil eventoContabil, Date dataBensMoveis) {
        String sql = " select cf.* " +
            "            from ConfigAlienacaoAtivos cf " +
            " where cf.evento_id = :evento " +
            "   and to_date(:data, 'dd/MM/yyyy') between trunc(cf.inicioVigencia) and coalesce(trunc(cf.fimVigencia), to_date(:data, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigAlienacaoAtivos.class);
        q.setParameter("data", DataUtil.getDataFormatada(dataBensMoveis));
        q.setParameter("evento", eventoContabil.getId());
        try {
            return (ConfigAlienacaoAtivos) q.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<LancamentoContabil> buscarLancamentosPelaVigencia(Date data, EventoContabil obj) {
        String sql = " select lanc.* from lancamentocontabil lanc ";
        sql += " inner join itemparametroevento item on item.id=lanc.itemparametroevento_id";
        sql += " inner join parametroevento paramet on paramet.id=item.parametroevento_id and paramet.eventocontabil_id =:evento";
        sql += " where datalancamento >= :data ";
        Query q = getEntityManager().createNativeQuery(sql, LancamentoContabil.class);
        q.setParameter("data", data);
        q.setParameter("evento", obj.getId());

        if (q.getResultList().isEmpty()) {
            return null;
        }
        return q.getResultList();
    }

    public void adicionarObjetoParametro(EventoContabil eventoContabil, Date data, List<ObjetoParametro> listaObj, ItemParametroEvento item, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        ConfigAlienacaoAtivos configAlienacaoAtivos = buscarConfigVigente(eventoContabil, data);
        if (configAlienacaoAtivos != null) {
            listaObj.add(new ObjetoParametro(configAlienacaoAtivos.getGrupo().getId().toString(), GrupoBem.class.getSimpleName(), item, tipoObjetoParametro));
            listaObj.add(new ObjetoParametro(configAlienacaoAtivos.getTipoGrupo().toString(), TipoGrupo.class.getSimpleName(), item, tipoObjetoParametro));
        }
    }

    public void encerrarVigencia(ConfigAlienacaoAtivos entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEvento());
        super.salvar(entity);
    }
}
