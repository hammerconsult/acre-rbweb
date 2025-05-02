package br.com.webpublico.interfaces;

import br.com.webpublico.negocios.tributario.auxiliares.AtributoGenerico;
import br.com.webpublico.util.anotacoes.AtributoIntegracaoSit;

import java.util.List;
import java.util.Map;


public interface DetentorAtributoGenerico {

    public Map<AtributoIntegracaoSit.ClasseAtributoGenerico, List<AtributoGenerico>> getAtributos();
}
