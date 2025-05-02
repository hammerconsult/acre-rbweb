/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SituacaoFuncional;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
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
 * @author peixe
 */
@ManagedBean(name = "relatorioServidoresQueEntraramOuSairamDaFP")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioServidoresQueEntraramOuSairamDaFP", pattern = "/relatorio/servidores-entraram-sairam-folha/novo/", viewId = "/faces/rh/relatorios/relatorioservidoresqueentraramousairamdafp.xhtml")
})
public class RelatorioServidoresQueEntraramOuSairamDaFP extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private Mes mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private EntradaSaida entradaSaida;
    private Integer versao;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private String filtro;

    public RelatorioServidoresQueEntraramOuSairamDaFP() {
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            filtro = "";
            String condicao =  buscarOrgao();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            dto.adicionarParametro("NOMERELATORIO", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("TIPORELATORIO", entradaSaida.getDescricao());
            dto.adicionarParametro("VERSAO", versao);
            dto.adicionarParametro("DATARELATORIO", mes.getNumeroMes() + "/" + ano);
            if (EntradaSaida.SAIDA.equals(entradaSaida)) {
                condicao += " and sf.codigo <> " + SituacaoFuncional.ATIVO_PARA_FOLHA;
                dto.adicionarParametro("ANOANTERIOR", ano);
                if (mes.getNumeroMes() == 1) {
                    dto.adicionarParametro("MESANTERIOR", mes.getNumeroMes() - 1);
                    dto.adicionarParametro("MES", 11);
                    dto.adicionarParametro("ANO", ano - 1);
                } else {
                    dto.adicionarParametro("MESANTERIOR", mes.getNumeroMes() - 1);
                    dto.adicionarParametro("MES", mes.getNumeroMes() - 2);
                    dto.adicionarParametro("ANO", ano);
                }
            } else {
                condicao += " and sf.codigo = " + SituacaoFuncional.ATIVO_PARA_FOLHA;
                dto.adicionarParametro("MES", mes.getNumeroMes() - 1);
                dto.adicionarParametro("ANO", ano);
                if (mes.getNumeroMes() == 1) {
                    dto.adicionarParametro("MESANTERIOR", 11);
                    dto.adicionarParametro("ANOANTERIOR", ano - 1);
                } else {
                    dto.adicionarParametro("MESANTERIOR", mes.getNumeroMes() - 2);
                    dto.adicionarParametro("ANOANTERIOR", ano);
                }
            }
            dto.adicionarParametro("TIPOFOLHA", tipoFolhaDePagamento.getToDto());
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("condicao", condicao);
            dto.adicionarParametro("FILTROS", filtro);
            dto.setNomeRelatorio("Relatório-de-Servidores-que-Entraram-ou-Sairam-da-Folha-de-Pagamento");
            dto.setApi("rh/servidores-entraram-sairam-folha/");
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

        if (versao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Versão é obrigatório");
        }

        ve.lancarException();
    }

    private String buscarOrgao() {
        filtro = "";
        String retorno = " ";
        if (hierarquiaOrganizacional != null) {
            filtro += hierarquiaOrganizacional.getSubordinada().getDescricao().toUpperCase();
            return " AND ho.codigo like '" + hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%'";
        }
        return retorno;
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

    @URLAction(mappingId = "novoRelatorioServidoresQueEntraramOuSairamDaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mes = Mes.JANEIRO;
        ano = getSistemaFacade().getExercicioCorrente().getAno();
        tipoFolhaDePagamento = null;
        versao = null;
        filtro = null;
        entradaSaida = EntradaSaida.ENTRADA;
        hierarquiaOrganizacional = null;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipos() {
        List<SelectItem> retorno = new ArrayList<>();
        for (EntradaSaida entradaSaida : EntradaSaida.values()) {
            retorno.add(new SelectItem(entradaSaida, entradaSaida.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (mes != null && ano != null && tipoFolhaDePagamento != null) {
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(mes, ano, tipoFolhaDePagamento)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes object : Mes.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public EntradaSaida getEntradaSaida() {
        return entradaSaida;
    }

    public void setEntradaSaida(EntradaSaida entradaSaida) {
        this.entradaSaida = entradaSaida;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public enum EntradaSaida {
        ENTRADA("Servidores que entraram na folha"),
        SAIDA("Servidores que saíram da folha");

        private String descricao;

        EntradaSaida(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

    }
}
