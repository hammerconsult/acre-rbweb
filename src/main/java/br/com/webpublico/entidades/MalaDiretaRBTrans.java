package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCredencialRBTrans;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by Wellington on 22/12/2016.
 */
@Entity
@Audited
@Etiqueta("Mala Direta de RBtrans")
public class MalaDiretaRBTrans extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Exercício")
    @ManyToOne
    private Exercicio exercicio;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Permissão Inicial")
    private Integer numeroPermissaoInicial;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Permissão Final")
    private Integer numeroPermissaoFinal;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Permissão")
    @Enumerated(EnumType.STRING)
    private TipoCredencialRBTrans tipoCredencialRBTrans;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Transporte")
    @Enumerated(EnumType.STRING)
    private TipoPermissaoRBTrans tipoPermissaoRBTrans;
    @Etiqueta("Dígito Inicial")
    private Integer digitoInicial;
    @Etiqueta("Dígito Final")
    private Integer digitoFinal;
    @Etiqueta("Texto da Mala Direta")
    private String texto;
    @OneToMany
    private List<MalaDiretaRBTransPermissao> permissoes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getNumeroPermissaoInicial() {
        return numeroPermissaoInicial;
    }

    public void setNumeroPermissaoInicial(Integer numeroPermissaoInicial) {
        this.numeroPermissaoInicial = numeroPermissaoInicial;
    }

    public Integer getNumeroPermissaoFinal() {
        return numeroPermissaoFinal;
    }

    public void setNumeroPermissaoFinal(Integer numeroPermissaoFinal) {
        this.numeroPermissaoFinal = numeroPermissaoFinal;
    }

    public TipoCredencialRBTrans getTipoCredencialRBTrans() {
        return tipoCredencialRBTrans;
    }

    public void setTipoCredencialRBTrans(TipoCredencialRBTrans tipoCredencialRBTrans) {
        this.tipoCredencialRBTrans = tipoCredencialRBTrans;
    }

    public Integer getDigitoInicial() {
        return digitoInicial;
    }

    public void setDigitoInicial(Integer digitoInicial) {
        this.digitoInicial = digitoInicial;
    }

    public Integer getDigitoFinal() {
        return digitoFinal;
    }

    public void setDigitoFinal(Integer digitoFinal) {
        this.digitoFinal = digitoFinal;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public List<MalaDiretaRBTransPermissao> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(List<MalaDiretaRBTransPermissao> permissoes) {
        this.permissoes = permissoes;
    }

    public TipoPermissaoRBTrans getTipoPermissaoRBTrans() {
        return tipoPermissaoRBTrans;
    }

    public void setTipoPermissaoRBTrans(TipoPermissaoRBTrans tipoPermissaoRBTrans) {
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
    }

    @Override
    public String toString() {
        return exercicio.getAno() + " - " + tipoCredencialRBTrans.getDescricao();
    }
}
