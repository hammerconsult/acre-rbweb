package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: JULIO-MGA
 * Date: 24/03/14
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public enum TipoPregao  {

     PRESENCIAL("Presencial "),
     PRESENCIAL_COM_REGISTRO_DE_PRECO("Presencial com Registro preço");

private String descricao;


        private TipoPregao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;

        }
}
