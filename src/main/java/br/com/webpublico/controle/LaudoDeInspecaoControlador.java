package br.com.webpublico.controle;

import br.com.webpublico.entidades.Veiculo;
import br.com.webpublico.enums.administrativo.TipoInspecao;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Desenvolvimento on 05/04/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLaudoInspecao",
        pattern = "/laudo-de-inspecao-veiculo/",
        viewId = "/faces/administrativo/frota/laudo-de-inspecao-tecnica-do-veiculo/edita.xhtml")
})
public class LaudoDeInspecaoControlador {
    protected static final Logger logger = LoggerFactory.getLogger(LaudoDeInspecaoControlador.class);
    @EJB
    private SistemaFacade sistemaFacade;
    public static final String CARRO = "CarroInspecao.png";
    public static final String ONIBUS = "OnibusInspecao.png";
    public static final String MOTOCICLETA = "MotocicletaInspecao.png";
    public static final String VAN = "VanInspecao.png";
    public static final String CAMIONETE = "CamioneteInspecao.jpg";
    public static final String BASCULANTE = "BasculanteInspecao.jpg";
    public static final String CARGA_SECA = "CargaSecaInspecao.jpg";
    private Veiculo veiculo;
    private String[] selectedTipos;

    public LaudoDeInspecaoControlador() {
    }

    @URLAction(mappingId = "novoLaudoInspecao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        veiculo = null;
        selectedTipos = new String[0];
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public String[] getSelectedTipos() {
        return selectedTipos;
    }

    public void setSelectedTipos(String[] selectedTipos) {
        this.selectedTipos = selectedTipos;
    }

    public List<SelectItem> getItensSelectTipoInspecao() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoInspecao tipoInspecao : TipoInspecao.values()) {
            retorno.add(new SelectItem(tipoInspecao.name(), tipoInspecao.getDescricao()));
        }
        return retorno;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Frotas");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("NOMERELATORIO", "LAUDO DE INSPEÇÃO TÉCNICA DE VEÍCULO");
            dto.adicionarParametro("IMAGEM", buscarImagemPorCategoriaVeiculo());
            dto.adicionarParametro("VEICULO_ID", veiculo.getId());
            dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("TIPOS", gruposDeInpecao());
            dto.setNomeRelatorio("LAUDO DE INSPEÇÃO TÉCNICA DE VEÍCULO");
            dto.setApi("administrativo/laudo-de-inspecao/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String buscarImagemPorCategoriaVeiculo() {
        switch (getVeiculo().getCategoria()) {
            case CARRO: {
                return buscarImagemCarroInspecao();
            }
            case MOTO: {
                return buscarImagemMotoInspecao();
            }
            case VAN: {
                return buscarImagemVanInspecao();
            }
            case CAMIONETE: {
                return buscarImagemCamioneteInspecao();
            }
            case ONIBUS: {
                return buscarImagemOnibusInspecao();
            }
            case CAMINHAO: {
                return getTipoCaminhao();
            }
        }
        return "";
    }

    private String buscarImagemOnibusInspecao() {
        return ONIBUS;
    }

    private String buscarImagemCamioneteInspecao() {
        return CAMIONETE;
    }

    private String buscarImagemVanInspecao() {
        return VAN;
    }

    private String buscarImagemMotoInspecao() {
        return MOTOCICLETA;
    }

    private String buscarImagemCarroInspecao() {
        return CARRO;
    }

    private String getTipoCaminhao() {
        switch (getVeiculo().getTipoCaminhao()) {
            case BASCULANTE: {
                return buscarBasculanteInspecao();
            }
            case CARGA_SECA: {
                return buscarCargaSecaInspecao();
            }
        }
        return "";
    }

    private String buscarCargaSecaInspecao() {
        return CARGA_SECA;
    }

    private String buscarBasculanteInspecao() {
        return BASCULANTE;
    }

    private String gruposDeInpecao() {
        String grupos = " (";
        for (String selectedTipo : selectedTipos) {
            grupos += "'" + selectedTipo + "',";
        }
        grupos = grupos.substring(0, grupos.length() - 1);
        grupos += ") ";
        return grupos;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (veiculo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo veículo deve ser informado.");
        }
        if (selectedTipos != null && selectedTipos.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo grupos de inspeções deve ser informado.");
        }
        ve.lancarException();
    }
}
