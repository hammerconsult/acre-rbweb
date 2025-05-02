/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModalidadeRenunciaLDO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa os valores que não serão recebidos em função de algum benefício
 * concedido pela entidade pública, a qual deverá descrever os detalhes da
 * operação e suas consequências orçamentárias. Usado no processo da LDO (Lei de
 * Diretrizes Orçamentárias).
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity

@Etiqueta("Renúncia Receita LDO")
public class RenunciaReceitaLDO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("LDO")
    private LDO ldo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Beneficiário")
    private String beneficiario;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Modalidade")
    private ModalidadeRenunciaLDO modalidadeRenuncia;
    @Monetario
    @Pesquisavel
    @Etiqueta("Valor")
    private BigDecimal valor;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tributo")
    private String tributo;
    private String compensacao;
    @OneToMany(mappedBy = "renunciaDeReceita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RenunciaReceitaExercicioLDO> renunciaReceitaExercicioLDOs;

    public RenunciaReceitaLDO() {
        this.valor = BigDecimal.ZERO;
        this.renunciaReceitaExercicioLDOs = new ArrayList<>();
    }

    public RenunciaReceitaLDO(BigDecimal valor, String beneficiario, String tributo, String compensacao, ModalidadeRenunciaLDO modalidadeRenuncia, LDO ldo, List<RenunciaReceitaExercicioLDO> renunciaReceitaExercicioLDOs) {
        this.valor = valor;
        this.beneficiario = beneficiario;
        this.tributo = tributo;
        this.compensacao = compensacao;
        this.modalidadeRenuncia = modalidadeRenuncia;
        this.ldo = ldo;
        this.renunciaReceitaExercicioLDOs = renunciaReceitaExercicioLDOs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public ModalidadeRenunciaLDO getModalidadeRenuncia() {
        return modalidadeRenuncia;
    }

    public void setModalidadeRenuncia(ModalidadeRenunciaLDO modalidadeRenuncia) {
        this.modalidadeRenuncia = modalidadeRenuncia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getTributo() {
        return tributo;
    }

    public void setTributo(String tributo) {
        this.tributo = tributo;
    }

    public String getCompensacao() {
        return compensacao;
    }

    public void setCompensacao(String compensacao) {
        this.compensacao = compensacao;
    }

    public List<RenunciaReceitaExercicioLDO> getRenunciaReceitaExercicioLDOs() {
        return renunciaReceitaExercicioLDOs;
    }

    public void setRenunciaReceitaExercicioLDOs(List<RenunciaReceitaExercicioLDO> renunciaReceitaExercicioLDOs) {
        this.renunciaReceitaExercicioLDOs = renunciaReceitaExercicioLDOs;
    }
}
