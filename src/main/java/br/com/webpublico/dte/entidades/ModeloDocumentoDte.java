package br.com.webpublico.dte.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Etiqueta("Modelo de Documento - DTE")
@Entity
@Audited
public class ModeloDocumentoDte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    private String descricao;

    @Etiqueta("Conteúdo")
    private String conteudo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public enum TagsDataHoje {

        DATA_HOJE_ANO("Data Hoje Ano"),
        DATA_HOJE_MES("Data Hoje Mês"),
        DATA_HOJE_DIA("Data Hoje Dia"),
        DATA_HOJE_DIA_EXTENSO("Data Hoje Dia Extenso"),
        DATA_HOJE_MES_EXTENSO("Data Hoje Mês Extenso"),
        DATA_HOJE_ANO_EXTENSO("Data Hoje Ano Extenso"),
        DATA_HOJE_POR_EXTENSO("Data Hoje por Extenso");

        private String descricao;

        TagsDataHoje(String descricao) {
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

        TagsCadastroEconomico(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
