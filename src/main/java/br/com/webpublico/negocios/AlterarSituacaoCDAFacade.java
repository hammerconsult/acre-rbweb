package br.com.webpublico.negocios;

import br.com.webpublico.controle.forms.FormAlterarSituacaoCDAControlador;
import br.com.webpublico.entidades.AlteracaoSituacaoCDA;
import br.com.webpublico.entidades.CertidaoDividaAtiva;
import br.com.webpublico.entidades.ComunicacaoSoftPlan;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.SituacaoCertidaoDA;
import br.com.webpublico.enums.SituacaoJudicial;
import br.com.webpublico.enums.TipoAlteracaoCertidaoDA;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Stateless
public class AlterarSituacaoCDAFacade extends AbstractFacade<AlteracaoSituacaoCDA> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private ComunicaSofPlanFacade comunicaSofPlanFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AlterarSituacaoCDAFacade() {
        super(AlteracaoSituacaoCDA.class);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void alterarCDAComuicarProcuradoria(CertidaoDividaAtiva certidao, FormAlterarSituacaoCDAControlador form, Date dataAtual, UsuarioSistema usuarioCorrente) {
        try {
            certidao = alterarCertidaoConformeTipoAlteracao(certidao, form.getTipoAlteracaoCertidaoDA());
            certidao = certidaoDividaAtivaFacade.salvaCertidao(certidao);
            AlteracaoSituacaoCDA altera = new AlteracaoSituacaoCDA(form.getTipoAlteracaoCertidaoDA(), dataAtual, usuarioCorrente, form.getNrProtocolo(), form.getAnoProtocolo(), retornaUltimoCodigoLong(), form.getMotivo(), certidao);
            salvar(altera);

            comunicaSofPlanFacade.alterarCDA(certidao);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private CertidaoDividaAtiva alterarCertidaoConformeTipoAlteracao(CertidaoDividaAtiva certidao, TipoAlteracaoCertidaoDA tipoAlteracaoCertidaoDA) {
        switch (tipoAlteracaoCertidaoDA) {
            case CANCELAR:
                certidao.setSituacaoCertidaoDA(SituacaoCertidaoDA.CANCELADA);
                break;
            case SUSPENDER:
                certidao.setSituacaoCertidaoDA(SituacaoCertidaoDA.SUSPENSA);
                break;
            case REATIVAR:
                certidao.setSituacaoCertidaoDA(SituacaoCertidaoDA.ABERTA);
                break;
            case AJUIZAR:
                certidao.setSituacaoJudicial(SituacaoJudicial.AJUIZADA);
                break;
            case PENHORAR:
                certidao.setSituacaoCertidaoDA(SituacaoCertidaoDA.PENHORA_REALIZADA);
                break;
        }
        return certidao;
    }

    @Override
    public AlteracaoSituacaoCDA recuperar(Object id) {
        AlteracaoSituacaoCDA recuperar = super.recuperar(id);
        Hibernate.initialize(recuperar.getCertidao().getPessoa());
        Hibernate.initialize(recuperar.getCertidao().getPessoa().getEnderecos());
        Hibernate.initialize(recuperar.getCertidao().getPessoa().getTelefones());
        Hibernate.initialize(recuperar.getCertidao().getPessoa().getRg_InscricaoEstadual());
        return recuperar;
    }
}
