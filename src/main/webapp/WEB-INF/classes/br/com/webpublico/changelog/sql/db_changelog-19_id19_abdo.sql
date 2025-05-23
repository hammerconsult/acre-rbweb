create or replace view vwenderecobcr as (
select cr.id cadastrorural_id,
       trim(cr.nomepropriedade) nomepropriedade,
       trim(cr.localizacaolote) localizacaolote
   from cadastrorural cr)