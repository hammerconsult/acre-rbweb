/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle.rh.relatorio;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.rh.SituacaoServidoresFichaFinanceiraDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * @author peixe
 */
@ManagedBean(name = "relatorioVerificacaoSituacoesFichaFinanceira")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioVerificacaoSituacoesFichaFinanceira", pattern = "/relatorio/verificacao-situacoes-ficha-financeira/", viewId = "/faces/rh/relatorios/relatorioverificacaosituacoesfichafinanceira.xhtml")
})
public class RelatorioVerificacaoSituacoesFichaFinanceira extends AbstractReport {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private Mes mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private SituacaoServidoresFichaFinanceira situacao;

    @URLAction(mappingId = "relatorioVerificacaoSituacoesFichaFinanceira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mes = null;
        ano = null;
        tipoFolhaDePagamento = null;
        hierarquiaOrganizacional = null;
        situacao = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE VERIFICAÇÃO DE SITUAÇÕES EM FICHA FINANCEIRA");
            dto.adicionarParametro("MES", mes.getNumeroMesIniciandoEmZero());
            dto.adicionarParametro("ANO", ano);
            dto.adicionarParametro("TIPOFOLHA", tipoFolhaDePagamento.getToDto());
            dto.adicionarParametro("DATAOPERACAO", getSistemaFacade().getDataOperacao());
            dto.adicionarParametro("TIPORELATORIO", situacao.toString().toUpperCase());
            dto.adicionarParametro("DATARELATORIO", mes.getNumeroMes() + "/" + ano);
            dto.adicionarParametro("SITUACAO-SERVIDOR", situacao.name());
            if (hierarquiaOrganizacional != null) {
                dto.adicionarParametro("CODIGO_HIERARQUIA", hierarquiaOrganizacional.getCodigoSemZerosFinais());
                dto.adicionarParametro("DESCRICAO_HIERARQUIA", hierarquiaOrganizacional.toString());
            }
            dto.adicionarParametro("DATARELATORIO", mes.getNumeroMes() + "/" + ano);
            dto.setNomeRelatorio("Relatório de Verificação de Situações em Ficha Financeira");
            dto.setApi("rh/verificacao-situacoes-ficha-financeira/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório de Verificação de Situações em Ficha Financeira: " + ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é obrigatório");
        }
        if (ano == null || (ano == 0)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha é obrigatório");
        }
        if (situacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Situações é obrigatório");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposFolha() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(SituacaoServidoresFichaFinanceira.values(), false);
    }
    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
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

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public SituacaoServidoresFichaFinanceira getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoServidoresFichaFinanceira situacao) {
        this.situacao = situacao;
    }

    public enum SituacaoServidoresFichaFinanceira {
        SEM_VERBAS_VANTAGEM_DESCONTO("Sem Verbas de Vantagem ou Desconto", SituacaoServidoresFichaFinanceiraDTO.SEM_VERBAS_VANTAGEM_DESCONTO),
        SEM_VERBA_REMUNERACAO_PRINCIPAL("Sem Verba de Remuneração Principal", SituacaoServidoresFichaFinanceiraDTO.SEM_VERBA_REMUNERACAO_PRINCIPAL),
        LIQUIDO_ZERADO("Líquido Zerado", SituacaoServidoresFichaFinanceiraDTO.LIQUIDO_ZERADO);

        private String descricao;
        private SituacaoServidoresFichaFinanceiraDTO toDto;

        SituacaoServidoresFichaFinanceira(String descricao, SituacaoServidoresFichaFinanceiraDTO toDto) {
            this.descricao = descricao;
            this.toDto = toDto;
        }

        public String getDescricao() {
            return descricao;
        }

        public SituacaoServidoresFichaFinanceiraDTO getToDto() {
            return toDto;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
