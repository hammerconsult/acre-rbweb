/*
 * Codigo gerado automaticamente em Tue Nov 08 15:05:04 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.AssistenteAlteracaoOrc;
import br.com.webpublico.entidadesauxiliares.VwHierarquiaDespesaORC;
import br.com.webpublico.entidadesauxiliares.contabil.AlteracaoOrcVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AlteracaoORCFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.OptimisticLockException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static br.com.webpublico.enums.StatusAlteracaoORC.ELABORACAO;

@ManagedBean(name = "alteracaoORCControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-alteracao-orcamentaria", pattern = "/alteracao-orcamentaria/novo/", viewId = "/faces/financeiro/orcamentario/alteracaoorcamentaria/edita.xhtml"),
    @URLMapping(id = "novo-alteracao-orcamentaria-recuperada", pattern = "/alteracao-orcamentaria/novo/recuperada/#{alteracaoORCControlador.id}/", viewId = "/faces/financeiro/orcamentario/alteracaoorcamentaria/edita.xhtml"),
    @URLMapping(id = "editar-alteracao-orcamentaria", pattern = "/alteracao-orcamentaria/editar/#{alteracaoORCControlador.id}/", viewId = "/faces/financeiro/orcamentario/alteracaoorcamentaria/edita.xhtml"),
    @URLMapping(id = "ver-alteracao-orcamentaria", pattern = "/alteracao-orcamentaria/ver/#{alteracaoORCControlador.id}/", viewId = "/faces/financeiro/orcamentario/alteracaoorcamentaria/visualizar.xhtml"),
    @URLMapping(id = "listar-alteracao-orcamentaria", pattern = "/alteracao-orcamentaria/listar/", viewId = "/faces/financeiro/orcamentario/alteracaoorcamentaria/lista.xhtml")
})
public class AlteracaoORCControlador extends PrettyControlador<AlteracaoORC> implements Serializable, CRUD {

    @EJB
    private AlteracaoORCFacade facade;
    private TipoConfigAlteracaoOrc tipoConfigAlteracaoOrc;
    private ConverterAutoComplete converterSubProjetoAtividade;
    private ConverterAutoComplete converterGrupoOrcamentario;
    private ConverterAutoComplete converterFonteDeRecursos;
    private MoneyConverter moneyConverter;
    private ConverterAutoComplete converterFonteDespesaORC;
    private ConverterAutoComplete converterReceitaLOA;
    private DespesaORC despesaORCSuplementacao;
    private DespesaORC despesaORCAnulacao;
    private SuplementacaoORC suplementacaoORCSelecionado;
    private AnulacaoORC anulacaoORCSelecionado;
    private ReceitaAlteracaoORC receitaAlteracaoORCSelecionada;
    private SolicitacaoDespesaOrc solicitacaoDespesaOrc;
    private GrupoOrcamentario grupoOrcamentarioSolicitacao;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private BigDecimal saldofonte;
    private String conteudoFicha;
    private String conteudoMinutaDiario;
    private String conteudoMinuta;
    private Integer indiceAba;
    private List<HierarquiaOrganizacional> orcamentariasUsuario;
    private List<AnulacaoORC> anulacoesRemovidas;
    private HierarquiaOrganizacional orcamentariaSolicitacao;
    private String conteudoMinutaWord;
    private Boolean confirmar;
    private Future<AssistenteAlteracaoOrc> future;
    private AssistenteAlteracaoOrc assistente;

    public AlteracaoORCControlador() {
        super(AlteracaoORC.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "editar-alteracao-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (selecionado.getStatus().isExcluido()) {
            FacesUtil.addOperacaoNaoPermitida("A Alteração Orçamentária encontra-se com a situação excluída, não é possível editar o registro.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
        recuperarVerEditar();
        assistente = new AssistenteAlteracaoOrc();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/alteracao-orcamentaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "novo-alteracao-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        confirmar = false;
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        novaAnulacaoOrc();
        novaSuplementacao();
        suplementacaoORCSelecionado.setAlteracaoORC(selecionado);
        novaReceitaAlteracao();
        selecionado.setDataAlteracao(getSistemaControlador().getDataOperacao());
        selecionado.setStatus(ELABORACAO);
        selecionado.setUnidadeOrganizacionalAdm(getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setUnidadeOrganizacionalOrc(getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente());
        tipoConfigAlteracaoOrc = TipoConfigAlteracaoOrc.SUPLEMENTAR;
        novaSolicitacao();
        recuperarOrcamentariasUsuario();
    }

    public String getDescricaoUnidade(UnidadeOrganizacional unidadeOrganizacional) {
        HierarquiaOrganizacional hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(
            getSistemaControlador().getDataOperacao(),
            unidadeOrganizacional,
            TipoHierarquiaOrganizacional.ORCAMENTARIA);
        if (hierarquiaOrganizacional != null) {
            return hierarquiaOrganizacional.getCodigo() + " " + hierarquiaOrganizacional.getDescricao();
        }
        return unidadeOrganizacional.getDescricao();
    }


    @URLAction(mappingId = "novo-alteracao-orcamentaria-recuperada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaAlteracaoRecuperada() {
        confirmar = false;
        AlteracaoORC alt = facade.recuperar(super.getId());
        selecionado = (AlteracaoORC) Util.clonarObjeto(alt);
        parametrosIniciaisAlteracaoRecuperada();
        selecionado.setId(null);
        selecionado.setAtoLegal(null);
        selecionado.setAtoLegalORC(null);
        selecionado.setDataEfetivacao(null);
        selecionado.setArquivo(null);
        selecionado.setStatus(StatusAlteracaoORC.ELABORACAO);
        selecionado.setCodigo("");
        selecionado.setDataAlteracao(getSistemaControlador().getDataOperacao());
        selecionado.setEfetivado(false);
        adicionarSuplementacaoAlteracaoRecuperada(alt);
        adicionarAnulacaoAlteracaoRecuperada(alt);
        adicionarReceitaAlteracaoRecuperada(alt);
        selecionado.setSolicitacoes(new ArrayList<SolicitacaoDespesaOrc>());
        novaSolicitacao();
        recuperarOrcamentariasUsuario();
    }

    public void recuperarSelecionado(AlteracaoORC alt) {
        selecionado = facade.recuperar(alt.getId());
    }

    public boolean desabilitarBotaoEnviarParaAnaliseNaLista(AlteracaoORC alt) {
        try {
            if (!alt.isAlteracaoEmElaboracao()) {
                return true;
            }
        } catch (Exception e) {
            logger.debug("Erro (des)habilitando botão de Análise na Lista", e);
        }
        return false;
    }

    public boolean desabilitarBotaoRecuperarAlteracao(AlteracaoORC alt) {
        try {
            if (!alt.isAlteracaoEstornada()) {
                return true;
            }
        } catch (Exception e) {
            logger.debug("Erro (des)habilitando botão de Recuperar Alteração", e);
        }
        return false;
    }

    public boolean desabilitarBtnEditarAnulacao(AnulacaoORC anulacaoORC) {
        return !selecionado.isAlteracaoEmElaboracao() && anulacaoORC.getId() != null;
    }

    public List<Conta> completarContaDespesa(String parte) {
        return facade.getProvisaoPPAFacade().getContaFacade().listaFiltrandoDespesaCriteria(parte.trim(), selecionado.getExercicio());
    }

    public List<Conta> completarDestinacao(String parte) {
        return facade.getProvisaoPPAFacade().getContaFacade().listaFiltrandoDestinacaoRecursos(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<SubAcaoPPA> completarSubAcaoPPA(String parte) {
        if (solicitacaoDespesaOrc.getAcaoPPA() != null) {
            return facade.getProvisaoPPAFacade().getSubProjetoAtividadeFacade().buscarSubPorProjetoAtividades(parte.trim(), solicitacaoDespesaOrc.getAcaoPPA());
        }
        return new ArrayList<>();
    }

    public List<AcaoPPA> completarAcaoPPA(String parte) {
        UnidadeOrganizacional unidadeOrganizacional = isUsuarioAcessoMaisQueUmaOrcamentaria() ? solicitacaoDespesaOrc.getUnidadeOrganizacional() : selecionado.getUnidadeOrganizacionalOrc();
        if (unidadeOrganizacional == null) {
            FacesUtil.addAtencao("O campo Unidade Orçamentária deve ser informado.");
            return new ArrayList<>();
        }
        return facade.getProvisaoPPAFacade().getProjetoAtividadeFacade().buscarProjetoAtividadeViculadoAProvisaoPPADespesa(parte.trim(), getSistemaControlador().getExercicioCorrente(), unidadeOrganizacional);
    }

    private List<TipoAtoLegal> getTiposDeAtoLegal() {
        List<TipoAtoLegal> retorno = Lists.newArrayList();
        if (TipoDespesaORC.ESPECIAL.equals(selecionado.getTipoDespesaORC()) || TipoDespesaORC.EXTRAORDINARIA.equals(selecionado.getTipoDespesaORC())) {
            retorno.add(TipoAtoLegal.DECRETO);
            retorno.add(TipoAtoLegal.MEDIDA_PROVISORIA);
            return retorno;
        }
        EsferaDoPoder esferaDoPoder = facade.recuperarEsferaPoderPorUnidade(getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente());
        if (esferaDoPoder != null && esferaDoPoder.isLegislativo()) {
            if (TipoDespesaORC.SUPLEMENTAR.equals(selecionado.getTipoDespesaORC())) {
                retorno.add(TipoAtoLegal.RESOLUCAO);
            } else {
                retorno.add(TipoAtoLegal.DECRETO);
                retorno.add(TipoAtoLegal.RESOLUCAO);
                retorno.add(TipoAtoLegal.MEDIDA_PROVISORIA);
            }
            return retorno;
        } else {
            retorno.add(TipoAtoLegal.DECRETO);
            retorno.add(TipoAtoLegal.MEDIDA_PROVISORIA);
            return retorno;
        }
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        List<TipoAtoLegal> tiposDeAto = getTiposDeAtoLegal();
        if (tiposDeAto != null && !tiposDeAto.isEmpty()) {
            return facade.getAtoLegalFacade().buscarAtosLegaisPorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente(), tiposDeAto);
        }
        return Lists.newArrayList();
    }

    public List<GrupoOrcamentario> completarGrupoOrcamentario(String parte) {
        return facade.getGrupoOrcamentarioFacade().listaGrupoPorExercicio(getSistemaControlador().getExercicioCorrente(), parte.trim());
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalUnidade(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte, "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), getSistemaControlador().getDataOperacao());
    }

    public List<ReceitaLOA> completarReceitaLoa(String parte) {
        List<ReceitaLOA> contasReceita = new ArrayList<>();
        try {
            validarOperacaoReceita();
            contasReceita = facade.getReceitaLOAFacade().buscarContaReceitaPorUnidadeExercicioAndOperacaoReceita(
                parte.trim(),
                getSistemaControlador().getExercicioCorrente(),
                hierarquiaOrganizacional.getSubordinada(),
                receitaAlteracaoORCSelecionada.getOperacaoReceita());
            if (contasReceita.isEmpty()) {
                FacesUtil.addAtencao("Conta de receita não encontrada para a operação: " + receitaAlteracaoORCSelecionada.getOperacaoReceita().getDescricao() + " e unidade: " + hierarquiaOrganizacional + ".");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        return contasReceita;
    }

    private void validarOperacaoReceita() {
        ValidacaoException ve = new ValidacaoException();
        if (receitaAlteracaoORCSelecionada.getOperacaoReceita() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Operação deve ser informado para filtrar a conta de receita.");
        }
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional deve ser informado para filtrar a conta de receita.");
        }
        ve.lancarException();
    }

    public List<FonteDespesaORC> completarDespesasORCSuplementacao(String parte) {
        List<FonteDespesaORC> dados = new ArrayList<FonteDespesaORC>();
        if (despesaORCSuplementacao != null) {
            dados = facade.getFonteDespesaORCFacade().completaFonteDespesaORC(parte, despesaORCSuplementacao);
        } else {
            FacesUtil.addOperacaoNaoPermitida("Selecione uma Despesa Orçamentária antes de selecionar a Fonte de Despesa Orçamentária.");
        }
        return dados;
    }

    public List<FonteDespesaORC> completarDespesasORCAnulacao(String parte) {
        List<FonteDespesaORC> fonteDespesaORCs = new ArrayList<>();
        if (despesaORCAnulacao != null) {
            fonteDespesaORCs = facade.getFonteDespesaORCFacade().completaFonteDespesaORC(parte, despesaORCAnulacao);
        }
        return fonteDespesaORCs;
    }

    public void definirAtoLegalOrc() {
        try {
            if (selecionado.getAtoLegal() != null) {
                AtoLegalORC atoLegalORC = facade.getAtoLegalFacade().buscarAtolegaOrcPorAtoLegal(selecionado.getAtoLegal());
                if (atoLegalORC == null) {
                    FacesUtil.addOperacaoNaoRealizada("Ato legal orçamentário não encontrado para o ato legal: " + selecionado.getAtoLegal().toString());
                    return;
                }
                selecionado.setAtoLegalORC(atoLegalORC);
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível recuperar ato legal orçamentário. Detalhes do erro: " + ex.getMessage());
        }
    }

    public void definirFonteOrcOrig(SelectEvent evt) {
        FonteDespesaORC fonte = (FonteDespesaORC) evt.getObject();
    }

    public List<SelectItem> getExtensaoFonteRecurso() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (ExtensaoFonteRecurso obj : facade.getExtensaoFonteRecursoFacade().listaDecrescente()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getOrigens() {
        List<SelectItem> dados = new ArrayList<>();
        dados.add(new SelectItem(null, ""));
        for (OrigemReceitaAlteracaoORC orig : OrigemReceitaAlteracaoORC.values()) {
            dados.add(new SelectItem(orig, orig.getDescricao()));
        }
        return Util.ordenaSelectItem(dados);
    }

    public List<SelectItem> getTipoConfiguracaoAlteracao() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoConfigAlteracaoOrc config : TipoConfigAlteracaoOrc.values()) {
            if (!config.equals(TipoConfigAlteracaoOrc.RECEITA)) {
                toReturn.add(new SelectItem(config, config.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getOrigensSupl() {
        List<SelectItem> dados = new ArrayList<>();
        dados.add(new SelectItem(null, ""));
        if (!isGestorOrcamento()) {
            dados.add(new SelectItem(OrigemSuplementacaoORC.ANULACAO, OrigemSuplementacaoORC.ANULACAO.getDescricao()));
        } else {
            for (OrigemSuplementacaoORC orig : OrigemSuplementacaoORC.values()) {
                dados.add(new SelectItem(orig, orig.getDescricao()));
            }
        }
        return Util.ordenaSelectItem(dados);
    }

    public List<SelectItem> getMes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes mes : Mes.values()) {
            toReturn.add(new SelectItem(mes, mes.getDescricao()));
            if (mes.getNumeroMes().equals((Integer) DataUtil.getMes(selecionado.getDataAlteracao()))) {
                anulacaoORCSelecionado.setMesAnulacao(mes);
                suplementacaoORCSelecionado.setMesSupmentencao(mes);
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTipoCredito() {
        List<SelectItem> dados = new ArrayList<>();
        dados.add(new SelectItem(null, ""));
        if (!isGestorOrcamento()
            && selecionado.isAlteracaoEmElaboracao()
            && selecionado.getAnulacoesORCs().isEmpty()
            && selecionado.getSuplementacoesORCs().isEmpty()) {
            dados.add(new SelectItem(TipoDespesaORC.SUPLEMENTAR, TipoDespesaORC.SUPLEMENTAR.getDescricao()));
        } else {
            for (TipoDespesaORC orig : TipoDespesaORC.values()) {
                if (orig != TipoDespesaORC.ORCAMENTARIA) {
                    dados.add(new SelectItem(orig, orig.getDescricao()));
                }
            }
        }
        return Util.ordenaSelectItem(dados);
    }

    public void removerSuplementacao(ActionEvent evento) {
        SuplementacaoORC obj = (SuplementacaoORC) evento.getComponent().getAttributes().get("sup");
        selecionado.getSuplementacoesORCs().remove(obj);
    }

    public void alterarSuplementacao(ActionEvent evento) {
        SuplementacaoORC obj = (SuplementacaoORC) evento.getComponent().getAttributes().get("sup");
        suplementacaoORCSelecionado = (SuplementacaoORC) Util.clonarObjeto(obj);
        suplementacaoORCSelecionado.setMesSupmentencao(Mes.getMesToInt(suplementacaoORCSelecionado.getMes()));
        despesaORCSuplementacao = obj.getFonteDespesaORC().getDespesaORC();
    }

    public void removerAnulacao(ActionEvent evento) {
        AnulacaoORC obj = (AnulacaoORC) evento.getComponent().getAttributes().get("anu");
        if (selecionado.getStatus().equals(StatusAlteracaoORC.EM_ANALISE)
            && obj.getId() != null) {
            anulacoesRemovidas.add(obj);
        }
        selecionado.getAnulacoesORCs().remove(obj);
    }

    public void alterarAnulacao(AnulacaoORC obj) {
        anulacaoORCSelecionado = (AnulacaoORC) Util.clonarObjeto(obj);
        despesaORCAnulacao = obj.getFonteDespesaORC().getDespesaORC();
        anulacaoORCSelecionado.setMesAnulacao(Mes.getMesToInt(anulacaoORCSelecionado.getMes()));
    }

    public void removerReceita(ReceitaAlteracaoORC obj) {
        selecionado.getReceitasAlteracoesORCs().remove(obj);
    }

    public void alterarReceita(ReceitaAlteracaoORC obj) {
        receitaAlteracaoORCSelecionada = (ReceitaAlteracaoORC) Util.clonarObjeto(obj);
        hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(
            getSistemaControlador().getDataOperacao(),
            obj.getReceitaLOA().getEntidade(),
            TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public void limparCampoReceitaLoa() {
        receitaAlteracaoORCSelecionada.setReceitaLOA(null);
    }

    public void adicionarReceita() {
        try {
            validarCamposReceita();
            receitaAlteracaoORCSelecionada.setFonteDeRecurso(receitaAlteracaoORCSelecionada.getContaDeDestinacao().getFonteDeRecursos());
            receitaAlteracaoORCSelecionada.setAlteracaoORC(selecionado);
            receitaAlteracaoORCSelecionada.setSaldo(receitaAlteracaoORCSelecionada.getValor());
            selecionado.setReceitasAlteracoesORCs(Util.adicionarObjetoEmLista(selecionado.getReceitasAlteracoesORCs(), receitaAlteracaoORCSelecionada));
            novaReceitaAlteracao();
            hierarquiaOrganizacional = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.debug("Erro Adicionando Receita", ex);
        }
    }

    private void validarCamposReceita() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Unidade Organizacional deve ser informado.");
        }
        receitaAlteracaoORCSelecionada.realizarValidacoes();
        for (ReceitaAlteracaoORC receitaAlteracaoORC : selecionado.getReceitasAlteracoesORCs()) {
            if (!receitaAlteracaoORC.equals(this.receitaAlteracaoORCSelecionada)
                && receitaAlteracaoORC.getReceitaLOA().getContaDeReceita().getId().equals(receitaAlteracaoORCSelecionada.getReceitaLOA().getContaDeReceita().getId())
                && receitaAlteracaoORC.getFonteDeRecurso().getId().equals(receitaAlteracaoORCSelecionada.getFonteDeRecurso().getId())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A conta de receita: " + receitaAlteracaoORCSelecionada.getReceitaLOA().getContaDeReceita().getCodigo() + " e Fonte: " + receitaAlteracaoORCSelecionada.getFonteDeRecurso().getCodigo() + " , já foram adicionados na lista de receitas.");
            }
        }
        ve.lancarException();
    }

    public void definirTipoCreditoNaSuplementacaoAndAnulacao() {
        for (SuplementacaoORC suplementacaoORC : selecionado.getSuplementacoesORCs()) {
            suplementacaoORC.setTipoDespesaORC(selecionado.getTipoDespesaORC());
            suplementacaoORC.setOrigemSuplemtacao(suplementacaoORCSelecionado.getOrigemSuplemtacao());
        }
        for (AnulacaoORC anulacaoORC : selecionado.getAnulacoesORCs()) {
            anulacaoORC.setTipoDespesaORC(selecionado.getTipoDespesaORC());
        }
    }

    public void adicionarSuplementacao() {
        try {
            if (selecionado.getTipoDespesaORC() != null) {
                suplementacaoORCSelecionado.setTipoDespesaORC(selecionado.getTipoDespesaORC());
            }
            validarCamposSuplementacao();
            suplementacaoORCSelecionado.setAlteracaoORC(selecionado);
            suplementacaoORCSelecionado.setMes(suplementacaoORCSelecionado.getMesSupmentencao().getNumeroMes());
            suplementacaoORCSelecionado.setSaldo(suplementacaoORCSelecionado.getValor());
            selecionado.setSuplementacoesORCs(Util.adicionarObjetoEmLista(selecionado.getSuplementacoesORCs(), suplementacaoORCSelecionado));
            novaSuplementacao();
            saldofonte = BigDecimal.ZERO;
            novaDespesaOrcSuplementacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void novaDespesaOrcSuplementacao() {
        despesaORCSuplementacao = new DespesaORC();
    }

    private void novaSuplementacao() {
        suplementacaoORCSelecionado = new SuplementacaoORC();
    }

    public void adicionarAnulacao() {
        try {
            if (selecionado.getTipoDespesaORC() != null) {
                anulacaoORCSelecionado.setTipoDespesaORC(selecionado.getTipoDespesaORC());
            }
            validarCamposAnulacao();
            anulacaoORCSelecionado.setAlteracaoORC(selecionado);
            anulacaoORCSelecionado.setMes(anulacaoORCSelecionado.getMesAnulacao().getNumeroMes());
            anulacaoORCSelecionado.setSaldo(anulacaoORCSelecionado.getValor());
            anulacaoORCSelecionado.setBloqueado(true);
            selecionado.setAnulacoesORCs(Util.adicionarObjetoEmLista(selecionado.getAnulacoesORCs(), anulacaoORCSelecionado));
            novaAnulacaoOrc();
            saldofonte = BigDecimal.ZERO;
            novaDespesaOrcAnulacao();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getAllMensagens());
        }
    }

    private void novaAnulacaoOrc() {
        anulacaoORCSelecionado = new AnulacaoORC();
    }

    private void novaDespesaOrcAnulacao() {
        despesaORCAnulacao = new DespesaORC();
    }

    private void novaSolicitacao() {
        solicitacaoDespesaOrc = new SolicitacaoDespesaOrc();
        orcamentariaSolicitacao = null;
    }

    public void adicionarSolicitacao() {
        try {
            validarCamposSolicitacaoElementoDespesa();
            solicitacaoDespesaOrc.setAlteracaoORC(selecionado);
            selecionado.setSolicitacoes(Util.adicionarObjetoEmLista(selecionado.getSolicitacoes(), solicitacaoDespesaOrc));
            novaSolicitacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCamposSolicitacaoElementoDespesa() {
        ValidacaoException ve = new ValidacaoException();
        solicitacaoDespesaOrc.realizarValidacoes();
        if (existeSolicitacaoRepetida(solicitacaoDespesaOrc)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Já existe uma solicitação idêntica adicionada na lista.");
        }
        if (existeSolicitacaoRepetidaNoOrcamento(solicitacaoDespesaOrc)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A Conta " + solicitacaoDespesaOrc.getConta().toString() + " com a Fonte de Recurso " + ((ContaDeDestinacao) solicitacaoDespesaOrc.getDestinacaoDeRecursos()).getFonteDeRecursos() + " já foi utilizada no orçamento.");
        }
        ve.lancarException();
    }

    public void limparCamposUnidade() {
        solicitacaoDespesaOrc.setAcaoPPA(null);
        solicitacaoDespesaOrc.setSubAcaoPPA(null);
        solicitacaoDespesaOrc.setConta(null);
        solicitacaoDespesaOrc.setExtensaoFonteRecurso(null);
    }

    private boolean existeSolicitacaoRepetidaNoOrcamento(SolicitacaoDespesaOrc solicitacaoDespesaOrc) {
        List<ProvisaoPPADespesa> provisaoPPADespesas = facade.getProvisaoPPAFacade().getProvisaoPPADespesaFacade().listaProvisaoDispesaPPARecuperandoFontes(solicitacaoDespesaOrc.getSubAcaoPPA());
        for (ProvisaoPPADespesa ppaDespesa : provisaoPPADespesas) {
            if (solicitacaoDespesaOrc.getConta().getId().equals(ppaDespesa.getContaDeDespesa().getId())) {
                for (ProvisaoPPAFonte provisaoPPAFonte : ppaDespesa.getProvisaoPPAFontes()) {
                    if (provisaoPPAFonte.getDestinacaoDeRecursos().getId().equals(solicitacaoDespesaOrc.getDestinacaoDeRecursos().getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ProvisaoPPADespesa recuperarDespesaOrcamento(SolicitacaoDespesaOrc solicitacaoDespesaOrc) {
        List<ProvisaoPPADespesa> provisaoPPADespesas = facade.getProvisaoPPAFacade().getProvisaoPPADespesaFacade().listaProvisaoDispesaPPARecuperandoFontes(solicitacaoDespesaOrc.getSubAcaoPPA());
        for (ProvisaoPPADespesa ppaDespesa : provisaoPPADespesas) {
            if (solicitacaoDespesaOrc.getConta().getId().equals(ppaDespesa.getContaDeDespesa().getId())) {
                return ppaDespesa;
            }
        }
        return null;
    }

    private boolean existeSolicitacaoRepetida(SolicitacaoDespesaOrc solicitacaoDespesaOrc) {
        for (SolicitacaoDespesaOrc sol : selecionado.getSolicitacoes()) {
            if (!sol.equals(solicitacaoDespesaOrc)) {
                if (solicitacaoDespesaOrc.getAcaoPPA().getId().equals(sol.getAcaoPPA().getId())
                    && solicitacaoDespesaOrc.getSubAcaoPPA().getId().equals(sol.getSubAcaoPPA().getId())
                    && solicitacaoDespesaOrc.getConta().getId().equals(sol.getConta().getId())
                    && solicitacaoDespesaOrc.getDestinacaoDeRecursos().getId().equals(sol.getDestinacaoDeRecursos().getId())
                    && solicitacaoDespesaOrc.getUnidadeOrganizacional().getId().equals(sol.getUnidadeOrganizacional().getId())
                    && solicitacaoDespesaOrc.getEsferaOrcamentaria().equals(sol.getEsferaOrcamentaria())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void validarAprovacaoElementoDespesa() {
        ValidacaoException ve = new ValidacaoException();
        if (grupoOrcamentarioSolicitacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Grupo Orçamentário deve ser informado.");
        }
        ve.lancarException();
    }

    public void aprovarSolicitacao() {
        try {
            validarAprovacaoElementoDespesa();
            facade.aprovarSolicitacao(selecionado, solicitacaoDespesaOrc, grupoOrcamentarioSolicitacao);
            FacesUtil.addOperacaoRealizada(" Solicitação aprovada com sucesso.");
            redirecionarParaEdita();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), e.getMessage());
        }
    }

    public void alterarSolicitacao(SolicitacaoDespesaOrc solicitacao) {
        solicitacaoDespesaOrc = (SolicitacaoDespesaOrc) Util.clonarEmNiveis(solicitacao, 1);
        orcamentariaSolicitacao = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", this.solicitacaoDespesaOrc.getUnidadeOrganizacional(), selecionado.getDataAlteracao());
    }

    public void removerSolicitacao(SolicitacaoDespesaOrc solicitacaoDespesaOrc) {
        selecionado.getSolicitacoes().remove(solicitacaoDespesaOrc);
    }

    public void acompanharFuture() throws ExecutionException, InterruptedException {
        if (!assistente.getBarraProgressoItens().getCalculando()) {
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
            if (future != null && future.isDone()) {
                if (assistente.getMensagens().isEmpty()) {
                    AlteracaoORC alteracaoORC = future.get().getAlteracaoORC();
                    if (alteracaoORC != null) {
                        selecionado = alteracaoORC;
                        FacesUtil.executaJavaScript("finalizarDeferir()");
                    }
                } else {
                    String mensagemCompleta = " ";
                    for (String mensagem : assistente.getMensagens()) {
                        mensagemCompleta += mensagem;
                    }
                    FacesUtil.executaJavaScript("aguarde.hide()");
                    FacesUtil.addOperacaoNaoRealizada(mensagemCompleta);
                    future = null;

                }
            }
        }
    }

    public void finalizarFuture() {
        FacesUtil.addOperacaoRealizada(" A alteração orçamentária foi deferida com sucesso.");
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + assistente.getAlteracaoORC().getId() + "/");
        future = null;
    }

    public void deferirAlteracao() {
        try {
            if (facade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(facade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            }
            validarDeferirAlteracao();
            assistente.setAlteracaoORC(selecionado);
            assistente.setAnulacoesRemovidas(anulacoesRemovidas);
            assistente.setDescricaoProcesso("Deferindo alteração orçamentária " + selecionado.toString());
            assistente.setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());
            assistente.setDataAtual(getSistemaControlador().getDataOperacao());
            future = facade.deferirAlteracaoOrc(assistente);
            FacesUtil.executaJavaScript("acompanharDeferir()");
            FacesUtil.executaJavaScript("dialogProgressBar.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            future = null;
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            future = null;
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            future = null;
            descobrirETratarException(e);
        }
    }

    private void validarDeferirAlteracao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Ato Legal deve ser informado.");

        } else if (selecionado.getTipoDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Tipo de Crédito deve ser informado.");

        } else if (selecionado.getDataEfetivacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Data da Efetivação deve ser informado.");

        } else {
            if (selecionado.getAtoLegal().getDataPromulgacao() != null && selecionado.getDataEfetivacao().compareTo(selecionado.getAtoLegal().getDataPromulgacao()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" A Data de Efetivação deve ser maior ou igual a Data de Sansão do ato legal.");
            }
            if (DataUtil.dataSemHorario(selecionado.getDataEfetivacao()).compareTo(DataUtil.dataSemHorario(selecionado.getDataAlteracao())) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" A Data de Efetivação deve ser maior ou igual a Data da Alteração Orçamentária.");
            }
        }
        ve.lancarException();
        validarPorFonte(false);
        validarGrupoOrcamentarioSuplementacaoAndAnulacao(ve);
        validarItensSuplementacaoAnulacaoSolicitacaoAndReceita(ve);
        validarValoresDecreto(ve);
    }

    private void validarValoresDecreto(ValidacaoException ve) {
        AtoLegalORC ato = selecionado.getAtoLegalORC();
        if (ato.getAtoLegal().getDataPromulgacao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Sansão do AtoLegal deve ser informada.");
        } else if (ato.getAtoLegal().getDataPublicacao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Publicação do Ato Legal deve ser informada.");
        }
        ve.lancarException();
    }

    private void validarGrupoOrcamentarioSuplementacaoAndAnulacao(ValidacaoException ve) {
        for (SuplementacaoORC suplementacaoORC : selecionado.getSuplementacoesORCs()) {
            if (suplementacaoORC.getFonteDespesaORC().getGrupoOrcamentario() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Grupo Orçamentário deve ser informado para a(s) suplementação(ões) pendente de grupo.");
                indiceAba = 0;
            }
            if (suplementacaoORC.getOrigemSuplemtacao() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Origem do Recurso deve ser informado.");
            }
        }
        for (AnulacaoORC anulacaoORC : selecionado.getAnulacoesORCs()) {
            if (anulacaoORC.getFonteDespesaORC().getGrupoOrcamentario() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Grupo Orçamentário deve ser informado para a(s) anulação(ões) pendente de grupo.");
                indiceAba = 1;
            }
        }
    }

    private void validarSaldoOrcamentarioAnulacaoOrc(ValidacaoException ve) {
        if (anulacaoORCSelecionado.getFonteDespesaORC().getId() != null) {
            BigDecimal saldo = saldofonte;

            for (AnulacaoORC anulacao : selecionado.getAnulacoesORCs()) {
                if (!anulacao.equals(this.anulacaoORCSelecionado) && anulacao.getFonteDespesaORC().equals(anulacaoORCSelecionado.getFonteDespesaORC())) {
                    saldo = saldo.subtract(anulacao.getValor());
                }
            }
            if (saldo.compareTo(anulacaoORCSelecionado.getValor()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O saldo disponível de " + "<b>" + Util.formataValor(saldo) + "</b>" + " na Fonte de Despesa " + anulacaoORCSelecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos() + " é menor que o valor da anulação de " + "<b>" + Util.formataValor(anulacaoORCSelecionado.getValor()) + "</b>");
            }
        }
        ve.lancarException();
    }

    public BigDecimal getSaldoFonteDespesaORCAnulacao() {
        if (anulacaoORCSelecionado.getFonteDespesaORC() != null && despesaORCAnulacao != null) {
            saldofonte = facade.getFonteDespesaORCFacade().saldoRealPorFonte(
                anulacaoORCSelecionado.getFonteDespesaORC(),
                getSistemaControlador().getDataOperacao(),
                despesaORCAnulacao.getProvisaoPPADespesa().getUnidadeOrganizacional());
            return saldofonte;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getSaldoOrcamentarioSuplementacao() {
        if (suplementacaoORCSelecionado.getFonteDespesaORC() != null && despesaORCSuplementacao != null) {
            saldofonte = facade.getFonteDespesaORCFacade().saldoRealPorFonte(
                suplementacaoORCSelecionado.getFonteDespesaORC(),
                getSistemaControlador().getDataOperacao(),
                despesaORCSuplementacao.getProvisaoPPADespesa().getUnidadeOrganizacional());
            return saldofonte;
        }
        return BigDecimal.ZERO;
    }

    public List<SelectItem> getTiposAlteracaoORC() {
        List<SelectItem> dados = new ArrayList<>();
        dados.add(new SelectItem(null, ""));
        for (TipoAlteracaoORC tipo : TipoAlteracaoORC.values()) {
            dados.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return Util.ordenaSelectItem(dados);
    }

    public List<SelectItem> getContasDeDestinacao() {
        List<SelectItem> dados = Lists.newArrayList();
        dados.add(new SelectItem(null, ""));
        try {
            if (receitaAlteracaoORCSelecionada.getReceitaLOA() != null
                && receitaAlteracaoORCSelecionada.getReceitaLOA().getId() != null) {
                for (ContaDeDestinacao contaDeDestinacao : facade.getContaFacade().buscarContasDeDestinacaoPorReceitaLOA(receitaAlteracaoORCSelecionada.getReceitaLOA())) {
                    dados.add(new SelectItem(contaDeDestinacao, contaDeDestinacao.toString()));
                }
            }
        } catch (Exception e) {
            logger.debug("Erro recuperando Contas de Destinação: {}", e);
        }
        return Util.ordenaSelectItem(dados);
    }

    public void recuperarInformacoesReceita(ReceitaAlteracaoORC receita) {
        receitaAlteracaoORCSelecionada = receita;
    }

    public void limparInformacoesReceita() {
        novaReceitaAlteracao();
    }

    private void validarCamposSuplementacao() {
        ValidacaoException ve = new ValidacaoException();
        if (despesaORCSuplementacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Elemento de Despesa deve ser informado.");
        }
        if (suplementacaoORCSelecionado.getFonteDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Fonte de Despesa Orçamentária deve ser informado.");
        }
        if (suplementacaoORCSelecionado.getValor() == null || suplementacaoORCSelecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Valor deve ser maior que zero(0).");
        }
        ve.lancarException();
        validarElementoDespesaSuplementacao(ve);
        validarMesmoElementoDespesaNaAnulacao(ve);
    }

    private void validarMesmoElementoDespesaNaAnulacao(ValidacaoException ve) {
        VwHierarquiaDespesaORC despesaAnulacao = facade.getDespesaORCFacade().recuperaVwDespesaOrc(despesaORCSuplementacao, selecionado.getDataAlteracao());

        for (AnulacaoORC anulacaoORC : selecionado.getAnulacoesORCs()) {
            Conta contaDespesaAnulacao = anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa();
            VwHierarquiaDespesaORC vwDespesaAnulacao = facade.getDespesaORCFacade().recuperaVwDespesaOrc(anulacaoORC.getFonteDespesaORC().getDespesaORC(), selecionado.getDataAlteracao());

            if (vwDespesaAnulacao.getSubAcao().equals(despesaAnulacao.getSubAcao())
                && contaDespesaAnulacao.equals(despesaORCSuplementacao.getContaDeDespesa())
                && anulacaoORC.getFonteDespesaORC().equals(suplementacaoORCSelecionado.getFonteDespesaORC())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Não é permitido suplementar e anular para um mesmo elemento de despesa.");
            }
        }
        ve.lancarException();
    }

    private void validarCamposAnulacao() {
        ValidacaoException ve = new ValidacaoException();
        if (despesaORCAnulacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Elemento de Despesa deve ser informado.");
        }
        if (anulacaoORCSelecionado.getFonteDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Fonte de Despesa Orçamento deve ser informado.");
        }
        if (anulacaoORCSelecionado.getValor() == null || anulacaoORCSelecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser mairo que zero(0).");
        }
        ve.lancarException();
        validarMesmoElementoDespesaAnulacao(ve);
        validarMesmoElementoDespesaSuplementacaoAnulacao(ve);
        validarSaldoOrcamentarioAnulacaoOrc(ve);
    }

    private void validarMesmoElementoDespesaSuplementacaoAnulacao(ValidacaoException ve) {
        VwHierarquiaDespesaORC despesaAnulacao = facade.getDespesaORCFacade().recuperaVwDespesaOrc(despesaORCAnulacao, selecionado.getDataAlteracao());

        for (SuplementacaoORC suplementacaoORC : selecionado.getSuplementacoesORCs()) {
            Conta contaDespesaSuplementacao = suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa();
            VwHierarquiaDespesaORC despesaSuplementacao = facade.getDespesaORCFacade().recuperaVwDespesaOrc(suplementacaoORC.getFonteDespesaORC().getDespesaORC(), selecionado.getDataAlteracao());

            if (despesaSuplementacao.getSubAcao().equals(despesaAnulacao.getSubAcao())
                && contaDespesaSuplementacao.equals(despesaORCAnulacao.getContaDeDespesa())
                && suplementacaoORC.getFonteDespesaORC().equals(anulacaoORCSelecionado.getFonteDespesaORC())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Não é permitido suplementar e anular para um mesmo elemento de despesa.");
            }
        }
        ve.lancarException();
    }

    private void validarElementoDespesaSuplementacao(ValidacaoException ve) {
        for (SuplementacaoORC suplementacaoORC : selecionado.getSuplementacoesORCs()) {
            Conta contaDespesa = suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa();

            VwHierarquiaDespesaORC vwHierarquiaDespesaORCSuplem = facade.getDespesaORCFacade().recuperaVwDespesaOrc(suplementacaoORC.getFonteDespesaORC().getDespesaORC(), selecionado.getDataAlteracao());

            VwHierarquiaDespesaORC vwHierarquiaDespesaORCDespesaORCSuplem = facade.getDespesaORCFacade().recuperaVwDespesaOrc(despesaORCSuplementacao, selecionado.getDataAlteracao());

            if (!suplementacaoORC.equals(this.suplementacaoORCSelecionado)) {
                if (vwHierarquiaDespesaORCDespesaORCSuplem.getSubAcao().equals(vwHierarquiaDespesaORCSuplem.getSubAcao())
                    && contaDespesa.equals(despesaORCSuplementacao.getContaDeDespesa())
                    && suplementacaoORC.getFonteDespesaORC().equals(suplementacaoORCSelecionado.getFonteDespesaORC())
                    && suplementacaoORC.getOrigemSuplemtacao().equals(suplementacaoORCSelecionado.getOrigemSuplemtacao())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Elemento de Despesa " + despesaORCSuplementacao.getContaDeDespesa().getCodigo() + " (Fonte: " + suplementacaoORCSelecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo() + ") " + " pertencente a funcional: " + vwHierarquiaDespesaORCDespesaORCSuplem.getSubAcao().substring(0, 21) + ", já foi adicionado na lista de suplementações.");
                }
            }
        }
        ve.lancarException();
    }

    private void validarMesmoElementoDespesaAnulacao(ValidacaoException ve) {
        for (AnulacaoORC anulacaoORC : selecionado.getAnulacoesORCs()) {
            Conta contaDespesa = anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa();

            VwHierarquiaDespesaORC vwHierarquiaDespesaORCAnulacao = facade.getDespesaORCFacade().recuperaVwDespesaOrc(anulacaoORC.getFonteDespesaORC().getDespesaORC(), selecionado.getDataAlteracao());

            VwHierarquiaDespesaORC vwHierarquiaDespesaORCDespesaORCAnulacao = facade.getDespesaORCFacade().recuperaVwDespesaOrc(despesaORCAnulacao, selecionado.getDataAlteracao());

            if (!anulacaoORC.equals(this.anulacaoORCSelecionado)) {
                if (vwHierarquiaDespesaORCDespesaORCAnulacao.getSubAcao().equals(vwHierarquiaDespesaORCAnulacao.getSubAcao())
                    && contaDespesa.equals(despesaORCAnulacao.getContaDeDespesa())
                    && anulacaoORC.getFonteDespesaORC().equals(anulacaoORCSelecionado.getFonteDespesaORC())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Elemento de Despesa " + despesaORCAnulacao.getContaDeDespesa().getCodigo() + " (Fonte: " + anulacaoORCSelecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo() + ") " + " pertencente a funcional: " + vwHierarquiaDespesaORCDespesaORCAnulacao.getSubAcao().substring(0, 21) + ", já foi adicionado na lista de anulações.");
                }
            }
        }
        ve.lancarException();
    }


    private void adicionarReceitaAlteracaoRecuperada(AlteracaoORC alt) {
        for (ReceitaAlteracaoORC receitaAlteracaoORC : alt.getReceitasAlteracoesORCs()) {
            receitaAlteracaoORCSelecionada = receitaAlteracaoORC;
            receitaAlteracaoORCSelecionada.setId(null);
            receitaAlteracaoORCSelecionada.setEventoContabil(null);
            receitaAlteracaoORCSelecionada.setHistoricoNota(null);
            receitaAlteracaoORCSelecionada.setHistoricoRazao(null);
            receitaAlteracaoORCSelecionada.setDataRegistro(getSistemaControlador().getDataOperacao());
            receitaAlteracaoORCSelecionada.setSaldo(receitaAlteracaoORC.getValor());
            receitaAlteracaoORCSelecionada.setOperacaoReceita(receitaAlteracaoORC.getOperacaoReceita());
            receitaAlteracaoORCSelecionada.setAlteracaoORC(selecionado);
            selecionado.getReceitasAlteracoesORCs().add(receitaAlteracaoORCSelecionada);
            novaReceitaAlteracao();
            receitaAlteracaoORCSelecionada.setReceitaLOA(new ReceitaLOA());
            receitaAlteracaoORCSelecionada.setValor(BigDecimal.ZERO);
            receitaAlteracaoORCSelecionada.setSaldo(BigDecimal.ZERO);
            hierarquiaOrganizacional = null;
        }
    }

    private void adicionarAnulacaoAlteracaoRecuperada(AlteracaoORC alt) {
        for (AnulacaoORC anulacaoORC : alt.getAnulacoesORCs()) {
            anulacaoORCSelecionado = anulacaoORC;
            anulacaoORCSelecionado.setId(null);
            anulacaoORCSelecionado.setEventoContabil(null);
            anulacaoORCSelecionado.setHistoricoNota(null);
            anulacaoORCSelecionado.setHistoricoRazao(null);
            anulacaoORCSelecionado.setDataRegistro(getSistemaControlador().getDataOperacao());
            anulacaoORCSelecionado.setAlteracaoORC(selecionado);
            anulacaoORCSelecionado.setSaldo(anulacaoORC.getValor());
            selecionado.getAnulacoesORCs().add(anulacaoORCSelecionado);
            novaAnulacaoOrc();
            novaDespesaOrcAnulacao();
        }
    }

    private void adicionarSuplementacaoAlteracaoRecuperada(AlteracaoORC alt) {
        for (SuplementacaoORC suplementacaoORC : alt.getSuplementacoesORCs()) {
            suplementacaoORCSelecionado = suplementacaoORC;
            suplementacaoORCSelecionado.setId(null);
            suplementacaoORCSelecionado.setEventoContabil(null);
            suplementacaoORCSelecionado.setHistoricoNota(null);
            suplementacaoORCSelecionado.setHistoricoRazao(null);
            suplementacaoORCSelecionado.setDataRegistro(getSistemaControlador().getDataOperacao());
            suplementacaoORCSelecionado.setAlteracaoORC(selecionado);
            suplementacaoORCSelecionado.setSaldo(suplementacaoORC.getValor());
            selecionado.getSuplementacoesORCs().add(suplementacaoORCSelecionado);
            novaSuplementacao();
            saldofonte = BigDecimal.ZERO;
            novaDespesaOrcSuplementacao();
        }
    }


    private void parametrosIniciaisAlteracaoRecuperada() {
        operacao = Operacoes.NOVO;
        novaAnulacaoOrc();
        novaSuplementacao();
        suplementacaoORCSelecionado.setAlteracaoORC(selecionado);
        novaReceitaAlteracao();
        receitaAlteracaoORCSelecionada.setReceitaLOA(new ReceitaLOA());
        selecionado.setReceitasAlteracoesORCs(new ArrayList<ReceitaAlteracaoORC>());
        selecionado.setSuplementacoesORCs(new ArrayList<SuplementacaoORC>());
        selecionado.setAnulacoesORCs(new ArrayList<AnulacaoORC>());
        anulacaoORCSelecionado.setMes((Integer) DataUtil.getMes(selecionado.getDataAlteracao()));
        tipoConfigAlteracaoOrc = TipoConfigAlteracaoOrc.SUPLEMENTAR;
    }

    @URLAction(mappingId = "ver-alteracao-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
    }

    public Boolean isGestorOrcamento() {
        try {
            return facade.isGestorOrcamento(
                getSistemaControlador().getUsuarioCorrente(),
                getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(),
                getSistemaControlador().getDataOperacao());
        } catch (Exception ex) {
            FacesUtil.addError("Erro: ", "Problema ao verificar se o usuario é gestor " + ex.getMessage());
            return false;
        }
    }

    public void atribuirDataEfetiavao() {
        selecionado.setDataEfetivacao(getSistemaControlador().getDataOperacao());
        podeEditarGrupoOrcamentario();
    }

    public Boolean isAlteracaoSalvaEmElaboracao() {
        return selecionado.getId() != null && selecionado.isAlteracaoEmElaboracao();
    }

    public Boolean renderizarBotaoIndeferirOuDefeir() {
        if (selecionado.getId() != null
            && selecionado.getStatus().equals(StatusAlteracaoORC.EM_ANALISE)) {
            return isGestorOrcamento();
        }
        return false;
    }

    public Boolean isAlteracaoIndeferida() {
        return selecionado.getId() != null && StatusAlteracaoORC.INDEFERIDO.equals(selecionado.getStatus());
    }

    public boolean isAlteracaoExcluida() {
        return StatusAlteracaoORC.EXCLUIDA.equals(selecionado.getStatus());
    }

    public Boolean renderizarBotaoImprimirMinuta() {
        return selecionado.getId() != null && isGestorOrcamento();
    }

    public Boolean renderizarMensagemSolicitacoesPendentes() {
        for (SolicitacaoDespesaOrc despesaOrc : selecionado.getSolicitacoes()) {
            if (despesaOrc.getDeferidaEm() == null) {
                return true;
            }
        }
        return false;
    }

    public Boolean renderizarMensagemSuplementacoesSemGrupoOrcamentorio() {
        for (SuplementacaoORC suplementacaoORC : selecionado.getSuplementacoesORCs()) {
            if (suplementacaoORC.getFonteDespesaORC().getGrupoOrcamentario() == null) {
                return true;
            }
        }
        return false;
    }

    public Boolean renderizarMensagemAnulacoesSemGrupoOrcamentorio() {
        for (AnulacaoORC anulacaoORC : selecionado.getAnulacoesORCs()) {
            if (anulacaoORC.getFonteDespesaORC().getGrupoOrcamentario() == null) {
                return true;
            }
        }
        return false;
    }

    public Boolean renderizarBotaoDeferirSolicitacao() {
        return selecionado.getId() != null && selecionado.isAlteracaoEmAnalise() && isGestorOrcamento();
    }

    public Boolean podeEditarQuandoForGestor() {
        if (selecionado.isAlteracaoEmElaboracao()) {
            return true;
        }
        if (selecionado.isAlteracaoEmAnalise()) {
            return isGestorOrcamento();
        }
        return false;
    }

    public void definirSolicitacaoElementoDesepesa(SolicitacaoDespesaOrc solicitacao) {
        solicitacaoDespesaOrc = solicitacao;
    }

    public Boolean desabilitarBotaoEditar() {
        if (StatusAlteracaoORC.EXCLUIDA.equals(selecionado.getStatus())) {
            return true;
        }
        if (StatusAlteracaoORC.ELABORACAO.equals(selecionado.getStatus())) {
            return true;
        }
        if (!StatusAlteracaoORC.ELABORACAO.equals(selecionado.getStatus())) {
            return isGestorOrcamento();
        }
        return false;
    }

    public Boolean desabilitarBotaoEditar(AlteracaoORC alteracaoORC) {
        recuperarSelecionado(alteracaoORC);
        if (selecionado.isAlteracaoEmElaboracao()) {
            return true;
        }
        if (!selecionado.isAlteracaoEmElaboracao() && isGestorOrcamento()) {
            return true;
        }
        return false;
    }

    private void recuperarVerEditar() {
        confirmar = false;
        selecionado = facade.recuperar(super.getId());
        novaSuplementacao();
        novaAnulacaoOrc();
        anulacoesRemovidas = new ArrayList<>();
        novaReceitaAlteracao();
        receitaAlteracaoORCSelecionada.setReceitaLOA(new ReceitaLOA());
        tipoConfigAlteracaoOrc = TipoConfigAlteracaoOrc.SUPLEMENTAR;
        novaSolicitacao();
        podeEditarGrupoOrcamentario();
        recuperarOrcamentariasUsuario();
    }

    private void novaReceitaAlteracao() {
        receitaAlteracaoORCSelecionada = new ReceitaAlteracaoORC();
        receitaAlteracaoORCSelecionada.setReceitaLOA(new ReceitaLOA());
    }

    private void podeEditarGrupoOrcamentario() {
        for (SuplementacaoORC suplementacaoORC : selecionado.getSuplementacoesORCs()) {
            definirPermissaoSuplementacao(suplementacaoORC);
        }
        for (AnulacaoORC anulacaoORC : selecionado.getAnulacoesORCs()) {
            definirPermissaoAnulacao(anulacaoORC);
        }
    }

    private void redirecionarParaEdita() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
        cleanScoped();
    }

    public void salvarAlteracoes() {
        if (isOperacaoNovo()) {
            facade.salvarNovo(selecionado);
            redirecionarParaEdita();
        } else {
            facade.salvar(selecionado, anulacoesRemovidas);
            redireciona();
        }
        FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
    }

    @Override
    public void salvar() {
        try {
            validarMotivoCasoGestorOrcamentoEStatusIndeferido();
            confirmar = false;
            Util.validarCampos(selecionado);
            informarDiferencaValorTotal();
            validarFonteValorSuplementacaoAnulacao();
            if (confirmar) {
                FacesUtil.executaJavaScript("confirmarSalvar.show()");
            } else {
                salvarAlteracoes();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ong) {
            FacesUtil.addOperacaoNaoRealizada(ong.getMessage());
        } catch (OptimisticLockException ole) {
            FacesUtil.addOperacaoOptimisticLock();
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarMotivoCasoGestorOrcamentoEStatusIndeferido() {
        if (isGestorOrcamento() && StatusAlteracaoORC.INDEFERIDO.equals(selecionado.getStatus())) {
            ValidacaoException ve = new ValidacaoException();
            if (selecionado.getMotivoIndeferimento() == null || selecionado.getMotivoIndeferimento().trim().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo deve ser informado.");
            }
            ve.lancarException();
        }
    }

    public void alterarTodasOrigemRecurso() {
        if (suplementacaoORCSelecionado.getOrigemSuplemtacao() != null) {
            for (SuplementacaoORC suplementacaoDaLista : selecionado.getSuplementacoesORCs()) {
                suplementacaoDaLista.setOrigemSuplemtacao(suplementacaoORCSelecionado.getOrigemSuplemtacao());
            }
        } else {
            FacesUtil.addCampoObrigatorio("Informe a Origem do Recurso!");
        }
    }

    private void validarPorFonte(Boolean escolherValidacaoDeSolicitacao) {
        ValidacaoException ve = new ValidacaoException();

        List<AlteracaoOrcVO> anulacoes = Lists.newArrayList();
        List<AlteracaoOrcVO> receitas = Lists.newArrayList();

        HashMap<FonteDeRecursos, BigDecimal> mapAnulacao = new HashMap<>();
        HashMap<FonteDeRecursos, BigDecimal> mapReceita = new HashMap<>();

        popularMapsValidacaoPorFonte(mapAnulacao, mapReceita);

        popularEntidadeAuxiliarValidacaoPorFonte(anulacoes, receitas, mapAnulacao, mapReceita);

        for (AlteracaoOrcVO receita : receitas) {
            for (AlteracaoOrcVO anulacao : anulacoes) {
                if (anulacao.getFonteDeRecursos().equals(receita.getFonteDeRecursos())) {
                    anulacao.setEncontrouFonte(true);
                    receita.setEncontrouFonte(true);
                    if (anulacao.getValor().compareTo(receita.getValor()) != 0) {
                        if (escolherValidacaoDeSolicitacao) {
                            FacesUtil.addWarn("Atenção", "O total da fonte de recurso(" + receita.getFonteDeRecursos() + ") de crédito adicional esta diferente das anulações de crédito.");
                            confirmar = true;
                        } else {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("O total da fonte de recurso(" + receita.getFonteDeRecursos() + ") de crédito adicional esta diferente das anulações de crédito.");
                        }
                    }
                }
            }
        }

        for (AlteracaoOrcVO anulacao : anulacoes) {
            if (!anulacao.getEncontrouFonte()) {
                if (escolherValidacaoDeSolicitacao) {
                    FacesUtil.addWarn("Atenção", "Falta lançamento(s) da fonte de recurso(" + anulacao.getFonteDeRecursos() + ") para crédito adicional.");
                    confirmar = true;
                } else {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Falta lançamento(s) da fonte de recurso(" + anulacao.getFonteDeRecursos() + ") para as anulações ou crédito adicional.");
                }
            }
        }

        for (AlteracaoOrcVO receita : receitas) {
            if (!receita.getEncontrouFonte()) {
                if (escolherValidacaoDeSolicitacao) {
                    FacesUtil.addWarn("Atenção", "Falta lançamento(s) da fonte de recurso(" + receita.getFonteDeRecursos() + ") para as anulações.");
                    confirmar = true;
                } else {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Falta lançamento(s) da fonte de recurso(" + receita.getFonteDeRecursos() + ") para crédito adicional ou receita.");
                }
            }
        }
        ve.lancarException();
    }

    private void popularEntidadeAuxiliarValidacaoPorFonte(List<AlteracaoOrcVO> anulacoes, List<AlteracaoOrcVO> receitas, HashMap<FonteDeRecursos, BigDecimal> mapAnulacao, HashMap<FonteDeRecursos, BigDecimal> mapReceita) {
        for (Map.Entry<FonteDeRecursos, BigDecimal> entryReceita : mapReceita.entrySet()) {
            AlteracaoOrcVO alteracaoOrcVO = new AlteracaoOrcVO();
            alteracaoOrcVO.setFonteDeRecursos(entryReceita.getKey());
            alteracaoOrcVO.setValor(entryReceita.getValue());
            receitas.add(alteracaoOrcVO);
        }

        for (Map.Entry<FonteDeRecursos, BigDecimal> entryAnulacao : mapAnulacao.entrySet()) {
            AlteracaoOrcVO alteracaoOrcVO = new AlteracaoOrcVO();
            alteracaoOrcVO.setFonteDeRecursos(entryAnulacao.getKey());
            alteracaoOrcVO.setValor(entryAnulacao.getValue());
            anulacoes.add(alteracaoOrcVO);
        }
    }

    private void popularMapsValidacaoPorFonte(HashMap<FonteDeRecursos, BigDecimal> mapAnulacao, HashMap<FonteDeRecursos, BigDecimal> mapReceita) {
        for (SuplementacaoORC suplementacoesORC : selecionado.getSuplementacoesORCs()) {
            if (!OrigemSuplementacaoORC.SUPERAVIT.equals(suplementacoesORC.getOrigemSuplemtacao())) {
                if (mapReceita.get(((ContaDeDestinacao) suplementacoesORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos()) != null) {
                    mapReceita.put(((ContaDeDestinacao) suplementacoesORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos(),
                        mapReceita.get(((ContaDeDestinacao) suplementacoesORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos()).add(suplementacoesORC.getValor()));
                } else {
                    mapReceita.put(((ContaDeDestinacao) suplementacoesORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos(),
                        suplementacoesORC.getValor());
                }
            }
        }

        for (AnulacaoORC anulacaoORC : selecionado.getAnulacoesORCs()) {
            if (mapAnulacao.get(((ContaDeDestinacao) anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos()) != null) {
                mapAnulacao.put(((ContaDeDestinacao) anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos(),
                    mapAnulacao.get(((ContaDeDestinacao) anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos()).add(anulacaoORC.getValor()));
            } else {
                mapAnulacao.put(((ContaDeDestinacao) anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos(),
                    anulacaoORC.getValor());
            }
        }

        for (ReceitaAlteracaoORC receitaAlteracaoORC : selecionado.getReceitasAlteracoesORCs()) {
            if (TipoAlteracaoORC.ANULACAO.equals(receitaAlteracaoORC.getTipoAlteracaoORC())) {
                if (mapReceita.get(receitaAlteracaoORC.getFonteDeRecurso()) != null) {
                    mapReceita.put(receitaAlteracaoORC.getFonteDeRecurso(), mapReceita.get(receitaAlteracaoORC.getFonteDeRecurso()).add(receitaAlteracaoORC.getValor()));
                } else {
                    mapReceita.put(receitaAlteracaoORC.getFonteDeRecurso(), receitaAlteracaoORC.getValor());
                }
            } else {
                if (mapAnulacao.get(receitaAlteracaoORC.getFonteDeRecurso()) != null) {
                    mapAnulacao.put(receitaAlteracaoORC.getFonteDeRecurso(), mapAnulacao.get(receitaAlteracaoORC.getFonteDeRecurso()).add(receitaAlteracaoORC.getValor()));
                } else {
                    mapAnulacao.put(receitaAlteracaoORC.getFonteDeRecurso(), receitaAlteracaoORC.getValor());
                }
            }
        }
    }

    private List<SuplementacaoORC> listaSuplementacaoPorOrigem(List<SuplementacaoORC> lista, OrigemSuplementacaoORC origemSuplementacaoORC) {
        List<SuplementacaoORC> retorno = new ArrayList<>();
        for (SuplementacaoORC sup : lista) {
            if (sup.getOrigemSuplemtacao().equals(origemSuplementacaoORC)) {
                retorno.add(sup);
            }
        }
        return retorno;
    }

    private List<ReceitaAlteracaoORC> listaReceitasTipoPrevisao(List<ReceitaAlteracaoORC> lista) {
        List<ReceitaAlteracaoORC> retorno = new ArrayList<>();
        for (ReceitaAlteracaoORC rec : lista) {
            if (rec.getTipoAlteracaoORC().equals(TipoAlteracaoORC.PREVISAO)) {
                retorno.add(rec);
            }
        }
        return retorno;
    }

    private void validarItensSuplementacaoAnulacaoSolicitacaoAndReceita(ValidacaoException ve) {
        if (selecionado.getAnulacoesORCs().isEmpty()
            && selecionado.getReceitasAlteracoesORCs().isEmpty()
            && selecionado.getSolicitacoes().isEmpty()
            && selecionado.getSuplementacoesORCs().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" É obrigatório conter ao menos 1 (UM) Item em qualquer uma das listas de Solicitações, Crédito Adicional, Anulação de Dotação ou Receita.");
        }
        if ((!listaSuplementacaoPorOrigem(selecionado.getSuplementacoesORCs(), OrigemSuplementacaoORC.ANULACAO).isEmpty() ||
            !listaSuplementacaoPorOrigem(selecionado.getSuplementacoesORCs(), OrigemSuplementacaoORC.RESERVA_CONTINGENCIA).isEmpty()) &&
            selecionado.getAnulacoesORCs().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" É obrigatório conter Anulações referente aos Créditos Adicionais.");
        }
        if ((listaSuplementacaoPorOrigem(selecionado.getSuplementacoesORCs(), OrigemSuplementacaoORC.ANULACAO).isEmpty() &&
            listaSuplementacaoPorOrigem(selecionado.getSuplementacoesORCs(), OrigemSuplementacaoORC.RESERVA_CONTINGENCIA).isEmpty()) &&
            !selecionado.getAnulacoesORCs().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" É obrigatório conter Créditos Adicionais referentes às Anulações.");
        }
        if ((!listaSuplementacaoPorOrigem(selecionado.getSuplementacoesORCs(), OrigemSuplementacaoORC.EXCESSO).isEmpty()
            || !listaSuplementacaoPorOrigem(selecionado.getSuplementacoesORCs(), OrigemSuplementacaoORC.OPERACAO_CREDITO).isEmpty())
            && listaReceitasTipoPrevisao(selecionado.getReceitasAlteracoesORCs()).isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" É obrigatório conter Receitas do Tipo 'Previsão' referentes aos Créditos Adicionais.");
        }
        if ((listaSuplementacaoPorOrigem(selecionado.getSuplementacoesORCs(), OrigemSuplementacaoORC.EXCESSO).isEmpty() &&
            listaSuplementacaoPorOrigem(selecionado.getSuplementacoesORCs(), OrigemSuplementacaoORC.OPERACAO_CREDITO).isEmpty()) &&
            !listaReceitasTipoPrevisao(selecionado.getReceitasAlteracoesORCs()).isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" É obrigatório conter Créditos Adicionais referentes às Receitas ");
        }
        ve.lancarException();
    }

    public void enviarAlteracaoParaAnalise() {
        try {
            selecionado.realizarValidacoes();
            facade.enviarAlteracaoParaAnalise(selecionado);
            FacesUtil.addOperacaoRealizada("Alteração Orçamentária Nº <b>" + selecionado.getCodigo() + "</b> enviada para análise com sucesso.");
            if (isGestorOrcamento()) {
                redirecionarParaEdita();
            } else {
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (OptimisticLockException ole) {
            FacesUtil.addOperacaoOptimisticLock();
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void enviarAlteracaoParaElaboracao() {
        try {
            selecionado.realizarValidacoes();
            facade.enviarAlteracaoParaElaboracao(selecionado);
            FacesUtil.addOperacaoRealizada("Alteração Orçamentária Nº <b>" + selecionado.getCodigo() + "</b> enviada para elaboração com sucesso.");
            if (isGestorOrcamento()) {
                redirecionarParaEdita();
            } else {
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (OptimisticLockException ole) {
            FacesUtil.addOperacaoOptimisticLock();
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void indeferirAleracao() {
        try {
            validarIndeferimento();
            facade.indeferirAlteracaoOrc(selecionado, anulacoesRemovidas);
            FacesUtil.addOperacaoRealizada("Alteração Orçamentária Nº <b>" + selecionado.getCodigo() + "</b> foi indeferida com sucesso.");
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (OptimisticLockException ole) {
            FacesUtil.addOperacaoOptimisticLock();
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao Indeferir: " + ex.getMessage() + " Entre em contato com o suporte!");
        }
    }

    private void validarIndeferimento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataIndeferimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo data é obrigatório.");
        } else if (selecionado.getDataIndeferimento().compareTo(selecionado.getDataAlteracao()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data do indeferimento não pode ser inferior a data da alteração.");
        }
        if (selecionado.getMotivoIndeferimento() == null || selecionado.getMotivoIndeferimento().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Motivo é obrigatório.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public StreamedContent baixarAnexo() {
        try {
            if (selecionado.getArquivo() != null) {
                return facade.getArquivoFacade().montarArquivoParaDownloadPorArquivo(selecionado.getArquivo());
            }
        } catch (Exception e) {
            logger.error("Erro ao recuperar arquivo ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível baixar o Edital em anexo. Detlhes: " + e.getMessage());
        }
        return null;
    }

    public void uploadArquivo(FileUploadEvent evento) {
        try {
            Arquivo arquivo = new Arquivo();
            arquivo.setMimeType(facade.getArquivoFacade().getMimeType(evento.getFile().getInputstream()));
            arquivo.setNome(evento.getFile().getFileName());
            arquivo.setDescricao(evento.getFile().getFileName());
            arquivo.setTamanho(evento.getFile().getSize());
            arquivo.setDescricao(evento.getFile().getFileName());
            arquivo.setInputStream(evento.getFile().getInputstream());
            selecionado.setArquivo(arquivo);
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Erro ao anexar arquivo: " + ex.getMessage());
        }
    }

    public void removerArquivo() {
        selecionado.setArquivo(null);
    }

    public void cancelarDeferimento() {
        removerArquivo();
        selecionado.setDataEfetivacao(null);
        atribuirNullAtoLegal();
    }

    public void atribuirNullAtoLegal() {
        selecionado.setAtoLegal(null);
        selecionado.setAtoLegalORC(null);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public void definirUnidade() {
        try {
            receitaAlteracaoORCSelecionada.getReceitaLOA().setEntidade(hierarquiaOrganizacional.getSubordinada());
        } catch (Exception e) {
            logger.debug("Erro setando Unidade", e);
        }
    }

    public void limparContaReceitaAndUnidade() {
        hierarquiaOrganizacional = null;
        receitaAlteracaoORCSelecionada.setReceitaLOA(null);
    }

    public void limparFonteRecursoAndContaReceita() {
        receitaAlteracaoORCSelecionada.setFonteDeRecurso(null);
        receitaAlteracaoORCSelecionada.setReceitaLOA(null);
    }

    public void definirNullFonteSuplementacao() {
        suplementacaoORCSelecionado.setFonteDespesaORC(null);
    }

    public void definirNullFonteAnulacao() {
        anulacaoORCSelecionado.setFonteDespesaORC(null);
    }

    public void imprimirFicha() {
        Util.geraPDF("Ficha de Solicitação de Crédito", conteudoFicha, FacesContext.getCurrentInstance());
    }

    public void imprimirMinutaDiarioPDF() {
        Util.geraPDF("Minuta Diário da Alteração Orçamentária", conteudoMinutaDiario, FacesContext.getCurrentInstance());
    }

    public void prepararAndImprimirFicha() {
        prepararFichaParaImprimir();
        imprimirFicha();
    }

    public void prepararFichaParaImprimir() {
        try {
            conteudoFicha = facade.getMinutaAlteracaoOrcamentariaFacade().montaFichaSolicitacaoCredito(selecionado);
            FacesUtil.executaJavaScript("dialogImprimirFicha.show()");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar a Ficha: " + ex.getMessage() + "Entre em contato com o suporte!");
        }
    }

    public void prepararMinutaDiarioParaImprimir() {
        try {
            conteudoMinutaDiario = facade.getMinutaAlteracaoOrcamentariaFacade().montaMinutaAlteracaoOrcamentariaDiario(selecionado);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar a Minuta do Diário: " + ex.getMessage() + "Entre em contato com o suporte!");
        }
    }

    public void buscarConteudoMinuta() {
        try {
            if (selecionado.getId() != null) {
                RestTemplate restTemplate = new RestTemplate();
                RelatorioDTO dto = new RelatorioDTO();
                dto.adicionarParametro("idAlteracaoORC", selecionado.getId());
                dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(facade.getSistemaFacade().getDataOperacao()));
                dto.adicionarParametro("anoExCorrente", facade.getSistemaFacade().getExercicioCorrente().getAno());
                dto.adicionarParametro("caminhoDaImagem", FacesUtil.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif");
                dto.setNomeRelatorio("Minuta da Alteração Orçamentária");
                dto.setApi("contabil/minuta-alteracao-orcamentaria/conteudo/");
                ConfiguracaoDeRelatorio configuracao = facade.getSistemaFacade().buscarConfiguracaoDeRelatorioPorChave();
                String urlWebreport = configuracao.getUrl() + dto.getApi();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, String.class);
                conteudoMinuta = exchange.getBody();
            }
        } catch (Exception ex) {
            FacesUtil.addError("Erro ao gerar a Minuta: ", ex.getMessage());
        }
    }

    public void imprimirMinutaPDF() {
        try {
            if (conteudoMinuta != null) {
                RelatorioDTO dto = new RelatorioDTO();
                dto.adicionarParametro("conteudoMinuta", conteudoMinuta);
                dto.setNomeRelatorio("Minuta da Alteração Orçamentária");
                dto.setApi("contabil/minuta-alteracao-orcamentaria/");
                ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            }
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void copiarConteudoWord() {
        conteudoMinutaWord = conteudoMinuta;
    }

    public BigDecimal totalSuplementacoes() {
        BigDecimal total = BigDecimal.ZERO;
        for (SuplementacaoORC suplementacaoORC : selecionado.getSuplementacoesORCs()) {
            total = total.add(suplementacaoORC.getValor());
        }
        return total;
    }

    public BigDecimal totalSolicitacoesElementoDesp() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado.getSolicitacoes() != null) {
            for (SolicitacaoDespesaOrc elemento : selecionado.getSolicitacoes()) {
                total = total.add(elemento.getValor());
            }
        }
        return total;
    }

    public BigDecimal totalAnulacoes() {
        BigDecimal total = BigDecimal.ZERO;
        for (AnulacaoORC anulacaoORC : selecionado.getAnulacoesORCs()) {
            total = total.add(anulacaoORC.getValor());
        }
        return total;
    }

    public BigDecimal totalReceitas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ReceitaAlteracaoORC receitaAlteracaoORC : selecionado.getReceitasAlteracoesORCs()) {
            total = total.add(receitaAlteracaoORC.getValor());
        }
        return total;
    }

    public DespesaORC getDespesaORCSuplementacao() {
        return despesaORCSuplementacao;
    }

    public void setDespesaORCSuplementacao(DespesaORC despesaORCSuplementacao) {
        this.despesaORCSuplementacao = despesaORCSuplementacao;
    }

    public ReceitaAlteracaoORC getReceitaAlteracaoORCSelecionada() {
        return receitaAlteracaoORCSelecionada;
    }

    public void setReceitaAlteracaoORCSelecionada(ReceitaAlteracaoORC receitaAlteracaoORCSelecionada) {
        this.receitaAlteracaoORCSelecionada = receitaAlteracaoORCSelecionada;
    }

    public SuplementacaoORC getSuplementacaoORCSelecionado() {
        return suplementacaoORCSelecionado;
    }

    public void setSuplementacaoORCSelecionado(SuplementacaoORC suplementacaoORCSelecionado) {
        this.suplementacaoORCSelecionado = suplementacaoORCSelecionado;
    }

    public AnulacaoORC getAnulacaoORCSelecionado() {
        return anulacaoORCSelecionado;
    }

    public void setAnulacaoORCSelecionado(AnulacaoORC anulacaoORCSelecionado) {
        this.anulacaoORCSelecionado = anulacaoORCSelecionado;
    }

    public TipoConfigAlteracaoOrc getTipoConfigAlteracaoOrc() {
        return tipoConfigAlteracaoOrc;
    }

    public void setTipoConfigAlteracaoOrc(TipoConfigAlteracaoOrc tipoConfigAlteracaoOrc) {
        this.tipoConfigAlteracaoOrc = tipoConfigAlteracaoOrc;
    }

    public String getConteudoMinuta() {
        return conteudoMinuta;
    }

    public void setConteudoMinuta(String conteudoMinuta) {
        this.conteudoMinuta = conteudoMinuta;
    }

    public String getConteudoFicha() {
        return conteudoFicha;
    }

    public void setConteudoFicha(String conteudoFicha) {
        this.conteudoFicha = conteudoFicha;
    }

    public String getConteudoMinutaDiario() {
        return conteudoMinutaDiario;
    }

    public void setConteudoMinutaDiario(String conteudoMinutaDiario) {
        this.conteudoMinutaDiario = conteudoMinutaDiario;
    }

    public BigDecimal getSaldofonte() {
        return saldofonte;
    }

    public void setSaldofonte(BigDecimal saldofonte) {
        this.saldofonte = saldofonte;
    }

    public AlteracaoORCFacade getFacade() {
        return facade;
    }

    public SolicitacaoDespesaOrc getSolicitacaoDespesaOrc() {
        return solicitacaoDespesaOrc;
    }

    public void setSolicitacaoDespesaOrc(SolicitacaoDespesaOrc solicitacaoDespesaOrc) {
        this.solicitacaoDespesaOrc = solicitacaoDespesaOrc;
    }

    public DespesaORC getDespesaORCAnulacao() {
        return despesaORCAnulacao;
    }

    public void setDespesaORCAnulacao(DespesaORC despesaORCAnulacao) {
        this.despesaORCAnulacao = despesaORCAnulacao;
    }

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public GrupoOrcamentario getGrupoOrcamentarioSolicitacao() {
        return grupoOrcamentarioSolicitacao;
    }

    public void setGrupoOrcamentarioSolicitacao(GrupoOrcamentario grupoOrcamentarioSolicitacao) {
        this.grupoOrcamentarioSolicitacao = grupoOrcamentarioSolicitacao;
    }

    private void definirPermissaoSuplementacao(SuplementacaoORC suplem) {
        if (suplem.getFonteDespesaORC().getGrupoOrcamentario() == null && suplem.getPermiteAlteracaoGrupoOrcamentario() == null) {
            suplem.setPermiteAlteracaoGrupoOrcamentario(Boolean.TRUE);
        } else {
            suplem.setPermiteAlteracaoGrupoOrcamentario(Boolean.FALSE);
        }
    }

    private void definirPermissaoAnulacao(AnulacaoORC anulacaoORC) {
        if (anulacaoORC.getFonteDespesaORC().getGrupoOrcamentario() == null && anulacaoORC.getPermiteAlteracaoGrupoOrcamentario() == null) {
            anulacaoORC.setPermiteAlteracaoGrupoOrcamentario(Boolean.TRUE);
        } else {
            anulacaoORC.setPermiteAlteracaoGrupoOrcamentario(Boolean.FALSE);
        }
    }

    public List<HierarquiaOrganizacional> getOrcamentariasUsuario() {
        return orcamentariasUsuario;
    }

    public void setOrcamentariasUsuario(List<HierarquiaOrganizacional> orcamentariasUsuario) {
        this.orcamentariasUsuario = orcamentariasUsuario;
    }

    public HierarquiaOrganizacional getOrcamentariaSolicitacao() {
        return orcamentariaSolicitacao;
    }

    public void setOrcamentariaSolicitacao(HierarquiaOrganizacional orcamentariaSolicitacao) {
        this.orcamentariaSolicitacao = orcamentariaSolicitacao;
    }

    private void recuperarOrcamentariasUsuario() {
        List<HierarquiaOrganizacional> orcamentaria = facade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao(), "ORCAMENTARIA", 3);
        orcamentariasUsuario = new ArrayList<>();
        orcamentariasUsuario.add(new HierarquiaOrganizacional(null, null, "", ""));
        if (orcamentaria != null || !orcamentaria.isEmpty()) {
            orcamentariasUsuario.addAll(orcamentaria);
        }
    }

    public Boolean isUsuarioAcessoMaisQueUmaOrcamentaria() {
        return orcamentariasUsuario != null && !orcamentariasUsuario.isEmpty();
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public ConverterAutoComplete getConverterGrupoOrcamentario() {
        if (converterGrupoOrcamentario == null) {
            converterGrupoOrcamentario = new ConverterAutoComplete(GrupoOrcamentario.class, facade.getGrupoOrcamentarioFacade());
        }
        return converterGrupoOrcamentario;
    }

    public ConverterAutoComplete getConverterFonteDeRecursos() {
        if (converterFonteDeRecursos == null) {
            converterFonteDeRecursos = new ConverterAutoComplete(ContaDeDestinacao.class, facade.getContaFacade());
        }
        return converterFonteDeRecursos;
    }

    public ConverterAutoComplete getConverterSubProjetoAtividade() {
        if (converterSubProjetoAtividade == null) {
            converterSubProjetoAtividade = new ConverterAutoComplete(SubAcaoPPA.class, facade.getProvisaoPPAFacade().getSubProjetoAtividadeFacade());
        }
        return converterSubProjetoAtividade;
    }


    public Converter getConverterReceitaLOA() {
        if (converterReceitaLOA == null) {
            converterReceitaLOA = new ConverterAutoComplete(ReceitaLOA.class, facade.getReceitaLOAFacade());
        }
        return converterReceitaLOA;
    }

    public Converter getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, facade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public String getConteudoMinutaWord() {
        return conteudoMinutaWord;
    }

    public void setConteudoMinutaWord(String conteudoMinutaWord) {
        this.conteudoMinutaWord = conteudoMinutaWord;
    }

    public void informarDiferencaValorTotal() {
        if (totalSuplementacoes().compareTo(totalAnulacoes()) != 0 && StatusAlteracaoORC.ELABORACAO.equals(selecionado.getStatus())) {
            FacesUtil.addWarn("Atenção", "Os valores dos créditos adicionais não são iguais aos créditos da anulação. " +
                "Valor suplementação: " + Util.formataValor(totalSuplementacoes()) + " Valor de anulação: " + Util.formataValor(totalAnulacoes()));
        }
    }

    private void validarFonteValorSuplementacaoAnulacao() {
        if (!selecionado.getSuplementacoesORCs().isEmpty() && !selecionado.getAnulacoesORCs().isEmpty()) {
            validarPorFonte(true);
        }
    }

    public AssistenteAlteracaoOrc getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteAlteracaoOrc assistente) {
        this.assistente = assistente;
    }

    public void alterarSituacaoParaExcluido() {
        try {
            validarMotivoExclusao();
            selecionado.setDataExclusao(getDataOperacao());
            selecionado.setStatus(StatusAlteracaoORC.EXCLUIDA);
            facade.salvar(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void validarMotivoExclusao() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(selecionado.getMotivoExclusao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo da Exclusão deve ser informado.");
        }
        ve.lancarException();
    }

    public void cancelarExclusao() {
        selecionado.setMotivoExclusao("");
    }

    public Date getDataOperacao() {
        return getSistemaControlador().getDataOperacao();
    }

    @Override
    public void cleanScoped() {
        super.cleanScoped();

        tipoConfigAlteracaoOrc = null;
        converterSubProjetoAtividade = null;
        converterGrupoOrcamentario = null;
        converterFonteDeRecursos = null;
        moneyConverter = null;
        converterFonteDespesaORC = null;
        converterReceitaLOA = null;
        despesaORCSuplementacao = null;
        despesaORCAnulacao = null;
        suplementacaoORCSelecionado = null;
        anulacaoORCSelecionado = null;
        receitaAlteracaoORCSelecionada = null;
        solicitacaoDespesaOrc = null;
        grupoOrcamentarioSolicitacao = null;
        hierarquiaOrganizacional = null;
        saldofonte = null;
        conteudoFicha = null;
        conteudoMinutaDiario = null;
        conteudoMinuta = null;
        indiceAba = null;
        orcamentariasUsuario = null;
        anulacoesRemovidas = null;
        orcamentariaSolicitacao = null;
        conteudoMinutaWord = null;
        confirmar = null;
        future = null;
        assistente = null;
    }
}
