package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;

import java.io.Serializable;

/**
 * @author leonardo
 */
@GrupoDiagrama(nome = "Certidao")
public class TipoModeloDoctoOficial implements Serializable {

    private String tipoModeloDocumento;

    public TipoModeloDoctoOficial() {
    }

    public String getTipoModeloDocumento() {
        return tipoModeloDocumento;
    }

    public enum TagsConfiguracaoDocumento {

        USUARIO("Usuário"),
        UNIDADE_LOGADA_ADM("Unidade Logada - Administrativa"),
        UNIDADE_LOGADA_ORC("Unidade Logada - Orçamentaria"),
        EXERCICIO_LOGADO("Exercício"),
        DATA_LOGADA("Data Logada"),
        QUEBRA_PAGINA("Quebra Página"),
        IP("IP");
        String descricao;

        private TagsConfiguracaoDocumento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsDataHoje {

        DATA_HOJE_ANO("Data Hoje Ano"),
        DATA_HOJE_MES("Data Hoje Mês"),
        DATA_HOJE_DIA("Data Hoje Dia"),
        DATA_HOJE_DIA_EXTENSO("Data Hoje Dia Extenso"),
        DATA_HOJE_MES_EXTENSO("Data Hoje Mês Extenso"),
        DATA_HOJE_ANO_EXTENSO("Data Hoje Ano Extenso"),
        DATA_HOJE_POR_EXTENSO("Data Hoje por Extenso"),
        ;
        private String descricao;

        private TagsDataHoje(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsCadastroImobiliario {

        CADASTRO_IMOBILIARIO("Cadastro Imobiliário"),
        INSCRICAO_CADASTRAL("Inscrição Cadastral"),
        PROPRIETARIO("Proprietário"),
        CPF_CNPJ_PROPRIETARIO("CPF/CNPJ do Proprietário"),
        PROPRIETARIOS("Proprietários"),
        LOGRADOURO("Logradouro"),
        NUMERO_LOGRADOURO("Número"),
        BAIRRO("Bairro"),
        CIDADE("Cidade"),
        UF("UF"),
        LOTEAMENTO("Loteamento"),
        COMPLEMENTO("Complemento"),
        QUADRA("Quadra"),
        LOTE("Lote"),
        AREA_DO_TERRENO("Área do Terreno"),
        TESTADA_PRINCIPAL("Testada Principal"),
        LOGRADOURO_PRINCIPAL("Logradouro Principal"),
        AREA_CONSTRUIDA("Área Construída"),
        VALOR_VENAL("Valor Venal"),
        ENDERECO_CORRESPONDENCIA("Endereço de Correspondência"),
        CEP("CEP"),
        TIPO_IMOVEL("Tipo do Imóvel"),
        CARACTERISTICA_CONSTRUCAO("Característica da Construção"),
        LADO_FACE("Lado da Face"),
        LARGURA_FACE("Largura da Face"),
        COMPROMISSARIO("Compromissário"),
        CPF_CNPJ_COMPROMISSARIO("CPF/CNPJ do Compromissário"),
        COMPROMISSARIOS("Compromissários"),
        NOME_DO_LOTEAMENTO("Nome do Loteamento"),
        LOTE_DO_LOTEAMENTO("Lote do Loteamento"),
        QUADRA_DO_LOTEAMENTO("Quadra do Loteamento"),
        TESTADA_EM_METROS("Testada(M)");

        private String descricao;

        private TagsCadastroImobiliario(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsCadastroEconomico {

        CMC("C.M.C."),
        SITUACAO_CADASTRAL("Situação Cadastral"),
        DATA_ABERTURA("Data de Abertura"),
        RAZAO_SOCIAL("Razão Social"),
        CPF_CNPJ("CPF/CNPJ"),
        INSCRICAO_ESTADUAL("Inscrição Estadual"),
        NOME_FANTASIA("Nome Fantasia"),

        TIPO_LOGRADOURO_BCE_LOCALIZACAO("Tipo Endereço de Localização do Cadastro Econômico"),
        LOGRADOURO_BCE_LOCALIZACAO("Logradouro de Localização do Cadastro Econômico"),
        BAIRRO_BCE_LOCALIZACAO("Bairro de Localização do Cadastro Econômico"),
        NUMERO_BCE_LOCALIZACAO("Numero de Localização do Cadastro Econômico"),
        COMPLEMENTO_BCE_LOCALIZACAO("Complemento de Localização do Cadastro Econômico"),

        TIPO_LOGRADOURO_BCE_CORRESPONDENCIA("Tipo Endereço de Correspondência do Cadastro Econômico"),
        LOGRADOURO_BCE_CORRESPONDENCIA("Logradouro de Correspondência do Cadastro Econômico"),
        BAIRRO_BCE_CORRESPONDENCIA("Bairro de Correspondência do Cadastro Econômico"),
        NUMERO_BCE_CORRESPONDENCIA("Numero de Correspondência do Cadastro Econômico"),
        COMPLEMENTO_BCE_CORRESPONDENCIA("Complemento de Correspondência do Cadastro Econômico"),


        TIPO_LOGRADOURO_PESSOA("Tipo Endereço da Pessoa"),
        LOGRADOURO_PESSOA("Logradouro do endereço da Pessoa"),
        COMPLEMENTO_PESSOA("Complemento de endereço da Pessoa"),
        NUMERO_PESSOA("Número do endereço da Pessoa"),
        BAIRRO_PESSOA("Bairro do endereço da Pessoa"),
        CIDADE_PESSOA("Cidade do endereço da Pessoa"),
        UF_PESSOA("UF do endereço da Pessoa"),
        CEP_PESSOA("CEP do endereço da Pessoa"),
        TELEFONES_PESSOA("Telefone(s) da Pessoa"),

        INSCRICAO_CADASTRAL_BCI("Inscrição Cadastral do Cadastro Imobiliário"),
        LOGRADOURO_BCI("Logradouro do Cadastro Imobiliário"),
        NUMERO_BCI("Número Endereço do Cadastro Imobiliário"),
        COMPLEMENTO_BCI("Complemento do endereço do Cadastro Imobiliário"),
        BAIRRO_BCI("Bairro do Cadastro Imobiliário"),
        //        CIDADE_BCI("Cidade do Cadastro Imobiliário"),
//        UF_BCI("UF do Cadastro Imobiliário"),
        QUADRA_BCI("Quadra do Cadastro Imobiliário"),
        LOTE_BCI("Lote do Cadastro Imobiliário"),
        ATIVIDADE_ECONOMICA("Atividade Econômica"),
        CLASSIFICACAO("Classificação"),
        AREA_UTILIZACAO("Área de Utilização"),
        ENDERECO_CORRESPONDENCIA("Endereço de Correspondência"),
        PORTE("Porte"),
        CNAE_PRIMARIO("CNAE Primário"),
        CNAES_SEGUNDARIOS("CNAEs Secundários"),
        HORARIOS_FUNCIONAMENTO("Horários de Funcionamento"),
        NATUREZA_JURIDICA("Natureza Jurídica");
        private String descricao;

        private TagsCadastroEconomico(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsCadastroRural {

        CADASTRO_RURAL("Cadastro Rural"),
        INCRA("Incra"),
        PROPRIETARIO("Proprietário"),
        CPF_CNPJ("CPF/CNPJ"),
        NOME_PROPRIEDADE("Nome da Propriedade"),
        LOCALIZACAO("Localização"),
        AREA("Área"),
        TIPO_AREA("Tipo da Área"),
        VALOR_VENAL("Valor Venal"),
        ENDERECO_CORRESPONDENCIA("Endereço de Correspondência");
        private String descricao;

        private TagsCadastroRural(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsPessoaFisica {

        CONTRIBUINTE("Contribuinte"),
        CPF("CPF"),
        RG_INSC_ESTADUAL("RG/Inscrição Estadual"),
        LOGRADOURO("Logradouro"),
        NUMERO("Número Endereço"),
        BAIRRO("Bairro"),
        CIDADE("Cidade"),
        UF("UF"),
        COMPLEMENTO("Complemento"),
        CEP("CEP"),
        EMAIL("e-mail"),
        TELEFONE("Telefone"),
        ENDERECO_CORRESPONDENCIA("Endereço de Correspondência"),
        ENDERECO_DOMICILIOFISCAL("Endereço de Domicílio Fiscal");
        private String descricao;

        private TagsPessoaFisica(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsPessoaJuridica {

        NOMEFANTASIA("Nome Fantasia"),
        RAZAO_SOCIAL("Razão Social"),
        CNPJ("CNPJ"),
        LOGRADOURO("Logradouro"),
        NUMERO("Número Endereço"),
        BAIRRO("Bairro"),
        CIDADE("Cidade"),
        COMPLEMENTO("Complemento"),
        UF("UF"),
        CEP("CEP"),
        TELEFONE("Telefone"),
        ENDERECO_CORRESPONDENCIA("Endereço de Correspondência");
        private String descricao;

        private TagsPessoaJuridica(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsFiscalizacao {

        ORDEM_SERVICO("Ordem de Serviço"),
        PROGRAMACAO_FISCAL("Programação Fiscal"),
        DATA_INICIAL_FISCAL("Data Inicial do Fiscal"),
        DATA_FINAL_FISCAL("Data Final do Fiscal"),
        DATA_INICIAL_FISCALIZACAO("Data Inicial de Fiscalização"),
        DATA_FINAL_FISCALIZACAO("Data Final de Fiscalização"),
        NUMERO_DIAS_PROGRAMACAO("Número de dias da Programação"),
        NUMERO_DIAS_APRESENTACAO_DOCTO("Número de dias para Apresentação de Documentos"),
        DATA("Data Atual"),
        DIA_ATUAL("Dia Atual"),
        MES_ATUAL("Mês Atual"),
        ANO_ATUAL("Ano Atual"),
        AGENTES_FISCAIS("Agentes Fiscais"),
        AGENTES_FISCAIS_MATRICULA("Agentes Fiscais com Matrícula"),
        NUMERO_AUTOINFRACAO("Número do Auto de Infração"),
        LISTA_LANCAMENTOS("Lista de Lançamentos Contábeis"),
        LISTA_MULTAS("Lista de Multas"),
        LISTA_EMBASAMENTO_MULTAS("Lista de Embasamento das Multas"),
        TEXTO_CONCLUSAO("Texto de Conclusão"),
        LISTA_LEVANTAMENTO_CREDITO_TRIBUTARIO("Lista de Levamentamento de Crédito Tributário"),
        DATA_PROGRAMACAO("Data da Programação"),
        LISTA_CNAE("Lista de CNAE e Descrição"),
        DATA_TERMO_FISCALIZACAO("Data do Termo de Fiscalizacao"),
        NOME_DIRETOR_DEPTO("Nome do Diretor do Departamento"),
        NOME_SECRETARIO("Nome do Secretario"),
        VALOR_AUTO_UFM("Valor do Auto em UFM"),
        VALOR_AUTO_REAIS("Valor do Auto em Reais"),
        NUMERO_TERMO_FISCALIZACAO("Numero do termo de Fiscalização"),
        NUMERO_LEVANTAMENTO("Número do Levantamento"),
        FUNDAMENTACAO("Fundamentação"),
        HISTORICOFISCAL("Histórico Fiscal"),
        VALOR_TOTAL_PENALIDADES("Valor Total das Penalidades"),
        VALOR_TOTAL_ISSQN_CORRIGIDO("Valor Total do ISSQN Corrigido"),
        VALOR_TOTAL_JUROS_MORA("Valor Total dos Juros de Mora"),
        VALOR_TOTAL_MULTA_MORA("Valor Total da Multa de Mora"),
        VALOR_TOTAL_GERAL("Valor Total Geral"),
        ASSINATURAS_FISCAIS("Assinatura dos Agentes Fiscais"),
        ASSINATURAS_FISCAIS_MATRICULA("Assinatura dos Agentes Fiscais com Matrícula");
        private String descricao;

        private TagsFiscalizacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsGerais {

        VALORNORMALVENCIDO("Valor Normal Vencido"),
        VALORNORMALVENCER("Valor Normal Vencer"),
        VALORPARCELAMENTOVENCIDO("Valor Parcelamento Vencido"),
        VALORPARCELAMENTOVENCER("Valor Parcelamento Vencer"),
        VALORNORMALVENCIDOACRESCIMOS("Valor Normal Vencido Acréscimos"),
        VALORPARCELAMENTOVENCIDOACRESCIMOS("Valor Parcelamento Vencido Acréscimos"),
        VALORNORMALVENCIDOPOREXTENSO("Valor Normal Vencido Por Extenso"),
        VALORNORMALVENCERPOREXTENSO("Valor Normal Vencer Por Extenso"),
        VALORPARCELAMENTOVENCIDOPOREXTENSO("Valor Parcelamento Vencido Por Extenso"),
        VALORPARCELAMENTOVENCERPOREXTENSO("Valor Parcelamento Vencer Por Extenso"),
        VALORNORMALVENCIDOACRESCIMOSPOREXTENSO("Valor Normal Vencido Acréscimos Por Extenso"),
        VALORPARCELAMENTOVENCIDOACRESCIMOSPOREXTENSO("Valor Parcelamento Vencido Acréscimos Por Extenso"),
        NOME_RESPONSAVEL("Nome do Responsável"),
        MATRICULA_RESPONSAVEL("Matrícula do Responsável"),
        ASSINATURA_RESPONSAVEL("Assinatura do Responsável"),
        FINALIDADE("Finalidade"),
        QRCODE("QrCode"),
        URL_AUTENTICIDADE_DOCUMENTOS("URL Autenticidade de Documentos");
        private String descricao;

        private TagsGerais(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsCertidaoBensImoveis {
        LISTA_IMOVEIS("Lista de Imóveis");
        private String descricao;

        private TagsCertidaoBensImoveis(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsGeraisSemValidacao {

        NUMERO_DOCUMENTO("Número do Documento"),
        ANO_DOCUMENTO("Ano do Documento"),
        DATA_EMISSAO("Data de Emissão"),
        DATA_VALIDADE("Data de Validade"),
        DIAS_VALIDADE("Dias de Validade"),
        CODIGO_VERIFICACAO("Código de Verificação"),
        OBSERVACAO("Observação"),
        QRCODE("QrCode");
        private String descricao;

        private TagsGeraisSemValidacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsTermoAdvertencia {
        LISTA_INFRACAO("Lista de infrações");
        private String descricao;

        private TagsTermoAdvertencia(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsDividaAtiva {

        NUMERO("Número da CDA"),
        EXERCICIO("Exercício da CDA"),
        NUMERO_AUTO_INFRACAO("N° do Auto de Infração/Ano"),
        PROTOCOLO_ACAO_FISCAL("Protocolo/Ano"),
        DATA_CDA("Data da CDA"),
        TABELA_DE_PARCELA("Tabela de Parcelas"),
        DATA_HOJE("Data de Hoje"),
        ASSINATURA("Assinatura"),
        NUMERO_LIVRO("Número do Livro"),
        ORIGEM_DIVIDA("Origem da dívida"),
        VALOR_TOTAL("Valor Total"),
        VALOR_EXTENSO("Valor por extenso");
        private String descricao;

        private TagsDividaAtiva(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsTermoDividaAtiva {

        NUMERO_TERMO("Número do Termo"),
        DATA_INSCRICAO("Data de inscrição"),
        NUMERO_LIVRO("Número do Livro"),
        PAGINA_LIVRO("Página do Livro"),
        ORIGEM_DIVIDA("Origem da Dívida"),
        DISCRIMINACAO_VALORES("Discriminação de valores");
        private String descricao;

        private TagsTermoDividaAtiva(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsPeticaoDividaAtiva {

        NUMERO("Número"),
        EXERCICIO("Exercício"),
        TABELA_DE_CADASTRO("Tabela de cadastro"),
        TABELA_DE_COMPROMISSARIO("Tabela de Compromissário"),
        TABELA_DE_CDA("Tabela de CDA(s)"),
        DATA_HOJE("Data de Hoje"),
        ASSINATURA("Assinatura"),
        PROCURADOR_NOME("Nome do Procurador"),
        PROCURADOR_OAB("OAB do Procurador"),
        VALOR_TOTAL_PETICAO("Valor Total da Petição"),
        VALOR_TOTAL_PETICAO_EX("Valor Total da Petição por extenso");
        private String descricao;

        private TagsPeticaoDividaAtiva(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsRBTrans {
        // Informações gerais

        DIA_EXTENSO("Dia por extenso"),
        MES_EXTENSO("Mês por extenso"),
        ANO_EXTENSO("Ano por extenso"),
        DATA_EMISSAO_TERMO("Data de Emissão do Termo"),
        DATA_TERMO_ANO("Ano da Data de Emissão do Termo"),
        DATA_TERMO_DIA_EXTENSO("Dia por extenso da Data de Emissão do Termo"),
        DATA_TERMO_DIA("Dia da Data de Emissão do Termo"),
        DATA_TERMO_MES_EXTENSO("Mês por extenso da Data de Emissão do Termo"),
        CNPJ_MUNICIPIO("CNPJ do Município"),
        EMAIL("Email"),
        ENDERECO("Endereço"),
        TELEFONEFAX("Telefone/Fax"),
        CEP("CEP"),
        NOME_SECRETARIA("Nome da Secretaria"),
        NOME_SECRETARIO("Nome do Secretário"),
        CARGO_SECRETARIO("Cargo do Secretário"),
        DECRETO_NOMEACAO_SECRETARIO("Decreto de Nomeação do Secretário"),
        DEPARTAMENTO("Departamento"),
        NOME_RESPONSAVEL("Nome do Responsável"),
        CARGO_RESPONSAVEL("Cargo do Responsável"),
        // Permissão
        NUMERO_PERMISSAO("Número da Permissão"),
        DATA_POR_EXTENSO("Data por Extenso"),
        ASSINATURA_DO_SUPERINTENDENTE("Assinatura do Superintendente "),
        // Permissionário
        NOME_PERMISSIONARIO("Nome do Permissionário"),
        LOGRADOURO_PERMISSIONARIO("Logradouro do Permissionário"),
        BAIRRO_PERMISSIONARIO("Bairro do Permissionário"),
        CPF_PERMISSIONARIO("Número do CPF do Permissionário"),
        RG_PERMISSIONARIO("Número do RG do Permissionário"),
        // Motorista Auxiliar
        NOME_MOTORISTA("Nome do Motorista Auxiliar"),
        LOGRADOURO_MOTORISTA("Logradouro do Motorista"),
        BAIRRO_MOTORISTA("Bairro do Motorista"),
        CPF_MOTORISTA("Número do CPF do Motorista Auxiliar"),
        RG_MOTORISTA("Número do RG do Motorista Auxiliar"),
        // Veículo
        PLACA("Placa Veículo"),
        CHASSI("Chassi Veículo"),
        ANO_FABRICACAO("Ano Fabricação do Veículo"),
        ANO_MODELO("Ano Modelo do Veículo"),
        MARCA("Marca do Veículo"),
        MODELO("Modelo"),
        COR("Cor do Veículo"),
        ESPECIE("Espécie do Veículo"),
        TIPO_VEICULO("Tipo de Veículo"),
        NOTA_FISCAL("Nota Fiscal do Veículo"),
        // Veículo Baixado
        VEICULO_BAIXADO_PLACA("Placa do Veículo Baixado"),
        VEICULO_BAIXADO_CHASSI("Chassi do Veículo Baixado"),
        VEICULO_BAIXADO_ANO_FABRICACAO("Ano Fabricação do Veículo Baixado"),
        VEICULO_BAIXADO_ANO_MODELO("Ano Modelo do Veículo Baixado"),
        VEICULO_BAIXADO_MARCA("Marca do Veículo Baixado"),
        VEICULO_BAIXADO_MODELO("Modelo do Veículo Baixado"),
        VEICULO_BAIXADO_COR("Cor do Veículo Baixado"),
        VEICULO_BAIXADO_ESPECIE("Espécie do Veículo Baixado"),
        VEICULO_BAIXADO_TIPO_VEICULO("Tipo de Veículo Baixado"),
        VEICULO_BAIXADO_NOTA_FISCAL("Nota Fiscal do Veículo Baixado");
        private String descricao;

        private TagsRBTrans(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsCertificadoOTTRBTrans {

        EXERCICIO("Ano do Certificado"),
        RAZAO_SOCIAL_OTT("Razão Social da OTT"),
        CNPJ_OTT("Cnpj da OTT"),
        NOME_REPRESENTANTE("Nome do Representante"),
        DATA_EMISSAO("Data de Emissão"),
        HORA_EMISSAO("Hora da Emissão"),
        VENCIMENTO("Vencimento"),
        QR_CODE("QR Code");


        private String descricao;

        private TagsCertificadoOTTRBTrans(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsCondutorOTTRBTrans {

        VENCIMENTO_CERTIFICADO_CONDUTOR("Vencimento do certificado do condutor"),
        VENCIMENTO_CERTIFICADO_VEICULO("Vencimento do certificado do veiculo"),
        NOME_CONDUTOR("Nome do Condutor da OTT"),
        CPF_CONDUTOR("CPF do Condutor da OTT"),
        MARCA_VEICULO("Marca do Veículo"),
        MODELO_VEICULO("Modelo do Veículo"),
        PLACA_VEICULO("Placa do Veículo"),
        RAZAO_SOCIAL_OTT("Razão Social da OTT"),
        DATA_EMISSAO("Data de Emissão"),
        HORA_EMISSAO("Hora da Emissão"),
        VEICULO_ALUGADO("Veiculo alugado"),
        QR_CODE("QR Code");

        private String descricao;

        private TagsCondutorOTTRBTrans(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsTermoDeAdvertencia {

        NUMERO("Número"),
        DATA_LAVRATURA("Data da Lavratura"),
        USUARIO("Usuário"),
        DATA_CIENCIA("Data de Ciência"),
        NOME_PESSOA_CIENCIA("Nome da Pessoa Ciência"),
        DESCRICAO_INFRACAO("Descrição da infração"),
        LISTA_INFRACAO("Lista de infração"),
        ENCERRAMENTO_TERMO("Encerramento do termo");
        private String descricao;

        private TagsTermoDeAdvertencia(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsAutoInfracaoFiscalizacao {

        NUMERO("Número"),
        NUMERO_FORMULARIO("Número do Formulário"),
        DATA_LAVRATURA("Data da Lavratura"),
        USUARIO("Usuário"),
        DATA_CIENCIA("Data de Ciência"),
        DATA_INFRACAO("Data da Infração"),
        NOME_PESSOA_CIENCIA("Nome da Pessoa Ciência"),
        DESCRICAO_INFRACAO("Descrição da infração"),
        ENCERRAMENTO_AUTO("Encerramento do auto"),
        PRAZO_RECURSO("Prazo para Recurso"),
        LISTA_INFRACAO("Lista de infração"),
        ENDERECO("Endereço"),
        NUMERO_ENDERECO("Número do endereço"),
        BAIRRO("Bairro"),
        PONTO_REFERENCIA("Ponto de Referência"),
        VALOR_TOTAL_PENALIDADES_UFM("Valor (UFM)"),
        VALOR_TOTAL_PENALIDADES_RS("Valor (R$)");
        private String descricao;

        private TagsAutoInfracaoFiscalizacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsFiscalizacaoRBTrans {

        NUMERO_NOTIFICACAO("Número da Notificação"),
        NOME_PERMISSIONARIO("Nome do Pemissionário"),
        CPF_CNPJ_PERMISSIONARIO("CPF/CNPJ do Permissionário"),
        ENDERECO_PERMISSIONARIO("Endereço do Permissionário"),
        VEICULO_MARCA_MODELO("Marca e Modelo do Veículo"),
        PLACA_VEICULO("Placa do Veículo"),
        NUMERO_PERMISSAO("Número da Pemissão"),
        DATA_EMISSAO_NOTIFICACAO_AUTUACAO("Data de Emissão da Notificação"),
        VALOR_MULTA("Valor da Multa"),
        NUMERO_AUTUACAO("Número da Autuação"),
        DATA_AUTUACAO("Data da Autuação"),
        HORA_AUTUACAO("Hora da Autuação"),
        LOCAL_AUTUACAO("Local da Autuação"),
        INFRACOES("Infrações"),
        FISCAL_AUTUADOR("Fiscal Autuador"),
        NOME_MOTORISTA_INFRATOR("Nome do Motorista Infrator"),
        CPF_MOTORISTA_INFRATOR("CPF do Motorista Infrator"),
        ENDERECO_MOTORISTA_INFRATOR("Endereço do Motorista Infrator"),
        DESCRICAO_DETALHADA("Descrição Detalhada"),
        NUMERO_SEQUENCIAL_DOCUMENTO("Número Sequencial do Documento"),
        DATA_NASCIMENTO_PERMISSIONARIO("Data de Nascimento do Permissionário "),
        DATA_NASCIMENTO_MOTORISTAAUX("Data de Nascimento do Motorista Auxiliar 1"),
        DATA_NASCIMENTO_MOTORISTAAUX1("Data de Nascimento do Motorista Auxiliar 2"),
        TIPO_TRANSPORTE("Tipo de Transporte");
        private String descricao;

        private TagsFiscalizacaoRBTrans(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsParecerRecursoFiscalizacao {

        TIPO_RECURSO("Tipo de Recurso"),
        DATA_ENTRADA("Data"),
        TEOR("Teor"),
        PARECER("PARECER"),
        RESULTADO_PARECER("RESULTADO DO PARECER"),
        DATA_PARECER("Data do parecer"),
        DATA_NOVO_PRAZO("Data do novo prazo");
        private String descricao;

        private TagsParecerRecursoFiscalizacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsProtocolo {

        UNIDADE_ORGANIZACIONAL_RESP("Unidade Organizacional Responsável"),
        NUMERO_PROCESSO("Número do Processo"),
        DATA_PROCESSO("Data do Processo"),
        NOME_REQUERENTE("Nome do Requerente"),
        CPF_CNPJ_REQUERENTE("CPF/CNPJ do Requerente"),
        ENDERECO_REQUERENTE("Endereço do Requerente"),
        ASSUNTO("Assunto"),
        SUB_ASSUNTO("Subassunto"),
        TRAMITES_PROCESSO("Tramites do Processo"),
        NUMERO_PARECER("Número do Parecer"),
        DATA_PARECER("Data do Parecer"),
        RESPONSAVEL_PARECER("Responsável pelo Parecer"),
        TEXTO_PARECER("Texto do Parecer"),
        UNIDADE_DESTINO("Unidade de Destino"),
        SITUACAO("Situação"),
        SENHA_CONSULTA("Senha para Consulta");
        private String descricao;

        private TagsProtocolo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsContratoRendasPatrimoniais {

        NUMERO_CONTRATO("Número do Contrato"),
        PONTOS_COMERCIAIS_RESUMO("pontos Comerciais Resumido"),
        PONTOS_COMERCIAIS_DETALHADA("pontos Comerciais Detalhados"),
        PONTOS_COMERCIAIS_VALORES("pontos Comerciais Valores"),
        VIGENCIA_CONTRATO("Vigência"),
        DIA_DATA_CONTRATO("Dia da Data do Contrato"),
        MES_DATA_CONTRATO("Mês da Data do Contrato"),
        ANO_DATA_CONTRATO("Ano da Data do Contrato"),
        DATA_CONTRATO("Data do Contrato"),
        DATA_CONTRATO_EXTENSO("Data do Contrato Por Extenso"),
        ATIVIDADE_CONTRATO("Atividade do Ponto Comercial"),
        NOME_SECRETARIA("Nome da Secretaria"),
        CNPJ_LOCALIZACAO("CNPJ da Secretaria"),
        LOGRADOURO_LOCALIZACAO("Logradouro da Secretaria"),
        NUMERO_LOCALIZACAO("Número do Logradouro da Secretaria"),
        BAIRRO_LOCALIZACAO("Bairro da Secretaria"),
        CEP_LOCALIZACAO("CEP da Secretaria"),
        NOME_PROCURADOR_LOCALIZACAO("Nome do Procurador"),
        NOME_REPRESENTANTE_LOCALIZACAO("Nome do Representante"),
        RG_PROCURADOR_LOCALIZACAO("RG, Órgão Emissor do RG e UF do RG do Procurador"),
        RG_REPRESENTANTE_LOCALIZACAO("RG, Órgão Emissor do RG e UF do RG do Representante"),
        CPF_PROCURADOR_LOCALIZACAO("CPF do Procurador"),
        CPF_REPRESENTANTE_LOCALIZACAO("CPF do Representante"),
        CARGO_PROCURADOR_LOCALIZACAO("Cargo do Procurador"),
        CARGO_REPRESENTANTE_LOCALIZACAO("Cargo do Representante"),
        DECRETO_LOCALIZACAO("Decreto"),
        PORTARIA_LOCALIZACAO("Portaria"),
        DESCRICAO_UNIDADE_ORGANIZACIONAL("Descrição da Unidade Organizacional");
        private String descricao;

        private TagsContratoRendasPatrimoniais(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsContratoCEASA {

        NUMERO_CONTRATO("Número do Contrato"),
        VIGENCIA_CONTRATO("Vigência"),
        DIA_DATA_CONTRATO("Dia da Data do Contrato"),
        MES_DATA_CONTRATO("Mês da Data do Contrato"),
        ANO_DATA_CONTRATO("Ano da Data do Contrato"),
        DATA_CONTRATO("Data do Contrato"),
        DATA_CONTRATO_EXTENSO("Data do Contrato Por Extenso"),
        ATIVIDADE_CONTRATO("Atividade do Ponto Comercial"),
        NUMERO_BOXES_CONTRATO("Número dos Boxes"),
        AREA_BOXES_CONTRATO("Área Total dos Boxes"),
        VALOR_CONTRATO_METRO_QUADRADO("Valor Total do Contrato Por Metro Quadrado por Mês (R$)"),
        VALOR_CONTRATO_MES("Valor Total do Contrato Por Mês (R$)"),
        VALOR_TOTAL_CONTRATO("Valor Total do Contrato (R$)"),
        VALOR_LICITACAO_CONTRATO("Valor da Licitação (R$)"),
        NUMERO_CMC("Número do C.M.C"),
        RAZAO_CMC("Razão Social"),
        CNPJ_CMC("CNPJ"),
        LOGRADOURO_CMC("Logradouro"),
        NUMERO_ENDERECO_CMC("Número (Endereço)"),
        COMPLEMENTO_CMC("Complemento"),
        BAIRRO_CMC("Bairro"),
        CEP_CMC("CEP"),
        CIDADE_CMC("Cidade"),
        UF_CMC("UF"),
        TELEFONE_CMC("Telefone"),
        VALOR_RATEIO_METRO_QUADRADO("Valor Total do Rateio Por Metro Quadrado por Mês (R$)"),
        VALOR_RATEIO_MES("Valor Total do Rateio Por Mês (R$)"),
        VALOR_TOTAL_RATEIO("Valor Total do Rateio (R$)"),
        SOCIOS("Sócios");
        private String descricao;

        private TagsContratoCEASA(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsIsencaoIPTU {

        DESCRICAO_CATEGORIA_ISENCAO("Descrição da Categoria de Isenção"),
        NUMERO_LEI("Número da Lei de Isenção"),
        DESCRICAO_LEI("Descrição da Lei de Isenção"),
        NUMERO_PROCESSO("Número do Processo"),
        EXERCICIO_PROCESSO("Exercício do Processo"),
        NUMERO_PROTOCOLO("Número do Protocolo"),
        ANO_PROTOCOLO("Ano do Protocolo"),
        EXERCICIO_REFERENCIA("Exercício de Referência"),
        DATA_LANCAMENTO("Data do Lançamento"),
        DATA_VALIDADE_ISENCAO("Data do Validade da Isenção");
        private String descricao;

        private TagsIsencaoIPTU(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsCobrancaAdministrativa {
        NUMERO_ITEM_COBRANCA("Nº do item cobrança"),
        VENCIMENTO("Vencimento"),
        DIRETOR_DEPARTAMENTO("Diretor de departamewnto"),
        CHEFE_DIVISAO("Chefe da divisão"),
        CARGO("Cargo"),
        PORTARIA("Portaria"),
        EXERCICIO("Exercício"),
        DIVIDAS("Dívidas"),
        DATA_ATUAL("Data Atual"),
        DATA_VENCIMENTO_NOTIFICACAO("Data de Vencimento da Notificação");
        private String descricao;

        private TagsCobrancaAdministrativa(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum TipoModeloDocumento {

        TAGSCADASTROIMOBILIARIO("Tags do Cadastro Imobiliário"),
        TAGSCADASTROECONOMICO("Tags do Cadastro Economico"),
        TAGSCADASTRORURAL("Tags do Cadastro Rural"),
        TAGSPESSOAFISICA("Tags da Pessoa Física"),
        TAGSPESSOAJURIDICA("Tags da Pessoa Jurídica"),
        TAGSGERAIS("Tags Gerais"),
        TAGSFISCALIZACAO("Tags Fiscalização"),
        TAGSRBTRANS("Tags do RBTrans"),
        TAGSDIVIDAATIVA("Tags da Dívida Ativa"),
        TAGSTERMOADVERTENCIA("Tags do Termo de Advertência"),
        TAGSAUTOINFRACAOFISCALIZACAO("Tags do Auto de Infração da Fiscalização"),
        TAGSPARECERFISCALIZACAO("Tags do parecer da Fiscalização"),
        TAGSFISCALIZACAORBTRANS("Tags Fiscalização RBTrans"),
        TAGSPROTOCOLO("Tags do Protocolo"),
        TAGSCONTRATORENDASPATRIMONIAS("Tags do Contrato de Rendas Patrimoniais"),
        TAGSCONTRATOCEASA("Tags do Contrato de CEASA"),
        TAGSISENCAOIPTU("Tags da Isenção de IPTU"),
        TAGSCOBRACAADMINISTRATIVA("Tags de Cobrança Administrativa");
        private String descricao;

        private TipoModeloDocumento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsProcessoParcelamento {
        NUMERO_PARCELAMENTO("Número do Parcelamento"),
        ANO_PARCELAMENTO("Ano do Parcelamento"),
        DATA_PARCELAMENTO("Data do Parcelamento"),
        VALOR_A_PARCELAR_ORIGINAL("Valor à Parcelar do Original"),
        VALOR_A_PARCELAR_MULTA("Valor à Parcelar da Multa"),
        VALOR_A_PARCELAR_JUROS("Valor à Parcelar dos Juros"),
        VALOR_A_PARCELAR_CORRECAO_MONETARIA("Valor à Parcelar da Correção Monetária"),
        VALOR_A_PARCELAR_HONORARIOS("Valor à Parcelar dos Honorários"),
        VALOR_TOTAL_A_PARCELAR("Valor Total à Parcelar"),

        VALOR_DESCONTO_ORIGINAL(" Valor do Desconto do Original"),
        VALOR_DESCONTO_MULTA(" Valor do Desconto da Multa"),
        VALOR_DESCONTO_JUROS(" Valor do Desconto dos Juros"),
        VALOR_DESCONTO_CORRECAO_MONETARIA(" Valor do Desconto da Correção Monetária"),
        VALOR_DESCONTO_HONORARIOS(" Valor do Desconto dos Honorários"),
        VALOR_TOTAL_DESCONTO(" Valor Total do Desconto"),

        VALOR_PARCELADO_ORIGINAL(" Valor Parcelado do Original"),
        VALOR_PARCELADO_MULTA(" Valor Parcelado da Multa"),
        VALOR_PARCELADO_JUROS(" Valor Parcelado dos Juros"),
        VALOR_PARCELADO_CORRECAO_MONETARIA(" Valor Parcelado da Correção Monetária"),
        VALOR_PARCELADO_HONORARIOS(" Valor Parcelado dos Honorários"),
        VALOR_TOTAL_PARCELADO(" Valor Total Parcelado"),

        QUANTIDADE_PARCELAS(" Quantidade de Parcelas"),
        VENCIMENTO_PRIMEIRA_PARCELA(" Data do Vencimento da Primeira Parcela"),
        VALOR_ENTRADA(" Valor da Entrada"),
        PERCENTUAL_ENTRADA(" Percentual de Entrada"),
        PERCENTUAL_ENTRADA_EXTENSO(" Percentual de Entrada em extenso"),
        VALOR_PARCELAS(" Valor das Parcelas"),

        FIADOR_NOME("Nome do Fiador"),
        FIADOR_CPF("CPF do Fiador"),
        FIADOR_RG_INSC_ESTADUAL("RG/Inscrição Estadual do Fiador"),
        FIADOR_LOGRADOURO("Logradouro do Fiador"),
        FIADOR_NUMERO("Número Endereço do Fiador"),
        FIADOR_BAIRRO("Bairro do Fiador"),
        FIADOR_CIDADE("Cidade do Fiador"),
        FIADOR_UF("UF do Fiador"),
        FIADOR_COMPLEMENTO("Complemento do Fiador"),
        FIADOR_CEP("CEP do Fiador"),
        FIADOR_EMAIL("e-mail do Fiador"),
        FIADOR_TELEFONE("Telefone do Fiador"),
        FIADOR_ENDERECO_CORRESPONDENCIA("Endereço de Correspondência do Fiador"),

        PROCURADOR_NOME("Nome do Procurador"),
        PROCURADOR_CPF("CPF do Procurador"),
        PROCURADOR_RG_INSC_ESTADUAL("RG/Inscrição Estadual do Procurador"),
        PROCURADOR_LOGRADOURO("Logradouro do Procurador"),
        PROCURADOR_NUMERO("Número Endereço do Procurador"),
        PROCURADOR_BAIRRO("Bairro do Procurador"),
        PROCURADOR_CIDADE("Cidade do Procurador"),
        PROCURADOR_UF("UF do Procurador"),
        PROCURADOR_COMPLEMENTO("Complemento do Procurador"),
        PROCURADOR_CEP("CEP do Procurador"),
        PROCURADOR_EMAIL("e-mail do Procurador"),
        PROCURADOR_TELEFONE("Telefone do Procurador"),
        PROCURADOR_ENDERECO_CORRESPONDENCIA("Endereço de Correspondência do Procurador");

        private String descricao;

        private TagsProcessoParcelamento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum TagsAuxilioFuneral {
        NOME_RESPONSAVEL("Nome do responsável"),
        ENDERECO_RESPONSAVEL("Endereço do responsável"),
        RG_RESPONSAVEL("RG do responsável"),
        ESTADO_CIVIL_RESPONSAVEL("Estado Civil do resposável"),
        CPF_RESPONSAVEL("CPF do responsável"),
        BENEFICIO_RENUNCIADO_DECLARACAO_BENEFICIO("Benefício Renunciado para Declaração de Benefícios Eventuais"),
        NOME_FALECIDO("Nome do falecido"),
        DATA_REQUISICAO("Data da Requisição"),
        HORA_REQUISICAO("Hora da Requisição"),
        CEMITERIO("Cemitério"),
        FUNERARIA("Funerária");
        private String descricao;

        private TagsAuxilioFuneral(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum TagsCertidaoBaixaAtividade {
        SITUACAO_BAIXA_BCE("Situação da Baixa de Atividade"),
        DATA_BAIXA_BCE("Data da Baixa de Atividade"),
        DATA_BAIXA_BCE_EXTENSO("Data da Baixa de Atividade por Extenso"),
        PROTOCOLO_BAIXA_BCE("Protocolo da Baixa de Atividade"),
        MOTIVO_BAIXA_BCE("Motivo da Baixa de Atividade");

        private String descricao;

        TagsCertidaoBaixaAtividade(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsSubvencao {
        PROCESSO_NUMERO("Número do Processo"),
        PROCESSO_EXERCICIO("Exercício do Processo"),
        PROCESSO_REFERENCIA("Referência do Processo"),
        PROCESSO_DATA_LANCAMENTO("Data de Lançamento do Processo"),
        PROCESSO_TIPO("Tipo do Processo"),
        PASSAGEIROS_TRANSPORTADOS("Passageiros Transportados"),
        VALOR_PASSAGEM_POR_PASSAGEIRO("Valor da passagem por passageiros"),
        PERCENTUAL_SUBVENCAO("Percentual da Subvenção"),

        DEVEDOR_RAZAO_SOCIAL("Razão Social do Devedor"),
        DEVEDOR_CNPJ("CNPJ do Devedor"),
        DEVEDOR_LOGRADOURO_ENDERECO("Logradouro do Devedor"),
        DEVEDOR_NUMERO_ENDERECO("Número do endereço do Devedor"),
        DEVEDOR_COMPLEMENTO_ENDERECO("Complemento do Devedor"),
        DEVEDOR_BAIRRO_ENDERECO("Bairro do Devedor"),
        DEVEDOR_TELEFONE("Telefone do Devedor"),
        DEVEDOR_MUNICIPIO("Município do Devedor"),
        DEVEDOR_UF("UF do Devedor"),
        DEVEDOR_CEP("CEP do Devedor"),

        CREDOR_RAZAO_SOCIAL("Razão Social do Credor"),
        CREDOR_CNPJ("CNPJ do Credor"),
        CREDOR_LOGRADOURO_ENDERECO("Logradouro do Credor"),
        CREDOR_NUMERO_ENDERECO("Número do endereço do Credor"),
        CREDOR_COMPLEMENTO_ENDERECO("Complemento do Credor"),
        CREDOR_BAIRRO_ENDERECO("Bairro do Credor"),
        CREDOR_TELEFONE("Telefone do Credor"),
        CREDOR_MUNICIPIO("Município do Credor"),
        CREDOR_UF("UF do Credor"),
        CREDOR_CEP("CEP do Credor"),

        VALOR_TOTAL_CREDITO_COMPENSAR("Valor Total do Crédito a compensar"),
        VALOR_CREDITO_UTILIZADO_SEM_HONORARIOS("Valor do Crédito utilizado sem Honorários"),
        VALOR_TOTAL_HONORARIOS("Valor Total dos Honorários"),

        LISTA_DEBITOS_SUBVENCIONADOS("Lista de Débitos Subvencionados"),

        NOME_PRIMEIRO_ASSINANTE("Nome do Primeiro Assinante"),
        CARGO_PRIMEIRO_ASSINANTE("Cargo do Primeiro Assinante"),
        DECRETO_PRIMEIRO_ASSINANTE("Decreto do Primeiro Assinante"),
        NOME_SEGUNDO_ASSINANTE("Nome do Segundo Assinante"),
        CARGO_SEGUNDO_ASSINANTE("Cargo do Segundo Assinante"),
        DECRETO_SEGUNDO_ASSINANTE("Decreto do Segundo Assinante"),
        VALOR_BLOQUEADO_POR_PROCESSO("Valor Bloqueado Via Processo de Bloqueio de Outorga"),
        PROCESSO_DE_BLOQUEIO("Processo de Bloqueio de Outorga"),
        EXERCICIO_DO_PROCESSO_DE_BLOQUEIO("Exercício do Processo de Bloqueio de Outorga");

        private String descricao;

        TagsSubvencao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsOrdemGeralMonitoramento {
        SITUACAO_DA_ORDEM("Situação da Ordem Geral de Monitoramento"),
        NUMERO_PROTOCOLO("Número do Protocolo"),
        ANO_PROTOCOLO("Ano do Protocolo"),
        DATA_PROGRAMADA_DE("Data Programada - De"),
        DATA_PROGRAMADA_ATE("Data Programada - Até"),
        DATA_CRIACAO("Data de Criação"),
        USUARIO_LOGADO("Usuário Logado"),
        AUDITOR_FISCAL_DA_ORDEM("Auditor Fiscal da Ordem Geral de Monitoramento"),
        DESCRICAO("Descrição");

        private String descricao;

        TagsOrdemGeralMonitoramento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsBloqueioOutorga {
        PROCESSO_NUMERO("Número do Processo"),
        PROCESSO_EXERCICIO("Exercício do Processo"),
        PROCESSO_PROTOCOLO("Protocolo do Processo"),
        PROCESSO_DATA_LANCAMENTO("Data de Lançamento do Processo"),
        PROCESSO_SITUACAO("Situação do Processo de Bloqueio de Outorga"),
        USUARIO_LOGADO("Usuário Logado"),
        ATO_LEGAL("Ato Legal"),
        EMPRESA("Empresa"),
        MOTIVO_OU_FUNDAMENTACAO("Motivo ou Fundamentação Legal"),
        LISTA_PARAMETROS("Lista de Parâmetros de Referência"),
        LISTA_DADOS_BLOQUEIO("Dados do Bloqueio"),
        LISTA_VALORES_BLOQUEADOS("Valores Bloqueados");

        private String descricao;

        TagsBloqueioOutorga(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsProcessoRegularizacaoConstrucao implements EnumComDescricao {
        CAU_CREA_AUTOR_PROJETO("CAU/CREA do Autor do Projeto"),
        CAU_CREA_RESP_EXECUCAO("CAU/CREA do Resp. Pela Execução"),
        ART_RRT_PROJETO("ART/RRT do Projeto"),
        ART_RRT_EXECUCAO("ART/RRT da Execução");

        private String descricao;

        TagsProcessoRegularizacaoConstrucao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsAlvaraConstrucao implements EnumComDescricao {
        CODIGO("Código do Alvará"),
        EXERCICIO("Exercício do Alvará"),
        USUARIO("Usuário Responsável pelo Registro"),
        SITUACAO("Situação do Alvará"),
        DATA_EXPEDICAO("Data de Expedição do Alvará"),
        DATA_VENCIMENTO_CARTAZ("Data de Vencimento do Cartaz"),
        DATA_VENCIMENTO_DEBITO("Data de Vencimento do Débito"),
        NUMERO_PROTOCOLO("Número do Protocolo"),
        ANO_PROTOCOLO("Ano do Protocolo"),
        RESPONSAVEL_OBRA("Responsável pela Obra"),
        RESPONSAVEL_EXECUCAO("Responsável pela Execução"),
        PROCESSO_REGULARIZACAO("Processo de Regularização do Alvará"),
        PROCESSO_REGULARIZACAO_ANO_PROTOCOLO("Número do Protocolo do Processo de Regularização do Alvará"),
        PROCESSO_REGULARIZACAO_NUMERO_PROTOCOLO("Ano do Protocolo do Processo de Regularização do Alvará"),
        SERVICOS("Serviços do Alvará de Construção"),
        CALCULO("Cálculo do Alvará de Construção"),
        DETALHES_CALCULO("Detalhes do Cálculo do Alvará de Construção"),
        DAM("DAM's do Alvará de Construção"),
        EXERCICIO_PROCESSO("Exercício do Processo"),
        SERVICO("Serviço do Alvará de Construção"),
        ITEM_SERVICO("Item de Serviço do Alvará de Construção"),
        AREA_A_CONSTRUIR("Área à Construir"),
        AREA_A_DEMOLIR("Área à Demolir"),
        AREA_TOTAL("Área Total"),
        NUMERO_PAVIMENTOS("Número de Pavimentos"),
        MATRICULA_OBRA_INSS("Número da Matricula da Obra INSS"),
        CATEGORIA_USO("Categoria de Uso"),
        CLASSIFICACAO_USO("Classificação de Uso"),
        ZONA("Zona"),
        ASSINATURA_SECRETARIO("Assinatura do Secretario"),
        ASSINATURA_DIRETOR("Assinatura do Diretor"),
        OBSERVACAO("Observação");

        private String descricao;

        TagsAlvaraConstrucao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsHabiteseConstrucao implements EnumComDescricao {
        CODIGO("Código do Habite-se"),
        EXERCICIO("Exercício do Habite-se"),
        USUARIO("Usuário Responsável pelo Registro"),
        SITUACAO("Situação do Habite-se"),
        DATA_EXPEDICAO("Data de Expedição do Habite-se"),
        DATA_VENCIMENTO_ISS("Data de Vencimento do ISSQN"),
        DATA_VISTORIA("Data de Vistoria"),
        NUMERO_PROTOCOLO("Número do Protocolo"),
        ANO_PROTOCOLO("Ano do Protocolo"),
        CARACTERISTICAS_CONSTRUCAO("Características da Construção"),
        SERVICOS("Serviços do Habite-se"),
        DEDUCOES("Deduções"),
        ISSQN("ISSQN"),
        EXERCICIO_PROCESSO("Exercício do Processo"),
        ASSINATURA_SECRETARIO("Assinatura do Secretario"),
        ASSINATURA_DIRETOR("Assinatura do Diretor");

        TagsHabiteseConstrucao(String descricao) {
            this.descricao = descricao;
        }

        private String descricao;

        @Override
        public String getDescricao() {
            return descricao;
        }
    }


    public enum TagsProcessoDeProtesto implements EnumComDescricao {
        EXERCICIO("Exercício do Processo de Protesto"),
        NUMERO("Número do Processo de Protesto"),
        PROTOCOLO("Número do Protocolo"),
        DATA("Data de Lançamento"),
        USUARIO("Usuário Responsável pelo Registro"),
        SITUACAO("Situação do Processo de Protesto"),
        ATO_LEGAL("Ato Legal do Processo de Protesto"),
        MOTIVO("Motivo"),
        DEBITOS("Débitos Protestados");

        TagsProcessoDeProtesto(String descricao) {
            this.descricao = descricao;
        }

        private String descricao;

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsLicencaETR implements EnumComDescricao {
        PROTOCOLO("Protocolo"),
        REQUERENTE("Requerente"),
        CNPJ_REQUERENTE("CNPJ do Requerente"),
        REQUERENTE_TECNICO("Requerente Técnico"),
        REQUERENTE_TECNICO_EXECUCAO("Requerente Técnico da Execução"),
        ENDERECO_REQUERIMENTO("Endereço do Requerimento");

        private String descricao;

        TagsLicencaETR(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsNotaOrcamentaria implements EnumComDescricao {

        //Geral
        NUMERO_DOCUMENTO("Número"),
        DATA_DOCUMENTO("Data"),
        HISTORICO("Histórico"),
        DOCUMENTOS_COMPROBATORIOS("Documentos Comprobatórios"),
        DETALHAMENTOS("Detalhamentos"),
        DATA_ESTORNO("Data do Estorno"),
        NOME_DA_NOTA("Nome da Nota"),

        //Unidade Gestora/Órgão/Unidade
        DESCRICAO_ORGAO("Descrição do Órgão"),
        CODIGO_ORGAO("Código do Órgão"),
        DESCRICAO_UNIDADE("Descrição da Unidade"),
        CODIGO_UNIDADE("Código da Unidade"),

        //Orçamento
        CODIGO_ACAO("Código do Projeto/Atividade"),
        DESCRICAO_ACAO("Descrição do Projeto/Atividade"),
        FUNCIONAL_PROGRAMATICA("Funcional Programática"),
        PROGRAMA_DE_TRABALHO("Programa de Trabalho"),
        ESPECIFICACAO_DESPESA("Especificação da Despesa"),
        DESTINACAO_RECURSO("Código e Descrição da Destinação de Recurso"),
        DESCRICAO_FONTE_RECURSO("Descrição da Fonte de Recurso"),
        NATUREZA_DESPESA("Natureza da Despesa"),
        CODIGO_FUNCAO("Código da Função"),
        DESCRICAO_FUNCAO("Descrição da Função"),
        CODIGO_SUBFUNCAO("Código da Subfunção"),
        DESCRICAO_SUBFUNCAO("Descrição da Subfunção"),
        CODIGO_PROGRAMA("Código do Programa"),
        CODIGO_PROGRAMA_TRABALHO("Código do Programa de Trabalho"),
        DESCRICAO_PROGRAMA("Descrição do Programa"),
        CODIGO_DETALHAMENTO("Código do Detalhamento"),
        DESCRICAO_DETALHAMENTO("Descrião do Detalhamento"),

        //Pessoa
        NOME_PESSOA("Nome da Pessoa"),
        BAIRRO("Bairro"),
        CIDADE("Cidade"),
        LOGRADOURO("Logradouro"),
        LOCALIDADE("Localidade"),
        CEP("CEP"),
        UF("UF"),
        CPF_CNPJ("Cpf/Cnpj"),
        DESCRICAO_CLASSE_PESSOA("Descrição da Classe da Pessoa"),

        //Licitação
        MODALIDADE_LICITACAO("Modalidade da Licitação"),
        NUMERO_LICITACAO("Número da Licitação"),
        LICITACAO("Licitação"),

        //Empenho
        TIPO_EMPENHO("Tipo de Empenho"),
        NUMERO_EMPENHO("Número do Empenho"),
        DATA_EMPENHO("Data do Empenho"),
        EXERCICIO_ORIGINAL_EMPENHO("Exercício Original do Empenho"),

        //Liquidação
        NUMERO_LIQUIDACAO("Número da Liquidação"),
        DATA_LIQUIDACAO("Data da Liquidação"),
        VALOR_EMPENHADO("Valor Empenhado"),

        //PAGAMENTO
        NUMERO_PAGAMENTO("Número do Pagamento"),
        DATA_PAGAMENTO("Data do Pagamento"),
        VALOR_LIQUIDADO("Valor Liquidado"),
        DESCRICAO_AGENCIA("Descrição da Agência"),
        DESCRICAO_BANCO("Descrição do Banco"),
        NUMERO_AGENCIA("Número da Agência"),
        NUMERO_BANCO("Número do Banco"),
        DIGITO_VERIFICADOR("Digito Verificador"),
        NUMERO_CONTA_CORRENTE("Número da Conta Corrente"),
        DIGITO_VERIFICADOR_CONTA_CORRENTE("Digito Verificador da Conta Corrente"),
        TIPO_CONTA_BANCARIA("Tipo da Conta Bancária"),
        SITUACAO_CONTA_BANCARIA("Situação da Conta Bancária"),
        BANCO_AGENCIA_CONTA("Banco, Agência e Conta"),
        BANCO_CONTA_BANCARIA("Banco da Conta Bancária"),
        CONTA_FINANCEIRA("Conta Financeira"),
        CONTA_EXTRAORCAMENTARIA("Conta Extraorçamentária"),
        RETENCOES("Retenções"),
        PAGAMENTOS("Pagamentos"),
        FATURAS("Faturas"),
        CONVENIOS("Convênios"),
        GPS("Guia GPS"),
        DARFS("Darfs"),
        DARFS_SIMPLES("Darfs Simples"),
        RECEITAS_EXTRAS("Receitas Extras"),

        //valores/saldos
        SALDO_ANTERIOR("Saldo Anterior"),
        VALOR("Valor"),
        SALDO_ATUAL("Saldo Atual"),
        VALOR_EXTENSO("Valor por Extenso"),
        VALOR_ESTORNO("Valor do Estorno"),

        //Contrato
        NUMERO_CONTRATO("Número do Contrato"),
        INICIO_CONTRATO("Início do Contrato"),
        FIM_CONTRATO("Fim do Contrato"),
        FIM_CONTRATO_ATUAL("Fim da Vigência Atualizada"),
        EXECUCAO_CONTRATO("Número da Execução do Contrato");
        private String descricao;

        TagsNotaOrcamentaria(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsReciboReinf implements EnumComDescricao {
        TIPO("Tipo"),
        EMPRESA("Empresa"),
        SITUACAO("Situação"),
        CODIGO_RESPOSTA("Código Resposta"),
        DESCRICAO_RESPOSTA("Descriçao Resposta"),
        OPERACAO("Operação"),
        RECIBO("Recibo"),
        MES_ANO("Mês/Ano"),
        DATA_REGISTRO("Data Registro");
        private String descricao;

        TagsReciboReinf(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsSolicitacaoMaterial implements EnumComDescricao {
        OBJETO("Objeto"),
        VALOR_SERVICO("Valor do Serviço"),
        VALOR_SERVICO_POR_EXTENSO("Valor do Serviço por Extenso"),
        MODALIDADE_LICITACAO("Modalidade da Licitação"),
        TIPO("Tipo"),
        PROGRAMA_DE_TRABALHO("Programa de Trabalho"),
        JUSTIFICATIVA("Justificativa da Necessidade"),
        ITENS("Itens"),
        FORMA_PAGAMENTO("Forma de Pagamento"),
        PRAZO_ENTREGA("Prazo de Entrga"),
        PRAZO_EXECUCAO("Prazo de Execução"),
        PRAZO_VIGENCIA("Prazo de Vigência"),
        NUMERO_PROCESSO("Número do Processo"),
        UNIDADE_ADMINISTRATIVA("Unidade Administrativa"),
        SOLICITANTE("Solicitante"),
        LEI_LICITACAO("Lei de Licitação"),
        LEI_14133_OU_LEGISLACAO_ANTERIOR("Descrição da Lei nº14.133/2021 ou Legislação Anterior"),
        DESCRICAO_NECESSIDADE("Descrição da Necessidade"),
        SOLUCAO_SUGERIDA("Solução Sugerida"),
        PREVISAO_PCA("Previsão no PAC"),
        DESCRICAO_COTACAO("Descrição Cotação"),
        INICIO_EXECUCAO("Início da Execução"),
        DESCRICAO_COMPLEMENTAR("Descrição Complementar"),
        CONTRATACOES_CORRELATAS("Contratações Correlatas"),
        CONTRATACOES_INTERDEPENDENTES("Contratações Interdependentes"),
        GRAU_PRIORIDADE("Grau de Prioridade"),
        APROVADOR_SOLICITACAO("Aprovador da Solicitação");
        private String descricao;

        TagsSolicitacaoMaterial(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsDeclaracaoDispensaLicenciamento {
        DDL_DATA_LICENCIAMENTO("Declaração de Dispensa de Licenciamento - Data do Licenciamento"),
        DDL_ENDERECO_LOCALIZACAO("Declaração de Dispensa de Licenciamento - Endereço de Localização"),
        DDL_ATIVIDADE_EXERCIDAS_NO_LOCAL("Declaração de Dispensa de Licenciamento - Atividades Exercidas no Local"),
        DDL_ATIVIDADE_NAO_EXERCIDAS_NO_LOCAL("Declaração de Dispensa de Licenciamento - Atividades não Exercidas no Local");

        private String descricao;

        TagsDeclaracaoDispensaLicenciamento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsMarcaFogo {
        MF_SIGLA("Marca Fogo - Sigla"),
        MF_NUMERO("Marca Fogo - Número"),
        MF_EXERCICIO("Marca Fogo - Exercício"),
        MF_NOME("Marca Fogo - Nome Contribuinte"),
        MF_CPFCNPJ("Marca Fogo - CPF/CNPJ Contribuinte"),
        MF_RG("Marca Fogo - RG Contribuinte"),
        MF_ENDERECO("Marca Fogo - Endereço Contribuinte"),
        MF_NOME_USUARIO("Marca Fogo - Nome Usuário Expedição"),
        MF_MAT_USUARIO("Marca Fogo - Matrícula Usuário Expedição"),
        MF_LOGO("Marca Fogo - Logo"),
        MF_CADASTROS_RURAIS("Marca Fogo - Cadastros Rurais"),
        MF_NUMERO_PROTOCOLO("Marca Fogo - Número do Protocolo"),
        MF_ANO_PROTOCOLO("Marca Fogo - Ano do Protocolo"),
        MF_DESCRICAO("Marca Fogo - Descrição da Marca a fogo");

        private String descricao;

        TagsMarcaFogo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum TagsLicenciamentoAmbiental {
        CODIGO("Licenciamento Ambiental - Número do Processo"),
        EXERCICIO("Licenciamento Ambiental - Ano do Processo"),
        PROTOCOLO("Licenciamento Ambiental - Número do Protocolo"),
        ANO("Licenciamento Ambiental - Ano do Protocolo"),
        RAZAO_SOCIAL("Licenciamento Ambiental - Nome/Razão Social"),
        LOCALIZACAO("Licenciamento Ambiental - Localização"),
        CPF_CNPJ("Licenciamento Ambiental - CPF/CNPJ"),
        CONDICIONANTES("Licenciamento Ambiental - Condicionantes"),
        DESCRICAO("Licenciamento Ambiental - Descrição"),
        OBSERVACAO("Licenciamento Ambiental - Observação"),
        CNAES("Licenciamento Ambiental - CNAE's"),
        SECRETARIA("Licenciamento Ambiental - Secretaria"),
        SECRETARIO("Licenciamento Ambiental - Secretário(a)"),
        ASSINATURA_SECRETARIO("Licenciamento Ambiental - Assinatura do Secretário(a)"),
        DECRETO_NOMEACAO_SECRETARIO("Licenciamento Ambiental - Decreto Nomeação do Secretário(a)"),
        SECRETARIO_ADJUNTO("Licenciamento Ambiental - Secretário(a) Adjunto(a)"),
        ASSINATURA_SECRETARIO_ADJUNTO("Licenciamento Ambiental - Assinatura do Secretário(a) Ajunto(a)"),
        DECRETO_NOMEACAO_SECRETARIO_ADJUNTO("Licenciamento Ambiental - Decreto Nomeação do Secretário(a) Adjunto(a)"),
        DIRETOR("Licenciamento Ambiental - Diretor(a)"),
        ASSINATURA_DIRETOR("Licenciamento Ambiental - Assinatura do Diretor(a)"),
        DECRETO_NOMEACAO_DIRETOR("Licenciamento Ambiental - Decreto Nomeação do Diretor(a)");

        private String descricao;

        TagsLicenciamentoAmbiental(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
