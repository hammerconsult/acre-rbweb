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
public abstract class RelatorioRestoPagarSuperControlador extends AbstractReport implements Serializable {

    @EJB
    protected RelatorioContabilSuperFacade facade;
    protected ConverterAutoComplete converterFonteDespesaORC;
    protected ConverterAutoComplete converterClasseCredor;
    protected ConverterAutoComplete converterContaDeDespesa;
    protected ConverterAutoComplete converterContaDeDestinacao;
    protected ConverterAutoComplete converterPessoa;
    protected ConverterAutoComplete converterFonteRecurso;
    protected Pessoa pessoa;
    protected ClasseCredor classeCredor;
    protected ProgramaPPA programaPPA;
    protected AcaoPPA projetoAtividade;
    protected Funcao funcao;
    protected SubFuncao subFuncao;
    protected Conta contaDespesa;
    protected Conta contaDestinacao;
    protected FonteDeRecursos fonteDeRecursos;
    protected HashMap parameters;
    protected Date dataInicial;
    protected Date dataFinal;
    protected Date dataReferencia;
    protected String filtros;
    @Enumerated(EnumType.STRING)
    protected TipoRestosProcessado tipoRestosProcessado;
    @Enumerated(EnumType.STRING)
    protected List<HierarquiaOrganizacional> listaUnidades;
    protected UnidadeGestora unidadeGestora;
    @Enumerated(EnumType.STRING)
    protected Apresentacao apresentacao;
    protected TipoExibicao tipoExibicao;

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public HashMap getParametrosPadrao() {
        HashMap parametros = new HashMap();
        parametros.put("USER", getNomeUsuarioLogado());
        parametros.put("MUNICIPIO", "Município de Rio Branco - AC");
        parametros.put("SUBREPORT_DIR", getCaminho());
        parametros.put("IMAGEM", getCaminhoImagem());
        parametros.put("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
        return parametros;
    }

    public HashMap gerarTodosFiltros() {
        HashMap parametros = new HashMap();
        parametros.put("FILTRO_PERIODO", getFiltroPeriodo());
        if (dataReferencia != null) {
            parametros.put("FILTRO_DATAREFERENCIA", "Referente a " + getDataReferenciaFormatada());
        }
        parametros.put("FILTRO_GERAL", filtros.trim());
        if (unidadeGestora != null) {
            parametros.put("FILTRO_UG", unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao());
        }
        return parametros;
    }

    public String getNomeUsuarioLogado() {
        return getSistemaControlador().getUsuarioCorrente().getNome();
    }

    public Exercicio buscarExercicioPelaDataFinal() {
        return getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(dataFinal));
    }

    public abstract String getNumeroRelatorio();

    public abstract String getNomeArquivo();

    public String getDataReferenciaParaImprimir() {
        if (dataReferencia != null) {
            return Util.getAnoDaData(dataReferencia) + Util.getMesDaData(dataReferencia);
        }
        return "";
    }

    public String getDataPorPeriodoParaImprimir() {
        if (dataInicial != null || dataFinal != null) {
            return Util.getAnoDaData(dataInicial) + Util.getMesDaData(dataInicial) + "A" + Util.getAnoDaData(dataFinal) + Util.getMesDaData(dataFinal);
        }
        return "";
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

    public Long getIdExercicioCorrente() {
        Exercicio exercicio = getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(dataInicial));
        return exercicio.getId();
    }

    public String getFiltroPeriodo() {
        if (dataInicial != null || dataFinal != null) {
            return DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal);
        }
        return "";
    }

    public void limparCamposGeral() {
        this.parameters = new HashMap();
        this.listaUnidades = new ArrayList<>();
        this.tipoRestosProcessado = null;
        this.pessoa = null;
        this.dataInicial = getSistemaControlador().getDataOperacao();
        this.dataFinal = getSistemaControlador().getDataOperacao();
        this.dataReferencia = dataFinal;
        this.filtros = "";
        this.unidadeGestora = null;
        this.programaPPA = null;
        this.projetoAtividade = null;
        this.funcao = null;
        this.subFuncao = null;
        this.contaDespesa = null;
        this.contaDestinacao = null;
        this.fonteDeRecursos = null;
        this.tipoExibicao = TipoExibicao.CONTA_DE_DESTINACAO;
    }

    public String atualizaFiltrosGerais() {
        return filtros = filtros.length() == 0 ? " " : filtros.substring(0, filtros.length() - 1);
    }

    public List<ParametrosRelatorios> filtrosParametrosUnidade(List<ParametrosRelatorios> listaParametros) {
        List<Long> listaIdsUnidades = new ArrayList<>();
        if (this.listaUnidades.size() > 0) {
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            listaParametros.add(new ParametrosRelatorios(" VW.SUBORDINADA_ID ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            filtros += " Unidade(s): " + unidades;

        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            listaUndsUsuarios = facade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), buscarExercicioPelaDataFinal(), getSistemaControlador().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                listaParametros.add(new ParametrosRelatorios(" VW.SUBORDINADA_ID ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        } else {
            listaParametros.add(new ParametrosRelatorios(" UG.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getCodigo(), null, 1, false));
            listaParametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getIdExercicioCorrente(), null, 0, false));
        }
        filtros = atualizaFiltrosGerais();
        return listaParametros;
    }

    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> listaParametros) {
        if (tipoRestosProcessado != null) {
            listaParametros.add(new ParametrosRelatorios(" EMP.TIPORESTOSPROCESSADOS", ":tipoResto", null, OperacaoRelatorio.IGUAL, tipoRestosProcessado.name(), null, 1, false));
            filtros += " Tipo de Resto: " + tipoRestosProcessado.getDescricao() + " -";
        }
        if (programaPPA != null) {
            listaParametros.add(new ParametrosRelatorios(" PROG.ID", ":idPrograma", null, OperacaoRelatorio.IGUAL, programaPPA.getId(), null, 1, false));
            filtros += " Programa: " + programaPPA + " -";
        }
        if (projetoAtividade != null) {
            listaParametros.add(new ParametrosRelatorios(" PROJETOATIVIDADE.ID", ":idProjetoAtividade", null, OperacaoRelatorio.IGUAL, projetoAtividade.getId(), null, 1, false));
            filtros += " Projeto/Atividade: " + projetoAtividade + " -";
        }
        if (funcao != null) {
            listaParametros.add(new ParametrosRelatorios(" FUNCAO.ID", ":idFuncao", null, OperacaoRelatorio.IGUAL, funcao.getId(), null, 1, false));
            filtros += " Função: " + funcao + " -";
        }
        if (subFuncao != null) {
            listaParametros.add(new ParametrosRelatorios(" SUBFUNCAO.ID", ":idSubFuncao", null, OperacaoRelatorio.IGUAL, subFuncao.getId(), null, 1, false));
            filtros += " SubFunção: " + subFuncao + " -";
        }
        if (contaDespesa != null) {
            listaParametros.add(new ParametrosRelatorios(" C.CODIGO", ":codigoContaDesp", null, OperacaoRelatorio.LIKE, contaDespesa.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta de Despesa: " + contaDespesa.getCodigo() + " -";
        }
        if (contaDestinacao != null) {
            listaParametros.add(new ParametrosRelatorios(" FONT.CODIGO", ":codigoContaDestinacao", null, OperacaoRelatorio.IGUAL, ((ContaDeDestinacao) contaDestinacao).getFonteDeRecursos().getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + ((ContaDeDestinacao) contaDestinacao).getFonteDeRecursos().getCodigo() + " -";
        }
        if (pessoa != null) {
            listaParametros.add(new ParametrosRelatorios(" EMP.FORNECEDOR_ID", ":idPessoa", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }
        if (classeCredor != null) {
            listaParametros.add(new ParametrosRelatorios(" EMP.CLASSECREDOR_ID ", ":idClasse", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe Credor: " + classeCredor.getCodigo() + " -";
        }
        filtros = atualizaFiltrosGerais();
        return listaParametros;
    }

    public List<ParametrosRelatorios> montarParametrosGeraisEFiltros(List<ParametrosRelatorios> parametros) {
        filtros += " Exibir: " + tipoExibicao.getDescricao() + " -";
        if (tipoRestosProcessado != null) {
            parametros.add(new ParametrosRelatorios(" EMP.TIPORESTOSPROCESSADOS", ":tipoResto", null, OperacaoRelatorio.IGUAL, tipoRestosProcessado.name(), null, 1, false));
            filtros += " Tipo de Resto: " + tipoRestosProcessado.getDescricao() + " -";
        }
        if (programaPPA != null) {
            parametros.add(new ParametrosRelatorios(" PROG.ID", ":idPrograma", null, OperacaoRelatorio.IGUAL, programaPPA.getId(), null, 1, false));
            filtros += " Programa: " + programaPPA + " -";
        }
        if (projetoAtividade != null) {
            parametros.add(new ParametrosRelatorios(" PROJETOATIVIDADE.ID", ":idProjetoAtividade", null, OperacaoRelatorio.IGUAL, projetoAtividade.getId(), null, 1, false));
            filtros += " Projeto/Atividade: " + projetoAtividade + " -";
        }
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" FUNCAO.ID", ":idFuncao", null, OperacaoRelatorio.IGUAL, funcao.getId(), null, 1, false));
            filtros += " Função: " + funcao + " -";
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" SUBFUNCAO.ID", ":idSubFuncao", null, OperacaoRelatorio.IGUAL, subFuncao.getId(), null, 1, false));
            filtros += " SubFunção: " + subFuncao + " -";
        }
        if (contaDespesa != null) {
            parametros.add(new ParametrosRelatorios(" C.CODIGO", ":codigoContaDesp", null, OperacaoRelatorio.LIKE, contaDespesa.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta de Despesa: " + contaDespesa.getCodigo() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" FONT.CODIGO", ":codigoFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (contaDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" CD.ID", ":idContaDeDestinacao", null, OperacaoRelatorio.IGUAL, contaDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDestinacao.getCodigo() + " -";
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" EMP.FORNECEDOR_ID", ":idPessoa", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }
        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" EMP.CLASSECREDOR_ID ", ":idClasse", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe Credor: " + classeCredor.getCodigo() + " -";
        }
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    public List<SelectItem> getTipoDeResto() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRestosProcessado resto : TipoRestosProcessado.values()) {
            toReturn.add(new SelectItem(resto, resto.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaApresentacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Apresentacao ap : Apresentacao.values()) {
            toReturn.add(new SelectItem(ap, ap.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposDeExibicao() {
        return Util.getListSelectItemSemCampoVazio(TipoExibicao.values(), false);
    }

    public List<Conta> completaFonteDespesaORC(String parte) {
        return facade.getContaFacade().listaFiltrandoDestinacaoDeRecursos(parte.trim(), buscarExercicioPelaDataFinal());
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return facade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), buscarExercicioPelaDataFinal());
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        if (pessoa != null) {
            return facade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), pessoa);
        } else {
            return facade.getClasseCredorFacade().listaFiltrandoDescricao(parte.trim());
        }
    }

    public List<Conta> completaContas(String parte) {
        try {
            List<Conta> contas = facade.getContaFacade().listaFiltrandoDespesaCriteria(parte.trim(), buscarExercicioPelaDataFinal());
            if (contas.isEmpty()) {
                return new ArrayList<>();
            } else {
                return contas;
            }
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            return new ArrayList<>();
        }
    }

    public ConverterAutoComplete getConverterContaDeDespesa() {
        if (converterContaDeDespesa == null) {
            converterContaDeDespesa = new ConverterAutoComplete(Conta.class, facade.getContaFacade());
        }
        return converterContaDeDespesa;
    }

    public ConverterAutoComplete getConverterContaDestinacao() {
        if (converterContaDeDestinacao == null) {
            converterContaDeDestinacao = new ConverterAutoComplete(Conta.class, facade.getContaFacade());
        }
        return converterContaDeDestinacao;
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, facade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, facade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterFonteRecurso() {
        if (converterFonteRecurso == null) {
            converterFonteRecurso = new ConverterAutoComplete(FonteDeRecursos.class, facade.getFonteDeRecursosFacade());
        }
        return converterFonteRecurso;
    }

    @Deprecated
    public Boolean validarDatas() {
        SimpleDateFormat formata = new SimpleDateFormat("yyyy");
        if (dataInicial == null) {
            FacesUtil.addCampoObrigatorio("O campo Data Inicial deve ser informado.");
            return false;
        }
        if (dataFinal == null) {
            FacesUtil.addCampoObrigatorio("O campo Data Final deve ser informado.");
            return false;
        }
        if (dataReferencia == null) {
            FacesUtil.addCampoObrigatorio("O campo Data de Referência deve ser informado.");
            return false;
        }
        if (dataFinal.before(dataInicial)) {
            FacesUtil.addOperacaoNaoPermitida(" A Data Final deve ser maior que a Data Inicial.");
            return false;
        }
        if (formata.format(dataInicial).compareTo(formata.format(dataFinal)) != 0) {
            FacesUtil.addOperacaoNaoPermitida(" As datas estão com exercícios diferentes.");
            return false;
        }
        if (dataReferencia != null) {
            if (dataReferencia.before(dataFinal)) {
                FacesUtil.addOperacaoNaoPermitida(" Data de Referência deve ser maior ou igual a Data Final.");
                return false;
            }
        }
        return true;
    }

    @Deprecated
    public Boolean validarDataIniciaFinal() {
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
            FacesUtil.addOperacaoNaoPermitida(" A Data Final deve ser maior que a Data Inicial.");
            return false;
        }
        if (formata.format(dataInicial).compareTo(formata.format(dataFinal)) != 0) {
            FacesUtil.addOperacaoNaoPermitida(" As datas estão com exercícios diferentes.");
            return false;
        }
        return true;
    }

    public void validarDataIniciaAndFinal(Boolean dataReferenciaObrigatoria) {
        ValidacaoException ve = new ValidacaoException();
        SimpleDateFormat formata = new SimpleDateFormat("yyyy");
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeCampoObrigatorio(" A Data Final deve ser maior que a Data Inicial.");
        }
        if (formata.format(dataInicial).compareTo(formata.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeCampoObrigatorio(" As datas estão com exercícios diferentes.");
        }
        if (dataReferenciaObrigatoria) {
            if (dataReferencia == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado.");
            } else if (dataReferencia.before(dataFinal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Data de Referência deve ser maior ou igual a Data Final.");
            }
        }
        ve.lancarException();
    }

    public enum Apresentacao {

        CONSOLIDADO("Consolidado"),
        ORGAO("Orgão"),
        UNIDADE("Unidade"),
        UNIDADE_GESTORA("Unidade Gestora");
        private String descricao;

        private Apresentacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
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

    public HashMap getParameters() {
        return parameters;
    }

    public void setParameters(HashMap parameters) {
        this.parameters = parameters;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public TipoRestosProcessado getTipoRestosProcessado() {
        return tipoRestosProcessado;
    }

    public void setTipoRestosProcessado(TipoRestosProcessado tipoRestosProcessado) {
        this.tipoRestosProcessado = tipoRestosProcessado;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public AcaoPPA getProjetoAtividade() {
        return projetoAtividade;
    }

    public void setProjetoAtividade(AcaoPPA projetoAtividade) {
        this.projetoAtividade = projetoAtividade;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public Conta getContaDestinacao() {
        return contaDestinacao;
    }

    public void setContaDestinacao(Conta contaDestinacao) {
        this.contaDestinacao = contaDestinacao;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public Apresentacao getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(Apresentacao apresentacao) {
        this.apresentacao = apresentacao;
    }

    public TipoExibicao getTipoExibicao() {
        return tipoExibicao;
    }

    public void setTipoExibicao(TipoExibicao tipoExibicao) {
        this.tipoExibicao = tipoExibicao;
    }
}
