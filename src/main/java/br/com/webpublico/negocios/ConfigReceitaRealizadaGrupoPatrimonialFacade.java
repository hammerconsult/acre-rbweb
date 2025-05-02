package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigReceitaRealizadaBensMoveis;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaReceita;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class ConfigReceitaRealizadaGrupoPatrimonialFacade extends AbstractFacade<ConfigReceitaRealizadaBensMoveis> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private ContaFacade contaFacade;

    public ConfigReceitaRealizadaGrupoPatrimonialFacade() {
        super(ConfigReceitaRealizadaBensMoveis.class);
    }

    public void encerrarVigencia(ConfigReceitaRealizadaBensMoveis entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), null);
        super.salvar(entity);
    }

    public boolean hasConfiguracaoExistente(ConfigReceitaRealizadaBensMoveis configReceitaRealizadaBensMoveis) {
        String sql = "SELECT CR.* "
            + " FROM CONFIGRECREALIZADABENSMOV CR "
            + " WHERE cr.contareceita_id = :conta "
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(cr.INICIOVIGENCIA) AND coalesce(trunc(cr.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (configReceitaRealizadaBensMoveis.getId() != null) {
            sql += " AND cr.ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfigReceitaRealizadaBensMoveis.class);
        q.setParameter("conta", configReceitaRealizadaBensMoveis.getContaReceita().getId());
        q.setParameter("data", DataUtil.getDataFormatada(configReceitaRealizadaBensMoveis.getInicioVigencia()));
        if (configReceitaRealizadaBensMoveis.getId() != null) {
            q.setParameter("config", configReceitaRealizadaBensMoveis.getId());
        }
        return q.getResultList() != null && !q.getResultList().isEmpty();
    }

    public ConfigReceitaRealizadaBensMoveis buscarConfiguracaoPorContaReceita(Conta contaReceita, Date data) {
        String sql = "SELECT CR.* "
            + " FROM CONFIGRECREALIZADABENSMOV CR "
            + " WHERE cr.contareceita_id = :conta "
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(cr.INICIOVIGENCIA) AND coalesce(trunc(cr.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigReceitaRealizadaBensMoveis.class);
        q.setParameter("conta", contaReceita.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (ConfigReceitaRealizadaBensMoveis) q.getSingleResult();
        }
        return null;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }
}
