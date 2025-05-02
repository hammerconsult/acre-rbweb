package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Menu;
import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
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
import java.io.Serializable;
import java.util.List;

/**
 * Created by Mateus on 22/01/2015.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-acessos", pattern = "/relatorio/acessos", viewId = "/faces/financeiro/relatorio/relatorioacessos.xhtml")
})
public class RelatorioAcessosControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private List<HierarquiaOrganizacional> unidades;
    private UsuarioSistema usuarioSistema;
    private String filtro;
    private List<RecursoSistema> recursos;
    private List<Menu> menus;

    public RelatorioAcessosControlador() {
    }

    @URLAction(mappingId = "relatorio-acessos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtro = "";
        usuarioSistema = null;
        unidades = Lists.newArrayList();
    }

    public void recuperarInformacoes() {
        try {
            if (usuarioSistema == null) {
                throw new ExcecaoNegocioGenerica("O Campo usuário deve ser informado.");
            }
            usuarioSistema = usuarioSistemaFacade.recuperar(usuarioSistema.getId());
            menus = usuarioSistemaFacade.getItensParaContruirMenu(usuarioSistema);
            recursos = Lists.newArrayList(usuarioSistemaFacade.getTodosRecursosDoUsuario(usuarioSistema));
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("EXERCICIO_ID", sistemaFacade.getExercicioCorrente().getId());
            dto.adicionarParametro("condicao", montarCondicao());
            dto.adicionarParametro("FILTROS", filtro);
            dto.adicionarParametro("DATALOGADO", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.setNomeRelatorio("Relatório de Relação de Acessos");
            dto.setApi("contabil/acesso-usuario/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicao() {
        StringBuilder sql = new StringBuilder();
        filtro = "";
        if (this.usuarioSistema != null) {
            sql.append(" and usu.id = ").append(usuarioSistema.getId());
            filtro += "Usuário: " + this.usuarioSistema.getLogin() + " - ";
        }
        if (!unidades.isEmpty()) {
            StringBuilder idUnidades = new StringBuilder();
            StringBuilder unds = new StringBuilder();
            String concatUnd = " ";
            for (HierarquiaOrganizacional ho : unidades) {
                idUnidades.append(concatUnd).append(ho.getId());
                unds.append(concatUnd).append(ho.getCodigo());
                concatUnd = ", ";
            }
            sql.append(" and VW.ID IN (").append(idUnidades.toString()).append(")");
            filtro += "Unidade(s): " + unds.toString() + " - ";
        } else {
            List<HierarquiaOrganizacional> undsUsuarios = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            StringBuilder unds = new StringBuilder();
            String concatUnd = " ";
            if (undsUsuarios != null && !undsUsuarios.isEmpty()) {
                for (HierarquiaOrganizacional ho : undsUsuarios) {
                    unds.append(concatUnd).append(ho.getId());
                    concatUnd = ", ";
                }
                sql.append(" and VW.ID IN (").append(unds.toString()).append(")");
            }
        }
        filtro = filtro.length() > 0 ? filtro.substring(0, filtro.length() - 1) : "";
        return sql.toString();
    }

    public List<UsuarioSistema> completaUsuario(String parte) {
        return usuarioSistemaFacade.buscarTodosUsuariosPorLoginOuNomeOuCpf(parte);
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<RecursoSistema> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursoSistema> recursos) {
        this.recursos = recursos;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }
}
