package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoSolicitacaoCadastroCredor;

public class GeraReferenciaSolicitacaoCadastroCredor extends GeraReferencia<CalculoSolicitacaoCadastroCredor>{

    @Override
    protected String getReferencia() {
        return "Solicitação de Cadastro de Credor: " + calculo.getSolicitacaoCadastroPessoa().getCpfCnpj() + " - " + calculo.getSolicitacaoCadastroPessoa().getEmail();
    }
}
