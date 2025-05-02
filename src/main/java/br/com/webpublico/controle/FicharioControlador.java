/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Fichario;
import br.com.webpublico.entidades.GavetaFichario;
import br.com.webpublico.entidades.PastaGaveta;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FicharioFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author leonardo
 */
@ManagedBean(name = "ficharioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoFichario", pattern = "/fichario/novo/", viewId = "/faces/rh/administracaodepagamento/fichario/edita.xhtml"),
    @URLMapping(id = "editarFichario", pattern = "/fichario/editar/#{ficharioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/fichario/edita.xhtml"),
    @URLMapping(id = "listarFichario", pattern = "/fichario/listar/", viewId = "/faces/rh/administracaodepagamento/fichario/lista.xhtml"),
    @URLMapping(id = "verFichario", pattern = "/fichario/ver/#{ficharioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/fichario/visualizar.xhtml")
})
public class FicharioControlador extends PrettyControlador<Fichario> implements Serializable, CRUD {

    @EJB
    private FicharioFacade ficharioFacade;
    private GavetaFichario gavetaFicharioSelecionado;

    public FicharioControlador() {
        super(Fichario.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return ficharioFacade;
    }

    public GavetaFichario getGavetaFicharioSelecionado() {
        return gavetaFicharioSelecionado;
    }

    public void setGavetaFicharioSelecionado(GavetaFichario gavetaFicharioSelecionado) {
        this.gavetaFicharioSelecionado = gavetaFicharioSelecionado;
    }

    @URLAction(mappingId = "novoFichario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        gavetaFicharioSelecionado = new GavetaFichario();
    }

    @URLAction(mappingId = "editarFichario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        gavetaFicharioSelecionado = new GavetaFichario();
    }

    @URLAction(mappingId = "verFichario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void adicionarGaveta() {
        try {
            validarGaveta();
            gavetaFicharioSelecionado.setFichario(selecionado);
            Integer numeroGaveta = 0;
            if (!selecionado.getGavetasFicharios().isEmpty()) {
                for (GavetaFichario gavetaFichario : selecionado.getGavetasFicharios()) {
                    if (!gavetaFichario.getPastasGavetas().isEmpty()) {
                        for (PastaGaveta pastaGaveta : gavetaFichario.getPastasGavetas()) {
                            if (pastaGaveta.getNumero() > numeroGaveta) {
                                numeroGaveta = pastaGaveta.getNumero();
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < gavetaFicharioSelecionado.getQuantidadePastas(); i++) {
                PastaGaveta pastaGaveta = new PastaGaveta();
                pastaGaveta.setGavetaFichario(gavetaFicharioSelecionado);
                pastaGaveta.setNumero(numeroGaveta + 1);
                gavetaFicharioSelecionado.getPastasGavetas().add(pastaGaveta);
                numeroGaveta++;
            }
            selecionado.getGavetasFicharios().add(gavetaFicharioSelecionado);

            gavetaFicharioSelecionado = new GavetaFichario();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerGaveta(GavetaFichario gaveta) {
        if (Operacoes.NOVO.equals(operacao) || ficharioFacade.hasPastaGavetaEmUso(gaveta)) {
            selecionado.getGavetasFicharios().remove(gaveta);
            gavetaFicharioSelecionado = new GavetaFichario();
        } else {
            FacesUtil.addOperacaoNaoRealizada("Existem pastas na gaveta que estão em uso, não podendo ser excluída.");
        }
    }

    private void validarGaveta() {
        ValidacaoException ve = new ValidacaoException();
        if (gavetaFicharioSelecionado.getCodigo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O código da gaveta é obrigatório.");
        }

        if (gavetaFicharioSelecionado.getDescricao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A descrição da gaveta é obrigatória.");
        }

        if (gavetaFicharioSelecionado.getQuantidadePastas() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Quantidade de pastas na gaveta é obrigatória.");
        } else {
            if (gavetaFicharioSelecionado.getQuantidadePastas() <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Quantidade de pastas na gaveta deve ser maior que zero.");
            }
        }

        for (GavetaFichario item : selecionado.getGavetasFicharios()) {
            if (gavetaFicharioSelecionado != null) {
                if (gavetaFicharioSelecionado.getCodigo() != null) {
                    if (item.getCodigo().compareTo(gavetaFicharioSelecionado.getCodigo()) == 0 &&
                        (!gavetaFicharioSelecionado.equals(item))) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma gaveta com o mesmo código.");
                        break;
                    }
                }
            }
        }


        for (GavetaFichario item : selecionado.getGavetasFicharios()) {
            if (gavetaFicharioSelecionado != null) {
                PastaGaveta ultimaGavetaEmUso = ficharioFacade.buscarUltimaGavetaEmUso(gavetaFicharioSelecionado);
                if (ultimaGavetaEmUso != null && ultimaGavetaEmUso.getNumero() > gavetaFicharioSelecionado.getQuantidadePastas() &&
                    (gavetaFicharioSelecionado.equals(item))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Existe pasta ocupada nesse intervalo.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/fichario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
