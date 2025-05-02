package br.com.webpublico.entidades.rh.pccr;

public enum ModoGeracaoProgressao {
        MODO_PADRAO_BASE_ULTIMO_ENQUADRAMENTO("Baseado no último enquadramento elegível para PgA"),
        MODO_DATA_INICIOVIGENCIA_CONTRATO("Baseado na data de nomeação do servidor");
        String descricao;

        ModoGeracaoProgressao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
