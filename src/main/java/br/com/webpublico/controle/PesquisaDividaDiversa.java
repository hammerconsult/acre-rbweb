package br.com.webpublico.controle;

import br.com.webpublico.entidades.CalculoDividaDiversa;
import br.com.webpublico.entidades.CalculoPessoa;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.SituacaoCalculoDividaDiversa;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.util.EntidadeMetaData;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@ViewScoped
public class PesquisaDividaDiversa extends ComponentePesquisaGenerico implements Serializable {


    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.exercicio.ano", "Ano", Integer.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.numeroLancamento", "Número", Integer.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.dataLancamento", "Data do Lançamento", Date.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.tipoCadastroTributario", "Tipo de Cadastro", TipoCadastroTributario.class, true, true));
        itens.add(new ItemPesquisaGenerica("obj.numeroProcessoProtocolo", "Número do Processo", Integer.class, false, false));
        itens.add(new ItemPesquisaGenerica("obj.situacao", "Situação", SituacaoCalculoDividaDiversa.class, true, true));
        itens.add(new ItemPesquisaGenerica("tipoDividaDiversa.descricao", "Tipo de Dívida Diversa (Descrição)", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("cad.inscricaoCadastral", "C.M.C.", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("cad.codigoImobiliario", "Inscrição Imobiliária", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("cad.codigoRural", "Código Rural", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("p.nome", "Nome do Contribuinte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("p.razaoSocial", "Razão Social do Contribuinte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("p", "CPF/CNPJ", Pessoa.class, false, true));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.exercicio.ano", "Ano", Integer.class, false, false));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.numeroLancamento", "Número", Integer.class, false, false));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.dataLancamento", "Data do Lançamento", Date.class, false, false));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.tipoCadastroTributario", "Tipo de Cadastro", TipoCadastroTributario.class, true, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.numeroProcessoProtocolo", "Número do Processo", Integer.class, false, false));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.situacao", "Situação", SituacaoCalculoDividaDiversa.class, true, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("tipoDividaDiversa.descricao", "Tipo de Dívida Diversa (Descrição)", String.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("cad.inscricaoCadastral", "C.M.C.", String.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("cad.codigoImobiliario", "Inscrição Imobiliária", String.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("cad.codigoRural", "Código Rural", String.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("p.nome", "Nome do Contribuinte", String.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("p.razaoSocial", "Razão Social do Contribuinte", String.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("p", "CPF/CNPJ", String.class, false, true));

    }

    @Override
    public String getHqlConsulta() {
        return "select obj from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public String getHqlContador() {
        return "select count( distinct obj.id) from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public String getComplementoQuery() {
        String innerJoin = complementarInnerJoinQuery();
        String hql = innerJoin + montaCondicao() + montaOrdenacao();
        return hql;
    }

    public String complementarInnerJoinQuery() {
        String innerJoin = "";
        innerJoin += " left join obj.cadastro cad ";
        innerJoin += " left join obj.pessoa p ";
        innerJoin += " left join obj.tipoDividaDiversa tipoDividaDiversa ";

        return innerJoin;
    }

    @Override
    public String montaCondicao() {
        String condicao = "";
        String juncao = " where ";
        for (DataTablePesquisaGenerico dataTablePesquisaGenerico : camposPesquisa) {
            if (validaItem(dataTablePesquisaGenerico)) {
                try {

                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("obj.exercicio.ano")) {
                        condicao += juncao + " obj.exercicio.ano = " + dataTablePesquisaGenerico.getValuePesquisa();
                        juncao = " and ";
                    }
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("obj.numeroLancamento")) {
                        condicao += juncao + " obj.numeroLancamento = " + dataTablePesquisaGenerico.getValuePesquisa();
                        juncao = " and ";
                    }

                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("obj.dataLancamento")) {

                        if (!dataTablePesquisaGenerico.getValuePesquisa().isEmpty() && !dataTablePesquisaGenerico.getValeuPesquisaDataFim().isEmpty()) {
                            condicao += juncao + " obj.dataLancamento >= '" + dataTablePesquisaGenerico.getValuePesquisa() + "'"
                                    + juncao + " obj.dataLancamento <= '" + dataTablePesquisaGenerico.getValeuPesquisaDataFim() + "'";
                        } else if (!dataTablePesquisaGenerico.getValeuPesquisaDataFim().isEmpty()) {
                            condicao += juncao + " obj.dataLancamento = '" + dataTablePesquisaGenerico.getValeuPesquisaDataFim() + "'";
                        } else {
                            condicao += juncao + " obj.dataLancamento = '" + dataTablePesquisaGenerico.getValuePesquisa() + "'";
                        }
                        juncao = " and ";
                    }

                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("obj.tipoCadastroTributario")) {
                        condicao += juncao + " obj.tipoCadastroTributario = '" + dataTablePesquisaGenerico.getValuePesquisa() + "'";
                        juncao = " and ";
                    }

                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("obj.numeroProcessoProtocolo")) {
                        condicao += juncao + " obj.numeroProcessoProtocolo = " + dataTablePesquisaGenerico.getValuePesquisa();
                        juncao = " and ";
                    }
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("obj.situacao")) {
                        condicao += juncao + " obj.situacao = '" + dataTablePesquisaGenerico.getValuePesquisa() + "'";
                        juncao = " and ";
                    }
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("tipoDividaDiversa.descricao")) {
                        condicao += juncao + " tipoDividaDiversa.descricao like '%" + dataTablePesquisaGenerico.getValuePesquisa() + "%'";
                        juncao = " and ";
                    }
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("cad.inscricaoCadastral")) {
                        condicao += juncao + " cad.inscricaoCadastral like '%" + dataTablePesquisaGenerico.getValuePesquisa() + "%'";
                        juncao = " and ";
                    }
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("cad.codigoImobiliario")) {
                        condicao += juncao + " cad.codigo like '%" + dataTablePesquisaGenerico.getValuePesquisa() + "%'";
                        juncao = " and ";
                    }
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("cad.codigoRural")) {
                        condicao += juncao + " cad.codigo like '%" + dataTablePesquisaGenerico.getValuePesquisa() + "%'";
                        juncao = " and ";
                    }

                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("p.nome")) {
                        condicao += juncao + " p.nome like '%" + dataTablePesquisaGenerico.getValuePesquisa() + "%'";
                        juncao = " and ";
                    }
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("p.razaoSocial")) {
                        condicao += juncao + " p.razaoSocial like '%" + dataTablePesquisaGenerico.getValuePesquisa() + "%'";
                        juncao = " and ";
                    }
                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("p")) {
                        condicao += juncao + " (p in (select pf from PessoaFisica pf where lower(pf.nome) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%' or (replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%'))"
                                + " or " + "( p in (select pj from PessoaJuridica pj where lower(pj.razaoSocial) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%'or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%'))))";
                    }

                    if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("p.cpf")) {
                        condicao += juncao + " p.cpf like '%" + dataTablePesquisaGenerico.getValuePesquisa().replaceAll(".", "").replaceAll("-", "").replaceAll("/", "") + "%'";
                        juncao = " and ";
                    }

                } catch (NullPointerException e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar montar a condição de pesquisa!", ""));
                }
            }
        }

        return condicao;
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
        lista = new ArrayList(new HashSet<CalculoDividaDiversa>(lista));
        if (!lista.isEmpty()) {
            metadata = new EntidadeMetaData(lista.get(0).getClass());
            for (CalculoDividaDiversa calculo : (List<CalculoDividaDiversa>) lista) {

                calculo.setValorTotalTabelavel(calculo.getValorReal());

                if (calculo.getCadastro() != null) {
                    for (CalculoPessoa cp : calculo.getPessoas()) {
                        calculo.getPessoasTabelavel().add("- " + cp.getPessoa().getNome() + " - " + cp.getPessoa().getCpf_Cnpj());
                    }
                    calculo.setCadastroTabelavel(calculo.getCadastro().getNumeroCadastro());
                } else {
                    calculo.setCadastroTabelavel(calculo.getPessoa().getCpf_Cnpj());
                }

                if (calculo.getPessoa() != null) {
                    calculo.getPessoasTabelavel().add("- " + calculo.getPessoa().getNome() + " - " + calculo.getPessoa().getCpf_Cnpj());
                } else {
                    calculo.getPessoasTabelavel().add("");
                }

            }
        }
        Collections.sort(lista);
    }

    protected String montaOrdenacao() {
        if (camposOrdenacao != null) {
            if (!camposOrdenacao.isEmpty()) {
                String ordenar = " order by ";
                for (ItemPesquisaGenerica itemPesquisaGenerica : camposOrdenacao) {
                    if (itemPesquisaGenerica.getCondicao() != null) {
                        if (!itemPesquisaGenerica.getCondicao().trim().isEmpty()) {
                            if (itemPesquisaGenerica.getCondicao().trim().equals("cad.codigoRural") || itemPesquisaGenerica.getCondicao().trim().equals("cad.codigoImobiliario")) {
                                ordenar += " cad.codigo " + itemPesquisaGenerica.getTipoOrdenacao() + ",";
                            } else {
                                ordenar += primeiraLetraMinuscula(itemPesquisaGenerica.getCondicao()) + " " + itemPesquisaGenerica.getTipoOrdenacao() + ",";
                            }

                        }
                    }
                }

                if (ordenar.equals(" order by ")) {
                    return " order by obj.exercicio.ano desc, obj.numeroLancamento desc";
                } else {
                    return ordenar.substring(0, ordenar.length() - 1).replace("obj. ", " ");
                }
            }
        }
        return " order by obj.exercicio.ano desc, obj.numeroLancamento desc";
    }
}
