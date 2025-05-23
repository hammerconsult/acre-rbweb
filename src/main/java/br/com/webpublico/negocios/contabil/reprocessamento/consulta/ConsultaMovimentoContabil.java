package br.com.webpublico.negocios.contabil.reprocessamento.consulta;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ConsultaMovimentoContabilException;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.*;

public class ConsultaMovimentoContabil {
    public final static Integer MAXIMO_REGISTROS = 10;
    private final static String ALIAS_SQL = "obj";
    private List<FiltroConsultaMovimentoContabil> filtros;
    private List<ConsultaMovimentoContabil.Ordem> ordenacao;
    private Map<FiltroConsultaMovimentoContabil, Map<String, Object>> parametroPorValor;
    private StringBuilder retorno;
    private Class classe;
    private List<String> complementoJoin;
    private Integer maximoRegistros;
    private Boolean imprimirSql;

    public ConsultaMovimentoContabil(Class classe, List<FiltroConsultaMovimentoContabil> filtros) {
        this.classe = classe;
        this.ordenacao = Lists.newArrayList();
        this.filtros = filtros;
        retorno = new StringBuilder("");
        parametroPorValor = Maps.newHashMap();
        complementoJoin = Lists.newArrayList(" left join parametroevento paramet on paramet.idorigem = to_char(obj.id) ");
        maximoRegistros = MAXIMO_REGISTROS;
        imprimirSql = Boolean.FALSE;
        alterarCondicaoFiltro(Campo.ID_PARAMETRO_EVENTO, " paramet.idorigem ");
    }

    public static List<Long> getUnidades(List<HierarquiaOrganizacional> hierarquiaOrganizacionals) {
        List<Long> retorno = Lists.newArrayList();
        for (HierarquiaOrganizacional hierarquia : hierarquiaOrganizacionals) {
            retorno.add(hierarquia.getSubordinada().getId());
        }
        return retorno;
    }

    public static List<String> getCodigosDasContas(List<Conta> contas) {
        List<String> retorno = Lists.newArrayList();
        for (Conta conta : contas) {
            retorno.add(conta.getCodigo().trim());
        }
        return retorno;
    }

    public static List<String> getCodigosDasFontes(List<FonteDeRecursos> fontes) {
        List<String> retorno = Lists.newArrayList();
        for (FonteDeRecursos fonte : fontes) {
            retorno.add(fonte.getCodigo().trim());
        }
        return retorno;
    }

    public ConsultaMovimentoContabil adicionarParametro(Campo campo, Operador operador, Object valor) {
        this.filtros.add(new FiltroConsultaMovimentoContabil(valor, operador, campo));
        return this;
    }

    public void adicionarOrdenacao(ConsultaMovimentoContabil.Ordem campo) {
        if (campo.getTipoOrdem() != null && campo.getCampo() != null) {
            this.ordenacao.add(campo);
        }
    }

    public String getSql() {
        validarFiltros();
        incluirSelect();
        incluirFrom();
        incluirJoinsSql(complementoJoin);
        montarWhere();
        montarOrdenacao();
        return retorno.toString();
    }

    private void validarFiltros() {
        for (FiltroConsultaMovimentoContabil filtroConsultaMovimentoContabil : getFiltros()) {
            if (filtroConsultaMovimentoContabil.getCondicao() == null
                && filtroConsultaMovimentoContabil.getCampo() == null
                && filtroConsultaMovimentoContabil.getValor() == null) {
                throw new ConsultaMovimentoContabilException(filtroConsultaMovimentoContabil, classe);
            }

            if (filtroConsultaMovimentoContabil.getValor() != null) {
                if (!filtroConsultaMovimentoContabil.getValor().getClass().equals(filtroConsultaMovimentoContabil.getCampo().tipo)) {
                    throw new ConsultaMovimentoContabilException(filtroConsultaMovimentoContabil, filtroConsultaMovimentoContabil.getCampo(), classe);
                }
            }
        }

    }

    private void incluirSelect() {
        retorno.append(" select distinct " + ALIAS_SQL + ".* ");
    }

    private String getNomeTabela() {
        String tabela = classe.getSimpleName();
        if (classe.isAnnotationPresent(Table.class)) {
            tabela = ((Table) classe.getAnnotation(Table.class)).name();
        }
        if (classe.isAnnotationPresent(Entity.class)) {
            Entity annotation = (Entity) classe.getAnnotation(Entity.class);
            if (annotation.name() != null && !annotation.name().trim().isEmpty()) {
                tabela = annotation.name();
            }

        }
        return tabela;
    }

    private void incluirFrom() {
        retorno.append(" from " + getNomeTabela() + " " + ALIAS_SQL + " ");
    }


    public void incluirJoinsOrcamentoDespesa(String fonteDespesaOrc) {
        complementoJoin.add(" inner join fontedespesaorc fdesp on fdesp.id = " + fonteDespesaOrc);
        complementoJoin.add(" inner join provisaoppafonte pfont on pfont.id = fdesp.provisaoppafonte_id ");
        complementoJoin.add(" inner join contadedestinacao cd on cd.id = pfont.destinacaoDeRecursos_id ");
        complementoJoin.add(" inner join fonteDeRecursos fonte on cd.fonteDeRecursos_id = fonte.id ");
        complementoJoin.add(" inner join despesaorc desp on fdesp.despesaorc_id = desp.id ");
        complementoJoin.add(" inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id ");
        complementoJoin.add(" inner join conta conta on prov.contaDeDespesa_id = conta.id ");
        alterarCondicaoContaDespesa();
        alterarCondicaoFonteDeRecurso();
    }


    public void incluirJoinsOrcamentoReceita(String receitaLoa, String fonte) {
        complementoJoin.add(" inner join receitaloa rec on " + receitaLoa + " = rec.id ");
        complementoJoin.add(" inner join conta conta on rec.contaDeReceita_id = conta.id");
        complementoJoin.add(" inner join receitaloafonte recfonte on " + fonte + " = recfonte.id ");
        complementoJoin.add(" inner join ContaDeDestinacao cd on recfonte.destinacaoDeRecursos_id = cd.id ");
        complementoJoin.add(" inner join fonteDeRecursos fonte on cd.fonteDeRecursos_id = fonte.id");
        alterarCondicaoContaReceita();
        alterarCondicaoFonteDeRecurso();
        alterarCondicaoOperacaoReceita();
    }


    public void alterarCondicaoContaDespesa() {
        alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_DESPESA, "conta.id");
    }

    public void alterarCondicaoContaReceita() {
        alterarCondicaoFiltro(Campo.CONTA_RECEITA, "conta.id");
    }

    public void alterarCondicaoOperacaoReceita() {
        alterarCondicaoFiltro(Campo.OPERACAO_RECEITA, "rec.operacaoreceita");
    }

    public void alterarCondicaoFonteDeRecurso() {
        alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "fonte.id");
    }

    public Integer getMaximoRegistros() {
        return maximoRegistros;
    }

    public void alterarMaximoRegistros(Integer maximoRegistros) {
        this.maximoRegistros = maximoRegistros;
    }

    public Boolean getImprimirSql() {
        return imprimirSql;
    }

    public void setImprimirSql(Boolean imprimirSql) {
        this.imprimirSql = imprimirSql;
    }

    private void incluirJoinsSql(List<String> joins) {
        for (String s : joins) {
            retorno.append(" ").append(s).append(" ");
        }
    }

    public void incluirJoinsComplementar(String join) {
        complementoJoin.add(join);
    }

    public FiltroConsultaMovimentoContabil getFiltro(Campo campo) {
        for (FiltroConsultaMovimentoContabil filtroConsultaMovimentoContabil : getFiltros()) {
            if (filtroConsultaMovimentoContabil.getCampo().equals(campo)) {
                return filtroConsultaMovimentoContabil;
            }
        }
        return null;
    }


    /**
     * Método responsável por definir a condição(campo/coluna) da classe especificado na consulta
     *
     * @param campo    é o campo (enum ConsultaMovimentoContabil.campo)
     * @param condicao é a condição que será utilizada na query. Exemplo: dataempenho, dataliquidacao, unidade.id
     *                 OBS.: quando o campo a ser utilizador, for atributo da classe em questão, deve-se colocar "obj."
     */
    public void alterarCondicaoFiltro(Campo campo, String condicao) {
        FiltroConsultaMovimentoContabil filtro = getFiltro(campo);
        if (filtro != null) {
            filtro.setCondicao(condicao);
        }
    }


    public Object getValor(Object valor, FiltroConsultaMovimentoContabil filtro) {
        if (valor instanceof String && filtro.getOperador().equals(ConsultaMovimentoContabil.Operador.LIKE)) {
            return "%" + valor + "%";
        }
        if (valor instanceof Date) {
            return DataUtil.getDataFormatada((Date) valor);
        }
        if (valor instanceof Enum) {
            return ((Enum) valor).name();
        }
        return valor;
    }

    public void montarWhere() {
        String juncao = " WHERE ";
        for (FiltroConsultaMovimentoContabil filtro : filtros) {
            if (filtro.getCampo() != null && filtro.getOperador() != null && filtro.getValor() != null) {
                retorno.append(juncao).append(filtro.getCondicao()).append(" ").append(defineCondicao(filtro));
                juncao = " AND ";
            }
        }
    }

    public void montarOrdenacao() {
        if (this.ordenacao.isEmpty()) {
            retorno.append(" ORDER BY " + ALIAS_SQL + ".ID");
        } else {
            String order = " ORDER BY ";
            for (ConsultaMovimentoContabil.Ordem ordem : this.getOrdenacao()) {
                FiltroConsultaMovimentoContabil filtro = getFiltro(ordem.getCampo());
                if (filtro != null) {
                    order += filtro.getCondicao() + " " + ordem.getTipoOrdem().name() + ", ";
                }
            }
            order = order.substring(0, order.length() - 2);
            retorno.append(order);
        }
    }

    private String defineCondicao(FiltroConsultaMovimentoContabil filtro) {
        String condicao = "";
        defineParametrosDaQuery(filtro);
        for (String parametro : parametroPorValor.get(filtro).keySet()) {
            if (parametroPorValor.get(filtro).get(parametro) != null) {
                condicao += parametro + ", ";
            }
        }
        condicao = condicao.contains(", ") ? condicao.substring(0, condicao.length() - 2) : condicao;
        if (filtro.getOperador().equals(Operador.IN) || filtro.getOperador().equals(Operador.NOT_IN)) {
            condicao = "(" + condicao + ")";
        }
        if (filtro.getCampo().getTipo().equals(Date.class)) {
            condicao = "to_date(" + condicao + ", 'dd/MM/yyyy')";
        }
        return filtro.getOperador().getOperador() + " " + condicao;
    }

    private void defineParametrosDaQuery(FiltroConsultaMovimentoContabil filtro) {
        String parametro = ":" + filtro.getCampo().name();
        if (!parametroPorValor.containsKey(filtro)) {
            parametroPorValor.put(filtro, new HashMap<String, Object>());
        }
        if (filtro.getValor() instanceof Collection) {
            for (Object obj : (Collection) filtro.getValor()) {
                parametroPorValor.get(filtro).put(parametro, obj);
                parametro = defineNomeDoParametro(filtro);
            }
        } else {
            parametroPorValor.get(filtro).put(parametro, filtro.getValor());
        }
    }

    private String defineNomeDoParametro(FiltroConsultaMovimentoContabil filtro) {
        Integer qntParametrosIguais = 1;
        String parametro = filtro.getCampo().name();
        for (FiltroConsultaMovimentoContabil f : parametroPorValor.keySet()) {
            for (String param : parametroPorValor.get(f).keySet()) {
                String acomparar = param.substring(0, StringUtils.lastIndexOf(param, "_")).replace(":", "");
                if (acomparar.equals(parametro)) {
                    qntParametrosIguais++;
                }
            }
        }
        return ":" + parametro + "_" + qntParametrosIguais;
    }


    public List<FiltroConsultaMovimentoContabil> getFiltros() {
        return filtros;
    }

    public void setFiltros(List<FiltroConsultaMovimentoContabil> filtros) {
        this.filtros = filtros;
    }

    public Map<FiltroConsultaMovimentoContabil, Map<String, Object>> getParametroPorValor() {
        return parametroPorValor;
    }

    public void setParametroPorValor(Map<FiltroConsultaMovimentoContabil, Map<String, Object>> parametroPorValor) {
        this.parametroPorValor = parametroPorValor;
    }

    public StringBuilder getRetorno() {
        return retorno;
    }

    public void setRetorno(StringBuilder retorno) {
        this.retorno = retorno;
    }

    public Class getClasse() {
        return classe;
    }

    public void setClasse(Class classe) {
        this.classe = classe;
    }

    public List<Ordem> getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(List<Ordem> ordenacao) {
        this.ordenacao = ordenacao;
    }

    @Override
    public String toString() {
        return "ConsultaMovimentoContabil{" +
            "filtros=" + filtros +
            ", parametroPorValor=" + parametroPorValor +
            ", retorno=" + retorno +
            ", classe=" + classe +
            ", complementoJoin=" + complementoJoin +
            '}';
    }

    public enum Juncao {
        AND("and"), OR("OR");
        private String juncao;

        private Juncao(String operador) {
            this.juncao = operador;
        }

        public String getJuncao() {
            return juncao;
        }
    }

    public enum Operador {
        MAIOR(">"), MENOR("<"), IGUAL("="), DIFERENTE("<>"), MAIOR_IGUAL(">="), MENOR_IGUAL("<="), LIKE("like"), IN("in"), NOT_IN("not in"), IS("is"), IS_NOT_NULL("is not null");
        private String operador;

        private Operador(String operador) {
            this.operador = operador;
        }

        public String getOperador() {
            return operador;
        }
    }

    public enum Campo {
        //COMUM
        UNIDADE(Long.class, "Unidade Organizacional"),
        LISTA_UNIDADE(ArrayList.class, "Lista de Unidade Organizacional"),
        FONTE_DE_RECURSO(Long.class, "Fonte de Recurso"),
        PESSOA(Long.class, "Pessoa"),
        CLASSE(Long.class, "Classe credor"),
        DATA_INICIAL(Date.class, "Data Inicial do Movimento"),
        DATA_FINAL(Date.class, "Data Final do Movimento"),
        EVENTO_CONTABIL(Long.class, "Evento Contábil"),
        TIPO_EVENTO_CONTABIL(TipoEventoContabil.class, "Tipo Evento Contábil"),
        NUMERO(Long.class, "Número do Movimento"),
        ID_PARAMETRO_EVENTO(Long.class, "ID Parâmetro Evento"),
        CONTAS_DEBITOS(ArrayList.class, "Contas de Débito"),
        CONTAS_CREDITOS(ArrayList.class, "Contas de Crédito"),
        FONTES_DEBITOS(ArrayList.class, "Fontes de Recursos de Débito"),
        FONTES_CREDITOS(ArrayList.class, "Fontes de Recursos de Crédito"),
        VALOR(BigDecimal.class, "Valor do Movimento"),
        HISTORICO(String.class, "Histórico do Movimento"),

        //Despesa
        CONTA_DESPESA(Long.class, "Conta de Despesa"),
        CATEGORIA_ORCAMENTARIA(CategoriaOrcamentaria.class, "Categoria Orçamentária"),

        //Receita
        CONTA_RECEITA(Long.class, "Conta de Receita"),
        OPERACAO_RECEITA(OperacaoReceita.class, "Operação da Receita"),

        //Extra
        CONTA_EXTRA(Long.class, "Conta de Extra-orçamentária"),

        //Financeiro
        CONTA_FINANCEIRA(Long.class, "Conta Financeira"),

        //Patrimonio
        TIPO_OPERACAO_BENS_MOVEIS(TipoOperacaoBensMoveis.class, "Tipo Operação de Bens Móveis"),
        TIPO_OPERACAO_BENS_IMOVEIS(TipoOperacaoBensImoveis.class, "Tipo Operação de Bens Imóveis"),
        TIPO_OPERACAO_BENS_INTANGIVEIS(TipoOperacaoBensIntangiveis.class, "Tipo Operação de Bens Intangiveis"),
        GRUPO_PATRIMONIAL(Long.class, "Grupo Patrimonial"),

        //Material
        GRUPO_MATERIAL(Long.class, "Grupo Material"),
        TIPO_ESTOQUE(TipoEstoque.class, "Tipo Estoque"),
        TIPO_OPERACAO_ESTOQUE(TipoOperacaoBensEstoque.class, "Tipo operação Estoque"),


        //Divida Publica
        OPERACAO_DIVIDAPUBLICA(OperacaoMovimentoDividaPublica.class, "Operação da Divida Pública"),
        ;


        private String descricao;
        private Class tipo;

        private Campo(Class tipo, String descricao) {

            this.tipo = tipo;
            this.descricao = descricao;
        }


        public Class getTipo() {
            return tipo;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public void setTipo(Class tipo) {
            this.tipo = tipo;
        }
    }

    public static class Ordem {
        private Campo campo;
        private TipoOrdem tipoOrdem;

        public Ordem() {

        }

        public Ordem(TipoOrdem tipoOrdem, Campo campo) {
            this.tipoOrdem = tipoOrdem;
            this.campo = campo;
        }

        public Campo getCampo() {
            return campo;
        }

        public void setCampo(Campo campo) {
            this.campo = campo;
        }

        public void setTipoOrdem(TipoOrdem tipoOrdem) {
            this.tipoOrdem = tipoOrdem;
        }

        public TipoOrdem getTipoOrdem() {
            return tipoOrdem;
        }

        @Override
        public String toString() {
            return "Ordem{" +
                "campo=" + campo +
                ", tipoOrdem=" + tipoOrdem +
                '}';
        }

        public static enum TipoOrdem {
            ASC("Crescente"),
            DESC("Decrescente");

            private String descricao;

            TipoOrdem(String descricao) {
                this.descricao = descricao;
            }

            public String getDescricao() {
                return descricao;
            }

            public void setDescricao(String descricao) {
                this.descricao = descricao;
            }

            @Override
            public String toString() {
                return getDescricao();
            }
        }
    }

    public static List<FiltroConsultaMovimentoContabil> clonarFiltros(List<FiltroConsultaMovimentoContabil> filtros) {
        ArrayList<FiltroConsultaMovimentoContabil> novaLista = new ArrayList<FiltroConsultaMovimentoContabil>();

        try {
            for (FiltroConsultaMovimentoContabil o : filtros) {
                novaLista.add((FiltroConsultaMovimentoContabil) BeanUtils.cloneBean(o));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return novaLista;
    }
}
