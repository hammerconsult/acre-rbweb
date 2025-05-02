package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 27/07/15
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-decreto-por-orgao", pattern = "/relatorio/credito-adicional/decreto-por-orgao/", viewId = "/faces/financeiro/relatorio/decreto-por-orgao.xhtml")
})
@ManagedBean
public class RelatorioDecretoPorOrgaoControlador extends RelatorioContabilSuperControlador implements Serializable {

    private AtoLegal atoLegal;
    private TipoOperacao tipoOperacao;
    private Ordenacao ordenacao;

    @URLAction(mappingId = "relatorio-decreto-por-orgao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        atoLegal = null;
        tipoOperacao = null;
    }

    public void gerarRelatorio(String tipoExtensaoRelatorio) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensaoRelatorio));
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("OPERACAO", tipoOperacao != null ? tipoOperacao.name() : null);
            dto.adicionarParametro("ordenacao", ordernarRelatorio());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            filtros = atualizaFiltrosGerais();
            dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
            if (unidadeGestora != null) {
                dto.adicionarParametro("FILTRO_UG", filtroUG);
            }
            dto.setNomeRelatorio("RELATORIO-DECRETO-POR-ORGAO");
            dto.setApi("contabil/decreto-por-orgao/");
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

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        filtrosParametrosUnidade(parametros);
        filtrosGerais(parametros);
        return parametros;
    }

    private String ordernarRelatorio() {
        return Ordenacao.DECRETO.equals(ordenacao) ? " order by to_number(numero_decreto), data_decreto, codigo_orgao, codigo_unidade, funcional, conta " :
            " order by to_number(numero), data_efetivacao, codigo_orgao, codigo_unidade, funcional, conta ";
    }

    private void filtrosGerais(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, getDataInicialFormatada(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, getDataFinalFormatada(), null, 0, true));
        filtros += getFiltrosPeriodo();

        if (atoLegal != null) {
            parametros.add(new ParametrosRelatorios(" ATO.NUMERO", ":codigoAto", null, OperacaoRelatorio.IGUAL, atoLegal.getNumero(), null, 1, false));
            filtros += " Ato Legal: " + atoLegal.getNumero() + " - " + DataUtil.getDataFormatada(atoLegal.getDataPublicacao()) + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" FONTE.CODIGO", ":codigoFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.toString().trim() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" C.CODIGO", ":codigoConta", null, OperacaoRelatorio.IGUAL, conta.getCodigo(), null, 1, false));
            filtros += " Conta: " + conta + " -";
        }
        if (tipoOperacao != null) {
            if (TipoOperacao.SUPLEMENTACAO.equals(tipoOperacao)) {
                parametros.add(new ParametrosRelatorios(" OPERACAO ", ":operacao", null, OperacaoRelatorio.LIKE, "Sup", null, 2, false));
            } else {
                parametros.add(new ParametrosRelatorios(" OPERACAO ", ":operacao", null, OperacaoRelatorio.LIKE, "Red", null, 2, false));
            }
            filtros += " Tipo de Operação: " + tipoOperacao.getDescricao() + " -";
        }
    }

    public List<SelectItem> getTipoOperacoes() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "Todas"));
        for (TipoOperacao obj : TipoOperacao.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getOrdenacoes() {
        return Util.getListSelectItemSemCampoVazio(Ordenacao.values());
    }

    public List<Conta> completarContasDeDespesa(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoDespesaCriteria(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    private enum Ordenacao {
        DECRETO("Decreto"),
        CREDITOADICIONAL("Crédito Adicional");

        private String descricao;

        Ordenacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    private enum TipoOperacao {
        SUPLEMENTACAO("Crédito Suplementar"),
        ANULACAO("Anulação de Dotação");
        private String descricao;

        TipoOperacao(String descricao) {
            this.descricao = descricao;
        }

        private String getDescricao() {
            return descricao;
        }
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-DECRETO-POR-ORGAO";
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public Ordenacao getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(Ordenacao ordenacao) {
        this.ordenacao = ordenacao;
    }
}
