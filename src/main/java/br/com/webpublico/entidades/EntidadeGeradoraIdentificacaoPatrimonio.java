package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 26/08/14
 * Time: 11:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Entidade Geradora Identificação do Patrimônio")
@Table(name = "ENTIDADEGERADORA")
public abstract class EntidadeGeradoraIdentificacaoPatrimonio extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Etiqueta("Entidade")
    @Obrigatorio
    private Entidade entidade;

    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @ManyToOne
    private ParametroPatrimonio parametroPatrimonio;

    public EntidadeGeradoraIdentificacaoPatrimonio() {
        super();
    }

    public abstract String getFaixaInicial();

    public abstract String getFaixaFinal();

    public abstract EntidadeSequenciaPropria getEntidadeSequenciaPropria();

    public abstract void setEntidadeSequenciaPropria(EntidadeSequenciaPropria entidadeSequenciaPropria);

    public abstract String getSeguindoSequencia();

    public abstract Boolean ehSequenciaPropria();

    public abstract Boolean podeAlterarTipoSequencia();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public ParametroPatrimonio getParametroPatrimonio() {
        return parametroPatrimonio;
    }

    public void setParametroPatrimonio(ParametroPatrimonio parametroPatrimonio) {
        this.parametroPatrimonio = parametroPatrimonio;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public boolean isTipoGeracaoBemMovel() {
        return TipoBem.MOVEIS.equals(tipoBem);
    }

    public boolean isTipoGeracaoBemImovel() {
        return TipoBem.IMOVEIS.equals(tipoBem);
    }

    @Override
    public String toString() {
        return this.entidade.getNome();
    }
}
