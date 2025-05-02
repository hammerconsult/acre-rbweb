package br.com.webpublico.converter;

import br.com.webpublico.entidades.Habilitacao;
import br.com.webpublico.entidades.Motorista;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.negocios.MotoristaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 13/10/14
 * Time: 17:23
 * To change this template use File | Settings | File Templates.
 */
public class ConverterMotorista implements Converter, Serializable {

    private MotoristaFacade motoristaFacade;
    private Motorista motorista;

    public ConverterMotorista(MotoristaFacade motoristaFacade) {
        this.motoristaFacade = motoristaFacade;
    }

    private void emitirAlertas(PessoaFisica pessoaFisica) {
        pessoaFisica = (PessoaFisica) motoristaFacade.getPessoaFacade().recuperarPF(pessoaFisica.getId());
        verificaVencimentoHabilitacao(pessoaFisica.getHabilitacao());

    }

    private void verificaVencimentoHabilitacao(Habilitacao habilitacao) {
        if (habilitacao != null && habilitacao.getId() != null &&
                habilitacao.getValidade() != null) {
            Date dataAtual = Util.getDataHoraMinutoSegundoZerado(motoristaFacade.getSistemaFacade().getDataOperacao());
            Date validadeHabilitacao = Util.getDataHoraMinutoSegundoZerado(habilitacao.getValidade());
            long dias = Util.diferencaDeDiasEntreData(dataAtual, validadeHabilitacao);
            if (dias <= 30) {
                FacesUtil.addAtencao("A Carteira de Habilitação do motorista " + habilitacao.getPessoaFisica() + " está à " + dias + " dia(s) para termino de sua validade.");
            }
        }
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        try {
            Object chave = Persistencia.getFieldId(Motorista.class).getType().getConstructor(String.class).newInstance(value);
            if (motorista == null || !motorista.getId().equals(chave)) {
                motorista = motoristaFacade.recuperar(chave);
                if (motorista != null && motorista.getId() != null) {
                    emitirAlertas(motorista.getPessoaFisica());
                }
            }
            return motorista;
        } catch (Exception ex) {
            //Evita mensagens durante a digitaçao parcial.
            //logger.log(Level.SEVERE, "Problema ao instanciar a chave", ex);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value == null) {
            return null;
        } else {
            if (value instanceof Long) {
                return String.valueOf(value);
            } else {
                try {
                    return Persistencia.getAttributeValue(value, Persistencia.getFieldId(Motorista.class).getName()).toString();
                } catch (Exception e) {
                    return String.valueOf(value);
                }
            }
        }
    }
}
