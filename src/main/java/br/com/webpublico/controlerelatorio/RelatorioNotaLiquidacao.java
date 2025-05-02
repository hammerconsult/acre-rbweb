/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.AgrupadorNota;
import br.com.webpublico.negocios.LiquidacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author major
 */
@ManagedBean
@SessionScoped
public class RelatorioNotaLiquidacao extends RelatorioGenerico implements Serializable {

    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Liquidacao liquidacao;
    private ConverterAutoComplete converterEmpenho;
    private ConverterAutoComplete converterHistoricoContail;
    private ConverterGenerico converterExercicio;
    private Date dataInicial;
    private Date dataFinal;
    private Empenho empenho;
    private HistoricoContabil historicoContabil;
    private HierarquiaOrganizacional orgaoInicial;
    private HierarquiaOrganizacional orgaoFinal;
    private HierarquiaOrganizacional unidadeInicial;
    private HierarquiaOrganizacional unidadeFinal;
    private Exercicio exercicioSelecionado;
    private AgrupadorNota agrupadorNota;
    private Boolean ativaUnidade = true;

    public void fimOrgao(SelectEvent evento) {
        //System.out.println("ENTRO NA PARADA");
        orgaoFinal = (HierarquiaOrganizacional) evento.getObject();
        if (orgaoFinal.getId() == orgaoInicial.getId()) {
            ativaUnidade = false;
        }
    }

    public List<SelectItem> getListaAgrupador() {
        List<SelectItem> agr = new ArrayList<SelectItem>();
        agr.add(new SelectItem(AgrupadorNota.EMPENHO_ID, AgrupadorNota.EMPENHO_ID.getDescricao()));
        agr.add(new SelectItem(AgrupadorNota.DATALIQUIDACAO, AgrupadorNota.DATALIQUIDACAO.getDescricao()));
        agr.add(new SelectItem(AgrupadorNota.HISTORICO_ID, AgrupadorNota.HISTORICO_ID.getDescricao()));
        agr.add(new SelectItem(AgrupadorNota.ORGAO_ID, AgrupadorNota.ORGAO_ID.getDescricao()));
        agr.add(new SelectItem(AgrupadorNota.UNIDADE_ID, AgrupadorNota.UNIDADE_ID.getDescricao()));
        agr.add(new SelectItem(AgrupadorNota.EXERCICIO_ID, AgrupadorNota.EXERCICIO_ID.getDescricao()));
        return agr;
    }

    public List<SelectItem> getListaExercicios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Exercicio obj : liquidacaoFacade.getExercicioFacade().lista()) {
            toReturn.add(new SelectItem(obj, obj.getAno().toString()));
        }
        return toReturn;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, liquidacaoFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<Empenho> completaEmpenho(String parte) {
        return liquidacaoFacade.getEmpenhoFacade().buscarPorNumeroEPessoa(parte.trim(), sistemaControlador.getExercicioCorrente().getAno());
    }

    public ConverterAutoComplete getConverterEmpenho() {
        if (converterEmpenho == null) {
            converterEmpenho = new ConverterAutoComplete(Empenho.class, liquidacaoFacade.getEmpenhoFacade());
        }
        return converterEmpenho;
    }

    public List<HistoricoContabil> completaHistoricoContabil(String parte) {
        return liquidacaoFacade.getHistoricoContabilFacade().listaFiltrando(parte, "codigo", "descricao");
    }

    public ConverterAutoComplete getConverterHistoricoContabil() {
        if (converterHistoricoContail == null) {
            converterHistoricoContail = new ConverterAutoComplete(HistoricoContabil.class, liquidacaoFacade.getHistoricoContabilFacade());
        }
        return converterHistoricoContail;
    }

    public RelatorioNotaLiquidacao() {
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
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

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public HistoricoContabil getHistoricoContabil() {
        return historicoContabil;
    }

    public void setHistoricoContabil(HistoricoContabil historicoContabil) {
        this.historicoContabil = historicoContabil;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public HierarquiaOrganizacional getOrgaoFinal() {
        return orgaoFinal;
    }

    public void setOrgaoFinal(HierarquiaOrganizacional orgaoFinal) {
        this.orgaoFinal = orgaoFinal;
    }

    public HierarquiaOrganizacional getOrgaoInicial() {
        return orgaoInicial;
    }

    public void setOrgaoInicial(HierarquiaOrganizacional orgaoInicial) {
        this.orgaoInicial = orgaoInicial;
    }

    public HierarquiaOrganizacional getUnidadeFinal() {
        return unidadeFinal;
    }

    public void setUnidadeFinal(HierarquiaOrganizacional unidadeFinal) {
        this.unidadeFinal = unidadeFinal;
    }

    public HierarquiaOrganizacional getUnidadeInicial() {
        return unidadeInicial;
    }

    public void setUnidadeInicial(HierarquiaOrganizacional unidadeInicial) {
        this.unidadeInicial = unidadeInicial;
    }

    public Exercicio getExercicioSelecionado() {
        return exercicioSelecionado;
    }

    public void setExercicioSelecionado(Exercicio exercicioSelecionado) {
        this.exercicioSelecionado = exercicioSelecionado;
    }

    public AgrupadorNota getAgrupadorNota() {
        return agrupadorNota;
    }

    public void setAgrupadorNota(AgrupadorNota agrupadorNota) {
        this.agrupadorNota = agrupadorNota;
    }

    public Boolean getAtivaUnidade() {
        return ativaUnidade;
    }

    public void setAtivaUnidade(Boolean ativaUnidade) {
        this.ativaUnidade = ativaUnidade;
    }
}
