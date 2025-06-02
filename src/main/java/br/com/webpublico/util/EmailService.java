package br.com.webpublico.util;

import br.com.webpublico.entidades.ConfiguracaoEmail;
import br.com.webpublico.entidadesauxiliares.email.AnexoDTO;
import br.com.webpublico.entidadesauxiliares.email.EmailDTO;
import br.com.webpublico.message.RabbitMQService;
import br.com.webpublico.negocios.SistemaFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.util.List;


public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final String ENVIAR_EMAIL_QUEUE_NAME = "enviarEmail";
    private final String ATUALIZAR_CONFIGURACAO_EMAIL_QUEUE_NAME = "atualizarConfiguracaoEmail";
    private final String enviarEmailQueue = Strings.isNullOrEmpty(System.getenv(ENVIAR_EMAIL_QUEUE_NAME)) ?
        ENVIAR_EMAIL_QUEUE_NAME : System.getenv(ENVIAR_EMAIL_QUEUE_NAME);
    private final String atualizarConfiguracaoEmailQueue = Strings.isNullOrEmpty(System.getenv(ATUALIZAR_CONFIGURACAO_EMAIL_QUEUE_NAME)) ?
        ATUALIZAR_CONFIGURACAO_EMAIL_QUEUE_NAME : System.getenv(ATUALIZAR_CONFIGURACAO_EMAIL_QUEUE_NAME);

    private RabbitMQService rabbitMQService;
    private ObjectMapper objectMapper;

    private EmailService() {
        try {
            rabbitMQService = (RabbitMQService) Util.getSpringBeanPeloNome("rabbitMQService");
        } catch (Exception e) {
            logger.error("Erro ao buscar instância do RabbitMQServico no EmailService. {}", e.getMessage());
            logger.debug("Detalhes do erro ao buscar instância do RabbitMQServico no EmailService.", e);
        }
        objectMapper = new ObjectMapper();
    }

    public static EmailService getInstance() {
        return new EmailService();
    }

    public void enviarEmail(String[] mails, String titulo, String conteudo) {
        for (String mail : mails) {
            enviarEmail(mail, titulo, conteudo);
        }
    }

    public void enviarEmail(String[] mails, String titulo, String conteudo, AnexoEmailDTO anexo) {
        for (String mail : mails) {
            enviarEmail(mail, titulo, conteudo, anexo);
        }
    }

    public void enviarEmail(String[] mails, String titulo, String conteudo,
                            ByteArrayOutputStream anexo) throws MessagingException {
        for (String mail : mails) {
            enviarEmail(mail, titulo, conteudo, anexo);
        }
    }

    public void enviarEmail(String mail, String titulo, String conteudo,
                            ByteArrayOutputStream anexo) {
        AnexoEmailDTO anexoDto = new AnexoEmailDTO(anexo, "anexo", "application/pdf", ".pdf");
        enviarEmail(mail, titulo, conteudo, anexoDto);
    }

    public void enviarEmail(String mail, String titulo, String conteudo,
                            AnexoEmailDTO anexo) {
        enviarEmail(mail, titulo, conteudo, Lists.newArrayList(anexo));
    }

    public void enviarEmail(String[] mails, String titulo, String conteudo, List<AnexoEmailDTO> anexos) {
        for (String mail : mails) {
            enviarEmail(mail, titulo, conteudo, anexos);
        }
    }

    public void enviarEmail(String mail, String titulo, String conteudo) {
        enviarEmail(mail, titulo, conteudo, Lists.newArrayList());
    }

    public void enviarEmail(String mail, String titulo, String conteudo,
                            List<AnexoEmailDTO> anexos) {

        try {
            EmailDTO emailDTO = new EmailDTO();
            emailDTO.setDe("Município de Rio Branco");
            emailDTO.setPara(mail);
            emailDTO.setAssunto(titulo);
            emailDTO.setConteudo(conteudo);
            emailDTO.setHtml(Boolean.TRUE);
            if (anexos != null && !anexos.isEmpty()) {
                for (AnexoEmailDTO anexo : anexos) {
                    AnexoDTO anexoDTO = new AnexoDTO();
                    anexoDTO.setAnexo(anexo.getAnexo().toByteArray());
                    anexoDTO.setNome(anexo.getName());
                    anexoDTO.setMimeType(anexo.getMimeType());
                    emailDTO.getAnexos().add(anexoDTO);
                }
            }
            emailDTO.setAplicacaoProducao(SistemaFacade.PerfilApp.PROD.equals(SistemaFacade.PERFIL_APP));
            rabbitMQService.basicPublish(enviarEmailQueue, objectMapper.writeValueAsBytes(emailDTO));
        } catch (Exception e) {
            logger.error("Erro ao enviar email. {}", e.getMessage());
            logger.debug("Detalhes do erro ao enviar email.", e);
        }
    }

    public void atualizarConfiguracao(ConfiguracaoEmail configuracaoEmail) {
        try {
            configuracaoEmail.setSistema("rbweb");
            rabbitMQService.basicPublish(atualizarConfiguracaoEmailQueue, objectMapper.writeValueAsBytes(configuracaoEmail));
        } catch (Exception e) {
            logger.error("Erro ao atualizar configuração de email. {}", e.getMessage());
            logger.debug("Detalhes do erro ao atualizar configuração de email.", e);
        }
    }

}
