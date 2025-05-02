/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jefferson
 */


@ManagedBean(name = "saldoContaCorrenteContribuinteControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRelatorioContaCorrenteContribuinte", pattern = "/tributario/conta-corrente/saldo-em-conta-corrente-do-contribuinte/", viewId = "/faces/tributario/relatorio/saldocontacorrentecontribuinte.xhtml"),
})

public class SaldoContaCorrenteContribuinteControlador extends AbstractReport implements Serializable {

    @Limpavel(Limpavel.NULO)
    private String cadastroInicial;
    @Limpavel(Limpavel.NULO)
    private String cadastroFinal;
    @Limpavel(Limpavel.NULO)
    private TipoCadastroTributario tipoCadastroTributario;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterCadastroImobiliario;
    @Limpavel(Limpavel.NULO)
    private Pessoa pessoa;
    @EJB
    private PessoaFacade pessoaFacade;
    private ConverterAutoComplete converterPessoa;
    @Limpavel(Limpavel.NULO)
    private Exercicio exercicioInicial;
    @Limpavel(Limpavel.NULO)
    private Exercicio exercicioFinal;
    @EJB
    private ExercicioFacade exercicioFacade;
    private Converter converterExercicio;
    @Limpavel(Limpavel.NULO)
    private TipoSaldo tipoSaldo;
    @EJB
    private OpcaoPagamentoFacade opcaoPagamentoFacade;
    @Limpavel(Limpavel.NULO)
    private OpcaoPagamento[] opcaoPagamentoSelecionados;
    private StringBuilder where;
    private StringBuilder filtro;
    private StringBuilder semDados;
    private String ordenacao;
    private String ordemSql;

    public TipoSaldo getTipoSaldo() {
        return tipoSaldo;
    }

    public void setTipoSaldo(TipoSaldo tipoSaldo) {
        this.tipoSaldo = tipoSaldo;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
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

    public OpcaoPagamento[] getOpcaoPagamentoSelecionados() {
        return opcaoPagamentoSelecionados;
    }

    public void setOpcaoPagamentoSelecionados(OpcaoPagamento[] opcaoPagamentoSelecionados) {
        this.opcaoPagamentoSelecionados = opcaoPagamentoSelecionados;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
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

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public String getOrdemSql() {
        return ordemSql;
    }

    public void setOrdemSql(String ordemSql) {
        this.ordemSql = ordemSql;
    }

    public SaldoContaCorrenteContribuinteControlador() {
        ordenacao = "S";
    }

    public void montaCondicao() {

        String juncao = " where ";
        ordemSql = "";


        if (pessoa != null && pessoa.getId() != null) {
            this.where.append(juncao).append(" coalesce(case when imo.codigo is null "
                    + "then pesfcmc.id else pesf.id end,pesj.id)  = ").append(pessoa.getId());
            juncao = " and ";
        }

        if (tipoCadastroTributario != null && tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
            filtro.append("Tipo de Cadastro Economico; ");
            if (cadastroInicial != null && cadastroFinal == null) {
                this.where.append(juncao).append(" cmc.inscricaocadastral = '").append(cadastroInicial).append("'");
                juncao = " and ";
                filtro.append("Cadastro Inicial = ").append(cadastroInicial).append("; ");
            } else if (cadastroFinal != null && cadastroInicial == null) {
                this.where.append(juncao).append(" cmc.inscricaocadastral = '").append(cadastroFinal).append("'");
                juncao = " and ";
                filtro.append("Cadastro Final = ").append(cadastroFinal).append("; ");
            } else if (cadastroInicial != null && cadastroFinal != null) {
                this.where.append(juncao).append(" cmc.inscricaocadastral between '").append(cadastroInicial).append("' and '").append(cadastroFinal).append("'");
                juncao = " and ";
                filtro.append("Cadastro Inicial = ").append(cadastroInicial).append(" e Cadastro Final = ").append(cadastroFinal).append("; ");
            }
        } else if (tipoCadastroTributario != null && tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
            filtro.append("Tipo de Cadastro Imobiliario; ");
            if (cadastroInicial != null && cadastroFinal == null) {
                this.where.append(juncao).append(" imo.inscricaocadastral = '").append(cadastroInicial).append("'");
                juncao = " and ";
                filtro.append("Cadastro Inicial = ").append(cadastroInicial).append("; ");
            } else if (cadastroFinal != null && cadastroInicial == null) {
                this.where.append(juncao).append(" imo.inscricaocadastral = '").append(cadastroFinal).append("'");
                juncao = " and ";
                filtro.append("Cadastro Final = ").append(cadastroFinal).append("; ");
            } else if (cadastroInicial != null && cadastroFinal != null) {
                this.where.append(juncao).append(" imo.inscricaocadastral between '").append(cadastroInicial).append("' and '").append(cadastroFinal).append("'");
                juncao = " and ";
                filtro.append("Cadastro Inicial entre ").append(cadastroInicial).append(" e Cadastro Final = ").append(cadastroFinal).append("; ");
            }
        }

        if (exercicioInicial != null && exercicioFinal == null) {
            this.where.append(juncao).append(" exe.ano >= ").append(exercicioInicial.getAno());
            juncao = " and ";
            filtro.append("Exercício Inicial maior ou = ").append(exercicioInicial.getAno()).append("; ");
        } else if (exercicioFinal != null && exercicioInicial == null) {
            this.where.append(juncao).append(" exe.ano <= ").append(exercicioFinal.getAno());
            juncao = " and ";
            filtro.append("Exercício Final menor ou = ").append(exercicioFinal.getAno()).append("; ");
        } else if (exercicioInicial != null && exercicioFinal != null) {
            inverteAno();
            this.where.append(juncao).append(" exe.ano between ").append(exercicioInicial.getAno()).append(" and ").append(exercicioFinal.getAno());
            juncao = " and ";
            filtro.append("Exercício Inicial entre ").append(exercicioInicial.getAno()).append("e Exercício Final = ").append(exercicioFinal.getAno()).append("; ");
        }

        if (tipoSaldo != null && tipoSaldo.equals(TipoSaldo.PAGAMENTO)) {
            this.where.append(juncao).append(" coalesce(trunc(item.valorpago,2),0) <> 0 ");
            juncao = " and ";
            filtro.append("Saldo sobre pagamento; ");
        } else if (tipoSaldo != null && tipoSaldo.equals(TipoSaldo.LANCAMENTO)) {
            this.where.append(juncao).append(" coalesce(trunc(item.valorpago,2),0) = 0 ");
            juncao = " and ";
            filtro.append("Saldo sobre lançamento; ");
        }


        if (opcaoPagamentoSelecionados.length > 0) {
            this.where.append(juncao).append(" op.id in (").append(opcaoPagamentoMarcados()).append(") ");
            juncao = " and ";
        }

        switch (ordenacao) {
            case "E":
                ordemSql = " exe.ano ";
                break;
            case "I":
                ordemSql = " case dv.tipocadastro when 'IMOBILIARIO' then imo.codigo when 'ECONOMICO' then cmc.inscricaocadastral end";
                break;
            case "C":
                ordemSql = " coalesce(case when imo.codigo is null then pesfcmc.nome else pesf.nome end, pesj.nomereduzido)";
                break;
            default:
                break;
        }

        if (!ordemSql.equals("")) {
            ordemSql = " order by " + ordemSql;
        } else if (ordemSql.equals("")) {
            ordemSql = " order by case when imo.codigo is null then pesfcmc.nome else pesf.nome end ";
        }

    }

    @URLAction(mappingId = "novoRelatorioContaCorrenteContribuinte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        ordenacao = "S";
        Util.limparCampos(this);
        this.cadastroInicial = "1";
        this.cadastroFinal = "999999999999999999";
    }

    public void gerarRelatorio() throws JRException, IOException {

        where = new StringBuilder();
        semDados = new StringBuilder("Não foram encontrados registros");
        filtro = new StringBuilder();

        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        String caminhoBrasao = getCaminhoImagem();

        String arquivoJasper = "SaldoContaCorrenteDoContribuinteBoletim.jasper";

        HashMap parameters = new HashMap();
        montaCondicao();
        //System.out.println("where   = " + where.toString());
        //System.out.println("orderby = " + ordemSql.toString());
        parameters.put("WHERE", where.toString());
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("BRASAO", caminhoBrasao);
        parameters.put("SEMDADOS", semDados.toString());
        parameters.put("FILTRO", filtro.toString());
        parameters.put("USUARIO", this.usuarioLogado().getUsername());
        parameters.put("ORDER", ordemSql.toString());
        gerarRelatorio(arquivoJasper, parameters);
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "TODOS"));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            if (tipo.equals(TipoCadastroTributario.ECONOMICO) || tipo.equals(TipoCadastroTributario.IMOBILIARIO)) {
                toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return cadastroEconomicoFacade.listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "codigo");
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, cadastroEconomicoFacade);
        }
        return converterCadastroEconomico;
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (Exercicio ex : exercicioFacade.lista()) {
            lista.add(new SelectItem(ex, ex.getAno().toString()));
        }
        return lista;
    }

    public List<SelectItem> getTiposSaldo() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoSaldo ts : TipoSaldo.values()) {
            lista.add(new SelectItem(ts, ts.getDescricao()));
        }

        return lista;
    }

    private void inverteAno() {
        if (exercicioInicial.getAno().compareTo(exercicioFinal.getAno()) > 0) {
            Exercicio aux = exercicioInicial;
            exercicioInicial = exercicioFinal;
            exercicioFinal = aux;
        }
    }

    public List<OpcaoPagamento> listaOpcaoPagamento() {
        return opcaoPagamentoFacade.lista();
    }

    private String opcaoPagamentoMarcados() {
        StringBuilder sb = new StringBuilder();
        for (OpcaoPagamento op : opcaoPagamentoSelecionados) {
            sb.append(op.getId()).append(",");
        }
        return sb.substring(0, sb.length() - 1).toString();
    }

    //Criando um enum//
    private enum TipoSaldo {

        PAGAMENTO("Por Pagamento"),
        LANCAMENTO("Por Lançamento");

        private TipoSaldo(String descricao) {
            this.descricao = descricao;
        }

        private String descricao;

        public String getDescricao() {
            return descricao;
        }
    }

    public void copiarCadastroInicialParaCadastroFinal() {
        //System.out.println("teste");
        this.cadastroFinal = this.cadastroInicial;
    }
}
