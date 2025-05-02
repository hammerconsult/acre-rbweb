package br.com.webpublico.util;

import br.com.webpublico.entidades.ReceitaFechamentoExercicio;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 04/11/14
 * Time: 08:56
 * To change this template use File | Settings | File Templates.
 */
public interface ISuperLista<E> extends List<E> {
    public void adicionar(E e);

    public void adicionarTodas();

    public void adicionarTodasNasDuasListas(List<E> e);

    public void removerTodas();

    public void remover(E e);

    public Boolean jaFoiAdicionado(E e);

    public Boolean todasAdicionadas();
}
