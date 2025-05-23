merge into processocalculoitbi pcitbi
using (select citbi.processocalculoitbi_id as idprocesso, citbi.sequencia as codigo,
              citbi.tipoitbi as tipoitbi, coalesce(calc.isento, 0) as isento,
              citbi.situacao as situacao, citbi.cadastroimobiliario_id as imobiliario,
              citbi.cadastrorural_id as rural, citbi.tipoisencaoitbi_id as tipoisencao,
              citbi.usuariosistemaretificacao_id usuario_retificacao, citbi.dataretificacao as data_retificacao,
              citbi.motivoretificacao as motivo_retificacao, citbi.usuariosistemacancelamento_id usuario_cancelamento,
              citbi.datacancelamento as data_cancelamento, citbi.motivocancelamento as motivo_cancelamento,
              citbi.codigoverificacao as verificacao
       from calculoitbi citbi
                inner join calculo calc on citbi.id = calc.id) calculo
on (pcitbi.id = calculo.idprocesso)
when matched then update set
                             pcitbi.codigo = calculo.codigo,
                             pcitbi.tipoitbi = calculo.tipoitbi,
                             pcitbi.isentoimune = calculo.isento,
                             pcitbi.situacao = calculo.situacao,
                             pcitbi.cadastroimobiliario_id = calculo.imobiliario,
                             pcitbi.cadastrorural_id = calculo.rural,
                             pcitbi.tipoisencaoitbi_id = calculo.tipoisencao,
                             pcitbi.usuarioretificacao_id = calculo.usuario_retificacao,
                             pcitbi.usuariocancelamento_id = calculo.usuario_cancelamento,
                             pcitbi.dataretificacao = calculo.data_retificacao,
                             pcitbi.datacancelamento = calculo.data_cancelamento,
                             pcitbi.motivoretificacao = calculo.motivo_retificacao,
                             pcitbi.motivocancelamento = calculo.motivo_cancelamento,
                             pcitbi.codigoverificacao = calculo.verificacao
                  where pcitbi.codigo is null
