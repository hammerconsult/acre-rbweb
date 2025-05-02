 create or replace force view vwconsultafiscsecretaria as
  select distinct
           c.cadastro_id id_cadastro,
           coalesce(ci.inscricaocadastral, coalesce(ce.inscricaocadastral, cr.numeroincra)) cadastro,
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
           null pagamento,
           dva.acrescimo_id id_configuracaoacrescimo,
           coalesce(pf.cpf, pj.cnpj) cpfcnpj,
           coalesce(pf.nome, pj.razaosocial) nomerazaosocial,
           pcf.codigo numeroprocesso,
           aif.numero numeroautoinfracao,
           coalesce(endbci.bairro, endbce.bairro, endbcr.nomepropriedade, endpes.bairro) bairro,
           coalesce(endbci.logradouro, endbce.logradouro, endbcr.localizacaolote, endpes.logradouro) logradouro,
           coalesce(endbci.numero, endbce.numero, '', endpes.numero) numero,
           coalesce(endbci.complemento, endbce.complemento, '', endpes.complemento) complemento,
           coalesce(endbci.cep, endbce.cep, '', endpes.cep) cep
       from calculoprocfiscalizacao cpf
      inner join processofiscalizacao pcf on pcf.id = cpf.processofiscalizacao_id
      inner join autoinfracaofiscalizacao aif on aif.id = pcf.autoinfracaofiscalizacao_id
      inner join calculo c on c.id = cpf.id
      inner join calculopessoa cp on cp.calculo_id = c.id
      inner join pessoa p on p.id = cp.pessoa_id
      left join pessoajuridica pj on pj.id = cp.pessoa_id
      left join pessoafisica pf on pf.id = cp.pessoa_id
      left join cadastroeconomico ce on c.cadastro_id = ce.id
      left join cadastroimobiliario ci on c.cadastro_id = ci.id
      left join cadastrorural cr on c.cadastro_id = cr.id
      left join enderecocorreio endpes on endpes.id = p.enderecoprincipal_id
      left join vwenderecobci endbci on endbci.cadastroimobiliario_id = ci.id
      left join vwenderecobce endbce on endbce.cadastroeconomico_id = ce.id
      left join vwenderecobcr endbcr on endbcr.cadastrorural_id = cr.id
      inner join valordivida vd on vd.calculo_id = c.id
      inner join exercicio e on e.id = vd.exercicio_id
      inner join divida d on d.id = vd.divida_id
      inner join dividaacrescimo dva on dva.divida_id = d.id
      inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
      inner join opcaopagamento op on op.id = pvd.opcaopagamento_id
      inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
    where spvd.situacaoparcela <> 'ISOLAMENTO'
