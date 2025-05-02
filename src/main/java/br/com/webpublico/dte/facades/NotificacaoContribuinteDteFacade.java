package br.com.webpublico.dte.facades;

import br.com.webpublico.dte.entidades.ModeloDocumentoDte;
import br.com.webpublico.dte.entidades.NotificacaoContribuinteDocDte;
import br.com.webpublico.dte.entidades.NotificacaoContribuinteDte;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoFormato;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.DocumentoOficialFacade;
import br.com.webpublico.negocios.EnderecoFacade;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.util.Html2Pdf;
import com.google.common.base.Strings;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.hibernate.Hibernate;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Stateless
public class NotificacaoContribuinteDteFacade extends AbstractFacade<NotificacaoContribuinteDte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UsuarioWebFacade usuarioWebFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;

    public NotificacaoContribuinteDteFacade() {
        super(NotificacaoContribuinteDte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public NotificacaoContribuinteDte recuperar(Object id) {
        NotificacaoContribuinteDte notificacao = super.recuperar(id);
        Hibernate.initialize(notificacao.getDocumentos());
        if (notificacao.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(notificacao.getDetentorArquivoComposicao().getArquivosComposicao());
            for (ArquivoComposicao arquivoComposicao : notificacao.getDetentorArquivoComposicao().getArquivosComposicao()) {
                Hibernate.initialize(arquivoComposicao.getArquivo().getPartes());
            }
        }
        return notificacao;
    }

    @Asynchronous
    public void enviarEmailsNotificacao(NotificacaoContribuinteDte notificacaoContribuinteDte) {
        List<UsuarioWeb> usuarios = usuarioWebFacade.buscarUsuariosDte(notificacaoContribuinteDte.getCadastroEconomico());
        if (usuarios != null && !usuarios.isEmpty()) {
            for (UsuarioWeb usuarioWeb : usuarios) {
                if (!Strings.isNullOrEmpty(usuarioWeb.getEmail()))
                    usuarioWebFacade.enviarEmail(usuarioWeb.getEmail(), notificacaoContribuinteDte.getTitulo(), notificacaoContribuinteDte.getConteudo());
            }
        }
    }

    public byte[] gerarDocumento(Long idNotificacaoDoc, Long idUsuarioWeb) throws Exception {
        NotificacaoContribuinteDocDte documento = em.find(NotificacaoContribuinteDocDte.class, idNotificacaoDoc);
        if (documento.getDocumento() == null) {
            UsuarioWeb usuarioWeb = null;
            if (idUsuarioWeb != null) {
                usuarioWeb = usuarioWebFacade.recuperar(idUsuarioWeb);
            }
            documento.setVisualizadoEm(new Date());
            documento.setVisualizadoPor(usuarioWeb);
            documento.setDocumento(trocarTagsConteudo(documento.getModeloDocumento().getConteudo(), documento.getNotificacaoContribuinte().getCadastroEconomico()));
            documento = em.merge(documento);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Html2Pdf.convert(documento.getDocumento(), baos);
        return baos.toByteArray();
    }

    public String trocarTagsConteudo(String conteudo, CadastroEconomico cadastroEconomico) {
        UUID uuid = UUID.randomUUID();

        Properties p = new Properties();
        p.setProperty("resource.loader", "string");
        p.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");

        VelocityEngine ve = new VelocityEngine();
        ve.init(p);

        StringResourceRepository repo = StringResourceLoader.getRepository();
        repo.putStringResource(uuid.toString(), conteudo);
        repo.setEncoding("UTF-8");

        VelocityContext context = new VelocityContext();

        documentoOficialFacade.tagsDataHoje(context);
        tagsCadastroEconomico(context, cadastroEconomico);

        Template template = ve.getTemplate(uuid.toString(), "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private void addTag(VelocityContext contexto, String chave, Object valor) {
        if (valor != null) {
            contexto.put(chave, valor);
        } else {
            contexto.put(chave, "");
        }
    }

    public void tagsCadastroEconomico(VelocityContext context, CadastroEconomico cadastroEconomico) {
        try {
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.CMC.name(), cadastroEconomico.getInscricaoCadastral());
            if (!cadastroEconomico.getSituacaoCadastroEconomico().isEmpty()) {
                addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.SITUACAO_CADASTRAL.name(),
                    cadastroEconomico.getSituacaoCadastroEconomico().get(0).getSituacaoCadastral().getDescricao());
            } else {
                addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.SITUACAO_CADASTRAL.name(),
                    SituacaoCadastralCadastroEconomico.NULA.name());
            }
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.DATA_ABERTURA.name(),
                documentoOficialFacade.nomeDoCampo(cadastroEconomico, "abertura", TipoFormato.DATA));

            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.RAZAO_SOCIAL.name(), cadastroEconomico.getPessoa().getNome());
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.NOME_FANTASIA.name(), cadastroEconomico.getPessoa().getNomeFantasia());
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.CPF_CNPJ.name(), cadastroEconomico.getPessoa().getCpf_Cnpj());
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.INSCRICAO_ESTADUAL.name(), cadastroEconomico.getPessoa().getRg_InscricaoEstadual());

            EnderecoCorreio enderecoCorreio = documentoOficialFacade.recuperaEnderecoPessoa(cadastroEconomico.getPessoa());
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.TIPO_LOGRADOURO_PESSOA.name(), documentoOficialFacade.nomeDoCampo(enderecoCorreio, "tipoEndereco", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.LOGRADOURO_PESSOA.name(), documentoOficialFacade.nomeDoCampo(enderecoCorreio, "logradouro", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.NUMERO_PESSOA.name(), documentoOficialFacade.nomeDoCampo(enderecoCorreio, "numero", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.BAIRRO_PESSOA.name(), documentoOficialFacade.nomeDoCampo(enderecoCorreio, "bairro", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.COMPLEMENTO_PESSOA.name(), documentoOficialFacade.nomeDoCampo(enderecoCorreio, "complemento", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.CIDADE_PESSOA.name(), documentoOficialFacade.nomeDoCampo(enderecoCorreio, "localidade", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.UF_PESSOA.name(), documentoOficialFacade.nomeDoCampo(enderecoCorreio, "uf", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.CEP_PESSOA.name(), documentoOficialFacade.nomeDoCampo(enderecoCorreio, "cep", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.TELEFONES_PESSOA.name(), documentoOficialFacade.montaListaTelefones(cadastroEconomico.getPessoa()));

            EnderecoCadastroEconomico enderecoCorrespondencia = cadastroEconomico.getEnderecoSecundario();

            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.LOGRADOURO_BCE_CORRESPONDENCIA.name(), documentoOficialFacade.nomeDoCampo(enderecoCorrespondencia, "logradouro", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.BAIRRO_BCE_CORRESPONDENCIA.name(), documentoOficialFacade.nomeDoCampo(enderecoCorrespondencia, "bairro", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.NUMERO_BCE_CORRESPONDENCIA.name(), documentoOficialFacade.nomeDoCampo(enderecoCorrespondencia, "numero", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.COMPLEMENTO_BCE_CORRESPONDENCIA.name(), documentoOficialFacade.nomeDoCampo(enderecoCorrespondencia, "complemento", null));

            EnderecoCadastroEconomico enderecoLocalizacao = cadastroEconomico.getEnderecoPrimario();

            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.LOGRADOURO_BCE_LOCALIZACAO.name(), documentoOficialFacade.nomeDoCampo(enderecoLocalizacao, "logradouro", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.BAIRRO_BCE_LOCALIZACAO.name(), documentoOficialFacade.nomeDoCampo(enderecoLocalizacao, "bairro", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.NUMERO_BCE_LOCALIZACAO.name(), documentoOficialFacade.nomeDoCampo(enderecoLocalizacao, "numero", null));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.COMPLEMENTO_BCE_LOCALIZACAO.name(), documentoOficialFacade.nomeDoCampo(enderecoLocalizacao, "complemento", null));

            EnquadramentoFiscal enquadramentoFiscal = cadastroEconomicoFacade.buscarEnquadramentoFiscalVigente(cadastroEconomico);

            if (enquadramentoFiscal != null) {
                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.PORTE.name(), enquadramentoFiscal.getPorte() != null ? enquadramentoFiscal.getPorte().getDescricao() : "");
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.PORTE.name(), "");
            }
            if (cadastroEconomico.getClassificacaoAtividade() != null) {
                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CLASSIFICACAO.name(), cadastroEconomico.getClassificacaoAtividade().getDescricao());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CLASSIFICACAO.name(), "");
            }
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.AREA_UTILIZACAO.name(), documentoOficialFacade.nomeDoCampo(cadastroEconomico, "areaUtilizacao", TipoFormato.DECIMAL));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.ENDERECO_CORRESPONDENCIA.name(), documentoOficialFacade.recuperarEnderecoDeCorrespondencia(cadastroEconomico.getPessoa()));

            EconomicoCNAE primario = cadastroEconomico.getEconomicoCNAEPrincipal();
            if (primario != null) {
                addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.CNAE_PRIMARIO.name(), primario.getCnae().getCodigoCnae() + " - " + primario.getCnae().getDescricaoDetalhada() + (primario.getCnae().getNivelComplexibilidade() != null ? (" - " + primario.getCnae().getNivelComplexibilidade()) : ""));
            } else {
                addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.CNAE_PRIMARIO.name(), "");
            }

            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.CNAES_SEGUNDARIOS.name(), documentoOficialFacade.montaTabelaCnaes(cadastroEconomico.getEconomicoCNAESecundariosVigentes()));
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.NATUREZA_JURIDICA.name(), cadastroEconomico.getNaturezaJuridica() != null ? cadastroEconomico.getNaturezaJuridica().getDescricao() : "");
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.HORARIOS_FUNCIONAMENTO.name(), documentoOficialFacade.montaTabelaHorariosFuncionamento(cadastroEconomico.getPessoa().getHorariosFuncionamentoAtivos()));

            addTagAtividadeEconomica(context, cadastroEconomico);
        } catch (NullPointerException e) {
            logger.error("Atributo nulo {}", e);
        }
    }

    private void addTagAtividadeEconomica(VelocityContext context, CadastroEconomico cadastroEconomico) {
        if (!cadastroEconomico.getEconomicoCNAE().isEmpty()) {
            StringBuilder atividade = new StringBuilder();
            for (EconomicoCNAE ecnae : cadastroEconomico.getEconomicoCNAE()) {
                atividade.append(ecnae.getCnae().getDescricaoDetalhada()).append(", ");
                if (ecnae.getCnae().getDescricaoDetalhada() != null) {
                    addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.ATIVIDADE_ECONOMICA.name(), atividade.substring(0, atividade.length() - 2));
                } else {
                    addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.ATIVIDADE_ECONOMICA.name(), "");
                }
            }
        } else {
            addTag(context, ModeloDocumentoDte.TagsCadastroEconomico.ATIVIDADE_ECONOMICA.name(), "");
        }
    }
}
