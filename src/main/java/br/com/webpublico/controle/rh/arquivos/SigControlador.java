package br.com.webpublico.controle.rh.arquivos;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.rh.arquivos.Sig;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.rh.sig.EsferaPoderSig;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.arquivos.SigFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean(name = "sigControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-sig", pattern = "/arquivo/sig/novo/", viewId = "/faces/rh/administracaodepagamento/arquivosig/edita.xhtml"),
    @URLMapping(id = "ver-sig", pattern = "/arquivo/sig/ver/#{sigControlador.id}/", viewId = "/faces/rh/administracaodepagamento/arquivosig/visualizar.xhtml"),
    @URLMapping(id = "editar-sig", pattern = "/arquivo/sig/editar/#{sigControlador.id}/", viewId = "/faces/rh/administracaodepagamento/arquivosig/edita.xhtml"),
    @URLMapping(id = "lista-sig", pattern = "/arquivo/sig/listar/", viewId = "/faces/rh/administracaodepagamento/arquivosig/lista.xhtml")
})
public class SigControlador extends PrettyControlador<Sig> implements Serializable, CRUD {
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<Sig> future;
    @EJB
    private SigFacade sigFacade;

    public SigControlador() {
        super(Sig.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return sigFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/arquivo/sig/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void gerarArquivoSig() {
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        assistenteBarraProgresso.setDataAtual(UtilRH.getDataOperacao());
        try {
            future = sigFacade.gerarArquivoSig(selecionado, assistenteBarraProgresso);
            FacesUtil.executaJavaScript("gerarArquivo()");
        } catch (ParserConfigurationException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (TransformerException e) {
            logger.error(e.getMessage());
        }
    }

    public void consultarAndamentoArquivo(){
        if(future != null && future.isDone()){
            try {
                selecionado = future.get();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            } catch (ExecutionException e) {
                logger.error(e.getMessage());
            }
            future = null;
            super.salvar();
        }
    }
    @Override
    @URLAction(mappingId = "novo-sig", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "ver-sig", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getRetornaMes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (Mes mes : Mes.values()) {
            retorno.add(new SelectItem(mes, mes.getDescricao()));
        }
        return retorno;
    }

    public List<PessoaFisica> completarPessoa(String parte) {
        return sigFacade.getContratoFPFacade().listaPessoasComContratosVigentes(parte);
    }

    public List<PessoaJuridica> completarPessoaJuridica(String parte) {
        return sigFacade.getPessoaFacade().getPessoaJuridicaFacade().listaFiltrandoRazaoSocialCNPJ(parte);
    }

    public List<SelectItem> getEsferaPoder() {
        return Util.getListSelectItem(EsferaPoderSig.values(), false);
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }
}
