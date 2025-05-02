package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioConvenioSuperControlador;
import br.com.webpublico.entidades.ConvenioDespesa;
import br.com.webpublico.entidades.EntidadeBeneficiaria;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Vereador;
import br.com.webpublico.enums.TipoConvenioDespesa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ConvenioDespesaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 07/01/14
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "relatorioConvenioDespesaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-convenio-despesa", pattern = "/covenios/relatorio/convenio-de-despesa/", viewId = "/faces/financeiro/relatorio/relatorioconveniodespesa.xhtml")
})
public class RelatorioConvenioDespesaControlador extends RelatorioConvenioSuperControlador {
    @EJB
    private ConvenioDespesaFacade convenioDespesaFacade;
    private ConvenioDespesa convenioDespesa;
    private TipoConvenioDespesa tipoConvenioDespesa;
    private EntidadeBeneficiaria entidadeBeneficiaria;
    private Vereador vereador;

    @URLAction(mappingId = "relatorio-convenio-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
        convenioDespesa = null;
        tipoConvenioDespesa = null;
        entidadeBeneficiaria = null;
        vereador = null;
    }

    public void gerarRelatorio(String tipoFormato) {
        try {
            validarGerarRelatorio();
            Exercicio e = sistemaFacade.getExercicioCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoFormato));
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC ");
            dto.adicionarParametro("ANO_EXERCICIO", e.getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", e.getId());
            dto.adicionarParametro("FILTRO_RELATORIO", DataUtil.getDataFormatada(vigenciaInicialDe) + " até " + DataUtil.getDataFormatada(vigenciaInicialAte));
            dto.adicionarParametro("condicao", montarCondicao());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/convenio-despesa/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicao() {
        String condicao = " where trunc(cd.datavigenciainicial) between to_date('" +
            DataUtil.getDataFormatada(vigenciaInicialDe) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(vigenciaInicialAte) + "', 'dd/MM/yyyy') ";

        if (vigenciaFinalDe != null && vigenciaFinalAte != null) {
            condicao += " and trunc(cd.datavigenciafinal) between to_date('" +
                DataUtil.getDataFormatada(vigenciaFinalDe) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(vigenciaFinalAte) + "', 'dd/MM/yyyy') ";
        }
        if (tipoConvenioDespesa != null) {
            condicao += " and cd.tipoconvenio = '" + tipoConvenioDespesa.name()+ "'";
        }
        if (entidadeBeneficiaria != null) {
            condicao += " and cd.entidadebeneficiaria_id = " + entidadeBeneficiaria.getId();
        }
        if (entidade != null) {
            condicao += " and cd.orgaoconvenente_id = " + entidade.getId();
        }
        if (vereador != null) {
            condicao += " and v.id = " + vereador.getId();
        }
        if (convenioDespesa != null) {
            condicao += " and cd.id = " + convenioDespesa.getId();
        }
        return condicao;
    }

    public List<ConvenioDespesa> completaConvenioDespesa(String parte) {
        return convenioDespesaFacade.listaFiltrando(parte.trim(), "numConvenio", "objeto");
    }

    public List<EntidadeBeneficiaria> completarEntidadesBeneficiarias(String parte) {
        return convenioDespesaFacade.getEntidadeBeneficiariaFacade().listaPorPessoaEClasseCredor(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    public List<SelectItem> getTiposConvenios() {
        return Util.getListSelectItem(TipoConvenioDespesa.values(), false);
    }

    public List<Vereador> completarVereadores(String filtro) {
        return convenioDespesaFacade.getVereadorFacade().listaVereadorPorExercicio(filtro, sistemaFacade.getExercicioCorrente());
    }

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
    }

    public TipoConvenioDespesa getTipoConvenioDespesa() {
        return tipoConvenioDespesa;
    }

    public void setTipoConvenioDespesa(TipoConvenioDespesa tipoConvenioDespesa) {
        this.tipoConvenioDespesa = tipoConvenioDespesa;
    }

    public EntidadeBeneficiaria getEntidadeBeneficiaria() {
        return entidadeBeneficiaria;
    }

    public void setEntidadeBeneficiaria(EntidadeBeneficiaria entidadeBeneficiaria) {
        this.entidadeBeneficiaria = entidadeBeneficiaria;
    }

    public Vereador getVereador() {
        return vereador;
    }

    public void setVereador(Vereador vereador) {
        this.vereador = vereador;
    }

    public String getNomeRelatorio() {
        return "Relatório Convênio Despesa";
    }
}


