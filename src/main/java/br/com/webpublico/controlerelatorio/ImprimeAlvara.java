package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Alvara;
import br.com.webpublico.entidadesauxiliares.ImpressaoAlvara;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.enums.TipoEndereco;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class ImprimeAlvara extends AbstractReport {

    public ImprimeAlvara() {
        geraNoDialog = true;
    }

    public JasperPrint gerarBytesDoRelatorio(TipoAlvara tipoAlvara, List<ImpressaoAlvara> impressoes, String login, String rodape, Connection connection) throws IOException, JRException {
        AbstractReport report = AbstractReport.getAbstractReport();
        return report.gerarBytesDeRelatorioComDadosEmCollectionView(getCaminho(),
            tipoAlvara.getJasper() + ".jasper",
            getParametrosImpressaoAlvara(login, rodape, false, report.getCaminho(), report.getCaminhoImagem(), connection),
            new JRBeanCollectionDataSource(impressoes));
    }

    private HashMap getParametrosImpressaoAlvara(String login, String rodape, Boolean unico, String caminho, String caminhoImagem, Connection connection) {
        HashMap parameters = new HashMap();
        parameters.put("SUBREPORT_DIR", caminho);
        parameters.put("REPORT_CONNECTION", connection);
        parameters.put("BRASAO", caminhoImagem);
        parameters.put("USUARIO", login);
        parameters.put("MENSAGEM_RODAPE_ALVARA", rodape);
        parameters.put("RECIBO", unico);
        parameters.put("CABECALHO_ESTADO", "ESTADO DO ACRE");
        parameters.put("CABECALHO_MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
        parameters.put("CABECALHO_SECRETARIA", "SECRETARIA DE DESENVOLVIMENTO ECONÔMICO E FINANÇAS");
        return parameters;
    }

    public ByteArrayOutputStream imprimirAlvaraRetornando(Alvara alvara, ImpressaoAlvara impressaoAlvara, String login, String rodape, String caminho, String caminhoImagem, Connection connection) throws JRException, IOException {
        if (impressaoAlvara != null) {
            String arquivoJasper = impressaoAlvara.getTipoAlvara().getJasper();
            if (alvara.getEmitirModeloNovo()) {
                arquivoJasper += "Novo";
            }
            HashMap parametros = getParametrosImpressaoAlvara(login, rodape, false, caminho, caminhoImagem, connection);
            adicionarCabecalhoNovoAlvara(alvara, parametros);
            return gerarBytesEmPdfDeRelatorioComDadosEmCollectionViewSemCabecalhoPadrao(caminho, arquivoJasper + ".jasper",
                parametros,
                new JRBeanCollectionDataSource(Lists.newArrayList(impressaoAlvara)));
        }
        return null;
    }

    public void imprimirAlvara(Alvara alvara, ImpressaoAlvara impressaoAlvara, String login, String rodape, Connection connection) throws JRException, IOException {
        if (impressaoAlvara != null) {
            String arquivoJasper = impressaoAlvara.getTipoAlvara().getJasper();
            if (alvara.getEmitirModeloNovo()) {
                arquivoJasper += "Novo";
            }
            HashMap parametros = getParametrosImpressaoAlvara(login, rodape, true, getCaminho(), getCaminhoImagem(), connection);

            adicionarCabecalhoNovoAlvara(alvara, parametros);
            gerarRelatorioComDadosEmCollectionSemCabecalhoPadrao(arquivoJasper + ".jasper", parametros,
                new JRBeanCollectionDataSource(Lists.newArrayList(impressaoAlvara)));
        }
    }

    private void adicionarCabecalhoNovoAlvara(Alvara alvara, HashMap parametros) {
        if (TipoAlvara.LOCALIZACAO_FUNCIONAMENTO.equals(alvara.getTipoAlvara())) {
            parametros.put("CABECALHO_MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            parametros.put("CABECALHO_SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
        }
    }

    private String getSelectAlvara() {
        return "select a.id," +
            "    a.inicioVigencia, " +
            "    a.finalVigencia, " +
            "    a.tipoAlvara, " +
            "    a.vencimento, " +
            "    a.cadastroEconomico_id, " +
            "    a.areaOcupada, " +
            "    a.licencaEspecial, " +
            "    a.observacao, " +
            "    a.assinaturaDigital, " +
            "    a.provisorio, " +
            "    coalesce(pj.razaoSocial,pf.nome) as razaoSocial, " +
            "    pj.nomeFantasia, " +
            "    coalesce(pj.cnpj, pf.cpf) as cpfCnpj, " +
            "    e.abertura, " +
            "    e.classificacaoAtividade, " +
            "    e.inscricaoCadastral, " +
            "    e.protocoloOficio, " +
            "    a.emissao, " +
            "    exe.ano, " +
            "    coalesce(pj.inscricaoEstadual,'') as inscricaoEstadual, " +
            "    (select junta.numeroprocesso from juntacomercialpj junta " +
            "      where pj.id = junta.pessoajuridica_id and rownum = 1) as juntaComercial, " +
            "    (select count(rec.id) from ReciboImpressaoAlvara rec where rec.alvara_id = a.id) as impressoes, " +
            "    calculo.dataCalculo as dataCalculo, " +
            "   (SELECT MIN(REC.DATAIMPRESSAO) FROM RECIBOIMPRESSAOALVARA REC WHERE REC.ALVARA_ID = A.ID) as dataPrimeiraImpressao, ";
    }

    private String getNovoSelectAlvara() {
        return "select a.id," +
            "    a.inicioVigencia, " +
            "    a.finalVigencia, " +
            "    a.tipoAlvara, " +
            "    a.vencimento, " +
            "    a.cadastroEconomico_id, " +
            "    a.areaOcupada, " +
            "    a.licencaEspecial, " +
            "    a.observacao, " +
            "    a.assinaturaDigital, " +
            "    a.provisorio, " +
            "    coalesce(pj.razaoSocial,pf.nome) as razaoSocial, " +
            "    pj.nomeFantasia, " +
            "    coalesce(pj.cnpj, pf.cpf) as cpfCnpj, " +
            "    e.abertura, " +
            "    e.classificacaoAtividade, " +
            "    e.inscricaoCadastral, " +
            "    e.protocoloOficio, " +
            "    a.emissao, " +
            "    exe.ano, " +
            "    coalesce(pj.inscricaoEstadual,'') as inscricaoEstadual, " +
            "    (select junta.numeroprocesso from juntacomercialpj junta " +
            "      where pj.id = junta.pessoajuridica_id and rownum = 1) as juntaComercial, " +
            "    (select count(rec.id) from ReciboImpressaoAlvara rec where rec.alvara_id = a.id) as impressoes, " +
            "    calculo.dataCalculo as dataCalculo, " +
            "   (SELECT MIN(REC.DATAIMPRESSAO) FROM RECIBOIMPRESSAOALVARA REC WHERE REC.ALVARA_ID = A.ID) as dataPrimeiraImpressao, ";
    }

    private String getSelectCamposEnderecoLocalizacao() {
        return " (case when ea.id is not null then (case when ea.logradouro is not null then ea.tipologradouro || ' ' || ea.logradouro " +
            "                                       else (tlea.descricao || ' ' || lea.nome) end) else (case when ec_aud.id is not null then ec_aud.logradouro " +
            "                                                                                           else vwendereco.logradouro end) end) as logradouro, " +
            "    (case when ea.id is not null then ea.numero else (case when ec_aud.id is not null then ec_aud.numero " +
            "                                                           else vwendereco.numero end) end) as numero, " +
            "    (case when ea.id is not null then ea.complemento else (case when ec_aud.id is not null then ec_aud.complemento " +
            "                                                                else vwendereco.complemento end) end) as complemento, " +
            "    (case when ea.id is not null then coalesce(ea.bairro, bea.descricao) else (case when ec_aud.id is not null then ec_aud.bairro " +
            "                                                           else vwendereco.bairro end) end) as bairro, " +
            "    (case when ea.id is not null then coalesce(ea.cep, lbea.cep) else (case when ec_aud.id is not null then ec_aud.cep " +
            "                                                        else vwendereco.cep end) end) as cep ";
    }

    private String getNovoSelectCamposEndereco() {
        return " ea.logradouro as logradouro, " +
            "    ea.numero as numero, " +
            "    ea.complemento as complemento, " +
            "    ea.bairro as bairro, " +
            "    ea.cep as cep ";
    }

    private String getSelectCamposEnderecoFuncionamentoESanitario() {
        return " (case when ea.id is not null then (case when ea.logradouro is not null then ea.tipologradouro || ' ' || ea.logradouro " +
            "                                       else (tlea.descricao || ' ' || lea.nome) end) else vwendereco.logradouro end) as logradouro, " +
            "    (case when ea.id is not null then ea.numero else vwendereco.numero end) as numero, " +
            "    (case when ea.id is not null then ea.complemento else vwendereco.complemento end) as complemento, " +
            "    (case when ea.id is not null then coalesce(ea.bairro, bea.descricao) else vwendereco.bairro end) as bairro, " +
            "    (case when ea.id is not null then coalesce(ea.cep, lbea.cep) else vwendereco.cep end) as cep ";
    }

    private String getFromAlvara() {
        return " from Alvara a ";
    }

    private String getJoinAlvaraFuncionamento() {
        return " left join cadastroeconomico e on e.id = a.cadastroeconomico_id " +
            " left join enderecoalvara ea on ea.id = a.enderecoalvara_id" +
            " left join logradourobairro lbea on ea.logradourobairro_id = lbea.id " +
            " left join logradouro lea on lbea.logradouro_id = lea.id " +
            " left join tipologradouro tlea on lea.tipologradouro_id = tlea.id " +
            " left join bairro bea on lbea.bairro_id = bea.id " +
            " left join vwenderecopessoa vwendereco on e.pessoa_id = vwendereco.pessoa_id " +
            " left join pessoajuridica pj on pj.id = e.pessoa_id " +
            " left join pessoafisica pf on pf.id = e.pessoa_id " +
            " left join exercicio exe on exe.id = a.exercicio_id " +
            " left join calculoalvarafunc calc on calc.alvara_id = a.id " +
            " left join calculo calculo on calculo.id = calc.id ";
    }

    private String getJoinAlvaraLocalizacao() {
        return " left join cadastroeconomico e on e.id = a.cadastroeconomico_id " +
            " left join pessoajuridica pj on pj.id = e.pessoa_id " +
            " left join pessoafisica pf on pf.id = e.pessoa_id " +
            " left join exercicio exe on exe.id = a.exercicio_id " +
            " left join calculoalvaralocalizacao calc on calc.alvara_id = a.id " +
            " left join calculo on calculo.id = calc.id " +
            " left join enderecoalvara ea on ea.id = a.enderecoalvara_id " +
            " left join logradourobairro lbea on ea.logradourobairro_id = lbea.id " +
            " left join logradouro lea on lbea.logradouro_id = lea.id " +
            " left join tipologradouro tlea on lea.tipologradouro_id = tlea.id " +
            " left join bairro bea on lbea.bairro_id = bea.id " +
            " left join vwenderecopessoa vwendereco on e.pessoa_id = vwendereco.pessoa_id " +
            " left join enderecocorreio_aud ec_aud on vwendereco.endereco_id = ec_aud.id " +
            " left join revisaoauditoria revisao on ec_aud.rev = revisao.id ";
    }

    private String getJoinAlvaraSanitario() {
        return " left join cadastroeconomico e on e.id = a.cadastroeconomico_id " +
            " left join enderecoalvara ea on ea.id = a.enderecoalvara_id " +
            " left join logradourobairro lbea on ea.logradourobairro_id = lbea.id " +
            " left join logradouro lea on lbea.logradouro_id = lea.id " +
            " left join tipologradouro tlea on lea.tipologradouro_id = tlea.id " +
            " left join bairro bea on lbea.bairro_id = bea.id " +
            " left join vwenderecopessoa vwendereco on e.pessoa_id = vwendereco.pessoa_id " +
            " left join pessoajuridica pj on pj.id = e.pessoa_id " +
            " left join pessoafisica pf on pf.id = e.pessoa_id " +
            " left join exercicio exe on exe.id = a.exercicio_id " +
            " left join calculoalvarasanitario calc on calc.alvara_id = a.id " +
            " left join calculo calculo on calculo.id = calc.id ";
    }

    private String getNovoJoinAlvara() {
        return " left join cadastroeconomico e on e.id = a.cadastroeconomico_id " +
            " left join naturezajuridica nj on nj.id = e.naturezajuridica_id " +
            " left join enderecoalvara ea on ea.alvara_id = a.id " +
            "                            and ea.tipoendereco = '" + TipoEndereco.COMERCIAL.name() + "' " +
            " left join pessoajuridica pj on pj.id = e.pessoa_id " +
            " left join pessoafisica pf on pf.id = e.pessoa_id " +
            " left join exercicio exe on exe.id = a.exercicio_id " +
            " left join processocalculoalvara proc on proc.alvara_id = a.id " +
            " left join calculoalvara calc on calc.processocalculoalvara_id = proc.id " +
            " left join calculo calculo on calculo.id = calc.id ";
    }


    public String getAlvaraCnaes() {
        return " Select Cnae.CODIGOCNAE, " +
            " Cnae.Descricaodetalhada as Descricaodetalhada," +
            " Cnae.grauDeRisco as grauDeRisco " +
            " From Economicocnae Economicocnae " +
            " Inner Join Cadastroeconomico Cmc On Cmc.Id = Economicocnae.Cadastroeconomico_Id " +
            " Inner Join Alvara Alvara On Alvara.Cadastroeconomico_Id = Cmc.Id " +
            " Inner Join Cnae Cnae On Cnae.Id = Economicocnae.Cnae_Id " +
            " where alvara.id = :alvaraId " +
            " and cnae.situacao = :ativo " +
            " and Economicocnae.tipo = :tipo " +
            " and coalesce(Economicocnae.EXERCIDANOLOCAL, 0) = 1 " +
            " order by cnae.CODIGOCNAE ";
    }

    public String getCnaesDoCadastro() {
        return " Select Cnae.CODIGOCNAE, " +
            " Cnae.Descricaodetalhada as Descricaodetalhada " +
            " From Economicocnae Economicocnae " +
            " Inner Join Cadastroeconomico Cmc On Cmc.Id = Economicocnae.Cadastroeconomico_Id " +
            " Inner Join Cnae Cnae On Cnae.Id = Economicocnae.Cnae_Id " +
            " where Cmc.id = :cadastroId " +
            " and Economicocnae.tipo = :tipo " +
            " and coalesce(Economicocnae.EXERCIDANOLOCAL, 0) = 1 " +
            " and cnae.situacao = :ativo " +
            " order by cnae.CODIGOCNAE ";
    }

    public String getAtividadesLicenciadas() {
        return " Select cnaealvara.alvara_id as id, " +
            " Cnae.CODIGOCNAE, " +
            " Cnae.Descricaodetalhada as Descricaodetalhada , " +
            " Coalesce(Horario.Descricao, 'Horário de funcionamento não informado') As Horarios " +
            " From CNAEAlvara cnaealvara " +
            " Inner Join Cnae Cnae On Cnae.Id = cnaealvara.Cnae_Id " +
            " Left Join Horariofuncionamento Horario On Horario.Id = cnaealvara.Horariofuncionamento_Id " +
            " where cnaealvara.alvara_id = :alvaraId " +
            " union all " +
            " Select Alvara.id, " +
            " Cnae.CODIGOCNAE, " +
            " Cnae.Descricaodetalhada as Descricaodetalhada , " +
            " Coalesce(Horario.Descricao, 'Horário de funcionamento não informado') As Horarios " +
            " From Economicocnae Economicocnae " +
            " Inner Join Cadastroeconomico Cmc On Cmc.Id = Economicocnae.Cadastroeconomico_Id " +
            " Inner Join Alvara Alvara On Alvara.Cadastroeconomico_Id = Cmc.Id " +
            " Inner Join Cnae Cnae On Cnae.Id = Economicocnae.Cnae_Id " +
            " inner join Exercicio ex on ex.id = Alvara.EXERCICIO_ID " +
            " Left Join Horariofuncionamento Horario On Horario.Id = Economicocnae.Horariofuncionamento_Id " +
            " where alvara.id = :alvaraId " +
            " and not exists (select ca.id from CNAEAlvara ca where ca.ALVARA_ID = alvara.id) " +
            " and Extract(year from Economicocnae.inicio) <= ex.ano ";
    }

    private String getCondicaoUnico() {
        return " (a.id = :idAlvara) ";
    }

    private String getCondicaoMultiplo() {
        return " (a.tipoAlvara = :tipoAlvara  " +
            " and (e.inscricaoCadastral >= :inscricaoInicial " +
            " and e.inscricaoCadastral <= :inscricaoFinal) " +
            " and exe.ano = :ano " +
            " and exists (select rec.id from RECIBOIMPRESSAOALVARA rec where rec.ALVARA_ID = a.id)) ";
    }

    private String getOrderBy() {
        return " order by a.tipoAlvara, exe.ano, e.inscricaoCadastral";
    }

    public String getSQLEnderecoAlvaraLocalizacaoPago(boolean condicaoAuditoria) {
        return " select " +
            getSelectCamposEnderecoLocalizacao() +
            getFromAlvara() +
            getJoinAlvaraLocalizacao() + " where " +
            getCondicaoUnico() + (condicaoAuditoria ? getCondicaoAuditoria() : "") +
            getOrderBy();

    }

    private String getSqlAlvaraLocalizacao(boolean unico, boolean condicaoAuditoria) {
        return getSelectAlvara() +
            getSelectCamposEnderecoLocalizacao() +
            " , coalesce(pj.porte,'') as porte, " +
            " coalesce(e.caracteristicasadicionais, '') as caracteristicasAdicionais, " +
            " '' as naturezajuridica " +
            getFromAlvara() +
            getJoinAlvaraLocalizacao() + " where " +
            (unico ? (getCondicaoUnico() + (condicaoAuditoria ? getCondicaoAuditoria() : "")) : getCondicaoMultiplo()) +
            getOrderBy();
    }

    private String getSqlAlvaraFuncionamento(boolean unico) {
        return getSelectAlvara() +
            getSelectCamposEnderecoFuncionamentoESanitario() +
            " , coalesce(pj.porte,'') as porte, " +
            " coalesce(e.caracteristicasadicionais, '') as caracteristicasAdicionais," +
            " '' as naturezajuridica " +
            getFromAlvara() +
            getJoinAlvaraFuncionamento() + " where " +
            (unico ? getCondicaoUnico() : getCondicaoMultiplo()) +
            getOrderBy();
    }

    private String getSqlAlvaraSanitario(boolean unico) {
        return getSelectAlvara() +
            getSelectCamposEnderecoFuncionamentoESanitario() +
            " , coalesce(pj.porte,'') as porte, " +
            " coalesce(e.caracteristicasadicionais, '') as caracteristicasAdicionais," +
            " '' as naturezajuridica " +
            getFromAlvara() +
            getJoinAlvaraSanitario() + " where " +
            (unico ? getCondicaoUnico() : getCondicaoMultiplo()) +
            getOrderBy();
    }

    public String getNovoSqlAlvara(boolean unico) {
        return getNovoSelectAlvara() + getNovoSelectCamposEndereco() +
            ", coalesce(pj.porte,'') as porte, " +
            "  coalesce(e.caracteristicasadicionais, '') as caracteristicasAdicionais," +
            "  nj.codigo||' - '||nj.descricao as naturezajuridica " +
            getFromAlvara() +
            getNovoJoinAlvara() +
            " where " + (unico ? getCondicaoUnico() : getCondicaoMultiplo()) +
            getOrderBy();
    }

    public String getCondicaoAuditoria() {
        return " and (revisao.id is null or ec_aud.rev = (select max(aud2.rev) from enderecocorreio_aud aud2 " +
            "                                             where aud2.rev <= :idRevisao and aud2.id = ec_aud.id " +
            "                                             and ec_aud.revtype <> 2 or ec_aud.revtype is null)) ";
    }

    public String getSqlAlvara(TipoAlvara tipoAlvara, boolean unico, boolean condicaoAuditoria) {
        switch (tipoAlvara) {
            case LOCALIZACAO:
                return getSqlAlvaraLocalizacao(unico, condicaoAuditoria);
            case FUNCIONAMENTO:
                return getSqlAlvaraFuncionamento(unico);
            case SANITARIO:
                return getSqlAlvaraSanitario(unico);
        }
        return "";
    }

}
