package br.com.webpublico.controle;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by israeleriston on 20/07/16.
 */
@ViewScoped
@ManagedBean
public class PesquisaGenericaSolicitacaoIsencaoIPTU extends ComponentePesquisaGenerico {

    @Override
    public String getHqlConsulta() {
        return "select new IsencaoCadastroImobiliario(obj) from IsencaoCadastroImobiliario obj";
    }

    @Override
    public String montaCondicao() {
        boolean hasInscricao = false;
        boolean hasSetor = false;
        boolean hasQuadra = false;
        boolean hasDistrito = false;
        String condicao = "";
        if (camposPesquisa != null) {

            for (DataTablePesquisaGenerico dataTablePesquisaGenerico : camposPesquisa) {
                if (validaItem(dataTablePesquisaGenerico)) {


                    String alias = "";
                    if (dataTablePesquisaGenerico.getValuePesquisa().trim().isEmpty()) {
                        String condicaoDoCampo = primeiraLetraMinuscula(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao());

                        if (!dataTablePesquisaGenerico.getItemPesquisaGenerica().isPertenceOutraClasse() && !isContainsCondicao(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao(), "obj.")) {
                            alias = "obj.";
                        }
                        condicao += alias + condicaoDoCampo + " is null and ";

                    } else {
                        String condicaoIsString = "";

                        String condicaoDoCampo = primeiraLetraMinuscula(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao());
                        String valueDoCampo = dataTablePesquisaGenerico.getValuePesquisa();
                        Class<?> classe;
                        boolean isString = true;
                        boolean isValor = false;
                        boolean isDate = false;
                        try {
                            classe = (Class<?>) dataTablePesquisaGenerico.getItemPesquisaGenerica().getTipo();
                            if (!isContainsCondicao(condicaoDoCampo, "to_number")) {
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
                                if (dataTablePesquisaGenerico.getValeuPesquisaDataFim() != null) {
                                    if (!dataTablePesquisaGenerico.getValeuPesquisaDataFim().isEmpty()) {
                                        valeuFimData = " <= to_date('" + dataTablePesquisaGenerico.getValeuPesquisaDataFim() + "','dd/MM/yyyy')";
                                    }
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
                                condicao += " (" + alias + condicaoDoCampo + " in (select pf from PessoaFisica pf where lower(pf.nome) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%' or (replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%'))"
                                        + " or " + alias + condicaoDoCampo + " in (select pj from PessoaJuridica pj where lower(pj.razaoSocial) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%'or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%')))";
                            } else if (dataTablePesquisaGenerico.getItemPesquisaGenerica().iseEnum()) {
                                condicao += alias + condicaoDoCampo + " = '" + valueDoCampo + "'";
                            } else if (isContainsCondicao(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao(), "cpf") || isContainsCondicao(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao(), "cnpj")) {
                                condicao += "(" + alias + condicaoDoCampo + " LIKE '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%' or (replace(replace(replace(" + alias + condicaoDoCampo + ",'.',''),'-',''),'/','')) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%')";


                            } else if (isContainsPesquisa(dataTablePesquisaGenerico, "Inscrição Inicial")
                                    && !isContainsCondicao(condicao, dataTablePesquisaGenerico.getItemPesquisaGenerica().getLabel())) {
                                condicao += "(" + alias + condicaoDoCampo + " >= '" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "' )";
                                hasInscricao = true;
                            } else if (isContainsPesquisa(dataTablePesquisaGenerico, "Inscrição Final") && hasInscricao) {
                                condicao += "(" + alias + condicaoDoCampo + " <= '" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "' )";
                            } else if (isContainsPesquisa(dataTablePesquisaGenerico, "Setor Inicial")
                                    && !isContainsCondicao(condicao, dataTablePesquisaGenerico.getItemPesquisaGenerica().getLabel())) {
                                condicao += "(" + alias + condicaoDoCampo + " >= '" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "' )";
                                hasSetor = true;
                            } else if (isContainsPesquisa(dataTablePesquisaGenerico, "Setor Final") && hasSetor) {
                                condicao += "(" + alias + condicaoDoCampo + " <= '" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "' )";
                            } else if (isContainsPesquisa(dataTablePesquisaGenerico, "Quadra Inicial")
                                    && !isContainsCondicao(condicao, dataTablePesquisaGenerico.getItemPesquisaGenerica().getLabel())) {
                                condicao += "(" + alias + condicaoDoCampo + " >= '" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "' )";
                                hasQuadra = true;
                            } else if (isContainsPesquisa(dataTablePesquisaGenerico, "Quadra Final") && hasQuadra) {
                                condicao += "(" + alias + condicaoDoCampo + " <= '" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "' )";
                            } else if (isContainsPesquisa(dataTablePesquisaGenerico, "Distrito Inicial")) {
                                condicao += "(" + alias + condicaoDoCampo + " >= '" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "' )";
                                hasDistrito = true;
                            } else if (isContainsPesquisa(dataTablePesquisaGenerico, "Distrito Final") && hasDistrito) {
                                condicao += "(" + alias + condicaoDoCampo + " <= '" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "' )";
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
                    }
                }
            }
        }
        condicao += " 1=1";
        return condicao;
    }

    private boolean isContainsCondicao(String condicao, String label) {
        return condicao.contains(label);
    }

    private boolean isContainsPesquisa(DataTablePesquisaGenerico dataTablePesquisaGenerico, String suffix) {
        return dataTablePesquisaGenerico.getItemPesquisaGenerica().getLabel().endsWith(suffix);
    }

    @Override
    public String processaIntervalo(String nomeCampo, String intervalo, boolean isString, boolean isValor, boolean isDate, boolean isStringExata) {
        if (isStringExata) {
            return nomeCampo + " = '" + intervalo + "'";
        }

        if (!isContainsCondicao(intervalo, ";") && !isContainsCondicao(intervalo, "-")) {
            if (isString && !isValor) {
                return nomeCampo + " LIKE '%" + intervalo.toLowerCase() + "%'";
            }
            if (isValor) {
                return nomeCampo + " = " + intervalo + "";
            }
            if (isDate) {
                return nomeCampo + intervalo;
            }
            return nomeCampo + " LIKE '%" + intervalo + "%'";
        }
        if (isDate) {
            String[] pedacos = intervalo.split("-");
            return nomeCampo + " between " + pedacos[0] + " and " + pedacos[1];
        }

        String aspas = isString ? "'" : "";
        StringBuilder retorno = new StringBuilder("(");
        int lengthInicialRetorno = retorno.length();
        String[] pedacos = intervalo.split(";");
        List<String> pedacosTraco = new ArrayList<>();
        for (String pedaco : pedacos) {

            if (!isContainsCondicao(pedaco, "-") && !"".equals(pedaco.trim())) {
                if (retorno.length() == lengthInicialRetorno) {
                    retorno.append(nomeCampo).append(" in (");
                }
                retorno.append(aspas).append(pedaco).append(aspas).append(",");
            } else {
                pedacosTraco.add(pedaco);
            }
        }
        if (retorno.length() != lengthInicialRetorno) {
            retorno.deleteCharAt(retorno.lastIndexOf(","));
            retorno.append(")");
        }
        for (String pedaco : pedacosTraco) {
            String[] pedacosDoTraco = pedaco.split("-");
            if (pedacosDoTraco.length >= 2) {
                if (retorno.length() != lengthInicialRetorno) {
                    retorno.append(" OR ");
                }
                retorno.append(nomeCampo).append(" BETWEEN ").append(aspas).append(pedacosDoTraco[0].toLowerCase()).append(aspas).append(" AND ").append(aspas).append(pedacosDoTraco[pedacosDoTraco.length - 1].toLowerCase()).append(aspas).append("");
            }
        }
        if (retorno.length() != lengthInicialRetorno) {
            retorno.append(")");
        }
        return retorno.toString();
    }
}
