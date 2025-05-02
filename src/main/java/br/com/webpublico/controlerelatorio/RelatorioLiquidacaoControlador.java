/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.ComponenteTreeDespesaORC;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.LiquidacaoFacade;
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

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * @author major
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-liquidacao", pattern = "/relatorio/liquidacao/", viewId = "/faces/financeiro/relatorio/relatoriofiltroliquidacao.xhtml")})
public class RelatorioLiquidacaoControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    private Conta contaDespesa;
    private Empenho empenho;
    private TipoRelatorio tipoRelatorio;
    @ManagedProperty(name = "componenteTreeDespesaORC", value = "#{componente}")
    private ComponenteTreeDespesaORC componenteTreeDespesaORC;
    private GeracaoRelatorio geracaoRelatorio;

    @URLAction(mappingId = "novo-relatorio-liquidacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        empenho = null;
        exercicio = getExercicioCorrente();
        tipoRelatorio = TipoRelatorio.RESUMIDO;
        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(new DespesaORC());
        componenteTreeDespesaORC.setDespesaSTR("");
        tipoLancamento = null;
        dataInicial = DataUtil.montaData(1, 0, getSistemaFacade().getExercicioCorrente().getAno()).getTime();
    }

    public List<SelectItem> getTiposRelatorios() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorio.values());
    }

    public List<SelectItem> getGeracoesDoRelatorio() {
        return Util.getListSelectItemSemCampoVazio(GeracaoRelatorio.values());
    }

    public List<SelectItem> getTiposLancamento() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, "Todos"));
        for (TipoLancamento lancamento : TipoLancamento.values()) {
            retorno.add(new SelectItem(lancamento, lancamento.getDescricao()));
        }
        return retorno;
    }

    public List<ClasseCredor> completarClasseCredor(String parte) {
        if (pessoa != null) {
            return relatorioContabilSuperFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), getPessoa());
        } else {
            return relatorioContabilSuperFacade.getClasseCredorFacade().listaFiltrandoDescricao(parte.trim());
        }
    }

    public List<Empenho> completarEmpenhos(String parte) {
        return liquidacaoFacade.getEmpenhoFacade().buscarPorNumeroAndPessoaRelatorio(parte.trim(), buscarExercicioPelaDataFinal().getAno());
    }

    public List<Pessoa> completarFornecedores(String parte) {
        return relatorioContabilSuperFacade.getPessoaFacade().listaTodasPessoasPorId(parte.trim());
    }

    public List<Conta> completarContasDeDestinacao(String filtro) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(filtro, buscarExercicioPelaDataFinal());
    }

    public List<Conta> completarContas(String parte) {
        Exercicio exDataFinal = buscarExercicioPelaDataFinal();
        if (componenteTreeDespesaORC != null &&
            componenteTreeDespesaORC.getDespesaORCSelecionada() != null &&
            componenteTreeDespesaORC.getDespesaORCSelecionada().getId() != null) {
            return relatorioContabilSuperFacade.getContaFacade().buscarContasFilhasDespesaORCPorTipo(
                parte.trim(),
                componenteTreeDespesaORC.getDespesaORCSelecionada().getProvisaoPPADespesa().getContaDeDespesa(),
                exDataFinal,
                null,
                false);
        } else {
            return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), exDataFinal);
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("USUARIO", relatorioContabilSuperFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("EXERCICIO", DataUtil.getAno(dataFinal).toString());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("apresentacao", apresentacao.name());
            dto.adicionarParametro("NOME_RELATORIO", "Relatório de " + geracaoRelatorio.getDescricao());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio("Relatório de " + geracaoRelatorio.getDescricao());
            dto.setApi(TipoRelatorio.RESUMIDO.equals(tipoRelatorio) ? "contabil/liquidacao/" : "contabil/liquidacao/detalhado/");
            ParametrosRelatorios.parametrosToDto(montarParametros());
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public String getNomeRelatorio() {
        if (TipoRelatorio.RESUMIDO.equals(tipoRelatorio)) {
            return "RELATORIO-LIQUIDACAO-RESUMIDO-" + DataUtil.getAno(dataInicial) + (DataUtil.getMes(dataInicial) < 10 ? "0" + DataUtil.getMes(dataInicial) : DataUtil.getMes(dataInicial))
                + "A" + DataUtil.getAno(dataFinal) + (DataUtil.getMes(dataFinal) < 10 ? "0" + DataUtil.getMes(dataFinal) : DataUtil.getMes(dataFinal));
        } else {
            return "RELATORIO-LIQUIDACAO-DETALHADO-" + DataUtil.getAno(dataInicial) + (DataUtil.getMes(dataInicial) < 10 ? "0" + DataUtil.getMes(dataInicial) : DataUtil.getMes(dataInicial))
                + "A" + DataUtil.getAno(dataFinal) + (DataUtil.getMes(dataFinal) < 10 ? "0" + DataUtil.getMes(dataFinal) : DataUtil.getMes(dataFinal));
        }
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));

        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";

        if (empenho != null) {
            parametros.add(new ParametrosRelatorios(" EMP.ID ", ":empId", null, OperacaoRelatorio.IGUAL, empenho.getId(), null, 1, false));
            filtros += " Empenho:  " + empenho.getNumero() + " -";
        }

        DespesaORC desp = componenteTreeDespesaORC.getDespesaORCSelecionada();
        if (desp != null && desp.getId() != null) {
            parametros.add(new ParametrosRelatorios(" DESP.ID ", ":despesaId", null, OperacaoRelatorio.IGUAL, desp.getId(), null, 1, false));
            filtros += " Despesa Orçamentária: " + desp.getCodigo() + " -";
        }

        if (contaDespesa != null) {
            parametros.add(new ParametrosRelatorios(" contaDesdob.codigo ", ":cCodigo", null, OperacaoRelatorio.LIKE, contaDespesa.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta de Despesa: " + contaDespesa.getCodigo() + " -";
        }

        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" P.ID ", ":fornecedorId", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }

        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" emp.classecredor_id ", ":classeCredorID", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe Credor: " + classeCredor.getDescricao() + " -";
        }

        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" cd.codigo ", ":contaDest", null, OperacaoRelatorio.LIKE, conta.getCodigo(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + conta.getCodigo() + " -";
        }

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.codigo ", ":codigoFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " -";
        }

        if (tipoLancamento != null) {
            parametros.add(new ParametrosRelatorios(" tipoLancamento ", ":tipoLancamento", null, OperacaoRelatorio.IGUAL, tipoLancamento.getAbreviacao(), null, 2, false));
            filtros += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + " -";
        }

        if (!listaUnidades.isEmpty()) {
            List<Long> idsUnidades = Lists.newArrayList();
            String unidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : listaUnidades) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                unidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;

            parametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), buscarExercicioPelaDataFinal(), getSistemaFacade().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            List<Long> idsUnidades = Lists.newArrayList();
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getCodigo(), null, 1, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }

        if (unidadeGestora != null || apresentacao.isApresentacaoUnidadeGestora()) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
        }

        if (GeracaoRelatorio.DO_EXERCICIO.equals(geracaoRelatorio)) {
            parametros.add(new ParametrosRelatorios(" l.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.NORMAL.name(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" emp.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.NORMAL.name(), null, 1, false));
        } else if (GeracaoRelatorio.RESTO.equals(geracaoRelatorio)) {
            parametros.add(new ParametrosRelatorios(" l.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.RESTO.name(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" emp.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.RESTO.name(), null, 1, false));
        }

        atualizaFiltrosGerais();
        return parametros;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public ComponenteTreeDespesaORC getComponenteTreeDespesaORC() {
        return componenteTreeDespesaORC;
    }

    public void setComponenteTreeDespesaORC(ComponenteTreeDespesaORC componenteTreeDespesaORC) {
        this.componenteTreeDespesaORC = componenteTreeDespesaORC;
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


    private enum TipoRelatorio {

        DETALHE("Detalhado"),
        RESUMIDO("Resumido");

        private String descricao;

        TipoRelatorio(String descricao) {
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

    private enum GeracaoRelatorio {

        DO_EXERCICIO_E_RESTO("Liquidação do Exercício e de Restos a Pagar"),
        DO_EXERCICIO("Liquidação do Exercício"),
        RESTO("Liquidação de Restos a Pagar");

        private String descricao;

        GeracaoRelatorio(String descricao) {
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
}
