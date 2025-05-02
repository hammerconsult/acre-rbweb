/*
 * Codigo gerado automaticamente em Mon Jun 13 17:10:09 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.LancamentoPadronizado;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContaFacade;
import br.com.webpublico.negocios.LancamentoPadronizadoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@SessionScoped
public class LancamentoPadronizadoControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private LancamentoPadronizadoFacade lancamentoPadronizadoFacade;
    @EJB
    private ContaFacade contaFacade;
    private ConverterAutoComplete converterContaCredito;
    private ConverterAutoComplete converterContaDebito;

    public LancamentoPadronizadoControlador() {
        metadata = new EntidadeMetaData(LancamentoPadronizado.class);
    }

    public LancamentoPadronizadoFacade getFacade() {
        return lancamentoPadronizadoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return lancamentoPadronizadoFacade;
    }

    public List<Conta> getContas() {
        Exercicio ex = getExercicioCorrente();
        return lancamentoPadronizadoFacade.listaConta(ex);
    }

    public List<Conta> completaContas(String parte) {
        Exercicio ex = getExercicioCorrente();
        return lancamentoPadronizadoFacade.listaFiltrando(parte, ex, "descricao", "codigo");
    }

    public Converter getConverterContaDebito() {
        if (converterContaDebito == null) {
            converterContaDebito = new ConverterAutoComplete(Conta.class, contaFacade);
        }
        return converterContaDebito;
    }

    public Converter getConverterContaCredito() {
        if (converterContaCredito == null) {
            converterContaCredito = new ConverterAutoComplete(Conta.class, contaFacade);
        }
        return converterContaCredito;
    }

}
