package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.ResponsavelTecnicoFiscal;
import br.com.webpublico.enums.TipoResponsavelFiscal;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Desenvolvimento on 29/03/2016.
 */
@Stateless
public class ResponsavelTecnicoFiscalFacade extends AbstractFacade<ResponsavelTecnicoFiscal> {

    private static final Logger logger = LoggerFactory.getLogger(ResponsavelTecnicoFiscalFacade.class);

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ProfissionalConfeaFacade profissionalConfeaFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ResponsavelTecnicoFiscalFacade() {
        super(ResponsavelTecnicoFiscal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public ProfissionalConfeaFacade getProfissionalConfeaFacade() {
        return profissionalConfeaFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public ResponsavelTecnicoFiscal recuperar(Object id) {
        ResponsavelTecnicoFiscal entity = super.recuperar(id);
        if (entity.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(entity.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        return entity;
    }


    public List<ResponsavelTecnicoFiscal> buscarResponsavelPorTiposVigentes(String parte, TipoResponsavelFiscal tipoResponsavel) {
        String sql = "SELECT DISTINCT r.*" +
            "   from ResponsavelTecnicoFiscal r" +
            " left join contrato c on c.id = r.contrato_id" +
            " left join exercicio ex on ex.id = c.exerciciocontrato_id" +
            " left join pessoafisica pf on pf.id = c.contratado_id" +
            " left join pessoajuridica pj on pj.id = c.contratado_id" +
            " left join contratofp servidor on servidor.id = r.contratofp_id" +
            " left join VinculoFP v on v.id = servidor.id  " +
            " left join MatriculaFP mtr on mtr.id = v.matriculafp_id" +
            " left join pessoafisica pf_servidor on pf_servidor.id = mtr.pessoa_id" +
            "   where r.tiporesponsavel = :tipoResponsavel" +
//            "     and r.tipofiscal = :tipoFiscal" +
//            "     and :dataOperacao BETWEEN r.INICIOVIGENCIA and coalesce(r.FIMVIGENCIA, :dataOperacao) " +
            "     and (((lower(pf.nome) like :parte) or (replace(replace(pf.cpf,'.',''),'-','') like :parte))" +
            "     or ((lower(pf_servidor.nome) like :parte) or (replace(replace(pf_servidor.cpf,'.',''),'-','') like :parte))" +
            "     or (lower(pj.razaoSocial) like :parte) or (pj.cnpj like :parte) or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte))";
        Query q = em.createNativeQuery(sql, ResponsavelTecnicoFiscal.class);
        q.setParameter("tipoResponsavel", tipoResponsavel.name());
//        q.setParameter("tipoFiscal", tipoFiscal.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
//        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao(), TemporalType.DATE);
        return q.getResultList();
    }

    public List<ResponsavelTecnicoFiscal> buscarListaDeResponsavelTecnicoFiscalPorResponsavel(ResponsavelTecnicoFiscal responsavel) {
        String hql = "select r " +
            "           from ResponsavelTecnicoFiscal r" +
            "         where ((r.contrato = :contrato) " +
            "              or (r.contratoFP = :contratoFP))" +
            "            and r.tipoResponsavel = :tipo ";
        Query q = em.createQuery(hql);
        q.setParameter("contrato", responsavel.getContrato());
        q.setParameter("contratoFP", responsavel.getContratoFP());
        q.setParameter("tipo", responsavel.getTipoResponsavel());
        return q.getResultList();
    }

    public List<ResponsavelTecnicoFiscal> buscarResponsaveisTecnicosFiscaisPorServidor(ContratoFP contrato) {
        String sql = " select r.* from ResponsavelTecnicoFiscal r " +
            " where r.CONTRATOFP_ID = :contrato ";
        Query q = em.createNativeQuery(sql, ResponsavelTecnicoFiscal.class);
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }
}
