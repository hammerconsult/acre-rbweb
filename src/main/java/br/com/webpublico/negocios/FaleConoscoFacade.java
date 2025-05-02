/*
 * Codigo gerado automaticamente em Fri Feb 11 08:42:00 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FaleConosco;
import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.EmailService;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FaleConoscoFacade extends AbstractFacade<FaleConosco> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LeitorWsConfig leitorWsConfig;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FaleConoscoFacade() {
        super(FaleConosco.class);
    }

    public void salvarFaleConosco(FaleConosco faleConosco) {
        salvarNotificaoFaleConosco(em.merge(faleConosco));
    }

    public void salvarNotificaoFaleConosco(FaleConosco faleConosco) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Acesse o link para visualizar a mensagem.");
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo("Fale Conosco ");
        notificacao.setTipoNotificacao(TipoNotificacao.FALE_CONOSCO_PORTAL_WEB);
        notificacao.setLink("/fale-conosco/ver/" + faleConosco.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public LeitorWsConfig getLeitorWsConfig() {
        return leitorWsConfig;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void criarNotificar(FaleConosco entity) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Existe uma nova mensagem do Fale Conosco, com o assunto: " + entity.getAssunto() + ".");
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo(TipoNotificacao.FALE_CONOSCO_PORTAL_NFSE.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.FALE_CONOSCO_PORTAL_NFSE);
        notificacao.setLink("/fale-conosco/ver/" + entity.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public void validarEnvioEmail(FaleConosco faleConosco) throws AddressException {
        ValidacaoException ve = new ValidacaoException();
        if (faleConosco.getEmail() == null || faleConosco.getEmail().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo E-mail é obrigatório.");
        } else {
            InternetAddress emailAddr = new InternetAddress(faleConosco.getEmail());
            emailAddr.validate();
        }
        ve.lancarException();
    }

    public void validarCampoMensagemResponsta(FaleConosco faleConosco) {
        ValidacaoException ve = new ValidacaoException();
        if (faleConosco.getResposta() == null || faleConosco.getResposta().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Resposta é obrigatório.");
        }
        ve.lancarException();
        if (faleConosco.getResposta().length() > 3000) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Resposta deve possuir no máximo 3.000 caracteres.");
        }
        ve.lancarException();
    }

    public void enviarEmail(FaleConosco faleConosco) throws MessagingException {
        validarEnvioEmail(faleConosco);

        String assunto = "RE: " + faleConosco.getAssunto();
        EmailService.getInstance().enviarEmail(faleConosco.getEmail(), assunto, montarConteudoEmail(faleConosco).toString());

        salvar(faleConosco);
    }

    private StringBuilder montarConteudoEmail(FaleConosco faleConosco) {
        StringBuilder str = new StringBuilder();
        String br = "<br/>";

        str.append("<div style='color: #497692; font-size: 15px'>Pergunta</div>");
        str.append("<div style='text-align: justify'>").append(faleConosco.getMensagem()).append("</div>").append(br).append(br);

        str.append("<div style='color: #497692; font-size: 15px'>Resposta</div>");
        str.append(faleConosco.getResposta()).append(br).append(br).append(br);

        str.append("Respondido por: ").append(getNomeUsuario(faleConosco)).append(br);
        str.append("Em: ").append(DataUtil.getDataFormatada(faleConosco.getDataResposta())).append(" às ")
            .append(Util.hourToString(faleConosco.getDataResposta())).append(br).append(br);

        str.append("Atenciosamente,").append(br);
        str.append("Suporte da Prefeitura Municipal de Rio Branco").append(br);
        str.append("Telefone para contato: (68) 3211-3974").append(br);
        str.append("Este é um e-mail automático. Por favor não o responda.");
        return str;
    }

    private String getNomeUsuario(FaleConosco faleConosco) {
        String nome = "";
        if (faleConosco.getUsuarioSistema() != null && faleConosco.getUsuarioSistema().getPessoaFisica() != null) {
            nome = faleConosco.getUsuarioSistema().getPessoaFisica().getPrimeiroNome() + " " + faleConosco.getUsuarioSistema().getPessoaFisica().getUltimoNome();
        }
        return nome;
    }
}
