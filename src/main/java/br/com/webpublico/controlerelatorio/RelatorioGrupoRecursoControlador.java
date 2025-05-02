package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.GrupoRecurso;
import br.com.webpublico.entidades.GrupoUsuario;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.GrupoRecursoFacade;
import br.com.webpublico.negocios.GrupoUsuarioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-grupo-recurso", pattern = "/relatorio/grupo-recurso/", viewId = "/faces/admin/relatorio/relatoriogruporecursos.xhtml")
})
@ManagedBean
public class RelatorioGrupoRecursoControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GrupoRecursoFacade grupoRecursoFacade;
    @EJB
    private GrupoUsuarioFacade grupoUsuarioFacade;
    private String filtros;
    private ModuloSistema moduloSistema;
    private GrupoRecurso grupoRecurso;
    private GrupoUsuario grupoUsuario;
    private TipoExibicao tipoExibicao;

    public List<SelectItem> getModulosSistema() {
        return Util.getListSelectItem(ModuloSistema.values());
    }

    public List<SelectItem> getTiposDeExibicao() {
        return Util.getListSelectItemSemCampoVazio(TipoExibicao.values());
    }

    public List<GrupoRecurso> completarGruposDeRecurso(String filtro) {
        return grupoRecursoFacade.buscarGrupoRecursoPorNome(filtro);
    }

    public List<GrupoUsuario> completarGruposDeUsuario(String filtro) {
        return grupoUsuarioFacade.buscarGrupoUsuarioPorNome(filtro);
    }

    @URLAction(mappingId = "relatorio-grupo-recurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtros = "";
        moduloSistema = null;
        grupoRecurso = null;
        grupoUsuario = null;
        tipoExibicao = TipoExibicao.POR_GRUPO_RECURSO;
    }


    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("parametros", montarParametros());
            dto.adicionarParametro("PORGRUPORECURSO", tipoExibicao.isPorGrupoRecurso());
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio("Relatório de " + tipoExibicao.getNomeNoPlural());
            dto.setApi("comum/grupo-recurso/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
            filtros = "";
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarParametros() {
        String retorno = "";
        String clausula = " where ";
        if (moduloSistema != null) {
            retorno += clausula + " gr.moduloSistema = '" + moduloSistema.name() + "'";
            clausula = " and ";
            filtros = " Módulo: " + moduloSistema.getDescricao() + " -";
        }
        if (grupoRecurso != null) {
            retorno += clausula + " gr.id = " + grupoRecurso.getId();
            clausula = " and ";
            filtros = " Grupo de Recurso: " + grupoRecurso.getNome() + " -";
        }
        if (grupoUsuario != null) {
            retorno += clausula + " exists (select 1 from grupousuario gu " +
                "  inner join ItemGrupoUsuario igu on gu.id = igu.GRUPOUSUARIO_ID " +
                "  inner join gruporecurso grupo on igu.GRUPORECURSO_ID = grupo.id " +
                " where gu.id = " + grupoUsuario.getId() +
                "   and grupo.id = gr.id) ";
            filtros = " Grupo de Usuário: " + grupoUsuario.getNome() + " -";
        }
        atualizarFiltros();
        return retorno;
    }

    private void atualizarFiltros() {
        filtros = filtros.length() == 0 ? " " : filtros.substring(0, filtros.length() - 1);
    }

    public ModuloSistema getModuloSistema() {
        return moduloSistema;
    }

    public void setModuloSistema(ModuloSistema moduloSistema) {
        this.moduloSistema = moduloSistema;
    }

    public GrupoRecurso getGrupoRecurso() {
        return grupoRecurso;
    }

    public void setGrupoRecurso(GrupoRecurso grupoRecurso) {
        this.grupoRecurso = grupoRecurso;
    }

    public GrupoUsuario getGrupoUsuario() {
        return grupoUsuario;
    }

    public void setGrupoUsuario(GrupoUsuario grupoUsuario) {
        this.grupoUsuario = grupoUsuario;
    }

    public TipoExibicao getTipoExibicao() {
        return tipoExibicao;
    }

    public void setTipoExibicao(TipoExibicao tipoExibicao) {
        this.tipoExibicao = tipoExibicao;
    }

    public enum TipoExibicao {
        POR_GRUPO_RECURSO("Grupo de Recurso", "Grupos de Recursos"),
        POR_GRUPO_USUARIO("Grupo de Usuário", "Grupos de Usuários");

        private String descricao;
        private String nomeNoPlural;

        TipoExibicao(String descricao, String nomeNoPlural) {
            this.descricao = descricao;
            this.nomeNoPlural = nomeNoPlural;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getNomeNoPlural() {
            return nomeNoPlural;
        }

        public boolean isPorGrupoRecurso() {
            return POR_GRUPO_RECURSO.equals(this);
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
