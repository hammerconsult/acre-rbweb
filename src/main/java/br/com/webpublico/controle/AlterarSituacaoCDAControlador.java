package br.com.webpublico.controle;

import br.com.webpublico.controle.forms.FormAlterarSituacaoCDAControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AlterarSituacaoCDAFacade;
import br.com.webpublico.negocios.CertidaoDividaAtivaFacade;
import br.com.webpublico.negocios.ComunicaSofPlanFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "alterarSituacaoCDAControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "alteracao-cda-novo",
        pattern = "/tributario/alteracao-situacao-cda/novo/",
        viewId = "/faces/tributario/dividaativa/alteracaosituacaocda/edita.xhtml"),

    @URLMapping(id = "alteracao-cda-edita",
        pattern = "/tributario/alteracao-situacao-cda/editar/#{alterarSituacaoCDAControlador.id}/",
        viewId = "/faces/tributario/dividaativa/alteracaosituacaocda/edita.xhtml"),

    @URLMapping(id = "alteracao-cda-ver",
        pattern = "/tributario/alteracao-situacao-cda/ver/#{alterarSituacaoCDAControlador.id}/",
        viewId = "/faces/tributario/dividaativa/alteracaosituacaocda/visualizar.xhtml"),

    @URLMapping(id = "alteracao-cda-listar",
        pattern = "/tributario/alteracao-situacao-cda/listar/",
        viewId = "/faces/tributario/dividaativa/alteracaosituacaocda/lista.xhtml")
})
public class AlterarSituacaoCDAControlador extends PrettyControlador<AlteracaoSituacaoCDA> implements Serializable, CRUD {

    private FormAlterarSituacaoCDAControlador form;
    private ConverterAutoComplete dividaConverter;
    private ConverterExercicio converterExercicio;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private AlterarSituacaoCDAFacade alterarSituacaoCDAFacade;
    private int maxResult, inicio;

    public AlterarSituacaoCDAControlador() {
        super(AlteracaoSituacaoCDA.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return alterarSituacaoCDAFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/alteracao-situacao-cda/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @URLAction(mappingId = "alteracao-cda-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        this.form = new FormAlterarSituacaoCDAControlador();
        inicio = 0;
        maxResult = 10;
    }

    @URLAction(mappingId = "alteracao-cda-edita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        operacao = Operacoes.EDITAR;
        selecionado = alterarSituacaoCDAFacade.recuperar(getId());
    }

    @URLAction(mappingId = "alteracao-cda-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        editar();
    }

    @URLAction(mappingId = "novoEnvioCertidaoDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEnvio() {
        novo();
    }

    public FormAlterarSituacaoCDAControlador getForm() {
        return form;
    }

    public void setForm(FormAlterarSituacaoCDAControlador form) {
        this.form = form;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(certidaoDividaAtivaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public Converter getDividaConverter() {
        if (dividaConverter == null) {
            dividaConverter = new ConverterAutoComplete(Divida.class, certidaoDividaAtivaFacade.getDividaFacade());
        }
        return dividaConverter;
    }

    public List<SelectItem> getTiposDividas() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        if (this.form.getTipoCadastro() != null) {
            lista.add(new SelectItem(null, ""));
            for (Divida divida : certidaoDividaAtivaFacade.getInscricaoDividaAtivaFacade().buscarDividasPorTipoCadastroTributario(this.form.getTipoCadastro())) {
                lista.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return lista;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (Exercicio e : certidaoDividaAtivaFacade.getExercicioFacade().getExerciciosAtualPassados()) {
            lista.add(new SelectItem(e, e.getAno().toString()));
        }
        return lista;
    }

    public void copiarCadastroInicialParaCadastroFinal() {
        this.form.setCadastroFinal(this.form.getCadastroInicial());
    }

    public void pesquisar() {
        try {
            validarTipoAlteracao();
            switch (this.form.getTipoAlteracaoCertidaoDA()) {
                case CANCELAR:
                case SUSPENDER:
                    form.setCertidoesDividaAtiva(certidaoDividaAtivaFacade.buscarCDAs(this.form.getTipoCadastro(), this.form.getCadastroInicial(), this.form.getCadastroFinal(), this.form.getExercicioInicial(), this.form.getExercicioFinal(), this.form.getNumeroCdaInicial(), this.form.getNumeroCdaFinal(), this.form.getDivida(), this.form.getPessoa(), Lists.newArrayList(SituacaoCertidaoDA.ABERTA), inicio, maxResult, false, false, null));
                    break;
                case AJUIZAR:
                    form.setCertidoesDividaAtiva(certidaoDividaAtivaFacade.buscarCDAs(this.form.getTipoCadastro(), this.form.getCadastroInicial(), this.form.getCadastroFinal(), this.form.getExercicioInicial(), this.form.getExercicioFinal(), this.form.getNumeroCdaInicial(), this.form.getNumeroCdaFinal(), this.form.getDivida(), this.form.getPessoa(), Lists.newArrayList(SituacaoCertidaoDA.ABERTA), inicio, maxResult, false, false, SituacaoJudicial.NAO_AJUIZADA));
                    break;
                case REATIVAR:
                    form.setCertidoesDividaAtiva(certidaoDividaAtivaFacade.buscarCDAs(this.form.getTipoCadastro(), this.form.getCadastroInicial(), this.form.getCadastroFinal(), this.form.getExercicioInicial(), this.form.getExercicioFinal(), this.form.getNumeroCdaInicial(), this.form.getNumeroCdaFinal(), this.form.getDivida(), this.form.getPessoa(), Lists.newArrayList(SituacaoCertidaoDA.CANCELADA, SituacaoCertidaoDA.SUSPENSA), inicio, maxResult, false, false, null));
                    break;
                case PENHORAR:
                    form.setCertidoesDividaAtiva(certidaoDividaAtivaFacade.buscarCDAs(this.form.getTipoCadastro(), this.form.getCadastroInicial(), this.form.getCadastroFinal(), this.form.getExercicioInicial(), this.form.getExercicioFinal(), this.form.getNumeroCdaInicial(), this.form.getNumeroCdaFinal(), this.form.getDivida(), this.form.getPessoa(), Lists.newArrayList(SituacaoCertidaoDA.ABERTA), inicio, maxResult, false, false, SituacaoJudicial.AJUIZADA));
                    break;
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarTipoAlteracao() {
        ValidacaoException ve = new ValidacaoException();
        if (this.form.getTipoAlteracaoCertidaoDA() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Alteração!");
        }
        ve.lancarException();
    }

    public String retornaInscricaoDoCadastro(Cadastro cadastro) {
        return certidaoDividaAtivaFacade.retornaInscricaoDoCadastro(cadastro);
    }

    public BigDecimal valorDacertidao(CertidaoDividaAtiva certidao) {
        return certidaoDividaAtivaFacade.valorDaCertidao(certidao);
    }

    public List<SelectItem> getTiposCadastroTributario() {
        return TipoCadastroTributario.asSelectItemList();
    }

    public List<SelectItem> getTiposAlteracao() {
        return TipoAlteracaoCertidaoDA.asSelectItemList();
    }

    public Date getDataAtual() {
        return getSistemaControlador().getDataOperacao();
    }

    public UsuarioSistema getUsuarioCorrente() {
        return getSistemaControlador().getUsuarioCorrente();
    }

    public void validaCertidoesSelecionadas() {
        if (form.getCertidoesSelecionadas() != null && form.getCertidoesSelecionadas().length > 0) {
            RequestContext.getCurrentInstance().execute("dialogAlteracaoSituacao.show()");
        } else {
            FacesUtil.addError("Atenção", "Nenhum Certidão foi selecionada!");
        }
    }

    private boolean validaAlteracao() {
        boolean validou = true;
        if ("".equals(form.getMotivo())) {
            FacesUtil.addError("Atenção", "Favor Informar o motivo!");
            validou = false;
        }
        return validou;
    }

    public void alterarCertidoes() {
        if (validaAlteracao()) {
            RequestContext.getCurrentInstance().execute("dialogAlteracaoSituacao.hide()");
            for (CertidaoDividaAtiva certidao : form.getCertidoesSelecionadas()) {
                try {
                    alterarSituacaoCDAFacade.alterarCDAComuicarProcuradoria(certidao, form, getDataAtual(), getUsuarioCorrente());
                    FacesUtil.addInfo("Operação Realizada!", "A Certidão " + certidao.getNumero() + "/" + certidao.getExercicio()
                        + " Foi alterada com sucesso");
                } catch (Exception e) {
                    FacesUtil.addError("Operação Não Realizada!", "A Certidão " + certidao.getNumero() + "/" + certidao.getExercicio()
                        + " Não pode ser Alterada, a comunicação com o servidor a procuradoria não pode ser estabelecida ("
                        + e.getMessage() + ")");
                }
            }
            form.limparDados();
        }
    }


    public void proximaPagina() {
        inicio += maxResult;
        pesquisar();
    }

    public void paginaAnterior() {
        inicio -= maxResult;
        pesquisar();
    }

    public Integer getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(Integer maxResult) {
        this.maxResult = maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public Integer getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }
}
