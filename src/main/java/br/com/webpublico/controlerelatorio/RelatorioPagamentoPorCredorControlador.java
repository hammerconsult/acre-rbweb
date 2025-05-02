package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ClasseCredor;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaDespesa;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContaFacade;
import br.com.webpublico.negocios.FonteDeRecursosFacade;
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
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-pagamento-credor", pattern = "/relatorio/pagamento-por-credor/", viewId = "/faces/financeiro/relatorio/relatoriopagamentoporcredor.xhtml")
})
@ManagedBean
public class RelatorioPagamentoPorCredorControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private ContaFacade contaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    private ContaDespesa contaDespesa;
    private GeracaoRelatorio geracaoRelatorio;
    private TipoLancamento tipo;

    public RelatorioPagamentoPorCredorControlador() {
    }

    public List<SelectItem>  getTiposLancamento() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, "Todos"));
        for (TipoLancamento lancamento : TipoLancamento.values()) {
            retorno.add(new SelectItem(lancamento, lancamento.getDescricao()));
        }
        return retorno;
    }

    public List<Conta> buscarContasDeDespesa(String parte) {
        return contaFacade.listaFiltrandoDespesaCriteria(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<ClasseCredor> buscarClassesCredores(String filtro) {
        return relatorioContabilSuperFacade.getClasseCredorFacade().listaFiltrandoDescricao(filtro.trim());
    }

    public List<Conta> buscarContasDeDestinacao(String parte) {
        return contaFacade.buscarContasDeDestinacaoDeRecursos(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<FonteDeRecursos> buscarFontesDeRecurso(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte, getSistemaControlador().getExercicioCorrente());
    }

    @URLAction(mappingId = "relatorio-pagamento-credor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        pessoa = null;
        contaDespesa = null;
        geracaoRelatorio = GeracaoRelatorio.DO_EXERCICIO_E_RESTO;
    }

    public List<SelectItem> getGeracoes() {
        return Util.getListSelectItemSemCampoVazio(GeracaoRelatorio.values());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            filtros = getFiltrosPeriodo() + filtros;
            dto.adicionarParametro("FILTRO", atualizaFiltrosGerais());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/pagamento-por-credor/");
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
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        filtros = "";
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.codigo ", ":fonteCodigo", null, OperacaoRelatorio.LIKE, this.fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" contad.id ", ":contaDest", null, OperacaoRelatorio.IGUAL, conta.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + conta.getCodigo() + " -";
        }
        parametros = filtrosParametrosUnidade(parametros);

        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" p.id ", ":fornecedorId", null, OperacaoRelatorio.IGUAL, this.pessoa.getId(), null, 1, false));
            filtros += " Credor: " + pessoa.getCpf_Cnpj() + " - " + pessoa.getNome() + " -";
        }
        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" cc.id ", ":classeId", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe de Pessoa: " + classeCredor.getCodigo() + " -";
        }
        if (contaBancariaEntidade != null) {
            parametros.add(new ParametrosRelatorios(" CBE.ID ", ":cbeId", null, OperacaoRelatorio.IGUAL, this.contaBancariaEntidade.getId(), null, 1, false));
            filtros += " Conta Bancária: " + contaBancariaEntidade.getNumeroConta() + " - " + contaBancariaEntidade.getDigitoVerificador() + " -";
        }
        if (contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" sc.id ", ":subId", null, OperacaoRelatorio.IGUAL, this.contaFinanceira.getId(), null, 1, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo() + " -";
        }
        if (contaDespesa != null) {
            parametros.add(new ParametrosRelatorios(" con.ID ", ":contaId", null, OperacaoRelatorio.IGUAL, contaDespesa.getId(), null, 1, false));
            filtros += " Conta de Despesa: " + contaDespesa.getCodigo() + " -";
        }
        if (tipo != null) {
            parametros.add(new ParametrosRelatorios(" tipoLancamento ", ":tipoLanc", null, OperacaoRelatorio.LIKE, tipo.getFiltro(), null, 2, false));
            filtros += " Tipo de Lançamento: " + tipo.getDescricao() + " -";
        }
        if (GeracaoRelatorio.DO_EXERCICIO.equals(geracaoRelatorio)) {
            parametros.add(new ParametrosRelatorios(" pgto.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.NORMAL.name(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" emp.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.NORMAL.name(), null, 1, false));
        } else if (GeracaoRelatorio.RESTO.equals(geracaoRelatorio)) {
            parametros.add(new ParametrosRelatorios(" pgto.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.RESTO.name(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" emp.CATEGORIAORCAMENTARIA ", ":categoria", null, OperacaoRelatorio.IGUAL, CategoriaOrcamentaria.RESTO.name(), null, 1, false));
        }
        return parametros;
    }

    @Override
    public String getNomeRelatorio() {
        return "PAGAMENTO-POR-CREDOR";
    }

    public ContaDespesa getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(ContaDespesa contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public GeracaoRelatorio getGeracaoRelatorio() {
        return geracaoRelatorio;
    }

    public void setGeracaoRelatorio(GeracaoRelatorio geracaoRelatorio) {
        this.geracaoRelatorio = geracaoRelatorio;
    }

    public void recuperaContaBancariaApartirDaContaFinanceira() {
        fonteDeRecursos = null;
        if (contaFinanceira != null) {
            contaBancariaEntidade = contaFinanceira.getContaBancariaEntidade();
        } else {
            contaBancariaEntidade = null;
        }
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoLancamento tipo) {
        this.tipo = tipo;
    }

    private enum TipoLancamento {
        NORMAL("Normal", "N"),
        ESTORNO("Estorno", "E");

        private String descricao;
        private String filtro;

        TipoLancamento(String descricao, String filtro) {
            this.descricao = descricao;
            this.filtro = filtro;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getFiltro() {
            return filtro;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    private enum GeracaoRelatorio {
        DO_EXERCICIO_E_RESTO("Pagamento do Exercício e de Restos a Pagar"),
        DO_EXERCICIO("Pagamento do Exercício"),
        RESTO("Pagamento de Restos a Pagar");

        private String descricao;

        GeracaoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao ;
        }
    }
}
