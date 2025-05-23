package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.PlanoContratacaoAnualGrupoObjetoCompraVO;
import br.com.webpublico.entidadesauxiliares.PlanoContratacaoAnualObjetoCompraContratoVO;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.enums.TipoRenumerarObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PlanoContratacaoAnualFacade;
import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.pncp.enums.TipoEventoPncp;
import br.com.webpublico.pncp.service.PncpService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPca", pattern = "/plano-contratacao-anual/novo/", viewId = "/faces/administrativo/licitacao/pca/edita.xhtml"),
    @URLMapping(id = "editarPca", pattern = "/plano-contratacao-anual/editar/#{planoContratacaoAnualControlador.id}/", viewId = "/faces/administrativo/licitacao/pca/edita.xhtml"),
    @URLMapping(id = "verPca", pattern = "/plano-contratacao-anual/ver/#{planoContratacaoAnualControlador.id}/", viewId = "/faces/administrativo/licitacao/pca/visualizar.xhtml"),
    @URLMapping(id = "listarPca", pattern = "/plano-contratacao-anual/listar/", viewId = "/faces/administrativo/licitacao/pca/lista.xhtml"),})

public class PlanoContratacaoAnualControlador extends PrettyControlador<PlanoContratacaoAnual> implements Serializable, CRUD {

    @EJB
    private PlanoContratacaoAnualFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private PlanoContratacaoAnualGrupoObjetoCompra pcaGrupoObjetoCompra;
    private PlanoContratacaoAnualObjetoCompra pcaObjetoCompra;
    private List<PlanoContratacaoAnualGrupoObjetoCompraVO> gruposVo;
    private Integer tabIndex;
    private TipoObjetoCompra tipoObjetoCompra;
    private Boolean objetoCriadoPeloBotaoNovoObjeto = Boolean.FALSE;
    private TipoRenumerarObjetoCompra tipoRenumerarObjetoCompra;
    private List<PlanoContratacaoAnualObjetoCompra> objetosCompra;
    private PlanoContratacaoAnualObjetoCompraContratoVO contratoSelecionadoVO;
    private List<PlanoContratacaoAnualObjetoCompraContratoVO> contratosVO;
    private Boolean isGrupoPreDeterminado = Boolean.FALSE;
    private ConverterAutoComplete converterPlanoContratacaoAnualObjetoCompra;

    public PlanoContratacaoAnualControlador() {
        super(PlanoContratacaoAnual.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoPca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioElaboracao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());

        HierarquiaOrganizacional hoOrgao = facade.getHierarquiaOrganizacionalFacade().buscarOrgaoDaUnidade(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente(), facade.getSistemaFacade().getDataOperacao());
        selecionado.setUnidadeOrganizacional(hoOrgao.getSubordinada());

        atribuirHierarquia();
        tabIndex = 0;
        tipoRenumerarObjetoCompra = TipoRenumerarObjetoCompra.CODIGO_OBJETO_COMPRA;
    }

    @URLAction(mappingId = "verPca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        ordenarObjetosCompraPorNumero();
        montarListaGeralObjetoCompra();
    }

    @URLAction(mappingId = "editarPca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        atribuirHierarquia();
        Collections.sort(selecionado.getGruposObjetoCompra());
        for (PlanoContratacaoAnualGrupoObjetoCompra grupo : selecionado.getGruposObjetoCompra()) {
            Collections.sort(grupo.getObjetosCompra());
        }
        tipoRenumerarObjetoCompra = TipoRenumerarObjetoCompra.CODIGO_OBJETO_COMPRA;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/plano-contratacao-anual/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarRegrasSalvar();
            selecionado = facade.salvarRetornando(selecionado);
            redirecionarParaVerOrEditar(selecionado.getId(), "ver");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void validarRegrasSalvar() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getGruposObjetoCompra().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O plano não possui grupo objeto de compra.");
        }

        if (selecionado.getId() == null && facade.hasPlanoParaUnidadeAndExercicio(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Plano de Contratações Anual para esta unidade e exercício.");
        }
        ve.lancarException();
    }

    private void atribuirHierarquia() {
        setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(facade.getSistemaFacade().getDataOperacao(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
    }

    public boolean hasGrupoOc() {
        return pcaGrupoObjetoCompra != null;
    }

    public boolean hasGruposVo() {
        return gruposVo != null && !gruposVo.isEmpty();
    }

    public void novoGrupoOc() {
        pcaGrupoObjetoCompra = new PlanoContratacaoAnualGrupoObjetoCompra();
        pcaGrupoObjetoCompra.setPlanoContratacaoAnual(selecionado);
        FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view:grupo-oc_input')");
    }

    public void cancelarGrupoOc() {
        pcaGrupoObjetoCompra = null;
    }

    public void adicionarGrupoOcSelecionados() {
        try {
            if (hasGruposVo()) {
                for (PlanoContratacaoAnualGrupoObjetoCompraVO grupo : gruposVo) {
                    if (grupo.getSelecionado()) {
                        novoGrupoOc();
                        pcaGrupoObjetoCompra.setGrupoObjetoCompra(grupo.getGrupoObjetoCompra());
                        validarGrupoOc();
                        Util.adicionarObjetoEmLista(selecionado.getGruposObjetoCompra(), pcaGrupoObjetoCompra);
                    }
                }
            }
            FacesUtil.atualizarComponente("Formulario:tab-view:pn-grupo");
            FacesUtil.executaJavaScript("dlgPesquisaGrupoOc.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    public void adicionarGrupoOc() {
        try {
            validarGrupoOc();
            Util.adicionarObjetoEmLista(selecionado.getGruposObjetoCompra(), pcaGrupoObjetoCompra);
            novoGrupoOc();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerGrupoOc(PlanoContratacaoAnualGrupoObjetoCompra obj) {
        selecionado.getGruposObjetoCompra().remove(obj);
    }

    public void selecionarGrupoOc(PlanoContratacaoAnualGrupoObjetoCompra obj) {
        pcaGrupoObjetoCompra = obj;
        tabIndex = 1;
        objetoCriadoPeloBotaoNovoObjeto = Boolean.FALSE;
        isGrupoPreDeterminado = Boolean.TRUE;
        novoObjetoCompra();
        FacesUtil.atualizarComponente("Formulario:tab-view");
    }

    public void validarGrupoOc() {
        Util.validarCampos(pcaGrupoObjetoCompra);
        ValidacaoException ve = new ValidacaoException();
        for (PlanoContratacaoAnualGrupoObjetoCompra grupo : selecionado.getGruposObjetoCompra()) {
            if (grupo.getGrupoObjetoCompra().equals(pcaGrupoObjetoCompra.getGrupoObjetoCompra())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O grupo objeto de compra " + grupo.getGrupoObjetoCompra().getCodigo() + "já foi adicionado à lista.");
            }
        }
        ve.lancarException();
    }

    public boolean hasObjetoCompra() {
        return pcaObjetoCompra != null;
    }

    public void novoObjetoCompra() {
        pcaObjetoCompra = new PlanoContratacaoAnualObjetoCompra();
        pcaObjetoCompra.setPlanoContratacaoAnualGrupo(pcaGrupoObjetoCompra);
        FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view:objeto-compra_input')");
    }

    public void cancelarObjetoCompra() {
        pcaObjetoCompra = null;
    }

    public void adicionarObjetoCompra() {
        try {
            validarObjetoCompra();
            if (objetoCriadoPeloBotaoNovoObjeto) {
                Util.adicionarObjetoEmLista(selecionado.getGruposObjetoCompra(), pcaGrupoObjetoCompra);
                pcaObjetoCompra.setPlanoContratacaoAnualGrupo(pcaGrupoObjetoCompra);
            }

            if (pcaObjetoCompra.getNumero() == null) {
                pcaObjetoCompra.setNumero(getMaiorNumeroDisponivelPcaObjetoCompra() + 1);
            }
            Util.adicionarObjetoEmLista(pcaGrupoObjetoCompra.getObjetosCompra(), pcaObjetoCompra);
            novoObjetoCompra();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private Integer getMaiorNumeroDisponivelPcaObjetoCompra() {
        Integer maiorNumeroDisponivel = 0;
        if (selecionado.getGruposObjetoCompra() != null && !selecionado.getGruposObjetoCompra().isEmpty()) {
            for (PlanoContratacaoAnualGrupoObjetoCompra pcaGrupo : selecionado.getGruposObjetoCompra()) {
                if (pcaGrupo.getObjetosCompra() != null && !pcaGrupo.getObjetosCompra().isEmpty()) {
                    for (PlanoContratacaoAnualObjetoCompra pcaObjetoCompra : pcaGrupo.getObjetosCompra()) {
                        if (pcaObjetoCompra.getNumero().compareTo(maiorNumeroDisponivel) > 0) {
                            maiorNumeroDisponivel = pcaObjetoCompra.getNumero();
                        }
                    }
                }
            }
        }
        return maiorNumeroDisponivel;
    }

    public void removerObjetoCompra(PlanoContratacaoAnualObjetoCompra obj) {
        if (pcaGrupoObjetoCompra != null) {
            pcaGrupoObjetoCompra.getObjetosCompra().remove(obj);
        } else {
            objetosCompra.remove(obj);
            obj.getPlanoContratacaoAnualGrupo().getObjetosCompra().remove(obj);
        }
    }

    public void editarObjetoCompra(PlanoContratacaoAnualObjetoCompra obj) {
        pcaObjetoCompra = obj;
        pcaGrupoObjetoCompra = obj.getPlanoContratacaoAnualGrupo();
    }

    public void validarObjetoCompra() {
        Util.validarCampos(pcaObjetoCompra);
        ValidacaoException ve = new ValidacaoException();
        for (PlanoContratacaoAnualObjetoCompra obj : pcaGrupoObjetoCompra.getObjetosCompra()) {
            if (!obj.equals(pcaObjetoCompra)
                && obj.getObjetoCompra().equals(pcaObjetoCompra.getObjetoCompra())
                && obj.getEspecificacao().equals(pcaObjetoCompra.getEspecificacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O objeto de compra já foi adicionado à lista.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposObjetoCompra() {
        return Util.getListSelectItemSemCampoVazio(TipoObjetoCompra.values());
    }

    public List<GrupoObjetoCompra> completarGrupoObjetoCompra(String parte) {
        return facade.getGrupoObjetoCompraFacade().buscarGruposAssociadoObjetoCompra(parte.trim(), null);
    }

    public void buscarGruposOc() {
        List<GrupoObjetoCompra> grupos = facade.getGrupoObjetoCompraFacade().buscarGruposAssociadoObjetoCompra("", tipoObjetoCompra);
        gruposVo = Lists.newArrayList();
        for (GrupoObjetoCompra grupo : grupos) {
            PlanoContratacaoAnualGrupoObjetoCompraVO grupoVo = new PlanoContratacaoAnualGrupoObjetoCompraVO(grupo);
            gruposVo.add(grupoVo);
        }
    }

    public void selecionarTodosGrupos(boolean selecinado) {
        if (hasGruposVo()) {
            for (PlanoContratacaoAnualGrupoObjetoCompraVO item : gruposVo) {
                item.setSelecionado(selecinado);
            }
        }
    }

    public boolean todosGruposSelcionados() {
        if (!hasGruposVo()) {
            return false;
        }
        boolean todosSelecionados = true;
        for (PlanoContratacaoAnualGrupoObjetoCompraVO item : gruposVo) {
            if (!item.getSelecionado()) {
                todosSelecionados = false;
                break;
            }
        }
        return todosSelecionados;
    }

    public List<ObjetoCompra> completarObjetoCompra(String parte) {
        if (hasGrupoOc() && pcaGrupoObjetoCompra.getGrupoObjetoCompra() != null) {
            return facade.getObjetoCompraFacade().buscarPorGrupoObjetoCompra(parte.trim(), pcaGrupoObjetoCompra.getGrupoObjetoCompra());
        }
        return facade.getObjetoCompraFacade().completaObjetoCompra(parte.trim());
    }

    public List<HierarquiaOrganizacional> completarUnidadeAdministrativa(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaPorOrgaoAndTipoAdministrativaAndUsuario(parte.trim());
    }

    public void cancelarNovoObjetoCompra() {
        objetoCriadoPeloBotaoNovoObjeto = Boolean.FALSE;
        isGrupoPreDeterminado = Boolean.FALSE;
        cancelarGrupoOc();
        cancelarObjetoCompra();
    }

    public void novoObjeto() {
        tabIndex = 1;
        objetoCriadoPeloBotaoNovoObjeto = Boolean.TRUE;
        isGrupoPreDeterminado = Boolean.FALSE;
        pcaObjetoCompra = new PlanoContratacaoAnualObjetoCompra();
        FacesUtil.atualizarComponente("Formulario:tab-view");
    }

    public void adicionarGrupoDoObjeto() {
        if (objetoCriadoPeloBotaoNovoObjeto) {
            pcaGrupoObjetoCompra = selecionado.getGruposObjetoCompra().stream()
                .filter(g -> g.getGrupoObjetoCompra().equals(pcaObjetoCompra.getObjetoCompra().getGrupoObjetoCompra())).findFirst().orElse(null);

            if (pcaGrupoObjetoCompra == null) {
                pcaGrupoObjetoCompra = new PlanoContratacaoAnualGrupoObjetoCompra();
                pcaGrupoObjetoCompra.setPlanoContratacaoAnual(selecionado);
                pcaGrupoObjetoCompra.setGrupoObjetoCompra(pcaObjetoCompra.getObjetoCompra().getGrupoObjetoCompra());
            }
        }
    }

    public void limparEspecificacao() {
        pcaObjetoCompra.setEspecificacao(null);
    }

    public void selecionarEspecificacao(ActionEvent evento) {
        ObjetoDeCompraEspecificacao especificacao = (ObjetoDeCompraEspecificacao) evento.getComponent().getAttributes().get("objeto");
        pcaObjetoCompra.setEspecificacao(especificacao.getTexto());
    }

    public void abrirTabelaEspecificacao(ObjetoCompra objetoCompra) {
        try {
            if (objetoCompra != null) {
                TabelaEspecificacaoObjetoCompraControlador tabelaEspecificacaoControlador = (TabelaEspecificacaoObjetoCompraControlador) Util.getControladorPeloNome("tabelaEspecificacaoObjetoCompraControlador");
                tabelaEspecificacaoControlador.recuperarObjetoCompra(objetoCompra);
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void montarListaGeralObjetoCompra() {
        objetosCompra = (selecionado.getGruposObjetoCompra()
            .stream()
            .flatMap(grupo -> grupo.getObjetosCompra().stream())
            .collect(Collectors.toList()))
            .stream().sorted(Comparator.comparing(PlanoContratacaoAnualObjetoCompra::getNumero)).collect(Collectors.toList());
    }

    public List<SelectItem> getTipoRenumerar() {
        return Util.getListSelectItemSemCampoVazio(TipoRenumerarObjetoCompra.values());
    }

    public void renumerar() {
        List<PlanoContratacaoAnualObjetoCompra> listaPlanosAgrupada = selecionado.getGruposObjetoCompra()
            .stream().flatMap(pcaGrupo -> pcaGrupo.getObjetosCompra().stream()).collect(Collectors.toList());

        List<PlanoContratacaoAnualObjetoCompra> listaPlanosAgrupadaOrdenada = ordenarlistaPlanosAgrupada(listaPlanosAgrupada);

        IntStream.range(0, listaPlanosAgrupada.size())
            .forEach(i -> listaPlanosAgrupadaOrdenada.get(i).setNumero(i + 1));

        atualizarObjetosComAListaAgrupadaOrdenada(listaPlanosAgrupadaOrdenada);

        ordenarObjetosCompraPorNumero();

        FacesUtil.executaJavaScript("dlgRenumerar.hide()");
        FacesUtil.atualizarComponente("Formulario:tab-view:tabela-objetos");
    }

    public List<PlanoContratacaoAnualObjetoCompra> ordenarlistaPlanosAgrupada(List<PlanoContratacaoAnualObjetoCompra> listaPlanosAgrupada) {
        if (TipoRenumerarObjetoCompra.CODIGO_OBJETO_COMPRA.equals(tipoRenumerarObjetoCompra)) {
            return listaPlanosAgrupada
                .stream().sorted(Comparator.comparing(p -> p.getObjetoCompra().getCodigo())).collect(Collectors.toList());
        } else if (TipoRenumerarObjetoCompra.DESCRICAO_OBJETO_COMPRA.equals(tipoRenumerarObjetoCompra)) {
            return listaPlanosAgrupada
                .stream().sorted(Comparator.comparing(p -> p.getObjetoCompra().getDescricao())).collect(Collectors.toList());
        }
        return listaPlanosAgrupada;
    }

    public void atualizarObjetosComAListaAgrupadaOrdenada(List<PlanoContratacaoAnualObjetoCompra> listaPlanosAgrupadaOrdenada) {
        for (PlanoContratacaoAnualObjetoCompra pcaObj : listaPlanosAgrupadaOrdenada) {
            for (PlanoContratacaoAnualGrupoObjetoCompra grupo : selecionado.getGruposObjetoCompra()) {
                if (grupo.getObjetosCompra().contains(pcaObj)) {
                    grupo.getObjetosCompra().set(grupo.getObjetosCompra().indexOf(pcaObj), pcaObj);
                }
            }
        }
    }

    private void ordenarObjetosCompraPorNumero() {
        selecionado.getGruposObjetoCompra().forEach(pcaGrupo ->
            pcaGrupo.setObjetosCompra(
                pcaGrupo.getObjetosCompra().stream()
                    .sorted(Comparator.comparing(PlanoContratacaoAnualObjetoCompra::getNumero))
                    .collect(Collectors.toList())
            )
        );
    }

    public void buscarContratosObjetoCompra() {
        if (pcaObjetoCompra.getObjetoCompra() != null) {
            contratosVO = facade.buscarContratoPorObjetoCompra(pcaObjetoCompra.getObjetoCompra().getId());
        }
    }

    public void confirmarContrato() {
        if (contratoSelecionadoVO != null) {
            pcaObjetoCompra.setValorUnitario(contratoSelecionadoVO.getValorUnitario());
            pcaObjetoCompra.calcularValorTotal();

            FacesUtil.atualizarComponente("Formulario:tab-view:valorUnit");
            FacesUtil.atualizarComponente("Formulario:tab-view:vl-total");

            cancelarContrato();
        } else {
            FacesUtil.addCampoObrigatorio("Selecione um contrato.");
        }
    }

    public void cancelarContrato() {
        setContratoSelecionadoVO(null);
        FacesUtil.executaJavaScript("dlgUltimosValoresObjetoCompra.hide()");
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public PlanoContratacaoAnualGrupoObjetoCompra getPcaGrupoObjetoCompra() {
        return pcaGrupoObjetoCompra;
    }

    public void setPcaGrupoObjetoCompra(PlanoContratacaoAnualGrupoObjetoCompra pcaGrupoObjetoCompra) {
        this.pcaGrupoObjetoCompra = pcaGrupoObjetoCompra;
    }

    public PlanoContratacaoAnualObjetoCompra getPcaObjetoCompra() {
        return pcaObjetoCompra;
    }

    public void setPcaObjetoCompra(PlanoContratacaoAnualObjetoCompra pcaObjetoCompra) {
        this.pcaObjetoCompra = pcaObjetoCompra;
    }

    public List<PlanoContratacaoAnualGrupoObjetoCompraVO> getGruposVo() {
        return gruposVo;
    }

    public void setGruposVo(List<PlanoContratacaoAnualGrupoObjetoCompraVO> gruposVo) {
        this.gruposVo = gruposVo;
    }

    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public Boolean getObjetoCriadoPeloBotaoNovoObjeto() {
        return objetoCriadoPeloBotaoNovoObjeto;
    }

    public void setObjetoCriadoPeloBotaoNovoObjeto(Boolean objetoCriadoPeloBotaoNovoObjeto) {
        this.objetoCriadoPeloBotaoNovoObjeto = objetoCriadoPeloBotaoNovoObjeto;
    }

    public TipoRenumerarObjetoCompra getTipoRenumerarObjetoCompra() {
        return tipoRenumerarObjetoCompra;
    }

    public void setTipoRenumerarObjetoCompra(TipoRenumerarObjetoCompra tipoRenumerarObjetoCompra) {
        this.tipoRenumerarObjetoCompra = tipoRenumerarObjetoCompra;
    }

    public List<PlanoContratacaoAnualObjetoCompra> getObjetosCompra() {
        return objetosCompra;
    }

    public void setObjetosCompra(List<PlanoContratacaoAnualObjetoCompra> objetosCompra) {
        this.objetosCompra = objetosCompra;
    }

    public PlanoContratacaoAnualObjetoCompraContratoVO getContratoSelecionadoVO() {
        return contratoSelecionadoVO;
    }

    public void setContratoSelecionadoVO(PlanoContratacaoAnualObjetoCompraContratoVO contratoSelecionadoVO) {
        this.contratoSelecionadoVO = contratoSelecionadoVO;
    }

    public List<PlanoContratacaoAnualObjetoCompraContratoVO> getContratosVO() {
        return contratosVO;
    }

    public void setContratosVO(List<PlanoContratacaoAnualObjetoCompraContratoVO> contratosVO) {
        this.contratosVO = contratosVO;
    }

    public Boolean getGrupoPreDeterminado() {
        return isGrupoPreDeterminado;
    }

    public void setGrupoPreDeterminado(Boolean grupoPreDeterminado) {
        isGrupoPreDeterminado = grupoPreDeterminado;
    }

    public ConverterAutoComplete getConverterPlanoContratacaoAnualObjetoCompra() {
        if (converterPlanoContratacaoAnualObjetoCompra == null) {
            converterPlanoContratacaoAnualObjetoCompra = new ConverterAutoComplete(PlanoContratacaoAnualObjetoCompra.class, facade.getPlanoContratacaoAnualObjetoCompraFacade());
        }
        return converterPlanoContratacaoAnualObjetoCompra;
    }

    public EventoPncpVO getEventoPncpVO() {
        EventoPncpVO eventoPncpVO = new EventoPncpVO();
        eventoPncpVO.setTipoEventoPncp(TipoEventoPncp.PLANO_CONTRATACAO_ANUAL);
        eventoPncpVO.setIdOrigem(selecionado.getId());
        eventoPncpVO.setIdPncp(selecionado.getIdPncp());
        eventoPncpVO.setSequencialIdPncp(selecionado.getSequencialIdPncp());
        eventoPncpVO.setAnoPncp(selecionado.getExercicio().getAno());
        eventoPncpVO.setIntegradoPNCP(selecionado.getIdPncp() != null && selecionado.getSequencialIdPncp() != null);
        return eventoPncpVO;
    }

    public void confirmarIdPncp(ActionEvent evento) {
        EventoPncpVO eventoPncpVO = (EventoPncpVO) evento.getComponent().getAttributes().get("objeto");
        selecionado.setIdPncp(eventoPncpVO.getIdPncp());
        selecionado.setSequencialIdPncp(eventoPncpVO.getSequencialIdPncp());
        PncpService.getService().criarEventoAtualizacaoIdSequencialPncp(selecionado.getId(), selecionado.getExercicio().getAno().toString());
        selecionado = facade.salvarRetornando(selecionado);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }
}
