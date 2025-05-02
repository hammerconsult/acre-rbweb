package br.com.webpublico.nfse.enums;

/**
 * Created by william on 21/09/17.
 */
public class TagsTemplateNfse {

    public enum EmailTomador {
        NFSE_LINK("Link do Sistema de Nota Fiscal"),
        NFSE_NUMERO("Número da Nota Fiscal"),
        NFSE_CODIGO_VERIFICACAO("Código de Verificação da Nota Fiscal"),
        PRESTADOR_RAZAO_SOCIAL("Razão Social do Prestador"),
        PRESTADOR_CPF_CNPJ("CPF/CNPJ do Prestador"),
        PRESTADOR_INSCRICAO_MUNICIPAL("Inscrição Municipal do Prestador"),
        TOMADOR_RAZAO_SOCIAL("Razão Social do Tomador"),
        TOMADOR_CMM("CMM do Tomador"),
        TOMADOR_CPF_CNPJ("CPF/CNPJ do Tomador");

        private String descricao;

        EmailTomador(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum EmailContador {
        NFSE_LINK("Link do Sistema de Nota Fiscal"),
        NFSE_NUMERO("Número da Nota Fiscal"),
        NFSE_CODIGO_VERIFICACAO("Código de Verificação da Nota Fiscal"),
        PRESTADOR_RAZAO_SOCIAL("Razão Social do Prestador"),
        PRESTADOR_CPF_CNPJ("CPF/CNPJ do Prestador"),
        PRESTADOR_CMC("Cadastro Mobiliário Municipal do Prestador"),
        TOMADOR_RAZAO_SOCIAL("Razão Social do Tomador"),
        TOMADOR_CMC("Cadastro Mobiliário Municipal do Tomador"),
        TOMADOR_CPF_CNPJ("CPF/CNPJ do Tomador");

        private String descricao;

        EmailContador(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum EmailCancelamentoTomadorContador {
        NFSE_LINK("Link do Sistema de Nota Fiscal"),
        NFSE_NUMERO("Número da Nota Fiscal"),
        NFSE_CODIGO_VERIFICACAO("Código de Verificação da Nota Fiscal"),
        PRESTADOR_RAZAO_SOCIAL("Razão Social do Prestador"),
        PRESTADOR_CPF_CNPJ("CPF/CNPJ do Prestador"),
        PRESTADOR_CMC("Cadastro Mobiliário Municipal do Prestador"),
        TOMADOR_RAZAO_SOCIAL("Razão Social do Tomador"),
        TOMADOR_CMC("Cadastro Mobiliário Municipal do Tomador"),
        TOMADOR_CPF_CNPJ("CPF/CNPJ do Tomador");

        private String descricao;

        EmailCancelamentoTomadorContador(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum FaleConosco {
        FALECONOSCO_NOMECOMPLETO("Nome Completo"),
        FALECONOSCO_CPFCNPJ("CPF/CNPJ"),
        FALECONOSCO_TELEFONE("Telefone"),
        FALECONOSCO_EMAIL("E-mail"),
        FALECONOSCO_DATAHORAENVIO("Data e Hora do Envio"),
        FALECONOSCO_MENSAGEM("Mensagem");

        private String descricao;

        FaleConosco(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum AtivacaoCadastro {
        NOMEUSUARIO("Nome do Usuário"),
        LINK_ATIVACAO_CADASTRO("Link do Sistema de Nota Fiscal");

        private String descricao;

        AtivacaoCadastro(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum SolicitacaoAlteracaoSenha {
        NOMEUSUARIO("Nome do Usuário"),
        LINK_ALTERACAO_SENHA("Link de Alteração de Senha");

        private String descricao;

        SolicitacaoAlteracaoSenha(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }


}
