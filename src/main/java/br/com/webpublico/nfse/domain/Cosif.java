package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.CosifNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "COSIF")
@Audited
@Etiqueta("Conta Cosif")
public class Cosif extends SuperEntidade implements Serializable, NfseEntity {

    public static String MASCARA_VERSAO_1 = "#.#.#.##.##.#";
    public static String MASCARA_VERSAO_2 = "#.##.##.##.##.#";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Conta")
    private String conta;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Etiqueta("Função")
    private String funcao;
    @Obrigatorio
    @Etiqueta("Versão")
    private Integer versao;

    public Cosif() {
        super();
        this.versao = 2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    @Override
    public String toString() {
        return conta + " - " + descricao;
    }

    @Override
    public CosifNfseDTO toNfseDto() {
        return new CosifNfseDTO(id, conta, descricao, funcao);
    }

    public String mascaraPorVersao() {
        return versao != null && versao == 2 ? MASCARA_VERSAO_2 : MASCARA_VERSAO_1;
    }

    public Integer maxLengthMascara() {
        return versao != null && versao == 2 ? 15 : 13;
    }
}
