package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;


import br.com.webpublico.interfaces.GeradorReferenciaParcela;
import com.google.common.collect.Maps;

import java.util.Map;

public class GerenciadorDeInstancias {
    private static final GerenciadorDeInstancias INSTANCE = new GerenciadorDeInstancias();
    private Map<Class<? extends GeradorReferenciaParcela>, GeradorReferenciaParcela> referencias;

    private GerenciadorDeInstancias() {
        referencias = Maps.newHashMap();
    }

    public static GerenciadorDeInstancias getINSTANCE() {
        return INSTANCE;
    }

    public GeradorReferenciaParcela getReferencia(Class<? extends GeradorReferenciaParcela> classe) throws IllegalAccessException, InstantiationException {
          if(!referencias.containsKey(classe)){
              referencias.put(classe, classe.newInstance());
          }
        return referencias.get(classe);
    }
}
