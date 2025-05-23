package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.ConcessaoFeriasLicenca;
import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.enums.rh.TipoAuditoriaRH;
import br.com.webpublico.negocios.jdbc.rowmapper.RevisaoAuditoriaRowMapper;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@Repository
public class AuditoriaJDBC extends JdbcDaoSupport implements Serializable {


    @Autowired
    public AuditoriaJDBC(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public LinkedList<ClasseAuditada> listarAtributosBasicos(final Class classe, Long id) {
        String select = "select tabela.id ";

        popularAtributosClasse(select, classe, "tabela", false);

        String tabela = getNomeTabela(classe);
        String sql = " from " + tabela.toUpperCase() + " tabela ";

        int index = 0;
        Class superClasse = classe.getSuperclass();
        String alias = "tabela_super" + index;
        String aliasAnterior = null;
        while (superClasse != null && superClasse.isAnnotationPresent(Entity.class)) {
            String tabelaSuper = getNomeTabela(superClasse);
            sql += " inner join " + tabelaSuper + " " + alias + " on " + (aliasAnterior == null ? "tabela" : aliasAnterior) + ".id = " + alias + ".id";
            popularAtributosClasse(select, superClasse, alias, false);

            aliasAnterior = alias;
            index++;
            alias = "tabela_super" + index;
            superClasse = superClasse.getSuperclass();
        }

        sql += " where tabela.id = " + id;

        LinkedList<ClasseAuditada> result = new LinkedList<>(getJdbcTemplate().query(select + sql, new ConversorResultSet(classe, 0, getNomeEntidade(classe))));

        return result;

    }

    private void popularAtributosClasse(String select, Class classe, String alias, boolean buscarAtributosSuperClasse) {
        for (Field field : Persistencia.getAtributos(classe, buscarAtributosSuperClasse)) {
            select = montarSelectPorTabelaveis(select, field, alias);
        }
    }

    public Integer contarAuditoria(Class classe, Long id, String coluna) {

        String tabela = getNomeTabela(classe);
        String sql = "select count(aud.id) from " + tabela.toUpperCase() + "_AUD aud ";

        if (id != null && Strings.isNullOrEmpty(coluna)) {
            sql += " where aud.id = " + id;
        } else if (id != null && !Strings.isNullOrEmpty(coluna)) {
            sql += " where " + coluna + " = " + id;
        }
        return getJdbcTemplate().queryForObject(sql, Integer.class);
    }


    public LinkedList<ClasseAuditada> listarAuditoria(FiltroClasseAuditada filtro, Long id, int nivel, String coluna) {
        if (nivel <= 0) {
            return Lists.newLinkedList();
        }
        String select = "select * from (select distinct" +
            " rev.datahora as Data_Da_Alteração," +
            " rev.id as Revisão," +
            " rev.ip as Ip_Do_Usuário," +
            " rev.usuario as Usuário_Auditoria," +
            " aud.id as ID ";

        for (Field field : Persistencia.getAtributos(filtro.getClasse())) {
            String alias = "aud";
            if (!field.getDeclaringClass().equals(filtro.getClasse())) {
                alias = "pai";
            }
            select = montarSelectPorTabelaveis(select, field, alias);
        }

        String tabela = getNomeTabela(filtro.getClasse());
        String sql = " from " + tabela.toUpperCase() + "_AUD aud " +
            " inner join REVISAOAUDITORIA rev on rev.id = aud.rev ";


        if (filtro.getClasse().getSuperclass() != null && filtro.getClasse().getSuperclass().isAnnotationPresent(Entity.class)) {
            String pai = getNomeTabela(filtro.getClasse().getSuperclass());
            sql += " inner join " + pai + "_aud pai on pai.id = aud.id and pai.rev = aud.rev ";
            select += ", pai.revtype ";
        } else {
            select += ", aud.revtype ";
        }

        if (id != null && Strings.isNullOrEmpty(coluna)) {
            sql += " where aud.id = " + id;
        } else if (id != null && !Strings.isNullOrEmpty(coluna)) {
            sql += " where " + coluna + " = " + id;
        }

        if (filtro.getDataInicial() != null) {
            sql += " and trunc(rev.datahora) >= to_date('" + DataUtil.getDataFormatada(filtro.getDataInicial()) + "', 'dd/MM/yyyy')";
        }

        if (filtro.getDataFinal() != null) {
            sql += " and trunc(rev.datahora) <= to_date('" + DataUtil.getDataFormatada(filtro.getDataFinal()) + "', 'dd/MM/yyyy')";
        }
        if (!Strings.isNullOrEmpty(filtro.getUsuario())) {
            sql += " and rev.usuario = '" + filtro.getUsuario() + "'";
        }
        sql += " order by rev.datahora desc )";

        sql += " where rownum <= " + filtro.getMaxResult();
        LinkedList<ClasseAuditada> result = new LinkedList<>(getJdbcTemplate().query(select + sql, new ConversorResultSet(filtro.getClasse(), 0, getNomeEntidade(filtro.getClasse()))));
        return result;
    }

    private String montarSelectPorTabelaveis(String select, Field field, String alias) {
        if (field.isAnnotationPresent(Tabelavel.class)
            && !field.isAnnotationPresent(Transient.class)
            && !Collection.class.isAssignableFrom(field.getType())) {

            if (field.isAnnotationPresent(Column.class) && !Strings.isNullOrEmpty(field.getAnnotation(Column.class).name())) {
                select += ", " + alias + "." + field.getAnnotation(Column.class).name();
            } else if (field.isAnnotationPresent(ManyToOne.class) || (field.isAnnotationPresent(OneToOne.class)
                && Strings.isNullOrEmpty((field.getAnnotation(OneToOne.class)).mappedBy()))) {
                if (field.isAnnotationPresent(JoinColumn.class)) {
                    select += ", " + alias + "." + field.getAnnotation(JoinColumn.class).name();
                } else {
                    select += ", " + alias + "." + field.getName().toLowerCase() + "_id ";
                }
            } else if (!field.isAnnotationPresent(ManyToOne.class) && !field.isAnnotationPresent(OneToOne.class)) {
                select += ", ";
                if (field.getType().equals(String.class)) {
                    select += "to_char(" + alias + "." + field.getName() + ") as " + field.getName();
                } else {
                    select += alias + "." + field.getName();
                }
            }
        }
        return select;
    }

    public List<ClasseAuditada> carregarAuditoria(final Class classe, final Long id, final Long rev, int nivel) {
        String tabela = getNomeEntidade(classe);
        return carregarAuditoria(classe, id, rev, nivel, tabela, null);
    }


    public Long buscarPrimeiraRevisaoAnterior(final Class classe, final Long id, final Long rev) {
        try {
            String select = "select * from (select " +
                " rev.id " +
                " from " + getNomeTabela(classe).toUpperCase() + "_AUD aud " +
                " inner join REVISAOAUDITORIA rev on rev.id = aud.rev " +
                " where rev.id < " + rev + " and aud.id = " + id +
                " order by rev.datahora desc) where rownum = 1";

            return getJdbcTemplate().queryForObject(select, Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<ClasseAuditada> carregarAuditoria(final Class classe, final Long id, final Long rev, int nivel, String label, String colunaWhere) {
        return carregarAuditoria(classe, id, rev, nivel, label, colunaWhere, null);
    }

    public List<ClasseAuditada> carregarAuditoria(final Class classe, final Long id, final Long rev, int nivel, String label, String colunaWhere, final Class classePai) {
        if (nivel <= 0) {
            return Lists.newArrayList();
        }
        String tabela = getNomeTabela(classe);
        String select = " select " +
            " rev.datahora as Data_Da_Alteração," +
            " rev.id as Revisão," +
            " rev.ip as Ip_Do_Usuário," +
            " rev.usuario as Usuário_Auditoria," +
            " aud.* ";


        String sql = " from " + tabela.toUpperCase() + "_AUD aud " +
            " inner join REVISAOAUDITORIA rev on rev.id = aud.rev ";

        if (classe.getSuperclass().isAnnotationPresent(Entity.class)) {
            String pai = getNomeTabela(classe.getSuperclass());
            sql += " inner join " + pai + "_aud pai on pai.id = aud.id and pai.rev = rev.id ";
            select += ", pai.* ";
        }

        if (classePai != null) {
            Long anteriorDoPai = buscarPrimeiraRevisaoAnterior(classePai, id, rev);
            sql += " where rev.id > " + anteriorDoPai + " and rev.id <= " + rev;
        } else {
            sql += " where rev.id = " + rev;
        }
        if (Strings.isNullOrEmpty(colunaWhere) && id != null) {
            sql += " and aud.id = " + id;
        }

        if (!Strings.isNullOrEmpty(colunaWhere)) {
            sql += " and (" + colunaWhere + " = " + id + " or (aud.revtype = 2 and " + colunaWhere + " is null ) )";
        }

        return getJdbcTemplate().query(select + sql, new ConversorResultSet(classe, nivel, label));
    }

    public static String getNomeTabela(Class classe) {
        return Util.getNomeTabela(classe);
    }

    public static String getNomeEntidade(Class classe) {
        return classe == null ? "" : classe.isAnnotationPresent(Etiqueta.class)
            ? ((Etiqueta) classe.getAnnotation(Etiqueta.class)).value() :
            classe.getSimpleName().replaceAll("([a-z]+)([A-Z])", "$1 $2");
    }

    public RevisaoAuditoria buscarRevisaoAuditoria(Long rev) {
        try {
            String select = "select id, dataHora, usuario, ip from revisaoauditoria where  id = ?";
            return (RevisaoAuditoria) getJdbcTemplate().queryForObject(select, new Object[]{rev}, new RevisaoAuditoriaRowMapper());
        } catch (Exception e) {
            return null;
        }
    }


    private class ConversorResultSet implements ResultSetExtractor<List<ClasseAuditada>> {
        Class classe;
        String label;
        Set<Field> atributos;
        Set<Field> atributosManyToOne;
        Set<Field> atributosOneToOne;
        Set<Field> atributosOneToMany;
        Map<Field, String> fieldsPorNome;
        int nivel;

        public ConversorResultSet(Class classe, int nivel, String label) {
            this.classe = classe;
            this.nivel = nivel;
            this.label = label;
            atributos = Sets.newHashSet(Persistencia.getAtributos(classe));
            atributosManyToOne = Sets.newHashSet();
            atributosOneToOne = Sets.newHashSet();
            atributosOneToMany = Sets.newHashSet();
            fieldsPorNome = Maps.newHashMap();
            for (Field atributo : atributos) {
                if (atributo.isAnnotationPresent(ManyToOne.class)) {
                    atributosManyToOne.add(atributo);
                }
                if (atributo.isAnnotationPresent(OneToMany.class)) {
                    atributosOneToMany.add(atributo);
                }
                if (atributo.isAnnotationPresent(OneToOne.class)) {
                    atributosOneToOne.add(atributo);
                }
            }
        }

        private String getNomeCampo(String coluna) {
            String name = coluna.toLowerCase();
            if (name.endsWith("_id")) {
                name = name.replaceAll("_id", "");
            }
            name = StringUtils.capitalize(name.replace("_", " "));

            for (Field atributo : atributos) {

                if (atributo.getName().toLowerCase().equals(name.toLowerCase())) {
                    if (atributo.isAnnotationPresent(Etiqueta.class) && !atributo.isAnnotationPresent(Id.class)) {
                        name = atributo.getAnnotation(Etiqueta.class).value();
                    } else {
                        name = StringUtils.capitalize(atributo.getName()).replaceAll("([a-z]+)([A-Z])", "$1 $2");
                    }
                    fieldsPorNome.put(atributo, name);
                    break;

                }
            }
            return name;
        }

        @Override
        public List<ClasseAuditada> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<ClasseAuditada> retorno = Lists.newLinkedList();
            if (rs != null) {
                while (rs.next()) {
                    Map<String, Object> atributoValor = Maps.newLinkedHashMap();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        int type = rsmd.getColumnType(i);
                        String name = getNomeCampo(rsmd.getColumnName(i));

                        if (name.equals("Revtype")) {
                            int rev = rs.getInt(i);
                            atributoValor.put(name, rev == 0 ? "Inclusão" : rev == 1 ? "Alteração" : "Exclusão");
                        } else if (type == Types.VARCHAR || type == Types.CHAR) {
                            atributoValor.put(name, rs.getString(i));
                        } else if (type == Types.DATE) {
                            atributoValor.put(name, DataUtil.getDataFormatada(rs.getDate(i)));
                        } else if (type == Types.TIMESTAMP) {
                            atributoValor.put(name, DataUtil.getDataFormatadaDiaHora(rs.getTimestamp(i)));
                        } else {
                            atributoValor.put(name, rs.getString(i));
                        }
                    }
                    ClasseAuditada e = new ClasseAuditada(classe, label, atributoValor);
                    retorno.add(e);
                    for (Field field : atributosManyToOne) {
                        extractField(retorno, atributoValor, e, field);

                    }
                    for (Field field : atributosOneToOne) {
                        extractField(retorno, atributoValor, e, field);

                    }
                    for (Field field : atributosOneToMany) {
                        try {
                            Class classe = null;

                            if (field.getType().equals(List.class)) {
                                classe = getGenericTypeFromCollection(field, classe);
                            }
                            if (classe == null) {
                                continue;
                            }
                            String coluna = field.getAnnotation(OneToMany.class).mappedBy();
                            if (Strings.isNullOrEmpty(coluna)) {
                                continue;
                            }
                            String label;
                            if (field.isAnnotationPresent(Etiqueta.class)) {
                                label = field.getAnnotation(Etiqueta.class).value();
                            } else {
                                label = StringUtils.capitalize(field.getName().toLowerCase());
                            }
                            if (atributoValor.get("Id") != null) {
                                List<ClasseAuditada> listaAtributosDoOutro =
                                    carregarAuditoria(classe,
                                        Long.valueOf((String) atributoValor.get("Id")),
                                        Long.valueOf((String) atributoValor.get("Revisão")),
                                        nivel - 1,
                                        label,
                                        coluna + "_id", this.classe);
                                retorno.addAll(listaAtributosDoOutro);
                            }
                        } catch (NumberFormatException ex) {
                        } catch (Exception ex) {
                            logger.error("Não deu certo ", ex);
                        }
                    }
                }
            }
            return retorno;
        }

        private void extractField(List<ClasseAuditada> retorno, Map<String, Object> atributoValor, ClasseAuditada e, Field field) {
            try {

                if (atributoValor.get(fieldsPorNome.get(field)) == null) {
                    return;
                }

                List<ClasseAuditada> listaAtributosDoOutro =
                    carregarAuditoria(field.getType(),
                        Long.valueOf((String) atributoValor.get(fieldsPorNome.get(field))),
                        Long.valueOf((String) atributoValor.get("Revisão")),
                        nivel - 1,
                        fieldsPorNome.get(field),
                        null);

                trocarIdDaColunaPorValores(retorno, atributoValor, e, field, listaAtributosDoOutro);
            } catch (NumberFormatException ex) {

            } catch (Exception ex) {
                logger.error("Não deu certo ", ex);
            }
        }

        private void trocarIdDaColunaPorValores(List<ClasseAuditada> retorno, Map<String, Object> atributoValor, ClasseAuditada e, Field field, List<ClasseAuditada> listaAtributosDoOutro) {
            LinkedList<ClasseAuditada> colunasBasicas = listarAtributosBasicos(field.getType(), Long.valueOf((String) atributoValor.get(fieldsPorNome.get(field))));
            if (!colunasBasicas.isEmpty()) {
                String colunasBasicasAssociacao = "";
                for (String s : colunasBasicas.get(0).getAtributos().keySet()) {
                    if (colunasBasicas.get(0).getAtributos().get(s) != null)
                        colunasBasicasAssociacao += s + ": " + "<b>" + colunasBasicas.get(0).getAtributos().get(s) + "</b>, ";
                }
                if (!Strings.isNullOrEmpty(colunasBasicasAssociacao)) {
                    e.getAtributos().put(fieldsPorNome.get(field), "[" + colunasBasicasAssociacao + "]");
                }

                retorno.addAll(listaAtributosDoOutro);
            }
        }
    }

    public static Class getGenericTypeFromCollection(Field field, Class classe) {
        ParameterizedType pt = (ParameterizedType) field.getGenericType();
        for (Type t : pt.getActualTypeArguments()) {
            if (Persistencia.isEntidade((Class) t)) {
                classe = (Class) t;
            }
        }
        return classe;
    }

    public List<Long> buscarIdsDosFilhos(Class classe, String where) {
        String select = " select distinct id from " + getNomeTabela(classe) + "_aud where " + where;
        return getJdbcTemplate().queryForList(select, Long.class);
    }

    public String buscarNomeUsuario(String usuario) {
        try {
            String sql = " select pf.nome from usuariosistema us " +
                " inner join pessoafisica pf on pf.id = us.pessoafisica_id " +
                " where us.login = '" + usuario + "' fetch first 1 rows only ";
            return getJdbcTemplate().queryForObject(sql, String.class);
        } catch (Exception e) {
            return usuario;
        }
    }

    public static class ClasseAuditada {
        private Class classe;
        private String nome;
        private Map<String, Object> atributos;

        public ClasseAuditada(Class classe, String nome, Map<String, Object> atributos) {
            this.classe = classe;
            this.nome = nome;
            this.atributos = atributos;
        }

        public Class getClasse() {
            return classe;
        }

        public String getNome() {
            if (ConcessaoFeriasLicenca.class.equals(getClasse()) && TipoAuditoriaRH.CONCESSAO_LICENCA.equals(Web.pegaDaSessao("CONCESSAO"))) {
                Web.poeNaSessao("CONCESSAO", TipoAuditoriaRH.CONCESSAO_LICENCA);
                return TipoAuditoriaRH.CONCESSAO_LICENCA.getDescricao();
            }
            return nome;
        }

        public Map<String, Object> getAtributos() {
            return atributos;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClasseAuditada that = (ClasseAuditada) o;

            return classe != null ? classe.equals(that.classe) : that.classe == null;
        }

        @Override
        public int hashCode() {
            return classe != null ? classe.hashCode() : 0;
        }
    }


    public static class FiltroClasseAuditada {
        private Date dataInicial;
        private Date dataFinal;
        private String usuario;
        private Integer maxResult;
        private List<Pagina> paginas;
        private Pagina paginaAtual;
        private Integer totalRegistros;

        private Integer nivel;
        private Class classe;

        public FiltroClasseAuditada() {
            nivel = 4;
            dataInicial = DataUtil.getDateParse("16/01/2015");
            dataFinal = new Date();
            maxResult = 999;
            paginas = Lists.newArrayList();
            paginaAtual = new Pagina(0, maxResult, 1);
        }

        public Date getDataInicial() {
            return dataInicial;
        }

        public void setDataInicial(Date dataInicial) {
            this.dataInicial = dataInicial;
        }

        public Date getDataFinal() {
            return dataFinal;
        }

        public void setDataFinal(Date dataFinal) {
            this.dataFinal = dataFinal;
        }

        public Integer getMaxResult() {
            return maxResult;
        }

        public void setMaxResult(Integer maxResult) {
            this.maxResult = maxResult;
        }

        public String getUsuario() {
            return usuario;
        }

        public void setUsuario(String usuario) {
            this.usuario = usuario;
        }

        public Class getClasse() {
            return classe;
        }

        public void setClasse(Class classe) {
            this.classe = classe;
        }

        public Integer getNivel() {
            return nivel;
        }

        public void setNivel(Integer nivel) {
            this.nivel = nivel;
        }

        public List<Pagina> getPaginas() {
            return paginas;
        }

        public Integer getTotalRegistros() {
            return totalRegistros;
        }

        public void setTotalRegistros(Integer totalRegistros) {
            this.totalRegistros = totalRegistros;
            definirPaginas();
        }

        public static FiltroClasseAuditada copy(FiltroClasseAuditada filtro, Class aClass) {
            FiltroClasseAuditada retorno = new FiltroClasseAuditada();
            retorno.setClasse(aClass);
            retorno.setDataInicial(filtro.getDataInicial());
            retorno.setDataFinal(filtro.getDataFinal());
            retorno.setMaxResult(filtro.getMaxResult());
            retorno.setNivel(filtro.getNivel());
            return retorno;
        }

        public Pagina getPaginaAtual() {
            return paginaAtual;
        }

        public void definirPaginas() {
            if (totalRegistros > maxResult) {
                int totalPaginas = totalRegistros / maxResult;
                int ultimaPagina = 0;
                for (Integer i = 0; i < totalPaginas; i++) {
                    paginas.add(new Pagina(ultimaPagina, ultimaPagina + maxResult, i + 1));
                    ultimaPagina += maxResult + 1;
                }
            } else {
                paginas.add(new Pagina(0, totalRegistros, 1));
            }
            paginaAtual = paginas.get(0);
        }

    }

    public static class Pagina {
        int linhaInicial;
        int linhaFinal;
        int numero;

        public Pagina(int linhaInicial, int linhaFinal, int numero) {
            this.linhaInicial = linhaInicial;
            this.linhaFinal = linhaFinal;
            this.numero = numero;
        }

        public int getNumero() {
            return numero;
        }

        public void setNumero(int numero) {
            this.numero = numero;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pagina pagina = (Pagina) o;

            if (linhaInicial != pagina.linhaInicial) return false;
            if (linhaFinal != pagina.linhaFinal) return false;
            return numero == pagina.numero;
        }

        @Override
        public int hashCode() {
            int result = linhaInicial;
            result = 31 * result + linhaFinal;
            result = 31 * result + numero;
            return result;
        }
    }
}
