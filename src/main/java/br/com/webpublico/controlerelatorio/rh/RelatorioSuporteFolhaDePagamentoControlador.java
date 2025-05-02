package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
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
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "relatorioSuporteFolhaDePagamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioSuporteFolhaDePagamento", pattern = "/relatorio-suporte-folha-pagamento/", viewId = "/faces/rh/relatorios/relatoriosuportefolhadepagamento.xhtml")
})
public class RelatorioSuporteFolhaDePagamentoControlador extends AbstractReport implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioSuporteFolhaDePagamentoControlador.class);
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private Integer ano;
    private Integer mes;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    public Integer versao;

    @URLAction(mappingId = "relatorioSuporteFolhaDePagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        ano = null;
        mes = null;
        tipoFolhaDePagamento = null;
        versao = null;
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("NOMERELATORIO", "Relatório de Suporte à Folha de Pagamento");
            dto.adicionarParametro("MES", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
            dto.adicionarParametro("ANO", ano);
            dto.adicionarParametro("TIPO-FOLHA", tipoFolhaDePagamento);
            if (versao != null) {
                dto.adicionarParametro("VERSAO", versao);
            }
            dto.setNomeRelatorio("Relatório de Suporte à Folha de Pagamento");
            dto.setApi("rh/relatorio-suporte-folha-pagamento/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar o relatório de Suporte à Folha de Pagamento {}", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O ano é um campo obrigatório!");
        }
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O mês é um campo obrigatório!");
        }
        if (!isMesValido()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Mês deve ser entre 1 e 12.");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O tipo folha de pagamento  é um campo obrigatório!");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTipoFolhaDePagamentos() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        if (mes != null && ano != null && tipoFolhaDePagamento != null && isMesValido()) {
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(Mes.getMesToInt(mes), ano, tipoFolhaDePagamento)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    private boolean isMesValido() {
        return mes != null && mes.compareTo(1) >= 0 && mes.compareTo(12) <= 0;
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }
}
