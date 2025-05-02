package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.entidadesauxiliares.comum.GrupoRecursoVO;
import br.com.webpublico.entidadesauxiliares.comum.GrupoUsuarioVO;
import br.com.webpublico.entidadesauxiliares.comum.UsuarioSistemaVO;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioUsuarioSistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-usuario", pattern = "/relatorio/usuario-sistema/", viewId = "/faces/comum/relatorio-usuario/edita.xhtml")
})
public class RelatorioUsuarioSistemaControlador implements Serializable {
    @EJB
    private RelatorioUsuarioSistemaFacade facade;
    private List<RecursoSistema> recursosSelecionados;
    private Boolean somenteUsuariosAtivos;
    private Boolean exibirGrupoUsuario;
    private Boolean exibirGrupoRecurso;
    private String filtros;
    private List<UsuarioSistemaVO> usuarios;
    private List<GrupoUsuarioVO> gruposUsuarios;
    private List<GrupoRecursoVO> gruposRecursos;

    @URLAction(mappingId = "relatorio-usuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        recursosSelecionados = Lists.newArrayList();
        somenteUsuariosAtivos = Boolean.TRUE;
        exibirGrupoUsuario = Boolean.TRUE;
        exibirGrupoRecurso = Boolean.TRUE;
        usuarios = Lists.newArrayList();
        gruposUsuarios = Lists.newArrayList();
        gruposRecursos = Lists.newArrayList();
        filtros = "";
    }

    public void gerarRelatorio(String tipoExtensao) {
        try {
            validarGerarRelatorio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensao));
            dto.adicionarParametro("USER", facade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("FILTROS", filtros);
            dto.adicionarParametro("usuarios", usuarios);
            dto.adicionarParametro("TOTAL_USUARIOS", String.valueOf(usuarios.size()));
            if (exibirGrupoUsuario) {
                dto.adicionarParametro("gruposUsuarios", gruposUsuarios);
                dto.adicionarParametro("TOTAL_GRUPOS_USUARIOS", String.valueOf(gruposUsuarios.size()));
            }
            if (exibirGrupoRecurso) {
                dto.adicionarParametro("gruposRecursos", gruposRecursos);
                dto.adicionarParametro("TOTAL_GRUPOS_RECURSOS", String.valueOf(gruposRecursos.size()));
            }
            dto.setNomeRelatorio("Relatório de Usuários ");
            dto.setApi("comum/usuario-sistema/");
            ReportService.getInstance().gerarRelatorio(facade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void validarGerarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (usuarios == null || usuarios.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum usuário encontrado para gerar o relatório.");
        }
        ve.lancarException();
    }

    public void buscarUsuarios() {
        try {
            validarCampos();
            usuarios = facade.buscarUsuariosSistemasVO(montarCondicaoEFiltrosUtilizados());
            if (exibirGrupoUsuario) {
                gruposUsuarios = facade.buscarGruposUsuariosSistemasVO(montarCondicaoEFiltrosUtilizados());
            }
            if (exibirGrupoRecurso) {
                gruposRecursos = facade.buscarGruposRecursosSistemasVO(montarCondicaoEFiltrosUtilizados());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (recursosSelecionados == null || recursosSelecionados.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Deve ser selecionado um ou mais Recursos de Sistema.");
        }
        ve.lancarException();
    }

    private String montarCondicaoEFiltrosUtilizados() {
        String retorno = "";
        String clausula = " where";
        if (somenteUsuariosAtivos) {
            retorno += clausula + " ps.situacaocadastralpessoa = '" + SituacaoCadastralPessoa.ATIVO.name() + "'";
            clausula = " and";
            filtros += " Somente Usuários Ativos: Sim -";
        }
        if (recursosSelecionados != null && !recursosSelecionados.isEmpty()) {
            String condicaoIn = "(";
            String juncao = "";
            filtros += " Recursos: ";
            for (RecursoSistema recurso : recursosSelecionados) {
                condicaoIn += juncao + recurso.getId();
                filtros += juncao + recurso.getNome();
                juncao = ", ";
            }
            retorno += clausula + " rs.id in " + condicaoIn + ") ";
            filtros += " -";
        }
        filtros = filtros.substring(0, filtros.length() - 1);
        return retorno;
    }

    public List<RecursoSistema> getRecursosSelecionados() {
        return recursosSelecionados;
    }

    public void setRecursosSelecionados(List<RecursoSistema> recursosSelecionados) {
        this.recursosSelecionados = recursosSelecionados;
    }

    public Boolean getSomenteUsuariosAtivos() {
        return somenteUsuariosAtivos;
    }

    public void setSomenteUsuariosAtivos(Boolean somenteUsuariosAtivos) {
        this.somenteUsuariosAtivos = somenteUsuariosAtivos;
    }

    public Boolean getExibirGrupoUsuario() {
        return exibirGrupoUsuario;
    }

    public void setExibirGrupoUsuario(Boolean exibirGrupoUsuario) {
        this.exibirGrupoUsuario = exibirGrupoUsuario;
    }

    public Boolean getExibirGrupoRecurso() {
        return exibirGrupoRecurso;
    }

    public void setExibirGrupoRecurso(Boolean exibirGrupoRecurso) {
        this.exibirGrupoRecurso = exibirGrupoRecurso;
    }

    public List<UsuarioSistemaVO> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioSistemaVO> usuarios) {
        this.usuarios = usuarios;
    }

    public List<GrupoUsuarioVO> getGruposUsuarios() {
        return gruposUsuarios;
    }

    public void setGruposUsuarios(List<GrupoUsuarioVO> gruposUsuarios) {
        this.gruposUsuarios = gruposUsuarios;
    }

    public List<GrupoRecursoVO> getGruposRecursos() {
        return gruposRecursos;
    }

    public void setGruposRecursos(List<GrupoRecursoVO> gruposRecursos) {
        this.gruposRecursos = gruposRecursos;
    }
}
