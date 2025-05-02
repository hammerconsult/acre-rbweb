package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.RespostaFormularioCampo;
import br.com.webpublico.entidades.comum.TemplateEmail;
import br.com.webpublico.entidades.comum.TipoTemplateEmail;
import br.com.webpublico.entidades.comum.trocatag.TrocaTagRegistroExigenciaETR;
import br.com.webpublico.enums.SituacaoLicenciamentoETR;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.comum.FormularioFacade;
import br.com.webpublico.negocios.comum.TemplateEmailFacade;
import br.com.webpublico.util.EmailService;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class RequerimentoLicenciamentoETRFacade extends AbstractFacade<RequerimentoLicenciamentoETR> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TemplateEmailFacade templateEmailFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private ParametroETRFacade parametroETRFacade;
    @EJB
    private FormularioFacade formularioFacade;

    public RequerimentoLicenciamentoETRFacade() {
        super(RequerimentoLicenciamentoETR.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public FormularioFacade getFormularioFacade() {
        return formularioFacade;
    }

    @Override
    public RequerimentoLicenciamentoETR recuperar(Object id) {
        RequerimentoLicenciamentoETR requerimento = super.recuperar(id);
        Hibernate.initialize(requerimento.getCadastroEconomico().getPessoa().getTelefones());
        Hibernate.initialize(requerimento.getCadastroEconomico().getPessoa().getEnderecos());
        Hibernate.initialize(requerimento.getCadastroEconomico().getEconomicoCNAE());
        Hibernate.initialize(requerimento.getRequerimentoLicenciamentoETRRespostaFormularioList());
        if (requerimento.getRequerimentoLicenciamentoETRRespostaFormularioList() != null) {
            for (RequerimentoLicenciamentoETRRespostaFormulario requerimentoLicenciamentoETRRespostaFormulario : requerimento.getRequerimentoLicenciamentoETRRespostaFormularioList()) {
                if (requerimentoLicenciamentoETRRespostaFormulario.getRespostaFormulario() != null) {
                    Hibernate.initialize(requerimentoLicenciamentoETRRespostaFormulario.getRespostaFormulario().getRespostaFormularioCampoList());
                    if (requerimentoLicenciamentoETRRespostaFormulario.getRespostaFormulario().getRespostaFormularioCampoList() != null) {
                        for (RespostaFormularioCampo respostaFormularioCampo : requerimentoLicenciamentoETRRespostaFormulario.getRespostaFormulario().getRespostaFormularioCampoList()) {
                            Hibernate.initialize(respostaFormularioCampo.getFormularioCampo().getFormularioCampoOpcaoList());
                        }
                    }
                }
            }
        }
        Hibernate.initialize(requerimento.getExigenciaETRList());
        if (requerimento.getExigenciaETRList() != null) {
            for (ExigenciaETR exigenciaETR : requerimento.getExigenciaETRList()) {
                Hibernate.initialize(exigenciaETR.getExigenciaETRFormularioList());
                if (exigenciaETR.getExigenciaETRFormularioList() != null) {
                    for (ExigenciaETRFormulario exigenciaETRFormulario : exigenciaETR.getExigenciaETRFormularioList()) {
                        if (exigenciaETRFormulario.getRespostaFormulario() != null) {
                            Hibernate.initialize(exigenciaETRFormulario.getRespostaFormulario().getRespostaFormularioCampoList());
                            if (exigenciaETRFormulario.getRespostaFormulario().getRespostaFormularioCampoList() != null) {
                                for (RespostaFormularioCampo respostaFormularioCampo : exigenciaETRFormulario.getRespostaFormulario().getRespostaFormularioCampoList()) {
                                    Hibernate.initialize(respostaFormularioCampo.getFormularioCampo().getFormularioCampoOpcaoList());
                                }
                            }
                        }
                    }
                }
            }
        }
        Hibernate.initialize(requerimento.getHistoricoList());
        Hibernate.initialize(requerimento.getAceiteList());
        return requerimento;
    }

    public void registrarExigencia(ExigenciaETR exigenciaETR) {
        exigenciaETR = em.merge(exigenciaETR);
        exigenciaETR.setRequerimentoLicenciamentoETR(recuperar(exigenciaETR.getRequerimentoLicenciamentoETR().getId()));
        exigenciaETR.getRequerimentoLicenciamentoETR().adicionarHistorico(new Date(), SituacaoLicenciamentoETR.EM_EXIGENCIA);
        exigenciaETR.setRequerimentoLicenciamentoETR(em.merge(exigenciaETR.getRequerimentoLicenciamentoETR()));
        enviarEmailRegistroExigencia(exigenciaETR);

    }

    public void enviarEmailRegistroExigencia(ExigenciaETR exigenciaETR) {
        TemplateEmail templateEmail = templateEmailFacade.findByTipoTemplateEmail(TipoTemplateEmail.REGISTRO_EXIGENCIA_ETR);
        if (templateEmail == null) {
            logger.error("Template de e-mail não configurado. {}", TipoTemplateEmail.REGISTRO_EXIGENCIA_ETR);
            return;
        }
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        ConfiguracaoEmail configuracaoEmail = configuracaoEmailFacade.recuperarUtilmo();
        EmailService.getInstance().enviarEmail(exigenciaETR.getRequerimentoLicenciamentoETR().getEmail(),
            templateEmail.getAssunto(),
            new TrocaTagRegistroExigenciaETR(configuracaoTributario, exigenciaETR)
                .trocarTags(templateEmail.getConteudo()));
    }

    public void alterarSituacao(RequerimentoLicenciamentoETR requerimento, SituacaoLicenciamentoETR situacao) {
        requerimento.adicionarHistorico(new Date(), situacao);
        switch (situacao) {
            case APROVADO: {
                aprovarRequerimento(requerimento);
            }
        }
        requerimento.setDataEfetivacao(new Date());
        requerimento.setUsuarioEfetivacao(sistemaFacade.getUsuarioCorrente());
        requerimento.setSituacao(situacao);
        salvar(requerimento);
    }

    private void aprovarRequerimento(RequerimentoLicenciamentoETR requerimento) {
        requerimento.setDispensaAlvara(isRequerimentoDispensaAlvara(requerimento));
    }

    private boolean isRequerimentoDispensaAlvara(RequerimentoLicenciamentoETR requerimento) {
        ParametroETR parametroETR = parametroETRFacade.recuperarParametroETR();
        for (ParametroETRFormulario parametroETRFormulario : parametroETR.getParametroETRFormularioList()) {
            for (ParametroETRFormularioCampoDispensa formularioCampoDispensa : parametroETRFormulario.getFormularioCampoDispensaList()) {
                RespostaFormularioCampo respostaFormularioCampo = requerimento.buscarRespostaFormularioCampo(formularioCampoDispensa.getFormularioCampo());
                if (respostaFormularioCampo.getResposta() != null && respostaFormularioCampo.getResposta().equals(formularioCampoDispensa.getResposta())) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<RequerimentoLicenciamentoETR> buscarPorCadastroEconomico(CadastroEconomico cadastroEconomico) {
        Query q = em.createQuery(" select r from RequerimentoLicenciamentoETR r " +
                " where r.cadastroEconomico = :cadastroEconomico " +
                " order by r.exercicio.ano, r.codigo ");
        q.setParameter("cadastroEconomico", cadastroEconomico);
        return q.getResultList();
    }

    public RequerimentoLicenciamentoETR salvarRetornado(RequerimentoLicenciamentoETR selecionado) {
        return em.merge(selecionado);
    }

    public void aceitarRequerimento(RequerimentoLicenciamentoETR requerimento, UnidadeOrganizacional unidadeOrganizacional) {
        RequerimentoLicenciamentoETRAceite aceite = new RequerimentoLicenciamentoETRAceite();
        aceite.setRequerimento(requerimento);
        aceite.setUnidadeAceite(unidadeOrganizacional);
        aceite.setUsuarioAceite(sistemaFacade.getUsuarioCorrente());
        aceite.setDataAceite(new Date());
        requerimento.getAceiteList().add(aceite);
        salvar(requerimento);
    }
}
