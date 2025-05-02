package br.com.webpublico.controle.administrativo.frotas;


import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.ObjetoFrota;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
import java.util.*;

/**
 * Created by William on 04/02/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioMediaConsumoVeiculo", pattern = "/relatorio-media-consumo-veiculo/",
        viewId = "/faces/administrativo/frota/relatorios/relatoriomediaconsumoveiculo.xhtml")
})
public class RelatorioMediaConsumoVeiculoControlador extends AbstractReport implements Serializable {

    @EJB
    private VeiculoFacade veiculoFacade;
    @EJB
    private EquipamentoFacade equipamentoFacade;
    private Date abastecimentoInicial;
    private Date abastecimentoFinal;
    private TipoObjetoFrota tipoObjetoFrota;
    private ObjetoFrota objetoFrota;


    @URLAction(mappingId = "novoRelatorioMediaConsumoVeiculo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparFiltros();
    }

    public void limparFiltros() {
        abastecimentoInicial = null;
        abastecimentoFinal = null;
        tipoObjetoFrota = null;
        objetoFrota = null;
    }

    private String montarDescritivoDeCriteriosUtilizados() {
        StringBuilder criteriosUtilizados = new StringBuilder();
        criteriosUtilizados.append("Data de Abastecimento Inicial: ").append(Util.dateToString(abastecimentoInicial)).append("; ");
        criteriosUtilizados.append("Data de Abastecimento Final: ").append(Util.dateToString(abastecimentoFinal)).append("; ");
        if (tipoObjetoFrota != null) {
            criteriosUtilizados.append("Tipo: ").append(tipoObjetoFrota).append("; ");
        }
        if (objetoFrota != null) {
            criteriosUtilizados.append("Objeto de Controle: ").append(objetoFrota).append("; ");
        }
        return criteriosUtilizados.toString();
    }

    private void validarFiltrosObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (abastecimentoInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A data de abastecimento inicial deve ser informada.");
        }
        if (abastecimentoFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A data de abastecimento final deve ser informada.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosObrigatorios();
            String nomeRelatorio = "Relatório de Média de Consumo";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("FILTROS", montarDescritivoDeCriteriosUtilizados());
            dto.adicionarParametro("MODULO", "FROTAS");
            dto.adicionarParametro("SECRETARIA", "SECRETÁRIA DE FROTAS");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("idsObjetosFrotas", buscarIdsObjetosFrotas());
            dto.adicionarParametro("abastecimentoInicial", DataUtil.getDataFormatada(abastecimentoInicial));
            dto.adicionarParametro("abastecimentoFinal", DataUtil.getDataFormatada(abastecimentoFinal));
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/medida-consumo-veiculo/");
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

    private List<Long> buscarIdsObjetosFrotas() {
        List<Long> retorno = Lists.newArrayList();
        List<ObjetoFrota> objetosParaMediaDeConsumo = Lists.newArrayList();
        if (objetoFrota != null && objetoFrota.getId() != null) {
            retorno.add(objetoFrota.getId());
        } else {
            if (tipoObjetoFrota == null || tipoObjetoFrota.equals(TipoObjetoFrota.VEICULO)) {
                objetosParaMediaDeConsumo.addAll(veiculoFacade.buscarVeiculosPorPlacaOrDescricaoOrIdentificacaoOrNumeroProcessoContrato(""));
            }
            if (tipoObjetoFrota == null || tipoObjetoFrota.equals(TipoObjetoFrota.EQUIPAMENTO)) {
                objetosParaMediaDeConsumo.addAll(equipamentoFacade.buscarEquipamentosPorAnoOrDescricaoOrIdentificacaoOrNumeroProcessoContrato(""));
            }
        }
        Collections.sort(objetosParaMediaDeConsumo, new Comparator<ObjetoFrota>() {
            @Override
            public int compare(ObjetoFrota o1, ObjetoFrota o2) {
                return o1.getUnidadeOrganizacional().getDescricao().compareTo(o2.getUnidadeOrganizacional().getDescricao());
            }
        });
        for (ObjetoFrota of : objetosParaMediaDeConsumo) {
            retorno.add(of.getId());
        }
        return retorno;
    }

    public Date getAbastecimentoInicial() {
        return abastecimentoInicial;
    }

    public void setAbastecimentoInicial(Date abastecimentoInicial) {
        this.abastecimentoInicial = abastecimentoInicial;
    }

    public Date getAbastecimentoFinal() {
        return abastecimentoFinal;
    }

    public void setAbastecimentoFinal(Date abastecimentoFinal) {
        this.abastecimentoFinal = abastecimentoFinal;
    }

    public TipoObjetoFrota getTipoObjetoFrota() {
        return tipoObjetoFrota;
    }

    public void setTipoObjetoFrota(TipoObjetoFrota tipoObjetoFrota) {
        this.tipoObjetoFrota = tipoObjetoFrota;
    }

    public ObjetoFrota getObjetoFrota() {
        return objetoFrota;
    }

    public void setObjetoFrota(ObjetoFrota objetoFrota) {
        this.objetoFrota = objetoFrota;
    }

    public String getMensagemInfoListagemObjetosFrota() {
        if (tipoObjetoFrota == null) {
            return "Com os filtros utilizados irá trazer todos os veículos e equipamentos/máquinas.";
        }
        if (tipoObjetoFrota.isVeiculo() && objetoFrota == null) {
            return "Com os filtros utilizados irá trazer todos os veículos.";
        }
        if (tipoObjetoFrota.isVeiculo() && objetoFrota != null) {
            return "Com os filtros utilizados irá trazer apenas o veículo: " + objetoFrota;
        }
        if (tipoObjetoFrota.isEquipamento() && objetoFrota == null) {
            return "Com os filtros utilizados irá trazer todos os equipamentos/máquinas.";
        }
        if (tipoObjetoFrota.isEquipamento() && objetoFrota != null) {
            return "Com os filtros utilizados irá trazer apenas o equipamento/máquina: " + objetoFrota;
        }
        return "";
    }
}
