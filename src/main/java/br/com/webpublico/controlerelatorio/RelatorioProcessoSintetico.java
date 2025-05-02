/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Assunto;
import br.com.webpublico.entidades.Processo;
import br.com.webpublico.entidades.SubAssunto;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.SituacaoProcesso;
import br.com.webpublico.enums.TipoProcessoTramite;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioProtocoloFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
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
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author andregustavo
 */
@ManagedBean(name = "relatorioProcessoSintetico")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "limpaCamposRelatorioProcessoSintetico", pattern = "/protocolo/relatorio/processosintetico/", viewId = "/faces/tributario/cadastromunicipal/relatorio/relatorioprocessosintetico.xhtml")
})
public class RelatorioProcessoSintetico implements Serializable {

    @EJB
    private RelatorioProtocoloFacade relatorioProtocoloFacade;
    private Long numeroProcesso;
    private Integer ano;
    private SituacaoProcesso situacaoProcessoSelecionada;
    private TipoProcessoTramite tipoProcessoSelecionado;
    private UsuarioSistema usuarioSelecionado;
    private Integer numeroProcessoInicial;
    private Integer numeroProcessoFinal;
    private Date dataInicial;
    private Date dataFinal;
    private Processo processoSelecionado;
    private Assunto assuntoSelecionadoSub;
    private Assunto assuntoSelecionado;
    private SubAssunto subAssuntoSelecionado;
    private List<UsuarioSistema> listaUsuariosSistema;
    private List<TipoProcessoTramite> listaTipoProcessos;
    private List<Assunto> listaAssuntos;
    private List<SubAssunto> listaSubAssuntos;

    public RelatorioProcessoSintetico() {
        listaTipoProcessos = Lists.newArrayList();
        listaAssuntos = Lists.newArrayList();
        listaSubAssuntos = Lists.newArrayList();
    }

    public SituacaoProcesso getSituacaoProcessoSelecionada() {
        return situacaoProcessoSelecionada;
    }

    public void setSituacaoProcessoSelecionada(SituacaoProcesso situacaoProcessoSelecionada) {
        this.situacaoProcessoSelecionada = situacaoProcessoSelecionada;
    }

    public List<UsuarioSistema> getListaUsuariosSistema() {
        return listaUsuariosSistema;
    }

    public UsuarioSistema getUsuarioSelecionado() {
        return usuarioSelecionado;
    }

    public void setUsuarioSelecionado(UsuarioSistema usuarioSelecionado) {
        this.usuarioSelecionado = usuarioSelecionado;
    }

    public void setListaUsuariosSistema(List<UsuarioSistema> listaUsuariosSistema) {
        this.listaUsuariosSistema = listaUsuariosSistema;
    }

    public Assunto getAssuntoSelecionadoSub() {
        return assuntoSelecionadoSub;
    }

    public void setAssuntoSelecionadoSub(Assunto assuntoSelecionadoSub) {
        this.assuntoSelecionadoSub = assuntoSelecionadoSub;
    }

    public List<SubAssunto> getListaSubAssuntos() {
        return listaSubAssuntos;
    }

    public void setListaSubAssuntos(List<SubAssunto> listaSubAssuntos) {
        this.listaSubAssuntos = listaSubAssuntos;
    }

    public List<Assunto> getListaAssuntos() {
        return listaAssuntos;
    }

    public void setListaAssuntos(List<Assunto> listaAssuntos) {
        this.listaAssuntos = listaAssuntos;
    }

    public Assunto getAssuntoSelecionado() {
        return assuntoSelecionado;
    }

    public void setAssuntoSelecionado(Assunto assuntoSelecionado) {
        this.assuntoSelecionado = assuntoSelecionado;
    }

    public List<TipoProcessoTramite> getListaTipoProcessos() {
        return listaTipoProcessos;
    }

    public void setListaTipoProcessos(List<TipoProcessoTramite> listaTipoProcessos) {
        this.listaTipoProcessos = listaTipoProcessos;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getNumeroProcessoFinal() {
        return numeroProcessoFinal;
    }

    public void setNumeroProcessoFinal(Integer numeroProcessoFinal) {
        this.numeroProcessoFinal = numeroProcessoFinal;
    }

    public Integer getNumeroProcessoInicial() {
        return numeroProcessoInicial;
    }

    public void setNumeroProcessoInicial(Integer numeroProcessoInicial) {
        this.numeroProcessoInicial = numeroProcessoInicial;
    }

    public SubAssunto getSubAssuntoSelecionado() {
        return subAssuntoSelecionado;
    }

    public void setSubAssuntoSelecionado(SubAssunto subAssuntoSelecionado) {
        this.subAssuntoSelecionado = subAssuntoSelecionado;
    }

    public Processo getProcessoSelecionado() {
        return processoSelecionado;
    }

    public void setProcessoSelecionado(Processo processoSelecionado) {
        this.processoSelecionado = processoSelecionado;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public TipoProcessoTramite getTipoProcessoSelecionado() {
        return tipoProcessoSelecionado;
    }

    public void setTipoProcessoSelecionado(TipoProcessoTramite tipoProcessoSelecionado) {
        this.tipoProcessoSelecionado = tipoProcessoSelecionado;
    }

    public Long getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(Long numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public void novoAssunto() {
        if (assuntoSelecionado != null && assuntoSelecionado.getId() != null) {
            listaAssuntos.add(assuntoSelecionado);
            assuntoSelecionado = new Assunto();
        } else {
            FacesUtil.addError("Atenção", "Favor informa um assunto.");
        }
    }

    public void removeAssunto(Assunto ass) {
        if (ass != null) {
            getListaAssuntos().remove(ass);
        }
    }

    public void novoTipoProcesso() {
        if (tipoProcessoSelecionado != null) {
            listaTipoProcessos.add(tipoProcessoSelecionado);
            tipoProcessoSelecionado = null;
        } else {
            FacesUtil.addError("Atenção", "Favor informar um tipo de processo.");
        }
    }

    public void removeTipoProcesso(TipoProcessoTramite tipo) {
        if (tipo != null) {
            getListaTipoProcessos().remove(tipo);
        }
    }

    public void novoSubAssunto() {
        if (subAssuntoSelecionado != null && subAssuntoSelecionado.getId() != null) {
            listaSubAssuntos.add(subAssuntoSelecionado);
            assuntoSelecionadoSub = new Assunto();
            subAssuntoSelecionado = new SubAssunto();
        } else {
            FacesUtil.addError("Atenção", "Favor informar um Subassunto!");
        }
    }

    public void removeSubAssunto(SubAssunto sub) {
        if (sub != null) {
            getListaSubAssuntos().remove(sub);
        }
    }

    public List<SelectItem> getTipoProcessoTramites() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoProcessoTramite object : TipoProcessoTramite.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private String concactenarTiposProcessos(List<TipoProcessoTramite> tiposProcessos) {
        String resultado = "(";
        for (TipoProcessoTramite obj : tiposProcessos) {
            resultado += "'" + obj.name() + "'" + ",";
        }
        resultado = resultado.substring(0, resultado.length() - 1);
        resultado += ")";

        return resultado;
    }

    private String concactenarAssuntos(List<Assunto> assuntos) {
        String resultado = "(";
        for (Assunto obj : assuntos) {
            resultado += "'" + obj.getId() + "'" + ",";
        }
        resultado = resultado.substring(0, resultado.length() - 1);
        resultado += ")";
        return resultado;
    }

    private String concactenarSubAssuntos(List<SubAssunto> subAssuntos) {
        String resultado = "(";
        for (SubAssunto obj : subAssuntos) {
            resultado += "'" + obj.getId() + "'" + ",";
        }
        resultado = resultado.substring(0, resultado.length() - 1);
        resultado += ")";
        return resultado;
    }

    private String montarCondicao() {
        String condicao = "";
        String clausula = " where ";

        if (dataInicial != null && dataFinal != null) {
            condicao += clausula + " trunc(PROCESSO.DATAREGISTRO) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/mm/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/mm/yyyy')";
            clausula = " and ";
        }

        if (dataInicial != null && dataFinal == null) {
            condicao += clausula + " trunc(PROCESSO.DATAREGISTRO) >= to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/mm/yyyy')";
            clausula = " and ";
        }

        if (dataFinal != null) {
            condicao += clausula + " trunc(PROCESSO.DATAREGISTRO) <= to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/mm/yyyy')";
            clausula = " and ";
        }

        if (situacaoProcessoSelecionada != null) {
            condicao += clausula + " PROCESSO.SITUACAO = " + "'" + situacaoProcessoSelecionada.name() + "'";
            clausula = " and ";
        }
        if (usuarioSelecionado != null) {
            condicao += clausula + " PROCESSO.PESSOA_ID = " + usuarioSelecionado.getId();
            clausula = " and ";
        }

        if (numeroProcessoInicial != null && numeroProcessoFinal != null) {
            condicao += clausula + " PROCESSO.NUMERO >= " + numeroProcessoInicial + " and PROCESSO.NUMERO <= " + numeroProcessoFinal + " and PROCESSO.ANO = " + ano;
            clausula = " and ";
        }

        if (numeroProcessoInicial != null && numeroProcessoFinal == null) {
            condicao += clausula + " PROCESSO.NUMERO >= " + numeroProcessoInicial + " and PROCESSO.ANO = " + ano;
            clausula = " and ";
        }

        if (numeroProcessoFinal != null && numeroProcessoInicial == null) {
            condicao += clausula + " PROCESSO.NUMERO <= " + numeroProcessoFinal + " and PROCESSO.ANO = " + ano;
            clausula = " and ";
        }

        if (ano != null) {
            condicao += clausula + " PROCESSO.ANO = " + ano;
            clausula = " and ";
        }

        if (!listaTipoProcessos.isEmpty()) {
            condicao += clausula + " PROCESSO.TIPOPROCESSOTRAMITE IN " + concactenarTiposProcessos(listaTipoProcessos);
            clausula = " and ";
        }

        if (!listaAssuntos.isEmpty()) {
            condicao += clausula + " ASSUNTO.ID IN " + concactenarAssuntos(listaAssuntos);
            clausula = " and ";
        }

        if (!listaSubAssuntos.isEmpty()) {
            condicao += clausula + " SUBASSUNTO.ID IN " + concactenarSubAssuntos(listaSubAssuntos);
        }

        return condicao;
    }

    @URLAction(mappingId = "limpaCamposRelatorioProcessoSintetico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = null;
        dataFinal = null;
        ano = relatorioProtocoloFacade.getSistemaFacade().getExercicioCorrente().getAno();
        numeroProcessoFinal = null;
        numeroProcessoInicial = null;
        situacaoProcessoSelecionada = SituacaoProcesso.EMTRAMITE;
        processoSelecionado = new Processo();
        usuarioSelecionado = null;
        tipoProcessoSelecionado = null;
        assuntoSelecionado = new Assunto();
        assuntoSelecionadoSub = new Assunto();
        subAssuntoSelecionado = new SubAssunto();
        listaAssuntos = Lists.newArrayList();
        listaSubAssuntos = Lists.newArrayList();
        listaTipoProcessos = Lists.newArrayList();
    }

    public List<SelectItem> getListaAssuntosFiltrados() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (assuntoSelecionadoSub != null && assuntoSelecionadoSub.getId() != null) {
            for (SubAssunto obj : relatorioProtocoloFacade.getSubAssuntoFacade().listaSubAssuntoPorAssunto(assuntoSelecionadoSub)) {
                toReturn.add(new SelectItem(obj, obj.getDescricao()));
            }
        } else {
            toReturn = null;
        }
        return toReturn;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (numeroProcessoInicial != null && numeroProcessoFinal != null && numeroProcessoInicial > numeroProcessoFinal) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Numero do Processo Final deve ser menor ou igual ao Numero do Processo Inicial.");
        }
        if (dataInicial != null && dataFinal != null && dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser menor ou igual a Data Final.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", relatorioProtocoloFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            dto.adicionarParametro("MODULO", "Protocolo");
            dto.adicionarParametro("condicao", montarCondicao());
            dto.setNomeRelatorio("Relatório Sintético de Processos");
            dto.setApi("administrativo/processo-sintetico/");
            ReportService.getInstance().gerarRelatorio(relatorioProtocoloFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
}
