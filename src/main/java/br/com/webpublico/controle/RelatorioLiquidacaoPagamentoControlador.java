package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioContabilSuperControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContaFacade;
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
import java.util.Date;
import java.util.List;

/**
 * Created by Mateus on 01/04/2015.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-liquidacao-pagamento", pattern = "/relatorio/liquidacao-pagamento/", viewId = "/faces/financeiro/relatorio/relatorioliquidacaopagamento.xhtml")})
@ManagedBean
public class RelatorioLiquidacaoPagamentoControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private ContaFacade contaFacade;
    private Conta contaDespesa;
    private GeracaoRelatorio geracaoRelatorio;
    private Conta contaDeDestinacao;

    public RelatorioLiquidacaoPagamentoControlador() {
    }

    @URLAction(mappingId = "novo-relatorio-liquidacao-pagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.limparCamposGeral();
        this.dataReferencia = new Date();
    }

    public List<Conta> completarContas(String parte) {
        return contaFacade.listaFiltrandoDespesaCriteria(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<Conta> completarContasDeDestinacao(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(parte.trim(), buscarExercicioPelaDataDeReferencia());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDataDeReferencia();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("MODULO", "Execução Orçamentária");
            dto.adicionarParametro("NOME_RELATORIO", "Relatório de " + geracaoRelatorio.getDescricao());
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/liquidacao-pagamento/");
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
        filtrosParametrosGerais(parametros);
        return parametros;
    }

    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, "01/01/" + DataUtil.getAno(dataReferencia), DataUtil.getDataFormatada(dataReferencia), 0, true));
        filtros = "Data de Referência: " + getDataReferenciaFormatada() + " -";

        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" cc.ID ", ":classeId", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe:  " + classeCredor.getCodigo() + " -";
        }

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fr.codigo ", ":fonteCodigo", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso:  " + fonteDeRecursos.getCodigo() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cdest.ID ", ":cdestId", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }

        if (contaDespesa != null) {
            parametros.add(new ParametrosRelatorios(" con.codigo ", ":cCodigo", null, OperacaoRelatorio.IGUAL, contaDespesa.getCodigo(), null, 1, false));
            filtros += " Conta de Despesa: " + contaDespesa.getCodigo() + " -";
        }
        if (eventoContabil != null) {
            parametros.add(new ParametrosRelatorios(" ec.id ", ":idEvento", null, OperacaoRelatorio.IGUAL, eventoContabil.getId(), null, 1, false));
            filtros += " Evento: " + eventoContabil.getCodigo() + " -";
        }

        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, unidadeGestora.getId(), null, 1, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }

        if (GeracaoRelatorio.DO_EXERCICIO.equals(geracaoRelatorio)) {
            parametros.add(new ParametrosRelatorios(" liq.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.NORMAL.name(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" emp.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.NORMAL.name(), null, 1, false));
        } else if (GeracaoRelatorio.RESTO.equals(geracaoRelatorio)) {
            parametros.add(new ParametrosRelatorios(" liq.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.RESTO.name(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" emp.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.RESTO.name(), null, 1, false));
        }

        Exercicio e = getExercicioFacade().getExercicioPorAno(DataUtil.getAno(dataReferencia));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, e.getId(), null, 0, false));

        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    public List<SelectItem> getTipoGeracaoRelatorio() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (GeracaoRelatorio item : GeracaoRelatorio.values()) {
            toReturn.add(new SelectItem(item, item.getDescricao()));
        }
        return toReturn;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        return relatorioContabilSuperFacade.getEventoContabilFacade().listaFiltrando(parte.trim(), "descricao", "codigo");
    }

    @Override
    public String getNomeRelatorio() {
        return "LIQUIDACAO-PAGAMENTO-DESPESA-E-RESTO-PAGAR";
    }

    public enum GeracaoRelatorio {

        DO_EXERCICIO_E_RESTO("Liquidação e Pagamento do Exercício e de Restos a Pagar não Processados"),
        DO_EXERCICIO("Liquidação e Pagamento do Exercício"),
        RESTO("Liquidação e Pagamento de Restos a Pagar não Processados");
        private String descricao;

        GeracaoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }


    public GeracaoRelatorio getGeracaoRelatorio() {
        return geracaoRelatorio;
    }

    public void setGeracaoRelatorio(GeracaoRelatorio geracaoRelatorio) {
        this.geracaoRelatorio = geracaoRelatorio;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}

