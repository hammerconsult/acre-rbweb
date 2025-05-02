package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 11/03/14
 * Time: 18:35
 * To change this template use File | Settings | File Templates.
 */
public enum GrauEscolaridadeDependente {
        NAO_INFORMADO("0", "Não Informado"),
        PRIMEIRO_OU_SEGUNDO_GRAU("1", "Estudante 1º ou 2º Grau"),
        UNIVERSITARIO("2", "Estudante Universitário"),
        NAO_ESTUDANTE("3", "Não Estudante");

        private GrauEscolaridadeDependente(String codigo, String descricao) {
            this.codigo = codigo;
            this.descricao = descricao;
        }

        private String codigo;
        private String descricao;

        public String getCodigo() {
            return codigo;
        }

        public String getDescricao() {
            return descricao;
        }
    }
