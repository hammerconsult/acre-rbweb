/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle.contabil;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.ConfiguracaoHierarquiaOrganizacional;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.contabil.ReformaAdministrativa;
import br.com.webpublico.entidades.contabil.ReformaAdministrativaUnidade;
import br.com.webpublico.entidades.contabil.ReformaAdministrativaUnidadeUsuario;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.contabil.TipoReformaAdministrativa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.contabil.execucao.ReformaAdministrativaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Html2Pdf;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@ManagedBean(name = "reformaAdministrativaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoReformaAdministrativa", pattern = "/contabil/reforma-administrativa/novo/", viewId = "/faces/financeiro/reforma-administrativa/edita.xhtml"),
    @URLMapping(id = "editarReformaAdministrativa", pattern = "/contabil/reforma-administrativa/editar/#{reformaAdministrativaControlador.id}/", viewId = "/faces/financeiro/reforma-administrativa/edita.xhtml"),
    @URLMapping(id = "listarReformaAdministrativa", pattern = "/contabil/reforma-administrativa/listar/", viewId = "/faces/financeiro/reforma-administrativa/lista.xhtml"),
    @URLMapping(id = "verReformaAdministrativa", pattern = "/contabil/reforma-administrativa/ver/#{reformaAdministrativaControlador.id}/", viewId = "/faces/financeiro/reforma-administrativa/visualizar.xhtml")
})
public class ReformaAdministrativaControlador extends PrettyControlador<ReformaAdministrativa> implements Serializable, CRUD {
    @EJB
    private ReformaAdministrativaFacade facade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    private ReformaAdministrativaUnidade unidade;
    private TreeNode root;
    private Boolean marcarTodos;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ReformaAdministrativaControlador() {
        super(ReformaAdministrativa.class);
    }

    @URLAction(mappingId = "novoReformaAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setData(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUnidades(Lists.newArrayList());
        marcarTodos = Boolean.FALSE;
    }

    @URLAction(mappingId = "editarReformaAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        ordenarUnidades();
        marcarTodos = Boolean.FALSE;
        facade.recuperarHierarquiasDasUnidadesDestinos(selecionado);
    }

    @URLAction(mappingId = "verReformaAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        ordenarUnidades();
        facade.recuperarHierarquiasDasUnidadesDestinos(selecionado);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/reforma-administrativa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    public boolean isDeferida() {
        return selecionado.getDeferidaEm() != null;
    }

    public void validarEstruturaUnidades() {
        try {
            selecionado = facade.salvarRetornando(selecionado);
            selecionado = validarEstruturaUnidades(selecionado);
            FacesUtil.atualizarComponente("form-validacao-estrutura");
            FacesUtil.executaJavaScript("dlgValidacaoEstrutura.show()");
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void deferirReforma() {
        try {
            ordenarUnidades();
            selecionado = validarEstruturaUnidades(selecionado);
            if (!selecionado.getErros().isEmpty()) {
                throw new ExcecaoNegocioGenerica("Corrigir os erros antes de deferir.");
            }
            selecionado = facade.deferirReforma(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        if (selecionado.getTipo() == null) {
            return Lists.newArrayList();
        }
        return facade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "2", selecionado.getTipo().name(), selecionado.getData());
    }

    public List<HierarquiaOrganizacional> completarTodasHierarquias(String parte) {
        if (selecionado.getTipo() == null) {
            return Lists.newArrayList();
        }
        return facade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaHorganizacionalTipo(parte.trim(), selecionado.getTipo().name(), selecionado.getData());
    }

    public void selecionarTipoHierarquia() {
        this.selecionado.setSecretaria(null);
    }

    public void confirmarSuperior() {
        FacesUtil.atualizarComponente("Formulario");
        FacesUtil.executaJavaScript("dlgAlterarSuperior.hide()");
    }

    public void alterarSuperior(ReformaAdministrativaUnidade unidade) {
        this.unidade = unidade;
        FacesUtil.atualizarComponente("form-alterar-superior");
        FacesUtil.executaJavaScript("dlgAlterarSuperior.show()");
    }

    public void adicionarFilha(ReformaAdministrativaUnidade unidade) {
        try {
            ReformaAdministrativaUnidade filha = new ReformaAdministrativaUnidade();
            filha.setReformaAdministrativa(selecionado);
            filha.setUnidadeSuperior(unidade);
            filha.setUnidade(unidade.getUnidade());
            if (unidade.getInicioVigencia() != null) {
                filha.setInicioVigencia(unidade.getInicioVigencia());
            } else {
                filha.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
            }

            filha.setFimVigencia(unidade.getFimVigencia());
            if (unidade.getCodigoNovo() != null) {
                filha.setCodigoNovo(unidade.getCodigoNovo());
            } else {
                filha.setCodigoNovo(unidade.getUnidade().getCodigo());
            }
            if (unidade.getDescricaoNovo() != null) {
                filha.setDescricaoNovo(unidade.getDescricaoNovo() + " - NOVO ");
            } else {
                filha.setDescricaoNovo(unidade.getUnidade().getDescricao() + " - NOVO ");
            }
            filha.setTipo(TipoReformaAdministrativa.CRIAR_NOVO_FILHO);

            selecionado.getUnidades().add(filha);

            ordenarUnidades();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar filha. Detalhes do erro: " + e.getMessage());
        }
    }

    public void removerUnidade(ReformaAdministrativaUnidade unidade) {
        selecionado.getUnidades().remove(unidade);
    }

    public void selecionarTipoUnidade(ReformaAdministrativaUnidade unidade, TipoReformaAdministrativa tipo) {
        if (unidade != null) {
            unidade.setTipo(tipo);
            if (unidade.getTipo().isCriarNovo() || unidade.getTipo().isAlterarExistente()) {
                unidade.setCodigoNovo(unidade.getUnidade().getCodigo());
                unidade.setDescricaoNovo(unidade.getUnidade().getDescricao());
                Integer ano = facade.getSistemaFacade().getExercicioCorrente().getAno();
                ano++;
                LocalDate localDate = DataUtil.criarDataPrimeiroDiaMes(1, ano);
                unidade.setInicioVigencia(DataUtil.localDateToDate(localDate));
            }
            if (unidade.getTipo().isEncerrar()) {
                Integer ano = facade.getSistemaFacade().getExercicioCorrente().getAno();
                DateTime localDate = DataUtil.criarDataUltimoDiaMes(12, ano);
                unidade.setFimVigencia(localDate.toDate());
            }
            if (unidade.getTipo().isNaoAlterar()) {
                unidade.setCodigoNovo(null);
                unidade.setDescricaoNovo(null);
                unidade.setInicioVigencia(null);
                unidade.setFimVigencia(null);
            }
        }
    }

    public List<ReformaAdministrativaUnidade> getSelectItensHierarquia() {
        List<ReformaAdministrativaUnidade> retorno = Lists.newArrayList();
        if (selecionado != null && selecionado.getUnidades() != null) {
            for (ReformaAdministrativaUnidade unidade : selecionado.getUnidades()) {
                if (unidade.getTipo().isNaoAlterar()) {
                    retorno.add(unidade);
                }
            }
        }
        return retorno;
    }

    public void selecionarHierarquia(HierarquiaOrganizacional hierarquia, boolean limpar) {
        if (limpar) {
            selecionado.getUnidades().clear();
        }
        adicionarHierarquia(hierarquia);
        List<HierarquiaOrganizacional> filhas = recuperarFilhas(hierarquia);
        if (filhas != null) {
            adicionarHierarquias(filhas);
            for (HierarquiaOrganizacional filha : filhas) {
                selecionarHierarquia(filha, false);
            }
        }
        ordenarUnidades();
    }

    private void ordenarUnidades() {
        Collections.sort(selecionado.getUnidades(), new Comparator<ReformaAdministrativaUnidade>() {
            @Override
            public int compare(ReformaAdministrativaUnidade o1, ReformaAdministrativaUnidade o2) {
                try {
                    String codigo1 = o1.getCodigoNovo() != null ? o1.getCodigoNovo() : o1.getUnidade().getCodigo();
                    String codigo2 = o2.getCodigoNovo() != null ? o2.getCodigoNovo() : o2.getUnidade().getCodigo();
                    return codigo1.compareTo(codigo2);
                } catch (Exception e) {
                    logger.error("Erro ao ordenar unidades.", e);
                    return Integer.MAX_VALUE;
                }
            }
        });
    }

    public void adicionarHierarquias(List<HierarquiaOrganizacional> filhas) {
        if (filhas != null) {
            for (HierarquiaOrganizacional filha : filhas) {
                adicionarHierarquia(filha);
            }
        }
    }

    private void adicionarHierarquia(HierarquiaOrganizacional filha) {
        Optional<ReformaAdministrativaUnidade> first = selecionado.getUnidades().stream().filter(f -> f.getUnidade().getId().equals(filha.getId())).findFirst();
        if (!first.isPresent()) {
            ReformaAdministrativaUnidade superior = getHierarquiaSuperior(filha);
            facade.criarReformaUnidadeUsuario(superior);
            ReformaAdministrativaUnidade novaReforma = new ReformaAdministrativaUnidade(filha, superior, selecionado);
            facade.criarReformaUnidadeUsuario(novaReforma);
            selecionado.getUnidades().add(novaReforma);
        }
    }

    private ReformaAdministrativaUnidade getHierarquiaSuperior(HierarquiaOrganizacional filha) {
        for (ReformaAdministrativaUnidade unidade : selecionado.getUnidades()) {
            if (filha.getSuperior().getId().equals(unidade.getUnidade().getSubordinada().getId())) {
                return unidade;
            }
        }

        return null;
    }

    public List<HierarquiaOrganizacional> recuperarFilhas(HierarquiaOrganizacional hierarquiaOrganizacional) {
        List<HierarquiaOrganizacional> filhas = facade.getHierarquiaOrganizacionalFacade().getHierarquiasFilhasDe(hierarquiaOrganizacional.getSubordinada(), selecionado.getData(), selecionado.getTipo());
        return filhas;
    }

    public List<SelectItem> getTipos() {
        return Util.getListSelectItem(TipoReformaAdministrativa.values());
    }

    public List<SelectItem> getTiposHierarquia() {
        return Util.getListSelectItem(TipoHierarquiaOrganizacional.values());
    }

    public void montarEstrutura() {
        root = montarArvore();
        FacesUtil.atualizarComponente("form-estrutura-organizacional");
        FacesUtil.executaJavaScript("dlgEstruturaOrganizacional.show()");
    }

    private TreeNode montarArvore() {
        TreeNode raiz = new DefaultTreeNode(new ReformaAdministrativaUnidade(selecionado.getSecretaria(), null, selecionado), null);
        try {
            List<ReformaAdministrativaUnidade> listaHO = selecionado.getUnidades();
            if (!listaHO.isEmpty()) {
                arvoreMontada(raiz, listaHO);
            }
            raiz.isExpanded();
        } catch (ExcecaoNegocioGenerica e) {
            logger.error("Erro Montando Arvore de HierarquiaOrganizacional " + selecionado.getTipo() + " com Data [" + DataUtil.getDataFormatada(selecionado.getData()) + "]: ", e);
        }
        return raiz;
    }

    public void arvoreMontada(TreeNode raiz, List<ReformaAdministrativaUnidade> listaHO) {
        for (ReformaAdministrativaUnidade hierarquiaOrganizacional : listaHO) {
            TreeNode treeNode = new DefaultTreeNode(hierarquiaOrganizacional, getPai(hierarquiaOrganizacional, raiz));
            treeNode.isExpanded();
        }
    }

    public TreeNode getPai(ReformaAdministrativaUnidade ho, TreeNode root) {
        ReformaAdministrativaUnidade no = (ReformaAdministrativaUnidade) root.getData();
        if (no.equals(ho.getUnidadeSuperior())) {
            return root;
        }
        for (TreeNode treeNodeDaVez : root.getChildren()) {
            TreeNode treeNode = getPai(ho, treeNodeDaVez);
            ReformaAdministrativaUnidade hoRecuparado = (ReformaAdministrativaUnidade) treeNode.getData();
            if (hoRecuparado.equals(ho.getUnidadeSuperior())) {
                return treeNode;
            }
        }
        return root;
    }

    public void gerarPDF() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(getConteudoHtml(), baos);
            byte[] bytes = baos.toByteArray();
            AbstractReport.poeRelatorioNaSessao(bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar o relatório da reforma administrativa. Detalhes do erro: " + e.getMessage());
        }
    }

    private String getConteudoHtml() {
        Date hoje = new Date();
        StringBuilder listItens = new StringBuilder("");

        listItens.append("<tr>");
        listItens.append("<td> Ação </td>");
        listItens.append("<td> Unidade </td>");
        listItens.append("<td> Novo/Alterar </td>");
        listItens.append("<td> Vigência </td>");
        listItens.append("</tr>");
        ordenarUnidades();
        for (ReformaAdministrativaUnidade unidade : selecionado.getUnidades()) {
            listItens.append("<tr>");
            listItens.append("<td> " + unidade.getTipo().getDescricao() + " </td>");
            listItens.append("<td> " + unidade.getUnidade().toString() + " </td>");

            listItens.append("<td> <p> " + (unidade.getCodigoNovo() != null ? unidade.getCodigoNovo() : "") + " </p>");
            listItens.append("<p> " + (unidade.getDescricaoNovo() != null ? unidade.getDescricaoNovo() : "") + " </p> </td>");

            listItens.append("<td> <p> " + (unidade.getInicioVigencia() != null ? DataUtil.getDataFormatada(unidade.getInicioVigencia()) : "") + " ");
            listItens.append("  " + (unidade.getFimVigencia() != null ? DataUtil.getDataFormatada(unidade.getFimVigencia()) : "") + " </p> </td>");
            listItens.append("</tr>");
        }
        StringBuilder conteudo = new StringBuilder("");
        conteudo.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>");
        conteudo.append(" <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        conteudo.append(" <html>");
        conteudo.append(" <head>");
        conteudo.append(" <style type=\"text/css\"  media=\"all\">");
        conteudo.append(" @page{");
        conteudo.append(" size: A4 landscape; ");
        conteudo.append(" margin-top: 1.5in;");
        conteudo.append(" margin-bottom: 1.0in;");
        conteudo.append(" @bottom-center {");
        conteudo.append(" content: element(footer);");
        conteudo.append(" }");
        conteudo.append(" @top-center {");
        conteudo.append(" content: element(header);");
        conteudo.append(" }");
        conteudo.append("}");
        conteudo.append("#page-header {");
        conteudo.append(" display: block;");
        conteudo.append(" position: running(header);");
        conteudo.append(" }");
        conteudo.append(" #page-footer {");
        conteudo.append(" display: block;");
        conteudo.append(" position: running(footer);");
        conteudo.append(" }");
        conteudo.append(" .page-number:before {");
        conteudo.append("  content: counter(page) ");
        conteudo.append(" }");
        conteudo.append(" .page-count:before {");
        conteudo.append(" content: counter(pages);  ");
        conteudo.append("}");
        conteudo.append("</style>");
        conteudo.append(" <style type=\"text/css\">");
        conteudo.append(".igualDataTable{");
        conteudo.append("    border-collapse: collapse; /* CSS2 */");
        conteudo.append("    width: 100%;");
        conteudo.append("    ;");
        conteudo.append("}");
        conteudo.append(".igualDataTable th{");
        conteudo.append("    border: 0px solid #aaaaaa; ");
        conteudo.append("    height: 20px;");
        conteudo.append("    background: #ebebeb 50% 50% repeat-x;");
        conteudo.append("}");
        conteudo.append(".igualDataTable td{");
        conteudo.append("    padding: 2px;");
        conteudo.append("    border: 0px; ");
        conteudo.append("    height: 20px;");
        conteudo.append("}");
        conteudo.append(".igualDataTable thead td{");
        conteudo.append("    border: 1px solid #aaaaaa; ");
        conteudo.append("    background: #6E95A6 repeat-x scroll 50% 50%; ");
        conteudo.append("    border: 0px; ");
        conteudo.append("    text-align: center; ");
        conteudo.append("    height: 20px;");
        conteudo.append("}");
        conteudo.append(" .igualDataTable tr:nth-child(2n+1) {");
        conteudo.append(" background:lightgray;");
        conteudo.append(" }");
        conteudo.append("body{");
        conteudo.append("font-size: 8pt; font-family:\"Arial\", Helvetica, sans-serif");
        conteudo.append("}");
        conteudo.append("</style>");
        conteudo.append(" <title>");
        conteudo.append(" < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">");
        conteudo.append(" </title>");
        conteudo.append(" </head>");
        conteudo.append(" <body>");
        conteudo.append(" <div id=\"page-header\">");
        conteudo.append(" <table style='line-height:15%; width: 100%;'>");
        conteudo.append("<tr>");
        conteudo.append("<td >");
        conteudo.append(adicionaCabecalho());
        conteudo.append("</td >");
        conteudo.append("</tr>");
        conteudo.append(" </table>");
        conteudo.append(" </div>");
        conteudo.append(" <div id=\"page-footer\">");
        conteudo.append(" <hr noshade/>");
        conteudo.append(" <table style='line-height:15%; width: 100%;'>");
        conteudo.append("<tr>");
        conteudo.append("<td >");
        conteudo.append("<p>");
        conteudo.append("WebPúblico");
        conteudo.append("</p>");
        conteudo.append("</td>");
        conteudo.append("<td style='text-align: right'>");
        conteudo.append("<p>");
        conteudo.append("Usuário: ");
        conteudo.append(facade.getSistemaFacade().getLogin());
        conteudo.append(" - Emitido em ");
        conteudo.append(Util.formatterDiaMesAno.format(hoje));
        conteudo.append(" às ").append(Util.formatterHoraMinuto.format(hoje));
        conteudo.append("</p>");
        conteudo.append("</td>");
        conteudo.append("</tr>");
        conteudo.append(" </table>");
        conteudo.append(" </div>");
        conteudo.append(" <div id=\"page-content\">");
        conteudo.append(" <br/>");
        conteudo.append(" <hr noshade/>");
        if (!listItens.toString().isEmpty()) {
            conteudo.append("<table style=\"width:100%;\" class=\"igualDataTable\">");
            conteudo.append(listItens);
            conteudo.append("</table>");
        }
        conteudo.append("</div>");
        conteudo.append(" </body> </html>");
        return conteudo.toString();
    }

    public String adicionaCabecalho() {
        String caminhoDaImagem = facade.getDocumentoOficialFacade().geraUrlImagemDir() + "img/Brasao_de_Rio_Branco_novo.gif";
        String conteudo =
            "<table>"
                + "<tbody>"
                + "<tr>"
                + "<td style=\"text-align: center;\">"
                + "<img src=\"" + caminhoDaImagem + "\" width=\"54\" height=\"70\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" />"
                + "</td>"
                + "<td style=\"line-height:100%; \">"
                + "<h2>Prefeitura do Município de Rio Branco</h2>"
                + "<h3>" + facade.getSistemaFacade().getSistemaService().getHierarquiAdministrativaCorrente().getDescricao() + " </h3>"
                + " <h3>Relatório da Reforma Administriva " + selecionado.getNumero() + " - Data: " + DataUtil.getDataFormatada(selecionado.getData()) + "</h3>"
                + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>";
        return conteudo;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public ReformaAdministrativaUnidade getUnidade() {
        return unidade;
    }

    public void setUnidade(ReformaAdministrativaUnidade unidade) {
        this.unidade = unidade;
    }

    public Boolean getMarcarTodos() {
        return marcarTodos == null ? Boolean.FALSE : marcarTodos;
    }

    public void setMarcarTodos(Boolean marcarTodos) {
        this.marcarTodos = marcarTodos;
    }

    public void marcarDesmarcarTodos() {
        if (unidade != null) {
            unidade.getUsuarios().forEach(reformaUsuario -> reformaUsuario.setMarcado(marcarTodos));
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public ReformaAdministrativa validarEstruturaUnidades(ReformaAdministrativa entity) {
        try {
            limparErros(entity);

            ConfiguracaoHierarquiaOrganizacional configuracaoHierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getConfiguracaoHierarquiaOrganizacionalFacade().configuracaoVigente(entity.getTipo());
            if (configuracaoHierarquiaOrganizacional == null) {
                throw new ExcecaoNegocioGenerica("Não foi encontrado configuração de hierarquia organizacional para o tipo " + entity.getTipo().getDescricao() + ".");
            }
            for (ReformaAdministrativaUnidade unidade : entity.getUnidades()) {
                validarCamposObrigatorio(entity, unidade);
                validarMascaraCodigo(entity, configuracaoHierarquiaOrganizacional, unidade);
                validarEstruturaRelacionamento(entity, unidade);
            }
        } catch (Exception e) {
            logger.error("Erro ao validar dados da reforma administrativa.", e);
        }
        return entity;
    }

    private static void limparErros(ReformaAdministrativa entity) {
        entity.setErros(Lists.newArrayList());
        entity.getUnidades().forEach(e -> e.setErros(""));
    }

    private void validarMascaraCodigo(ReformaAdministrativa entity, ConfiguracaoHierarquiaOrganizacional configuracaoHierarquiaOrganizacional, ReformaAdministrativaUnidade unidade) {
        if (StringUtils.isNotEmpty(unidade.getCodigoNovo())) {
            if (unidade.getCodigoNovo().length() != configuracaoHierarquiaOrganizacional.getMascara().length()) {
                adicionarErro(entity, unidade, "O código esta no formato inválido. Máscara: " + configuracaoHierarquiaOrganizacional.getMascara());
            }
        }
    }

    private void validarEstruturaRelacionamento(ReformaAdministrativa entity, ReformaAdministrativaUnidade unidade) {
        if (unidade.getTipo().isCriarNovo() || unidade.getTipo().isAlterarExistente()) {
            validarCodigoIgual(entity, unidade);
            validarCodigoNivelCodigoZerado(entity, unidade);
            validarCodigoPaiDiferente(entity, unidade);
            validaRegistrosVigentesRHVinculados(entity, unidade);
        }
        if (unidade.getTipo().isEncerrar()) {
            validaRegistrosVigentesRHVinculados(entity, unidade);
            String codigo = unidade.getCodigoNovo();

            for (ReformaAdministrativaUnidade filha : entity.getUnidades()) {
                if (filha.getCodigoNovo() != null && codigo != null) {
                    String codigoSuperior = filha.getCodigoSemZerosAoFinal();
                    if (codigoSuperior.startsWith(codigo) && filha.getFimVigencia() == null) {
                        adicionarErro(entity, unidade, "A Unidade Filha não possui fim de vigência. Código : <b>" + filha.getUnidade().getCodigo() + "</b>");
                    }
                }
            }
        }

    }

    private void validaRegistrosVigentesRHVinculados(ReformaAdministrativa entity, ReformaAdministrativaUnidade unidade) {
        if (unidade.getFimVigencia() != null && TipoHierarquiaOrganizacional.ADMINISTRATIVA.equals(entity.getTipo())) {
            boolean existeVinculoFPVigenteNaData = vinculoFPFacade.existeVinculoFPVigenteNaData(unidade.getFimVigencia(), unidade.getUnidade());
            boolean existePrestadorServicoVigenteNaData = prestadorServicosFacade.existePrestadorServicoVigenteNaData(unidade.getFimVigencia(), unidade.getUnidade());
            boolean existeLotacaoFuncionalVigenteNaData = lotacaoFuncionalFacade.existeLotacaoFuncionalVigenteNaData(unidade.getFimVigencia(), unidade.getUnidade());
            if (existeVinculoFPVigenteNaData || existePrestadorServicoVigenteNaData || existeLotacaoFuncionalVigenteNaData) {
                adicionarErro(entity, unidade, "Existem registros no módulo de RH vigentes vinculados à hierarquia organizacional administrativa. Esses registros deverão ser transferidos para permitir o encerramento. <br/>Código : <b>" + unidade.getUnidade().getCodigo() + "</b>");
            }
        }
    }


    private void validarCodigoPaiDiferente(ReformaAdministrativa entity, ReformaAdministrativaUnidade unidade) {
        Boolean encontrouCodigoPaiDiferente = false;
        if (unidade.getCodigoNovo() != null && unidade.getUnidadeSuperior() != null) {

            String codigoSuperior = unidade.getUnidadeSuperior().getCodigoNovo() != null ? unidade.getUnidadeSuperior().getCodigoSemZerosAoFinal() : unidade.getUnidadeSuperior().getUnidade().getCodigoSemZerosFinais();

            int i = codigoSuperior.length();
            while (i > 0) {
                char caracterSuperior = codigoSuperior.charAt(i - 1);
                char caracter = unidade.getCodigoNovo().charAt(i - 1);
                if (caracterSuperior != caracter) {
                    encontrouCodigoPaiDiferente = true;
                }
                i--;
            }
            if (encontrouCodigoPaiDiferente) {
                adicionarErro(entity, unidade, "Não é permitido que o código da unidade seja diferente do superior. Código: <b>" + unidade.getCodigoNovo() + "</b> e o código do superior : <b> " + unidade.getUnidadeSuperior().toString() + "</b>");
            }
        }
    }

    private void validarCodigoNivelCodigoZerado(ReformaAdministrativa entity, ReformaAdministrativaUnidade unidade) {
        Boolean encontrouNivelCodigoZerado = false;
        if (unidade.getCodigoNovo() != null) {

            String[] niveis = unidade.getCodigoSemZerosAoFinal().split("\\.");
            for (String nivel : niveis) {
                boolean tudoZero = true;
                int i = nivel.length();
                while (i > 0) {
                    char c = nivel.charAt(i - 1);
                    if (c != '.' && c != '0') {
                        tudoZero = false;
                    }
                    i--;
                }
                if (tudoZero) {
                    encontrouNivelCodigoZerado = true;
                }
            }
        }
        if (encontrouNivelCodigoZerado) {
            adicionarErro(entity, unidade, "Não é permitido que um nível tenha o código todo zerado. Código: <b>" + unidade.getCodigoNovo() + "</b>");
        }
    }

    private void validarCodigoIgual(ReformaAdministrativa entity, ReformaAdministrativaUnidade unidade) {
        Boolean encontrouMesmoCodigo = false;
        for (ReformaAdministrativaUnidade pai : entity.getUnidades()) {
            if (!StringUtils.isEmpty(unidade.getCodigoNovo()) && pai.getTipo().isNaoAlterar()) {
                if (pai.getUnidade().getCodigo().startsWith(unidade.getCodigoNovo()) && !unidade.equals(pai)) {
                    encontrouMesmoCodigo = true;
                }
            }
        }
        if (encontrouMesmoCodigo) {
            adicionarErro(entity, unidade, "Foi encontrado hierarquia com o mesmo código: <b>" + unidade.getCodigoNovo() + "</b>");
        }
    }

    private void adicionarErro(ReformaAdministrativa entity, ReformaAdministrativaUnidade unidade, String mensagem) {
        entity.getErros().add(mensagem);
        if (unidade.getErros() == null) {
            unidade.setErros("Erros: ");
        }
        unidade.setErros(unidade.getErros() + " <br></br> " + mensagem);
    }

    private void validarCamposObrigatorio(ReformaAdministrativa entity, ReformaAdministrativaUnidade unidade) {
        if (unidade.getTipo() == null) {
            adicionarErro(entity, unidade, "O Tipo é obrigatório.");
        }
        if (unidade.getUnidade() == null) {
            adicionarErro(entity, unidade, "A Unidade é obrigatório.");
        }
        if (unidade.getTipo().isCriarNovo() || unidade.getTipo().isAlterarExistente()) {
            if (StringUtils.isEmpty(unidade.getCodigoNovo())) {
                adicionarErro(entity, unidade, "O campo Código é obrigatório. Unidade: " + unidade.getUnidade().toString());
            }
            if (StringUtils.isEmpty(unidade.getDescricaoNovo())) {
                adicionarErro(entity, unidade, "O campo Descrição é obrigatório. Unidade: " + unidade.getUnidade().toString());
            }
            if (unidade.getInicioVigencia() == null) {
                adicionarErro(entity, unidade, "O Inicio de vigência é obrigatório. Unidade: " + unidade.getUnidade().toString());
            }
        }
        if (unidade.getTipo().isEncerrar()) {
            if (unidade.getFimVigencia() == null) {
                adicionarErro(entity, unidade, "O Fim de vigência é obrigatório.");
            }
        }
    }

    public void alterarUsuarios(ReformaAdministrativaUnidade unidade) {
        this.unidade = unidade;
        ordenarUsuarios(unidade.getUsuarios());
        FacesUtil.atualizarComponente("form-usuarios");
        FacesUtil.executaJavaScript("dlgUsuarios.show()");
    }

    public void confirmarAtualizacaoUsuarios() {
        try {
            Util.adicionarObjetoEmLista(selecionado.getUnidades(), unidade);
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.executaJavaScript("dlgUsuarios.hide()");
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public void atualizarUsuariosMarcados() {
        try {
            validarAtualizarUsuarios();
            unidade.getUsuarios().forEach(usuario -> {
                if (usuario.getMarcado()) {
                    usuario.setHierarquiaOrganizacional(hierarquiaOrganizacional);
                    usuario.setUnidadeDestino(hierarquiaOrganizacional.getSubordinada());
                }
            });
            ordenarUsuarios(unidade.getUsuarios());
            hierarquiaOrganizacional = null;
            marcarTodos = false;
            marcarDesmarcarTodos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAtualizarUsuarios() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Destino deve ser informado.");
        }
        if (unidade.getUsuarios().stream().noneMatch(ReformaAdministrativaUnidadeUsuario::getMarcado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado usuários marcados para serem atualizados.");
        }
        ve.lancarException();
    }

    private void ordenarUsuarios(List<ReformaAdministrativaUnidadeUsuario> usuarios) {
        usuarios.sort((o1, o2) -> {
            try {
                return o1.getUsuarioSistema().getNome().compareTo(o2.getUsuarioSistema().getNome());
            } catch (Exception e) {
                logger.error("Erro ao ordenar usuários.", e);
                return Integer.MAX_VALUE;
            }
        });
    }
}
