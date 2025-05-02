package br.com.webpublico.ws.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MENSAGENS_RETORNO")
public class WSDadosArrecadacaoSaida {

    /*CODERRO
    01	DAM já se encontra Paga
    02	DAM já se encontra parcelada
    03  DAM já se encontra inscrita em D.A.
    04  DAM já se encontra ajuizada
    05  DAM já possui declaração de Sem Movimento
    06  DAM já se encontra cancelada
    07  DAM já se encontra estornada
    08  Ano de referencia informado inválido
    09  Mês de referencia informado inválido
    10  Código CMC informado não existe
    11	Falha durante o processo
    12	DAM não existente
    13	Data de vencimento informada inválida
    14	Tipo de movimento informado inválido
    15 	Data de movimento informado inválido
    16	Valor de movimento informado inválido
    17	Código da DAM informado fora do intervalo correto ou já existente
    18	Código da DAM informado já existente
    19	Código da DAM informado inexistente
    99	Erro Interno do Servidor
    00	Operação efetuada com sucesso*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Integer CODERRO;
    /*MSGERRO*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private String MSGERRO;

    public WSDadosArrecadacaoSaida() {
    }


    public WSDadosArrecadacaoSaida(Erro codigoDoErro) {
        this.CODERRO = codigoDoErro.getCodigo();
        atribuirMensagem();
    }

    public Integer getCODERRO() {
        return CODERRO;
    }

    public void setCODERRO(Integer CODERRO) {
        this.CODERRO = CODERRO;
        atribuirMensagem();
    }

    public String getMSGERRO() {
        return MSGERRO;
    }

    public void setMSGERRO(String MSGERRO) {
        this.MSGERRO = MSGERRO;
    }


    private void atribuirMensagem() {
        MSGERRO = Erro.getDescricaoPorCodigo(CODERRO);
    }


    public static enum Erro {
        DAM_PAGO(1, "DAM já se encontra Paga"),
        DAM_PARCELADO(2, "DAM já se encontra parcelada"),
        DAM_INSCRITO_DA(3, "DAM já se encontra inscrita em D.A."),
        DAM_AJUIZADO(4, "DAM já se encontra ajuizada"),
        DAM_SEM_MOVIMENTO(5, "DAM já possui declaração de Sem Movimento"),
        DAM_CANCELADO(6, "DAM já se encontra cancelada"),
        DAM_ESTORNADO(7, "DAM já se encontra estornada"),
        ANO_INVALIDO(8, "Ano de referencia informado inválido"),
        MES_INVALIDO(9, "Mês de referencia informado inválido"),
        CMC_NAO_EXISTE(10, "Código CMC informado não existe"),
        FALHA(11, "Falha durante o processo"),
        DAM_NAO_EXISTE(12, "DAM não existente"),
        VENCIMENTO_INVALIDO(13, "Data de vencimento informada inválida"),
        TIPO_MOVIMENTO_INVALIDO(14, "Tipo de movimento informado inválido"),
        DATA_MOVIMENTO_INVALIDO(15, "Data de movimento informado inválido"),
        VALOR_MOVIMENTO_INVALIDO(16, "Valor de movimento informado inválido"),
        CODIGO_DAM_FORA_INTEVALO(17, "Código da DAM informado fora do intervalo correto ou já existente"),
        DAM_JA_EXISTE(18, "Código da DAM informado já existente"),
        CODIGO_DAM_NAO_EXISTE(19, "Código da DAM informado inexistente"),
        ERRO_INTERNO(99, "Erro Interno do Servidor"),
        OPERACAO_EFETUADA(0, "Operação efetuada com sucesso"),;
        private Integer codigo;
        private String descricao;

        Erro(Integer codigo, String descricao) {
            this.codigo = codigo;
            this.descricao = descricao;
        }

        public static String getDescricaoPorCodigo(Integer codigo) {
            for (Erro erro : values()) {
                if (erro.getCodigo() == codigo) {
                    return erro.getDescricao();
                }
            }
            return "";
        }

        public Integer getCodigo() {
            return codigo;
        }

        public String getDescricao() {
            return descricao;
        }
    }

}
