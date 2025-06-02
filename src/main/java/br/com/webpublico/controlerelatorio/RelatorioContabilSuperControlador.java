/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.RelatorioContabilSuperFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Edi
 */
public abstract class RelatorioContabilSuperControlador extends AbstractReport implements Serializable {

    @EJB
    protected RelatorioContabilSuperFacade relatorioContabilSuperFacade;
    protected Exercicio exercicio;
    protected Conta conta;
    protected UnidadeGestora unidadeGestora;
    protected ContaBancariaEntidade contaBancariaEntidade;
    protected SubConta contaFinanceira;
    protected List<SubConta> contasFinanceiras;
    protected DividaPublica dividaPublica;
    protected EventoContabil eventoContabil;
    protected Pessoa pessoa;
    protected ClasseCredor classeCredor;
    protected FonteDeRecursos fonteDeRecursos;
    protected List<FonteDeRecursos> fontes;
    private List<ContaDeDestinacao> contasDestinacoes;
    protected HashMap parameters;
    protected Date dataInicial;
    protected Date dataFinal;
    protected Date dataReferencia;
    protected String filtros;
    protected String filtroUG;
    protected String sql;
    protected Mes mes;
    protected String numero;
    protected List<HierarquiaOrganizacional> listaUnidades;
    @Enumerated(EnumType.STRING)
    protected ApresentacaoRelatorio apresentacao;
    @Enumerated(EnumType.STRING)
    protected EsferaDoPoder esferaDoPoder;
    protected TipoLancamento tipoLancamento;
    protected Funcao funcao;
    protected SubFuncao subFuncao;
    protected ProgramaPPA programaPPA;
    protected TipoAcaoPPA tipoAcaoPPA;
    protected AcaoPPA acaoPPA;
    protected SubAcaoPPA subAcaoPPA;
    private ConverterAutoComplete converterExercicio;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterEventoContabil;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterClasseCredor;
    private ConverterAutoComplete converterContaReceita;
    private ConverterAutoComplete converterContaFinanceira;
    private ConverterAutoComplete converterFonteDeRecursos;
    private ConverterAutoComplete converterDividaPublica;
    private ConverterAutoComplete converterContaBancariaEntidade;
    private ConverterAutoComplete converterSubProjetoAtividade;

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void limparCamposGeral() {
        this.dataInicial = getSistemaControlador().getDataOperacao();
        this.dataFinal = getSistemaControlador().getDataOperacao();
        this.dataReferencia = getSistemaControlador().getDataOperacao();
        this.unidadeGestora = null;
        this.contaBancariaEntidade = null;
        this.pessoa = null;
        this.classeCredor = null;
        this.contaFinanceira = null;
        this.contasFinanceiras = Lists.newArrayList();
        this.fonteDeRecursos = null;
        this.fontes = Lists.newArrayList();
        this.contasDestinacoes = Lists.newArrayList();
        this.dividaPublica = null;
        this.eventoContabil = null;
        this.conta = null;
        this.mes = null;
        this.parameters = new HashMap();
        this.listaUnidades = new ArrayList<>();
        this.esferaDoPoder = null;
        this.filtros = "";
        this.filtroUG = "";
        this.tipoLancamento = TipoLancamento.NORMAL;
    }

    public String getNomeUsuarioLogado() {
        if (getSistemaControlador().getUsuarioCorrente().getPessoaFisica() != null) {
            return getSistemaControlador().getUsuarioCorrente().getPessoaFisica().getNome();
        } else {
            return getSistemaControlador().getUsuarioCorrente().getUsername();
        }
    }

    public HashMap getParametrosPadrao() {
        HashMap parametros = new HashMap();
        parametros.put("USER", getNomeUsuarioLogado());
        parametros.put("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
        parametros.put("SUBREPORT_DIR", getCaminho());
        parametros.put("IMAGEM", getCaminhoImagem());
        parametros.put("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
        parametros.put("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
        return parametros;
    }

    public HashMap gerarTodosFiltros() {
        HashMap parametros = new HashMap();
        parametros.put("FILTRO_DATA", getFiltrosDatas());
        parametros.put("FILTRO_DATAREFERENCIA", getDataReferenciaFormatada());
        filtros = atualizaFiltrosGerais();
        parametros.put("FILTRO_GERAL", filtros.trim());
        if (unidadeGestora != null) {
            parametros.put("FILTRO_UG", filtroUG);
        }
        return parametros;
    }

    public HashMap getFiltrosPadrao() {
        HashMap parametros = new HashMap();
        filtros = getFiltrosPeriodo() + filtros;
        parametros.put("FILTRO", atualizaFiltrosGerais());
        return parametros;
    }

    public abstract String getNomeRelatorio();

    public String getNomeArquivo() {
        return getNomeRelatorio() + "-" + getDataParaImprimir();
    }

    public String getDataReferenciaFormatada() {
        return DataUtil.getDataFormatada(dataReferencia);
    }

    public String getDataInicialFormatada() {
        return DataUtil.getDataFormatada(dataInicial);
    }

    public String getDataFinalFormatada() {
        return DataUtil.getDataFormatada(dataFinal);
    }

    public String getDataParaImprimir() {
        if (mes != null) {
            if (!mes.equals(Mes.EXERCICIO)
                && !mes.equals(Mes.DEZEMBRO)
                && !mes.equals(Mes.NOVEMBRO)
                && !mes.equals(Mes.OUTUBRO)) {
                return getSistemaControlador().getExercicioCorrente().getAno().toString() + "-0" + getMes().getNumeroMes();
            } else {
                return getSistemaControlador().getExercicioCorrente().getAno().toString() + "-" + getMes().getNumeroMes();
            }
        } else if (dataReferencia != null) {
            return DataUtil.getAno(dataReferencia) + DataUtil.getDataFormatada(dataReferencia, "MM");
        } else {
            return DataUtil.getAno(dataInicial) + DataUtil.getDataFormatada(dataInicial, "MM") +
                "A" + DataUtil.getAno(dataFinal) + DataUtil.getDataFormatada(dataFinal, "MM");
        }
    }

    public String getDataReferenciaFinal() {
        if (!mes.equals(Mes.EXERCICIO)
            && !mes.equals(Mes.DEZEMBRO)
            && !mes.equals(Mes.NOVEMBRO)
            && !mes.equals(Mes.OUTUBRO)) {
            return Util.getDiasMes(mes.getNumeroMes(), getSistemaControlador().getExercicioCorrente().getAno()) + "/" + "0" + mes.getNumeroMes() + "/" + getSistemaControlador().getExercicioCorrente().getAno();
        } else {
            return Util.getDiasMes(mes.getNumeroMes(), getSistemaControlador().getExercicioCorrente().getAno()) + "/" + mes.getNumeroMes() + "/" + getSistemaControlador().getExercicioCorrente().getAno();
        }
    }

    @Deprecated
    public Boolean validarDataReferencia() {
        if (this.dataReferencia == null) {
            FacesUtil.addCampoObrigatorio("O campo Data de Referência deve ser informado.");
            return false;
        }
        return true;
    }

    public void validarDataDeReferencia() {
        ValidacaoException ve = new ValidacaoException();
        if (this.dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado.");
        }
        ve.lancarException();
    }

    public void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        verificarCampoNull(ve, dataInicial, "O campo Data Inicial deve ser informado.");
        verificarCampoNull(ve, dataFinal, "O campo Data Final deve ser informado.");
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");
        }
        verificarDataEmExercicioDiferente(ve, dataInicial, getSistemaControlador().getExercicioCorrente().getAno().toString(), " A Data Inicial deve ter o mesmo ano do exercício corrente.");
        verificarDataEmExercicioDiferente(ve, dataFinal, getSistemaControlador().getExercicioCorrente().getAno().toString(), " A Data Final deve ter o mesmo ano do exercício corrente.");
        verificarDataEmExercicioDiferente(ve, dataInicial, dataFinal, " As datas estão com exercícios diferentes.");
        ve.lancarException();
    }

    public void validarDatasSemVerificarExercicioLogado() {
        ValidacaoException ve = new ValidacaoException();
        verificarCampoNull(ve, dataInicial, "O campo Data Inicial deve ser informado.");
        verificarCampoNull(ve, dataFinal, "O campo Data Final deve ser informado.");
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");
        }
        verificarDataEmExercicioDiferente(ve, dataInicial, dataFinal, " As datas estão com exercícios diferentes.");
        ve.lancarException();
    }

    public void validarDatasComDataReferenciaSemVerificarExercicioLogado() {
        ValidacaoException ve = new ValidacaoException();
        verificarCampoNull(ve, dataInicial, "O campo Data Inicial deve ser informado.");
        verificarCampoNull(ve, dataFinal, "O campo Data Final deve ser informado.");
        verificarCampoNull(ve, dataReferencia, "O campo Data de Referência deve ser informado.");
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");
        }
        if (dataReferencia.before(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data de Referência deve ser maior que a Data Final.");
        }
        verificarDataEmExercicioDiferente(ve, dataInicial, dataFinal, " As datas estão com exercícios diferentes.");
        verificarDataEmExercicioDiferente(ve, dataFinal, dataReferencia, " As datas estão com exercícios diferentes.");
        ve.lancarException();
    }

    public void verificarCampoNull(ValidacaoException ve, Object campo, String mensagemValidacao) {
        if (campo == null) {
            ve.adicionarMensagemDeCampoObrigatorio(mensagemValidacao);
        }
    }

    public void verificarDataEmExercicioDiferente(ValidacaoException ve, Date data, Object campoExercicioStringOuData, String mensagemValidacao) {
        SimpleDateFormat formata = new SimpleDateFormat("yyyy");
        if (campoExercicioStringOuData instanceof Date) {
            campoExercicioStringOuData = formata.format(campoExercicioStringOuData);
        }
        if (!formata.format(data).equals(campoExercicioStringOuData)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagemValidacao);
        }
    }

    @Deprecated
    public Boolean validaDatas() {
        SimpleDateFormat formata = new SimpleDateFormat("yyyy");
        if (dataInicial == null) {
            FacesUtil.addCampoObrigatorio("O campo Data Inicial deve ser informado.");
            return false;
        }
        if (dataFinal == null) {
            FacesUtil.addCampoObrigatorio("O campo Data Final deve ser informado.");
            return false;
        }
        if (dataFinal.before(dataInicial)) {
            FacesUtil.addOperacaoNaoPermitida("O Data Final deve ser maior que a Data Inicial.");
            return false;
        }
        if (formata.format(dataInicial).compareTo(getSistemaControlador().getExercicioCorrente().getAno().toString()) != 0) {
            FacesUtil.addOperacaoNaoPermitida(" A Data Inicial deve ter o mesmo ano do exercício corrente.");
            return false;
        }
        if (formata.format(dataFinal).compareTo(getSistemaControlador().getExercicioCorrente().getAno().toString()) != 0) {
            FacesUtil.addOperacaoNaoPermitida(" A Data Final deve ter o mesmo ano do exercício corrente.");
            return false;
        }
        if (formata.format(dataInicial).compareTo(formata.format(dataFinal)) != 0) {
            FacesUtil.addOperacaoNaoPermitida(" As datas estão com exercícios diferentes.");
            return false;
        }
        return true;
    }

    public String getFiltrosDatas() {
        if (mes != null) {
            if (!mes.equals(Mes.EXERCICIO) && !mes.equals(Mes.DEZEMBRO)) {
                return " a " + mes.getDescricao();
            } else {
                return " ao Exercício";
            }
        } else {
            return getDataInicialFormatada() + " a " + getDataFinalFormatada();
        }
    }

    public String getFiltrosPeriodo() {
        String toReturn = "";
        if (dataInicial != null && dataFinal != null) {
            toReturn += " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        } else if (dataReferencia != null) {
            toReturn += " Data de Referência: " + DataUtil.getDataFormatada(dataReferencia) + " -";
        }
        return toReturn;
    }

    public String getUnidadeGestoraCabecalho() {
        if (unidadeGestora != null) {
            return unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao();
        }
        return "";
    }

    public String getDescricaoSegundoEsferaDoPoder() {
        if (esferaDoPoder == null) {
            return "Município de Rio Branco - AC";
        } else if (esferaDoPoder.equals(EsferaDoPoder.LEGISLATIVO)) {
            return "Câmara Municipal de Rio Branco - AC";
        } else if (esferaDoPoder.equals(EsferaDoPoder.EXECUTIVO)) {
            return "Prefeitura Municipal de Rio Branco - AC";
        }
        return "";
    }

    public String atualizaFiltrosGerais() {
        return filtros = filtros.length() == 0 ? " " : filtros.substring(0, filtros.length() - 1);
    }

    public List<SelectItem> getListaEsferaDoPoder() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Consolidado"));
        for (EsferaDoPoder edp : EsferaDoPoder.values()) {
            if (!edp.equals(EsferaDoPoder.JUDICIARIO)) {
                toReturn.add(new SelectItem(edp, edp.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTipoLancamentos() {
        return Util.getListSelectItemSemCampoVazio(TipoLancamento.values(), false);
    }

    public List<SelectItem> getListaMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Mes m : Mes.values()) {
            toReturn.add(new SelectItem(m, m.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaApresentacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (ApresentacaoRelatorio ap : ApresentacaoRelatorio.values()) {
            toReturn.add(new SelectItem(ap, ap.getDescricao()));
        }
        return toReturn;
    }

    public List<ParametrosRelatorios> filtrosParametrosUnidade(List<ParametrosRelatorios> listaParametros) {
        List<Long> listaIdsUnidades = new ArrayList<>();
        if (this.listaUnidades.size() > 0) {
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            filtros += " Unidade(s): " + unidades;

        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            listaUndsUsuarios = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        } else {
            listaParametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getCodigo(), null, 1, false));
            adicionarExercicio(listaParametros);
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
            filtroUG = unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao();
        }
        return listaParametros;
    }

    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));
    }


    public Exercicio buscarExercicioPelaDataFinal() {
        return dataFinal != null ? getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(dataFinal)) : getSistemaFacade().getExercicioCorrente();
    }

    public Exercicio buscarExercicioPelaDataDeReferencia() {
        return dataReferencia != null ? getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(dataReferencia)) : getSistemaFacade().getExercicioCorrente();
    }

    public void sqlUnidades() {
        String listaIdsUnidades = "";
        String filtroUnidade = "";
        if (this.listaUnidades.size() > 0) {
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades += lista.getSubordinada().getId() + ",";
                filtroUnidade += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            sql += " and vw.subordinada_id in (" + listaIdsUnidades.substring(0, listaIdsUnidades.length() - 1) + ") ";
            filtros += " Unidade(s): " + filtroUnidade;

        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> hierarquiasOrcamentariasDoUsuario = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : hierarquiasOrcamentariasDoUsuario) {
                listaIdsUnidades += hierarquia.getSubordinada().getId() + ",";
                filtroUnidade += " " + hierarquia.getCodigo().substring(3, 10) + " -";
            }
            if (!listaIdsUnidades.isEmpty()) {
                sql += " and vw.subordinada_id in (" + listaIdsUnidades.substring(0, listaIdsUnidades.length() - 1) + ")";
            }
        } else {
            sql += " and ug.id = " + unidadeGestora.getId();
            filtroUG += unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao();
        }
    }


    public List<UnidadeOrganizacional> recuperarUnidadesUsuario() {
        List<HierarquiaOrganizacional> hierarquias = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
        List<UnidadeOrganizacional> unidadesUsuario = new ArrayList<>();
        for (HierarquiaOrganizacional hierarquia : hierarquias) {
            unidadesUsuario.add(hierarquia.getSubordinada());
        }
        return unidadesUsuario;
    }

    protected List<UnidadeOrganizacional> buscarUnidadesDaContaBancaria() {
        if (contaBancariaEntidade != null) {
            return relatorioContabilSuperFacade.getContaBancariaEntidadeFacade().buscarUnidadesDaContaBancaria(contaBancariaEntidade, getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao());
        }
        return new ArrayList<>();
    }

    //    Inicio Converters
    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, relatorioContabilSuperFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, relatorioContabilSuperFacade.getContaFacade());
        }
        return converterConta;
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, relatorioContabilSuperFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (converterEventoContabil == null) {
            converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, relatorioContabilSuperFacade.getEventoContabilFacade());
        }
        return converterEventoContabil;
    }

    public ConverterAutoComplete getConverterContaReceita() {
        if (converterContaReceita == null) {
            converterContaReceita = new ConverterAutoComplete(ContaReceita.class, relatorioContabilSuperFacade.getContaFacade());
        }
        return converterContaReceita;
    }

    public ConverterAutoComplete getConverterContaFinanceira() {
        if (converterContaFinanceira == null) {
            converterContaFinanceira = new ConverterAutoComplete(SubConta.class, relatorioContabilSuperFacade.getSubContaFacade());
        }
        return converterContaFinanceira;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, relatorioContabilSuperFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterFonteDeRecursos() {
        if (converterFonteDeRecursos == null) {
            converterFonteDeRecursos = new ConverterAutoComplete(FonteDeRecursos.class, relatorioContabilSuperFacade.getFonteDeRecursosFacade());
        }
        return converterFonteDeRecursos;
    }

    public ConverterAutoComplete getConverterDividaPublica() {
        if (converterDividaPublica == null) {
            converterDividaPublica = new ConverterAutoComplete(DividaPublica.class, relatorioContabilSuperFacade.getDividaPublicaFacade());
        }
        return converterDividaPublica;
    }

    public ConverterAutoComplete getConverterContaBancariaEntidade() {
        if (converterContaBancariaEntidade == null) {
            converterContaBancariaEntidade = new ConverterAutoComplete(ContaBancariaEntidade.class, relatorioContabilSuperFacade.getContaBancariaEntidadeFacade());
        }
        return converterContaBancariaEntidade;
    }

    public ConverterAutoComplete getConverterSubProjetoAtividade() {
        if (converterSubProjetoAtividade == null) {
            converterSubProjetoAtividade = new ConverterAutoComplete(SubAcaoPPA.class, relatorioContabilSuperFacade.getSubProjetoAtividadeFacade());
        }
        return converterSubProjetoAtividade;
    }

    public void setarContaBancaria() {
        try {
            contaBancariaEntidade = contaFinanceira.getContaBancariaEntidade();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //    Fim Converters

    public void adicionarParameterMunicipio() {
        this.parameters.put("MUNICIPIO", "Município de Rio Branco - AC");
    }

    public void adicionarParameterUser() {
        if (getSistemaControlador().getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", getSistemaControlador().getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", getSistemaControlador().getUsuarioCorrente().getLogin());
        }
    }

    public void adicionarParameterBrasao() {
        this.parameters.put("BRASAO", getCaminhoImagem());
    }

    public void adicionarParameterMesRefencia() {
        this.parameters.put("MES", mes.getDescricao());
    }

    public void adicionarParameterExercicio() {
        this.parameters.put("EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno() + "");
    }

    public void definirContaAndFonteComoNull() {
        contaBancariaEntidade = null;
        contaFinanceira = null;
        fonteDeRecursos = null;
        fontes = Lists.newArrayList();
        contasDestinacoes = Lists.newArrayList();
        contasFinanceiras = Lists.newArrayList();
    }

    public void definirContaFinanceiraAndFonteComoNull() {
        contaFinanceira = null;
        fonteDeRecursos = null;
        fontes = Lists.newArrayList();
        contasDestinacoes = Lists.newArrayList();
        contasFinanceiras = Lists.newArrayList();
    }

    public List<FonteDeRecursos> completarFonteRecurso(String parte) {
        if (contaFinanceira != null) {
            return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFonteDeRecursosPorContaFinanceira(contaFinanceira, getSistemaControlador().getExercicioCorrente());
        } else {
            return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente());
        }
    }

    public List<DividaPublica> completarDividaPublica(String parte) {
        List<DividaPublica> lista = relatorioContabilSuperFacade.getDividaPublicaFacade().listaFiltrandoDividaPublica(parte.trim());
        return lista;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public HashMap getParameters() {
        return parameters;
    }

    public void setParameters(HashMap parameters) {
        this.parameters = parameters;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
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

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public List<SubConta> getContasFinanceiras() {
        return contasFinanceiras;
    }

    public void setContasFinanceiras(List<SubConta> contasFinanceiras) {
        this.contasFinanceiras = contasFinanceiras;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public List<FonteDeRecursos> getFontes() {
        return fontes;
    }

    public void setFontes(List<FonteDeRecursos> fontes) {
        this.fontes = fontes;
    }

    public List<ContaDeDestinacao> getContasDestinacoes() {
        return contasDestinacoes;
    }

    public void setContasDestinacoes(List<ContaDeDestinacao> contasDestinacoes) {
        this.contasDestinacoes = contasDestinacoes;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public TipoAcaoPPA getTipoAcaoPPA() {
        return tipoAcaoPPA;
    }

    public void setTipoAcaoPPA(TipoAcaoPPA tipoAcaoPPA) {
        this.tipoAcaoPPA = tipoAcaoPPA;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public enum Mes {
        JANEIRO("Janeiro", 1),
        FEVEREIRO("Fevereiro", 2),
        MARCO("Março", 3),
        ABRIL("Abril", 4),
        MAIO("Maio", 5),
        JUNHO("Junho", 6),
        JULHO("Julho", 7),
        AGOSTO("Agosto", 8),
        SETEMBRO("Setembro", 9),
        OUTUBRO("Outubro", 10),
        NOVEMBRO("Novembro", 11),
        DEZEMBRO("Dezembro", 12),
        EXERCICIO("Exercício", 12);

        private String descricao;
        private int numeroMes;

        private Mes(String descricao, int numeroMes) {
            this.descricao = descricao;
            this.numeroMes = numeroMes;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public int getNumeroMes() {
            return numeroMes;
        }

        public void setNumeroMes(int numeroMes) {
            this.numeroMes = numeroMes;
        }
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public boolean isLancamentoNormal() {
        return tipoLancamento != null && TipoLancamento.NORMAL.equals(tipoLancamento);
    }

    protected void adicionarFontes(List<ParametrosRelatorios> parametros, String nomeAtributo, String campo, Integer local) {
        if (fontes != null && !fontes.isEmpty()) {
            List<String> codigosFontes = Lists.newArrayList();
            String juncao = "";
            filtros += fontes.size() > 1 ? " Fontes de Recursos: " : " Fonte de Recursos: ";
            for (FonteDeRecursos fonte : fontes) {
                codigosFontes.add(fonte.getCodigo());
                filtros += juncao + fonte.getCodigo();
                juncao = ", ";
            }
            filtros += " -";
            parametros.add(new ParametrosRelatorios(nomeAtributo, campo, null, OperacaoRelatorio.IN, codigosFontes, null, local, false));
        }
    }

    protected void adicionarContasDestinacoes(List<ParametrosRelatorios> parametros, String nomeAtributo, String campo, Integer local) {
        if (contasDestinacoes != null && !contasDestinacoes.isEmpty()) {
            List<String> codigosContas = Lists.newArrayList();
            String juncao = "";
            filtros += " Conta de Destinação de Recurso: ";
            for (ContaDeDestinacao cdd : contasDestinacoes) {
                codigosContas.add(cdd.getCodigo());
                filtros += juncao + cdd.getCodigo();
                juncao = ", ";
            }
            filtros += " -";
            parametros.add(new ParametrosRelatorios(nomeAtributo, campo, null, OperacaoRelatorio.IN, codigosContas, null, local, false));
        }
    }
}
