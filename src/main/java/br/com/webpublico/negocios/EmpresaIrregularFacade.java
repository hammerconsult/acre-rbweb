package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.EmpresaIrregular;
import br.com.webpublico.entidades.MonitoramentoFiscal;
import br.com.webpublico.entidades.SituacaoEmpresaIrregular;
import br.com.webpublico.exception.ValidacaoException;
import org.hibernate.Hibernate;
import org.springframework.dao.support.DataAccessUtils;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;

@Stateless
public class EmpresaIrregularFacade extends AbstractFacade<EmpresaIrregular> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private IrregularidadeFacade irregularidadeFacade;
    @EJB
    private MonitoramentoFiscalFacade monitoramentoFiscalFacade;
    @Resource
    private SessionContext ctx;

    public IrregularidadeFacade getIrregularidadeFacade() {
        return irregularidadeFacade;
    }

    @Override
    public EmpresaIrregular recuperar(Object id) {
        return inicializar(super.recuperar(id));
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return inicializar(super.recuperar(id));
    }

    @Override
    public void salvar(EmpresaIrregular entity) {
        salvar(entity, true);
    }

    @Override
    public void salvarNovo(EmpresaIrregular entity) {
        salvar(entity);
    }

    public EmpresaIrregular salvarRetornando(EmpresaIrregular entity) {
        return em.merge(entity);
    }

    public MonitoramentoFiscalFacade getMonitoramentoFiscalFacade() {
        return monitoramentoFiscalFacade;
    }

    public void salvar(EmpresaIrregular entity, boolean lancarException) {
        EmpresaIrregular empresaIrregular = buscarEmpresaIrregularParaCadastroEconomico(entity.getCadastroEconomico());
        boolean deuErro = false;
        if (empresaIrregular == null) {
            if ((entity.getSituacao() == null || SituacaoEmpresaIrregular.Situacao.INSERIDA.equals(entity.getSituacao()))) {
                entity.setCodigo(buscarUltimoCodigoMaisUm());
                salvarRetornando(entity);
                return;
            } else {
                deuErro = true;
                if (lancarException) {
                    ValidacaoException ve = new ValidacaoException();
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível retirar a empresa pois ela não está inserida");
                    ve.lancarException();
                }
            }
        }
        if (!deuErro) {
            if (entity.getSituacao() == null) {
                entity.setSituacao(SituacaoEmpresaIrregular.Situacao.INSERIDA);
                SituacaoEmpresaIrregular empresa = entity.getUltimaSituacao(empresaIrregular.getSituacao());
                MonitoramentoFiscal monitoramento = em.find(MonitoramentoFiscal.class, empresa.getMonitoramentoFiscal().getId());
                monitoramento.setEmpresaIrregular(Boolean.TRUE);
            }
            salvarRetornando(entity);
        }
    }

    private EmpresaIrregular inicializar(EmpresaIrregular empresaIrregular) {
        Hibernate.initialize(empresaIrregular.getSituacoes());
        for (SituacaoEmpresaIrregular situ : empresaIrregular.getSituacoes()) {
            Hibernate.initialize(situ);
        }
        Hibernate.initialize(empresaIrregular.getCadastroEconomico());
        Hibernate.initialize(empresaIrregular.getCadastroEconomico().getEconomicoCNAE());
        Hibernate.initialize(empresaIrregular.getCadastroEconomico().getServico());
        Hibernate.initialize(empresaIrregular.getCadastroEconomico().getPessoa());
        Hibernate.initialize(empresaIrregular.getCadastroEconomico().getPessoa().getTelefones());
        return empresaIrregular;
    }

    public EmpresaIrregular adicionarNovaSituacao(EmpresaIrregular empresaIrregular) {
        SituacaoEmpresaIrregular situacaoEmpresaIrregular = new SituacaoEmpresaIrregular();
        situacaoEmpresaIrregular.setSituacao(empresaIrregular.getSituacao());
        situacaoEmpresaIrregular.setEmpresaIrregular(empresaIrregular);
        situacaoEmpresaIrregular.setData(new Date());
        situacaoEmpresaIrregular.setAnoProtocolo(empresaIrregular.getAnoProtocolo());
        situacaoEmpresaIrregular.setNumeroProtocolo(empresaIrregular.getNumeroProtocolo());
        situacaoEmpresaIrregular.setIrregularidade(empresaIrregular.getIrregularidade());
        situacaoEmpresaIrregular.setMonitoramentoFiscal(empresaIrregular.getMonitoramentoFiscal());
        situacaoEmpresaIrregular.setObservacao(empresaIrregular.getObservacao());
        situacaoEmpresaIrregular.setUsuarioSistema(empresaIrregular.getUsuarioSistema());
        empresaIrregular.getSituacoes().add(situacaoEmpresaIrregular);
        return empresaIrregular;
    }

    public Long buscarUltimoCodigoMaisUm() {
        Query q = em.createNativeQuery("select coalesce(max(codigo),0) + 1 from EMPRESAIRREGULAR");
        BigDecimal value = (BigDecimal) q.getSingleResult();
        return value.longValue();
    }

    public EmpresaIrregular buscarEmpresaIrregularParaCadastroEconomico(CadastroEconomico cadastroEconomico) {
        TypedQuery<EmpresaIrregular> q = em.createQuery("from EmpresaIrregular where cadastroEconomico = :cad", EmpresaIrregular.class);
        q.setParameter("cad", cadastroEconomico);
        q.setMaxResults(1);
        EmpresaIrregular empresaIrregular = DataAccessUtils.singleResult(q.getResultList());
        if (empresaIrregular != null) {
            inicializar(empresaIrregular);
        }
        return empresaIrregular;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmpresaIrregularFacade() {
        super(EmpresaIrregular.class);
    }

    public EntityManager getEm() {
        return em;
    }


}
