/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.LancamentoConciliacaoBancaria;
import br.com.webpublico.entidades.OperacaoConciliacao;
import br.com.webpublico.entidades.TipoConciliacao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoOperacaoConcilicaoBancaria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LancamentoConciliacaoBancariaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Renato
 */
@ManagedBean(name = "lancamentoConciliacaoBancariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-conciliacao-bancaria", pattern = "/conciliacao-bancaria/novo/", viewId = "/faces/financeiro/conciliacao/conciliacaobancaria/edita.xhtml"),
    @URLMapping(id = "editar-conciliacao-bancaria", pattern = "/conciliacao-bancaria/editar/#{lancamentoConciliacaoBancariaControlador.id}/", viewId = "/faces/financeiro/conciliacao/conciliacaobancaria/edita.xhtml"),
    @URLMapping(id = "ver-conciliacao-bancaria", pattern = "/conciliacao-bancaria/ver/#{lancamentoConciliacaoBancariaControlador.id}/", viewId = "/faces/financeiro/conciliacao/conciliacaobancaria/visualizar.xhtml"),
    @URLMapping(id = "listar-conciliacao-bancaria", pattern = "/conciliacao-bancaria/listar/", viewId = "/faces/financeiro/conciliacao/conciliacaobancaria/lista.xhtml")
})

public class LancamentoConciliacaoBancariaControlador extends PrettyControlador<LancamentoConciliacaoBancaria> implements Serializable, CRUD {

    @EJB
    private LancamentoConciliacaoBancariaFacade lancamentoConciliacaoBancariaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterTipoOperacao;
    private ContaBancariaEntidade contaBancariaEntidade;

    public LancamentoConciliacaoBancariaControlador() {
        super(LancamentoConciliacaoBancaria.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return lancamentoConciliacaoBancariaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/conciliacao-bancaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "ver-conciliacao-bancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-conciliacao-bancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }


    @Override
    @URLAction(mappingId = "novo-conciliacao-bancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setData(sistemaControlador.getDataOperacao());
        selecionado.setSubConta(null);
    }

    public void recuperarEditarVer() {
        selecionado = lancamentoConciliacaoBancariaFacade.recuperar(((LancamentoConciliacaoBancaria) selecionado).getId());
        setarContaBancaria();
    }

    public ConverterAutoComplete getConverterTipoOperacao() {
        if (converterTipoOperacao == null) {
            converterTipoOperacao = new ConverterAutoComplete(TipoConciliacao.class, lancamentoConciliacaoBancariaFacade.getTipoConciliacaoFacade());
        }
        return converterTipoOperacao;
    }

    public void setConverterTipoOperacao(ConverterAutoComplete converterTipoOperacao) {
        this.converterTipoOperacao = converterTipoOperacao;
    }

    public List<OperacaoConciliacao> completaOperacaoConciliacao(String parte) {
        return lancamentoConciliacaoBancariaFacade.getOperacaoConciliacaoFacade().completaOperacaoPorCodigoDescricao(parte.trim());
    }

    public List<TipoConciliacao> completaTipoConciliacao(String parte) {
        return lancamentoConciliacaoBancariaFacade.getTipoConciliacaoFacade().listaFiltrando(parte.trim(), "numero", "descricao");
    }

    private void validarDatasFase() {
        ValidacaoException ve = new ValidacaoException();
        if (lancamentoConciliacaoBancariaFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/conciliacao/conciliacaobancaria/edita.xhtml", selecionado.getData(), selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada( "A data de lançamento está fora do período fase!");
        }
        if (selecionado.getDataConciliacao() != null && lancamentoConciliacaoBancariaFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/conciliacao/conciliacaobancaria/edita.xhtml", selecionado.getDataConciliacao(), selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A data de conciliação está fora do período fase!");
        }
        ve.lancarException();
    }

    public LancamentoConciliacaoBancaria esteSelecionado() {
        return ((LancamentoConciliacaoBancaria) selecionado);
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarValor();
            validarDatasFase();
            if (operacao.equals(Operacoes.NOVO)) {
                lancamentoConciliacaoBancariaFacade.salvarNovo(selecionado);
            } else {
                lancamentoConciliacaoBancariaFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    @Override
    public void validarExclusaoEntidade() {
        ValidacaoException ve = new ValidacaoException();
        if (lancamentoConciliacaoBancariaFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/conciliacao/conciliacaobancaria/edita.xhtml", selecionado.getData(), selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A data de lançamento está fora do período fase!");
        }
        ve.lancarException();
    }

    private void validarValor() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo valor deve ser maior que zero (0).");
        }
        if (selecionado.getDataConciliacao() != null) {
            if (selecionado.getDataConciliacao().before(selecionado.getData())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" A data de conciliação deve ser superior a data de lançamento da pendência.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTipoOperacaoConcilicaoBancaria() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (TipoOperacaoConcilicaoBancaria tipo : TipoOperacaoConcilicaoBancaria.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public void setarContaBancaria() {
        try {
            contaBancariaEntidade = selecionado.getSubConta().getContaBancariaEntidade();
        } catch (Exception e) {

        }
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
