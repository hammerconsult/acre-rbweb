package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.AlteracaoORCFacade;
import br.com.webpublico.negocios.AtoLegalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 10/01/14
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-credito-adicional", pattern = "/relatorio/credito-adicional/", viewId = "/faces/financeiro/relatorio/relatorio-credito-adicional.xhtml")
})
@ManagedBean
public class RelatorioCreditoAdicionalControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private AlteracaoORCFacade alteracaoORCFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private AtoLegal atoLegal;
    private Conta contaDespesa;
    private FonteDeRecursos fonteDeRecursos;
    private TipoDespesaORC tiposCredito;
    private OrigemSuplementacaoORC origemRecurso;
    private Ordenacao ordenacao;
    private TipoAtoLegal tipoAtoLegal;

    @URLAction(mappingId = "relatorio-credito-adicional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        ordenacao = Ordenacao.DECRETO;
        atoLegal = null;
        contaDespesa = null;
        fonteDeRecursos = null;
        tiposCredito = null;
        origemRecurso = null;
    }

    public void gerarRelatorio(String tipoExtensaoRelatorio) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensaoRelatorio));
            dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("FILTRO_DATA", getFiltrosDatas());
            dto.adicionarParametro("FILTRO_DATAREFERENCIA", getDataReferenciaFormatada());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("orderBy", ordernarRelatorio());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
            if (unidadeGestora != null) {
                dto.adicionarParametro("FILTRO_UG", filtroUG);
            }
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/credito-adicional/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        EsferaDoPoder esferaDoPoder = alteracaoORCFacade.recuperarEsferaPoderPorUnidade(getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente());
        if (esferaDoPoder != null) {
            return atoLegalFacade.buscarAtoLegalTipoDecretoResolucaoPorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente(), esferaDoPoder, Boolean.FALSE);
        }
        return new ArrayList<>();
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        filtrosParametros(parametros);
        filtrosParametrosUnidade(parametros);
        filtrosParametrosGerais(parametros);
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
    }

    private String ordernarRelatorio() {
        String ordernar = " ";
        if (ordenacao.equals(Ordenacao.DECRETO)) {
            ordernar = " ORDER BY to_number(NUMERO_DECRETO), DATA_DECRETO, orgao, unidade, DOTACAO, conta_despesa ";
        } else {
            ordernar = " ORDER BY to_number(NUMERO), DATA_EFETIVACAO, orgao, unidade, DOTACAO, conta_despesa";
        }
        return ordernar;
    }

    private void filtrosParametros(List<ParametrosRelatorios> listaParametros) {
        listaParametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, getDataInicialFormatada(), null, 0, true));
        listaParametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, getDataFinalFormatada(), null, 0, true));
        filtros += getFiltrosPeriodo();
    }

    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> listaParametros) {
        if (atoLegal != null) {
            listaParametros.add(new ParametrosRelatorios(" ATO.NUMERO", ":codigoAto", null, OperacaoRelatorio.IGUAL, atoLegal.getNumero(), null, 1, false));
            filtros += " Ato Legal: " + atoLegal.getNumero() + " - " + DataUtil.getDataFormatada(atoLegal.getDataPublicacao()) + " -";
        }
        if (tipoAtoLegal != null) {
            listaParametros.add(new ParametrosRelatorios(" ATO.TIPOATOLEGAL ", ":tipoAtoLegal", null, OperacaoRelatorio.IGUAL, tipoAtoLegal.name(), null, 1, false));
            filtros += " Tipo de Ato Legal: " + tipoAtoLegal.getDescricao() + " -";
        }
        if (fonteDeRecursos != null) {
            listaParametros.add(new ParametrosRelatorios(" FONTE.CODIGO", ":codigoFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.toString().trim() + " -";
        }
        if (contaDespesa != null) {
            listaParametros.add(new ParametrosRelatorios(" C.CODIGO", ":codigoConta", null, OperacaoRelatorio.LIKE, contaDespesa.getCodigoSemZerosAoFinal() + ".%", null, 1, false));
            filtros += " Conta: " + contaDespesa + " -";
        }
        if (tiposCredito != null) {
            listaParametros.add(new ParametrosRelatorios(" SU.TIPODESPESAORC", ":tipoCredito", null, OperacaoRelatorio.IGUAL, tiposCredito.name(), null, 2, false));
            listaParametros.add(new ParametrosRelatorios(" ANUL.TIPODESPESAORC", ":tipoCredito", null, OperacaoRelatorio.IGUAL, tiposCredito.name(), null, 3, false));
            filtros += " Tipo de Crédito: " + tiposCredito.getDescricao() + " -";
        }
        if (origemRecurso != null) {
            listaParametros.add(new ParametrosRelatorios(" ORIGEM_RECURSO ", ":origemRecurso", null, OperacaoRelatorio.IGUAL, origemRecurso.name(), null, 4, false));
            filtros += " Origem de Recurso: " + origemRecurso.getDescricao() + " -";
        }
        filtros = atualizaFiltrosGerais();
        return listaParametros;
    }

    public List<SelectItem> getTiposCreditos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (TipoDespesaORC obj : TipoDespesaORC.values()) {
            if (!obj.equals(TipoDespesaORC.ORCAMENTARIA)) {
                toReturn.add(new SelectItem(obj, obj.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getOrdenacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Ordenacao obj : Ordenacao.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposAtos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Consolidado"));
        toReturn.add(new SelectItem(TipoAtoLegal.DECRETO, TipoAtoLegal.DECRETO.getDescricao()));
        toReturn.add(new SelectItem(TipoAtoLegal.RESOLUCAO, TipoAtoLegal.RESOLUCAO.getDescricao()));
        toReturn.add(new SelectItem(TipoAtoLegal.MEDIDA_PROVISORIA, TipoAtoLegal.MEDIDA_PROVISORIA.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> getOrigemRecursos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todas"));
        for (OrigemSuplementacaoORC obj : OrigemSuplementacaoORC.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<Conta> completaContaDespesa(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    private enum Ordenacao {
        CREDITOADICIONAL("Crédito Adicional"),
        DECRETO("Decreto");
        private String descricao;

        private Ordenacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    @Override
    public String getNomeRelatorio() {
        return "RELACAO-CREDITO-ADICIONAL";
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public TipoDespesaORC getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TipoDespesaORC tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    public OrigemSuplementacaoORC getOrigemRecurso() {
        return origemRecurso;
    }

    public void setOrigemRecurso(OrigemSuplementacaoORC origemRecurso) {
        this.origemRecurso = origemRecurso;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public Ordenacao getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(Ordenacao ordenacao) {
        this.ordenacao = ordenacao;
    }

    public TipoAtoLegal getTipoAtoLegal() {
        return tipoAtoLegal;
    }

    public void setTipoAtoLegal(TipoAtoLegal tipoAtoLegal) {
        this.tipoAtoLegal = tipoAtoLegal;
    }
}
