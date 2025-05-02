/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ArquivoParte;
import br.com.webpublico.entidades.MotoristaSaud;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.MotoristaSaudFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @author Usuario
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarMotoristaSaud",
        pattern = "/tributario/saud/motorista-saud/listar/",
        viewId = "/faces/tributario/saud/motoristasaud/lista.xhtml"),
    @URLMapping(id = "novoMotoristaSaud",
        pattern = "/tributario/saud/motorista-saud/novo/",
        viewId = "/faces/tributario/saud/motoristasaud/edita.xhtml"),
    @URLMapping(id = "editarMotoristaSaud",
        pattern = "/tributario/saud/motorista-saud/editar/#{motoristaSaudControlador.id}/",
        viewId = "/faces/tributario/saud/motoristasaud/edita.xhtml"),
    @URLMapping(id = "verMotoristaSaud",
        pattern = "/tributario/saud/motorista-saud/ver/#{motoristaSaudControlador.id}/",
        viewId = "/faces/tributario/saud/motoristasaud/visualizar.xhtml")
})
public class MotoristaSaudControlador extends PrettyControlador<MotoristaSaud> implements Serializable, CRUD {

    @EJB
    private MotoristaSaudFacade facade;

    public MotoristaSaudControlador() {
        super(MotoristaSaud.class);
    }

    @Override
    public MotoristaSaudFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/saud/motorista-saud/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoMotoristaSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setPessoaFisica((PessoaFisica) Web.pegaDaSessao(PessoaFisica.class));
        recuperarPessoaFisica();
    }

    @URLAction(mappingId = "editarMotoristaSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarPessoaFisica();
    }

    @URLAction(mappingId = "verMotoristaSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        recuperarPessoaFisica();
    }

    private void carregarFoto() {
        if (selecionado.getFoto() == null) return;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (ArquivoParte a : selecionado.getFoto().getPartes()) {
                buffer.write(a.getDados());
            }
            InputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
            DefaultStreamedContent imagemFoto = new DefaultStreamedContent(inputStream,
                selecionado.getFoto().getMimeType(),
                selecionado.getFoto().getNome());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void selecionouPessoaFisica() {
        recuperarPessoaFisica();
    }

    private void recuperarPessoaFisica() {
        selecionado.setEnderecoPrincipal(null);
        selecionado.setCnh(null);
        if (selecionado.getPessoaFisica() == null) return;
        PessoaFisica pessoaFisica = facade.getPessoaFisicaFacade().recuperar(selecionado.getPessoaFisica().getId());
        selecionado.setPessoaFisica(pessoaFisica);
        selecionado.setEnderecoPrincipal(pessoaFisica.getEnderecoPrincipal());
        selecionado.setTelefonePrincipal(pessoaFisica.getTelefonePrincipal());
        selecionado.setCnh(pessoaFisica.getCNH());
        if (selecionado.getPessoaFisica().getArquivo() != null) {
            selecionado.setFoto(facade.getArquivoFacade()
                .recuperaDependencias(selecionado.getPessoaFisica().getArquivo().getId()));
            carregarFoto();
        }
    }

    public boolean mostraImagem() {
        try {
            return ((StreamedContent) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("imagem-foto")).getName().trim().length() <= 0;
        } catch (Exception e) {
            return true;
        }
    }

}
