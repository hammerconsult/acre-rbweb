package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;


@Service
public class ServiceAtualizacaoViewMaterializadaBI {
    private static final Logger logger = LoggerFactory.getLogger(ServiceAtualizacaoViewMaterializadaBI.class.getName());

    @PersistenceContext
    protected transient EntityManager em;

    @Transactional
    public void atualizarViewMaterializadaBI() {

        logger.debug("Iniciando atualizacao da view materializada VwCadastroEconomicoBIPessoaMAT");
        Connection conn = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
        try {
            String refresh = "{call DBMS_snapshot.refresh('VwCadastroEconomicoBIPessoaMAT')}";
            CallableStatement call = conn.prepareCall(refresh);
            call.execute();
        } catch (SQLException e) {
            logger.error("Erro sql, não foi possível atualizar a VwCadastroEconomicoBIPessoaMAT, verifique o log em DEBBUG");
            logger.debug("Tentando criar a view materializada");
            criarViewMaterializada();
        } catch (Exception ex) {
            logger.error("Não foi possível atualizar a VwCadastroEconomicoBIPessoaMAT, verifique o log em DEBBUG");
            throw new ExcecaoNegocioGenerica("Erro ao atualizar a View, " + ex.getMessage());
        } finally {
            logger.debug("Fechando conexão JDBC");
            AbstractReport.fecharConnection(conn);
        }
        logger.debug("Terminando atualizacao da view materializada VwCadastroEconomicoBIPessoaMAT");

    }


    public void criarViewMaterializada() {
        logger.debug("Iniciando criação da view materializada VwCadastroEconomicoBIPessoaMAT");
        em.createNativeQuery("CREATE MATERIALIZED VIEW VwCadastroEconomicoBIPessoaMAT " +
            "AS " +
            "SELECT distinct * " +
            "FROM VwCadastroEconomicoBIPessoa ").executeUpdate();
        logger.debug("Terminando criação da view materializada VwCadastroEconomicoBIPessoaMAT");
    }

}
