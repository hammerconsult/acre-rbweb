package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.contabil.financeiro.ContaAcompanhamentoFinanceiro;
import br.com.webpublico.entidades.contabil.financeiro.FiltroAcompanhamentoFinanceiro;
import br.com.webpublico.entidades.contabil.financeiro.UnidadeAcompanhamentoFinanceiro;
import br.com.webpublico.entidadesauxiliares.AcompanhamentoLiberacaoFinanc;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.RelatorioAcompanhamentoLiberacaoFinanceiraSuperFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mateus on 11/07/17.
 */
public abstract class RelatorioAcompanhamentoLiberacaoFinanceiraSuperControlador extends AbstractReport implements Serializable {


    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    protected SistemaControlador sistemaControlador;
    protected Mes mesReferencia;
    protected String filtro;
    protected FonteDeRecursos fonteDeRecursos;
    protected List<HierarquiaOrganizacional> hierarquias;
    protected TipoLiberacaoFinanceira tipoLiberacaoFinanceira;
    protected TipoMovimento tipoMovimento;
    protected Boolean exibirContasBancarias;
    protected UnidadeAcompanhamentoFinanceiro unidadeAcompanhamentoFinanceiro;
    protected List<UnidadeAcompanhamentoFinanceiro> configuracoes;
    protected Conta contaDeDestinacao;

    public abstract String getNomeRelatorio();

    public abstract String getNomeArquivo();

    public abstract RelatorioAcompanhamentoLiberacaoFinanceiraSuperFacade getFacade();

    public void gerarRelatorio() {
        try {
            HashMap parameters = new HashMap();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(buscaDados());
            adicionarParametros(parameters);
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeRelatorio(), getNomeArquivo(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<FonteDeRecursos> completarFontes(String parte) {
        return getFacade().getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<Conta> completarContaDeDestinacao(String parte) {
        return getFacade().getContaFacade().buscarContasDeDestinacaoDeRecursos(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public void limparCampos() {
        filtro = "";
        hierarquias = new ArrayList<>();
        exibirContasBancarias = false;
        inicializarConfiguracoes();
        fonteDeRecursos = new FonteDeRecursos();
        contaDeDestinacao = null;
    }

    private void inicializarConfiguracoes() {
        instanciarUnidadeAcompanhamentoFinanceiro();
        configuracoes = getFacade().getUnidadeAcompanhamentoFinanceiroFacade().recuperarConfiguracoes();
        if (configuracoes == null) {
            configuracoes = Lists.newArrayList();
        }
    }

    public void adicionarConfiguracao() {
        try {
            atribuirUnidade();
            validarConfiguracao();
            adicionarFiltros();
            salvarConfiguracao();
            configuracoes = Util.adicionarObjetoEmLista(configuracoes, unidadeAcompanhamentoFinanceiro);
            instanciarUnidadeAcompanhamentoFinanceiro();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void salvarConfiguracao() {
        if (unidadeAcompanhamentoFinanceiro.getId() == null) {
            getFacade().getUnidadeAcompanhamentoFinanceiroFacade().salvarNovo(unidadeAcompanhamentoFinanceiro);
        } else {
            getFacade().getUnidadeAcompanhamentoFinanceiroFacade().salvar(unidadeAcompanhamentoFinanceiro);
        }
    }

    private void adicionarFiltros() {
        unidadeAcompanhamentoFinanceiro.getFiltros().clear();
        for (TipoLiberacaoFinanceira liberacaoFinanceira : unidadeAcompanhamentoFinanceiro.getTipos()) {
            FiltroAcompanhamentoFinanceiro filtro = new FiltroAcompanhamentoFinanceiro(unidadeAcompanhamentoFinanceiro, liberacaoFinanceira);
            unidadeAcompanhamentoFinanceiro.getFiltros().add(filtro);
        }
    }

    private void atribuirUnidade() {
        HierarquiaOrganizacional hierarquiaOrganizacional = unidadeAcompanhamentoFinanceiro.getHierarquiaOrganizacional();
        if (hierarquiaOrganizacional != null) {
            unidadeAcompanhamentoFinanceiro.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
    }

    private void validarConfiguracao() {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeAcompanhamentoFinanceiro.getUnidadeOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Unidade organizacional é obrigatório.");
        } else {
            for (UnidadeAcompanhamentoFinanceiro configuracao : configuracoes) {
                if (configuracao.getUnidadeOrganizacional().getId().equals(unidadeAcompanhamentoFinanceiro.getUnidadeOrganizacional().getId())
                    && !configuracao.equals(unidadeAcompanhamentoFinanceiro)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade " + unidadeAcompanhamentoFinanceiro.getUnidadeOrganizacional() + " já foi adicionado na lista.");
                }
            }

        }
        ve.lancarException();
    }

    public void removerConfiguracao(UnidadeAcompanhamentoFinanceiro unidadeAcompanhamentoFinanceiro) {
        configuracoes.remove(unidadeAcompanhamentoFinanceiro);
        getFacade().getUnidadeAcompanhamentoFinanceiroFacade().remover(unidadeAcompanhamentoFinanceiro);
    }

    public void alterarConfiguracao(UnidadeAcompanhamentoFinanceiro unidadeAcompanhamentoFinanceiro) {
        this.unidadeAcompanhamentoFinanceiro = unidadeAcompanhamentoFinanceiro;
        this.unidadeAcompanhamentoFinanceiro.setHierarquiaOrganizacional(getFacade().getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(
            sistemaControlador.getDataOperacao(), unidadeAcompanhamentoFinanceiro.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA));

        this.unidadeAcompanhamentoFinanceiro.setTipos(new TipoLiberacaoFinanceira[this.unidadeAcompanhamentoFinanceiro.getFiltros().size()]);
        int posicao = 0;
        for (FiltroAcompanhamentoFinanceiro filtroAcompanhamentoFinanceiro : this.unidadeAcompanhamentoFinanceiro.getFiltros()) {
            this.unidadeAcompanhamentoFinanceiro.getTipos()[posicao] = filtroAcompanhamentoFinanceiro.getTipoLiberacaoFinanceira();
            posicao++;
        }
    }

    private void instanciarUnidadeAcompanhamentoFinanceiro() {
        unidadeAcompanhamentoFinanceiro = new UnidadeAcompanhamentoFinanceiro();
    }

    public List<TipoLiberacaoFinanceira> getTipos() {
        return Arrays.asList(TipoLiberacaoFinanceira.values());
    }

    public List<TipoContaAcompanhamento> getContas() {
        return Arrays.asList(TipoContaAcompanhamento.values());
    }

    public void removerConta(ContaAcompanhamentoFinanceiro conta) {
        if (unidadeAcompanhamentoFinanceiro.getIntervalosConta().contains(conta)) {
            unidadeAcompanhamentoFinanceiro.getIntervalosConta().remove(conta);
        }
    }

    public void adicionarConta() {
        try {
            validarIntervaloDeContasAcompanhamento();
            if (!unidadeAcompanhamentoFinanceiro.getIntervalosConta().contains(unidadeAcompanhamentoFinanceiro.getConta())) {
                unidadeAcompanhamentoFinanceiro.getIntervalosConta().add(unidadeAcompanhamentoFinanceiro.getConta());
                unidadeAcompanhamentoFinanceiro.setConta(new ContaAcompanhamentoFinanceiro());
                unidadeAcompanhamentoFinanceiro.getConta().setUnidade(unidadeAcompanhamentoFinanceiro);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarIntervaloDeContasAcompanhamento() {
        ValidacaoException ve = new ValidacaoException();

        if (!isPossuiIntervaloInicial()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar ao menos uma Conta Financeira Inicial para o Intervalo!");
        } else {
            atribuirContaFinanceiraFinal();

            if (getIntervaloFinal() < getIntervaloInicial()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta Financeira Final deve ser maior ou igual a Conta Financeira Inicial!");
            }

            for (ContaAcompanhamentoFinanceiro intervalo : unidadeAcompanhamentoFinanceiro.getIntervalosConta()) {
                if (isPossuiIntervaloFinal() && isTipoContaAcompanhamentoIguais(intervalo)) {
                    if (isIntervalosIguais(intervalo, getIntervaloInicial(), getIntervaloFinal())
                        || isIntervalosCadastrados(intervalo, getIntervaloInicial(), getIntervaloFinal())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O intervalo a ser cadastrado conflita com intervalo já cadastrado!");
                        break;
                    }
                }
            }
        }
        ve.lancarException();
    }

    private Long getIntervaloFinal() {
        return Long.parseLong(unidadeAcompanhamentoFinanceiro.getConta().getIntervaloFinal());
    }

    private Long getIntervaloInicial() {
        return Long.parseLong(unidadeAcompanhamentoFinanceiro.getConta().getIntervaloInicial());
    }

    private Boolean isTipoContaAcompanhamentoIguais(ContaAcompanhamentoFinanceiro intervalo) {
        return unidadeAcompanhamentoFinanceiro.getConta().getTipoContaAcompanhamento().equals(intervalo.getTipoContaAcompanhamento());
    }

    private boolean isPossuiIntervaloFinal() {
        return !Strings.isNullOrEmpty(unidadeAcompanhamentoFinanceiro.getConta().getIntervaloFinal().trim());
    }

    private boolean isPossuiIntervaloInicial() {
        return !Strings.isNullOrEmpty(unidadeAcompanhamentoFinanceiro.getConta().getIntervaloInicial().trim());
    }

    private boolean isIntervalosCadastrados(ContaAcompanhamentoFinanceiro intervalo, Long intervaloInicial, Long intervaloFinal) {
        return isIntervaloInicialCadastrado(intervalo, intervaloInicial, intervaloFinal)
            || isIntervaloFinalCadastrado(intervalo, intervaloInicial, intervaloFinal);
    }

    private boolean isIntervalosIguais(ContaAcompanhamentoFinanceiro intervalo, Long intervaloInicial, Long intervaloFinal) {
        return isIntervaloInicialIguais(intervalo, intervaloInicial)
            || isIntervaloFinalIguais(intervalo, intervaloFinal);
    }

    private boolean isIntervaloInicialCadastrado(ContaAcompanhamentoFinanceiro intervalo, Long intervaloInicial, Long intervaloFinal) {
        return Long.parseLong(intervalo.getIntervaloInicial()) >= intervaloInicial
            && Long.parseLong(intervalo.getIntervaloInicial()) <= intervaloFinal;
    }

    private boolean isIntervaloFinalCadastrado(ContaAcompanhamentoFinanceiro intervalo, Long intervaloInicial, Long intervaloFinal) {
        return Long.parseLong(intervalo.getIntervaloFinal()) >= intervaloInicial
            && Long.parseLong(intervalo.getIntervaloFinal()) <= intervaloFinal;
    }

    private boolean isIntervaloInicialIguais(ContaAcompanhamentoFinanceiro intervalo, Long intervaloAux) {
        return Long.parseLong(intervalo.getIntervaloInicial()) == intervaloAux;
    }

    private boolean isIntervaloFinalIguais(ContaAcompanhamentoFinanceiro intervalo, Long intervaloAux) {
        return Long.parseLong(intervalo.getIntervaloFinal()) == intervaloAux;
    }

    private void atribuirContaFinanceiraFinal() {
        if (isPossuiIntervaloInicial() && !isPossuiIntervaloFinal()) {
            unidadeAcompanhamentoFinanceiro.getConta().setIntervaloFinal(unidadeAcompanhamentoFinanceiro.getConta().getIntervaloInicial());
        }
    }


    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return getFacade().getHierarquiaOrganizacionalFacade().filtraPorNivel(parte, "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaControlador.getDataOperacao());
    }

    public void adicionarParametros(HashMap parameters) {
        parameters.put("FILTRO", filtro);
        parameters.put("USER", sistemaControlador.getUsuarioCorrente().getNome());
        parameters.put("MUNICIPIO", "Município de Rio Branco - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
    }

    public List<ParametrosRelatorios> buscarParametrosAndAtualizarFiltros() {
        filtro = " Período: Janeiro a " + mesReferencia.getDescricao() + " -";

        if (tipoLiberacaoFinanceira != null) {
            filtro += " Tipo: " + tipoLiberacaoFinanceira.getDescricao() + " -";
        }

        if (tipoMovimento != null) {
            filtro += " Tipo de Movimento: " + tipoMovimento.getDescricao() + " -";
        }

        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":MESFINAL", null, null, Util.getDiasMes(mesReferencia.getNumeroMes(), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesReferencia.getNumeroMesString() + "/" + sistemaControlador.getExercicioCorrente().getAno(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO_ID", null, null, sistemaControlador.getExercicioCorrente().getId(), null, 0, true));

        if (fonteDeRecursos != null) {
            filtro += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
            parametros.add(new ParametrosRelatorios(" fr.id ", ":frId", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cd.id", ":cdest", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtro += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }
        List<Long> idsUnidades = new ArrayList<>();
        if (this.hierarquias.size() > 0) {
            String unidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquias) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                unidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
        } else {
            List<HierarquiaOrganizacional> listaUndsUsuarios = getFacade().getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquiaOrganizacional : listaUndsUsuarios) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
        }

        if (idsUnidades.size() != 0) {
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        }

        if (tipoLiberacaoFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" liberacao.tipoLiberacaoFinanceira ", ":tipoLiberacao", null, OperacaoRelatorio.LIKE, tipoLiberacaoFinanceira.name(), null, 2, false));
            parametros.add(new ParametrosRelatorios(" transferencia.tipoTransferenciaFinanceira ", ":tipoLiberacao", null, OperacaoRelatorio.LIKE, tipoLiberacaoFinanceira.name(), null, 3, false));
        }

        filtro = filtro.substring(0, filtro.length() - 1);
        return parametros;
    }

    public List<AcompanhamentoLiberacaoFinanc> buscaDados() {
        List<ParametrosRelatorios> parametros = buscarParametrosAndAtualizarFiltros();
        return getFacade().recuperarRelatorio(parametros, mesReferencia.getNumeroMes(), tipoMovimento, exibirContasBancarias);
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItemSemCampoVazio(Mes.values(), false);
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Mes getMesReferencia() {
        return mesReferencia;
    }

    public void setMesReferencia(Mes mesReferencia) {
        this.mesReferencia = mesReferencia;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }

    public TipoLiberacaoFinanceira getTipoLiberacaoFinanceira() {
        return tipoLiberacaoFinanceira;
    }

    public void setTipoLiberacaoFinanceira(TipoLiberacaoFinanceira tipoLiberacaoFinanceira) {
        this.tipoLiberacaoFinanceira = tipoLiberacaoFinanceira;
    }

    public TipoMovimento getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimento tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public Boolean getExibirContasBancarias() {
        return exibirContasBancarias;
    }

    public void setExibirContasBancarias(Boolean exibirContasBancarias) {
        this.exibirContasBancarias = exibirContasBancarias;
    }

    public UnidadeAcompanhamentoFinanceiro getUnidadeAcompanhamentoFinanceiro() {
        return unidadeAcompanhamentoFinanceiro;
    }

    public void setUnidadeAcompanhamentoFinanceiro(UnidadeAcompanhamentoFinanceiro unidadeAcompanhamentoFinanceiro) {
        this.unidadeAcompanhamentoFinanceiro = unidadeAcompanhamentoFinanceiro;
    }

    public List<UnidadeAcompanhamentoFinanceiro> getConfiguracoes() {
        return configuracoes;
    }

    public void setConfiguracoes(List<UnidadeAcompanhamentoFinanceiro> configuracoes) {
        this.configuracoes = configuracoes;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}
