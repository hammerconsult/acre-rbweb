package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoValidacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Fabio
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("SubContas da Dívida Pública")
public class SubContaDividaPublica extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Dívida Pública")
    @ManyToOne
    private DividaPublica dividaPublica;
    @Obrigatorio
    @Etiqueta("Exercício")
    @ManyToOne
    @Tabelavel
    private Exercicio exercicio;
    @Obrigatorio
    @Etiqueta("Conta Financeira")
    @ManyToOne
    @Tabelavel
    private SubConta subConta;
    @Obrigatorio
    @Etiqueta("Conta de Destinação de Recurso")
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Validação")
    private TipoValidacao tipoValidacao;

    public SubContaDividaPublica() {
        super();
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public TipoValidacao getTipoValidacao() {
        return tipoValidacao;
    }

    public void setTipoValidacao(TipoValidacao tipoValidacao) {
        this.tipoValidacao = tipoValidacao;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    @Override
    public String toString() {
        return "SubContaDividaPublica{" + "id=" + id + '}';
    }

}
