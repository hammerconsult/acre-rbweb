  CREATE OR REPLACE VIEW VWFALTA AS
  SELECT
            falta.contratoFP_id,
            falta.falta_id,
            falta.justificativa_id,
            falta.inicio,
            falta.fim,
            falta.inicioJustificativa,
            falta.fimJustificativa,
            falta.totalFaltas+1 as totalFaltas,
            case when falta.totalFaltasEfetivas < 0 then 0
            when falta.totalFaltasEfetivas >= 0 and falta.justificativa_id is null then falta.totalFaltasEfetivas+1
            else falta.totalFaltasEfetivas end as totalFaltasEfetivas,
            case
            when falta.justificativa_id is null then 0
            when falta.totalFaltasJustificadas < 0 then 0
            when falta.totalFaltasJustificadas >= 0 then falta.totalFaltasJustificadas+1
            else falta.totalFaltasJustificadas end as totalFaltasJustificadas,
            falta.tipoFalta,
             cidfalta_id,
             medicoFalta_id,
             observacaoFalta,
             cidJustificativa_id,
             medicojustificativa_id,
             observacaoJustificativa,
             dataParaCalculo,
             falta.datacadastro
    FROM (
       select
      f.contratofp_id,
      f.id as falta_id,
      jf.id as justificativa_id,
      f.INICIO as inicio,
      f.FIM as fim,
      jf.INICIOVIGENCIA inicioJustificativa,
      jf.FINALVIGENCIA fimJustificativa,
      (coalesce(f.fim - f.inicio,0)) as totalFaltas,
      (coalesce(f.fim - f.inicio,0)) - (coalesce(jf.finalVigencia -jf.inicioVigencia,0)) as totalFaltasEfetivas,
      coalesce((jf.finalVigencia -jf.inicioVigencia),0) as totalFaltasJustificadas,
      case
      when jf.id is null then f.tipoFalta
      when (f.fim - f.inicio) - (jf.finalVigencia -jf.inicioVigencia) = 0 then 'FALTA_JUSTIFICADA'
      when (f.fim - f.inicio) - (jf.finalVigencia -jf.inicioVigencia) <> 0 then f.tipoFalta
      else f.tipoFalta end   as tipoFalta,
      f.cid_id as cidfalta_id,
      f.medico_id as medicoFalta_id,
      f.obs as observacaoFalta,
      jf.cid_id as cidJustificativa_id,
      jf.medico_id as medicojustificativa_id,
      jf.obs as observacaoJustificativa,
      jf.dataParaCalculo as dataParaCalculo,
      f.DATACADASTRO as datacadastro
    from faltas f left join JUSTIFICATIVAFALTAS jf on jf.FALTAS_ID = f.id ) falta
