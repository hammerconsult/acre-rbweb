package br.com.webpublico.negocios.comum.dao;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.entidades.ArquivoParte;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.negocios.comum.dao.setter.ArquivoComposicaoSetter;
import br.com.webpublico.negocios.comum.dao.setter.ArquivoParteSetter;
import br.com.webpublico.negocios.comum.dao.setter.ArquivoSetter;
import br.com.webpublico.negocios.comum.dao.setter.DetentorArquivoComposicaoSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

/**
 * Created by wellington on 27/06/17.
 */
@Repository
public class JdbcDetentorArquivoComposicaoDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcDetentorArquivoComposicaoDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public synchronized void persistirDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        List<DetentorArquivoComposicao> detentoresArquivoComposicao = Lists.newArrayList();
        detentoresArquivoComposicao.add(detentorArquivoComposicao);
        persistirDetentorArquivoComposicao(detentoresArquivoComposicao);
    }

    public synchronized void persistirDetentorArquivoComposicao(List<DetentorArquivoComposicao> detentoresArquivoComposicao) {
        getJdbcTemplate().batchUpdate(DetentorArquivoComposicaoSetter.SQL_INSERT_DETENTOR_ARQUIVO_COMPOSICAO,
            new DetentorArquivoComposicaoSetter(detentoresArquivoComposicao, geradorDeIds));
    }

    public synchronized void persistirArquivoComposicao(ArquivoComposicao arquivoComposicao) {
        List<ArquivoComposicao> arquivosComposicao = Lists.newArrayList();
        arquivosComposicao.add(arquivoComposicao);
        persistirArquivoComposicao(arquivosComposicao);
    }

    public synchronized void persistirArquivoComposicao(List<ArquivoComposicao> arquivosComposicao) {
        getJdbcTemplate().batchUpdate(ArquivoComposicaoSetter.SQL_INSERT_ARQUIVO_COMPOSICAO,
            new ArquivoComposicaoSetter(arquivosComposicao, geradorDeIds));
    }

    public synchronized void persistirArquivo(Arquivo arquivo) {
        List<Arquivo> arquivos = Lists.newArrayList();
        arquivos.add(arquivo);
        persistirArquivo(arquivos);
    }

    public synchronized void persistirArquivo(List<Arquivo> arquivos) {
        getJdbcTemplate().batchUpdate(ArquivoSetter.SQL_INSERT_ARQUIVO,
            new ArquivoSetter(arquivos, geradorDeIds));
    }

    public synchronized void persistirArquivoParte(ArquivoParte arquivoParte) {
        List<ArquivoParte> arquivoPartes = Lists.newArrayList();
        arquivoPartes.add(arquivoParte);
        persistirArquivoParte(arquivoPartes);
    }

    public synchronized void persistirArquivoParte(List<ArquivoParte> aquivoPartes) {
        getJdbcTemplate().batchUpdate(ArquivoParteSetter.SQL_INSERT_ARQUIVO_PARTE,
            new ArquivoParteSetter(aquivoPartes, geradorDeIds));
    }
}
