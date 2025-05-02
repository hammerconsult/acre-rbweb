package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoLicitacaoFacade;
import br.com.webpublico.pncp.service.PncpService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by hudson on 09/12/15.
 */

@ManagedBean(name = "configuracaoLicitacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-configuracao-licitacao", pattern = "/configuracao/licitacao/novo/", viewId = "/faces/administrativo/configuracao/geral/edita.xhtml"),
    @URLMapping(id = "editar-configuracao-licitacao", pattern = "/configuracao/licitacao/editar/#{configuracaoLicitacaoControlador.id}/", viewId = "/faces/administrativo/configuracao/geral/edita.xhtml"),
    @URLMapping(id = "ver-configuracao-licitacao", pattern = "/configuracao/licitacao/ver/#{configuracaoLicitacaoControlador.id}/", viewId = "/faces/administrativo/configuracao/geral/visualizar.xhtml"),
    @URLMapping(id = "listar-configuracao-licitacao", pattern = "/configuracao/licitacao/listar/", viewId = "/faces/administrativo/configuracao/geral/lista.xhtml")
})

public class ConfiguracaoLicitacaoControlador extends PrettyControlador<ConfiguracaoLicitacao> implements Serializable, CRUD, ValidadorEntidade {

    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    private ConfiguracaoProcessoCompra configuracaoProcessoCompra;
    private UnidadeTercerializadaRh unidadeTercerializadaRh;
    private UnidadeGrupoObjetoCompra unidadeGrupoObjetoCompra;
    private ConfiguracaoReservaDotacao configuracaoReservaDotacao;
    private ConfiguracaoSubstituicaoObjetoCompra configuracaoSubstituicaoObjetoCompra;
    private ConfiguracaoAnexoLicitacao configuracaoAnexoLicitacao;
    private ConfiguracaoAnexoLicitacaoTipoDocumento configuracaoAnexoLicitacaoTipoDocumento;
    private PncpService pncpService;

    @PostConstruct
    private void init() {
        pncpService = (PncpService) Util.getSpringBeanPeloNome("pncpService");
    }

    public ConfiguracaoLicitacaoControlador() {
        super(ConfiguracaoLicitacao.class);
    }

    @URLAction(mappingId = "nova-configuracao-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        novaUnidade();
        novaUnidadeTercerializada();
        novaUnidadeGrupoObjetoCompra();
        novaConfiguracaoSubstituicaoObjetoCompra();
        novaConfigDotacao();
        novaConfigAnexo();
        novoTipoDocumentoAnexo();
    }

    @URLAction(mappingId = "ver-configuracao-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-configuracao-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        novaUnidade();
        novaUnidadeTercerializada();
        novaUnidadeGrupoObjetoCompra();
        novaConfiguracaoSubstituicaoObjetoCompra();
        novaConfigDotacao();
        novaConfigAnexo();
        novoTipoDocumentoAnexo();
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoLicitacaoFacade;
    }


    @Override
    public String getCaminhoPadrao() {
        return "/configuracao/licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarConfirmacao();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void novaUnidade() {
        configuracaoProcessoCompra = new ConfiguracaoProcessoCompra();
    }

    public void removerUnidade(ConfiguracaoProcessoCompra obj) {
        selecionado.getConfigProcessoCompraUnidades().remove(obj);
    }

    public void adicionarUnidade() {
        try {
            validarListaUnidade();
            configuracaoProcessoCompra.setConfiguracaoLicitacao(selecionado);
            configuracaoProcessoCompra.setUnidadeOrganizacional(configuracaoProcessoCompra.getHierarquiaOrganizacional().getSubordinada());
            Util.adicionarObjetoEmLista(selecionado.getConfigProcessoCompraUnidades(), configuracaoProcessoCompra);
            novaUnidade();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarListaUnidade() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoProcessoCompra.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma Unidade.");
        } else {
            for (ConfiguracaoProcessoCompra cp : selecionado.getConfigProcessoCompraUnidades()) {
                if (cp.getHierarquiaOrganizacional() != null && cp.getHierarquiaOrganizacional().equals(configuracaoProcessoCompra.getHierarquiaOrganizacional())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Unidade já foi adicionada a lista");
                }
            }
        }
        ve.lancarException();
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        validarCampos();
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Campo Início de Vigência obrigatório.");
        }
        if (selecionado.getInicioVigencia() != null) {
            if (!DataUtil.isVigenciaValida(selecionado, configuracaoLicitacaoFacade.buscarConfiguracaoLicitacao(selecionado))) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Erro na validação da Vigência.");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getConfigProcessoCompraUnidades().isEmpty() || selecionado.getConfigProcessoCompraUnidades() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Ao menos uma unidade organizacional deve ser adicionada.");
        }
        ve.lancarException();
    }

    @Override
    public ConverterAutoComplete getConverterGenerico() {
        return super.getConverterGenerico();
    }

    public void novaUnidadeTercerializada() {
        unidadeTercerializadaRh = new UnidadeTercerializadaRh();
    }

    public void novaUnidadeGrupoObjetoCompra() {
        unidadeGrupoObjetoCompra = new UnidadeGrupoObjetoCompra();
    }

    public void novaConfiguracaoSubstituicaoObjetoCompra() {
        configuracaoSubstituicaoObjetoCompra = new ConfiguracaoSubstituicaoObjetoCompra();
    }

    public void novaConfigDotacao() {
        configuracaoReservaDotacao = new ConfiguracaoReservaDotacao();
    }

    public void novaConfigAnexo() {
        configuracaoAnexoLicitacao = new ConfiguracaoAnexoLicitacao();
    }

    public void novoTipoDocumentoAnexo() {
        configuracaoAnexoLicitacaoTipoDocumento = new ConfiguracaoAnexoLicitacaoTipoDocumento();
    }

    public void removerUnidadeTercerializada(UnidadeTercerializadaRh obj) {
        selecionado.getUnidadesTercerializadasRh().remove(obj);
    }

    public void removerUnidadeGrupoObjetoCompra(UnidadeGrupoObjetoCompra obj) {
        selecionado.getUnidadesGrupoObjetoCompra().remove(obj);
    }

    public void removerConfiguracaoSubstituicaoObjetoCompra(ConfiguracaoSubstituicaoObjetoCompra obj) {
        selecionado.getConfigSubstituicoesObjetoCompra().remove(obj);
    }

    public void removerConfigDotacao(ConfiguracaoReservaDotacao obj) {
        try {
            validarEdicaoConfigDotacao(obj);
            selecionado.getConfigReservasDotacoes().remove(obj);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerConfigAnexo(ConfiguracaoAnexoLicitacao obj) {
        selecionado.getConfigAnexosLicitacoes().remove(obj);
    }

    public void editarConfigDotacao(ConfiguracaoReservaDotacao obj) {
        try {
            validarEdicaoConfigDotacao(obj);
            configuracaoReservaDotacao = obj;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarConfigAnexo(ConfiguracaoAnexoLicitacao obj) {
        try {
            configuracaoAnexoLicitacao = obj;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItem(ModalidadeLicitacao.values());
    }

    public List<SelectItem> getTiposMovimentos() {
        if (configuracaoAnexoLicitacao == null) {
            novaConfigAnexo();
        }
        return Util.getListSelectItem(TipoMovimentoProcessoLicitatorio.getTiposMovimentosAnexoLicitacao());
    }

    public List<SelectItem> getTiposDocumentosAnexos() {
        List<SelectItem> selectItems = Lists.newArrayList();
        List<TipoDocumentoAnexo> tiposDocumentoAnexo = configuracaoLicitacaoFacade.getTipoDocumentoAnexoFacade().buscarTodosTipoDocumentosAnexo();
        selectItems.add(new SelectItem(null, ""));
        for (TipoDocumentoAnexo tipoDocumentoAnexo : tiposDocumentoAnexo) {
            selectItems.add(new SelectItem(tipoDocumentoAnexo, tipoDocumentoAnexo.getDescricao()));
        }
        return selectItems;
    }

    public List<SelectItem> getNaturezasProcedimento() {
        if (configuracaoReservaDotacao.getModalidadeLicitacao() != null) {
            return Util.getListSelectItem(TipoNaturezaDoProcedimentoLicitacao.getTiposNaturezaProcedimento(configuracaoReservaDotacao.getModalidadeLicitacao()));
        }
        return Util.getListSelectItem(TipoNaturezaDoProcedimentoLicitacao.values());
    }

    public List<SelectItem> getTiposReserva() {
        return Util.getListSelectItemSemCampoVazio(TipoReservaDotacaoConfigLicitacao.values());
    }

    public void adicionarUnidadeTercerializada() {
        try {
            validarUnidadeTercerializada();
            unidadeTercerializadaRh.setConfiguracaoLicitacao(selecionado);
            unidadeTercerializadaRh.setUnidadeOrganizacional(unidadeTercerializadaRh.getHierarquiaOrganizacional().getSubordinada());
            Util.adicionarObjetoEmLista(selecionado.getUnidadesTercerializadasRh(), unidadeTercerializadaRh);
            novaUnidadeTercerializada();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarUnidadeGrupoObjetoCompra() {
        try {
            validarUnidadeGrupoObjetoCompra();
            unidadeGrupoObjetoCompra.setConfiguracaoLicitacao(selecionado);
            unidadeGrupoObjetoCompra.setUnidadeOrganizacional(unidadeGrupoObjetoCompra.getHierarquiaOrganizacional().getSubordinada());
            Util.adicionarObjetoEmLista(selecionado.getUnidadesGrupoObjetoCompra(), unidadeGrupoObjetoCompra);
            novaUnidadeGrupoObjetoCompra();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarConfiguracaoSubstituicaoObjetoCompra() {
        try {
            validarConfiguracaoSubstituicaoObjetoCompra();
            configuracaoSubstituicaoObjetoCompra.setConfiguracaoLicitacao(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getConfigSubstituicoesObjetoCompra(), configuracaoSubstituicaoObjetoCompra);
            novaConfiguracaoSubstituicaoObjetoCompra();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarConfigDotacao() {
        try {
            validarConfigDotacao();
            configuracaoReservaDotacao.setConfiguracaoLicitacao(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getConfigReservasDotacoes(), configuracaoReservaDotacao);
            novaConfigDotacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarConfigAnexo() {
        try {
            validarConfigAnexo();
            configuracaoAnexoLicitacao.setConfiguracaoLicitacao(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getConfigAnexosLicitacoes(), configuracaoAnexoLicitacao);
            novaConfigAnexo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<UsuarioSistema> completarUsuario(String filtro) {
        return configuracaoLicitacaoFacade.getUsuarioSistemaFacade().buscarTodosUsuariosPorLoginOuNome(filtro.trim());
    }

    public void removerTipoDocumento(ConfiguracaoAnexoLicitacaoTipoDocumento tipoDocumento) {
        configuracaoAnexoLicitacao.getTiposDocumentos().remove(tipoDocumento);
    }

    public void adicionarTipoDocumento() {
        try {
            validarTipoDocumento();
            configuracaoAnexoLicitacaoTipoDocumento.setConfiguracaoAnexoLicitacao(configuracaoAnexoLicitacao);
            Util.adicionarObjetoEmLista(configuracaoAnexoLicitacao.getTiposDocumentos(), configuracaoAnexoLicitacaoTipoDocumento);
            novoTipoDocumentoAnexo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    public void validarUnidadeTercerializada() {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeTercerializadaRh.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade organizacional deve ser informado.");
        }
        ve.lancarException();
        for (UnidadeTercerializadaRh unid : selecionado.getUnidadesTercerializadasRh()) {
            if (unid.getUnidadeOrganizacional().equals(unidadeTercerializadaRh.getHierarquiaOrganizacional().getSubordinada())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Unidade já foi adicionada à lista.");
            }
        }
        ve.lancarException();
    }

    public void validarUnidadeGrupoObjetoCompra() {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeGrupoObjetoCompra.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional deve ser informado.");
        }
        ve.lancarException();
        for (UnidadeGrupoObjetoCompra unid : selecionado.getUnidadesGrupoObjetoCompra()) {
            if (unid.getUnidadeOrganizacional().equals(unidadeGrupoObjetoCompra.getHierarquiaOrganizacional().getSubordinada())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Unidade já foi adicionada à lista.");
            }
        }
        ve.lancarException();
    }

    public void validarConfiguracaoSubstituicaoObjetoCompra() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoSubstituicaoObjetoCompra.getUsuarioSistema() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Usuário Sistema deve ser informado.");
        }
        ve.lancarException();
        for (ConfiguracaoSubstituicaoObjetoCompra config : selecionado.getConfigSubstituicoesObjetoCompra()) {
            if (config.getUsuarioSistema().equals(configuracaoSubstituicaoObjetoCompra.getUsuarioSistema())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Usuário já foi adicionada à lista.");
            }
        }
        ve.lancarException();
    }

    public void validarConfigAnexo() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoAnexoLicitacao.getRecursoTela() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo recurso tela deve ser informado.");
        }
        ve.lancarException();
        if (Util.isListNullOrEmpty(configuracaoAnexoLicitacao.getTiposDocumentos())) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione um tipo de documento.");
        }
        ve.lancarException();
        if (configuracaoAnexoLicitacao.getId() == null) {
            for (ConfiguracaoAnexoLicitacao anexo : selecionado.getConfigAnexosLicitacoes()) {
                if (anexo.getRecursoTela().equals(configuracaoAnexoLicitacao.getRecursoTela())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Este recurso tela já foi adicionado à lista.");
                }
            }
        }
        ve.lancarException();
    }

    public void validarTipoDocumento() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoAnexoLicitacaoTipoDocumento.getTipoDocumentoAnexo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um tipo de documento.");
        }
        ve.lancarException();
        for (ConfiguracaoAnexoLicitacaoTipoDocumento doc : configuracaoAnexoLicitacao.getTiposDocumentos()) {
            if (doc.getTipoDocumentoAnexo().equals(configuracaoAnexoLicitacaoTipoDocumento.getTipoDocumentoAnexo())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Este tipo de documento já foi adicionado à lista.");
            }
        }
        ve.lancarException();
    }

    public void validarConfigDotacao() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(configuracaoReservaDotacao);
        selecionado.getConfigReservasDotacoes().stream()
            .filter(mod -> !mod.equals(configuracaoReservaDotacao))
            .filter(mod -> mod.getModalidadeLicitacao().equals(configuracaoReservaDotacao.getModalidadeLicitacao()))
            .filter(mod -> mod.getNaturezaProcedimento().equals(configuracaoReservaDotacao.getNaturezaProcedimento()))
            .map(mod -> "Modalidade/Natureza do Procedimento já foi adicionada à lista.").forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        ve.lancarException();
    }


    public void validarEdicaoConfigDotacao(ConfiguracaoReservaDotacao config) {
        ValidacaoException ve = new ValidacaoException();
        List<SolicitacaoMaterial> solicitacoes = configuracaoLicitacaoFacade.getSolicitacaoMaterialFacade().buscarSolicitacaoPorModalidadeNatureza(config.getModalidadeLicitacao(), config.getNaturezaProcedimento());
        if (!Util.isListNullOrEmpty(solicitacoes)
            && solicitacoes.stream().anyMatch(sol -> configuracaoLicitacaoFacade.getSolicitacaoMaterialFacade().hasVinculoComDotacao(sol)
            || configuracaoLicitacaoFacade.getSolicitacaoMaterialFacade().temVinculoComProcessoDeCompra(sol))) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não é permitido editar uma configuração que já foi utilizada no processo. ");
        }
        ve.lancarException();
    }

    public void cancelarConfigAnexo() {
        configuracaoAnexoLicitacao = null;
    }

    public ConfiguracaoProcessoCompra getConfiguracaoProcessoCompra() {
        return configuracaoProcessoCompra;
    }

    public void setConfiguracaoProcessoCompra(ConfiguracaoProcessoCompra configuracaoProcessoCompra) {
        this.configuracaoProcessoCompra = configuracaoProcessoCompra;
    }

    public UnidadeTercerializadaRh getUnidadeTercerializadaRh() {
        return unidadeTercerializadaRh;
    }

    public void setUnidadeTercerializadaRh(UnidadeTercerializadaRh unidadeTercerializadaRh) {
        this.unidadeTercerializadaRh = unidadeTercerializadaRh;
    }

    public UnidadeGrupoObjetoCompra getUnidadeGrupoObjetoCompra() {
        return unidadeGrupoObjetoCompra;
    }

    public void setUnidadeGrupoObjetoCompra(UnidadeGrupoObjetoCompra unidadeGrupoObjetoCompra) {
        this.unidadeGrupoObjetoCompra = unidadeGrupoObjetoCompra;
    }

    public ConfiguracaoSubstituicaoObjetoCompra getConfiguracaoSubstituicaoObjetoCompra() {
        return configuracaoSubstituicaoObjetoCompra;
    }

    public void setConfiguracaoSubstituicaoObjetoCompra(ConfiguracaoSubstituicaoObjetoCompra configuracaoSubstituicaoObjetoCompra) {
        this.configuracaoSubstituicaoObjetoCompra = configuracaoSubstituicaoObjetoCompra;
    }

    public ConfiguracaoReservaDotacao getConfiguracaoReservaDotacao() {
        return configuracaoReservaDotacao;
    }

    public void setConfiguracaoReservaDotacao(ConfiguracaoReservaDotacao configuracaoReservaDotacao) {
        this.configuracaoReservaDotacao = configuracaoReservaDotacao;
    }

    public ConfiguracaoAnexoLicitacao getConfiguracaoAnexoLicitacao() {
        return configuracaoAnexoLicitacao;
    }

    public void setConfiguracaoAnexoLicitacao(ConfiguracaoAnexoLicitacao configuracaoAnexoLicitacao) {
        this.configuracaoAnexoLicitacao = configuracaoAnexoLicitacao;
    }

    public ConfiguracaoAnexoLicitacaoTipoDocumento getConfiguracaoAnexoLicitacaoTipoDocumento() {
        return configuracaoAnexoLicitacaoTipoDocumento;
    }

    public void setConfiguracaoAnexoLicitacaoTipoDocumento(ConfiguracaoAnexoLicitacaoTipoDocumento configuracaoAnexoLicitacaoTipoDocumento) {
        this.configuracaoAnexoLicitacaoTipoDocumento = configuracaoAnexoLicitacaoTipoDocumento;
    }

    public void limparCacheURLPncpService() {
        this.pncpService.limparBaseUrl();
    }
}
