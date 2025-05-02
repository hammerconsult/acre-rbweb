/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relacao-bases-recolhimento-encargos-sociais", pattern = "/relatorio/relacao-bases-recolhimento-encargos-sociais/", viewId = "/faces/rh/relatorios/relatoriorecolhimentoencargossociais.xhtml")
})
public class RelatorioRecolhimentoEncargosSociaisControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    private Entidade entidade;
    private Mes mes;
    private Integer ano;
    private Boolean fgts;
    private Boolean inss;
    private Boolean irrf;
    private Boolean salarioMaternidade;
    private Boolean salarioFamilia;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private Integer versao;
    private Boolean subFolha;
    private Boolean relDetalahdo;
    private Boolean todasUnidades;
    private List<GrupoRecursoFP> listaGruposRecursosFP;
    private GrupoRecursoFP[] gruposSelecionados;

    public RelatorioRecolhimentoEncargosSociaisControlador() {
        this.subFolha = Boolean.TRUE;
        this.relDetalahdo = Boolean.FALSE;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("MES", mes.getNumeroMesIniciandoEmZero());
            dto.adicionarParametro("ANO", ano);
            dto.adicionarParametro("FGTS", fgts);
            dto.adicionarParametro("INSS", inss);
            dto.adicionarParametro("IRRF", irrf);
            dto.adicionarParametro("SALARIOFAMILIA", salarioFamilia);
            dto.adicionarParametro("SALARIOMATERNIDADEA", salarioMaternidade);
            dto.adicionarParametro("CODIGOORGAO", getMontarGrupo());
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
            dto.adicionarParametro("TIPOFOLHA", tipoFolhaDePagamento.name());
            dto.adicionarParametro("tipoFolhaDTO", tipoFolhaDePagamento.getToDto());
            dto.adicionarParametro("COMPLEMENTOQUERY", getComplementoQuery());
            dto.adicionarParametro("DETALHADO", relDetalahdo);
            dto.adicionarParametro("VERSAO", montarCampoVersao());
            dto.adicionarParametro("VERSAODESCRICAO", montarDescricaoVersao());
            if (subFolha && !todasUnidades) {
                hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacional(getEntidade());
                dto.adicionarParametro("ORGAO", hierarquiaOrganizacionalSelecionada.toString());
                dto.adicionarParametro("CODIGOHO", " and ho.codigo like '" + hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%'");
            }
            if (subFolha && entidade == null) {
                dto.adicionarParametro("CODIGOHO", "");
            }
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("rh/recolhimento-encargos-sociais/");
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

    private String getNomeRelatorio() {
        return "RELAÇÃO DE BASES PARA RECOLHIMENTO DOS ENCARGOS SOCIAIS";
    }

    private String montarDescricaoVersao() {
        return versao != null ? " Versão " + versao : " ";
    }

    private String getMontarGrupo() {
        String retorno = " and grupo.nome in ( ";
        for (GrupoRecursoFP grupo : gruposSelecionados) {
            retorno += "'" + grupo.getNome() + "',";
        }
        retorno = retorno.substring(0, retorno.length() - 1);
        retorno += ") ";
        return retorno;
    }

    public String getComplementoQuery() {
        if (!fgts && !inss && !irrf && !salarioFamilia && !salarioMaternidade) {
            return " ";
        }
        String retorno = " and ( ";
        if (fgts) {
            retorno += " ficha.fgts <> 0 or ficha.basefgts <> 0 or ";
        }
        if (inss) {
            retorno += " ficha.inss <> 0  or ficha.baseinss <> 0 or ";
        }
        if (irrf) {
            retorno += " ficha.irrf <> 0  or ficha.baseirrf <> 0 or ";
        }
        if (salarioFamilia) {
            retorno += " ficha.salariofamilia <> 0  or ficha.basesalariofamilia <> 0 or ";
        }
        if (salarioMaternidade) {
            retorno += " ficha.salariomaternidade <> 0  or ficha.basesalariomaternidade <> 0 or ";
        }
        retorno = retorno.substring(0, retorno.length() - 3);
        retorno += " ) ";
        return retorno;

    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (ano == null || ano == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha deve ser informado.");
        }
        if (entidade == null && !todasUnidades) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Estabelecimento deve ser informado.");
        }
        if (gruposSelecionados.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione O(s) Grupo(s) de Recurso Folha de Pagamento.");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "relacao-bases-recolhimento-encargos-sociais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        mes = null;
        ano = null;
        fgts = Boolean.FALSE;
        inss = Boolean.FALSE;
        irrf = Boolean.FALSE;
        salarioFamilia = Boolean.FALSE;
        salarioMaternidade = Boolean.FALSE;
        listaGruposRecursosFP = new ArrayList<>();
        gruposSelecionados = new GrupoRecursoFP[]{};
        subFolha = Boolean.TRUE;
        relDetalahdo = Boolean.FALSE;
        todasUnidades = Boolean.FALSE;
        tipoFolhaDePagamento = null;
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public Boolean getFgts() {
        return fgts;
    }

    public void setFgts(Boolean fgts) {
        this.fgts = fgts;
    }

    public Boolean getInss() {
        return inss;
    }

    public void setInss(Boolean inss) {
        this.inss = inss;
    }

    public Boolean getIrrf() {
        return irrf;
    }

    public void setIrrf(Boolean irrf) {
        this.irrf = irrf;
    }

    public Boolean getSalarioMaternidade() {
        return salarioMaternidade;
    }

    public void setSalarioMaternidade(Boolean salarioMaternidade) {
        this.salarioMaternidade = salarioMaternidade;
    }

    public Boolean getSalarioFamilia() {
        return salarioFamilia;
    }

    public void setSalarioFamilia(Boolean salarioFamilia) {
        this.salarioFamilia = salarioFamilia;
    }

    public List<GrupoRecursoFP> getListaGruposRecursosFP() {
        return listaGruposRecursosFP;
    }

    public void setListaGruposRecursosFP(List<GrupoRecursoFP> listaGruposRecursosFP) {
        this.listaGruposRecursosFP = listaGruposRecursosFP;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public GrupoRecursoFP[] getGruposSelecionados() {
        return gruposSelecionados;
    }

    public void setGruposSelecionados(GrupoRecursoFP[] gruposSelecionados) {
        this.gruposSelecionados = gruposSelecionados;
    }

    public Boolean getSubFolha() {
        return subFolha;
    }

    public void setSubFolha(Boolean subFolha) {
        this.subFolha = subFolha;
    }

    public Boolean getRelDetalahdo() {
        return relDetalahdo;
    }

    public void setRelDetalahdo(Boolean relDetalahdo) {
        this.relDetalahdo = relDetalahdo;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Boolean getTodasUnidades() {
        return todasUnidades;
    }

    public void setTodasUnidades(Boolean todasUnidades) {
        this.todasUnidades = todasUnidades;
    }

    public void carregaListaRecursos() {
        if (!todasUnidades) {
            if (hierarquiaOrganizacionalSelecionada != null) {
                listaGruposRecursosFP = grupoRecursoFPFacade.buscarGruposRecursoFPPorOrgaoAndDataOperacao(hierarquiaOrganizacionalSelecionada);
            }
        } else {
            entidade = null;
            listaGruposRecursosFP = grupoRecursoFPFacade.lista();
        }
    }


    private String montarCampoVersao() {
        if (versao != null) {
            return " and folha.versao = " + versao;
        }
        return " ";
    }

    public List<SelectItem> getVersoes() {
        if (mes != null && getAno() != null && getTipoFolhaDePagamento() != null) {
            return Util.getListSelectItem(folhaDePagamentoFacade.recuperarVersao(mes, getAno(), getTipoFolhaDePagamento()), false);
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> getEntidades() {
        return Util.getListSelectItem(entidadeFacade.buscarEntidadesDeclarantesVigentePorCategoria(CategoriaDeclaracaoPrestacaoContas.SEFIP), false);
    }

    public void carregarGrupoRecursosFPPorEntidade() {
        if (getEntidade() != null && mes != null && ano != null) {
            List<GrupoRecursoFP> grupoRecursoFPs = Lists.newLinkedList();
            for (HierarquiaOrganizacional hierarquiaOrganizacional : buscarHierarquiasDaEntidade()) {
                for (GrupoRecursoFP grupoRecursoFP : grupoRecursoFPFacade.buscarGruposRecursoFPPorOrgaoAndDataOperacao(hierarquiaOrganizacional)) {
                    if (!grupoRecursoFPs.contains(grupoRecursoFP)) {
                        grupoRecursoFPs.add(grupoRecursoFP);
                    }
                }
            }
            setListaGruposRecursosFP(grupoRecursoFPs);
        }
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasDaEntidade() {
        List<HierarquiaOrganizacional> hierarquias = entidadeFacade.buscarHierarquiasDaEntidade(entidade, CategoriaDeclaracaoPrestacaoContas.SEFIP, DataUtil.primeiroDiaMes(DataUtil.criarDataComMesEAno(mes.getNumeroMes(), ano).toDate()).getTime(), DataUtil.ultimoDiaMes(DataUtil.criarDataComMesEAno(mes.getNumeroMes(), ano).toDate()).getTime());
        Collections.sort(hierarquias);
        return hierarquias;
    }
}
