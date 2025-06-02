package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 19/07/13
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ProvimentoReversaoFacade extends AbstractFacade<ProvimentoReversao> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SituacaoContratoFPFacade situacaoContratoFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private HorarioContratoFPFacade horarioContratoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;
    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProvimentoReversaoFacade() {
        super(ProvimentoReversao.class);
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public SituacaoContratoFPFacade getSituacaoContratoFPFacade() {
        return situacaoContratoFPFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public AposentadoriaFacade getAposentadoriaFacade() {
        return aposentadoriaFacade;
    }


    public EnquadramentoFuncionalFacade getEnquadramentoFuncionalFacade() {
        return enquadramentoFuncionalFacade;
    }

    public HorarioContratoFPFacade getHorarioContratoFPFacade() {
        return horarioContratoFPFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public CargoFacade getCargoFacade() {
        return cargoFacade;
    }


    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacadeNovo() {
        return hierarquiaOrganizacionalFacadeNovo;
    }

    public ConcessaoFeriasLicencaFacade getConcessaoFeriasLicencaFacade() {
        return concessaoFeriasLicencaFacade;
    }


    public boolean existeFolhaProcessadaParaContratoDepoisDe(ContratoFP contrato, Date data) {
        if (contrato == null || contrato.getId() == null) {
            return false;
        }
        String sql = "  select folha.id from folhadepagamento folha " +
            " inner join fichafinanceirafp ficha on folha.id = ficha.folhadepagamento_id " +
            " where ficha.vinculofp_id = :vinculo " +
            " and folha.calculadaem  >= to_date(:dataVigencia, 'dd/MM/yyyy')";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(data));
        q.setParameter("vinculo", contrato.getId());
        return !q.getResultList().isEmpty();
    }

    public List<CBO> listarFiltrandoPorCargo(String parte, Cargo cargo) {
        String hql = " select cbo from CBO cbo"
            + "    where (lower(cbo.descricao) like :filtro or lower(cbo.codigo) like :filtro)"
            + "    and :cargo in elements (cbo.cargos)";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("cargo", cargo);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public boolean isVinculoFPVivo(Long id) {
        String sql = "      select v.id                                                        " +
            "        from vinculofp        v                                          " +
            "  inner join matriculafp      m on m.id               = v.matriculafp_id " +
            "  inner join pessoafisica    pf on pf.id              = m.pessoa_id      " +
            "  inner join registrodeobito ro on ro.pessoafisica_id = pf.id            " +
            "       where v.id = :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        try {
            return q.getSingleResult() == null;
        } catch (NoResultException nre) {
            return true;
        }
    }

    public boolean hasAutorizacaoEspecialRH(UsuarioSistema usuarioSistema, TipoAutorizacaoRH tipo) {
        String sql = "select * from AUTORIZACAOUSUARIORH where tipoautorizacaorh = :tipo and usuariosistema_id = :usuario";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", tipo.name());
        q.setParameter("usuario", usuarioSistema.getId());
        return !q.getResultList().isEmpty();
    }

    public VinculoFP recuperarVinculoFPComDependenciaLotacaoFuncional(Object id) {
        VinculoFP vinculoFP = em.find(VinculoFP.class, id);
        vinculoFP.getLotacaoFuncionals().size();
        vinculoFP.getRecursosDoVinculoFP().size();
        if (vinculoFP.getLotacaoFuncionalVigente() != null && vinculoFP.getLotacaoFuncionalVigente().getHorarioContratoFP() != null) {
            vinculoFP.getLotacaoFuncionalVigente().getHorarioContratoFP().getLocalTrabalho().size();
        }

        return vinculoFP;
    }

    public EnquadramentoPCS recuperaValor(CategoriaPCS c, ProgressaoPCS p, Date vigencia) {
        EnquadramentoPCS e = null;
        Query q = em.createQuery("from EnquadramentoPCS e where e.categoriaPCS = :cat "
            + " and e.progressaoPCS = :pro "
            + " and :dataVigencia >= e.inicioVigencia "
            + " and :dataVigencia <= coalesce(e.finalVigencia,:dataVigencia) "
            + " order by e.vencimentoBase desc ");
        q.setParameter("cat", c);
        q.setParameter("pro", p);
        q.setParameter("dataVigencia", vigencia == null ? Util.getDataHoraMinutoSegundoZerado(new Date()) : vigencia);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            e = (EnquadramentoPCS) q.getSingleResult();
        }
        return e;
    }

    public List<ProgressaoPCS> getRaizPorPlanoECategoriaVigenteNoEnquadramento(PlanoCargosSalarios p, CategoriaPCS categoria, Date dataVigencia) {
        Query q = em.createQuery("select distinct g.superior from ProgressaoPCS g, EnquadramentoPCS enquadramento, CategoriaPCS categoria "
            + " where g.planoCargosSalarios = :p "
            + " and enquadramento.categoriaPCS = :cat "
            + " and enquadramento.progressaoPCS = g "
            + " and :dataVigencia between enquadramento.inicioVigencia "
            + " and coalesce(enquadramento.finalVigencia, :dataVigencia) ");

        try {
            q.setParameter("p", p);
            q.setParameter("cat", categoria);
            q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ProgressaoPCS recuperarProgressaoPCS(Long id) {
        ProgressaoPCS p = em.find(ProgressaoPCS.class, id);
        Hibernate.initialize(p.getFilhos());
        Hibernate.initialize(p.getMesesProgressao());
        return p;
    }

    public List<ProgressaoPCS> getFilhosDe(ProgressaoPCS pp, PlanoCargosSalarios p) {
        Query q = em.createQuery("from ProgressaoPCS p where p.superior = :superior and p.planoCargosSalarios = :parametro order by p.descricao asc");
        q.setParameter("superior", pp);
        q.setParameter("parametro", p);
        return q.getResultList();
    }

    public List<CategoriaPCS> recuperaCategoriasNoEnquadramentoFuncionalVigente(PlanoCargosSalarios pcs, Date dataVigencia) {
        String hql = " select distinct categoria from EnquadramentoPCS enquadramento "
            + " inner join enquadramento.categoriaPCS categoria "
            + " where categoria.planoCargosSalarios = :pcs "
            + " and :dataVigencia between enquadramento.inicioVigencia and coalesce(enquadramento.finalVigencia,:dataVigencia) "
            + " order by categoria.descricao";//
        Query q = em.createQuery(hql);                //
        q.setParameter("pcs", pcs);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));

        return q.getResultList();
    }

    public CategoriaPCS recuperarCategoriaPCS(Long id) {
        CategoriaPCS c = em.find(CategoriaPCS.class, id);
        Hibernate.initialize(c.getCargosCategoriaPCS());
        Hibernate.initialize(c.getMesesPromocao());
        Hibernate.initialize(c.getFilhos());
        for (CategoriaPCS categoriaPCS : c.getFilhos()) {
            Hibernate.initialize(categoriaPCS.getCargosCategoriaPCS());
        }
        if (c.temPlanoCargosSalarios()) {
            c.setPlanoCargosSalarios(recuperarPlanoCargosSalarios(c.getPlanoCargosSalarios().getId()));
        }
        return c;
    }

    public PlanoCargosSalarios recuperarPlanoCargosSalarios(Long id) {
        PlanoCargosSalarios p = em.find(PlanoCargosSalarios.class, id);
        p.getEntidadesPCS().size();
        p.getMesesProgressao().size();
        p.getMesesPromocao().size();
        Hibernate.initialize(p.getEntidadesPCS());
        return p;
    }

    public List<PlanoCargosSalarios> getPlanosPorQuadro(Date dataVigencia) {
        String hql = " from PlanoCargosSalarios a where :vigencia >= a.inicioVigencia and "
            + " :vigencia <= coalesce(a.finalVigencia,:vigencia) and a.tipoPCS <> :funcaoGratificada order by a.descricao";
        Calendar c = Calendar.getInstance();
        c.setTime(dataVigencia);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Query q = em.createQuery(hql);
        q.setParameter("vigencia", c.getTime());
        q.setParameter("funcaoGratificada", TipoPCS.FUNCAO_GRATIFICADA);
        return q.getResultList();
    }

    public TipoAposentadoria recuperaTipoAposentadoria(Aposentadoria aposentadoria) {
        String hql = "select tipo from Aposentadoria aposentadoria " +
            " inner join aposentadoria.tipoAposentadoria tipo " +
            " where aposentadoria = :aposentadoria";

        Query q =  em.createQuery(hql);
        q.setParameter("aposentadoria", aposentadoria);
        return (TipoAposentadoria) q.getSingleResult();
    }

    public TipoProvimento recuperaTipoProvimentoPorCodigo(int codigo) {
        Query q = getEntityManager().createQuery("from TipoProvimento tipo where tipo.codigo = :parametroCodigo");
        q.setParameter("parametroCodigo", codigo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (TipoProvimento) q.getSingleResult();
    }

    private void mergeObject(Object obj) {
        if (obj != null) {
            em.merge(obj);
        }
    }

    public void remover(ProvimentoReversao entity, ContratoFPCargo contratoFPCargo) {
        try {
            entity = getEntityManager().find(ProvimentoReversao.class, entity.getId());
            salvarRegistrosAteriores(entity);
            if (contratoFPCargo != null) {
                em.merge(contratoFPCargo);
            }
            super.remover(entity);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public void salvarNovo(ProvimentoReversao entity, Arquivo arquivo, FileUploadEvent fileUploadEvent) {
        try {
            if (fileUploadEvent != null) {
                entity.getLaudoProvimentoReversao().setArquivo(arquivo);
                arquivoFacade.salvar(arquivo, fileUploadEvent);
            }
            salvarRegistrosAteriores(entity);

            EnquadramentoFuncional enquadramentoFuncional = entity.getEnquadramentoFuncional();
            if (enquadramentoFuncional != null) {
                if (enquadramentoFuncional.getId() == null) {
                    getEntityManager().persist(enquadramentoFuncional);
                } else {
                    entity.setEnquadramentoFuncional(getEntityManager().merge(enquadramentoFuncional));
                }
            }
            salvarNovo(entity);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }

    }

    public void salvar(ProvimentoReversao entity, Arquivo arquivo, FileUploadEvent fileUploadEvent, Arquivo aRemover,
                       ContratoFPCargo ultimoContratoFPCargo) {
        try {
            if (arquivoFacade.verificaSeExisteArquivo(aRemover)) {
                arquivoFacade.removerArquivo(aRemover);
            }
            if (fileUploadEvent != null) {
                entity.getLaudoProvimentoReversao().setArquivo(arquivo);
                arquivoFacade.salvar(arquivo, fileUploadEvent);
            }
            salvarRegistrosAteriores(entity);

            if (ultimoContratoFPCargo != null) {
                em.merge(ultimoContratoFPCargo);
            }
            em.merge(entity);
        }catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void salvarRegistrosAteriores(ProvimentoReversao entity) {
        mergeObject(entity.getEnquadramentoFuncionalAnterior());
        mergeObject(entity.getModalidadeContratoFPDataAnterior());
        mergeObject(entity.getPrevidenciaAnterior());
        mergeObject(entity.getOpcaoValeTransporteAnterior());
        mergeObject(entity.getHorarioAnterior());
        mergeObject(entity.getLotacaoFuncionalAnterior());
        mergeObject(entity.getSindicatoAnterior());
        mergeObject(entity.getAssociacaoAnterior());
        mergeObject(entity.getContratoVinculoDeContratoAnterior());
        mergeObject(entity.getRecursoDoVinculoFPAnterior());
        mergeObject(entity.getContratoFPCargoAnterior());

    }

    public ProvimentoReversao recuperarProvimentoVigente(Aposentadoria aposentadoria) {
        try {
            String hql = "select pr from ProvimentoReversao pr " +
                " where pr.aposentadoria = :aposentadoria " +
                " and :dataVigencia >= pr.inicioVigencia and "
                + " :dataVigencia <= coalesce(pr.finalVigencia, :dataVigencia)";
            Query q = em.createQuery(hql);
            q.setParameter("aposentadoria", aposentadoria);
            q.setParameter("dataVigencia", new Date());
            return (ProvimentoReversao) q.getSingleResult();
        } catch (NoResultException nre) {
            throw new ExcecaoNegocioGenerica("Não foi possível recuperar o Provimento de Reversão do Servidor");
        }
    }

    public boolean temReversaoVigenteParaOContratoFP(ProvimentoReversao provimentoReversao, Date dataInicio, Date dataFim) {
        if (provimentoReversao.getContratoFP() == null) {
            return false;
        }
        String sql = "select 1 from ProvimentoReversao pr " +
            " inner join contratofp c on c.ID = pr.contratofp_id " +
            " where contratofp_id = :contratoFPID " +
            " and pr.id <> :prID " +
            " and ( (:dataInicio between pr.inicioVigencia and coalesce(pr.finalVigencia, :dataInicio)) " +
            "    or (coalesce(:dataFim, :dataInicio) between pr.inicioVigencia and coalesce(pr.finalVigencia, :dataInicio)) " +
            "    or (:dataInicio <= pr.inicioVigencia and coalesce(:dataFim, :dataInicio) >= coalesce(pr.finalVigencia, :dataInicio)) " +
            "    or (:dataFim is null and pr.inicioVigencia >= :dataInicio))";

        Query q = em.createNativeQuery(sql);
        q.setParameter("contratoFPID", provimentoReversao.getContratoFP().getId());
        q.setParameter("prID", provimentoReversao.getId() != null ? provimentoReversao.getId() : 0L);
        q.setParameter("dataInicio", dataInicio, TemporalType.DATE);
        q.setParameter("dataFim", dataFim, TemporalType.DATE);

        return !q.getResultList().isEmpty();
    }

    public List<ProvimentoReversao> buscarProvimentoReversaoPorVinculoFP(Long id) {
        String sql = "select reversao.* from ProvimentoReversao reversao " +
            "   where reversao.contratofp_id = :vinculoFP_id or reversao.aposentadoria_id = :vinculoFP_id " +
            "   order by reversao.iniciovigencia desc";
        Query q = em.createNativeQuery(sql, ProvimentoReversao.class);
        q.setParameter("vinculoFP_id", id);
        return q.getResultList();
    }

    public boolean podeExcluir(VinculoFP contrato, Date data) {
        if (contrato == null || contrato.getId() == null) {
            return true;
        } else {
            Query query = em.createQuery("from FolhaDePagamento folha inner join folha.fichaFinanceiraFPs ficha "
                + " where ficha.vinculoFP = :vinculo and "
                + " folha.ano >= :ano  and folha.mes >= :mes");
            query.setParameter("ano", DataUtil.getAno(data));
            query.setParameter("mes", Mes.getMesToInt(DataUtil.getMes(data)));
            query.setParameter("vinculo", contrato);
            return query.getResultList().isEmpty();
        }
    }

    public List<Aposentadoria> buscarAposentadoriaPorIdContratoVigente(Long id, LocalDate inicio, LocalDate fim) {
        String sql = "select *  " +
            " from  aposentadoria apo  " +
            " inner join vinculofp vinculo on vinculo.id = apo.id " +
            " inner join provimentoreversao provimento on provimento.aposentadoria_id = vinculo.id " +
            " where apo.contratofp_id = :vinculoFP_id  " +
            " and (to_date(to_char(:inicio, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(vinculo.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy')  and to_date(to_char(coalesce(vinculo.FINALVIGENCIA, :inicio), 'mm/yyyy'), 'mm/yyyy')  " +
            " or  to_date(to_char(:fim, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(vinculo.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') and to_date(to_char(coalesce(vinculo.FINALVIGENCIA, :fim), 'mm/yyyy'), 'mm/yyyy'))  " +
            " order by vinculo.inicioVigencia asc ";

        Query q = em.createNativeQuery(sql, Aposentadoria.class);
        q.setParameter("vinculoFP_id", id);
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public ProvimentoReversao recuperarUltimoprovimentoReversaoDaAposentadoria(Aposentadoria aposentadoria) {
        String sql = "select reversao.* from ProvimentoReversao reversao " +
            "   inner join aposentadoria apo on apo.id = reversao.aposentadoria_id " +
            "   where apo.id = :aposentadoria_id " +
            "   order by reversao.iniciovigencia desc";
        Query q = em.createNativeQuery(sql, ProvimentoReversao.class);
        q.setParameter("aposentadoria_id", aposentadoria.getId());
        q.setMaxResults(1);
        try {
            return (ProvimentoReversao) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ProvimentoReversao recuperarUltimoProvimentoReversaoDoContratoFP(ContratoFP contratoFP) {
        String sql = "select reversao.* from ProvimentoReversao reversao " +
            "   inner join contratofp contrato on contrato.id = reversao.contratofp_id " +
            "   where contrato.id = :contratoID " +
            "   order by reversao.iniciovigencia desc";
        Query q = em.createNativeQuery(sql, ProvimentoReversao.class);
        q.setParameter("contratoID", contratoFP.getId());
        q.setMaxResults(1);
        try {
            return (ProvimentoReversao) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean temProvimentoReversaoPorContratoFP(ContratoFP contratoFP) {
        if(contratoFP == null || contratoFP.getId() == null){
            return false;
        }

        String sql = "select 1 from ProvimentoReversao reversao " +
            "   inner join contratofp contrato on contrato.id = reversao.contratofp_id " +
            "   where contrato.id = :contratofp_id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratofp_id", contratoFP.getId());
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }

    public boolean temProvimentoReversaoVigentePorContratoFP(ContratoFP contratoFP, Date dataAposentadoria) {
        if(contratoFP == null || contratoFP.getId() == null){
            return false;
        }

        String sql = "select 1 from ProvimentoReversao reversao " +
            " inner join contratofp contrato on contrato.id = reversao.contratofp_id " +
            " where contrato.id = :contratofp_id " +
            " and :dataAposentadoria between reversao.iniciovigencia and coalesce(reversao.finalvigencia, :dataAposentadoria) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratofp_id", contratoFP.getId());
        q.setParameter("dataAposentadoria", dataAposentadoria);
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }

}
