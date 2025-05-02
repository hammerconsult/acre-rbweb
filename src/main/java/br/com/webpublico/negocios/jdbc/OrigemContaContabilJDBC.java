package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by venom on 24/02/15.
 */
public final class OrigemContaContabilJDBC extends ClassPatternJDBC {

    private static OrigemContaContabilJDBC instancia;
    private BancarioJDBC bancarioJDBC = BancarioJDBC.createInstance(conexao);
    private OrcamentoJDBC orcamentoJDBC = OrcamentoJDBC.createInstance(conexao);
    private CommonsJDBC commonsJDBC = CommonsJDBC.createInstance(conexao);
    private PreparedStatement psSelectCLP;
    private PreparedStatement psSelectLCP;
    private PreparedStatement psSelectTagOCC;
    private PreparedStatement psSelectOCCConta;
    private PreparedStatement psSelectOccBanco;
    private PreparedStatement psSelectOccGrupoBem;
    private PreparedStatement psSelectEventoContabil;
    private PreparedStatement psSelectOccClassePessoa;
    private PreparedStatement psSelectOccGrupoMaterial;
    private PreparedStatement psSelectOccOrigemRecurso;
    private PreparedStatement psSelectOccTipoPassivoAtuarial;
    private PreparedStatement psSelectOccNaturezaDividaPublica;
    private Map<Long, EventoContabil> eventos = new HashMap<>();
    private Map<MapCLP, CLP> clps = new HashMap<>();
    private Map<Long, TagOCC> tagsOCC = new HashMap<>();
    private HashMap<MapTagOcc, OrigemContaContabil> occsByTag = new HashMap<>();

    private OrigemContaContabilJDBC(Connection conn) {
        this.conexao = conn;
    }

    public static synchronized OrigemContaContabilJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new OrigemContaContabilJDBC(conn);
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public OCCGrupoMaterial getOccGrupoMaterialByMap(MapTagOcc map, Date data) {
        if (!occsByTag.containsKey(map)) {
            loadOccGrupoMaterialByMap(map, data);
        }
        return (OCCGrupoMaterial) occsByTag.get(map);
    }

    private void loadOccGrupoMaterialByMap(MapTagOcc map, Date data) {
        try {
            preparaSelectOccGrupoMaterial();
            psSelectOccGrupoMaterial.clearParameters();
            psSelectOccGrupoMaterial.setLong(1, map.getTag().getId());
            psSelectOccGrupoMaterial.setLong(2, Long.parseLong(map.getIdObjeto()));
            psSelectOccGrupoMaterial.setDate(3, (java.sql.Date) data);
            psSelectOccGrupoMaterial.setDate(4, (java.sql.Date) data);
            ResultSet rs = psSelectOccGrupoMaterial.executeQuery();
            if (rs.next()) {
                OCCGrupoMaterial occ = new OCCGrupoMaterial();
                occ.setId(rs.getLong("id"));
                occ.setGrupoMaterial(commonsJDBC.getGrupoMaterialById(rs.getLong("grupomaterial")));
                occ.setTagOCC(map.getTag());
                occ.setContaContabil((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("contacontabil")));
                occ.setReprocessar(rs.getBoolean("reprocessar"));
                occ.setContaIntra((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intra")));
                occ.setContaInterUniao((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interuniao")));
                occ.setContaInterEstado((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interestado")));
                occ.setContaInterMunicipal((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intermunicipal")));
                occ.setInicioVigencia(rs.getDate("iniciovigencia"));
                occ.setFimVigencia(rs.getDate("fimvigencia"));
                occsByTag.put(map, occ);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar OCC de grupo material: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectOccGrupoMaterial() throws SQLException {
        if (psSelectOccGrupoMaterial == null) {
            psSelectOccGrupoMaterial = this.conexao.prepareStatement(Queries.selectOccGrupoMaterial());
        }
    }

    public OCCGrupoBem getOccGrupoBemByMap(MapTagOcc map, Date data) {
        if (!occsByTag.containsKey(map)) {
            loadOccGrupoBemByMap(map, data);
        }
        return (OCCGrupoBem) occsByTag.get(map);
    }

    private void loadOccGrupoBemByMap(MapTagOcc map, Date data) {
        try {
            preparaSelectOccGrupoBem();
            psSelectOccGrupoBem.clearParameters();
            psSelectOccGrupoBem.setLong(1, map.getTag().getId());
            psSelectOccGrupoBem.setLong(2, Long.parseLong(map.getIdObjeto()));
            psSelectOccGrupoBem.setDate(3, (java.sql.Date) data);
            psSelectOccGrupoBem.setDate(4, (java.sql.Date) data);
            ResultSet rs = psSelectOccGrupoBem.executeQuery();
            if (rs.next()) {
                OCCGrupoBem occ = new OCCGrupoBem();
                occ.setId(rs.getLong("id"));
                occ.setGrupoBem(commonsJDBC.getGrupoBemById(rs.getLong("grupobem")));
                occ.setTipoGrupo(TipoGrupo.valueOf(rs.getString("tipogrupo")));
                occ.setTagOCC(map.getTag());
                occ.setContaContabil((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("contacontabil")));
                occ.setReprocessar(rs.getBoolean("reprocessar"));
                occ.setContaIntra((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intra")));
                occ.setContaInterUniao((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interuniao")));
                occ.setContaInterEstado((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interestado")));
                occ.setContaInterMunicipal((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intermunicipal")));
                occ.setInicioVigencia(rs.getDate("iniciovigencia"));
                occ.setFimVigencia(rs.getDate("fimvigencia"));
                occsByTag.put(map, occ);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar OCC grupo bem: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectOccGrupoBem() throws SQLException {
        if (psSelectOccGrupoBem == null) {
            psSelectOccGrupoBem = this.conexao.prepareStatement(Queries.selectOccGrupoBem());
        }
    }

    public OCCOrigemRecurso getOccOrigemRecursoByMap(MapTagOcc map, Date data) {
        if (!occsByTag.containsKey(map)) {
            loadOccOrigemRecursoByMap(map, data);
        }
        return (OCCOrigemRecurso) occsByTag.get(map);
    }

    private void loadOccOrigemRecursoByMap(MapTagOcc map, Date data) {
        try {
            preparaSelectOccOrigemRecurso();
            psSelectOccOrigemRecurso.clearParameters();
            psSelectOccOrigemRecurso.setLong(1, map.getTag().getId());
            psSelectOccOrigemRecurso.setLong(2, Long.parseLong(map.getIdObjeto()));
            psSelectOccOrigemRecurso.setDate(3, (java.sql.Date) data);
            psSelectOccOrigemRecurso.setDate(4, (java.sql.Date) data);
            ResultSet rs = psSelectOccOrigemRecurso.executeQuery();
            if (rs.next()) {
                OCCOrigemRecurso occ = new OCCOrigemRecurso();
                occ.setId(rs.getLong("id"));
                occ.setOrigemSuplementacaoORC(OrigemSuplementacaoORC.valueOf(rs.getString("origem")));
                occ.setTagOCC(map.getTag());
                occ.setContaContabil((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("contacontabil")));
                occ.setReprocessar(rs.getBoolean("reprocessar"));
                occ.setContaIntra((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intra")));
                occ.setContaInterUniao((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interuniao")));
                occ.setContaInterEstado((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interestado")));
                occ.setContaInterMunicipal((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intermunicipal")));
                occ.setInicioVigencia(rs.getDate("iniciovigencia"));
                occ.setFimVigencia(rs.getDate("fimvigencia"));
                occsByTag.put(map, occ);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar OCC origem recurso: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectOccOrigemRecurso() throws SQLException {
        if (psSelectOccOrigemRecurso == null) {
            psSelectOccOrigemRecurso = this.conexao.prepareStatement(Queries.selectOccOrigemRecurso());
        }
    }

    public OCCTipoPassivoAtuarial getOccTipoPassivoAtuarialByMap(MapTagOcc map, Date data) {
        if (!occsByTag.containsKey(map)) {
            loadOccTipoPassivoAtuarialByTag(map, data);
        }
        return (OCCTipoPassivoAtuarial) occsByTag.get(map);
    }

    private void loadOccTipoPassivoAtuarialByTag(MapTagOcc map, Date data) {
        try {
            preparaSelectOccTipoPassivoAtuarial();
            psSelectOccTipoPassivoAtuarial.clearParameters();
            psSelectOccTipoPassivoAtuarial.setLong(1, map.getTag().getId());
            psSelectOccTipoPassivoAtuarial.setLong(2, Long.parseLong(map.getIdObjeto()));
            psSelectOccTipoPassivoAtuarial.setDate(3, (java.sql.Date) data);
            psSelectOccTipoPassivoAtuarial.setDate(4, (java.sql.Date) data);
            ResultSet rs = psSelectOccTipoPassivoAtuarial.executeQuery();
            if (rs.next()) {
                OCCTipoPassivoAtuarial occ = new OCCTipoPassivoAtuarial();
                occ.setId(rs.getLong("id"));
                occ.setTipoPassivoAtuarial(commonsJDBC.getTipoPassivoAtuarialById(rs.getLong("passivo")));
                occ.setTagOCC(map.getTag());
                occ.setContaContabil((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("contacontabil")));
                occ.setReprocessar(rs.getBoolean("reprocessar"));
                occ.setContaIntra((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intra")));
                occ.setContaInterUniao((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interuniao")));
                occ.setContaInterEstado((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interestado")));
                occ.setContaInterMunicipal((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intermunicipal")));
                occ.setInicioVigencia(rs.getDate("iniciovigencia"));
                occ.setFimVigencia(rs.getDate("fimvigencia"));
                occsByTag.put(map, occ);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar OCC de tipo passivo atuarial: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectOccTipoPassivoAtuarial() throws SQLException {
        if (psSelectOccTipoPassivoAtuarial == null) {
            psSelectOccTipoPassivoAtuarial = this.conexao.prepareStatement(Queries.selectOccTipoPassivoAtuarial());
        }
    }

    public OCCNaturezaDividaPublica getOccNaturezaDividaPublicaByMap(MapTagOcc map, Date data) {
        if (!occsByTag.containsKey(map)) {
            loadOccNaturezaDividaPublicaByTag(map, data);
        }
        return (OCCNaturezaDividaPublica) occsByTag.get(map);
    }

    private void loadOccNaturezaDividaPublicaByTag(MapTagOcc map, Date data) {
        try {
            preparaSelectOccNaturezaDividaPublica();
            psSelectOccNaturezaDividaPublica.clearParameters();
            psSelectOccNaturezaDividaPublica.setLong(1, map.getTag().getId());
            psSelectOccNaturezaDividaPublica.setLong(2, Long.parseLong(map.getIdObjeto()));
            psSelectOccNaturezaDividaPublica.setDate(3, (java.sql.Date) data);
            psSelectOccNaturezaDividaPublica.setDate(4, (java.sql.Date) data);
            ResultSet rs = psSelectOccNaturezaDividaPublica.executeQuery();
            if (rs.next()) {
                OCCNaturezaDividaPublica occ = new OCCNaturezaDividaPublica();
                occ.setId(rs.getLong("id"));
                occ.setCategoriaDividaPublica(commonsJDBC.getCategoriaDividaPublicaById(rs.getLong("categoria")));
                occ.setTagOCC(map.getTag());
                occ.setContaContabil((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("contacontabil")));
                occ.setReprocessar(rs.getBoolean("reprocessar"));
                occ.setContaIntra((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intra")));
                occ.setContaInterUniao((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interuniao")));
                occ.setContaInterEstado((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interestado")));
                occ.setContaInterMunicipal((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intermunicipal")));
                occ.setInicioVigencia(rs.getDate("iniciovigencia"));
                occ.setFimVigencia(rs.getDate("fimvigencia"));
                occsByTag.put(map, occ);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar OCC de natureza da dívida pública: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectOccNaturezaDividaPublica() throws SQLException {
        if (psSelectOccNaturezaDividaPublica == null) {
            psSelectOccNaturezaDividaPublica = this.conexao.prepareStatement(Queries.selectOccNaturezaDividaPublica());
        }
    }

    public OccClassePessoa getOccClassePessoaByMap(MapTagOcc map, Date data) {
        if (!occsByTag.containsKey(map)) {
            loadOccClassePessoaByTag(map, data);
        }
        return (OccClassePessoa) occsByTag.get(map);
    }

    private void loadOccClassePessoaByTag(MapTagOcc map, Date data) {
        try {
            preparaSelectOccClassePessoa();
            psSelectOccClassePessoa.clearParameters();
            psSelectOccClassePessoa.setLong(1, map.getTag().getId());
            psSelectOccClassePessoa.setLong(2, Long.parseLong(map.getIdObjeto()));
            psSelectOccClassePessoa.setDate(3, (java.sql.Date) data);
            psSelectOccClassePessoa.setDate(4, (java.sql.Date) data);
            ResultSet rs = psSelectOccClassePessoa.executeQuery();
            if (rs.next()) {
                OccClassePessoa occ = new OccClassePessoa();
                occ.setId(rs.getLong("id"));
                occ.setClassePessoa(commonsJDBC.getClasseCredorById(rs.getLong("classepessoa")));
                occ.setTagOCC(map.getTag());
                occ.setContaContabil((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("contacontabil")));
                occ.setReprocessar(rs.getBoolean("reprocessar"));
                occ.setContaIntra((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intra")));
                occ.setContaInterUniao((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interuniao")));
                occ.setContaInterEstado((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interestado")));
                occ.setContaInterMunicipal((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intermunicipal")));
                occ.setInicioVigencia(rs.getDate("iniciovigencia"));
                occ.setFimVigencia(rs.getDate("fimvigencia"));
                occsByTag.put(map, occ);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar OCC de classe de pessoa");
        }
    }

    private void preparaSelectOccClassePessoa() throws SQLException {
        if (psSelectOccClassePessoa == null) {
            psSelectOccClassePessoa = this.conexao.prepareStatement(Queries.selectOccClassePessoa());
        }
    }

    public OCCBanco getOccBancoByMap(MapTagOcc map, Date data) {
        if (!occsByTag.containsKey(map)) {
            loadOccBancoByTag(map, data);
        }
        return (OCCBanco) occsByTag.get(map);
    }

    private void loadOccBancoByTag(MapTagOcc map, Date data) {
        try {
            preparaSelectOccBanco();
            psSelectOccBanco.clearParameters();
            psSelectOccBanco.setLong(1, map.getTag().getId());
            psSelectOccBanco.setLong(2, Long.parseLong(map.getIdObjeto()));
            psSelectOccBanco.setDate(3, (java.sql.Date) data);
            psSelectOccBanco.setDate(4, (java.sql.Date) data);
            ResultSet rs = psSelectOccBanco.executeQuery();
            if (rs.next()) {
                OCCBanco occ = new OCCBanco();
                occ.setId(rs.getLong("id"));
                occ.setSubConta(bancarioJDBC.getSubContaById(rs.getLong("subconta")));
                occ.setTagOCC(map.getTag());
                occ.setContaContabil((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("contacontabil")));
                occ.setReprocessar(rs.getBoolean("reprocessar"));
                occ.setContaIntra((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intra")));
                occ.setContaInterUniao((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interuniao")));
                occ.setContaInterEstado((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interestado")));
                occ.setContaInterMunicipal((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intermunicipal")));
                occ.setInicioVigencia(rs.getDate("iniciovigencia"));
                occ.setFimVigencia(rs.getDate("fimvigencia"));
                occsByTag.put(map, occ);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar occ de banco: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectOccBanco() throws SQLException {
        if (psSelectOccBanco == null) {
            psSelectOccBanco = this.conexao.prepareStatement(Queries.selectOccBanco());
        }
    }

    public OCCConta getOccContaByMap(MapTagOcc map, Date data) {
        if (!occsByTag.containsKey(map)) {
            loadOccContaByTag(map, data);
        }
        return (OCCConta) occsByTag.get(map);
    }

    private void loadOccContaByTag(MapTagOcc map, Date data) {
        try {
            preparaSelectOCCConta();
            psSelectOCCConta.clearParameters();
            psSelectOCCConta.setLong(1, map.getTag().getId());
            psSelectOCCConta.setLong(2, Long.parseLong(map.getIdObjeto()));
            psSelectOCCConta.setDate(3, (java.sql.Date) data);
            psSelectOCCConta.setDate(4, (java.sql.Date) data);
            ResultSet rs = psSelectOCCConta.executeQuery();
            if (rs.next()) {
                OCCConta occ = new OCCConta();
                occ.setId(rs.getLong("id"));
                occ.setContaOrigem(orcamentoJDBC.getContaById(rs.getLong("contaorigem")));
                occ.setTagOCC(map.getTag());
                occ.setContaContabil((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("contacontabil")));
                occ.setReprocessar(rs.getBoolean("reprocessar"));
                occ.setContaIntra((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intra")));
                occ.setContaInterUniao((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interuniao")));
                occ.setContaInterEstado((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("interestado")));
                occ.setContaInterMunicipal((ContaContabil) orcamentoJDBC.getContaById(rs.getLong("intermunicipal")));
                occ.setInicioVigencia(rs.getDate("iniciovigencia"));
                occ.setFimVigencia(rs.getDate("fimvigencia"));
                occsByTag.put(map, occ);
            } else {
                throw new ExcecaoNegocioGenerica("Occ não encontrada para a tag: " + map.toString());
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar OCC de Conta: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectOCCConta() throws SQLException {
        if (psSelectOCCConta == null) {
            psSelectOCCConta = this.conexao.prepareStatement(Queries.selectOccConta());
        }
    }

    public CLP selectCLP(EventoContabil evento, TagValor tag, Date data) throws SQLException {
        MapCLP map = new MapCLP(evento.getId(), tag, data);
        List<CLP> lista = new ArrayList<>();
        if (!clps.containsKey(map)) {
            preparaSelectClp();
            psSelectCLP.clearParameters();
            psSelectCLP.setString(1, tag.name());
            psSelectCLP.setLong(2, evento.getId());
            psSelectCLP.setDate(3, new java.sql.Date(data.getTime()));
            psSelectCLP.setDate(4, new java.sql.Date(data.getTime()));
            try (ResultSet rs = psSelectCLP.executeQuery()) {
                while (rs.next()) {
                    CLP clp = new CLP();
                    clp.setId(rs.getLong("id"));
                    clp.setCodigo(rs.getString("codigo"));
                    clp.setDescricao(rs.getString("descricao"));
                    clp.setNome(rs.getString("nome"));
                    clp.setExercicio(commonsJDBC.getExercicioById(rs.getLong("exercicioid")));
                    clp.setInicioVigencia(rs.getDate("iniciovigencia"));
                    clp.setFimVigencia(rs.getDate("fimvigencia"));
                    clp = getLcps(clp);
                    lista.add(clp);
                }
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao buscar clp: " + ex.getMessage(), ex);
            }
            if (lista.size() > 1) {
                throw new ExcecaoNegocioGenerica("Mais de uma clp encontrada!");
            } else {
                clps.put(map, lista.get(0));
            }
        }
        return clps.get(map);
    }

    private void preparaSelectClp() throws SQLException {
        if (psSelectCLP == null) {
            psSelectCLP = this.conexao.prepareStatement(Queries.selectCLP());
        }
    }

    private CLP getLcps(CLP clp) throws SQLException {
        clp.setLcps(new ArrayList<LCP>());
        preparaSelectLcp();
        psSelectLCP.clearParameters();
        psSelectLCP.setLong(1, clp.getId());
        try (ResultSet rs = psSelectLCP.executeQuery()) {
            while (rs.next()) {
                orcamentoJDBC.preparaListaContas(rs.getLong("contacredito"), rs.getLong("contadebito")
                        , rs.getLong("contacreditointra"), rs.getLong("contadebitointra")
                        , rs.getLong("contacreditointeruniao"), rs.getLong("contadebitointeruniao")
                        , rs.getLong("contacreditointerestado"), rs.getLong("contadebitointerestado")
                        , rs.getLong("contacreditointermunicipal"), rs.getLong("contadebitointermunicipal"));
                LCP lcp = new LCP();
                lcp.setId(rs.getLong("id"));
                lcp.setCodigo(rs.getString("codigo"));
                lcp.setObrigatorio(rs.getBoolean("obrigatorio"));
                lcp.setVariacao(rs.getBoolean("variacao"));
                lcp.setItem(rs.getInt("item"));
                lcp.setUsoInterno(rs.getBoolean("usointerno"));
                lcp.setTipoMovimentoTCECredito(TipoMovimentoTCE.valueOf(rs.getString("tipomovtcecredito")));
                lcp.setTipoMovimentoTCEDebito(TipoMovimentoTCE.valueOf(rs.getString("tipomovtcedebito")));
                lcp.setContaCredito(orcamentoJDBC.getContaById(rs.getLong("contacredito")));
                lcp.setContaDebito(orcamentoJDBC.getContaById(rs.getLong("contadebito")));
                lcp.setContaCreditoInterEstado(orcamentoJDBC.getContaById(rs.getLong("contacreditointerestado")));
                lcp.setContaDebitoInterEstado(orcamentoJDBC.getContaById(rs.getLong("contadebitointerestado")));
                lcp.setContaCreditoInterUniao(orcamentoJDBC.getContaById(rs.getLong("contacreditointeruniao")));
                lcp.setContaDebitoInterUniao(orcamentoJDBC.getContaById(rs.getLong("contadebitointeruniao")));
                lcp.setContaCreditoInterMunicipal(orcamentoJDBC.getContaById(rs.getLong("contacreditointermunicipal")));
                lcp.setContaDebitoInterMunicipal(orcamentoJDBC.getContaById(rs.getLong("contadebitointermunicipal")));
                lcp.setTagOCCCredito(getTagOCC(rs.getLong("tagocccredito")));
                lcp.setTagOCCDebito(getTagOCC(rs.getLong("tagoccdebito")));
                lcp.setTipoContaAuxiliarCredito(orcamentoJDBC.getTipoContaAuxiliar(rs.getLong("tipoauxiliarcredito")));
                lcp.setTipoContaAuxiliarDebito(orcamentoJDBC.getTipoContaAuxiliar(rs.getLong("tipoauxiliardebito")));
                lcp.setClp(clp);
                clp.getLcps().add(lcp);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar lcp's: " + ex.getMessage(), ex);
        }
        return clp;
    }

    private void preparaSelectLcp() throws SQLException {
        if (psSelectLCP == null) {
            psSelectLCP = this.conexao.prepareStatement(Queries.selectLCP());
        }
    }

    private TagOCC getTagOCC(Long id) {
        if (id != null) {
            if (tagsOCC.isEmpty()) {
                try {
                    loadTagsOCC();
                } catch (SQLException ex) {
                    throw new ExcecaoNegocioGenerica("Erro ao buscar tags occ: " + ex.getMessage(), ex);
                }
            }
            return tagsOCC.get(id);
        } else {
            return null;
        }
    }

    private void loadTagsOCC() throws SQLException {
        preparaSelectTagOCC();
        try (ResultSet rs = psSelectTagOCC.executeQuery()) {
            while (rs.next()) {
                TagOCC tag = new TagOCC();
                tag.setId(rs.getLong("id"));
                tag.setCodigo(rs.getString("codigo"));
                tag.setDescricao(rs.getString("descricao"));
                tag.setEntidadeOCC(EntidadeOCC.valueOf(rs.getString("entidadeocc")));
                if (!tagsOCC.containsKey(tag.getId())) {
                    tagsOCC.put(tag.getId(), tag);
                }
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar tags OCC: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectTagOCC() throws SQLException {
        if (psSelectTagOCC == null) {
            psSelectTagOCC = this.conexao.prepareStatement(Queries.selectTagOcc());
        }
    }

    public EventoContabil selectEventoContabilById(Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        if (!eventos.containsKey(id)) {
            preparaSelectEventoContabil();
            psSelectEventoContabil.clearParameters();
            psSelectEventoContabil.setLong(1, id);
            try (ResultSet rs = psSelectEventoContabil.executeQuery()) {
                if (rs.next()) {
                    EventoContabil ev = new EventoContabil();
                    ev.setId(rs.getLong("id"));
                    ev.setCodigo(rs.getString("codigo"));
                    ev.setDescricao(rs.getString("descricao"));
                    ev.setChave(rs.getString("chave"));
                    ev.setTipoBalancete(TipoBalancete.valueOf(rs.getString("tipobalancete")));
                    ev.setEventoTce(rs.getString("eventotce"));
                    ev.setTipoEventoContabil(TipoEventoContabil.valueOf(rs.getString("tipoeventocontabil")));
                    ev.setInicioVigencia(rs.getDate("iniciovigencia"));
                    ev.setFimVigencia(rs.getDate("fimvigencia"));
                    ev.setTipoOperacaoConciliacao(TipoOperacaoConcilicaoBancaria.valueOf(rs.getString("tipooperacaoconciliacao")));
                    eventos.put(id, ev);
                } else {
                    System.out.println("Não tem evento contábil para o pagamento!");
                }
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao buscar evento contábil: " + ex.getMessage(), ex);
            }
        }
        return eventos.get(id);
    }

    private void preparaSelectEventoContabil() throws SQLException {
        if (psSelectEventoContabil == null) {
            psSelectEventoContabil = this.conexao.prepareStatement(Queries.selectEventoContabil());
        }
    }
}
