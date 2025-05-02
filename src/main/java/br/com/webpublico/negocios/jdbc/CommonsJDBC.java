package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by venom on 24/02/15.
 */
public final class CommonsJDBC extends ClassPatternJDBC {

    private static CommonsJDBC instancia;
    private PreparedStatement psSelectExercicio;
    private PreparedStatement psSelectClasseCredor;
    private PreparedStatement psSelectGrupoBem;
    private PreparedStatement psSelectGrupoMaterial;
    private PreparedStatement psSelectCategoriaDividaPublica;
    private PreparedStatement psSelectTipoPassivoAtuarial;
    private Map<Long, Exercicio> exercicios = new HashMap<>();
    private Map<Long, ClasseCredor> classesCredor = new HashMap<>();
    private Map<Long, CategoriaDividaPublica> categorias = new HashMap<>();
    private Map<Long, TipoPassivoAtuarial> tiposPassivoAtuarial = new HashMap<>();
    private Map<Long, GrupoBem> gruposBem = new HashMap<>();
    private Map<Long, GrupoMaterial> gruposMaterial = new HashMap<>();

    private CommonsJDBC(Connection conn) {
        this.conexao = conn;
    }

    public static synchronized CommonsJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new CommonsJDBC(conn);
            instancia.loadExercicios();
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public Exercicio getExercicioByAno(Integer ano) {
        if (ano == null || ano.equals("")) {
            return null;
        }
        if (exercicios.isEmpty() || !exercicios.containsKey(ano.longValue())) {
            loadExercicios();
        }
        return exercicios.get(ano.longValue());
    }

    public Exercicio getExercicioById(Long id) {
        if (id == null) {
            return null;
        }
        if (exercicios.isEmpty() || !exercicios.containsKey(id)) {
            loadExercicios();
        }
        return exercicios.get(id);
    }

    private void loadExercicios() {
        try {
            preparaSelectExercicios();
            ResultSet rs = psSelectExercicio.executeQuery();
            while (rs.next()) {
                Exercicio ex = new Exercicio();
                ex.setId(rs.getLong("id"));
                ex.setAno(rs.getInt("ano"));
                ex.setDataRegistro(rs.getDate("dataregistro"));
                if (!exercicios.containsKey(ex.getId())) {
                    exercicios.put(ex.getId(), ex);
                }
                if (!exercicios.containsKey(ex.getAno().longValue())) {
                    exercicios.put(ex.getAno().longValue(), ex);
                }
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar exercícios: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectExercicios() throws SQLException {
        if (psSelectExercicio == null) {
            psSelectExercicio = this.conexao.prepareStatement(Queries.selectExercicios());
        }
    }


    public ClasseCredor getClasseCredorById(Long id) {
        if (id == null) {
            return null;
        }
        if (classesCredor.isEmpty() || !classesCredor.containsKey(id)) {
            loadClassesCredor();
        }
        return classesCredor.get(id);
    }

    private void loadClassesCredor() {
        try {
            preparaSelectClassesCredor();
            ResultSet rs = psSelectClasseCredor.executeQuery();
            while (rs.next()) {
                ClasseCredor cc = new ClasseCredor();
                cc.setId(rs.getLong("id"));
                cc.setDescricao(rs.getString("descricao"));
                cc.setCodigo(rs.getString("codigo"));
                cc.setTipoClasseCredor(TipoClasseCredor.valueOf(rs.getString("tipoclasse")));
                cc.setAtivoInativo(SituacaoCadastralContabil.valueOf(rs.getString("ativo")));
                if (!classesCredor.containsKey(cc.getId())) {
                    classesCredor.put(cc.getId(), cc);
                }
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar classes de credor: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectClassesCredor() throws SQLException {
        if (psSelectClasseCredor == null) {
            psSelectClasseCredor = this.conexao.prepareStatement(Queries.selectClassesCredor());
        }
    }

    public CategoriaDividaPublica getCategoriaDividaPublicaById(Long id) {
        if (id == null) {
            return null;
        }
        if (categorias.isEmpty() || !categorias.containsKey(id)) {
            loadCategoriasDividaPublica();
        }
        return categorias.get(id);
    }

    private void loadCategoriasDividaPublica() {
        try {
            preparaSelectCategoriasDividaPublica();
            ResultSet rs = psSelectCategoriaDividaPublica.executeQuery();
            while (rs.next()) {
                CategoriaDividaPublica cdp = new CategoriaDividaPublica();
                cdp.setId(rs.getLong("id"));
                cdp.setCodigo(rs.getString("codigo"));
                cdp.setDescricao(rs.getString("descricao"));
                cdp.setSuperior(getCategoriaDividaPublicaById(rs.getLong("superior")));
                cdp.setNaturezaDividaPublica(NaturezaDividaPublica.valueOf(rs.getString("natureza")));
                cdp.setAtivoInativo(SituacaoCadastralCadastroEconomico.valueOf(rs.getString("ativo")));
                if (!categorias.containsKey(cdp.getId())) {
                    categorias.put(cdp.getId(), cdp);
                }
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar categorias de dívida pública: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectCategoriasDividaPublica() throws SQLException {
        if (psSelectCategoriaDividaPublica == null) {
            psSelectCategoriaDividaPublica = this.conexao.prepareStatement(Queries.selectCategoriasDividaPublica());
        }
    }

    public TipoPassivoAtuarial getTipoPassivoAtuarialById(Long id) {
        if (id == null) {
            return null;
        }
        if (tiposPassivoAtuarial.isEmpty() || !tiposPassivoAtuarial.containsKey(id)) {
            loadTiposPassivoAtuarial();
        }
        return tiposPassivoAtuarial.get(id);
    }

    private void loadTiposPassivoAtuarial() {
        try {
            preparaSelectTiposPassivoAtuarial();
            ResultSet rs = psSelectTipoPassivoAtuarial.executeQuery();
            while (rs.next()) {
                TipoPassivoAtuarial tpa = new TipoPassivoAtuarial();
                tpa.setId(rs.getLong("id"));
                tpa.setCodigo(rs.getString("codigo"));
                tpa.setDescricao(rs.getString("descricao"));
                if (!tiposPassivoAtuarial.containsKey(tpa.getId())) {
                    tiposPassivoAtuarial.put(tpa.getId(), tpa);
                }
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar tipo passivo atuarial: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectTiposPassivoAtuarial() throws SQLException {
        if (psSelectTipoPassivoAtuarial == null) {
            psSelectTipoPassivoAtuarial = this.conexao.prepareStatement(Queries.selectTiposPassivoAtuarial());
        }
    }

    public GrupoBem getGrupoBemById(Long id) {
        if (id == null) {
            return null;
        }
        if (gruposBem.isEmpty() || !gruposBem.containsKey(id)) {
            loadGruposBem();
        }
        return gruposBem.get(id);
    }

    private void loadGruposBem() {
        try {
            preparaSelectGruposBem();
            ResultSet rs = psSelectGrupoBem.executeQuery();
            while (rs.next()) {
                GrupoBem gb = new GrupoBem();
                gb.setId(rs.getLong("id"));
                gb.setCodigo(rs.getString("codigo"));
                gb.setDescricao(rs.getString("descricao"));
                gb.setSuperior(getGrupoBemById(rs.getLong("superior")));
                gb.setAtivoInativo(SituacaoCadastralContabil.valueOf(rs.getString("ativo")));
                gb.setTipoBem(TipoBem.valueOf(rs.getString("tipobem")));
                if (!gruposBem.containsKey(gb.getId())) {
                    gruposBem.put(gb.getId(), gb);
                }
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar grupos bem: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectGruposBem() throws SQLException {
        if (psSelectGrupoBem == null) {
            psSelectGrupoBem = this.conexao.prepareStatement(Queries.selectGruposBem());
        }
    }

    public GrupoMaterial getGrupoMaterialById(Long id) {
        if (id == null) {
            return null;
        }
        if (gruposMaterial.isEmpty() || !gruposMaterial.containsKey(id)) {
            loadGruposMaterial();
        }
        return gruposMaterial.get(id);
    }

    private void loadGruposMaterial() {
        try {
            preparaSelectGruposMaterial();
            ResultSet rs = psSelectGrupoMaterial.executeQuery();
            while (rs.next()) {
                GrupoMaterial gm = new GrupoMaterial();
                gm.setId(rs.getLong("id"));
                gm.setCodigo(rs.getString("codigo"));
                gm.setDescricao(rs.getString("descricao"));
                gm.setSuperior(getGrupoMaterialById(rs.getLong("superior")));
                gm.setAtivoInativo(SituacaoCadastralContabil.valueOf("ativo"));
                if (!gruposMaterial.containsKey(gm.getId())) {
                    gruposMaterial.put(gm.getId(), gm);
                }
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar grupos de material: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectGruposMaterial() throws SQLException {
        if (psSelectGrupoMaterial == null) {
            psSelectGrupoMaterial = this.conexao.prepareStatement(Queries.selectGruposMaterial());
        }
    }
}
