package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.ModalidadeConta;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;


@Entity
@Audited
@GrupoDiagrama(nome = "Configuração RH")
@Etiqueta("Configurações De Crédito e Salário")
public class ConfiguracaoCreditoSalario extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer sequencialInicial;
    private Boolean validarContaCorrenteContrato;
    @Enumerated(EnumType.STRING)
    private ModalidadeConta modalidadeConta;
    //Quando marcado, será utilizado o novo sistema da caixa econômica federal(NSGD) onde não será utilzado o campo operaçao da conta.
    //Padrão Anterior:
    //Agência + Operação + Conta+DV
    //Ex: 7924.037.99999999-1
    //Novo Padrão – NSGD:
    //Agência + Conta+DV
    //Ex: 7924 / 000.999.999.932-3*
    private Boolean utilizarNovoSistemaCaixa;
    private Boolean permMultCorrentContConjunta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSequencialInicial() {
        return sequencialInicial;
    }

    public void setSequencialInicial(Integer sequencialInicial) {
        this.sequencialInicial = sequencialInicial;
    }

    public Boolean getValidarContaCorrenteContrato() {
        return validarContaCorrenteContrato != null ? validarContaCorrenteContrato : false;
    }

    public void setValidarContaCorrenteContrato(Boolean validarContaCorrenteContrato) {
        this.validarContaCorrenteContrato = validarContaCorrenteContrato;
    }

    public ModalidadeConta getModalidadeConta() {
        return modalidadeConta;
    }

    public void setModalidadeConta(ModalidadeConta modalidadeConta) {
        this.modalidadeConta = modalidadeConta;
    }

    public Boolean getUtilizarNovoSistemaCaixa() {
        return utilizarNovoSistemaCaixa == null ? false : utilizarNovoSistemaCaixa;
    }

    public void setUtilizarNovoSistemaCaixa(Boolean utilizarNovoSistemaCaixa) {
        this.utilizarNovoSistemaCaixa = utilizarNovoSistemaCaixa;
    }

    public Boolean getPermMultCorrentContConjunta() {
        return permMultCorrentContConjunta;
    }

    public void setPermMultCorrentContConjunta(Boolean permMultCorrentContConjunta) {
        this.permMultCorrentContConjunta = permMultCorrentContConjunta;
    }
}
