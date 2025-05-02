package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.EventoBem;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 01/10/14
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
public interface ManipuladorDeBem {

    void selecionar(EventoBem bem);

    void selecionarTodos();

    void desmarcarTodos();

    void desmarcar(EventoBem eventoBem);

    EventoBem recuperarEventoDoBem(Bem bem);

    void pesquisar();
}
