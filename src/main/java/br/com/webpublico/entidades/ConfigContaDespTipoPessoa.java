package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPessoaPermitido;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 13/01/15
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Etiqueta("Configuração Conta de Despesa/Tipo de Pessoa")
public class ConfigContaDespTipoPessoa extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Tipo de Pessoa")
    private TipoPessoaPermitido tipoPessoa;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta(value = "Exercício")
    private Exercicio exercicio;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta(value = "Conta de Despesa")
    private Conta contaDespesa;
    @Transient
    private Boolean erroDuranteProcessamento;
    @Transient
    private String mensagemDeErro;

    public ConfigContaDespTipoPessoa() {
        super();
    }

    public ConfigContaDespTipoPessoa(Conta contaDespesa, Exercicio exercicio, TipoPessoaPermitido tipoPessoa) {
        this.contaDespesa = contaDespesa;
        this.exercicio = exercicio;
        this.tipoPessoa = tipoPessoa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoPessoaPermitido getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoaPermitido tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public Boolean getErroDuranteProcessamento() {
        return erroDuranteProcessamento != null ? erroDuranteProcessamento : Boolean.FALSE;
    }

    public void setErroDuranteProcessamento(Boolean erroDuranteProcessamento) {
        this.erroDuranteProcessamento = erroDuranteProcessamento;
    }

    public String getMensagemDeErro() {
        return mensagemDeErro;
    }

    public void setMensagemDeErro(String mensagemDeErro) {
        this.mensagemDeErro = mensagemDeErro;
    }

    public String toString() {
        try {
            return contaDespesa + " - " + tipoPessoa.getDescricao() + " - " + exercicio.getAno();
        } catch (Exception e) {
            return "";
        }

    }
}
