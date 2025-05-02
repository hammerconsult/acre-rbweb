/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Agencia;
import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.SaldoConstContaBancaria;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SaldoConstContaBancariaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Edi
 */

@ManagedBean(name = "saldoConstContaBancariaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-saldo-constante",   pattern = "/conciliacao/saldo-constante-bancario/novo/",                                            viewId = "/faces/financeiro/conciliacao/saldoconstcontabancaria/edita.xhtml"),
        @URLMapping(id = "editar-saldo-constante", pattern = "/conciliacao/saldo-constante-bancario/editar/#{saldoConstContaBancariaControlador.id}/", viewId = "/faces/financeiro/conciliacao/saldoconstcontabancaria/edita.xhtml"),
        @URLMapping(id = "ver-saldo-constante",    pattern = "/conciliacao/saldo-constante-bancario/ver/#{saldoConstContaBancariaControlador.id}/",    viewId = "/faces/financeiro/conciliacao/saldoconstcontabancaria/visualizar.xhtml"),
        @URLMapping(id = "listar-saldo-constante", pattern = "/conciliacao/saldo-constante-bancario/listar/",                                          viewId = "/faces/financeiro/conciliacao/saldoconstcontabancaria/lista.xhtml")
})

public class SaldoConstContaBancariaControlador extends PrettyControlador<SaldoConstContaBancaria> implements Serializable, CRUD {

    @EJB
    private SaldoConstContaBancariaFacade saldoConstContaBancariaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private List<SaldoConstContaBancaria> lista;
    private Banco banco;
    private Agencia agencia;
    private ConverterAutoComplete converterBanco;
    private ConverterAutoComplete converterAgencia;
    private ConverterAutoComplete converterContaFinaceira;

    public SaldoConstContaBancariaControlador() {
        super(SaldoConstContaBancaria.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/conciliacao/saldo-constante-bancario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return saldoConstContaBancariaFacade;
    }

    @URLAction(mappingId = "ver-saldo-constante", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-saldo-constante", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }


    @Override
    @URLAction(mappingId = "novo-saldo-constante", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado = new SaldoConstContaBancaria();
        selecionado.setDataSaldo(sistemaControlador.getDataOperacao());
        banco = null;
        agencia = null;
        selecionado.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public Agencia getAgencia() {
        return agencia;
    }

    public void setAgencia(Agencia agencia) {
        this.agencia = agencia;
    }

    public ConverterAutoComplete getConverterBanco() {
        if (converterBanco == null) {
            converterBanco = new ConverterAutoComplete(Banco.class, saldoConstContaBancariaFacade.getBancoFacade());
        }
        return converterBanco;
    }

    public void setConverterBanco(ConverterAutoComplete converterBanco) {
        this.converterBanco = converterBanco;
    }

    public ConverterAutoComplete getConverterAgencia() {
        if (converterAgencia == null) {
            converterAgencia = new ConverterAutoComplete(Agencia.class, saldoConstContaBancariaFacade.getAgenciaFacade());
        }
        return converterAgencia;
    }

    public void setConverterAgencia(ConverterAutoComplete converterAgencia) {
        this.converterAgencia = converterAgencia;
    }

    public ConverterAutoComplete getConverterContaFinaceira() {
        if (converterContaFinaceira == null) {
            converterContaFinaceira = new ConverterAutoComplete(ContaBancariaEntidade.class, saldoConstContaBancariaFacade.getContaBancariaEntidadeFacade());
        }
        return converterContaFinaceira;
    }

    public List<SaldoConstContaBancaria> getLista() {
        if (lista == null) {
            lista = saldoConstContaBancariaFacade.lista();
        }
        return lista;
    }

    public List<Banco> completaBanco(String parte) {
        List<Banco> listaBanco = saldoConstContaBancariaFacade.getBancoFacade().listaFiltrando(parte.trim(), "numeroBanco", "descricao");
        return listaBanco;
    }

    public List<Agencia> completaAgencia(String parte) {
        if (banco != null) {
            List<Agencia> listaAgencia = saldoConstContaBancariaFacade.getAgenciaFacade().listaFiltrandoPorBanco(parte.trim(), banco);
            return listaAgencia;
        }
        return null;
    }

    public List<ContaBancariaEntidade> completaContaBancaria(String parte) {
        if (agencia != null) {
            List<ContaBancariaEntidade> listaContaBancaria = saldoConstContaBancariaFacade.getContaBancariaEntidadeFacade().getContabancariaPorAgencia(parte, agencia);
            return listaContaBancaria;
        }
        return null;
    }

    public void salvar() {
        if (validaCampos()) {
            try {
                if (operacao.equals(Operacoes.NOVO)) {
                    saldoConstContaBancariaFacade.salvarNovo(selecionado);
                } else {
                    saldoConstContaBancariaFacade.salvar(selecionado);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Operação Realizada! ", " Registro salvo com sucesso."));
                redireciona();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação não Realizada ", e.getMessage().toString()));
            }
        }
    }

    public Boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getDataSaldo() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Data é obrigatório."));
            retorno = false;
        }
        if (banco == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Banco é obrigatório."));
            retorno = false;
        }
        if (agencia == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Agência é obrigatório."));
            retorno = false;
        }
        if (selecionado.getContaBancariaEntidade() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Conta Bancária é obrigatório."));
            retorno = false;
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valor incorreto! ", " O valor da deve ser maior que zero (0)."));
            retorno = false;
        }
        return retorno;
    }

    public void recuperarEditarVer() {
        selecionado = saldoConstContaBancariaFacade.recuperar(((SaldoConstContaBancaria) selecionado).getId());
        banco = saldoConstContaBancariaFacade.getBancoFacade().recuperar(selecionado.getContaBancariaEntidade().getAgencia().getBanco().getId());
        agencia = saldoConstContaBancariaFacade.getAgenciaFacade().recuperar(selecionado.getContaBancariaEntidade().getAgencia().getId());
    }


    public SaldoConstContaBancaria getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(SaldoConstContaBancaria selecionado) {
        this.selecionado = selecionado;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
