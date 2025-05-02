package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.EnderecoLocalEstoque;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LocalEstoqueFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;


@ManagedBean(name = "localEstoqueControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLocalEstoque", pattern = "/local-de-estoque/novo/", viewId = "/faces/administrativo/materiais/localestoque/edita.xhtml"),
    @URLMapping(id = "editarLocalEstoque", pattern = "/local-de-estoque/editar/#{localEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/localestoque/edita.xhtml"),
    @URLMapping(id = "listarLocalEstoque", pattern = "/local-de-estoque/listar/", viewId = "/faces/administrativo/materiais/localestoque/lista.xhtml"),
    @URLMapping(id = "verLocalEstoque", pattern = "/local-de-estoque/ver/#{localEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/localestoque/visualizar.xhtml"),
    @URLMapping(id = "adiconarFilhoLocalEstoque", pattern = "/local-de-estoque/adicionar-filho/#{localEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/localestoque/edita.xhtml"),
    @URLMapping(id = "associativaMaterialLocalEstoque", pattern = "/associativa-material-local-de-estoque/", viewId = "/faces/administrativo/materiais/localestoque/associativa-material.xhtml")
})
public class LocalEstoqueControlador extends PrettyControlador<LocalEstoque> implements Serializable, CRUD {

    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    private HierarquiaOrganizacional hierarquiaSelecionada;
    private String mascaraDoCodigo;
    private LocalEstoqueMaterial localEstoqueMaterial;
    private GrupoMaterial grupoMaterial;
    private boolean exibirPanelGrupoMaterial;
    private boolean exibirPanelMateriais;
    private GestorLocalEstoque gestorLocalEstoqueSelecionado;
    private LocalEstoque pai;
    private ArvoreLocalEstoque arvoreLocalEstoque;
    private LazyDataModel<LocalEstoqueMaterial> model;
    private FiltroLocalEstoqueMaterial filtro;
    private List<LocalEstoqueMaterial> materiaisNovos;
    private List<LocalEstoqueMaterial> materiaisParaExcluir;
    private ConverterAutoComplete converterCadastroImobiliario;
    private EnderecoLocalEstoque enderecoLocalEstoque;

    public LocalEstoqueControlador() {
        metadata = new EntidadeMetaData(LocalEstoque.class);
        gestorLocalEstoqueSelecionado = new GestorLocalEstoque();
    }

    @Override
    public void salvar() {
        if (hierarquiaSelecionada != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaSelecionada.getSubordinada());
        }

        if (podeSalvar()) {
            if (selecionado.getId() == null) {
                localEstoqueFacade.salvarNovo(selecionado, materiaisNovos);
            } else {
                localEstoqueFacade.salvar(selecionado, materiaisNovos, materiaisParaExcluir);
            }

            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            redireciona();
        }
    }

    public void salvarAssociacao() {
        try {
            validarCampos();
            localEstoqueFacade.salvar(selecionado, materiaisNovos, materiaisParaExcluir);
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Local de Estoque deve ser informado.");
        }
        ve.lancarException();
    }

    public Date getDataOperacao() {
        return localEstoqueFacade.getSistemaFacade().getDataOperacao();
    }

    public LazyDataModel<LocalEstoqueMaterial> getModel() {
        return model;
    }

    public void setModel(LazyDataModel<LocalEstoqueMaterial> model) {
        this.model = model;
    }

    private boolean podeSalvar() {
        if (localEstoqueFacade.validaCodigoDoLocalRepetido(selecionado)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Código repetido! Informe outro código para cadastrar este local de estoque.");
            return false;
        }

        if (selecionado.getListaGestorLocalEstoque().isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe pelo menos um gestor para este local de estoque.");
            return false;
        }

        return Util.validaCampos(selecionado);
    }

    @Override
    @URLAction(mappingId = "novoLocalEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();

        hierarquiaSelecionada = (HierarquiaOrganizacional) Web.pegaDaSessao(HierarquiaOrganizacional.class);
        if (hierarquiaSelecionada == null) {
            hierarquiaSelecionada = new HierarquiaOrganizacional();
        }
        selecionado.setTipoEstoque(TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO);
        selecionado.setCodigo(localEstoqueFacade.geraCodigoNovo());
        recuperarMascaraDoCodigo();

        Material material = (Material) Web.pegaDaSessao(Material.class);
        if (material != null) {
            localEstoqueMaterial = new LocalEstoqueMaterial();
            localEstoqueMaterial.setLocalEstoque(selecionado);
            localEstoqueMaterial.setMaterial(material);
            exibirPanelMateriais = Boolean.TRUE;
        }

        arvoreLocalEstoque = new ArvoreLocalEstoque(selecionado);
        materiaisNovos = Lists.newArrayList();
        materiaisParaExcluir = Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "editarLocalEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        operacao = Operacoes.EDITAR;
        filtro = new FiltroLocalEstoqueMaterial();
        selecionado = localEstoqueFacade.recuperarLocalDeEstoqueSemMateriais(getId());

        criarItensPaginacao();

        getHierarquiaDaUnidade();

        int nivelDaMascara = recuperarNivel(selecionado.getCodigo());

        if (nivelDaMascara > 1) {
            int posicaoDoCodigoQuePodeSerAlterado = selecionado.getCodigo().lastIndexOf(".");
            String codigoQuePodeSerAlterado = selecionado.getCodigo().substring(posicaoDoCodigoQuePodeSerAlterado + 1, selecionado.getCodigo().length());
            String codigo = "";

            for (int i = 0; i < codigoQuePodeSerAlterado.length(); i++) {
                codigo += "9";
            }

            mascaraDoCodigo = selecionado.getCodigo().substring(0, posicaoDoCodigoQuePodeSerAlterado) + "." + codigo;
        } else {
            recuperarMascaraDoCodigo();
        }

        Material material = (Material) Web.pegaDaSessao(Material.class);
        if (material != null) {
            localEstoqueMaterial = new LocalEstoqueMaterial();
            localEstoqueMaterial.setLocalEstoque(selecionado);
            localEstoqueMaterial.setMaterial(material);
            exibirPanelMateriais = Boolean.TRUE;
        }
        arvoreLocalEstoque = new ArvoreLocalEstoque(selecionado);

        materiaisNovos = Lists.newArrayList();
        materiaisParaExcluir = Lists.newArrayList();
        buscarEnderecoLocalEstoque();
    }

    public void buscarMateriais() {
        selecionado = localEstoqueFacade.recuperarLocalDeEstoqueSemMateriais(selecionado.getId());
        criarItensPaginacao();
    }

    private void criarItensPaginacao() {
        model = new LazyDataModel<LocalEstoqueMaterial>() {

            @Override
            public List<LocalEstoqueMaterial> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                filtro.setLocalEstoque(selecionado);
                filtro.setPrimeiroRegistro(first);
                filtro.setQuantidadeRegistro(pageSize);
                filtro.materialExclusao.addAll(materiaisParaExcluir);
                setRowCount(localEstoqueFacade.quantidadeMaterial(filtro));
                return localEstoqueFacade.recuperarLocalEstoqueMaterial(filtro);
            }
        };
    }

    private void getHierarquiaDaUnidade() {
        hierarquiaSelecionada = localEstoqueFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getUnidadeOrganizacional(), localEstoqueFacade.getSistemaFacade().getDataOperacao());
    }

    public List<LocalEstoque> completarLocaisDeEstoque(String filtro) {
        return localEstoqueFacade.completarLocalEstoqueAbertos(filtro);
    }

    public List<CadastroImobiliario> completarCadastroImobiliario(String filtro) {
        return localEstoqueFacade.getCadastroImobiliarioFacade().buscarCadastroImobiliarioAtivo(filtro.trim());
    }

    public void buscarEnderecoLocalEstoque() {
        if (selecionado.getCadastroImobiliario() != null) {
            selecionado.setCadastroImobiliario(localEstoqueFacade.getCadastroImobiliarioFacade().recuperar(selecionado.getCadastroImobiliario().getId()));
            enderecoLocalEstoque = localEstoqueFacade.recuperarEnderecoLocalEstoque(selecionado.getCadastroImobiliario().getId());
            verInscricaoMapa();
        }
    }

    public Converter getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, localEstoqueFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public void verInscricaoMapa() {
        if (selecionado.getCadastroImobiliario() != null) {
            int setor = Integer.parseInt(selecionado.getCadastroImobiliario().getLote().getSetor().getCodigo());
            int quadra = selecionado.getCadastroImobiliario().getLote().getQuadra().getCodigoToInteger();
            int lote = Integer.parseInt(selecionado.getCadastroImobiliario().getLote().getCodigoLote());
            String inscricaoMapa = "1-" + setor + "-" + quadra + "-" + lote;
            FacesUtil.executaJavaScript("verIncricao('" + inscricaoMapa + "')");
        }
    }

    @URLAction(mappingId = "verLocalEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        filtro = new FiltroLocalEstoqueMaterial();
        materiaisNovos = Lists.newArrayList();
        materiaisParaExcluir = Lists.newArrayList();
        selecionado = localEstoqueFacade.recuperarLocalDeEstoqueSemMateriais(getId());
        criarItensPaginacao();
        getHierarquiaDaUnidade();
        if (arvoreLocalEstoque == null) {
            arvoreLocalEstoque = new ArvoreLocalEstoque(selecionado);
        }
        buscarEnderecoLocalEstoque();
    }

    @URLAction(mappingId = "adiconarFilhoLocalEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoFilho() {
        pai = localEstoqueFacade.recuperarComDependenciaGestor(super.getId());
        super.novo();
        selecionado.setCodigo(localEstoqueFacade.gerarCodigoFilho(pai));
        selecionado.setSuperior(pai);
        selecionado.setUnidadeOrganizacional(pai.getUnidadeOrganizacional());
        selecionado.setTipoEstoque(pai.getTipoEstoque());
        for (GestorLocalEstoque g : pai.getListaGestorLocalEstoque()) {
            selecionado.getListaGestorLocalEstoque().add(new GestorLocalEstoque(g, selecionado));
        }
        getHierarquiaDaUnidade();
        int nivel = recuperarNivel(pai.getCodigo());
        String mascara = localEstoqueFacade.recuperaMascaraDoCodigoLocalEstoque();
        String mascaraAtePrimeiroPonto[] = mascara.split("\\.");
        String mascaraDoCodigoDoNivel = mascaraAtePrimeiroPonto[nivel].replaceAll("#", "9");
        mascaraDoCodigo = pai.getCodigo() + "." + mascaraDoCodigoDoNivel;
        selecionado.setListaLocalEstoqueMaterial(new ArrayList<LocalEstoqueMaterial>());
        arvoreLocalEstoque = new ArvoreLocalEstoque(pai);
    }

    @URLAction(mappingId = "associativaMaterialLocalEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoassociativaMaterialLocalEstoque() {
        operacao = Operacoes.EDITAR;
        selecionado = null;
        filtro = new FiltroLocalEstoqueMaterial();
        materiaisNovos = Lists.newArrayList();
        materiaisParaExcluir = Lists.newArrayList();
        LocalEstoque localEstoque = (LocalEstoque) Web.pegaDaSessao("LOCAL_ESTOQUE");
        if (localEstoque != null) {
            selecionado = localEstoque;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/local-de-estoque/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void limparListaDeGestor() {
        if (selecionado.getListaGestorLocalEstoque() != null) {
            selecionado.getListaGestorLocalEstoque().clear();
            if (pai != null) {
                for (GestorLocalEstoque g : pai.getListaGestorLocalEstoque()) {
                    selecionado.getListaGestorLocalEstoque().add(new GestorLocalEstoque(g, selecionado));
                }
                selecionado.setDescricao(hierarquiaSelecionada.getDescricao());
            }
        }
    }

    public void navegarMaterial() {
        Web.navegacao(getUrlAtual(), "/material/novo/", selecionado, hierarquiaSelecionada);
    }

    private void recuperarMascaraDoCodigo() {
        String mascara = localEstoqueFacade.recuperaMascaraDoCodigoLocalEstoque();
        String mascaraAtePrimeiroPonto[] = mascara.split("\\.");
        mascaraDoCodigo = mascaraAtePrimeiroPonto[0].replaceAll("#", "9");
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String filtro) {
        if (isLocalEstoqueFilho()) {
            HierarquiaOrganizacional hoAdm = localEstoqueFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(
                localEstoqueFacade.getSistemaFacade().getDataOperacao(),
                selecionado.getSuperior().getUnidadeOrganizacional(),
                TipoHierarquiaOrganizacional.ADMINISTRATIVA);

            return localEstoqueFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalAdministrativaFilhasOndeUsuarioEhGestorMaterial(
                filtro
                , null,
                localEstoqueFacade.getSistemaFacade().getDataOperacao(),
                localEstoqueFacade.getSistemaFacade().getUsuarioCorrente(),
                hoAdm);
        }
        return localEstoqueFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalTipo(filtro.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), localEstoqueFacade.getSistemaFacade().getDataOperacao());
    }

    public List<UsuarioSistema> completaUsuariosGestoresDeMateriaisDaUnidade(String filtro) {
        if (hierarquiaSelecionada != null && hierarquiaSelecionada.getSubordinada() != null) {
            return localEstoqueFacade.getUsuarioSistemaFacade().recuperarUsuariosGestoresDeMateriaisDaUnidade(hierarquiaSelecionada.getSubordinada(), filtro);
        }
        FacesUtil.addOperacaoNaoPermitida("Informe a unidade organizacional para selecionar os gestores do local de estoque.");
        return null;
    }

    public List<LocalEstoque> completarLocalEstoqueUsuarioGestorDeMaterial(String filtro) {
        return localEstoqueFacade.completaLocaisDeEstoquePorUsuarioGestor(filtro, localEstoqueFacade.getSistemaFacade().getUsuarioCorrente(), localEstoqueFacade.getSistemaFacade().getDataOperacao());
    }

    public Boolean paiPodeTerFilhos(Long idLocalEstoque) {
        String mascara = localEstoqueFacade.recuperaMascaraDoCodigoLocalEstoque();
        if (mascara != null) {
            String codigoLocalEstoque = localEstoqueFacade.getCodigoLocalEstoque(idLocalEstoque);
            if (codigoLocalEstoque.length() == mascara.length()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void excluir() {
        if (validaVinculoDoLocalEstoquePossuirFilho()) {
            return;
        }

        if (validaVinculoDoLocalEstoquePossuiEstoque()) {
            return;
        }

        if (validarVinculoDoLocalEstoqueComEntrada()) {
            return;
        }

        if (validarVinculoDoLocalEstoqueComSaida()) {
            return;
        }
        super.excluir();
    }

    public LocalEstoqueMaterial getLocalEstoqueMaterial() {
        return localEstoqueMaterial;
    }

    public void setLocalEstoqueMaterial(LocalEstoqueMaterial localEstoqueMaterial) {
        this.localEstoqueMaterial = localEstoqueMaterial;
    }

    @Override
    public AbstractFacade getFacede() {
        return localEstoqueFacade;
    }

    private int recuperarNivel(String codigoDoPai) {
        int nivel = 1;
        for (int i = 0; i < codigoDoPai.length(); i++) {
            char c = codigoDoPai.charAt(i);
            if (c == '.') {
                nivel++;
            }
        }
        return nivel;
    }

    public boolean bloquearCodigo() {
        LocalEstoque filho = getLocalEstoqueFilho();
        if (filho != null) {
            return true;
        }
        if (isLocalEstoqueFilho()) {
            return true;
        }
        return false;
    }

    public boolean isLocalEstoqueFilho() {
        return selecionado.getSuperior() != null;
    }

    private LocalEstoque getLocalEstoqueFilho() {
        return localEstoqueFacade.buscaLocalFilhoDoLocalDeEstoque(selecionado);
    }

    public String getMensagemMotivoBloqueioCodigo() {
        String mensagem = "*Código bloqueado devido este local de estoque ";
        if (getLocalEstoqueFilho() != null) {
            return mensagem + "possuir filho(s) vinculado(s)!";
        }
        if (isLocalEstoqueFilho()) {
            return mensagem + "ser filho de outro local de estoque!";
        }
        return "";
    }

    private boolean validaVinculoDoLocalEstoquePossuiEstoque() {
        Estoque estoqueRecuperado = localEstoqueFacade.getEstoqueFacade().validaLocalDeEstoquePossuiEstoque(selecionado);
        if (estoqueRecuperado != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar excluir o local de estoque!", "O Local de estoque  Cod: " + selecionado.getCodigo() + " - " + selecionado.getDescricao().toUpperCase() + " possui estoque fisíco de " + estoqueRecuperado.getFisico() + " do material Cod: " + estoqueRecuperado.getMaterial().getCodigo() + " - " + estoqueRecuperado.getMaterial().getDescricao().toUpperCase()));
            return true;
        }
        return false;
    }

    public boolean validaVinculoDoLocalEstoquePossuirFilho() {
        LocalEstoque localRecuperado = getLocalEstoqueFilho();
        if (localRecuperado != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar excluir o local de estoque!", "O Local de estoque  Cod: " + selecionado.getCodigo() + " - " + selecionado.getDescricao().toUpperCase() + " possui uma associação(FILHO) com o Local de estoque Cod: " + localRecuperado.getCodigo() + " - " + localRecuperado.getDescricao().toUpperCase()));
            return true;
        }
        return false;
    }

    public HierarquiaOrganizacional getHierarquiaSelecionada() {
        return hierarquiaSelecionada;
    }

    public void setHierarquiaSelecionada(HierarquiaOrganizacional hierarquiaSelecionada) {
        if (hierarquiaSelecionada != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaSelecionada.getSubordinada());
        }
        this.hierarquiaSelecionada = hierarquiaSelecionada;
    }

    private boolean validarVinculoDoLocalEstoqueComEntrada() {
        ItemEntradaMaterial itemRecuperado = localEstoqueFacade.getEstoqueFacade().validaVinculoDoLocalEstoqueComEntrada(selecionado);
        if (itemRecuperado != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar excluir o local de estoque!", "O Local de estoque  Cod: " + selecionado.getCodigo() + " - " + selecionado.getDescricao().toUpperCase() + " possui vinculo com a Entrada Material  Nrº : " + itemRecuperado.getEntradaMaterial().getNumero() + " na data de :" + new SimpleDateFormat("dd/MM/yyyy").format(itemRecuperado.getEntradaMaterial().getDataEntrada())));
            return true;
        }
        return false;
    }

    private boolean validarVinculoDoLocalEstoqueComSaida() {
        ItemSaidaMaterial itemRecuperado = localEstoqueFacade.getEstoqueFacade().validaVinculoDoLocalEstoqueComSaida(selecionado);
        if (itemRecuperado != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar excluir o local de estoque!", "O Local de estoque  Cod: " + selecionado.getCodigo() + " - " + selecionado.getDescricao().toUpperCase() + " possui vinculo com a Saída Material  Nrº : " + itemRecuperado.getSaidaMaterial().getNumero() + " na data de :" + new SimpleDateFormat("dd/MM/yyyy").format(itemRecuperado.getSaidaMaterial().getDataSaida())));
            return true;
        }
        return false;
    }

    public String getMascaraDoCodigo() {
        return mascaraDoCodigo;
    }

    public void setMascaraDoCodigo(String mascaraDoCodigo) {
        this.mascaraDoCodigo = mascaraDoCodigo;
    }

    public void criarNovoLocalEstoqueMaterial() {
        cancelarGrupoMaterial();
        this.localEstoqueMaterial = new LocalEstoqueMaterial();
        this.localEstoqueMaterial.setLocalEstoque(selecionado);
        exibirPanelMateriais = Boolean.TRUE;
    }

    public void cancelarEstoqueMaterial() {
        this.localEstoqueMaterial = null;
        exibirPanelMateriais = Boolean.FALSE;
    }

    public void criarNovoGrupoMaterial() {
        cancelarEstoqueMaterial();
        exibirPanelGrupoMaterial = Boolean.TRUE;
        this.grupoMaterial = null;
    }

    public void cancelarGrupoMaterial() {
        this.grupoMaterial = null;
        exibirPanelGrupoMaterial = Boolean.FALSE;
    }

    public void criarMateriaisDeferidos() {
        cancelarEstoqueMaterial();
        cancelarGrupoMaterial();
        materiaisParaExcluir.clear();
        materiaisNovos.clear();
        if (selecionado.getId() != null) {
            materiaisParaExcluir.addAll(localEstoqueFacade.recuperaListaLocalEstoqueMaterial(selecionado));
        }
        for (Material material : localEstoqueFacade.getMaterialFacade().lista()) {
            LocalEstoqueMaterial localEstoqueMaterial = new LocalEstoqueMaterial();
            localEstoqueMaterial.setLocalEstoque(selecionado);
            localEstoqueMaterial.setMaterial(material);
            materiaisNovos.add(localEstoqueMaterial);
        }
    }

    public GestorLocalEstoque getGestorLocalEstoqueSelecionado() {
        return gestorLocalEstoqueSelecionado;
    }

    public void setGestorLocalEstoqueSelecionado(GestorLocalEstoque gestorLocalEstoqueSelecionado) {
        this.gestorLocalEstoqueSelecionado = gestorLocalEstoqueSelecionado;
    }

    public void adicionarMaterialNaLista() {
        try {
            validarNovoMaterial();
            materiaisNovos.add(localEstoqueMaterial);
            criarNovoLocalEstoqueMaterial();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }


    public void removerItem(LocalEstoqueMaterial lem) {
        materiaisParaExcluir.add(lem);
    }

    public void removerMaterialNovo(LocalEstoqueMaterial localEstoqueMaterial) {
        materiaisNovos.remove(localEstoqueMaterial);
    }

    private void validarNovoMaterial() {
        ValidacaoException ve = new ValidacaoException();
        if (localEstoqueMaterial.getMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um material.");
            ve.lancarException();
        }
        if (selecionado.getId() == null) {
            verificarMaterialNaNovaLista(ve);
        } else {
            List<LocalEstoqueMaterial> localEstoqueMaterials = localEstoqueFacade.buscarListaLocalEstoqueMaterialEspecifico(selecionado, materiaisParaExcluir);
            for (LocalEstoqueMaterial lem : localEstoqueMaterials) {
                if (lem.getMaterial().equals(localEstoqueMaterial.getMaterial())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Material já adicionado.");
                    break;
                }
            }
            verificarMaterialNaNovaLista(ve);
        }
        ve.lancarException();

    }

    private void verificarMaterialNaNovaLista(ValidacaoException ve) {
        for (LocalEstoqueMaterial lem : materiaisNovos) {
            if (lem.getMaterial().equals(localEstoqueMaterial.getMaterial())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Material já adicionado.");
                break;
            }
        }
    }

    public void preencherListaDeMaterialPorGrupo() {
        if (!temGrupoMaterialInformado()) return;
        ArrayList<StatusMaterial> situacoesMaterial = Lists.newArrayList(StatusMaterial.DEFERIDO, StatusMaterial.AGUARDANDO, StatusMaterial.INATIVO);
        List<Material> listaDeMaterialDoGrupo = localEstoqueFacade.getMaterialFacade().buscarMaterialPorGrupoMaterial(this.grupoMaterial, "", situacoesMaterial);
        if (listaDeMaterialDoGrupo.isEmpty()) {
            FacesUtil.addOperacaoNaoRealizada("O Grupo selecionado não possui Materiais Cadastrados.");
            return;
        }
        List<Material> listaDeMateriaisAtual = new ArrayList<>();
        if (selecionado.getId() != null) {
            for (LocalEstoqueMaterial localEstoqueDaVez : localEstoqueFacade.recuperaListaLocalEstoqueMaterial(selecionado)) {
                listaDeMateriaisAtual.add(localEstoqueDaVez.getMaterial());
            }
        }
        for (LocalEstoqueMaterial localEstoqueDaVez : materiaisNovos) {
            listaDeMateriaisAtual.add(localEstoqueDaVez.getMaterial());
        }
        for (Material material : listaDeMaterialDoGrupo) {
            if (!listaDeMateriaisAtual.contains(material)) {
                localEstoqueMaterial = new LocalEstoqueMaterial();
                localEstoqueMaterial.setLocalEstoque(selecionado);
                localEstoqueMaterial.setMaterial(material);
                materiaisNovos.add(localEstoqueMaterial);
                this.grupoMaterial = new GrupoMaterial();
            }
        }
    }

    public boolean temGrupoMaterialInformado() {
        if (grupoMaterial == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Grupo de Material deve ser informado!");
            return false;
        }
        return true;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public boolean isExibirPanelGrupoMaterial() {
        return exibirPanelGrupoMaterial;
    }

    public void setExibirPanelGrupoMaterial(boolean exibirPanelGrupoMaterial) {
        this.exibirPanelGrupoMaterial = exibirPanelGrupoMaterial;
    }

    public boolean isExibirPanelMateriais() {
        return exibirPanelMateriais;
    }

    public void setExibirPanelMateriais(boolean exibirPanelMateriais) {
        this.exibirPanelMateriais = exibirPanelMateriais;
    }

    public List<SelectItem> getItensTipoLocalEstoque() {
        return Util.getListSelectItem(Arrays.asList(TipoEstoque.values()));
    }

    public List<LocalEstoque> completaLocalEstoque(String parte) {
        return localEstoqueFacade.completarLocalEstoqueAbertos(parte);
    }

    public List<LocalEstoque> completarLocalEstoqueDoPrimeiroNivel(String parte) {
        return localEstoqueFacade.completarLocalEstoqueAbertos(parte);
    }

    public List<LocalEstoque> completaLocalEstoqueTipo(String parte, UnidadeOrganizacional unidadeOrganizacional, TipoEstoque tipoEstoque) {
        return localEstoqueFacade.completarLocalEstoqueAbertos(parte, tipoEstoque, unidadeOrganizacional);
    }

    public boolean isVisualizar() {
        return Operacoes.VER.equals(operacao);
    }

    public void confirmarGestor() {
        if (validarGestorSelecionado()) {
            try {
                validarVigenciaGestor();
                gestorLocalEstoqueSelecionado.setLocalEstoque(selecionado);
                selecionado.setListaGestorLocalEstoque(Util.adicionarObjetoEmLista(selecionado.getListaGestorLocalEstoque(), gestorLocalEstoqueSelecionado));
                gestorLocalEstoqueSelecionado = new GestorLocalEstoque();
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    public void validarVigenciaGestor() {
        ValidacaoException ve = new ValidacaoException();
        if (gestorLocalEstoqueSelecionado.getFimVigencia() != null
            && gestorLocalEstoqueSelecionado.getInicioVigencia().after(gestorLocalEstoqueSelecionado.getFimVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data Fim de Vigência deve ser igual ou superior a data Início de Vigência.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public boolean validarGestorSelecionado() {
        if (!Util.validaCampos(gestorLocalEstoqueSelecionado)) {
            return false;
        }

        if (!DataUtil.isVigenciaValida(gestorLocalEstoqueSelecionado, filtrarGestores(gestorLocalEstoqueSelecionado))) {
            return false;
        }

        for (GestorLocalEstoque g : selecionado.getListaGestorLocalEstoque()) {
            if (g.getUsuarioSistema().equals(gestorLocalEstoqueSelecionado.getUsuarioSistema())) {
                if (g.getFimVigencia() == null) {
                    FacesUtil.addOperacaoNaoPermitida("Já existe um gestor " + gestorLocalEstoqueSelecionado.getUsuarioSistema().getPessoaFisica().getNome() + " vigente.");
                    return false;
                }
            }
        }

        return true;
    }

    public List<GestorLocalEstoque> filtrarGestores(GestorLocalEstoque gestorLocalEstoque) {
        List<GestorLocalEstoque> retorno = new ArrayList<>();
        for (GestorLocalEstoque g : selecionado.getListaGestorLocalEstoque()) {
            if (g.getUsuarioSistema().equals(gestorLocalEstoque.getUsuarioSistema())) {
                retorno.add(g);
            }
        }
        return retorno;
    }

    public void cancelarGestor() {
        this.gestorLocalEstoqueSelecionado = new GestorLocalEstoque();
    }

    public void removerGestor(GestorLocalEstoque gestor) {
        selecionado.getListaGestorLocalEstoque().remove(gestor);
    }

    public LocalEstoque getPai() {
        return pai;
    }

    public ArvoreLocalEstoque getArvoreLocalEstoque() {
        return arvoreLocalEstoque;
    }

    public void setArvoreLocalEstoque(ArvoreLocalEstoque arvoreLocalEstoque) {
        this.arvoreLocalEstoque = arvoreLocalEstoque;
    }

    public List<LocalEstoqueMaterial> getMateriaisNovos() {
        return materiaisNovos;
    }

    public void setMateriaisNovos(List<LocalEstoqueMaterial> materiaisNovos) {
        this.materiaisNovos = materiaisNovos;
    }

    public List<LocalEstoqueMaterial> getMateriaisParaExcluir() {
        return materiaisParaExcluir;
    }

    public void setMateriaisParaExcluir(List<LocalEstoqueMaterial> materiaisParaExcluir) {
        this.materiaisParaExcluir = materiaisParaExcluir;
    }

    public Boolean getAlterarTipoEstoque() {
        return selecionado.getId() != null && localEstoqueFacade.verificarEstoque(selecionado);
    }

    public class ArvoreLocalEstoque {
        private TreeNode[] selecionados;
        private TreeNode arvore;

        public ArvoreLocalEstoque() {
            arvore = createDocuments(localEstoqueFacade.recuperaRaiz(selecionado));
        }

        public ArvoreLocalEstoque(LocalEstoque localEstoque) {
            arvore = createDocuments(localEstoque);
        }

        public TreeNode createDocuments(LocalEstoque localEstoque) {
            try {
                TreeNode arvore = new DefaultTreeNode();
                TreeNode inicio = new DefaultTreeNode(localEstoque, arvore);
                construirMenu(localEstoqueFacade.recuperarFilhos(localEstoque), inicio);
                return arvore;
            } catch (NullPointerException nu) {
                return new DefaultTreeNode(localEstoque, null);
            }
        }

        public void construirMenu(List<LocalEstoque> filhos, TreeNode pai) {
            for (LocalEstoque local : filhos) {
                TreeNode node = new DefaultTreeNode(local, pai);
                List<LocalEstoque> filhos_aux = localEstoqueFacade.recuperarFilhos(local);
                if (filhos_aux != null && !filhos_aux.isEmpty()) {
                    construirMenu(filhos_aux, node);
                }
            }
        }

        public TreeNode[] getSelecionados() {
            return selecionados;
        }

        public void setSelecionados(TreeNode[] selecionados) {
            this.selecionados = selecionados;
        }

        public TreeNode getArvore() {
            return arvore;
        }

        public void setArvore(TreeNode arvore) {
            this.arvore = arvore;
        }
    }

    public class FiltroLocalEstoqueMaterial {
        private int primeiroRegistro;
        private int quantidadeRegistro;
        private LocalEstoque localEstoque;
        private List<LocalEstoqueMaterial> materialExclusao;

        public FiltroLocalEstoqueMaterial() {
            materialExclusao = Lists.newArrayList();
        }

        public int getPrimeiroRegistro() {
            return primeiroRegistro;
        }

        public void setPrimeiroRegistro(int primeiroRegistro) {
            this.primeiroRegistro = primeiroRegistro;
        }

        public int getQuantidadeRegistro() {
            return quantidadeRegistro;
        }

        public void setQuantidadeRegistro(int quantidadeRegistro) {
            this.quantidadeRegistro = quantidadeRegistro;
        }

        public LocalEstoque getLocalEstoque() {
            return localEstoque;
        }

        public void setLocalEstoque(LocalEstoque localEstoque) {
            this.localEstoque = localEstoque;
        }

        public List<LocalEstoqueMaterial> getMaterialExclusao() {
            return materialExclusao;
        }

        public void setMaterialExclusao(List<LocalEstoqueMaterial> materialExclusao) {
            this.materialExclusao = materialExclusao;
        }
    }

    public void setEnderecoLocalEstoque(EnderecoLocalEstoque enderecoLocalEstoque) {
        this.enderecoLocalEstoque = enderecoLocalEstoque;
    }

    public EnderecoLocalEstoque getEnderecoLocalEstoque() {
        return enderecoLocalEstoque;
    }
}
