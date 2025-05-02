package br.com.webpublico.controle;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.Date;

@ManagedBean
@ViewScoped
public class PesquisaItemNotificacaoControlador extends ComponentePesquisaGenerico {

    @Override
    public void getCampos() {
        itens.clear();
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.numero", "Número", Integer.class, false));
        itens.add(new ItemPesquisaGenerica("obj.contribuinte", "Contribuinte.Nome", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.contribuinte", "Contribuinte.Razão Social", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.contribuinte", "Contribuinte.CPF/CNPJ", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.cadastro", "Cadastro.Pessoa.Nome", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.cadastro", "Cadastro.Pessoa.Razão Social", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.cadastro", "Cadastro.Pessoa.CPF/CNPJ", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.notificacaoADM.programacaoCobranca.numero", "NotificaçãoADM.Programação de Cobrança.Numero", String.class));
        itens.add(new ItemPesquisaGenerica("obj.notificacaoADM.programacaoCobranca.descricao", "NotificaçãoADM.Programação de Cobrança.Descrição", String.class));
        itens.add(new ItemPesquisaGenerica("obj.notificacaoADM.aceite", "NotificaçãoADM.Cobrança com Aceite", Boolean.class));
        itens.add(new ItemPesquisaGenerica("obj.notificacaoADM.damAgrupado", "NotificaçãoADM.DAM Agrupado", Boolean.class));
        itens.add(new ItemPesquisaGenerica("obj.notificacaoADM.vencimentoDam", "NotificaçãoADM.Vencimento do DAM", Date.class));

        itensOrdenacao.clear();
        itensOrdenacao.addAll(itens);
    }

    @Override
    public String montaCondicao() {
        String condicao = "";
        if (camposPesquisa != null) {
            for (DataTablePesquisaGenerico dataTablePesquisaGenerico : camposPesquisa) {
                condicao = montarCondicaoDosFiltros(condicao, dataTablePesquisaGenerico);
            }
        }
        condicao += " 1=1";
        return condicao;
    }

    private String montarCondicaoDosFiltros(String condicao, DataTablePesquisaGenerico dataTablePesquisaGenerico) {
        if (validaItem(dataTablePesquisaGenerico)) {
            String alias = "";
            if (dataTablePesquisaGenerico.getValuePesquisa().trim().isEmpty()) {
                String condicaoDoCampo = primeiraLetraMinuscula(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao());

                if (!dataTablePesquisaGenerico.getItemPesquisaGenerica().isPertenceOutraClasse() && !dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().contains("obj.")) {
                    alias = "obj.";
                }
                condicao += alias + condicaoDoCampo + " is null and ";

            } else {
                condicao = popularFiltros(condicao, dataTablePesquisaGenerico, alias);
            }
        }
        return condicao;
    }

    private String popularFiltros(String condicao, DataTablePesquisaGenerico dataTablePesquisaGenerico, String alias) {

        String condicaoIsString = "";
        String condicaoDoCampo = primeiraLetraMinuscula(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao());
        String valueDoCampo = dataTablePesquisaGenerico.getValuePesquisa();
        Class<?> classe;
        boolean isString = true;
        boolean isValor = false;
        boolean isDate = false;
        try {
            classe = (Class<?>) dataTablePesquisaGenerico.getItemPesquisaGenerica().getTipo();
            if (!condicaoDoCampo.contains("to_number")) {
                condicaoIsString = "lower(to_char(" + alias + condicaoDoCampo + "))";
            } else {
                condicaoIsString = alias + condicaoDoCampo;
            }

            if (classe.equals(Integer.class) || classe.equals(Long.class)) {
                isString = false;
                isValor = true;
                condicaoIsString = alias + condicaoDoCampo + "";
                valueDoCampo = "to_number(to_char('" + valueDoCampo + "'))";
            }
            if (classe.equals(Date.class)) {
                isString = false;
                isDate = true;
                condicaoIsString = alias + condicaoDoCampo;
                String valeuFimData = "";
                if (dataTablePesquisaGenerico.getValeuPesquisaDataFim() != null && !dataTablePesquisaGenerico.getValeuPesquisaDataFim().isEmpty()) {
                    valeuFimData = " <= to_date('" + dataTablePesquisaGenerico.getValeuPesquisaDataFim() + "','dd/MM/yyyy')";
                }

                boolean dateInicioFimPreenchido = false;
                if (!valueDoCampo.isEmpty() && !valeuFimData.isEmpty()) {
                    valueDoCampo = " to_date('" + valueDoCampo + "','dd/MM/yyyy')" + "-" + " to_date('" + dataTablePesquisaGenerico.getValeuPesquisaDataFim() + "','dd/MM/yyyy')";
                    dateInicioFimPreenchido = true;
                }
                if (!dateInicioFimPreenchido) {
                    if (!valueDoCampo.isEmpty()) {
                        valueDoCampo = " >= to_date('" + valueDoCampo + "','dd/MM/yyyy')";
                    }
                    if (!valeuFimData.isEmpty()) {
                        valueDoCampo = valeuFimData;
                    }
                }
            }
            if (classe.equals(BigDecimal.class) || classe.equals(Double.class)) {
                condicaoIsString = alias + condicaoDoCampo + "";
                isString = false;
                isValor = true;
            }
            if (classe.equals(Boolean.class)) {
                condicaoIsString = alias + condicaoDoCampo + "";
                isString = false;
                isValor = true;
            }
            if (classe.equals(Pessoa.class)) {
                if ("obj.cadastro".equals(condicaoDoCampo)) {
                    condicao += " ( obj.cadastro  in( select ce from CadastroEconomico ce where ce.pessoa in (select pf from PessoaFisica pf where lower(pf.nome) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%' or (replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%')))"
                        + " or obj.cadastro  in( select ce from CadastroEconomico ce where ce.pessoa in (select pj from PessoaJuridica pj where lower(pj.razaoSocial) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%'or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%'))))";
                } else {
                    condicao += " (" + alias + condicaoDoCampo + " in (select pf from PessoaFisica pf where lower(pf.nome) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%' or (replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%'))"
                        + " or " + alias + condicaoDoCampo + " in (select pj from PessoaJuridica pj where lower(pj.razaoSocial) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%'or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%')))";
                }
            } else if (dataTablePesquisaGenerico.getItemPesquisaGenerica().iseEnum()) {
                condicao += alias + condicaoDoCampo + " = '" + valueDoCampo + "'";
            } else if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().contains("cpf") || dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().contains("cnpj")) {
                condicao += "(" + alias + condicaoDoCampo + " LIKE '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%' or (replace(replace(replace(" + alias + condicaoDoCampo + ",'.',''),'-',''),'/','')) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%')";
            } else {
                condicao += processaIntervalo(condicaoIsString, valueDoCampo, isString, isValor, isDate, dataTablePesquisaGenerico.getItemPesquisaGenerica().isStringExata());
            }
            if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getSubCondicao() != null) {
                condicao += " and " + alias + dataTablePesquisaGenerico.getItemPesquisaGenerica().getSubCondicao();
            }
            condicao += " and ";
        } catch (NullPointerException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar montar a condição de pesquisa!", ""));
        }
        return condicao;
    }
}
