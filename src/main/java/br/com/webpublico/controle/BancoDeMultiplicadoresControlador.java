package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCapacitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.BancoDeMultiplicadoresFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by carlos on 25/08/15.
 */
@ManagedBean(name = "bancoDeMultiplicadoresControlador")
@ViewScoped
@URLMapping(id = "novo-bancoDeMultiplicadores", pattern = "/banco-multiplicadores/", viewId = "/faces/rh/relatorios/multiplicadores.xhtml")
public class BancoDeMultiplicadoresControlador extends AbstractReport implements Serializable {

    @EJB
    private BancoDeMultiplicadoresFacade facade;
    private Habilidade habilidade;
    private Capacitacao capacitacao;
    private ContratoFP contratoFP;
    private Cargo cargo;
    private HierarquiaOrganizacional hierarquia;
    private HierarquiaOrganizacional unidade;

    public BancoDeMultiplicadoresControlador() {
    }

    @URLAction(mappingId = "novo-bancoDeMultiplicadores", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        habilidade = null;
        capacitacao = null;
        contratoFP = null;
        cargo = null;
        hierarquia = null;
        unidade = null;
    }

    public List<Capacitacao> completarEventoCapacitacao(String parte) {
        return facade.getEventoCapacitacaoFacade().buscarEventosCapacitacaoPorSituacaoEmAndamento(parte.trim(), SituacaoCapacitacao.EM_ANDAMENTO);
    }

    public List<Habilidade> completarHabilidade(String parte) {
        if (capacitacao != null) {
            return facade.getHabilidadeFacade().buscarHabilidadesPorCapacitacao(parte.trim(), capacitacao);
        }
        return facade.getHabilidadeFacade().completaHabilidade(parte.trim());
    }

    public List<ContratoFP> completarContrato(String parte) {
        if (capacitacao != null) {
            return facade.getContratoFPFacade().buscarContratoFPPorCapacitacao(parte.trim(), capacitacao);
        }
        return facade.getContratoFPFacade().buscarContratoFPPorCapacitacao(parte.trim(), null);
    }

    public List<Cargo> completarCargo(String parte) {
        if (capacitacao != null) {
            return facade.getCargoFacade().recuperarCargoPorCapacitacao(parte.trim(), capacitacao);
        }
        return facade.getCargoFacade().recuperarCargoPorCapacitacao(parte.trim(), null);
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        if (capacitacao != null) {
            return facade.getHierarquiaOrganizacionalFacade().recuperarHierarquiaOrganizacionalPorCapacitacao(parte.trim(), capacitacao);
        }
        return facade.getHierarquiaOrganizacionalFacade().recuperarHierarquiaOrganizacionalPorCapacitacao(parte.trim(), null);
    }

    public List<HierarquiaOrganizacional> completarUnidade(String parte) {
        if (capacitacao != null) {
            return facade.getHierarquiaOrganizacionalFacade().recuperarUnidadePorCapacitacao(parte.trim(), capacitacao);
        }
        return facade.getHierarquiaOrganizacionalFacade().recuperarUnidadePorCapacitacao(parte.trim(), null);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try{
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            dto.setNomeRelatorio("Relatório de Multiplicadores");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.adicionarParametro("FILTRO_SQL", filtroSql());
            dto.setApi("rh/relatorio-multiplicadores/");
            ReportService.getInstance().gerarRelatorio(Util.getSistemaControlador().getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Ocorreu um erro ao gerar o relatorio: " + e);
        }
    }

    private String filtroSql() {
        String condicao = " ";
        condicao = " where c.situacao = '" + SituacaoCapacitacao.EM_ANDAMENTO.name() + "'";
        condicao += " and   to_date('" + getDataOperacao() + "', 'dd/MM/yyyy') between trunc(lf.iniciovigencia) and coalesce(trunc(lf.finalvigencia), to_date('" + getDataOperacao() + "', 'dd/MM/yyyy'))";
        condicao += " and   to_date('" + getDataOperacao() + "', 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date('" + getDataOperacao() + "', 'dd/MM/yyyy'))";
        if (capacitacao != null) {
            condicao += " and e.id = " + capacitacao.getId();
        }
        if (habilidade != null) {
            condicao += " and ch.habilidade_id = " + habilidade.getId();
        }
        if (contratoFP != null) {
            condicao += " and cont.id = " + contratoFP.getId();
        }
        if (cargo != null) {
            condicao += " and cargo.id = " + cargo.getId();
        }
        if (hierarquia != null) {
            condicao += " and ho.codigo like '" + hierarquia.getCodigo().substring(0, 6) + "%'";
        }
        if (unidade != null) {
            condicao += " and lf.unidadeorganizacional_id = " + unidade.getSubordinada().getId();
        }
        return condicao;
    }

    private String getDataOperacao() {
        return DataUtil.getDataFormatada(facade.getSistemaFacade().getDataOperacao());
    }

    public void atribuirNullHabilidade() {
        setCapacitacao(null);
        setHabilidade(null);
        setContratoFP(null);
        setCargo(null);
        setHierarquia(null);
    }

    public Habilidade getHabilidade() {
        return habilidade;
    }

    public void setHabilidade(Habilidade habilidade) {
        this.habilidade = habilidade;
    }

    public Capacitacao getCapacitacao() {
        return capacitacao;
    }

    public void setCapacitacao(Capacitacao capacitacao) {
        this.capacitacao = capacitacao;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public HierarquiaOrganizacional getHierarquia() {
        return hierarquia;
    }

    public void setHierarquia(HierarquiaOrganizacional hierarquia) {
        this.hierarquia = hierarquia;
    }

    public HierarquiaOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(HierarquiaOrganizacional unidade) {
        this.unidade = unidade;
    }
}
