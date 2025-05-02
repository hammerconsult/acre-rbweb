/*
 * Codigo gerado automaticamente em Fri Feb 11 08:42:00 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.enums.tributario.SituacaoFaleConoscoNfse;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.nfse.domain.FaleConoscoNfse;
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
import java.util.Date;

@Stateless
public class FaleConoscoNfseFacade extends AbstractFacade<FaleConoscoNfse> {

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

    public FaleConoscoNfseFacade() {
        super(FaleConoscoNfse.class);
    }

    public LeitorWsConfig getLeitorWsConfig() {
        return leitorWsConfig;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void validarEnvioEmail(FaleConoscoNfse faleConosco) throws AddressException {
        ValidacaoException ve = new ValidacaoException();
        if (faleConosco.getDadosReclamante().getEmail() == null || faleConosco.getDadosReclamante().getEmail().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo E-mail é obrigatório.");
        } else {
            InternetAddress emailAddr = new InternetAddress(faleConosco.getDadosReclamante().getEmail());
            emailAddr.validate();
        }
        ve.lancarException();
    }

    public void validarCampoMensagemResponsta(FaleConoscoNfse faleConosco) {
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

    public void responder(FaleConoscoNfse faleConosco) throws MessagingException {
        validarCampoMensagemResponsta(faleConosco);
        faleConosco.setDataResposta(new Date());
        faleConosco.setUsuarioResposta(getSistemaFacade().getUsuarioCorrente());
        faleConosco.setSituacao(SituacaoFaleConoscoNfse.ENCERRADO);
        enviarEmail(faleConosco);
    }

    public void enviarEmail(FaleConoscoNfse faleConosco) throws MessagingException {
        validarEnvioEmail(faleConosco);

        String assunto = "RE: " + faleConosco.getAssunto();
        EmailService.getInstance().enviarEmail(faleConosco.getDadosReclamante().getEmail(), assunto, montarConteudoEmail(faleConosco).toString());

        salvar(faleConosco);
    }

    private StringBuilder montarConteudoEmail(FaleConoscoNfse faleConosco) {
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

    private String getNomeUsuario(FaleConoscoNfse faleConosco) {
        String nome = "";
        if (faleConosco.getUsuarioResposta() != null && faleConosco.getUsuarioResposta().getPessoaFisica() != null) {
            nome = faleConosco.getUsuarioResposta().getPessoaFisica().getPrimeiroNome() + " " + faleConosco.getUsuarioResposta().getPessoaFisica().getUltimoNome();
        }
        return nome;
    }
}
