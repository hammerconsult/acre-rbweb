package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.FormaCalculoMultaFiscalizacao;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.LancamentoMultaAcessoriaFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex
 * @since 30/03/2017 14:59
 */
@ManagedBean(name = "lancamentoMultaAcessoriaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoMultaAcessoria", pattern = "/lancamento-multa-acessoria/novo/", viewId = "/faces/tributario/multaacessoria/edita.xhtml"),
    @URLMapping(id = "listarMultaAcessoria", pattern = "/lancamento-multa-acessoria/listar/", viewId = "/faces/tributario/multaacessoria/lista.xhtml"),
    @URLMapping(id = "verMultaAcessoria", pattern = "/lancamento-multa-acessoria/ver/#{lancamentoMultaAcessoriaControlador.id}/", viewId = "/faces/tributario/multaacessoria/visualizar.xhtml"),
    @URLMapping(id = "editarMultaAcessoria", pattern = "/lancamento-multa-acessoria/editar/#{lancamentoMultaAcessoriaControlador.id}/", viewId = "/faces/tributario/multaacessoria/edita.xhtml")
})
public class LancamentoMultaAcessoriaControlador extends PrettyControlador<LancamentoMultaAcessoria> implements Serializable, CRUD {

    @EJB
    private LancamentoMultaAcessoriaFacade lacamentomultaAcessoriaFacade;
    private TipoCadastroTributario tipoCadastroTributario;
    private ItemLancamentoMultaAcessoria itemLancamentoSelecionado;
    private List<ResultadoParcela> parcelas;

    @Override
    public String getCaminhoPadrao() {
        return "/lancamento-multa-acessoria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return lacamentomultaAcessoriaFacade;
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public LancamentoMultaAcessoriaControlador() {
        super(LancamentoMultaAcessoria.class);
    }

    @Override
    @URLAction(mappingId = "novoMultaAcessoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        cancelarLancamento();
        selecionado.setDataLancamento(lacamentomultaAcessoriaFacade.getSistemaFacade().getDataOperacao());
    }

    @Override
    @URLAction(mappingId = "verMultaAcessoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        atribuirTipoCadastroTributario();
        if (selecionado.getProcessoMultaAcessoria() != null) {
            parcelas = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getProcessoMultaAcessoria().getCalculos().get(0).getId()).executaConsulta().getResultados();
        }
    }

    @URLAction(mappingId = "editarMultaAcessoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        cancelarLancamento();
        atribuirTipoCadastroTributario();
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            if (tipo.equals(TipoCadastroTributario.ECONOMICO) || tipo.equals(TipoCadastroTributario.PESSOA))
                toReturn.add(new SelectItem(tipo, tipo.getDescricaoLonga()));
        }
        return toReturn;
    }

    public void adicionarLancamento() {
        try {
            validarLancamento();
            itemLancamentoSelecionado.setLancamentoMultaAcessoria(selecionado);
            if (selecionado.getValorTotal() == null) {
                selecionado.setValorTotal(BigDecimal.ZERO);
            }
            selecionado.setValorTotal(selecionado.getValorTotal().add(itemLancamentoSelecionado.getValorMulta()));
            Util.adicionarObjetoEmLista(selecionado.getItemLancamentoMultaAcessoria(), itemLancamentoSelecionado);
            cancelarLancamento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelarLancamento() {
        itemLancamentoSelecionado = new ItemLancamentoMultaAcessoria();
    }

    private void validarLancamento() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoCadastroTributario == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Cadastro é obrigatório.");
        } else if (TipoCadastroTributario.ECONOMICO.equals(tipoCadastroTributario) && selecionado.getCadastroEconomico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro Econômico é obrigatório.");
        } else if (TipoCadastroTributario.PESSOA.equals(tipoCadastroTributario) && selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Gerar Por Contriuinte é obrigatório.");
        }

        if (itemLancamentoSelecionado.getMultaFiscalizacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Multa é obrigatório.");
        } else {
            if (FormaCalculoMultaFiscalizacao.VALOR.equals(itemLancamentoSelecionado.getMultaFiscalizacao().getFormaCalculoMultaFiscalizacao()) &&
                (itemLancamentoSelecionado.getValorMulta() == null ||
                itemLancamentoSelecionado.getValorMulta().compareTo(BigDecimal.ZERO) <= 0)) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Valor da Multa é obrigatório e deve ser maior que zero(0).");
            } else if (FormaCalculoMultaFiscalizacao.QUANTIDADE.equals(itemLancamentoSelecionado.getMultaFiscalizacao().getFormaCalculoMultaFiscalizacao()) &&
                (itemLancamentoSelecionado.getQuantidadeUFMRB() == null || itemLancamentoSelecionado.getQuantidadeUFMRB() <= 0)) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Quantidade UFMBR é obrigatório e deve ser maior que zero(0).");
            }
        }
        ve.lancarException();
        for (ItemLancamentoMultaAcessoria itemLancamento : selecionado.getItemLancamentoMultaAcessorias()) {
            if (!itemLancamento.equals(itemLancamentoSelecionado) && itemLancamento.getMultaFiscalizacao().equals(itemLancamentoSelecionado.getMultaFiscalizacao())) {
                ve.adicionarMensagemDeCampoObrigatorio("A multa selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    private void validarListaLancamento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getItemLancamentoMultaAcessoria().isEmpty() || selecionado.getItemLancamentoMultaAcessoria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Não há multas lançadas, é obrigatório que tenha pelo menos uma.");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarListaLancamento();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Não foi possível salvar o lancamento de multa acessoria: " + ex);
        }
    }

    @Override
    public void redireciona() {
        if (!isOperacaoVer() && selecionado.getId() != null) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } else {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
        }

    }

    @Override
    public void cancelar() {
        Web.getEsperaRetorno();
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    public void gerarDebitosMultaAcessoria() {
        try {
            realizarProcessoCalculoMultaAcessoria();
            lacamentomultaAcessoriaFacade.getGeraDebitoMultaAcessoria().geraDebito(selecionado.getProcessoMultaAcessoria());
            lacamentomultaAcessoriaFacade.getDamFacade().geraDAM(selecionado.getProcessoMultaAcessoria().getCalculos().get(0));
            parcelas = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getProcessoMultaAcessoria().getCalculos().get(0).getId()).executaConsulta().getResultados();
            FacesUtil.addOperacaoRealizada("Débito gerado com sucesso!");
        } catch (Exception e) {
            logger.error("Não foi possível gerar os débidos do lançamento da multa acessória: " + e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível gerar os débidos do lançamento da multa acessória.");
        }
    }

    private void realizarProcessoCalculoMultaAcessoria() {
        ConfiguracaoTributario configuracao = lacamentomultaAcessoriaFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        ProcessoCalculoMultaAcessoria processo = lacamentomultaAcessoriaFacade.criarProcessoCalculoMultaAcessoria(selecionado, configuracao, "Lançamento de Multa Acessória", selecionado.getValorTotal(), Calculo.TipoCalculo.MULTA_ACESSORIA, Boolean.FALSE);
        selecionado.setProcessoMultaAcessoria(processo);
        selecionado = lacamentomultaAcessoriaFacade.atualizarLancamentoWithProcessoGerado(selecionado);
    }

    public void calcularValores() {
        if (itemLancamentoSelecionado.getMultaFiscalizacao() != null) {
            if (FormaCalculoMultaFiscalizacao.VALOR.equals(itemLancamentoSelecionado.getMultaFiscalizacao().getFormaCalculoMultaFiscalizacao())) {
                itemLancamentoSelecionado.setValorMulta(itemLancamentoSelecionado.getMultaFiscalizacao().getValorMulta());
            }
            if (FormaCalculoMultaFiscalizacao.QUANTIDADE.equals(itemLancamentoSelecionado.getMultaFiscalizacao().getFormaCalculoMultaFiscalizacao())) {
                if (itemLancamentoSelecionado.getQuantidadeUFMRB() == null || itemLancamentoSelecionado.getQuantidadeUFMRB() <= 0) {
                    FacesUtil.addCampoObrigatorio("O campo Quantidade UFMBR é obrigatório e deve ser maior que zero(0).");
                } else {
                    itemLancamentoSelecionado.setValorMulta((lacamentomultaAcessoriaFacade.getMoedaFacade().recuperaValorVigenteUFM().multiply
                        (BigDecimal.valueOf(itemLancamentoSelecionado.getQuantidadeUFMRB()))).setScale(BigDecimal.ROUND_HALF_UP, 2));
                }
            }
        }
    }

    public void imprimirDAM() {
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        CalculoMultaAcessoria calculo = lacamentomultaAcessoriaFacade.recuperarCalculoMultaAcessoria(selecionado);
        DAM dam = lacamentomultaAcessoriaFacade.getDamFacade().recuperaDAMpeloCalculo(calculo);
        ImprimeDAM imprimeDAM = new ImprimeDAM();
        imprimeDAM.adicionarParametro("SUBREPORT_DIR", subReport);
        try {
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(dam);
        } catch (Exception e) {
            logger.error("Não foi possivel imprimir o DAM: " + e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível imprimir o DAM: " + e);
        }
    }

    public boolean verificarListaMultaVazia() {
        if (isOperacaoNovo()) {
            return selecionado.getItemLancamentoMultaAcessoria().isEmpty();
        } else if (isOperacaoEditar()) {
            if (selecionado.getCadastroEconomico() != null) {
                tipoCadastroTributario = TipoCadastroTributario.ECONOMICO;
            } else {
                tipoCadastroTributario = TipoCadastroTributario.PESSOA;
            }
        }
        return false;
    }

    public void atribuirDescricaoMulta() {
        itemLancamentoSelecionado.setDescricao(itemLancamentoSelecionado.getMultaFiscalizacao().getArtigo());
        if(FormaCalculoMultaFiscalizacao.VALOR.equals(itemLancamentoSelecionado.getMultaFiscalizacao().getFormaCalculoMultaFiscalizacao())) {
            itemLancamentoSelecionado.setQuantidadeUFMRB(null);
        }
    }

    public void trocarTipoCadastro() {
        if (TipoCadastroTributario.ECONOMICO.equals(tipoCadastroTributario)) {
            selecionado.setPessoa(null);
        } else {
            selecionado.setCadastroEconomico(null);
        }
    }

    public void selecionarLancamento(ItemLancamentoMultaAcessoria item) {
        itemLancamentoSelecionado = (ItemLancamentoMultaAcessoria) Util.clonarObjeto(item);
        atribuirTipoCadastroTributario();
        recalcularTotal(item);
    }

    private void atribuirTipoCadastroTributario() {
        if (selecionado.getCadastroEconomico() != null) {
            tipoCadastroTributario = selecionado.getCadastroEconomico().getTipoDeCadastro();
        } else {
            tipoCadastroTributario = TipoCadastroTributario.PESSOA;
        }
    }

    public void excluirLancamento(ItemLancamentoMultaAcessoria item) {
        recalcularTotal(item);
        selecionado.getItemLancamentoMultaAcessoria().remove(item);
    }

    public void recalcularTotal(ItemLancamentoMultaAcessoria item) {
        selecionado.setValorTotal(selecionado.getValorTotal().subtract(item.getValorMulta()));
    }

    public boolean verificarParcelas() {
        if (parcelas == null) {
            return true;
        }
        return parcelas.isEmpty();
    }

    public void selecionarMulta(ItemLancamentoMultaAcessoria multa) {
        itemLancamentoSelecionado = multa;
    }

    public List<MultaFiscalizacao> recuperarMultas(String partes) {
        return lacamentomultaAcessoriaFacade.getMultaFiscalizacaoFacade().listaFiltrando(partes);
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public ItemLancamentoMultaAcessoria getItemLancamentoSelecionado() {
        return itemLancamentoSelecionado;
    }

    public void setItemLancamentoSelecionado(ItemLancamentoMultaAcessoria itemLancamentoSelecionado) {
        this.itemLancamentoSelecionado = itemLancamentoSelecionado;
    }
}
