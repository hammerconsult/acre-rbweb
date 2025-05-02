package br.com.webpublico.util;

import br.com.webpublico.entidades.ReceitaFechamentoExercicio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 04/11/14
 * Time: 08:54
 * To change this template use File | Settings | File Templates.
 */
public class SuperLista<T> extends ArrayList<T> implements ISuperLista<T> {
    private List<T> objetos;
    private Class<T> classe;

    public Class<T> getClasse() {
        return classe;
    }

    public List<T> getObjetos() {
        return objetos;
    }

    public SuperLista(List<T> objetos, Class<T> classe) {
        this.classe = classe;
        this.objetos = objetos;
    }

    @Override
    public void adicionar(T t) {
        objetos.add(t);
    }

    @Override
    public void adicionarTodas() {
        objetos.clear();
        for (T objeto : this) {
            objetos.add(objeto);
        }
    }

    @Override
    public void adicionarTodasNasDuasListas(List<T> e) {
        objetos.clear();
        for (T objeto : e) {
            objetos.add(objeto);
        }
        this.addAll(objetos);
    }

    @Override
    public void removerTodas() {
        for (T objeto : this) {
            objetos.remove(objeto);
        }
    }

    @Override
    public void remover(T t) {
        objetos.remove(t);
    }

    @Override
    public Boolean jaFoiAdicionado(T t) {
        if (objetos.contains(t)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean todasAdicionadas() {
        if (objetos.size() == this.size()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
