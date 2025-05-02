/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.ModalidadeContaDTO;

import java.util.Arrays;
import java.util.List;

/**
 * @author boy
 */
public enum ModalidadeConta implements EnumComDescricao {

    CONTA_CORRENTE("Conta Corrente", "01", "01", ModalidadeContaDTO.CONTA_CORRENTE, "1", "01", "00"),
    CONTA_CAIXA_FACIL("Conta Fácil", "01", "23", ModalidadeContaDTO.CONTA_CAIXA_FACIL, "1", "01", "00"),
    CONTA_POUPANCA("Conta Poupança", "05", "13", ModalidadeContaDTO.CONTA_POUPANCA, "2", "05", "51"),
    CONTA_POUPANCA_PESSOA_JURIDICA("Poupança - Pessoa Jurídica", "", "", ModalidadeContaDTO.CONTA_POUPANCA_PESSOA_JURIDICA, "2", "05","00"),
    CONTA_SALARIO("Conta Salário", "", "37", ModalidadeContaDTO.CONTA_SALARIO, "1", "01", "00"),
    CONTA_SALARIO_NSGD("Conta NSGD", "", "00", ModalidadeContaDTO.CONTA_SALARIO_NSGD, "1", "01", "00"),
    ENTIDADES_PUBLICAS("Entidades Públicas", "", "", ModalidadeContaDTO.ENTIDADES_PUBLICAS, " ", "01","00");

    private String descricao;
    private String codigo;
    private String codigoCaixa;
    private ModalidadeContaDTO toDto;
    private String codigoCaixaTED;
    private String formaLancamento;
    private String operacaoBancoBrasil;


    ModalidadeConta(String descricao, String codigo, String codigoCaixa, ModalidadeContaDTO toDto, String codigoCaixaTED, String formaLancamento, String operacaoBancoBrasil) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.codigoCaixa = codigoCaixa;
        this.toDto = toDto;
        this.codigoCaixaTED = codigoCaixaTED;
        this.formaLancamento = formaLancamento;
        this.operacaoBancoBrasil = operacaoBancoBrasil;
    }

    public String getCodigoCaixa() {
        return codigoCaixa;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public ModalidadeContaDTO getToDto() {
        return toDto;
    }

    public static List<ModalidadeConta> getModalidadesPessoaFisica() {
        return Arrays.asList(CONTA_CORRENTE, CONTA_POUPANCA, CONTA_SALARIO, CONTA_SALARIO_NSGD, CONTA_CAIXA_FACIL);
    }

    public String getFormaLancamento() {
        return formaLancamento;
    }

    public String getCodigoCaixaTED() {
        return codigoCaixaTED;
    }

    public String getOperacaoBancoBrasil() {
        return operacaoBancoBrasil;
    }
}
