/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.entidadesauxiliares.ConsultaPagamento;
import br.com.webpublico.relatoriofacade.RelatorioMaioresContribuintesFacade;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Claudio
 */
@ManagedBean(name = "relatorioMaioresContribuintesControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMaioresContribuintes", pattern = "/relatorio-de-maiores-contribuintes/",
                viewId = "/faces/tributario/contacorrente/relatorio/maiorescontribuintes.xhtml"),})
public class RelatorioMaioresContribuintesControlador extends AbstractReport implements Serializable {

    @EJB
    private RelatorioMaioresContribuintesFacade relatorioMaioresContribuintesFacade;
    @Limpavel(Limpavel.NULO)
    private List<ConsultaPagamento> lista;
    @Limpavel(Limpavel.NULO)
    private List<Divida> dividasSelecionadas;
    @Enumerated(EnumType.STRING)
    @Limpavel(Limpavel.NULO)
    private TipoCadastroTributario tipoCadastroTributario;
    @Limpavel(Limpavel.NULO)
    private Integer exercicioInicial;
    @Limpavel(Limpavel.NULO)
    private Integer exercicioFinal;
    @Limpavel(Limpavel.NULO)
    private Date dataPagamentoInicial;
    @Limpavel(Limpavel.NULO)
    private Date dataPagamentoFinal;
    @Limpavel(Limpavel.ZERO)
    private Integer quantidadeContribuinte = 0;
    @Limpavel(Limpavel.VERDADEIRO)
    private Boolean situacaoDoExercicio = true;
    @Limpavel(Limpavel.VERDADEIRO)
    private Boolean situacaoDividaAtiva = true;
    @Limpavel(Limpavel.VERDADEIRO)
    private Boolean situacaoDividaAtivaAjuizada = true;
    private ConverterExercicio converterExercicio;
    @Enumerated(EnumType.STRING)
    @Limpavel(Limpavel.NULO)
    private ClassificacaoAtividade classificacaoAtividade;
    private ConverterGenerico converterDivida;

    public ClassificacaoAtividade getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public List<SelectItem> getListaClassificacaoAtividade() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, " "));
        for (ClassificacaoAtividade ca : ClassificacaoAtividade.values()) {
            lista.add(new SelectItem(ca, ca.getDescricao()));
        }
        return lista;
    }

    public void setClassificacaoAtividade(ClassificacaoAtividade classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public List<ConsultaPagamento> getLista() {
        return lista;
    }

    public void setLista(List<ConsultaPagamento> lista) {
        this.lista = lista;
    }

    public Date getDataPagamentoFinal() {
        return dataPagamentoFinal;
    }

    public void setDataPagamentoFinal(Date dataPagamentoFinal) {
        this.dataPagamentoFinal = dataPagamentoFinal;
    }

    public Date getDataPagamentoInicial() {
        return dataPagamentoInicial;
    }

    public void setDataPagamentoInicial(Date dataPagamentoInicial) {
        this.dataPagamentoInicial = dataPagamentoInicial;
    }

    public Integer getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Integer exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Integer getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Integer exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Integer getQuantidadeContribuinte() {
        return quantidadeContribuinte;
    }

    public void setQuantidadeContribuinte(Integer quantidadeContribuinte) {
        this.quantidadeContribuinte = quantidadeContribuinte;
    }

    public Boolean getSituacaoDividaAtiva() {
        return situacaoDividaAtiva;
    }

    public void setSituacaoDividaAtiva(Boolean situacaoDividaAtiva) {
        this.situacaoDividaAtiva = situacaoDividaAtiva;
    }

    public Boolean getSituacaoDividaAtivaAjuizada() {
        return situacaoDividaAtivaAjuizada;
    }

    public void setSituacaoDividaAtivaAjuizada(Boolean situacaoDividaAtivaAjuizada) {
        this.situacaoDividaAtivaAjuizada = situacaoDividaAtivaAjuizada;
    }

    public Boolean getSituacaoDoExercicio() {
        return situacaoDoExercicio;
    }

    public void setSituacaoDoExercicio(Boolean situacaoDoExercicio) {
        this.situacaoDoExercicio = situacaoDoExercicio;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    @URLAction(mappingId = "novoMaioresContribuintes", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        Util.limparCampos(this);
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(relatorioMaioresContribuintesFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public void gerarRelatorioMaioresContribuintes() throws JRException, IOException {

        if (validaCampos()) {
            lista = relatorioMaioresContribuintesFacade.listaParcelaPagamentoPorFiltroETipoDeCadastro(exercicioInicial.toString(),
                    exercicioFinal.toString(), dataPagamentoInicial, dataPagamentoFinal,
                    getIdsDividasSelecionadas(), situacaoDividaAtiva, situacaoDividaAtivaAjuizada,
                    situacaoDoExercicio, quantidadeContribuinte, classificacaoAtividade);
            String arquivoJasper = "RelatorioDeMaioresContribuintes.jasper";
            HashMap parameters = new HashMap();
            parameters.put("BRASAO", getCaminhoImagem());
            geraNoDialog = true;
            gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, new JRBeanCollectionDataSource(lista));
        }
    }

    public boolean validaCampos() {
        boolean retorno = true;
        if (exercicioInicial == null) {
            FacesUtil.addError("Não foi possível continuar.", "Informe o Exercício Inicial.");
            retorno = false;
        }
        if (exercicioFinal == null) {
            FacesUtil.addError("Não foi possível continuar.", "Informe o Exercício Final.");
            retorno = false;
        }
        if (tipoCadastroTributario == null) {
            FacesUtil.addError("Não foi possível continuar.", "Selecione o tipo de cadastro.");
            retorno = false;
        } else if (dividasSelecionadas == null || dividasSelecionadas.size() <= 0) {
            FacesUtil.addError("Não foi possível continuar.", "Selecione ao menos uma dívida na lista.");
            retorno = false;
        }
        if (quantidadeContribuinte == null) {
            FacesUtil.addError("Não foi possível continuar.", "Informe a quantidade de contribuintes.");
            retorno = false;
        }
        if (!situacaoDoExercicio && !situacaoDividaAtiva && !situacaoDividaAtivaAjuizada) {
            FacesUtil.addError("Não foi possível continuar.", "Selecione ao menos uma situação do débito.");
            retorno = false;
        }
        return retorno;
    }

    public void copiarExercicioInicialParaExercicioFinal() {
        exercicioFinal = exercicioInicial;
    }

    public List<Divida> getDividasSelecionadas() {
        return dividasSelecionadas;
    }

    public void setDividasSelecionadas(List<Divida> dividasSelecionadas) {
        this.dividasSelecionadas = dividasSelecionadas;
    }

    private String getIdsDividasSelecionadas() {
        String idsDesconsiderados = "";
        if (dividasSelecionadas != null && dividasSelecionadas.size() > 0) {
            for (Divida dividaSelecionada : dividasSelecionadas) {
                idsDesconsiderados += dividaSelecionada.getId().toString() + ", ";
            }
            idsDesconsiderados = idsDesconsiderados.substring(0, idsDesconsiderados.length() - 2);
        }
        return idsDesconsiderados;
    }

    public List<Divida> recuperaDividas(String parte) {
        List<Divida> retorno = new ArrayList();
        if (tipoCadastroTributario != null) {
            retorno = relatorioMaioresContribuintesFacade.getDividaFacade().listaDividasDoTipoCadastro(parte.trim(), tipoCadastroTributario, getIdsDividasSelecionadas());
        } else {
            FacesUtil.addInfo("Atenção!", "Para selecionar Dívidas é preciso informar o Tipo de Cadastro!");
        }
        return retorno;
    }

    public ConverterGenerico getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterGenerico(Divida.class, relatorioMaioresContribuintesFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public void limparDividasSelecionadas() {
        dividasSelecionadas = new ArrayList();
    }
}
