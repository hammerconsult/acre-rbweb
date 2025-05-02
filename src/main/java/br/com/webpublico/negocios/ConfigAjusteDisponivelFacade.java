package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AjusteAtivoDisponivel;
import br.com.webpublico.entidades.ConfigAjusteAtivoDisponivel;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * @author Rafael Major
 */
@Stateless
public class ConfigAjusteDisponivelFacade extends AbstractFacade<ConfigAjusteAtivoDisponivel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private AjusteAtivoDisponivelFacade ajusteAtivoDisponivelFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigAjusteDisponivelFacade() {
        super(ConfigAjusteAtivoDisponivel.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ConfigAjusteAtivoDisponivel verificaConfiguracaoExistente(ConfigAjusteAtivoDisponivel config, Date dataVigencia) {
        String sql = " SELECT CE.*, CD.* ";
        sql += " FROM CONFIGAJUSTEATIVODISP CD ";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CD.ID = CE.ID ";
        sql += " WHERE CE.TIPOLANCAMENTO = :tipoLancamento ";
        sql += " AND CD.TIPOAJUSTEDISPONIVEL = :tipoAjustedisp ";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += "and cd.id <> :idConfig";
        }
        Query q = em.createNativeQuery(sql, ConfigAjusteAtivoDisponivel.class);
        q.setParameter("tipoLancamento", config.getTipoLancamento().name());
        q.setParameter("tipoAjustedisp", config.getTipoAjusteDisponivel().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("idConfig", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigAjusteAtivoDisponivel) q.getSingleResult();
        }
        return null;
    }

    public ConfigAjusteAtivoDisponivel getUltimaConfiguracao(AjusteAtivoDisponivel ajuste) {
        try {
            Preconditions.checkNotNull(ajuste.getTipoAjusteDisponivel(),"Tipo de Ajuste Ativo Disponível está vazio!");
            String sql = " SELECT CE.*, c.* FROM CONFIGAJUSTEATIVODISP C "
                    + " INNER JOIN CONFIGURACAOEVENTO CE on CE.id = c.id "
                    + " WHERE to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
                    + " and CE.TIPOLANCAMENTO = :tipolancamento "
                    + " and C.TIPOAJUSTEDISPONIVEL = :tipoajustedisp "
                    + " order by CE.FIMVIGENCIA";
            Query q = em.createNativeQuery(sql, ConfigAjusteAtivoDisponivel.class);
            q.setParameter("tipolancamento", ajuste.getTipoLancamento().name());
            q.setParameter("tipoajustedisp", ajuste.getTipoAjusteDisponivel().name());
            q.setParameter("data",  DataUtil.getDataFormatada(ajuste.getDataAjuste()));
            if (q.getResultList() != null && !q.getResultList().isEmpty()) {
                return (ConfigAjusteAtivoDisponivel) q.getSingleResult();
            } else {
                throw new ExcecaoNegocioGenerica("Nenhum Evento Encontrado para o Tipo de Lançamento: " + ajuste.getTipoLancamento().getDescricao() + "; Tipo de Ajuste: " + ajuste.getTipoAjusteDisponivel().getDescricao() + "; Data: " + DataUtil.getDataFormatada(ajuste.getDataAjuste()));
            }
        }   catch (Exception e){
            throw new ExcecaoNegocioGenerica(e.getMessage());

        }


    }
    public AjusteAtivoDisponivelFacade getAjusteAtivoDisponivelFacade() {
        return ajusteAtivoDisponivelFacade;
    }

    @Override
    public ConfigAjusteAtivoDisponivel recuperar(Object id) {
        ConfigAjusteAtivoDisponivel caad = em.find(ConfigAjusteAtivoDisponivel.class, id);
        return caad;
    }

    public void verificaAlteracoesEvento(ConfigAjusteAtivoDisponivel confgAjustAtiDisNaoAlterado, ConfigAjusteAtivoDisponivel caad) {

        if (caad.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!confgAjustAtiDisNaoAlterado.getTipoLancamento().equals(caad.getTipoLancamento())) {
            alterou = true;
        }
        if (!confgAjustAtiDisNaoAlterado.getTipoAjusteDisponivel().equals(caad.getTipoAjusteDisponivel())) {
            alterou = true;
        }
        if (!confgAjustAtiDisNaoAlterado.getInicioVigencia().equals(caad.getInicioVigencia())) {
            alterou = true;
        }
        if (!confgAjustAtiDisNaoAlterado.getEventoContabil().equals(caad.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(caad.getEventoContabil(), caad.getId(), caad.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(confgAjustAtiDisNaoAlterado.getEventoContabil(), confgAjustAtiDisNaoAlterado.getId(), confgAjustAtiDisNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(caad.getEventoContabil(), caad.getId(), caad.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigAjusteAtivoDisponivel confgAjustAtiDisNaoAlterado, ConfigAjusteAtivoDisponivel configuracaoEncontrada) {
        verificaAlteracoesEvento(confgAjustAtiDisNaoAlterado, configuracaoEncontrada);
        if (configuracaoEncontrada.getId() == null) {
            salvarNovo(configuracaoEncontrada);
        } else {
            salvar(configuracaoEncontrada);
        }
    }

    public void encerrarVigencia(ConfigAjusteAtivoDisponivel entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
