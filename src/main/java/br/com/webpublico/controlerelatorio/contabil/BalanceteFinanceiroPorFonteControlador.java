package br.com.webpublico.controlerelatorio.contabil;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoRelatorioApresentacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
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
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-balancete-financeiro-por-fonte", pattern = "/relatorio/balancete-financeiro-por-fonte/", viewId = "/faces/financeiro/relatorio/balancete-financeiro-por-fonte.xhtml")
})
public class BalanceteFinanceiroPorFonteControlador implements Serializable {
    @EJB
    private SistemaFacade sistemaFacade;
    private Mes mesInicial;
    private Mes mesFinal;
    private List<FonteDeRecursos> fontes;
    private Boolean fonteComDiferenca;
    private Exercicio exercicio;
    private TipoRelatorioApresentacao tipoRelatorio;

    @URLAction(mappingId = "relatorio-balancete-financeiro-por-fonte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        exercicio = sistemaFacade.getExercicioCorrente();
        mesInicial = Mes.JANEIRO;
        mesFinal = Mes.getMesToInt(DataUtil.getMes(sistemaFacade.getDataOperacao()));
        fontes = Lists.newArrayList();
        fonteComDiferenca = Boolean.FALSE;
        tipoRelatorio = TipoRelatorioApresentacao.DETALHADO;
    }

    public void gerarRelatorio(String tipoExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensao));
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("DATA_INICIAL",
                DataUtil.getDataFormatada(DataUtil.getDateParse("01/" + mesInicial.getNumeroMes() + "/" + exercicio.getAno()))
            );
            dto.adicionarParametro("DATA_FINAL",
                DataUtil.getDataFormatada(DataUtil.ultimoDiaMes(DataUtil.getDateParse("01/" + mesFinal.getNumeroMes() + "/" + exercicio.getAno())).getTime())
            );
            dto.adicionarParametro("FONTE_COM_DIFERENCA", fonteComDiferenca);
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno());
            dto.adicionarParametro("TIPO_RELATORIO", tipoRelatorio.name());
            dto.adicionarParametro("NOMERELATORIO", "Balancete Financeiro por Fonte de Recurso - " + tipoRelatorio.getDescricao());
            if (fontes != null && !fontes.isEmpty()) {
                dto.adicionarParametro("fontes", FonteDeRecursos.fontesToDto(fontes));
            }
            dto.adicionarParametro("FILTRO", getFiltrosUtilizados());
            dto.setNomeRelatorio("BALANCETE_FINANCEIRO_POR_FONTE_" + mesInicial.getNumeroMesString() + "/" + exercicio.getAno() +
                "ATÉ" + mesFinal.getNumeroMesString() + "/" + exercicio.getAno() + "_" + tipoRelatorio.getDescricao().toUpperCase());
            dto.setApi("contabil/balancete-financeiro-por-fonte/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (mesInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês Inicial deve ser informado.");
        }
        if (mesFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês Final deve ser informado.");
        }
        if (tipoRelatorio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo Relatório deve ser informado.");
        }
        ve.lancarException();
        if (mesInicial.getNumeroMes() > mesFinal.getNumeroMes()) {
            ve.adicionarMensagemDeCampoObrigatorio("O Mês Final deve ser igual ou superior ao Mês Inicial.");
        }
        ve.lancarException();
    }

    private String getFiltrosUtilizados() {
        String retorno = mesInicial.getNumeroMesString() + "/" + exercicio.getAno() +
            " até " + mesFinal.getNumeroMesString() + "/" + exercicio.getAno() +
            "; Fonte com Diferença: " + (fonteComDiferenca ? "Sim" : "Não");
        if (fontes != null && !fontes.isEmpty()) {
            String juncao = "";
            retorno += "; Fonte(s): ";
            for (FonteDeRecursos fonte : fontes) {
                retorno += juncao + fonte.getCodigo();
                juncao = ", ";
            }
        }
        return retorno;
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> getTiposRelatorios() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorioApresentacao.values(), false);
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public List<FonteDeRecursos> getFontes() {
        return fontes;
    }

    public void setFontes(List<FonteDeRecursos> fontes) {
        this.fontes = fontes;
    }

    public Boolean getFonteComDiferenca() {
        return fonteComDiferenca;
    }

    public void setFonteComDiferenca(Boolean fonteComDiferenca) {
        this.fonteComDiferenca = fonteComDiferenca;
    }

    public TipoRelatorioApresentacao getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorioApresentacao tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }
}
