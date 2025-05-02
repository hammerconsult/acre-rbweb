package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PagamentoEstornoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        PagamentoEstorno pagamentoEstorno = new PagamentoEstorno();
        pagamentoEstorno.setId(rs.getLong("ID"));
        pagamentoEstorno.setNumero(rs.getString("NUMERO"));
        pagamentoEstorno.setValor(rs.getBigDecimal("VALOR"));
        pagamentoEstorno.setDataEstorno(rs.getDate("DATAESTORNO"));
        pagamentoEstorno.setComplementoHistorico(rs.getString("COMPLEMENTOHISTORICO"));
        MovimentoDespesaORC movimentoDespesaORC = new MovimentoDespesaORC();
        movimentoDespesaORC.setId(rs.getLong("MOVIMENTODESPESAORC_ID"));
        pagamentoEstorno.setMovimentoDespesaORC(movimentoDespesaORC.getId() != 0 ? movimentoDespesaORC : null);
        pagamentoEstorno.setCategoriaOrcamentaria(rs.getString("CATEGORIAORCAMENTARIA") != null ? CategoriaOrcamentaria.valueOf(rs.getString("CATEGORIAORCAMENTARIA")) : null);
        HistoricoContabil historicoContabil = new HistoricoContabil();
        historicoContabil.setId(rs.getLong("HISTORICOCONTABIL_ID"));
        pagamentoEstorno.setHistoricoContabil(historicoContabil.getId() != 0 ? historicoContabil : null);
        UnidadeOrganizacional unidadeOrganizacionalAdm = new UnidadeOrganizacional();
        unidadeOrganizacionalAdm.setId(rs.getLong("UNIDADEORGANIZACIONALADM_ID"));
        pagamentoEstorno.setUnidadeOrganizacionalAdm(unidadeOrganizacionalAdm.getId() != 0 ? unidadeOrganizacionalAdm : null);
        UnidadeOrganizacional unidadeOrganizacional = new UnidadeOrganizacional();
        unidadeOrganizacional.setId(rs.getLong("UNIDADEORGANIZACIONAL_ID"));
        pagamentoEstorno.setUnidadeOrganizacional(unidadeOrganizacional.getId() != 0 ? unidadeOrganizacional : null);
        pagamentoEstorno.setDataConciliacao(rs.getDate("DATACONCILIACAO"));
        pagamentoEstorno.setHistoricoRazao(rs.getString("HISTORICORAZAO"));
        pagamentoEstorno.setHistoricoNota(rs.getString("HISTORICONOTA"));
        EventoContabil eventoContabil = new EventoContabil();
        eventoContabil.setId(rs.getLong("EVENTOCONTABIL_ID"));
        pagamentoEstorno.setEventoContabil(eventoContabil.getId() != 0 ? eventoContabil : null);
        pagamentoEstorno.setValor(rs.getBigDecimal("VALORFINAL"));
        Exercicio exercicio = new Exercicio();
        exercicio.setId(rs.getLong("EXERCICIO_ID"));
        pagamentoEstorno.setExercicio(exercicio.getId() != 0 ? exercicio : null);
        pagamentoEstorno.setUuid(rs.getString("UUID"));
        Identificador identificador = new Identificador();
        identificador.setId(rs.getLong("IDENTIFICADOR_ID"));
        pagamentoEstorno.setIdentificador(identificador.getId() != 0 ? identificador : null);
        UsuarioSistema usuarioSistema = new UsuarioSistema();
        usuarioSistema.setId(rs.getLong("USUARIOSISTEMA_ID"));
        pagamentoEstorno.setUsuarioSistema(usuarioSistema.getId() != 0 ? usuarioSistema : null);
        pagamentoEstorno.setUsuarioSistema(usuarioSistema);
        Pagamento pagamento = new Pagamento();
        pagamento.setId(rs.getLong("PAGAMENTO_ID"));
        pagamentoEstorno.setPagamento(pagamento.getId() != 0 ? pagamento : null);
        return pagamentoEstorno;
    }

    public Pessoa createNewPessoa(){
        Pessoa pessoa = new Pessoa() {
            @Override
            public String getNome() {
                return null;
            }

            @Override
            public String getNomeTratamento() {
                return null;
            }

            @Override
            public String getNomeFantasia() {
                return null;
            }

            @Override
            public String getCpf_Cnpj() {
                return null;
            }

            @Override
            public String getRg_InscricaoEstadual() {
                return null;
            }

            @Override
            public String getOrgaoExpedidor() {
                return null;
            }

            @Override
            public int compareTo(Pessoa o) {
                return 0;
            }
        };
        return pessoa;
    }
}
