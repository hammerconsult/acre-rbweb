package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.enums.TipoFolhaDePagamento;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * Created by William on 12/06/2018.
 */
public class RelatorioServidoresCargosVerbasVO {
    private String matricula;
    private String numero;
    private String nome;
    private Date inicioVinculo;
    private String cargo;
    private String descricao;
    private String unidade;
    private Number valorMensal;
    private Number mes;
    private Number ano;
    private Number idVinculo;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private List<RelatorioServidoresCargosVerbasItemVO> item;

    public RelatorioServidoresCargosVerbasVO() {
        item = Lists.newArrayList();
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getInicioVinculo() {
        return inicioVinculo;
    }

    public void setInicioVinculo(Date inicioVinculo) {
        this.inicioVinculo = inicioVinculo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Number getValorMensal() {
        return valorMensal;
    }

    public void setValorMensal(Number valorMensal) {
        this.valorMensal = valorMensal;
    }

    public Number getMes() {
        return mes;
    }

    public void setMes(Number mes) {
        this.mes = mes;
    }

    public Number getAno() {
        return ano;
    }

    public void setAno(Number ano) {
        this.ano = ano;
    }

    public List<RelatorioServidoresCargosVerbasItemVO> getItem() {
        return item;
    }

    public void setItem(List<RelatorioServidoresCargosVerbasItemVO> item) {
        this.item = item;
    }

    public Number getIdVinculo() {
        return idVinculo;
    }

    public void setIdVinculo(Number idVinculo) {
        this.idVinculo = idVinculo;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }
}
