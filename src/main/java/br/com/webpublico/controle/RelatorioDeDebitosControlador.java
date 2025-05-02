/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoInscricaoContribuinte;
import br.com.webpublico.enums.TipoInscricaoRelatorioDebitos;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@ManagedBean(name = "relatorioDeDebitosControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRelatorioDebito", pattern = "/tributario/relatoriodedebitos/", viewId = "/faces/tributario/contacorrente/relatorio/relatoriodedebitos.xhtml"),
})
public class RelatorioDeDebitosControlador extends AbstractReport implements Serializable {

    @EJB
    private DividaFacade dividafacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    private SituacaoParcela[] situacoesSelecionadas;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private ConverterAutoComplete converterExercicio;
    private Date dataVencimentoInicial;
    private Date dataVencimentoFinal;
    private Divida divida;
    private ConverterAutoComplete converterDivida;
    private TipoCadastroTributario tipoCadastroTributario;
    private TipoInscricaoContribuinte tipoInscricaoContribuinte;
    private TipoInscricaoRelatorioDebitos tipoInscricaoRelatorioDebitos;
    private String cadastroInicial;
    private String cadastroFinal;
    private Pessoa pessoa;
    private ConverterAutoComplete converterPessoa;
    private BigDecimal valorMinimo;
    private BigDecimal valorMaximo;
    private StringBuilder semDados;
    private StringBuilder filtros;
    private StringBuilder where;
    private String ordemSql;
    private String ordenacao;
    private String caminho;
    private boolean desativaBtImprime = true;

    public TipoInscricaoRelatorioDebitos getTipoInscricaoRelatorioDebitos() {
        return tipoInscricaoRelatorioDebitos;
    }

    public void setTipoInscricaoRelatorioDebitos(TipoInscricaoRelatorioDebitos tipoInscricaoRelatorioDebitos) {
        this.tipoInscricaoRelatorioDebitos = tipoInscricaoRelatorioDebitos;
    }

    public TipoInscricaoContribuinte getTipoInscricaoContribuinte() {
        return tipoInscricaoContribuinte;
    }

    public void setTipoInscricaoContribuinte(TipoInscricaoContribuinte tipoInscricaoContribuinte) {
        if (tipoInscricaoContribuinte == null) {
            desativaBtImprime = true;
        } else {
            desativaBtImprime = false;
        }
        this.tipoInscricaoContribuinte = tipoInscricaoContribuinte;
    }

    public BigDecimal getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(BigDecimal valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public BigDecimal getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public SituacaoParcela[] getSituacoesSelecionadas() {
        return situacoesSelecionadas;
    }

    public void setSituacoesSelecionadas(SituacaoParcela[] situacoesSelecionadas) {
        this.situacoesSelecionadas = situacoesSelecionadas;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public StringBuilder getFiltros() {
        return filtros;
    }

    public void setFiltros(StringBuilder filtros) {
        this.filtros = filtros;
    }

    public StringBuilder getSemDados() {
        return semDados;
    }

    public void setSemDados(StringBuilder semDados) {
        this.semDados = semDados;
    }

    public StringBuilder getWhere() {
        return where;
    }

    public void setWhere(StringBuilder where) {
        this.where = where;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public DividaFacade getDividafacade() {
        return dividafacade;
    }

    public void setDividafacade(DividaFacade dividafacade) {
        this.dividafacade = dividafacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public void setExercicioFacade(ExercicioFacade exercicioFacade) {
        this.exercicioFacade = exercicioFacade;
    }

    public String getOrdemSql() {
        return ordemSql;
    }

    public void setOrdemSql(String ordemSql) {
        this.ordemSql = ordemSql;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Date getDataVencimentoFinal() {
        return dataVencimentoFinal;
    }

    public void setDataVencimentoFinal(Date dataVencimentoFinal) {
        this.dataVencimentoFinal = dataVencimentoFinal;
    }

    public Date getDataVencimentoInicial() {
        return dataVencimentoInicial;
    }

    public void setDataVencimentoInicial(Date dataVencimentoInicial) {
        this.dataVencimentoInicial = dataVencimentoInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public boolean isDesativaBtImprime() {
        return desativaBtImprime;
    }

    public void setDesativaBtImprime(boolean desativaBtImprime) {
        this.desativaBtImprime = desativaBtImprime;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, dividafacade);
        }
        return converterDivida;
    }

    public List<Pessoa> completaPessoas(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public List<Divida> completaDivida(String parte) {
        return dividafacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public RelatorioDeDebitosControlador() {
        novo();
    }

    @URLAction(mappingId = "novoRelatorioDebito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        desativaBtImprime = true;
        exercicioInicial = null;
        exercicioFinal = null;
        dataVencimentoInicial = null;
        dataVencimentoFinal = null;
        divida = null;
        tipoCadastroTributario = null;
        tipoInscricaoContribuinte = null;
        tipoInscricaoRelatorioDebitos = null;
        pessoa = null;
        where = new StringBuilder();
        situacoesSelecionadas = null;
        valorMinimo = new BigDecimal(0);
        valorMaximo = new BigDecimal(99999999);
        cadastroFinal = "999999999999999999";
        cadastroInicial = "1";
        limpaCadastro();
    }

    private boolean validaCampos() {
        boolean retorno = true;

        if (tipoInscricaoContribuinte == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:tipocadastro", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar.", "Selecione o tipo de cadastro."));
            retorno = false;
        } else if (tipoInscricaoRelatorioDebitos == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar.", "Selecione ao menos uma Inscrição na lista."));
            retorno = false;
        }
        if (exercicioInicial == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:exercicioinicialfinal", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar.", "Informe o exercicio inicial."));
            retorno = false;
        }
        return retorno;
    }

    public void montaRelatorio() throws JRException, IOException {
        if (validaCampos()) {

        }
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        String caminhoBrasao = getCaminhoImagem();
        semDados = new StringBuilder("NÃO FOI ENCONTRADO NENHUM REGISTRO PARA O FILTRO SOLICITADO!");
        String arquivoJasper = "RelatorioDeDebitos.jasper";
        if (tipoInscricaoContribuinte == TipoInscricaoContribuinte.INSCRICAO) {
            arquivoJasper = "RelatorioDeDebitos.jasper";
        } else if (tipoInscricaoContribuinte == TipoInscricaoContribuinte.CONTRIBUINTE) {
            arquivoJasper = "RelatorioDeDebitosContribuinte.jasper";
        }
        HashMap parameters = new HashMap();
        ;
        montaCondicao();
        parameters.put("WHERE", where.toString());
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("BRASAO", caminhoBrasao);
        parameters.put("USUARIO", this.usuarioLogado().getUsername());
        parameters.put("SEMDADOS", semDados.toString());
        parameters.put("FILTROS", filtros.toString());
        gerarRelatorio(arquivoJasper, parameters);
    }

    private void montaCondicao() {
        where = new StringBuilder();
        String juncao = " and ";
        filtros = new StringBuilder();
        filtros.append("Filtros: ");

        if (tipoInscricaoContribuinte != null) {
            if (tipoInscricaoContribuinte.equals(TipoInscricaoContribuinte.CONTRIBUINTE)) {
                if (pessoa != null && pessoa.getId() != null) {
                    this.where.append(juncao).append("cpes.pessoa_id =").append(pessoa.getId());
                    juncao = " and ";
                    filtros.append(" Contribuinte = ").append(pessoa.getId()).append("; ");
                }
            } else if (tipoInscricaoContribuinte.equals(TipoInscricaoContribuinte.INSCRICAO)) {
                if (tipoInscricaoRelatorioDebitos != null) {
                    if (tipoInscricaoRelatorioDebitos.equals(TipoInscricaoRelatorioDebitos.ECONOMICO)) {
                        if (!cadastroInicial.isEmpty()) {
                            this.where.append(juncao).append(" cmc.inscricaocadastral >= '").append(cadastroInicial).append("'");
                            juncao = " and ";
                            filtros.append("Cadastro Inicial = ").append(cadastroInicial).append("; ");
                        }
                        if (!cadastroFinal.isEmpty()) {
                            this.where.append(juncao).append(" cmc.inscricaocadastral <= '").append(cadastroFinal).append("'");
                            juncao = " and ";
                            filtros.append("Cadastro Final = ").append(cadastroFinal).append("; ");
                        }
                    } else if (tipoInscricaoRelatorioDebitos.equals(TipoInscricaoRelatorioDebitos.IMOBILIARIO)) {
                        if (!cadastroInicial.isEmpty()) {
                            this.where.append(juncao).append(" bci.inscricaocadastral >= '").append(cadastroInicial).append("'");
                            juncao = " and ";
                            filtros.append("Cadastro Inicial = ").append(cadastroInicial).append("; ");
                        }
                        if (!cadastroFinal.isEmpty()) {
                            this.where.append(juncao).append(" bci.inscricaocadastral <= '").append(cadastroFinal).append("'");
                            juncao = " and ";
                            filtros.append("Cadastro Final = ").append(cadastroFinal).append("; ");
                        }
                    } else if (tipoInscricaoRelatorioDebitos.equals(TipoInscricaoRelatorioDebitos.RURAL)) {
                        if (!cadastroInicial.isEmpty()) {
                            this.where.append(juncao).append(" calculo.cadastro_id in (select id from cadastrorural where codigo >= ").append(cadastroInicial).append(")");
                            juncao = " and ";
                            filtros.append("Cadastro Inicial = ").append(cadastroInicial).append("; ");
                        }
                        if (!cadastroFinal.isEmpty()) {
                            this.where.append(juncao).append(" calculo.cadastro_id in (select id from cadastrorural where codigo <= ").append(cadastroFinal).append(")");
                            juncao = " and ";
                            filtros.append("Cadastro Final = ").append(cadastroFinal).append("; ");
                        }
                    }
                }
            }
        }

        if (exercicioInicial != null) {
            this.where.append(juncao).append(" exe.ano >= ").append(exercicioInicial.getAno());
            juncao = " and ";
            filtros.append(" Exercício inicial = ").append(exercicioInicial.getAno()).append("; ");
        }
        if (exercicioFinal != null) {
            this.where.append(juncao).append(" exe.ano <= ").append(exercicioFinal.getAno());
            juncao = " and ";
            filtros.append(" Exercício final = ").append(exercicioFinal.getAno()).append("; ");
        }
        if (dataVencimentoInicial != null) {
            this.where.append(juncao).append(" pvd.vencimento >= to_date(").append(formataData(dataVencimentoInicial)).append(",'dd/MM/yyyy') ");
            juncao = " and ";
            filtros.append(" Data de Vencimento Inicial = ").append(dataVencimentoInicial).append("; ");
        }
        if (dataVencimentoFinal != null) {
            this.where.append(juncao).append(" pvd.vencimento <= to_date(").append(formataData(dataVencimentoFinal)).append(",'dd/MM/yyyy')");
            juncao = " and ";
            filtros.append(" Data de Vencimento Final = ").append(dataVencimentoFinal).append("; ");
        }
        if (divida != null) {
            this.where.append(juncao).append(" divida.id = ").append(divida.getId());
            juncao = " and ";
            filtros.append(" Dívida = ").append(divida.getDescricao()).append("; ");
        }

        if (situacoesSelecionadas.length > 0) {
            this.where.append(juncao).append(" spv.situacaoparcela in (").append(situacoesMarcadas()).append(") ");
            filtros.append(" Situações = ").append(situacoesMarcadas()).append("; ");
        }

        if (valorMinimo != null && valorMinimo.compareTo(BigDecimal.ZERO) >= 0) {
            this.where.append(juncao).append(" (coalesce(pvd.valor,0)+coalesce(itemlb.juro,0)+coalesce(itemlb.multa,0)+coalesce(itemlb.correcao,0)) >= ").append(valorMinimo);
            juncao = " and ";
            filtros.append(" Valor Mínimo = ").append(valorMinimo).append("; ");
        }
        if (valorMaximo != null && valorMaximo.compareTo(BigDecimal.ZERO) >= 0) {
            this.where.append(juncao).append("(coalesce(pvd.valor,0)+coalesce(itemlb.juro,0)+coalesce(itemlb.multa,0)+coalesce(itemlb.correcao,0)) <= ").append(valorMaximo);
            juncao = " and ";
            filtros.append(" Valor Maximo = ").append(valorMaximo).append("; ");
        }

        //System.out.println("where...: " + where.toString());
    }

    public List<SelectItem> getTiposInscricaoContribuinte() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoInscricaoContribuinte tipo : TipoInscricaoContribuinte.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposInscricaoRelatorioDebitos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoInscricaoRelatorioDebitos tipo : TipoInscricaoRelatorioDebitos.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public boolean mostrarContribuinte() {
        boolean retorno = false;
        if (tipoInscricaoContribuinte != null) {
            retorno = tipoInscricaoContribuinte.equals(TipoInscricaoContribuinte.CONTRIBUINTE);
        }
        return retorno;
    }

    public boolean mostrarInscricao() {
        boolean retorno = false;
        if (tipoInscricaoContribuinte != null) {
            retorno = tipoInscricaoContribuinte.equals(TipoInscricaoContribuinte.INSCRICAO);
        }
        return retorno;
    }

    public boolean mostrarCadastro() {
        boolean retorno = false;
        if (tipoInscricaoRelatorioDebitos != null) {
            retorno = tipoInscricaoRelatorioDebitos.equals(TipoInscricaoRelatorioDebitos.ECONOMICO)
                    || tipoInscricaoRelatorioDebitos.equals(TipoInscricaoRelatorioDebitos.IMOBILIARIO)
                    || tipoInscricaoRelatorioDebitos.equals(TipoInscricaoRelatorioDebitos.RURAL);
        }
        return retorno;
    }

    public List<SituacaoParcela> getListaSituacoes() {
        List<SituacaoParcela> retorno = new ArrayList<SituacaoParcela>();
        for (SituacaoParcela s : SituacaoParcela.getValues()) {
            retorno.add(s);
        }
        return retorno;
    }

    private String situacoesMarcadas() {
        StringBuilder sb = new StringBuilder();
        for (SituacaoParcela sp : situacoesSelecionadas) {
            sb.append("'").append(sp).append("',");
        }
        return sb.substring(0, sb.length() - 1).toString();
    }

    public void limpaCadastro() {
    }

    private String formataData(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "'" + sdf.format(data) + "'";
    }

}
