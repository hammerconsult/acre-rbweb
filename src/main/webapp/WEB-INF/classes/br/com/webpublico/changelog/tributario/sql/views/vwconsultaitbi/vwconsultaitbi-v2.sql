  create or replace force view  vwconsultaitbi as
  select distinct
        ci.id id_cadastro,
        ci.inscricaocadastral cadastro,
        e.ano exercicio,
        cp.pessoa_id id_pessoa,
        c.id id_calculo,
        c.subdivida sd,
        vd.id id_valordivida,
        c.datacalculo emissao,
        pvd.id id_parcela,
        pvd.opcaopagamento_id id_opcaopagamento,
        op.promocional cotaunica,
        pvd.vencimento,
        spvd.situacaoparcela situacaoparcela,
        spvd.referencia,
        pacote_consulta_de_debitos.getnumeroparcela(vd.id, op.id, op.promocional, pvd.sequenciaparcela) parcela,
        case
          when spvd.situacaoparcela <> 'EM_ABERTO'
          then pvd.valor
          else spvd.saldo
        end valororiginal,
        d.descricao divida,
        d.id id_divida,
        coalesce(pvd.dividaativa,0) dividaativa,
        coalesce(pvd.dividaativaajuizada,0) dividaativaajuizada,
        coalesce(pf.cpf, pj.cnpj) cpfcnpj,
        coalesce(pf.nome, pj.razaosocial) nome,
        coalesce((select 'PREDIAL'
                     from construcao c
                  where c.imovel_id = ci.id
                    and coalesce(c.cancelada, 0) = 0 and rownum = 1), 'TERRITORIAL') tipoimovel,
        trim(setor.codigo) setor,
        trim(quadra.codigo) quadra,
        trim(lote.codigolote) lote,
        trim(bairro.CODIGO) || ' - ' || trim(bairro.descricao) bairro,
        trim(coalesce(tp.descricao,''))||' '|| trim(coalesce(logradouro.nome,'')) logradouro,
        trim(ci.numero) numero,
        trim(ci.complementoendereco) complemento,
        trim(face.cep) cep,
        dva.acrescimo_id id_configuracaoacrescimo,
        setor.id id_setor,
        e.id id_exercicio,
        bairro.id id_bairro,
        logradouro.id id_logradouro,
        itbi.numeroparcelas,
        itbi.tipoitbi,
        itbi.sequencia,
        itbi.situacao as situacaoItbi,
        itbi.basecalculo,
        c.valorefetivo,
        pp.pessoa_id as id_proprietario,
        coalesce(pfProprietario.nome, pjProprietario.razaoSocial) as nomeProprietario,
        coalesce(pfProprietario.cpf, pjProprietario.cnpj) as cpfCnpjProprietario,
        adquirente.adquirente_id as id_adquirente,
        coalesce(pfadquirente.nome, pjadquirente.razaoSocial) as nomeadquirente,
        coalesce(pfadquirente.cpf, pjadquirente.cnpj) as cpfCnpjadquirente,
        transmitente.pessoa_id as id_transmitente,
        coalesce(pftransmitente.nome, pjtransmitente.razaoSocial) as nometransmitente,
        coalesce(pftransmitente.cpf, pjtransmitente.cnpj) as cpfCnpjtransmitente,
        coalesce((select ecbci.valor from EventoCalculoBCI ecbci
                  inner join EventoConfiguradoBCI ec on ec.id = ecbci.eventocalculo_id
                  inner join eventocalculo ev on ev.id = ec.eventocalculo_id
                  where ecbci.cadastroimobiliario_id = ci.id
                    and ev.identificacao = 'valorVenalImovelBCI'), 0) as valorVenalImovel,
        coalesce((select ecbci.valor from EventoCalculoBCI ecbci
                  inner join EventoConfiguradoBCI ec on ec.id = ecbci.eventocalculo_id
                  inner join eventocalculo ev on ev.id = ec.eventocalculo_id
                  where ecbci.cadastroimobiliario_id = ci.id
                    and ev.identificacao = 'valorVenalTerrenoParaBci'), 0) as valorVenalTerreno
       from calculoitbi itbi
      inner join calculo c on c.id = itbi.id
      inner join calculopessoa cp on cp.calculo_id = c.id
      left join pessoafisica pf on pf.id = cp.pessoa_id
      left join pessoajuridica pj on pj.id = cp.pessoa_id
      inner join valordivida vd on vd.calculo_id = c.id
      inner join divida d on vd.divida_id = d.id
      inner join dividaacrescimo dva on dva.divida_id = d.id
      inner join exercicio e on vd.exercicio_id = e.id
      left join cadastroimobiliario ci on ci.id = itbi.cadastroimobiliario_id
      left join propriedade pp on pp.imovel_id = ci.id
      left join pessoafisica pfProprietario on pfProprietario.id = pp.pessoa_id
      left join pessoajuridica pjProprietario on pjProprietario.id = pp.pessoa_id
      left join cadastrorural cr on cr.id = itbi.cadastrorural_id
      inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
      inner join opcaopagamento op on pvd.opcaopagamento_id = op.id
      inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
      inner join lote lote on lote.id = ci.lote_id
      left join setor setor on setor.id = lote.setor_id
      left join quadra quadra on quadra.id = lote.quadra_id
      left join loteamento loteamento on loteamento.id = quadra.loteamento_id
      left join testada testadas on testadas.lote_id = lote.id
      left join face face on face.id = testadas.face_id
      left join logradourobairro logbairro on logbairro.id = face.logradourobairro_id
      left join logradouro logradouro on logradouro.id = logbairro.logradouro_id
      left join tipologradouro tp on tp.id = logradouro.tipologradouro_id
      left join bairro bairro on bairro.id = logbairro.bairro_id
      left join AdquirentesCalculoITBI adquirente on adquirente.calculoitbi_id = itbi.id
      left join pessoafisica pfadquirente on pfadquirente.id = adquirente.adquirente_id
      left join pessoajuridica pjadquirente on pjadquirente.id = adquirente.adquirente_id
      left join TransmitentesCalculoITBI transmitente on transmitente.calculoitbi_id = itbi.id
      left join pessoafisica pftransmitente on pftransmitente.id = transmitente.pessoa_id
      left join pessoajuridica pjtransmitente on pjtransmitente.id = transmitente.pessoa_id
      where spvd.situacaoparcela <> 'ISOLAMENTO'
        and (testadas.id = coalesce((select max(s_testada.id)
                            from testada s_testada
                         where s_testada.lote_id = lote.id
                           and s_testada.principal = 1), (select max(s_testada.id)
                                                             from testada s_testada
                                                          where s_testada.lote_id = lote.id)) or testadas.id is null)
       and cp.id = (select max(s_cp.id) from calculopessoa s_cp where s_cp.calculo_id = c.id)
       and pp.id = (select max(s_pp.id) from propriedade s_pp where s_pp.imovel_id = ci.id and pp.finalvigencia is null)
 ;
