/*
 * Codigo gerado automaticamente em Thu Nov 22 14:40:47 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoReceitaExtra;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ReceitaExtraFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "transportar-receitaextra", pattern = "/transportar-receitaextra/", viewId = "/faces/financeiro/extraorcamentario/receitaextra/transportar-receita-extra.xhtml"),
})
public class TransportarReceitaExtraControlador implements Serializable {

    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    private List<ReceitaExtra> receitaExtras;
    private List<ReceitaExtra> receitaExtrasSelecionadas;
    private MoneyConverter moneyConverter;
    private Date dtInicial;
    private Date dtFinal;
    private Date dataTransporte;
    private HierarquiaOrganizacional hierarquiaOrganizacionalFiltro;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Conta contaExtra;
    private Conta contaExtraTransporte;
    private SubConta contaFinanceira;
    private SubConta contaFinanceiraTransporte;
    private Pessoa pessoaTransporte;
    private Pessoa pessoa;
    private ClasseCredor classeTransporte;
    private ClasseCredor classeCredor;
    private Exercicio exercicioCorrente;
    private Exercicio exercicio;
    private ContaDeDestinacao contaDeDestinacao;
    private ContaDeDestinacao contaDeDestinacaoTransporte;


    public ReceitaExtraFacade getFacade() {
        return receitaExtraFacade;
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void novoTransporte() {
        receitaExtras = new ArrayList<>();
        receitaExtrasSelecionadas = new ArrayList<>();
        exercicioCorrente = getSistemaControlador().getExercicioCorrente();
        exercicio = null;
        dtFinal = null;
        dtInicial = null;
        contaExtra = null;
        contaExtraTransporte = null;
        contaFinanceiraTransporte = null;
        contaFinanceira = null;
        classeTransporte = null;
        classeCredor = null;
        hierarquiaOrganizacional = null;
        hierarquiaOrganizacionalFiltro = null;
        pessoaTransporte = null;
        pessoa = null;
        contaDeDestinacao = null;
        contaDeDestinacaoTransporte = null;
        receitaExtras.clear();
        receitaExtrasSelecionadas.clear();
        FacesUtil.executaJavaScript("setaFoco('Formulario:exercicio_input')");
    }

    @URLAction(mappingId = "transportar-receitaextra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        parametrosIniciais();
    }

    private void parametrosIniciais() {
        receitaExtras = new ArrayList<>();
        receitaExtrasSelecionadas = new ArrayList<>();
        exercicioCorrente = getSistemaControlador().getExercicioCorrente();
        dataTransporte = new DataUtil().montaData(01, 0, exercicioCorrente.getAno()).getTime();
        exercicio = null;
    }

    public void setarExercicioNaData() {
        if (exercicio != null) {
            dtInicial = new DataUtil().montaData(01, 0, exercicio.getAno()).getTime();
            dtFinal = new DataUtil().montaData(31, 11, exercicio.getAno()).getTime();
            hierarquiaOrganizacionalFiltro = null;
            receitaExtras.clear();
        }
    }

    public void transportarReceitaExtra() {
        try {
            if (validarTransporteReceita()) {
                configurarReceitaParaTransportar();
                FacesUtil.addOperacaoRealizada("Foram transportada(s) " + receitaExtrasSelecionadas.size() + " receita(s) extraorçamentária(s) para o exercício de " + exercicioCorrente.toString() + ".");
                FacesUtil.executaJavaScript("dialogFinalizar.show()");
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void configurarReceitaParaTransportar() {
        try {
            for (ReceitaExtra receitaOriginal : receitaExtrasSelecionadas) {
                receitaOriginal = receitaExtraFacade.recuperar(receitaOriginal.getId());
                ReceitaExtra receitaTransportada = (ReceitaExtra) Util.clonarObjeto(receitaOriginal);

                recuperarCodigoDaUnidade(receitaOriginal);
                receitaTransportada.setId(null);
                receitaTransportada.setExercicio(getSistemaControlador().getExercicioCorrente());
                receitaTransportada.setDataReceita(dataTransporte);
                receitaTransportada.setDataConciliacao(null);
                receitaTransportada.setEventoContabil(null);
                receitaTransportada.setValor(receitaOriginal.getSaldo());
                receitaTransportada.setValorEstornado(BigDecimal.ZERO);
                receitaTransportada.setComplementoHistorico(configurarHistoricoReceitaExtra(receitaOriginal));
                receitaTransportada.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
                receitaTransportada.setContaExtraorcamentaria(contaExtraTransporte);
                receitaTransportada.setSubConta(contaFinanceiraTransporte);
                receitaTransportada.setPessoa(pessoaTransporte == null ? receitaOriginal.getPessoa() : pessoaTransporte);
                receitaTransportada.setClasseCredor(classeTransporte == null ? receitaOriginal.getClasseCredor() : classeTransporte);
                receitaTransportada.setContaDeDestinacao(contaDeDestinacaoTransporte);
                receitaTransportada.setFonteDeRecursos(contaDeDestinacaoTransporte.getFonteDeRecursos());
                receitaTransportada.setSituacaoReceitaExtra(SituacaoReceitaExtra.ABERTO);
                receitaTransportada.setNumero(receitaExtraFacade.getSingletonGeradorCodigoContabil().getNumeroReceitaExtra(exercicioCorrente, receitaTransportada.getUnidadeOrganizacional(), getSistemaControlador().getDataOperacao()));
                receitaTransportada.setTransportado(Boolean.TRUE);
                receitaTransportada.setReceitaDeAjusteDeposito(Boolean.FALSE);
                receitaExtraFacade.salvarReceitaTransportada(receitaTransportada);

                receitaOriginal.setSaldo(BigDecimal.ZERO);
                receitaExtraFacade.salvar(receitaOriginal);
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica("Erro ao configurar a receita extraorçamentária " + ex.getMessage());
        }
    }

    private String recuperarCodigoDaUnidade(ReceitaExtra receitaOriginal) {
        HierarquiaOrganizacional hierarquiaOrganizacional = receitaExtraFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(receitaOriginal.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        return hierarquiaOrganizacional.getCodigo();
    }

    private String configurarHistoricoReceitaExtra(ReceitaExtra receitaOriginal) {
        if (receitaOriginal.getComplementoHistorico() != null) {
            return "Receita Origem: " + receitaOriginal.getNumero() + "/" + receitaOriginal.getExercicio() + " (" + DataUtil.getDataFormatada(receitaOriginal.getDataReceita()) + ") " + " | " + receitaOriginal.getComplementoHistorico();
        } else {
            return "Receita Origem: " + receitaOriginal.getNumero() + "/" + receitaOriginal.getExercicio() + " (" + DataUtil.getDataFormatada(receitaOriginal.getDataReceita()) + ") " + " | Histórico não informado: ";
        }
    }

    public void redirecionarParaLista() {
        FacesUtil.redirecionamentoInterno("/receita-extra/listar/");
    }

    private boolean validarTransporteReceita() {
        if (receitaExtras.size() <= 0) {
            FacesUtil.addOperacaoNaoPermitida(" Informe os filtros obrigatórios e clique em perquisar para encontrar as receitas extrasorçamentárias.");
            return false;
        }
        if (receitaExtrasSelecionadas.size() <= 0) {
            FacesUtil.addOperacaoNaoPermitida(" Selecione um ou mais receitas extraoçarmentárias para transportar.");
            return false;
        }
        if (hierarquiaOrganizacional == null) {
            FacesUtil.addOperacaoNaoPermitida(" Selecione uma Unidade Organizacional correspondente para a Unidade da receita a ser transportada.");
            return false;
        }
        if (contaExtraTransporte == null) {
            FacesUtil.addOperacaoNaoPermitida(" Selecione uma Conta Extraorçamentária para a receita a ser transportada.");
            return false;
        }
        if (contaFinanceiraTransporte == null) {
            FacesUtil.addOperacaoNaoPermitida(" Selecione uma Conta Financeira para a receita a ser transportada.");
            return false;
        }
        if (contaDeDestinacaoTransporte == null) {
            FacesUtil.addOperacaoNaoPermitida(" Selecione uma Conta de Destinação de Recurso para a receita a ser transportada.");
            return false;
        }
        return true;
    }

    public List<ContaExtraorcamentaria> completaContaExtraorcamentaria(String parte) {
        if (exercicio != null) {
            return receitaExtraFacade.getContaFacade().listaFiltrandoTodasContaExtraPorExercicio(parte.trim(), exercicio);
        }
        return new ArrayList<>();
    }

    public List<Exercicio> completaExercicio(String parte) {
        return receitaExtraFacade.getExercicioFacade().listaFiltrandoExerciciosAtualPassados(parte.trim());
    }

    public List<ClasseCredor> completaClasseTransporte(String parte) {
        if (pessoaTransporte != null) {
            return receitaExtraFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), pessoaTransporte);
        }
        return new ArrayList<>();
    }

    public List<ClasseCredor> completaClasse(String parte) {
        if (pessoa != null) {
            return receitaExtraFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), pessoa);
        }
        return new ArrayList<>();
    }

    public List<SubConta> completaContaFinanceiraTransporte(String parte) {
        return receitaExtraFacade.getSubContaFacade().listaPorExercicio(parte.trim(), exercicioCorrente);
    }

    public List<SubConta> completaContaFinanceira(String parte) {
        return receitaExtraFacade.getSubContaFacade().listaPorExercicio(parte.trim(), exercicio);
    }

    public List<ContaDeDestinacao> completarContasDeDestinacoes(String parte) {
        if (contaFinanceira != null) {
            return receitaExtraFacade.getContaFacade().buscarContasDeDestinacaoPorCodigoOrDescricaoESubConta(parte.trim(), exercicio, contaFinanceira.getId());
        }
        return new ArrayList<>();
    }

    public List<ContaDeDestinacao> completarContasDeDestinacoesTransporte(String parte) {
        if (contaFinanceiraTransporte != null) {
            return receitaExtraFacade.getContaFacade().buscarContasDeDestinacaoPorCodigoOrDescricaoESubConta(parte.trim(), exercicioCorrente, contaFinanceiraTransporte.getId());
        }
        return new ArrayList<>();
    }

    public List<ContaExtraorcamentaria> completaContaExtraTransporte(String parte) {
        return receitaExtraFacade.getContaFacade().listaFiltrandoContaExtraAtivasPorExercicio(parte.trim(), exercicioCorrente);
    }

    public List<Pessoa> completaPessaoTransporte(String parte) {
        return receitaExtraFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String parte) {
        if (dtInicial != null) {
            return receitaExtraFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", "ORCAMENTARIA", dtInicial);
        }
        return new ArrayList<>();
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacionalTransportada(String parte) {
        return receitaExtraFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel(parte.trim(), getSistemaControlador().getUsuarioCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao(), "ORCAMENTARIA", 3);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalVigente() {
        try {
            HierarquiaOrganizacional hierarquiaOrg = receitaExtraFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(getSistemaControlador().getDataOperacao(), hierarquiaOrganizacionalFiltro.getSubordinada(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
            if (hierarquiaOrg != null) {
                return hierarquiaOrg;
            } else {
                throw new ExcecaoNegocioGenerica("A Unidade Organizacional " + hierarquiaOrganizacionalFiltro + " não possui uma unidade vinculada para o exercício de " + getSistemaControlador().getExercicioCorrente());
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addAtencao(ex.getMessage());
            return null;
        }
    }


    public ContaExtraorcamentaria getContaExtraVigente() {
        try {
            ContaExtraorcamentaria contaExtraorcamentaria = receitaExtraFacade.getContaFacade().recuperarContaExtraPorExercicio(contaExtra.getCodigo(), exercicioCorrente);
            if (contaExtraorcamentaria != null) {
                return contaExtraorcamentaria;
            } else {
                throw new ExcecaoNegocioGenerica("A Conta Extraorçamentária " + contaExtra + " não possui uma conta vigente para o exercício de " + exercicioCorrente);
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addAtencao(ex.getMessage());
            return null;
        }
    }

    public SubConta getContaFinanceiraVigente() {
        try {
            SubConta contaFinanc = receitaExtraFacade.getSubContaFacade().recuperarContaFinanceiraPorCodigoExericicio(contaFinanceira.getCodigo(), exercicioCorrente);
            if (contaFinanc != null) {
                return contaFinanc;
            } else {
                throw new ExcecaoNegocioGenerica("A Conta Financeira " + contaFinanceira + " não possui uma conta vigente para o exercício de " + exercicioCorrente);
            }
        } catch (Exception ex) {
            FacesUtil.addAtencao(ex.getMessage());
            return null;
        }
    }

    private ContaDeDestinacao getContaDeDestinacaoVigente() {
        try {
            ContaDeDestinacao cd = receitaExtraFacade.getContaFacade().recuperarContaDeDestinacaoPorCodigoEExercicio(contaDeDestinacao.getCodigo(), exercicioCorrente.getId());
            if (cd != null) {
                return cd;
            } else {
                throw new ExcecaoNegocioGenerica("A Fonte de Recurso" + contaDeDestinacao + " não possui uma fonte vigente para o exercício de " + exercicioCorrente);
            }
        } catch (Exception ex) {
            FacesUtil.addAtencao(ex.getMessage());
            return null;
        }
    }

    public List<ReceitaExtra> pesquisarReceitasExtras() {
        try {
            if (validaFiltros()) {
                receitaExtras = receitaExtraFacade.listaReceitaExtraParaTransportar(dtInicial, dtFinal, hierarquiaOrganizacionalFiltro.getSubordinada(), contaExtra, contaFinanceira, contaDeDestinacao, pessoa, classeCredor);

                for (ReceitaExtra receitaExtra : receitaExtras) {
                    receitaExtra.setCodigoUnidade(recuperarCodigoDaUnidade(receitaExtra));
                }
                if (!receitaExtras.isEmpty()) {
                    setarCamposParaTransporte();
                } else {
                    FacesUtil.addAtencao("Não foram localizadas receitas extraorçamentárias para os filtros informados.");
                }
                return receitaExtras;
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
        return new ArrayList<>();
    }

    private void setarCamposParaTransporte() {
        hierarquiaOrganizacional = getHierarquiaOrganizacionalVigente();
        if (contaExtra != null) {
            contaExtraTransporte = getContaExtraVigente();
        }
        if (contaFinanceira != null) {
            contaFinanceiraTransporte = getContaFinanceiraVigente();
        }
        if (contaDeDestinacaoTransporte != null) {
            contaDeDestinacaoTransporte = getContaDeDestinacaoVigente();
        }
        if (pessoa != null) {
            pessoaTransporte = pessoa;
        }
        if (classeCredor != null) {
            classeTransporte = classeCredor;
        }
        receitaExtrasSelecionadas.clear();
    }

    public void limparFiltros() {
        novoTransporte();
    }

    public void validarExercicioDataInicial(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date dataInicial = (Date) value;
        Calendar dataInicialInstance = Calendar.getInstance();
        dataInicialInstance.setTime(dataInicial);
        Integer ano = exercicio.getAno();
        if (dataInicialInstance.get(Calendar.YEAR) != ano) {
            message.setSummary("Operação não Permitida! ");
            message.setDetail(" A Data Inicial deve pertencer ao exercício selecionado no filtro.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void validarExercicioDataFinal(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date dataFinal = (Date) value;
        Calendar dataFinalInstance = Calendar.getInstance();
        dataFinalInstance.setTime(dataFinal);
        Integer ano = exercicio.getAno();
        if (dataFinalInstance.get(Calendar.YEAR) != ano) {
            message.setSummary("Operação não Permitida! ");
            message.setDetail(" A Data Final deve pertencer ao exercício selecionado no filtro.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public boolean validaFiltros() {
        boolean controle = true;
        if (exercicio == null) {
            FacesUtil.addCampoObrigatorio("O campo Exercício deve ser informado para iniciar operação.");
            return false;
        }
        if (dtInicial == null) {
            FacesUtil.addCampoObrigatorio(" A Data Inicial deve ser informada para filtrar a(s) receita(s) extraorçamentária(s).");
            controle = false;
        }
        if (dtFinal == null) {
            FacesUtil.addCampoObrigatorio(" A Data Final deve ser informada para filtrar a(s) receita(s) extraorçamentária(s).");
            controle = false;
        } else if (dtFinal.before(dtInicial)) {
            FacesUtil.addCampoObrigatorio(" A Data Final deve ser maior ou igual a Data Inicial.");
            controle = false;
        } else if (hierarquiaOrganizacionalFiltro == null) {
            FacesUtil.addCampoObrigatorio("O campo Unidade Organizacional deve ser informado para filtrar a(s) receita(s) extraorçamentária(s).");
            return false;
        }
        return controle;
    }

    public String icone(ReceitaExtra receita) {
        if (receitaExtrasSelecionadas.contains(receita)) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String title(ReceitaExtra receita) {
        if (receitaExtrasSelecionadas.contains(receita)) {
            return "Clique para deselecionar esta receita extraorçamentária.";
        }
        return "Clique para selecionar esta receita extraorçamentária.";
    }

    public String iconeTodos() {
        if (receitaExtrasSelecionadas.size() == receitaExtras.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String titleTodos() {
        if (receitaExtrasSelecionadas.size() == receitaExtras.size()) {
            return "Clique para deselecionar todas receitas extraorçamentárias.";
        }
        return "Clique para selecionar todas receitas extraorçamentárias.";
    }

    public void selecionarReceitaExtra(ReceitaExtra receita) {
        if (receitaExtrasSelecionadas.contains(receita)) {
            receitaExtrasSelecionadas.remove(receita);
        } else {
            receitaExtrasSelecionadas.add(receita);
        }
    }

    public void selecionarTodasReceitasExtras() {
        if (receitaExtrasSelecionadas.size() == receitaExtras.size()) {
            receitaExtrasSelecionadas.removeAll(receitaExtras);
        } else {
            for (ReceitaExtra rec : receitaExtras) {
                selecionarReceitaExtra(rec);
            }
        }
    }

    public BigDecimal getValorTotalReceitas() {
        BigDecimal soma = BigDecimal.ZERO;
        if (receitaExtras != null) {
            for (ReceitaExtra rec : receitaExtras) {
                soma = soma.add(rec.getValor());
            }
        }
        return soma;
    }

    public BigDecimal getValorTotalReceitasSelecionadas() {
        BigDecimal soma = BigDecimal.ZERO;
        if (receitaExtrasSelecionadas != null) {
            for (ReceitaExtra rec : receitaExtrasSelecionadas) {
                soma = soma.add(rec.getValor());
            }
        }
        return soma;
    }


    public BigDecimal getValorTotalSaldo() {
        BigDecimal soma = BigDecimal.ZERO;
        if (receitaExtras != null) {
            for (ReceitaExtra rec : receitaExtras) {
                soma = soma.add(rec.getSaldo());
            }
        }
        return soma;
    }

    public BigDecimal getValorTotalSaldoReceitasSelecionadas() {
        BigDecimal soma = BigDecimal.ZERO;
        if (receitaExtrasSelecionadas != null) {
            for (ReceitaExtra rec : receitaExtrasSelecionadas) {
                soma = soma.add(rec.getSaldo());
            }
        }
        return soma;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }


    public List<ReceitaExtra> getReceitaExtras() {
        return receitaExtras;
    }

    public void setReceitaExtras(List<ReceitaExtra> receitaExtras) {
        this.receitaExtras = receitaExtras;
    }

    public List<ReceitaExtra> getReceitaExtrasSelecionadas() {
        return receitaExtrasSelecionadas;
    }

    public void setReceitaExtrasSelecionadas(List<ReceitaExtra> receitaExtrasSelecionadas) {
        this.receitaExtrasSelecionadas = receitaExtrasSelecionadas;
    }

    public Date getDtInicial() {
        return dtInicial;
    }

    public void setDtInicial(Date dtInicial) {
        this.dtInicial = dtInicial;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public Conta getContaExtra() {
        return contaExtra;
    }

    public void setContaExtra(Conta contaExtra) {
        this.contaExtra = contaExtra;
    }

    public Exercicio getExercicioCorrente() {
        return exercicioCorrente;
    }

    public void setExercicioCorrente(Exercicio exercicioCorrente) {
        this.exercicioCorrente = exercicioCorrente;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalFiltro() {
        return hierarquiaOrganizacionalFiltro;
    }

    public void setHierarquiaOrganizacionalFiltro(HierarquiaOrganizacional hierarquiaOrganizacionalFiltro) {
        this.hierarquiaOrganizacionalFiltro = hierarquiaOrganizacionalFiltro;
    }

    public Conta getContaExtraTransporte() {
        return contaExtraTransporte;
    }

    public void setContaExtraTransporte(Conta contaExtraTransporte) {
        this.contaExtraTransporte = contaExtraTransporte;
    }

    public SubConta getContaFinanceiraTransporte() {
        return contaFinanceiraTransporte;
    }

    public void setContaFinanceiraTransporte(SubConta contaFinanceiraTransporte) {
        this.contaFinanceiraTransporte = contaFinanceiraTransporte;
    }

    public Pessoa getPessoaTransporte() {
        return pessoaTransporte;
    }

    public void setPessoaTransporte(Pessoa pessoaTransporte) {
        this.pessoaTransporte = pessoaTransporte;
    }

    public ClasseCredor getClasseTransporte() {
        return classeTransporte;
    }

    public void setClasseTransporte(ClasseCredor classeTransporte) {
        this.classeTransporte = classeTransporte;
    }

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
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

    public Date getDataTransporte() {
        return dataTransporte;
    }

    public void setDataTransporte(Date dataTransporte) {
        this.dataTransporte = dataTransporte;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public ContaDeDestinacao getContaDeDestinacaoTransporte() {
        return contaDeDestinacaoTransporte;
    }

    public void setContaDeDestinacaoTransporte(ContaDeDestinacao contaDeDestinacaoTransporte) {
        this.contaDeDestinacaoTransporte = contaDeDestinacaoTransporte;
    }
}
