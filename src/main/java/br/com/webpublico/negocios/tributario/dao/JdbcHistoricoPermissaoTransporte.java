package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidadesauxiliares.VOHistoricoPermissao;
import br.com.webpublico.negocios.tributario.rowmapper.HistoricoPermisaoRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User; André Gustavo
 * Date; 14/01/14
 * Time; 16;59
 */
@Repository(value = "historicoPermissaoDAO")
public class JdbcHistoricoPermissaoTransporte extends JdbcDaoSupport implements Serializable {
    @Autowired
    public JdbcHistoricoPermissaoTransporte(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<VOHistoricoPermissao> recuperaHistoricoPermissao(Integer numero, String tipoPermissao) {
        List<VOHistoricoPermissao> historicos = getJdbcTemplate().query(selectHistoricoPermissao(), new Object[]{numero, tipoPermissao}, new HistoricoPermisaoRowMapper());
        //System.out.println(historicos.size());
        return historicos;
    }


    public static String selectHistoricoPermissao() {
        StringBuilder select = new StringBuilder("select to_char(historico.Dtaperhst, 'dd/MM/yyyy') as dataAlteracao, ");
        select.append("historico.Mtvaltperh as motivoAlteracao, ");
        select.append("historico.Ncomperhst as nomePermissionario, ");
        select.append("historico.Ncgcperhst as cpfPermissionario, ");
        select.append("historico.Insmunhst as cmcPermissionario, ");
        select.append("logradouroPermissionario.Tpolgr || ' ' || logradouroPermissionario.Nomlgr as enderecoPermissionario, ");
        select.append("logradouroPermissionario.Ceplgr as cepPermissionario, ");
        select.append("bairroPermissionario.Nomsetor as bairroPermissionario, ");
        select.append("historico.Nom1auxhst as nomeAuxiliar1, ");
        select.append("historico.CPF1auxhst as cpfAuxiliar1, ");
        select.append("historico.Cmc1auxhst as cmcAuxiliar1, ");
        select.append("logradouroAuxiliar1.Tpolgr || ' ' || logradouroAuxiliar1.Nomlgr as enderecoAuxiliar1, ");
        select.append("logradouroAuxiliar1.Ceplgr as cepAuxiliar1, ");
        select.append("bairroAuxiliar1.Nomsetor as bairroAuxiliar1, ");
        select.append("historico.Nom2auxhst as nomeAuxiliar2, ");
        select.append("historico.Cpf2auxhst as cpfAuxiliar2, ");
        select.append("historico.Cmc2auxhst as cmcAuxiliar2, ");
        select.append("logradouroAuxiliar2.Tpolgr || ' ' || logradouroAuxiliar2.Nomlgr as enderecoAuxiliar2, ");
        select.append("logradouroAuxiliar2.Ceplgr as cepAuxiliar2, ");
        select.append("bairroAuxiliar2.Nomsetor as bairroAuxiliar2, ");
        select.append("historico.Eveichst as especieVeiculo, ");
        select.append("tipoCombustivel.Dsctpocomb as tipoCombustivel, ");
        select.append("marcaVeiculo.Dscmrcveic as marcaVeiculo, ");
        select.append("modeloVeiculo.Dscmodveic as modeloVeiculo, ");
        select.append("corVeiculo.Dsccorveic as corVeiculo, ");
        select.append("historico.Afabveichs as anoFabricacaoVeiculo, ");
        select.append("historico.Amodveichs as anoModeloVeiculo ");
        select.append("from agata.atrm2h historico ");
        select.append("left join agata.atr13 logradouroPermissionario on historico.Clgrperhst = logradouroPermissionario.Cdglgr ");
        select.append("left join agata.atr10 bairroPermissionario on historico.Cbrrperhst = bairroPermissionario.Cdgsetor ");
        select.append("left join agata.atr13 logradouroAuxiliar1 on historico.Lgr1auxhst = logradouroAuxiliar1.Cdglgr ");
        select.append("left join agata.atr10 bairroAuxiliar1 on historico.Brr1auxhst = bairroAuxiliar1.Cdgsetor ");
        select.append("left join agata.atr13 logradouroAuxiliar2 on historico.Lgr2auxhst = logradouroAuxiliar2.Cdglgr ");
        select.append("left join agata.atr10 bairroAuxiliar2 on historico.Brr2auxhst = bairroAuxiliar2.Cdgsetor ");
        select.append("left join agata.atrm3 corVeiculo on historico.Corveichst = corVeiculo.Cdgcorveic ");
        select.append("left join agata.atrm0 modeloVeiculo on historico.Modveichst = modeloveiculo.Cdgmodveic and historico.Mveichst = modeloveiculo.Cdgmrcveic ");
        select.append("left join agata.atrl9 marcaVeiculo on historico.Mveichst = marcaVeiculo.Cdgmrcveic ");
        select.append("left join agata.atrm1 tipoCombustivel on historico.Tcombveich = tipoCombustivel.Cdgtpocomb ");
        select.append("where historico.Ncltperhst = ? and historico.Ttrspperhs = ? ");
        select.append("order by historico.Dtaperhst desc ");
        return select.toString();

    }

}
