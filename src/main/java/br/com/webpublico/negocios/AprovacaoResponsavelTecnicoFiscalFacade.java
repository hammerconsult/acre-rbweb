package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AprovacaoResponsavelTecnicoFiscal;
import br.com.webpublico.entidades.ObraTecnicoFiscal;
import br.com.webpublico.entidades.ResponsavelTecnicoFiscal;
import br.com.webpublico.entidades.SolicitacaoResponsaveltecnicoFiscal;
import br.com.webpublico.enums.SituacaoSolicitacaoFiscal;
import br.com.webpublico.enums.TipoFiscal;
import br.com.webpublico.enums.TipoResponsavelFiscal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Desenvolvimento on 31/03/2016.
 */
@Stateless
public class AprovacaoResponsavelTecnicoFiscalFacade extends AbstractFacade<AprovacaoResponsavelTecnicoFiscal> {

    private static final Logger logger = LoggerFactory.getLogger(AprovacaoResponsavelTecnicoFiscal.class);

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private AtoLegalFacade atoLegalFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public AprovacaoResponsavelTecnicoFiscalFacade() {
        super(AprovacaoResponsavelTecnicoFiscal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public void salvarNovo(AprovacaoResponsavelTecnicoFiscal aprovacao) throws ValidacaoException {
        boolean validou = true;
        if (AprovacaoResponsavelTecnicoFiscal.SituacaoAprovacao.APROVADA.equals(aprovacao.getSituacaoAprovacao()) &&
            TipoResponsavelFiscal.TECNICO.equals(aprovacao.getSolicitacao().getResponsavelTecnicoFiscal().getTipoResponsavel())) {
            ObraTecnicoFiscal tecnicoFiscal = new ObraTecnicoFiscal();
            tecnicoFiscal.setObra(aprovacao.getSolicitacao().getObra());
            tecnicoFiscal.setTecnicoFiscal(aprovacao.getSolicitacao().getResponsavelTecnicoFiscal());
            validou = DataUtil.isVigenciaValida(tecnicoFiscal, buscarResponsavelTecnicoFiscalAprovados(aprovacao.getSolicitacao()));
            if (validou) {
                em.merge(tecnicoFiscal);
            } else {
                throw new ValidacaoException("");
            }
        }
        if (validou) {
            aprovacao.setCodigo(singletonGeradorCodigo.getProximoCodigo(AprovacaoResponsavelTecnicoFiscal.class, "codigo"));
            aprovacao.getSolicitacao().setSituacaoSolicitacao(SituacaoSolicitacaoFiscal.valueOf(aprovacao.getSituacaoAprovacao().name()));
            em.merge(aprovacao.getSolicitacao());
            super.salvarNovo(aprovacao);
        }
    }

    public List<ObraTecnicoFiscal> buscarResponsavelTecnicoFiscalAprovados(SolicitacaoResponsaveltecnicoFiscal solicitacao) {
        String hql = "select  o " +
            "           from ObraTecnicoFiscal o " +
            "         where o.obra = :obra " +
            "           and ( o.tecnicoFiscal.contrato = :contrato " +
            "              or o.tecnicoFiscal.contratoFP = :contratoFp)" +
            "           and o.tecnicoFiscal.tipoResponsavel = :tipo";
        Query q = em.createQuery(hql, ObraTecnicoFiscal.class);
        q.setParameter("obra", solicitacao.getObra());
        q.setParameter("tipo", solicitacao.getResponsavelTecnicoFiscal().getTipoResponsavel());
        q.setParameter("contrato", solicitacao.getResponsavelTecnicoFiscal().getContrato() != null ? solicitacao.getResponsavelTecnicoFiscal().getContrato() : null);
        q.setParameter("contratoFp", solicitacao.getResponsavelTecnicoFiscal().getContratoFP() != null ? solicitacao.getResponsavelTecnicoFiscal().getContratoFP() : null);

        return q.getResultList();
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }
}
