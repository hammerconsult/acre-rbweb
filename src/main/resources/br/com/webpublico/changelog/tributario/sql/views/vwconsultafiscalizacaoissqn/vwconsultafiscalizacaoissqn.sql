    create or replace force view vwconsultafiscalizacaoissqn as
    select distinct
           ce.id id_cadastro,
           ce.inscricaocadastral cadastro,
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
           coalesce(pf.cpf, pj.cnpj) cpforcnpj,
           coalesce(pf.nome, pj.razaosocial) nomeorrazaosocial,
           endbce.tipologradouro,
           endbce.logradouro,
           endbce.numero,
           endbce.complemento,
           endbce.bairro,
           endbce.cep,
           pgf.numero numeroprogramacaofiscal,
           af.ordemservico numeroordemservico,
           aif.numero numeroautoinfracao,
           aif.ano anoautoinfracao,
           aif.id idautoinfracao
       from calculofiscalizacao cf
      inner join autoinfracaofiscal aif on aif.id = cf.autoinfracaofiscal_id
      inner join registrolancamentocontabil rlc on rlc.id = aif.registro_id
      inner join acaofiscal af on af.id = rlc.acaofiscal_id
      inner join programacaofiscal pgf on pgf.id = af.programacaofiscal_id
      inner join calculo c on c.id = cf.id
      inner join cadastroeconomico ce on c.cadastro_id = ce.id
      left join vwenderecobce endbce on endbce.cadastroeconomico_id = ce.id
      left join processocalculofiscal pcf on pcf.id = cf.processocalculofiscal_id
      inner join calculopessoa cp on cp.calculo_id = c.id
      left join pessoajuridica pj on pj.id = cp.pessoa_id
      left join pessoafisica pf on pf.id = cp.pessoa_id
      inner join valordivida vd on vd.calculo_id = c.id
      inner join divida d on vd.divida_id = d.id
      inner join dividaacrescimo dva on dva.divida_id = d.id
      inner join exercicio e on e.id = vd.exercicio_id
      inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
      inner join opcaopagamento op on op.id = pvd.opcaopagamento_id
      inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
    where spvd.situacaoparcela <> 'ISOLAMENTO'
