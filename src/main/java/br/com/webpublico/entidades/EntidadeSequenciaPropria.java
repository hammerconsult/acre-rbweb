package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 26/08/14
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Entidade Sequência Própria")
public class EntidadeSequenciaPropria extends EntidadeGeradoraIdentificacaoPatrimonio {

    @Etiqueta("Faixa Inicial")
    @Obrigatorio
    private String faixaInicial;

    @Etiqueta("Faixa Final")
    @Obrigatorio
    private String faixaFinal;

    @OneToMany(mappedBy = "entidadeSequenciaPropria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntidadeSequenciaAgregada> agregadas;


    public EntidadeSequenciaPropria() {
        super();
        agregadas = new ArrayList<>();
    }

    public String getFaixaInicial() {
        return faixaInicial;
    }

    public void setFaixaInicial(String faixaInicial) {
        this.faixaInicial = faixaInicial;
    }

    public String getFaixaFinal() {
        return faixaFinal;
    }

    @Override
    public EntidadeSequenciaPropria getEntidadeSequenciaPropria() {
        return this;
    }

    @Override
    public void setEntidadeSequenciaPropria(EntidadeSequenciaPropria entidadeSequenciaPropria) {
    }

    @Override
    public String getSeguindoSequencia() {
        return "Própria";
    }

    @Override
    public Boolean ehSequenciaPropria() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean podeAlterarTipoSequencia() {
        return agregadas.isEmpty();
    }

    public void setFaixaFinal(String faixaFinal) {
        this.faixaFinal = faixaFinal;
    }

    public List<EntidadeSequenciaAgregada> getAgregadas() {
        return agregadas;
    }

    public void setAgregadas(List<EntidadeSequenciaAgregada> agregadas) {
        this.agregadas = agregadas;
    }
}
