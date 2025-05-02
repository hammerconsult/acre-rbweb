package br.com.webpublico.controlerelatorio.administrativo.frotas;

import br.com.webpublico.entidades.Equipamento;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.administrativo.TipoInspecaoEquipamento;
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

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos on 25/05/17.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLaudoInspecaoEquipamento",
        pattern = "/laudo-de-inpecao-de-equipamento/novo/",
        viewId = "/faces/administrativo/frota/laudo-de-inspecao-tecnica-de-equipamento/edita.xhtml")
})
public class RelatorioLaudoInspecaoTecnicaEquipamentoControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    private Equipamento equipamento;
    private List<String> tipos;
    private List<String> tiposSelecionados;

    public RelatorioLaudoInspecaoTecnicaEquipamentoControlador() {
    }

    @URLAction(mappingId = "novoLaudoInspecaoEquipamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        tiposSelecionados = new ArrayList<>();
    }

    public List<SelectItem> getItensSelectTipoInspecaoEquipamento() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoInspecaoEquipamento tipoInspecaoEquipamento : TipoInspecaoEquipamento.values()) {
            retorno.add(new SelectItem(tipoInspecaoEquipamento.name(), tipoInspecaoEquipamento.getDescricao()));
        }
        return retorno;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", sistemaFacade.getMunicipio());
            dto.adicionarParametro("SECRETARIA", sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente().getDescricao().toUpperCase());
            dto.adicionarParametro("NOME_RELATORIO", "LAUDO DE INSPEÇÃO TÉCNICA DE EQUIPAMENTO");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.setNomeRelatorio("LAUDO DE INSPEÇÃO TÉCNICA DE EQUIPAMENTO");
            dto.setApi("administrativo/laudo-inspecao-tecnica-equipamento/");
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

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        if (equipamento != null) {
            parametros.add(new ParametrosRelatorios(" ID_EQUIPAMENTO ", ":EQUIPAMENTO", null, OperacaoRelatorio.IGUAL, equipamento.getId(), null, 0, false));
        }
        if (tipos != null && !tipos.isEmpty()) {
            parametros.add(new ParametrosRelatorios(" TIPOINSPECAOEQUIPAMENTO ", ":TIPOINSPECAOEQUIPAMENTO", null, OperacaoRelatorio.IN, gruposInspecionado(), null, 1, false));
        }
        return parametros;
    }


    public List<String> gruposInspecionado() {
        tiposSelecionados.clear();
        if (!tipos.isEmpty()) {
            for (String tipo : tipos) {
                tiposSelecionados.add(tipo);
            }
        }
        return tiposSelecionados;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (equipamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É necessário selecionar um Equipamento!");
        }
        if (tipos != null && tipos.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione ao menos um Grupo de Inspeção");
        }
        ve.lancarException();
    }

    public void limparCampos() {
        equipamento = null;
        tipos = new ArrayList<>();
        tiposSelecionados = new ArrayList<>();
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    public List<String> getTipos() {
        return tipos;
    }

    public void setTipos(List<String> tipos) {
        this.tipos = tipos;
    }
}
