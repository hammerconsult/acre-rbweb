package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.Telefone;
import br.com.webpublico.negocios.tributario.rowmapper.PessoaRowMapper;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.repositorios.jdbc.util.JDBCUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository(value = "consultaPessoaDAO")
public class JdbcPessoaDAO extends JdbcDaoSupport implements Serializable {
    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcPessoaDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<Pessoa> lista(String filtro) {
        filtro = filtro.trim().replace("-", "").replace("/", "").replace(".", "").toLowerCase();
        String sql = "SELECT " +
            "  P.ID, " +
            "  PF.NOME, " +
            "  PF.CPF, " +
            "  PJ.NOMEFANTASIA, " +
            "  PJ.RAZAOSOCIAL, " +
            "  PJ.CNPJ, " +
            "  CASE  WHEN PF.ID IS NOT NULL THEN 'F' ELSE 'J' END TIPOPESSOA " +
            " FROM " +
            "  PESSOA P " +
            " LEFT JOIN PESSOAFISICA PF " +
            " ON " +
            "  P.ID = PF.ID " +
            " LEFT JOIN PESSOAJURIDICA PJ " +
            " ON " +
            "  PJ.ID = P.ID " +
            " WHERE " +
            "  (LOWER(PF.NOME) LIKE '%" + filtro + "%' OR " +
            "  LOWER(PJ.RAZAOSOCIAL) LIKE '%" + filtro + "%' OR " +
            "  LOWER((REPLACE(REPLACE(REPLACE(PF.CPF,'.',''),'-',''),'/',''))) LIKE '%" + filtro + "%' OR " +
            "  LOWER((REPLACE(REPLACE(REPLACE(PJ.CNPJ,'.',''),'-',''),'/',''))) LIKE '%" + filtro + "%' OR " +
            "  LOWER(PJ.NOMEFANTASIA) LIKE '%" + filtro + "%') AND " +
            "  ROWNUM <= 10";
        List<Pessoa> lista = getJdbcTemplate().query(sql, new Object[]{}, new PessoaRowMapper());
        return lista;
    }

    public Pessoa inserir(Pessoa pessoa) {
        pessoa.setId(geradorDeIds.getProximoId());
        String sql = "INSERT INTO Pessoa " +
            "(ID, situacaoCadastralPessoa) " +
            "VALUES (" + pessoa.getId() + ", '" + pessoa.getSituacaoCadastralPessoa().name() + "')";
        getJdbcTemplate().update(sql);
        sql = "INSERT INTO PessoaJuridica " +
            "(ID, CNPJ, RAZAOSOCIAL) " +
            "VALUES (" + pessoa.getId() + ", '" + pessoa.getCpf_Cnpj() + "', '" + pessoa.getNome().replace("'", "") + "')";
        getJdbcTemplate().update(sql);
        return pessoa;
    }

    public void update(Long rev, List<PessoaFisica> pessoasFisicas) {
        updatePessoaFisica(pessoasFisicas);
        insertPessoaAud(rev, 1L, pessoasFisicas);
        insertPessoaFisicaAud(rev, 1L, pessoasFisicas);
    }

    public void insert(Long rev, List<PessoaFisica> pessoasFisicas) {
        insertPessoa(pessoasFisicas);
        insertPessoaFisica(pessoasFisicas);
        insertPessoaAud(rev, 1L, pessoasFisicas);
        insertPessoaFisicaAud(rev, 1L, pessoasFisicas);
    }

    public void updateEnderecoAndTelefonePessoa(List<PessoaFisica> pessoasFisicas,
                                                List<EnderecoCorreio> enderecosCorreio,
                                                List<Telefone> telefones) {
        for (PessoaFisica pessoasFisica : pessoasFisicas) {
            for (EnderecoCorreio enderecoCorreio : enderecosCorreio) {
                if (enderecoCorreio.getPessoaFisica().getId().equals(pessoasFisica.getId())) {
                    updateEnderecoPessoa(pessoasFisica.getId(), enderecoCorreio.getId());
                }
            }
            for (Telefone telefone : telefones) {
                if (telefone.getPessoa().getId().equals(pessoasFisica.getId())) {
                    updateTelefonePessoa(telefone.getPessoa().getId(), telefone.getId());
                }
            }
        }
    }

    private void updateEnderecoPessoa(Long idPessoa, Long idEndereco) {
        String sql = " UPDATE PESSOA " +
            " SET ENDERECOPRINCIPAL_ID = " + idEndereco +
            " WHERE ID = " + idPessoa;
        getJdbcTemplate().update(sql);
    }

    public void updateEnderecoCorreio(EnderecoCorreio enderecoCorreio) {
        String sql = " UPDATE ENDERECOCORREIO " +
            " SET tipoendereco = '" + enderecoCorreio.getTipoEndereco().name() + "'" +
            " , principal = " + (enderecoCorreio.getPrincipal() ? "1" : "0") +
            " WHERE ID = " + enderecoCorreio.getId();
        getJdbcTemplate().update(sql);
    }

    private void updateTelefonePessoa(Long idPessoa, Long idTelefone) {
        String sql = "UPDATE PESSOA " +
            " SET TELEFONEPRINCIPAL_ID = " + idTelefone +
            " WHERE ID = " + idPessoa;
        getJdbcTemplate().update(sql);
    }

    private void updatePessoaFisica(List<PessoaFisica> pessoasFisicas) {
        getJdbcTemplate().batchUpdate(" update pessoa set email = ? where id = ? ",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    PessoaFisica pessoaFisica = pessoasFisicas.get(i);
                    JDBCUtil.setString(preparedStatement, 1, pessoaFisica.getEmail());
                    JDBCUtil.setLong(preparedStatement, 2, pessoaFisica.getId());
                }

                @Override
                public int getBatchSize() {
                    return pessoasFisicas.size();
                }
            });

        getJdbcTemplate().batchUpdate(" update pessoafisica set nome = ?, mae = ?, datanascimento = ? " +
                " where id = ? ",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    PessoaFisica pessoaFisica = pessoasFisicas.get(i);
                    JDBCUtil.setString(preparedStatement, 1, pessoaFisica.getNome());
                    JDBCUtil.setString(preparedStatement, 2, pessoaFisica.getMae());
                    JDBCUtil.setDate(preparedStatement, 3, pessoaFisica.getDataNascimento());
                    JDBCUtil.setLong(preparedStatement, 4, pessoaFisica.getId());
                }

                @Override
                public int getBatchSize() {
                    return pessoasFisicas.size();
                }
            });
    }

    private void insertPessoaFisica(List<PessoaFisica> pessoasFisicas) {
        getJdbcTemplate().batchUpdate(" insert into pessoafisica (id, cpf, datanascimento, " +
                " deficientefisico, doadorsangue, estadocivil, nome, mae, pai, racacor, sexo, tiposanguineo, " +
                " naturalidade_id, nivelescolaridade_id, tipodeficiencia, anochegada, datainvalidez, " +
                " nomeabreviado, nometratamento, nacionalidadepai_id, nacionalidademae_id, esferagoverno_id, " +
                " viveuniaoestavel, situacaoqualificacaocadastral, tipocondicaoingresso, casadobrasileiro, " +
                " filhobrasileiro, cotadeficiencia, observacaocotadeficiencia, acumulacargo, orgao, " +
                " localtrabalho) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                " ?, ?, ?, ?, ?, ?) ",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    PessoaFisica pessoaFisica = pessoasFisicas.get(i);
                    JDBCUtil.setLong(preparedStatement, 1, pessoaFisica.getId());
                    JDBCUtil.setString(preparedStatement, 2, pessoaFisica.getCpf());
                    JDBCUtil.setDate(preparedStatement, 3, pessoaFisica.getDataNascimento());
                    JDBCUtil.setBoolean(preparedStatement, 4, pessoaFisica.getDeficienteFisico());
                    JDBCUtil.setBoolean(preparedStatement, 5, pessoaFisica.getDoadorSangue());
                    JDBCUtil.setEnum(preparedStatement, 6, pessoaFisica.getEstadoCivil());
                    JDBCUtil.setString(preparedStatement, 7, pessoaFisica.getNome());
                    JDBCUtil.setString(preparedStatement, 8, pessoaFisica.getMae());
                    JDBCUtil.setString(preparedStatement, 9, pessoaFisica.getPai());
                    JDBCUtil.setEnum(preparedStatement, 10, pessoaFisica.getRacaCor());
                    JDBCUtil.setEnum(preparedStatement, 11, pessoaFisica.getSexo());
                    JDBCUtil.setEnum(preparedStatement, 12, pessoaFisica.getTipoSanguineo());
                    JDBCUtil.setEntity(preparedStatement, 13, pessoaFisica.getNaturalidade());
                    JDBCUtil.setEntity(preparedStatement, 14, pessoaFisica.getNivelEscolaridade());
                    JDBCUtil.setEnum(preparedStatement, 15, pessoaFisica.getTipoDeficiencia());
                    JDBCUtil.setInt(preparedStatement, 16, pessoaFisica.getAnoChegada());
                    JDBCUtil.setDate(preparedStatement, 17, pessoaFisica.getDataInvalidez());
                    JDBCUtil.setString(preparedStatement, 18, pessoaFisica.getNomeAbreviado());
                    JDBCUtil.setString(preparedStatement, 19, pessoaFisica.getNomeTratamento());
                    JDBCUtil.setEntity(preparedStatement, 20, pessoaFisica.getNacionalidadePai());
                    JDBCUtil.setEntity(preparedStatement, 21, pessoaFisica.getNacionalidadeMae());
                    JDBCUtil.setEntity(preparedStatement, 22, pessoaFisica.getEsferaGoverno());
                    JDBCUtil.setBoolean(preparedStatement, 23, pessoaFisica.getViveUniaoEstavel());
                    JDBCUtil.setEnum(preparedStatement, 24, pessoaFisica.getSituacaoQualificacaoCadastral());
                    JDBCUtil.setEnum(preparedStatement, 25, pessoaFisica.getTipoCondicaoIngresso());
                    JDBCUtil.setBoolean(preparedStatement, 26, pessoaFisica.getCasadoBrasileiro());
                    JDBCUtil.setBoolean(preparedStatement, 27, pessoaFisica.getFilhoBrasileiro());
                    JDBCUtil.setBoolean(preparedStatement, 28, pessoaFisica.getCotaDeficiencia());
                    JDBCUtil.setString(preparedStatement, 29, pessoaFisica.getObservacaoCotaDeficiencia());
                    JDBCUtil.setBoolean(preparedStatement, 30, pessoaFisica.getAcumulaCargo());
                    JDBCUtil.setString(preparedStatement, 31, pessoaFisica.getOrgao());
                    JDBCUtil.setString(preparedStatement, 32, pessoaFisica.getLocalTrabalho());
                }

                @Override
                public int getBatchSize() {
                    return pessoasFisicas.size();
                }
            });
    }

    private void insertPessoa(List<PessoaFisica> pessoasFisicas) {
        getJdbcTemplate().batchUpdate(" insert into pessoa (id, dataregistro, email, homepage, " +
                " contacorrentecontribuinte_id, nacionalidade_id, situacaocadastralpessoa, migracaochave, " +
                " unidadeorganizacional_id, motivo, bloqueado, classepessoa, unidadeexterna_id, observacao, " +
                " enderecoprincipal_id, profissao_id, telefoneprincipal_id, contacorrenteprincipal_id, " +
                " detentorarquivocomposicao_id, arquivo_id, codigocnaebi, key) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    PessoaFisica pessoaFisica = pessoasFisicas.get(i);
                    pessoaFisica.setId(geradorDeIds.getProximoId());
                    JDBCUtil.setLong(preparedStatement, 1, pessoaFisica.getId());
                    JDBCUtil.setDate(preparedStatement, 2, pessoaFisica.getDataRegistro());
                    JDBCUtil.setString(preparedStatement, 3, pessoaFisica.getEmail());
                    JDBCUtil.setString(preparedStatement, 4, pessoaFisica.getHomePage());
                    JDBCUtil.setEntity(preparedStatement, 5, pessoaFisica.getContaCorrenteContribuinte());
                    JDBCUtil.setEntity(preparedStatement, 6, pessoaFisica.getNacionalidade());
                    JDBCUtil.setEnum(preparedStatement, 7, pessoaFisica.getSituacaoCadastralPessoa());
                    JDBCUtil.setString(preparedStatement, 8, pessoaFisica.getMigracaoChave());
                    JDBCUtil.setEntity(preparedStatement, 9, pessoaFisica.getUnidadeOrganizacional());
                    JDBCUtil.setString(preparedStatement, 10, pessoaFisica.getMotivo());
                    JDBCUtil.setBoolean(preparedStatement, 11, pessoaFisica.getBloqueado());
                    JDBCUtil.setEnum(preparedStatement, 12, pessoaFisica.getClassePessoa());
                    JDBCUtil.setEntity(preparedStatement, 13, pessoaFisica.getUnidadeExterna());
                    JDBCUtil.setString(preparedStatement, 14, pessoaFisica.getObservacao());
                    JDBCUtil.setEntity(preparedStatement, 15, pessoaFisica.getEnderecoPrincipal());
                    JDBCUtil.setEntity(preparedStatement, 16, pessoaFisica.getProfissao());
                    JDBCUtil.setEntity(preparedStatement, 17, pessoaFisica.getEnderecoPrincipal());
                    JDBCUtil.setEntity(preparedStatement, 18, pessoaFisica.getContaCorrentePrincipal());
                    JDBCUtil.setEntity(preparedStatement, 19, pessoaFisica.getDetentorArquivoComposicao());
                    JDBCUtil.setEntity(preparedStatement, 20, pessoaFisica.getArquivo());
                    JDBCUtil.setString(preparedStatement, 21, pessoaFisica.getCodigoCnaeBI());
                    JDBCUtil.setString(preparedStatement, 22, pessoaFisica.getKey());
                }

                @Override
                public int getBatchSize() {
                    return pessoasFisicas.size();
                }
            });
    }

    private void insertPessoaFisicaAud(Long rev, Long revType, List<PessoaFisica> pessoasFisicas) {
        getJdbcTemplate().batchUpdate(" insert into pessoafisica_aud (id, rev, revtype, cpf, datanascimento, " +
                " deficientefisico, doadorsangue, estadocivil, nome, mae, pai, racacor, sexo, tiposanguineo, " +
                " naturalidade_id, nivelescolaridade_id, tipodeficiencia, anochegada, datainvalidez, " +
                " nomeabreviado, nometratamento, nacionalidadepai_id, nacionalidademae_id, esferagoverno_id, " +
                " viveuniaoestavel, situacaoqualificacaocadastral, tipocondicaoingresso, casadobrasileiro, " +
                " filhobrasileiro, cotadeficiencia, observacaocotadeficiencia, acumulacargo, orgao, " +
                " localtrabalho) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                " ?, ?, ?, ?, ?, ?) ",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    PessoaFisica pessoaFisica = pessoasFisicas.get(i);
                    JDBCUtil.setLong(preparedStatement, 1, pessoaFisica.getId());
                    JDBCUtil.setLong(preparedStatement, 2, rev);
                    JDBCUtil.setLong(preparedStatement, 3, revType);
                    JDBCUtil.setString(preparedStatement, 4, pessoaFisica.getCpf());
                    JDBCUtil.setDate(preparedStatement, 5, pessoaFisica.getDataNascimento());
                    JDBCUtil.setBoolean(preparedStatement, 6, pessoaFisica.getDeficienteFisico());
                    JDBCUtil.setBoolean(preparedStatement, 7, pessoaFisica.getDoadorSangue());
                    JDBCUtil.setEnum(preparedStatement, 8, pessoaFisica.getEstadoCivil());
                    JDBCUtil.setString(preparedStatement, 9, pessoaFisica.getNome());
                    JDBCUtil.setString(preparedStatement, 10, pessoaFisica.getMae());
                    JDBCUtil.setString(preparedStatement, 11, pessoaFisica.getPai());
                    JDBCUtil.setEnum(preparedStatement, 12, pessoaFisica.getRacaCor());
                    JDBCUtil.setEnum(preparedStatement, 13, pessoaFisica.getSexo());
                    JDBCUtil.setEnum(preparedStatement, 14, pessoaFisica.getTipoSanguineo());
                    JDBCUtil.setEntity(preparedStatement, 15, pessoaFisica.getNaturalidade());
                    JDBCUtil.setEntity(preparedStatement, 16, pessoaFisica.getNivelEscolaridade());
                    JDBCUtil.setEnum(preparedStatement, 17, pessoaFisica.getTipoDeficiencia());
                    JDBCUtil.setInt(preparedStatement, 18, pessoaFisica.getAnoChegada());
                    JDBCUtil.setDate(preparedStatement, 19, pessoaFisica.getDataInvalidez());
                    JDBCUtil.setString(preparedStatement, 20, pessoaFisica.getNomeAbreviado());
                    JDBCUtil.setString(preparedStatement, 21, pessoaFisica.getNomeTratamento());
                    JDBCUtil.setEntity(preparedStatement, 22, pessoaFisica.getNacionalidadePai());
                    JDBCUtil.setEntity(preparedStatement, 23, pessoaFisica.getNacionalidadeMae());
                    JDBCUtil.setEntity(preparedStatement, 24, pessoaFisica.getEsferaGoverno());
                    JDBCUtil.setBoolean(preparedStatement, 25, pessoaFisica.getViveUniaoEstavel());
                    JDBCUtil.setEnum(preparedStatement, 26, pessoaFisica.getSituacaoQualificacaoCadastral());
                    JDBCUtil.setEnum(preparedStatement, 27, pessoaFisica.getTipoCondicaoIngresso());
                    JDBCUtil.setBoolean(preparedStatement, 28, pessoaFisica.getCasadoBrasileiro());
                    JDBCUtil.setBoolean(preparedStatement, 29, pessoaFisica.getFilhoBrasileiro());
                    JDBCUtil.setBoolean(preparedStatement, 30, pessoaFisica.getCotaDeficiencia());
                    JDBCUtil.setString(preparedStatement, 31, pessoaFisica.getObservacaoCotaDeficiencia());
                    JDBCUtil.setBoolean(preparedStatement, 32, pessoaFisica.getAcumulaCargo());
                    JDBCUtil.setString(preparedStatement, 33, pessoaFisica.getOrgao());
                    JDBCUtil.setString(preparedStatement, 34, pessoaFisica.getLocalTrabalho());
                }

                @Override
                public int getBatchSize() {
                    return pessoasFisicas.size();
                }
            });
    }

    private void insertPessoaAud(Long rev, Long revType, List<PessoaFisica> pessoasFisicas) {
        getJdbcTemplate().batchUpdate(" insert into pessoa_aud (id, rev, revtype, dataregistro, email, homepage, " +
                " contacorrentecontribuinte_id, nacionalidade_id, situacaocadastralpessoa, migracaochave, " +
                " unidadeorganizacional_id, motivo, bloqueado, classepessoa, unidadeexterna_id, observacao, " +
                " enderecoprincipal_id, profissao_id, telefoneprincipal_id, contacorrenteprincipal_id, " +
                " detentorarquivocomposicao_id, arquivo_id, codigocnaebi, key) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    PessoaFisica pessoaFisica = pessoasFisicas.get(i);
                    JDBCUtil.setLong(preparedStatement, 1, pessoaFisica.getId());
                    JDBCUtil.setLong(preparedStatement, 2, rev);
                    JDBCUtil.setLong(preparedStatement, 3, revType);
                    JDBCUtil.setDate(preparedStatement, 4, pessoaFisica.getDataRegistro());
                    JDBCUtil.setString(preparedStatement, 5, pessoaFisica.getEmail());
                    JDBCUtil.setString(preparedStatement, 6, pessoaFisica.getHomePage());
                    JDBCUtil.setEntity(preparedStatement, 7, pessoaFisica.getContaCorrenteContribuinte());
                    JDBCUtil.setEntity(preparedStatement, 8, pessoaFisica.getNacionalidade());
                    JDBCUtil.setEnum(preparedStatement, 9, pessoaFisica.getSituacaoCadastralPessoa());
                    JDBCUtil.setString(preparedStatement, 10, pessoaFisica.getMigracaoChave());
                    JDBCUtil.setEntity(preparedStatement, 11, pessoaFisica.getUnidadeOrganizacional());
                    JDBCUtil.setString(preparedStatement, 12, pessoaFisica.getMotivo());
                    JDBCUtil.setBoolean(preparedStatement, 13, pessoaFisica.getBloqueado());
                    JDBCUtil.setEnum(preparedStatement, 14, pessoaFisica.getClassePessoa());
                    JDBCUtil.setEntity(preparedStatement, 15, pessoaFisica.getUnidadeExterna());
                    JDBCUtil.setString(preparedStatement, 16, pessoaFisica.getObservacao());
                    JDBCUtil.setEntity(preparedStatement, 17, pessoaFisica.getEnderecoPrincipal());
                    JDBCUtil.setEntity(preparedStatement, 18, pessoaFisica.getProfissao());
                    JDBCUtil.setEntity(preparedStatement, 19, pessoaFisica.getTelefonePrincipal());
                    JDBCUtil.setEntity(preparedStatement, 20, pessoaFisica.getContaCorrentePrincipal());
                    JDBCUtil.setEntity(preparedStatement, 21, pessoaFisica.getDetentorArquivoComposicao());
                    JDBCUtil.setEntity(preparedStatement, 22, pessoaFisica.getArquivo());
                    JDBCUtil.setString(preparedStatement, 23, pessoaFisica.getCodigoCnaeBI());
                    JDBCUtil.setString(preparedStatement, 24, pessoaFisica.getKey());
                }

                @Override
                public int getBatchSize() {
                    return pessoasFisicas.size();
                }
            });
    }
}
