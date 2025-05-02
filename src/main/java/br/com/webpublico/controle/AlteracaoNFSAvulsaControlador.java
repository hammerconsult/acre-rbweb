/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AlteracaoNFSAvulsaFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */


@ManagedBean(name = "alteracaoNFSAvulsaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAlteracaoNFAvulsa",
        pattern = "/tributario/alteracao-notafiscal-avulsa/novo/",
        viewId = "/faces/tributario/notafiscalavulsa/alteracao/edita.xhtml"),

    @URLMapping(id = "listaAlteracaoNFAvulsa",
        pattern = "/tributario/alteracao-notafiscal-avulsa/listar/",
        viewId = "/faces/tributario/notafiscalavulsa/alteracao/lista.xhtml"),

    @URLMapping(id = "editaAlteracaoNFAvulsa",
        pattern = "/tributario/alteracao-notafiscal-avulsa/editar/#{alteracaoNFSAvulsaControlador.id}/",
        viewId = "/faces/tributario/notafiscalavulsa/alteracao/edita.xhtml"),

    @URLMapping(id = "verAlteracaoNFAvulsa",
        pattern = "/tributario/alteracao-notafiscal-avulsa/ver/#{alteracaoNFSAvulsaControlador.id}/",
        viewId = "/faces/tributario/notafiscalavulsa/alteracao/visualiza.xhtml")
})
public class AlteracaoNFSAvulsaControlador extends PrettyControlador<AlteracaoNFSAvulsa> implements CRUD, Serializable {

    @EJB
    private AlteracaoNFSAvulsaFacade alteracaoNFSAvulsaFacade;
    private ConfiguracaoTributario configuracaoTributario;
    private ConverterAutoComplete converterNFSAvulsa;
    private ConverterExercicio converterExecicio;
    private NFSAvulsa notaFiscalAlterada;

    public AlteracaoNFSAvulsaControlador() {
        super(AlteracaoNFSAvulsa.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/alteracao-notafiscal-avulsa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return alteracaoNFSAvulsaFacade;
    }

    @URLAction(mappingId = "novoAlteracaoNFAvulsa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        configuracaoTributario = alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().getConfiguracaoTributarioFacade().retornaUltimo();
        selecionado = (AlteracaoNFSAvulsa) Web.pegaDaSessao(AlteracaoNFSAvulsa.class);
        if (selecionado == null) {
            selecionado = new AlteracaoNFSAvulsa();
        } else {
            recuperaItensDaNota();
        }
        getSelecionado().setDataAlteracao(new Date());

    }

    @Override
    @URLAction(mappingId = "verAlteracaoNFAvulsa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        configuracaoTributario = alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().getConfiguracaoTributarioFacade().retornaUltimo();
        operacao = Operacoes.VER;
        selecionado = alteracaoNFSAvulsaFacade.recuperar(getId());
        selecionado.setNFSAvulsa(alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().recuperar(selecionado.getNFSAvulsa().getId()));
        notaFiscalAlterada = alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().recuperar(selecionado.getNFSAvulsa().getNFSAvulsa().getId());
    }

    @Override
    @URLAction(mappingId = "editaAlteracaoNFAvulsa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public NFSAvulsa getNotaFiscalAlterada() {
        return notaFiscalAlterada;
    }

    public void setNotaFiscalAlterada(NFSAvulsa notaFiscalAlterada) {
        this.notaFiscalAlterada = notaFiscalAlterada;
    }


    public ConverterAutoComplete getConverterNFSAvulsa() {
        if (converterNFSAvulsa == null) {
            converterNFSAvulsa = new ConverterAutoComplete(NFSAvulsa.class, alteracaoNFSAvulsaFacade.getNfsAvulsaFacade());
        }
        return converterNFSAvulsa;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExecicio == null) {
            converterExecicio = new ConverterExercicio(alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().getExercicioFacade());
        }
        return converterExecicio;
    }

    public List<NFSAvulsa> completaNFSAvulsa(String parte) {
        return this.alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().completaNFSAvulsaQueNaoEstejaCancelada(parte.trim());
    }

    public void setSelecionado(AlteracaoNFSAvulsa selecionado) {
        this.selecionado = selecionado;
    }


    public List<Exercicio> completaExercicio(String parte) {
        return alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }


    public void recuperaItensDaNota() {
        selecionado.setNFSAvulsa(alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().recuperar(selecionado.getNFSAvulsa().getId()));

        NFSAvulsa novaNota = (NFSAvulsa) Util.clonarObjeto(selecionado.getNFSAvulsa());
        novaNota.setId(null);
        novaNota.setItens(new ArrayList<NFSAvulsaItem>());
        novaNota.setEmissao(selecionado.getNFSAvulsa().getEmissao());
        novaNota.setUsuario(((SistemaControlador) Util.getControladorPeloNome("sistemaControlador")).getUsuarioCorrente());

        for (NFSAvulsaItem nFSAvulsaItem : selecionado.getNFSAvulsa().getItens()) {
            NFSAvulsaItem item = (NFSAvulsaItem) Util.clonarObjeto(nFSAvulsaItem);
            item.setId(null);
            item.setNFSAvulsa(novaNota);
            novaNota.getItens().add(item);
        }

        novaNota.setInicioVigencia(new Date());
        selecionado.getNFSAvulsa().setFimVigencia(new Date());
        notaFiscalAlterada = novaNota;

    }

    public void salvar() {
        try {
            validarCampos();
            selecionado.getNFSAvulsa().setNFSAvulsa(notaFiscalAlterada);
            selecionado.getNFSAvulsa().setSituacao(NFSAvulsa.Situacao.ALTERADA);
            selecionado = alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().salvarAlteracaoNFS(selecionado);
            notaFiscalAlterada = selecionado.getNFSAvulsa().getNFSAvulsa();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com sucesso!", ""));
            redirecionaParaVisualiza();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void redirecionaParaVisualiza() {
        FacesUtil.redirecionamentoInterno("/tributario/alteracao-notafiscal-avulsa/ver/" + selecionado.getId() + "/");
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (notaFiscalAlterada != null) {
            if (notaFiscalAlterada.getPrestador() == null && notaFiscalAlterada.getCmcPrestador() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o prestador.");
            }
            if (notaFiscalAlterada.getTomador() == null && notaFiscalAlterada.getCmcTomador() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o tomador.");
            }

            if (notaFiscalAlterada.getPrestador() != null && notaFiscalAlterada.getTomador() != null) {
                if (notaFiscalAlterada.getPrestador().equals(notaFiscalAlterada.getTomador())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O tomador deve ser diferente do prestador.");
                }
            }
            if (notaFiscalAlterada != null && (notaFiscalAlterada.getCmcTomador() != null && notaFiscalAlterada.getCmcPrestador() != null)) {
                if (notaFiscalAlterada.getCmcPrestador().equals(notaFiscalAlterada.getCmcTomador())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O tomador deve ser diferente do prestador.");
                }
            }
            for (NFSAvulsaItem item : notaFiscalAlterada.getItens()) {
                if (item.getDescricao() == null || item.getDescricao().trim().length() <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O item com quantidade "
                        + item.getQuantidade()
                        + ", unidade "
                        + item.getUnidade()
                        + " e com o valor de "
                        + new DecimalFormat("#,##0.00").format(item.getValorTotal())
                        + " está sem descrição.");
                }
            }
        }

        if (selecionado.getNFSAvulsa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Nota Fiscal Avulsa ");
        }
        if (selecionado.getNFSAvulsa() != null && selecionado.getNFSAvulsa().getDataNota() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data da Nota Fiscal.");
        } else {
            if (selecionado.getNFSAvulsa() != null && selecionado.getNFSAvulsa().getEmissao() != null) {
                if (DataUtil.dataSemHorario(selecionado.getNFSAvulsa().getDataNota())
                    .compareTo(DataUtil.dataSemHorario(selecionado.getNFSAvulsa().getEmissao())) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Data da Nota Fiscal não pode ser superior a data de Emissão. Data de Emissão..: "
                        + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(selecionado.getNFSAvulsa().getEmissao()));
                }
            }
        }
        if (selecionado.getMotivo() == null || selecionado.getMotivo().trim().length() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo motivo é obrigatório.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public String getEnderecoPrestadorNotaAntiga() {
        if (selecionado.getNFSAvulsa().getPrestador() != null) {
            return getEndereco(selecionado.getNFSAvulsa().getPrestador());
        }
        if (selecionado.getNFSAvulsa().getCmcPrestador() != null) {
            EnderecoCorreio end = alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().getCadastroEconomicoFacade().recuperarEnderecoPessoaCMC(selecionado.getNFSAvulsa().getCmcPrestador().getId());
            return end != null ? (end.getTipoLogradouro() != null ? end.getTipoLogradouro().getDescricao() + " " : "") + end.getLogradouro() + " " + end.getBairro() + " Nº " + end.getNumero() : "";
        }
        return "";
    }

    public String getEnderecoTomadorNotaAntiga() {
        if (selecionado.getNFSAvulsa().getTomador() != null) {
            return getEndereco(selecionado.getNFSAvulsa().getTomador());
        }
        if (selecionado.getNFSAvulsa().getCmcTomador() != null) {
            EnderecoCorreio end = alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().getCadastroEconomicoFacade().recuperarEnderecoPessoaCMC(selecionado.getNFSAvulsa().getCmcTomador().getId());
            return end != null ? (end.getTipoLogradouro() != null ? end.getTipoLogradouro().getDescricao() + " " : "") + end.getLogradouro() + " " + end.getBairro() + " Nº " + end.getNumero() : "";
        }
        return "";
    }

    public String getEnderecoPrestadorNotaAlterada() {
        if (notaFiscalAlterada != null && notaFiscalAlterada.getPrestador() != null) {
            return getEndereco(notaFiscalAlterada.getPrestador());
        }

        if (notaFiscalAlterada != null && notaFiscalAlterada.getCmcPrestador() != null) {
            EnderecoCorreio end = alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().getCadastroEconomicoFacade().recuperarEnderecoPessoaCMC(notaFiscalAlterada.getCmcPrestador().getId());
            return end != null ? (end.getTipoLogradouro() != null ? end.getTipoLogradouro().getDescricao() + " " : "") + end.getLogradouro() + " " + end.getBairro() + " Nº " + end.getNumero() : "";
        }
        return "";
    }

    public String getEnderecoTomadorNotaAlterada() {
        if (notaFiscalAlterada != null && notaFiscalAlterada.getTomador() != null) {
            return getEndereco(notaFiscalAlterada.getTomador());
        }

        if (notaFiscalAlterada != null && notaFiscalAlterada.getCmcTomador() != null) {
            EnderecoCorreio end = alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().getCadastroEconomicoFacade().recuperarEnderecoPessoaCMC(notaFiscalAlterada.getCmcTomador().getId());
            return end != null ? (end.getTipoLogradouro() != null ? end.getTipoLogradouro().getDescricao() + " " : "") + end.getLogradouro() + " " + end.getBairro() + " Nº " + end.getNumero() : "";
        }
        return "";
    }


    private String getEndereco(Pessoa p) {
        List<EnderecoCorreio> enderecos = alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().getListaDeEnderecos(p);
        EnderecoCorreio enderecoCorreio = null;
        for (EnderecoCorreio end : enderecos) {
            if (end.getPrincipal()) {
                enderecoCorreio = end;
            }
            if (enderecoCorreio == null) {
                if (TipoEndereco.DOMICILIO_FISCAL.equals(end.getTipoEndereco())) {
                    enderecoCorreio = end;
                }
            }

            if (enderecoCorreio == null) {
                if (TipoEndereco.CORRESPONDENCIA.equals(end.getTipoEndereco())) {
                    return end.toString();
                }
            }
        }
        if (enderecoCorreio == null && enderecos.size() > 0) {
            enderecoCorreio = enderecos.get(0);
        }
        return enderecoCorreio != null ? enderecoCorreio.toString() : "";
    }

    public BigDecimal getTotalIssNotaAntiga() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : selecionado.getNFSAvulsa().getItens()) {
                total = total.add(it.getValorIss());
            }
        }
        return total;
    }

    public BigDecimal getTotalParcialNotaAntiga() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : selecionado.getNFSAvulsa().getItens()) {
                total = total.add(it.getValorTotal());
            }
        }
        return total;
    }

    public BigDecimal getTotalUnitarioNotaAntiga() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : selecionado.getNFSAvulsa().getItens()) {
                total = total.add(it.getValorUnitario());
            }
        }
        return total;
    }

    public BigDecimal getTotalIssNotaAlterada() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : notaFiscalAlterada.getItens()) {
                total = total.add(it.getValorIss());
            }
        }
        return total;
    }

    public BigDecimal getTotalParcialNotaAlterada() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : notaFiscalAlterada.getItens()) {
                total = total.add(it.getValorTotal());
            }
        }
        return total;
    }

    public BigDecimal getTotalUnitarioNotaAlterada() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (NFSAvulsaItem it : notaFiscalAlterada.getItens()) {
                total = total.add(it.getValorUnitario());
            }
        }
        return total;
    }

    private boolean isPrestadorComCMCNotaAntiga() {
        if (selecionado == null) {
            return false;
        }
        return selecionado.getNFSAvulsa().getCmcPrestador() != null;
    }

    private boolean isPrestadorComCMCNotaAlterada() {
        if (selecionado == null) {
            return false;
        }
        return notaFiscalAlterada.getCmcPrestador() != null;
    }

    public boolean liberaPlacaNotaAntiga() {
        return (isPrestadorComCMCNotaAntiga()
            && selecionado.getNFSAvulsa().getCmcPrestador().getTipoAutonomo() != null
            && selecionado.getNFSAvulsa().getCmcPrestador().getTipoAutonomo().getNecessitaPlacas() != null
            && selecionado.getNFSAvulsa().getCmcPrestador().getTipoAutonomo().getNecessitaPlacas());
    }

    public boolean liberaPlacaNotaAlterada() {
        return (isPrestadorComCMCNotaAntiga()
            && notaFiscalAlterada.getCmcPrestador().getTipoAutonomo() != null
            && notaFiscalAlterada.getCmcPrestador().getTipoAutonomo().getNecessitaPlacas() != null
            && notaFiscalAlterada.getCmcPrestador().getTipoAutonomo().getNecessitaPlacas());
    }

    public void emitirNota() throws JRException, IOException {
        alteracaoNFSAvulsaFacade.getNfsAvulsaFacade().emitirNota(notaFiscalAlterada);
    }

    public boolean liberaImpressao() {
        if (notaFiscalAlterada != null && notaFiscalAlterada.getId() != null) {
            if ((notaFiscalAlterada.getSituacao() != null
                && notaFiscalAlterada.getSituacao().equals(NFSAvulsa.Situacao.CONCLUIDA))
                || (configuracaoTributario != null
                && configuracaoTributario.getEmiteSemPagamento() != null
                && configuracaoTributario.getEmiteSemPagamento()
                && !notaFiscalAlterada.getSituacao().equals(NFSAvulsa.Situacao.ABERTA)
                && !notaFiscalAlterada.getSituacao().equals(NFSAvulsa.Situacao.CANCELADA))) {
                return true;
            }
        }
        return false;
    }

    public void atualizaCamposContruibuinteGeral() {
        FacesUtil.executaJavaScript("Formulario");
//        FacesUtil.executaJavaScript(":Formulario:panelTomadorNotaAlterada:cmcTomador");
//        FacesUtil.executaJavaScript(":Formulario:panelTomadorNotaAlterada:enderecoTomador");
    }

    public String caminhoAtual() {
        if (selecionado.getId() != null) {
            return getCaminhoPadrao() + "editar/" + getUrlKeyValue() + "/";
        }
        return getCaminhoPadrao() + "novo/";
    }

    public void limparTomador() {
        notaFiscalAlterada.setCmcTomador(null);
        notaFiscalAlterada.setTomador(null);

    }
}
