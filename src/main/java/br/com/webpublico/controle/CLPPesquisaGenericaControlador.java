/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import com.google.common.collect.Lists;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import java.io.Serializable;

/**
 * @author Major
 */
@ManagedBean
@ViewScoped
public class CLPPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void novo(ComponentSystemEvent evento) {
        super.novo(evento);
        ItemPesquisaGenerica codigo = new ItemPesquisaGenerica();
        codigo.setLabel("Código");
        codigo.setCondicao("obj.codigo");
        codigo.setTipoOrdenacao("asc");
        super.setCamposOrdenacao(Lists.<ItemPesquisaGenerica>newArrayList());
        super.getCamposOrdenacao().add(codigo);

        ItemPesquisaGenerica inicioVigencia = new ItemPesquisaGenerica();
        inicioVigencia.setLabel("Início Vigência");
        inicioVigencia.setCondicao("obj.inicioVigencia");
        inicioVigencia.setTipoOrdenacao("desc");
        super.getCamposOrdenacao().add(inicioVigencia);
    }

    @Override
    public String getHqlConsulta() {
        if (!montaCondicao().contains("eventoContabil")) {
            return super.getHqlConsulta();
        } else {
            return "select obj  from ItemEventoCLP item inner join item.clp obj ";
        }
    }

    @Override
    public String getHqlContador() {
        if (!montaCondicao().contains("eventoContabil")) {
            return super.getHqlContador();
        } else {
            return "select count(obj.id) from ItemEventoCLP item inner join item.clp obj ";
        }
    }

    @Override
    public void getCampos() {
        super.getCampos();
        super.getItens().add(new ItemPesquisaGenerica("item.eventoContabil.codigo", "Código do Evento Contábil", String.class, false, true));
        super.getItens().add(new ItemPesquisaGenerica("item.eventoContabil.descricao", "Descrição do Evento Contábil", String.class, false, true));
        super.getItens().add(new ItemPesquisaGenerica("item.eventoContabil.tipoEventoContabil", "Tipo do Evento Contábil", TipoEventoContabil.class, true, true));
        super.getItens().add(new ItemPesquisaGenerica("item.eventoContabil.tipoLancamento", "Tipo de Lançamento do Evento Contábil", TipoLancamento.class, true, true));
        super.getItens().add(new ItemPesquisaGenerica("item.eventoContabil.tipoBalancete", "Tipo de Balancete do Evento Contábil", TipoBalancete.class, true, true));
        super.getItens().add(new ItemPesquisaGenerica("item.eventoContabil.eventoTce", "Codigo do EVENTO TCE do Evento Contábil", String.class, false, true));
    }
}
