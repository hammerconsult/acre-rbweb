package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.DataUtil;

import java.util.Date;

public class ResumoExecucaoOrigemVO {

    public Long id;
    public String numero;
    public Integer ano;
    public Date data;
    public OrigemExecucao origem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public OrigemExecucao getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemExecucao origem) {
        this.origem = origem;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public enum OrigemExecucao {
        CONTRATO("Contrato"),
        ADITIVO_CONTRATO("Aditivo"),
        ADITIVO_ATA("Aditivo"),
        APOSTILAMENTO_CONTRATO("Apostilamento"),
        APOSTILAMENTO_ATA("Apostilamento"),
        DISPENSA_LICITACAO_INEXIGIBILIDADE("Dispensa de Licitação/Inexigibilidade"),
        ATA_REGISTRO_PRECO("Ata Registro de Preço");
        private String descricao;

        OrigemExecucao(String descricao) {
            this.descricao = descricao;
        }

        public boolean isContrato() {
            return CONTRATO.equals(this);
        }

        public boolean isAditivo() {
            return ADITIVO_CONTRATO.equals(this) || ADITIVO_ATA.equals(this);
        }

        public boolean isApostilamento() {
            return APOSTILAMENTO_CONTRATO.equals(this) || APOSTILAMENTO_ATA.equals(this);
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public String getUrlOrigem() {
        String caminho = "";
        switch (origem) {
            case CONTRATO:
                caminho = "/contrato-adm/ver/";
                break;
            case ADITIVO_CONTRATO:
                caminho = "/aditivo-contrato/ver/";
                break;
            case ADITIVO_ATA:
                caminho = "/aditivo-ata-registro-preco/ver/";
                break;
            case APOSTILAMENTO_CONTRATO:
                caminho = "/apostilamento-contrato/ver/";
                break;
            case APOSTILAMENTO_ATA:
                caminho = "/apostilamento-ata-registro-preco/ver/";
                break;
            case ATA_REGISTRO_PRECO:
                caminho = "/ata-registro-preco/ver/";
                break;
            case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                caminho = "/dispensa-licitacao/ver/";
                break;
            default:
                caminho = "";
        }
        return caminho + id + "/";
    }

    public boolean isTipoProcessoNavegavel() {
        return origem.isAditivo() || origem.isApostilamento();
    }

    @Override
    public String toString() {
        if (origem.isContrato()) {
            return origem.getDescricao();
        }
        return origem.getDescricao() + " " + numero + "/" + ano + " - " + DataUtil.getDataFormatada(data);
    }
}
