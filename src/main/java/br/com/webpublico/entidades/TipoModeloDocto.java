/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;

import java.io.Serializable;

/**
 * @author brainiac
 */
@GrupoDiagrama(nome = "Licitacao")
public class TipoModeloDocto implements Serializable {

    private String tipoModeloDocumento;

    public TipoModeloDocto() {
    }

    public String getTipoModeloDocumento() {
        return tipoModeloDocumento;
    }

    public enum TagsLicitacao {

        OBJETO("Objeto"),
        NUMERO("Número"),
        NUMEROLICITACAO("Número Da Licitação"),
        TIPOAPURACAO("Tipo de apuração"),
        TIPODEAVALIACAO("Tipo de avaliação"),
        MODALIDADE("Modalidade"),
        EXERCICIOMODALIDADE("Exercicio da Modalidade"),
        PROCEDIMENTO("Natureza do Procedimento"),
        STATUSLICITACAO("Status Licitação"),
        // parecer
        PARECER("Parecer"),
        DATAPARECER("Data Parecer Licitação"),
        NUMEROPARECER("Numero do Parecer"),
        TIPOPARECER("Tipo do Parecer"),
        PESSOAPARECER("Pessoa do Parecer"),
        OBSERVACAOPARECER("Observações do Parecer"),
        //Fornecedores e representante
        FORNECEDOR_REPRESENTANTE("Fornecedor/Representante"),
        //Fornecedores vencedores
        FORNECEDOR_VENCEDOR("Fornecedor Vencedor"),
        // Recurso
        RECURSO("Recurso"),
        DATARECURSO("Data do Recurso"),
        NUMERORECURSO("Numero do Recurso"),
        TIPORECURSO("Tipo do Recurso"),
        INTERPONENTERECURSO("Interponete do Recurso"),
        MOTIVORECURSO("Motivo do Recurso"),
        TIPOJULGAMENTORECURSO("Tipo de Julgamento do Recurso"),
        DATAJULGAMENTORECURSO("Data do Julgamento do Recurso"),
        //
        //Publicação
        PUBLICACAO("Publicação"),
        DATAPUBLICACAO("Data da Publicação"),
        VEICULOPUBLICACAO("Veículo de Publicação"),
        //
        DOCUMENTOSHABILITACAO("Documentos Habilitação"),
        MEMBROSCOMISSAO("Membros da Comissao"),
        PRESIDENTECOMISSAO("Presidente da Comissao"),
        FORNECEDORES("Fornecedores"),
        PROCESSODECOMPRA("Processo de Compra"),
        COMISSAO("Comissao"),
        EXERCICIO("Exercicio"),
        DOCUMENTOS("Documentos"),
        DATADEEMISSAO("Data de emissão"),
        DATADEABERTURA("Data de abertura"),
        DATAJULGAMENTO("Data Julgamento"),
        VALORMAXIMO("Valor Maximo"),
        DATAHOMOLOGACAO("Data Homologação"),
        LOCALENTREGA("Local Entrega"),
        REGIMEEXECUCAO("Regime de Execução"),
        FORMAPAGAMENTO("Forma de Pagamento"),
        PERIODOEXECUCAO("Periodo de Execução"),
        EXCLUSIVOMICRO("Exclusivo Micro/Pequena Empresa"),
        NUMERO_PROCESSO_COMPRA("Número do Processo De Compra"),
        EXERCICIO_PROCESSO_COMPRA("Exercício do Processo De Compra"),
        ITENS("Itens"),
        VALOR_TOTAL("Valor Total"),
        VALOR_MAXIMO("Valor Maxímo"),
        ATO_LEGAL("Ato Legal"),
        // Licitação
        NOMECOMISSAOLICITACAO("Nome da Comissão de Licitação"),
        HORAABERTURALICITACAO("Hora da Abertura da Licitação"),
        SECRETARIAQUESOLICITOULICITACAO("Secretaria que solicitou a Licitação"),
        DOTACOESORCAMENTARIASLICITACAO("Dotações Orçamentarias da Licitação"),
        PREGOEIRO("Pregoeiro"),
        HORARIOABERTURAESCRITAPOREXTENSO("Horário de Abertura Escrita por Extenso"),
        DATAABERTURAPOREXTENSO("Data de Abertura por Extenso"),
        PRAZOVIGENCIA("Prazo de Vigência da Licitação");

        private String descricao;

        private TagsLicitacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

//    public enum TagsContratoDeCompra {
//
//        NUMERO("Número"),
//        DATA("Data"),
//        VALOR_TOTAL("Valor Total");
//        private String descricao;
//
//        public String getDescricao() {
//            return descricao;
//        }
//
//        private TagsContratoDeCompra(String descricao) {
//            this.descricao = descricao;
//        }
//    }
    public enum TagsAtasDaLicitacao {

        MODALIDADE("Modalidade"),
        NUMERO("Número"),
        DATA("Data"),
        EXERCICIO("Exercício"),
        TIPOAPURACAO("Tipo de apuração"),
        TIPODEAVALIACAO("Tipo de avaliação"),
        OBJETO("Objeto"),
        OBSERVACOES("Observações"),
        FORNECEDORES("Fornecedor"),
        REPRESENTANTE("Representante"),
        DOCUMENTOREPRESENTANTE("Documento do Representando"),
        MEMBROSCOMISSAO("Membros da Comissao"),
        ITENS("Itens"),
        NUMERO_PROCESSO_COMPRA("Número do Processo De Compra"),
        EXERCICIO_PROCESSO_COMPRA("Exercício do Processo De Compra"),
        UNIDADERESPONSAVEL_PROCESSO_COMPRA("Unidade Responsável do Processo De Compra"),
        VALOR_TOTAL("Valor Total"),
        VALOR_UNITARIO("Valor Unitario"),
        HORADEABERTURAESCRITAPOREXTENSO("Horário de abertura escrita por extenso"),
        DATADEABERTURAPOREXTENSO("Data de abertura por extenso"),
        PREGOEIRO("Pregoeiro"),
        EQUIPEDEAPOIO("Equipe de apoio"),
        UNIDADEQUESOLICITOUALICITACAO("Unidade que solicitou a licitação"),
        LOCALDAPUBLICACAODOAVISODELICITACAO("Local da publicação do aviso de licitação"),
        DIADAPUBLICACAODOAVISODELICITACAO("Dia da publicação do aviso de licitação"),
        FORNECEDORESPARTICIPANTESCOMREPRESENTANTELEGAL("Fornecedores participantes com Representante legal"),
        RESULTADODOPREGAO("Resultado do pregão (lista de itens com os vencedores)"),
        EMPRESASINABILITADAS("Empresas inabilitadas"),
        EMPRESASHABILITADAS("Empresas habilitadas");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private TagsAtasDaLicitacao(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum TagRecebimentoTermoObra {
        DESCRICAO_SERVICO("Descrição do Serviço"),
        ENDERECO_OBRA("Endereço da Obra"),
        EMPRESA_CONTRATADA("Empresa Contratada"),
        CNPJ_EMPRESA_CONTRATADA("CNPJ da Empresa Contratada"),
        ENDERECO_EMPRESA_CONTRATADA("Endereço da Empresa Contratada"),
        NUMERO_PROCESSO_ADM_CONTRATO("Número do Processo Adminstrativo do Contrato"),
        NUMERO_CONTRATO("Número do Contrato"),
        VALOR_CONTRATO("Valor do Contrato"),
        SECRETARIA_DECLARANTE("Secretaria Contratante"),
        DATA_TERMINO_RECIBIMENTO("Data de Termino do Recebimento"),
        FISCAL_OBRA("Fiscal da Obra"),
        PROFISSAO_FISCAL_OBRA("Profissão do Fiscal da Obra"),
        CREA_FISCAL_OBRA("CREA do Fiscal da Obra"),
        CPF_FISCAL_OBRA("CPF do Fiscal da Obra"),
        NOME_SECRETARIO("Nome do Secretário"),
        RESPONSAVEL_EMPRESA("Responsavel da Empresa"),
        CPF_RESPONSAVEL_EMPRESA("CPF do Responsavel da Empresa"),
        NOME_EMPRESA("Nome da Empresa"),
        NUMERO_LICITACAO("Número da Licitação");

        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        TagRecebimentoTermoObra(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum TagsAvisoDeLicitacao {

        MODALIDADE("Modalidade"),
        OBJETO("Objeto"),
        DATADEABERTURA("Data de abertura"),
        DATADEEMISSAO("Data de emissão"),
        TIPODEAVALIACAO("Tipo de avaliação"),
        NUMERO("Número"),
        PRESIDENTECOMISSAO("Presidente da Comissão"),
        EXERCICIO("Exercicio"),
        VEICULOPUBLICACAO("Veiculo de Publicação"),
        DATA("Data"),
        VALOR_TOTAL("Valor Total"),
        VALOR_MAXIMO("Valor Maxímo"),
        ATO_LEGAL("Ato Legal");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private TagsAvisoDeLicitacao(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum TipoModeloDocumento {

        TAGSLICITACAO("Tags da Licitação"),
        TAGS_ATA_LICITACAO("Tags da Ata da Licitação"),
        TAGSAVISODELICITACAO("Tags de aviso de licitação"),
        TAGSCONTRATODECOMPRA("Tags do Contrato de Compra"),
        TAGSRECEBIMENTOPROVISORIOOBRA("Tags do Termo de Recebimento Provisório da Obra"),
        TAGSRECEBIMENTODEFINITIVOOBRA("Tags do Termo de Recebimento Definitivo da Obra");
        private String descricao;

        private TipoModeloDocumento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
