/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContaFacade;
import br.com.webpublico.negocios.SubContaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author juggernaut
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-estorno-arrecadacao-diario", pattern = "/relatorio/movimento-arrecadacao-diario-estorno/", viewId = "/faces/financeiro/relatorio/relatorioestornomovimentoarrecadacaodiario.xhtml")
})
public class RelatorioEstornoMovimentoArrecadacaoDiario extends AbstractReport implements Serializable {

    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private ContaFacade contaFacade;
    private String filtro;
    private Date dataReferencia;
    private SubConta contaFinanceira;
    private Conta contaReceita;
    private List<HierarquiaOrganizacional> listaUnidades;
    private UnidadeGestora unidadeGestora;

    public List<SubConta> completarSubContas(String parte) {
        return subContaFacade.listaFiltrandoSubConta(parte);
    }

    public List<Conta> completarContas(String parte) {
        return contaFacade.listaFiltrandoReceita(parte, getSistemaFacade().getExercicioCorrente());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("EXERCICIO_ID", getSistemaFacade().getExercicioCorrente().getId());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("condicao", montarCondicaoEFiltros());
            dto.adicionarParametro("FILTROS", filtro);
            dto.setNomeRelatorio("Estorno de Movimento de Arrecadação Diário");
            dto.setApi("contabil/estorno-movimento-arrecadacao-diario/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Referência deve ser informado.");
        }
        ve.lancarException();
    }

    private String montarCondicaoEFiltros() {
        StringBuilder stb = new StringBuilder();
        String juncao = " AND ";

        stb.append(juncao).append(" trunc(REST.DATAESTORNO) BETWEEN to_date(\'").append(DataUtil.getDataFormatada(dataReferencia)).append("\', 'DD/MM/YYYY') ").append(juncao).append(" TO_DATE(\'").append(DataUtil.getDataFormatada(dataReferencia)).append("\', 'DD/MM/YYYY') ");
        filtro = " Data Referência: " + DataUtil.getDataFormatada(dataReferencia) + " -";

        if (listaUnidades.size() > 0) {
            StringBuilder idUnidades = new StringBuilder();
            String unidades = "";
            String concatena = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concatena).append(lista.getSubordinada().getId());
                unidades += lista.getCodigo().substring(3, 10) + " - ";
                concatena = ",";
            }
            filtro += "  Unidade(s): " + unidades + " -";
            stb.append(juncao).append(" LANC.UNIDADEORGANIZACIONAL_ID IN (").append(idUnidades).append(")");
        }
        if (unidadeGestora != null) {
            stb.append(juncao).append(" ug.id = ").append(unidadeGestora.getId());
            filtro += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        if (this.contaFinanceira != null) {
            stb.append(juncao).append("  SUB.ID = ").append(contaFinanceira.getId());
            filtro += " Conta Financeira: " + contaFinanceira.getCodigo() + " - " + contaFinanceira.getDescricao() + " -";
        }
        if (this.contaReceita != null) {
            stb.append(juncao).append("  c.ID = ").append(contaReceita.getId());
            filtro += " Conta Receita: " + contaReceita.getCodigo() + " -";
        }

        filtro = filtro.substring(0, filtro.length() - 1);
        return stb.toString();
    }

    @URLAction(mappingId = "relatorio-estorno-arrecadacao-diario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.filtro = "";
        this.dataReferencia = getSistemaFacade().getDataOperacao();
        this.listaUnidades = Lists.newArrayList();
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
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

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public Conta getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(Conta contaReceita) {
        this.contaReceita = contaReceita;
    }
}
