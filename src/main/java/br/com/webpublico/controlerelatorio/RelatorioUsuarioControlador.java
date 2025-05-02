package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
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
    @URLMapping(id = "relatorio-usuarios", pattern = "/relatorio/usuarios/", viewId = "/faces/admin/relatorio/relatoriousuarios.xhtml")
})
@ManagedBean
public class RelatorioUsuarioControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private String filtros;
    private UsuarioSistema usuarioSistema;
    private ModuloSistema moduloSistema;
    private PessoaFisica pessoaFisica;

    @URLAction(mappingId = "relatorio-usuarios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtros = "";
        usuarioSistema = null;
        moduloSistema = null;
        pessoaFisica = null;
    }

    public List<SelectItem> getModulosSistema() {
        return Util.getListSelectItem(ModuloSistema.values());
    }

    public List<UsuarioSistema> completarUsuariosSistema(String filtro) {
        return usuarioSistemaFacade.buscarTodosUsuariosPorLoginOuNomeOuCpf(filtro);
    }

    public List<PessoaFisica> completarPessoasFisicas(String filtro) {
        return pessoaFisicaFacade.listaFiltrandoTodasPessoasByNomeAndCpf(filtro);
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("parametros", montarParametros());
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio("Relatório de Usuários");
            dto.setApi("comum/usuarios/");
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
            retorno += clausula + " exists (select 1 from GRUPOUSUARIOSISTEMA gus " +
                " inner join grupousuario gu on gu.id = gus.GRUPOUSUARIO_ID " +
                " inner join ItemGrupoUsuario igu on gu.id = igu.GRUPOUSUARIO_ID " +
                " inner join gruporecurso gr on igu.GRUPORECURSO_ID = gr.id " +
                " inner join GRUPORECURSOSISTEMA grs on grs.GRUPORECURSO_ID = gr.ID " +
                " inner join RECURSOSISTEMA rs on grs.RECURSOSISTEMA_ID = rs.id " +
                " where rs.MODULO = '" + moduloSistema.name() + "'" +
                " and gus.USUARIOSISTEMA_ID = usu.id " +
                " union all" +
                " select 1 from RecursosUsuario ru " +
                " inner join RECURSOSISTEMA rs on ru.RECURSOSISTEMA_ID = rs.id " +
                " where rs.MODULO = '" + moduloSistema.name() + "'" +
                " and ru.USUARIOSISTEMA_ID = usu.id " +
                " )";
            clausula = " and ";
            filtros = " Módulo: " + moduloSistema.getDescricao() + " -";
        }

        if (pessoaFisica != null) {
            retorno += clausula + " pf.id = " + pessoaFisica.getId();
            clausula = " and ";
            filtros = " Pessoa Física: " + pessoaFisica.getNome() + " -";
        }
        if (usuarioSistema != null) {
            retorno += clausula + " usu.id = " + usuarioSistema.getId();
            filtros = " Usuário: " + usuarioSistema.getLogin() + " -";
        }
        atualizarFiltros();
        return retorno;
    }

    private void atualizarFiltros() {
        filtros = filtros.length() == 0 ? " " : filtros.substring(0, filtros.length() - 1);
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public ModuloSistema getModuloSistema() {
        return moduloSistema;
    }

    public void setModuloSistema(ModuloSistema moduloSistema) {
        this.moduloSistema = moduloSistema;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }
}
