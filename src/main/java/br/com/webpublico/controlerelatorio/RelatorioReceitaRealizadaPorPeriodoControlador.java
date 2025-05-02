package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mateus on 24/08/17.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-receita-realizada-por-periodo", pattern = "/relatorio/receita-realizada-por-periodo/", viewId = "/faces/financeiro/relatorio/relatorioreceitarealizadaporperiodo.xhtml"),
    @URLMapping(id = "relatorio-receita-realizada-por-competencia", pattern = "/relatorio/receita-realizada-por-competencia/", viewId = "/faces/financeiro/relatorio/relatorioreceitarealizadaporcompetencia.xhtml"),
    @URLMapping(id = "relatorio-receita-realizada-por-pessoa", pattern = "/relatorio/receita-realizada-por-pessoa/", viewId = "/faces/financeiro/relatorio/relatorioreceitarealizadaporpessoa.xhtml")
})
public class RelatorioReceitaRealizadaPorPeriodoControlador extends RelatorioContabilSuperControlador {

    private Date dataCompetencia;
    private TipoRelatorioReceitaRealizada tipoRelatorioReceitaRealizada;
    private Conta contaDeDestinacao;

    @URLAction(mappingId = "relatorio-receita-realizada-por-periodo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposPeriodo() {
        super.limparCamposGeral();
        tipoRelatorioReceitaRealizada = TipoRelatorioReceitaRealizada.RELATORIO_RECEITA_REALIZADA_POR_PERIODO;
        dataCompetencia = null;
        mes = null;
        dataReferencia = null;
    }

    @URLAction(mappingId = "relatorio-receita-realizada-por-competencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposCompetencia() {
        super.limparCamposGeral();
        tipoRelatorioReceitaRealizada = TipoRelatorioReceitaRealizada.RELATORIO_RECEITA_REALIZADA_POR_COMPETENCIA;
        dataCompetencia = new Date();
        mes = null;
        dataReferencia = null;
    }

    @URLAction(mappingId = "relatorio-receita-realizada-por-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposPessoa() {
        super.limparCamposGeral();
        tipoRelatorioReceitaRealizada = TipoRelatorioReceitaRealizada.RELATORIO_RECEITA_REALIZADA_POR_PESSOA;
        dataCompetencia = null;
        mes = null;
        dataReferencia = null;
    }

    public List<Conta> completarContasDeReceita(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoReceita(parte.trim(), getExercicio());
    }

    public List<Conta> completarContaDeDestinacao(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("orderBy", tipoRelatorioReceitaRealizada.getOrderBy());
            dto.adicionarParametro("arquivoJrxml", tipoRelatorioReceitaRealizada.getArquivoJrxml());
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/receita-realizada-por-periodo/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    @Override
    public void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        verificarCampoNull(ve, dataInicial, "O campo Data Inicial deve ser informado.");
        verificarCampoNull(ve, dataFinal, "O campo Data Final deve ser informado.");
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Data Final deve ser maior que a Data Inicial.");
        }
        verificarDataEmExercicioDiferente(ve, dataInicial, dataFinal, " As datas estão com exercícios diferentes.");
        ve.lancarException();
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        filtros = getFiltrosPeriodo();
        if (dataCompetencia != null) {
            parametros.add(new ParametrosRelatorios(" to_char(lanc.dataReferencia, 'MM/yyyy') ", ":competencia", null, OperacaoRelatorio.IGUAL, Util.formatterMesAno.format(dataCompetencia), null, 2, false));
            parametros.add(new ParametrosRelatorios(" to_char(rre.dataReferencia, 'MM/yyyy') ", ":competencia", null, OperacaoRelatorio.IGUAL, Util.formatterMesAno.format(dataCompetencia), null, 3, false));
            filtros += " Competência: " + Util.formatterMesAno.format(dataCompetencia) + " -";
        }
        if (this.contaBancariaEntidade != null) {
            parametros.add(new ParametrosRelatorios(" cbe.ID ", ":cbeId", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 1, false));
            filtros += " Conta Bancária: " + contaBancariaEntidade.getAgencia().getBanco().getNumeroBanco() + " AG: " + contaBancariaEntidade.getAgencia().getNumeroAgencia() + "-" + contaBancariaEntidade.getAgencia().getDigitoVerificador() + " C/C: " + contaBancariaEntidade.getNumeroConta() + "-" + contaBancariaEntidade.getDigitoVerificador() + " -";
        }
        if (this.contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" SUB.CODIGO ", ":codigo", null, OperacaoRelatorio.IGUAL, contaFinanceira.getCodigo(), null, 1, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo() + " -";
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" p.id ", ":pessoaId", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getCpf_Cnpj() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":conta", null, OperacaoRelatorio.LIKE, conta.getCodigo(), null, 1, false));
            filtros += " Conta de Receita: " + conta.getCodigo() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" f.id", ":fonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.toString() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cd.id", ":cdest", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }
        List<Long> idsUnidades = new ArrayList<>();
        if (!listaUnidades.isEmpty()) {
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            filtros += " Unidade(s): " + unidades;
        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), getExercicio(), getDataFiltro(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : unidadesDoUsuario) {
                idsUnidades.add(lista.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        } else {
            parametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getCodigo(), null, 1, false));
            adicionarExercicio(parametros);
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        if (ApresentacaoRelatorio.UNIDADE_GESTORA.equals(apresentacao)) {
            adicionarExercicio(parametros);
        }
        atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getExercicio().getId(), null, 0, false));
    }

    @Override
    public String getNomeRelatorio() {
        return tipoRelatorioReceitaRealizada.getNomeRelatorio();
    }

    public Exercicio getExercicio() {
        return dataFinal != null ? relatorioContabilSuperFacade.getExercicioFacade().getExercicioPorAno(DataUtil.getAno(dataFinal)) : getSistemaFacade().getExercicioCorrente();
    }

    public Date getDataFiltro() {
        return dataFinal != null ? dataFinal : getSistemaFacade().getDataOperacao();
    }

    public TipoRelatorioReceitaRealizada getTipoRelatorioReceitaRealizada() {
        return tipoRelatorioReceitaRealizada;
    }

    public void setTipoRelatorioReceitaRealizada(TipoRelatorioReceitaRealizada tipoRelatorioReceitaRealizada) {
        this.tipoRelatorioReceitaRealizada = tipoRelatorioReceitaRealizada;
    }

    public Date getDataCompetencia() {
        return dataCompetencia;
    }

    public void setDataCompetencia(Date dataCompetencia) {
        this.dataCompetencia = dataCompetencia;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public enum TipoRelatorioReceitaRealizada {
        RELATORIO_RECEITA_REALIZADA_POR_PERIODO("RELATORIO-RECEITA-REALIZADA-POR-PERIODO", "RelatorioReceitaRealizadaPorPeriodo.jrxml", " codigoConta, datalancamento, NUMERO "),
        RELATORIO_RECEITA_REALIZADA_POR_COMPETENCIA("RELATORIO-RECEITA-REALIZADA-POR-COMPETENCIA", "RelatorioReceitaRealizadaPorCompetencia.jrxml", " to_char(dataReferencia, 'MM/yyyy'), codigoConta, datalancamento, NUMERO "),
        RELATORIO_RECEITA_REALIZADA_POR_PESSOA("RELATORIO-RECEITA-REALIZADA-POR-PESSOA", "RelatorioReceitaRealizadaPorPessoa.jrxml", " codigoConta, PESSOA, datalancamento, NUMERO ");

        private String nomeRelatorio;
        private String arquivoJrxml;
        private String orderBy;

        TipoRelatorioReceitaRealizada(String nomeRelatorio, String arquivoJasper, String orderBy) {
            this.nomeRelatorio = nomeRelatorio;
            this.arquivoJrxml = arquivoJasper;
            this.orderBy = orderBy;
        }

        public String getNomeRelatorio() {
            return nomeRelatorio;
        }

        public String getArquivoJrxml() {
            return arquivoJrxml;
        }

        public String getOrderBy() {
            return orderBy;
        }
    }

}
