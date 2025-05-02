/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-divida-ativa", pattern = "/relatorio/divida-ativa/", viewId = "/faces/financeiro/relatorio/relatoriodividaativa.xhtml")
})
public class RelatorioDividaAtivaControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Pessoa pessoa;
    private ClasseCredor classeCredor;
    private TiposCredito tiposCredito;
    private OperacaoDividaAtiva operacaoDividaAtiva;
    private String numeroInicial;
    private String numeroFinal;
    private Date dataInicial;
    private Date dataFinal;
    private Conta contaReceita;
    private Conta contaDeDestinacao;
    private String filtro;
    private EventoContabil eventoContabil;
    private List<HierarquiaOrganizacional> hierarquias;
    private ApresentacaoRelatorio apresentacao;
    private UnidadeGestora unidadeGestora;
    private TipoLancamento tipoLancamento;

    public List<SelectItem> getApresentacoes() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (ApresentacaoRelatorio ap : ApresentacaoRelatorio.values()) {
            toReturn.add(new SelectItem(ap, ap.getDescricao()));
        }
        return toReturn;
    }

    public void gerarRelatorio() {
        try {
            validarDatas();
            RelatorioDTO dto = montarRelatorioDto();
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = montarRelatorioDto();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDto() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), Boolean.FALSE);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC ");
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("APRESENTACAO", apresentacao.name());
        dto.adicionarParametro("MODULO", "Execução Orçamentária");
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("FILTRO", filtro);
        dto.setNomeRelatorio("Relatório de Dívida Ativa Contábil");
        dto.setApi("contabil/divida-ativa/");
        return dto;
    }

    public Exercicio buscarExercicioPelaDataFinal() {
        if (dataFinal == null) {
            return sistemaFacade.getExercicioCorrente();
        }
        return exercicioFacade.getExercicioPorAno(DataUtil.getAno(dataFinal));
    }

    public List<SelectItem> getTiposCreditos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TiposCredito tc : TiposCredito.values()) {
            if (tc.getDescricao().startsWith("Dívida")) {
                toReturn.add(new SelectItem(tc, tc.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getOperacoesDividasAtivas() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoDividaAtiva ocr : OperacaoDividaAtiva.values()) {
            toReturn.add(new SelectItem(ocr, ocr.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposLancamentos() {
        return Util.getListSelectItem(TipoLancamento.values());
    }

    public List<ClasseCredor> completarClassesCredores(String parte) {
        return classeCredorFacade.listaFiltrando(parte, "descricao");
    }

    public List<EventoContabil> completarEventosContabil(String parte) {
        return eventoContabilFacade.listaFiltrando(parte, "descricao");
    }

    public List<Conta> completarContas(String parte) {
        return contaFacade.listaFiltrandoReceita(parte, buscarExercicioPelaDataFinal());
    }

    public List<Pessoa> completarPessoas(String parte) {
        return pessoaFacade.listaTodasPessoasPorId(parte);
    }

    public List<Conta> completarContasDeDestinacao(String filtro) {
        return contaFacade.buscarContasDeDestinacaoDeRecursos(filtro, buscarExercicioPelaDataFinal());
    }

    @URLAction(mappingId = "relatorio-divida-ativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = sistemaFacade.getDataOperacao();
        dataFinal = sistemaFacade.getDataOperacao();
        numeroInicial = "";
        numeroFinal = "";
        classeCredor = null;
        pessoa = null;
        tiposCredito = null;
        operacaoDividaAtiva = null;
        eventoContabil = null;
        contaReceita = null;
        hierarquias = Lists.newArrayList();
        contaDeDestinacao = null;
    }

    private List<ParametrosRelatorios> montarParametros() {
        filtro = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(" trunc(DIVIDA.DATADIVIDA) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));

        if (!Strings.isNullOrEmpty(numeroInicial) && !Strings.isNullOrEmpty(numeroFinal)) {
            parametros.add(new ParametrosRelatorios(" DIVIDA.NUMERO ", ":numInicial", ":numFinal", OperacaoRelatorio.BETWEEN, numeroInicial, numeroFinal, 1, false));
            filtro += " Número inicial: " + numeroInicial + " Número final: " + numeroFinal + " -";
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" DIVIDA.PESSOA_ID ", ":pessoaId", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtro += " Pessoa: " + pessoa.getNome() + " -";
        }
        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" DIVIDA.CLASSECREDORPESSOA_ID ", ":classeCredorID", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtro += " Classe: " + classeCredor.getDescricao() + " -";
        }
        if (contaReceita != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":codigoContaReceita", null, OperacaoRelatorio.IGUAL, contaReceita.getCodigo(), null, 1, false));
            filtro += " Conta de Receita: " + contaReceita.getCodigo() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" contadest.codigo ", ":codigoContaDeDestinacao", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getCodigo(), null, 1, false));
            filtro += " Conta de Destinação: " + contaDeDestinacao.getCodigo() + " -";
        }
        if (eventoContabil != null) {
            parametros.add(new ParametrosRelatorios(" divida.EVENTOCONTABIL_ID ", ":eventoId", null, OperacaoRelatorio.IGUAL, eventoContabil.getId(), null, 1, false));
            filtro += " Evento Contabil: " + eventoContabil.getCodigo() + " - " + eventoContabil.getDescricao() + " -";
        }
        if (tiposCredito != null) {
            parametros.add(new ParametrosRelatorios(" CONTAREC.TIPOSCREDITO ", ":tipoCredito", null, OperacaoRelatorio.IGUAL, tiposCredito.name(), null, 1, false));
            filtro += " Tipo de Conta: " + tiposCredito.getDescricao() + " -";
        }
        if (tipoLancamento != null) {
            parametros.add(new ParametrosRelatorios(" DIVIDA.TIPOLANCAMENTO ", ":tipoLancamento", null, OperacaoRelatorio.IGUAL, tipoLancamento.name(), null, 1, false));
            filtro += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + " -";
        }
        if (this.operacaoDividaAtiva != null) {
            parametros.add(new ParametrosRelatorios(" DIVIDA.OPERACAODIVIDAATIVA  ", ":operacaoDivida", null, OperacaoRelatorio.IGUAL, operacaoDividaAtiva.name(), null, 1, false));
            filtro += " Operação de Crédito: " + operacaoDividaAtiva.getDescricao() + " -";
        }
        if (!hierarquias.isEmpty()) {
            List<Long> idsUnidades = Lists.newArrayList();
            String unidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquias) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                unidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (this.unidadeGestora == null) {
            List<Long> idsUnidades = Lists.newArrayList();
            List<HierarquiaOrganizacional> unidadesDoUsuario = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), buscarExercicioPelaDataFinal(), dataFinal, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtro += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        if (unidadeGestora != null || apresentacao.isApresentacaoUnidadeGestora()) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
        }
        filtro = filtro.substring(0, filtro.length() - 1);
        return parametros;
    }

    private void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial não pode ser maior que a Data Final.");
        }
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes");
        }
        ve.lancarException();
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public Conta getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(Conta contaReceita) {
        this.contaReceita = contaReceita;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public TiposCredito getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TiposCredito tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    public OperacaoDividaAtiva getOperacaoDividaAtiva() {
        return operacaoDividaAtiva;
    }

    public void setOperacaoDividaAtiva(OperacaoDividaAtiva operacaoDividaAtiva) {
        this.operacaoDividaAtiva = operacaoDividaAtiva;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }
}
