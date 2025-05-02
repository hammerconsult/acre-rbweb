/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.report.ReportService;
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
 * @author boy
 */
@ManagedBean(name = "relatorioEventosCalculadosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "gerarRelatorioEventosCalculados", pattern = "/relatorio/eventos-calculados/", viewId = "/faces/rh/relatorios/relatorioeventoscalculados.xhtml")
})
public class RelatorioEventosCalculadosControlador extends AbstractReport implements Serializable {

    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private Mes mes;
    private Integer ano;
    private Integer versao;
    private TipoFolhaDePagamento tipoFolhaDePagamento;

    public RelatorioEventosCalculadosControlador() {
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (ano == null || ano == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha deve ser informado.");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "gerarRelatorioResumoDeEmpenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioResumoDeEmpenho() {
        limparCampos();
    }

    @URLAction(mappingId = "gerarRelatorioEventosCalculados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioEventosCalculados() {
        limparCampos();
    }

    public void limparCampos() {
        mes = null;
        ano = null;
        tipoFolhaDePagamento = null;
        versao = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MES", (mes.getNumeroMesIniciandoEmZero()));
            dto.adicionarParametro("ANO", ano);
            dto.adicionarParametro("TIPOFOLHA", tipoFolhaDePagamento.getToDto());
            dto.adicionarParametro("MODULO","Recursos Humanos");
            dto.setNomeRelatorio("Relatório-de-Eventos-Calculados");
            dto.setApi("rh/eventos-calculados-no-mes/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
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

    private String montarCampoVersao() {
        if (versao != null) {
            return " and folha.versao = " + versao;
        }
        return " ";
    }

    public List<SelectItem> tiposFolhaDePagamento() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, "Selecione o tipo de folha"));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes obj : Mes.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (mes != null && ano != null && tipoFolhaDePagamento != null) {
            retorno.add(new SelectItem(null, ""));
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(mes, ano, tipoFolhaDePagamento)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

}
