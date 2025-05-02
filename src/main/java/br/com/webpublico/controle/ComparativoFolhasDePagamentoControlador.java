package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.RecursoFP;
import br.com.webpublico.entidadesauxiliares.rh.comparativofolhasdepagamento.ComparativoFolhasDePagamento;
import br.com.webpublico.entidadesauxiliares.rh.comparativofolhasdepagamento.ItemComparativoFolhasDePagamento;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.RecursoFPFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.negocios.rh.administracaodepagamento.ComparativoFolhasDePagamentoExecutor;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.beust.jcommander.internal.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean(name = "comparativoFolhasDePagamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoComparativoFolhasDePagamento", pattern = "/comparativofolhasdepagamento/novo/", viewId = "/faces/rh/administracaodepagamento/comparativofolhasdepagamento/lista.xhtml")
})
public class ComparativoFolhasDePagamentoControlador implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ComparativoFolhasDePagamentoControlador.class);

    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Integer mes1;
    private Integer ano1;
    private TipoFolhaDePagamento tipoFolhaDePagamento1;
    private Integer versao1;
    private Integer mes2;
    private Integer ano2;
    private TipoFolhaDePagamento tipoFolhaDePagamento2;
    private Integer versao2;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<AssistenteBarraProgresso> future;
    private ComparativoFolhasDePagamento comparativoFolhasDePagamento;
    private List<RecursoFP> recursos;
    private Boolean semRetroacao;
    private Boolean exclusivoFolha1;
    private HierarquiaOrganizacional hierarquiaOrganizacional;


    @URLAction(mappingId = "novoComparativoFolhasDePagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mes1 = null;
        ano1 = null;
        tipoFolhaDePagamento1 = null;
        versao1 = null;
        mes2 = null;
        ano2 = null;
        tipoFolhaDePagamento2 = null;
        versao2 = null;
        comparativoFolhasDePagamento = new ComparativoFolhasDePagamento();
        recursos = Lists.newArrayList();
        semRetroacao = false;
        exclusivoFolha1 = false;
        hierarquiaOrganizacional = null;
    }

    public void compararFolhas() {
        try {
            validarCampos();
            comparativoFolhasDePagamento.setItens(new ArrayList<ItemComparativoFolhasDePagamento>());
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Comparando Folhas de Pagamento...");
            assistenteBarraProgresso.zerarContadoresProcesso();

            future = new ComparativoFolhasDePagamentoExecutor(folhaDePagamentoFacade, vinculoFPFacade).execute(assistenteBarraProgresso,
                ano1, mes1, tipoFolhaDePagamento1, ano2, mes2, tipoFolhaDePagamento2, montarFiltroRecursoFP(), semRetroacao, exclusivoFolha1, hierarquiaOrganizacional);
            FacesUtil.executaJavaScript("compararFolhasDePagamento()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("erro", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao comparar folhas informadas.");
        }
    }

    public void verificarTermino() {
        if (future != null && future.isDone()) {
            try {
                AssistenteBarraProgresso assistente = future.get();
                comparativoFolhasDePagamento = (ComparativoFolhasDePagamento) assistente.getSelecionado();
            } catch (Exception e) {
                logger.error("erro", e);
                FacesUtil.addOperacaoNaoRealizada(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao() + "Erro ao comparar folhas informadas.");
            }
            RequestContext.getCurrentInstance().update("Formulario");
            FacesUtil.executaJavaScript("termina()");
            future = null;
            FacesUtil.executaJavaScript("dialogo.hide()");
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (ano1 == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O ano referente a folha 1 deve ser preenchido!");
        } else if (ano1.toString().length() != 4) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O ano referente a folha 1 informado é inválido!");
        }
        if (mes1 == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O mês referente a folha 1 deve ser preenchido!");
        } else {

            if (mes1 < 1 || mes1 > 12) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O mês referente a folha 1 informado é inválido!");
            }
        }
        if (tipoFolhaDePagamento1 == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Folha de Pagamento referente a folha 1 deve ser preenchido!");
        }
        if (ano2 == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O ano referente a folha 2 deve ser preenchido!");
        } else if (ano2.toString().length() != 4) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O ano referente a folha 2 informado é inválido !");
        }
        if (mes2 == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O mês referente a folha 2 deve ser preenchido!");
        } else {

            if (mes2 < 1 || mes2 > 12) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O mês referente a folha 2 informado é inválido!");
            }
        }
        if (tipoFolhaDePagamento2 == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Folha de Pagamento referente a folha 2 deve ser preenchido!");
        }
        ve.lancarException();
    }

    public String montarFiltroRecursoFP() {
        String filtro = "";
        if (!recuperarSomenteRecursosSelecionados().isEmpty()) {
            filtro += " and  rec.id in (";
            for (RecursoFP recursoFP : recuperarSomenteRecursosSelecionados()) {
                filtro += recursoFP.getId() + ", ";
            }
            filtro = filtro.substring(0, filtro.length() - 2);
            filtro += ")";
        }
        return filtro;
    }

    public List<SelectItem> getTiposFolhaDePagamento() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (mes1 != null && ano1 != null && tipoFolhaDePagamento1 != null) {
            retorno.add(new SelectItem(null, "TODAS"));
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(Mes.getMesToInt(mes1), ano1, tipoFolhaDePagamento1)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public List<SelectItem> getVersoes2() {
        List<SelectItem> retorno = new ArrayList<>();
        if (mes2 != null && ano2 != null && tipoFolhaDePagamento2 != null) {
            retorno.add(new SelectItem(null, "TODAS"));
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(Mes.getMesToInt(mes2), ano2, tipoFolhaDePagamento2)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public void recuperarRecursosFP() {
        recursos = Lists.newArrayList();
        if (mes1 != null && ano1 != null && mes2 != null && ano2 != null && tipoFolhaDePagamento1 != null) {
            recursos = recursoFPFacade.buscarRecursosPorMesesAndAnosFolha(Mes.getMesToInt(mes1), ano1, Mes.getMesToInt(mes2), ano2, tipoFolhaDePagamento1);
            if (!recursos.isEmpty()) {
                Collections.sort(recursos, new Comparator<RecursoFP>() {
                    @Override
                    public int compare(RecursoFP o1, RecursoFP o2) {
                        String i1 = o1.getCodigo();
                        String i2 = o2.getCodigo();
                        return i1.compareTo(i2);
                    }
                });
            }
        }
    }

    public void removerTodosRecursos() {
        for (RecursoFP recurso : recursos) {
            recurso.setSelecionado(false);
        }
    }

    public void removerRecurso(ActionEvent ev) {
        RecursoFP r = (RecursoFP) ev.getComponent().getAttributes().get("recurso");
        r.setSelecionado(false);
    }

    public Boolean todosRecursosMarcados() {
        for (RecursoFP recurso : recursos) {
            if (!recurso.isSelecionado())
                return false;
        }
        return true;
    }

    public void adicionarTodosRecursos() {
        for (RecursoFP recurso : recursos) {
            recurso.setSelecionado(true);
        }
    }

    public void adicionarRecurso(ActionEvent ev) {
        RecursoFP r = (RecursoFP) ev.getComponent().getAttributes().get("recurso");
        r.setSelecionado(true);
    }

    public List<RecursoFP> recuperarSomenteRecursosSelecionados() {
        List<RecursoFP> retorno = Lists.newArrayList();
        for (RecursoFP recurso : recursos) {
            if (recurso.isSelecionado()) {
                retorno.add(recurso);
            }
        }
        return retorno;
    }

    public String getCor(BigDecimal valor) {
        return valor.compareTo(BigDecimal.ZERO) >= 0 ? "alert-success" : "alert-danger";
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
            return hierarquiaOrganizacionalFacade.listaTodasHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public Integer getMes1() {
        return mes1;
    }

    public void setMes1(Integer mes1) {
        this.mes1 = mes1;
    }

    public Integer getAno1() {
        return ano1;
    }

    public void setAno1(Integer ano1) {
        this.ano1 = ano1;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento1() {
        return tipoFolhaDePagamento1;
    }

    public void setTipoFolhaDePagamento1(TipoFolhaDePagamento tipoFolhaDePagamento1) {
        this.tipoFolhaDePagamento1 = tipoFolhaDePagamento1;
    }

    public Integer getVersao1() {
        return versao1;
    }

    public void setVersao1(Integer versao1) {
        this.versao1 = versao1;
    }

    public Integer getMes2() {
        return mes2;
    }

    public void setMes2(Integer mes2) {
        this.mes2 = mes2;
    }

    public Integer getAno2() {
        return ano2;
    }

    public void setAno2(Integer ano2) {
        this.ano2 = ano2;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento2() {
        return tipoFolhaDePagamento2;
    }

    public void setTipoFolhaDePagamento2(TipoFolhaDePagamento tipoFolhaDePagamento2) {
        this.tipoFolhaDePagamento2 = tipoFolhaDePagamento2;
    }

    public Integer getVersao2() {
        return versao2;
    }

    public void setVersao2(Integer versao2) {
        this.versao2 = versao2;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public ComparativoFolhasDePagamento getComparativoFolhasDePagamento() {
        return comparativoFolhasDePagamento;
    }

    public void setComparativoFolhasDePagamento(ComparativoFolhasDePagamento comparativoFolhasDePagamento) {
        this.comparativoFolhasDePagamento = comparativoFolhasDePagamento;
    }

    public List<RecursoFP> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursoFP> recursos) {
        this.recursos = recursos;
    }

    public Boolean getSemRetroacao() {
        return semRetroacao;
    }

    public void setSemRetroacao(Boolean semRetroacao) {
        this.semRetroacao = semRetroacao;
    }

    public Boolean getExclusivoFolha1() {
        return exclusivoFolha1;
    }

    public void setExclusivoFolha1(Boolean exclusivoFolha1) {
        this.exclusivoFolha1 = exclusivoFolha1;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
