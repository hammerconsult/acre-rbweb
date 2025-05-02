/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.OpcaoPagamentoFacade;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jefferson
 */
@ManagedBean(name = "relatorioSituacaoParcelamentoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRelatorioSituacaoParcelmanto", pattern = "/tributario/contacorrente/relatoriosituacaoparcelamento/", viewId = "/faces/tributario/relatorio/relatoriosituacaoparcelamento.xhtml"),
})

public class RelatorioSituacaoParcelamentoControlador extends AbstractReport implements Serializable {

    private StringBuilder filtro;
    private StringBuilder semDados;
    private StringBuilder where;
    @Limpavel(Limpavel.NULO)
    private Exercicio exercicioInicial;
    @Limpavel(Limpavel.NULO)
    private Exercicio exercicioFinal;
    @Limpavel(Limpavel.NULO)
    private Date dataParcelamentoInicial;
    @Limpavel(Limpavel.NULO)
    private Date dataParcelamentoFinal;
    @Limpavel(Limpavel.NULO)
    private Date dataVencimentoInicial;
    @Limpavel(Limpavel.NULO)
    private Date dataVencimentoFinal;
    @Limpavel(Limpavel.NULO)
    private String cadastroInicial;
    @Limpavel(Limpavel.NULO)
    private String cadastroFinal;
    @Limpavel(Limpavel.NULO)
    private SituacaoParcela[] situacoesSelecionadas;
    @Limpavel(Limpavel.NULO)
    private TipoCadastroTributario tipoCadastroTributario;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    private ConverterAutoComplete converterCadastroEconomico;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private ConverterAutoComplete converterCadastroImobiliario;
    private Converter converterExercicio;
    @EJB
    private OpcaoPagamentoFacade opcaoPagamentoFacade;
    @Limpavel(Limpavel.NULO)
    private OpcaoPagamento[] opcaoPagamentoSelecionados;
    private String ordenacao;
    private String ordemSql;

    public RelatorioSituacaoParcelamentoControlador() {
        ordenacao = "S";
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

    public OpcaoPagamento[] getOpcaoPagamentoSelecionados() {
        return opcaoPagamentoSelecionados;
    }

    public void setOpcaoPagamentoSelecionados(OpcaoPagamento[] opcaoPagamentoSelecionados) {
        this.opcaoPagamentoSelecionados = opcaoPagamentoSelecionados;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public SituacaoParcela[] getSituacoesSelecionadas() {
        return situacoesSelecionadas;
    }

    public void setSituacoesSelecionadas(SituacaoParcela[] situacoesSelecionadas) {
        this.situacoesSelecionadas = situacoesSelecionadas;
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

    public Date getDataParcelamentoFinal() {
        return dataParcelamentoFinal;
    }

    public void setDataParcelamentoFinal(Date dataParcelamentoFinal) {
        this.dataParcelamentoFinal = dataParcelamentoFinal;
    }

    public Date getDataParcelamentoInicial() {
        return dataParcelamentoInicial;
    }

    public void setDataParcelamentoInicial(Date dataParcelamentoInicial) {
        this.dataParcelamentoInicial = dataParcelamentoInicial;
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

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public void montaCondicao() {


        String juncao = " where ";
        ordemSql = "";

//        if (inscricaoCadastralInicial != null) {
//            this.where.append(juncao).append(" to_number(ce.inscricaocadastral) >= ").append(inscricaoCadastralInicial);
//            juncao = " and ";
//        }

        if (tipoCadastroTributario != null && tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
            filtro.append("Tipo de Cadastro Economico; ");
            if (cadastroInicial != null && cadastroFinal == null) {
                this.where.append(juncao).append(" cmc.inscricaocadastral = ").append(cadastroInicial);
                juncao = " and ";
                filtro.append("Cadastro Inicial = ").append(cadastroInicial).append("; ");
            } else if (cadastroFinal != null && cadastroInicial == null) {
                this.where.append(juncao).append(" cmc.inscricaocadastral = ").append(cadastroFinal);
                juncao = " and ";
                filtro.append("Cadastro Final = ").append(cadastroFinal).append("; ");
            } else if (cadastroInicial != null && cadastroFinal != null) {
                this.where.append(juncao).append(" cmc.inscricaocadastral between ").append(cadastroInicial).append(" and ").append(cadastroFinal);
                juncao = " and ";
                filtro.append("Cadastro Inicial = ").append(cadastroInicial).append(" e Cadastro Final = ").append(cadastroFinal).append("; ");
            }
        } else if (tipoCadastroTributario != null && tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
            filtro.append("Tipo de Cadastro Imobiliario; ");
            if (cadastroInicial != null && cadastroFinal == null) {
                this.where.append(juncao).append(" imo.inscricaoCadastral = ").append(cadastroInicial);
                juncao = " and ";
                filtro.append("Cadastro Inicial = ").append(cadastroInicial).append("; ");
            } else if (cadastroFinal != null && cadastroInicial == null) {
                this.where.append(juncao).append(" imo.inscricaoCadastral = ").append(cadastroFinal);
                juncao = " and ";
                filtro.append("Cadastro Final = ").append(cadastroFinal).append("; ");
            } else if (cadastroInicial != null && cadastroFinal != null) {
                this.where.append(juncao).append(" imo.inscricaoCadastral between ").append(cadastroInicial).append(" and ").append(cadastroFinal);
                juncao = " and ";
                filtro.append("Cadastro Inicial = ").append(cadastroInicial).append(" e Cadastro Final = ").append(cadastroFinal).append("; ");
            }
        }
        // data de Parcelamento Inicial e Final
        if (dataParcelamentoInicial != null && dataParcelamentoFinal == null) {
            this.where.append(juncao).append(" proc.dataprocessoparcelamento = to_date(").append(formataData(dataParcelamentoInicial)).append(", 'dd/MM/yyyy')");
            juncao = " and ";
            filtro.append("Data de Parcelamento Inicial = ").append(formataData(dataParcelamentoInicial)).append("; ");
        } else if (dataParcelamentoFinal != null && dataParcelamentoInicial == null) {
            this.where.append(juncao).append(" proc.dataprocessoparcelamento = to_date(").append(formataData(dataParcelamentoFinal)).append(", 'dd/MM/yyyy')");
            juncao = " and ";
            filtro.append("Data de Parcelamento Final = ").append(formataData(dataParcelamentoFinal)).append("; ");
        } else if (dataParcelamentoInicial != null && dataParcelamentoFinal != null) {
            inverteDataParcelamento();
            this.where.append(juncao).append(" proc.dataprocessoparcelamento between to_date(").append(formataData(dataParcelamentoInicial)).append(", 'dd/MM/yyyy')").append(" and to_date(").append(formataData(dataParcelamentoFinal)).append(", 'dd/MM/yyyy')");
            juncao = " and ";
            filtro.append("Data de parcelamento Inicial = ").append(formataData(dataParcelamentoInicial)).append(" e Data de Parcelamento Final = ").append(formataData(dataParcelamentoFinal)).append("; ");
        }
        // data de Vencimento Inicial e Final
        if (dataVencimentoInicial != null && dataVencimentoFinal == null) {
            this.where.append(juncao).append(" pvd.vencimento = to_date(").append(formataData(dataVencimentoInicial)).append(", 'dd/MM/yyyy')");
            juncao = " and ";
            filtro.append("Data de Vencimento Inicial = ").append(formataData(dataVencimentoInicial)).append("; ");
        } else if (dataVencimentoFinal != null && dataVencimentoInicial == null) {
            this.where.append(juncao).append(" pvd.vencimento  = to_date(").append(formataData(dataVencimentoFinal)).append(", 'dd/MM/yyyy')");
            ;
            juncao = " and ";
            filtro.append("Data de Vencimento Final = ").append(formataData(dataVencimentoFinal)).append("; ");
        } else if (dataVencimentoInicial != null && dataVencimentoFinal != null) {
            inverteDataVencimento();
            this.where.append(juncao).append(" pvd.vencimento between to_date(").append(formataData(dataVencimentoInicial)).append(", 'dd/MM/yyyy')").append(" and to_date(").append(formataData(dataVencimentoFinal)).append(", 'dd/MM/yyyy')");
            juncao = " and ";
            filtro.append("Data de Vencimento Inicial = ").append(formataData(dataVencimentoInicial)).append("e Data de Vencimento Final = ").append(formataData(dataVencimentoFinal)).append("; ");
        }

        if (exercicioInicial != null && exercicioFinal == null) {
            this.where.append(juncao).append(" exe.ano = ").append(exercicioInicial.getAno());
            juncao = " and ";
            filtro.append("Exercício Inicial = ").append(exercicioInicial.getAno()).append("; ");
        } else if (exercicioFinal != null && exercicioInicial == null) {
            this.where.append(juncao).append(" exe.ano = ").append(exercicioFinal.getAno());
            juncao = " and ";
            filtro.append("Exercício Final = ").append(exercicioFinal.getAno()).append("; ");
        } else if (exercicioInicial != null && exercicioFinal != null) {
            inverteAno();
            this.where.append(juncao).append(" exe.ano between ").append(exercicioInicial.getAno()).append(" and ").append(exercicioFinal.getAno());
            juncao = " and ";
            filtro.append("Exercício Inicial = ").append(exercicioInicial.getAno()).append("e Exercício Final = ").append(exercicioFinal.getAno()).append("; ");
        }

        if (opcaoPagamentoSelecionados.length > 0) {
            this.where.append(juncao).append(" op.id in (").append(opcaoPagamentoMarcados()).append(") ");
            filtro.append("Descrição da Dívida = ").append(opcaoPagamentoMarcados()).append("; ");
        }

        if (situacoesSelecionadas.length > 0) {
            this.where.append(juncao).append(" (select situacaoparcela from situacaoparcelavalordivida "
                    + " where id =(select max(id) from situacaoparcelavalordivida "
                    + " where parcela_id =pvd.id)) in (").append(situacoesMarcadas()).append(") ");
            filtro.append("Situação da Parcela = ").append(situacoesMarcadas()).append("; ");
        }

        switch (ordenacao) {
            case "P":
                ordemSql = " proc.numero ";
                break;
            case "I":
                ordemSql = " case tpcad.codigo when 1 then imo.codigo when 2 then cmc.inscricaocadastral else null end";
                break;
            case "C":
                ordemSql = " case tpcad.codigo when 1 then pesf.nome when 2 then pesfcmc.nome end";
                break;
            default:
                break;
        }

        if (!ordemSql.equals("")) {
            ordemSql = " order by " + ordemSql;
        } else if (ordemSql.equals("")) {
            ordemSql = " order by pvd.id ";
        }
    }


    @URLAction(mappingId = "novoRelatorioSituacaoParcelmanto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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

        String arquivoJasper = "SituacaodosParcelamentos.jasper";

        HashMap parameters = new HashMap();
        montaCondicao();
        //System.out.println("montacondicao" + where.toString());
        parameters.put("WHERE", where.toString());
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("BRASAO", caminhoBrasao);
        parameters.put("SEMDADOS", semDados.toString());
        parameters.put("FILTRO", filtro.toString());
        parameters.put("USUARIO", this.usuarioLogado().getUsername());
        parameters.put("ORDER", ordemSql);
        gerarRelatorio(arquivoJasper, parameters);
    }

    public List<SituacaoParcela> getListaSituacoes() {
        List<SituacaoParcela> retorno = new ArrayList<SituacaoParcela>();
        for (SituacaoParcela s : SituacaoParcela.getValues()) {
            retorno.add(s);
        }
        return retorno;
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

    private void inverteDataParcelamento() {
        if (dataParcelamentoInicial.compareTo(dataParcelamentoFinal) > 0) {
            Date aux = dataParcelamentoInicial;
            dataParcelamentoInicial = dataParcelamentoFinal;
            dataParcelamentoFinal = aux;
        }
    }

    private void inverteDataVencimento() {
        if (dataVencimentoInicial.compareTo(dataVencimentoFinal) > 0) {
            Date aux = dataVencimentoInicial;
            dataVencimentoInicial = dataVencimentoFinal;
            dataVencimentoFinal = aux;
        }
    }

    private void inverteAno() {
        if (exercicioInicial.getAno().compareTo(exercicioFinal.getAno()) > 0) {
            Exercicio aux = exercicioInicial;
            exercicioInicial = exercicioFinal;
            exercicioFinal = aux;
        }
    }

    private String formataData(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "'" + sdf.format(data) + "'";
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

    private String situacoesMarcadas() {
        StringBuilder sb = new StringBuilder();
        for (SituacaoParcela sp : situacoesSelecionadas) {
            sb.append("'").append(sp).append("',");
        }
        return sb.substring(0, sb.length() - 1).toString();
    }

    public void copiaCadastroInicialParaCadastroFinal() {
        this.cadastroFinal = this.cadastroInicial;
    }
}
