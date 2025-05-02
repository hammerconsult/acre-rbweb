package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.service.S2220Service;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

/**
 * Created by carlos on 22/06/15.
 */
@Stateless
public class ASOFacade extends AbstractFacade<ASO> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private MedicoFacade medicoFacade;
    @EJB
    private ExameFacade exameFacade;
    @EJB
    private JornadaDeTrabalhoFacade jornadaDeTrabalhoFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private RiscoFacade riscoFacade;
    @EJB
    private FatorDeRiscoFacade fatorDeRiscoFacade;
    @EJB
    private ProtecaoEPIFacade protecaoEPIFacade;
    @EJB
    private EquipamentoEPIFacade equipamentoEPIFacade;
    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ASOFacade() {
        super(ASO.class);
        s2220Service = (S2220Service) Util.getSpringBeanPeloNome("s2220Service");
    }

    @Override
    public void salvar(ASO entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(ASO entity) {
        super.salvarNovo(entity);
    }

    private S2220Service s2220Service;




    public List<ASO> listaPorServidor(ContratoFP contratoFP) {
        String hql = " select aso from ASO where contratoFP = :contratoFP ";
        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", contratoFP);
        List<ASO> lista = q.getResultList();
        return lista;
    }

    @Override
    public ASO recuperar(Object id) {
        ASO aso = super.recuperar(id);
        if (aso != null) {
            aso.getExameComplementares().size();
            aso.getRiscos().size();
            aso.getEquipamentosPCMSO().size();
        }

        return aso;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public ExameFacade getExameFacade() {
        return exameFacade;
    }

    public MedicoFacade getMedicoFacade() {
        return medicoFacade;
    }

    public JornadaDeTrabalhoFacade getJornadaDeTrabalhoFacade() {
        return jornadaDeTrabalhoFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public RiscoFacade getRiscoFacade() {
        return riscoFacade;
    }

    public FatorDeRiscoFacade getFatorDeRiscoFacade() {
        return fatorDeRiscoFacade;
    }

    public ProtecaoEPIFacade getProtecaoEPIFacade() {
        return protecaoEPIFacade;
    }

    public EquipamentoEPIFacade getEquipamentoEPIFacade() {
        return equipamentoEPIFacade;
    }

    public List<ASO> buscarBeneficiariosPorEntidade(ConfiguracaoEmpregadorESocial empregadorESocial, String parte) {
        String sql = "select aso.* from aso " +
            "inner join vinculofp vinculo on ASO.CONTRATOFP_ID = vinculo.ID " +
            "inner join matriculafp mat on vinculo.MATRICULAFP_ID = mat.ID " +
            "inner join pessoafisica pf on mat.PESSOA_ID = pf.ID " +
            "WHERE  vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades) and " +
            "       (lower(pf.nome) like :filtro) or " +
            "       (lower(pf.cpf) like :filtro) or " +
            "       (lower(mat.matricula) like :filtro) ";

        Query q = em.createNativeQuery(sql, ASO.class);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    private List<Long> montarIdOrgaoEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        List<Long> ids = Lists.newArrayList();
        for (ItemConfiguracaoEmpregadorESocial item : empregador.getItemConfiguracaoEmpregadorESocial()) {
            ids.add(item.getUnidadeOrganizacional().getId());
        }
        return ids;
    }

    public void enviarS2220ESocial(ConfiguracaoEmpregadorESocial empregador, ASO aso) throws ValidacaoException {
        PrestadorServicos prestadorServicos = prestadorServicosFacade.buscarPrestadorServicosPorIdPessoa(Optional.ofNullable(aso).map(ASO::getContratoFP).map(ContratoFP::getMatriculaFP).map(MatriculaFP::getPessoa).map(Pessoa::getId).orElse(null));
        s2220Service.enviarS2220(empregador, recuperar(aso.getId()), prestadorServicos);
    }
}
