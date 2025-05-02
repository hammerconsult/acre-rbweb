package br.com.webpublico.enums;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by AndreGustavo on 06/06/2014.
 */
public enum TipoCalculoRBTRans {

    /**
     * Gerado para: Frete.
     * Débito para: Permissionário
     * Quando: No momento do cadastro da Permissão
     */
    AUTORIZACAO_FUNCIONAMENTO("Autorização para Funcionamento de Transporte", TipoValor.VALOR, false, TipoRequerenteDoCalculo.PERMISSIONARIO, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete.
     * Débito para: Permissionário
     * Quando: Ao efetuar uma Baixa de Motorista Auxiliar
     */
    BAIXA_MOTORISTA_AUXILIAR("Baixa de Motorista Auxiliar", TipoValor.VALOR, false, TipoRequerenteDoCalculo.PERMISSIONARIO, TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete
     * Débito para: Permissionário
     * Quando: Ao efetuar uma Baixa de Veículo
     */
    BAIXA_VEICULO("Baixa de Veículo", TipoValor.VALOR, false, TipoRequerenteDoCalculo.PERMISSIONARIO, TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete.
     * Débito para: Requerente
     * Quando: Quando não possuem CMC cadastrado.
     */
    CADASTRO_AUTONOMO("Cadastro de Autônomo", TipoValor.VALOR, true, TipoRequerenteDoCalculo.REQUERENTE, TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete
     * Débito para: Permissionário
     * Quando: Ao inserir um Auxiliar.
     */
    INSERCAO_MOTORISTA_AUXILIAR("Inserção de Motorista Auxiliar", TipoValor.VALOR, false, TipoRequerenteDoCalculo.PERMISSIONARIO, TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete.
     * Débito para: Permissionário
     * Quando: Ao inserir um novo veículo.
     */
    INSERCAO_VEICULO("Inserção de Veículo", TipoValor.VALOR, false, TipoRequerenteDoCalculo.PERMISSIONARIO, TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Moto-Táxi
     * Débito para: Permissionário
     * Quando: No cadastro de uma nova Permissão, Gerada 1 vez por Ano no processo de Renovação
     * Obs: Pode ser gerada no processo de Transferência caso a permissão não tenha vencido e a taxa ainda não paga pelo
     * antigo permissionário
     */
    OUTORGA("Outorga", TipoValor.VALOR, false, TipoRequerenteDoCalculo.PERMISSIONARIO, TipoPermissaoRBTrans.MOTO_TAXI), // somente para moto-taxi
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete.
     * Débito para: Permissionário
     * Quando: No início do ano, por lote.
     * Obs: Ela pode ser gerada individualmente caso haja transferência da permissão antes do vencimento.
     */
    RENOVACAO_PERMISSAO("Renovação de Permissão", TipoValor.VALOR, false, TipoRequerenteDoCalculo.PERMISSIONARIO, TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete.
     * Débito para: Permissionário
     * Quando: Quando o Permissionário ou Auxiliar solicita algum serviço, e no momento do cadastro da Permissão de Frete.
     */
    REQUERIMENTO("Requerimento", TipoValor.VALOR, false, TipoRequerenteDoCalculo.PERMISSIONARIO, TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Táxi.
     * Débito para: Permissionário
     * Quando: Ao inserir um novo veículo, na transfêrencia de Permissão com veículos,
     * e na renovação caso o veículo tenha mais de 3 anos
     */
    VISTORIA_VEICULO("Vistoria de Veículo", TipoValor.VALOR, false, TipoRequerenteDoCalculo.PERMISSIONARIO, TipoPermissaoRBTrans.TAXI),
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete.
     * Débito para: Permissionário
     * Quando: Quando solicitado pelo permissionário
     */
    SEGUNDA_VIA_CREDENCIAL_TRAFEGO("Taxa de 2ª Via da Credencial de Tráfego", TipoValor.VALOR, false, TipoRequerenteDoCalculo.PERMISSIONARIO, TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete.
     * Débito para: Requerente
     * Quando: O requerente solicitar;
     */
    SEGUNDA_VIA_CREDENCIAL_TRANSPORTE("Taxa de 2ª Via da Credencial de Transporte", TipoValor.VALOR, false, TipoRequerenteDoCalculo.REQUERENTE, TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete
     * Débito para: Requerente
     * Quando: Solicitado
     */
    SEGUNDA_VIA_DOCUMENTO("Taxa de Emissão de 2ª Via de Documento", TipoValor.VALOR, false, TipoRequerenteDoCalculo.REQUERENTE, TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Táxi e Moto-Táxi
     * Débito para: Novo Permissionário
     * Quando: Quando executada a transferência
     * Obs: Não cobrada quando o motivo da Transferência for por herança
     */
    TRANSFERENCIA_PERMISSAO("Transferência de Permissão", TipoValor.VALOR, false, TipoRequerenteDoCalculo.PERMISSIONARIO, TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI);


    private String descricao;
    private TipoValor tipoValor;
    private Boolean geraIss;
    private TipoRequerenteDoCalculo tipoRequerente;
    private TipoPermissaoRBTrans[] tiposPermissao;

    TipoCalculoRBTRans(String descricao, TipoValor tipoValor, boolean geraIss, TipoRequerenteDoCalculo tipoRequerente, TipoPermissaoRBTrans... tiposPermissao) {
        this.descricao = descricao;
        this.tipoValor = tipoValor;
        this.geraIss = Boolean.valueOf(geraIss);
        this.tiposPermissao = tiposPermissao;
        this.tipoRequerente = tipoRequerente;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoValor getTipoValor() {
        return tipoValor;
    }

    public Boolean getGeraIss() {
        return geraIss;
    }

    public TipoRequerenteDoCalculo getTipoRequerente() {
        return tipoRequerente;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public TipoPermissaoRBTrans[] getTiposPermissao() {
        return tiposPermissao;
    }

    public boolean contains(TipoPermissaoRBTrans tipoPermissao) {
        return Lists.newArrayList(this.tiposPermissao).contains(tipoPermissao);
    }

    public enum TipoValor {
        VALOR("Valor Fixo"),
        PERCENTUAL("Percentual"),
        NENHUM("Nenhum");

        private final String descricao;

        TipoValor(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TipoRequerenteDoCalculo {
        PERMISSIONARIO,
        REQUERENTE
    }

    public static List<String> buscarTipoCalculoRBTransMalaDireta() {
        List<String> toReturn = Lists.newArrayList();
        toReturn.add(TipoCalculoRBTRans.RENOVACAO_PERMISSAO.name());
        toReturn.add(TipoCalculoRBTRans.VISTORIA_VEICULO.name());
        toReturn.add(TipoCalculoRBTRans.OUTORGA.name());
        return toReturn;
    }

    public static String clausulaInTiposCalculoRBTransMalaDireta() {
        List<String> tipos = Lists.newArrayList();
        for (String tipo : buscarTipoCalculoRBTransMalaDireta()) {
            tipos.add("'" + tipo + "'");
        }
        return " in (" + StringUtils.join(tipos, ",") + ") ";
    }
}

