/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.Formulario;
import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.enums.SituacaoLicenciamentoETR;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(
                id = "ver-requerimento-licenciamento-etr",
                pattern = "/tributario/licenciamento-etr/requerimento/ver/#{requerimentoLicenciamentoETRControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/licenciamentoetr/requerimentolicenciamentoetr/visualizar.xhtml"),
        @URLMapping(
                id = "listar-requerimento-licenciamento-etr",
                pattern = "/tributario/licenciamento-etr/requerimento/listar/",
                viewId = "/faces/tributario/cadastromunicipal/licenciamentoetr/requerimentolicenciamentoetr/lista.xhtml")
})
public class RequerimentoLicenciamentoETRControlador extends PrettyControlador<RequerimentoLicenciamentoETR> implements Serializable, CRUD {

    @EJB
    private RequerimentoLicenciamentoETRFacade facade;
    private SituacaoLicenciamentoETR situacao;
    private ExigenciaETR exigenciaETR;
    private ExigenciaETRFormulario exigenciaETRFormulario;
    private ExigenciaETRFormularioCampo exigenciaETRFormularioCampo;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ParametroETRFacade parametroETRFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SolicitacaoDoctoOficialFacade solicitacaoDoctoOficialFacade;
    private String emails;
    private String mensagemEmail;
    private List<SelectItem> selectItemsFormulario;
    private List<SelectItem> selectItemsFormularioCampo;
    private List<UnidadeOrganizacional> unidadeAceiteList;
    private Map<UnidadeOrganizacional, RequerimentoLicenciamentoETRAceite> mapAceitePorUnidade;

    public RequerimentoLicenciamentoETRControlador() {
        super(RequerimentoLicenciamentoETR.class);
    }

    @Override
    public RequerimentoLicenciamentoETRFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/licenciamento-etr/requerimento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public SituacaoLicenciamentoETR getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoLicenciamentoETR situacao) {
        this.situacao = situacao;
    }

    public ExigenciaETR getExigenciaETR() {
        return exigenciaETR;
    }

    public void setExigenciaETR(ExigenciaETR exigenciaETR) {
        this.exigenciaETR = exigenciaETR;
    }

    public ExigenciaETRFormulario getExigenciaETRFormulario() {
        return exigenciaETRFormulario;
    }

    public void setExigenciaETRFormulario(ExigenciaETRFormulario exigenciaETRFormulario) {
        this.exigenciaETRFormulario = exigenciaETRFormulario;
    }

    public ExigenciaETRFormularioCampo getExigenciaETRFormularioCampo() {
        return exigenciaETRFormularioCampo;
    }

    public void setExigenciaETRFormularioCampo(ExigenciaETRFormularioCampo exigenciaETRFormularioCampo) {
        this.exigenciaETRFormularioCampo = exigenciaETRFormularioCampo;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public List<UnidadeOrganizacional> getUnidadeAceiteList() {
        return unidadeAceiteList;
    }

    public void setUnidadeAceiteList(List<UnidadeOrganizacional> unidadeAceiteList) {
        this.unidadeAceiteList = unidadeAceiteList;
    }

    public Map<UnidadeOrganizacional, RequerimentoLicenciamentoETRAceite> getMapAceitePorUnidade() {
        return mapAceitePorUnidade;
    }

    public void setMapAceitePorUnidade(Map<UnidadeOrganizacional, RequerimentoLicenciamentoETRAceite> mapAceitePorUnidade) {
        this.mapAceitePorUnidade = mapAceitePorUnidade;
    }

    @URLAction(mappingId = "ver-requerimento-licenciamento-etr", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarUnidadesAceite();
        carregarMapAceitePorUnidade();
    }

    private void carregarMapAceitePorUnidade() {
        mapAceitePorUnidade = Maps.newHashMap();
        for (UnidadeOrganizacional unidadeOrganizacional : unidadeAceiteList) {
            RequerimentoLicenciamentoETRAceite aceite = selecionado.getAceite(unidadeOrganizacional);
            if (aceite != null) {
                mapAceitePorUnidade.put(unidadeOrganizacional, aceite);
            }
        }
    }

    private void recuperarUnidadesAceite() {
        unidadeAceiteList = Lists.newArrayList();
        ParametroETR parametroETR = parametroETRFacade.recuperarParametroETR();
        for (ParametroETRAceite parametroETRAceite : parametroETR.getAceiteList()) {
            unidadeAceiteList.add(parametroETRAceite.getUnidadeOrganizacional());
        }
    }

    private void validarAceite() {
        for (UnidadeOrganizacional unidadeOrganizacional : unidadeAceiteList) {
            if (mapAceitePorUnidade.get(unidadeOrganizacional) == null) {
                throw new ValidacaoException("Todas as Unidades Administrativas devem aceitar o requerimento antes da aprovação.");
            }
        }
    }

    public void iniciarAprovacao() {
        try {
            validarAceite();
            situacao = SituacaoLicenciamentoETR.APROVADO;
            selecionado.setJustificativa(null);
            FacesUtil.executaJavaScript("dlgAlteracaoSituacao.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }


    public void iniciarSuspensao() {
        situacao = SituacaoLicenciamentoETR.SUSPENSO;
        selecionado.setJustificativa(null);
        FacesUtil.executaJavaScript("dlgAlteracaoSituacao.show()");
    }

    public void validarAlteracaoSituacao() {
        ValidacaoException ve = new ValidacaoException();
        if (SituacaoLicenciamentoETR.SUSPENSO.equals(situacao) && Strings.isNullOrEmpty(selecionado.getJustificativa())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Justificativa deve ser informado.");
        }
        ve.lancarException();
    }

    public void alterarSituacao() {
        try {
            validarAlteracaoSituacao();
            facade.alterarSituacao(selecionado, situacao);
            FacesUtil.executaJavaScript("dlgAlteracaoSituacao.hide()");
            FacesUtil.addOperacaoRealizada("Requerimento alterado para " + selecionado.getSituacao().getDescricao() + " com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void iniciarExigencia() {
        exigenciaETR = new ExigenciaETR();
        exigenciaETRFormulario = new ExigenciaETRFormulario();
        exigenciaETRFormularioCampo = new ExigenciaETRFormularioCampo();
        buscarSelectItemsFormulario();
        FacesUtil.executaJavaScript("dlgExigencia.show()");
    }

    public void adicionarExigenciaFormulario() {
        try {
            validarExigenciaFormulario();
            exigenciaETRFormulario.setExigenciaETR(exigenciaETR);
            exigenciaETR.getExigenciaETRFormularioList().add(exigenciaETRFormulario);
            exigenciaETRFormulario = new ExigenciaETRFormulario();
            Collections.sort(exigenciaETR.getExigenciaETRFormularioList());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void validarExigenciaFormulario() {
        exigenciaETRFormulario.realizarValidacoes();
        if (exigenciaETR.hasExigenciaETRFormulario(exigenciaETRFormulario)) {
            throw new ValidacaoException("O Formulário já está adicionado.");
        }
    }

    public void editarExigenciaFormulario(ExigenciaETRFormulario exigenciaETRFormulario) {
        this.exigenciaETR.getExigenciaETRFormularioList().remove(exigenciaETRFormulario);
        this.exigenciaETRFormulario = exigenciaETRFormulario;
    }

    public void removerExigenciaFormulario(ExigenciaETRFormulario exigenciaETRFormulario) {
        exigenciaETR.getExigenciaETRFormularioList().remove(exigenciaETRFormulario);
    }

    public void validarExigencia() {
        exigenciaETR.realizarValidacoes();
        if (exigenciaETR.getExigenciaETRFormularioList() == null || exigenciaETR.getExigenciaETRFormularioList().isEmpty()) {
            throw new ValidacaoException("Adicione ao menos um formulário para a exigência.");
        }
    }

    public void salvarExigencia() {
        try {
            validarExigencia();
            exigenciaETR.setRequerimentoLicenciamentoETR(selecionado);
            facade.registrarExigencia(exigenciaETR);
            FacesUtil.executaJavaScript("dlgExigencia.hide()");
            FacesUtil.addOperacaoRealizada("Exigência registrada com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void enviarEmailRegistroExigencia(ExigenciaETR exigenciaETR) {
        facade.enviarEmailRegistroExigencia(exigenciaETR);
        FacesUtil.addOperacaoRealizada("Exigência enviado com sucesso para:" + exigenciaETR.getRequerimentoLicenciamentoETR().getEmail() + "!");
    }

    public String montaEnderecoMapa() {
        String enderecoMapa = "";
        if (Util.isNotNull(selecionado.getEnderecoInstalacao())) {
            EnderecoCorreio endereco = selecionado.getEnderecoInstalacao();
            if (Util.isNotNull(endereco.getLogradouro())) {
                enderecoMapa += endereco.getLogradouro() + ", ";
            }
            if (Util.isNotNull(endereco.getNumero())) {
                enderecoMapa += endereco.getNumero() + ", ";
            }
            if (Util.isNotNull((endereco.getCep()))) {
                enderecoMapa += endereco.getCep() + ", ";
            }
            if (Util.isNotNull(endereco.getLocalidade())) {
                enderecoMapa += endereco.getLocalidade() + ", ";
            }
            if (Util.isNotNull(endereco.getUf())) {
                enderecoMapa += endereco.getUf();
            }
        }
        return StringUtil.removeAcentuacao(enderecoMapa);
    }

    public String getUrlMapa() {
        if (Util.isNotNull(selecionado.getEnderecoInstalacao().getEnderecoCompleto())) {
            return "https://www.google.com.br/maps?q=" + montaEnderecoMapa() + "&output=embed&z=18";
        }
        return "";
    }

    public StreamedContent baixarTodosAnexos() throws IOException {
        List<Arquivo> arquivos = new ArrayList<>();
        for (RequerimentoLicenciamentoETRRespostaFormulario itens :
                selecionado.getRequerimentoLicenciamentoETRRespostaFormularioList()) {
            List<Arquivo> arquivosAnexados = itens.getRespostaFormulario().getArquivosAnexados();
            if (arquivosAnexados != null && !arquivosAnexados.isEmpty()) {
                arquivos.addAll(arquivosAnexados);
            }
        }
        return arquivoFacade.ziparArquivos("Documentos em anexo", arquivos);
    }

    public void imprimirAlvara() {
        try {
            DocumentoOficial documentoOficial = gerarDocumento();
            selecionado.setDocumentoOficial(documentoOficial);
            selecionado = facade.salvarRetornado(selecionado);
            selecionado = facade.recuperar(selecionado.getId());
            documentoOficialFacade.emiteDocumentoOficial(gerarDocumento());
        } catch (Exception ex) {
            FacesUtil.addOperacaoRealizada(ex.getMessage());
        }
    }

    private DocumentoOficial gerarDocumento() throws UFMException, AtributosNulosException {
        if (selecionado.getDocumentoOficial() == null) {
            ParametroETR parametroETR = parametroETRFacade.recuperarParametroETR();
            TipoDoctoOficial tipoDoctoOficial = selecionado.getDispensaAlvara() ? parametroETR.getTipoDoctoOficialDispensa() : parametroETR.getTipoDoctoOficialAlvara();
            if (tipoDoctoOficial != null) {
                DocumentoOficial docto = documentoOficialFacade.geraDocumentoLicencaETR(selecionado, selecionado.getDocumentoOficial(), selecionado.getCadastroEconomico(), tipoDoctoOficial, getSistemaControlador());
                selecionado.setDocumentoOficial(docto);
                return docto;
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado o parâmetro do tipo de documento");
            }
        }
        return selecionado.getDocumentoOficial();
    }

    public void prepararImpressaoParaEnvio() {
        emails = selecionado.getEmail();
        mensagemEmail = null;
        FacesUtil.executaJavaScript("listaEmails.show()");
    }

    public void enviarDocumentoPorEmail() {
        try {
            String[] emailsSeparados = validarAndSepararEmails();

            if (selecionado.getDocumentoOficial() == null || selecionado.getDocumentoOficial().getConteudo() == null) {
                selecionado.setDocumentoOficial(gerarDocumento());
            }
            selecionado = facade.salvarRetornando(selecionado);
            selecionado = facade.recuperar(selecionado.getId());

            enviarEmail(emailsSeparados);
        } catch (AddressException e) {
            FacesUtil.addOperacaoNaoRealizada("O e-mail informado é invalido!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addError("Impossível continuar", ex.getMessage());
            logger.error("Erro: ", ex);
        }
    }

    private void enviarEmail(String[] emailsSeparados) {
        try {
            ParametroETR parametroETR = parametroETRFacade.recuperarParametroETR();
            ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
            String prefeitura = "Prefeitura Municipal de " + configuracaoTributario.getCidade().getNome() + " / " + configuracaoTributario.getCidade().getUf();
            String assunto = prefeitura + " - " + (selecionado.getDispensaAlvara() ? parametroETR.getTipoDoctoOficialAlvara().getDescricao() : parametroETR.getTipoDoctoOficialDispensa().getDescricao());
            String mensagem = prefeitura + "<br/>" + (selecionado.getDispensaAlvara() ? parametroETR.getTipoDoctoOficialAlvara().getDescricao() : parametroETR.getTipoDoctoOficialDispensa().getDescricao()) + " (" + selecionado.getCodigo() + ")" + "<br/><br/>" + mensagemEmail;
            solicitacaoDoctoOficialFacade.getDocumentoOficialFacade().enviarEmailDocumentoOficial(emailsSeparados, selecionado.getDocumentoOficial(), assunto, mensagem);
            FacesUtil.addOperacaoRealizada("E-mail enviado com sucesso!");
            FacesUtil.executaJavaScript("listaEmails.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível enviar o e-mail");
            logger.error("Não foi possível enviar o e-mail: " + e);
        }
    }

    private String[] validarAndSepararEmails() throws AddressException {
        ValidacaoException ve = new ValidacaoException();
        String emailsQuebrados[] = null;
        if (emails == null || emails.trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo E-mail é obrigatório.");
        } else {
            emailsQuebrados = emails.split(Pattern.quote(","));
            for (String emailsQuebrado : emailsQuebrados) {
                InternetAddress emailAddr = new InternetAddress(emailsQuebrado);
                emailAddr.validate();
            }
        }
        if (mensagemEmail == null || mensagemEmail.trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mensagem é obrigatório.");
        }
        ve.lancarException();
        return emailsQuebrados;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getMensagemEmail() {
        return mensagemEmail;
    }

    public void setMensagemEmail(String mensagemEmail) {
        this.mensagemEmail = mensagemEmail;
    }

    public List<SelectItem> getSelectItemsFormulario() {
        return selectItemsFormulario;
    }

    public List<SelectItem> getSelectItemsFormularioCampo() {
        return selectItemsFormularioCampo;
    }

    public void buscarSelectItemsFormulario() {
        selectItemsFormulario = Lists.newArrayList();
        selectItemsFormulario.add(new SelectItem(null, ""));
        for (RequerimentoLicenciamentoETRRespostaFormulario requerimentoLicenciamentoETRRespostaFormulario : selecionado.getRequerimentoLicenciamentoETRRespostaFormularioList()) {
            Formulario formulario = requerimentoLicenciamentoETRRespostaFormulario.getRespostaFormulario().getFormulario();
            selectItemsFormulario.add(new SelectItem(formulario, formulario.getTitulo()));
        }
    }

    public void buscarSelectItemsFormularioCampo() {
        selectItemsFormularioCampo = Lists.newArrayList();
        selectItemsFormularioCampo.add(new SelectItem(null, ""));
        if (exigenciaETRFormulario != null && exigenciaETRFormulario.getFormulario() != null) {
            Formulario formulario = facade.getFormularioFacade().recuperar(exigenciaETRFormulario.getFormulario().getId());
            for (FormularioCampo formularioCampo : formulario.getFormularioCampoList()) {
                selectItemsFormularioCampo.add(new SelectItem(formularioCampo, formularioCampo.getTitulo()));
            }
        }
    }

    public void adicionarExigenciaFormularioCampo() {
        try {
            validarExigenciaFormularioCampo();
            exigenciaETRFormularioCampo.setExigenciaETRFormulario(exigenciaETRFormulario);
            exigenciaETRFormulario.getFormularioCampoList().add(exigenciaETRFormularioCampo);
            exigenciaETRFormularioCampo = new ExigenciaETRFormularioCampo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void validarExigenciaFormularioCampo() {
        exigenciaETRFormularioCampo.realizarValidacoes();
        if (exigenciaETRFormulario.hasExigenciaETRFormularioCampo(exigenciaETRFormularioCampo)) {
            throw new ValidacaoException("O Campo já está adicionado.");
        }
    }

    public void editarExigenciaFormularioCampo(ExigenciaETRFormularioCampo exigenciaETRFormularioCampo) {
        this.exigenciaETRFormulario.getFormularioCampoList().remove(exigenciaETRFormularioCampo);
        this.exigenciaETRFormularioCampo = exigenciaETRFormularioCampo;
    }

    public void removerExigenciaFormularioCampo(ExigenciaETRFormularioCampo exigenciaETRFormularioCampo) {
        exigenciaETRFormulario.getFormularioCampoList().remove(exigenciaETRFormularioCampo);
    }

    public UsuarioSistema buscarUsuarioAceite(UnidadeOrganizacional unidadeOrganizacional) {
        RequerimentoLicenciamentoETRAceite requerimentoLicenciamentoETRAceite = mapAceitePorUnidade.get(unidadeOrganizacional);
        return requerimentoLicenciamentoETRAceite != null ? requerimentoLicenciamentoETRAceite.getUsuarioAceite() : null;
    }

    public Date buscarDataAceite(UnidadeOrganizacional unidadeOrganizacional) {
        RequerimentoLicenciamentoETRAceite requerimentoLicenciamentoETRAceite = mapAceitePorUnidade.get(unidadeOrganizacional);
        return requerimentoLicenciamentoETRAceite != null ? requerimentoLicenciamentoETRAceite.getDataAceite() : null;
    }

    public boolean canAceitarRequerimento(UnidadeOrganizacional unidadeOrganizacional) {
        boolean aceito = mapAceitePorUnidade.get(unidadeOrganizacional) != null;
        boolean unidadeLogadaCorreta = facade.getSistemaFacade()
                .getUnidadeOrganizacionalAdministrativaCorrente().equals(unidadeOrganizacional);
        return (!SituacaoLicenciamentoETR.APROVADO.equals(selecionado.getSituacao()) &&
                !SituacaoLicenciamentoETR.SUSPENSO.equals(selecionado.getSituacao())) &&
                !aceito && unidadeLogadaCorreta;
    }

    public void aceitarRequerimento(UnidadeOrganizacional unidadeOrganizacional) {
        try {
            facade.aceitarRequerimento(selecionado, unidadeOrganizacional);
            selecionado = facade.recuperar(selecionado.getId());
            carregarMapAceitePorUnidade();
            FacesUtil.addOperacaoRealizada("Aceite realizado com sucesso!");
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }
}
