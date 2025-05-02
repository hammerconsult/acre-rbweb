package br.com.webpublico.controle;

import br.com.webpublico.entidades.EventoRedeSim;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class PesquisaEventosRedeSimControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        condicao += " and obj.situacao = '" + EventoRedeSim.Situacao.PROCESSADO.name() + "' ";
        return condicao;
    }
}
