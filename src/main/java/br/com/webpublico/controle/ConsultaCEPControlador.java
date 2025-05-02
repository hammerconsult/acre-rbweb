/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CEPLocalidade;
import br.com.webpublico.entidades.CEPLogradouro;
import br.com.webpublico.entidades.CEPUF;
import br.com.webpublico.negocios.ConsultaCepFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author munif
 */
@ManagedBean
@ViewScoped
public class ConsultaCEPControlador implements Serializable {

    @EJB
    private ConsultaCepFacade consultaCepFacade;
    private String cep;
    private List<CEPLogradouro> logradouros;
    private CEPUF uf;
    private CEPLocalidade localidade;
    private CEPLogradouro logradouro;
    private String parteLogradouro;

    public CEPLogradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(CEPLogradouro logradouro) {
        this.logradouro = logradouro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public List<CEPLogradouro> getLogradouros() {
        return logradouros;
    }

    public void atualizaLogradouros() {
        logradouros = consultaCepFacade.consultaLogradouroPorCEP(cep);
    }

    public void atualizaCEP() {
        logradouros = new ArrayList<CEPLogradouro>();
        logradouros.add(logradouro);
    }

    public CEPLocalidade getLocalidade() {
        return localidade;
    }

    public void setLocalidade(CEPLocalidade localidade) {
        this.localidade = localidade;
    }

    public String getParteLogradouro() {
        return parteLogradouro;
    }

    public void setParteLogradouro(String parteLogradouro) {
        this.parteLogradouro = parteLogradouro;
    }

    public CEPUF getUf() {
        return uf;
    }

    public void setUf(CEPUF uf) {
        this.uf = uf;
    }

    public List<CEPUF> completaUF(String parte) {
        return consultaCepFacade.consultaUf(parte.trim());
    }

    public List<CEPLocalidade> completaLocalidade(String parte) {
        return consultaCepFacade.consultaLocalidades(uf, parte.trim());
    }

    public List<CEPLogradouro> completaLogradouro(String parte) {
        return consultaCepFacade.consultaLogradouros(localidade, parte.trim());
    }

    public Converter getConverterUf() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String string) {
                try {
                    if (string != null) {
                        Long chave = new Long(string);
                        return consultaCepFacade.recuperaCEPUF(chave);

                    }
                } catch (NumberFormatException ex) {
                    //ignorada
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                CEPUF uf = (CEPUF) value;
                if (uf != null) {
                    return uf.getId().toString();
                }
                return null;
            }
        };
    }

    public Converter getConverterLocalidade() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String string) {
                try {
                    if (string != null) {
                        Long chave = new Long(string);
                        return consultaCepFacade.recuperaCEPLocalidade(chave);

                    }
                } catch (NumberFormatException ex) {
                    //ignorada
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                CEPLocalidade localidade = (CEPLocalidade) value;
                if (localidade != null) {
                    return localidade.getId().toString();
                }
                return null;
            }
        };
    }

    public Converter getConverterLogradouro() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String string) {
                try {
                    if (string != null) {
                        Long chave = new Long(string);
                        return consultaCepFacade.recuperaCEPLogradouro(chave);

                    }
                } catch (NumberFormatException ex) {
                    //ignorada
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                CEPLogradouro logradouro = (CEPLogradouro) value;
                if (logradouro != null) {
                    return logradouro.getId().toString();
                }
                return null;
            }
        };
    }

    /*
     * converters que retornam string
     */

    public Converter getConverterUfString() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String string) {
                try {
                    if (string != null) {
                        Long chave = new Long(string);
                        CEPUF cep = consultaCepFacade.recuperaCEPUF(chave);
                        return cep.getNome();

                    }
                } catch (NumberFormatException ex) {
                    //ignorada
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (uf != null) {
                    return value.toString();
                }
                return null;
            }
        };
    }

    public Converter getConverterLocalidadeString() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String string) {
                try {
                    if (string != null) {
                        Long chave = new Long(string);
                        CEPLocalidade cep = consultaCepFacade.recuperaCEPLocalidade(chave);
                        return cep.getNome();

                    }
                } catch (NumberFormatException ex) {
                    //ignorada
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {

                if (localidade != null) {
                    return value.toString();
                }
                return null;
            }
        };
    }

    public Converter getConverterLogradouroString() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String string) {
                try {
                    if (string != null) {
                        Long chave = new Long(string);
                        CEPLogradouro cep = consultaCepFacade.recuperaCEPLogradouro(chave);
                        return cep.getNome();

                    }
                } catch (NumberFormatException ex) {
                    //ignorada
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {

                if (logradouro != null) {
                    return value.toString();
                }
                return null;
            }
        };
    }
}
