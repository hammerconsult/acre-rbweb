/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author cheles
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Parâmetros de Informações do RBTRANS")
@Table(name = "PARAMINFORMACOESRBTRANS")
public class ParametrosInformacoesRBTrans extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Exercício")
    @Obrigatorio
    private Exercicio exercicio;
    @Etiqueta("Secretaria")
    @Obrigatorio
    private String secretaria;
    @Etiqueta("Secretário")
    @Obrigatorio
    private String secretario;
    @Obrigatorio
    @Etiqueta("Cargo do Secretário")
    private String cargoSecretario;
    @Etiqueta("Decreto de Nomeação")
    private String decretoNomeacao;
    @Obrigatorio
    @Etiqueta("Departamento")
    private String departamento;
    @Obrigatorio
    @Etiqueta("Responsável")
    private String responsavel;
    @Obrigatorio
    @Etiqueta("Cargo do Responsável")
    private String cargoResponsavel;
    @Obrigatorio
    @Etiqueta("CNPJ")
    private String cnpj;
    @Obrigatorio
    @Etiqueta("Email")
    private String email;
    @Obrigatorio
    @Etiqueta("Endereço")
    private String endereco;
    @Obrigatorio
    @Etiqueta("Telefone/Fax")
    private String telFax;
    @Obrigatorio
    @Etiqueta("CEP")
    private String cep;
    @Obrigatorio
    @Etiqueta("Assinatura do Secretário")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCargoResponsavel() {
        return cargoResponsavel;
    }

    public void setCargoResponsavel(String cargoResponsavel) {
        this.cargoResponsavel = cargoResponsavel;
    }

    public String getCargoSecretario() {
        return cargoSecretario;
    }

    public void setCargoSecretario(String cargoSecretario) {
        this.cargoSecretario = cargoSecretario;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(String secretaria) {
        this.secretaria = secretaria;
    }

    public String getSecretario() {
        return secretario;
    }

    public void setSecretario(String secretario) {
        this.secretario = secretario;
    }

    public String getTelFax() {
        return telFax;
    }

    public void setTelFax(String telFax) {
        this.telFax = telFax;
    }

    public String getDecretoNomeacao() {
        return decretoNomeacao;
    }

    public void setDecretoNomeacao(String decretoNomeacao) {
        this.decretoNomeacao = decretoNomeacao;
    }
}
