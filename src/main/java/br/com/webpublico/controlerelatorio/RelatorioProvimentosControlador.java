/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.TipoProvimento;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
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
import java.util.Date;
import java.util.List;

/**
 * @author Desenvolvimento
 */
@ManagedBean(name = "relatorioProvimentosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioProvimentos", pattern = "/relatorio/provimento/novo/", viewId = "/faces/rh/relatorios/relatorioprovimentos.xhtml")
})
public class RelatorioProvimentosControlador extends AbstractReport {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioProvimentosControlador.class);

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    private VinculoFP vinculoFPSelecionado;
    private Boolean detalhado;
    private Date dataInicial;
    private Date dataFinal;
    private String tipoBusca;
    private TipoProvimento tipoProvimento;
    private HierarquiaOrganizacional hierarquiaAdministrativa;

    public RelatorioProvimentosControlador() {
        setGeraNoDialog(true);
    }

    @URLAction(mappingId = "novoRelatorioProvimentos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.setGeraNoDialog(true);
        this.vinculoFPSelecionado = null;
        this.detalhado = Boolean.FALSE;
        this.tipoBusca = "ORGAO";
        this.hierarquiaAdministrativa = null;
        this.tipoProvimento = null;
        this.dataInicial = sistemaFacade.getDataOperacao();
        this.dataFinal = sistemaFacade.getDataOperacao();
    }

    public void limparTipoDeBusca() {
        this.vinculoFPSelecionado = null;
        this.hierarquiaAdministrativa = null;
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-de-provimentos/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        StringBuilder filtros = new StringBuilder();
        String condicoesQueryAndFiltros = montarCondicoes(filtros);
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE PROVIMENTOS");
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE PROVIMENTOS");


        dto.adicionarParametro("DETALHADO", detalhado);
        dto.adicionarParametro("FILTROS", filtros.toString());
        dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(DataUtil.dataSemHorario(sistemaFacade.getDataOperacao())));
        dto.adicionarParametro("CARGOCOMISSAO", ModalidadeContratoFP.CARGO_COMISSAO.intValue());
        dto.adicionarParametro("CONDICOESQUERYANDFILTROS", condicoesQueryAndFiltros);
        return dto;
    }

    private String montarCondicoes(StringBuilder filtros) {
        StringBuilder condicao = new StringBuilder();
        if (detalhado) {
            filtros.append(" Detalhado: ").append(Util.converterBooleanSimOuNao(detalhado)).append(", ");
        }
        if (dataInicial != null) {
            condicao.append(" and trunc(P.DATAPROVIMENTO) >= TO_DATE('").append(DataUtil.getDataFormatada(dataInicial)).append("', 'dd/MM/yyyy')");
            filtros.append(" Ocorrência entre ").append(DataUtil.getDataFormatada(dataInicial));
        }
        if (dataFinal != null) {
            condicao.append(" and trunc(P.DATAPROVIMENTO) <= TO_DATE('").append(DataUtil.getDataFormatada(dataFinal)).append("', 'dd/MM/yyyy')");
            filtros.append(" e ").append(DataUtil.getDataFormatada(dataFinal)).append(", ");
        }
        if (vinculoFPSelecionado != null) {
            condicao.append(" and v.id = ").append(vinculoFPSelecionado.getId());
            filtros.append(" Servidor: ").append(vinculoFPSelecionado.toString());
        }
        if (tipoProvimento != null) {
            condicao.append(" and P.TIPOPROVIMENTO_ID = ").append(tipoProvimento.getId());
            filtros.append(" Tipo de Provimento: ").append(tipoProvimento.toString());
        }
        if (hierarquiaAdministrativa != null) {
            condicao.append(" and VW.codigo like '").append(hierarquiaAdministrativa.getCodigoSemZerosFinais()).append("%'");
            filtros.append(" Órgão: ").append(hierarquiaAdministrativa.toString());
        }
        return condicao.toString();
    }

    public List<TipoProvimento> completarTipoDeProvimento(String s) {
        return provimentoFPFacade.getTipoProvimentoFacade().buscarTipoProvimentoPorCodigoAndDescricao(s);
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (vinculoFPSelecionado == null && getTipoBusca().equals("SERVIDOR")) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o servidor.");
        }
        if (hierarquiaAdministrativa == null && getTipoBusca().equals("ORGAO")) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o órgão.");
        }
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data inicial.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data final.");
        }
        if (dataInicial != null && dataFinal != null) {
            if (dataInicial.compareTo(dataFinal) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Data inicial deve ser anterior a data final");
            }
        }
        ve.lancarException();
    }

    public void setTipoBusca(String tipoBusca) {
        this.tipoBusca = tipoBusca;
    }

    public String getTipoBusca() {
        return tipoBusca;
    }

    public void setTipoProvimento(TipoProvimento tipoProvimento) {
        this.tipoProvimento = tipoProvimento;
    }

    public TipoProvimento getTipoProvimento() {
        return tipoProvimento;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public VinculoFP getVinculoFPSelecionado() {
        return vinculoFPSelecionado;
    }

    public void setVinculoFPSelecionado(VinculoFP vinculoFPSelecionado) {
        this.vinculoFPSelecionado = vinculoFPSelecionado;
    }

    public Boolean getDetalhado() {
        return detalhado;
    }

    public void setDetalhado(Boolean detalhado) {
        this.detalhado = detalhado;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

}
