package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 26/08/14
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Entidade Sequência Agregada")
public class EntidadeSequenciaAgregada extends EntidadeGeradoraIdentificacaoPatrimonio {

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Seguindo sequência de")
    private EntidadeSequenciaPropria entidadeSequenciaPropria;

    public EntidadeSequenciaAgregada() {
        super();
    }

    @Override
    public String getFaixaInicial() {
        try {
            return entidadeSequenciaPropria.getFaixaInicial();
        } catch (NullPointerException ex) {
            return "";
        }
    }

    @Override
    public String getFaixaFinal() {
        try {
            return entidadeSequenciaPropria.getFaixaFinal();
        } catch (NullPointerException ex) {
            return "";
        }
    }

    public EntidadeSequenciaPropria getEntidadeSequenciaPropria() {
        return entidadeSequenciaPropria;
    }

    @Override
    public String getSeguindoSequencia() {
        return getEntidadeSequenciaPropria().toString();
    }

    @Override
    public Boolean ehSequenciaPropria() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean podeAlterarTipoSequencia() {
        return Boolean.TRUE;
    }

    public void setEntidadeSequenciaPropria(EntidadeSequenciaPropria entidadeSequenciaPropria) {
        this.entidadeSequenciaPropria = entidadeSequenciaPropria;
    }
}
