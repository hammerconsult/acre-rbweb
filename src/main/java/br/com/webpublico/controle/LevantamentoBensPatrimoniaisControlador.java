package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LevantamentoBensPatrimoniaisFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Arthur Zavadski
 * @author Lucas Cheles
 */
@ManagedBean(name = "levantamentoBensPatrimoniaisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLevantamento", pattern = "/levantamento-de-bens-patrimoniais/novo/", viewId = "/faces/administrativo/patrimonio/levantamentodebenspatrimoniais/edita.xhtml"),
    @URLMapping(id = "editarLevantamento", pattern = "/levantamento-de-bens-patrimoniais/editar/#{levantamentoBensPatrimoniaisControlador.id}/", viewId = "/faces/administrativo/patrimonio/levantamentodebenspatrimoniais/edita.xhtml"),
    @URLMapping(id = "listarLevantamento", pattern = "/levantamento-de-bens-patrimoniais/listar/", viewId = "/faces/administrativo/patrimonio/levantamentodebenspatrimoniais/lista.xhtml"),
    @URLMapping(id = "verLevantamento", pattern = "/levantamento-de-bens-patrimoniais/ver/#{levantamentoBensPatrimoniaisControlador.id}/", viewId = "/faces/administrativo/patrimonio/levantamentodebenspatrimoniais/visualizar.xhtml"),
    @URLMapping(id = "emitirRelatorio", pattern = "/levantamento-de-bens-patrimoniais/relatorio-de-valores/", viewId = "/faces/administrativo/patrimonio/levantamentodebenspatrimoniais/relatorio.xhtml"),
    @URLMapping(id = "emitirRelatorioLevantamentoBemCodigoRepetido", pattern = "/levantamento-de-bens-patrimoniais/relatorio-de-levantamentos-codigo-repetido/", viewId = "/faces/administrativo/patrimonio/levantamentodebenspatrimoniais/relatorioLevantBemCodigoRepetido.xhtml")
})
public class LevantamentoBensPatrimoniaisControlador extends PrettyControlador<LevantamentoBemPatrimonial> implements CRUD {

    @EJB
    private LevantamentoBensPatrimoniaisFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria;
    private OrigemRecursoBem origemRecursoBemSelecionada;
    private RelatorioValoresLevantamentoBem relatorioValoresLevantamentoBem;
    private RelatorioLevantamentoBemCodigoRepetido relatorioLevantamentoBemCodigoRepetido;
    private GrupoBem grupoBem;
    private LevantamentoBemPatrimonial levantamentoBemPatrimonialOriginal;
    private String notaEmpenho;
    private Boolean segurado = Boolean.FALSE;
    private Boolean garantido = Boolean.FALSE;
    private String nomeRelatorio;

    public LevantamentoBensPatrimoniaisControlador() {
        super(LevantamentoBemPatrimonial.class);
    }

    @URLAction(mappingId = "novoLevantamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributosParaNovoCadastro();
        levantamentoBemPatrimonialOriginal = selecionado;
        novaSeguradora();
        novaGarantia();
    }

    @URLAction(mappingId = "verLevantamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        inicializarAtributosParaVisualizarEditar();
    }

    @URLAction(mappingId = "editarLevantamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarAtributosParaVisualizarEditar();
    }

    private HierarquiaOrganizacional recuperarHierarquia(TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional, UnidadeOrganizacional unidadeOrganizacional) {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            tipoHierarquiaOrganizacional.name(),
            unidadeOrganizacional,
            selecionado.getDataLevantamento());
    }

    @URLAction(mappingId = "emitirRelatorio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        relatorioValoresLevantamentoBem = new RelatorioValoresLevantamentoBem();
        relatorioValoresLevantamentoBem.limparCampos();
    }

    @URLAction(mappingId = "emitirRelatorioLevantamentoBemCodigoRepetido", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposRelatorioLevantamentoBemCodigoRepetido() {
        relatorioLevantamentoBemCodigoRepetido = new RelatorioLevantamentoBemCodigoRepetido();
        relatorioLevantamentoBemCodigoRepetido.limparCampos();
    }

    @Override
    public void salvar() {
        try {
            validarSelecinado();
            if (isOperacaoNovo()) {
                salvarValidandoFase();
            } else {
                if (bloqueioPorFase(selecionado)) {
                    validarCamposNaoAlterados();
                    salvarLevantamento();
                } else {
                    salvarValidandoFase();
                }
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    private void novaGarantia() {
        if (selecionado.getGarantia() == null) {
            selecionado.setGarantia(new Garantia());
        }
    }

    private void inicializarAtributosParaNovoCadastro() {
        this.selecionado.setDataLevantamento(facade.getSistemaFacade().getDataOperacao());
        selecionado.setTipoGrupo(TipoGrupo.BEM_MOVEL_PRINCIPAL);
        this.origemRecursoBemSelecionada = new OrigemRecursoBem();
        novaGarantia();
        novaSeguradora();
    }

    private void inicializarAtributosParaVisualizarEditar() {
        this.origemRecursoBemSelecionada = new OrigemRecursoBem();
        recuperarAssociacaoComGrupoBem();
        hierarquiaOrganizacionalAdministrativa = recuperarHierarquia(TipoHierarquiaOrganizacional.ADMINISTRATIVA, selecionado.getUnidadeAdministrativa());
        hierarquiaOrganizacionalOrcamentaria = recuperarHierarquia(TipoHierarquiaOrganizacional.ORCAMENTARIA, selecionado.getUnidadeOrcamentaria());
        levantamentoBemPatrimonialOriginal = (LevantamentoBemPatrimonial) Util.clonarObjeto(selecionado);
        garantido = selecionado.getGarantia() != null;
        segurado = selecionado.getSeguradora() != null;
        if (isOperacaoEditar()) {
            novaGarantia();
            novaSeguradora();
        }
    }

    private void validarSelecinado() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        validarNotaEmpenho(ve);
    }

    private void salvarValidandoFase() {
        ValidacaoException ve = new ValidacaoException();
        validarPeriodoFase(ve);
        salvarLevantamento();
    }

    private void validarPeriodoFase(ValidacaoException ve) {
        Fase fase = facade.getFaseFacade().faseDoRecurso(
            "/administrativo/patrimonio/levantamentodebenspatrimoniais/edita.xhtml",
            selecionado.getDataAquisicao(),
            selecionado.getUnidadeOrcamentaria());
        String nomeFase = fase != null ? fase.toString() : "Nenhuma configuração (Fase ou Período Fase) encontrada!";
        if (bloqueioPorFase(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Bloqueado pelo controle de fase: " + nomeFase);
        }
        ve.lancarException();
    }

    private void validarNotaEmpenho(ValidacaoException ve) {
        if (notaEmpenho == null || notaEmpenho.trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Nota de Empenho deve ser informado.");
        }
        ve.lancarException();
        try {
            selecionado.setNotaEmpenho(Integer.valueOf(notaEmpenho));
        } catch (NumberFormatException ne) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo nota de empenho deve ser composto somente de numeros. Ex: 25632/2010 ficaria assim 25632.");
        }
        ve.lancarException();
    }

    public void salvarLevantamento() {
        try {
            validarSeguradoraOrGarantia();
            validarBem();
            ParametroPatrimonio parametroPatrimonio = facade.getParametroPatrimonioFacade().recuperarParametro();
            selecionado.validarNegocio(facade.getSistemaFacade().getDataOperacao(), parametroPatrimonio != null ? parametroPatrimonio.getDataDeCorte() : null);
            Util.validarDataMinima(selecionado.getDataAquisicao(), "data de aquisição");
            Util.validarDataMinima(selecionado.getDataNotaEmpenho(), "data da nota de empenho");
            Util.validarDataMinima(selecionado.getDataNotaFiscal(), "data da nota fiscal");
            super.salvar();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void validarSeguradoraOrGarantia() {
        if (!segurado) {
            selecionado.setSeguradora(null);
        } else {
            Util.validarCampos(selecionado.getSeguradora());
        }
        if (!garantido) {
            selecionado.setGarantia(null);
        } else {
            Util.validarCampos(selecionado.getGarantia());
        }
    }

    private void validarBem() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getItem().getGrupoObjetoCompra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Grupo Objeto de Compra do Bem deve ser informado.");
        }
        ve.lancarException();
    }

    @Override
    public AbstractFacade getFacede() {
        return this.facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/levantamento-de-bens-patrimoniais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return this.selecionado.getId();
    }

    public void recuperarAssociacaoComGrupoBem() {
        try {

            if (selecionado.getItem() != null) {
                GrupoObjetoCompraGrupoBem grupoObjetoCompraGrupoBem = facade.getGrupoObjetoCompraGrupoBemFacade().recuperarAssociacaoDoGrupoObjetoCompra(
                    selecionado.getObjetoCompra().getGrupoObjetoCompra(), facade.getSistemaFacade().getDataOperacao());
                if (grupoObjetoCompraGrupoBem != null) {
                    if (!grupoObjetoCompraGrupoBem.getGrupoBem().getTipoBem().equals(TipoBem.MOVEIS)) {
                        selecionado.setItem(null);
                        selecionado.setGrupoBem(null);
                        FacesUtil.addAtencao("O Grupo Patrimonial " + grupoObjetoCompraGrupoBem.getGrupoBem() + " vinculado ao Grupo do Objeto de Compra " +
                            grupoObjetoCompraGrupoBem.getGrupoObjetoCompra() + " não é do tipo " + TipoBem.MOVEIS.getDescricao());
                    }
                    selecionado.setGrupoBem(grupoObjetoCompraGrupoBem.getGrupoBem());
                } else {
                    if (selecionado.getItem() != null && selecionado.getGrupoObjetoCompra() != null) {
                        FacesUtil.addAtencao("Não foi possível encontrar uma associação de Grupo Objeto de Compra " + selecionado.getGrupoObjetoCompra().getDescricao() + " com um Grupo Patrimonial. Informe esta associação no 'Cadastro de Associação Grupo de Objeto de Compra e Grupo Patrimonial'");
                    }
                    selecionado.setGrupoBem(null);
                }
            }
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalAdministrativa() {
        return hierarquiaOrganizacionalAdministrativa;
    }

    public void setHierarquiaOrganizacionalAdministrativa(HierarquiaOrganizacional hierarquiaOrganizacionalAdministrativa) {
        if (hierarquiaOrganizacionalAdministrativa != null && selecionado != null) {
            selecionado.setUnidadeAdministrativa(hierarquiaOrganizacionalAdministrativa.getSubordinada());
        }
        this.hierarquiaOrganizacionalAdministrativa = hierarquiaOrganizacionalAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentaria() {
        return hierarquiaOrganizacionalOrcamentaria;
    }

    public void setHierarquiaOrganizacionalOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria) {
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalOrcamentaria;
    }

    public OrigemRecursoBem getOrigemRecursoBemSelecionada() {
        return origemRecursoBemSelecionada;
    }

    public void setOrigemRecursoBemSelecionada(OrigemRecursoBem origemRecursoBemSelecionada) {
        this.origemRecursoBemSelecionada = origemRecursoBemSelecionada;
    }

    public RelatorioValoresLevantamentoBem getRelatorio() {
        return relatorioValoresLevantamentoBem;
    }

    public void setRelatorio(RelatorioValoresLevantamentoBem relatorio) {
        this.relatorioValoresLevantamentoBem = relatorio;
    }

    public RelatorioLevantamentoBemCodigoRepetido getRelatorioLevantamentoBemCodigoRepetido() {
        return relatorioLevantamentoBemCodigoRepetido;
    }

    public void setRelatorioLevantamentoBemCodigoRepetido(RelatorioLevantamentoBemCodigoRepetido relatorioLevantamentoBemCodigoRepetido) {
        this.relatorioLevantamentoBemCodigoRepetido = relatorioLevantamentoBemCodigoRepetido;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public List<SelectItem> getListaSelectItemTipoRecursoAquisicaoBem() {
        return Util.getListSelectItem(Arrays.asList(TipoRecursoAquisicaoBem.values()));
    }

    public List<SelectItem> getListaSelectItemTipoDeAquisicaoBem() {
        return Util.getListSelectItem(Arrays.asList(LevantamentoBemPatrimonial.tiposDeAquisicaoPermitidosNoCadastro));
    }

    public List<SelectItem> getItensSelectTodosTipoDeAquisicaoBem() {
        return Util.getListSelectItem(TipoAquisicaoBem.values());
    }

    public List<SelectItem> getListaSelectItemEstadosDeConservacao() {
        return Util.getListSelectItem(Arrays.asList(EstadoConservacaoBem.getValoresPossiveisParaLevantamentoDeBemPatrimonial()));
    }

    public List<SelectItem> getListaSelectItemSituacaoConservacaoBem() {
        try {
            return Util.getListSelectItem(this.selecionado.getEstadoConservacaoBem().getSituacoes());
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public List<SelectItem> getTipoGrupo() {
        try {
            return Util.getListSelectItem(Arrays.asList(TipoGrupo.values()));
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public void adicionarOrigemRecurso() {
        try {
            origemRecursoBemSelecionada.realizarValidacoes();
            validaAdicionarMesmaOrigem();
            origemRecursoBemSelecionada.setLevantamentoBemPatrimonial(selecionado);
            selecionado.getListaDeOriemRecursoBem().add(origemRecursoBemSelecionada);
            origemRecursoBemSelecionada = new OrigemRecursoBem();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void validaAdicionarMesmaOrigem() {
        for (OrigemRecursoBem origem : selecionado.getListaDeOriemRecursoBem()) {
            if (origem.getTipoRecursoAquisicaoBem().equals(origemRecursoBemSelecionada.getTipoRecursoAquisicaoBem())) {
                throw new ExcecaoNegocioGenerica("Adicione origens de tipos diferentes.");
            }
        }
    }

    public void removerOrigemRecurso(OrigemRecursoBem origem) {
        selecionado.getListaDeOriemRecursoBem().remove(origem);
    }

    public void editarOrigemRecurso(OrigemRecursoBem origem) {
        origemRecursoBemSelecionada = origem;
        selecionado.getListaDeOriemRecursoBem().remove(origem);
    }

    public Boolean podeExcluir() {
        return facade.getLoteEfetivacaoLevantamentoBemFacade().getEfetivacaoDoLevantamento(selecionado) == null;
    }

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (hierarquiaOrganizacionalAdministrativa != null && hierarquiaOrganizacionalAdministrativa.getSubordinada() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaOrganizacionalAdministrativa.getSubordinada(), facade.getSistemaFacade().getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
            return toReturn;
        }

        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(facade.getSistemaFacade().getUsuarioCorrente(), facade.getSistemaFacade().getDataOperacao())) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> retornarUnidadeOrcamentaria() {
        if (hierarquiaOrganizacionalAdministrativa != null && hierarquiaOrganizacionalAdministrativa.getSubordinada() != null) {
            List<SelectItem> toReturn = new ArrayList<>();
            toReturn.add(new SelectItem(null, ""));

            for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaOrganizacionalAdministrativa.getSubordinada(), facade.getSistemaFacade().getDataOperacao())) {
                toReturn.add(new SelectItem(obj.getSubordinada(), obj.toString()));
            }

            return toReturn;
        }
        return null;
    }

    @Override
    public void excluir() {
        if (!bloqueioPorFase(selecionado)) {
            super.excluir();
        } else {
            try {
                FacesUtil.addOperacaoNaoPermitida("Bloqueado pelo controle de fase: " + facade.getFaseFacade().faseDoRecurso("/administrativo/patrimonio/levantamentodebenspatrimoniais/edita.xhtml", selecionado.getDataAquisicao(), selecionado.getUnidadeOrcamentaria()).getNome());
            } catch (NullPointerException nu) {
                FacesUtil.addOperacaoNaoRealizada("Nenhuma configuração (Fase ou Período Fase) encontrada!");
            }
        }
    }

    public String getNotaEmpenho() {
        if (selecionado != null && selecionado.getNotaEmpenho() != null) {
            notaEmpenho = selecionado.getNotaEmpenho().toString();
        }
        return notaEmpenho;
    }

    public void setNotaEmpenho(String notaEmpenho) {
        if (!notaEmpenho.isEmpty()) {
            try {
                selecionado.setNotaEmpenho(Integer.valueOf(notaEmpenho.trim()));
            } catch (NumberFormatException ne) {
                FacesUtil.addOperacaoNaoPermitida("O campo nota de empenho deve ser composto somente de numeros. Ex: 25632/2010 ficaria assim 25632");
            }
        }
        this.notaEmpenho = notaEmpenho;
    }

    public boolean bloqueioPorFase(LevantamentoBemPatrimonial levantamentoBemPatrimonial) {
        return !facade.getFaseFacade().existePeriodoFaseVigenteNaDataDeFechamento("/administrativo/patrimonio/levantamentodebenspatrimoniais/edita.xhtml", levantamentoBemPatrimonial.getDataAquisicao(), levantamentoBemPatrimonial.getUnidadeOrcamentaria());
    }

    public void validarCamposNaoAlterados() {
        ValidacaoException ve = new ValidacaoException();
        Fase fase = facade.getFaseFacade().faseDoRecurso(
            "/administrativo/patrimonio/levantamentodebenspatrimoniais/edita.xhtml",
            selecionado.getDataAquisicao(),
            selecionado.getUnidadeOrcamentaria());
        String nomeDaFase = fase != null ? fase.toString() : "";

        if (!selecionado.getUnidadeOrcamentaria().getId().equals(levantamentoBemPatrimonialOriginal.getUnidadeOrcamentaria().getId())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Unidade Orçamentária não pode ser alterado por que está bloqueado pelo controle de fase: " + nomeDaFase);
        }
        if (!DataUtil.dataSemHorario(selecionado.getDataAquisicao()).equals(DataUtil.dataSemHorario(levantamentoBemPatrimonialOriginal.getDataAquisicao()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data de Aquisição não pode ser alterado por que está bloqueado pelo controle de fase: " + nomeDaFase);
        }
        if (!selecionado.getGrupoBem().equals(levantamentoBemPatrimonialOriginal.getGrupoBem())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Grupo Bem não pode ser alterado por que está bloqueado pelo controle de fase: " + nomeDaFase);
        }
        if (selecionado.getValorBem().compareTo(levantamentoBemPatrimonialOriginal.getValorBem()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor do Bem não pode ser alterado por que está bloqueado pelo controle de fase): " + nomeDaFase);
        }
        ve.lancarException();
    }

    public void novaSeguradora() {
        if (selecionado.getSeguradora() == null) {
            selecionado.setSeguradora(new Seguradora());
        }
    }

    public Boolean getSegurado() {
        return segurado;
    }

    public void setSegurado(Boolean segurado) {
        this.segurado = segurado;
    }

    public boolean isGarantido() {
        return garantido;
    }

    public void setGarantido(boolean garantido) {
        this.garantido = garantido;
    }

    public enum TipoOrdenacaoRelatorioLevantamentoBem {

        UNIDADE("Unidade Organizacional", "LOCALIZACAO"),
        NUMERO_PATRIMONIO("Nº do Patrimônio", "ORDENACAO_NUMERICA"),
        DESCRICAO_BEM("Descrição do Bem", "DESCRICAOBEM"),
        ESTADO_BEM("Estado do Bem", "ESTADOCONSERVACAOBEM"),
        FORMA_AQUISICAO("Forma de Aquisição", "FORMA_AQUISICAO"),
        DATA_AQUISICAO("Data de Aquisição", "DATAAQUISICAO"),
        NUMERO_NOTA("Nº da nota", "NOTAFISCAL"),
        NUMERO_EMPENHO("Nº do Empenho", "NOTAEMPENHO"),
        VALOR_BEM("Valor do Bem", "VALOR_AQUISICAO");
        private String descricao;
        private String coluna;

        private TipoOrdenacaoRelatorioLevantamentoBem(String descricao, String coluna) {
            this.descricao = descricao;
            this.coluna = coluna;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getColuna() {
            return coluna;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public class RelatorioValoresLevantamentoBem extends AbstractReport {
        private HierarquiaOrganizacional ho;
        private Boolean detalhar;
        private Boolean grupoNivelDois;
        private Boolean unidadesHierarquicas;

        public void limparCampos() {
            ho = new HierarquiaOrganizacional();
            detalhar = Boolean.FALSE;
            grupoNivelDois = Boolean.FALSE;
            unidadesHierarquicas = Boolean.FALSE;
        }

        public void gerarRelatorio(String tipoRelatorioExtensao) {
            try {
                validarHo();
                RelatorioDTO dto = new RelatorioDTO();
                dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
                nomeRelatorio = "RELATÓRIO DE BENS PATRIMONIAIS";
                dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
                dto.setNomeParametroBrasao("BRASAO");
                dto.adicionarParametro("CONSOLIDADO", detalhar ? 1 : 0);
                dto.adicionarParametro("GRUPO_NIVEL_2", grupoNivelDois ? 1 : 0);
                dto.adicionarParametro("UNIDADE_HIERARQUICA", unidadesHierarquicas ? 1 : 0);
                dto.adicionarParametro("DESCRICAO_HIERARQUIA", ho.getDescricao());
                dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(facade.getSistemaFacade().getDataOperacao()));
                dto.adicionarParametro("CRITERIOS", getCriteriosUtilizados());
                dto.adicionarParametro("CODIGO_HIERARQUIA", ho.getCodigo());
                dto.adicionarParametro("CODIGO_HIERARQUIA_LIKE", ho.getCodigoSemZerosFinais() + "%");
                dto.adicionarParametro("arquivoJrxml", getArquivoJrxml());
                dto.setNomeRelatorio(nomeRelatorio);
                dto.setApi("administrativo/levantamento-bens-patrimoniais/");
                ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            } catch (WebReportRelatorioExistenteException e) {
                ReportService.getInstance().abrirDialogConfirmar(e);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } catch (Exception ex) {
                logger.error("Erro ao gerar relatório: " + ex);
                FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            }
        }

        private void validarHo() {
            ValidacaoException ve = new ValidacaoException();
            if (ho == null || ho.getId() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa deve ser informado.");
            }
            ve.lancarException();
        }

        public String getCriteriosUtilizados() {
            String retorno = "Unidade Organizacional: " + ho.getCodigo() + " " + ho.getDescricao();
            retorno += " Consolidar: " + Util.converterBooleanSimOuNao(detalhar) + "          Agrupar por Grupo de Objeto de Compra de nível 2: " + Util.converterBooleanSimOuNao(grupoNivelDois) + "          Exibir unidades organizacionais hierárquicamente: " + Util.converterBooleanSimOuNao(unidadesHierarquicas);

            return retorno;
        }

        public String getArquivoJrxml() {

            if (!grupoNivelDois && !unidadesHierarquicas) {
                nomeRelatorio += " POR GRUPO OBJETO COMPRA FOLHA";
                return "BensPatrimoniaisPorGrupoObjetoCompraFolha.jrxml";
            }

            if (grupoNivelDois && !unidadesHierarquicas) {
                nomeRelatorio += " POR GRUPO OBJETO COMPRA NÍVEL DOIS";
                return "BensPatrimoniaisPorGrupoObjetoCompraNivelDois.jrxml";
            }

            if (!detalhar && !grupoNivelDois && unidadesHierarquicas) {
                nomeRelatorio += " POR UNIDADES HIERARQUICAS E GRUPO OBJETO COMPRA FOLHA";
                return "BensPatrimoniaisPorUnidadesHierarquicasEGrupoObjetoCompraFolha.jrxml";
            }

            if (detalhar && !grupoNivelDois && unidadesHierarquicas) {
                nomeRelatorio += " POR UNIDADES HIERARQUICAS E GRUPO OBJETO COMPRA FOLHA CONSOLIDADO";
                return "BensPatrimoniaisPorUnidadesHierarquicasEGrupoObjetoCompraFolhaCONSOLIDADO.jrxml";
            }

            if (!detalhar && grupoNivelDois && unidadesHierarquicas) {
                nomeRelatorio += " POR UNIDADES HIERARQUICAS E GRUPO OBJETO COMPRA NÍVEL DOIS";
                return "BensPatrimoniaisPorUnidadesHierarquicasEGrupoObjetoCompraNivelDois.jrxml";
            }

            if (detalhar && grupoNivelDois && unidadesHierarquicas) {
                nomeRelatorio += " POR UNIDADES HIERARQUICAS E GRUPO OBJETO COMPRA NÍVEL DOIS CONSOLIDADO";
                return "BensPatrimoniaisPorUnidadesHierarquicasEGrupoObjetoCompraNivelDoisCONSOLIDADO.jrxml";
            }

            throw new RuntimeException("Nenhum report encontrado!");
        }

        public HierarquiaOrganizacional getHo() {
            return ho;
        }

        public void setHo(HierarquiaOrganizacional ho) {
            this.ho = ho;
        }

        public Boolean getDetalhar() {
            return detalhar;
        }

        public void setDetalhar(Boolean detalhar) {
            this.detalhar = detalhar;
        }

        public Boolean getGrupoNivelDois() {
            return grupoNivelDois;
        }

        public void setGrupoNivelDois(Boolean grupoNivelDois) {
            this.grupoNivelDois = grupoNivelDois;
        }

        public Boolean getUnidadesHierarquicas() {
            return unidadesHierarquicas;
        }

        public void setUnidadesHierarquicas(Boolean unidadesHierarquicas) {
            this.unidadesHierarquicas = unidadesHierarquicas;
        }
    }

    public class RelatorioLevantamentoBemCodigoRepetido extends AbstractReport {
        private HierarquiaOrganizacional ho;
        private Entidade entidade;
        private ObjetoCompra objetoCompra;

        public void limparCampos() {
            ho = null;
            objetoCompra = null;
            entidade = null;
        }

        public void gerarRelatorio(String tipoRelatorioExtensao) {
            try {
                validarCampos();
                RelatorioDTO dto = new RelatorioDTO();
                dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
                dto.setNomeParametroBrasao("BRASAO");
                dto.adicionarParametro("dataOperacao", DataUtil.dataSemHorario(facade.getSistemaFacade().getDataOperacao()));
                dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getLogin(), true);
                dto.adicionarParametro("SECRETARIA", (ho != null ? ho.getDescricao().toUpperCase().trim() : entidade != null ? entidade.getNome().toUpperCase().trim() : ""));
                dto.adicionarParametro("entidadeId", (entidade != null ? entidade.getId() : (ho != null ? facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(ho.getSubordinada()).getId() : null)));
                dto.adicionarParametro("clausulaWhere", getClausulaWhere());
                dto.setNomeRelatorio("Levantamento de Bens com Código do Patrimônio Repetido");
                dto.setApi("administrativo/levantamento-bens-codigo-repetido/");
                ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            } catch (WebReportRelatorioExistenteException e) {
                ReportService.getInstance().abrirDialogConfirmar(e);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            }
        }

        private void validarCampos() {
            ValidacaoException ve = new ValidacaoException();
            if (entidade == null && ho == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Selecione ao menos uma Unidade Organizacional ou Entidade.");
            }
            ve.lancarException();
        }

        private String getClausulaWhere() {
            String where = "";
            if (ho != null && ho.getSubordinada().getId() != null) {
                where += " AND VW.SUBORDINADA_ID = " + ho.getSubordinada().getId() + " ";
            }

            if (entidade != null && entidade.getId() != null) {
                where += " AND VW.ENTIDADE_ID = " + entidade.getId() + " ";
            }

            if (objetoCompra != null && objetoCompra.getId() != null) {
                where += " AND OBJ.ID = " + objetoCompra.getId() + " ";
            }
            return where;
        }

        public HierarquiaOrganizacional getHo() {
            return ho;
        }

        public void setHo(HierarquiaOrganizacional ho) {
            this.ho = ho;
        }

        public ObjetoCompra getObjetoCompra() {
            return objetoCompra;
        }

        public void setObjetoCompra(ObjetoCompra objetoCompra) {
            this.objetoCompra = objetoCompra;
        }

        public Entidade getEntidade() {
            return entidade;
        }

        public void setEntidade(Entidade entidade) {
            this.entidade = entidade;
        }

        public List<Entidade> completaEntidadeOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
            return facade.getEntidadeFacade().getEntidadeOndeUsuarioEhGestorPatrimonio(parte, facade.getSistemaFacade().getDataOperacao(), facade.getSistemaFacade().getUsuarioCorrente());
        }

        public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdministrativaOndeUsuarioLogadoEhGestorPatrimonioPorEntidade(String parte) {
            return facade.getHierarquiaOrganizacionalAdministrativaOndeUsuarioEhGestorPatrimonioPorEntidade(parte, null, facade.getSistemaFacade().getDataOperacao(), facade.getSistemaFacade().getUsuarioCorrente(), getEntidade());
        }
    }
}
