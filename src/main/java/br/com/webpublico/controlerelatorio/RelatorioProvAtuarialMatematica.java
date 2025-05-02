/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-provisao-matematica-previdenciaria", pattern = "/relatorio/provisao-matematica-previdenciaria/", viewId = "/faces/financeiro/relatorio/relatorioprovatuarialmatematica.xhtml")
})
public class RelatorioProvAtuarialMatematica implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioProvAtuarialMatematica.class);
    @EJB
    private SistemaFacade sistemaFacade;
    private String numero;
    private Date dataInicial;
    private Date dataFinal;
    private List<HierarquiaOrganizacional> listaUnidades;
    private String filtro;
    private ApresentacaoRelatorio apresentacao;
    private UnidadeGestora unidadeGestora;

    public List<SelectItem> getApresentacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (ApresentacaoRelatorio ap : ApresentacaoRelatorio.values()) {
            toReturn.add(new SelectItem(ap, ap.getDescricao()));
        }
        return toReturn;
    }

    private void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial deve ser menor que a Data Final");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("EXERCICIO_ID", sistemaFacade.getExercicioCorrente().getId());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("condicao", montarCondicaoEFiltros());
            dto.adicionarParametro("FILTRO", filtro);
            dto.setNomeRelatorio("Relatório-de-Provisão-Matemática-Previdenciária");
            dto.setApi("contabil/provisao-matematica-previdenciaria/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: " + ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicaoEFiltros() {
        filtro = "";
        String juncao = " and ";
        StringBuilder where = new StringBuilder();
        where.append(juncao).append(" trunc(PAM.DATALANCAMENTO) between to_date('").append(DataUtil.getDataFormatada(dataInicial)).append("', 'dd/mm/yyyy') and to_date('").append(DataUtil.getDataFormatada(dataFinal)).append("', 'dd/mm/yyyy')");
        filtro += " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        if (numero != null) {
            if (!numero.isEmpty()) {
                where.append(juncao).append(" PAM.numero = '").append(numero).append("'");
                filtro += " Número: " + numero + " -";
            }
        }
        if (this.listaUnidades.size() > 0) {
            StringBuilder idUnidades = new StringBuilder();
            StringBuilder unds = new StringBuilder();
            String concat = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concat).append(lista.getSubordinada().getId());
                unds.append(concat).append(lista.getCodigo());
                concat = ", ";
            }
            filtro += " Unidade(s): " + unds.toString() + " -";
            where.append(juncao).append(" PAM.UNIDADEORGANIZACIONAL_ID IN (").append(idUnidades).append(")");
        }
        if (unidadeGestora != null) {
            where.append(juncao).append(" ug.id = ").append(unidadeGestora.getId());
            filtro += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        filtro = filtro.substring(0, filtro.length() - 1);
        return where.toString();
    }

    @URLAction(mappingId = "relatorio-provisao-matematica-previdenciaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.numero = null;
        this.dataFinal = new Date();
        this.dataInicial = new Date();
        this.filtro = null;
        this.listaUnidades = new ArrayList<>();
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }
}
