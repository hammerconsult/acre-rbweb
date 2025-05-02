package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Audited
public class ParametroCobrancaSPC extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private Boolean debitoExercicio;
    private Boolean debitoDividaAtiva;
    private Boolean debitoDividaAtivaProtestada;
    private Boolean debitoDividaAtivaAjuizada;
    private Boolean pessoaFisica;
    private Boolean pessoaJuridica;
    @Etiqueta("Código da Natureza")
    @Obrigatorio
    private Long codigoNatureza;
    @Etiqueta("Código do Tipo de Devedor")
    @Obrigatorio
    private String codigoTipoDevedor;
    @Etiqueta("Código do Motivo de Exclusão")
    private Long codigoMotivoExclusao;

    public ParametroCobrancaSPC() {
        super();
        this.debitoExercicio = Boolean.FALSE;
        this.debitoDividaAtiva = Boolean.FALSE;
        this.debitoDividaAtivaProtestada = Boolean.FALSE;
        this.debitoDividaAtivaAjuizada = Boolean.FALSE;
        this.pessoaFisica = Boolean.FALSE;
        this.pessoaJuridica = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDebitoExercicio() {
        return debitoExercicio;
    }

    public void setDebitoExercicio(Boolean debitoExercicio) {
        this.debitoExercicio = debitoExercicio;
    }

    public Boolean getDebitoDividaAtiva() {
        return debitoDividaAtiva;
    }

    public void setDebitoDividaAtiva(Boolean debitoDividaAtiva) {
        this.debitoDividaAtiva = debitoDividaAtiva;
    }

    public Boolean getDebitoDividaAtivaProtestada() {
        return debitoDividaAtivaProtestada;
    }

    public void setDebitoDividaAtivaProtestada(Boolean debitoDividaAtivaProtestada) {
        this.debitoDividaAtivaProtestada = debitoDividaAtivaProtestada;
    }

    public Boolean getDebitoDividaAtivaAjuizada() {
        return debitoDividaAtivaAjuizada;
    }

    public void setDebitoDividaAtivaAjuizada(Boolean debitoDividaAtivaAjuizada) {
        this.debitoDividaAtivaAjuizada = debitoDividaAtivaAjuizada;
    }

    public Boolean getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(Boolean pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Boolean getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(Boolean pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public Long getCodigoNatureza() {
        return codigoNatureza;
    }

    public void setCodigoNatureza(Long codigoNatureza) {
        this.codigoNatureza = codigoNatureza;
    }

    public String getCodigoTipoDevedor() {
        return codigoTipoDevedor;
    }

    public void setCodigoTipoDevedor(String codigoTipoDevedor) {
        this.codigoTipoDevedor = codigoTipoDevedor;
    }

    public Long getCodigoMotivoExclusao() {
        return codigoMotivoExclusao;
    }

    public void setCodigoMotivoExclusao(Long codigoMotivoExclusao) {
        this.codigoMotivoExclusao = codigoMotivoExclusao;
    }
}
