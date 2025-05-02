package br.com.webpublico.controle;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class PesquisaPermissaoTransporteControlador extends ComponentePesquisaGenerico {

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("permissionario.cadastroEconomico.inscricaoCadastral", "C.M.C. Permissionário", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("vei.placa", "Placa do Veículo", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("ce.pessoa", "Permissionário", Pessoa.class, false, true));

        itensOrdenacao.add(new ItemPesquisaGenerica("permissionario.cadastroEconomico.inscricaoCadastral", "C.M.C. Permissionário", String.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("vei.placa", "Placa do Veículo", String.class, false, true));
    }

    @Override
    public String getHqlConsulta() {
        return "select distinct new PermissaoTransporte(" +
                " obj.id, " +
                " obj.inicioVigencia, " +
                " obj.numero, " +
                " obj.tipoPermissaoRBTrans," +
                " permissionario.cadastroEconomico) " +
                " from PermissaoTransporte obj ";
    }

    @Override
    public String getHqlContador() {
        return "select count(obj.id) from PermissaoTransporte obj ";
    }

    @Override
    public String getComplementoQuery() {
        String condicao = montaCondicao();
        String retorno = " join obj.permissionarios permissionario ";
        if (condicao.contains("vei.placa")) {
            retorno += " left join obj.listaDeVeiculos veiculos ";
            retorno += " join veiculos.veiculoTransporte vei ";
            retorno += " where veiculos.finalVigencia is null and ";
        } if (condicao.contains("ce.pessoa")) {
            retorno += " left join permissionario.cadastroEconomico ce ";
            retorno += " where ";
        } else {
            retorno += " where ";
        }
        return retorno + " (permissionario.finalVigencia is null) and " + condicao + montaOrdenacao();
    }

}
