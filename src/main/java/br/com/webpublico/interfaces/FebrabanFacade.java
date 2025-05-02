/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModalidadeConta;

import java.util.List;


/**
 * @author peixe
 */

public interface FebrabanFacade {

    FebrabanHeaderArquivo recuperaFebrabanHeaderArquivo(UnidadeOrganizacional unidade, ContaBancariaEntidade conta);

    List<FebrabanDetalheSegmentoA> recuperaDetalheSegmentoAContaCorrente(HierarquiaOrganizacional hierarquia, FolhaDePagamento folhaPagamento, ModalidadeConta tipoConta, Banco banco, ContratoFP c);

    List<FebrabanDetalheSegmentoA> recuperaDetalheSegmentoOutrosBanco(HierarquiaOrganizacional hierarquia, FolhaDePagamento folhaPagamento, Banco banco, ContratoFP c);
}
