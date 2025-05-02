package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 14/07/14
 * Time: 18:28
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "ConciliacaoBancaria")
@Etiqueta("Operação da Concilia")
public class Conciliacao extends SuperEntidade {


    //    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//    @Tabelavel
//    @Pesquisavel
//    @Obrigatorio
//    @Etiqueta("Data")
    private Date data;
    //    @Tabelavel
//    @Pesquisavel
//    @Obrigatorio
//    @Etiqueta("Conta Bancária")
    private ContaBancariaEntidade contaBancaria;
    //    @Pesquisavel
//    @Obrigatorio
//    @Tabelavel
//    @ManyToOne
//    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public ContaBancariaEntidade getContaBancaria() {
        return contaBancaria;
    }

    public void setContaBancaria(ContaBancariaEntidade contaBancaria) {
        this.contaBancaria = contaBancaria;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    @Override
    public Long getId() {
        return null;
    }
}
