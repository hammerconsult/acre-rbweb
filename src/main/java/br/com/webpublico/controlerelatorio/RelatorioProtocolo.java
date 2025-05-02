package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.SituacaoProcesso;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoRelatorioQuantitativoProtocolo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "relatorioProtocolo")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-protocolo", pattern = "/protocolo/relatorio/relatorio-de-protocolos/", viewId = "/faces/tributario/cadastromunicipal/relatorio/relatorio-de-protocolos.xhtml"),
})
public class RelatorioProtocolo implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaProtocolo;
    private HierarquiaOrganizacional hierarquiaOrigem;
    private HierarquiaOrganizacional hierarquiaDestino;
    private TipoRelatorioQuantitativoProtocolo tipoRelatorio;
    private String filtros;

    public RelatorioProtocolo() {
    }

    @URLAction(mappingId = "relatorio-protocolo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = sistemaFacade.getDataOperacao();
        dataFinal = sistemaFacade.getDataOperacao();
        hierarquiaProtocolo = null;
        hierarquiaOrigem = null;
        hierarquiaDestino = null;
        tipoRelatorio = null;
    }

    public List<HierarquiaOrganizacional> completarHierarquiasAdministrativas(String filtro) {
        return usuarioSistemaFacade.filtrandoHierarquiaOrganizacionalAdministrativaComUsuarios(filtro, dataFinal != null ? dataFinal : sistemaFacade.getDataOperacao());
    }

    public List<SelectItem> getTiposRelatorios() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (TipoRelatorioQuantitativoProtocolo trqp : TipoRelatorioQuantitativoProtocolo.values()) {
            if (TipoRelatorioQuantitativoProtocolo.EXPEDIDOS.equals(trqp)) {
                retorno.add(new SelectItem(trqp, "Gerados"));
            } else {
                retorno.add(new SelectItem(trqp, trqp.getDescricao()));
            }
        }
        return retorno;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data final deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser anterior ou igual a Data Final!");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            HierarquiaOrganizacional ho = hierarquiaOrigem != null ? hierarquiaOrigem : hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataFinal, sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("DESCRICAO_HIERARQUIA", ho.getDescricao());
            dto.adicionarParametro("condicao", montarCondicaoEFiltros());
            dto.adicionarParametro("tipoRelatorio", tipoRelatorio != null ? tipoRelatorio.getToDto() : "");
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/relatorio-protocolo/");
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

    private String getNomeRelatorio() {
        String retorno = "RELATÓRIO DE PROTOCOLOS ";
        if (tipoRelatorio != null) {
            retorno += TipoRelatorioQuantitativoProtocolo.EXPEDIDOS.equals(tipoRelatorio) ? "GERADOS" : tipoRelatorio.getDescricao().toUpperCase();
        }
        return retorno;
    }

    public String montarCondicaoEFiltros() {
        String condicao = " and trunc(p.dataregistro) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') ";
        filtros = "Período: " + DataUtil.getDataFormatada(dataInicial) + " à " + DataUtil.getDataFormatada(dataFinal);
        if ((hierarquiaOrigem == null && hierarquiaDestino == null && hierarquiaProtocolo == null)
            && !TipoRelatorioQuantitativoProtocolo.EXPEDIDOS.equals(tipoRelatorio)) {
            condicao += " and tr.DATAREGISTRO = (SELECT max(t.DATAREGISTRO) from TRAMITE t where t.PROCESSO_ID = p.ID) ";
        }
        if (hierarquiaProtocolo != null) {
            condicao += " and hoProtocolo.subordinada_id = " + hierarquiaProtocolo.getSubordinada().getId();
            filtros += " - Unidade do Protocolo: " + hierarquiaProtocolo;
        }
        if (hierarquiaOrigem != null) {
            condicao += " and hoOrigem.subordinada_id = " + hierarquiaOrigem.getSubordinada().getId();
            filtros += " - Unidade de Origem do Tramite: " + hierarquiaOrigem;
        }
        if (hierarquiaDestino != null) {
            condicao += " and hoDestino.subordinada_id = " + hierarquiaDestino.getSubordinada().getId();
            filtros += " - Unidade de Destino do Tramite: " + hierarquiaDestino;
        }
        if (tipoRelatorio != null) {
            filtros += " - Estado do Protocolo: " + (TipoRelatorioQuantitativoProtocolo.EXPEDIDOS.equals(tipoRelatorio) ? "Gerados" : tipoRelatorio.toString());
            switch (tipoRelatorio) {
                case EXPEDIDOS:
                    condicao += " and tr.DATAREGISTRO = (SELECT min(t.DATAREGISTRO) from TRAMITE t where t.PROCESSO_ID = p.ID) ";
                    break;
                case RECEBIDOS:
                    condicao += " and hoDestino.ID is not null ";
                    break;
                case ENCAMINHADOS:
                    condicao += " and hoDestino.ID is not null and p.situacao <> '" + SituacaoProcesso.GERADO.name() + "' ";
                    break;
                case ARQUIVADOS:
                    condicao += " AND p.confidencial = 0 and p.situacao = '" + SituacaoProcesso.ARQUIVADO.name() + "' ";
                    break;
                case FINALIZADOS:
                    condicao += " AND p.confidencial = 0 and p.situacao = '" + SituacaoProcesso.FINALIZADO.name() + "' ";
                    break;
                case ACEITOS:
                    condicao += " and tr.DATAACEITE is not null ";
                    break;
            }
        }
        filtros = filtros.substring(0, filtros.length() - 1);
        return condicao;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public TipoRelatorioQuantitativoProtocolo getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorioQuantitativoProtocolo tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public HierarquiaOrganizacional getHierarquiaOrigem() {
        return hierarquiaOrigem;
    }

    public void setHierarquiaOrigem(HierarquiaOrganizacional hierarquiaOrigem) {
        this.hierarquiaOrigem = hierarquiaOrigem;
    }

    public HierarquiaOrganizacional getHierarquiaDestino() {
        return hierarquiaDestino;
    }

    public void setHierarquiaDestino(HierarquiaOrganizacional hierarquiaDestino) {
        this.hierarquiaDestino = hierarquiaDestino;
    }

    public HierarquiaOrganizacional getHierarquiaProtocolo() {
        return hierarquiaProtocolo;
    }

    public void setHierarquiaProtocolo(HierarquiaOrganizacional hierarquiaProtocolo) {
        this.hierarquiaProtocolo = hierarquiaProtocolo;
    }
}
