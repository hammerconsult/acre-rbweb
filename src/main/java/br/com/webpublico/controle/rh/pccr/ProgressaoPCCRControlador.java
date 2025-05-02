package br.com.webpublico.controle.rh.pccr;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.pccr.PlanoCargosSalariosPCCR;
import br.com.webpublico.entidades.rh.pccr.ProgressaoPCCR;
import br.com.webpublico.entidades.rh.pccr.ReferenciaProgressaoPCCR;
import br.com.webpublico.entidades.rh.pccr.ValorProgressaoPCCR;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.pccr.PlanoCargosSalariosPCCRFacade;
import br.com.webpublico.negocios.rh.pccr.ProgressaoPCCRFacade;
import br.com.webpublico.negocios.rh.pccr.ReferenciaProgressaoPCCRFacade;
import br.com.webpublico.negocios.rh.pccr.ValorProgressaoPCCRFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 28/06/14
 * Time: 10:24
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoProgressaoPCCR", pattern = "/progressao-pccr-novo/novo/", viewId = "/faces/rh/pccr/progressaopccr/edita.xhtml"),
        @URLMapping(id = "editarProgressaoPCCR", pattern = "/progressao-pccr-novo/editar/#{progressaoPCCRControlador.id}/", viewId = "/faces/rh/pccr/progressaopccr/edita.xhtml"),
        @URLMapping(id = "listarProgressaoPCCR", pattern = "/progressao-pccr-novo/listar/", viewId = "/faces/rh/pccr/progressaopccr/lista.xhtml"),
        @URLMapping(id = "verProgressaoPCCR", pattern = "/progressao-pccr-novo/ver/#{progressaoPCCRControlador.id}/", viewId = "/faces/rh/pccr/progressaopccr/visualizar.xhtml")
})
public class ProgressaoPCCRControlador extends PrettyControlador<ProgressaoPCCR> implements Serializable, CRUD {
    @EJB
    private ProgressaoPCCRFacade progressaoPCCRFacade;
    @EJB
    private PlanoCargosSalariosPCCRFacade planoCargosSalariosPCCRFacade;
    @EJB
    private ReferenciaProgressaoPCCRFacade referenciaProgressaoPCCRFacade;
    @EJB
    private ValorProgressaoPCCRFacade valorProgressaoPCCRFacade;
    private Converter converterPlanoCargosPCCR;
    private ValorProgressaoPCCR valorProgressaoPCCR;
    private ReferenciaProgressaoPCCR referenciaProgressaoPCCR;
    private HashMap<Date, VigenciaValoresProgressoes> vigenciaMap;

    public ProgressaoPCCRControlador() {
        super(ProgressaoPCCR.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/progressao-pccr-novo/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AbstractFacade getFacede() {
        return progressaoPCCRFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<SelectItem> getPlanosCargosSalariosPCCRs() {
        return Util.getListSelectItem(planoCargosSalariosPCCRFacade.findPlanosCargosPCCRVigentes(SistemaFacade.getDataCorrente()));
    }

    public List<SelectItem> getProgressoes() {
        return Util.getListSelectItem(progressaoPCCRFacade.lista());
    }


    public Converter getConverterPlanoPCCR() {
        if (converterPlanoCargosPCCR == null) {
            converterPlanoCargosPCCR = new ConverterAutoComplete(PlanoCargosSalariosPCCR.class, planoCargosSalariosPCCRFacade);
        }
        return converterPlanoCargosPCCR;
    }

    public ValorProgressaoPCCR getValorProgressaoPCCR() {
        return valorProgressaoPCCR;
    }

    public void setValorProgressaoPCCR(ValorProgressaoPCCR valorProgressaoPCCR) {
        this.valorProgressaoPCCR = valorProgressaoPCCR;
    }

    public ReferenciaProgressaoPCCR getReferenciaProgressaoPCCR() {
        return referenciaProgressaoPCCR;
    }

    public void setReferenciaProgressaoPCCR(ReferenciaProgressaoPCCR referenciaProgressaoPCCR) {
        this.referenciaProgressaoPCCR = referenciaProgressaoPCCR;
    }

    @URLAction(mappingId = "novoProgressaoPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();    //To change body of overridden methods use File | Settings | File Templates.
        referenciaProgressaoPCCR = new ReferenciaProgressaoPCCR();
        referenciaProgressaoPCCR.setLetra("A");
        referenciaProgressaoPCCR.setNumero(1);
        valorProgressaoPCCR = new ValorProgressaoPCCR();
        vigenciaMap = new LinkedHashMap<>();
    }

    @URLAction(mappingId = "verProgressaoPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editarProgressaoPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
        carregaMapValores();
    }

    private void carregaMapValores() {
        vigenciaMap = new LinkedHashMap<>();
        referenciaProgressaoPCCR = new ReferenciaProgressaoPCCR();
        valorProgressaoPCCR = new ValorProgressaoPCCR();
//        Collections.sort(selecionado.getValorProgressaoPCCRs());
        if (selecionado.getId() != null) {
            for (ValorProgressaoPCCR valor : valorProgressaoPCCRFacade.findValorByProgresao(selecionado)) {
                if (valor.getFinalVigencia() == null) {
                    valorProgressaoPCCR.setInicioVigencia(valor.getInicioVigencia());
                    continue;
                }
                if (vigenciaMap.containsKey(valor.getInicioVigencia())) {
                    vigenciaMap.get(valor.getInicioVigencia()).getValorProgressaoPCCRs().add(valor);
                } else {
                    VigenciaValoresProgressoes v = new VigenciaValoresProgressoes();
                    v.setInicioVigencia(valor.getInicioVigencia());
                    v.setFinalVigencia(valor.getFinalVigencia());
                    v.getValorProgressaoPCCRs().add(valor);
                    vigenciaMap.put(valor.getInicioVigencia(), v);
                }
            }
        }
    }


    public HashMap<Date, VigenciaValoresProgressoes> getVigenciaMap() {
        return vigenciaMap;
    }

    public List<Date> getDates() {
        return Lists.newArrayList(vigenciaMap.keySet());
    }

    public void setVigenciaMap(HashMap<Date, VigenciaValoresProgressoes> vigenciaMap) {
        this.vigenciaMap = vigenciaMap;
    }

    public void addReferencia() {
        Date inicioVigencia = valorProgressaoPCCR.getInicioVigencia();
        Date finalVigencia = valorProgressaoPCCR.getFinalVigencia();
        valorProgressaoPCCR.setReferenciaProgressaoPCCR(referenciaProgressaoPCCR);
        valorProgressaoPCCR.setProgressaoPCCR(selecionado);
        selecionado.getValorProgressaoPCCRs().add(valorProgressaoPCCR);

        valorProgressaoPCCR = new ValorProgressaoPCCR();
        valorProgressaoPCCR.setInicioVigencia(inicioVigencia);
        valorProgressaoPCCR.setFinalVigencia(finalVigencia);
        referenciaProgressaoPCCR = new ReferenciaProgressaoPCCR();
    }

    public void removeValor(ValorProgressaoPCCR valorProgressaoPCCR) {
        selecionado.getValorProgressaoPCCRs().remove(valorProgressaoPCCR);
    }

    public List<SelectItem> getReferenciasDaProgressao() {
        if (selecionado.getId() != null) {
            return Util.getListSelectItem(referenciaProgressaoPCCRFacade.findReferenciaByProgressaoPCCR(selecionado));
        } else {
            return new ArrayList<>();
        }
    }

    public void finalizarPCCRAtual() {
        if (podeFinalizarPCCR()) {
            for (ValorProgressaoPCCR val : selecionado.getValorProgressaoPCCRsVigentes()) {
                val.setFinalVigencia(valorProgressaoPCCR.getFinalVigencia());
            }
            carregaMapValores();
        }


    }

    private boolean podeFinalizarPCCR() {
        return valorProgressaoPCCR.getFinalVigencia() != null;
    }


    public class VigenciaValoresProgressoes {
        private Date inicioVigencia;
        private Date finalVigencia;
        private List<ValorProgressaoPCCR> valorProgressaoPCCRs;

        public VigenciaValoresProgressoes() {
            valorProgressaoPCCRs = new LinkedList<>();
        }

        public VigenciaValoresProgressoes(Date inicioVigencia, Date finalVigencia, List<ValorProgressaoPCCR> valorProgressaoPCCRs) {
            this.inicioVigencia = inicioVigencia;
            this.finalVigencia = finalVigencia;
            this.valorProgressaoPCCRs = valorProgressaoPCCRs;
        }

        public List<ValorProgressaoPCCR> getValorProgressaoPCCRs() {
            return valorProgressaoPCCRs;
        }

        public void setValorProgressaoPCCRs(List<ValorProgressaoPCCR> valorProgressaoPCCRs) {
            this.valorProgressaoPCCRs = valorProgressaoPCCRs;
        }

        public Date getFinalVigencia() {
            return finalVigencia;
        }

        public void setFinalVigencia(Date finalVigencia) {
            this.finalVigencia = finalVigencia;
        }

        public Date getInicioVigencia() {
            return inicioVigencia;
        }

        public void setInicioVigencia(Date inicioVigencia) {
            this.inicioVigencia = inicioVigencia;
        }
    }


}
