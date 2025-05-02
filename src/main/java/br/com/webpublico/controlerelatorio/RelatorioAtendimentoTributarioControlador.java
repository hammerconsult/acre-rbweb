package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.UsuarioUnidadeOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 02/02/15
 * Time: 10:31
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-atendimento-tributario", pattern = "/relatorio-atendimento-tributario/", viewId = "/faces/tributario/contacorrente/relatorio/relatorioAtendimentoTributario.xhtml")
})
@ManagedBean
public class RelatorioAtendimentoTributarioControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    private StringBuffer filtro;
    private FiltrosRelatorio filtrosRelatorio;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public RelatorioAtendimentoTributarioControlador() {
    }

    @URLAction(mappingId = "relatorio-atendimento-tributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtrosRelatorio = new FiltrosRelatorio();
    }


    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (filtrosRelatorio.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        } if (filtrosRelatorio.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (filtrosRelatorio.getDataInicial().after(filtrosRelatorio.getDataFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Final deve ser superior a Data Inicial.");
        }
        ve.lancarException();
    }

    private String montarCondicaoEFiltros() {
        StringBuilder condicao = new StringBuilder();
        filtro = new StringBuffer();
        String juncao = " WHERE ";

        filtro.append(" Data Inicial: ");
        filtro.append(Util.formatterDiaMesAno.format(filtrosRelatorio.getDataInicial()));
        filtro.append("; ");
        filtro.append(" Data Final: ");
        filtro.append(Util.formatterDiaMesAno.format(filtrosRelatorio.getDataFinal()));
        filtro.append("; ");

        if (filtrosRelatorio.getUsuarios() != null && filtrosRelatorio.getUsuarios().size() > 0) {
            condicao.append(juncao);
            condicao.append(" atendimentos.usuario_id in ");
            condicao.append(filtrosRelatorio.inUsuarios());

            filtro.append(" Usuários: ");
            filtro.append(filtrosRelatorio.filtroUsuariosSelecionados());
        } else if (filtrosRelatorio.getHierarquias() != null && filtrosRelatorio.getHierarquias().size() > 0) {
            condicao.append(juncao);
            condicao.append(" exists (select 1 " +
                "     from usuariounidadeorganizacio " +
                "  where usuariounidadeorganizacio.usuariosistema_id = atendimentos.usuario_id " +
                "    and usuariounidadeorganizacio.unidadeorganizacional_id in ");
            condicao.append(filtrosRelatorio.inUnidades());
            condicao.append(") ");

            filtro.append(" Unidades Organizacionais: ");
            filtro.append(filtrosRelatorio.filtroHierarquiasSelecionadas());
        }
        return condicao.toString();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Tributário");
            dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças-SEFIN");
            dto.adicionarParametro("NOMERELATORIO", "Relatório de Atendimento do Tributário");
            dto.adicionarParametro("DATA_INICIAL", DataUtil.getDataFormatada(filtrosRelatorio.getDataInicial()));
            dto.adicionarParametro("DATA_FINAL", DataUtil.getDataFormatada(filtrosRelatorio.getDataFinal()));
            dto.adicionarParametro("condicao", montarCondicaoEFiltros());
            dto.adicionarParametro("FILTROS", filtro.toString());
            dto.setNomeRelatorio("Relatório de Atendimento Tributário");
            dto.setApi("tributario/atendimento-tributario/");
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

    public List<UsuarioSistema> completaUsuario(String parte) {
        return usuarioSistemaFacade.recuperarUsuariosPorHierarquiasAdministrativas(filtrosRelatorio.getHierarquias(), new Date(), parte);
    }

    public class FiltrosRelatorio {

        private Date dataInicial;
        private Date dataFinal;
        private HierarquiaOrganizacional hierarquiaOrganizacional;
        private List<HierarquiaOrganizacional> hierarquias;
        private UsuarioSistema usuarioSistema;
        private List<UsuarioSistema> usuarios;


        public void FiltrosRelatorio() {
            dataInicial = sistemaFacade.getDataOperacao();
            dataFinal = sistemaFacade.getDataOperacao();
            usuarios = Lists.newArrayList();
            hierarquias = Lists.newArrayList();
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

        public UsuarioSistema getUsuarioSistema() {
            return usuarioSistema;
        }

        public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
            this.usuarioSistema = usuarioSistema;
        }

        public List<UsuarioSistema> getUsuarios() {
            return usuarios;
        }

        public void setUsuarios(List<UsuarioSistema> usuarios) {
            this.usuarios = usuarios;
        }

        public HierarquiaOrganizacional getHierarquiaOrganizacional() {
            return hierarquiaOrganizacional;
        }

        public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
            this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        }

        public List<HierarquiaOrganizacional> getHierarquias() {
            return hierarquias;
        }

        public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
            this.hierarquias = hierarquias;
        }

        public void addHierarquia() {
            if (hierarquias == null) {
                hierarquias = new ArrayList();
            }
            if (hierarquiaOrganizacional == null) {
                FacesUtil.addOperacaoNaoPermitida("Selecione uma unidade organizacional.");
                return;
            }
            if (hierarquias.contains(hierarquiaOrganizacional)) {
                FacesUtil.addOperacaoNaoPermitida("Unidade Organizacional já adicionada.");
                return;
            }
            hierarquias.add(hierarquiaOrganizacional);
            hierarquiaOrganizacional = null;
        }

        public void delHierarquia(HierarquiaOrganizacional hierarquiaOrganizacional) {
            hierarquias.remove(hierarquiaOrganizacional);
            removeUsuariosQueNaoPossuemAsUnidadesSelecionada();
        }

        private boolean usuarioPossuiAlgumaDasUnidadesSelecionadas(UsuarioSistema usuarioSistema) {
            for (HierarquiaOrganizacional hierarquiaOrganizacional : filtrosRelatorio.getHierarquias()) {
                for (UsuarioUnidadeOrganizacional usuarioUnidadeOrganizacional : usuarioSistema.getUsuarioUnidadeOrganizacional()) {
                    if (hierarquiaOrganizacional.getSubordinada().getId().equals(usuarioUnidadeOrganizacional.getUnidadeOrganizacional().getId())) {
                        return true;
                    }

                }
            }
            return false;
        }

        private void removeUsuariosQueNaoPossuemAsUnidadesSelecionada() {
            if (filtrosRelatorio.getUsuarios() != null) {
                List<UsuarioSistema> remover = new ArrayList();
                for (UsuarioSistema u : filtrosRelatorio.getUsuarios()) {
                    if (!usuarioPossuiAlgumaDasUnidadesSelecionadas(u)) {
                        remover.add(u);
                    }
                }
                for (UsuarioSistema ur : remover) {
                    filtrosRelatorio.delUsuario(ur);
                }
            }
        }

        public void addUsuario() {
            if (usuarios == null) {
                usuarios = new ArrayList();
            }
            if (usuarioSistema == null) {
                FacesUtil.addOperacaoNaoPermitida("Selecione um usuário.");
                return;
            }
            if (usuarios.contains(usuarioSistema)) {
                FacesUtil.addOperacaoNaoPermitida("Usuário já adicionado.");
                return;
            }
            usuarios.add(usuarioSistemaFacade.recuperarUsuarioComUnidadesAdmistrativas(usuarioSistema.getId()));
            usuarioSistema = null;
        }

        public void delUsuario(UsuarioSistema usuarioSistema) {
            usuarios.remove(usuarioSistema);
        }

        public String inUsuarios() {
            StringBuilder in = new StringBuilder();
            String juncao = " (";

            if (usuarios != null && usuarios.size() > 0) {
                for (UsuarioSistema usuario : usuarios) {
                    in.append(juncao);
                    in.append(usuario.getId());
                    juncao = ", ";
                }
                in.append(") ");
            }
            return in.toString();
        }

        public String filtroUsuariosSelecionados() {
            StringBuilder selecionados = new StringBuilder();

            if (usuarios != null && usuarios.size() > 0) {
                for (UsuarioSistema usuario : usuarios) {
                    selecionados.append(usuario.getPessoaFisica().getNome());
                    selecionados.append("; ");
                }
            }
            return selecionados.toString();
        }

        public String inUnidades() {
            StringBuilder in = new StringBuilder();
            String juncao = " (";

            if (hierarquias != null && hierarquias.size() > 0) {
                for (HierarquiaOrganizacional hierarquia : hierarquias) {
                    in.append(juncao);
                    in.append(hierarquia.getSubordinada().getId());
                    juncao = ", ";
                }
                in.append(") ");
            }
            return in.toString();
        }

        public String filtroHierarquiasSelecionadas() {
            StringBuilder selecionados = new StringBuilder();

            if (hierarquias != null && hierarquias.size() > 0) {
                for (HierarquiaOrganizacional hierarquia : hierarquias) {
                    selecionados.append(hierarquia);
                    selecionados.append("; ");
                }
            }
            return selecionados.toString();
        }
    }

    public FiltrosRelatorio getFiltrosRelatorio() {
        return filtrosRelatorio;
    }

    public void setFiltrosRelatorio(FiltrosRelatorio filtrosRelatorio) {
        this.filtrosRelatorio = filtrosRelatorio;
    }
}
