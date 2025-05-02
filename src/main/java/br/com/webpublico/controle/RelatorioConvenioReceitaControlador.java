package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioConvenioSuperControlador;
import br.com.webpublico.entidades.ConvenioReceita;
import br.com.webpublico.entidades.EntidadeRepassadora;
import br.com.webpublico.entidades.contabil.emendagoverno.Parlamentar;
import br.com.webpublico.enums.TipoConvenioReceita;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ConvenioReceitaFacade;
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
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 07/01/14
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "relatorioConvenioReceitaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-convenio-receita", pattern = "/convenios/relatorio/convenio-de-receita/", viewId = "/faces/financeiro/relatorio/relatorioconvenioreceita.xhtml")
})
public class RelatorioConvenioReceitaControlador extends RelatorioConvenioSuperControlador implements Serializable {
    @EJB
    private ConvenioReceitaFacade convenioReceitaFacade;
    private ConvenioReceita convenioReceita;
    private TipoConvenioReceita tipoConvenioReceita;
    private EntidadeRepassadora entidadeRepassadora;
    private Parlamentar parlamentar;

    @URLAction(mappingId = "relatorio-convenio-receita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
        convenioReceita = null;
        tipoConvenioReceita = null;
        entidadeRepassadora = null;
        parlamentar = null;
    }

    public void gerarRelatorio(String tipoFormato) {
        try {
            validarGerarRelatorio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoFormato));
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC ");
            dto.adicionarParametro("ANO_EXERCICIO", sistemaFacade.getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("condicao", montarCondicao());
            dto.adicionarParametro("FILTRO_RELATORIO", DataUtil.getDataFormatada(vigenciaInicialDe) + " até " + DataUtil.getDataFormatada(vigenciaInicialAte));
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/convenio-receita/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicao() {
        String condicao = " where trunc(cr.vigenciainicial) between to_date('" +
            DataUtil.getDataFormatada(vigenciaInicialDe) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(vigenciaInicialAte) + "', 'dd/MM/yyyy') ";

        if (vigenciaFinalDe != null && vigenciaFinalAte != null) {
            condicao += " and trunc(cr.vigenciafinal) between to_date('" +
                DataUtil.getDataFormatada(vigenciaFinalDe) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(vigenciaFinalAte) + "', 'dd/MM/yyyy') ";
        }
        if (tipoConvenioReceita != null) {
            condicao += " and cr.tipoconvenioreceita = '" + tipoConvenioReceita.name()+"'";
        }
        if (entidadeRepassadora != null) {
            condicao += " and cr.entidaderepassadora_id = " + entidadeRepassadora.getId();
        }
        if (parlamentar != null) {
            condicao += " and egp.parlamentar_id = " + parlamentar.getId();
        }
        if (entidade != null) {
            condicao += " and cr.entidadeconvenente = " + entidade.getId();
        }
        return condicao;
    }

    public List<ConvenioReceita> completarConveniosReceita(String parte) {
        return convenioReceitaFacade.listaFiltrando(parte.trim(), "numero", "nomeConvenio");
    }

    public List<EntidadeRepassadora> completarEntidadesRepassadoras(String parte) {
        return convenioReceitaFacade.getEntidadeRepassadoraFacade().completaEntidadeRepassadora(parte);
    }

    public List<SelectItem> getTiposConvenios() {
        return Util.getListSelectItem(TipoConvenioReceita.values(), false);
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    public TipoConvenioReceita getTipoConvenioReceita() {
        return tipoConvenioReceita;
    }

    public void setTipoConvenioReceita(TipoConvenioReceita tipoConvenioReceita) {
        this.tipoConvenioReceita = tipoConvenioReceita;
    }

    public EntidadeRepassadora getEntidadeRepassadora() {
        return entidadeRepassadora;
    }

    public void setEntidadeRepassadora(EntidadeRepassadora entidadeRepassadora) {
        this.entidadeRepassadora = entidadeRepassadora;
    }

    public Parlamentar getParlamentar() {
        return parlamentar;
    }

    public void setParlamentar(Parlamentar parlamentar) {
        this.parlamentar = parlamentar;
    }

    public String getNomeRelatorio() {
        return "RELATORIO-CONVENIO-RECEITA";
    }
}


