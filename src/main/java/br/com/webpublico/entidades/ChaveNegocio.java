package br.com.webpublico.entidades;

/**
 * @author Daniel
 * @since 27/08/2015 10:52
 */

/**
 * Chave de Negócio é a chave lógica de uma Classe, ou seja, a combinação de atributos de uma classe que a tornam única,
 * com exceção do ID, uma vez que o ID é uma informação interna, sem valor de negócio, criada única e exclusivamente para
 * garantir a unicidade.
 *
 * Para a criação de uma Chave de Negócio, basta criar uma classe que implemente esta Interface, e criar os atributos privados nesta
 * classe que constituem a Chave de Negócio da Entidade escolhida, implementar um construtor que receba todos os atributos,
 * usando para settar o valor o método ChaveNegocioUtil.validarNull quando o valor for obrigatório, e sobrescrever
 * os métodos equals() e hashcode() utilizando todos os campos
 *
 * Não é necessário criar Getters, e não se deve criar os setters, uma vez que a chave deve ser Imutável
 */
public interface ChaveNegocio {

}
