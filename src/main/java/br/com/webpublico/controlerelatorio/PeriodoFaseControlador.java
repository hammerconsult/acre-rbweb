package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.PeriodoFaseUnidadeVO;
import br.com.webpublico.enums.SituacaoPeriodoFase;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PeriodoFaseFacade;
import br.com.webpublico.seguranca.menu.LeitorMenuFacade;
import br.com.webpublico.seguranca.service.RecursoSistemaService;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 16/10/13
 * Time: 09:42
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-periodofase", pattern = "/periodofase/novo/", viewId = "/faces/admin/controleusuario/periodofase/edita.xhtml"),
    @URLMapping(id = "editar-periodofase", pattern = "/periodofase/editar/#{periodoFaseControlador.id}/", viewId = "/faces/admin/controleusuario/periodofase/edita.xhtml"),
    @URLMapping(id = "ver-periodofase", pattern = "/periodofase/ver/#{periodoFaseControlador.id}/", viewId = "/faces/admin/controleusuario/periodofase/visualizar.xhtml"),
    @URLMapping(id = "listar-periodofase", pattern = "/periodofase/listar/", viewId = "/faces/admin/controleusuario/periodofase/lista.xhtml")
})
public class PeriodoFaseControlador extends PrettyControlador<PeriodoFase> implements Serializable, CRUD {

    @EJB
    private PeriodoFaseFacade periodoFaseFacade;
    private ConverterAutoComplete conveterFase;
    private PeriodoFaseUnidade periodo;
    private UsuarioSistema usuarioSistema;
    private GrupoUsuario grupoUsuario;
    private List<UsuarioSistema> usuarioSistemas;
    private List<PeriodoFase> periodoFases;
    @ManagedProperty(name = "recursoSistemaService", value = "#{recursoSistemaService}")
    private RecursoSistemaService recursoSistemaService;
    private Notificacao notificacaoRecuperada;
    private List<HierarquiaOrganizacional> listaUnidades;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<PeriodoFaseUnidadeVO> listaPeriodoFaseUnidadeVO;
    private List<PeriodoFaseUnidadeVO> listaPeriodoFaseUnidadeVOSelecionados;
    private List<PeriodoFaseUnidadeVO> listaFiltroPeriodoFaseUnidadeVO;


    public PeriodoFaseControlador() {
        super(PeriodoFase.class);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "novo-periodofase", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        parametrosIniciais();
    }

    public List<Fase> completarFase(String filtro) {
        return periodoFaseFacade.getFaseFacade().listaFiltrando(filtro.trim(), "nome", "codigo");
    }

    public List<HierarquiaOrganizacional> buscarHierarquias() {
        listaUnidades = periodoFaseFacade.getHierarquiaOrganizacionalFacade().filtraPorNivelTrazendoTodas("", "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), getSistemaControlador().getDataOperacao());
        return listaUnidades;
    }

    private void recuperarUsuarios() {
        usuarioSistemas = Lists.newArrayList();
        if (!selecionado.getPeriodoFaseUnidades().isEmpty()) {
            for (PeriodoFaseUnidade periodoFaseUnidade : selecionado.getPeriodoFaseUnidades()) {
                for (PeriodoFaseUsuario usuario : periodoFaseUnidade.getUsuarios()) {
                    Util.adicionarObjetoEmLista(usuarioSistemas, usuario.getUsuarioSistema());
                }
            }
        }
    }

    private void parametrosIniciais() {
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        definirDataPadrao();

        usuarioSistemas = Lists.newArrayList();
        listaUnidades = buscarHierarquias();
        listaPeriodoFaseUnidadeVO = Lists.newArrayList();
        listaPeriodoFaseUnidadeVOSelecionados = Lists.newArrayList();
        listaFiltroPeriodoFaseUnidadeVO = Lists.newArrayList();
    }

    @URLAction(mappingId = "ver-periodofase", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditaVer();
    }

    @URLAction(mappingId = "editar-periodofase", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditaVer();
    }

    private void recuperarEditaVer() {
        try {
            validarVinculoUnidadeNoPeriodoFase();
            recuperarUsuarios();
            periodoFases = Util.adicionarObjetoEmLista(periodoFases, selecionado);
            listaUnidades = buscarHierarquias();
            listaPeriodoFaseUnidadeVO = Lists.newArrayList();
            listaPeriodoFaseUnidadeVOSelecionados = Lists.newArrayList();
            listaFiltroPeriodoFaseUnidadeVO = Lists.newArrayList();
            recuperarUnidades();
            recuperarUnidadesSelecionadas();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível recuperar o período fase. Entre em contato com o suporte.");
        }
    }

    private void recuperarUnidadesSelecionadas() {
        if (selecionado.getPeriodoFaseUnidades() != null && !selecionado.getPeriodoFaseUnidades().isEmpty()) {
            for (PeriodoFaseUnidade periodoFaseUnidade : selecionado.getPeriodoFaseUnidades()) {
                PeriodoFaseUnidadeVO periodoFaseUnidadeVO = new PeriodoFaseUnidadeVO();
                HierarquiaOrganizacional hierarquiaOrganizacional = periodoFaseFacade.getHierarquiaOrganizacionalFacade()
                    .getHierarquiaOrganizacionalPorUnidade(UtilRH.getDataOperacao(),
                        periodoFaseUnidade.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
                if (hierarquiaOrganizacional != null) {
                    periodoFaseUnidadeVO.setPeriodoFaseUnidade(periodoFaseUnidade);
                    periodoFaseUnidadeVO.setHierarquiaOrganizacional(hierarquiaOrganizacional);
                    listaPeriodoFaseUnidadeVOSelecionados.add(periodoFaseUnidadeVO);
                }
            }
            Util.ordenarListas(listaPeriodoFaseUnidadeVOSelecionados);
        }
    }

    public void atualizarPeriodoFaseUnidade() {
        if (selecionado.getInicioVigencia() == null) {
            definirDataPadrao();
            FacesUtil.addOperacaoNaoPermitida("É necessário informar uma data, caso contrário, será usado a data " + DataUtil.getDataFormatada(selecionado.getInicioVigencia(), "dd/MM/yyyy"));
        }

        if (selecionado.getFimVigencia() == null) {
            definirDataPadrao();
            FacesUtil.addOperacaoNaoPermitida("É necessário informar uma data, caso contrário, será usado a data " + DataUtil.getDataFormatada(selecionado.getFimVigencia(), "dd/MM/yyyy"));
        }

        if (selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
            FacesUtil.addOperacaoNaoPermitida("A Data do Último Movimento deve ser igual ou superior a Data do Fechamento.");
            definirDataPadrao();
        }

        for (PeriodoFaseUnidade periodoFaseUnidade : selecionado.getPeriodoFaseUnidades()) {
            periodoFaseUnidade.setInicioVigencia(selecionado.getInicioVigencia());
            periodoFaseUnidade.setFimVigencia(selecionado.getFimVigencia());
        }

        for (PeriodoFaseUnidadeVO periodoFaseUnidade : listaPeriodoFaseUnidadeVO) {
            periodoFaseUnidade.getPeriodoFaseUnidade().setInicioVigencia(selecionado.getInicioVigencia());
            periodoFaseUnidade.getPeriodoFaseUnidade().setFimVigencia(selecionado.getFimVigencia());
        }

    }

    private void definirDataPadrao() {
        Calendar calendar = DataUtil.ultimoDiaMes(getSistemaControlador().getDataOperacao());
        selecionado.setFimVigencia(calendar.getTime());
        selecionado.setInicioVigencia(DataUtil.adicionarMeses(selecionado.getFimVigencia(), -1));
    }

    public void recuperarUnidades() {
        listaPeriodoFaseUnidadeVO.clear();
        listaFiltroPeriodoFaseUnidadeVO.clear();
        List<HierarquiaOrganizacional> hierarquias = buscarHierarquias();
        for (HierarquiaOrganizacional hierarquia : hierarquias) {
            PeriodoFaseUnidade unidade = new PeriodoFaseUnidade();
            PeriodoFaseUnidadeVO vo = new PeriodoFaseUnidadeVO();
            unidade.setPeriodoFase(selecionado);
            unidade.setInicioVigencia(selecionado.getInicioVigencia());
            unidade.setFimVigencia(selecionado.getFimVigencia());
            setarVigenciaPeriodoFaseUnidadeSelecionado(hierarquia, unidade);
            unidade.setSituacaoPeriodoFase(SituacaoPeriodoFase.ABERTO);
            unidade.setUnidadeOrganizacional(hierarquia.getSubordinada());
            vo.setPeriodoFaseUnidade(unidade);
            vo.setHierarquiaOrganizacional(hierarquia);
            listaPeriodoFaseUnidadeVO.add(vo);
            listaFiltroPeriodoFaseUnidadeVO.add(vo);
        }
        Util.ordenarListas(listaPeriodoFaseUnidadeVO, listaFiltroPeriodoFaseUnidadeVO);
        isRenderizaTabelaUnidade();
    }

    private void setarVigenciaPeriodoFaseUnidadeSelecionado(HierarquiaOrganizacional hierarquia, PeriodoFaseUnidade unidade) {
        for (PeriodoFaseUnidade periodoFaseUnidade : selecionado.getPeriodoFaseUnidades()) {
            if (periodoFaseUnidade.getUnidadeOrganizacional().equals(hierarquia.getSubordinada())) {
                unidade.setInicioVigencia(periodoFaseUnidade.getInicioVigencia());
                unidade.setFimVigencia(periodoFaseUnidade.getFimVigencia());
            }
        }
    }


    private void validarVinculoUnidadeNoPeriodoFase() {
        if (selecionado.getPeriodoFaseUnidades().isEmpty()) {
            FacesUtil.addWarn("Nenhuma Unidade Vinculada neste Período Fase " + selecionado.getExercicio() + " - " + selecionado.getFase(), "");
        }
    }

    public ConverterAutoComplete getConverterFase() {
        if (conveterFase == null) {
            conveterFase = new ConverterAutoComplete(Fase.class, periodoFaseFacade.getFaseFacade());
        }
        return conveterFase;
    }

    @Override
    public void excluir() {
        super.excluir();
        recursoSistemaService.getSingletonRecursosSistema().getFases().clear();
        periodoFaseFacade.marcarNotificarComoLida(selecionado, getNotificacaoRecuperada());
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            validarPeriodoFase();
            atualizarUsuarios();
            periodoFaseFacade.salvarPeriodoFase(selecionado);
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "O Período da Fase foi salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    private void atualizarUsuarios() {
        List<PeriodoFaseUsuario> periodoFaseUsuarios = Lists.newArrayList();
        for (UsuarioSistema usuarioSistema : usuarioSistemas) {
            if (isOperacaoNovo()) {
                adicionarUsuarioEmListaVazia(usuarioSistema, periodoFaseUsuarios);
            } else {
                for (PeriodoFaseUnidade periodoFaseUnidade : selecionado.getPeriodoFaseUnidades()) {
                    if (!periodoFaseUnidade.getUsuarios().isEmpty()) {
                        adicionarUsuarioEmListaPopulada(usuarioSistema, periodoFaseUsuarios);
                    } else {
                        adicionarUsuarioEmListaVazia(usuarioSistema, periodoFaseUsuarios);
                    }
                    break;
                }
            }
        }
        for (PeriodoFaseUnidade periodoFaseUnidade : selecionado.getPeriodoFaseUnidades()) {
            periodoFaseUnidade.getUsuarios().addAll(periodoFaseUsuarios);
        }
    }

    public void limparFases() {
        try {
            recursoSistemaService.getSingletonRecursosSistema().limparFases();
            LeitorMenuFacade leitorMenuFacade = (LeitorMenuFacade) Util.getSpringBeanPeloNome("leitorMenuFacade");
            leitorMenuFacade.limparTodosMenus();
            FacesUtil.addOperacaoRealizada("Todos os menus, recursos e fases foram limpos.");
        } catch (Exception e) {
            FacesUtil.addError("Operação Não Realizada ao limpar as fases!", e.getMessage());
        }
    }

    public String listaIdsPeriodoFase() {
        String listaIdsPeriodoFase = "";
        for (PeriodoFase periodoFase : periodoFases) {
            periodoFaseFacade.recuperarPeriodoFasePorFase(periodoFase.getFase());
            listaIdsPeriodoFase += periodoFase.getId() + ",";
        }
        return listaIdsPeriodoFase;
    }

    public void gerarRelacaoUsuariosPorPeriodo(PeriodoFase periodoFase) throws JRException, IOException {
        AbstractReport abstractReport = AbstractReport.getAbstractReport();
        HashMap parameters = new HashMap();
        String nomeArquivo = "RelacaoUsuarioPorPeriodoFase.jasper";
        String sql = " where P.ID = " + periodoFase.getId();
        parameters.put("SQL", sql);
        parameters.put("IMAGEM", abstractReport.getCaminhoImagem());
        parameters.put("USER", getNomeUsuarioLogado());
        parameters.put("MUNICIPIO", "Município de Rio Branco");
        abstractReport.gerarRelatorioAlterandoNomeArquivo(getNomeRelatorio(), nomeArquivo, parameters);
    }

    public void gerarRelacaoTodosUsuariosPorPeriodo() throws JRException, IOException {
        AbstractReport abstractReport = AbstractReport.getAbstractReport();
        HashMap parameters = new HashMap();
        String nomeArquivo = "RelacaoUsuarioPorPeriodoFase.jasper";
        String sql = " where";
        if (!listaIdsPeriodoFase().isEmpty()) {
            sql += " P.ID IN (" + listaIdsPeriodoFase().substring(0, listaIdsPeriodoFase().length() - 1) + ")";
        }
        parameters.put("SQL", sql);
        parameters.put("IMAGEM", abstractReport.getCaminhoImagem());
        parameters.put("USER", getNomeUsuarioLogado());
        parameters.put("MUNICIPIO", "Município de Rio Branco");
        abstractReport.gerarRelatorioAlterandoNomeArquivo(getNomeRelatorio(), nomeArquivo, parameters);
    }

    public String getNomeRelatorio() {
        return "RELACAO-USUARIOS-POR-PERIODO-FASE " + getSistemaControlador().getDataOperacaoFormatada();
    }

    public String getNomeUsuarioLogado() {
        if (getSistemaControlador().getUsuarioCorrente().getPessoaFisica() != null) {
            return getSistemaControlador().getUsuarioCorrente().getPessoaFisica().getNome();
        } else {
            return getSistemaControlador().getUsuarioCorrente().getUsername();
        }
    }

    public void redirecionarParaLista() {
        FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
        redireciona();
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDescricao() == null || selecionado.getDescricao().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo descrição é obrigatório.");
        }
        if (selecionado.getFase() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione pelo menos uma Fase.");
        }
        if (selecionado.getPeriodoFaseUnidades() == null || selecionado.getPeriodoFaseUnidades().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione pelo menos uma Unidade Orçamentária.");
        }
        ve.lancarException();
    }

    public void validarPeriodoFase() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (selecionado.getPeriodoFaseUnidades() != null) {
            for (PeriodoFaseUnidade periodoFaseUnidade : selecionado.getPeriodoFaseUnidades()) {
                Util.validarCampos(periodoFaseUnidade);

                PeriodoFaseUnidade recuperado = periodoFaseFacade.validouVigenciaPeriodoFase(periodoFaseUnidade);
                if (recuperado != null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Já existe um Período Fase cadastrado " + recuperado.getPeriodoFase() + " e " + recuperado + ".");
                }
            }
        }
        ve.lancarException();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/periodofase/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return periodoFaseFacade;
    }

    public void limpar() {
        removerPeriodoFase();
        periodoFases.clear();
    }

    public void selecionarTodasUnidades() {
        if (listaPeriodoFaseUnidadeVO.size() == listaPeriodoFaseUnidadeVOSelecionados.size()) {
            listaPeriodoFaseUnidadeVOSelecionados.removeAll(listaPeriodoFaseUnidadeVO);
            selecionado.getPeriodoFaseUnidades().clear();
        } else {
            listaPeriodoFaseUnidadeVOSelecionados.clear();
            selecionado.getPeriodoFaseUnidades().clear();
            listaPeriodoFaseUnidadeVOSelecionados.addAll(listaPeriodoFaseUnidadeVO);
            for (PeriodoFaseUnidadeVO unidadeVOSelecionado : listaPeriodoFaseUnidadeVOSelecionados) {
                selecionado.getPeriodoFaseUnidades().add(unidadeVOSelecionado.getPeriodoFaseUnidade());
            }
        }
    }

    public void selecionarUnidade(PeriodoFaseUnidadeVO vo) {
        PeriodoFaseUnidade periodoFaseUnidade = new PeriodoFaseUnidade();
        if (listaPeriodoFaseUnidadeVOSelecionados.contains(vo)) {
            listaPeriodoFaseUnidadeVOSelecionados.remove(vo);
            for (PeriodoFaseUnidade unidade : selecionado.getPeriodoFaseUnidades()) {
                if (unidade.getUnidadeOrganizacional().getId().equals(vo.getPeriodoFaseUnidade().getUnidadeOrganizacional().getId())) {
                    periodoFaseUnidade = unidade;
                }
            }
            selecionado.getPeriodoFaseUnidades().remove(periodoFaseUnidade);
        } else {
            listaPeriodoFaseUnidadeVOSelecionados.add(vo);
            selecionado.getPeriodoFaseUnidades().add(vo.getPeriodoFaseUnidade());
        }
    }

    public String iconeTodasUnidadesSelecionada() {
        if (listaPeriodoFaseUnidadeVOSelecionados.size() == listaPeriodoFaseUnidadeVO.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String iconeUnidadeSelecionada(PeriodoFaseUnidadeVO vo) {
        if (listaPeriodoFaseUnidadeVOSelecionados.contains(vo)) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    private void removerPeriodoFase() {
        if (periodoFases != null) {
            for (PeriodoFase periodo : periodoFases) {
                if (periodo.getId() != null) {
                    periodoFaseFacade.remover(periodo);
                }
            }
        }
    }

    public void removerTodosUsuarios() {
        for (UsuarioSistema usuario : usuarioSistemas) {
            for (PeriodoFaseUnidade periodoFaseUnidade : selecionado.getPeriodoFaseUnidades()) {
                for (Iterator<PeriodoFaseUsuario> i = periodoFaseUnidade.getUsuarios().iterator(); i.hasNext(); ) {
                    PeriodoFaseUsuario periodoFaseUsuario = i.next();
                    if (periodoFaseUsuario.getUsuarioSistema().equals(usuario)) {
                        i.remove();
                    }
                }
            }
        }
        usuarioSistemas.clear();
    }

    public boolean renderizaBotaoRemoverTodosUsuarios() {
        return usuarioSistemas != null && usuarioSistemas.size() > 1;
    }

    public void adicionarUsuario() {
        try {
            validarUsuario();
            adicionarUsuario(usuarioSistema);
            usuarioSistema = null;
            FacesUtil.executaJavaScript("setaFoco('Formulario:usuarioSistema_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarUsuario() {
        ValidacaoException ve = new ValidacaoException();
        if (usuarioSistema == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Usuário deve ser informado.");
        }
        ve.lancarException();
        for (PeriodoFaseUnidade periodoFaseUnidade : selecionado.getPeriodoFaseUnidades()) {
            for (PeriodoFaseUsuario periodoFaseUsuario : periodoFaseUnidade.getUsuarios()) {
                if (usuarioSistema.equals(periodoFaseUsuario.getUsuarioSistema())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O usuário " + usuarioSistema + " já foi adicionado.");
                    ve.lancarException();
                }
            }
        }
    }

    public void adicionarUsuario(UsuarioSistema usuarioSistema) {
        if (usuarioSistemas.contains(usuarioSistema)) {
            FacesUtil.addOperacaoNaoRealizada("O usuário " + usuarioSistema + " já foi adicionado.");
        } else {
            Util.adicionarObjetoEmLista(usuarioSistemas, usuarioSistema);
        }
    }

    private void adicionarUsuarioEmListaPopulada(UsuarioSistema usuarioSistema, List<PeriodoFaseUsuario> periodoFaseUsuarios) {
        for (PeriodoFaseUnidade pfu : selecionado.getPeriodoFaseUnidades()) {
            for (Iterator<PeriodoFaseUsuario> i = pfu.getUsuarios().iterator(); i.hasNext(); ) {
                PeriodoFaseUsuario periodoFaseUsuario = i.next();
                if (!periodoFaseUsuario.getUsuarioSistema().equals(usuarioSistema)) {
                    periodoFaseUsuarios.add(new PeriodoFaseUsuario(usuarioSistema, pfu));
                } else {
                    periodoFaseUsuarios.add(periodoFaseUsuario);
                }
                break;
            }
        }
    }

    private void adicionarUsuarioEmListaVazia(UsuarioSistema usuarioSistema, List<PeriodoFaseUsuario> periodoFaseUsuarios) {
        for (PeriodoFaseUnidadeVO voSelecionado : listaPeriodoFaseUnidadeVOSelecionados) {
            periodoFaseUsuarios.add(new PeriodoFaseUsuario(usuarioSistema, voSelecionado.getPeriodoFaseUnidade()));
        }
    }

    public void removerUsuario(UsuarioSistema usuarioSistema) {
        for (PeriodoFaseUnidade periodoFaseUnidade : selecionado.getPeriodoFaseUnidades()) {
            for (Iterator<PeriodoFaseUsuario> i = periodoFaseUnidade.getUsuarios().iterator(); i.hasNext(); ) {
                PeriodoFaseUsuario periodoFaseUsuario = i.next();
                if (periodoFaseUsuario.getUsuarioSistema().equals(usuarioSistema)) {
                    i.remove();
                }
            }
        }

        usuarioSistemas.remove(usuarioSistema);
    }

    public void adicionarUsuarioRecuperandoDasUnidadesSelecionadas() {
        List<HierarquiaOrganizacional> listaHierarquias = new ArrayList<>();
        for (PeriodoFaseUnidadeVO vo : listaPeriodoFaseUnidadeVOSelecionados) {
            listaHierarquias.add(vo.getHierarquiaOrganizacional());
        }
        if (listaHierarquias.isEmpty()) {
            FacesUtil.addOperacaoNaoRealizada("É obrigatório selecionar pelo menos uma Unidade.");
            return;
        }
        List<UsuarioSistema> usuarios = periodoFaseFacade.getUsuarioSistemaFacade().recuperarUsuariosPorHierarquiasOrcamentaris(listaHierarquias, getSistemaControlador().getDataOperacao());
        if (usuarios.isEmpty()) {
            FacesUtil.addOperacaoRealizada("Não foi encontrado usuários para a(s) unidade(s) selecionada(s).");
        }
        for (UsuarioSistema usuario : usuarios) {
            adicionarUsuario(usuario);
        }
    }

    public void adicionarGrupoUsuario() {
        if (grupoUsuario == null) {
            FacesUtil.addCampoObrigatorio("O campo Grupo de Usuário deve ser informado.");
            return;
        }
        grupoUsuario = periodoFaseFacade.getGrupoUsuarioFacade().recuperar(grupoUsuario.getId());
        for (UsuarioSistema usuario : grupoUsuario.getUsuarios()) {
            adicionarUsuario(usuario);
        }
    }

    public Boolean isRenderizaTabelaUnidade() {
        Boolean renderiza = Boolean.TRUE;

        if (selecionado.getInicioVigencia() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Fechamento é obrigatório!");
            renderiza = Boolean.FALSE;
        }

        if (selecionado.getFimVigencia() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Último Movimento é obrigatório!");
            renderiza = Boolean.FALSE;
        }

        if (selecionado.getFase() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Fase é obrigatório!");
            renderiza = Boolean.FALSE;
        }

        return renderiza;
    }


    public List<PeriodoFase> getPeriodoFases() {
        return periodoFases;
    }

    public void setPeriodoFases(List<PeriodoFase> periodoFases) {
        this.periodoFases = periodoFases;
    }

    public RecursoSistemaService getRecursoSistemaService() {
        return recursoSistemaService;
    }

    public void setRecursoSistemaService(RecursoSistemaService recursoSistemaService) {
        this.recursoSistemaService = recursoSistemaService;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<UsuarioSistema> getUsuarioSistemas() {
        return usuarioSistemas;
    }

    public void setUsuarioSistemas(List<UsuarioSistema> usuarioSistemas) {
        this.usuarioSistemas = usuarioSistemas;
    }

    public GrupoUsuario getGrupoUsuario() {
        return grupoUsuario;
    }

    public void setGrupoUsuario(GrupoUsuario grupoUsuario) {
        this.grupoUsuario = grupoUsuario;
    }

    public Notificacao getNotificacaoRecuperada() {
        return notificacaoRecuperada;
    }

    public void setNotificacaoRecuperada(Notificacao notificacaoRecuperada) {
        this.notificacaoRecuperada = notificacaoRecuperada;
    }

    public PeriodoFaseUnidade getPeriodo() {
        return periodo;
    }

    public void setPeriodo(PeriodoFaseUnidade periodo) {
        this.periodo = periodo;
    }

    public void setaUsuarioNoPeriodoFaseUnidade(PeriodoFaseUnidade periodo) {
        this.periodo = periodo;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<PeriodoFaseUnidadeVO> getListaPeriodoFaseUnidadeVO() {
        return listaPeriodoFaseUnidadeVO;
    }

    public void setListaPeriodoFaseUnidadeVO(List<PeriodoFaseUnidadeVO> listaPeriodoFaseUnidadeVO) {
        this.listaPeriodoFaseUnidadeVO = listaPeriodoFaseUnidadeVO;
    }

    public List<PeriodoFaseUnidadeVO> getListaPeriodoFaseUnidadeVOSelecionados() {
        return listaPeriodoFaseUnidadeVOSelecionados;
    }

    public void setListaPeriodoFaseUnidadeVOSelecionados(List<PeriodoFaseUnidadeVO> listaPeriodoFaseUnidadeVOSelecionados) {
        this.listaPeriodoFaseUnidadeVOSelecionados = listaPeriodoFaseUnidadeVOSelecionados;
    }

    public List<PeriodoFaseUnidadeVO> getListaFiltroPeriodoFaseUnidadeVO() {
        return listaFiltroPeriodoFaseUnidadeVO;
    }

    public void setListaFiltroPeriodoFaseUnidadeVO(List<PeriodoFaseUnidadeVO> listaFiltroPeriodoFaseUnidadeVO) {
        this.listaFiltroPeriodoFaseUnidadeVO = listaFiltroPeriodoFaseUnidadeVO;
    }
}
