/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "relatorioServidoresPorVerbaRecVincFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-servidores-por-verba-rec-vincfp", pattern = "/relatorio/servidores-por-verba-recurso-vinculofp/", viewId = "/faces/rh/relatorios/relatorioservidoresporverbarecvincfp.xhtml")
})
public class RelatorioServidoresPorVerbaRecVincFPControlador implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    private Integer mes;
    private Integer ano;
    private Integer versao;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private EventoFP eventoFPSelecionado;
    private TipoFolhaDePagamento tipoFolhaDePagamento;

    public RelatorioServidoresPorVerbaRecVincFPControlador() {
    }

    @URLAction(mappingId = "relatorio-servidores-por-verba-rec-vincfp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        eventoFPSelecionado = null;
        hierarquiaOrganizacionalSelecionada = null;
        mes = null;
        ano = null;
        versao = null;
        tipoFolhaDePagamento = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosRelatorio();

            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("rh/servidores-por-verbas-recurso-vinculofp/");
            dto.setNomeRelatorio("servidores-por-verbas-recurso-vinculofp");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("FILTROS_SQL", gerarFiltrosSql());
            dto.adicionarParametro("TIPOFOLHA", "Folha " + tipoFolhaDePagamento.getDescricao());
            dto.adicionarParametro("DESCRICAOMESANO", Mes.getMesToInt(mes).getDescricao().toUpperCase() + " DE " + ano);
            dto.adicionarParametro("DESCRICAOVERBA", eventoFPSelecionado.getCodigo() + " " + eventoFPSelecionado.getDescricao());

            if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            } else {
                dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin());
            }
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private String gerarFiltrosSql() {
        String filtros = "";
        if (mes != null) {
            filtros += " and folha.mes = " + (mes - 1);
        }
        if (ano != null) {
            filtros += " and folha.ano = " + ano;
        }
        if (tipoFolhaDePagamento != null) {
            filtros += " and folha.tipofolhadepagamento = " + "'" + tipoFolhaDePagamento.name() + "'";
        }
        if (Integer.parseInt(hierarquiaOrganizacionalSelecionada.getCodigoNo()) == 1 && hierarquiaOrganizacionalSelecionada.getIndiceDoNo() == 1) {
            filtros += "";
        } else {
            filtros += " and rec.codigoorgao = cast('" + hierarquiaOrganizacionalSelecionada.getCodigoNo() + "' as integer) ";
        }
        if (eventoFPSelecionado != null) {
            filtros += " and evento.id = " + eventoFPSelecionado.getId();
        }
        if (versao != null) {
            filtros += " and folha.versao = " + versao;
        }
        return filtros;
    }

    public void validarFiltrosRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha deve ser informado.");
        }
        if (mes != null && (mes < 1 || mes > 12)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        if (eventoFPSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Verba deve ser informado.");
        }
        ve.lancarException();
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<EventoFP> completaEventoFP(String parte) {
        return eventoFPFacade.listaFiltrandoEventosAtivos(parte.trim());
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public EventoFP getEventoFPSelecionado() {
        return eventoFPSelecionado;
    }

    public void setEventoFPSelecionado(EventoFP eventoFPSelecionado) {
        this.eventoFPSelecionado = eventoFPSelecionado;
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Todas"));
        if (mes != null && ano != null && tipoFolhaDePagamento != null) {
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(Mes.getMesToInt(mes), ano, tipoFolhaDePagamento)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public List<SelectItem> getTipoFolhaPagamento() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }
}
