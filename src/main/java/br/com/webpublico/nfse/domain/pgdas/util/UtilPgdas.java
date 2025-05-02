package br.com.webpublico.nfse.domain.pgdas.util;

import br.com.webpublico.nfse.domain.pgdas.ArquivoPgdas;
import br.com.webpublico.nfse.domain.pgdas.registros.PgdasRegistro00000;
import br.com.webpublico.nfse.domain.pgdas.registros.PgdasRegistro03000;
import br.com.webpublico.nfse.domain.pgdas.registros.PgdasRegistroAAAAA;
import br.com.webpublico.util.Persistencia;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

public class UtilPgdas {


    public TabelaView getViewObjetosJaRecuperados(List objetos) {


        if (objetos != null && !objetos.isEmpty()) {
            Class classe = objetos.get(0).getClass();

            TabelaView view = new TabelaView();
            view.setNomeTabela(Persistencia.getNomeDaClasse(classe));
            view.getObjetos().clear();
            view.getHeaders().clear();
            for (Field field : Persistencia.getAtributos(classe)) {
                field.setAccessible(true);
                if (!field.getName().equals("criadoEm")
                    && !field.getName().equals("id")
                    && !field.getType().equals(PgdasRegistro00000.class)
                    && !field.getType().equals(PgdasRegistroAAAAA.class)
                    && !field.getType().equals(PgdasRegistro03000.class)
                    && !field.getType().equals(ArquivoPgdas.class)
                ) {
                    view.getHeaders().add(new ColunaView(null, Persistencia.getNomeDoCampo(field)));
                }
            }

            for (Object obj : objetos) {
                List<ColunaView> colunas = Lists.newArrayList();
                int posicao = 0;
                try {
                    for (Field field : Persistencia.getAtributos(classe)) {
                        field.setAccessible(true);
                        if (!field.getName().equals("criadoEm")
                            && !field.getName().equals("id")
                            && !field.getType().equals(PgdasRegistro00000.class)
                            && !field.getType().equals(PgdasRegistroAAAAA.class)
                            && !field.getType().equals(PgdasRegistro03000.class)
                            && !field.getType().equals(ArquivoPgdas.class)) {
                            /*ColunaView coluna = preparaInstanciaColunaView(view, field.get(obj), posicao);*/
                            colunas.add(new ColunaView(field.get(obj), field.getName()));
                            posicao++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                view.getObjetos().put(obj, colunas);
            }
            return view;
        }
        TabelaView view = new TabelaView();
        view.setNomeTabela("nenhum registro encontrado");
        return view;
    }

    public static List<String> getListComplementar(List<String> pipes, int tamanho) {
        List<String> teste = Lists.newArrayList();
        teste.addAll(pipes);
        if (teste.size() <= tamanho) {
            for (int i = teste.size(); teste.size() <= tamanho; i++) {
                teste.add("");
            }
        }
        return teste;
    }

    public static BigDecimal getAsValor(String linha) {
        if (linha == null || linha.isEmpty()) {
            return null;
        }
        return new BigDecimal(linha.replace(",", "."));
    }
}
